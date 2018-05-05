package com.javamalls.platform.service;

import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.OrderForm;
import com.javamalls.platform.domain.SettleLog;

/**结算
 *                       
 * @Filename: ISettleLogService.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
public abstract interface ISettleLogService {

    public abstract boolean save(SettleLog SettleLog);

    public abstract SettleLog getObjById(Long paramLong);

    public abstract boolean delete(Long id);

    public abstract boolean batchDelete(List<Long> paramList);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract boolean update(SettleLog SettleLog);

    public abstract List<SettleLog> query(String paramString, Map paramMap, int paramInt1,
                                          int paramInt2);

    public abstract List<Map<String, Object>> query(String string);

    public abstract boolean saveSettleLog(OrderForm order);
}
