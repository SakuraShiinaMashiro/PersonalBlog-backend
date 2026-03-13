package com.czf.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("blog_anime_subject")
public class AnimeSubject {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Integer bgmId;
    
    private String title;
    
    private String imageUrl;
    
    private Integer eps;
    
    private Integer airYear;
    
    private Integer airSeason;
    
    private Integer status; // 0想看, 1在看, 2已完成

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
