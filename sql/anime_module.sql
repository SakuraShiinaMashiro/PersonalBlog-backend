-- Create Anime Subject Table
CREATE TABLE IF NOT EXISTS `blog_anime_subject` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `bgm_id` INT NOT NULL COMMENT 'Bangumi条目ID',
    `title` VARCHAR(255) NOT NULL COMMENT '番剧标题',
    `image_url` VARCHAR(500) COMMENT '封面图URL',
    `eps` INT DEFAULT 0 COMMENT '总集数',
    `air_year` INT COMMENT '放送年份',
    `air_season` INT COMMENT '放送季度 (1:春, 2:夏, 3:秋, 4:冬)',
    `status` TINYINT DEFAULT 0 COMMENT '追番状态 (0:想看, 1:在看, 2:已完成)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_bgm_id` (`bgm_id`),
    INDEX `idx_air_date` (`air_year`, `air_season`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='番剧元数据表';

-- Create Anime Progress Table
CREATE TABLE IF NOT EXISTS `blog_anime_progress` (
    `anime_id` BIGINT NOT NULL COMMENT '关联 blog_anime_subject 的 id',
    `watched_eps` JSON COMMENT '已看集数列表 (JSON 数组, 如 [1,2,3])',
    PRIMARY KEY (`anime_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='追番进度表';
