package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.SnsFriend;

public abstract interface ISnsFriendService {
    public abstract boolean save(SnsFriend paramSnsFriend);

    public abstract SnsFriend getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(SnsFriend paramSnsFriend);

    public abstract List<SnsFriend> query(String paramString, Map paramMap, int paramInt1,
                                          int paramInt2);
}
