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
import com.javamalls.platform.domain.IntegralGoods;
import com.javamalls.platform.service.IIntegralGoodsService;

@Service
@Transactional
public class IntegralGoodsServiceImpl implements IIntegralGoodsService {
    @Resource(name = "integralGoodsDAO")
    private IGenericDAO<IntegralGoods> integralGoodsDao;

    public boolean save(IntegralGoods integralGoods) {
        try {
            this.integralGoodsDao.save(integralGoods);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public IntegralGoods getObjById(Long id) {
        IntegralGoods integralGoods = (IntegralGoods) this.integralGoodsDao.get(id);
        if (integralGoods != null) {
            return integralGoods;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.integralGoodsDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> integralGoodsIds) {
        for (Serializable id : integralGoodsIds) {
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
        GenericPageList pList = new GenericPageList(IntegralGoods.class, query, params,
            this.integralGoodsDao);
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

    public boolean update(IntegralGoods integralGoods) {
        try {
            this.integralGoodsDao.update(integralGoods);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<IntegralGoods> query(String query, Map params, int begin, int max) {
        return this.integralGoodsDao.query(query, params, begin, max);
    }
}
