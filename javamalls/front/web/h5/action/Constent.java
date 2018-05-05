package com.javamalls.front.web.h5.action;

import java.util.HashMap;
import java.util.Map;

public class Constent {

    public static final String                     WEB_TYPE_KEY               = "jm_view_type";

    /**首页分页数量
     *Comment for <code>INDEX_GOODS_PAGE_SIZE</code>
     */
    public static final Integer                    INDEX_GOODS_PAGE_SIZE      = 6;

    /**商品列表页面分页数量
     *Comment for <code>GOODS_LIST_PAGE_SIZE</code>
     */
    public static final Integer                    GOODS_LIST_PAGE_SIZE       = 3;

    /**商品分类列表页面分页数量
     *Comment for <code>GOODS_LIST_PAGE_SIZE</code>
     */
    public static final Integer                    CLASS_GOODS_LIST_PAGE_SIZE = 6;

    /**每日特价列表页面分页数量
     *Comment for <code>BARGAIN_LIST_PAGE_SIZE</code>
     */
    public static final Integer                    BARGAIN_LIST_PAGE_SIZE     = 6;

    /**团购列表页面分页数量
     *Comment for <code>GROUP_LIST_PAGE_SIZE</code>
     */
    public static final Integer                    GROUP_LIST_PAGE_SIZE       = 1;

    /**
     *Comment for <code>PAY_ORDER_TYPE</code>
     */
    public static Map<String, String>              PAY_ORDER_TYPE             = new HashMap<String, String>();

    /**
     *Comment for <code>ALIPAY_ORDER</code>
     */
    public static Map<String, Map<String, String>> ALIPAY_ORDER               = new HashMap<String, Map<String, String>>();
    public static Map<String, Object>              CHINA_BANK_PAY             = new HashMap<String, Object>();

    //客服代码
    public static final String                     CUS_SER_CODE               = "cus_ser_code";

}
