package com.czf.blog.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: OAuth 配置属性
 * @author czf
 * @date 2026-03-31
 */
@Data
@ConfigurationProperties(prefix = "oauth")
public class OAuthProperties {
    private Map<String, Provider> providers = new HashMap<>();

    /**
     * OAuth 平台配置
     */
    @Data
    public static class Provider {
        private boolean enabled;
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private String authorizeUrl;
        private String tokenUrl;
        private String userInfoUrl;
        private String scope;
        private String userIdField;
        private String usernameField;
        private String avatarField;
        private boolean accessTokenInQuery;
        private String accessTokenField;
    }
}
