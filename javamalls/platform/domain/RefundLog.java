package com.javamalls.platform.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_refund_log")
public class RefundLog extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    private String            refund_id;
    @ManyToOne(fetch = FetchType.LAZY)
    private OrderForm         of;
    private String            refund_log;
    private String            refund_type;
    @Column(precision = 12, scale = 2)
    private BigDecimal        refund;
    @ManyToOne(fetch = FetchType.LAZY)
    private User              refund_user;

    @Column(precision = 12, scale = 2)
    private BigDecimal        refund_freight;       //退款扣减运费金额

    @Lob
    @Column(columnDefinition = "LongText")
    public OrderForm getOf() {
        return this.of;
    }

    public void setOf(OrderForm of) {
        this.of = of;
    }

    public String getRefund_log() {
        return this.refund_log;
    }

    public void setRefund_log(String refund_log) {
        this.refund_log = refund_log;
    }

    public BigDecimal getRefund() {
        return this.refund;
    }

    public void setRefund(BigDecimal refund) {
        this.refund = refund;
    }

    public User getRefund_user() {
        return this.refund_user;
    }

    public void setRefund_user(User refund_user) {
        this.refund_user = refund_user;
    }

    public String getRefund_type() {
        return this.refund_type;
    }

    public void setRefund_type(String refund_type) {
        this.refund_type = refund_type;
    }

    public String getRefund_id() {
        return this.refund_id;
    }

    public void setRefund_id(String refund_id) {
        this.refund_id = refund_id;
    }

    public BigDecimal getRefund_freight() {
        return refund_freight;
    }

    public void setRefund_freight(BigDecimal refund_freight) {
        this.refund_freight = refund_freight;
    }

}
