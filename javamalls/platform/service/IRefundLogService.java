package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.RefundLog;

public abstract interface IRefundLogService {
    public abstract boolean save(RefundLog paramRefundLog);

    public abstract RefundLog getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);
    
    public abstract IPageList noLastlist(IQueryObject paramIQueryObject);

    public abstract boolean update(RefundLog paramRefundLog);

    public abstract List<RefundLog> query(String paramString, Map paramMap, int paramInt1,
                                          int paramInt2);
}
