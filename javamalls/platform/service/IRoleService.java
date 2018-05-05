package com.javamalls.platform.service;

import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.Role;

public abstract interface IRoleService {
    public abstract boolean save(Role paramRole);

    public abstract boolean delete(Long paramLong);

    public abstract boolean update(Role paramRole);

    public abstract Role getObjById(Long paramLong);

    public abstract List<Role> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract Role getObjByProperty(String paramString, Object paramObject);
}
