package top.xiaoboey.practice.spring.boot3.simplestarter.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.xiaoboey.practice.spring.boot3.simplestarter.entity.SimpleLog;
import top.xiaoboey.practice.spring.boot3.simplestarter.pojo.ApiResult;
import top.xiaoboey.practice.spring.boot3.simplestarter.service.SimpleLogService;

import java.lang.reflect.Method;

/**
 * @author xiaoqb
 */
@Aspect
@Component
public class OperationLogAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final SimpleLogService simpleLogService;

    public OperationLogAspect(SimpleLogService simpleLogService) {
        this.simpleLogService = simpleLogService;
    }

    @Pointcut("@annotation(top.xiaoboey.practice.spring.boot3.simplestarter.log.OperationLog)")
    public void operateLogPointCut() {
    }

    @Before(value = "operateLogPointCut()")
    public void handleBefore(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        OperationLog operationLog = method.getAnnotation(OperationLog.class);

        if (operationLog != null) {
            HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
            String requestId = request.getRequestId();
            SimpleLog dto = simpleLogService.getFromCache(requestId);

            String operation = operationLog.operation();
            if (StringUtils.hasText(operation)) {
                dto.setOperation(operation);
            }

            if (operationLog.ignoreParams()) {
                dto.setQueryParams(null);
            }

            dto.setStatusCode(HttpServletResponse.SC_OK);
            simpleLogService.saveToCache(dto);
        }
    }

    @AfterReturning(value = "operateLogPointCut()", returning = "jsonResult")
    public void handleReturning(JoinPoint joinPoint, ApiResult jsonResult) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        OperationLog operationLog = method.getAnnotation(OperationLog.class);

        if (operationLog != null) {
            HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
            String requestId = request.getRequestId();
            SimpleLog simpleLog = simpleLogService.getFromCache(requestId);

            if (!operationLog.ignoreResult() && jsonResult.getContent() != null) {
                try {
                    simpleLog.setJsonResult(objectMapper.writeValueAsString(jsonResult.getContent()));
                } catch (JsonProcessingException e) {
                    simpleLog.setJsonResult(e.getClass().getName() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            simpleLog.setStatusCode(jsonResult.getCode());
            String message = jsonResult.getMessage();
            simpleLog.setStatusMsg(message);

            simpleLogService.saveToCache(simpleLog);
        }
    }

    @AfterThrowing(pointcut = "operateLogPointCut()", throwing = "e")
    public void handleThrowing(JoinPoint joinPoint, Throwable e) {
        try {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            String className = joinPoint.getTarget().getClass().getName();
            logger.error("class: {}, method: {}, error: {}", className, method.getName(), e.getMessage());
            e.printStackTrace();

            OperationLog operationLog = method.getAnnotation(OperationLog.class);
            if (operationLog != null) {
                HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
                String requestId = request.getRequestId();
                if (StringUtils.hasText(requestId)) {
                    SimpleLog simpleLog = simpleLogService.getFromCache(requestId);
                    simpleLog.setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    simpleLog.setStatusMsg(e.getMessage());
                    simpleLogService.saveToCache(simpleLog);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
