-- 用户认证与权限校验模块表结构
Drop Table If Exists `blog_user`;
CREATE TABLE IF NOT EXISTS `blog_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role` VARCHAR(20) NOT NULL COMMENT '角色: OWNER/VISITOR',
  `username` VARCHAR(64) NOT NULL COMMENT '用户名',
  `password_hash` VARCHAR(255) DEFAULT NULL COMMENT '密码哈希(仅博主)',
  `email` VARCHAR(128) DEFAULT NULL COMMENT '邮箱(仅博主)',
  `email_verified` TINYINT DEFAULT 0 COMMENT '邮箱是否验证(0否1是)',
  `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `status` TINYINT DEFAULT 1 COMMENT '状态(1正常0禁用)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

Drop Table If Exists `blog_user_oauth`;
CREATE TABLE IF NOT EXISTS `blog_user_oauth` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `provider` VARCHAR(20) NOT NULL COMMENT '第三方平台: github/google/sina',
  `provider_user_id` VARCHAR(128) NOT NULL COMMENT '平台用户ID',
  `access_token` VARCHAR(255) DEFAULT NULL COMMENT 'OAuth Access Token(可选)',
  `refresh_token` VARCHAR(255) DEFAULT NULL COMMENT 'OAuth Refresh Token(可选)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_provider_user` (`provider`, `provider_user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='OAuth 关联表';

drop table if exists `blog_owner_key`;
CREATE TABLE IF NOT EXISTS `blog_owner_key` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `key_hash` VARCHAR(255) NOT NULL COMMENT '密钥哈希',
  `enabled` TINYINT DEFAULT 1 COMMENT '是否启用(1启用0禁用)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='博主登录入口密钥';

CREATE TABLE IF NOT EXISTS `blog_refresh_token` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `token_hash` VARCHAR(255) NOT NULL COMMENT 'Refresh Token 哈希',
  `expire_at` DATETIME NOT NULL COMMENT '过期时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Refresh Token 表';

CREATE TABLE IF NOT EXISTS `blog_email_code` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `email` VARCHAR(128) NOT NULL COMMENT '邮箱',
  `code_hash` VARCHAR(255) NOT NULL COMMENT '验证码哈希',
  `expire_at` DATETIME NOT NULL COMMENT '过期时间',
  `used` TINYINT DEFAULT 0 COMMENT '是否已使用(0否1是)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='邮箱验证码表';

-- 初始数据示例（已填入可用值）
-- 1) 初始化博主账号（OWNER）
-- 密码为 BCrypt 加密值
INSERT INTO `blog_user` (
  `role`, `username`, `password_hash`, `email`, `email_verified`, `avatar_url`, `status`
) VALUES (
  'OWNER', 'challenge', '$2a$10$NgV.8ngzsqg1Ai.dayRsJuRF/PPSKFxpQf/Q8Nk8xw/Dz2FwTbsBG', '871953354@qq.com', 1, 'https://uploadfiles.nowcoder.com/images/20250812/296966364_1755006903365/FECD76F09C4EFFA7102ECDBC1795FB3B', 1
);

-- 2) 初始化密钥哈希（用于导入密钥解锁）
-- 密钥文件内容使用 SHA-256 计算后写入
INSERT INTO `blog_owner_key` (`key_hash`, `enabled`) VALUES ('73d750027fc1ad4dea9e470d77ca39ece0758f86e2e00b8aff993f72639d0551', 1);
