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
import com.javamalls.platform.dao.HomepageNavigationDao;
import com.javamalls.platform.domain.HomepageNavigation;
import com.javamalls.platform.service.IHomepageNavigationService;

/**
 * 首页导航表服务
 *                       
 * @Filename: HomepageNavigationServiceImpl.java
 * @Version: 1.0
 * 
 */
@Service
@Transactional
public class HomepageNavigationServiceImpl implements IHomepageNavigationService {
	private static Logger log = LogManager.getLogger(HomepageNavigationServiceImpl.class);
	
	@Resource(name = "homepageNavigationDao")
	private HomepageNavigationDao homepageNavigationDao; 
    
    @Override
    public boolean save(HomepageNavigation homepageNavigation) {
        try {
            this.homepageNavigationDao.save(homepageNavigation);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public HomepageNavigation getObjById(Long id) {
        HomepageNavigation result = (HomepageNavigation) this.homepageNavigationDao.get(id);
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
            HomepageNavigation obj = this.getObjById(CommUtil.null2Long(id));
            obj.setDisabled(true);
            this.homepageNavigationDao.update(obj);
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
        GenericPageList pList = new GenericPageList(HomepageNavigation.class, query, params, this.homepageNavigationDao);
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
    
  public boolean update(HomepageNavigation obj) {
        try {
            this.homepageNavigationDao.update(obj);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<HomepageNavigation> query(String query, Map params, int begin, int max) {
        return this.homepageNavigationDao.query(query, params, begin, max);
    }
}