package com.javamalls.front.web.timer;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.javamalls.platform.domain.Payment;
import com.javamalls.platform.service.IPaymentService;
import com.javamalls.platform.service.IWeixinService;

@Component
public class TaskWxAccessToken {
	public final Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private IWeixinService		weixinService;
	@Autowired
	private IPaymentService		paymentService;
	/**
	  * 定时器：每1小时50分钟 获取access_token
	  * 并更新到数据库字典中。
	  */
	@Scheduled(cron="0 0/110 * * * ?") 
	public  void getWxAccessToken(){
		  
        //先获取平台Payment吧，
		List<Payment> list = this.paymentService.query("" +
				" select obj from Payment obj where obj.disabled = false and obj.mark='weixin_wap' and obj.type='admin' ", null, -1, -1);
		if(list!=null&&list.size()>0){
			for(Payment payment:list){
				weixinService.upateWxAccessToken(payment, 0);
			}
			logger.info("定时器：每1小时50分钟 获取access_token 执行完毕");
		}else{
			logger.info("未检测到平台微信支付设置");
		}
		//store的以后再添加
		 
	 }
}
