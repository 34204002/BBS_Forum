package com.jiang.bbs_forum.service.admin;

import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.request.CreateBoardRequest;
import com.jiang.bbs_forum.dto.request.UpdateBoardRequest;

public interface AdminService {
    Response<?> listUsers(String keyword, int page, int size);
    Response<?> updateUserStatus(int userId, int status);
    Response<?> getSystemLogs(int page, int size);
    Response<?> createBoard(CreateBoardRequest request, int adminId, String ip);
    Response<?> updateBoard(int boardId, UpdateBoardRequest request, int adminId, String ip);
    Response<?> deleteBoard(int boardId, int adminId, String ip);
    Response<?> toggleTop(int postId, int isTop);
    Response<?> toggleEssence(int postId, int isEssence);
}
