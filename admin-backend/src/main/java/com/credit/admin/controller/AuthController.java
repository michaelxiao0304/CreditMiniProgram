package com.credit.admin.controller;

import com.credit.admin.dto.ApiResponse;
import com.credit.admin.dto.LoginRequest;
import com.credit.admin.dto.LoginResponse;
import com.credit.admin.entity.AdminUser;
import com.credit.admin.repository.AdminUserRepository;
import com.credit.admin.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AuthController {

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        AdminUser user = adminUserRepository.findByUsername(request.getUsername());
        if (user == null) {
            return ApiResponse.error("用户不存在");
        }
        if (!user.getPassword().equals(request.getPassword())) {
            return ApiResponse.error("密码错误");
        }
        if (user.getStatus() == 0) {
            return ApiResponse.error("账号已被禁用");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        LoginResponse response = new LoginResponse(token, user.getUsername(), user.getRealName());
        return ApiResponse.success(response);
    }

    @GetMapping("/info")
    public ApiResponse<Map<String, String>> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.getUsernameFromToken(token);
            AdminUser user = adminUserRepository.findByUsername(username);
            if (user == null) {
                return ApiResponse.error("用户不存在");
            }
            return ApiResponse.success(Map.of(
                "username", user.getUsername(),
                "realName", user.getRealName() != null ? user.getRealName() : user.getUsername()
            ));
        } catch (Exception e) {
            return ApiResponse.error("获取用户信息失败");
        }
    }
}
