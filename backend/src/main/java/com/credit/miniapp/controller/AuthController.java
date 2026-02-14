package com.credit.miniapp.controller;

import com.credit.miniapp.dto.ApiResponse;
import com.credit.miniapp.dto.LoginRequest;
import com.credit.miniapp.dto.LoginResponse;
import com.credit.miniapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request.getCode(), request.getUserInfo());
        return ApiResponse.success(response);
    }

    @GetMapping("/info")
    public ApiResponse<String> getInfo(HttpServletRequest request) {
        String openid = (String) request.getAttribute("openid");
        return ApiResponse.success(openid);
    }
}
