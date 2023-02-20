package top.xiaoboey.practice.single.web;

import org.springframework.web.bind.annotation.*;
import top.xiaoboey.practice.single.pojo.ApiResult;
import top.xiaoboey.practice.single.service.SimpleUserService;

import java.util.Optional;

/**
 * @author xiaoqb
 */
@RequestMapping("/user")
@RestController
public class UserController {
    private final SimpleUserService simpleUserService;

    public UserController(SimpleUserService simpleUserService) {
        this.simpleUserService = simpleUserService;
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(required = false) String name) {
        return String.format("Hello, %s!", Optional.ofNullable(name).orElse("world"));
    }

    @PostMapping("/login")
    public ApiResult<String> login(@RequestParam String name, @RequestParam String pwd) {
        return simpleUserService.login(name, pwd);
    }
}
