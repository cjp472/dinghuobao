package com.javamalls.platform.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_coin_ztc_log")
public class ZTCGoldLog extends CommonEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Goods  zgl_goods;
    private int    zgl_gold;
    private int    zgl_type;
    private String zgl_content;

    public Goods getZgl_goods() {
        return this.zgl_goods;
    }

    public void setZgl_goods(Goods zgl_goods) {
        this.zgl_goods = zgl_goods;
    }

    public int getZgl_gold() {
        return this.zgl_gold;
    }

    public void setZgl_gold(int zgl_gold) {
        this.zgl_gold = zgl_gold;
    }

    public int getZgl_type() {
        return this.zgl_type;
    }

    public void setZgl_type(int zgl_type) {
        this.zgl_type = zgl_type;
    }

    public String getZgl_content() {
        return this.zgl_content;
    }

    public void setZgl_content(String zgl_content) {
        this.zgl_content = zgl_content;
    }
}
