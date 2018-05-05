package com.javamalls.front.web.h5.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.payment.weixin.util.MobileUtil;
import com.javamalls.payment.weixin.vo.ResultMsg;
import com.javamalls.platform.domain.OrderForm;
import com.javamalls.platform.domain.Payment;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.service.IOrderFormService;
import com.javamalls.platform.service.IPaymentService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;
import com.javamalls.platform.service.IWeixinService;

@Controller
public class H5WeixinController {
	private static final Logger logger = Logger.getLogger(H5WeixinController.class);
	@Autowired
	private IOrderFormService	orderFormService;
	@Autowired
	private IPaymentService		paymentService;
	@Autowired
	private IWeixinService		weixinService;
	@Autowired
	private ISysConfigService	configService;
	@Autowired
	private IUserConfigService	userConfigService;
	@Autowired
	private IUserService		userService;
	/**
     * 微信注入权限
     * @param request
     * @param response
     * @param url
     */
	@RequestMapping(value = "/store/{storeId}.htm/mobile/initJsApiConfig.htm")
	public void initJsApiConfig(HttpServletRequest request, HttpServletResponse response,String url,String paymentid) {
		Payment payment  = this.paymentService.getObjById(CommUtil.null2Long(paymentid));
    	User user = SecurityUserHolder.getCurrentUser();
    	ResultMsg rm = this.weixinService.initJsApiConfig(url,payment,user,request);
        response.setContentType("json/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            String jsonStr = JSONObject.toJSONString(rm);
            writer.print(jsonStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	/**
     * 商品订单-去支付
     * @param request
     * @param response
     * @param id
     * @param order_status
     * @return
     */
    @RequestMapping({ "/store/{id}.htm/mobile/choiceDoWxPay" })
    public ModelAndView payChoice(HttpServletRequest request, HttpServletResponse response,
                                  String id) {
        String code = request.getParameter("code");
        ModelAndView model = new JModelAndView("h5/choiceDoWxPay.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
                response);
        model.addObject("code", code);
        OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if(of!=null&&of.getPayment()!=null){
        	Payment payment = this.paymentService.getObjById(of.getPayment().getId());
            //获取用户openID(JSAPI支付必须传openid)
            String openid = MobileUtil.getOpenId(code,payment);
            model.addObject("paymentid", payment.getId());
            model.addObject("openid", openid);
            model.addObject("obj", of);
        }else{
        	model.addObject("paymentid", null);
            model.addObject("openid", null);
            model.addObject("obj", of);
        }
        return model;
        
    }
    /**
     * 微信支付-订单
     * @param request
     * @param order_id
     * @return
     */
    @RequestMapping(value = { "/store/{storeId}.htm/mobile/wxpay.htm" })
    public void pay(HttpServletRequest request, HttpServletResponse response, String id,
                    String code, String openid) {
        ResultMsg rmg = new ResultMsg();
        OrderForm order = this.orderFormService.getObjById(CommUtil.null2Long(id));
        logger.info("微信支付-预下单开始 code:"+code);
        response.setContentType("json/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        String notify_url = CommUtil.getURL(request) + "/mobile/wxNotify_url.htm";
   	    //生成微信预下单，并保存支付日志paylogs
        rmg =  this.weixinService.generateOrder(order, code, openid, notify_url);
        logger.info("微信支付-预下单完毕");
        try {
            PrintWriter writer = response.getWriter();
            String jsonStr = JSONObject.toJSONString(rmg);
            writer.print(jsonStr);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    /**
	 * 绑定微信
	 * @param request
	 * @param redirect_uri
	 * @return
	 */
	@RequestMapping("/store/{storeId}.htm/mobile/{userId}/bindWxOpenIdToMember.htm")
    public String bindWxOpenIdToMember(HttpServletRequest request,@PathVariable String storeId,String backUrl,@PathVariable String userId) {
		Payment payment =null;
   	 	List<Payment> list = this.paymentService.query("" +
				" select obj from Payment obj where obj.disabled = false and obj.mark='weixin_wap' and obj.type='admin' ", null, -1, -1);
     	if(list!=null&&list.size()>0){
     		payment = list.get(0);
     	}
		//绑定用户openid
		//微信网页授权第一步：用户同意授权，获取code;并放入缓存中。
        String code = request.getParameter("code");
        User  user = this.userService.getObjById(CommUtil.null2Long(userId));
        //用户绑定微信
        ResultMsg rmg = this.weixinService.bindWxOpenIdToMember(code, user.getId(),payment);
		return	"redirect:"+backUrl;
	}
    /**
   	 *微信登录点击事件
   	 * @param request
   	 * @param redirect_uri
   	 * @return
   	 */
   	@RequestMapping("/store/{storeId}.htm/mobile/toWxOpenIdOrToken.htm")
    public String toWxOpenIdOrToken(HttpServletRequest request,@PathVariable String storeId) {
   		String redirect_uri = CommUtil.getURL(request)+"/mobile/wxOpenIdOrToken.htm";
   		Payment payment =null;
   	 	List<Payment> list = this.paymentService.query("" +
				" select obj from Payment obj where obj.disabled = false and obj.mark='weixin_wap' and obj.type='admin' ", null, -1, -1);
     	if(list!=null&&list.size()>0){
     		payment = list.get(0);
     	}
		String url="https://open.weixin.qq.com/connect/oauth2/authorize?appid="+payment.getWeixin_appId()+"&redirect_uri="+redirect_uri+"&response_type=code&scope=snsapi_userinfo&state=0&connect_redirect=1#wechat_redirect";
   		return	"redirect:"+url;
   	}
    /**
	 * 微信-获取openid
	 * @param request
	 * @param redirect_uri
	 * @return
	 */
	@RequestMapping("/store/{storeId}.htm/mobile/wxOpenIdOrToken.htm")
    public String getWxOpenIdOrToken(HttpServletRequest request,@PathVariable String storeId) {
		//绑定用户openid
		//微信网页授权第一步：用户同意授权，获取code;并放入缓存中。
        String code = request.getParameter("code");
        Payment payment =null;
        List<Payment> list = this.paymentService.query("" +
				" select obj from Payment obj where obj.disabled = false and obj.mark='weixin_wap' and obj.type='admin' ", null, -1, -1);
        if(list!=null&&list.size()>0){
        	payment = list.get(0);
        }
        //获取微信唯一标识openid
        String openid = this.weixinService.getWxOpenIdOrToken(code, 0, payment);
        //根据openid获取/创建 User用户
        User user = this.userService.getUserByWxOpenid(openid);
        //登录 
        String url = "/jm_login.htm?username="+user.getUserName()+"&password=jm_thid_login_"+user.getPassword()+"&encode=true&is_mobile=false&jm_view_type=mobile&jm_store_id="+storeId;
		return	"redirect:"+url;
	}
	
    
    
}
