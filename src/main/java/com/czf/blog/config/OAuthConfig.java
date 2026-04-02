package com.czf.blog.config;

import com.czf.blog.config.properties.OAuthProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @description: OAuth 配置加载
 * @author czf
 * @date 2026-03-31
 */
@Configuration
@EnableConfigurationProperties(OAuthProperties.class)
public class OAuthConfig {
}
