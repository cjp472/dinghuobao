package com.javamalls.platform.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.GoodsCart;
import com.javamalls.platform.domain.GoodsClass;
import com.javamalls.platform.domain.OrderForm;
import com.javamalls.platform.domain.SettleLog;
import com.javamalls.platform.service.ISettleLogService;

/**
 * 结算
 * 
 * @Filename: SettleLogServiceImpl.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 * 
 */
@Service
@Transactional
public class SettleLogServiceImpl implements ISettleLogService {
    @Resource(name = "settleLogDAO")
    private IGenericDAO<SettleLog> settleLogDAO;

    public boolean save(SettleLog SettleLog) {
        try {
            this.settleLogDAO.save(SettleLog);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public SettleLog getObjById(Long id) {
        SettleLog SettleLog = (SettleLog) this.settleLogDAO.get(id);
        if (SettleLog != null) {
            return SettleLog;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.settleLogDAO.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Long> advertIds) {
        for (Long id : advertIds) {
            delete(id);
        }
        return true;
    }

    public IPageList list(IQueryObject properties) {
        if (properties == null) {
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(SettleLog.class, query, params,
            this.settleLogDAO);
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

    public boolean update(SettleLog SettleLog) {
        try {
            this.settleLogDAO.update(SettleLog);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<SettleLog> query(String query, Map params, int begin, int max) {
        return this.settleLogDAO.query(query, params, begin, max);
    }

    @Override
    public List<Map<String, Object>> query(String string) {
        return this.settleLogDAO.query(string, null, -1, -1);
    }

    @Override
    public boolean saveSettleLog(OrderForm order) {
        // 此订单所有的购物车
        List<GoodsCart> carts = order.getGcs();
        for (GoodsCart car : carts) {
            Goods goods = car.getGoods();

            // 佣金比例=此商品对应的商品分类下的佣金，如果没有，则取父类，依次递归
            GoodsClass gc = goods.getGc();
            if (-1d == gc.getBrokerage())
                gc = this.getParentClass(gc);

            Double yongjin = gc == null ? 0d : gc.getBrokerage() < 0d ? 0d
                : gc.getBrokerage() / 100;

            // 销售金额 = 此商品对应的购物车的结算价格
            Double saleAccount = car.getPrice().doubleValue() * car.getCount();
            // 佣金金额=销售金额*佣金比例
            Double saleYongjin = saleAccount * yongjin;
            // 结算金额=销售金额-佣金金额
            Double SettleAccount = saleAccount - saleYongjin;

            // 添加结账单记录
            SettleLog settle = new SettleLog();
            settle.setCode(new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_"
                           + goods.getId().toString() + "_" + car.getId().toString());
            settle.setSettle_explain("买家评价添加账单记录");
            settle.setSale_account(new BigDecimal(saleAccount));
            settle.setSale_yongjin(new BigDecimal(saleYongjin));
            settle.setSettle_account(new BigDecimal(SettleAccount));
            settle.setStatus(1);
            settle.setStore_user_name(order.getStore().getUser().getTrueName());
            settle.setUser(SecurityUserHolder.getCurrentUser());
            settle.setOrder(order);
            settle.setIncome_time(new Date());
            settle.setGoods(goods);
            save(settle);
        }
        return false;
    }

    private GoodsClass getParentClass(GoodsClass gc) {

        if (gc.getParent() != null && gc.getParent().getBrokerage() == -1d)
            getParentClass(gc.getParent());

        return gc.getParent();
    }
}
