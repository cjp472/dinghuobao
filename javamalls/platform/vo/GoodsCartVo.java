package com.javamalls.platform.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.javamalls.base.annotation.FormIgnore;
import com.javamalls.base.domain.CommonEntity;

/**购物车接口vo
 * 
 *                       
 * @Filename: GoodsCart.java
 * @Version: 2.7.0
 * @Author: 桑森林
 * @Email: sangslmail@163.com
 *
 */

public class GoodsCartVo{

    private Long              id;
    private Date              createtime;
    private boolean           disabled;
    private int                     count;
    private BigDecimal              price;
    private String                  spec_id;
    private String                  spec_info;
    private Long sc_id;
    private Long goods_id;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Date getCreatetime() {
        return createtime;
    }
    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }
    public boolean isDisabled() {
        return disabled;
    }
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public String getSpec_id() {
        return spec_id;
    }
    public void setSpec_id(String spec_id) {
        this.spec_id = spec_id;
    }
    public String getSpec_info() {
        return spec_info;
    }
    public void setSpec_info(String spec_info) {
        this.spec_info = spec_info;
    }
    public Long getSc_id() {
        return sc_id;
    }
    public void setSc_id(Long sc_id) {
        this.sc_id = sc_id;
    }
    public Long getGoods_id() {
        return goods_id;
    }
    public void setGoods_id(Long goods_id) {
        this.goods_id = goods_id;
    }
    
    
    

}
