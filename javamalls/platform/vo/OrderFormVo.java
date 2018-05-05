package com.javamalls.platform.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.annotation.FormIgnore;
import com.javamalls.base.domain.CommonEntity;
import com.javamalls.platform.domain.GoodsCart;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.User;

/**订单接口vo
 * 
 *                       
 * @Filename: OrderForm.java
 * @Version: 2.7.0
 * @Author: 桑森林
 * @Email: sangslmail@163.com
 *
 */
public class OrderFormVo {

    private Long              id;
    private Date              createtime;
    private boolean           disabled;
    private String            order_id;
    private int               order_status;//0已取消;10待付款;15线下支付待审核;16货到付款待发货;20已付款;30已发货;40已收货;45买家申请退货;46退货中;47退货完成，已结束;48卖家拒绝退货;49退货失败;50已完成,已评价;60已结束;65已结束，不可评价
    private Long              user_id;
    private Long             store_id;
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
    public String getOrder_id() {
        return order_id;
    }
    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }
    public int getOrder_status() {
        return order_status;
    }
    public void setOrder_status(int order_status) {
        this.order_status = order_status;
    }
    public Long getUser_id() {
        return user_id;
    }
    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }
    public Long getStore_id() {
        return store_id;
    }
    public void setStore_id(Long store_id) {
        this.store_id = store_id;
    }
    

    
}
