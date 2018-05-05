package com.javamalls.platform.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.javamalls.base.domain.CommonEntity;
import com.javamalls.platform.domain.Address;
import com.javamalls.platform.domain.ExpressCompany;
import com.javamalls.platform.domain.GoodsCart;
import com.javamalls.platform.domain.Payment;

/**提交订单接口Vo
 * 
 *                       
 * @Filename: OrderFormAddVo.java
 * @Version: 2.7.0
 * @Author: 桑森林
 * @Email: sangslmail@163.com
 *
 */
public class OrderFormAddVo extends CommonEntity {

    private String            order_id;
    private String            order_type;
    List<GoodsCartVo>           gcs              = new ArrayList<GoodsCartVo>();
    private BigDecimal        totalPrice;
    private BigDecimal        goods_amount;
    private String            msg;//订单备注
    private Payment           payment;
    private String            transport;
    private String            shipCode;
    private String            return_shipCode;
    private Date              return_shipTime;
    private String            return_content;
    private BigDecimal        ship_price;
    private ExpressCompany    ec;
    private int               order_status;//0已取消;10待付款;15线下支付待审核;16货到付款待发货;20已付款;30已发货;40已收货;45买家申请退货;46退货中;47退货完成，已结束;48卖家拒绝退货;49退货失败;50已完成,已评价;60已结束;65已结束，不可评价
    private Long              user_id;
    private Long             store_id;
    private Date              payTime;
    private Date              shipTime;
    private Address           addr;
    private int               invoiceType;
    private String            invoice;   
    private Date delivery_date;//交货日期
    public String getOrder_id() {
        return order_id;
    }
    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }
    public String getOrder_type() {
        return order_type;
    }
    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }
    public List<GoodsCartVo> getGcs() {
        return gcs;
    }
    public void setGcs(List<GoodsCartVo> gcs) {
        this.gcs = gcs;
    }
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    public BigDecimal getGoods_amount() {
        return goods_amount;
    }
    public void setGoods_amount(BigDecimal goods_amount) {
        this.goods_amount = goods_amount;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public Payment getPayment() {
        return payment;
    }
    public void setPayment(Payment payment) {
        this.payment = payment;
    }
    public String getTransport() {
        return transport;
    }
    public void setTransport(String transport) {
        this.transport = transport;
    }
    public String getShipCode() {
        return shipCode;
    }
    public void setShipCode(String shipCode) {
        this.shipCode = shipCode;
    }
    public String getReturn_shipCode() {
        return return_shipCode;
    }
    public void setReturn_shipCode(String return_shipCode) {
        this.return_shipCode = return_shipCode;
    }
    public Date getReturn_shipTime() {
        return return_shipTime;
    }
    public void setReturn_shipTime(Date return_shipTime) {
        this.return_shipTime = return_shipTime;
    }
    public String getReturn_content() {
        return return_content;
    }
    public void setReturn_content(String return_content) {
        this.return_content = return_content;
    }
    public BigDecimal getShip_price() {
        return ship_price;
    }
    public void setShip_price(BigDecimal ship_price) {
        this.ship_price = ship_price;
    }
    
    public ExpressCompany getEc() {
        return ec;
    }
    public void setEc(ExpressCompany ec) {
        this.ec = ec;
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
    public Date getPayTime() {
        return payTime;
    }
    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }
    public Date getShipTime() {
        return shipTime;
    }
    public void setShipTime(Date shipTime) {
        this.shipTime = shipTime;
    }
    public Address getAddr() {
        return addr;
    }
    public void setAddr(Address addr) {
        this.addr = addr;
    }
    public int getInvoiceType() {
        return invoiceType;
    }
    public void setInvoiceType(int invoiceType) {
        this.invoiceType = invoiceType;
    }
    public String getInvoice() {
        return invoice;
    }
    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }
    public Date getDelivery_date() {
        return delivery_date;
    }
    public void setDelivery_date(Date delivery_date) {
        this.delivery_date = delivery_date;
    }

    
}
