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
import com.javamalls.platform.domain.MobileGoods;
import com.javamalls.platform.domain.Role;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.service.IStoreService;

@Service
@Transactional
public class StoreServiceImpl implements IStoreService {
	@Resource(name = "roleDAO")
    private IGenericDAO<Role> roleDAO;
    @Resource(name = "storeDAO")
    private IGenericDAO<Store> storeDao;
    @Resource(name = "userDAO")
    private IGenericDAO<User> userDAO;

    public boolean save(Store store) {
        try {
            this.storeDao.save(store);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Store getObjById(Long id) {
        Store store = (Store) this.storeDao.get(id);
        if (store != null) {
            return store;
        }
        return null;
    }
    /**
     * 删除店铺
     */
    public boolean delete(Long id) {
        try {
        	
        	
        	Store store = (Store) this.storeDao.get(id);
        	User user = (User) this.userDAO.get(store.getUser().getId());
        	String query = "select obj from Role obj where obj.type =:type ";
        	HashMap<String,String> map = new HashMap<String,String>();
        	map.put("type", "SELLER");
        	List<Role> sellerRoles = roleDAO.query(query, map, -1, -1);
        	String userRole = user.getUserRole().replaceFirst("_SELLER", "");
        	user.setUserRole(userRole);
        	user.getRoles().removeAll(sellerRoles);		
        	//1，移除店铺
            this.storeDao.remove(id);
            //2，删除店铺权限
            this.userDAO.update(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> storeIds) {
        for (Serializable id : storeIds) {
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
        GenericPageList pList = new GenericPageList(Store.class, query, params, this.storeDao);
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
        GenericPageList pList = new GenericPageList(Store.class, query, params,
            this.storeDao);
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
    public boolean update(Store store) {
        try {
            this.storeDao.update(store);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Store> query(String query, Map params, int begin, int max) {
        return this.storeDao.query(query, params, begin, max);
    }

    public Store getObjByProperty(String propertyName, Object value) {
        return (Store) this.storeDao.getBy(propertyName, value);
    }
}
