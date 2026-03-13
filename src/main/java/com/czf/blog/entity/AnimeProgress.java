package com.czf.blog.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import java.util.List;

@Data
@TableName(value = "blog_anime_progress", autoResultMap = true)
public class AnimeProgress {
    @TableId
    private Long animeId;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Integer> watchedEps;
}
