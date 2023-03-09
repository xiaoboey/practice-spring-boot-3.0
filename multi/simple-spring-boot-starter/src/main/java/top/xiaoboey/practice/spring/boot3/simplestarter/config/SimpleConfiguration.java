package top.xiaoboey.practice.spring.boot3.simplestarter.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import top.xiaoboey.practice.spring.boot3.simplestarter.dao.SimpleUserRepository;
import top.xiaoboey.practice.spring.boot3.simplestarter.service.SimpleUserService;
import top.xiaoboey.practice.spring.boot3.simplestarter.service.UserAuthService;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author xiaoqb
 */
@Configuration
@EnableConfigurationProperties(SimpleProperties.class)
@EntityScan(basePackages = {"top.xiaoboey.practice.spring.boot3.simplestarter.entity"})
@EnableJpaRepositories(basePackages = {"top.xiaoboey.practice.spring.boot3.simplestarter.dao"})
public class SimpleConfiguration {
    public static final String CLAIM_AUTHORITIES = "authorities";
    public static final String CLAIM_JTI = "jti";

    @Autowired
    private SimpleProperties simpleProperties;

    /**
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    @Bean
    @ConditionalOnProperty(prefix = "simple.starter", name = "enable-jwt-private-key", havingValue = "true")
    public RSAPrivateKey rsaPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        ClassPathResource resource = new ClassPathResource(simpleProperties.getJwtRsaKey());
        byte[] keyBytes;
        try (InputStream inputStream = resource.getInputStream()) {
            keyBytes = inputStream.readAllBytes();
        }
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    @Bean
    @ConditionalOnProperty(prefix = "simple.starter", name = "enable-jwt-private-key", havingValue = "true")
    public UserAuthService userAuthService(@Autowired PasswordEncoder passwordEncoder,
                                           @Autowired SimpleUserRepository simpleUserRepository,
                                           @Autowired RSAPrivateKey rsaPrivateKey) {
        return new UserAuthService(passwordEncoder, simpleUserRepository, rsaPrivateKey, simpleProperties);
    }

    /**
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    @Bean
    public JWTVerifier jwtVerifier() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        ClassPathResource resource = new ClassPathResource(simpleProperties.getJwtRsaPub());
        byte[] keyBytes;
        try (InputStream inputStream = resource.getInputStream()) {
            keyBytes = inputStream.readAllBytes();
        }

        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        RSAPublicKey publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
        return JWT.require(Algorithm.RSA256(publicKey, null))
                .withIssuer(simpleProperties.getJwtIssuer())
                .build();
    }

    @Bean
    public SimpleUserService simpleUserService(@Autowired SimpleUserRepository simpleUserRepository) {
        return new SimpleUserService(simpleUserRepository);
    }
}
