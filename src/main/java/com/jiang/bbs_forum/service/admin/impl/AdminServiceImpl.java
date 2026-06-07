package com.jiang.bbs_forum.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiang.bbs_forum.common.PageResponse;
import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.response.LogVO;
import com.jiang.bbs_forum.dto.response.StatusVO;
import com.jiang.bbs_forum.dto.response.UserVO;
import com.jiang.bbs_forum.entity.SystemLog;
import com.jiang.bbs_forum.entity.User;
import com.jiang.bbs_forum.mapper.SystemLogMapper;
import com.jiang.bbs_forum.mapper.UserMapper;
import com.jiang.bbs_forum.service.admin.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SystemLogMapper systemLogMapper;

    @Override
    public Response<PageResponse<UserVO>> listUsers(String keyword, int page, int size) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(User::getUsername, keyword).or().like(User::getEmail, keyword);
        }
        wrapper.orderByDesc(User::getCreateTime);
        IPage<User> userPage = userMapper.selectPage(pageParam, wrapper);

        List<UserVO> voList = userPage.getRecords().stream().map(user -> {
            UserVO vo = new UserVO();
            vo.setId(user.getId());
            vo.setUsername(user.getUsername());
            vo.setPassword(user.getPassword());
            vo.setEmail(user.getEmail());
            vo.setRole(user.getRole());
            vo.setStatus(user.getStatus());
            vo.setPoints(user.getPoints());
            vo.setCreateTime(user.getCreateTime() != null ? user.getCreateTime().toString() : null);
            return vo;
        }).collect(Collectors.toList());

        long total = userPage.getTotal();
        int pages = (int) Math.ceil((double) total / size);
        return Response.success(new PageResponse<>(total, voList, page, size, pages));
    }

    @Override
    public Response<StatusVO> updateUserStatus(int userId, int status) {
        if (status != 0 && status != 1) {
            return Response.error(400, "状态值必须为0（禁用）或1（正常）");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Response.error(404, "用户不存在");
        }
        user.setStatus(status);
        userMapper.updateById(user);

        StatusVO vo = new StatusVO();
        vo.setStatus(status);
        return Response.success("更新成功", vo);
    }

    @Override
    public Response<PageResponse<LogVO>> getSystemLogs(int page, int size) {
        Page<SystemLog> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SystemLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SystemLog::getCreateTime);
        IPage<SystemLog> logPage = systemLogMapper.selectPage(pageParam, wrapper);

        List<LogVO> voList = logPage.getRecords().stream().map(log -> {
            LogVO vo = new LogVO();
            vo.setId(log.getId());
            vo.setAdminId(log.getAdminId());
            vo.setOperation(log.getOperation());
            vo.setIp(log.getIp());
            vo.setCreateTime(log.getCreateTime() != null ? log.getCreateTime().toString() : null);
            return vo;
        }).collect(Collectors.toList());

        long total = logPage.getTotal();
        int pages = (int) Math.ceil((double) total / size);
        return Response.success(new PageResponse<>(total, voList, page, size, pages));
    }
}
