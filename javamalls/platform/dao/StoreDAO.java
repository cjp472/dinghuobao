package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;

import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.Store;

@Repository("storeDAO")
public class StoreDAO extends GenericDAO<Store> {
}
