package com.campus.timebank.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 1. 定义密钥
    private static final String SECRET_KEY = "campus_time_bank_secure_key_2024_make_it_longer_than_32_bits";

    // 2. 过期时间：24小时 (单位毫秒)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    // 使用 HmacSHA256 算法生成 Key
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    /**
     * 生成 Token
     * @param userId 用户ID
     * @return 加密后的字符串
     */
    public String createToken(Long userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId)) // 将 userId 放入 Token 主体
                .setIssuedAt(new Date())            // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 设置过期时间
                .signWith(key, SignatureAlgorithm.HS256) // 使用 HS256 算法签名
                .compact();
    }

    /**
     * 解析 Token 获取用户ID
     * @param token 前端传来的 Token 字符串
     * @return 用户ID (如果解析失败返回 null)
     */
    public Long parseToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            // 解析失败（比如 Token 过期、被篡改、格式错误）
            return null;
        }
    }
}