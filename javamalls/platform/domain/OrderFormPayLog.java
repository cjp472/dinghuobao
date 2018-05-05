package com.javamalls.platform.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_order_pay_log")
public class OrderFormPayLog extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User              user;                  //会员ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderForm         of;                    //订单ID
    private int               pay_child_class;       //支付子类: 1 银联在线,2支付宝,3 微信，4 银行卡（线下）,5 现金（线下）
    private BigDecimal        should_pay_amount;     //应付金额
    private BigDecimal        actual_pay_amount;     //实际支付金额
    private int               pay_status;            //支付状态,0未支付,1已支付2.已退款
    private String            bank_deal_num;         //银行交易号
    private String            bank_retmess;          //银行返回信息
    private Date              pay_time;              //支付时间

    /**
     * 会员ID
     * @return
     */
    public User getUser() {
        return user;
    }

    /**
     * 会员ID
     * @param user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * 订单ID
     * @return
     */
    public OrderForm getOf() {
        return of;
    }

    /**
     * 订单ID
     * @param of
     */
    public void setOf(OrderForm of) {
        this.of = of;
    }

    /**
     * 支付子类: 1 银联在线,2支付宝,3 微信，4 刷卡（线下）,5 现金（线下）
     * @return
     */
    public int getPay_child_class() {
        return pay_child_class;
    }

    /**
     * 支付子类: 1 银联在线,2支付宝,3 微信，4 刷卡（线下）,5 现金（线下）
     * @param pay_child_class
     */
    public void setPay_child_class(int pay_child_class) {
        this.pay_child_class = pay_child_class;
    }

    /**
     * 应付金额
     * @return
     */
    public BigDecimal getShould_pay_amount() {
        return should_pay_amount;
    }

    /**
     * 应付金额
     * @param should_pay_amount
     */
    public void setShould_pay_amount(BigDecimal should_pay_amount) {
        this.should_pay_amount = should_pay_amount;
    }

    /**
     * 实际支付金额
     * @return
     */
    public BigDecimal getActual_pay_amount() {
        return actual_pay_amount;
    }

    /**
     * 实际支付金额
     * @param actual_pay_amount
     */
    public void setActual_pay_amount(BigDecimal actual_pay_amount) {
        this.actual_pay_amount = actual_pay_amount;
    }

    /**
     * 支付状态,0未支付,1已支付2.已退款
     * @return
     */
    public int getPay_status() {
        return pay_status;
    }

    /**
     * 支付状态,0未支付,1已支付2.已退款
     * @param pay_status
     */
    public void setPay_status(int pay_status) {
        this.pay_status = pay_status;
    }

    /**
     * 银行交易号
     * @return
     */
    public String getBank_deal_num() {
        return bank_deal_num;
    }

    /**
     * 银行交易号
     * @param bank_deal_num
     */
    public void setBank_deal_num(String bank_deal_num) {
        this.bank_deal_num = bank_deal_num;
    }

    /**
     * 银行返回信息
     * @return
     */
    public String getBank_retmess() {
        return bank_retmess;
    }

    /**
     * 银行返回信息
     * @param bank_retmess
     */
    public void setBank_retmess(String bank_retmess) {
        this.bank_retmess = bank_retmess;
    }

    /**
     * 支付时间
     * @return
     */
    public Date getPay_time() {
        return pay_time;
    }

    /**
     * 支付时间
     * @param pay_time
     */
    public void setPay_time(Date pay_time) {
        this.pay_time = pay_time;
    }

}
