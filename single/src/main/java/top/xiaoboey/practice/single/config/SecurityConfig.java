package top.xiaoboey.practice.single.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import top.xiaoboey.practice.single.entity.SimpleLog;
import top.xiaoboey.practice.single.pojo.ApiResult;
import top.xiaoboey.practice.single.service.SimpleLogService;

import java.io.IOException;

/**
 * @author xiaoqb
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final AuthAndLogFilter authAndLogFilter;
    private final SimpleLogService simpleLogService;

    public SecurityConfig(AuthAndLogFilter authAndLogFilter, SimpleLogService simpleLogService) {
        this.authAndLogFilter = authAndLogFilter;
        this.simpleLogService = simpleLogService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.addFilterBefore(authAndLogFilter, UsernamePasswordAuthenticationFilter.class);

        http
                .csrf().disable()
                .formLogin().disable()
                .logout().disable()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.OPTIONS).permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/user/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling((exceptions) -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                );

        return http.build();
    }

    /**
     * @return
     */
    @Bean
    AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> handle(request, response, simpleLogService,
                new ApiResult<>(HttpServletResponse.SC_UNAUTHORIZED, "Token expired or invalid."));
    }

    /**
     * @return
     */
    @Bean
    AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> handle(request, response, simpleLogService,
                new ApiResult<>(HttpServletResponse.SC_FORBIDDEN, "Access denied."));
    }

    /**
     * @param response
     * @param apiResult
     * @throws IOException
     */
    private static void handle(HttpServletRequest request,
                               HttpServletResponse response,
                               SimpleLogService simpleLogService,
                               ApiResult<String> apiResult) throws IOException {
        SimpleLog simpleLog = simpleLogService.getFromCache(request.getRequestId());
        simpleLog.setStatusCode(apiResult.getCode());
        simpleLog.setStatusMsg(apiResult.getMessage());
        simpleLogService.saveThenClean(simpleLog);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(apiResult.getCode());

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), apiResult);
    }
}
