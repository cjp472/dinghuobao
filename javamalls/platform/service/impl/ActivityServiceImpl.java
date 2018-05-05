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
import com.javamalls.platform.domain.Activity;
import com.javamalls.platform.service.IActivityService;

@Service
@Transactional
public class ActivityServiceImpl implements IActivityService {
    @Resource(name = "activityDAO")
    private IGenericDAO<Activity> activityDao;

    public boolean save(Activity activity) {
        try {
            this.activityDao.save(activity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Activity getObjById(Long id) {
        Activity activity = (Activity) this.activityDao.get(id);
        if (activity != null) {
            return activity;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.activityDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> activityIds) {
        for (Serializable id : activityIds) {
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
        GenericPageList pList = new GenericPageList(Activity.class, query, params, this.activityDao);
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

    public boolean update(Activity activity) {
        try {
            this.activityDao.update(activity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Activity> query(String query, Map params, int begin, int max) {
        return this.activityDao.query(query, params, begin, max);
    }
}
