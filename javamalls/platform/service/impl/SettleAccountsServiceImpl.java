package com.javamalls.platform.service.impl;

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
import com.javamalls.platform.domain.SettleAccunts;
import com.javamalls.platform.service.ISettleAccountsService;

/**
 * 结算
 * 
 * @Filename: SettleAccountsServiceImpl.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 * 
 */
@Service
@Transactional
public class SettleAccountsServiceImpl implements ISettleAccountsService {
    @Resource(name = "settleAccountsDAO")
    private IGenericDAO<SettleAccunts> settleAccuntsDAO;

    public boolean save(SettleAccunts settleAccunts) {
        try {
            this.settleAccuntsDAO.save(settleAccunts);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public SettleAccunts getObjById(Long id) {
        SettleAccunts settleAccunts = (SettleAccunts) this.settleAccuntsDAO.get(id);
        if (settleAccunts != null) {
            return settleAccunts;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.settleAccuntsDAO.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Long> advertIds) {
        for (Long id : advertIds) {
            delete(id);
        }
        return true;
    }

    public IPageList list(IQueryObject properties) {
        if (properties == null) {
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(SettleAccunts.class, query, params,
            this.settleAccuntsDAO);
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

    public boolean update(SettleAccunts settleAccunts) {
        try {
            this.settleAccuntsDAO.update(settleAccunts);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<SettleAccunts> query(String query, Map params, int begin, int max) {
        return this.settleAccuntsDAO.query(query, params, begin, max);
    }
}
