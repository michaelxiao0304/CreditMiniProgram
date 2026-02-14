package com.credit.miniapp.security;

import com.credit.miniapp.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行预检请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 放行公开接口
        String uri = request.getRequestURI();
        if (uri.startsWith("/api/auth/login") ||
            uri.startsWith("/api/products") ||
            uri.startsWith("/api/banks") ||
            uri.startsWith("/api/consultant") ||
            uri.startsWith("/swagger") ||
            uri.startsWith("/v3/api-docs")) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtUtil.validateToken(token)) {
                String openid = jwtUtil.getOpenidFromToken(token);
                request.setAttribute("openid", openid);
                return true;
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"code\":401,\"msg\":\"未授权\"}");
        return false;
    }
}
