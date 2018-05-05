package com.javamalls.platform.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_predeposit_log")
public class PredepositLog extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    @ManyToOne(fetch = FetchType.LAZY)
    private User              pd_log_user;
    @Column(precision = 12, scale = 2)
    private BigDecimal        pd_log_amount;
    private String            pd_type;
    private String            pd_op_type;
    @ManyToOne(fetch = FetchType.LAZY)
    private User              pd_log_admin;
    @Column(columnDefinition = "LongText")
    private String            pd_log_info;
    @OneToOne(fetch = FetchType.EAGER)
    private Predeposit        predeposit;
    @OneToOne(fetch = FetchType.EAGER)
    private PredepositCash    predepositCash;

    public PredepositCash getPredepositCash() {
        return this.predepositCash;
    }

    public void setPredepositCash(PredepositCash predepositCash) {
        this.predepositCash = predepositCash;
    }

    public Predeposit getPredeposit() {
        return this.predeposit;
    }

    public void setPredeposit(Predeposit predeposit) {
        this.predeposit = predeposit;
    }

    public User getPd_log_user() {
        return this.pd_log_user;
    }

    public void setPd_log_user(User pd_log_user) {
        this.pd_log_user = pd_log_user;
    }

    public BigDecimal getPd_log_amount() {
        return this.pd_log_amount;
    }

    public void setPd_log_amount(BigDecimal pd_log_amount) {
        this.pd_log_amount = pd_log_amount;
    }

    public String getPd_type() {
        return this.pd_type;
    }

    public void setPd_type(String pd_type) {
        this.pd_type = pd_type;
    }

    public String getPd_op_type() {
        return this.pd_op_type;
    }

    public void setPd_op_type(String pd_op_type) {
        this.pd_op_type = pd_op_type;
    }

    public User getPd_log_admin() {
        return this.pd_log_admin;
    }

    public void setPd_log_admin(User pd_log_admin) {
        this.pd_log_admin = pd_log_admin;
    }

    public String getPd_log_info() {
        return this.pd_log_info;
    }

    public void setPd_log_info(String pd_log_info) {
        this.pd_log_info = pd_log_info;
    }
}
