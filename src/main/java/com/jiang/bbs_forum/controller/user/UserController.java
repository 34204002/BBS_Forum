package com.jiang.bbs_forum.controller.user;

import lombok.extern.slf4j.Slf4j;

import com.jiang.bbs_forum.common.PageResponse;
import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.request.ChangePasswordRequest;
import com.jiang.bbs_forum.dto.request.UpdateProfileRequest;
import com.jiang.bbs_forum.dto.response.*;
import com.jiang.bbs_forum.service.user.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private PointService pointService;

    /**
     * 获取当前登录用户完整信息
     */
    @GetMapping("/current")
    public Response<UserVO> getCurrentUser(@RequestAttribute("userId") int userId) {
        log.info("GET /api/users/current - 获取当前用户, userId={}", userId);
        return userService.getCurrentUser(userId);
    }

    /**
     * 根据用户ID获取公开信息
     */
    @GetMapping("/{id}")
    public Response<UserVO> getUserById(@PathVariable int id) {
        log.info("GET /api/users/{} - 查看用户公开信息", id);
        return userService.getUserById(id);
    }

    /**
     * 修改个人资料
     */
    @PutMapping("/profile")
    public Response<ProfileVO> updateProfile(@RequestAttribute("userId") int userId,
                                             @Valid @RequestBody UpdateProfileRequest request) {
        log.info("PUT /api/users/profile - 修改个人资料, userId={}", userId);
        return userService.updateProfile(userId, request);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Response<Void> changePassword(@RequestAttribute("userId") int userId,
                                         @Valid @RequestBody ChangePasswordRequest request) {
        log.info("PUT /api/users/password - 修改密码, userId={}", userId);
        return userService.changePassword(userId, request);
    }

    /**
     * 获取我的积分记录（分页）
     */
    @GetMapping("/points")
    public Response<PageResponse<PointRecordVO>> getPointRecords(
            @RequestAttribute("userId") int userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/users/points - 获取积分记录, userId={}", userId);
        return pointService.getPointRecords(userId, page, size);
    }

    /**
     * 积分排行榜
     */
    @GetMapping("/points/rank")
    public Response<List<RankItemVO>> getPointsRank(@RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/users/points/rank - 积分排行榜");
        return pointService.getPointsRank(size);
    }

    /**
     * 获取我的帖子（分页）
     */
    @GetMapping("/posts")
    public Response<PageResponse<PostVO>> getMyPosts(
            @RequestAttribute("userId") int userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/users/posts - 我的帖子, userId={}", userId);
        return postService.listPostsByUser(userId, page, size);
    }

    /**
     * 获取我的回复（分页）
     */
    @GetMapping("/comments")
    public Response<PageResponse<CommentVO>> getMyComments(
            @RequestAttribute("userId") int userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/users/comments - 我的回复, userId={}", userId);
        return commentService.listCommentsByUser(userId, page, size);
    }

    /**
     * 获取我的收藏（分页）
     */
    @GetMapping("/favorites")
    public Response<PageResponse<PostVO>> getMyFavorites(
            @RequestAttribute("userId") int userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/users/favorites - 我的收藏, userId={}", userId);
        return favoriteService.listFavorites(userId, page, size);
    }

    /**
     * 获取我的点赞（分页）
     */
    @GetMapping("/likes")
    public Response<PageResponse<PostVO>> getMyLikes(
            @RequestAttribute("userId") int userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/users/likes - 我的点赞, userId={}", userId);
        return likeService.listLikedPosts(userId, page, size);
    }
}
