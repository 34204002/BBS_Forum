package com.jiang.bbs_forum.controller.user;

import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.request.LikeBatchRequest;
import com.jiang.bbs_forum.dto.request.LikeRequest;
import com.jiang.bbs_forum.dto.response.LikeBatchVO;
import com.jiang.bbs_forum.service.user.LikeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
@Slf4j
public class LikeController {

    @Autowired
    private LikeService likeService;


    /**
     * 点赞（帖子或回复）
     */
    @PostMapping
    public Response<Void> like(
            @RequestAttribute("userId") int userId,
            @Valid @RequestBody LikeRequest request) {

        log.info(
                "POST /api/likes - 点赞, userId={}, targetType={}, targetId={}",
                userId,
                request.getTargetType(),
                request.getTargetId()
        );

        return likeService.like(
                userId,
                request.getTargetType(),
                request.getTargetId()
        );

    }
    @PostMapping("/batch-status")
    public Response<LikeBatchVO> batchStatus(
            @RequestAttribute("userId") int userId,
            @RequestBody LikeBatchRequest request) {

        return likeService.batchStatus(userId, request);
    }
    /**
     * 取消点赞
     */
    @DeleteMapping
    public Response<Void> unlike(
            @RequestAttribute("userId") int userId,
            @RequestBody LikeRequest request) {

        log.info("DELETE /api/likes - 取消点赞, userId={}", userId);

        return likeService.unlike(
                userId,
                request.getTargetType(),
                request.getTargetId()
        );
    }

    /**
     * 查询当前用户是否已点赞
     */
    @GetMapping("/status")
    public Response<Boolean> isLiked(
            @RequestAttribute("userId") int userId,
            @RequestParam int targetType,
            @RequestParam int targetId) {

        log.info("GET /api/likes/status - 查询点赞状态, userId={}", userId);

        return likeService.isLiked(
                userId,
                targetType,
                targetId
        );
    }

}
