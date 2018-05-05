package com.javamalls.platform.service.impl;

import java.io.Serializable;
import java.util.HashMap;
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
import com.javamalls.platform.domain.UserStrategy;
import com.javamalls.platform.service.IUserStrategyService;

@Service
@Transactional
public class UserStrategyServiceImpl implements IUserStrategyService {
    @Resource(name = "userStrategyDAO")
    private IGenericDAO<UserStrategy> userStrategyDao;

    public boolean save(UserStrategy userStrategy) {
        try {
            this.userStrategyDao.save(userStrategy);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public UserStrategy getObjById(Long id) {
    	UserStrategy userStrategy = (UserStrategy) this.userStrategyDao.get(id);
        if (userStrategy != null) {
            return userStrategy;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.userStrategyDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> userStrategyIds) {
        for (Serializable id : userStrategyIds) {
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
        GenericPageList pList = new GenericPageList(UserStrategy.class, query, params, this.userStrategyDao);
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

    public boolean update(UserStrategy userStrategy) {
        try {
            this.userStrategyDao.update(userStrategy);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<UserStrategy> query(String query, Map params, int begin, int max) {
        return this.userStrategyDao.query(query, params, begin, max);
    }
    public  UserStrategy getUserStrategyByUserIdAndStoreId(Long userId,Long storeId){
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("userId", userId);
    	map.put("storeId", storeId);
    	List<UserStrategy> list = this.query("select obj from UserStrategy obj where obj.disabled = 0" +
    			" and obj.user.id=:userId and obj.store_id=:storeId ", map, -1, -1);
    	if(list!=null&&list.size()>0){
    		return list.get(0);
    	}
    	return null;
    }
    public  UserStrategy getObjByProperty(String propertyName, Object value){
       return (UserStrategy) this.userStrategyDao.getBy(propertyName, value);
    }
}
