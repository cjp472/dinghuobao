package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.GoodsBrandCategory;

public abstract interface IGoodsBrandCategoryService {
    public abstract boolean save(GoodsBrandCategory paramGoodsBrandCategory);

    public abstract GoodsBrandCategory getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(GoodsBrandCategory paramGoodsBrandCategory);

    public abstract List<GoodsBrandCategory> query(String paramString, Map paramMap, int paramInt1,
                                                   int paramInt2);

    public abstract GoodsBrandCategory getObjByProperty(String paramString, Object paramObject);
}
