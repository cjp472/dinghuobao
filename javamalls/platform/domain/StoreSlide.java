package com.javamalls.platform.domain;

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
@Table(name = "jm_store_slide")
public class StoreSlide extends CommonEntity {
    private String    url;
    @OneToOne(fetch = FetchType.LAZY)
    private Accessory acc;
    @ManyToOne(fetch = FetchType.LAZY)
    private Store     store;
    
    private Integer type;//0:pc端轮播图，1移动端轮播图
    
    
    
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Store getStore() {
        return this.store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Accessory getAcc() {
        return this.acc;
    }

    public void setAcc(Accessory acc) {
        this.acc = acc;
    }
}
