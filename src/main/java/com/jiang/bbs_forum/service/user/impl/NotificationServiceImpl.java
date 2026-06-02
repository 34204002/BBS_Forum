package com.jiang.bbs_forum.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiang.bbs_forum.common.PageResponse;
import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.response.NotificationVO;
import com.jiang.bbs_forum.entity.Notification;
import com.jiang.bbs_forum.mapper.NotificationMapper;
import com.jiang.bbs_forum.service.user.NotificationService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Resource
    private NotificationMapper notificationMapper;

    @Override
    public void notifyLike(int fromUserId, int toUserId, int targetType, int targetId) {
        String content;
        if (targetType == 1) {
            content = "点赞了你的帖子";
        } else if (targetType == 2) {
            content = "点赞了你的评论";
        } else {
            throw new IllegalArgumentException("未知targetType: " + targetType);
        }

        insert(fromUserId, toUserId, 1, targetType, targetId, content);
    }

    @Override
    public void notifyComment(int fromUserId, int toUserId, int targetType, int targetId, String commentContent) {
        String prefix = (targetType == 1) ? "评论了你的帖子" : "回复了你的评论";

        String safeContent = commentContent == null ? "" : commentContent;

        // 限制一下长度防止撑爆数据库字段
        String finalContent = prefix + "：" + (safeContent.length() > 20 ? safeContent.substring(0, 20) + "..." : safeContent);

        insert(fromUserId, toUserId, 3, targetType, targetId, finalContent);
    }

    @Override
    public void notifyFavorite(int fromUserId, int toUserId, int postId) {
        insert(fromUserId, toUserId, 5, 1, postId, "收藏了你的帖子");
    }

    @Override
    public void notifyAt(int fromUserId, int toUserId, int targetType, int targetId) {
        insert(fromUserId, toUserId, 6, targetType, targetId, "@了你");
    }

    private void insert(int fromUserId, int toUserId, int type,
                        int targetType, int targetId, String content) {
        Notification n = new Notification();
        n.setFromUserId(fromUserId);
        n.setUserId(toUserId);
        n.setType(type);
        n.setTargetType(targetType);
        n.setTargetId(targetId);
        n.setContent(content);
        n.setIsRead(0);

        LocalDateTime now = LocalDateTime.now();
        n.setCreateTime(now);
        n.setUpdateTime(now);

        notificationMapper.insert(n);
    }

    @Override
    public Response<Long> getUnreadCount(int userId) {

        Long count = notificationMapper.selectCount(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .eq(Notification::getIsRead, 0)
        );
        return Response.success(count);
    }

    @Override
    public Response<PageResponse<NotificationVO>> list(int userId, int page, int size) {
        //  LambdaQueryWrapper 排序
        Page<Notification> mpPage = notificationMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .orderByDesc(Notification::getCreateTime)
        );

        List<NotificationVO> voList = mpPage.getRecords()
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        // 完整填充分页属性，防止前端拿不到当前页数和总页数而崩溃
        PageResponse<NotificationVO> result = new PageResponse<>();
        result.setTotal(mpPage.getTotal());
        result.setList(voList);
        result.setPage(page);
        result.setSize(size);
        result.setPages((int) mpPage.getPages()); // MyBatis-Plus 自动计算出的总页数

        return Response.success(result);
    }

    @Override
    public Response<Void> markRead(int notificationId) {
        Notification n = new Notification();
        n.setId((long) notificationId);
        n.setIsRead(1);

        notificationMapper.updateById(n);
        return Response.success(null);
    }

    @Override
    public Response<Void> markAllRead(int userId) {
        Notification n = new Notification();
        n.setIsRead(1);

        notificationMapper.update(
                n,
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .eq(Notification::getIsRead, 0)
        );

        return Response.success(null);
    }

    private NotificationVO toVO(Notification n) {
        NotificationVO vo = new NotificationVO();
        vo.setId(n.getId());
        vo.setFromUserId(n.getFromUserId());
        vo.setType(n.getType());
        vo.setTargetType(n.getTargetType());
        vo.setTargetId(n.getTargetId());
        vo.setContent(n.getContent());
        vo.setIsRead(n.getIsRead());
        vo.setCreateTime(n.getCreateTime());
        return vo;
    }
}