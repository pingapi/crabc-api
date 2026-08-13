package cn.crabc.core.app.util;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Security;
import org.apache.commons.codec.binary.Hex;
import java.util.Locale;

/**
 *  国密SM3加密工具类
 *
 * @author yuqf
 */
public class SM3Util {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private SM3Util() {
    }

    /**
     * 字符串的SM3算法哈希值
     *
     * @param data 输入字符串
     * @return 十六进制格式的哈希值
     */
    public static String hash(String data) {
        return hashHex(data);
    }

    /**
     * 字符串的SM3算法哈希值
     *
     * @param data 输入字符串
     * @return 小写十六进制格式的哈希值
     */
    public static String hashHex(String data) {
        return hash(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 字节数组的SM3算法哈希值。
     *
     * @param bytes 输入字节数组
     * @return 小写十六进制格式的哈希值
     */
    public static String hash(byte[] bytes) {
        SM3Digest digest = new SM3Digest();
        digest.update(bytes, 0, bytes.length);
        byte[] result = new byte[digest.getDigestSize()];
        digest.doFinal(result, 0);
        return Hex.encodeHexString(result);
    }

    /**
     * 使用密钥前缀方式生成 SM3 签名，签名串由调用方负责规范化。
     *
     * @param data 待签名字符串
     * @param secret 应用密钥
     * @return 小写十六进制格式的签名
     */
    public static String sign(String data, String secret) {
        return hashHex(secret + data);
    }

    /**
     * 验证带密钥的 SM3 签名，避免调用方手动拼接 secret 和签名串。
     *
     * @param data 待签名字符串
     * @param secret 应用密钥
     * @param sign 预期签名
     * @return 是否验证通过
     */
    public static boolean verify(String data, String secret, String sign) {
        return constantTimeEquals(sign(data, secret), sign);
    }

    private static boolean constantTimeEquals(String expected, String actual) {
        if (expected == null || actual == null) {
            return false;
        }
        return MessageDigest.isEqual(
                expected.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.US_ASCII),
                actual.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.US_ASCII));
    }
}
