package com.czf.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.czf.blog.utils.AsyncExecutor;
import com.czf.blog.utils.HashUtils;
import com.czf.blog.entity.BlogEmailCode;
import com.czf.blog.exception.BizException;
import com.czf.blog.exception.code.BizErrorCode;
import com.czf.blog.mapper.BlogEmailCodeMapper;
import com.czf.blog.service.EmailCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @description: 邮箱验证码服务实现
 * @author czf
 * @date 2026-03-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailCodeServiceImpl implements EmailCodeService {
    private static final int CODE_LENGTH = 6;
    private static final int COOLDOWN_SECONDS = 60;
    private static final int EXPIRE_MINUTES = 5;

    private final BlogEmailCodeMapper emailCodeMapper;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String mailFrom;

    /**
     * 生成并发送验证码。
     *
     * @param email 邮箱
     */
    @Override
    public void sendCode(String email) {
        BlogEmailCode latest = emailCodeMapper.selectOne(new LambdaQueryWrapper<BlogEmailCode>()
                .eq(BlogEmailCode::getEmail, email)
                .orderByDesc(BlogEmailCode::getCreateTime)
                .last("limit 1"));
        if (latest != null && latest.getCreateTime() != null) {
            Duration duration = Duration.between(latest.getCreateTime(), LocalDateTime.now());
            if (duration.getSeconds() < COOLDOWN_SECONDS) {
                throw new BizException(BizErrorCode.AUTH_EMAIL_CODE_COOLDOWN);
            }
        }

        String code = generateCode();
        BlogEmailCode entity = new BlogEmailCode();
        entity.setEmail(email);
        entity.setCodeHash(HashUtils.sha256(code));
        entity.setExpireAt(LocalDateTime.now().plusMinutes(EXPIRE_MINUTES));
        entity.setUsed(0);
        emailCodeMapper.insert(entity);

        String subject = "个人博客验证码";
        String content = buildHtmlContent(code);
        AsyncExecutor.execute(() -> {
            boolean sent = sendMail(email, subject, content);
            if (!sent) {
                log.error("验证码异步发送失败, email={}", email);
            }
        });
    }

    /**
     * 校验验证码有效性。
     *
     * @param email 邮箱
     * @param code 验证码
     */
    @Override
    public void verifyCode(String email, String code) {
        BlogEmailCode latest = emailCodeMapper.selectOne(new LambdaQueryWrapper<BlogEmailCode>()
                .eq(BlogEmailCode::getEmail, email)
                .eq(BlogEmailCode::getUsed, 0)
                .orderByDesc(BlogEmailCode::getCreateTime)
                .last("limit 1"));
        if (latest == null) {
            throw new BizException(BizErrorCode.AUTH_EMAIL_CODE_INVALID);
        }
        if (latest.getExpireAt() != null && latest.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new BizException(BizErrorCode.AUTH_EMAIL_CODE_EXPIRED);
        }
        String hashed = HashUtils.sha256(code);
        if (!hashed.equals(latest.getCodeHash())) {
            throw new BizException(BizErrorCode.AUTH_EMAIL_CODE_INVALID);
        }
        latest.setUsed(1);
        emailCodeMapper.updateById(latest);
    }

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        int min = (int) Math.pow(10, CODE_LENGTH - 1);
        int max = (int) Math.pow(10, CODE_LENGTH) - 1;
        int code = random.nextInt(max - min + 1) + min;
        return String.valueOf(code);
    }

    private boolean sendMail(String to, String subject, String content) {
        boolean sent = true;
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            helper.setTo(to);
            if (StringUtils.hasText(mailFrom)) {
                helper.setFrom(mailFrom);
            }
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MailException | MessagingException e) {
            log.error("发送验证码失败", e);
            sent = false;
        }
        return sent;
    }

    private String buildHtmlContent(String code) {
        return "<div style='font-family:Arial,sans-serif;line-height:1.6;'>"
                + "<h3>您的验证码</h3>"
                + "<p>验证码：<strong>" + code + "</strong></p>"
                + "<p>有效期 " + EXPIRE_MINUTES + " 分钟，请尽快使用。</p>"
                + "</div>";
    }
}
