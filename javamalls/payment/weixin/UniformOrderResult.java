package com.javamalls.payment.weixin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by cjl on 2016-09-19 
 */
@XmlRootElement(name = "xml")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class UniformOrderResult {

    /*<xml>
    <return_code><![CDATA[SUCCESS]]></return_code>
    <return_msg><![CDATA[OK]]></return_msg>
    <appid><![CDATA[wx2421b1c4370ec43b]]></appid>
    <mch_id><![CDATA[10000100]]></mch_id>
    <nonce_str><![CDATA[IITRi8Iabbblz1Jc]]></nonce_str>
    <sign><![CDATA[7921E432F65EB8ED0CE9755F0E86D72F]]></sign>
    <result_code><![CDATA[SUCCESS]]></result_code>
    <prepay_id><![CDATA[wx201411101639507cbf6ffd8b0779950874]]></prepay_id>
    <trade_type><![CDATA[JSAPI]]></trade_type>
    </xml>*/


    private String return_code;
    private String return_msg;
    private String appid;
    private String mch_id;
    private String nonce_str;
    private String sign;
    private String result_code;
    private String prepay_id;
    private String trade_type;


    @Override
    public String toString() {
        return "UniformOrderResult{" +
                "return_code='" + return_code + '\'' +
                ", return_msg='" + return_msg + '\'' +
                ", appid='" + appid + '\'' +
                ", mch_id='" + mch_id + '\'' +
                ", nonce_str='" + nonce_str + '\'' +
                ", sign='" + sign + '\'' +
                ", result_code='" + result_code + '\'' +
                ", prepay_id='" + prepay_id + '\'' +
                ", trade_type='" + trade_type + '\'' +
                '}';
    }

    public String getReturn_code() {
        return return_code;
    }

    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }

    public String getReturn_msg() {
        return return_msg;
    }

    public void setReturn_msg(String return_msg) {
        this.return_msg = return_msg;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getResult_code() {
        return result_code;
    }

    public void setResult_code(String result_code) {
        this.result_code = result_code;
    }

    public String getPrepay_id() {
        return prepay_id;
    }

    public void setPrepay_id(String prepay_id) {
        this.prepay_id = prepay_id;
    }

    public String getTrade_type() {
        return trade_type;
    }

    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }
}
