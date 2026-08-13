package cn.crabc.core.app.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import javax.servlet.http.HttpServletRequest;

import java.util.Date;
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
    private static String secret = "KLSDJIWKD27JKZDFJALKJKD82KM90DL1NWMD";
    public static final String TOKEN_PREFIX = "bearer ";
    public static long expirationTime = 1000L * 60 * 60 * 8;  // 8小时过期

    /**
     * 创建令牌
     */
    public static String createToken(Long userId, String userName) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("userName", userName);
        return createToken(claims);
    }

    /**
     * 生成令牌
     */
    public static String createToken(Map<String, Object> claims) {
        String uuid = UUID.randomUUID().toString();
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTime);
        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setHeaderParam("typ", "JWT")
                    .setHeaderParam("alg", "HS256")
                    .setExpiration(expiration)
                    .setId(uuid)
                    .signWith(SignatureAlgorithm.HS256, secret.getBytes())
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
                    .setSigningKey(secret.getBytes())
                    .parseClaimsJws(token)
                    .getBody();
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
