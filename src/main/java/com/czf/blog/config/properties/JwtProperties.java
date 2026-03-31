package com.czf.blog.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description: JWT 配置属性
 * @author czf
 * @date 2026-03-31
 */
@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;
    private long accessExpireMinutes;
    private long refreshExpireDays;
}
