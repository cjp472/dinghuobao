package com.javamalls.platform.service.impl;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.javamalls.base.query.GenericPageList;
import com.javamalls.base.query.PageObject;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.dao.UserStoreDistributorDao;
import com.javamalls.platform.domain.UserStoreDistributor;
import com.javamalls.platform.domain.UserStoreRelation;
import com.javamalls.platform.service.IUserStoreDistributorService;


/**
 * 用户与店铺关联表服务
 *                       
 * @Filename: UserStoreDistributorServiceImpl.java
 * @Version: 1.0
 * 
 */
@Service
@Transactional
public class UserStoreDistributorServiceImpl implements IUserStoreDistributorService {
	private static Logger log = LogManager.getLogger(UserStoreDistributorServiceImpl.class);
	
	@Resource(name = "userStoreDistributorDao")
	private UserStoreDistributorDao userStoreDistributorDao; 
    
    @Override
    public boolean save(UserStoreDistributor userStoreDistributor) {
        try {
            this.userStoreDistributorDao.save(userStoreDistributor);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public UserStoreDistributor getObjById(Long id) {
        UserStoreDistributor result = (UserStoreDistributor) this.userStoreDistributorDao.get(id);
        if (result != null) {
            return result;
        }
        return null;
    }
    
    /**
    *逻辑删除
    **/
     public boolean delete(Long id) {
        try {
            UserStoreDistributor obj = this.getObjById(CommUtil.null2Long(id));
            obj.setDisabled(true);
            this.userStoreDistributorDao.update(obj);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
   
   public boolean batchDelete(List<Serializable> ids) {
        for (Serializable id : ids) {
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
        GenericPageList pList = new GenericPageList(UserStoreDistributor.class, query, params, this.userStoreDistributorDao);
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
    
  public boolean update(UserStoreDistributor obj) {
        try {
            this.userStoreDistributorDao.update(obj);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<UserStoreDistributor> query(String query, Map params, int begin, int max) {
        return this.userStoreDistributorDao.query(query, params, begin, max);
    }

	@Override
	public List<UserStoreDistributor> getByUserIdAndStoreId(Long userId, Long storeid) {
		
		return null;
	}

	@Override
	public IPageList noLastList(IQueryObject properties) {
		if (properties == null) {
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(UserStoreDistributor.class, query, params,
            this.userStoreDistributorDao);
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