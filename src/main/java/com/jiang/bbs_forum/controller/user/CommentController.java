package com.jiang.bbs_forum.controller.user;

import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.request.CreateCommentRequest;
import com.jiang.bbs_forum.dto.request.UpdateCommentRequest;
import com.jiang.bbs_forum.service.user.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // GET /api/posts/{postId}/comments — 获取帖子回复列表（含楼中楼）
    @GetMapping("/posts/{postId}/comments")
    public Response<?> listComments(@PathVariable int postId,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "20") int size) {
        return commentService.listComments(postId, page, size);
    }

    // POST /api/comments — 发布回复（一级回复/楼中楼）
    @PostMapping("/comments")
    public Response<?> createComment(@RequestAttribute("userId") int userId,
                                     @Valid @RequestBody CreateCommentRequest request) {
        return commentService.createComment(userId, request);
    }

    // PUT /api/comments/{id} — 修改回复
    @PutMapping("/comments/{id}")
    public Response<?> updateComment(@RequestAttribute("userId") int userId,
                                     @PathVariable("id") int commentId,
                                     @Valid @RequestBody UpdateCommentRequest request) {
        return commentService.updateComment(userId, commentId, request);
    }

    // DELETE /api/comments/{id} — 删除回复
    @DeleteMapping("/comments/{id}")
    public Response<?> deleteComment(@RequestAttribute("userId") int userId,
                                     @PathVariable("id") int commentId) {
        return commentService.deleteComment(userId, commentId);
    }
}
