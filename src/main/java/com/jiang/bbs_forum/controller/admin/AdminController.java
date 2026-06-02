package com.jiang.bbs_forum.controller.admin;

import lombok.extern.slf4j.Slf4j;

import com.jiang.bbs_forum.common.PageResponse;
import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.request.*;
import com.jiang.bbs_forum.dto.response.BoardVO;
import com.jiang.bbs_forum.dto.response.LogVO;
import com.jiang.bbs_forum.dto.response.StatusVO;
import com.jiang.bbs_forum.dto.response.UserVO;
import com.jiang.bbs_forum.service.admin.AdminService;
import com.jiang.bbs_forum.service.user.BoardService;
import com.jiang.bbs_forum.service.user.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@Slf4j
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private PostService postService;
    @Autowired
    private BoardService boardService;

    // ==================== 用户管理 ====================

    /**
     * 获取用户列表（分页，支持关键词搜索）
     */
    @GetMapping("/users")
    public Response<PageResponse<UserVO>> listUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/admin/users - 获取用户列表");
        return adminService.listUsers(keyword, page, size);
    }

    /**
     * 禁用/启用用户
     */
    @PutMapping("/users/{id}/status")
    public Response<StatusVO> updateUserStatus(
            @PathVariable("id") int userId,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        log.info("PUT /api/admin/users/{}/status - 禁用/启用用户", userId);
        return adminService.updateUserStatus(userId, request.getStatus());
    }

    // ==================== 板块管理 ====================

    /**
     * 新增板块
     */
    @PostMapping("/boards")
    public Response<BoardVO> createBoard(@Valid @RequestBody CreateBoardRequest request,
                                         @RequestAttribute("userId") int adminId,
                                         HttpServletRequest httpRequest) {
        log.info("POST /api/admin/boards - 新增板块, adminId={}", adminId);
        return boardService.createBoard(request, adminId, httpRequest.getRemoteAddr());
    }

    /**
     * 修改板块
     */
    @PutMapping("/boards/{id}")
    public Response<BoardVO> updateBoard(@PathVariable("id") int boardId,
                                         @Valid @RequestBody UpdateBoardRequest request,
                                         @RequestAttribute("userId") int adminId,
                                         HttpServletRequest httpRequest) {
        log.info("PUT /api/admin/boards/{} - 修改板块, adminId={}", boardId, adminId);
        return boardService.updateBoard(boardId, request, adminId, httpRequest.getRemoteAddr());
    }

    /**
     * 删除板块
     */
    @DeleteMapping("/boards/{id}")
    public Response<Void> deleteBoard(@PathVariable("id") int boardId,
                                      @RequestAttribute("userId") int adminId,
                                      HttpServletRequest httpRequest) {
        log.info("DELETE /api/admin/boards/{} - 删除板块, adminId={}", boardId, adminId);
        return boardService.deleteBoard(boardId, adminId, httpRequest.getRemoteAddr());
    }

    // ==================== 帖子管理 ====================

    /**
     * 帖子置顶/取消置顶
     */
    @PutMapping("/posts/{id}/top")
    public Response<StatusVO> toggleTop(@PathVariable("id") int postId,
                                        @RequestBody TopRequest request) {
        log.info("PUT /api/admin/posts/{}/top - 帖子置顶/取消置顶, isTop={}", postId, request.getIsTop());
        return postService.toggleTop(postId, request.getIsTop());
    }

    /**
     * 帖子加精/取消加精
     */
    @PutMapping("/posts/{id}/essence")
    public Response<StatusVO> toggleEssence(@PathVariable("id") int postId,
                                            @RequestBody EssenceRequest request) {
        log.info("PUT /api/admin/posts/{}/essence - 帖子加精/取消, isEssence={}", postId, request.getIsEssence());
        return postService.toggleEssence(postId, request.getIsEssence());
    }

    // ==================== 系统日志 ====================

    /**
     * 获取系统操作日志（分页）
     */
    @GetMapping("/logs")
    public Response<PageResponse<LogVO>> getSystemLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/admin/logs - 获取系统日志");
        return adminService.getSystemLogs(page, size);
    }
}
