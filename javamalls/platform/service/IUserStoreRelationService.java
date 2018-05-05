package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.UserStoreRelation;

public abstract interface IUserStoreRelationService {
    public abstract boolean save(UserStoreRelation paramAdvert);

    public abstract UserStoreRelation getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public IPageList noLastList(IQueryObject properties);

    public abstract boolean update(UserStoreRelation paramAdvert);

    public abstract List<UserStoreRelation> query(String paramString, Map paramMap, int paramInt1,
                                                  int paramInt2);

    public abstract Long queryCount(String paramString, Map paramMap);
}
