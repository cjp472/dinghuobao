package com.javamalls.platform.domain.query;

import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.query.QueryObject;

public class GoodsModelQueryObject extends QueryObject {
    public GoodsModelQueryObject(String currentPage, ModelAndView mv, String orderBy,
                                 String orderType) {
        super(currentPage, mv, orderBy, orderType);
    }

    public GoodsModelQueryObject() {
    }
}
