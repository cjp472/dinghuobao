package com.javamalls.base.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javamalls.base.basedao.GenericEntityDao;
import com.javamalls.base.service.IQueryService;

@Service
@Transactional
public class QueryService implements IQueryService {
    @Autowired
    @Qualifier("genericEntityDao")
    private GenericEntityDao geDao;

    public GenericEntityDao getGeDao() {
        return this.geDao;
    }

    public void setGeDao(GenericEntityDao geDao) {
        this.geDao = geDao;
    }

    public List query(String scope, Map params, int page, int pageSize) {
        return this.geDao.query(scope, params, page, pageSize);
    }

    public List executeNativeQuery(String nnq, Map params, int begin, int max) {
        return this.geDao.executeNativeQuery(nnq, params, begin, max);
    }

    public int executeNativeSQL(String nnq) {
        return this.geDao.executeNativeSQL(nnq);
    }
}
