package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;

import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.MobileVerifyCode;

@Repository("mobileVerifyCodeDAO")
public class MobileVerifyCodeDAO extends GenericDAO<MobileVerifyCode> {
}
