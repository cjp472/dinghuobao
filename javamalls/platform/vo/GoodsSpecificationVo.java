package com.javamalls.platform.vo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.javamalls.base.domain.CommonEntity;
import com.javamalls.platform.domain.GoodsSpecProperty;

public class GoodsSpecificationVo extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long       serialVersionUID = 1L;
    private String                  name;
    private int                     sequence;
    private String                  type;
    private List<GoodsSpecProperty> properties ;
    
    private Long store_id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<GoodsSpecProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<GoodsSpecProperty> properties) {
        this.properties = properties;
    }

    public Long getStore_id() {
        return store_id;
    }

    public void setStore_id(Long store_id) {
        this.store_id = store_id;
    }
    
  
}
