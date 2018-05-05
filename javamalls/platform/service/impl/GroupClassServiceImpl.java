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
import com.javamalls.platform.domain.GroupClass;
import com.javamalls.platform.service.IGroupClassService;

@Service
@Transactional
public class GroupClassServiceImpl implements IGroupClassService {
    @Resource(name = "groupClassDAO")
    private IGenericDAO<GroupClass> groupClassDao;

    public boolean save(GroupClass groupClass) {
        try {
            this.groupClassDao.save(groupClass);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public GroupClass getObjById(Long id) {
        GroupClass groupClass = (GroupClass) this.groupClassDao.get(id);
        if (groupClass != null) {
            return groupClass;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.groupClassDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> groupClassIds) {
        for (Serializable id : groupClassIds) {
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
        GenericPageList pList = new GenericPageList(GroupClass.class, query, params,
            this.groupClassDao);
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

    public boolean update(GroupClass groupClass) {
        try {
            this.groupClassDao.update(groupClass);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<GroupClass> query(String query, Map params, int begin, int max) {
        return this.groupClassDao.query(query, params, begin, max);
    }
}
