package com.javamalls.platform.domain;

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

/**订单
 *                       
 * @Filename: OrderForm.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_order")
public class OrderForm extends CommonEntity {
    private static final long          serialVersionUID         = 728588118388285624L;

    private String                     order_id;//订单编号
    private String                     out_order_id;//交易流水号
    private String                     order_type;//订单类型weixin：微信订单、android：Android订单、 underline：线下订单 、mobile：移动端订单、web、电脑端订单 、outline:实体店订单
    @OneToMany(mappedBy = "of", fetch = FetchType.LAZY)
    List<GoodsCart>                    gcs                      = new ArrayList<GoodsCart>();
    @Column(precision = 12, scale = 2)
    private BigDecimal                 totalPrice;//订单总额
    @Column(precision = 12, scale = 2)
    private BigDecimal                 goods_amount;//商品金额
    @Lob
    @Column(columnDefinition = "LongText")
    private String                     msg;                                                             //订单备注
    @ManyToOne(fetch = FetchType.EAGER)
    private Payment                    payment;//支付方式
    private String                     transport;//邮寄方式
    private String                     shipCode;//物流单号
    private String                     return_shipCode;//退货编号
    private Date                       return_shipTime;//退货时间
    @Lob
    @Column(columnDefinition = "LongText")
    private String                     return_content;//退货描述
    @ManyToOne(fetch = FetchType.LAZY)
    private ExpressCompany             ec;//优惠卷
    @ManyToOne(fetch = FetchType.LAZY)
    private ExpressCompany             return_ec;//退货优惠卷
    @Column(precision = 12, scale = 2)
    private BigDecimal                 ship_price;
    @FormIgnore
    private int                        order_status;                                                    //0已取消;10待付款;15线下支付待审核;16货到付款待发货;20已付款;25买家申请取消订单;30已发货;40已收货;45买家申请退货;46退货中;47退货完成，已结束;48卖家拒绝退货;49退货失败;50已完成,已评价;60已结束;65已结束，不可评价
    @ManyToOne(fetch = FetchType.EAGER)
    private User                       user;
    @ManyToOne(fetch = FetchType.EAGER)
    private Store                      store;
    private Date                       payTime;//支付时间
    private Date                       shipTime;//发快递时间
    private Date                       finishTime;
    @ManyToOne(fetch = FetchType.LAZY)
    private Address                    addr;//收货地址
    private int                        invoiceType;//发票类型
    private String                     invoice;//  发票抬头

    private String            clerkCode;//营业员编号
    private Date                       delivery_date;                                                   //交货日期
    @OneToMany(mappedBy = "orderForm", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
    private List<OrderFormFile>        orderFilelist            = new ArrayList<OrderFormFile>();

    @OneToMany(mappedBy = "of", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
    private List<OrderFormCancelAudit> orderFormCancelAuditList = new ArrayList<OrderFormCancelAudit>(); //买家取消订单申请列表

    @Column(precision = 12, scale = 2)
    private BigDecimal                 refund_freight;                                                  //退款扣减运费金额
    private Integer                    refund_mold;                                                     //退款类型，0：全额退款，1：扣减运费

    /**
     * 买家取消订单申请列表
     * @return
     */
    public List<OrderFormCancelAudit> getOrderFormCancelAuditList() {
        return orderFormCancelAuditList;
    }

    /**
     * 买家取消订单申请列表
     * @param orderFormCancelAuditList
     */
    public void setOrderFormCancelAuditList(List<OrderFormCancelAudit> orderFormCancelAuditList) {
        this.orderFormCancelAuditList = orderFormCancelAuditList;
    }

    public List<OrderFormFile> getOrderFilelist() {
        return orderFilelist;
    }

    public void setOrderFilelist(List<OrderFormFile> orderFilelist) {
        this.orderFilelist = orderFilelist;
    }

    public Date getDelivery_date() {
        return delivery_date;
    }

    public void setDelivery_date(Date delivery_date) {
        this.delivery_date = delivery_date;
    }

    public String getOrder_type() {
        return this.order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }

    @OneToMany(mappedBy = "of", cascade = { javax.persistence.CascadeType.REMOVE })
    private List<OrderFormLog>   ofls       = new ArrayList<OrderFormLog>();
    @OneToMany(mappedBy = "of", cascade = { javax.persistence.CascadeType.REMOVE })
    private List<RefundLog>      rls        = new ArrayList<RefundLog>();
    @Lob
    @Column(columnDefinition = "LongText")
    private String               pay_msg;
    @Column(precision = 12, scale = 2)
    private BigDecimal           refund; //退款金额
    private String               refund_type;//退款方式（如：线下支付）
    @Column(columnDefinition = "bit default 0")
    private boolean              auto_confirm_email;
    @Column(columnDefinition = "bit default 0")
    private boolean              auto_confirm_sms;
    @OneToMany(mappedBy = "of", cascade = { javax.persistence.CascadeType.REMOVE })
    private List<GoodsReturnLog> grls       = new ArrayList<GoodsReturnLog>();
    @OneToMany(mappedBy = "of", cascade = { javax.persistence.CascadeType.REMOVE })
    private List<Evaluate>       evas       = new ArrayList<Evaluate>();
    @OneToMany(mappedBy = "of", cascade = { javax.persistence.CascadeType.REMOVE })
    private List<Complaint>      complaints = new ArrayList<Complaint>();
    @OneToOne(fetch = FetchType.LAZY)
    private CouponInfo           ci;
    @Column(columnDefinition = "LongText")
    private String               order_seller_intro;

    /** 退货理由,对应State.REFUND_REASON的状态 */
    private Integer              refund_reason;

    public String getReturn_content() {
        return this.return_content;
    }

    public void setReturn_content(String return_content) {
        this.return_content = return_content;
    }

    public Date getReturn_shipTime() {
        return this.return_shipTime;
    }

    public void setReturn_shipTime(Date return_shipTime) {
        this.return_shipTime = return_shipTime;
    }

    public String getReturn_shipCode() {
        return this.return_shipCode;
    }

    public void setReturn_shipCode(String return_shipCode) {
        this.return_shipCode = return_shipCode;
    }

    public ExpressCompany getReturn_ec() {
        return this.return_ec;
    }

    public void setReturn_ec(ExpressCompany return_ec) {
        this.return_ec = return_ec;
    }

    public CouponInfo getCi() {
        return this.ci;
    }

    public void setCi(CouponInfo ci) {
        this.ci = ci;
    }

    public List<Complaint> getComplaints() {
        return this.complaints;
    }

    public void setComplaints(List<Complaint> complaints) {
        this.complaints = complaints;
    }

    public List<Evaluate> getEvas() {
        return this.evas;
    }

    public void setEvas(List<Evaluate> evas) {
        this.evas = evas;
    }

    public List<GoodsReturnLog> getGrls() {
        return this.grls;
    }

    public void setGrls(List<GoodsReturnLog> grls) {
        this.grls = grls;
    }

    public BigDecimal getRefund() {
        return this.refund;
    }

    public void setRefund(BigDecimal refund) {
        this.refund = refund;
    }

    public String getRefund_type() {
        return this.refund_type;
    }

    public void setRefund_type(String refund_type) {
        this.refund_type = refund_type;
    }

    public String getOrder_id() {
        return this.order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public BigDecimal getTotalPrice() {
        return this.totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getShip_price() {
        return this.ship_price;
    }

    public void setShip_price(BigDecimal ship_price) {
        this.ship_price = ship_price;
    }

    public int getOrder_status() {
        return this.order_status;
    }

    public void setOrder_status(int order_status) {
        this.order_status = order_status;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Payment getPayment() {
        return this.payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getPayTime() {
        return this.payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public List<GoodsCart> getGcs() {
        return this.gcs;
    }

    public void setGcs(List<GoodsCart> gcs) {
        this.gcs = gcs;
    }

    public Address getAddr() {
        return this.addr;
    }

    public void setAddr(Address addr) {
        this.addr = addr;
    }

    public String getShipCode() {
        return this.shipCode;
    }

    public void setShipCode(String shipCode) {
        this.shipCode = shipCode;
    }

    public Date getShipTime() {
        return this.shipTime;
    }

    public void setShipTime(Date shipTime) {
        this.shipTime = shipTime;
    }

    public Date getFinishTime() {
        return this.finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public int getInvoiceType() {
        return this.invoiceType;
    }

    public void setInvoiceType(int invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getInvoice() {
        return this.invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public Store getStore() {
        return this.store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public List<OrderFormLog> getOfls() {
        return this.ofls;
    }

    public void setOfls(List<OrderFormLog> ofls) {
        this.ofls = ofls;
    }

    public String getPay_msg() {
        return this.pay_msg;
    }

    public void setPay_msg(String pay_msg) {
        this.pay_msg = pay_msg;
    }

    public BigDecimal getGoods_amount() {
        return this.goods_amount;
    }

    public void setGoods_amount(BigDecimal goods_amount) {
        this.goods_amount = goods_amount;
    }

    public List<RefundLog> getRls() {
        return this.rls;
    }

    public void setRls(List<RefundLog> rls) {
        this.rls = rls;
    }

    public boolean isAuto_confirm_email() {
        return this.auto_confirm_email;
    }

    public void setAuto_confirm_email(boolean auto_confirm_email) {
        this.auto_confirm_email = auto_confirm_email;
    }

    public boolean isAuto_confirm_sms() {
        return this.auto_confirm_sms;
    }

    public void setAuto_confirm_sms(boolean auto_confirm_sms) {
        this.auto_confirm_sms = auto_confirm_sms;
    }

    public String getTransport() {
        return this.transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public ExpressCompany getEc() {
        return this.ec;
    }

    public void setEc(ExpressCompany ec) {
        this.ec = ec;
    }

    public String getOut_order_id() {
        return this.out_order_id;
    }

    public void setOut_order_id(String out_order_id) {
        this.out_order_id = out_order_id;
    }

    public String getOrder_seller_intro() {
        return this.order_seller_intro;
    }

    public void setOrder_seller_intro(String order_seller_intro) {
        this.order_seller_intro = order_seller_intro;
    }

    public Integer getRefund_reason() {
        return refund_reason;
    }

    public void setRefund_reason(Integer refund_reason) {
        this.refund_reason = refund_reason;
    }

    public BigDecimal getRefund_freight() {
        return refund_freight;
    }

    public void setRefund_freight(BigDecimal refund_freight) {
        this.refund_freight = refund_freight;
    }

    public Integer getRefund_mold() {
        return refund_mold;
    }

    public void setRefund_mold(Integer refund_mold) {
        this.refund_mold = refund_mold;
    }

    /**
     * 营业员编号
     * @return
     */
    public String getClerkCode() {
        return clerkCode;
    }
    /**
     * 营业员编号
     * @param clerkCode
     */
    public void setClerkCode(String clerkCode) {
        this.clerkCode = clerkCode;
    }
}
