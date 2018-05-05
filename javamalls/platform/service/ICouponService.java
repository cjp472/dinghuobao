package com.javamalls.platform.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.Coupon;

public abstract interface ICouponService {
    public abstract boolean save(Coupon paramCoupon);

    public abstract Coupon getObjById(Long paramLong);

    public abstract boolean delete(Long paramLong);

    public abstract boolean batchDelete(List<Serializable> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(Coupon paramCoupon);

    public abstract List<Coupon> query(String paramString, Map paramMap, int paramInt1,
                                       int paramInt2);
}
