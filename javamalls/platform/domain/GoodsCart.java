package com.javamalls.platform.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.javamalls.base.domain.CommonEntity;

/**购物车
 *                       
 * @Filename: GoodsCart.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_goods_shopcart")
public class GoodsCart extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long       serialVersionUID = 1L;
    @OneToOne
    @JsonIgnore
    private Goods                   goods;
    private int                     count;
    @Column(precision = 12, scale = 2)
    private BigDecimal              price;
    @ManyToMany
    @JoinTable(name = "jm_shopcart_gsp", joinColumns = { @javax.persistence.JoinColumn(name = "cart_id") }, inverseJoinColumns = { @javax.persistence.JoinColumn(name = "gsp_id") })
    @JsonIgnore
    private List<GoodsSpecProperty> gsps             = new ArrayList<GoodsSpecProperty>();
    @Column(name = "spec_id")
    private String                  spec_id;
    @Lob
    @Column(columnDefinition = "LongText")
    private String                  spec_info;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private OrderForm               of;
    private String                  cart_type;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private StoreCart               sc;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Goods                   d_delivery_goods;                                     //保存赠送的商品

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_item_id")
    private GoodsItem               goodsItem;                                             //货品

    public StoreCart getSc() {
        return this.sc;
    }

    public void setSc(StoreCart sc) {
        this.sc = sc;
    }

    public Goods getGoods() {
        return this.goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
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

    public OrderForm getOf() {
        return this.of;
    }

    public void setOf(OrderForm of) {
        this.of = of;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCart_type() {
        return this.cart_type;
    }

    public void setCart_type(String cart_type) {
        this.cart_type = cart_type;
    }

    public Goods getD_delivery_goods() {
        return d_delivery_goods;
    }

    public void setD_delivery_goods(Goods d_delivery_goods) {
        this.d_delivery_goods = d_delivery_goods;
    }

    public String getSpec_id() {
        return spec_id;
    }

    public void setSpec_id(String spec_id) {
        this.spec_id = spec_id;
    }

    public GoodsItem getGoodsItem() {
        return goodsItem;
    }

    public void setGoodsItem(GoodsItem goodsItem) {
        this.goodsItem = goodsItem;
    }
    
    

}
