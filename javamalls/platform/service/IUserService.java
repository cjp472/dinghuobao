package com.javamalls.platform.service;

import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.User;

public abstract interface IUserService {
    public abstract boolean save(User paramUser);

    public abstract boolean delete(Long paramLong);

    public abstract boolean update(User paramUser);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract User getObjById(Long paramLong);

    public abstract User getObjByProperty(String paramString1, String paramString2);

    @SuppressWarnings("rawtypes")
    public abstract List<User> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

    public abstract Long queryCount(String paramString, Map paramMap);
    
    /**
     * 根据微信身份标识查询用户，若不存在，自动创建新用户
     * @param openid
     * @return
     */
    public abstract User	getUserByWxOpenid(String wxOpenid);
}
