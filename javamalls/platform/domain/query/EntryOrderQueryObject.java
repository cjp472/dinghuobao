package com.javamalls.platform.domain.query;

import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.query.QueryObject;

public class EntryOrderQueryObject extends QueryObject {
    public EntryOrderQueryObject() {
    }

    public EntryOrderQueryObject(String currentPage, ModelAndView mv, String orderBy, String orderType) {
        super(currentPage, mv, orderBy, orderType);
    }
}
