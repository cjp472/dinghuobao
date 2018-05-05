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
import com.javamalls.platform.domain.SnsAttention;
import com.javamalls.platform.service.ISnsAttentionService;

@Service
@Transactional
public class SnsAttentionServiceImpl implements ISnsAttentionService {
    @Resource(name = "homeAttentionDAO")
    private IGenericDAO<SnsAttention> homeAttentionDao;

    public boolean save(SnsAttention homeAttention) {
        try {
            this.homeAttentionDao.save(homeAttention);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public SnsAttention getObjById(Long id) {
        SnsAttention homeAttention = (SnsAttention) this.homeAttentionDao.get(id);
        if (homeAttention != null) {
            return homeAttention;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.homeAttentionDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> homeAttentionIds) {
        for (Serializable id : homeAttentionIds) {
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
        GenericPageList pList = new GenericPageList(SnsAttention.class, query, params,
            this.homeAttentionDao);
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

    public boolean update(SnsAttention homeAttention) {
        try {
            this.homeAttentionDao.update(homeAttention);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<SnsAttention> query(String query, Map params, int begin, int max) {
        return this.homeAttentionDao.query(query, params, begin, max);
    }
}
