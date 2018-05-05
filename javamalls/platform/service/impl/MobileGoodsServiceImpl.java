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
import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.domain.Favorite;
import com.javamalls.platform.domain.MobileGoods;
import com.javamalls.platform.service.IMobileGoodsService;

@Service
@Transactional
public class MobileGoodsServiceImpl implements IMobileGoodsService {
    @Resource(name = "mobileGoodsDAO")
    private IGenericDAO<MobileGoods> mobileGoodsDAO;

    public boolean save(MobileGoods goods) {
        try {
            this.mobileGoodsDAO.save(goods);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public MobileGoods getObjById(Long id) {
        MobileGoods goods = (MobileGoods) this.mobileGoodsDAO.get(id);
        if (goods != null) {
            return goods;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            // 由物理删除改为逻辑删除
            //this.goodsDao.remove(id);
            MobileGoods goods = (MobileGoods) this.mobileGoodsDAO.get(id);
            if (goods != null) {
                goods.setDisabled(true);
                this.mobileGoodsDAO.update(goods);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> goodsIds) {
        for (Serializable id : goodsIds) {
            delete((Long) id);
        }
        return true;
    }

    public IPageList list(IQueryObject properties) {
        if (properties == null) {
            return null;
        }
        String query = properties.getQuery();
        Map<String, Object> params = properties.getParameters();
        GenericPageList pList = new GenericPageList(MobileGoods.class, query, params,
            this.mobileGoodsDAO);
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
    public IPageList noLastList(IQueryObject properties) {
        if (properties == null) {
            return null;
        }
        String query = properties.getQuery();
        Map<String, Object> params = properties.getParameters();
        GenericPageList pList = new GenericPageList(MobileGoods.class, query, params,
            this.mobileGoodsDAO);
        if (properties != null) {
            PageObject pageObj = properties.getPageObj();
            if (pageObj != null) {
                pList.doNolastList(pageObj.getCurrentPage() == null ? 0 : pageObj.getCurrentPage()
                    .intValue(), pageObj.getPageSize() == null ? 0 : pageObj.getPageSize()
                    .intValue());
            }
        } else {
            pList.doNolastList(0, -1);
        }
        return pList;
    }
    
    public boolean update(MobileGoods goods) {
        try {
            this.mobileGoodsDAO.update(goods);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<MobileGoods> query(String query, Map params, int begin, int max) {
        return this.mobileGoodsDAO.query(query, params, begin, max);
    }

    public MobileGoods getObjByProperty(String propertyName, Object value) {
        return (MobileGoods) this.mobileGoodsDAO.getBy(propertyName, value);
    }

    @Override
    public boolean remove(String ids) {

        try {
            String[] idlist = ids.split(",");
            for (String id : idlist) {
                this.mobileGoodsDAO.remove(CommUtil.null2Long(id));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
