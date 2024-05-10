package cn.crabc.core.app.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Jwt工具类
 *
 * @author yuqf
 */
public class JwtUtil {
    private static String header = "Authorization";
    // 令牌秘钥
    private static String secret = "crabcjfakdjfaldjflkadjlafjaldkjlaflkalk";
    public static final String TOKEN_PREFIX = "bearer ";

    /**
     * 创建令牌
     */
    public static String createToken(Long userId, String userName) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("userName", userName);
        claims.put("expireTime", System.currentTimeMillis());
        return createToken(claims);
    }

    /**
     * 生成令牌
     */
    public static String createToken(Map<String, Object> claims) {
        String uuid = UUID.randomUUID().toString();
        try {
            return Jwts.builder()
                    .header()
                    .add("typ", "JWT")
                    .add("alg", "HS256")
                    .and()
                    .claims(claims)
                    // 令牌ID
                    .id(uuid)
                    .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                    .compact();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseSignedClaims(token).getPayload();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public static String getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 获取请求token
     *
     * @param request
     * @return token
     */
    public static String getToken(HttpServletRequest request) {
        String token = request.getHeader(header);
        if (token != null && token.startsWith(TOKEN_PREFIX)) {
            token = token.replace(TOKEN_PREFIX, "");
        }
        return token;
    }
}
