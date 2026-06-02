package com.jiang.bbs_forum.controller.user;

import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.service.user.NotificationService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Resource
    private NotificationService notificationService;

    /**
     * 未读数量
     */
    @GetMapping("/unread-count")
    public Response<Long> getUnreadCount(@RequestAttribute("userId") int userId) {
        return notificationService.getUnreadCount(userId);
    }

    /**
     * 通知列表
     */
    @GetMapping
    public Response<?> list(@RequestAttribute("userId") int userId,
                            @RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "10") int size) {
        return notificationService.list(userId, page, size);
    }

    /**
     * 标记已读
     */
    @PutMapping("/{id}/read")
    public Response<Void> read(@PathVariable int id) {
        return notificationService.markRead(id);
    }

    /**
     * 全部已读
     */
    @PutMapping("/read-all")
    public Response<Void> readAll(@RequestAttribute("userId") int userId) {
        return notificationService.markAllRead(userId);
    }
}
