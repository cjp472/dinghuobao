package com.javamalls.payment.chinabank.h5.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.security.MessageDigest;

/**
 * Created by lijunfu on 14-4-27.
 */
public class MD5Util {

    /**
     * @param data 明文
     * @return 密文大写
     */
    public static String md5(String data){
        return DigestUtils.md5Hex(data).toUpperCase();
    }

    /**
     * MD5方法
     *
     * @param text 明文
     * @param salt 盐
     * @return 密文
     * @throws Exception
     */
    public static String md5UpperCase(String text, String salt) throws Exception {
        byte[] bytes = (text + salt).getBytes();

        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(bytes);
        bytes = messageDigest.digest();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if ((bytes[i] & 0xff) < 0x10) {
                sb.append("0");
            }
            sb.append(Long.toString(bytes[i] & 0xff, 16));
        }
        return sb.toString().toUpperCase();
    }

    /**
     * MD5方法
     *
     * @param text 明文
     * @param salt 盐
     * @return 密文
     * @throws Exception
     */
    public static String md5LowerCase(String text, String salt) throws Exception {
        byte[] bytes = (text + salt).getBytes();

        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(bytes);
        bytes = messageDigest.digest();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if ((bytes[i] & 0xff) < 0x10) {
                sb.append("0");
            }
            sb.append(Long.toString(bytes[i] & 0xff, 16));
        }
        return sb.toString().toLowerCase();
    }

    /**
     * MD5验证方法
     *
     * @param text 明文
     * @param salt 盐
     * @param md5  密文
     * @return true/false
     * @throws Exception
     */
    public static boolean verify(String text, String salt, String md5) throws Exception {
        String md5Text = md5UpperCase(text, salt);
        if (md5Text.equalsIgnoreCase(md5)) {
            return true;
        } else {
            return false;
        }
    }
}
