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
import com.javamalls.platform.domain.ArticleClass;
import com.javamalls.platform.service.IArticleClassService;

@Service
@Transactional
public class ArticleClassServiceImpl implements IArticleClassService {
    @Resource(name = "articleClassDAO")
    private IGenericDAO<ArticleClass> articleClassDao;

    public boolean save(ArticleClass articleClass) {
        try {
            this.articleClassDao.save(articleClass);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArticleClass getObjById(Long id) {
        ArticleClass articleClass = (ArticleClass) this.articleClassDao.get(id);
        if (articleClass != null) {
            return articleClass;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.articleClassDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> articleClassIds) {
        for (Serializable id : articleClassIds) {
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
        GenericPageList pList = new GenericPageList(ArticleClass.class, query, params,
            this.articleClassDao);
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

    public boolean update(ArticleClass articleClass) {
        try {
            this.articleClassDao.update(articleClass);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<ArticleClass> query(String query, Map params, int begin, int max) {
        return this.articleClassDao.query(query, params, begin, max);
    }

    public ArticleClass getObjByPropertyName(String propertyName, Object value) {
        return (ArticleClass) this.articleClassDao.getBy(propertyName, value);
    }
}
