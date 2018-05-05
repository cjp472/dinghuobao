package com.javamalls.platform.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_goods_backitem")
public class GoodsReturnItem extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long       serialVersionUID = 1L;
    @ManyToOne(fetch = FetchType.LAZY)
    private Goods                   goods;
    @ManyToOne(fetch = FetchType.LAZY)
    private GoodsReturn             gr;
    @ManyToMany(cascade = { javax.persistence.CascadeType.ALL })
    @JoinTable(name = "jm_back_gsp", joinColumns = { @javax.persistence.JoinColumn(name = "item_id") }, inverseJoinColumns = { @javax.persistence.JoinColumn(name = "gsp_id") })
    private List<GoodsSpecProperty> gsps             = new ArrayList<GoodsSpecProperty>();
    @Lob
    @Column(columnDefinition = "LongText")
    private String                  spec_info;
    private int                     count;

    public Goods getGoods() {
        return this.goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public GoodsReturn getGr() {
        return this.gr;
    }

    public void setGr(GoodsReturn gr) {
        this.gr = gr;
    }

    public List<GoodsSpecProperty> getGsps() {
        return this.gsps;
    }

    public void setGsps(List<GoodsSpecProperty> gsps) {
        this.gsps = gsps;
    }

    public String getSpec_info() {
        return this.spec_info;
    }

    public void setSpec_info(String spec_info) {
        this.spec_info = spec_info;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
