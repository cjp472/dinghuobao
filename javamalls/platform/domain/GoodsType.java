package com.javamalls.platform.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.javamalls.base.domain.CommonEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_goods_type")
public class GoodsType extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long        serialVersionUID = 1L;
    private String                   name;
    private int                      sequence;
    @ManyToMany
    @JoinTable(name = "jm_goodstype_spec", joinColumns = { @javax.persistence.JoinColumn(name = "type_id") }, inverseJoinColumns = { @javax.persistence.JoinColumn(name = "spec_id") })
    private List<GoodsSpecification> gss              = new ArrayList<GoodsSpecification>();
    @ManyToMany
    @JoinTable(name = "jm_goodstype_brand", joinColumns = { @javax.persistence.JoinColumn(name = "type_id") }, inverseJoinColumns = { @javax.persistence.JoinColumn(name = "brand_id") })
    @JsonIgnore
    private List<GoodsBrand>         gbs              = new ArrayList<GoodsBrand>();
    @OneToMany(mappedBy = "goodsType", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<GoodsTypeProperty>  properties       = new ArrayList<GoodsTypeProperty>();
    @OneToMany(mappedBy = "goodsType")
    @JsonIgnore
    private List<GoodsClass>         gcs              = new ArrayList<GoodsClass>();

    private Long store_id;//店铺Id
    
    
    
    public Long getStore_id() {
		return store_id;
	}

	public void setStore_id(Long store_id) {
		this.store_id = store_id;
	}

	public List<GoodsClass> getGcs() {
        return this.gcs;
    }

    public void setGcs(List<GoodsClass> gcs) {
        this.gcs = gcs;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSequence() {
        return this.sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public List<GoodsSpecification> getGss() {
        return this.gss;
    }

    public void setGss(List<GoodsSpecification> gss) {
        this.gss = gss;
    }

    public List<GoodsBrand> getGbs() {
        return this.gbs;
    }

    public void setGbs(List<GoodsBrand> gbs) {
        this.gbs = gbs;
    }

    public List<GoodsTypeProperty> getProperties() {
        return this.properties;
    }

    public void setProperties(List<GoodsTypeProperty> properties) {
        this.properties = properties;
    }
}
