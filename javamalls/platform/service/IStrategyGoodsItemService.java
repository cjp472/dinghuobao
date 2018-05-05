package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.StrategyGoodsItem;

public abstract interface IStrategyGoodsItemService {
    public abstract boolean save(StrategyGoodsItem paramStrategyGoodsItem);

    public abstract StrategyGoodsItem getObjById(Long paramLong);
    /**
     * 物理删除
     * @param paramLong
     * @return
     */
    public abstract boolean delete(Long paramLong);
    
    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);
    
    public abstract IPageList nolastlist(IQueryObject paramIQueryObject);

    public abstract boolean update(StrategyGoodsItem paramStrategyGoodsItem);

    public abstract List<StrategyGoodsItem> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

    public abstract StrategyGoodsItem getObjByProperty(String paramString, Object paramObject);
}
