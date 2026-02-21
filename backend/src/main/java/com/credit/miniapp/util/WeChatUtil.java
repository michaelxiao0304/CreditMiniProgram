package com.credit.miniapp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 微信小程序数据解密工具类
 * 用于解密 getPhoneNumber 返回的 encryptedData
 *
 * 官方文档: https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/getPhoneNumber.html
 */
@Component
public class WeChatUtil {

    private static final Logger logger = LoggerFactory.getLogger(WeChatUtil.class);

    /**
     * 字符集
     */
    private static final String CHARSET = "UTF-8";

    /**
     *  алгоритм解密算法
     */
    private static final String ALGORITHM = "AES/CBC/PKCS7Padding";

    /**
     * 解密微信手机号数据
     *
     * @param sessionKey    会话密钥（从 Redis 获取）
     * @param encryptedData 加密数据（前端传入）
     * @param iv            加密向量（前端传入）
     * @return 解密后的 JSON 字符串，包含 phoneNumber 等信息
     * @throws Exception 解密失败抛出异常
     */
    public String decryptPhoneNumber(String sessionKey, String encryptedData, String iv) throws Exception {
        logger.info("开始解密微信手机号数据, sessionKey长度={}, encryptedData长度={}, iv长度={}",
                sessionKey != null ? sessionKey.length() : 0,
                encryptedData != null ? encryptedData.length() : 0,
                iv != null ? iv.length() : 0);

        try {
            // Base64 解码
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] ivBytes = Base64.getDecoder().decode(iv);
            byte[] sessionKeyBytes = sessionKey.getBytes(StandardCharsets.UTF_8);

            // 密钥规范
            SecretKeySpec secretKeySpec = new SecretKeySpec(sessionKeyBytes, "AES");

            // IV 规范
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);

            // 初始化密码器
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            // 解密
            byte[] decrypted = cipher.doFinal(encryptedBytes);

            // 转换为 JSON 字符串
            String decryptedJson = new String(decrypted, CHARSET);
            logger.info("微信手机号解密成功, 数据长度={}", decryptedJson.length());

            return decryptedJson;

        } catch (IllegalArgumentException e) {
            logger.error("Base64 解码失败，请检查 encryptedData 和 iv 格式", e);
            throw new Exception("Base64 解码失败: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("微信手机号解密失败，请检查 sessionKey 是否有效", e);
            throw new Exception("解密失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从解密后的 JSON 中提取手机号
     *
     * @param decryptedJson 解密后的 JSON 字符串
     * @return 手机号
     * @throws Exception 解析失败抛出异常
     */
    public String extractPhoneNumber(String decryptedJson) throws Exception {
        try {
            // 简单的 JSON 解析，提取 phoneNumber 字段
            // 使用字符串查找方式，避免引入额外依赖
            String phoneKey = "\"phoneNumber\":\"";
            int phoneStart = decryptedJson.indexOf(phoneKey);

            if (phoneStart == -1) {
                phoneKey = "\"phoneNumber\": \"";
                phoneStart = decryptedJson.indexOf(phoneKey);
            }

            if (phoneStart == -1) {
                logger.error("解密数据中未找到 phoneNumber 字段, json={}", decryptedJson);
                throw new Exception("解密数据中未找到手机号");
            }

            phoneStart += phoneKey.length();
            int phoneEnd = decryptedJson.indexOf("\"", phoneStart);

            if (phoneEnd == -1) {
                logger.error("解密数据格式错误, json={}", decryptedJson);
                throw new Exception("解密数据格式错误");
            }

            String phoneNumber = decryptedJson.substring(phoneStart, phoneEnd);
            logger.info("成功提取手机号, 手机号前3位={}", phoneNumber.substring(0, 3));

            return phoneNumber;
        } catch (Exception e) {
            logger.error("提取手机号失败", e);
            throw new Exception("提取手机号失败: " + e.getMessage(), e);
        }
    }
}
