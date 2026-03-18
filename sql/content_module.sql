-- 内容主表 (blog_content)
CREATE TABLE IF NOT EXISTS `blog_content` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` VARCHAR(200) NOT NULL COMMENT '文章标题',
  `summary` VARCHAR(500) DEFAULT NULL COMMENT '摘要',
  `content` MEDIUMTEXT NOT NULL COMMENT 'Markdown原文本',
  `module_type` TINYINT NOT NULL COMMENT '模块类型: 0-学习记录, 1-生活随笔, 2-兴趣使然',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-草稿, 1-已发布',
  `cover_url` VARCHAR(500) DEFAULT NULL COMMENT '封面图URL',
  `views` INT NOT NULL DEFAULT 0 COMMENT '阅读量统计',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_module_type` (`module_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='核心内容表';

-- 标签表 (blog_tag)
CREATE TABLE IF NOT EXISTS `blog_tag` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签表';

-- 内容与标签关联表 (blog_content_tag)
CREATE TABLE IF NOT EXISTS `blog_content_tag` (
  `content_id` BIGINT NOT NULL COMMENT '文章ID',
  `tag_id` BIGINT NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`content_id`, `tag_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章与标签关联表';
