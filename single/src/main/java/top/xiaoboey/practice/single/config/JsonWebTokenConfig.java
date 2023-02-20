package top.xiaoboey.practice.single.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
public class JsonWebTokenConfig {
    public static final String CLAIM_AUTHORITIES = "authorities";
    public static final String CLAIM_JTI = "jti";

    @Value("${my.jwt.issuer}")
    private String issuer;

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
    public RSAPrivateKey jwtPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        ClassPathResource resource = new ClassPathResource("jwt_rsa.key");
        byte[] keyBytes;
        try (InputStream inputStream = resource.getInputStream()) {
            keyBytes = inputStream.readAllBytes();
        }
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    /**
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    @Bean
    public JWTVerifier jwtVerifier() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        ClassPathResource resource = new ClassPathResource("jwt_rsa.pub");
        byte[] keyBytes;
        try(InputStream inputStream = resource.getInputStream()) {
            keyBytes = inputStream.readAllBytes();
        }

        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        RSAPublicKey publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
        return JWT.require(Algorithm.RSA256(publicKey, null))
                .withIssuer(issuer)
                .build();
    }
}
