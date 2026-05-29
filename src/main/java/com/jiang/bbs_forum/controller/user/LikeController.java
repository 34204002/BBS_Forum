package com.jiang.bbs_forum.controller.user;

import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.request.LikeRequest;
import com.jiang.bbs_forum.service.user.LikeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    // POST /api/likes — 点赞（帖子/回复）
    @PostMapping
    public Response<?> like(@RequestAttribute("userId") int userId,
                            @Valid @RequestBody LikeRequest request) {
        return likeService.like(userId, request.getTargetType(), request.getTargetId());
    }

    // DELETE /api/likes — 取消点赞
    @DeleteMapping
    public Response<?> unlike(@RequestAttribute("userId") int userId,
                              @RequestParam int targetType,
                              @RequestParam int targetId) {
        return likeService.unlike(userId, targetType, targetId);
    }
}
