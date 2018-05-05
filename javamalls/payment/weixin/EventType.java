package com.javamalls.payment.weixin;

/**
 * Created by cjl on 2016-09-19 15:13:57
 */
public enum EventType {
    SUBSCRIBE("subscribe"),
    UNSUBSCRIBE("unsubscribe"),
    SCAN("SCAN"),
    LOCATION("LOCATION"),
    CLICK("CLICK"),
    VIEW("VIEW");
    private String eventType;
    EventType(String eventType){
        this.eventType = eventType;
    }


    @Override
    public String toString() {
        return this.eventType;
    }

}
