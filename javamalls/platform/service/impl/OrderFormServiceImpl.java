package com.javamalls.platform.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.Access;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javamalls.base.basedao.IGenericDAO;
import com.javamalls.base.query.GenericPageList;
import com.javamalls.base.query.PageObject;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.ctrl.admin.tools.MsgTools;
import com.javamalls.platform.domain.GoodsCart;
import com.javamalls.platform.domain.GoodsItem;
import com.javamalls.platform.domain.GoodsSpecProperty;
import com.javamalls.platform.domain.OrderForm;
import com.javamalls.platform.domain.OrderFormLog;
import com.javamalls.platform.domain.OrderFormPayLog;
import com.javamalls.platform.domain.Payment;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.service.IGoodsCartService;
import com.javamalls.platform.service.IGoodsItemService;
import com.javamalls.platform.service.IGoodsSpecPropertyService;
import com.javamalls.platform.service.IOrderFormLogService;
import com.javamalls.platform.service.IOrderFormPayLogService;
import com.javamalls.platform.service.IOrderFormService;
import com.javamalls.platform.service.IPaymentService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.ITemplateService;
import com.javamalls.platform.service.IUserService;

@Service
@Transactional
public class OrderFormServiceImpl implements IOrderFormService {
    @Resource(name = "orderFormDAO")
    private IGenericDAO<OrderForm> orderFormDao;
    @Autowired
    private IOrderFormPayLogService	orderFormPayLogService;
    @Autowired
    private IOrderFormLogService	orderFormLogService;
    @Autowired
    private IUserService			userService;
    @Autowired
    private IPaymentService			paymentService;
    @Autowired
    private IGoodsItemService		goodsItemService;
    @Autowired
    private IGoodsSpecPropertyService	goodsSpecPropertyService;
    @Autowired
    private IGoodsCartService			goodsCartService;
    @Autowired
    private ISysConfigService			configService;
    @Autowired
    private ITemplateService			templateService;
    @Autowired
    private MsgTools					msgTools;
    public boolean save(OrderForm orderForm) {
        try {
            this.orderFormDao.save(orderForm);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public OrderForm getObjById(Long id) {
        OrderForm orderForm = (OrderForm) this.orderFormDao.get(id);
        if (orderForm != null) {
            return orderForm;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.orderFormDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> orderFormIds) {
        for (Serializable id : orderFormIds) {
            delete((Long) id);
        }
        return true;
    }

    public IPageList list(IQueryObject properties) {
        if (properties == null) {
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(OrderForm.class, query, params,
            this.orderFormDao);
        if (properties != null) {
            PageObject pageObj = properties.getPageObj();
            if (pageObj != null) {
                pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj.getCurrentPage()
                    .intValue(), pageObj.getPageSize() == null ? 0 : pageObj.getPageSize()
                    .intValue());
            }
        } else {
            pList.doList(0, -1);
        }
        return pList;
    }

    public boolean update(OrderForm orderForm) {
        try {
            this.orderFormDao.update(orderForm);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<OrderForm> query(String query, Map params, int begin, int max) {
        return this.orderFormDao.query(query, params, begin, max);
    }

	@Override
	public IPageList noLastList(IQueryObject properties) {
        if (properties == null) {
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(OrderForm.class, query, params,
            this.orderFormDao);
        if (properties != null) {
            PageObject pageObj = properties.getPageObj();
            if (pageObj != null) {
                pList.doNolastList(pageObj.getCurrentPage() == null ? 0 : pageObj.getCurrentPage()
                    .intValue(), pageObj.getPageSize() == null ? 0 : pageObj.getPageSize()
                    .intValue());
            }
        } else {
            pList.doNolastList(0, -1);
        }
        return pList;
    }

	@Override
	public int count(IQueryObject properties) {
        if (properties == null) {
            return 0;
        }
        int count=0;
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(OrderForm.class, query, params,
            this.orderFormDao);
        if (properties != null) {
        	count=pList.getCount();
        } 
        return count;
    }

	@Override
	public Long queryCount(String paramString, Map paramMap) {
		
		return this.orderFormDao.queryCount(paramString, paramMap);
	}
	@Override
	public OrderForm getObjByProperty(String propertyName, String value){
		return (OrderForm) this.orderFormDao.getBy(propertyName, value);
	}
	/**
     * 实体店下单
     */
	@Transactional
	@Override
    public  boolean outline_order_save(HttpServletRequest request,
            HttpServletResponse response, String addr_id,
            String client, String goods_amount, String ship_price,
            String total_price, String msg, String invoiceType,
            String companyName, String delivery_date,
            String file_id, String clerkCode, String cashPayPrice,
            String aliPayPrice, String wxPayPrice,
            String bankPayPrice){
    	boolean flag = false;
    	try{
    		//保存订单
            OrderForm of = new OrderForm();
            of.setCreatetime(new Date());
            of.setDisabled(false);
            of.setOrder_id(SecurityUserHolder.getCurrentUser().getId()
                           + CommUtil.formatTime("yyyyMMddHHmmss", new Date()));
            //Address addr = this.addressService.getObjById(CommUtil.null2Long(addr_id));
            //of.setAddr(addr);//收货地址
            of.setOrder_status(40);//已收货
            User buyer = this.userService.getObjById(CommUtil.null2Long(client));
            of.setUser(buyer);//客户
            Store store = SecurityUserHolder.getCurrentUser().getStore();
            of.setStore(store);//卖家
            of.setGoods_amount(new BigDecimal(goods_amount));//商品总价
            of.setShip_price(new BigDecimal(ship_price));//邮费
            of.setTotalPrice(new BigDecimal(total_price));//订单总金额
            of.setOrder_type("outline");//实体店订单
            of.setMsg(msg);//订单备注
            //of.setInvoiceType(CommUtil.null2Int(invoiceType));//发票类型
            //of.setInvoice(companyName);//发票抬头
            if (delivery_date != null && !"".equals(delivery_date)) {//交货日期
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    of.setDelivery_date(sdf.parse(delivery_date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            of.setClerkCode(clerkCode);//营业员编号
            of.setPay_msg("实体店下单进行线下支付");
            Map<String, Object> params = new HashMap<String, Object>();
            List<Payment> payments = null;
            params.put("mark", "outline");
            params.put("type", "admin");
            payments = this.paymentService
                .query(
                    "select obj from Payment obj where obj.mark=:mark and obj.type=:type ",
                    params, -1, -1);
            if (payments.size() > 0) {
                of.setPayment((Payment) payments.get(0));
                of.setPayTime(new Date());
            }
            of.setTransport("卖家承担");
            this.save(of);

            //    List<GoodsCart>           gcs              = new ArrayList<GoodsCart>();
            String[] numbers = request.getParameterValues("number");
            String[] purchase_prices = request.getParameterValues("purchase_price");
            String[] itemIds = request.getParameterValues("itemId");

            if (itemIds != null && itemIds.length > 0) {
                Date now = new Date();
                for (int i = 0; i < itemIds.length; i++) {
                    GoodsItem goodsItem = this.goodsItemService.getObjById(CommUtil
                        .null2Long(itemIds[i]));
                    GoodsCart goodsCart = new GoodsCart();
                    goodsCart.setCreatetime(now);
                    goodsCart.setDisabled(false);
                    goodsCart.setCount(CommUtil.null2Int(numbers[i]));
                    goodsCart.setPrice(CommUtil.null2BigDecimal(purchase_prices[i]));
                    String spec_combination = goodsItem.getSpec_combination();
                    if (spec_combination != null && !"".equals(spec_combination)) {
                        String[] gsp_ids = spec_combination.split("_");
                        if (gsp_ids != null && gsp_ids.length > 0) {
                            for (String gsp_id : gsp_ids) {
                                GoodsSpecProperty spec_property = this.goodsSpecPropertyService
                                    .getObjById(CommUtil.null2Long(gsp_id));
                                goodsCart.getGsps().add(spec_property);

                            }

                        }

                        spec_combination = spec_combination.replaceAll("_", ",");
                        goodsCart.setSpec_id(spec_combination);
                        goodsCart.setSpec_info(goodsItem.getSpec_info());
                    }
                    goodsCart.setGoods(goodsItem.getGoods());
                    goodsCart.setOf(of);

                    this.goodsCartService.save(goodsCart);

                    /*//更新库存
                    goodsItem.setGoods_inventory(goodsItem.getGoods_inventory()-CommUtil.null2Int(numbers[i]));
                     Map<String,Object> map=new HashMap<String, Object>();
                     map.put("goodsItem_id", goodsItem.getId());
                     //更新库存商品的库存
                     List<WarehouseGoodsItem> list = this.warehouseGoodsItemService.query(
                    		 "select obj from WarehouseGoodsItem obj where obj.goods_item.id=:goodsItem_id", map, 0, 1);
                     if(list!=null&&list.size()>0){
                    	 WarehouseGoodsItem warehouseGoodsItem = list.get(0);
                    	 warehouseGoodsItem.setWarehoust_number(warehouseGoodsItem.getWarehoust_number()-CommUtil.null2Int(numbers[i]));
                    	 this.warehouseGoodsItemService.update(warehouseGoodsItem);
                    	 
                     }
                     this.goodsItemService.update(goodsItem);*/

                }
            }
            //String cashPayPrice,String aliPayPrice
            //String wxPayPrice,String bankPayPrice
            if (cashPayPrice != null && !"".equals(cashPayPrice)) {//现金支付
                OrderFormPayLog payLog = new OrderFormPayLog();
                payLog.setCreatetime(new Date());
                payLog.setDisabled(false);
                payLog.setOf(of);
                payLog.setPay_child_class(5);
                payLog.setPay_status(1);
                payLog.setPay_time(new Date());
                payLog.setUser(buyer);
                payLog.setShould_pay_amount(of.getTotalPrice());//应付金额
                payLog.setActual_pay_amount(new BigDecimal(cashPayPrice));
                this.orderFormPayLogService.save(payLog);

            }
            if (aliPayPrice != null && !"".equals(aliPayPrice)) {//支付宝支付
                OrderFormPayLog payLog = new OrderFormPayLog();
                payLog.setCreatetime(new Date());
                payLog.setDisabled(false);
                payLog.setOf(of);
                payLog.setPay_child_class(2);
                payLog.setPay_status(1);
                payLog.setPay_time(new Date());
                payLog.setUser(buyer);
                payLog.setShould_pay_amount(of.getTotalPrice());//应付金额
                payLog.setActual_pay_amount(new BigDecimal(aliPayPrice));
                this.orderFormPayLogService.save(payLog);
            }
            if (wxPayPrice != null && !"".equals(wxPayPrice)) {//微信支付
                OrderFormPayLog payLog = new OrderFormPayLog();
                payLog.setCreatetime(new Date());
                payLog.setDisabled(false);
                payLog.setOf(of);
                payLog.setPay_child_class(3);
                payLog.setPay_status(1);
                payLog.setPay_time(new Date());
                payLog.setUser(buyer);
                payLog.setShould_pay_amount(of.getTotalPrice());//应付金额
                payLog.setActual_pay_amount(new BigDecimal(wxPayPrice));
                this.orderFormPayLogService.save(payLog);
            }
            if (bankPayPrice != null && !"".equals(bankPayPrice)) {//银行卡支付
                OrderFormPayLog payLog = new OrderFormPayLog();
                payLog.setCreatetime(new Date());
                payLog.setDisabled(false);
                payLog.setOf(of);
                payLog.setPay_child_class(4);
                payLog.setPay_status(1);
                payLog.setPay_time(new Date());
                payLog.setUser(buyer);
                payLog.setShould_pay_amount(of.getTotalPrice());//应付金额
                payLog.setActual_pay_amount(new BigDecimal(bankPayPrice));
                this.orderFormPayLogService.save(payLog);
            }

            OrderFormLog ofl = new OrderFormLog();
            ofl.setCreatetime(new Date());
            ofl.setOf(of);
            ofl.setLog_info("实体店下单");
            ofl.setLog_user(SecurityUserHolder.getCurrentUser());
            this.orderFormLogService.save(ofl);

            if (this.configService.getSysConfig().isSmsEnbale()) {
                com.javamalls.platform.domain.Template template = this.templateService
                    .getObjByProperty("mark", "sms_tobuyer_underline_pay_ok_notify");
                if ((template != null) && (template.isOpen())) {
                    //短信发送  
                    Map<String, String> map = new HashMap<String, String>();
                    String buyerName = buyer.getUserName();
                    if (buyer.getTrueName() != null && !"".equals(buyer.getTrueName())) {
                        buyerName = buyer.getTrueName();
                    }
                    map.put("buyerName", buyerName);

                    map.put("order_id", of.getOrder_id());
                    map.put("storeName", store.getStore_name());

                    if (buyer.getMobile() != null && !"".equals(buyer.getMobile())) {
                        this.msgTools.sendSMS(buyer.getMobile(), template.getTitle(), map);
                    }
                }
            }
            flag = true;
    	}catch(Exception e){
    		e.printStackTrace();
    		return  flag;
    	}
    	
        
    	return flag;
    }
}
