package com.javamalls.platform.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_settle_log")
public class SettleLog extends CommonEntity implements java.io.Serializable {
    private static final long serialVersionUID = -9140283337196620264L;

    private String            code;
    private String            settle_explain;
    private String            sk_user;
    private String            hk_user;
    private String            hk_type;
    private String            remark;
    private BigDecimal        sale_account;
    private BigDecimal        sale_yongjin;
    private BigDecimal        settle_account;
    private Integer           status;
    private String            store_user_name;
    private Date              income_time;
    @ManyToOne(fetch = FetchType.LAZY)
    private User              user;
    @ManyToOne(fetch = FetchType.LAZY)
    private OrderForm         order;
    @ManyToOne(fetch = FetchType.LAZY)
    private Goods             goods;

    public SettleLog() {
    }

    public SettleLog(String code, String settle_explain, BigDecimal sale_account,
                     BigDecimal sale_yongjin, BigDecimal settle_account, Integer status,
                     String store_user_name, User user, OrderForm order, Date income_time,
                     Goods goods) {
        this.code = code;
        this.settle_explain = settle_explain;
        this.sale_account = sale_account;
        this.sale_yongjin = sale_yongjin;
        this.settle_account = settle_account;
        this.status = status;
        this.store_user_name = store_user_name;
        this.user = user;
        this.order = order;
        this.income_time = income_time;
        this.goods = goods;
        setCreatetime(new java.util.Date());
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setSettle_explain(String settle_explain) {
        this.settle_explain = settle_explain;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getSale_account() {
        return sale_account;
    }

    public void setSale_account(BigDecimal sale_account) {
        this.sale_account = sale_account;
    }

    public BigDecimal getSale_yongjin() {
        return sale_yongjin;
    }

    public void setSale_yongjin(BigDecimal sale_yongjin) {
        this.sale_yongjin = sale_yongjin;
    }

    public BigDecimal getSettle_account() {
        return settle_account;
    }

    public void setSettle_account(BigDecimal settle_account) {
        this.settle_account = settle_account;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public OrderForm getOrder() {
        return order;
    }

    public void setOrder(OrderForm order) {
        this.order = order;
    }

    public String getStore_user_name() {
        return store_user_name;
    }

    public void setStore_user_name(String store_user_name) {
        this.store_user_name = store_user_name;
    }

    public Date getIncome_time() {
        return income_time;
    }

    public void setIncome_time(Date income_time) {
        this.income_time = income_time;
    }

    public String getSettle_explain() {
        return settle_explain;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public String getSk_user() {
        return sk_user;
    }

    public void setSk_user(String sk_user) {
        this.sk_user = sk_user;
    }

    public String getHk_user() {
        return hk_user;
    }

    public void setHk_user(String hk_user) {
        this.hk_user = hk_user;
    }

    public String getHk_type() {
        return hk_type;
    }

    public void setHk_type(String hk_type) {
        this.hk_type = hk_type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
