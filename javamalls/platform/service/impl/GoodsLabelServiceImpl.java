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
import com.javamalls.platform.dao.GoodsLabelDao;
import com.javamalls.platform.domain.GoodsLabel;
import com.javamalls.platform.service.IGoodsLabelService;


/**
 * 商品标签表服务
 *                       
 * @Filename: GoodsLabelServiceImpl.java
 * @Version: 1.0
 * 
 */
@Service
@Transactional
public class GoodsLabelServiceImpl implements IGoodsLabelService {
	private static Logger log = LogManager.getLogger(GoodsLabelServiceImpl.class);
	
	@Resource(name = "goodsLabelDao")
	private GoodsLabelDao goodsLabelDao; 
    
    @Override
    public boolean save(GoodsLabel goodsLabel) {
        try {
            this.goodsLabelDao.save(goodsLabel);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public GoodsLabel getObjById(Long id) {
        GoodsLabel result = (GoodsLabel) this.goodsLabelDao.get(id);
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
            GoodsLabel obj = this.getObjById(CommUtil.null2Long(id));
            obj.setDisabled(true);
            this.goodsLabelDao.update(obj);
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
        GenericPageList pList = new GenericPageList(GoodsLabel.class, query, params, this.goodsLabelDao);
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
    
  public boolean update(GoodsLabel obj) {
        try {
            this.goodsLabelDao.update(obj);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<GoodsLabel> query(String query, Map params, int begin, int max) {
        return this.goodsLabelDao.query(query, params, begin, max);
    }
}