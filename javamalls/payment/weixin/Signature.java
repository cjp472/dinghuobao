/* 
 * Signature.java 
 * Created on  206/30/15 11:20 AM 
 * 版本       修改时间       作者      修改内容 
 * V1.0.1       206/30/15      xiaomc    初始版本 * 
 * Copyright (c) 2015 启航星辰 版权所有 
 * SETSAIL STARS STU.,LTD. All Rights Reserved. 
 */

package com.javamalls.payment.weixin;

/**
 * Created by cjl
 * 签名类的封装
 */
public class Signature {

    //微信加密签名
    private String signature;
    //时间戳
    private String timestamp;
    //随机数
    private String nonce;

    private String url ;

    private String jspApiTicket;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getJspApiTicket() {
        return jspApiTicket;
    }

    public void setJspApiTicket(String jspApiTicket) {
        this.jspApiTicket = jspApiTicket;
    }

    private String echostr;
    public String getSignature() {
        return signature;
    }
    public void setSignature(String signature) {
        this.signature = signature;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public String getNonce() {
        return nonce;
    }
    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
    public String getEchostr() {
        return echostr;
    }
    public void setEchostr(String echostr) {
        this.echostr = echostr;
    }
}
