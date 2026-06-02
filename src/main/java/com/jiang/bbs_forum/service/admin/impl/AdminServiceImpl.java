package com.jiang.bbs_forum.service.admin.impl;

import com.jiang.bbs_forum.common.PageResponse;
import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.response.LogVO;
import com.jiang.bbs_forum.dto.response.StatusVO;
import com.jiang.bbs_forum.dto.response.UserVO;
import com.jiang.bbs_forum.mapper.SystemLogMapper;
import com.jiang.bbs_forum.mapper.UserMapper;
import com.jiang.bbs_forum.service.admin.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SystemLogMapper systemLogMapper;

    @Override
    public Response<PageResponse<UserVO>> listUsers(String keyword, int page, int size) {
        // TODO: 分页查询用户列表，支持按用户名/邮箱模糊搜索
        return null;
    }

    @Override
    public Response<StatusVO> updateUserStatus(int userId, int status) {
        // TODO: 更新用户status字段（0-禁用，1-正常）
        return null;
    }

    @Override
    public Response<PageResponse<LogVO>> getSystemLogs(int page, int size) {
        // TODO: 分页查询系统日志，按时间倒序
        return null;
    }
}
