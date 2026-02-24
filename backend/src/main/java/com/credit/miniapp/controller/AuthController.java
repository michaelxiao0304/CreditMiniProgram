package com.credit.miniapp.controller;

import com.credit.miniapp.dto.ApiResponse;
import com.credit.miniapp.dto.LoginRequest;
import com.credit.miniapp.dto.LoginResponse;
import com.credit.miniapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    /**
     * 绑定手机号
     */
    @PostMapping("/bindPhone")
    public ApiResponse<Map<String, String>> bindPhone(
            @RequestBody Map<String, String> params,
            HttpServletRequest request) {
        String openid = (String) request.getAttribute("openid");
        String encryptedData = params.get("encryptedData");
        String iv = params.get("iv");

        try {
            String phoneNumber = userService.bindPhone(openid, encryptedData, iv);
            return ApiResponse.success(Map.of("phoneNumber", phoneNumber));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
