package com.jiang.bbs_forum.service.user;

import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.request.CreateBoardRequest;
import com.jiang.bbs_forum.dto.request.UpdateBoardRequest;
import com.jiang.bbs_forum.dto.response.BoardVO;

import java.util.List;

public interface BoardService {
    Response<List<BoardVO>> listBoards();

    Response<BoardVO> getBoardById(int id);

    Response<BoardVO> createBoard(CreateBoardRequest request, int adminId, String ip);

    Response<BoardVO> updateBoard(int boardId, UpdateBoardRequest request, int adminId, String ip);

    Response<Void> deleteBoard(int boardId, int adminId, String ip);
}
