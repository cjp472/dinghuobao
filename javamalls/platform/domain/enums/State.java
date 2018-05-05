package com.javamalls.platform.domain.enums;

/**
 * 状态枚举
 * 
 * @author zhaihl
 * 
 */
public enum State {

    /** 订单退货理由 */
    REFUND_REASON(new Integer[] { 0, 1, 2, 3, 4 }, new String[] { "我不想买了", "信息填写错误，重新拍", "卖家缺货",
                                                                 "同城见面交易", "其它原因" }),
    /** 订单取消理由 申请原因 1：我不想买、2：买错了、3：其它原因*/
    CANCEL_REASON(new Integer[]{1,2,3}, new String[] {"我不想买了","买错了","其它原因"}),
    
    /** 评价类型 */
    EVALUATE_TYPE(new Integer[] { 0, 1 }, new String[] { "发出的评价", "收到的评价" });

    private Integer[] keys;
    private String[]  values;

    /**
     * 构造器。
     * 
     * @param name
     *            名称
     * @param value
     *            值
     */
    private State(Integer[] key, String[] value) {
        this.keys = key;
        this.values = value;
    }

    /**
     * 根据值获取名称
     * 
     * @param name
     *            指定的值
     * @return 相应的名称
     */
    public static String getValue(State st, Integer key) {
        for (Integer i = 0; i < st.keys.length; i++) {
            if (st.keys[i].intValue() == key.intValue())
                return st.values[i];
        }
        return null;
    }

    /**
     * 根据给定的名称获取相应的值
     * 
     * @param value
     *            给定的名称
     * @return 相应的值
     */
    public static Integer getKey(State st, String value) {
        for (int i = 0; i < st.values.length; i++) {
            if (st.values[i].equals(value))
                return st.keys[i];
        }
        return null;
    }

    public Integer[] getKeys() {
        return keys;
    }

    public void setKeys(Integer[] keys) {
        this.keys = keys;
    }

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }

}
