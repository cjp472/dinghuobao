package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;

import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.Advert;
import com.javamalls.platform.domain.UserStoreRelation;

@Repository("userStoreRelationDAO")
public class UserStoreRelationDAO extends GenericDAO<UserStoreRelation> {
}
