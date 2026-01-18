package com.campus.timebank.config;

import com.campus.timebank.common.UserContext;
import com.campus.timebank.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/api/**")       // 拦截所有 api 接口
                .excludePathPatterns("/api/auth/**"); // 放行登录和注册接口
    }

    /**
     * 内部类：定义拦截器逻辑
     */
    class LoginInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            // 1. 获取请求头中的 Token
            // 前端通常格式: Authorization: Bearer <token>
            String authHeader = request.getHeader("Authorization");

            if (!StringUtils.hasLength(authHeader) || !authHeader.startsWith("Bearer ")) {
                response.setStatus(401); // 401 未授权
                return false;
            }

            // 2. 截取 Token 字符串 (去掉 "Bearer " 前缀)
            String token = authHeader.substring(7);

            // 3. 解析 Token
            Long userId = jwtUtil.parseToken(token);
            if (userId == null) {
                response.setStatus(401);
                return false;
            }

            // 4. 将用户ID放入上下文，供 Controller 使用
            UserContext.setUserId(userId);
            return true; // 放行
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
            // 请求结束后，清理线程变量，防止内存泄漏
            UserContext.remove();
        }
    }
}