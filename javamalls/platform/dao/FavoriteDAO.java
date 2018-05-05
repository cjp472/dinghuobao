package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;

import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.Favorite;

@Repository("favoriteDAO")
public class FavoriteDAO extends GenericDAO<Favorite> {
}
