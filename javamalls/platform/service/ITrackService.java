package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.Track;

public abstract interface ITrackService {
    public void save(Track paramTrack);

    public abstract Track getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);
    
    public abstract IPageList noLastList(IQueryObject paramIQueryObject);

    public void update(Track paramTrack);

    public abstract List<Track> query(String paramString, Map<String, Object> paramMap,
                                      int paramInt1, int paramInt2);
}
