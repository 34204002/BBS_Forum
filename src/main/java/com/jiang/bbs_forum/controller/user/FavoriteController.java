package com.jiang.bbs_forum.controller.user;

import com.jiang.bbs_forum.common.PageResponse;
import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.request.FavoriteRequest;
import com.jiang.bbs_forum.dto.response.PostVO;
import com.jiang.bbs_forum.service.user.FavoriteService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
@Slf4j
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /**
     * 收藏帖子
     */
    @PostMapping
    public Response<Void> favorite(
            @RequestAttribute("userId") int userId,
            @Valid @RequestBody FavoriteRequest request) {
        log.info("POST /api/favorites - 收藏, userId={}", userId);
        return favoriteService.favorite(userId, request.getPostId());
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/{postId}")
    public Response<Void> unfavorite(
            @RequestAttribute("userId") int userId,
            @PathVariable int postId) {

        return favoriteService.unfavorite(userId, postId);
    }

    /**
     * 收藏列表
     */
    @GetMapping("/list")
    public Response<PageResponse<PostVO>> listFavorites(
            @RequestAttribute("userId") int userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        return favoriteService.listFavorites(userId, page, size);
    }
}