package com.jiang.bbs_forum.service.user.impl;

import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.mapper.CommentMapper;
import com.jiang.bbs_forum.mapper.LikeMapper;
import com.jiang.bbs_forum.mapper.PostMapper;
import com.jiang.bbs_forum.service.user.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private CommentMapper commentMapper;

    @Override
    public Response<?> like(int userId, int targetType, int targetId) {
        // TODO: 1. 检查是否已点赞（联合唯一索引防重复）
        // TODO: 2. 插入like记录
        // TODO: 3. 更新帖子/回复的like_count
        return null;
    }

    @Override
    public Response<?> unlike(int userId, int targetType, int targetId) {
        // TODO: 1. 删除like记录
        // TODO: 2. 更新帖子/回复的like_count
        return null;
    }
}
