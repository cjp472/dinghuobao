package com.javamalls.payment.chinabank.h5.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by wywangzhenlong on 14-8-9.
 */
public class SignUtil {

    // 不需要签名的object中的对象key
    private static List<String> unSignKeyList = Arrays.asList("merchantSign", "token", "version");

    /**
     * 私钥对加密数据进行签名(加密)
     *
     * @param object        需要签名(加密)的对象
     * @param rsaPriKey     私钥
     * @param rsaPriKey     私钥
     * @return
     */
    public static String sign(Object object, String rsaPriKey) {
        String result = "";
        try {
            //获取签名需要字符串和类型
            String sourceSignString = SignUtil.signString(object, unSignKeyList);
            //摘要
            String sha256SourceSignString = SHAUtil.Encrypt(sourceSignString, null);
            System.out.println("===============mpay:" + sourceSignString);
            //私钥对摘要进行加密
            byte[] newsks = RSACoder.encryptByPrivateKey(sha256SourceSignString.getBytes("UTF-8"),
                rsaPriKey);
            result = RSACoder.encryptBASE64(newsks);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 生成签名原串
     *
     * @param object
     * @param unSignKeyList
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static String signString(Object object, List<String> unSignKeyList)
                                                                              throws IllegalArgumentException,
                                                                              IllegalAccessException {
        TreeMap<String, Object> map = SignUtil.objectToMap(object);

        // 拼原String
        StringBuilder sb = new StringBuilder();
        // 删除不需要参与签名的属性
        for (String str : unSignKeyList) {
            map.remove(str);
        }
        // 连接
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            sb.append(entry.getKey() + "=" + (entry.getValue() == null ? "" : entry.getValue())
                      + "&");
        }
        // 去掉最后一个&
        String result = sb.toString();
        if (result.endsWith("&")) {
            result = result.substring(0, result.length() - 1);
        }

        return result;
    }

    /**
     * 对象转map
     *
     * @param object
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static TreeMap<String, Object> objectToMap(Object object)
                                                                    throws IllegalArgumentException,
                                                                    IllegalAccessException {
        TreeMap<String, Object> map = new TreeMap<String, Object>();

        // 父类属性
        for (Class<?> cls = object.getClass(); cls != Object.class; cls = cls.getSuperclass()) {
            // 添加属性key到list
            Field[] fields = cls.getDeclaredFields();
            for (Field f : fields) {
                f.setAccessible(true);
                map.put(f.getName(), f.get(object));
            }
        }
        return map;
    }
}
