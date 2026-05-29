package com.jiang.bbs_forum.controller.user;

import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.service.user.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    @Autowired
    private BoardService boardService;

    // GET /api/boards — 获取所有板块
    @GetMapping
    public Response<?> listBoards() {
        return boardService.listBoards();
    }

    // GET /api/boards/{id} — 获取板块详情
    @GetMapping("/{id}")
    public Response<?> getBoardById(@PathVariable int id) {
        return boardService.getBoardById(id);
    }
}
