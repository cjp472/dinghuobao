package com.javamalls.platform.domain.query;

import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.query.QueryObject;

public class HomePageGoodsClassQueryObject extends QueryObject {
    public HomePageGoodsClassQueryObject(String currentPage, ModelAndView mv, String orderBy,
                                         String orderType) {
        super(currentPage, mv, orderBy, orderType);
    }

    public HomePageGoodsClassQueryObject() {
    }
}
