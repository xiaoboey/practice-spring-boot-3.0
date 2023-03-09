package top.xiaoboey.practice.spring.boot3.appone.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import top.xiaoboey.practice.spring.boot3.simplestarter.entity.SimpleLog;
import top.xiaoboey.practice.spring.boot3.simplestarter.pojo.ApiResult;
import top.xiaoboey.practice.spring.boot3.simplestarter.service.SimpleLogService;

/**
 * @author xiaoqb
 */
@ControllerAdvice
public class GlobalExceptionController {
    private final SimpleLogService simpleLogService;

    public GlobalExceptionController(SimpleLogService simpleLogService) {
        this.simpleLogService = simpleLogService;
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity<ApiResult<String>> exceptionHandler(Exception e, HttpServletRequest request) {
        int status;
        if (e instanceof AccessDeniedException) {
            status = HttpServletResponse.SC_FORBIDDEN;
        } else {
            status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }

        SimpleLog simpleLog = simpleLogService.getFromCache(request.getRequestId());
        simpleLog.setStatusCode(status);
        simpleLog.setStatusMsg(e.getMessage());
        simpleLogService.saveThenClean(simpleLog);

        return ResponseEntity.status(status).body(new ApiResult<String>(status, null, e.getMessage()));
    }
}
