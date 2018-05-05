package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.Supplier;

public abstract interface ISupplierService {
    public abstract boolean save(Supplier paramSupplier);

    public abstract Supplier getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(Supplier paramSupplier);

    public abstract List<Supplier> query(String paramString, Map paramMap, int paramInt1,
                                        int paramInt2);
}
