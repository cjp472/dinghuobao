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
import com.javamalls.platform.dao.GoodslabelGoodsDao;
import com.javamalls.platform.domain.GoodslabelGoods;
import com.javamalls.platform.service.IGoodslabelGoodsService;

/**
 * 商品标志与商品关联表服务
 *                       
 * @Filename: GoodslabelGoodsServiceImpl.java
 * @Version: 1.0
 * 
 */
@Service
@Transactional
public class GoodslabelGoodsServiceImpl implements IGoodslabelGoodsService {
	private static Logger log = LogManager.getLogger(GoodslabelGoodsServiceImpl.class);
	
	@Resource(name = "goodslabelGoodsDao")
	private GoodslabelGoodsDao goodslabelGoodsDao; 
    
    @Override
    public boolean save(GoodslabelGoods goodslabelGoods) {
        try {
            this.goodslabelGoodsDao.save(goodslabelGoods);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public GoodslabelGoods getObjById(Long id) {
        GoodslabelGoods result = (GoodslabelGoods) this.goodslabelGoodsDao.get(id);
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
            GoodslabelGoods obj = this.getObjById(CommUtil.null2Long(id));
            obj.setDisabled(true);
            this.goodslabelGoodsDao.update(obj);
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
        GenericPageList pList = new GenericPageList(GoodslabelGoods.class, query, params, this.goodslabelGoodsDao);
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
    
  public boolean update(GoodslabelGoods obj) {
        try {
            this.goodslabelGoodsDao.update(obj);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<GoodslabelGoods> query(String query, Map params, int begin, int max) {
        return this.goodslabelGoodsDao.query(query, params, begin, max);
    }

	@Override
	public List<Long> queryGoodsLabelLongId(String paramString, Map paramMap, int paramInt1, int paramInt2) {
		
		return this.goodslabelGoodsDao.query(paramString, paramMap, paramInt1, paramInt2);
	}

}