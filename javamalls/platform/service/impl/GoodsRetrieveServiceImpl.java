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
import com.javamalls.platform.domain.GoodsBrand;
import com.javamalls.platform.domain.GoodsRetrieve;
import com.javamalls.platform.service.IGoodsBrandService;
import com.javamalls.platform.service.IGoodsRetrieveService;

@Service
@Transactional
public class GoodsRetrieveServiceImpl implements IGoodsRetrieveService {
    @Resource(name = "goodsRetrieveDAO")
    private IGenericDAO<GoodsRetrieve> goodsRetrieveDao;

    public boolean save(GoodsRetrieve goodsRetrieve) {
        try {
            this.goodsRetrieveDao.save(goodsRetrieve);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public GoodsRetrieve getObjById(Long id) {
        GoodsRetrieve goodsRetrieve = (GoodsRetrieve) this.goodsRetrieveDao.get(id);
        if (goodsRetrieve != null) {
            return goodsRetrieve;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.goodsRetrieveDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> goodsRetrieveIds) {
        for (Serializable id : goodsRetrieveIds) {
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
        GenericPageList pList = new GenericPageList(GoodsRetrieve.class, query, params,
            this.goodsRetrieveDao);
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

    public boolean update(GoodsRetrieve goodsRetrieve) {
        try {
            this.goodsRetrieveDao.update(goodsRetrieve);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<GoodsRetrieve> query(String query, Map params, int begin, int max) {
        return this.goodsRetrieveDao.query(query, params, begin, max);
    }
}
