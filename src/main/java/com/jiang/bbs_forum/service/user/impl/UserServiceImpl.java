package com.jiang.bbs_forum.service.user.impl;

import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.request.ChangePasswordRequest;
import com.jiang.bbs_forum.dto.request.UpdateProfileRequest;
import com.jiang.bbs_forum.mapper.*;
import com.jiang.bbs_forum.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserProfileMapper userProfileMapper;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private FavoriteMapper favoriteMapper;
    @Autowired
    private PointRecordMapper pointRecordMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Response<?> getCurrentUser(int userId) {
        // TODO: 查询user和user_profile，返回完整个人信息
        return null;
    }

    @Override
    public Response<?> getUserById(int id) {
        // TODO: 查询用户公开信息（昵称、头像、积分、签名等），不返回敏感字段
        return null;
    }

    @Override
    public Response<?> updateProfile(int userId, UpdateProfileRequest request) {
        // TODO: 更新user_profile表
        return null;
    }

    @Override
    public Response<?> changePassword(int userId, ChangePasswordRequest request) {
        // TODO: 1. 校验原密码
        // TODO: 2. BCrypt加密新密码并更新
        return null;
    }

    @Override
    public Response<?> getPointRecords(int userId, int page, int size) {
        // TODO: 分页查询point_records表，按创建时间倒序
        return null;
    }

    @Override
    public Response<?> getPointsRank(int size) {
        // TODO: 按积分降序查询用户表，取前N名
        return null;
    }

    @Override
    public Response<?> getMyPosts(int userId, int page, int size) {
        // TODO: 分页查询当前用户的帖子
        return null;
    }

    @Override
    public Response<?> getMyComments(int userId, int page, int size) {
        // TODO: 分页查询当前用户的回复（含帖子标题）
        return null;
    }

    @Override
    public Response<?> getMyFavorites(int userId, int page, int size) {
        // TODO: 分页查询当前用户的收藏（关联帖子表）
        return null;
    }
}
