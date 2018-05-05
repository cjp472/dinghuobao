package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.StoreGradeLog;

public abstract interface IStoreGradeLogService {
    public abstract boolean save(StoreGradeLog paramStoreGradeLog);

    public abstract StoreGradeLog getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(StoreGradeLog paramStoreGradeLog);

    public abstract List<StoreGradeLog> query(String paramString, Map paramMap, int paramInt1,
                                              int paramInt2);
}
