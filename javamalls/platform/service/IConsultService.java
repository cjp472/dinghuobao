package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.Consult;

public abstract interface IConsultService {
    public abstract boolean save(Consult paramConsult);

    public abstract Consult getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);
    
    public abstract IPageList nolastlist(IQueryObject paramIQueryObject);

    public abstract boolean update(Consult paramConsult);

    public abstract List<Consult> query(String paramString, Map paramMap, int paramInt1,
                                        int paramInt2);
}
