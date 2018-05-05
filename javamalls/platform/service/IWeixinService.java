package com.javamalls.platform.service;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import com.javamalls.payment.weixin.UniformOrder;
import com.javamalls.payment.weixin.WeixinPayNotify;
import com.javamalls.payment.weixin.vo.ResultMsg;
import com.javamalls.platform.domain.OrderForm;
import com.javamalls.platform.domain.Payment;
import com.javamalls.platform.domain.User;

public abstract interface IWeixinService {
	/**
	 * 注入权限
	 */
    ResultMsg initJsApiConfig(String url,Payment payment,User user,HttpServletRequest request);
    
    /**
     * 标品订单  构造微信支付下单参数
     */
    ResultMsg togenerateOrder(Payment payment,String out_trade_no,BigDecimal total_price,String code,String openid,String notify_url);
    
    /**
     * 
     * 微信支付回调函数-订单
     * @param notify
     * @param request
     * @return
     */
    ResultMsg wxNotify_url(WeixinPayNotify notify, HttpServletRequest request);
    
    /**
	  * 调用微信接口获取access_token，
	  * 若调用失败，继续调用（最多连续调用3次），若再失败返回null
	  * @param Payment  包含微信的appid和密钥
	  * @param count
	  * @return
	  */
   String upateWxAccessToken(Payment payment,int count);
   
   /**
    *  -微信预下单
    */
   ResultMsg generateOrder(OrderForm order, String code, String openid, String notify_url);
   /**
    * 微信支付下单操作
    */
	ResultMsg toUniformOrder(UniformOrder uniformOrder,Payment payment);
	
	/**
	 * 用户绑定微信
	 * 获取openid，并更新到用户中.
	 */
	ResultMsg	bindWxOpenIdToMember(String code,Long memberId,Payment payment);
	 /**
     * 根据code获取openid与access_token
     * @param type 0：获取openid ，1：获取access_token
     */
    String   getWxOpenIdOrToken(String code,int type,Payment payment);
}
