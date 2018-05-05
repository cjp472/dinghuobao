package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;

import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.Role;

@Repository("roleDAO")
public class RoleDAO extends GenericDAO<Role> {
}
