package com.javamalls.platform.service.impl;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.javamalls.base.query.GenericPageList;
import com.javamalls.base.query.PageObject;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.dao.EntryOrderDetailDao;
import com.javamalls.platform.domain.EntryOrderDetail;
import com.javamalls.platform.service.IEntryOrderDetailService;

/**
 * 出货单服务
 *                       
 * @Filename: EntryOrderDetailServiceImpl.java
 * @Version: 1.0
 * 
 */
@Service
@Transactional
public class EntryOrderDetailServiceImpl implements IEntryOrderDetailService {
	private static Logger log = LogManager.getLogger(EntryOrderDetailServiceImpl.class);
	
	@Resource(name = "entryOrderDetailDao")
	private EntryOrderDetailDao entryOrderDetailDao; 
    
    @Override
    public boolean save(EntryOrderDetail entryOrderDetail) {
        try {
            this.entryOrderDetailDao.save(entryOrderDetail);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public EntryOrderDetail getObjById(Long id) {
        EntryOrderDetail result = (EntryOrderDetail) this.entryOrderDetailDao.get(id);
        if (result != null) {
            return result;
        }
        return null;
    }
    
    /**
    *逻辑删除
    **/
     public boolean delete(Long id) {
        try {
            EntryOrderDetail obj = this.getObjById(CommUtil.null2Long(id));
            obj.setDisabled(true);
            this.entryOrderDetailDao.update(obj);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
   
   public boolean batchDelete(List<Serializable> ids) {
        for (Serializable id : ids) {
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
        GenericPageList pList = new GenericPageList(EntryOrderDetail.class, query, params, this.entryOrderDetailDao);
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
    
  public boolean update(EntryOrderDetail obj) {
        try {
            this.entryOrderDetailDao.update(obj);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<EntryOrderDetail> query(String query, Map params, int begin, int max) {
        return this.entryOrderDetailDao.query(query, params, begin, max);
    }
}