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
import com.javamalls.platform.domain.PurchaseOrder;
import com.javamalls.platform.service.IPurchaseOrderService;

@Service
@Transactional
public class PurchaseOrderServiceImpl implements IPurchaseOrderService {
    @Resource(name = "purchaseOrderDAO")
    private IGenericDAO<PurchaseOrder> purchaseOrderDAO;

    public boolean save(PurchaseOrder purchaseOrder) {
        try {
            this.purchaseOrderDAO.save(purchaseOrder);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public PurchaseOrder getObjById(Long id) {
        PurchaseOrder purchaseOrder = (PurchaseOrder) this.purchaseOrderDAO.get(id);
        if (purchaseOrder != null) {
            return purchaseOrder;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.purchaseOrderDAO.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> purchaseOrderIds) {
        for (Serializable id : purchaseOrderIds) {
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
        GenericPageList pList = new GenericPageList(PurchaseOrder.class, query, params, this.purchaseOrderDAO);
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

    public boolean update(PurchaseOrder purchaseOrder) {
        try {
            this.purchaseOrderDAO.update(purchaseOrder);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<PurchaseOrder> query(String query, Map params, int begin, int max) {
        return this.purchaseOrderDAO.query(query, params, begin, max);
    }
}
