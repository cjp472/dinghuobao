package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.GoodsLabel;

public interface IGoodsLabelService {

	public abstract boolean save(GoodsLabel param);

    public abstract GoodsLabel getObjById(Long paramLong);
    
    public abstract boolean delete(Long paramLong);
    
    public abstract boolean batchDelete(List<Serializable> paramList);
    
    public abstract IPageList list(IQueryObject paramIQueryObject);
     
    public abstract boolean update(GoodsLabel param);
    
    public abstract List<GoodsLabel> query(String paramString, Map paramMap, int paramInt1,
                                             int paramInt2);
    
   
}