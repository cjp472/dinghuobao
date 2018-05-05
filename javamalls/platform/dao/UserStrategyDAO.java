package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;

import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.Activity;
import com.javamalls.platform.domain.UserStrategy;

@Repository("userStrategyDAO")
public class UserStrategyDAO extends GenericDAO<UserStrategy> {
}
