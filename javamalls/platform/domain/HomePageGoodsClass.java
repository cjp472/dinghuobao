package com.javamalls.platform.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

/**
 *                       
 * @Filename: HomePageGoodsClass.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_homepage_goodsclass")
public class HomePageGoodsClass extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    @ManyToOne(fetch = FetchType.LAZY)
    private User              user;
    @ManyToOne(fetch = FetchType.LAZY)
    private GoodsClass        gc;

    public GoodsClass getGc() {
        return this.gc;
    }

    public void setGc(GoodsClass gc) {
        this.gc = gc;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
