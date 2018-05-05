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
import com.javamalls.platform.domain.SysLog;
import com.javamalls.platform.service.ISysLogService;

@Service
@Transactional
public class SysLogServiceImpl implements ISysLogService {
    @Resource(name = "sysLogDAO")
    private IGenericDAO<SysLog> sysLogDao;

    public boolean save(SysLog sysLog) {
        try {
            this.sysLogDao.save(sysLog);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public SysLog getObjById(Long id) {
        SysLog sysLog = (SysLog) this.sysLogDao.get(id);
        if (sysLog != null) {
            return sysLog;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.sysLogDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> sysLogIds) {
        for (Serializable id : sysLogIds) {
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
        GenericPageList pList = new GenericPageList(SysLog.class, query, params, this.sysLogDao);
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

    public boolean update(SysLog sysLog) {
        try {
            this.sysLogDao.update(sysLog);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<SysLog> query(String query, Map params, int begin, int max) {
        return this.sysLogDao.query(query, params, begin, max);
    }
}
