package com.javamalls.platform.domain;

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
@Table(name = "jm_coupon_info")
public class CouponInfo extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    private String            coupon_sn;//优惠券编码
    @ManyToOne(fetch = FetchType.LAZY)
    private User              user;
    @ManyToOne(fetch = FetchType.LAZY)
    private Coupon            coupon;//优惠券id
    @Column(columnDefinition = "int default 0")
    private int               status;//优惠券使用状态  0：未使用 1：已使用
    /**
     * 优惠券使用状态  0：未使用 1：已使用
     * @return
     */
    public int getStatus() {
        return this.status;
    }
    /**
     * 优惠券使用状态  0：未使用 1：已使用
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }
    /**
     * 优惠券编码
     * @return
     */
    public String getCoupon_sn() {
        return this.coupon_sn;
    }
    /**
     * 优惠券编码
     * @param coupon_sn
     */
    public void setCoupon_sn(String coupon_sn) {
        this.coupon_sn = coupon_sn;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    /**
     * 优惠券id
     * @return
     */
    public Coupon getCoupon() {
        return this.coupon;
    }
    /**
     * 优惠券id
     * @param coupon
     */
    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }
}
