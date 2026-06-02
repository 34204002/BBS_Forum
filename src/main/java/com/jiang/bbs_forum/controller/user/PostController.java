package com.jiang.bbs_forum.controller.user;

import lombok.extern.slf4j.Slf4j;

import com.jiang.bbs_forum.common.PageResponse;
import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.request.CreatePostRequest;
import com.jiang.bbs_forum.dto.request.UpdatePostRequest;
import com.jiang.bbs_forum.dto.response.PostDetailVO;
import com.jiang.bbs_forum.dto.response.PostVO;
import com.jiang.bbs_forum.service.user.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@Slf4j
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * 获取帖子列表（支持按板块、关键词筛选和排序）
     */
    @GetMapping
    public Response<PageResponse<PostVO>> listPosts(
            @RequestParam(required = false) Integer boardId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createTime") String orderBy) {
        log.info("GET /api/posts - 获取帖子列表");
        return postService.listPosts(boardId, keyword, page, size, orderBy);
    }

    /**
     * 获取热门帖子（近7天热度排行）
     */
    @GetMapping("/hot")
    public Response<List<PostVO>> getHotPosts(@RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/posts/hot - 获取热门帖子");
        return postService.getHotPosts(size);
    }

    /**
     * 根据帖子ID获取详情（浏览量+1）
     */
    @GetMapping("/{id}")
    public Response<PostVO> getPostById(@PathVariable int id) {
        log.info("GET /api/posts/{} - 根据帖子ID获取详情", id);
        return postService.getPostById(id);
    }

    /**
     * 获取帖子详情（含评论树和点赞收藏状态，支持游客访问）
     */
    @GetMapping("/{id}/detail")
    public Response<PostDetailVO> getPostDetail(
            @RequestAttribute(value = "userId", required = false) Integer userId,
            @PathVariable("id") int postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/posts/{}/detail userId={}", postId, userId);
        return postService.getPostDetail(userId, postId, page, size);
    }

    /**
     * 发布帖子（普通帖/需求帖）
     */
    @PostMapping
    public Response<PostVO> createPost(@RequestAttribute("userId") int userId,
                                       @Valid @RequestBody CreatePostRequest request) {
        log.info("POST /api/posts - 发布帖子, userId={}", userId);
        return postService.createPost(userId, request);
    }

    /**
     * 修改帖子（仅作者本人）
     */
    @PutMapping("/{id}")
    public Response<PostVO> updatePost(@RequestAttribute("userId") int userId,
                                       @PathVariable("id") int postId,
                                       @Valid @RequestBody UpdatePostRequest request) {
        log.info("PUT /api/posts/{} - 修改帖子, userId={}", postId, userId);
        return postService.updatePost(userId, postId, request);
    }

    /**
     * 删除帖子（作者或管理员）
     */
    @DeleteMapping("/{id}")
    public Response<Void> deletePost(@RequestAttribute("userId") int userId,
                                     @PathVariable("id") int postId) {
        log.info("DELETE /api/posts/{} - 删除帖子, userId={}", postId, userId);
        return postService.deletePost(userId, postId);
    }
}
