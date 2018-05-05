package com.javamalls.platform.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.javamalls.base.domain.CommonEntity;

/**商品品牌
 *                       
 * @Filename: GoodsBrand.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_goods_brand")
public class GoodsBrand extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long  serialVersionUID = 1L;
    private String             name;
    private int                sequence;
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Accessory          brandLogo;

    private boolean            recommend;
    @Column(columnDefinition = "int default 0")
    private int                audit;
    @Column(columnDefinition = "int default 0")
    private int                userStatus;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User               user;
    @Column(columnDefinition = "LongText")
    private String             remark;
    @ManyToMany(mappedBy = "gbs")
    private List<GoodsType>    types            = new ArrayList<GoodsType>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private GoodsBrandCategory category;
    @OneToMany(mappedBy = "goods_brand")
    @JsonIgnore
    private List<Goods>        goods_list       = new ArrayList<Goods>();

    private String             first_word;

    @Column(columnDefinition = "int default 0")
    private Integer            btype;
    @ManyToOne(fetch = FetchType.LAZY)
    private Store store;

    public String getFirst_word() {
        return this.first_word;
    }

    public void setFirst_word(String first_word) {
        this.first_word = first_word;
    }

  

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getUserStatus() {
        return this.userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Goods> getGoods_list() {
        return this.goods_list;
    }

    public void setGoods_list(List<Goods> goods_list) {
        this.goods_list = goods_list;
    }

    public GoodsBrandCategory getCategory() {
        return this.category;
    }

    public void setCategory(GoodsBrandCategory category) {
        this.category = category;
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

    public Accessory getBrandLogo() {
        return this.brandLogo;
    }

    public void setBrandLogo(Accessory brandLogo) {
        this.brandLogo = brandLogo;
    }

    public boolean isRecommend() {
        return this.recommend;
    }

    public void setRecommend(boolean recommend) {
        this.recommend = recommend;
    }

    public int getAudit() {
        return this.audit;
    }

    public void setAudit(int audit) {
        this.audit = audit;
    }

    public List<GoodsType> getTypes() {
        return this.types;
    }

    public void setTypes(List<GoodsType> types) {
        this.types = types;
    }

    public Integer getBtype() {
        return btype;
    }

    public void setBtype(Integer btype) {
        this.btype = btype;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

}
