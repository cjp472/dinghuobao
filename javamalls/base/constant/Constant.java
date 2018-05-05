package com.javamalls.base.constant;

import java.util.HashMap;
import java.util.Map;

public class Constant {
    //新版阿里大鱼短信迁移后接口参数
    /**
     * 产品名称:云通信短信API产品,开发者无需替换
     */
    public static String                     product                       = "Dysmsapi";
    /**
     * 产品域名,开发者无需替换
     */
    public static String                     domain                        = "dysmsapi.aliyuncs.com";

    public static int                        SINGLE_GOODS_NUM              = 16;                                                                                                                                                                                                                                                        //单件
    public static int                        PACKGET_GOODS_NUM             = 20;                                                                                                                                                                                                                                                        //整包
    public static int                        SHARE_GOODS_NUM               = 21;                                                                                                                                                                                                                                                        //走份
    public static String                     API_URL                       = "http://47.94.0.153:5011/api/v1/call";

    //删除时上送json串，批量删除时上送json数组
    //用户接口地址
    public static String                     USER_INTEFACE_URL_ADD         = API_URL + "/user_add";
    public static String                     USER_INTEFACE_URL_EDIT        = API_URL + "/user_edit";
    public static String                     USER_INTEFACE_URL_DEL         = API_URL + "/user_del";

    //调用公司接口
    public static String                     COMPANY_INTEFACE_URL_ADD      = API_URL
                                                                             + "/company_add";
    public static String                     COMPANY_INTEFACE_URL_EDIT     = API_URL
                                                                             + "/company_edit";
    public static String                     COMPANY_INTEFACE_URL_DEL      = API_URL
                                                                             + "/company_del";
    //调用店铺接口
    public static String                     STORE_INTEFACE_URL_ADD        = API_URL + "/store_add";
    public static String                     STORE_INTEFACE_URL_EDIT       = API_URL
                                                                             + "/store_edit";
    public static String                     STORE_INTEFACE_URL_DEL        = API_URL + "/store_del";
    //调用供采关系接口接口
    public static String                     STORE_USER_RELATION_URL_ADD   = API_URL
                                                                             + "/user_store_relation_add";
    public static String                     STORE_USER_RELATION_URL_EDIT  = API_URL
                                                                             + "/user_store_relation_edit";
    public static String                     STORE_USER_RELATION_URL_DEL   = API_URL
                                                                             + "/user_store_relation_del";

    //调用品牌接口
    public static String                     STORE_BRAND_URL_ADD           = API_URL + "/brand_add";
    public static String                     STORE_BRAND_URL_EDIT          = API_URL
                                                                             + "/brand_edit";
    public static String                     STORE_BRAND_URL_DEL           = API_URL + "/brand_del";

    //调用用户分类接口
    public static String                     STORE_GOODSCLASS_URL_ADD      = API_URL
                                                                             + "/usergoodsclass_add";
    public static String                     STORE_GOODSCLASS_URL_EDIT     = API_URL
                                                                             + "/usergoodsclass_edit";
    public static String                     STORE_GOODSCLASS_URL_DEL      = API_URL
                                                                             + "/usergoodsclass_del";
    public static String                     STORE_GOODSCLASS_URL_BATCHADD = API_URL
                                                                             + "/usergoodsclass_batchadd";

    //调用商品接口
    public static String                     GOODS_URL_ADD                 = API_URL + "/goods_add";
    public static String                     GOODS_URL_EDIT                = API_URL
                                                                             + "/goods_edit";
    public static String                     GOODS_URL_DEL                 = API_URL + "/goods_del";
    public static String                     GOODS_URL_BATCHADD            = API_URL
                                                                             + "/goods_batchadd";

    //调用规格接口
    public static String                     STORE_GOODSSPEC_URL_ADD       = API_URL
                                                                             + "/goodsspec_add";
    public static String                     STORE_GOODSSPEC_URL_EDIT      = API_URL
                                                                             + "/goodsspec_edit";
    public static String                     STORE_GOODSSPEC_URL_DEL       = API_URL
                                                                             + "/goodsspec_del";
    //新增规格值
    public static String                     STORE_GOODSSPECPRO_URL_ADD    = API_URL
                                                                             + "/goodsSpecProperty_add";
    //调用规格模板接口
    public static String                     STORE_GOODSTYPE_URL_ADD       = API_URL
                                                                             + "/goodstype_add";
    public static String                     STORE_GOODSTYPE_URL_EDIT      = API_URL
                                                                             + "/goodstype_edit";
    public static String                     STORE_GOODSTYPE_URL_DEL       = API_URL
                                                                             + "/goodstype_del";

    //调用订单接口
    //创建订单
    public static String                     STORE_ORDERFORM_URL_ADD       = API_URL
                                                                             + "/orderform_add";
    //取消订单，收货，发货
    public static String                     STORE_ORDERFORM_URL_EDIT      = API_URL
                                                                             + "/orderform_edit";

    //订单状态  
    public static final Map<String, String>  ORDER_STATUS_VALUE            = new HashMap<String, String>() {
                                                                               {
                                                                                   put("0", "已取消");
                                                                                   put("10", "待付款");
                                                                                   put("15",
                                                                                       "线下支付待审核");
                                                                                   put("16",
                                                                                       "货到付款待发货");
                                                                                   put("20", "已付款");
                                                                                   put("25",
                                                                                       "买家申请取消订单");
                                                                                   put("30", "已发货");
                                                                                   put("40", "已收货");
                                                                                   put("45",
                                                                                       "买家申请退货");
                                                                                   put("46", "退货中");
                                                                                   put("47",
                                                                                       "退货完成，已结束");
                                                                                   put("48",
                                                                                       "卖家拒绝退货");
                                                                                   put("49", "退货失败");
                                                                                   put("50",
                                                                                       "已完成,已评价");
                                                                                   put("60", "已结束");
                                                                                   put("65",
                                                                                       "已结束，不可评价");
                                                                                   put("265",
                                                                                       "已申请结算");
                                                                                   put("270", "已结算");
                                                                               }
                                                                           };
    public static final String               SF_FILE_SEPARATOR             = System
                                                                               .getProperty("file.separator");                                                                                                                                                                                                                          //文件分隔符
    public static final String               SF_LINE_SEPARATOR             = System
                                                                               .getProperty("line.separator");                                                                                                                                                                                                                          //行分隔符
    public static final String               SF_PATH_SEPARATOR             = System
                                                                               .getProperty("path.separator");                                                                                                                                                                                                                          //路径分隔符
    //public static final String QRCODE_PATH = ClassUtils.getDefaultClassLoader().getResource("static").getPath()+SF_FILE_SEPARATOR+"qrcode"; 
    //微信账单 相关字段 用于load文本到数据库
    public static final String               WEIXIN_BILL                   = "tradetime, ghid, mchid, submch, deviceid, wxorder, bzorder, openid, tradetype, tradestatus, bank, currency, totalmoney, redpacketmoney, wxrefund, bzrefund, refundmoney, redpacketrefund, refundtype, refundstatus, productname, bzdatapacket, fee, rate";

    public static final String               PATH_BASE_INFO_XML            = SF_FILE_SEPARATOR
                                                                             + "WEB-INF"
                                                                             + SF_FILE_SEPARATOR
                                                                             + "xmlConfig"
                                                                             + SF_FILE_SEPARATOR;

    //实体店下单支付方式  1 银联在线,2支付宝,3 微信，4 银行卡（线下）,5 现金（线下）
    public static final Map<Integer, String> PHYSTORE_PAY_TYPE             = new HashMap<Integer, String>() {
                                                                               {
                                                                                   put(1, "银联在线");
                                                                                   put(2, "支付宝");
                                                                                   put(3, "微信");
                                                                                   put(4, "银行卡");
                                                                                   put(5, "现金");
                                                                               }
                                                                           };

}
