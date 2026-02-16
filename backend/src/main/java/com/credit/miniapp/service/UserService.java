package com.credit.miniapp.service;

import com.credit.miniapp.dto.LoginResponse;
import com.credit.miniapp.dto.FeedbackRequest;
import com.credit.miniapp.dto.ProductDTO;
import com.credit.miniapp.entity.Feedback;
import com.credit.miniapp.entity.UserFavorite;
import com.credit.miniapp.entity.UserHistory;
import com.credit.miniapp.repository.FeedbackRepository;
import com.credit.miniapp.repository.UserFavoriteRepository;
import com.credit.miniapp.repository.UserHistoryRepository;
import com.credit.miniapp.util.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserFavoriteRepository userFavoriteRepository;

    @Autowired
    private UserHistoryRepository userHistoryRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${wechat.appid}")
    private String appid;

    @Value("${wechat.secret}")
    private String secret;

    public LoginResponse login(String code, String userInfo) {
        // 调用微信API获取openid和session_key
        String[] result = getOpenidAndSessionKeyFromWechat(code);
        String openid = result[0];
        String sessionKey = result[1];

        // 生成JWT token
        String token = jwtUtil.generateToken(openid);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setOpenid(openid);

        // 解析用户信息
        if (userInfo != null && !userInfo.isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode userInfoJson = mapper.readTree(userInfo);
                response.setNickname(userInfoJson.has("nickName") ? userInfoJson.get("nickName").asText() : "");
                response.setAvatarUrl(userInfoJson.has("avatarUrl") ? userInfoJson.get("avatarUrl").asText() : "");
            } catch (Exception e) {
                // ignore
            }
        }

        return response;
    }

    /**
     * 绑定手机号
     */
    public String bindPhone(String openid, String encryptedData, String iv) {
        // TODO: 从缓存或数据库获取用户的session_key
        // 这里需要将session_key与用户关联存储
        // 暂时返回错误，需要先实现session_key存储
        throw new RuntimeException("暂不支持手机号绑定，请先在微信小程序后台配置");
    }

    /**
     * 获取微信openid和session_key
     * @return String[0]=openid, String[1]=session_key
     */
    private String[] getOpenidAndSessionKeyFromWechat(String code) {
        String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appid, secret, code
        );

        try {
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response);

            if (jsonNode.has("openid")) {
                String openid = jsonNode.get("openid").asText();
                String sessionKey = jsonNode.has("session_key") ? jsonNode.get("session_key").asText() : "";
                return new String[]{openid, sessionKey};
            } else {
                throw new RuntimeException("获取openid失败: " + response);
            }
        } catch (Exception e) {
            // 开发环境使用模拟openid
            return new String[]{"mock_openid_" + System.currentTimeMillis(), ""};
        }
    }

    public List<UserFavorite> getFavorites(String openid) {
        return userFavoriteRepository.findByOpenid(openid);
    }

    public UserFavorite addFavorite(String openid, Long productId) {
        // 检查是否已收藏
        UserFavorite existing = userFavoriteRepository.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getOpenid, openid)
                        .eq(UserFavorite::getProductId, productId)
        );

        if (existing != null) {
            return existing;
        }

        UserFavorite favorite = new UserFavorite();
        favorite.setOpenid(openid);
        favorite.setProductId(productId);
        favorite.setCreatedAt(LocalDateTime.now());
        userFavoriteRepository.insert(favorite);
        return favorite;
    }

    public void removeFavorite(String openid, Long productId) {
        userFavoriteRepository.delete(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getOpenid, openid)
                        .eq(UserFavorite::getProductId, productId)
        );
    }

    public boolean isFavorited(String openid, Long productId) {
        return userFavoriteRepository.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getOpenid, openid)
                        .eq(UserFavorite::getProductId, productId)
        ) > 0;
    }

    public List<UserHistory> getHistory(String openid) {
        return userHistoryRepository.findByOpenid(openid);
    }

    public UserHistory addHistory(String openid, Long productId) {
        // 检查是否已存在
        UserHistory existing = userHistoryRepository.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserHistory>()
                        .eq(UserHistory::getOpenid, openid)
                        .eq(UserHistory::getProductId, productId)
        );

        if (existing != null) {
            existing.setCreatedAt(LocalDateTime.now());
            userHistoryRepository.updateById(existing);
            return existing;
        }

        UserHistory history = new UserHistory();
        history.setOpenid(openid);
        history.setProductId(productId);
        history.setCreatedAt(LocalDateTime.now());
        userHistoryRepository.insert(history);
        return history;
    }

    public void clearHistory(String openid) {
        userHistoryRepository.delete(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserHistory>()
                        .eq(UserHistory::getOpenid, openid)
        );
    }

    public Feedback submitFeedback(String openid, FeedbackRequest request) {
        Feedback feedback = new Feedback();
        feedback.setOpenid(openid);
        feedback.setContent(request.getContent());
        feedback.setContact(request.getContact());
        feedback.setStatus(0);
        feedback.setCreatedAt(LocalDateTime.now());
        feedbackRepository.insert(feedback);
        return feedback;
    }
}
