package com.javamalls.platform.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_discount")
public class Bargain extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    @Temporal(TemporalType.DATE)
    private Date              bargain_time;
    @Column(precision = 3, scale = 2)
    private BigDecimal        rebate;
    @Column(columnDefinition = "int default 0")
    private int               maximum;
    @Column(columnDefinition = "LongText")
    private String            state;

    public Date getBargain_time() {
        return this.bargain_time;
    }

    public void setBargain_time(Date bargain_time) {
        this.bargain_time = bargain_time;
    }

    public BigDecimal getRebate() {
        return this.rebate;
    }

    public void setRebate(BigDecimal rebate) {
        this.rebate = rebate;
    }

    public int getMaximum() {
        return this.maximum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
