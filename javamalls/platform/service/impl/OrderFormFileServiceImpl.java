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
import com.javamalls.platform.domain.OrderFormFile;
import com.javamalls.platform.service.IOrderFormFileService;

@Service
@Transactional
public class OrderFormFileServiceImpl implements IOrderFormFileService {
    @Resource(name = "orderFormFileDAO")
    private IGenericDAO<OrderFormFile> orderFormFileDAO;

    public boolean save(OrderFormFile orderFormFile) {
        try {
            this.orderFormFileDAO.save(orderFormFile);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public OrderFormFile getObjById(Long id) {
        OrderFormFile orderFormFile = (OrderFormFile) this.orderFormFileDAO.get(id);
        if (orderFormFile != null) {
            return orderFormFile;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.orderFormFileDAO.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> orderFormFileIds) {
        for (Serializable id : orderFormFileIds) {
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
        GenericPageList pList = new GenericPageList(OrderFormFile.class, query, params, this.orderFormFileDAO);
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

    public boolean update(OrderFormFile orderFormFile) {
        try {
            this.orderFormFileDAO.update(orderFormFile);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<OrderFormFile> query(String query, Map params, int begin, int max) {
        return this.orderFormFileDAO.query(query, params, begin, max);
    }
}
