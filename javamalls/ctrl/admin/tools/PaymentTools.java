package com.javamalls.ctrl.admin.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.domain.Payment;
import com.javamalls.platform.service.IPaymentService;
import com.javamalls.platform.service.IUserService;

@Component
public class PaymentTools {
    @Autowired
    private IPaymentService paymentService;
    @Autowired
    private IUserService    userService;

    public boolean queryPayment(String mark, String type) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mark", mark);
        params.put("type", type);
        List<Payment> objs = this.paymentService.query(
            "select obj from Payment obj where obj.mark=:mark and obj.type=:type", params, -1, -1);
        if (objs.size() > 0) {
            return ((Payment) objs.get(0)).isInstall();
        }
        return false;
    }

    public Map<String, Object> queryPayment(String mark) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mark", mark);
        params.put("type", "user");
        Long store_id = null;
        store_id = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId())
            .getStore().getId();
        params.put("store_id", store_id);
        List<Payment> objs = this.paymentService
            .query(
                "select obj from Payment obj where obj.mark=:mark and obj.type=:type and obj.store.id=:store_id",
                params, -1, -1);
        Map<String, Object> ret = new HashMap<String, Object>();
        if (objs.size() == 1) {
            ret.put("install", Boolean.valueOf(((Payment) objs.get(0)).isInstall()));
            ret.put("already", Boolean.valueOf(true));
        } else {
            ret.put("install", Boolean.valueOf(false));
            ret.put("already", Boolean.valueOf(false));
        }
        return ret;
    }

    public Map<String, Object> queryStorePayment(String mark, String store_id) {
        Map<String, Object> ret = new HashMap<String, Object>();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mark", mark);
        params.put("store_id", CommUtil.null2Long(store_id));
        List<Payment> objs = this.paymentService.query(
            "select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id", params,
            -1, -1);
        if (objs.size() == 1) {
            ret.put("install", Boolean.valueOf(((Payment) objs.get(0)).isInstall()));
            ret.put("content", ((Payment) objs.get(0)).getContent());
            ret.put("name", ((Payment) objs.get(0)).getName());
        } else {
            ret.put("install", Boolean.valueOf(false));
            ret.put("content", "");
            ret.put("name", "");
        }
        return ret;
    }

    public Map<String, Object> queryShopPayment(String mark) {
        Map<String, Object> ret = new HashMap<String, Object>();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mark", mark);
        params.put("type", "admin");
        List<Payment> objs = this.paymentService.query(
            "select obj from Payment obj where obj.mark=:mark and obj.type=:type", params, -1, -1);
        if (objs.size() == 1) {
            ret.put("install", Boolean.valueOf(((Payment) objs.get(0)).isInstall()));
            ret.put("content", ((Payment) objs.get(0)).getContent());
            ret.put("name", ((Payment) objs.get(0)).getName());
        } else {
            ret.put("install", Boolean.valueOf(false));
            ret.put("content", "");
            ret.put("name", "");
        }
        return ret;
    }
}
