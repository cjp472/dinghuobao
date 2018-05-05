package com.javamalls.platform.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.javamalls.base.domain.CommonEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_goodsspecification")
public class GoodsSpecification extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long       serialVersionUID = 1L;
    private String                  name;
    private int                     sequence;
    private String                  type;
    @ManyToMany(mappedBy = "gss", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<GoodsType>         types            = new ArrayList<GoodsType>();
    @OneToMany(mappedBy = "spec", fetch = FetchType.LAZY)
    @OrderBy("sequence asc")
    private List<GoodsSpecProperty> properties       = new ArrayList<GoodsSpecProperty>();

    private Long                    store_id;

    public Long getStore_id() {
        return store_id;
    }

    public void setStore_id(Long store_id) {
        this.store_id = store_id;
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

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<GoodsType> getTypes() {
        return this.types;
    }

    public void setTypes(List<GoodsType> types) {
        this.types = types;
    }

    public List<GoodsSpecProperty> getProperties() {
        return this.properties;
    }

    public void setProperties(List<GoodsSpecProperty> properties) {
        this.properties = properties;
    }
}
