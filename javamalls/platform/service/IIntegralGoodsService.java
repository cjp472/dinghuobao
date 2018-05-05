package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.IntegralGoods;

public abstract interface IIntegralGoodsService {
    public abstract boolean save(IntegralGoods paramIntegralGoods);

    public abstract IntegralGoods getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(IntegralGoods paramIntegralGoods);

    public abstract List<IntegralGoods> query(String paramString, Map paramMap, int paramInt1,
                                              int paramInt2);
}
