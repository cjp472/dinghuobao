package com.javamalls.payment.weixin.tools;



import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.javamalls.base.tools.CommUtil;
import com.javamalls.payment.weixin.model.Product;
import com.javamalls.platform.domain.OrderForm;
import com.javamalls.platform.domain.Payment;

@Component
public class WeixinPayTools {
     
	@Autowired
	private HttpServletRequest  request;
	/*@Autowired
	private WxChatProperties		   wxproperties;*/
	/**
	 * 付款按钮点击时使用
	 * @param id
	 * @return
	 * obj:订单
	 * mark:以前是 
	 * redirect_uri：去支付路径
	 * 返回:跳转url
	 */
    public String weixinPayMobileUrl(OrderForm obj,Payment payment,String mark,String redirect_uri) {
    	Product product = new Product();
    	product.setOutTradeNo(obj.getOrder_id());
    	product.setSubject("微信支付_"+mark+"_"+obj.getId());
    	 //总金额(单位是分)
    	BigDecimal baiFee = new BigDecimal(100);
    	int totalFee = baiFee.multiply(obj.getTotalPrice()).intValue();
    	product.setTotalFee(totalFee+""); 
    	product.setBody("微信支付_"+mark+"_"+obj.getId());
    	//String redirect_uri=CommUtil.getURL(request)+"/mobile/buyer/order_fuwu_pay.htm?id="+id;
    	String url =  this.weixinPayMobile(payment,product,request,redirect_uri);
       return url;
    }
    
  
    
    private String weixinPayMobile(Payment payment,Product product,HttpServletRequest request,String redirect_uri) {
		 
		StringBuffer url = new StringBuffer();
		String totalFee = product.getTotalFee();
		//redirect_uri 需要在微信支付端添加认证网址
		totalFee =  CommUtil.subZeroAndDot(totalFee);
		url.append("http://open.weixin.qq.com/connect/oauth2/authorize?");
		url.append("appid="+payment.getWeixin_appId());
		url.append("&redirect_uri="+redirect_uri);
		url.append("&response_type=code&scope=snsapi_base&state=");
		url.append(product.getOutTradeNo()+"/"+totalFee);//订单号/金额(单位是分)
		url.append("#wechat_redirect");
		return  url.toString();
	}
  
}
