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
import com.javamalls.platform.domain.GroupPriceRange;
import com.javamalls.platform.service.IGroupPriceRangeService;

@Service
@Transactional
public class GroupPriceRangeServiceImpl implements IGroupPriceRangeService {
    @Resource(name = "groupPriceRangeDAO")
    private IGenericDAO<GroupPriceRange> groupPriceRangeDao;

    public boolean save(GroupPriceRange groupPriceRange) {
        try {
            this.groupPriceRangeDao.save(groupPriceRange);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public GroupPriceRange getObjById(Long id) {
        GroupPriceRange groupPriceRange = (GroupPriceRange) this.groupPriceRangeDao.get(id);
        if (groupPriceRange != null) {
            return groupPriceRange;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.groupPriceRangeDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> groupPriceRangeIds) {
        for (Serializable id : groupPriceRangeIds) {
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
        GenericPageList pList = new GenericPageList(GroupPriceRange.class, query, params,
            this.groupPriceRangeDao);
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

    public boolean update(GroupPriceRange groupPriceRange) {
        try {
            this.groupPriceRangeDao.update(groupPriceRange);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<GroupPriceRange> query(String query, Map params, int begin, int max) {
        return this.groupPriceRangeDao.query(query, params, begin, max);
    }
}
