package com.javamalls.platform.domain;

import java.util.Date;

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
@Table(name = "jm_coin_record")
public class GoldRecord extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    private String            gold_sn;
    @ManyToOne(fetch = FetchType.LAZY)
    private User              gold_user;
    private String            gold_payment;
    private int               gold_money;
    private int               gold_count;
    @Column(columnDefinition = "LongText")
    private String            gold_exchange_info;
    @ManyToOne(fetch = FetchType.LAZY)
    private User              gold_admin;
    @Column(columnDefinition = "LongText")
    private String            gold_admin_info;
    private Date              gold_admin_time;
    private int               gold_status;
    private int               gold_pay_status;
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "gr")
    private GoldLog           log;

    public String getGold_sn() {
        return this.gold_sn;
    }

    public void setGold_sn(String gold_sn) {
        this.gold_sn = gold_sn;
    }

    public User getGold_user() {
        return this.gold_user;
    }

    public void setGold_user(User gold_user) {
        this.gold_user = gold_user;
    }

    public String getGold_payment() {
        return this.gold_payment;
    }

    public void setGold_payment(String gold_payment) {
        this.gold_payment = gold_payment;
    }

    public int getGold_money() {
        return this.gold_money;
    }

    public void setGold_money(int gold_money) {
        this.gold_money = gold_money;
    }

    public String getGold_exchange_info() {
        return this.gold_exchange_info;
    }

    public void setGold_exchange_info(String gold_exchange_info) {
        this.gold_exchange_info = gold_exchange_info;
    }

    public User getGold_admin() {
        return this.gold_admin;
    }

    public void setGold_admin(User gold_admin) {
        this.gold_admin = gold_admin;
    }

    public String getGold_admin_info() {
        return this.gold_admin_info;
    }

    public void setGold_admin_info(String gold_admin_info) {
        this.gold_admin_info = gold_admin_info;
    }

    public Date getGold_admin_time() {
        return this.gold_admin_time;
    }

    public void setGold_admin_time(Date gold_admin_time) {
        this.gold_admin_time = gold_admin_time;
    }

    public int getGold_status() {
        return this.gold_status;
    }

    public void setGold_status(int gold_status) {
        this.gold_status = gold_status;
    }

    public int getGold_pay_status() {
        return this.gold_pay_status;
    }

    public void setGold_pay_status(int gold_pay_status) {
        this.gold_pay_status = gold_pay_status;
    }

    public int getGold_count() {
        return this.gold_count;
    }

    public void setGold_count(int gold_count) {
        this.gold_count = gold_count;
    }

    public GoldLog getLog() {
        return this.log;
    }

    public void setLog(GoldLog log) {
        this.log = log;
    }
}
