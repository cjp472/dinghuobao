package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.StoreVIP;

public interface IStoreVIPService {
    public boolean save(StoreVIP storeVIP);

    public StoreVIP getObjById(Long paramLong);

    public boolean delete(Long paramLong);

    public boolean batchDelete(List<Serializable> paramList);

    public IPageList list(IQueryObject paramIQueryObject);

    public boolean update(StoreVIP storeVIP);

    public List<StoreVIP> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}
