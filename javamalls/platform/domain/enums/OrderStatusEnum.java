package com.javamalls.platform.domain.enums;

public enum OrderStatusEnum {

    ORDER_SUBMIT("order_submit", 10),

    ORDER_GETPAY_CONFIRM("order_getPay_confirm", 15),

    ORDER_PAY("order_pay", 20),

    ORDER_SHIPPING("order_shipping", 30),

    ORDER_RECEIVE("order_receive", 40),

    ORDER_RETURN_APPLY("order_return_apply", 45),

    ORDER_RETURN("order_return", 46),

    ORDER_EVALUATE("order_evaluate", 50),

    ORDER_FINISH("order_finish", 60),

    ORDER_CANCEL("order_cancel", 0);

    // 成员变量  
    private String name;
    private int    index;

    // 构造方法  
    private OrderStatusEnum(String name, int index) {
        this.name = name;
        this.index = index;
    }

    // 普通方法  
    public static String getName(int index) {
        for (OrderStatusEnum c : OrderStatusEnum.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }

    // get set 方法  
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    // 普通方法  
    public static int getEnumIndexByName(String name) {
        for (OrderStatusEnum c : OrderStatusEnum.values()) {
            if (c.getName().equals(name)) {
                return c.index;
            }
        }
        return -1;
    }

}
