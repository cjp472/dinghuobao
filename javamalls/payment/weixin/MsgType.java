package com.javamalls.payment.weixin;

/**
 * Created by cjl 2016-09-19
 */
public enum MsgType {

    TEXT("text"),
    IMAGE("image"),
    VOICE("voice"),
    VIDEO("video"),
    SHORTVIDEO("shortvideo"),
    LOCATION("location"),
    LINK("link"),
    EVENT("event");
    private String msgType;

    MsgType(String msgType){
        this.msgType = msgType;
    }

    @Override
    public String toString() {
        return this.msgType;
    }

}
