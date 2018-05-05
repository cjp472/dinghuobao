package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.UserGoodsClass;

public abstract interface IUserGoodsClassService {
    public abstract boolean save(UserGoodsClass paramUserGoodsClass);

    public abstract UserGoodsClass getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(UserGoodsClass paramUserGoodsClass);

    public abstract List<UserGoodsClass> query(String paramString, Map paramMap, int paramInt1,
                                               int paramInt2);
    public abstract int importGoodClass(User user,MultipartFile excelFile);
    
    public abstract UserGoodsClass getUserGoodsClassByName(User user,String name);
}
