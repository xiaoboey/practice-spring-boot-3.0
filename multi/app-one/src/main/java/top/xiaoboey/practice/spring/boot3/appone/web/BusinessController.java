package top.xiaoboey.practice.spring.boot3.appone.web;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.xiaoboey.practice.spring.boot3.simplestarter.pojo.ApiResult;

import java.util.Optional;

/**
 * @author xiaoqb
 */
@RequestMapping("/business")
@RestController
public class BusinessController {
    @GetMapping("/someInfo")
    public ApiResult<String> someInfo(@RequestParam(required = false) String key) {
        String content = String.format("Some Info Found %s!", Optional.ofNullable(key).orElse("null"));
        return new ApiResult<String>(HttpStatus.OK.value(), null, content);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/sensitiveInfo")
    public ApiResult<String> sensitiveInfo(@RequestParam(required = false) String key) {
        String content = String.format("Sensitive Info Found %s!", Optional.ofNullable(key).orElse("null"));
        return new ApiResult<String>(HttpStatus.OK.value(), null, content);
    }

    @PreAuthorize("hasAuthority('DIGGING')")
    @GetMapping("/dig")
    public ApiResult<String> dig() {
        return new ApiResult<String>(HttpStatus.OK.value(), null, "Digging");
    }
}
