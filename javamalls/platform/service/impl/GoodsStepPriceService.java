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
import com.javamalls.platform.domain.GoodsStepPrice;
import com.javamalls.platform.service.IGoodsStepPriceService;

@Service
@Transactional
public class GoodsStepPriceService implements IGoodsStepPriceService {
    @Resource(name = "goodsStepPriceDAO")
    private IGenericDAO<GoodsStepPrice> goodsStepPriceDAO;

    public boolean save(GoodsStepPrice goodsStepPrice) {
        try {
            this.goodsStepPriceDAO.save(goodsStepPrice);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public GoodsStepPrice getObjById(Long id) {
    	GoodsStepPrice goodsStepPrice = (GoodsStepPrice) this.goodsStepPriceDAO.get(id);
        if (goodsStepPrice != null) {
            return goodsStepPrice;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.goodsStepPriceDAO.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> goodsStepPriceIds) {
        for (Serializable id : goodsStepPriceIds) {
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
        GenericPageList pList = new GenericPageList(GoodsStepPrice.class, query, params, this.goodsStepPriceDAO);
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

    public boolean update(GoodsStepPrice goodsStepPrice) {
        try {
            this.goodsStepPriceDAO.update(goodsStepPrice);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<GoodsStepPrice> query(String query, Map params, int begin, int max) {
        return this.goodsStepPriceDAO.query(query, params, begin, max);
    }
}
