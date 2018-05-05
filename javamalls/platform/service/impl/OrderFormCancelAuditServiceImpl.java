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
import com.javamalls.platform.domain.OrderFormCancelAudit;
import com.javamalls.platform.service.IOrderFormCancelAuditService;

@Service
@Transactional
public class OrderFormCancelAuditServiceImpl implements IOrderFormCancelAuditService {
    @Resource(name = "orderFormCancelAuditDAO")
    private IGenericDAO<OrderFormCancelAudit> orderFormCancelAuditDao;

    public boolean save(OrderFormCancelAudit orderFormCancelAudit) {
        try {
            this.orderFormCancelAuditDao.save(orderFormCancelAudit);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public OrderFormCancelAudit getObjById(Long id) {
        OrderFormCancelAudit orderFormCancelAudit = (OrderFormCancelAudit) this.orderFormCancelAuditDao.get(id);
        if (orderFormCancelAudit != null) {
            return orderFormCancelAudit;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.orderFormCancelAuditDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> orderFormCancelAuditIds) {
        for (Serializable id : orderFormCancelAuditIds) {
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
        GenericPageList pList = new GenericPageList(OrderFormCancelAudit.class, query, params,
            this.orderFormCancelAuditDao);
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

    public boolean update(OrderFormCancelAudit orderFormCancelAudit) {
        try {
            this.orderFormCancelAuditDao.update(orderFormCancelAudit);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<OrderFormCancelAudit> query(String query, Map params, int begin, int max) {
        return this.orderFormCancelAuditDao.query(query, params, begin, max);
    }
}
