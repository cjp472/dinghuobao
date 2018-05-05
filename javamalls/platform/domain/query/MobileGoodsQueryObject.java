package com.javamalls.platform.domain.query;

import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.query.QueryObject;

public class MobileGoodsQueryObject extends QueryObject {
    public MobileGoodsQueryObject(String currentPage, ModelAndView mv, String orderBy, String orderType) {
        super(currentPage, mv, orderBy, orderType);
    }

    public MobileGoodsQueryObject() {
    }
}
