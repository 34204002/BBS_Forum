package com.jiang.bbs_forum.controller.user;

import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.request.CreatePostRequest;
import com.jiang.bbs_forum.dto.request.UpdatePostRequest;
import com.jiang.bbs_forum.service.user.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    // GET /api/posts — 获取帖子列表（支持按板块、关键词、排序筛选）
    @GetMapping
    public Response<?> listPosts(@RequestParam(required = false) Integer boardId,
                                 @RequestParam(required = false) String keyword,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(defaultValue = "createTime") String orderBy) {
        return postService.listPosts(boardId, keyword, page, size, orderBy);
    }

    // GET /api/posts/hot — 获取热门帖子
    @GetMapping("/hot")
    public Response<?> getHotPosts(@RequestParam(defaultValue = "10") int size) {
        return postService.getHotPosts(size);
    }

    // GET /api/posts/{id} — 获取帖子详情
    @GetMapping("/{id}")
    public Response<?> getPostById(@PathVariable int id) {
        return postService.getPostById(id);
    }

    // POST /api/posts — 发布帖子（普通帖/需求帖）
    @PostMapping
    public Response<?> createPost(@RequestAttribute("userId") int userId,
                                  @Valid @RequestBody CreatePostRequest request) {
        return postService.createPost(userId, request);
    }

    // PUT /api/posts/{id} — 修改帖子
    @PutMapping("/{id}")
    public Response<?> updatePost(@RequestAttribute("userId") int userId,
                                  @PathVariable("id") int postId,
                                  @Valid @RequestBody UpdatePostRequest request) {
        return postService.updatePost(userId, postId, request);
    }

    // DELETE /api/posts/{id} — 删除帖子
    @DeleteMapping("/{id}")
    public Response<?> deletePost(@RequestAttribute("userId") int userId,
                                  @PathVariable("id") int postId) {
        return postService.deletePost(userId, postId);
    }
}
