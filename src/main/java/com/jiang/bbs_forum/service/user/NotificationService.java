package com.jiang.bbs_forum.service.user;

import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.response.NotificationVO;
import com.jiang.bbs_forum.common.PageResponse;

public interface NotificationService {

    void notifyLike(int fromUserId, int toUserId, int targetType, int targetId);

    void notifyComment(int fromUserId, int toUserId, int targetType, int targetId, String content);

    void notifyFavorite(int fromUserId, int toUserId, int postId);

    void notifyAt(int fromUserId, int toUserId, int targetType, int targetId);

    Response<Long> getUnreadCount(int userId);

    Response<PageResponse<NotificationVO>> list(int userId, int page, int size);

    Response<Void> markRead(int notificationId);

    Response<Void> markAllRead(int userId);
}