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
import com.javamalls.platform.domain.CompanyInfo;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.service.ICompanyInfoService;

@Service
@Transactional
public class CompanyInfoService implements ICompanyInfoService {
    @Resource(name = "companyInfoDAO")
    private IGenericDAO<CompanyInfo> companyInfoDAO;

    public boolean save(CompanyInfo companyInfo) {
        try {
            this.companyInfoDAO.save(companyInfo);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public CompanyInfo getObjById(Long id) {
    	CompanyInfo companyInfo = (CompanyInfo) this.companyInfoDAO.get(id);
        if (companyInfo != null) {
            return companyInfo;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.companyInfoDAO.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> companyInfoIds) {
        for (Serializable id : companyInfoIds) {
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
        GenericPageList pList = new GenericPageList(CompanyInfo.class, query, params,
            this.companyInfoDAO);
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

    public boolean update(CompanyInfo companyInfo) {
        try {
            this.companyInfoDAO.update(companyInfo);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<CompanyInfo> query(String query, Map params, int begin, int max) {
        return this.companyInfoDAO.query(query, params, begin, max);
    }

	@Override
	public boolean delete_wuli(Long paramLong) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IPageList nolastlist(IQueryObject properties) {
        if (properties == null) {
            return null;
        }
        String query = properties.getQuery();
        Map<String, Object> params = properties.getParameters();
        GenericPageList pList = new GenericPageList(Goods.class, query, params, this.companyInfoDAO);
        if (properties != null) {
            PageObject pageObj = properties.getPageObj();
            if (pageObj != null) {
                pList.doNolastList(pageObj.getCurrentPage() == null ? 0 : pageObj.getCurrentPage()
                    .intValue(), pageObj.getPageSize() == null ? 0 : pageObj.getPageSize()
                    .intValue());
            }
        } else {
            pList.doNolastList(0, -1);
        }
        return pList;
    }

	@Override
	public CompanyInfo getObjByProperty(String propertyName, Object value) {
        return (CompanyInfo) this.companyInfoDAO.getBy(propertyName, value);
       }
}
