package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.GoodslabelGoods;

public interface IGoodslabelGoodsService {

	public abstract boolean save(GoodslabelGoods param);

    public abstract GoodslabelGoods getObjById(Long paramLong);
    
    public abstract boolean delete(Long paramLong);
    
    public abstract boolean batchDelete(List<Serializable> paramList);
    
    public abstract IPageList list(IQueryObject paramIQueryObject);
     
    public abstract boolean update(GoodslabelGoods param);
    
    public abstract List<GoodslabelGoods> query(String paramString, Map paramMap, int paramInt1,
                                             int paramInt2);
    //查询GoodsLabelId 集合
    public abstract List<Long> queryGoodsLabelLongId(String paramString, Map paramMap, int paramInt1,
            int paramInt2);
}