package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;

import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.UserConfig;

@Repository("userConfigDAO")
public class UserConfigDAO extends GenericDAO<UserConfig> {
}
