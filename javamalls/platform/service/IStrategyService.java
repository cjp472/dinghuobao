package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.Strategy;

public abstract interface IStrategyService {
    public abstract boolean save(Strategy paramStrategy);

    public abstract Strategy getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);
    
    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);
    
    public abstract IPageList nolastlist(IQueryObject paramIQueryObject);

    public abstract boolean update(Strategy paramStrategy);

    public abstract List<Strategy> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

    public abstract Strategy getObjByProperty(String paramString, Object paramObject);
}
