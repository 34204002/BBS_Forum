package com.jiang.bbs_forum.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("likes")
public class Like {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("user_id")
    private Integer userId;

    @TableField("target_type")
    private Integer targetType;

    @TableField("target_id")
    private Integer targetId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

