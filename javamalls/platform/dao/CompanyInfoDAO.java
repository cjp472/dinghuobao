package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;

import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.CompanyInfo;

@Repository("companyInfoDAO")
public class CompanyInfoDAO extends GenericDAO<CompanyInfo> {
}
