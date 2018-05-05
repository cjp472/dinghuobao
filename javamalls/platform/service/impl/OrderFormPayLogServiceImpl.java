package com.javamalls.platform.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javamalls.base.basedao.IGenericDAO;
import com.javamalls.base.query.GenericPageList;
import com.javamalls.base.query.PageObject;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.OrderFormPayLog;
import com.javamalls.platform.service.IOrderFormPayLogService;

@Service
@Transactional
public class OrderFormPayLogServiceImpl implements IOrderFormPayLogService {
    @Resource(name = "orderFormPayLogDAO")
    private IGenericDAO<OrderFormPayLog> orderFormPayLogDao;

    public boolean save(OrderFormPayLog orderFormPayLog) {
        try {
            this.orderFormPayLogDao.save(orderFormPayLog);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public OrderFormPayLog getObjById(Long id) {
        OrderFormPayLog orderFormPayLog = (OrderFormPayLog) this.orderFormPayLogDao.get(id);
        if (orderFormPayLog != null) {
            return orderFormPayLog;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.orderFormPayLogDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> orderFormPayLogIds) {
        for (Serializable id : orderFormPayLogIds) {
            delete((Long) id);
        }
        return true;
    }

    public IPageList list(IQueryObject properties) {
        if (properties == null) {
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(OrderFormPayLog.class, query, params,
            this.orderFormPayLogDao);
        if (properties != null) {
            PageObject pageObj = properties.getPageObj();
            if (pageObj != null) {
                pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj.getCurrentPage()
                    .intValue(), pageObj.getPageSize() == null ? 0 : pageObj.getPageSize()
                    .intValue());
            }
        } else {
            pList.doList(0, -1);
        }
        return pList;
    }

    public boolean update(OrderFormPayLog orderFormPayLog) {
        try {
            this.orderFormPayLogDao.update(orderFormPayLog);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<OrderFormPayLog> query(String query, Map params, int begin, int max) {
        return this.orderFormPayLogDao.query(query, params, begin, max);
    }
}
