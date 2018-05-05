package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.IntegralGoodsOrder;

public abstract interface IIntegralGoodsOrderService {
    public abstract boolean save(IntegralGoodsOrder paramIntegralGoodsOrder);

    public abstract IntegralGoodsOrder getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);
    
    public abstract IPageList noLastList(IQueryObject paramIQueryObject);

    public abstract boolean update(IntegralGoodsOrder paramIntegralGoodsOrder);

    public abstract List<IntegralGoodsOrder> query(String paramString, Map paramMap, int paramInt1,
                                                   int paramInt2);
}
