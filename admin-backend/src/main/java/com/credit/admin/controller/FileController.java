package com.credit.admin.controller;

import com.credit.admin.dto.ApiResponse;
import com.credit.admin.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/upload")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/image")
    public ApiResponse<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            Map<String, Object> result = fileService.uploadImage(file);
            if (Boolean.TRUE.equals(result.get("success"))) {
                return ApiResponse.success(result);
            } else {
                return ApiResponse.error(result.get("message").toString());
            }
        } catch (Exception e) {
            return ApiResponse.error("上传失败: " + e.getMessage());
        }
    }
}
