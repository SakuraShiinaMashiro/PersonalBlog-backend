package com.czf.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * RestClient 配置文件
 * @author Gemini
 * @date 2026-03-18
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient bangumiRestClient() {
        return RestClient.builder()
                .baseUrl("https://api.bgm.tv/v0")
                .defaultHeader("User-Agent", "SakuraShiinaMashiro/PersonalBlog (https://github.com/SakuraShiinaMashiro)")
                .build();
    }
}
