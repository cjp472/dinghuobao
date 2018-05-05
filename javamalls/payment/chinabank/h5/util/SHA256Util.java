package com.javamalls.payment.chinabank.h5.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by wywangzhenlong on 14-8-9.
 */
public class SHA256Util {

    //定义摘要算法为SHA-256
    private static final String SHA256 = "SHA-256";

    /**
     * 对字符串进行摘要,摘要算法使用SHA-256
     *
     * @param bts 要加密的字符串的byte数组
     * @return 16进制表示的大写字符串 长度一定是8的整数倍
     */
    public static byte[] encrypt(byte[] bts) {
        MessageDigest md = null;
        byte[] result = null;
        try {
            md = MessageDigest.getInstance(SHA256);
            md.update(bts);
            result = md.digest();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return result;
    }
}
