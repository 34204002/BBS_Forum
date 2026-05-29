package com.jiang.bbs_forum.controller.user;

import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.request.ChangePasswordRequest;
import com.jiang.bbs_forum.dto.request.UpdateProfileRequest;
import com.jiang.bbs_forum.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // GET /api/users/current — 获取当前用户信息
    @GetMapping("/current")
    public Response<?> getCurrentUser(@RequestAttribute("userId") int userId) {
        return userService.getCurrentUser(userId);
    }

    // GET /api/users/{id} — 获取用户公开信息
    @GetMapping("/{id}")
    public Response<?> getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    // PUT /api/users/profile — 修改个人资料
    @PutMapping("/profile")
    public Response<?> updateProfile(@RequestAttribute("userId") int userId,
                                     @Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateProfile(userId, request);
    }

    // PUT /api/users/password — 修改密码
    @PutMapping("/password")
    public Response<?> changePassword(@RequestAttribute("userId") int userId,
                                      @Valid @RequestBody ChangePasswordRequest request) {
        return userService.changePassword(userId, request);
    }

    // GET /api/users/points — 获取我的积分记录
    @GetMapping("/points")
    public Response<?> getPointRecords(@RequestAttribute("userId") int userId,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        return userService.getPointRecords(userId, page, size);
    }

    // GET /api/users/points/rank — 积分排行榜
    @GetMapping("/points/rank")
    public Response<?> getPointsRank(@RequestParam(defaultValue = "10") int size) {
        return userService.getPointsRank(size);
    }

    // GET /api/users/posts — 获取我的帖子
    @GetMapping("/posts")
    public Response<?> getMyPosts(@RequestAttribute("userId") int userId,
                                  @RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        return userService.getMyPosts(userId, page, size);
    }

    // GET /api/users/comments — 获取我的回复
    @GetMapping("/comments")
    public Response<?> getMyComments(@RequestAttribute("userId") int userId,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        return userService.getMyComments(userId, page, size);
    }

    // GET /api/users/favorites — 获取我的收藏
    @GetMapping("/favorites")
    public Response<?> getMyFavorites(@RequestAttribute("userId") int userId,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        return userService.getMyFavorites(userId, page, size);
    }
}
