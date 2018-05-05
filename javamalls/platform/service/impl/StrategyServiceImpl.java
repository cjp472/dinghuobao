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
import com.javamalls.platform.domain.Strategy;
import com.javamalls.platform.domain.StrategyGoodsItem;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.service.IStrategyService;

@Service
@Transactional
public class StrategyServiceImpl implements IStrategyService {
    @Resource(name = "strategyDAO")
    private IGenericDAO<Strategy> strategyDAO;
    @Resource(name = "strategyGoodsItemDAO")
    private IGenericDAO<StrategyGoodsItem> strategyGoodsItemDAO;
    
    @Resource(name = "userDAO")
    private IGenericDAO<User> userDAO;

    public boolean save(Strategy strategy) {
        try {
            this.strategyDAO.save(strategy);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Strategy getObjById(Long id) {
    	Strategy strategy = (Strategy) this.strategyDAO.get(id);
        if (strategy != null) {
            return strategy;
        }
        return null;
    }
    /**
     * 物理删除
     */
    public boolean delete(Long id) {
        try {
        	//1将有此策略的客户的策略之为
        	//改为逻辑删除
           // this.strategyDAO.remove(id);
        	Strategy strategy = (Strategy)this.strategyDAO.get(id);
        	if(strategy!=null){
        		//清除策略商品
            	this.strategyGoodsItemDAO.executeNativeSQL("delete from jm_strategy_goods_item where strategy_id="+id);
        		strategy.setDisabled(true);
        		//将制定策略的用户去掉策略
        		List<User> user_list = strategy.getUser_list();
        		if(user_list!=null&&user_list.size()>0){
        			for (User user : user_list) {
						user.setStrategy(null);
						this.userDAO.update(user);
					}
        		}
        		this.strategyDAO.update(strategy);
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
        GenericPageList pList = new GenericPageList(Strategy.class, query, params, this.strategyDAO);
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

    public boolean update(Strategy strategy) {
        try {
            this.strategyDAO.update(strategy);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Strategy> query(String query, Map params, int begin, int max) {
        return this.strategyDAO.query(query, params, begin, max);
    }

    public Strategy getObjByProperty(String propertyName, Object value) {
        return (Strategy) this.strategyDAO.getBy(propertyName, value);
    }

	@Override
	public IPageList nolastlist(IQueryObject properties) {
        if (properties == null) {
            return null;
        }
        String query = properties.getQuery();
        Map<String, Object> params = properties.getParameters();
        GenericPageList pList = new GenericPageList(Strategy.class, query, params, this.strategyDAO);
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
