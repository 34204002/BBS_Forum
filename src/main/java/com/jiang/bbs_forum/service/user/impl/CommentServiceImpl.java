package com.jiang.bbs_forum.service.user.impl;

import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.request.CreateCommentRequest;
import com.jiang.bbs_forum.dto.request.UpdateCommentRequest;
import com.jiang.bbs_forum.mapper.CommentMapper;
import com.jiang.bbs_forum.mapper.PostMapper;
import com.jiang.bbs_forum.service.user.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private PostMapper postMapper;

    @Override
    public Response<?> listComments(int postId, int page, int size) {
        // TODO: 1. 查询一级回复（parent_id IS NULL），分页
        // TODO: 2. 每个一级回复下查询子回复（楼中楼）
        // TODO: 3. 构建树形结构返回
        return null;
    }

    @Override
    public Response<?> createComment(int userId, CreateCommentRequest request) {
        // TODO: 1. 插入回复记录
        // TODO: 2. 更新帖子comment_count
        return null;
    }

    @Override
    public Response<?> updateComment(int userId, int commentId, UpdateCommentRequest request) {
        // TODO: 1. 校验回复存在且未删除
        // TODO: 2. 校验是否为回复作者（非作者返回403）
        // TODO: 3. 更新内容
        return null;
    }

    @Override
    public Response<?> deleteComment(int userId, int commentId) {
        // TODO: 1. 校验回复存在
        // TODO: 2. 校验是否为回复作者或管理员
        // TODO: 3. 逻辑删除（级联删除子回复、点赞记录）
        // TODO: 4. 更新帖子comment_count
        return null;
    }
}
