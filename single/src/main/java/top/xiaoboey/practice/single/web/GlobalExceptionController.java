package top.xiaoboey.practice.single.web;

import jakarta.servlet.http.HttpServletResponse;
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
    public ApiResult<String> exceptionHandler(Exception e) {
        return new ApiResult<String>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
