package com.javamalls.payment.chinabank.h5.util;

import java.io.Serializable;

/**
 * Created by wywangzhenlong on 14-5-5.
 */
public class PaySignEntity implements Serializable {

    /**
     * 接口版本
     */
    private String version;

    /**
     * 用户交易令牌
     */
    private String token;

    /**
     * 商户签名
     */
    private String merchantSign;

    /**
     * 商户号
     */
    private String merchantNum;

    /**
     * 商户备注
     */
    private String merchantRemark;

    /**
     * 交易流水号
     */
    private String tradeNum;

    /**
     * 交易名称
     */
    private String tradeName;

    /**
     * 交易描述
     */
    private String tradeDescription;

    /**
     * 交易时间
     */
    private String tradeTime;

    /**
     * 交易金额
     */
    private String tradeAmount;

    /**
     * 货比种类
     */
    private String currency;

    /**
     * 异步通知页面地址
     */
    private String notifyUrl;

    /**
     *支付成功跳转路径
     */
    private String successCallbackUrl;

    /**
     *支付失败跳转路径
     */
    private String failCallbackUrl;


    public String getMerchantSign() {
        return merchantSign;
    }

    public void setMerchantSign(String merchantSign) {
        this.merchantSign = merchantSign;
    }

    public String getMerchantNum() {
        return merchantNum;
    }

    public void setMerchantNum(String merchantNum) {
        this.merchantNum = merchantNum;
    }

    public String getMerchantRemark() {
        return merchantRemark;
    }

    public void setMerchantRemark(String merchantRemark) {
        this.merchantRemark = merchantRemark;
    }

    public String getTradeNum() {
        return tradeNum;
    }

    public void setTradeNum(String tradeNum) {
        this.tradeNum = tradeNum;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public String getTradeDescription() {
        return tradeDescription;
    }

    public void setTradeDescription(String tradeDescription) {
        this.tradeDescription = tradeDescription;
    }

    public String getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(String tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSuccessCallbackUrl() {
        return successCallbackUrl;
    }

    public void setSuccessCallbackUrl(String successCallbackUrl) {
        this.successCallbackUrl = successCallbackUrl;
    }

    public String getFailCallbackUrl() {
        return failCallbackUrl;
    }

    public void setFailCallbackUrl(String failCallbackUrl) {
        this.failCallbackUrl = failCallbackUrl;
    }
}
