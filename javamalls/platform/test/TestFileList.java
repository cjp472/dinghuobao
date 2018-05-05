package com.javamalls.platform.test;

import java.io.File;

public class TestFileList {
    public static void main(String[] args) {
        String strPath = "F:\\JAVA_PRO\\javamalls\\data\\20120829_1";
        File dir = new File(strPath);
        File[] files = dir.listFiles();
        for (File f : files) {
            System.out.println(f.getName());
        }
    }
}
