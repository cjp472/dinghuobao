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
import com.javamalls.platform.domain.SnsFriend;
import com.javamalls.platform.service.ISnsFriendService;

@Service
@Transactional
public class SnsFriendServiceImpl implements ISnsFriendService {
    @Resource(name = "snsFriendDAO")
    private IGenericDAO<SnsFriend> snsFriendDao;

    public boolean save(SnsFriend snsFriend) {
        try {
            this.snsFriendDao.save(snsFriend);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public SnsFriend getObjById(Long id) {
        SnsFriend snsFriend = (SnsFriend) this.snsFriendDao.get(id);
        if (snsFriend != null) {
            return snsFriend;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.snsFriendDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> snsFriendIds) {
        for (Serializable id : snsFriendIds) {
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
        GenericPageList pList = new GenericPageList(SnsFriend.class, query, params,
            this.snsFriendDao);
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

    public boolean update(SnsFriend snsFriend) {
        try {
            this.snsFriendDao.update(snsFriend);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<SnsFriend> query(String query, Map params, int begin, int max) {
        return this.snsFriendDao.query(query, params, begin, max);
    }
}
