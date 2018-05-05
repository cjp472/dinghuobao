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
import com.javamalls.platform.domain.IntegralGoodsCart;
import com.javamalls.platform.service.IIntegralGoodsCartService;

@Service
@Transactional
public class IntegralGoodsCartServiceImpl implements IIntegralGoodsCartService {
    @Resource(name = "integralGoodsCartDAO")
    private IGenericDAO<IntegralGoodsCart> integralGoodsCartDao;

    public boolean save(IntegralGoodsCart integralGoodsCart) {
        try {
            this.integralGoodsCartDao.save(integralGoodsCart);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public IntegralGoodsCart getObjById(Long id) {
        IntegralGoodsCart integralGoodsCart = (IntegralGoodsCart) this.integralGoodsCartDao.get(id);
        if (integralGoodsCart != null) {
            return integralGoodsCart;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.integralGoodsCartDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> integralGoodsCartIds) {
        for (Serializable id : integralGoodsCartIds) {
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
        GenericPageList pList = new GenericPageList(IntegralGoodsCart.class, query, params,
            this.integralGoodsCartDao);
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

    public boolean update(IntegralGoodsCart integralGoodsCart) {
        try {
            this.integralGoodsCartDao.update(integralGoodsCart);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<IntegralGoodsCart> query(String query, Map params, int begin, int max) {
        return this.integralGoodsCartDao.query(query, params, begin, max);
    }
}
