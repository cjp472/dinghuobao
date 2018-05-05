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
import com.javamalls.platform.domain.StoreStat;
import com.javamalls.platform.service.IStoreStatService;

@Service
@Transactional
public class StoreStatServiceImpl implements IStoreStatService {
    @Resource(name = "storeStatDAO")
    private IGenericDAO<StoreStat> storeStatDao;

    public boolean save(StoreStat storeStat) {
        try {
            this.storeStatDao.save(storeStat);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public StoreStat getObjById(Long id) {
        StoreStat storeStat = (StoreStat) this.storeStatDao.get(id);
        if (storeStat != null) {
            return storeStat;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.storeStatDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> storeStatIds) {
        for (Serializable id : storeStatIds) {
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
        GenericPageList pList = new GenericPageList(StoreStat.class, query, params,
            this.storeStatDao);
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

    public boolean update(StoreStat storeStat) {
        try {
            this.storeStatDao.update(storeStat);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<StoreStat> query(String query, Map params, int begin, int max) {
        return this.storeStatDao.query(query, params, begin, max);
    }
}
