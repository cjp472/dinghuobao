package com.javamalls.base.domain.virtual;

import java.util.Date;

/**
 *                       
 * @Filename: ShopData.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
public class ShopData {
    private String name;
    private String phyPath;
    private double size;
    private int    boundSize;
    private Date   createtime;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhyPath() {
        return this.phyPath;
    }

    public void setPhyPath(String phyPath) {
        this.phyPath = phyPath;
    }

    public double getSize() {
        return this.size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public Date getCreatetime() {
        return this.createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public int getBoundSize() {
        return this.boundSize;
    }

    public void setBoundSize(int boundSize) {
        this.boundSize = boundSize;
    }
}
