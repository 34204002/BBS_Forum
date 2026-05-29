package com.jiang.bbs_forum.service.user.impl;

import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.mapper.BoardMapper;
import com.jiang.bbs_forum.service.user.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    private BoardMapper boardMapper;

    @Override
    public Response<?> listBoards() {
        // TODO: 查询所有板块，按sort升序排列
        return null;
    }

    @Override
    public Response<?> getBoardById(int id) {
        // TODO: 查询单个板块详情
        return null;
    }
}
