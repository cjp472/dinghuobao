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
import com.javamalls.platform.domain.DeliveryLog;
import com.javamalls.platform.service.IDeliveryLogService;

@Service
@Transactional
public class DeliveryLogServiceImpl implements IDeliveryLogService {
    @Resource(name = "deliveryLogDAO")
    private IGenericDAO<DeliveryLog> deliveryLogDao;

    public boolean save(DeliveryLog deliveryLog) {
        try {
            this.deliveryLogDao.save(deliveryLog);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public DeliveryLog getObjById(Long id) {
        DeliveryLog deliveryLog = (DeliveryLog) this.deliveryLogDao.get(id);
        if (deliveryLog != null) {
            return deliveryLog;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.deliveryLogDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> deliveryLogIds) {
        for (Serializable id : deliveryLogIds) {
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
        GenericPageList pList = new GenericPageList(DeliveryLog.class, query, params,
            this.deliveryLogDao);
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

    public boolean update(DeliveryLog deliveryLog) {
        try {
            this.deliveryLogDao.update(deliveryLog);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<DeliveryLog> query(String query, Map params, int begin, int max) {
        return this.deliveryLogDao.query(query, params, begin, max);
    }
}
