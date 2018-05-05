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
import com.javamalls.platform.domain.WarehouseGoodsItem;
import com.javamalls.platform.service.IWarehouseGoodsItemService;

@Service
@Transactional
public class WarehouseGoodsItemServiceImpl implements IWarehouseGoodsItemService {
    @Resource(name = "warehouseGoodsItemDAO")
    private IGenericDAO<WarehouseGoodsItem> warehouseGoodsItemDAO;

    public boolean save(WarehouseGoodsItem warehouseGoodsItem) {
        try {
            this.warehouseGoodsItemDAO.save(warehouseGoodsItem);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public WarehouseGoodsItem getObjById(Long id) {
    	WarehouseGoodsItem warehouseGoodsItem = (WarehouseGoodsItem) this.warehouseGoodsItemDAO.get(id);
        if (warehouseGoodsItem != null) {
            return warehouseGoodsItem;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.warehouseGoodsItemDAO.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> warehouseIds) {
        for (Serializable id : warehouseIds) {
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
        GenericPageList pList = new GenericPageList(WarehouseGoodsItem.class, query, params, this.warehouseGoodsItemDAO);
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

    public boolean update(WarehouseGoodsItem warehouseGoodsItem) {
        try {
            this.warehouseGoodsItemDAO.update(warehouseGoodsItem);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<WarehouseGoodsItem> query(String query, Map params, int begin, int max) {
        return this.warehouseGoodsItemDAO.query(query, params, begin, max);
    }
}
