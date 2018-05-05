package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.CompanyInfo;

public abstract interface ICompanyInfoService {
    public abstract boolean save(CompanyInfo paramCompanyInfo);

    public abstract CompanyInfo getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);
    
    public abstract boolean delete_wuli(Long paramLong);
    
    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);
    
    public abstract IPageList nolastlist(IQueryObject paramIQueryObject);

    public abstract boolean update(CompanyInfo paramCompanyInfo);

    public abstract List<CompanyInfo> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

    public abstract CompanyInfo getObjByProperty(String paramString, Object paramObject);
}
