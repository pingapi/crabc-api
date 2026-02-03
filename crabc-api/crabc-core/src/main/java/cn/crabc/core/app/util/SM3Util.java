package cn.crabc.core.app.util;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.nio.charset.StandardCharsets;
import java.security.Security;

/**
 *  国密SM3加密工具类
 *
 * @author yuqf
 */
public class SM3Util {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 字符串的SM3算法哈希值
     * @param data 输入字符串
     * @return 十六进制格式的哈希值
     */
    public static String hash(String data) {
        return hash(data.getBytes());
    }

    /**
     * 节数组的SM3算法哈希值
     * @param bytes 输入字节数组
     * @return 十六进制格式的哈希值
     */
    public static String hash(byte[] bytes) {
        SM3Digest digest = new SM3Digest();
        digest.update(bytes, 0, bytes.length);
        byte[] result = new byte[digest.getDigestSize()];
        digest.doFinal(result, 0);
        return new String(Hex.encodeHex(result));
    }

    /**
     * 验证SM3签名哈希值
     * @param data 待签名字符串
     * @param sign 预期哈希值
     * @return 是否验证通过
     */
    public static boolean verify(String data, String sign) {
        String hash = hash(data.getBytes(StandardCharsets.UTF_8));
        return hash.equalsIgnoreCase(sign);
    }
}
