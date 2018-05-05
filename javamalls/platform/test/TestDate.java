package com.javamalls.platform.test;

import java.util.Arrays;

public class TestDate {
    public static void main(String[] args) {
        String gsp = "";
        String[] gsp_ids = gsp.trim().split(",");
        System.out.println(gsp_ids.length);
        
        String[] strs={"10","2","1"};
        Arrays.sort(strs);
        for (String string : strs) {
            System.out.println(string);
        }
        
       System.out.println(strs.toString()); 
    }
}
