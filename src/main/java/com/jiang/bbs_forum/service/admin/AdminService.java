package com.jiang.bbs_forum.service.admin;

import com.jiang.bbs_forum.common.PageResponse;
import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.response.LogVO;
import com.jiang.bbs_forum.dto.response.StatusVO;
import com.jiang.bbs_forum.dto.response.UserVO;

public interface AdminService {
    Response<PageResponse<UserVO>> listUsers(String keyword, int page, int size);

    Response<StatusVO> updateUserStatus(int userId, int status);

    Response<PageResponse<LogVO>> getSystemLogs(int page, int size);
}
