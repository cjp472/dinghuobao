package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.PurchaseOrder;

public abstract interface IPurchaseOrderService {
    public abstract boolean save(PurchaseOrder paramPurchaseOrder);

    public abstract PurchaseOrder getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(PurchaseOrder paramPurchaseOrder);

    public abstract List<PurchaseOrder> query(String paramString, Map paramMap, int paramInt1,
                                        int paramInt2);
}
