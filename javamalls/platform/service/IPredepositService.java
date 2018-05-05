package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.Predeposit;

public abstract interface IPredepositService {
    public abstract boolean save(Predeposit paramPredeposit);

    public abstract Predeposit getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(Predeposit paramPredeposit);

    public abstract List<Predeposit> query(String paramString, Map paramMap, int paramInt1,
                                           int paramInt2);
}
