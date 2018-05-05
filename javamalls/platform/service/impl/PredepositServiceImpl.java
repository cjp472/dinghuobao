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
import com.javamalls.platform.domain.Predeposit;
import com.javamalls.platform.service.IPredepositService;

@Service
@Transactional
public class PredepositServiceImpl implements IPredepositService {
    @Resource(name = "predepositDAO")
    private IGenericDAO<Predeposit> predepositDao;

    public boolean save(Predeposit predeposit) {
        try {
            this.predepositDao.save(predeposit);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Predeposit getObjById(Long id) {
        Predeposit predeposit = (Predeposit) this.predepositDao.get(id);
        if (predeposit != null) {
            return predeposit;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.predepositDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> predepositIds) {
        for (Serializable id : predepositIds) {
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
        GenericPageList pList = new GenericPageList(Predeposit.class, query, params,
            this.predepositDao);
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

    public boolean update(Predeposit predeposit) {
        try {
            this.predepositDao.update(predeposit);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Predeposit> query(String query, Map params, int begin, int max) {
        return this.predepositDao.query(query, params, begin, max);
    }
}
