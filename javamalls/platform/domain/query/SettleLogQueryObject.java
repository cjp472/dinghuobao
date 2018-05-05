package com.javamalls.platform.domain.query;

import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.query.QueryObject;

/**
 * 结帐单
 * 
 * @Filename: SettleLogQueryObject.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 * 
 */
public class SettleLogQueryObject extends QueryObject {
    public SettleLogQueryObject(String currentPage, ModelAndView mv, String orderBy,
                                String orderType) {
        super(currentPage, mv, orderBy, orderType);
    }

    public SettleLogQueryObject() {
    }
}
