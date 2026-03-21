package com.czf.blog.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "blog_anime_progress", autoResultMap = true)
public class AnimeProgress {
    @TableId
    private Long animeId;

    private Integer status;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Integer> watchedEps;

    private LocalDate trackDate;

    private LocalDateTime lastWatchAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
