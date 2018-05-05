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
import com.javamalls.platform.domain.WaterMark;
import com.javamalls.platform.service.IWaterMarkService;

@Service
@Transactional
public class WaterMarkServiceImpl implements IWaterMarkService {
    @Resource(name = "waterMarkDAO")
    private IGenericDAO<WaterMark> waterMarkDao;

    public boolean save(WaterMark waterMark) {
        try {
            this.waterMarkDao.save(waterMark);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public WaterMark getObjById(Long id) {
        WaterMark waterMark = (WaterMark) this.waterMarkDao.get(id);
        if (waterMark != null) {
            return waterMark;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.waterMarkDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> waterMarkIds) {
        for (Serializable id : waterMarkIds) {
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
        GenericPageList pList = new GenericPageList(WaterMark.class, query, params,
            this.waterMarkDao);
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

    public boolean update(WaterMark waterMark) {
        try {
            this.waterMarkDao.update(waterMark);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<WaterMark> query(String query, Map params, int begin, int max) {
        return this.waterMarkDao.query(query, params, begin, max);
    }

    public WaterMark getObjByProperty(String propertyName, Object value) {
        return (WaterMark) this.waterMarkDao.getBy(propertyName, value);
    }
}
