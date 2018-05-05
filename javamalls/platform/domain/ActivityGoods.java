package com.javamalls.platform.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_act_goods")
public class ActivityGoods extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    @ManyToOne(fetch = FetchType.LAZY)
    private Goods             ag_goods;
    private int               ag_status;
    @ManyToOne(fetch = FetchType.LAZY)
    private User              ag_admin;
    @ManyToOne(fetch = FetchType.EAGER)
    private Activity          act;
    @Column(precision = 12, scale = 2)
    private BigDecimal        ag_price;

    public Activity getAct() {
        return this.act;
    }

    public void setAct(Activity act) {
        this.act = act;
    }

    public Goods getAg_goods() {
        return this.ag_goods;
    }

    public void setAg_goods(Goods ag_goods) {
        this.ag_goods = ag_goods;
    }

    public int getAg_status() {
        return this.ag_status;
    }

    public void setAg_status(int ag_status) {
        this.ag_status = ag_status;
    }

    public User getAg_admin() {
        return this.ag_admin;
    }

    public void setAg_admin(User ag_admin) {
        this.ag_admin = ag_admin;
    }

    public BigDecimal getAg_price() {
        return this.ag_price;
    }

    public void setAg_price(BigDecimal ag_price) {
        this.ag_price = ag_price;
    }
}
