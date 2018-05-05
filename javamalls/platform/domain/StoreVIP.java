package com.javamalls.platform.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.javamalls.base.domain.CommonEntity;

/**
 * 店铺会员
 * 
 * @author zhaihl
 */
@Entity
@Table(name = "jm_store_vip")
public class StoreVIP extends CommonEntity {
    private static final long serialVersionUID = 1L;

    @Transient
    private Long              storeid;
    @Transient
    private Long              userid;

    private BigDecimal        deal_price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store             store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User              user;

    public StoreVIP() {
    }

    public StoreVIP(BigDecimal deal_price, Store store, User user) {
        this.deal_price = deal_price;
        this.store = store;
        this.user = user;
        setCreatetime(new Date());
    }

    // /////////////////getter and setter///////////////////////

    public Long getStoreid() {
        return storeid;
    }

    public void setStoreid(Long storeid) {
        this.storeid = storeid;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public BigDecimal getDeal_price() {
        return deal_price;
    }

    public void setDeal_price(BigDecimal deal_price) {
        this.deal_price = deal_price;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
