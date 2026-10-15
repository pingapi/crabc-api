package cn.crabc.core.app.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置
 * <p>
 * 签名密钥不允许在代码或配置文件中保留默认值，必须由部署环境注入
 * （环境变量 {@code JWT_SECRET}），缺失或仍为已知的旧密钥时应用启动失败。
 *
 * @author yuqf
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "crabc.jwt")
public class JwtProperties {

    /** 当前签名密钥，Base64 编码，解码后不少于 32 字节（HS256 要求 256 位） */
    private String secret;

    /** 当前签名密钥标识，写入令牌头部的 kid；仅用于识别令牌来源，不参与密钥选择 */
    private String keyId = "k1";

    /** 令牌有效期（分钟），默认 480 分钟即 8 小时 */
    private long expireMinutes = 480;

    /** 签发者，校验时强制匹配 */
    private String issuer = "apigo";

    /** 受众，校验时强制匹配 */
    private String audience = "apigo-admin";

    /** 允许的时钟偏移（秒），用于容忍服务端之间的时间差 */
    private long clockSkewSeconds = 60;
}
