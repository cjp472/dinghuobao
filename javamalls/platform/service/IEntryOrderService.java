package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.EntryOrder;
import com.javamalls.platform.domain.User;

public interface IEntryOrderService {

	public abstract boolean save(EntryOrder param);

    public abstract EntryOrder getObjById(Long paramLong);
    
    public abstract boolean delete(Long paramLong);
    
    public abstract boolean batchDelete(List<Serializable> paramList);
    
    public abstract IPageList list(IQueryObject paramIQueryObject);
     
    public abstract boolean update(EntryOrder param);
    
    public abstract List<EntryOrder> query(String paramString, Map paramMap, int paramInt1,
                                             int paramInt2);
    /**
     * 保存出入单明细  把出库商的商品导入到入库商
     * @param numbers
     * @param itemIds
     * @param entryOrder
     * @param userInStore
     * @return
     */
    public abstract boolean saveEntryOrderAndGoodsAndGoodsItem(String[] numbers ,String[] itemIds ,EntryOrder entryOrder,User userInStore);
   
}