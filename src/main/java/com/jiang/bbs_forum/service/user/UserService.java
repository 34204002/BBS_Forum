package com.jiang.bbs_forum.service.user;

import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.request.ChangePasswordRequest;
import com.jiang.bbs_forum.dto.request.UpdateProfileRequest;

public interface UserService {
    Response<?> getCurrentUser(int userId);
    Response<?> getUserById(int id);
    Response<?> updateProfile(int userId, UpdateProfileRequest request);
    Response<?> changePassword(int userId, ChangePasswordRequest request);
    Response<?> getPointRecords(int userId, int page, int size);
    Response<?> getPointsRank(int size);
    Response<?> getMyPosts(int userId, int page, int size);
    Response<?> getMyComments(int userId, int page, int size);
    Response<?> getMyFavorites(int userId, int page, int size);
}
