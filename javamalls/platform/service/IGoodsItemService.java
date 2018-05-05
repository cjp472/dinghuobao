package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.GoodsItem;

public abstract interface IGoodsItemService {
    public abstract boolean save(GoodsItem paramGoodsItem);

    public abstract GoodsItem getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);
    
    public abstract boolean delete_wuli(Long paramLong);
    
    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);
    
    public abstract IPageList nolastlist(IQueryObject paramIQueryObject);

    public abstract boolean update(GoodsItem paramGoodsItem);

    public abstract List<GoodsItem> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

    public abstract GoodsItem getObjByProperty(String paramString, Object paramObject);
}
