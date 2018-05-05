package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;

import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.Goods;

@Repository("goodsDAO")
public class GoodsDAO extends GenericDAO<Goods> {
}
