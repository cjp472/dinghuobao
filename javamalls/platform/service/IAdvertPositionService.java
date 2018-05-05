package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.AdvertPosition;

public abstract interface IAdvertPositionService {
    public abstract boolean save(AdvertPosition paramAdvertPosition);

    public abstract AdvertPosition getObjById(Long paramLong);
    
    public abstract AdvertPosition getObjByMark(String paramMark);
    
    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(AdvertPosition paramAdvertPosition);

    public abstract List<AdvertPosition> query(String paramString, Map paramMap, int paramInt1,
                                               int paramInt2);
}
