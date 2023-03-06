package cn.crabc.core.system.util;

import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA加/解密工具类
 *
 *
 */
public class RSAUtils {

    /**
     * 公钥解密
     *
     * @param pubKey
     * @param text
     * @return
     * @throws Exception
     */
    public static String decryptByPubKey(String pubKey, String text) throws Exception {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64Utils.decodeFromString(pubKey));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] result = Base64Utils.decodeFromString(text);
        return new String(result);
    }

    /**
     * 私钥加密
     *
     * @param priKey
     * @param text
     * @return
     * @throws Exception
     */
    public static String encryptByPriKey(String priKey, String text) throws Exception {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64Utils.decodeFromString(priKey));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] result = cipher.doFinal(text.getBytes());
        return Base64Utils.encodeToString(result);
    }

    /**
     * 私钥解密
     *
     * @param priKey
     * @param text
     * @return
     * @throws Exception
     */
    public static String decryptByPriKey(String priKey, String text) throws Exception {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec5 = new PKCS8EncodedKeySpec(Base64Utils.decodeFromString(priKey));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec5);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] result = cipher.doFinal(Base64Utils.decodeFromString(text));
        return new String(result);
    }

    /**
     * 公钥加密
     *
     * @param pubKey
     * @param text
     * @return
     */
    public static String encryptByPubKey(String pubKey, String text) throws Exception {
        X509EncodedKeySpec x509EncodedKeySpec2 = new X509EncodedKeySpec(Base64Utils.decodeFromString(pubKey));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec2);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] result = cipher.doFinal(text.getBytes());
        return Base64Utils.encodeToString(result);
    }

    /**
     * 构建RSA密钥对
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static Map<String,String> getKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        String pubKey = Base64Utils.encodeToString(rsaPublicKey.getEncoded());
        String priKey = Base64Utils.encodeToString(rsaPrivateKey.getEncoded());
        Map<String,String> keyMap = new HashMap<>();
        keyMap.put("pubKey", pubKey);
        keyMap.put("priKey", priKey);
        return keyMap;
    }

}