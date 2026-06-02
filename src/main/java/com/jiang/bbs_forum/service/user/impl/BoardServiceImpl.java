package com.jiang.bbs_forum.service.user.impl;

import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.request.CreateBoardRequest;
import com.jiang.bbs_forum.dto.request.UpdateBoardRequest;
import com.jiang.bbs_forum.dto.response.BoardVO;
import com.jiang.bbs_forum.mapper.BoardMapper;
import com.jiang.bbs_forum.service.user.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    private BoardMapper boardMapper;

    @Override
    public Response<List<BoardVO>> listBoards() {
        // TODO: 查询所有板块，按sort升序排列
        return null;
    }

    @Override
    public Response<BoardVO> getBoardById(int id) {
        // TODO: 查询单个板块详情
        return null;
    }

    @Override
    public Response<BoardVO> createBoard(CreateBoardRequest request, int adminId, String ip) {
        // TODO: 1. 插入板块
        // TODO: 2. 记录操作日志
        return null;
    }

    @Override
    public Response<BoardVO> updateBoard(int boardId, UpdateBoardRequest request, int adminId, String ip) {
        // TODO: 1. 更新板块信息
        // TODO: 2. 记录操作日志
        return null;
    }

    @Override
    public Response<Void> deleteBoard(int boardId, int adminId, String ip) {
        // TODO: 1. 检查板块下是否有帖子
        // TODO: 2. 逻辑删除板块
        // TODO: 3. 记录操作日志
        return null;
    }
}
