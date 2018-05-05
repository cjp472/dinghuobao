package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.User;

public abstract interface IGoodsService {
    public abstract boolean save(Goods paramGoods);

    public abstract Goods getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean delete_wuli(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract IPageList nolastlist(IQueryObject paramIQueryObject);

    public abstract boolean update(Goods paramGoods);

    public abstract List<Goods> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

    public abstract Goods getObjByProperty(String paramString, Object paramObject);

    public abstract int importSingleGoods(User user, MultipartFile excelFile,
                                          HttpServletRequest request);

    public abstract int importPackageGoods(User user, MultipartFile excelFile,
                                           HttpServletRequest request);

    public abstract int importShareGoods(User user, MultipartFile excelFile,
                                         HttpServletRequest request);

    public abstract int importBuyerGoods(User user, String[] goodIds, HttpServletRequest request,
                                         String[] sale_pricesArr);

    public abstract int importMergeBuyerGoods(User user, String[] goodIds,
                                              HttpServletRequest request, String[] sale_pricesArr);
}
