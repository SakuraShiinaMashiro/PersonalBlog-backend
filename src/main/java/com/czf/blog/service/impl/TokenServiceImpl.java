package com.czf.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.czf.blog.utils.HashUtils;
import com.czf.blog.config.properties.JwtProperties;
import com.czf.blog.dto.TokenPairVO;
import com.czf.blog.entity.BlogRefreshToken;
import com.czf.blog.entity.BlogUser;
import com.czf.blog.exception.BizException;
import com.czf.blog.exception.code.BizErrorCode;
import com.czf.blog.mapper.BlogRefreshTokenMapper;
import com.czf.blog.mapper.BlogUserMapper;
import com.czf.blog.security.UserPrincipal;
import com.czf.blog.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

/**
 * @description: Token 服务实现
 * @author czf
 * @date 2026-03-31
 */
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final JwtProperties jwtProperties;
    private final BlogRefreshTokenMapper refreshTokenMapper;
    private final BlogUserMapper userMapper;

    /**
     * 签发 Access/Refresh Token。
     *
     * @param user 用户信息
     * @return Token 对
     */
    @Override
    public TokenPairVO issueTokens(BlogUser user) {
        String accessToken = buildAccessToken(user);
        String refreshToken = UUID.randomUUID().toString().replace("-", "");
        BlogRefreshToken record = new BlogRefreshToken();
        record.setUserId(user.getId());
        record.setTokenHash(HashUtils.sha256(refreshToken));
        record.setExpireAt(LocalDateTime.now().plusDays(jwtProperties.getRefreshExpireDays()));
        refreshTokenMapper.insert(record);
        return new TokenPairVO(accessToken, refreshToken);
    }

    /**
     * 刷新 Access Token。
     *
     * @param refreshToken Refresh Token
     * @return Token 对
     */
    @Override
    public TokenPairVO refresh(String refreshToken) {
        String hash = HashUtils.sha256(refreshToken);
        BlogRefreshToken record = refreshTokenMapper.selectOne(new LambdaQueryWrapper<BlogRefreshToken>()
                .eq(BlogRefreshToken::getTokenHash, hash)
                .last("limit 1"));
        if (record == null) {
            throw new BizException(BizErrorCode.AUTH_REFRESH_INVALID);
        }
        if (record.getExpireAt() != null && record.getExpireAt().isBefore(LocalDateTime.now())) {
            refreshTokenMapper.deleteById(record.getId());
            throw new BizException(BizErrorCode.AUTH_REFRESH_INVALID);
        }
        BlogUser user = userMapper.selectById(record.getUserId());
        if (user == null) {
            refreshTokenMapper.deleteById(record.getId());
            throw new BizException(BizErrorCode.AUTH_REFRESH_INVALID);
        }
        refreshTokenMapper.deleteById(record.getId());
        return issueTokens(user);
    }

    /**
     * 使 Refresh Token 失效。
     *
     * @param refreshToken Refresh Token
     */
    @Override
    public void invalidate(String refreshToken) {
        String hash = HashUtils.sha256(refreshToken);
        refreshTokenMapper.delete(new LambdaQueryWrapper<BlogRefreshToken>()
                .eq(BlogRefreshToken::getTokenHash, hash));
    }

    /**
     * 解析 Access Token 获取用户信息。
     *
     * @param token Access Token
     * @return 用户信息模型，解析失败返回 null
     */
    @Override
    public UserPrincipal parseAccessToken(String token) {
        UserPrincipal principal = null;
        boolean failed = false;
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            Long userId = claims.get("uid", Long.class);
            String username = claims.get("uname", String.class);
            String role = claims.get("role", String.class);
            principal = new UserPrincipal(userId, username, role);
        } catch (JwtException | IllegalArgumentException e) {
            failed = true;
        }
        if (failed) {
            return null;
        }
        return principal;
    }

    private String buildAccessToken(BlogUser user) {
        Date expireAt = Date.from(LocalDateTime.now()
                .plusMinutes(jwtProperties.getAccessExpireMinutes())
                .atZone(ZoneId.systemDefault())
                .toInstant());
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("uid", user.getId())
                .claim("uname", user.getUsername())
                .claim("role", user.getRole())
                .setExpiration(expireAt)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
