package com.credit.miniapp.service;

import com.credit.miniapp.dto.LoginResponse;
import com.credit.miniapp.dto.FeedbackRequest;
import com.credit.miniapp.dto.ProductDTO;
import com.credit.miniapp.entity.Feedback;
import com.credit.miniapp.entity.User;
import com.credit.miniapp.entity.UserFavorite;
import com.credit.miniapp.entity.UserHistory;
import com.credit.miniapp.repository.FeedbackRepository;
import com.credit.miniapp.repository.UserFavoriteRepository;
import com.credit.miniapp.repository.UserHistoryRepository;
import com.credit.miniapp.repository.UserRepository;
import com.credit.miniapp.util.CryptoUtil;
import com.credit.miniapp.util.JwtUtil;
import com.credit.miniapp.util.WeChatUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * Redis 中 session_key 的 key 前缀
     */
    private static final String SESSION_KEY_PREFIX = "wechat:session_key:";

    /**
     * session_key 有效期（分钟）
     */
    private static final long SESSION_KEY_EXPIRE_MINUTES = 25;

    @Autowired
    private UserFavoriteRepository userFavoriteRepository;

    @Autowired
    private UserHistoryRepository userHistoryRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private WeChatUtil weChatUtil;

    @Autowired
    private CryptoUtil cryptoUtil;

    @Value("${wechat.appid}")
    private String appid;

    @Value("${wechat.secret}")
    private String secret;

    public LoginResponse login(String code, String userInfo) {
        logger.info("开始微信登录, code={}", code);

        // 调用微信API获取openid和session_key
        String[] result = getOpenidAndSessionKeyFromWechat(code);
        String openid = result[0];
        String sessionKey = result[1];

        logger.info("微信登录成功, openid={}, sessionKey长度={}", openid, sessionKey != null ? sessionKey.length() : 0);

        // 保存 session_key 到 Redis（有效期25分钟）
        if (sessionKey != null && !sessionKey.isEmpty()) {
            String redisKey = SESSION_KEY_PREFIX + openid;
            redisTemplate.opsForValue().set(redisKey, sessionKey, SESSION_KEY_EXPIRE_MINUTES, TimeUnit.MINUTES);
            logger.info("session_key 已存入 Redis, key={}, 有效期={}分钟", redisKey, SESSION_KEY_EXPIRE_MINUTES);
        } else {
            logger.warn("session_key 为空，可能是开发环境模拟数据");
        }

        // 生成JWT token
        String token = jwtUtil.generateToken(openid);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setOpenid(openid);

        // 解析用户信息，并保存/更新用户表
        if (userInfo != null && !userInfo.isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode userInfoJson = mapper.readTree(userInfo);
                String nickname = userInfoJson.has("nickName") ? userInfoJson.get("nickName").asText() : "";
                String avatarUrl = userInfoJson.has("avatarUrl") ? userInfoJson.get("avatarUrl").asText() : "";

                response.setNickname(nickname);
                response.setAvatarUrl(avatarUrl);

                // 保存或更新用户信息到数据库
                saveOrUpdateUser(openid, nickname, avatarUrl);

            } catch (Exception e) {
                logger.error("解析用户信息失败", e);
            }
        }

        logger.info("登录完成, openid={}", openid);
        return response;
    }

    /**
     * 保存或更新用户信息
     */
    private void saveOrUpdateUser(String openid, String nickname, String avatarUrl) {
        try {
            User user = userRepository.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                            .eq(User::getOpenid, openid)
            );

            if (user == null) {
                // 新建用户
                user = new User();
                user.setOpenid(openid);
                user.setNickname(nickname);
                user.setAvatarUrl(avatarUrl);
                user.setCreatedAt(LocalDateTime.now());
                userRepository.insert(user);
                logger.info("新建用户, openid={}, nickname={}", openid, nickname);
            } else {
                // 更新用户信息
                user.setNickname(nickname);
                user.setAvatarUrl(avatarUrl);
                user.setUpdatedAt(LocalDateTime.now());
                userRepository.updateById(user);
                logger.info("更新用户信息, openid={}, nickname={}", openid, nickname);
            }
        } catch (Exception e) {
            logger.error("保存用户信息失败, openid={}", openid, e);
        }
    }

    /**
     * 绑定手机号
     */
    public String bindPhone(String openid, String encryptedData, String iv) {
        logger.info("开始绑定手机号, openid={}", openid);

        // 参数校验
        if (encryptedData == null || encryptedData.isEmpty()) {
            logger.error("encryptedData 为空");
            throw new RuntimeException("加密数据不能为空");
        }
        if (iv == null || iv.isEmpty()) {
            logger.error("iv 为空");
            throw new RuntimeException("加密向量不能为空");
        }

        // 1. 从 Redis 获取 session_key
        String redisKey = SESSION_KEY_PREFIX + openid;
        Object sessionKeyObj = redisTemplate.opsForValue().get(redisKey);

        // 处理 Redis 序列化问题，确保转换为 String
        String sessionKey = null;
        if (sessionKeyObj != null) {
            if (sessionKeyObj instanceof String) {
                sessionKey = (String) sessionKeyObj;
            } else {
                // 如果是其他类型（如 LinkedHashMap），尝试转换为 String
                sessionKey = sessionKeyObj.toString();
            }
        }

        if (sessionKey == null || sessionKey.isEmpty()) {
            logger.error("session_key 已过期或不存在, openid={}, redisKey={}", openid, redisKey);
            throw new RuntimeException("登录已过期，请重新登录后再试");
        }

        logger.info("从 Redis 获取 session_key 成功, key={}", redisKey);

        try {
            // 2. 解密微信数据
            String decryptedJson = weChatUtil.decryptPhoneNumber(sessionKey, encryptedData, iv);

            // 3. 提取手机号
            String phoneNumber = weChatUtil.extractPhoneNumber(decryptedJson);

            logger.info("微信手机号解密成功, openid={}, 手机号前3位={}", openid, phoneNumber.substring(0, 3));

            // 4. 加密存储到数据库
            String encryptedPhone = cryptoUtil.encrypt(phoneNumber);
            saveOrUpdateUserPhone(openid, encryptedPhone);

            // 5. 返回脱敏后的手机号
            String maskedPhone = cryptoUtil.maskPhone(phoneNumber);
            logger.info("手机号绑定完成, openid={}, 脱敏手机号={}", openid, maskedPhone);

            return maskedPhone;

        } catch (Exception e) {
            logger.error("手机号绑定失败, openid={}, error={}", openid, e.getMessage(), e);
            throw new RuntimeException("手机号绑定失败: " + e.getMessage());
        }
    }

    /**
     * 保存或更新用户手机号
     */
    private void saveOrUpdateUserPhone(String openid, String encryptedPhone) {
        try {
            User user = userRepository.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                            .eq(User::getOpenid, openid)
            );

            if (user == null) {
                // 用户不存在，先创建
                user = new User();
                user.setOpenid(openid);
                user.setPhoneEncrypted(encryptedPhone);
                user.setCreatedAt(LocalDateTime.now());
                userRepository.insert(user);
                logger.info("新建用户并绑定手机号, openid={}", openid);
            } else {
                // 更新手机号
                user.setPhoneEncrypted(encryptedPhone);
                user.setUpdatedAt(LocalDateTime.now());
                userRepository.updateById(user);
                logger.info("更新用户手机号, openid={}", openid);
            }
        } catch (Exception e) {
            logger.error("保存用户手机号失败, openid={}", openid, e);
            throw new RuntimeException("保存手机号失败");
        }
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
