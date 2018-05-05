package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.Favorite;

public abstract interface IFavoriteService {
    public abstract boolean save(Favorite paramFavorite);

    public abstract Favorite getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);
    
    public abstract IPageList noLastList(IQueryObject paramIQueryObject);

    public abstract boolean update(Favorite paramFavorite);

    public abstract List<Favorite> query(String paramString, Map paramMap, int paramInt1,
                                         int paramInt2);
    
    public abstract int count(IQueryObject paramIQueryObject);
}
