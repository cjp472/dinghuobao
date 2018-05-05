package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.GoodsBrand;
import com.javamalls.platform.domain.GoodsRetrieve;

public abstract interface IGoodsRetrieveService {
    public abstract boolean save(GoodsRetrieve paramGoodsRetrieve);

    public abstract GoodsRetrieve getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(GoodsRetrieve paramGoodsRetrieve);

    public abstract List<GoodsRetrieve> query(String paramString, Map paramMap, int paramInt1,
                                           int paramInt2);
}
