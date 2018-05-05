package com.javamalls.payment.weixin.vo;

import java.io.Serializable;

/**
 * Created by cjl 2016-09-20 
 */
public class ResultMsg implements Serializable {

    private Boolean result;

    private String msg;

    private Object data;

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
