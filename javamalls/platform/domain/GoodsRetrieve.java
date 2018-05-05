package com.javamalls.platform.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

//检索属性表
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_goodsretrieve")
public class GoodsRetrieve extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long           serialVersionUID           = 1L;
    private String                      name;
    private int                         sequence;
    private int                         type;
    /*@ManyToOne(fetch = FetchType.LAZY)
    private User              user;*/
    @OneToMany(mappedBy = "retrieve")
    private List<GoodsRetrieveProperty> goodsRetrievePropertieList = new ArrayList<GoodsRetrieveProperty>();

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<GoodsRetrieveProperty> getGoodsRetrievePropertieList() {
        return goodsRetrievePropertieList;
    }

    public void setGoodsRetrievePropertieList(List<GoodsRetrieveProperty> goodsRetrievePropertieList) {
        this.goodsRetrievePropertieList = goodsRetrievePropertieList;
    }

}
