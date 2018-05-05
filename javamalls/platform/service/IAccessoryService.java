package com.javamalls.platform.service;

import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.Accessory;

public abstract interface IAccessoryService {
    public abstract boolean save(Accessory paramAccessory);

    public abstract boolean delete(Long paramLong);

    public abstract boolean update(Accessory paramAccessory);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract Accessory getObjById(Long paramLong);

    public abstract Accessory getObjByProperty(String paramString1, String paramString2);

    public abstract List<Accessory> query(String paramString, Map paramMap, int paramInt1,
                                          int paramInt2);
}
