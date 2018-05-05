package com.javamalls.payment.chinabank.h5.util;

/**
 * 说明：异步通知
 * author:wyyusong
 * Date:14-8-25
 * Time:上午11:34
 */
public class AsynNotificationReqDto {

    /**
     * 接口版本
     */
    private String version;

    /**
     * 商户号
     */
    private String merchant;

    /**
     * 终端号
     */
    private String terminal;

    /**
     * 订单号
     */
    private String orderId;

    /**
     * 加密数据
     */
    private String data;

    /**
     * 签名
     */
    private String sign;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

}
