package com.javamalls.payment.weixin.model;

public class TemplateMsgResult extends ResultState{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3845425983929677012L;
	private String msgid; // 消息id(发送模板消息)
	/**
	 * 消息id(发送模板消息)
	 * @return
	 */
	public String getMsgid() {
		return msgid;
	}
	/**
	 * 消息id(发送模板消息)
	 * @param msgid
	 */
	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}
	
	
}
