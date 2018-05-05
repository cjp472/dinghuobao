package com.javamalls.platform.test;

import com.javamalls.base.tools.CommUtil;

public class TestDouble {
    public static void main(String[] args) {
        float a = 290.0F;
        float b = 211.39999F;
        System.out.println(CommUtil.subtract(Double.valueOf(a), Double.valueOf(b)));
    }
}
