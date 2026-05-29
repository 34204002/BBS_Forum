package com.jiang.bbs_forum.controller.user;

import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.request.FavoriteRequest;
import com.jiang.bbs_forum.service.user.FavoriteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    // POST /api/favorites — 收藏帖子
    @PostMapping
    public Response<?> favorite(@RequestAttribute("userId") int userId,
                                @Valid @RequestBody FavoriteRequest request) {
        return favoriteService.favorite(userId, request.getPostId());
    }

    // DELETE /api/favorites — 取消收藏（?postId=xxx）
    @DeleteMapping
    public Response<?> unfavorite(@RequestAttribute("userId") int userId,
                                  @RequestParam int postId) {
        return favoriteService.unfavorite(userId, postId);
    }
}
