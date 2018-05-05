package com.javamalls.platform.service;

import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.SettleAccunts;

/**结算
 *                       
 * @Filename: ISettleAccountsService.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
public abstract interface ISettleAccountsService {

    public abstract boolean save(SettleAccunts settleAccunts);

    public abstract SettleAccunts getObjById(Long paramLong);

    public abstract boolean delete(Long id);

    public abstract boolean batchDelete(List<Long> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(SettleAccunts settleAccunts);

    public abstract List<SettleAccunts> query(String paramString, Map paramMap, int paramInt1,
                                              int paramInt2);
}
