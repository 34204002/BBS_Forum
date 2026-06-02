package com.jiang.bbs_forum.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知前端展示对象
 * 不直接暴露 entity
 */
@Data
public class NotificationVO {

    private Long id;

    private Integer fromUserId;

    private String fromUserName;

    private Integer type;

    private Integer targetType;

    private Integer targetId;

    private String content;

    private Integer isRead;

    private LocalDateTime createTime;
}