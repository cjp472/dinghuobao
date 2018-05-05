package com.javamalls.payment.weixin.model;
/**
 * 模版消息 Data参数Bean
 */
public class MessageData {
	private String value;
	private String color="#1C86EE";
	
	
	public MessageData(String value) {
		super();
		this.value = value;
	}
	public MessageData(String value, String color) {
		super();
		this.value = value;
		this.color = color;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	
	
}
