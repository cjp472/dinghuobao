package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;

import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.HomePage;

@Repository("homePageDAO")
public class HomePageDAO extends GenericDAO<HomePage> {
}
