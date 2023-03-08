package top.xiaoboey.practice.spring.boot3.simplestarter.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import top.xiaoboey.practice.spring.boot3.simplestarter.config.SimpleConfiguration;
import top.xiaoboey.practice.spring.boot3.simplestarter.config.SimpleProperties;
import top.xiaoboey.practice.spring.boot3.simplestarter.dao.SimpleUserRepository;
import top.xiaoboey.practice.spring.boot3.simplestarter.entity.SimpleUser;
import top.xiaoboey.practice.spring.boot3.simplestarter.pojo.ApiResult;

import java.security.interfaces.RSAPrivateKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author xiaoqb
 */
@Service
public class UserAuthService {
    private final String LOGIN_FAIL = "The username or password is incorrect.";

    private final PasswordEncoder passwordEncoder;
    private final SimpleUserRepository simpleUserRepository;
    private final RSAPrivateKey rsaPrivateKey;
    private final SimpleProperties simpleProperties;

    public UserAuthService(PasswordEncoder passwordEncoder,
                           SimpleUserRepository simpleUserRepository,
                           RSAPrivateKey rsaPrivateKey,
                           SimpleProperties simpleProperties) {
        this.passwordEncoder = passwordEncoder;
        this.simpleUserRepository = simpleUserRepository;
        this.rsaPrivateKey = rsaPrivateKey;
        this.simpleProperties = simpleProperties;
    }

    public ApiResult<String> login(String username, String password) {
        SimpleUser user = simpleUserRepository.findByUsername(username);
        if (user == null) {
            return new ApiResult<>(HttpStatus.NOT_FOUND.value(), LOGIN_FAIL);
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return new ApiResult<>(HttpStatus.UNAUTHORIZED.value(), LOGIN_FAIL);
        }

        Instant now = Instant.now();
        Instant expiresAt = now.plus(Duration.parse("PT30M"));

        List<String> authorityList = user.getAuthorities().stream().map(p -> p.getAuthority()).collect(Collectors.toList());
        String token = JWT.create()
                .withIssuer(simpleProperties.getJwtIssuer())
                .withSubject(user.getUsername())
                .withJWTId(UUID.randomUUID().toString())
                .withClaim(SimpleConfiguration.CLAIM_AUTHORITIES, authorityList)
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(expiresAt))
                .sign(Algorithm.RSA256(null, rsaPrivateKey));
        return new ApiResult<>(HttpStatus.OK.value(), null, token);
    }
}
