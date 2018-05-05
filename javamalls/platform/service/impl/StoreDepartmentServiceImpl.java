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
import com.javamalls.platform.domain.StoreDepartment;
import com.javamalls.platform.service.IStoreDepartmentService;

@Service
@Transactional
public class StoreDepartmentServiceImpl implements IStoreDepartmentService {
    @Resource(name = "storeDepartmentDAO")
    private IGenericDAO<StoreDepartment> storeDepartmentDao;

    public boolean save(StoreDepartment storeDepartment) {
        try {
            this.storeDepartmentDao.save(storeDepartment);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public StoreDepartment getObjById(Long id) {
    	StoreDepartment storeDepartment = (StoreDepartment) this.storeDepartmentDao.get(id);
        if (storeDepartment != null) {
            return storeDepartment;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.storeDepartmentDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> storeDepartmentIds) {
        for (Serializable id : storeDepartmentIds) {
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
        GenericPageList pList = new GenericPageList(StoreDepartment.class, query, params,
            this.storeDepartmentDao);
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

    public boolean update(StoreDepartment storeDepartment) {
        try {
            this.storeDepartmentDao.update(storeDepartment);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<StoreDepartment> query(String query, Map params, int begin, int max) {
        return this.storeDepartmentDao.query(query, params, begin, max);
    }
}
