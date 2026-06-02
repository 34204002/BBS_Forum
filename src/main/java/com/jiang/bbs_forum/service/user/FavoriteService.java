package com.jiang.bbs_forum.service.user;

import com.jiang.bbs_forum.common.PageResponse;
import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.response.PostVO;

public interface FavoriteService {
    Response<Void> favorite(int userId, int postId);

    Response<Void> unfavorite(int userId, int postId);

    Response<PageResponse<PostVO>> listFavorites(int userId, int page, int size);

}
