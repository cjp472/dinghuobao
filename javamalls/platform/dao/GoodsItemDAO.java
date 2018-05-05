package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;

import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.GoodsItem;

@Repository("goodsItemDAO")
public class GoodsItemDAO extends GenericDAO<GoodsItem> {
}
