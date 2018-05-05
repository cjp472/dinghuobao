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
import com.javamalls.platform.domain.SpareGoods;
import com.javamalls.platform.service.ISpareGoodsService;

@Service
@Transactional
public class SpareGoodsServiceImpl implements ISpareGoodsService {
    @Resource(name = "spareGoodsDAO")
    private IGenericDAO<SpareGoods> spareGoodsDao;

    public boolean save(SpareGoods spareGoods) {
        try {
            this.spareGoodsDao.save(spareGoods);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public SpareGoods getObjById(Long id) {
        SpareGoods spareGoods = (SpareGoods) this.spareGoodsDao.get(id);
        if (spareGoods != null) {
            return spareGoods;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.spareGoodsDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> spareGoodsIds) {
        for (Serializable id : spareGoodsIds) {
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
        GenericPageList pList = new GenericPageList(SpareGoods.class, query, params,
            this.spareGoodsDao);
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

    public boolean update(SpareGoods spareGoods) {
        try {
            this.spareGoodsDao.update(spareGoods);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<SpareGoods> query(String query, Map params, int begin, int max) {
        return this.spareGoodsDao.query(query, params, begin, max);
    }
}
