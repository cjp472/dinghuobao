package com.javamalls.payment.weixin.model;

import java.io.Serializable;

/**
 * 微信API返回状态
 */
public class ResultState  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int errcode; // 状态  
    
    private String errmsg; //信息  

	public int getErrcode() {
		return errcode;
	}

	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
    
    
}
