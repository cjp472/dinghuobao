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
import com.javamalls.platform.domain.Warehouse;
import com.javamalls.platform.service.IWarehouseService;

@Service
@Transactional
public class WarehouseServiceImpl implements IWarehouseService {
    @Resource(name = "warehouseDAO")
    private IGenericDAO<Warehouse> warehouseDAO;

    public boolean save(Warehouse warehouse) {
        try {
            this.warehouseDAO.save(warehouse);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Warehouse getObjById(Long id) {
    	Warehouse warehouse = (Warehouse) this.warehouseDAO.get(id);
        if (warehouse != null) {
            return warehouse;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.warehouseDAO.remove(id);
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
        GenericPageList pList = new GenericPageList(Warehouse.class, query, params, this.warehouseDAO);
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

    public boolean update(Warehouse warehouse) {
        try {
            this.warehouseDAO.update(warehouse);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Warehouse> query(String query, Map params, int begin, int max) {
        return this.warehouseDAO.query(query, params, begin, max);
    }
}
