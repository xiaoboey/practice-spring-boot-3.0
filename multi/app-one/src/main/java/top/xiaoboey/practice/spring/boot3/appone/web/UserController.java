package top.xiaoboey.practice.spring.boot3.appone.web;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import top.xiaoboey.practice.spring.boot3.simplestarter.log.OperationLog;
import top.xiaoboey.practice.spring.boot3.simplestarter.pojo.ApiResult;
import top.xiaoboey.practice.spring.boot3.simplestarter.service.UserAuthService;

import java.util.Optional;

/**
 * @author xiaoqb
 */
@RequestMapping("/user")
@RestController
public class UserController {
    private final UserAuthService userAuthService;

    public UserController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @GetMapping("/hello")
    public ApiResult<String> hello(@RequestParam(required = false) String name) {
        return new ApiResult<String>(HttpServletResponse.SC_OK, null, String.format("Hello, %s!", Optional.ofNullable(name).orElse("world")));
    }

    @OperationLog(operation = "Login")
    @PostMapping("/login")
    public ApiResult<String> login(@RequestParam String name, @RequestParam String pwd) {
        return userAuthService.login(name, pwd);
    }
}
