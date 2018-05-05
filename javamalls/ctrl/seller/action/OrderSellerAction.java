package com.javamalls.ctrl.seller.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import shaded.com.google.gson.Gson;

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.constant.Constant;
import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.TimeUtil;
import com.javamalls.ctrl.admin.tools.MsgTools;
import com.javamalls.ctrl.admin.tools.PaymentTools;
import com.javamalls.front.web.tools.GoodsViewTools;
import com.javamalls.front.web.tools.StoreViewTools;
import com.javamalls.payment.alipay.config.AlipayConfig;
import com.javamalls.payment.alipay.util.AlipaySubmit;
import com.javamalls.payment.chinabank.h5.util.JsonUtil;
import com.javamalls.platform.domain.Address;
import com.javamalls.platform.domain.Area;
import com.javamalls.platform.domain.Evaluate;
import com.javamalls.platform.domain.ExpressCompany;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.GoodsCart;
import com.javamalls.platform.domain.GoodsClass;
import com.javamalls.platform.domain.GoodsItem;
import com.javamalls.platform.domain.GoodsReturn;
import com.javamalls.platform.domain.GoodsReturnItem;
import com.javamalls.platform.domain.GoodsReturnLog;
import com.javamalls.platform.domain.GoodsSpecProperty;
import com.javamalls.platform.domain.GroupGoods;
import com.javamalls.platform.domain.IntegralLog;
import com.javamalls.platform.domain.OrderForm;
import com.javamalls.platform.domain.OrderFormCancelAudit;
import com.javamalls.platform.domain.OrderFormFile;
import com.javamalls.platform.domain.OrderFormLog;
import com.javamalls.platform.domain.Payment;
import com.javamalls.platform.domain.RefundLog;
import com.javamalls.platform.domain.SettleAccunts;
import com.javamalls.platform.domain.SettleLog;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.WarehouseGoodsItem;
import com.javamalls.platform.domain.enums.OrderStatusEnum;
import com.javamalls.platform.domain.enums.State;
import com.javamalls.platform.domain.query.AddressQueryObject;
import com.javamalls.platform.domain.query.EvaluateQueryObject;
import com.javamalls.platform.domain.query.GoodsCartQueryObject;
import com.javamalls.platform.domain.query.GoodsItemQueryObject;
import com.javamalls.platform.domain.query.OrderFormQueryObject;
import com.javamalls.platform.domain.query.SettleLogQueryObject;
import com.javamalls.platform.domain.query.UserQueryObject;
import com.javamalls.platform.domain.virtual.TransInfo;
import com.javamalls.platform.service.IAddressService;
import com.javamalls.platform.service.IAreaService;
import com.javamalls.platform.service.IEvaluateService;
import com.javamalls.platform.service.IExpressCompanyService;
import com.javamalls.platform.service.IGoodsCartService;
import com.javamalls.platform.service.IGoodsItemService;
import com.javamalls.platform.service.IGoodsReturnItemService;
import com.javamalls.platform.service.IGoodsReturnLogService;
import com.javamalls.platform.service.IGoodsReturnService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IGoodsSpecPropertyService;
import com.javamalls.platform.service.IGroupGoodsService;
import com.javamalls.platform.service.IIntegralLogService;
import com.javamalls.platform.service.IOrderFormCancelAuditService;
import com.javamalls.platform.service.IOrderFormFileService;
import com.javamalls.platform.service.IOrderFormLogService;
import com.javamalls.platform.service.IOrderFormPayLogService;
import com.javamalls.platform.service.IOrderFormService;
import com.javamalls.platform.service.IPaymentService;
import com.javamalls.platform.service.IRefundLogService;
import com.javamalls.platform.service.ISettleAccountsService;
import com.javamalls.platform.service.ISettleLogService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.ITemplateService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;
import com.javamalls.platform.service.IWarehouseGoodsItemService;
import com.javamalls.platform.vo.OrderFormVo;
import com.utils.SendReqAsync;

@Controller
public class OrderSellerAction {
    @Autowired
    private ISysConfigService            configService;
    @Autowired
    private IUserConfigService           userConfigService;
    @Autowired
    private IOrderFormService            orderFormService;
    @Autowired
    private IOrderFormLogService         orderFormLogService;
    @Autowired
    private IRefundLogService            refundLogService;
    @Autowired
    private IGoodsService                goodsService;
    @Autowired
    private IGoodsReturnService          goodsReturnService;
    @Autowired
    private IGoodsReturnItemService      goodsReturnItemService;
    @Autowired
    private IGoodsReturnLogService       goodsReturnLogService;
    @Autowired
    private IGoodsCartService            goodsCartService;
    @Autowired
    private IEvaluateService             evaluateService;
    @Autowired
    private IUserService                 userService;
    @Autowired
    private IIntegralLogService          integralLogService;
    @Autowired
    private IGroupGoodsService           groupGoodsService;
    @Autowired
    private ITemplateService             templateService;
    @Autowired
    private IPaymentService              paymentService;
    @Autowired
    private IExpressCompanyService       expressCompayService;
    @Autowired
    private StoreViewTools               storeViewTools;
    @Autowired
    private MsgTools                     msgTools;
    @Autowired
    private PaymentTools                 paymentTools;
    @Autowired
    private ISettleLogService            settleLogService;
    @Autowired
    private ISettleAccountsService       settleAccountsService;
    @Autowired
    private IGoodsItemService            goodsItemService;
    @Autowired
    private IWarehouseGoodsItemService   warehouseGoodsItemService;
    @Autowired
    private GoodsViewTools               goodsViewTools;
    @Autowired
    private IAddressService              addressService;
    @Autowired
    private IAreaService                 areaService;
    @Autowired
    private IOrderFormFileService        orderFormFileService;
    @Autowired
    private IGoodsSpecPropertyService    goodsSpecPropertyService;
    @Autowired
    private SendReqAsync                 sendReqAsync;
    @Autowired
    private IOrderFormCancelAuditService orderFormCancelAuditService;
    @Autowired
    private IOrderFormPayLogService      orderFormPayLogService;

    /**
     * 结算账户设置
     */
    @RequestMapping({ "/seller/setaccount.htm" })
    public ModelAndView setaccount(HttpServletRequest request, HttpServletResponse response,
                                   String bankName, String accountName, String bankAccount) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/setaccount.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if (null != bankName && !"".equals(bankName) && null != accountName
            && !"".equals(accountName) && null != bankAccount && !"".equals(bankAccount)) {
            User user = SecurityUserHolder.getCurrentUser();
            user.setBankName(bankName);
            user.setAccountName(accountName);
            user.setBankAccount(bankAccount);
            userService.update(user);
            mv.addObject("user", user);
        }
        return mv;
    }

    /*
     * 買家結算
     */
    @RequestMapping({ "/seller/mybill.htm" })
    public ModelAndView sellersettle_log_list(HttpServletRequest request,
                                              HttpServletResponse response, String currentPage,
                                              String orderBy, String orderType, String type,
                                              String settle_code, String start_count,
                                              String end_count, String settle_date,
                                              String end_date, String storeId) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/mybill.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        User user = SecurityUserHolder.getCurrentUser();
        if (storeId != null && !"".equals(storeId) && user != null
            && storeId.equals(user.getStore().getId().toString())) {
            List<Map<String, Object>> logs = this.settleLogService
                .query("select new map(sum(obj.sale_account) as sale_account,"
                       + "sum(obj.sale_yongjin) as sale_yongjin,"
                       + "sum(obj.settle_account) as settle_account) "
                       + " from SettleLog obj where obj.status = 2 and obj.disabled = 0 ");

            if (logs != null && logs.size() > 0) {
                Map<String, Object> m = logs.get(0);
                String sale_account = CommUtil.null2String(m.get("sale_account"));
                String sale_yongjin = CommUtil.null2String(m.get("sale_yongjin"));
                String settle_account = CommUtil.null2String(m.get("settle_account"));
                mv.addObject("sale_account", sale_account);
                mv.addObject("sale_yongjin", sale_yongjin);
                mv.addObject("settle_account", settle_account);
            }

            String msg = "今天不是本月结算日，不能结算！";
            if (type != null && !"".equals(type)) {
                int month = Integer.valueOf(TimeUtil.getMonth());
                SettleAccunts settleAccunts = null;
                List<SettleAccunts> settles = settleAccountsService.query(
                    "select obj from SettleAccunts obj where obj.month = " + month, null, -1, -1);
                if (settles != null && settles.size() > 0) {
                    settleAccunts = settles.get(0);
                    String day = Integer.valueOf(TimeUtil.getDay()) + "";
                    mv.addObject("settleDate",
                        CommUtil.substringStartEnd(settleAccunts.getSettle_date()));
                    boolean flag = settleAccunts.getSettle_date().contains("," + day + ",");
                    if (!flag) {// 非结算日
                        mv.addObject("msg", msg);
                        // settle_date = TimeUtil.getYearMonth();
                        IPageList pList = querySettleLogList(currentPage, orderBy, orderType, type,
                            settle_code, start_count, end_count, settle_date, end_date, mv, null,
                            storeId);
                        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
                    } else {// 是结算日
                        IPageList pList = querySettleLogList(currentPage, orderBy, orderType, type,
                            settle_code, start_count, end_count, settle_date, end_date, mv, null,
                            storeId);
                        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
                    }
                }
            } else {
                mv.addObject("msg", msg);
            }
        }

        mv.addObject("type", type);
        mv.addObject("storeId", storeId);
        String curDay = TimeUtil.getChineseToDay();
        mv.addObject("curDay", curDay);
        return mv;
    }

    private IPageList querySettleLogList(String currentPage, String orderBy, String orderType,
                                         String type, String settle_code, String start_count,
                                         String end_count, String settle_date, String end_date,
                                         ModelAndView mv, Integer pageSize, String storeId) {

        String queryTime = "obj.createtime";
        if (Integer.valueOf(type) > 2)
            queryTime = "obj.income_time";
        SettleLogQueryObject qo = new SettleLogQueryObject(currentPage, mv, orderBy, orderType);
        if (pageSize != null)
            qo.setPageSize(pageSize);
        qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");
        qo.addQuery("obj.status", new SysMap("status", Integer.valueOf(type)), "=");
        if (settle_code != null && !"".equals(settle_code)) {
            qo.addQuery("obj.code", new SysMap("code", settle_code), "=");
            mv.addObject("settle_code", settle_code);
        }
        if (storeId != null && !"".equals(storeId)) {
            qo.addQuery("obj.order.store.id", new SysMap("storeId", Long.valueOf(storeId)), "=");
        }

        if (start_count != null && !"".equals(start_count)) {
            qo.addQuery("obj.settle_account",
                new SysMap("start_account", BigDecimal.valueOf(CommUtil.null2Double(start_count))),
                ">=");
            mv.addObject("start_count", start_count);
        }
        if (end_count != null && !"".equals(end_count)) {
            qo.addQuery("obj.settle_account",
                new SysMap("end_account", BigDecimal.valueOf(CommUtil.null2Double(end_count))),
                "<=");
            mv.addObject("end_count", end_count);
        }
        if (!CommUtil.null2String(settle_date).equals("")) {
            qo.addQuery(queryTime, new SysMap("income_time", CommUtil.formatDate(settle_date)),
                ">=");
            mv.addObject("settle_date", settle_date);
        }
        if (!CommUtil.null2String(end_date).equals("")) {
            qo.addQuery(queryTime, new SysMap("end_date", CommUtil.formatDate(end_date)), "<=");
            mv.addObject("end_date", end_date);
        }
        if (CommUtil.null2String(end_date).equals("")
            && CommUtil.null2String(settle_date).equals("")) {
            qo.addQuery(queryTime,
                new SysMap("income_time", CommUtil.formatDate(TimeUtil.getYearMonth())), ">=");
        }
        qo.setOrderBy(orderBy);
        qo.setOrderType(orderType);
        IPageList pList = this.settleLogService.list(qo);
        return pList;
    }

    /**
     * 卖家商品评价
     * 
     * @param request
     * @param response
     * @param currentPage
     * @param order_id
     * @param beginTime
     * @param endTime
     * @param order_status
     * @return
     */
    @RequestMapping({ "/seller/pingjia.htm" })
    public ModelAndView pingjia(HttpServletRequest request, HttpServletResponse response,
                                String currentPage, String order_id, String beginTime,
                                String endTime, String order_status) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller_pingjia.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        EvaluateQueryObject ofqo = new EvaluateQueryObject(currentPage, mv, "createtime", "desc");
        ofqo.addQuery("obj.evaluate_goods.goods_store.id", new SysMap("store_id",
            SecurityUserHolder.getCurrentUser().getStore().getId()), "=");
        IPageList pList = evaluateService.list(ofqo);
        mv.addObject("objs", pList.getResult());
        return mv;
    }

    @SecurityMapping(title = "卖家订单列表", value = "/seller/order.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/order.htm" })
    public ModelAndView order(HttpServletRequest request, HttpServletResponse response,
                              String currentPage, String order_status, String order_id,
                              String beginTime, String endTime, String buyer_userName,
                              String goods_name) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller_order.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv, "createtime", "desc");
        // ofqo.addQuery("obj.store.user.id", new SysMap("user_id",
        // SecurityUserHolder.getCurrentUser().getId()), "=");
        ofqo.addQuery("obj.store.id", new SysMap("store_id", SecurityUserHolder.getCurrentUser()
            .getStore().getId()), "=");

        if (!CommUtil.null2String(order_status).equals("")) {

            ofqo.addQuery("obj.order_status",
                new SysMap("order_status", OrderStatusEnum.getEnumIndexByName(order_status)), "=");
        }

        if (!CommUtil.null2String(order_id).equals("")) {
            ofqo.addQuery("obj.order_id", new SysMap("order_id", "%" + order_id + "%"), "like");
        }
        if (!CommUtil.null2String(beginTime).equals("")) {
            ofqo.addQuery("obj.createtime",
                new SysMap("beginTime", CommUtil.formatMaxDate(beginTime + " 00:00:00")), ">=");
        }
        if (!CommUtil.null2String(endTime).equals("")) {
            ofqo.addQuery("obj.createtime",
                new SysMap("endTime", CommUtil.formatMaxDate(endTime + " 23:59:59")), "<=");
        }
        if (!CommUtil.null2String(buyer_userName).equals("")) {
            ofqo.addQuery("obj.user.userName", new SysMap("userName", buyer_userName), "=");
        }

        if (!CommUtil.null2String(goods_name).equals("")) {
            mv.addObject("goods_name", goods_name);
            GoodsCartQueryObject gcqo = new GoodsCartQueryObject(currentPage, mv, "createtime",
                "desc");
            gcqo.addQuery("obj.goods.goods_name", new SysMap("goods_name", "%" + goods_name + "%"),
                "like");
            IPageList pListgc = this.goodsCartService.list(gcqo);
            List<GoodsCart> idlist = pListgc.getResult();
            Map<String, Object> map = new HashMap<String, Object>();
            Set<Long> ids = new HashSet<Long>();
            if (null != idlist) {
                for (GoodsCart gc : idlist) {
                    if (null != gc.getOf())
                        ids.add(gc.getOf().getId());
                }
            } else {
                ids.add(-1L);
            }
            map.put("ids", ids);
            ofqo.addQuery("obj.id in(:ids)", map);
        }
        ofqo.addQuery("obj.disabled", new SysMap("disabled", false), "=");
        IPageList pList = this.orderFormService.list(ofqo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        mv.addObject("storeViewTools", this.storeViewTools);
        mv.addObject("order_id", order_id);
        mv.addObject("goods_name", goods_name);
        mv.addObject("order_status", order_status == null ? "all" : order_status);
        mv.addObject("beginTime", beginTime);
        mv.addObject("endTime", endTime);
        mv.addObject("buyer_userName", buyer_userName);
        mv.addObject("auDay", this.configService.getSysConfig().getAuto_order_confirm());
        return mv;
    }

    @SecurityMapping(title = "卖家订单详情", value = "/seller/order_view.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/order_view.htm" })
    public ModelAndView order_view(HttpServletRequest request, HttpServletResponse response,
                                   String id) {
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/seller/seller_order_view.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (obj.getStore().getId().equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
            mv.addObject("obj", obj);
            TransInfo transInfo = query_ship_getData(CommUtil.null2String(obj.getId()));
            mv.addObject("transInfo", transInfo);
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "您店铺中没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "卖家取消订单", value = "/seller/order_cancel.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/order_cancel.htm" })
    public ModelAndView order_cancel(HttpServletRequest request, HttpServletResponse response,
                                     String id, String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller_order_cancel.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (obj.getStore().getId().equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
            mv.addObject("obj", obj);
            mv.addObject("currentPage", currentPage);
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "卖家取消订单保存", value = "/seller/order_cancel_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/order_cancel_save.htm" })
    public String order_cancel_save(HttpServletRequest request, HttpServletResponse response,
                                    String id, String currentPage, String state_info,
                                    String other_state_info) throws Exception {
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (obj.getStore().getId().equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
            obj.setOrder_status(0);
            this.orderFormService.update(obj);
            OrderFormLog ofl = new OrderFormLog();
            ofl.setCreatetime(new Date());
            ofl.setLog_info("取消订单");
            ofl.setLog_user(SecurityUserHolder.getCurrentUser());
            ofl.setOf(obj);
            if (state_info.equals("other")) {
                ofl.setState_info(other_state_info);
            } else {
                ofl.setState_info(state_info);
            }
            this.orderFormLogService.save(ofl);
            //调用订单接口
            OrderFormVo orderFormVo = new OrderFormVo();
            orderFormVo.setId(obj.getId());
            orderFormVo.setCreatetime(obj.getCreatetime());
            orderFormVo.setDisabled(obj.isDisabled());
            orderFormVo.setOrder_id(obj.getOrder_id());
            orderFormVo.setOrder_status(obj.getOrder_status());
            orderFormVo.setStore_id(obj.getStore().getId());
            orderFormVo.setUser_id(obj.getUser().getId());
            String write2JsonStr = JsonUtil.write2JsonStr(orderFormVo);
            sendReqAsync
                .sendMessageUtil(Constant.STORE_ORDERFORM_URL_EDIT, write2JsonStr, "卖家取消订单");
            System.out.println(write2JsonStr);
            /*  if (this.configService.getSysConfig().isEmailEnable()) {
                  send_email(request, obj, "email_tobuyer_order_cancel_notify");
              }
              if (this.configService.getSysConfig().isSmsEnbale()) {
                  send_sms(request, obj, obj.getUser().getMobile(), "sms_tobuyer_order_cancel_notify");
              }*/
        }
        return "redirect:order.htm?currentPage=" + currentPage;
    }

    @SecurityMapping(title = "卖家调整订单费用", value = "/seller/order_fee.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/order_fee.htm" })
    public ModelAndView order_fee(HttpServletRequest request, HttpServletResponse response,
                                  String id, String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller_order_fee.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (obj.getStore().getId().equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
            mv.addObject("obj", obj);
            mv.addObject("currentPage", currentPage);
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "卖家调整订单费用保存", value = "/seller/order_fee_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/order_fee_save.htm" })
    public String order_fee_save(HttpServletRequest request, HttpServletResponse response,
                                 String id, String currentPage, String goods_amount,
                                 String ship_price, String totalPrice) throws Exception {
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (obj.getStore().getId().equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
            obj.setGoods_amount(BigDecimal.valueOf(CommUtil.null2Double(goods_amount)));
            obj.setShip_price(BigDecimal.valueOf(CommUtil.null2Double(ship_price)));
            obj.setTotalPrice(BigDecimal.valueOf(CommUtil.null2Double(totalPrice)));
            this.orderFormService.update(obj);
            OrderFormLog ofl = new OrderFormLog();
            ofl.setCreatetime(new Date());
            ofl.setLog_info("调整订单费用");
            ofl.setLog_user(SecurityUserHolder.getCurrentUser());
            ofl.setOf(obj);
            this.orderFormLogService.save(ofl);
            if (this.configService.getSysConfig().isEmailEnable()) {
                send_email(request, obj, "email_tobuyer_order_update_fee_notify");
            }
            if (this.configService.getSysConfig().isSmsEnbale()) {
                send_sms(request, obj, obj.getUser().getMobile(), "sms_tobuyer_order_fee_notify");
            }
        }
        return "redirect:order.htm?currentPage=" + currentPage;
    }

    @SecurityMapping(title = "线下付款确认", value = "/seller/seller_order_outline.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/seller_order_outline.htm" })
    public ModelAndView seller_order_outline(HttpServletRequest request,
                                             HttpServletResponse response, String id,
                                             String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller_order_outline.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (obj.getStore().getId().equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
            mv.addObject("obj", obj);
            mv.addObject("currentPage", currentPage);
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "线下付款确认保存", value = "/seller/seller_order_outline_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/seller_order_outline_save.htm" })
    public String seller_order_outline_save(HttpServletRequest request,
                                            HttpServletResponse response, String id,
                                            String currentPage, String state_info) throws Exception {
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (obj.getStore().getId().equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
            obj.setOrder_status(20);
            this.orderFormService.update(obj);
            for (GoodsCart gc : obj.getGcs()) {
                Goods goods = gc.getGoods();
                if ((goods.getGroup() != null) && (goods.getGroup_buy() == 2)) {
                    for (GroupGoods gg : goods.getGroup_goods_list()) {
                        if (gg.getGroup().equals(goods.getGroup().getId())) {
                            gg.setGg_count(gg.getGg_count() - gc.getCount());
                            gg.setGg_def_count(gg.getGg_def_count() + gc.getCount());
                            this.groupGoodsService.update(gg);
                        }
                    }
                }
                List<String> gsps = new ArrayList<String>();
                for (GoodsSpecProperty gsp : gc.getGsps()) {
                    gsps.add(gsp.getId().toString());
                }
                String[] gsp_list = new String[gsps.size()];
                gsps.toArray(gsp_list);
                goods.setGoods_salenum(goods.getGoods_salenum() + gc.getCount());
                String inventory_type = goods.getInventory_type() == null ? "all" : goods
                    .getInventory_type();
                Map<String, Object> temp;
                //减库存    为了保持库存统一总库存和规格属性库存都要减
                /**
                 * 订货宝库存更新-线下支付
                 */
                //   goods.setGoods_inventory(goods.getGoods_inventory() - gc.getCount());
                if (!inventory_type.equals("all")) {/*
                                                    List<HashMap> list = (List) Json.fromJson(ArrayList.class,
                                                    goods.getGoods_inventory_detail());
                                                    for (Iterator localIterator4 = list.iterator(); localIterator4.hasNext();) {
                                                    temp = (Map) localIterator4.next();
                                                    String[] temp_ids = CommUtil.null2String(temp.get("id")).split("_");
                                                    Arrays.sort(temp_ids);
                                                    Arrays.sort(gsp_list);
                                                    if (Arrays.equals(temp_ids, gsp_list)) {
                                                    temp.put(
                                                    "count",
                                                    Integer.valueOf(CommUtil.null2Int(temp.get("count"))
                                                    - gc.getCount()));
                                                    }
                                                    }
                                                    goods.setGoods_inventory_detail(Json.toJson(list, JsonFormat.compact()));
                                                    */
                    List<GoodsItem> goods_items = new ArrayList<GoodsItem>();
                    HashMap<String, Object> paramMap = new HashMap<String, Object>();
                    paramMap.put("goodsId", goods.getId());
                    goods_items = this.goodsItemService
                        .query(
                            "select obj from GoodsItem obj where obj.disabled=false and  obj.goods.id =:goodsId",
                            paramMap, -1, -1);
                    if (goods_items != null && goods_items.size() > 0) {
                        if (goods_items.size() > 1) {
                            for (GoodsItem item : goods_items) {
                                if (item.getSpec_combination() != null
                                    && !"".equals(item.getSpec_combination())) {
                                    String[] temp_ids = item.getSpec_combination().split("_");
                                    Arrays.sort(temp_ids);
                                    Arrays.sort(gsp_list);
                                    if (Arrays.equals(temp_ids, gsp_list)) {
                                        item.setGoods_inventory(item.getGoods_inventory()
                                                                - gc.getCount());
                                        Map<String, Object> map = new HashMap<String, Object>();
                                        map.put("goodsItem_id", item.getId());
                                        //更新库存商品的库存
                                        List<WarehouseGoodsItem> list = this.warehouseGoodsItemService
                                            .query(
                                                "select obj from WarehouseGoodsItem obj where obj.goods_item.id=:goodsItem_id",
                                                map, 0, 1);
                                        if (list != null && list.size() > 0) {
                                            WarehouseGoodsItem warehouseGoodsItem = list.get(0);
                                            warehouseGoodsItem
                                                .setWarehoust_number(warehouseGoodsItem
                                                    .getWarehoust_number() - gc.getCount());
                                            this.warehouseGoodsItemService
                                                .update(warehouseGoodsItem);

                                        }
                                    }
                                }
                            }
                        } else {
                            GoodsItem item = goods_items.get(0);
                            item.setGoods_inventory(item.getGoods_inventory() - gc.getCount());
                            //商品库存更新
                            goods.setGoods_inventory(item.getGoods_inventory());

                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("goodsItem_id", item.getId());
                            //更新库存商品的库存
                            List<WarehouseGoodsItem> list = this.warehouseGoodsItemService
                                .query(
                                    "select obj from WarehouseGoodsItem obj where obj.goods_item.id=:goodsItem_id",
                                    map, 0, 1);
                            if (list != null && list.size() > 0) {
                                WarehouseGoodsItem warehouseGoodsItem = list.get(0);
                                warehouseGoodsItem.setWarehoust_number(warehouseGoodsItem
                                    .getWarehoust_number() - gc.getCount());
                                this.warehouseGoodsItemService.update(warehouseGoodsItem);

                            }
                        }
                    }
                    goods.setGoods_item_list(goods_items);
                }
                for (GroupGoods gg : goods.getGroup_goods_list()) {
                    if ((gg.getGroup().getId().equals(goods.getGroup().getId()))
                        && (gg.getGg_count() == 0)) {
                        goods.setGroup_buy(3);
                    }
                }
                this.goodsService.update(goods);
            }
            OrderFormLog ofl = new OrderFormLog();
            ofl.setCreatetime(new Date());
            ofl.setLog_info("确认线下付款");
            ofl.setLog_user(SecurityUserHolder.getCurrentUser());
            ofl.setOf(obj);
            ofl.setState_info(state_info);
            this.orderFormLogService.save(ofl);
            if (this.configService.getSysConfig().isEmailEnable()) {
                send_email(request, obj, "email_tobuyer_order_outline_pay_ok_notify");
            }
            if (this.configService.getSysConfig().isSmsEnbale()) {
                send_sms(request, obj, obj.getUser().getMobile(),
                    "sms_tobuyer_order_outline_pay_ok_notify");
            }
            update_goods_bind(obj, null);
        }
        return "redirect:order.htm?currentPage=" + currentPage;
    }

    @SecurityMapping(title = "卖家确认发货", value = "/seller/order_shipping.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/order_shipping.htm" })
    public ModelAndView order_shipping(HttpServletRequest request, HttpServletResponse response,
                                       String id, String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller_order_shipping.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if ((obj.getOrder_status() == 16 || obj.getOrder_status() == 20)
            && obj.getStore().getId()
                .equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
            mv.addObject("obj", obj);
            mv.addObject("currentPage", currentPage);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("oid", CommUtil.null2Long(id));
            List<GoodsCart> goodsCarts = this.goodsCartService.query(
                "select obj from GoodsCart obj where obj.of.id = :oid", map, -1, -1);
            List<GoodsCart> deliveryGoods = new ArrayList<GoodsCart>();
            boolean physicalGoods = false;
            for (GoodsCart gc : goodsCarts) {
                if (gc.getGoods().getGoods_choice_type() == 1) {
                    deliveryGoods.add(gc);
                } else {
                    physicalGoods = true;
                }
            }
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("status", Integer.valueOf(0));
            Object expressCompanys = this.expressCompayService
                .query(
                    "select obj from ExpressCompany obj where obj.company_status=:status order by company_sequence asc",
                    params, -1, -1);
            mv.addObject("expressCompanys", expressCompanys);
            mv.addObject("physicalGoods", Boolean.valueOf(physicalGoods));
            mv.addObject("deliveryGoods", deliveryGoods);
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "卖家确认发货保存", value = "/seller/order_shipping_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/order_shipping_save.htm" })
    public String order_shipping_save(HttpServletRequest request, HttpServletResponse response,
                                      String id, String currentPage, String shipCode,
                                      String state_info, String order_seller_intro, String ec_id)
                                                                                                 throws Exception {
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        ExpressCompany ec = this.expressCompayService.getObjById(CommUtil.null2Long(ec_id));
        if (obj.getStore().getId().equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
            //验证库存
            boolean inventoryFlag = false;
            for (GoodsCart gc : obj.getGcs()) {
                /* if (gc.getGoods().getGoods_inventory() > gc.getCount()) {
                     inventoryFlag = true;
                 }*/
                if (gc.getSpec_id() == null || "".equals(gc.getSpec_id())) {
                    GoodsItem item = gc.getGoods().getGoods_item_list().get(0);

                    if ((item.getGoods_inventory() + gc.getCount()) >= gc.getCount()) {
                        inventoryFlag = true;
                    }
                } else {
                    String[] gsp_ids = gc.getSpec_id().split(",");
                    Arrays.sort(gsp_ids, new Comparator<String>() {
                        @Override
                        public int compare(String arg0, String arg1) {
                            return Integer.valueOf(arg0).compareTo(Integer.valueOf(arg1));
                        }
                    });
                    String spec_combination = "";
                    for (int i = 0; i < gsp_ids.length; i++) {
                        spec_combination = spec_combination + gsp_ids[i] + "_";
                    }
                    List<GoodsItem> goodsItems = gc.getGoods().getGoods_item_list();
                    if (goodsItems != null && goodsItems.size() > 0) {
                        for (GoodsItem item : goodsItems) {
                            if (spec_combination.equals(item.getSpec_combination())) {
                                if ((item.getGoods_inventory() + gc.getCount()) >= gc.getCount()) {
                                    inventoryFlag = true;
                                }
                            }

                        }
                    }
                }
            }
            if (inventoryFlag) {
                obj.setOrder_status(30);
                obj.setShipCode(shipCode);
                obj.setShipTime(new Date());
                obj.setEc(ec);
                obj.setOrder_seller_intro(order_seller_intro);
                this.orderFormService.update(obj);
                OrderFormLog ofl = new OrderFormLog();
                ofl.setCreatetime(new Date());
                ofl.setLog_info("确认发货");
                ofl.setState_info(state_info);
                ofl.setLog_user(SecurityUserHolder.getCurrentUser());
                ofl.setOf(obj);
                this.orderFormLogService.save(ofl);

                //调用订单接口
                OrderFormVo orderFormVo = new OrderFormVo();
                orderFormVo.setId(obj.getId());
                orderFormVo.setCreatetime(obj.getCreatetime());
                orderFormVo.setDisabled(obj.isDisabled());
                orderFormVo.setOrder_id(obj.getOrder_id());
                orderFormVo.setOrder_status(obj.getOrder_status());
                orderFormVo.setStore_id(obj.getStore().getId());
                orderFormVo.setUser_id(obj.getUser().getId());
                String write2JsonStr = JsonUtil.write2JsonStr(orderFormVo);
                sendReqAsync.sendMessageUtil(Constant.STORE_ORDERFORM_URL_EDIT, write2JsonStr,
                    "卖家确认发货");
                System.out.println(write2JsonStr);

                if (this.configService.getSysConfig().isEmailEnable()) {
                    send_email(request, obj, "email_tobuyer_order_ship_notify");
                }
                if (this.configService.getSysConfig().isSmsEnbale()) {
                    send_sms(request, obj, obj.getUser().getMobile(),
                        "sms_tobuyer_order_ship_notify");
                }
                if (obj.getPayment().getMark().equals("alipay")) {
                    boolean synch = false;
                    String safe_key = "";
                    String partner = "";
                    if (!CommUtil.null2String(obj.getPayment().getSafeKey()).equals("")) {
                        if (!CommUtil.null2String(obj.getPayment().getPartner()).equals("")) {
                            safe_key = obj.getPayment().getSafeKey();
                            partner = obj.getPayment().getPartner();
                            synch = true;
                        }
                    }

                    if (!synch) { // 如果synch为false,继续判断
                        Map<String, Object> params = new HashMap<String, Object>();
                        params.put("type", "admin");
                        params.put("mark", "alipay");
                        List<Payment> payments = this.paymentService.query(
                            "select obj from Payment obj where obj.type=:type and obj.mark=:mark",
                            params, -1, -1);
                        if ((payments.size() > 0) && (payments.get(0) != null)) {
                            if (!CommUtil.null2String(((Payment) payments.get(0)).getSafeKey())
                                .equals("")) {
                                if (!CommUtil.null2String(((Payment) payments.get(0)).getPartner())
                                    .equals("")) {
                                    safe_key = ((Payment) payments.get(0)).getSafeKey();
                                    partner = ((Payment) payments.get(0)).getPartner();
                                    synch = true;
                                }
                            }
                        }
                    }
                    if (synch) {
                        AlipayConfig config = new AlipayConfig();
                        config.setKey(safe_key);
                        config.setPartner(partner);
                        Map<String, String> sParaTemp = new HashMap<String, String>();
                        sParaTemp.put("service", "send_goods_confirm_by_platform");
                        sParaTemp.put("partner", config.getPartner());
                        sParaTemp.put("_input_charset", config.getInput_charset());
                        sParaTemp.put("trade_no", obj.getOut_order_id());
                        sParaTemp.put("logistics_name", ec.getCompany_name());
                        sParaTemp.put("invoice_no", shipCode);
                        sParaTemp.put("transport_type", ec.getCompany_type());

                        String str1 = AlipaySubmit.buildRequest(config, "web", sParaTemp, "", "");
                    }
                }
            } else {
                request.getSession(false).setAttribute("op_title", "商品库存不足");
                request.getSession(false).setAttribute("url",
                    CommUtil.getURL(request) + "/seller/order.htm?currentPage=" + currentPage);
                return "redirect:/error.htm";
            }
        }
        return "redirect:order.htm?currentPage=" + currentPage;
    }

    @SecurityMapping(title = "卖家修改物流", value = "/seller/order_shipping_code.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/order_shipping_code.htm" })
    public ModelAndView order_shipping_code(HttpServletRequest request,
                                            HttpServletResponse response, String id,
                                            String currentPage) {
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/seller_order_shipping_code.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (obj.getStore().getId().equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
            mv.addObject("obj", obj);
            mv.addObject("currentPage", currentPage);
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "卖家修改物流保存", value = "/seller/order_shipping_code_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/order_shipping_code_save.htm" })
    public String order_shipping_code_save(HttpServletRequest request,
                                           HttpServletResponse response, String id,
                                           String currentPage, String shipCode, String state_info) {
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (obj.getStore().getId().equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
            obj.setShipCode(shipCode);
            this.orderFormService.update(obj);
            OrderFormLog ofl = new OrderFormLog();
            ofl.setCreatetime(new Date());
            ofl.setLog_info("修改物流信息");
            ofl.setState_info(state_info);
            ofl.setLog_user(SecurityUserHolder.getCurrentUser());
            ofl.setOf(obj);
            this.orderFormLogService.save(ofl);
        }
        return "redirect:order.htm?currentPage=" + currentPage;
    }

    @SecurityMapping(title = "卖家审核取消订单", value = "/seller/order_cancel_audit.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/order_cancel_audit.htm" })
    public ModelAndView order_cancel_audit(HttpServletRequest request,
                                           HttpServletResponse response, String id,
                                           String currentPage) {
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/seller_order_cancel_audit.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        OrderFormCancelAudit audit = null;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("storeId", SecurityUserHolder.getCurrentUser().getStore().getId());
        map.put("orderId", CommUtil.null2Long(id));
        map.put("state", 1);
        List<OrderFormCancelAudit> auditList = this.orderFormCancelAuditService.query(
            "" + "select obj from OrderFormCancelAudit obj " + " where obj.disabled = false "
                    + " and obj.store.id =:storeId "
                    + " and obj.state =:state and obj.of.id =:orderId", map, -1, -1);
        if (auditList != null && auditList.size() > 0) {
            audit = auditList.get(0);
        }
        if (audit != null) {
            mv.addObject("reason", State.getValue(State.CANCEL_REASON, audit.getCancel_reason()));
            mv.addObject("obj", audit);
            mv.addObject("currentPage", currentPage);
            mv.addObject("paymentTools", this.paymentTools);
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "您没有编号为" + id + "的待审核取消订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "卖家审核取消订单保存", value = "/seller/order_cancel_audit_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/order_cancel_audit_save.htm" })
    public String order_cancel_audit_save(HttpServletRequest request, HttpServletResponse response,
                                          String id, String currentPage, String state) {
        OrderFormCancelAudit obj = this.orderFormCancelAuditService.getObjById(CommUtil
            .null2Long(id));
        OrderForm of = this.orderFormService.getObjById(obj.getOf().getId());
        if (of.getOrder_status() == 25) {
            if (CommUtil.null2Int(state) == 2) {//已通过
                of.setOrder_status(0);
                this.orderFormService.update(of);

                obj.setAudit_user(SecurityUserHolder.getCurrentUser());
                obj.setAudit_time(new Date());
                obj.setState(CommUtil.null2Int(state));
                this.orderFormCancelAuditService.update(obj);

                OrderFormLog ofl = new OrderFormLog();
                ofl.setCreatetime(new Date());
                ofl.setLog_info("买家申请取消订单已审核通过");
                ofl.setLog_user(SecurityUserHolder.getCurrentUser());
                ofl.setOf(of);
                this.orderFormLogService.save(ofl);
            } else if (CommUtil.null2Int(state) == 3) {//已驳回
                of.setOrder_status(20);
                this.orderFormService.update(of);

                obj.setAudit_user(SecurityUserHolder.getCurrentUser());
                obj.setAudit_time(new Date());
                obj.setState(CommUtil.null2Int(state));
                this.orderFormCancelAuditService.update(obj);

                OrderFormLog ofl = new OrderFormLog();
                ofl.setCreatetime(new Date());
                ofl.setLog_info("买家申请取消订单审核被驳回");
                ofl.setLog_user(SecurityUserHolder.getCurrentUser());
                ofl.setOf(of);
                this.orderFormLogService.save(ofl);
            }

        }

        return "redirect:order.htm?currentPage=" + currentPage;
    }

    @SecurityMapping(title = "卖家退款", value = "/seller/order_refund.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/order_refund.htm" })
    public ModelAndView order_refund(HttpServletRequest request, HttpServletResponse response,
                                     String id, String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller_order_refund.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (obj.getStore().getId().equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
            mv.addObject("obj", obj);
            mv.addObject("currentPage", currentPage);
            mv.addObject("paymentTools", this.paymentTools);
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "卖家退款保存", value = "/seller/order_refund_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/order_refund_save.htm" })
    public String order_refund_save(HttpServletRequest request, HttpServletResponse response,
                                    String id, String currentPage, String refund,
                                    String refund_log, String refund_type) {
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (obj.getStore().getId().equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
            obj.setRefund(BigDecimal.valueOf(CommUtil.add(obj.getRefund(), refund)));

            RefundLog log = new RefundLog();
            //退款类型
            String refund_mold = request.getParameter("refund_mold");
            //扣减运费
            String refund_freight = request.getParameter("refund_freight");

            if (refund_mold.equals("1")) {
                //扣减运费
                obj.setRefund_freight(CommUtil.null2BigDecimal(refund_freight));
                log.setRefund_freight(CommUtil.null2BigDecimal(refund_freight));
            }

            obj.setRefund_mold(CommUtil.null2Int(refund_mold));

            this.orderFormService.update(obj);

            String type = "预存款";
            if (type.equals(refund_type)) {
                User seller = this.userService.getObjById(obj.getStore().getUser().getId());
                seller
                    .setAvailableBalance(BigDecimal.valueOf(CommUtil.subtract(
                        seller.getAvailableBalance(),
                        BigDecimal.valueOf(CommUtil.null2Double(refund)))));
                this.userService.update(seller);
                User buyer = obj.getUser();
                buyer
                    .setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
                        buyer.getAvailableBalance(),
                        BigDecimal.valueOf(CommUtil.null2Double(refund)))));
                this.userService.update(buyer);
            }

            log.setCreatetime(new Date());
            log.setRefund_id(CommUtil.formatTime("yyyyMMddHHmmss", new Date())
                             + obj.getUser().getId().toString());
            log.setOf(obj);
            log.setRefund(BigDecimal.valueOf(CommUtil.null2Double(refund)));
            log.setRefund_log(refund_log);
            log.setRefund_type(refund_type);
            log.setRefund_user(SecurityUserHolder.getCurrentUser());
            this.refundLogService.save(log);
        }
        return "redirect:order.htm?currentPage=" + currentPage;
    }

    @SecurityMapping(title = "卖家退货", value = "/seller/order_return.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/order_return.htm" })
    public ModelAndView order_return(HttpServletRequest request, HttpServletResponse response,
                                     String id, String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller_order_return.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (obj.getStore().getId().equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
            mv.addObject("obj", obj);
            mv.addObject("currentPage", currentPage);
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "卖家退货保存", value = "/seller/order_return_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/order_return_save.htm" })
    public String order_return_save(HttpServletRequest request, HttpServletResponse response,
                                    String id, String return_info, String currentPage) {
        GoodsReturn gr = null;
        try {

            OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
            if (obj.getStore().getId()
                .equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
                Enumeration enum1 = request.getParameterNames();
                gr = new GoodsReturn();
                gr.setCreatetime(new Date());
                gr.setOf(obj);
                gr.setReturn_id(CommUtil.formatTime("yyyyMMddHHmmss", new Date())
                                + obj.getUser().getId().toString());
                gr.setUser(SecurityUserHolder.getCurrentUser());
                gr.setReturn_info(return_info);
                this.goodsReturnService.save(gr);
                while (enum1.hasMoreElements()) {
                    String paramName = (String) enum1.nextElement();
                    if (paramName.indexOf("refund_count_") >= 0) {
                        GoodsCart gc = this.goodsCartService.getObjById(CommUtil
                            .null2Long(paramName.substring(13)));
                        int count = CommUtil.null2Int(request.getParameter(paramName));

                        if (count > 0 && count <= gc.getCount()) {
                            gc.setCount(gc.getCount() - count);
                            this.goodsCartService.update(gc);
                            GoodsReturnItem item = new GoodsReturnItem();
                            item.setCreatetime(new Date());
                            item.setCount(count);
                            item.setGoods(gc.getGoods());
                            item.setGr(gr);
                            for (GoodsSpecProperty gsp : gc.getGsps()) {
                                item.getGsps().add(gsp);
                            }
                            item.setSpec_info(gc.getSpec_info());
                            this.goodsReturnItemService.save(item);

                            //还原库存
                            Goods goods = gc.getGoods();
                            //减库存    为了保持库存统一总库存和规格属性库存都要加
                            /**
                             * 订货宝库存更新-退货
                             */
                            //   goods.setGoods_inventory(goods.getGoods_inventory() + count);
                            if (!goods.getInventory_type().equals("all")) {/*
                                                                           List<String> gsps = new ArrayList<String>();
                                                                           for (GoodsSpecProperty gsp : gc.getGsps()) {
                                                                           gsps.add(gsp.getId().toString());
                                                                           }
                                                                           String[] gsp_list = new String[gsps.size()];
                                                                           gsps.toArray(gsp_list);
                                                                           List<Map<String, Object>> list = (List<Map<String, Object>>) Json
                                                                           .fromJson(ArrayList.class, goods.getGoods_inventory_detail());
                                                                           for (Map<String, Object> temp : list) {
                                                                           String[] temp_ids = CommUtil.null2String(temp.get("id")).split(
                                                                           "_");
                                                                           Arrays.sort(temp_ids);
                                                                           Arrays.sort(gsp_list);
                                                                           if (Arrays.equals(temp_ids, gsp_list)) {
                                                                           temp.put(
                                                                           "count",
                                                                           Integer.valueOf(CommUtil.null2Int(temp.get("count"))
                                                                           + count));
                                                                           }
                                                                           }
                                                                           goods.setGoods_inventory_detail(Json.toJson(list,
                                                                           JsonFormat.compact()));
                                                                           */
                                List<String> gsps = new ArrayList<String>();
                                for (GoodsSpecProperty gsp : gc.getGsps()) {
                                    gsps.add(gsp.getId().toString());
                                }
                                String[] gsp_list = new String[gsps.size()];
                                gsps.toArray(gsp_list);

                                List<GoodsItem> goods_items = new ArrayList<GoodsItem>();
                                HashMap<String, Object> paramMap = new HashMap<String, Object>();
                                paramMap.put("goodsId", goods.getId());
                                goods_items = this.goodsItemService
                                    .query(
                                        "select obj from GoodsItem obj where obj.disabled=false and  obj.goods.id =:goodsId",
                                        paramMap, -1, -1);
                                if (goods_items != null && goods_items.size() > 0) {
                                    if (goods_items.size() > 1) {
                                        for (GoodsItem item1 : goods_items) {
                                            String[] temp_ids = item1.getSpec_combination().split(
                                                "_");
                                            Arrays.sort(temp_ids);
                                            Arrays.sort(gsp_list);
                                            if (Arrays.equals(temp_ids, gsp_list)) {
                                                item1.setGoods_inventory(item1.getGoods_inventory()
                                                                         + count);
                                                Map<String, Object> map = new HashMap<String, Object>();
                                                map.put("goodsItem_id", item1.getId());
                                                //更新库存商品的库存
                                                List<WarehouseGoodsItem> list = this.warehouseGoodsItemService
                                                    .query(
                                                        "select obj from WarehouseGoodsItem obj where obj.goods_item.id=:goodsItem_id",
                                                        map, 0, 1);
                                                if (list != null && list.size() > 0) {
                                                    WarehouseGoodsItem warehouseGoodsItem = list
                                                        .get(0);
                                                    warehouseGoodsItem
                                                        .setWarehoust_number(warehouseGoodsItem
                                                            .getWarehoust_number() + count);
                                                    this.warehouseGoodsItemService
                                                        .update(warehouseGoodsItem);

                                                }
                                            }
                                        }
                                    } else {
                                        GoodsItem item1 = goods_items.get(0);
                                        item1
                                            .setGoods_inventory(item1.getGoods_inventory() + count);
                                        Map<String, Object> map = new HashMap<String, Object>();
                                        map.put("goodsItem_id", item1.getId());
                                        //更新库存商品的库存
                                        List<WarehouseGoodsItem> list = this.warehouseGoodsItemService
                                            .query(
                                                "select obj from WarehouseGoodsItem obj where obj.goods_item.id=:goodsItem_id",
                                                map, 0, 1);
                                        if (list != null && list.size() > 0) {
                                            WarehouseGoodsItem warehouseGoodsItem = list.get(0);
                                            warehouseGoodsItem
                                                .setWarehoust_number(warehouseGoodsItem
                                                    .getWarehoust_number() + count);
                                            this.warehouseGoodsItemService
                                                .update(warehouseGoodsItem);

                                        }
                                    }
                                }
                                goods.setGoods_item_list(goods_items);
                            }

                            goods.setGoods_salenum(goods.getGoods_salenum() - count);
                            this.goodsService.update(goods);
                            //是预存款购买则退预存款
                            if (obj.getPayment().getMark().equals("balance")) {
                                //                            BigDecimal balance = goods.getGoods_current_price();
                                BigDecimal balance = gc.getPrice();
                                User seller = this.userService.getObjById(SecurityUserHolder
                                    .getCurrentUser().getId());
                                if (this.configService.getSysConfig().getBalance_fenrun() == 1) {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("type", "admin");
                                    params.put("mark", "balance");
                                    List<Payment> payments = this.paymentService
                                        .query(
                                            "select obj from Payment obj where obj.type=:type and obj.mark=:mark",
                                            params, -1, -1);
                                    Payment shop_payment = new Payment();
                                    if (payments.size() > 0) {
                                        shop_payment = (Payment) payments.get(0);
                                    }
                                    double shop_availableBalance = CommUtil.null2Double(balance)
                                                                   * CommUtil
                                                                       .null2Double(shop_payment
                                                                           .getBalance_divide_rate());
                                    balance = BigDecimal.valueOf(CommUtil.null2Double(balance)
                                                                 - shop_availableBalance);
                                }
                                //更新订单退款金额
                                BigDecimal bd = CommUtil.null2BigDecimal(obj.getRefund());
                                obj.setRefund(balance.add(bd));
                                this.orderFormService.update(obj);

                                seller.setAvailableBalance(BigDecimal.valueOf(CommUtil.subtract(
                                    seller.getAvailableBalance(), balance)));
                                User buyer = obj.getUser();

                                buyer.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
                                    buyer.getAvailableBalance(), balance)));
                                this.userService.update(seller);
                                this.userService.update(buyer);
                            }
                        }
                    }
                }
                GoodsReturnLog grl = new GoodsReturnLog();
                grl.setCreatetime(new Date());
                grl.setGr(gr);
                grl.setOf(obj);
                grl.setReturn_user(SecurityUserHolder.getCurrentUser());
                this.goodsReturnLogService.save(grl);
            }
        } catch (Exception e) {
            //出异常时需要回滚退货单
            if (gr != null && !"".equals(gr.getId())) {
                List<GoodsReturnItem> items = gr.getItems();
                if (!items.isEmpty())
                    items.clear();
                this.goodsReturnService.delete(gr.getId());
            }
        }
        return "redirect:order.htm?currentPage=" + currentPage;
    }

    @SecurityMapping(title = "卖家评价", value = "/seller/order_evaluate.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/order_evaluate.htm" })
    public ModelAndView order_evaluate(HttpServletRequest request, HttpServletResponse response,
                                       String id, String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller_order_evaluate.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (obj.getStore().getId().equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
            mv.addObject("obj", obj);
            mv.addObject("currentPage", currentPage);
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "卖家评价保存", value = "/seller/order_evaluate_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/order_evaluate_save.htm" })
    public ModelAndView order_evaluate_save(HttpServletRequest request,
                                            HttpServletResponse response, String id,
                                            String evaluate_info, String evaluate_seller_val) {
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (obj.getStore().getId().equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
            if (obj.getOrder_status() == 50) {
                obj.setOrder_status(60);
                obj.setFinishTime(new Date());
                this.orderFormService.update(obj);
                Enumeration enum1 = request.getParameterNames();
                while (enum1.hasMoreElements()) {
                    String paramName = (String) enum1.nextElement();
                    if (paramName.indexOf("evaluate_seller_val") >= 0) {
                        Evaluate eva = this.evaluateService.getObjById(CommUtil.null2Long(paramName
                            .substring(19)));
                        eva.setEvaluate_seller_val(CommUtil.null2Int(request
                            .getParameter(paramName)));
                        eva.setEvaluate_seller_user(SecurityUserHolder.getCurrentUser());
                        eva.setEvaluate_seller_info(request.getParameter("evaluate_info"
                                                                         + eva.getId().toString()));
                        eva.setEvaluate_seller_time(new Date());

                        this.evaluateService.update(eva);
                        User user = obj.getUser();
                        user.setUser_credit(user.getUser_credit() + eva.getEvaluate_seller_val());
                        if (this.configService.getSysConfig().isIntegral()) {
                            int integral = 0;
                            if (this.configService.getSysConfig().getConsumptionRatio() > 0) {
                                integral = CommUtil.null2Int(Double.valueOf(CommUtil.div(obj
                                    .getTotalPrice(), Integer.valueOf(this.configService
                                    .getSysConfig().getConsumptionRatio()))));
                            }
                            integral = integral > this.configService.getSysConfig()
                                .getEveryIndentLimit() ? this.configService.getSysConfig()
                                .getEveryIndentLimit() : integral;
                            user.setIntegral(user.getIntegral() + integral);
                            this.userService.update(user);
                            IntegralLog log = new IntegralLog();
                            log.setCreatetime(new Date());
                            log.setContent("订单" + obj.getOrder_id() + "完成增加" + integral + "分");
                            log.setIntegral(integral);
                            log.setIntegral_user(user);
                            log.setType("login");
                            this.integralLogService.save(log);
                        }
                    }
                }
            }
            OrderFormLog ofl = new OrderFormLog();
            ofl.setCreatetime(new Date());
            ofl.setLog_info("评价订单");
            ofl.setLog_user(SecurityUserHolder.getCurrentUser());
            ofl.setOf(obj);
            this.orderFormLogService.save(ofl);
        }
        ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        mv.addObject("op_title", "订单评价成功！");
        mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        return mv;
    }

    @SecurityMapping(title = "打印订单", value = "/seller/order_print.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/order_print.htm" })
    public ModelAndView order_print(HttpServletRequest request, HttpServletResponse response,
                                    String id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/order_print.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if ((id != null) && (!id.equals(""))) {
            OrderForm orderform = this.orderFormService.getObjById(CommUtil.null2Long(id));
            mv.addObject("obj", orderform);
        }
        return mv;
    }

    @SecurityMapping(title = "卖家物流详情", value = "/seller/ship_view.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/ship_view.htm" })
    public ModelAndView order_ship_view(HttpServletRequest request, HttpServletResponse response,
                                        String id) {
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/seller/seller_order_ship_view.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (obj.getStore().getId().equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
            mv.addObject("obj", obj);
            TransInfo transInfo = query_ship_getData(CommUtil.null2String(obj.getId()));
            mv.addObject("transInfo", transInfo);
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "您店铺中没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }
        return mv;
    }

    private TransInfo query_ship_getData(String id) {
        TransInfo info = new TransInfo();
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        try {
            URL url = new URL("http://api.kuaidi100.com/api?id="
                              + this.configService.getSysConfig().getKuaidi_id() + "&com="
                              + (obj.getEc() != null ? obj.getEc().getCompany_mark() : "") + "&nu="
                              + obj.getShipCode() + "&show=0&muti=1&order=asc");
            URLConnection con = url.openConnection();
            con.setAllowUserInteraction(false);
            InputStream urlStream = url.openStream();
            String type = URLConnection.guessContentTypeFromStream(urlStream);
            String charSet = null;
            if (type == null) {
                type = con.getContentType();
            }
            if ((type == null) || (type.trim().length() == 0)
                || (type.trim().indexOf("text/html") < 0)) {
                return info;
            }
            if (type.indexOf("charset=") > 0) {
                charSet = type.substring(type.indexOf("charset=") + 8);
            }
            byte[] b = new byte[10000];
            int numRead = urlStream.read(b);
            String content = new String(b, 0, numRead, charSet);
            while (numRead != -1) {
                numRead = urlStream.read(b);
                if (numRead != -1) {
                    String newContent = new String(b, 0, numRead, charSet);
                    content = content + newContent;
                }
            }
            info = (TransInfo) Json.fromJson(TransInfo.class, content);
            urlStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    @SecurityMapping(title = "卖家物流详情", value = "/seller/order_query_userinfor.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/order_query_userinfor.htm" })
    public ModelAndView seller_query_userinfor(HttpServletRequest request,
                                               HttpServletResponse response, String id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller_query_userinfor.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        mv.addObject("obj", obj);
        return mv;
    }

    @SecurityMapping(title = "买家退货申请详情", value = "/seller/seller_order_return_apply_view.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/seller_order_return_apply_view.htm" })
    public ModelAndView seller_order_return_apply_view(HttpServletRequest request,
                                                       HttpServletResponse response, String id,
                                                       String currentPage) {
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/seller_order_return_apply_view.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (obj.getStore().getId().equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
            mv.addObject("obj", obj);
            mv.addObject("currentPage", currentPage);
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "卖家保存退货申请", value = "/seller/seller_order_return.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/seller_order_return.htm" })
    public String seller_order_return(HttpServletRequest request, HttpServletResponse response,
                                      String id, String gr_id, String currentPage, String mark)
                                                                                               throws Exception {
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (mark.equals("true")) {
            if (obj.getStore().getId()
                .equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
                //Enumeration enum1 = request.getParameterNames();
                //GoodsReturn gr = this.goodsReturnService.getObjById(CommUtil.null2Long(gr_id));
                obj.setOrder_status(46);
                int auto_order_return = this.configService.getSysConfig().getAuto_order_return();
                Calendar cal = Calendar.getInstance();
                cal.add(6, auto_order_return);
                obj.setReturn_shipTime(cal.getTime());

                //退货申请日志
                OrderFormLog ofl = new OrderFormLog();
                ofl.setCreatetime(new Date());
                ofl.setLog_info("供货商同意退货");
                ofl.setLog_user(SecurityUserHolder.getCurrentUser());
                ofl.setOf(obj);
                this.orderFormLogService.save(ofl);

                if (this.configService.getSysConfig().isEmailEnable()) {
                    send_email(request, obj, "email_tobuyer_order_return_apply_ok_notify");
                }
                if (this.configService.getSysConfig().isSmsEnbale()) {
                    send_sms(request, obj, obj.getUser().getMobile(),
                        "sms_tobuyer_order_return_apply_ok_notify");
                }
            }
        } else {
            obj.setOrder_status(48);
            //退货申请日志
            OrderFormLog ofl = new OrderFormLog();
            ofl.setCreatetime(new Date());
            ofl.setLog_info("供货商拒绝退货");
            ofl.setLog_user(SecurityUserHolder.getCurrentUser());
            ofl.setOf(obj);
            this.orderFormLogService.save(ofl);

            if (this.configService.getSysConfig().isEmailEnable()) {
                send_email(request, obj, "email_tobuyer_order_return_apply_refuse_notify");
            }
            if (this.configService.getSysConfig().isSmsEnbale()) {
                send_sms(request, obj, obj.getUser().getMobile(),
                    "sms_tobuyer_order_return_apply_refuse_notify");
            }
        }
        this.orderFormService.update(obj);
        return "redirect:order.htm?currentPage=" + currentPage;
    }

    @SecurityMapping(title = "确认买家退货", value = "/seller/seller_order_return_confirm.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/seller_order_return_confirm.htm" })
    public ModelAndView seller_order_return_confirm(HttpServletRequest request,
                                                    HttpServletResponse response, String id) {
        ModelAndView mv = new JModelAndView("error.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (obj.getStore().getId().equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
            List<GoodsReturn> grs = this.goodsReturnService.query(
                "select obj from GoodsReturn obj where obj.of.id = " + obj.getId(), null, -1, -1);
            if (!grs.isEmpty() && grs.size() > 0) {

                obj.setOrder_status(47);
                this.orderFormService.update(obj);
                mv = new JModelAndView("success.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "您已成功确认退货");
                mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
            } else {
                mv.addObject("op_title", "此订单没有退货记录,请先退货");
                mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
            }
        } else {
            mv.addObject("op_title", "您店铺中没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "买家退货物流详情", value = "/seller/seller_order_return_ship_view.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/seller_order_return_ship_view.htm" })
    public ModelAndView seller_order_return_ship_view(HttpServletRequest request,
                                                      HttpServletResponse response, String id) {
        ModelAndView mv = new JModelAndView("error.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (obj.getStore().getId().equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
            if ((obj.getReturn_shipCode() != null) && (!obj.getReturn_shipCode().equals(""))
                && (obj.getReturn_ec() != null) && (!obj.getReturn_ec().equals(""))) {
                mv = new JModelAndView(
                    "user/default/usercenter/seller_order_return_ship_view.html",
                    this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0,
                    request, response);
                TransInfo transInfo = query_return_ship(CommUtil.null2String(obj.getId()));
                mv.addObject("obj", obj);
                mv.addObject("transInfo", transInfo);
            } else {
                mv.addObject("op_title", "买家没有提交退货物流信息");
                mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
            }
        } else {
            mv.addObject("op_title", "您店铺中没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }
        return mv;
    }

    /**
     * 申请结算
     * 
     * @param request
     * @param response
     * @param currentPage
     * @param mulitId
     * @return
     */
    @RequestMapping({ "/seller/settle_log_list.htm" })
    public String settle_log_list(HttpServletRequest request, HttpServletResponse response,
                                  String currentPage, String mulitId) {

        long storeId = 0;
        User user = SecurityUserHolder.getCurrentUser();
        if (mulitId != null && !"".equals(mulitId) && user != null) {

            String[] ids = mulitId.split(",");
            for (int i = 0; i < ids.length; i++) {
                SettleLog settleLog = this.settleLogService.getObjById(Long.valueOf(ids[i]));
                if (storeId == 0) {
                    storeId = settleLog.getOrder().getStore().getId();
                }
                if (storeId == user.getStore().getId()) {
                    settleLog.setStatus(3);
                    this.settleLogService.update(settleLog);
                }
            }
        }
        return "redirect:/seller/mybill.htm?type=2&storeId=" + storeId + "&currentPage="
               + currentPage;
    }

    private GoodsClass getParentClass(GoodsClass gc) {

        if (gc != null && gc.getParent() != null && gc.getParent().getBrokerage() == -1d)
            getParentClass(gc.getParent());

        return gc.getParent();
    }

    private TransInfo query_return_ship(String id) {
        TransInfo info = new TransInfo();
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        try {
            URL url = new URL("http://api.kuaidi100.com/api?id="
                              + this.configService.getSysConfig().getKuaidi_id()
                              + "&com="
                              + (obj.getReturn_ec() != null ? obj.getReturn_ec().getCompany_mark()
                                  : "") + "&nu=" + obj.getReturn_shipCode()
                              + "&show=0&muti=1&order=asc");
            URLConnection con = url.openConnection();
            con.setAllowUserInteraction(false);
            InputStream urlStream = url.openStream();
            String type = URLConnection.guessContentTypeFromStream(urlStream);
            String charSet = null;
            if (type == null) {
                type = con.getContentType();
            }
            if ((type == null) || (type.trim().length() == 0)
                || (type.trim().indexOf("text/html") < 0)) {
                return info;
            }
            if (type.indexOf("charset=") > 0) {
                charSet = type.substring(type.indexOf("charset=") + 8);
            }
            byte[] b = new byte[10000];
            int numRead = urlStream.read(b);
            String content = new String(b, 0, numRead, charSet);
            while (numRead != -1) {
                numRead = urlStream.read(b);
                if (numRead != -1) {
                    String newContent = new String(b, 0, numRead, charSet);
                    content = content + newContent;
                }
            }
            info = (TransInfo) Json.fromJson(TransInfo.class, content);
            urlStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    private void send_email(HttpServletRequest request, OrderForm order, String mark) {
        try {
            com.javamalls.platform.domain.Template template = this.templateService
                .getObjByProperty("mark", mark);
            if ((template != null) && (template.isOpen())) {
                String email = order.getUser().getEmail();
                String subject = template.getTitle();
                String path = request.getSession().getServletContext().getRealPath("")
                              + File.separator + "vm" + File.separator;
                if (!CommUtil.fileExist(path)) {
                    CommUtil.createFolder(path);
                }
                PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
                    path + "msg.vm", false), "UTF-8"));
                pwrite.print(template.getContent());
                pwrite.flush();
                pwrite.close();

                Properties p = new Properties();
                p.setProperty("file.resource.loader.path", request.getRealPath("") + File.separator
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
                context.put("webPath", CommUtil.getURL(request));
                context.put("order", order);
                StringWriter writer = new StringWriter();
                blank.merge(context, writer);

                String content = writer.toString();
                this.msgTools.sendEmail(email, subject, content);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void send_sms(HttpServletRequest request, OrderForm order, String mobile, String mark) {
        try {
            com.javamalls.platform.domain.Template template = this.templateService
                .getObjByProperty("mark", mark);
            if ((template != null) && (template.isOpen())) {
                /*String path = request.getSession().getServletContext().getRealPath("")
                              + File.separator + "vm" + File.separator;
                if (!CommUtil.fileExist(path)) {
                    CommUtil.createFolder(path);
                }
                PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
                    path + "msg.vm", false), "UTF-8"));
                pwrite.print(template.getContent());
                pwrite.flush();
                pwrite.close();

                Properties p = new Properties();
                p.setProperty("file.resource.loader.path", request.getRealPath("/") + "vm"
                                                           + File.separator);
                p.setProperty("input.encoding", "UTF-8");
                p.setProperty("output.encoding", "UTF-8");
                Velocity.init(p);
                org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm", "UTF-8");
                VelocityContext context = new VelocityContext();
                context.put("buyer", order.getUser());
                context.put("seller", order.getStore().getUser());
                context.put("config", this.configService.getSysConfig());
                context.put("send_time", CommUtil.formatLongDate(new Date()));
                context.put("webPath", CommUtil.getURL(request));
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
                if ("sms_tobuyer_order_fee_notify".equals(mark)) {
                    map.put("totalPrice", order.getTotalPrice().toString());
                } else if ("sms_tobuyer_order_outline_pay_ok_notify".equals(mark)
                           || "sms_tobuyer_order_cancel_notify".equals(mark)
                           || "sms_tobuyer_order_ship_notify".equals(mark)) {
                    map.put("storeName", order.getStore().getStore_name());
                }
                this.msgTools.sendSMS(mobile, template.getTitle(), map);
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    // 付款后，如果有捆绑商品，按店铺拆分为多个
    private void update_goods_bind(OrderForm order, String trade_no) {
        Map<Long, OrderForm> mp = new HashMap<Long, OrderForm>();
        int n = 0;

        for (int i = order.getGcs().size() - 1; i >= 0; i--) {
            GoodsCart gc = order.getGcs().get(i);
            if ("bind".equals(gc.getCart_type())) {
                Long storeid = gc.getGoods().getGoods_store().getId();
                OrderForm of = mp.get(storeid);

                order.getGcs().remove(gc);
                order
                    .setTotalPrice(BigDecimal.valueOf(CommUtil.subtract(
                        order.getTotalPrice(),
                        BigDecimal.valueOf(Double.parseDouble(gc.getPrice().toString())
                                           * gc.getCount()))));
                if (of != null) {
                    of.setTotalPrice(BigDecimal.valueOf(Double.parseDouble(of.getTotalPrice()
                        .toString()) + Double.parseDouble(gc.getPrice().toString()) * gc.getCount()));
                    of.getGcs().add(gc);
                } else {

                    n++;

                    of = new OrderForm();
                    of.setAddr(order.getAddr());
                    of.setAuto_confirm_email(order.isAuto_confirm_email());
                    of.setAuto_confirm_sms(order.isAuto_confirm_sms());
                    of.setCreatetime(order.getCreatetime());
                    of.setDisabled(order.isDisabled());
                    of.setEc(order.getEc());
                    of.setFinishTime(order.getFinishTime());

                    of.setOrder_type(order.getOrder_type());
                    of.setOrder_id(order.getOrder_id() + "-" + i);
                    of.setOrder_status(order.getOrder_status());
                    of.setOut_order_id(trade_no);
                    of.setPay_msg(order.getPay_msg());
                    of.setPayment(order.getPayment());
                    of.setPayTime(order.getPayTime());
                    of.setStore(gc.getGoods().getGoods_store());
                    of.setUser(order.getUser());

                    of.setTotalPrice(BigDecimal.valueOf(Double
                        .parseDouble(gc.getPrice().toString()) * gc.getCount()));
                    of.getGcs().add(gc);

                    mp.put(storeid, of);

                }

            }
        }

        if (mp.size() > 0) {
            for (OrderForm of : mp.values()) {
                this.orderFormService.save(of);
                for (GoodsCart gc : of.getGcs()) {
                    gc.setOf(of);
                    this.goodsCartService.save(gc);
                }
            }
            this.orderFormService.update(order);
        }
    }

    @SecurityMapping(title = "实体店下单", value = "/seller/outline_order_add.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/outline_order_add.htm" })
    public ModelAndView outline_order_add(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/seller/seller_outline_order_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if (SecurityUserHolder.getCurrentUser() == null) {
            mv = new JModelAndView("login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            return mv;
        }
        return mv;
    }

    @SecurityMapping(title = "实体店下单保存", value = "/seller/outline_order_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/outline_order_save.htm" })
    public ModelAndView outline_order_save(HttpServletRequest request,
                                           HttpServletResponse response, String addr_id,
                                           String client, String goods_amount, String ship_price,
                                           String total_price, String msg, String invoiceType,
                                           String companyName, String delivery_date,
                                           String file_id, String clerkCode, String cashPayPrice,
                                           String aliPayPrice, String wxPayPrice,
                                           String bankPayPrice) {
        ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);

        if (SecurityUserHolder.getCurrentUser() == null) {
            mv = new JModelAndView("login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            return mv;
        } else {
            mv.addObject("op_title", "实体店下单成功！");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }
        boolean flag = this.orderFormService.outline_order_save(request, response, addr_id, client,
            goods_amount, ship_price, total_price, msg, invoiceType, companyName, delivery_date,
            file_id, clerkCode, cashPayPrice, aliPayPrice, wxPayPrice, bankPayPrice);
        return mv;
    }

    @SecurityMapping(title = "代客下单", value = "/seller/valet_order_add.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/valet_order_add.htm" })
    public ModelAndView valet_order_add(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/seller/seller_valet_order_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if (SecurityUserHolder.getCurrentUser() == null) {
            mv = new JModelAndView("login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            return mv;
        }
        return mv;
    }

    @SecurityMapping(title = "代客下单保存", value = "/seller/valet_order_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
    @RequestMapping({ "/seller/valet_order_save.htm" })
    public ModelAndView valet_order_save(HttpServletRequest request, HttpServletResponse response,
                                         String addr_id, String client, String goods_amount,
                                         String ship_price, String total_price, String msg,
                                         String invoiceType, String companyName,
                                         String delivery_date, String file_id) {
        ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);

        if (SecurityUserHolder.getCurrentUser() == null) {
            mv = new JModelAndView("login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            return mv;
        } else {
            mv.addObject("op_title", "代客下单成功！");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }
        //保存订单
        OrderForm of = new OrderForm();
        of.setCreatetime(new Date());
        of.setDisabled(false);
        of.setOrder_id(SecurityUserHolder.getCurrentUser().getId()
                       + CommUtil.formatTime("yyyyMMddHHmmss", new Date()));
        Address addr = this.addressService.getObjById(CommUtil.null2Long(addr_id));
        of.setAddr(addr);//收货地址
        of.setOrder_status(15);//线下支付待审核
        User buyer = this.userService.getObjById(CommUtil.null2Long(client));
        of.setUser(buyer);//客户
        Store store = SecurityUserHolder.getCurrentUser().getStore();
        of.setStore(store);//卖家
        of.setGoods_amount(new BigDecimal(goods_amount));//商品总价
        of.setShip_price(new BigDecimal(ship_price));//邮费
        of.setTotalPrice(new BigDecimal(total_price));//订单总金额
        of.setOrder_type("underline");//线下订单
        of.setMsg(msg);//订单备注
        of.setInvoiceType(CommUtil.null2Int(invoiceType));//发票类型
        of.setInvoice(companyName);//发票抬头
        if (delivery_date != null && !"".equals(delivery_date)) {//交货日期
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                of.setDelivery_date(sdf.parse(delivery_date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        of.setPay_msg("代客下单进行线下支付");
        Map<String, Object> params = new HashMap<String, Object>();
        List<Payment> payments = null;
        params.put("mark", "outline");
        params.put("store_id", of.getStore().getId());
        payments = this.paymentService.query(
            "select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id", params,
            -1, -1);
        if (payments.size() > 0) {
            of.setPayment((Payment) payments.get(0));
            of.setPayTime(new Date());
        }
        of.setTransport("卖家承担");
        this.orderFormService.save(of);

        if (file_id != null && !"".equals(file_id)) {//订单附件
            OrderFormFile orderFormFile = this.orderFormFileService.getObjById(CommUtil
                .null2Long(file_id));
            if (orderFormFile != null) {
                orderFormFile.setOrderForm(of);
                this.orderFormFileService.update(orderFormFile);
            }
        }

        //    List<GoodsCart>           gcs              = new ArrayList<GoodsCart>();
        String[] numbers = request.getParameterValues("number");
        String[] purchase_prices = request.getParameterValues("purchase_price");
        String[] itemIds = request.getParameterValues("itemId");

        if (itemIds != null && itemIds.length > 0) {
            Date now = new Date();
            for (int i = 0; i < itemIds.length; i++) {
                GoodsItem goodsItem = this.goodsItemService.getObjById(CommUtil
                    .null2Long(itemIds[i]));
                GoodsCart goodsCart = new GoodsCart();
                goodsCart.setCreatetime(now);
                goodsCart.setDisabled(false);
                goodsCart.setCount(CommUtil.null2Int(numbers[i]));
                goodsCart.setPrice(CommUtil.null2BigDecimal(purchase_prices[i]));
                String spec_combination = goodsItem.getSpec_combination();
                if (spec_combination != null && !"".equals(spec_combination)) {
                    String[] gsp_ids = spec_combination.split("_");
                    if (gsp_ids != null && gsp_ids.length > 0) {
                        for (String gsp_id : gsp_ids) {
                            GoodsSpecProperty spec_property = this.goodsSpecPropertyService
                                .getObjById(CommUtil.null2Long(gsp_id));
                            goodsCart.getGsps().add(spec_property);

                        }

                    }

                    spec_combination = spec_combination.replaceAll("_", ",");
                    goodsCart.setSpec_id(spec_combination);
                    goodsCart.setSpec_info(goodsItem.getSpec_info());
                }
                goodsCart.setGoods(goodsItem.getGoods());
                goodsCart.setOf(of);

                this.goodsCartService.save(goodsCart);

                /*//更新库存
                goodsItem.setGoods_inventory(goodsItem.getGoods_inventory()-CommUtil.null2Int(numbers[i]));
                 Map<String,Object> map=new HashMap<String, Object>();
                 map.put("goodsItem_id", goodsItem.getId());
                 //更新库存商品的库存
                 List<WarehouseGoodsItem> list = this.warehouseGoodsItemService.query(
                		 "select obj from WarehouseGoodsItem obj where obj.goods_item.id=:goodsItem_id", map, 0, 1);
                 if(list!=null&&list.size()>0){
                	 WarehouseGoodsItem warehouseGoodsItem = list.get(0);
                	 warehouseGoodsItem.setWarehoust_number(warehouseGoodsItem.getWarehoust_number()-CommUtil.null2Int(numbers[i]));
                	 this.warehouseGoodsItemService.update(warehouseGoodsItem);
                	 
                 }
                 this.goodsItemService.update(goodsItem);*/

            }
        }

        OrderFormLog ofl = new OrderFormLog();
        ofl.setCreatetime(new Date());
        ofl.setOf(of);
        ofl.setLog_info("代客下单");
        ofl.setLog_user(SecurityUserHolder.getCurrentUser());
        this.orderFormLogService.save(ofl);

        if (this.configService.getSysConfig().isSmsEnbale()) {
            com.javamalls.platform.domain.Template template = this.templateService
                .getObjByProperty("mark", "sms_tobuyer_underline_pay_ok_notify");
            if ((template != null) && (template.isOpen())) {
                //短信发送  
                Map<String, String> map = new HashMap<String, String>();
                String buyerName = buyer.getUserName();
                if (buyer.getTrueName() != null && !"".equals(buyer.getTrueName())) {
                    buyerName = buyer.getTrueName();
                }
                map.put("buyerName", buyerName);

                map.put("order_id", of.getOrder_id());
                map.put("storeName", store.getStore_name());

                if (buyer.getMobile() != null && !"".equals(buyer.getMobile())) {
                    this.msgTools.sendSMS(buyer.getMobile(), template.getTitle(), map);
                }
            }
        }
        return mv;
    }

    /**
     * 代客下单客户选择
     * @param request
     * @param response
     * @param currentPage
     * @param orderBy
     * @param orderType
     * @param type
     * @param quserName
     * @return
     */
    @RequestMapping({ "/seller/valet_order_clientChoose.htm" })
    public ModelAndView storeUser(HttpServletRequest request, HttpServletResponse response,
                                  String currentPage, String orderBy, String orderType,
                                  String type, String quserName) {
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/seller/seller_valet_order_clientchoose.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        UserQueryObject qo = new UserQueryObject(currentPage, mv, orderBy, orderType);

        qo.addQuery("obj.userRole", new SysMap("userRole", "ADMIN"), "!=");
        qo.addQuery("obj.parent.id is null ", null);
        if (quserName != null && !"".equals(quserName)) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("client_name", "%" + quserName + "%");
            map.put("mobile", "%" + quserName + "%");
            map.put("trueName", "%" + quserName + "%");
            qo.addQuery(
                " (obj.client_name like :client_name or obj.mobile like :mobile or obj.trueName like :trueName ) ",
                map);
        }

        qo.addQuery("obj.disabled ", new SysMap("disabled", false), "=");
        mv.addObject("quserName", quserName);
        IPageList pList = null;
        try {
            pList = this.userService.list(qo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        CommUtil.saveIPageList2ModelAndView(url + "/seller/valet_order_clientChoose.htm", "", "",
            pList, mv);
        return mv;
    }

    /**
     * 代客下单货品选择
     * @param request
     * @param response
     * @param currentPage
     * @param orderBy
     * @param orderType
     * @return
     */
    @RequestMapping({ "/seller/valet_order_goods_choose.htm" })
    public ModelAndView valet_order_goods_choose(HttpServletRequest request,
                                                 HttpServletResponse response, String currentPage,
                                                 String orderBy, String orderType,
                                                 String goods_name, String buyer_id) {
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/seller/seller_valet_order_goodsChoose.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }

        if (SecurityUserHolder.getCurrentUser() == null) {
            mv = new JModelAndView("login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            return mv;
        }
        String params = "";
        GoodsItemQueryObject qo = new GoodsItemQueryObject(currentPage, mv, orderBy, orderType);

        qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");
        qo.addQuery("obj.goods_inventory", new SysMap("goods_inventory", 0), ">");
        qo.addQuery("obj.goods.goods_store.id", new SysMap("store_id", SecurityUserHolder
            .getCurrentUser().getStore().getId()), "=");

        qo.addQuery("obj.goods.disabled", new SysMap("goodsdisabled", false), "=");
        qo.addQuery("obj.goods.goods_status", new SysMap("goods_status", 0), "=");

        if (goods_name != null && !"".equals(goods_name)) {

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("goods_name", "%" + goods_name + "%");
            map.put("bar_code", "%" + goods_name + "%");
            qo.addQuery(
                " (obj.goods.goods_name like :goods_name or obj.bar_code like :bar_code  ) ", map);

        }
        mv.addObject("goods_name", goods_name);
        IPageList pList = this.goodsItemService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/seller_valet_order_goodsChoose.html", "",
            params, pList, mv);
        mv.addObject("buyer_id", buyer_id);
        mv.addObject("goodsViewTools", goodsViewTools);
        return mv;
    }

    @RequestMapping({ "/seller/valet_order_addr_choose.htm" })
    public ModelAndView valet_order_addr_choose(HttpServletRequest request,
                                                HttpServletResponse response, String currentPage,
                                                String orderBy, String orderType, String client_id) {
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/seller/seller_valet_order_addrChoose.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }

        if (SecurityUserHolder.getCurrentUser() == null) {
            mv = new JModelAndView("login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            return mv;
        }

        AddressQueryObject qo = new AddressQueryObject(currentPage, mv, orderBy, orderType);
        qo.addQuery("obj.user.id", new SysMap("user_id", CommUtil.null2Long(client_id)), "=");

        qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");
        qo.setPageSize(30);
        IPageList pList = this.addressService.list(qo);
        List list = pList.getResult();
        mv.addObject("objs", list);
        mv.addObject("client_id", client_id);
        List<Area> areas = this.areaService.query(
            "select obj from Area obj where obj.parent.id is null", null, -1, -1);
        mv.addObject("areas", areas);
        return mv;
    }

    @RequestMapping({ "/seller/valet_order_addr_save.htm" })
    public void valet_order_addr_save(HttpServletRequest request, HttpServletResponse response,
                                      String client_id, String trueName, String mobile,
                                      String area_info, String area_id) {

        Address address = new Address();
        address.setTrueName(trueName);
        address.setCreatetime(new Date());
        address.setDisabled(false);
        address.setMobile(mobile);
        address.setUser(this.userService.getObjById(CommUtil.null2Long(client_id)));
        address.setArea_info(area_info);
        address.setArea(this.areaService.getObjById(CommUtil.null2Long(area_id)));
        this.addressService.save(address);

        Address addr = new Address();
        addr.setId(address.getId());
        addr.setTrueName(address.getTrueName());
        addr.setMobile(address.getMobile());
        Area area = address.getArea();
        String area_info2 = "";
        if (area.getParent() != null && area.getParent().getParent() != null) {
            area_info2 = area.getParent().getParent().getAreaName() + " "
                         + area.getParent().getAreaName() + " " + area.getAreaName() + " "
                         + address.getArea_info();
        } else if (area.getParent() != null) {
            area_info2 = area.getParent().getAreaName() + " " + area.getAreaName() + " "
                         + address.getArea_info();
        }
        addr.setArea_info(area_info2);
        String areaJson = JsonUtil.write2JsonStr(addr);
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(areaJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 异步上传文件
     * @param request
     * @param response
     * @param fileIndex
     */
    @RequestMapping(value = "/seller/uploadFiles.htm", method = { RequestMethod.POST })
    public void uploadFiles(MultipartHttpServletRequest request, HttpServletResponse response,
                            String fileId) {

        /*
        * 附件上传
        * */
        String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
        String saveFilePathName = request.getSession().getServletContext().getRealPath("/")
                                  + uploadFilePath + "/orderFile";

        OrderFormFile file = this.orderFormFileService.getObjById(CommUtil.null2Long(fileId));
        if (file == null) {
            file = new OrderFormFile();
        }
        try {
            Map map = new HashMap();
            map = CommUtil.saveFileToServer(request, "order_file", saveFilePathName, null, null);

            if (!"".equals(map.get("fileName"))) {
                file.setFile_name(CommUtil.null2String(map.get("oldName")));
                file.setExt(CommUtil.null2String(map.get("mime")));
                file.setSize(CommUtil.null2Float(map.get("fileSize")));
                file.setFile_url(uploadFilePath + "/orderFile/" + map.get("fileName"));
                file.setCreatetime(new Date());
                file.setDisabled(false);
                if (fileId != null && !"".equals(fileId)) {
                    this.orderFormFileService.update(file);
                } else {
                    this.orderFormFileService.save(file);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();

            writer.print(gson.toJson(file));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
