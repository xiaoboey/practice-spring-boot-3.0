package top.xiaoboey.practice.single.config;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import top.xiaoboey.practice.single.entity.SimpleLog;
import top.xiaoboey.practice.single.entity.SimpleUser;
import top.xiaoboey.practice.single.pojo.ApiResult;
import top.xiaoboey.practice.single.service.SimpleLogService;
import top.xiaoboey.practice.single.service.SimpleUserService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaoqb
 */
@Component
public class AuthAndLogFilter extends OncePerRequestFilter {
    @Value("${management.endpoints.web.base-path:/actuator}" + "/")
    private String actuatorBasePath;

    private final ObjectMapper objectMapper;
    private final JWTVerifier jwtVerifier;
    private final SimpleLogService simpleLogService;
    private final SimpleUserService simpleUserService;

    public AuthAndLogFilter(JWTVerifier jwtVerifier,
                            SimpleLogService simpleLogService,
                            SimpleUserService simpleUserService) {
        this.objectMapper = new ObjectMapper();

        this.jwtVerifier = jwtVerifier;
        this.simpleLogService = simpleLogService;
        this.simpleUserService = simpleUserService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        // Token validation and access logging for the actuator is ignored.
        String requestId = request.getRequestId();
        if (!uri.startsWith(this.actuatorBasePath)) {
            SimpleLog simpleLog = new SimpleLog(request);
            simpleLog.setQueryParams(objectMapper.writeValueAsString(request.getParameterMap()));

            try {
                UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    SimpleUser simpleUser =  (SimpleUser) simpleUserService.loadUserByUsername((String)authentication.getPrincipal());
                    simpleLog.setUserId(simpleUser.getId());
                }
                simpleLogService.saveToCache(simpleLog);
            } catch (Exception ex) {
                // Exceptions here are not caught by global ControllerAdvice
                ApiResult<String> apiResult;
                if (ex instanceof TokenExpiredException) {
                    apiResult = new ApiResult<String>(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized.");
                } else {
                    ex.printStackTrace();
                    apiResult = new ApiResult<String>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
                }
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.setStatus(apiResult.getCode());
                objectMapper.writeValue(response.getOutputStream(), apiResult);

                saveAccessLog(requestId, response);
                return;
            }
        }

        filterChain.doFilter(request, response);
        saveAccessLog(requestId, response);
    }

    /**
     *
     * @param request
     * @return
     */
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            DecodedJWT jwt = jwtVerifier.verify(token);
            String jwtId = jwt.getClaim(JsonWebTokenConfig.CLAIM_JTI).asString();
            String[] authorities = jwt.getClaim(JsonWebTokenConfig.CLAIM_AUTHORITIES).asArray(String.class);
            List<String> authorityList = new ArrayList<>();
            authorityList.addAll(Arrays.asList(authorities));
            String username = jwt.getSubject();
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    jwtId,
                    authorityList.stream().map(authority -> new SimpleGrantedAuthority(authority)).collect(Collectors.toList())
            );

            return authentication;
        }

        return null;
    }

    private void saveAccessLog(String requestId, HttpServletResponse response) {
        if (StringUtils.hasText(requestId)) {
            SimpleLog logDTO = simpleLogService.getFromCache(requestId);
            if (logDTO != null && logDTO.getLogTime() == null) {
                logDTO.setStatusCode(response.getStatus());
                simpleLogService.saveThenClean(logDTO);
            }
        }
    }
}
