package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;

import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.Album;

@Repository("albumDAO")
public class AlbumDAO extends GenericDAO<Album> {
}
