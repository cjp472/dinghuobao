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
import com.javamalls.platform.domain.StoreVIP;
import com.javamalls.platform.service.IStoreVIPService;

@Service
@Transactional
public class StoreVIPServImpl implements IStoreVIPService {

    @Resource(name = "storeVIPDAO")
    private IGenericDAO<StoreVIP> storeVIPDAO;

    @Override
    public boolean save(StoreVIP storeVIP) {
        try {
            this.storeVIPDAO.save(storeVIP);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public StoreVIP getObjById(Long id) {
        StoreVIP storeClass = (StoreVIP) this.storeVIPDAO.get(id);
        if (storeClass != null) {
            return storeClass;
        }
        return null;
    }

    @Override
    public boolean delete(Long id) {
        try {
            this.storeVIPDAO.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean batchDelete(List<Serializable> storeClassIds) {
        for (Serializable id : storeClassIds) {
            delete((Long) id);
        }
        return true;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public IPageList list(IQueryObject properties) {
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(StoreVIP.class, query, params, this.storeVIPDAO);
        PageObject pageObj = properties.getPageObj();
        if (pageObj != null) {
            pList.doList(
                pageObj.getCurrentPage() == null ? 0 : pageObj.getCurrentPage().intValue(),
                pageObj.getPageSize() == null ? 0 : pageObj.getPageSize().intValue());
        }
        return pList;
    }

    @Override
    public boolean update(StoreVIP storeVIP) {
        try {
            this.storeVIPDAO.update(storeVIP);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<StoreVIP> query(String query, Map params, int begin, int max) {
        return this.storeVIPDAO.query(query, params, begin, max);
    }

}
