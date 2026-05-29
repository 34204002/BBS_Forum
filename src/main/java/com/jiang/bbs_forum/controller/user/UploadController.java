package com.jiang.bbs_forum.controller.user;

import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.service.user.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired
    private FileUploadService fileUploadService;

    // POST /api/upload/avatar — 上传头像（≤2MB, jpg/png/gif）
    @PostMapping("/avatar")
    public Response<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return fileUploadService.uploadAvatar(file);
    }

    // POST /api/upload/post — 上传帖子图片（≤5MB, jpg/png/gif）
    @PostMapping("/post")
    public Response<?> uploadPostImage(@RequestParam("file") MultipartFile file) {
        return fileUploadService.uploadPostImage(file);
    }
}
