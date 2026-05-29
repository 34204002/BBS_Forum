package com.jiang.bbs_forum.controller.user;

import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.request.LoginRequest;
import com.jiang.bbs_forum.dto.request.RegisterRequest;
import com.jiang.bbs_forum.service.user.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // POST /api/auth/register — 用户注册
    @PostMapping("/register")
    public Response<?> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    // POST /api/auth/login — 用户登录
    @PostMapping("/login")
    public Response<?> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    // POST /api/auth/logout — 退出登录
    @PostMapping("/logout")
    public Response<?> logout(@RequestAttribute("userId") int userId) {
        return authService.logout(userId);
    }
}
