package com.jiang.bbs_forum.service.user.impl;

import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.mapper.FavoriteMapper;
import com.jiang.bbs_forum.service.user.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    public Response<?> favorite(int userId, int postId) {
        // TODO: 1. 检查是否已收藏（联合唯一索引防重复）
        // TODO: 2. 插入favorite记录
        return null;
    }

    @Override
    public Response<?> unfavorite(int userId, int postId) {
        // TODO: 删除favorite记录
        return null;
    }
}
