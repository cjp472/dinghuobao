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
@Table(name = "jm_goodsretrieveproperty")
public class GoodsRetrieveProperty extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long  serialVersionUID = 1L;
    private int                sequence;
    @Column(columnDefinition = "LongText")
    private String             value;
    @ManyToOne(fetch = FetchType.LAZY)
    private GoodsRetrieve retrieve;
    @ManyToOne(fetch = FetchType.LAZY)
    private Store              store;
    public int getSequence() {
        return sequence;
    }
    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public GoodsRetrieve getRetrieve() {
        return retrieve;
    }
    public void setRetrieve(GoodsRetrieve retrieve) {
        this.retrieve = retrieve;
    }
    public Store getStore() {
        return store;
    }
    public void setStore(Store store) {
        this.store = store;
    }
    
   
}
