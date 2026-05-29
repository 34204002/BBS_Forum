package com.jiang.bbs_forum.service.user;

import com.jiang.bbs_forum.common.Response;

public interface FavoriteService {
    Response<?> favorite(int userId, int postId);
    Response<?> unfavorite(int userId, int postId);
}
