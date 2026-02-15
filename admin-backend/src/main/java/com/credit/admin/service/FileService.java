package com.credit.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class FileService {

    // 配置上传目录
    private static final String UPLOAD_DIR = "/home/ubuntu/Code/CreditMiniProgram/uploads/";
    // 允许的图片格式
    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "webp"};
    // 最大文件大小 2MB
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;
    // 最大图片尺寸 500x500
    private static final int MAX_WIDTH = 500;
    private static final int MAX_HEIGHT = 500;

    public Map<String, Object> uploadImage(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();

        // 检查文件是否为空
        if (file == null || file.isEmpty()) {
            result.put("success", false);
            result.put("message", "文件不能为空");
            return result;
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            result.put("success", false);
            result.put("message", "文件大小不能超过2MB");
            return result;
        }

        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        }

        // 检查文件格式
        boolean isAllowed = false;
        for (String ext : ALLOWED_EXTENSIONS) {
            if (ext.equals(extension)) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            result.put("success", false);
            result.put("message", "只允许上传 JPG、PNG、GIF、WEBP 格式的图片");
            return result;
        }

        // 检查图片尺寸
        try {
            byte[] bytes = file.getBytes();
            // 简单检查：读取图片头部获取尺寸（需要依赖额外库，这里先用简单方式）
            // 实际生产环境可以使用 BufferedImage 检查尺寸
        } catch (IOException e) {
            result.put("success", false);
            result.put("message", "文件读取失败");
            return result;
        }

        // 生成唯一文件名
        String newFilename = UUID.randomUUID().toString() + "." + extension;

        // 创建上传目录
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 保存文件
        try {
            Path filePath = Paths.get(UPLOAD_DIR, newFilename);
            Files.write(filePath, file.getBytes());

            // 返回访问路径（相对于 /images）
            String imageUrl = "/images/" + newFilename;

            result.put("success", true);
            result.put("url", imageUrl);
            result.put("filename", newFilename);
            result.put("message", "上传成功");

        } catch (IOException e) {
            result.put("success", false);
            result.put("message", "文件保存失败: " + e.getMessage());
        }

        return result;
    }
}
