package top.xiaoboey.practice.single.web;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import top.xiaoboey.practice.single.pojo.ApiResult;

/**
 * @author xiaoqb
 */
@ControllerAdvice
public class GlobalExceptionController {
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity<ApiResult<String>> exceptionHandler(Exception e) {
        int status;
        if (e instanceof AccessDeniedException) {
            status = HttpServletResponse.SC_FORBIDDEN;
        } else {
            status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.status(status).body(new ApiResult<String>(status, null, e.getMessage()));
    }
}
