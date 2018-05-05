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
import com.javamalls.platform.domain.StrategyGoodsItem;
import com.javamalls.platform.service.IStrategyGoodsItemService;

@Service
@Transactional
public class StrategyGoodsItemServiceImpl implements IStrategyGoodsItemService {
    @Resource(name = "strategyGoodsItemDAO")
    private IGenericDAO<StrategyGoodsItem> strategyGoodsItemDAO;

    public boolean save(StrategyGoodsItem strategyGoodsItem) {
        try {
            this.strategyGoodsItemDAO.save(strategyGoodsItem);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public StrategyGoodsItem getObjById(Long id) {
    	StrategyGoodsItem strategyGoodsItem = (StrategyGoodsItem) this.strategyGoodsItemDAO.get(id);
        if (strategyGoodsItem != null) {
            return strategyGoodsItem;
        }
        return null;
    }
    /**
     * 物理删除
     */
    public boolean delete(Long id) {
        try {
          
            this.strategyGoodsItemDAO.remove(id);
         
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    

    public boolean batchDelete(List<Serializable> strategyGoodsItemIds) {
        for (Serializable id : strategyGoodsItemIds) {
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
        GenericPageList pList = new GenericPageList(StrategyGoodsItem.class, query, params, this.strategyGoodsItemDAO);
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

    public boolean update(StrategyGoodsItem strategyGoodsItem) {
        try {
            this.strategyGoodsItemDAO.update(strategyGoodsItem);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<StrategyGoodsItem> query(String query, Map params, int begin, int max) {
        return this.strategyGoodsItemDAO.query(query, params, begin, max);
    }

    public StrategyGoodsItem getObjByProperty(String propertyName, Object value) {
        return (StrategyGoodsItem) this.strategyGoodsItemDAO.getBy(propertyName, value);
    }

	@Override
	public IPageList nolastlist(IQueryObject properties) {
        if (properties == null) {
            return null;
        }
        String query = properties.getQuery();
        Map<String, Object> params = properties.getParameters();
        GenericPageList pList = new GenericPageList(StrategyGoodsItem.class, query, params, this.strategyGoodsItemDAO);
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
