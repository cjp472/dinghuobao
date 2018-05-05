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
import com.javamalls.platform.domain.Consult;
import com.javamalls.platform.service.IConsultService;

@Service
@Transactional
public class ConsultServiceImpl implements IConsultService {
    @Resource(name = "consultDAO")
    private IGenericDAO<Consult> consultDao;

    public boolean save(Consult consult) {
        try {
            this.consultDao.save(consult);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Consult getObjById(Long id) {
        Consult consult = (Consult) this.consultDao.get(id);
        if (consult != null) {
            return consult;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.consultDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> consultIds) {
        for (Serializable id : consultIds) {
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
        GenericPageList pList = new GenericPageList(Consult.class, query, params, this.consultDao);
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

    public boolean update(Consult consult) {
        try {
            this.consultDao.update(consult);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Consult> query(String query, Map params, int begin, int max) {
        return this.consultDao.query(query, params, begin, max);
    }

	@Override
	public IPageList nolastlist(IQueryObject properties) {
        if (properties == null) {
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(Consult.class, query, params, this.consultDao);
        if (properties != null) {
            PageObject pageObj = properties.getPageObj();
            if (pageObj != null) {
                pList.doNolastList(pageObj.getCurrentPage() == null ? 0 : pageObj.getCurrentPage()
                    .intValue(), pageObj.getPageSize() == null ? 0 : pageObj.getPageSize()
                    .intValue());
            }
        } else {
            pList.doNolastList(0, -1);
        }
        return pList;
    }
}
