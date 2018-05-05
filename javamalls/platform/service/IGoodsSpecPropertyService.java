package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.GoodsSpecProperty;

public abstract interface IGoodsSpecPropertyService {
    public abstract boolean save(GoodsSpecProperty paramGoodsSpecProperty);

    public abstract GoodsSpecProperty getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(GoodsSpecProperty paramGoodsSpecProperty);

    public abstract List<GoodsSpecProperty> query(String paramString, Map paramMap, int paramInt1,
                                                  int paramInt2);
}
