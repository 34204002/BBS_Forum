package com.jiang.bbs_forum.service.user;

import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.request.ChangePasswordRequest;
import com.jiang.bbs_forum.dto.request.UpdateProfileRequest;
import com.jiang.bbs_forum.dto.response.ProfileVO;
import com.jiang.bbs_forum.dto.response.UserVO;

public interface UserService {
    Response<UserVO> getCurrentUser(int userId);

    Response<UserVO> getUserById(int id);

    Response<ProfileVO> updateProfile(int userId, UpdateProfileRequest request);

    Response<Void> changePassword(int userId, ChangePasswordRequest request);

    Response<Void> updateAvatar(int userId, String avatarUrl);
}
