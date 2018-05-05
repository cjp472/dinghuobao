package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.EntryOrderDetail;

public interface IEntryOrderDetailService {

	public abstract boolean save(EntryOrderDetail param);

    public abstract EntryOrderDetail getObjById(Long paramLong);
    
    public abstract boolean delete(Long paramLong);
    
    public abstract boolean batchDelete(List<Serializable> paramList);
    
    public abstract IPageList list(IQueryObject paramIQueryObject);
     
    public abstract boolean update(EntryOrderDetail param);
    
    public abstract List<EntryOrderDetail> query(String paramString, Map paramMap, int paramInt1,
                                             int paramInt2);
    
   
}