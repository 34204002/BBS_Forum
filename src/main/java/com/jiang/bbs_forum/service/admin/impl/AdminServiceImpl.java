package com.jiang.bbs_forum.service.admin.impl;

import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.request.CreateBoardRequest;
import com.jiang.bbs_forum.dto.request.UpdateBoardRequest;
import com.jiang.bbs_forum.mapper.*;
import com.jiang.bbs_forum.service.admin.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BoardMapper boardMapper;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private SystemLogMapper systemLogMapper;

    @Override
    public Response<?> listUsers(String keyword, int page, int size) {
        // TODO: 分页查询用户列表，支持按用户名/邮箱模糊搜索
        return null;
    }

    @Override
    public Response<?> updateUserStatus(int userId, int status) {
        // TODO: 更新用户status字段（0-禁用，1-正常）
        return null;
    }

    @Override
    public Response<?> getSystemLogs(int page, int size) {
        // TODO: 分页查询系统日志，按时间倒序
        return null;
    }

    @Override
    public Response<?> createBoard(CreateBoardRequest request, int adminId, String ip) {
        // TODO: 1. 插入板块
        // TODO: 2. 记录操作日志
        return null;
    }

    @Override
    public Response<?> updateBoard(int boardId, UpdateBoardRequest request, int adminId, String ip) {
        // TODO: 1. 更新板块信息
        // TODO: 2. 记录操作日志
        return null;
    }

    @Override
    public Response<?> deleteBoard(int boardId, int adminId, String ip) {
        // TODO: 1. 检查板块下是否有帖子
        // TODO: 2. 逻辑删除板块
        // TODO: 3. 记录操作日志
        return null;
    }

    @Override
    public Response<?> toggleTop(int postId, int isTop) {
        // TODO: 更新帖子is_top字段
        return null;
    }

    @Override
    public Response<?> toggleEssence(int postId, int isEssence) {
        // TODO: 更新帖子is_essence字段
        return null;
    }
}
