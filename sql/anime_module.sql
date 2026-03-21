-- V2.0: local development rebuild script
-- WARNING: only execute in local development environment.

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `blog_anime_progress`;
DROP TABLE IF EXISTS `blog_anime_subject`;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `blog_anime_subject` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `bgm_id` INT NOT NULL COMMENT 'Bangumi条目ID',
    `title` VARCHAR(255) NOT NULL COMMENT '番剧标题',
    `image_url` VARCHAR(500) NULL COMMENT '封面图URL',
    `eps` INT DEFAULT 0 COMMENT '总集数',
    `air_date` DATE NULL COMMENT '首播日期',
    `air_year` INT NULL COMMENT '首播年份（由air_date派生）',
    `air_season` TINYINT NULL COMMENT '首播季度（1冬,2春,3夏,4秋）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_bgm_id` (`bgm_id`),
    KEY `idx_air_date` (`air_year`, `air_season`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='番剧元数据表';

CREATE TABLE `blog_anime_progress` (
    `anime_id` BIGINT NOT NULL COMMENT '关联 blog_anime_subject.id',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '追番状态 (0:想看, 1:在看, 2:已看完)',
    `watched_eps` JSON NULL COMMENT '已看集数列表(JSON数组)',
    `track_date` DATE NOT NULL COMMENT '开始追番时间',
    `last_watch_at` DATETIME NULL COMMENT '最近观看时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`anime_id`),
    KEY `idx_status_track_date` (`status`, `track_date`),
    CONSTRAINT `fk_progress_subject` FOREIGN KEY (`anime_id`) REFERENCES `blog_anime_subject` (`id`) ON DELETE CASCADE,
    CONSTRAINT `chk_progress_status` CHECK (`status` IN (0, 1, 2))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='追番进度表';
