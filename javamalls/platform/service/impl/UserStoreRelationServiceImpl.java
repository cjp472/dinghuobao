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
import com.javamalls.platform.domain.UserStoreRelation;
import com.javamalls.platform.service.IUserStoreRelationService;

@Service
@Transactional
public class UserStoreRelationServiceImpl implements IUserStoreRelationService {
    @Resource(name = "userStoreRelationDAO")
    private IGenericDAO<UserStoreRelation> userStoreRelationDAO;

    public boolean save(UserStoreRelation advert) {
        try {
            this.userStoreRelationDAO.save(advert);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public UserStoreRelation getObjById(Long id) {
        UserStoreRelation UserStoreRelation = (UserStoreRelation) this.userStoreRelationDAO.get(id);
        if (UserStoreRelation != null) {
            return UserStoreRelation;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.userStoreRelationDAO.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> advertIds) {
        for (Serializable id : advertIds) {
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
        GenericPageList pList = new GenericPageList(UserStoreRelation.class, query, params,
            this.userStoreRelationDAO);
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

    public boolean update(UserStoreRelation advert) {
        try {
            this.userStoreRelationDAO.update(advert);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<UserStoreRelation> query(String query, Map params, int begin, int max) {
        return this.userStoreRelationDAO.query(query, params, begin, max);
    }

    @Override
    public Long queryCount(String paramString, Map paramMap) {
        return this.userStoreRelationDAO.queryCount(paramString, paramMap);
    }

    @Override
    public IPageList noLastList(IQueryObject properties) {
        if (properties == null) {
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(UserStoreRelation.class, query, params,
            this.userStoreRelationDAO);
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
