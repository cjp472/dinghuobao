package com.javamalls.platform.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_store_shopcart")
public class StoreCart extends CommonEntity {

    @ManyToOne
    private Store           store;
    @OneToMany(mappedBy = "sc")
    private List<GoodsCart> gcs = new ArrayList<GoodsCart>();
    private BigDecimal      total_price;
    @ManyToOne
    private User            user;
    private String          cart_session_id;
    @Column(columnDefinition = "int default 0")
    private int             sc_status;

    public int getSc_status() {
        return this.sc_status;
    }

    public void setSc_status(int sc_status) {
        this.sc_status = sc_status;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCart_session_id() {
        return this.cart_session_id;
    }

    public void setCart_session_id(String cart_session_id) {
        this.cart_session_id = cart_session_id;
    }

    public Store getStore() {
        return this.store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public List<GoodsCart> getGcs() {
        return this.gcs;
    }

    public void setGcs(List<GoodsCart> gcs) {
        this.gcs = gcs;
    }

    public BigDecimal getTotal_price() {
        return this.total_price;
    }

    public void setTotal_price(BigDecimal total_price) {
        this.total_price = total_price;
    }
}
