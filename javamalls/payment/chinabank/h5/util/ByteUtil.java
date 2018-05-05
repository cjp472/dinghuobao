package com.javamalls.payment.chinabank.h5.util;

import java.io.ByteArrayOutputStream;

/**
 * Created by wywangzhenlong on 14-8-9.
 */
public class ByteUtil {
    private static String hexString = "0123456789ABCDEF";

    /**
     * 转换成16进制表示的大写字符串
     *
     * @param bts 被转换的byte数组
     * @return 16进制表示的字符串
     */
    public static String byte2HexUpperCase(byte[] bts) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des.toUpperCase();
    }

    /**
     * 转换成16进制表示的小写字符串
     *
     * @param bts 被转换的byte数组
     * @return 16进制表示的字符串
     */
    public static String byte2HexLowerCase(byte[] bts) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des.toLowerCase();
    }

    public static String byte2HexString(byte[] src) throws Exception {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        String bytes = stringBuilder.toString();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);
        // 将每2位16进制整数组装成一个字节
        for (int i = 0; i < bytes.length(); i += 2)
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
                    .indexOf(bytes.charAt(i + 1))));
        return new String(baos.toByteArray());
    }
}
