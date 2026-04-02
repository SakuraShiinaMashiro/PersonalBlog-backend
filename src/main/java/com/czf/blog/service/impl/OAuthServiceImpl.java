package com.czf.blog.service.impl;

import com.czf.blog.config.properties.OAuthProperties;
import com.czf.blog.dto.OAuthUserInfoDTO;
import com.czf.blog.dto.TokenPairVO;
import com.czf.blog.entity.BlogUser;
import com.czf.blog.exception.BizException;
import com.czf.blog.exception.code.BizErrorCode;
import com.czf.blog.service.OAuthService;
import com.czf.blog.service.TokenService;
import com.czf.blog.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @description: OAuth 登录服务实现
 * @author czf
 * @date 2026-03-31
 */
@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {
    private final OAuthProperties oAuthProperties;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final TokenService tokenService;

    private static final String DEFAULT_ACCESS_TOKEN_FIELD = "access_token";

    /**
     * 获取第三方登录授权地址。
     *
     * @param provider 平台标识
     * @return 授权跳转地址
     */
    @Override
    public String getAuthorizeUrl(String provider) {
        OAuthProperties.Provider config = getProviderConfig(provider);
        String scope = StringUtils.hasText(config.getScope()) ? config.getScope() : "";
        String url = config.getAuthorizeUrl()
                + "?client_id=" + encode(config.getClientId())
                + "&redirect_uri=" + encode(config.getRedirectUri())
                + "&response_type=code";
        if (StringUtils.hasText(scope)) {
            url = url + "&scope=" + encode(scope);
        }
        return url;
    }

    /**
     * 处理 OAuth 回调并签发 Token。
     *
     * @param provider 平台标识
     * @param code 授权码
     * @param state 状态码
     * @return Token 对
     */
    @Override
    public TokenPairVO handleCallback(String provider, String code, String state) {
        OAuthProperties.Provider config = getProviderConfig(provider);
        String accessToken = exchangeAccessToken(config, code);
        OAuthUserInfoDTO userInfo = fetchUserInfo(config, provider, accessToken);
        BlogUser user = userService.findOrCreateVisitor(userInfo);
        return tokenService.issueTokens(user);
    }

    private OAuthProperties.Provider getProviderConfig(String provider) {
        if (!StringUtils.hasText(provider)) {
            throw new BizException(BizErrorCode.AUTH_OAUTH_NOT_SUPPORTED);
        }
        Map<String, OAuthProperties.Provider> providers = oAuthProperties.getProviders();
        OAuthProperties.Provider config = providers.get(provider);
        if (config == null || !config.isEnabled()) {
            throw new BizException(BizErrorCode.AUTH_OAUTH_NOT_SUPPORTED);
        }
        if (!StringUtils.hasText(config.getClientId()) || !StringUtils.hasText(config.getClientSecret())
                || !StringUtils.hasText(config.getAuthorizeUrl()) || !StringUtils.hasText(config.getTokenUrl())
                || !StringUtils.hasText(config.getUserInfoUrl())) {
            throw new BizException(BizErrorCode.AUTH_OAUTH_NOT_SUPPORTED);
        }
        if (!StringUtils.hasText(config.getUserIdField()) || !StringUtils.hasText(config.getUsernameField())) {
            throw new BizException(BizErrorCode.AUTH_OAUTH_NOT_SUPPORTED);
        }
        return config;
    }

    private String exchangeAccessToken(OAuthProperties.Provider config, String code) {
        RestClient client = RestClient.builder().build();
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", config.getClientId());
        body.add("client_secret", config.getClientSecret());
        body.add("code", code);
        if (StringUtils.hasText(config.getRedirectUri())) {
            body.add("redirect_uri", config.getRedirectUri());
        }
        body.add("grant_type", "authorization_code");
        JsonNode response = client.post()
                .uri(config.getTokenUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(JsonNode.class);
        if (response == null) {
            throw new BizException(BizErrorCode.AUTH_OAUTH_NOT_SUPPORTED);
        }
        String accessTokenField = StringUtils.hasText(config.getAccessTokenField())
                ? config.getAccessTokenField()
                : DEFAULT_ACCESS_TOKEN_FIELD;
        JsonNode tokenNode = response.get(accessTokenField);
        if (tokenNode == null || !tokenNode.isTextual()) {
            throw new BizException(BizErrorCode.AUTH_OAUTH_NOT_SUPPORTED);
        }
        return tokenNode.asText();
    }

    private OAuthUserInfoDTO fetchUserInfo(OAuthProperties.Provider config, String provider, String accessToken) {
        RestClient client = RestClient.builder().build();
        String url = config.getUserInfoUrl();
        JsonNode response;
        if (config.isAccessTokenInQuery()) {
            url = url + (url.contains("?") ? "&" : "?") + "access_token=" + encode(accessToken);
            response = client.get()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(JsonNode.class);
        } else {
            response = client.get()
                    .uri(url)
                    .header("Authorization", "Bearer " + accessToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(JsonNode.class);
        }
        if (response == null) {
            throw new BizException(BizErrorCode.AUTH_OAUTH_NOT_SUPPORTED);
        }
        JsonNode userIdNode = response.get(config.getUserIdField());
        JsonNode usernameNode = response.get(config.getUsernameField());
        if (userIdNode == null || usernameNode == null) {
            throw new BizException(BizErrorCode.AUTH_OAUTH_NOT_SUPPORTED);
        }
        String avatar = null;
        if (StringUtils.hasText(config.getAvatarField())) {
            JsonNode avatarNode = response.get(config.getAvatarField());
            if (avatarNode != null && avatarNode.isTextual()) {
                avatar = avatarNode.asText();
            }
        }
        return new OAuthUserInfoDTO(provider, userIdNode.asText(), usernameNode.asText(), avatar);
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
