package cn.crabc.core.app.util;

import cn.crabc.core.app.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Locator;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Jwt工具类
 * <p>
 * 签名密钥由 {@link JwtProperties} 从部署环境注入，代码中不保留任何默认值：
 * 密钥缺失、强度不足或仍是历史泄露密钥时，构造阶段直接抛异常，应用启动失败。
 * 签发与校验只使用这一个密钥，更换密钥后此前签发的令牌立即失效。
 *
 * @author yuqf
 */
@Slf4j
@Component
public class JwtUtil {

    /**
     * 请求头名称
     */
    private static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "bearer ";

    /**
     * 令牌头部算法
     */
    private static final String ALGORITHM = "HS256";

    /**
     * 历史版本中硬编码并已随源码公开的密钥，禁止继续用于签发或校验
     */
    private static final Set<String> LEAKED_SECRETS = Set.of(
            "KLSDJIWKD27JKZDFJALKJKD82KM90DL1NWMD",
            "sk-*****");

    /**
     * HS256 最少需要 256 位密钥
     */
    private static final int MIN_KEY_BYTES = 32;

    private final String issuer;

    private final String audience;

    private final long clockSkewSeconds;

    /**
     * 当前签名密钥标识
     */
    private final String keyId;

    /**
     * 当前签名密钥
     */
    private final SecretKey signingKey;

    /**
     * 令牌有效期（毫秒）
     */
    private final long expirationTime;

    /**
     * 校验密钥定位：只有当前密钥，kid 或算法不一致即拒绝
     */
    private final Locator<Key> keyLocator;

    public JwtUtil(JwtProperties properties) {
        this.issuer = requireText(properties.getIssuer(), "crabc.jwt.issuer");
        this.audience = requireText(properties.getAudience(), "crabc.jwt.audience");
        this.keyId = requireText(properties.getKeyId(), "crabc.jwt.key-id");
        if (properties.getExpireMinutes() <= 0) {
            throw new IllegalStateException("crabc.jwt.expire-minutes 必须大于 0，当前值：" + properties.getExpireMinutes());
        }
        this.expirationTime = properties.getExpireMinutes() * 60_000L;
        this.clockSkewSeconds = Math.max(properties.getClockSkewSeconds(), 0);
        this.signingKey = toSecretKey(properties.getSecret(), "crabc.jwt.secret（环境变量 JWT_SECRET）");

        this.keyLocator = header -> {
            // 只接受 HS256，避免算法被替换为同密钥的其他 MAC 算法
            String alg = header.getAlgorithm();
            if (!ALGORITHM.equals(alg)) {
                throw new UnsupportedJwtException("令牌签名算法不受支持：" + alg);
            }
            Object kid = header.get("kid");
            if (kid == null || !keyId.equals(kid.toString())) {
                // kid 不一致说明不是当前密钥签发，直接拒绝，不回退到任何默认密钥
                throw new UnsupportedJwtException("令牌 kid 与当前签名密钥不一致：" + kid);
            }
            return signingKey;
        };
        log.info("JWT 初始化完成：issuer={}, audience={}, keyId={}, 有效期={}分钟",
                this.issuer, this.audience, this.keyId, properties.getExpireMinutes());
    }

    /**
     * 创建令牌
     */
    public String createToken(Long userId, String userName) {
        Map<String, Object> claims = new HashMap<>(4);
        claims.put("userId", userId);
        claims.put("userName", userName);
        return createToken(claims);
    }

    /**
     * 生成令牌
     * <p>
     * 补充标准声明：iss/aud/iat/exp/jti 与 sub（取 userId），头部写入 kid 以标识密钥来源。
     */
    public String createToken(Map<String, Object> claims) {
        Map<String, Object> safeClaims = claims == null ? new HashMap<>() : new HashMap<>(claims);
        Date now = new Date();
        JwtBuilder builder = Jwts.builder()
                .header().add("typ", "JWT").add("kid", keyId).and()
                .claims(safeClaims)
                .issuer(issuer)
                .audience().add(audience).and()
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationTime))
                .id(UUID.randomUUID().toString());
        Object userId = safeClaims.get("userId");
        if (userId != null) {
            builder.subject(userId.toString());
        }
        try {
            return builder.signWith(signingKey, Jwts.SIG.HS256).compact();
        } catch (RuntimeException e) {
            log.error("JWT 签发失败", e);
        }
        return null;
    }

    /**
     * 从令牌中获取数据声明
     * <p>
     * 校验失败（签名不匹配、kid 未知、iss/aud 不匹配、已过期等）统一返回 null，由调用方拒绝请求，
     * 不再打印异常栈，避免令牌噪声刷屏与异常细节外泄。
     *
     * @param token 令牌
     * @return 数据声明，校验失败返回 null
     */
    public Claims parseToken(String token) {
        if (!hasText(token)) {
            return null;
        }
        try {
            return Jwts.parser()
                    .keyLocator(keyLocator)
                    .requireIssuer(issuer)
                    .requireAudience(audience)
                    .clockSkewSeconds(clockSkewSeconds)
                    .build()
                    .parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            log.debug("JWT 已过期：{}", e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("JWT 校验失败：{}", e.getMessage());
        } catch (RuntimeException e) {
            log.warn("JWT 校验异常", e);
        }
        return null;
    }

    /**
     * 从令牌中获取用户ID（优先取标准声明 sub）
     *
     * @param token 令牌
     * @return 用户ID，令牌无效返回 null
     */
    public String getUserId(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        if (hasText(claims.getSubject())) {
            return claims.getSubject();
        }
        Object userId = claims.get("userId");
        return userId == null ? null : userId.toString();
    }

    /**
     * 获取请求token
     * <p>
     * 前缀大小写不敏感，但不接受无前缀的裸令牌。
     *
     * @param request 请求
     * @return 令牌，缺失或格式不合法返回 null
     */
    public String getToken(HttpServletRequest request) {
        String value = request.getHeader(AUTHORIZATION_HEADER);
        if (value == null) {
            return null;
        }
        value = value.trim();
        if (value.length() <= TOKEN_PREFIX.length()) {
            return null;
        }
        if (!value.regionMatches(true, 0, TOKEN_PREFIX, 0, TOKEN_PREFIX.length())) {
            return null;
        }
        String token = value.substring(TOKEN_PREFIX.length()).trim();
        return hasText(token) ? token : null;
    }

    /**
     * 令牌有效期（秒），用于登录响应与前端保持一致
     */
    public long getExpireSeconds() {
        return expirationTime / 1000L;
    }

    /**
     * 解析 Base64 密钥并校验强度，任何不合法情况都直接中断启动
     */
    private static SecretKey toSecretKey(String rawSecret, String label) {
        String value = rawSecret == null ? "" : rawSecret.trim();
        if (value.isEmpty()) {
            throw new IllegalStateException(label + " 未配置：请由部署环境的密钥存储注入，"
                    + "例如环境变量 JWT_SECRET=\"$(openssl rand -base64 32)\"");
        }
        if (LEAKED_SECRETS.contains(value)) {
            throw new IllegalStateException(label + " 使用了已随源码公开的历史固定密钥，必须更换："
                    + "请重新生成高熵密钥后再启动");
        }
        byte[] bytes;
        try {
            bytes = Decoders.BASE64.decode(value);
        } catch (RuntimeException e) {
            throw new IllegalStateException(label + " 不是合法的 Base64 密钥：请使用 openssl rand -base64 32 生成", e);
        }
        if (bytes.length < MIN_KEY_BYTES) {
            throw new IllegalStateException(label + " 强度不足：HS256 至少需要 " + MIN_KEY_BYTES
                    + " 字节，当前解码后仅 " + bytes.length + " 字节");
        }
        return Keys.hmacShaKeyFor(bytes);
    }

    private static String requireText(String value, String label) {
        if (!hasText(value)) {
            throw new IllegalStateException(label + " 不能为空");
        }
        return value.trim();
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
