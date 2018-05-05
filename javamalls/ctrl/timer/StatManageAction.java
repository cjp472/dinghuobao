package com.javamalls.ctrl.timer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.ctrl.admin.tools.MsgTools;
import com.javamalls.ctrl.admin.tools.StatTools;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.MobileVerifyCode;
import com.javamalls.platform.domain.OrderForm;
import com.javamalls.platform.domain.OrderFormLog;
import com.javamalls.platform.domain.PredepositLog;
import com.javamalls.platform.domain.SettleLog;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.StoreStat;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.service.IActivityGoodsService;
import com.javamalls.platform.service.IActivityService;
import com.javamalls.platform.service.IDeliveryGoodsService;
import com.javamalls.platform.service.IEvaluateService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IGroupGoodsService;
import com.javamalls.platform.service.IGroupService;
import com.javamalls.platform.service.IMobileVerifyCodeService;
import com.javamalls.platform.service.IOrderFormLogService;
import com.javamalls.platform.service.IOrderFormService;
import com.javamalls.platform.service.IPaymentService;
import com.javamalls.platform.service.IPredepositLogService;
import com.javamalls.platform.service.ISettleLogService;
import com.javamalls.platform.service.IStoreClassService;
import com.javamalls.platform.service.IStorePointService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.IStoreStatService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.ITemplateService;
import com.javamalls.platform.service.IUserService;

/**店铺相关用户统计      店铺评分       删除过期的手机验证码开始    店铺得分        团购
 *                       
 * @Filename: StatManageAction.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Component("shop_stat")
public class StatManageAction {
    @Autowired
    private IStoreStatService        storeStatService;
    @Autowired
    private StatTools                statTools;
    @Autowired
    private IMobileVerifyCodeService mobileverifycodeService;
    @Autowired
    private IStoreService            storeService;
    @Autowired
    private IEvaluateService         evaluateService;
    @Autowired
    private IStorePointService       storePointService;
    @Autowired
    private IGroupService            groupService;
    @Autowired
    private IOrderFormService        orderFormService;
    @Autowired
    private IOrderFormLogService     orderFormLogService;
    @Autowired
    private IPaymentService          paymentService;
    @Autowired
    private IPredepositLogService    predepositLogService;
    @Autowired
    private ISysConfigService        configService;
    @Autowired
    private IUserService             userService;
    @Autowired
    private ITemplateService         templateService;
    @Autowired
    private IActivityService         activityService;
    @Autowired
    private IGoodsService            goodsService;
    @Autowired
    private IDeliveryGoodsService    deliveryGoodsService;
    @Autowired
    private IStoreClassService       storeClassService;
    @Autowired
    private IActivityGoodsService    activityGoodsService;
    @Autowired
    private IGroupGoodsService       groupGoodsService;
    @Autowired
    private MsgTools                 msgTools;
    @Autowired
    private ISettleLogService        settleLogService;

    private void execute() {
        try {
            //店铺相关用户统计开始
            List<StoreStat> stats = this.storeStatService.query("select obj from StoreStat obj",
                null, -1, -1);
            StoreStat stat = null;
            if (stats.size() > 0) {
                stat = (StoreStat) stats.get(0);
            } else {
                stat = new StoreStat();
            }
            stat.setCreatetime(new Date());
            Calendar cal = Calendar.getInstance();
            cal.add(12, 30);
            stat.setNext_time(cal.getTime());
            stat.setWeek_complaint(this.statTools.query_complaint(-7));
            stat.setWeek_goods(this.statTools.query_goods(-7));
            stat.setWeek_order(this.statTools.query_order(-7));
            stat.setWeek_report(this.statTools.query_report(-7));
            stat.setWeek_store(this.statTools.query_store(-7));
            stat.setWeek_user(this.statTools.query_user(-7));
            stat.setAll_goods(this.statTools.query_all_goods());
            stat.setAll_store(this.statTools.query_all_store());
            stat.setAll_user(this.statTools.query_all_user());
            stat.setStore_update(this.statTools.query_update_store());
            stat.setOrder_amount(BigDecimal.valueOf(this.statTools.query_all_amount()));
            if (stats.size() > 0) {
                this.storeStatService.update(stat);
            } else {
                this.storeStatService.save(stat);
            }
            //店铺相关用用户统计结束

            //删除过期的手机验证码开始
            cal.setTime(new Date());
            cal.add(12, -15);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("time", cal.getTime());
            List<MobileVerifyCode> mvcs = this.mobileverifycodeService.query(
                "select obj from MobileVerifyCode obj where obj.createtime<=:time", params, -1, -1);
            for (MobileVerifyCode mvc : mvcs) {
                this.mobileverifycodeService.delete(mvc.getId());
            }
            //删除过期手机验证码结束

            //店铺得分开始
            List<Store> stores = this.storeService.query("select obj from Store obj", null, -1, -1);
            /*List<Evaluate> evas;
            double service_evaluate;
            double ship_evaluate;
            DecimalFormat df;*/
            /* for (Store store : stores) {
                 params.clear();
                 params.put("store_id", store.getId());
                 evas = this.evaluateService.query(
                     "select obj from Evaluate obj where obj.of.store.id=:store_id", params, -1, -1);
                 double store_evaluate1 = 0.0D;
                 double store_evaluate1_total = 0.0D;
                 double description_evaluate = 0.0D;
                 double description_evaluate_total = 0.0D;
                 service_evaluate = 0.0D;
                 double service_evaluate_total = 0.0D;
                 ship_evaluate = 0.0D;
                 double ship_evaluate_total = 0.0D;
                 df = new DecimalFormat("0.0");
                 for (Evaluate eva1 : evas) {
                     store_evaluate1_total = store_evaluate1_total + eva1.getEvaluate_buyer_val();//卖家好评等级

                     description_evaluate_total = description_evaluate_total
                                                  + CommUtil.null2Double(eva1
                                                      .getDescription_evaluate());//描述评分

                     service_evaluate_total = service_evaluate_total
                                              + CommUtil.null2Double(eva1.getService_evaluate());//服务评分

                     ship_evaluate_total = ship_evaluate_total
                                           + CommUtil.null2Double(eva1.getShip_evaluate());//发货评分
                 }
                 store_evaluate1 = CommUtil.null2Double(df.format(store_evaluate1_total
                                                                  / evas.size()));
                 description_evaluate = CommUtil.null2Double(df.format(description_evaluate_total
                                                                       / evas.size()));
                 service_evaluate = CommUtil.null2Double(df.format(service_evaluate_total
                                                                   / evas.size()));
                 ship_evaluate = CommUtil.null2Double(df.format(ship_evaluate_total / evas.size()));
                 double description_evaluate_halfyear = 0.0D;
                 double service_evaluate_halfyear = 0.0D;
                 double ship_evaluate_halfyear = 0.0D;
                 int description_evaluate_halfyear_count5 = 0;
                 int description_evaluate_halfyear_count4 = 0;
                 int description_evaluate_halfyear_count3 = 0;
                 int description_evaluate_halfyear_count2 = 0;
                 int description_evaluate_halfyear_count1 = 0;
                 int service_evaluate_halfyear_count5 = 0;
                 int service_evaluate_halfyear_count4 = 0;
                 int service_evaluate_halfyear_count3 = 0;
                 int service_evaluate_halfyear_count2 = 0;
                 int service_evaluate_halfyear_count1 = 0;
                 int ship_evaluate_halfyear_count5 = 0;
                 int ship_evaluate_halfyear_count4 = 0;
                 int ship_evaluate_halfyear_count3 = 0;
                 int ship_evaluate_halfyear_count2 = 0;
                 int ship_evaluate_halfyear_count1 = 0;
                 Calendar cal1 = Calendar.getInstance();
                 cal1.add(2, -6);
                 params.clear();
                 params.put("store_id", store.getId());
                 params.put("createtime", cal1.getTime());
                 evas = this.evaluateService
                     .query(
                         "select obj from Evaluate obj where obj.of.store.id=:store_id and obj.createtime>=:createtime",
                         params, -1, -1);
                 for (Evaluate eva : evas) {
                     description_evaluate_halfyear = description_evaluate_halfyear
                                                     + CommUtil.null2Double(eva
                                                         .getDescription_evaluate());

                     service_evaluate_halfyear = service_evaluate_halfyear
                                                 + CommUtil.null2Double(eva.getService_evaluate());

                     ship_evaluate_halfyear = ship_evaluate_halfyear
                                              + CommUtil.null2Double(eva.getService_evaluate());
                     if (CommUtil.null2Double(eva.getDescription_evaluate()) >= 4.0D) {
                         description_evaluate_halfyear_count5++;
                     }
                     if ((CommUtil.null2Double(eva.getDescription_evaluate()) >= 3.0D)
                         && (CommUtil.null2Double(eva.getDescription_evaluate()) < 4.0D)) {
                         description_evaluate_halfyear_count4++;
                     }
                     if ((CommUtil.null2Double(eva.getDescription_evaluate()) >= 2.0D)
                         && (CommUtil.null2Double(eva.getDescription_evaluate()) < 3.0D)) {
                         description_evaluate_halfyear_count3++;
                     }
                     if ((CommUtil.null2Double(eva.getDescription_evaluate()) >= 1.0D)
                         && (CommUtil.null2Double(eva.getDescription_evaluate()) < 2.0D)) {
                         description_evaluate_halfyear_count2++;
                     }
                     if ((CommUtil.null2Double(eva.getDescription_evaluate()) >= 0.0D)
                         && (CommUtil.null2Double(eva.getDescription_evaluate()) < 1.0D)) {
                         description_evaluate_halfyear_count1++;
                     }
                     if (CommUtil.null2Double(eva.getService_evaluate()) >= 4.0D) {
                         service_evaluate_halfyear_count5++;
                     }
                     if ((CommUtil.null2Double(eva.getService_evaluate()) >= 3.0D)
                         && (CommUtil.null2Double(eva.getService_evaluate()) < 4.0D)) {
                         service_evaluate_halfyear_count4++;
                     }
                     if ((CommUtil.null2Double(eva.getService_evaluate()) >= 2.0D)
                         && (CommUtil.null2Double(eva.getService_evaluate()) < 3.0D)) {
                         service_evaluate_halfyear_count3++;
                     }
                     if ((CommUtil.null2Double(eva.getService_evaluate()) >= 1.0D)
                         && (CommUtil.null2Double(eva.getService_evaluate()) < 2.0D)) {
                         service_evaluate_halfyear_count2++;
                     }
                     if ((CommUtil.null2Double(eva.getService_evaluate()) >= 0.0D)
                         && (CommUtil.null2Double(eva.getService_evaluate()) < 1.0D)) {
                         service_evaluate_halfyear_count1++;
                     }
                     if (CommUtil.null2Double(eva.getShip_evaluate()) >= 4.0D) {
                         ship_evaluate_halfyear_count5++;
                     }
                     if ((CommUtil.null2Double(eva.getShip_evaluate()) >= 3.0D)
                         && (CommUtil.null2Double(eva.getShip_evaluate()) < 4.0D)) {
                         ship_evaluate_halfyear_count4++;
                     }
                     if ((CommUtil.null2Double(eva.getShip_evaluate()) >= 2.0D)
                         && (CommUtil.null2Double(eva.getShip_evaluate()) < 3.0D)) {
                         ship_evaluate_halfyear_count3++;
                     }
                     if ((CommUtil.null2Double(eva.getShip_evaluate()) >= 1.0D)
                         && (CommUtil.null2Double(eva.getShip_evaluate()) < 2.0D)) {
                         ship_evaluate_halfyear_count2++;
                     }
                     if ((CommUtil.null2Double(eva.getShip_evaluate()) >= 0.0D)
                         && (CommUtil.null2Double(eva.getShip_evaluate()) < 1.0D)) {
                         ship_evaluate_halfyear_count1++;
                     }
                 }
                 if (evas.size() > 0) {
                     description_evaluate_halfyear = description_evaluate_halfyear / evas.size();

                     service_evaluate_halfyear = service_evaluate_halfyear / evas.size();
                     ship_evaluate_halfyear /= evas.size();
                 }
                 params.clear();
                 params.put("store_id", store.getId());
                 List<StorePoint> sps = this.storePointService.query(
                     "select obj from StorePoint obj where obj.store.id=:store_id", params, -1, -1);
                 StorePoint point = null;
                 if (sps.size() > 0) {
                     point = (StorePoint) sps.get(0);
                 } else {
                     point = new StorePoint();
                 }
                 point.setStatTime(new Date());
                 point.setStore(store);
                 point.setDescription_evaluate(BigDecimal.valueOf(description_evaluate));
                 point.setService_evaluate(BigDecimal.valueOf(service_evaluate));
                 point.setShip_evaluate(BigDecimal.valueOf(ship_evaluate));
                 point.setStore_evaluate1(BigDecimal.valueOf(store_evaluate1));
                 point.setDescription_evaluate_halfyear(BigDecimal
                     .valueOf(description_evaluate_halfyear));
                 point.setDescription_evaluate_halfyear_count1(description_evaluate_halfyear_count1);
                 point.setDescription_evaluate_halfyear_count2(description_evaluate_halfyear_count2);
                 point.setDescription_evaluate_halfyear_count3(description_evaluate_halfyear_count3);
                 point.setDescription_evaluate_halfyear_count4(description_evaluate_halfyear_count4);
                 point.setDescription_evaluate_halfyear_count5(description_evaluate_halfyear_count5);
                 point.setService_evaluate_halfyear(BigDecimal.valueOf(service_evaluate_halfyear));
                 point.setService_evaluate_halfyear_count1(service_evaluate_halfyear_count1);
                 point.setService_evaluate_halfyear_count2(service_evaluate_halfyear_count2);
                 point.setService_evaluate_halfyear_count3(service_evaluate_halfyear_count3);
                 point.setService_evaluate_halfyear_count4(service_evaluate_halfyear_count4);
                 point.setService_evaluate_halfyear_count5(service_evaluate_halfyear_count5);
                 point.setShip_evaluate_halfyear(BigDecimal.valueOf(ship_evaluate));
                 point.setShip_evaluate_halfyear_count1(ship_evaluate_halfyear_count1);
                 point.setShip_evaluate_halfyear_count2(ship_evaluate_halfyear_count2);
                 point.setShip_evaluate_halfyear_count3(ship_evaluate_halfyear_count3);
                 point.setShip_evaluate_halfyear_count4(ship_evaluate_halfyear_count4);
                 point.setShip_evaluate_halfyear_count5(ship_evaluate_halfyear_count5);
                 if (sps.size() > 0) {
                     this.storePointService.update(point);
                 } else {
                     this.storePointService.save(point);
                 }
             }*/
            //店铺得分结束

            //计算店铺评分 类型总体得分开始
            /*  List<StoreClass> scs = this.storeClassService.query("select obj from StoreClass obj",
                  null, -1, -1);*/
            //    double description_evaluate;
            /*for (StoreClass sc : scs) {
                description_evaluate = 0.0D;
                service_evaluate = 0.0D;
                ship_evaluate = 0.0D;
                params.clear();
                params.put("sc_id", sc.getId());
                List<StorePoint> sp_list = this.storePointService.query(
                    "select obj from StorePoint obj where obj.store.sc.id=:sc_id", params, -1, -1);
                for (StorePoint sp : sp_list) {
                    description_evaluate = CommUtil.add(Double.valueOf(description_evaluate),
                        sp.getDescription_evaluate());
                    service_evaluate = CommUtil.add(Double.valueOf(service_evaluate),
                        sp.getService_evaluate());
                    ship_evaluate = CommUtil.add(Double.valueOf(ship_evaluate),
                        sp.getShip_evaluate());
                }
                sc.setDescription_evaluate(BigDecimal.valueOf(CommUtil.div(
                    Double.valueOf(description_evaluate), Integer.valueOf(sp_list.size()))));
                sc.setService_evaluate(BigDecimal.valueOf(CommUtil.div(
                    Double.valueOf(service_evaluate), Integer.valueOf(sp_list.size()))));
                sc.setShip_evaluate(BigDecimal.valueOf(CommUtil.div(Double.valueOf(ship_evaluate),
                    Integer.valueOf(sp_list.size()))));
                this.storeClassService.update(sc);
            }*/
            //店铺类型总体得分结束

            //团购的相关处理开始
            Goods goods;
            /*  try {
                List<Group> groups = this.groupService.query(
                    "select obj from Group obj order by obj.createtime", null, -1, -1);
                for (Group group : groups) {
                    if ((group.getBeginTime().before(new Date()))
                        && (group.getEndTime().after(new Date()))) {
                        group.setStatus(0);
                        this.groupService.update(group);
                    }
                    if (group.getEndTime().before(new Date())) {
                        group.setStatus(-2);
                        this.groupService.update(group);
                        for (GroupGoods gg : group.getGg_list()) {
                            gg.setGg_status(-2);
                            this.groupGoodsService.update(gg);
                            goods = gg.getGg_goods();
                            goods.setGroup_buy(0);
                            goods.setGoods_current_price(goods.getStore_price());
                            this.goodsService.update(goods);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            //团购的相关处理结束

            //商城活动开始
            /* try {
                 params.clear();
                 params.put("ac_end_time", new Date());
                 params.put("ac_status", Integer.valueOf(1));
                 List<Activity> acts = this.activityService
                     .query(
                         "select obj from Activity obj where obj.ac_end_time<=:ac_end_time and obj.ac_status=:ac_status",
                         params, -1, -1);
                 for (Activity act : acts) {
                     act.setAc_status(0);
                     this.activityService.update(act);

                     for (ActivityGoods ac : act.getAgs()) {
                         ac.setAg_status(-2);
                         this.activityGoodsService.update(ac);

                         goods = ac.getAg_goods();
                         goods.setActivity_status(0);
                         goods.setGoods_current_price(goods.getStore_price());
                         this.goodsService.update(goods);
                     }

                 }
             } catch (Exception e) {
                 e.printStackTrace();
             }*/
            //商城活动结束

            try {
                int auto_order_notice = this.configService.getSysConfig().getAuto_order_notice();
                cal = Calendar.getInstance();
                params.clear();
                cal.add(6, -auto_order_notice);
                params.put("shipTime", cal.getTime());
                params.put("auto_confirm_email", Boolean.valueOf(true));
                params.put("auto_confirm_sms", Boolean.valueOf(true));
                List<OrderForm> notice_ofs = this.orderFormService
                    .query(
                        "select obj from OrderForm obj where obj.shipTime<=:shipTime and (obj.auto_confirm_email=:auto_confirm_email or obj.auto_confirm_sms=:auto_confirm_sms)",
                        params, -1, -1);
                for (OrderForm of : notice_ofs) {
                    if (!of.isAuto_confirm_email()) {
                        boolean email = send_email(of, "email_tobuyer_order_will_confirm_notify");
                        if (email) {
                            of.setAuto_confirm_email(true);
                            this.orderFormService.update(of);
                        }
                    }
                    if (!of.isAuto_confirm_sms()) {
                        boolean sms = send_sms(of, of.getUser().getMobile(),
                            "sms_tobuyer_order_will_confirm_notify");
                        if (sms) {
                            of.setAuto_confirm_sms(true);
                            this.orderFormService.update(of);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //自动确认收货
            try {
                int auto_order_confirm = this.configService.getSysConfig().getAuto_order_confirm();
                cal = Calendar.getInstance();
                params.clear();
                cal.add(6, -auto_order_confirm);
                params.put("shipTime", cal.getTime());
                params.put("order_status", 30);
                List<OrderForm> confirm_ofs = this.orderFormService
                    .query(
                        "select obj from OrderForm obj  fetch all properties where obj.order_status=:order_status and obj.shipTime<=:shipTime",
                        params, -1, -1);
                OrderFormLog ofl;
                PredepositLog log;
                for (OrderForm of : confirm_ofs) {
                    of.setOrder_status(40);
                    boolean ret = this.orderFormService.update(of);
                    if (ret) {

                        ofl = new OrderFormLog();
                        ofl.setCreatetime(new Date());
                        ofl.setLog_info("系统自动确认收货");
                        ofl.setLog_user(SecurityUserHolder.getCurrentUser());
                        ofl.setOf(of);
                        this.orderFormLogService.save(ofl);
                        if (this.configService.getSysConfig().isEmailEnable()) {
                            send_email(of, "email_toseller_order_receive_ok_notify");
                        }
                        /*if (this.configService.getSysConfig().isSmsEnbale()) {
                            send_sms(of, of.getStore().getUser().getMobile(),
                                "sms_toseller_order_receive_ok_notify");
                        }*/

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                int auto_order_evaluate = this.configService.getSysConfig()
                    .getAuto_order_evaluate();
                cal = Calendar.getInstance();
                params.clear();
                cal.add(6, -auto_order_evaluate);//auto_order_evaluate
                params.put("return_shipTime", cal.getTime());
                params.put("order_status_40", Integer.valueOf(40));
                params.put("order_status_47", Integer.valueOf(47));
                params.put("order_status_48", Integer.valueOf(48));
                params.put("order_status_49", Integer.valueOf(49));
                params.put("order_status_50", Integer.valueOf(50));
                params.put("order_status_60", Integer.valueOf(60));
                List<OrderForm> confirm_evaluate_ofs = this.orderFormService
                    .query(
                        "select obj from OrderForm obj where obj.return_shipTime<=:return_shipTime and obj.order_status>=:order_status_40 and obj.order_status!=:order_status_47 and obj.order_status!=:order_status_48 and obj.order_status!=:order_status_49 and obj.order_status!=:order_status_50 and obj.order_status!=:order_status_60",
                        params, -1, -1);
                for (OrderForm order : confirm_evaluate_ofs) {
                    order.setOrder_status(65);
                    this.orderFormService.update(order);
                    List<SettleLog> sls = this.settleLogService.query(
                        "select obj from SettleLog obj where obj.order.id=" + order.getId(), null,
                        -1, -1);
                    for (SettleLog sl : sls) {
                        sl.setStatus(2);
                        settleLogService.update(sl);
                    }
                }
                int auto_order_return = this.configService.getSysConfig().getAuto_order_return();
                cal = Calendar.getInstance();
                params.clear();
                cal.add(6, -auto_order_return);
                params.put("return_shipTime", cal.getTime());
                params.put("order_status", Integer.valueOf(46));
                List<OrderForm> confirm_return_ofs = this.orderFormService
                    .query(
                        "select obj from OrderForm obj where obj.return_shipTime<=:return_shipTime and obj.order_status=:order_status",
                        params, -1, -1);
                for (OrderForm order : confirm_return_ofs) {
                    order.setOrder_status(49);
                    this.orderFormService.update(order);
                }
                params.clear();

                //买就送套餐结束的店铺，设置其所有买就送结束
                /* params.put("delivery_end_time", new Date());
                 List<DeliveryGoods> dgs = this.deliveryGoodsService
                     .query(
                         "select obj from DeliveryGoods obj where obj.d_goods.goods_store.delivery_end_time<:delivery_end_time",
                         params, -1, -1);
                 for (DeliveryGoods dg : dgs) {
                     //dg.setD_status(-2);
                     //this.deliveryGoodsService.update(dg);

                     goods = dg.getD_goods();
                     goods.setDelivery_status(0);
                     this.goodsService.update(goods);

                     this.deliveryGoodsService.delete(dg.getId());
                 }
                */
                //买就送过了结束时间的，设置结束
                /*  params.clear();
                  params.put("delivery_end_time", new Date());
                  params.put("status", -2);
                  List<DeliveryGoods> dgss = this.deliveryGoodsService
                      .query(
                          "select obj from DeliveryGoods obj where obj.d_status>:status and obj.d_end_time<:delivery_end_time",
                          params, -1, -1);
                  for (DeliveryGoods dg : dgss) {
                      //dg.setD_status(-2);
                      //this.deliveryGoodsService.update(dg);
                      goods = dg.getD_goods();
                      goods.setDelivery_status(0);
                      this.goodsService.update(goods);

                      this.deliveryGoodsService.delete(dg.getId());
                  }
                */
                params.clear();
                params.put("combin_end_time", new Date());
                stores = this.storeService.query(
                    "select obj from Store obj where obj.combin_end_time<=:combin_end_time",
                    params, -1, -1);
                for (Store store : stores) {

                    for (int i = 0; i < store.getGoods_list().size(); i++) {
                        goods = store.getGoods_list().get(i);
                        if (goods.getCombin_status() != 0) {
                            goods.setCombin_begin_time(null);
                            goods.setCombin_end_time(null);
                            goods.setCombin_price(null);
                            goods.setCombin_status(0);
                            goods.getCombin_goods().clear();
                            this.goodsService.update(goods);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            /* try {
                 List<Goods> goods_list = this.evaluateService.query_goods(
                     "select distinct obj.evaluate_goods from Evaluate obj ", null, -1, -1);

                 for (int i = 0; i < goods_list.size(); i++) {
                     goods = goods_list.get(i);
                     description_evaluate = 0.0D;
                     params.clear();
                     params.put("evaluate_goods_id", goods.getId());
                     List<Evaluate> eva_list = this.evaluateService
                         .query(
                             "select obj from Evaluate obj where obj.evaluate_goods.id=:evaluate_goods_id",
                             params, -1, -1);
                     for (Evaluate eva : eva_list) {
                         description_evaluate = CommUtil.add(eva.getDescription_evaluate(),
                             Double.valueOf(description_evaluate));
                     }
                     description_evaluate = CommUtil.div(Double.valueOf(description_evaluate),
                         Integer.valueOf(eva_list.size()));
                     goods.setDescription_evaluate(BigDecimal.valueOf(description_evaluate));
                     this.goodsService.update(goods);
                 }
             } catch (Exception e) {
                 e.printStackTrace();
             }*/

        } catch (Exception ee) {
            ee.printStackTrace();
        }

    }

    private boolean send_email(OrderForm order, String mark) {
        try {
            com.javamalls.platform.domain.Template template = this.templateService
                .getObjByProperty("mark", mark);
            if (template != null && template.isOpen()) {
                String email = order.getStore().getUser().getEmail();
                String subject = template.getTitle();
                String path = System.getProperty("javamalls.root") + "vm" + File.separator;
                PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
                    path + "msg.vm", false), "UTF-8"));
                pwrite.print(template.getContent());
                pwrite.flush();
                pwrite.close();

                Properties p = new Properties();
                p.setProperty("file.resource.loader.path", System.getProperty("javamalls.root")
                                                           + "vm" + File.separator);
                p.setProperty("input.encoding", "UTF-8");
                p.setProperty("output.encoding", "UTF-8");
                Velocity.init(p);
                org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm", "UTF-8");
                VelocityContext context = new VelocityContext();
                context.put("buyer", order.getUser());
                context.put("seller", order.getStore().getUser());
                context.put("config", this.configService.getSysConfig());
                context.put("send_time", CommUtil.formatLongDate(new Date()));
                context.put("webPath", this.configService.getSysConfig().getAddress());
                context.put("order", order);
                StringWriter writer = new StringWriter();
                blank.merge(context, writer);

                String content = writer.toString();
                boolean ret = this.msgTools.sendEmail(email, subject, content);
                return ret;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean send_sms(OrderForm order, String mobile, String mark) {
        try {
            com.javamalls.platform.domain.Template template = this.templateService
                .getObjByProperty("mark", mark);
            if (template != null && template.isOpen()) {
                /*   String path = System.getProperty("javamalls.root") + "vm" + File.separator;
                   PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
                       path + "msg.vm", false), "UTF-8"));
                   pwrite.print(template.getContent());
                   pwrite.flush();
                   pwrite.close();

                   Properties p = new Properties();
                   p.setProperty("file.resource.loader.path", System.getProperty("javamalls.root")
                                                              + "vm" + File.separator);
                   p.setProperty("input.encoding", "UTF-8");
                   p.setProperty("output.encoding", "UTF-8");
                   Velocity.init(p);
                   org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm", "UTF-8");
                   VelocityContext context = new VelocityContext();
                   context.put("buyer", order.getUser());
                   context.put("seller", order.getStore().getUser());
                   context.put("config", this.configService.getSysConfig());
                   context.put("send_time", CommUtil.formatLongDate(new Date()));
                   context.put("webPath", this.configService.getSysConfig().getAddress());
                   context.put("order", order);
                   StringWriter writer = new StringWriter();
                   blank.merge(context, writer);

                   String content = writer.toString();*/
                Map<String, String> map = new HashMap<String, String>();
                User buyer = order.getUser();
                String buyerName = buyer.getUserName();
                if (buyer.getTrueName() != null && !"".equals(buyer.getTrueName())) {
                    buyerName = buyer.getTrueName();
                }
                map.put("buyerName", buyerName);
                User seller = order.getStore().getUser();
                String sellerName = seller.getUserName();
                if (seller.getTrueName() != null && !"".equals(seller.getTrueName())) {
                    sellerName = seller.getTrueName();
                }
                map.put("sellerName", sellerName);
                map.put("order_id", order.getOrder_id());

                boolean ret = this.msgTools.sendSMS(mobile, template.getTitle(), map);
                //  boolean ret = this.msgTools.sendSMS(mobile, content);
                return ret;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

}
