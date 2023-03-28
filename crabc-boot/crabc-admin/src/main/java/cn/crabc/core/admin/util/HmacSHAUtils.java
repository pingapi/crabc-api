package cn.crabc.core.admin.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * sha 加密工具类
 *
 *
 */
public class HmacSHAUtils {

	private HmacSHAUtils() {
	}

	// 签名算法HmacSha256
	private static final String HMAC_SHA256 = "HmacSHA256";
	
	// 签名算法HmacSha1
	private static final String HMAC_SHA1 = "HmacSHA1";

	/**
	 * HmacSHA256加密
	 * 
	 * @param data
	 *            消息
	 * @param secret
	 *            秘钥
	 * @return 加密后字符串
	 */
	public static String HmacSHA256(String data, String secret) throws Exception {
		return sign(data, secret, HMAC_SHA256);
	}
	
	/**
	 * HmacSHA1加密
	 * 
	 * @param data
	 *            消息
	 * @param secret
	 *            秘钥
	 * @return 加密后字符串
	 */
	public static String HmacSHA1(String data, String secret) throws Exception {
		return sign(data, secret, HMAC_SHA1);
	}
	
	/**
	 * 
	 * @param @param data 加签内容
	 * @param @param key  秘钥
	 * @param @param hmac 算法：HmacSHA256/HmacSHA1
	 * @return String  加密后字符串
	 */
	private static String sign(String data, String key, String hmac) throws Exception  {
		Mac hmacSha256 = Mac.getInstance(hmac);
		byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
		hmacSha256.init(new SecretKeySpec(keyBytes, 0, keyBytes.length, hmac));
		byte[] array = hmacSha256.doFinal(data.getBytes(StandardCharsets.UTF_8));
		return byte2hex(array);
	}

	/**
	 * 二进制转换字符串
	 * @param data
	 * @return String
	 */
	public static String byte2hex(byte[] data) 
	{
	    StringBuilder hs = new StringBuilder();
	    String stmp;
	    for (int n = 0; data!=null && n < data.length; n++) {
	        stmp = Integer.toHexString(data[n] & 0XFF);
	        if (stmp.length() == 1)
	            hs.append('0');
	        hs.append(stmp);
	    }
	    return hs.toString().toUpperCase();
	}
}