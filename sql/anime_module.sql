-- Create Anime Subject Table
CREATE TABLE IF NOT EXISTS `blog_anime_subject` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `bgm_id` INT NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `image_url` VARCHAR(500),
    `eps` INT DEFAULT 0,
    `air_year` INT,
    `air_season` INT,
    `status` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_bgm_id` (`bgm_id`),
    INDEX `idx_air_date` (`air_year`, `air_season`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Create Anime Progress Table
CREATE TABLE IF NOT EXISTS `blog_anime_progress` (
    `anime_id` BIGINT NOT NULL,
    `watched_eps` JSON,
    PRIMARY KEY (`anime_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
