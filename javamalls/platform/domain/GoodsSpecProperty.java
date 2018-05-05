package com.javamalls.platform.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.javamalls.base.domain.CommonEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_goodsspecproperty")
public class GoodsSpecProperty extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long  serialVersionUID = 1L;
    private int                sequence;
    @Column(columnDefinition = "LongText")
    private String             value;
    @OneToOne(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
    private Accessory          specImage;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private GoodsSpecification spec;

    public int getSequence() {
        return this.sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Accessory getSpecImage() {
        return this.specImage;
    }

    public void setSpecImage(Accessory specImage) {
        this.specImage = specImage;
    }

    public GoodsSpecification getSpec() {
        return this.spec;
    }

    public void setSpec(GoodsSpecification spec) {
        this.spec = spec;
    }

    
}
