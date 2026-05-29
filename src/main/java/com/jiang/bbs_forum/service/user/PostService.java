package com.jiang.bbs_forum.service.user;

import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.request.CreatePostRequest;
import com.jiang.bbs_forum.dto.request.UpdatePostRequest;

public interface PostService {
    Response<?> listPosts(Integer boardId, String keyword, int page, int size, String orderBy);
    Response<?> getHotPosts(int size);
    Response<?> getPostById(int id);
    Response<?> createPost(int userId, CreatePostRequest request);
    Response<?> updatePost(int userId, int postId, UpdatePostRequest request);
    Response<?> deletePost(int userId, int postId);
}
