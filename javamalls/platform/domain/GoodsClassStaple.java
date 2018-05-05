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
@Table(name = "jm_goodsclassstaple")
public class GoodsClassStaple extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    private String            name;
    @ManyToOne(fetch = FetchType.LAZY)
    private GoodsClass        gc;
    @ManyToOne(fetch = FetchType.LAZY)
    private Store             store;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GoodsClass getGc() {
        return this.gc;
    }

    public void setGc(GoodsClass gc) {
        this.gc = gc;
    }

    public Store getStore() {
        return this.store;
    }

    public void setStore(Store store) {
        this.store = store;
    }
}
