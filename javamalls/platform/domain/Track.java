package com.javamalls.platform.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.javamalls.base.domain.CommonEntity;

/**
 * 浏览足迹
 * 
 * @author zhaihl
 */
@Entity
@Table(name = "jm_track")
public class Track extends CommonEntity implements java.io.Serializable {
    private static final long serialVersionUID = 3948118814597241731L;

    @Transient
    private Long              userid;

    @Transient
    private Long              goodsid;

    private Long              ac_cnt;                                  //浏览次数

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid")
    private User              user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goodsid")
    private Goods             goods;

    public Track(Goods goods, User user) {
        this.user = user;
        this.goods = goods;
        setCreatetime(new Date());
    }

    public Track() {
        super();
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public Long getGoodsid() {
        return goodsid;
    }

    public void setGoodsid(Long goodsid) {
        this.goodsid = goodsid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public Long getAc_cnt() {
        return ac_cnt;
    }

    public void setAc_cnt(Long ac_cnt) {
        this.ac_cnt = ac_cnt;
    }

}
