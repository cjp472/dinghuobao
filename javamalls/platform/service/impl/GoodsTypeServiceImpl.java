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
import com.javamalls.platform.domain.GoodsType;
import com.javamalls.platform.service.IGoodsTypeService;

@Service
@Transactional
public class GoodsTypeServiceImpl implements IGoodsTypeService {
    @Resource(name = "goodsTypeDAO")
    private IGenericDAO<GoodsType> goodsTypeDao;

    public boolean save(GoodsType goodsType) {
        try {
            this.goodsTypeDao.save(goodsType);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public GoodsType getObjById(Long id) {
        GoodsType goodsType = (GoodsType) this.goodsTypeDao.get(id);
        if (goodsType != null) {
            return goodsType;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.goodsTypeDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> goodsTypeIds) {
        for (Serializable id : goodsTypeIds) {
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
        GenericPageList pList = new GenericPageList(GoodsType.class, query, params,
            this.goodsTypeDao);
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

    public boolean update(GoodsType goodsType) {
        try {
            this.goodsTypeDao.update(goodsType);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<GoodsType> query(String query, Map params, int begin, int max) {
        return this.goodsTypeDao.query(query, params, begin, max);
    }
}
