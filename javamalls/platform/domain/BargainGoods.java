package com.javamalls.platform.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

/**折扣商品
 *                       
 * @Filename: BargainGoods.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_discount_goods")
public class BargainGoods extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    @ManyToOne(fetch = FetchType.LAZY)
    private Goods             bg_goods;
    private int               bg_status;
    @Column(precision = 3, scale = 1)
    private BigDecimal        bg_rebate;
    @Column(columnDefinition = "int default 1")
    private int               bg_count;
    @ManyToOne(fetch = FetchType.LAZY)
    private User              bg_admin_user;
    @Column(precision = 12, scale = 2)
    private BigDecimal        bg_price;
    @Temporal(TemporalType.DATE)
    private Date              bg_time;
    private Date              audit_time;

    public Date getAudit_time() {
        return this.audit_time;
    }

    public void setAudit_time(Date audit_time) {
        this.audit_time = audit_time;
    }

    public Date getBg_time() {
        return this.bg_time;
    }

    public void setBg_time(Date bg_time) {
        this.bg_time = bg_time;
    }

    public Goods getBg_goods() {
        return this.bg_goods;
    }

    public void setBg_goods(Goods bg_goods) {
        this.bg_goods = bg_goods;
    }

    public int getBg_status() {
        return this.bg_status;
    }

    public void setBg_status(int bg_status) {
        this.bg_status = bg_status;
    }

    public User getBg_admin_user() {
        return this.bg_admin_user;
    }

    public void setBg_admin_user(User bg_admin_user) {
        this.bg_admin_user = bg_admin_user;
    }

    public BigDecimal getBg_price() {
        return this.bg_price;
    }

    public void setBg_price(BigDecimal bg_price) {
        this.bg_price = bg_price;
    }

    public BigDecimal getBg_rebate() {
        return this.bg_rebate;
    }

    public void setBg_rebate(BigDecimal bg_rebate) {
        this.bg_rebate = bg_rebate;
    }

    public int getBg_count() {
        return this.bg_count;
    }

    public void setBg_count(int bg_count) {
        this.bg_count = bg_count;
    }
}
