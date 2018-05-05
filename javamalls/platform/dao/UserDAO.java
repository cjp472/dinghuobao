package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;

import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.User;

@Repository("userDAO")
public class UserDAO extends GenericDAO<User> {
}
