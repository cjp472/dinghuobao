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
import com.javamalls.platform.domain.BargainGoods;
import com.javamalls.platform.service.IBargainGoodsService;

/**折扣商品
 *                       
 * @Filename: BargainGoodsServiceImpl.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Service
@Transactional
public class BargainGoodsServiceImpl implements IBargainGoodsService {
    @Resource(name = "bargainGoodsDAO")
    private IGenericDAO<BargainGoods> bargainGoodsDao;

    public boolean save(BargainGoods bargainGoods) {
        try {
            this.bargainGoodsDao.save(bargainGoods);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public BargainGoods getObjById(Long id) {
        BargainGoods bargainGoods = (BargainGoods) this.bargainGoodsDao.get(id);
        if (bargainGoods != null) {
            return bargainGoods;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.bargainGoodsDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> bargainGoodsIds) {
        for (Serializable id : bargainGoodsIds) {
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
        GenericPageList pList = new GenericPageList(BargainGoods.class, query, params,
            this.bargainGoodsDao);
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

    public boolean update(BargainGoods bargainGoods) {
        try {
            this.bargainGoodsDao.update(bargainGoods);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<BargainGoods> query(String query, Map params, int begin, int max) {
        return this.bargainGoodsDao.query(query, params, begin, max);
    }
}
