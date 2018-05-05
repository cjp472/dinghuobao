package com.javamalls.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lucene启动新的线程生成索引文件
 *                       
 * @Filename: LuceneThread.java
 * @Version: 1.0
 * @Author: 王朋
 * @Email: wpjava@163.com
 *
 */
public class LuceneThread implements Runnable {
    private String         path;
    private List<LuceneVo> vo_list = new ArrayList<LuceneVo>();

    public LuceneThread(String path, List<LuceneVo> vo_list) {
        this.path = path;
        this.vo_list = vo_list;
    }

    public void run() {
        LuceneUtil lucene = LuceneUtil.instance();
        LuceneUtil.setIndex_path(this.path);
        lucene.deleteAllIndex(true);
        try {
            lucene.writeIndex(this.vo_list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
