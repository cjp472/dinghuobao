package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;

import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.Complaint;

@Repository("complaintDAO")
public class ComplaintDAO extends GenericDAO<Complaint> {
}
