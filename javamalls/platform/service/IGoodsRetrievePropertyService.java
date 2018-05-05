package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.GoodsBrand;
import com.javamalls.platform.domain.GoodsRetrieve;
import com.javamalls.platform.domain.GoodsRetrieveProperty;

public abstract interface IGoodsRetrievePropertyService {
    public abstract boolean save(GoodsRetrieveProperty paramGoodsRetrieveProperty);

    public abstract GoodsRetrieveProperty getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(GoodsRetrieveProperty paramGoodsRetrieveProperty);

    public abstract List<GoodsRetrieveProperty> query(String paramString, Map paramMap, int paramInt1,
                                           int paramInt2);
}
