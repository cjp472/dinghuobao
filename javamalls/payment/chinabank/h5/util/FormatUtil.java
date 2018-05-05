package com.javamalls.payment.chinabank.h5.util;

/**
 * 数据格式化通用类
 *
 * @author gaozhenhai
 * @version 1.0.0_1
 * @since 2013.01.15
 */
public class FormatUtil {

    /**
     * null "" 格式化为""
     *
     * @param value
     * @return ""
     */
    public static String stringBlank(String value) {
        if (value == null || value.equals("")) {
            value = "";
        }
        return value.replaceAll("\r|\n", "");
    }

}