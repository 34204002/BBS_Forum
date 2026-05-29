package com.jiang.bbs_forum.service.user;

import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.request.LoginRequest;
import com.jiang.bbs_forum.dto.request.RegisterRequest;

public interface AuthService {
    Response<?> register(RegisterRequest request);
    Response<?> login(LoginRequest request);
    Response<?> logout(int userId);
}
