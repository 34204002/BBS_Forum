package com.jiang.bbs_forum.service.user.impl;

import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.response.UploadVO;
import com.jiang.bbs_forum.service.user.FileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    private static final Set<String> ALLOWED_TYPES = new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "gif"));
    private static final long AVATAR_MAX_SIZE = 2 * 1024 * 1024;
    private static final long POST_IMAGE_MAX_SIZE = 5 * 1024 * 1024;

    @Value("${upload.path:./uploads}")
    private String uploadPath;

    @Override
    public Response<UploadVO> uploadAvatar(MultipartFile file) {
        if (file.isEmpty()) {
            return Response.error(400, "文件不能为空");
        }
        if (file.getSize() > AVATAR_MAX_SIZE) {
            return Response.error(400, "头像大小不能超过2MB");
        }
        return upload(file, "avatar");
    }

    @Override
    public Response<UploadVO> uploadPostImage(MultipartFile file) {
        if (file.isEmpty()) {
            return Response.error(400, "文件不能为空");
        }
        if (file.getSize() > POST_IMAGE_MAX_SIZE) {
            return Response.error(400, "图片大小不能超过5MB");
        }
        return upload(file, "post");
    }

    private Response<UploadVO> upload(MultipartFile file, String dir) {
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
        }
        if (!ALLOWED_TYPES.contains(ext)) {
            return Response.error(400, "仅支持 jpg/png/gif 格式");
        }

        String filename = UUID.randomUUID().toString() + "." + ext;
        Path dirPath = Paths.get(uploadPath, dir);
        try {
            Files.createDirectories(dirPath);
            file.transferTo(dirPath.resolve(filename));
        } catch (IOException e) {
            return Response.error(500, "文件上传失败");
        }

        String url = "/uploads/" + dir + "/" + filename;
        return Response.success("上传成功", UploadVO.builder().url(url).build());
    }
}
