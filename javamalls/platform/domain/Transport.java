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
@Table(name = "jm_transport")
public class Transport extends CommonEntity {
    private String  trans_name;
    @Column(columnDefinition = "int default 0")
    private int     trans_time;
    @Column(columnDefinition = "int default 0")
    private int     trans_type;
    @ManyToOne(fetch = FetchType.LAZY)
    private Store   store;
    private boolean trans_mail;
    @Column(columnDefinition = "LongText")
    private String  trans_mail_info;
    private boolean trans_express;
    @Column(columnDefinition = "LongText")
    private String  trans_express_info;
    private boolean trans_ems;
    @Column(columnDefinition = "LongText")
    private String  trans_ems_info;

    public int getTrans_time() {
        return this.trans_time;
    }

    public void setTrans_time(int trans_time) {
        this.trans_time = trans_time;
    }

    public int getTrans_type() {
        return this.trans_type;
    }

    public void setTrans_type(int trans_type) {
        this.trans_type = trans_type;
    }

    public boolean isTrans_mail() {
        return this.trans_mail;
    }

    public void setTrans_mail(boolean trans_mail) {
        this.trans_mail = trans_mail;
    }

    public boolean isTrans_express() {
        return this.trans_express;
    }

    public void setTrans_express(boolean trans_express) {
        this.trans_express = trans_express;
    }

    public boolean isTrans_ems() {
        return this.trans_ems;
    }

    public void setTrans_ems(boolean trans_ems) {
        this.trans_ems = trans_ems;
    }

    public String getTrans_name() {
        return this.trans_name;
    }

    public void setTrans_name(String trans_name) {
        this.trans_name = trans_name;
    }

    public String getTrans_mail_info() {
        return this.trans_mail_info;
    }

    public void setTrans_mail_info(String trans_mail_info) {
        this.trans_mail_info = trans_mail_info;
    }

    public String getTrans_express_info() {
        return this.trans_express_info;
    }

    public void setTrans_express_info(String trans_express_info) {
        this.trans_express_info = trans_express_info;
    }

    public String getTrans_ems_info() {
        return this.trans_ems_info;
    }

    public void setTrans_ems_info(String trans_ems_info) {
        this.trans_ems_info = trans_ems_info;
    }

    public Store getStore() {
        return this.store;
    }

    public void setStore(Store store) {
        this.store = store;
    }
}
