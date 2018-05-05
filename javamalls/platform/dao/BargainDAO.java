package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;

import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.Bargain;

@Repository("bargainDAO")
public class BargainDAO extends GenericDAO<Bargain> {
}
