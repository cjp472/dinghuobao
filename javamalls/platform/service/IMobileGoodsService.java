package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.MobileGoods;

public abstract interface IMobileGoodsService {
    public abstract boolean save(MobileGoods paramGoods);

    public abstract MobileGoods getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);
    public abstract IPageList noLastList(IQueryObject properties);

    public abstract boolean update(MobileGoods paramGoods);

    public abstract List<MobileGoods> query(String paramString, Map paramMap, int paramInt1,
                                            int paramInt2);

    public abstract MobileGoods getObjByProperty(String paramString, Object paramObject);

    public abstract boolean remove(String ids);
}
