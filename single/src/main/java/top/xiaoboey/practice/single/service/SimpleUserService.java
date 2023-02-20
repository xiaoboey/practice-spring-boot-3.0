package top.xiaoboey.practice.single.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import top.xiaoboey.practice.single.config.CaffeineConfiguration;
import top.xiaoboey.practice.single.config.JsonWebTokenConfig;
import top.xiaoboey.practice.single.pojo.ApiResult;
import top.xiaoboey.practice.single.dao.SimpleUserRepository;
import top.xiaoboey.practice.single.entity.SimpleUser;

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
public class SimpleUserService implements UserDetailsService {
    private final String LOGIN_FAIL = "The username or password is incorrect.";

    @Value("${my.jwt.issuer}")
    private String issuer;

    private final SimpleUserRepository simpleUserRepository;
    private final RSAPrivateKey rsaPrivateKey;
    private final PasswordEncoder passwordEncoder;

    public SimpleUserService(SimpleUserRepository simpleUserRepository,
                             RSAPrivateKey rsaPrivateKey,
                             PasswordEncoder passwordEncoder) {
        this.simpleUserRepository = simpleUserRepository;
        this.rsaPrivateKey = rsaPrivateKey;
        this.passwordEncoder = passwordEncoder;
    }

    @Cacheable(cacheNames = CaffeineConfiguration.CACHE_SIMPLE_USER, key = "#username")
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return simpleUserRepository.findByUsername(username);
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
                .withIssuer(issuer)
                .withSubject(user.getUsername())
                .withJWTId(UUID.randomUUID().toString())
                .withClaim(JsonWebTokenConfig.CLAIM_AUTHORITIES, authorityList)
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(expiresAt))
                .sign(Algorithm.RSA256(null, rsaPrivateKey));
        return new ApiResult<>(HttpStatus.OK.value(), null, token);
    }
}
