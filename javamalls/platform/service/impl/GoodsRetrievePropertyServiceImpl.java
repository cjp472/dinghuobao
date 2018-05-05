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
import com.javamalls.platform.domain.GoodsRetrieve;
import com.javamalls.platform.domain.GoodsRetrieveProperty;
import com.javamalls.platform.service.IGoodsRetrievePropertyService;

@Service
@Transactional
public class GoodsRetrievePropertyServiceImpl implements IGoodsRetrievePropertyService {
    @Resource(name = "goodsRetrievePropertyDAO")
    private IGenericDAO<GoodsRetrieveProperty> goodsRetrievePropertyDao;

    public boolean save(GoodsRetrieveProperty goodsRetrieveProperty) {
        try {
            this.goodsRetrievePropertyDao.save(goodsRetrieveProperty);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public GoodsRetrieveProperty getObjById(Long id) {
        GoodsRetrieveProperty goodsRetrieveProperty = (GoodsRetrieveProperty) this.goodsRetrievePropertyDao.get(id);
        if (goodsRetrieveProperty != null) {
            return goodsRetrieveProperty;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.goodsRetrievePropertyDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> goodsRetrievePropertyIds) {
        for (Serializable id : goodsRetrievePropertyIds) {
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
        GenericPageList pList = new GenericPageList(GoodsRetrieveProperty.class, query, params,
            this.goodsRetrievePropertyDao);
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

    public boolean update(GoodsRetrieveProperty goodsRetrieveProperty) {
        try {
            this.goodsRetrievePropertyDao.update(goodsRetrieveProperty);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<GoodsRetrieveProperty> query(String query, Map params, int begin, int max) {
        return this.goodsRetrievePropertyDao.query(query, params, begin, max);
    }
}
