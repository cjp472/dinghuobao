package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.OrderForm;
import com.javamalls.platform.domain.Payment;

public abstract interface IOrderFormService {
    public abstract boolean save(OrderForm paramOrderForm);

    public abstract OrderForm getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);
    
    public abstract IPageList noLastList(IQueryObject paramIQueryObject);

    public abstract boolean update(OrderForm paramOrderForm);

    public abstract List<OrderForm> query(String paramString, Map paramMap, int paramInt1,
                                          int paramInt2);
    public abstract Long queryCount(String paramString, Map paramMap);
    
    public abstract int count(IQueryObject paramIQueryObject);
    
    public abstract OrderForm getObjByProperty(String paramString1, String paramString2);
    
    /**
     * 实体店下单
     */
    public abstract boolean outline_order_save(HttpServletRequest request,
            HttpServletResponse response, String addr_id,
            String client, String goods_amount, String ship_price,
            String total_price, String msg, String invoiceType,
            String companyName, String delivery_date,
            String file_id, String clerkCode, String cashPayPrice,
            String aliPayPrice, String wxPayPrice,
            String bankPayPrice);
}
