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
import com.javamalls.platform.domain.GroupGoods;
import com.javamalls.platform.service.IGroupGoodsService;

@Service
@Transactional
public class GroupGoodsServiceImpl implements IGroupGoodsService {
    @Resource(name = "groupGoodsDAO")
    private IGenericDAO<GroupGoods> groupGoodsDao;

    public boolean save(GroupGoods groupGoods) {
        try {
            this.groupGoodsDao.save(groupGoods);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public GroupGoods getObjById(Long id) {
        GroupGoods groupGoods = (GroupGoods) this.groupGoodsDao.get(id);
        if (groupGoods != null) {
            return groupGoods;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.groupGoodsDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> groupGoodsIds) {
        for (Serializable id : groupGoodsIds) {
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
        GenericPageList pList = new GenericPageList(GroupGoods.class, query, params,
            this.groupGoodsDao);
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

    public boolean update(GroupGoods groupGoods) {
        try {
            this.groupGoodsDao.update(groupGoods);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<GroupGoods> query(String query, Map params, int begin, int max) {
        return this.groupGoodsDao.query(query, params, begin, max);
    }
}
