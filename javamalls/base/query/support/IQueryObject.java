package com.javamalls.base.query.support;

import java.util.Map;

import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.query.PageObject;

public abstract interface IQueryObject {
    public abstract String getQuery();

    public abstract Map<String, Object> getParameters();

    public abstract PageObject getPageObj();

    public abstract IQueryObject addQuery(String paramString, Map<String, Object> paramMap);

    public abstract IQueryObject addQuery(String paramString1, SysMap paramSysMap,
                                          String paramString2);

    public abstract IQueryObject addQuery(String paramString1, Object paramObject,
                                          String paramString2, String paramString3);
}
