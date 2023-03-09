package top.xiaoboey.practice.spring.boot3.simplestarter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

/**
 * @author xiaoqb
 */
@ConfigurationProperties(prefix = "simple.starter")
public class SimpleProperties {
    private String jwtIssuer = "simple";
    private String jwtRsaPub = "jwt_rsa.pub";
    private String jwtRsaKey = "jwt_rsa.key";
    private Boolean enableJwtPrivateKey = Boolean.TRUE;
    private List<String> permitAllRequests = Arrays.asList("/actuator/**", "/user/**");

    public String getJwtIssuer() {
        return jwtIssuer;
    }

    public void setJwtIssuer(String jwtIssuer) {
        this.jwtIssuer = jwtIssuer;
    }

    public String getJwtRsaPub() {
        return jwtRsaPub;
    }

    public void setJwtRsaPub(String jwtRsaPub) {
        this.jwtRsaPub = jwtRsaPub;
    }

    public String getJwtRsaKey() {
        return jwtRsaKey;
    }

    public void setJwtRsaKey(String jwtRsaKey) {
        this.jwtRsaKey = jwtRsaKey;
    }

    public Boolean getEnableJwtPrivateKey() {
        return enableJwtPrivateKey;
    }

    public void setEnableJwtPrivateKey(Boolean enableJwtPrivateKey) {
        this.enableJwtPrivateKey = enableJwtPrivateKey;
    }

    public List<String> getPermitAllRequests() {
        return permitAllRequests;
    }

    public void setPermitAllRequests(List<String> permitAllRequests) {
        this.permitAllRequests = permitAllRequests;
    }
}
