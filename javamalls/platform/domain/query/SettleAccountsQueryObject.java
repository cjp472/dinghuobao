package com.javamalls.platform.domain.query;

import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.query.QueryObject;

/**结算设置
 *                       
 * @Filename: SettleAccountsQueryObject.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
public class SettleAccountsQueryObject extends QueryObject {
    public SettleAccountsQueryObject(String currentPage, ModelAndView mv, String orderBy,
                                     String orderType) {
        super(currentPage, mv, orderBy, orderType);
    }

    public SettleAccountsQueryObject() {
    }
}
