package com.javamalls.platform.test;

import java.awt.Font;

import com.javamalls.base.tools.CommUtil;

public class TestWaterMark {
    public static void main(String[] args) {
        String pressImg = "D:\\logo.jpg";
        String targetImg = "D:\\2.jpg";
        int pos = 5;
        float alpha = 0.9F;
        try {
            CommUtil.waterMarkWithText(targetImg, "D:\\2.jpg", "javamalls", "#FF0000", new Font(
                "宋体", 1, 90), pos, Float.MAX_VALUE);
            System.out.println("图片水印完成！");
        } catch (Exception localException) {
        }
    }
}
