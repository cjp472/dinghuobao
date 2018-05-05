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
import com.javamalls.platform.domain.DeliveryGoods;
import com.javamalls.platform.service.IDeliveryGoodsService;

@Service
@Transactional
public class DeliveryGoodsServiceImpl implements IDeliveryGoodsService {
    @Resource(name = "deliveryGoodsDAO")
    private IGenericDAO<DeliveryGoods> deliveryGoodsDao;

    public boolean save(DeliveryGoods deliveryGoods) {
        try {
            this.deliveryGoodsDao.save(deliveryGoods);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public DeliveryGoods getObjById(Long id) {
        DeliveryGoods deliveryGoods = (DeliveryGoods) this.deliveryGoodsDao.get(id);
        if (deliveryGoods != null) {
            return deliveryGoods;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.deliveryGoodsDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> deliveryGoodsIds) {
        for (Serializable id : deliveryGoodsIds) {
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
        GenericPageList pList = new GenericPageList(DeliveryGoods.class, query, params,
            this.deliveryGoodsDao);
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

    public boolean update(DeliveryGoods deliveryGoods) {
        try {
            this.deliveryGoodsDao.update(deliveryGoods);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<DeliveryGoods> query(String query, Map params, int begin, int max) {
        return this.deliveryGoodsDao.query(query, params, begin, max);
    }
}
