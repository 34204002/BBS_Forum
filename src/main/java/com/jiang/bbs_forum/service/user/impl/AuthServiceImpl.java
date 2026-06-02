package com.jiang.bbs_forum.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.request.LoginRequest;
import com.jiang.bbs_forum.dto.request.RegisterRequest;
import com.jiang.bbs_forum.dto.response.LoginVO;
import com.jiang.bbs_forum.dto.response.UserVO;
import com.jiang.bbs_forum.entity.User;
import com.jiang.bbs_forum.entity.UserProfile;
import com.jiang.bbs_forum.mapper.UserMapper;
import com.jiang.bbs_forum.mapper.UserProfileMapper;
import com.jiang.bbs_forum.service.user.AuthService;
import com.jiang.bbs_forum.service.user.PointService;
import com.jiang.bbs_forum.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class AuthServiceImpl implements AuthService {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
    @Transactional
    public Response<LoginVO> register(RegisterRequest request) {
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.eq(User::getUsername, request.getUsername());
        if (userMapper.selectCount(query) > 0) {
            return Response.error(400, "用户名已存在");
        }

        query = new LambdaQueryWrapper<>();
        query.eq(User::getEmail, request.getEmail());
        if (userMapper.selectCount(query) > 0) {
            return Response.error(400, "邮箱已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole("user");
        user.setStatus(1);
        user.setPoints(0);
        user.setNickname(request.getUsername());
        user.setAvatar("default.png");
        userMapper.insert(user);

        UserProfile profile = new UserProfile();
        profile.setUserId(user.getId());
        profile.setAvatar("default_avatar.png");
        userProfileMapper.insert(profile);

        pointService.addPoints(user.getId(), 100, "新用户注册奖励");

        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
        long expireMillis = System.currentTimeMillis() + 604800000L;

        UserVO userVO = UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .points(100)
                .createTime(user.getCreateTime() != null
                        ? user.getCreateTime().format(DTF)
                        : LocalDateTime.now().format(DTF))
                .build();

        LoginVO data = LoginVO.builder()
                .token(token)
                .expireTime(DTF.format(LocalDateTime.ofInstant(
                        java.time.Instant.ofEpochMilli(expireMillis), ZoneId.systemDefault())))
                .user(userVO)
                .build();

        return Response.created("注册成功", data);
    }

    @Override
    public Response<LoginVO> login(LoginRequest request) {
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(query);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return Response.error(400, "用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            return Response.error(400, "账号已被禁用");
        }

        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setLastLoginTime(LocalDateTime.now());
        updateUser.setLoginCount(user.getLoginCount() + 1);
        userMapper.updateById(updateUser);

        UserProfile profile = userProfileMapper.selectOne(
                new LambdaQueryWrapper<UserProfile>().eq(UserProfile::getUserId, user.getId()));

        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
        long expireMillis = System.currentTimeMillis() + 604800000L;

        UserVO userVO = UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(profile != null ? profile.getNickname() : null)
                .avatar(profile != null ? profile.getAvatar() : null)
                .role(user.getRole())
                .points(user.getPoints())
                .build();

        LoginVO data = LoginVO.builder()
                .token(token)
                .expireTime(DTF.format(LocalDateTime.ofInstant(
                        java.time.Instant.ofEpochMilli(expireMillis), ZoneId.systemDefault())))
                .user(userVO)
                .build();

        return Response.success("登录成功", data);
    }

    @Override
    public Response<Void> logout(int userId) {
        return Response.success("退出成功", null);
    }
}
