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
import com.javamalls.platform.domain.Supplier;
import com.javamalls.platform.service.ISupplierService;

@Service
@Transactional
public class SupplierServiceImpl implements ISupplierService {
    @Resource(name = "supplierDAO")
    private IGenericDAO<Supplier> supplierDAO;

    public boolean save(Supplier supplier) {
        try {
            this.supplierDAO.save(supplier);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Supplier getObjById(Long id) {
    	Supplier supplier = (Supplier) this.supplierDAO.get(id);
        if (supplier != null) {
            return supplier;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.supplierDAO.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> supplierIds) {
        for (Serializable id : supplierIds) {
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
        GenericPageList pList = new GenericPageList(Supplier.class, query, params, this.supplierDAO);
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

    public boolean update(Supplier supplier) {
        try {
            this.supplierDAO.update(supplier);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Supplier> query(String query, Map params, int begin, int max) {
        return this.supplierDAO.query(query, params, begin, max);
    }
}