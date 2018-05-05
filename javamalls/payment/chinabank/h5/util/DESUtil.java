package com.javamalls.payment.chinabank.h5.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;

/**
 * DES通用类
 *
 * @author gaozhenhai
 * @version 1.0.0_1
 * @since 2013.01.15
 */
public class DESUtil {
    /**
     * 生成密钥方法
     *
     * @param seed 密钥种子
     * @return 密钥 BASE64
     * @throws Exception
     */
    public static String generateKey(String seed) throws Exception {
        byte[] seedBase64DecodeBytes = BASE64.decode(seed);

        SecureRandom secureRandom = new SecureRandom(seedBase64DecodeBytes);
        KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
        keyGenerator.init(secureRandom);
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] bytes = secretKey.getEncoded();

        String keyBase64EncodeString = BASE64.encode(bytes);

        return FormatUtil.stringBlank(keyBase64EncodeString);
    }

    /**
     * 加密方法
     *
     * @param text    明文
     * @param key     密钥 BASE64
     * @param charset 字符集
     * @return 密文
     * @throws Exception
     */
    public static String encrypt(String text, String key, String charset) throws Exception {
        byte[] keyBase64DecodeBytes = BASE64.decode(key);//base64解码key
        DESKeySpec desKeySpec = new DESKeySpec(keyBase64DecodeBytes);//前8个字节做为密钥
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] textBytes = text.getBytes(charset);//明文UTF-8格式
        byte[] bytes = cipher.doFinal(textBytes);//DES加密

        String encryptBase64EncodeString = BASE64.encode(bytes);//base64编码

        return encryptBase64EncodeString;
    }

    /**
     *
     * @param text 明文
     * @param key 8字节秘钥
     * @param charset 字符集
     * @return
     * @throws Exception
     */
    public static String encrypt(String text, byte[] key, String charset) throws Exception {
        DESKeySpec desKeySpec = new DESKeySpec(key);//前8个字节做为密钥
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] textBytes = text.getBytes(charset);//明文UTF-8格式
        byte[] bytes = cipher.doFinal(textBytes);//DES加密

        String encryptBase64EncodeString = BASE64.encode(bytes);//base64编码

        return encryptBase64EncodeString;
    }

    /**
     * 解密方法
     *
     * @param text    密文
     * @param key     密钥 BASE64
     * @param charset 字符集
     * @return 明文
     * @throws Exception
     */
    public static String decrypt(String text, String key, String charset) throws Exception {
        byte[] keyBase64DecodeBytes = BASE64.decode(key);

        DESKeySpec desKeySpec = new DESKeySpec(keyBase64DecodeBytes);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] textBytes = BASE64.decode(text);
        byte[] bytes = cipher.doFinal(textBytes);

        String decryptString = new String(bytes, charset);

        return FormatUtil.stringBlank(decryptString);
    }

    /**
     * @param text    密文
     * @param key     8字节秘钥
     * @param charset 字符集
     * @return
     * @throws Exception
     */
    public static String decrypt(String text, byte[] key, String charset) throws Exception {

        DESKeySpec desKeySpec = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] textBytes = BASE64.decode(text);
        byte[] bytes = cipher.doFinal(textBytes);

        String decryptString = new String(bytes, charset);

        return FormatUtil.stringBlank(decryptString);
    }


}