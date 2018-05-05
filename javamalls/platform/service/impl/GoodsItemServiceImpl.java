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
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.GoodsItem;
import com.javamalls.platform.service.IGoodsItemService;
import com.javamalls.platform.service.IGoodsService;

@Service
@Transactional
public class GoodsItemServiceImpl implements IGoodsItemService {
    @Resource(name = "goodsItemDAO")
    private IGenericDAO<GoodsItem> goodsItemDao;

    public boolean save(GoodsItem goodsItem) {
        try {
            this.goodsItemDao.save(goodsItem);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public GoodsItem getObjById(Long id) {
    	GoodsItem goodsItem = (GoodsItem) this.goodsItemDao.get(id);
        if (goodsItem != null) {
            return goodsItem;
        }
        return null;
    }
    /**
     * 物理删除
     */
    public boolean delete_wuli(Long id) {
        try {
          
            this.goodsItemDao.remove(id);
         
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean delete(Long id) {
        try {
            // 由物理删除改为逻辑删除
            //this.goodsDao.remove(id);
        	GoodsItem goodsItem = (GoodsItem) this.goodsItemDao.get(id);
            if (goodsItem != null) {
            	goodsItem.setDisabled(true);
                this.goodsItemDao.update(goodsItem);
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
        GenericPageList pList = new GenericPageList(GoodsItem.class, query, params, this.goodsItemDao);
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

    public boolean update(GoodsItem goodsItem) {
        try {
            this.goodsItemDao.update(goodsItem);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<GoodsItem> query(String query, Map params, int begin, int max) {
        return this.goodsItemDao.query(query, params, begin, max);
    }

    public GoodsItem getObjByProperty(String propertyName, Object value) {
        return (GoodsItem) this.goodsItemDao.getBy(propertyName, value);
    }

	@Override
	public IPageList nolastlist(IQueryObject properties) {
        if (properties == null) {
            return null;
        }
        String query = properties.getQuery();
        Map<String, Object> params = properties.getParameters();
        GenericPageList pList = new GenericPageList(GoodsItem.class, query, params, this.goodsItemDao);
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
}
