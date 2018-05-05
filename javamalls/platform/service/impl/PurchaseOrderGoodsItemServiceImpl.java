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
import com.javamalls.platform.domain.PurchaseOrderGoodsItem;
import com.javamalls.platform.service.IPurchaseGoodsItemService;

@Service
@Transactional
public class PurchaseOrderGoodsItemServiceImpl implements IPurchaseGoodsItemService {
    @Resource(name = "purchaseOrderGoodsItemDAO")
    private IGenericDAO<PurchaseOrderGoodsItem> purchaseOrderGoodsItemDAO;

    public boolean save(PurchaseOrderGoodsItem purchaseOrderGoodsItem) {
        try {
            this.purchaseOrderGoodsItemDAO.save(purchaseOrderGoodsItem);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public PurchaseOrderGoodsItem getObjById(Long id) {
    	PurchaseOrderGoodsItem purchaseOrderGoodsItem = (PurchaseOrderGoodsItem) this.purchaseOrderGoodsItemDAO.get(id);
        if (purchaseOrderGoodsItem != null) {
            return purchaseOrderGoodsItem;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.purchaseOrderGoodsItemDAO.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> purchaseOrderGoodsItemIds) {
        for (Serializable id : purchaseOrderGoodsItemIds) {
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
        GenericPageList pList = new GenericPageList(PurchaseOrderGoodsItem.class, query, params, this.purchaseOrderGoodsItemDAO);
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

    public boolean update(PurchaseOrderGoodsItem goldLog) {
        try {
            this.purchaseOrderGoodsItemDAO.update(goldLog);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<PurchaseOrderGoodsItem> query(String query, Map params, int begin, int max) {
        return this.purchaseOrderGoodsItemDAO.query(query, params, begin, max);
    }
}
