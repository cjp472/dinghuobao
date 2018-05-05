package com.javamalls.platform.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

/**
 * 用户收藏
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_collect")
public class Favorite extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    private int               type;//1，店铺收藏。0：商品收藏
    @ManyToOne(fetch = FetchType.LAZY)
    private Goods             goods;
    @ManyToOne(fetch = FetchType.LAZY)
    private Store             store;
    @ManyToOne(fetch = FetchType.LAZY)
    private User              user;
    /**
     * 1，店铺收藏。0：商品收藏
     * @return
     */
    public int getType() {
        return this.type;
    }
    /**
     * 1，店铺收藏。0：商品收藏
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }

    public Goods getGoods() {
        return this.goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public Store getStore() {
        return this.store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
