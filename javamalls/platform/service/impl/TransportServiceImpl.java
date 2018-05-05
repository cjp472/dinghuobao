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
import com.javamalls.platform.domain.Transport;
import com.javamalls.platform.service.ITransportService;

@Service
@Transactional
public class TransportServiceImpl implements ITransportService {
    @Resource(name = "transportDAO")
    private IGenericDAO<Transport> transportDao;

    public boolean save(Transport transport) {
        try {
            this.transportDao.save(transport);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Transport getObjById(Long id) {
        Transport transport = (Transport) this.transportDao.get(id);
        if (transport != null) {
            return transport;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.transportDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> transportIds) {
        for (Serializable id : transportIds) {
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
        GenericPageList pList = new GenericPageList(Transport.class, query, params,
            this.transportDao);
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

    public boolean update(Transport transport) {
        try {
            this.transportDao.update(transport);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Transport> query(String query, Map params, int begin, int max) {
        return this.transportDao.query(query, params, begin, max);
    }
}
