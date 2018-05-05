package com.javamalls.platform.service;

import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.Res;

public abstract interface IResService {
    public abstract boolean save(Res paramRes);

    public abstract boolean delete(Long paramLong);

    public abstract boolean update(Res paramRes);

    public abstract Res getObjById(Long paramLong);

    public abstract List<Res> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

    public abstract IPageList list(IQueryObject paramIQueryObject);
}
