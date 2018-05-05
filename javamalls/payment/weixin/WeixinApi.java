package com.javamalls.payment.weixin;

/**
 * Created by cjl 2016-09-19 
 */
public class WeixinApi {

    // 获取tokenURL
    public static final String URL_ACCESSTOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    //获取用户信息
    public static final String URL_USERINFO = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";

    //H5授权
    public static final String URL_AUTHORIZATION = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=%s&connect_redirect=1#wechat_redirect";

    //H5授权后获得token及openid
    public static final String URL_AUTHORIZATION_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";

    //H5授权后获得用户信息
    public static final String URL_AUTHORIZATION_USERINFO = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";

    //统一下单接口
    public static final String URL_UNIFORM_ORDER = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    //获取jsticket
    public static final String URL_JSTICKET = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=%s&type=jsapi";

}

