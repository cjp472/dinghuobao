package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.Strategy;
import com.javamalls.platform.domain.UserStrategy;

public abstract interface IUserStrategyService {
    public abstract boolean save(UserStrategy paramUserStrategy);

    public abstract UserStrategy getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(UserStrategy paramUserStrategy);

    public abstract List<UserStrategy> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
    
    public abstract UserStrategy getUserStrategyByUserIdAndStoreId(Long userId,Long storeId);
    
    public abstract UserStrategy getObjByProperty(String paramString, Object paramObject);
}
