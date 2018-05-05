package com.javamalls.ctrl.admin.tools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.domain.Complaint;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.OrderForm;
import com.javamalls.platform.domain.Report;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.service.IComplaintService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IOrderFormService;
import com.javamalls.platform.service.IReportService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.IUserService;

@Component
public class StatTools {
    @Autowired
    private IStoreService     storeService;
    @Autowired
    private IGoodsService     goodsService;
    @Autowired
    private IOrderFormService orderFormService;
    @Autowired
    private IUserService      userService;
    @Autowired
    private IReportService    reportService;
    @Autowired
    private IComplaintService complaintService;

    public int query_store(int count) {
        List<Store> stores = new ArrayList<Store>();
        Map<String, Object> params = new HashMap<String, Object>();
        Calendar cal = Calendar.getInstance();
        cal.add(6, count);
        params.put("time", cal.getTime());
        stores = this.storeService.query("select obj from Store obj where obj.createtime>=:time",
            params, -1, -1);
        return stores.size();
    }

    public int query_user(int count) {
        List<User> users = new ArrayList<User>();
        Map<String, Object> params = new HashMap<String, Object>();
        Calendar cal = Calendar.getInstance();
        cal.add(6, count);
        params.put("time", cal.getTime());
        users = this.userService.query("select obj from User obj where obj.createtime>=:time",
            params, -1, -1);
        return users.size();
    }

    public int query_goods(int count) {
        List<Goods> goods = new ArrayList<Goods>();
        Map<String, Object> params = new HashMap<String, Object>();
        Calendar cal = Calendar.getInstance();
        cal.add(6, count);
        params.put("time", cal.getTime());
        goods = this.goodsService.query(
            "select obj from Goods obj where obj.disabled=0 and obj.createtime>=:time", params, -1,
            -1);
        return goods.size();
    }

    public int query_order(int count) {
        List<OrderForm> orders = new ArrayList<OrderForm>();
        Map<String, Object> params = new HashMap<String, Object>();
        Calendar cal = Calendar.getInstance();
        cal.add(6, count);
        params.put("time", cal.getTime());
        orders = this.orderFormService.query(
            "select obj from OrderForm obj where obj.createtime>=:time", params, -1, -1);
        return orders.size();
    }

    public int query_all_user() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userRole", "ADMIN");
        List<User> users = this.userService.query(
            "select obj from User obj where obj.userRole!=:userRole and obj.parent.id is null ", params, -1, -1);
        return users.size();
    }

    public int query_all_goods() {
        List<Goods> goods = this.goodsService.query(
            "select obj from Goods obj where obj.disabled=0", null, -1, -1);
        return goods.size();
    }

    public int query_all_store() {
        List<Store> stores = this.storeService.query("select obj from Store obj", null, -1, -1);
        return stores.size();
    }

    public int query_update_store() {
        List<Store> stores = this.storeService.query(
            "select obj from Store obj where obj.update_grade.id is not null", null, -1, -1);
        return stores.size();
    }

    public double query_all_amount() {
        double price = 0.0D;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("order_status", Integer.valueOf(50));
        List<OrderForm> ofs = this.orderFormService.query(
            "select obj from OrderForm obj where obj.order_status>=:order_status", params, -1, -1);
        for (OrderForm of : ofs) {
            price = CommUtil.null2Double(of.getTotalPrice()) + price;
        }
        return price;
    }

    public int query_complaint(int count) {
        List<Complaint> objs = new ArrayList();
        Map<String, Object> params = new HashMap<String, Object>();
        Calendar cal = Calendar.getInstance();
        cal.add(6, count);
        params.put("time", cal.getTime());
        params.put("status", Integer.valueOf(0));
        objs = this.complaintService.query(
            "select obj from Complaint obj where obj.createtime>=:time and obj.status=:status",
            params, -1, -1);
        return objs.size();
    }

    public int query_report(int count) {
        List<Report> objs = new ArrayList<Report>();
        Map<String, Object> params = new HashMap<String, Object>();
        Calendar cal = Calendar.getInstance();
        cal.add(6, count);
        params.put("time", cal.getTime());
        params.put("status", Integer.valueOf(0));
        objs = this.reportService.query(
            "select obj from Report obj where obj.createtime>=:time and obj.status=:status",
            params, -1, -1);
        return objs.size();
    }
}
