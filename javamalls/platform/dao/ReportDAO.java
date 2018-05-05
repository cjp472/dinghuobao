package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;

import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.Report;

@Repository("reportDAO")
public class ReportDAO extends GenericDAO<Report> {
}
