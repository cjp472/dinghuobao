package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;

import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.Coupon;

@Repository("couponDAO")
public class CouponDAO extends GenericDAO<Coupon> {
}
