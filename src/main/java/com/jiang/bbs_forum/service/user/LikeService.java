package com.jiang.bbs_forum.service.user;

import com.jiang.bbs_forum.common.Response;

public interface LikeService {
    Response<?> like(int userId, int targetType, int targetId);
    Response<?> unlike(int userId, int targetType, int targetId);
}
