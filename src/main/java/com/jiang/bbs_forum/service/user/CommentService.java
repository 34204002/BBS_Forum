package com.jiang.bbs_forum.service.user;

import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.request.CreateCommentRequest;
import com.jiang.bbs_forum.dto.request.UpdateCommentRequest;

public interface CommentService {
    Response<?> listComments(int postId, int page, int size);
    Response<?> createComment(int userId, CreateCommentRequest request);
    Response<?> updateComment(int userId, int commentId, UpdateCommentRequest request);
    Response<?> deleteComment(int userId, int commentId);
}
