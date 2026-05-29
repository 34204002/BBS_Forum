package com.jiang.bbs_forum.service.user.impl;

import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.mapper.UserProfileMapper;
import com.jiang.bbs_forum.service.user.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Value("${upload.path:./uploads}")
    private String uploadPath;

    @Override
    public Response<?> uploadAvatar(MultipartFile file) {
        // TODO: 1. 校验文件类型（jpg/png/gif）
        // TODO: 2. 校验文件大小（不超过2MB）
        // TODO: 3. 保存文件到 uploads/avatar/ 目录
        // TODO: 4. 返回文件URL
        return null;
    }

    @Override
    public Response<?> uploadPostImage(MultipartFile file) {
        // TODO: 1. 校验文件类型（jpg/png/gif）
        // TODO: 2. 校验文件大小（不超过5MB）
        // TODO: 3. 保存文件到 uploads/post/ 目录
        // TODO: 4. 返回文件URL
        return null;
    }
}
