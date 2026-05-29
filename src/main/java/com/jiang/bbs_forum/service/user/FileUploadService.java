package com.jiang.bbs_forum.service.user;

import com.jiang.bbs_forum.common.Response;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    Response<?> uploadAvatar(MultipartFile file);
    Response<?> uploadPostImage(MultipartFile file);
}
