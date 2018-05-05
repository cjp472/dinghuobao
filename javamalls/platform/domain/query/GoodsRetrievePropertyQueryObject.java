package com.javamalls.platform.domain.query;

import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.query.QueryObject;

public class GoodsRetrievePropertyQueryObject extends QueryObject {
    public GoodsRetrievePropertyQueryObject(String currentPage, ModelAndView mv, String orderBy,
                                 String orderType) {
        super(currentPage, mv, orderBy, orderType);
    }

    public GoodsRetrievePropertyQueryObject() {
    }
}
