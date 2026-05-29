package com.jiang.bbs_forum.controller.admin;

import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.request.*;
import com.jiang.bbs_forum.service.admin.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // ==================== 用户管理 ====================

    // GET /api/admin/users — 获取用户列表
    @GetMapping("/users")
    public Response<?> listUsers(@RequestParam(required = false) String keyword,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        return adminService.listUsers(keyword, page, size);
    }

    // PUT /api/admin/users/{id}/status — 禁用/启用用户
    @PutMapping("/users/{id}/status")
    public Response<?> updateUserStatus(@PathVariable("id") int userId,
                                        @Valid @RequestBody UpdateUserStatusRequest request) {
        return adminService.updateUserStatus(userId, request.getStatus());
    }

    // ==================== 板块管理 ====================

    // POST /api/admin/boards — 新增板块
    @PostMapping("/boards")
    public Response<?> createBoard(@Valid @RequestBody CreateBoardRequest request,
                                   @RequestAttribute("userId") int adminId,
                                   HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();
        return adminService.createBoard(request, adminId, ip);
    }

    // PUT /api/admin/boards/{id} — 修改板块
    @PutMapping("/boards/{id}")
    public Response<?> updateBoard(@PathVariable("id") int boardId,
                                   @Valid @RequestBody UpdateBoardRequest request,
                                   @RequestAttribute("userId") int adminId,
                                   HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();
        return adminService.updateBoard(boardId, request, adminId, ip);
    }

    // DELETE /api/admin/boards/{id} — 删除板块
    @DeleteMapping("/boards/{id}")
    public Response<?> deleteBoard(@PathVariable("id") int boardId,
                                   @RequestAttribute("userId") int adminId,
                                   HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();
        return adminService.deleteBoard(boardId, adminId, ip);
    }

    // ==================== 帖子管理 ====================

    // PUT /api/admin/posts/{id}/top — 帖子置顶/取消置顶
    @PutMapping("/posts/{id}/top")
    public Response<?> toggleTop(@PathVariable("id") int postId,
                                 @RequestBody TopRequest request) {
        return adminService.toggleTop(postId, request.getIsTop());
    }

    // PUT /api/admin/posts/{id}/essence — 帖子加精/取消加精
    @PutMapping("/posts/{id}/essence")
    public Response<?> toggleEssence(@PathVariable("id") int postId,
                                     @RequestBody EssenceRequest request) {
        return adminService.toggleEssence(postId, request.getIsEssence());
    }

    // ==================== 系统日志 ====================

    // GET /api/admin/logs — 获取系统日志
    @GetMapping("/logs")
    public Response<?> getSystemLogs(@RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        return adminService.getSystemLogs(page, size);
    }
}
