package com.jiang.bbs_forum.service.user;

import com.jiang.bbs_forum.common.Response;

public interface BoardService {
    Response<?> listBoards();
    Response<?> getBoardById(int id);
}
