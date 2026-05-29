package com.jiang.bbs_forum.service.user.impl;

import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.request.LoginRequest;
import com.jiang.bbs_forum.dto.request.RegisterRequest;
import com.jiang.bbs_forum.mapper.UserMapper;
import com.jiang.bbs_forum.mapper.UserProfileMapper;
import com.jiang.bbs_forum.service.user.AuthService;
import com.jiang.bbs_forum.service.user.PointService;
import com.jiang.bbs_forum.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserProfileMapper userProfileMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private PointService pointService;

    @Override
    public Response<?> register(RegisterRequest request) {
        // TODO: 1. 校验用户名和邮箱是否已存在
        // TODO: 2. BCrypt加密密码
        // TODO: 3. 插入user表（默认role=user, points=100）
        // TODO: 4. 插入user_profile表（默认avatar=default_avatar.png）
        // TODO: 5. 添加注册积分记录（+100）
        // TODO: 6. 生成JWT token并返回
        return null;
    }

    @Override
    public Response<?> login(LoginRequest request) {
        // TODO: 1. 根据username查询用户
        // TODO: 2. 校验密码、用户状态
        // TODO: 3. 更新last_login_time和login_count
        // TODO: 4. 生成JWT token并返回（含用户基本信息）
        return null;
    }

    @Override
    public Response<?> logout(int userId) {
        // TODO: 退出登录（客户端删除token即可，如需黑名单可在此实现）
        return Response.success("退出成功", null);
    }
}
