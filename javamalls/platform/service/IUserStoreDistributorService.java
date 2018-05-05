package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.UserStoreDistributor;

public interface IUserStoreDistributorService {

	public abstract boolean save(UserStoreDistributor param);

    public abstract UserStoreDistributor getObjById(Long paramLong);
    
    public abstract boolean delete(Long paramLong);
    
    public abstract boolean batchDelete(List<Serializable> paramList);
    
    public abstract IPageList list(IQueryObject paramIQueryObject);
    
    public IPageList noLastList(IQueryObject properties); 
    
    public abstract boolean update(UserStoreDistributor param);
    
    public abstract List<UserStoreDistributor> query(String paramString, Map paramMap, int paramInt1,
                                             int paramInt2);
    
    /**
     * 
     */
    public abstract List<UserStoreDistributor> getByUserIdAndStoreId(Long userId,Long storeid);
   
}