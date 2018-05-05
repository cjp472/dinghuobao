package com.javamalls.payment.weixin.model;

import java.util.Dictionary;
import java.util.List;
import java.util.Map;

/**
 * 模版消息
 * @author cjl
 *
 */
public class WeChatTemplateMsg {

	private String touser;//接收者openid
	private String template_id;//模版消息id
	private String url;//模板跳转链接
	public Map<String, MessageData> data;//模版数据
	 
	/**
	 * 接收者openid
	 * @return
	 */
	public String getTouser() {
		return touser;
	}
	/**
	 * 接收者openid
	 * @param touser
	 */
	public void setTouser(String touser) {
		this.touser = touser;
	}
	/**
	 * 模版消息id
	 * @return
	 */
	public String getTemplate_id() {
		return template_id;
	}
	/**
	 * 模版消息id
	 * @param template_id
	 */
	public void setTemplate_id(String template_id) {
		this.template_id = template_id;
	}
	/**
	 * 模板跳转链接
	 * @return
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * 模板跳转链接
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * 模版数据
	 * @return
	 */
	public Map<String, MessageData> getData() {
		return data;
	}
	/**
	 * 模版数据
	 * @param data
	 */
	public void setData(Map<String, MessageData> data) {
		this.data = data;
	}
	 
	
	
}
