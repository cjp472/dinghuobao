package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;

import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.Advert;

@Repository("advertDAO")
public class AdvertDAO extends GenericDAO<Advert> {
}
