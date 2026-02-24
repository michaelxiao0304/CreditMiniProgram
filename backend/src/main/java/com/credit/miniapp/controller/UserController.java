package com.credit.miniapp.controller;

import com.credit.miniapp.dto.ApiResponse;
import com.credit.miniapp.dto.FeedbackRequest;
import com.credit.miniapp.entity.Feedback;
import com.credit.miniapp.entity.UserFavorite;
import com.credit.miniapp.entity.UserHistory;
import com.credit.miniapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/favorites")
    public ApiResponse<List<UserFavorite>> getFavorites(HttpServletRequest request) {
        String openid = (String) request.getAttribute("openid");
        List<UserFavorite> favorites = userService.getFavorites(openid);
        return ApiResponse.success(favorites);
    }

    @PostMapping("/favorites")
    public ApiResponse<Map<String, Object>> addFavorite(@RequestBody Map<String, Long> params, HttpServletRequest request) {
        Long productId = params.get("productId");
        String openid = (String) request.getAttribute("openid");
        UserFavorite favorite = userService.addFavorite(openid, productId);

        Map<String, Object> result = new HashMap<>();
        result.put("id", favorite.getId());
        result.put("favorited", true);
        return ApiResponse.success(result);
    }

    @DeleteMapping("/favorites/{productId}")
    public ApiResponse<Map<String, Object>> removeFavorite(@PathVariable Long productId, HttpServletRequest request) {
        String openid = (String) request.getAttribute("openid");
        userService.removeFavorite(openid, productId);

        Map<String, Object> result = new HashMap<>();
        result.put("favorited", false);
        return ApiResponse.success(result);
    }

    @GetMapping("/favorites/check/{productId}")
    public ApiResponse<Map<String, Object>> checkFavorite(@PathVariable Long productId, HttpServletRequest request) {
        String openid = (String) request.getAttribute("openid");
        boolean favorited = userService.isFavorited(openid, productId);

        Map<String, Object> result = new HashMap<>();
        result.put("favorited", favorited);
        return ApiResponse.success(result);
    }

    @GetMapping("/history")
    public ApiResponse<List<UserHistory>> getHistory(HttpServletRequest request) {
        String openid = (String) request.getAttribute("openid");
        List<UserHistory> history = userService.getHistory(openid);
        return ApiResponse.success(history);
    }

    @PostMapping("/history")
    public ApiResponse<UserHistory> addHistory(@RequestBody Map<String, Long> params, HttpServletRequest request) {
        Long productId = params.get("productId");
        String openid = (String) request.getAttribute("openid");
        UserHistory history = userService.addHistory(openid, productId);
        return ApiResponse.success(history);
    }

    @DeleteMapping("/history")
    public ApiResponse<Void> clearHistory(HttpServletRequest request) {
        String openid = (String) request.getAttribute("openid");
        userService.clearHistory(openid);
        return ApiResponse.success();
    }

    @PostMapping("/feedback")
    public ApiResponse<Feedback> submitFeedback(@RequestBody FeedbackRequest request, HttpServletRequest httpRequest) {
        String openid = (String) httpRequest.getAttribute("openid");
        Feedback feedback = userService.submitFeedback(openid, request);
        return ApiResponse.success(feedback);
    }
}
