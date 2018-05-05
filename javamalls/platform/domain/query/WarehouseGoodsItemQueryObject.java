package com.javamalls.platform.domain.query;

import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.query.QueryObject;

public class WarehouseGoodsItemQueryObject extends QueryObject {
    public WarehouseGoodsItemQueryObject(String currentPage, ModelAndView mv, String orderBy,
                                      String orderType) {
        super(currentPage, mv, orderBy, orderType);
    }

    public WarehouseGoodsItemQueryObject() {
    }
}
