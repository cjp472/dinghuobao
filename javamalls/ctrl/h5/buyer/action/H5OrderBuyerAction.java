package com.javamalls.ctrl.h5.buyer.action;

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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.tools.generic.MathTool;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.constant.Constant;
import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.ctrl.admin.tools.MsgTools;
import com.javamalls.payment.chinabank.h5.util.JsonUtil;
import com.javamalls.platform.domain.Complaint;
import com.javamalls.platform.domain.ComplaintSubject;
import com.javamalls.platform.domain.Evaluate;
import com.javamalls.platform.domain.ExpressCompany;
import com.javamalls.platform.domain.GoodsCart;
import com.javamalls.platform.domain.OrderForm;
import com.javamalls.platform.domain.OrderFormCancelAudit;
import com.javamalls.platform.domain.OrderFormLog;
import com.javamalls.platform.domain.RefundLog;
import com.javamalls.platform.domain.SettleLog;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.StorePoint;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.enums.State;
import com.javamalls.platform.domain.query.EvaluateQueryObject;
import com.javamalls.platform.domain.query.GoodsCartQueryObject;
import com.javamalls.platform.domain.query.OrderFormQueryObject;
import com.javamalls.platform.domain.query.RefundLogQueryObject;
import com.javamalls.platform.domain.virtual.TransInfo;
import com.javamalls.platform.service.IComplaintSubjectService;
import com.javamalls.platform.service.IEvaluateService;
import com.javamalls.platform.service.IExpressCompanyService;
import com.javamalls.platform.service.IGoodsCartService;
import com.javamalls.platform.service.IOrderFormCancelAuditService;
import com.javamalls.platform.service.IOrderFormLogService;
import com.javamalls.platform.service.IOrderFormService;
import com.javamalls.platform.service.IPaymentService;
import com.javamalls.platform.service.IPredepositLogService;
import com.javamalls.platform.service.IRefundLogService;
import com.javamalls.platform.service.ISettleLogService;
import com.javamalls.platform.service.IStorePointService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.ITemplateService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;
import com.javamalls.platform.vo.OrderFormVo;
import com.utils.SendReqAsync;

/**
 * 订单管理
 * 
 * @Filename: OrderBuyerAction.java
 * @Version: 2.7.0
 * @Author: 刘杰
 * @Email: attay125@163.com
 * 
 */
@Controller
public class H5OrderBuyerAction {
    private static final Logger      logger = Logger.getLogger(H5OrderBuyerAction.class);

    @Autowired
    private ISysConfigService        configService;
    @Autowired
    private IComplaintSubjectService complaintSubjectService;
    @Autowired
    private IUserConfigService       userConfigService;
    @Autowired
    private IOrderFormService        orderFormService;
    @Autowired
    private IOrderFormLogService     orderFormLogService;
    @Autowired
    private IEvaluateService         evaluateService;
    @Autowired
    private IUserService             userService;
    @Autowired
    private IStoreService            storeService;
    @Autowired
    private ITemplateService         templateService;
    @Autowired
    private IStorePointService       storePointService;
    @Autowired
    private IPredepositLogService    predepositLogService;
    @Autowired
    private IPaymentService          paymentService;
    @Autowired
    private IGoodsCartService        goodsCartService;
    @Autowired
    private IExpressCompanyService   expressCompayService;
    @Autowired
    private MsgTools                 msgTools;
    @Autowired
    private IRefundLogService        refundLogService;
    @Autowired
    private ISettleLogService        settleLogService;

    @Autowired
    private SendReqAsync             sendReqAsync;
    @Autowired
    private IOrderFormCancelAuditService	orderFormCancelAuditService;
    /**
     * 买家评价管理
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
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/pingjia.htm" })
    public ModelAndView pingjia(HttpServletRequest request, HttpServletResponse response,
                                String currentPage, String order_id, String beginTime,
                                String endTime, String order_status) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/buyer_pingjia.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        EvaluateQueryObject ofqo = new EvaluateQueryObject(currentPage, mv, "createtime", "desc");
        ofqo.setPageSize(8);
        ofqo.addQuery("obj.evaluate_user.id", new SysMap("user_id", SecurityUserHolder
            .getCurrentUser().getId()), "=");
        IPageList pList = evaluateService.list(ofqo);
        String url = CommUtil.getURL(request);
        CommUtil.saveIPageList2ModelAndView(url + "/mobile/buyer/pingjia.htm", "", "", pList, mv);
        //mv.addObject("objs", pList.getResult());
        return mv;
    }

    @SecurityMapping(title = "买家订单列表", value = "/mobile/buyer/order.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/order.htm" })
    public ModelAndView order(HttpServletRequest request, HttpServletResponse response,
                              String currentPage, String order_id, String beginTime,
                              String endTime, String order_status, String goods_name,
                              @PathVariable String storeId, String syState) {
        User user = SecurityUserHolder.getCurrentUser();
        ModelAndView mv = null;
        if (user != null) {
            mv = new JModelAndView("user/default/usercenter/h5/buyer_order.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0,
                request, response);
            OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv, "createtime",
                "desc");
            ofqo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser()
                .getId()), "=");

            if (!CommUtil.null2String(order_status).equals("")) {

                //待付款 待收货 已完成 已取消
                //待付款
                if (order_status.equals("order_submit")) {
                    List<Integer> mapin = new ArrayList<Integer>();
                    mapin.add(10);//待付款
                    mapin.add(15);//线下支付待审核
                    ofqo.addQuery("obj.order_status", new SysMap("order_status", mapin), "in");
                }
                //待收货
                if (order_status.equals("order_shipping")) {
                    List<Integer> mapin = new ArrayList<Integer>();
                    mapin.add(16);//货到付款待发货
                    mapin.add(20);//已付款
                    mapin.add(30);//已发货
                    ofqo.addQuery("obj.order_status", new SysMap("order_status", mapin), "in");
                }
                //已完成
                if (order_status.equals("order_finish")) {
                    List<Integer> mapin = new ArrayList<Integer>();
                    mapin.add(40);//已收货
                    mapin.add(45);//买家申请退货
                    mapin.add(46);//退货中
                    mapin.add(47);//退货完成，已结束
                    mapin.add(48);//卖家拒绝退货
                    mapin.add(49);//退货失败
                    mapin.add(50);//已完成,已评价
                    mapin.add(60);//已结束
                    mapin.add(65);//已结束，不可评价
                    ofqo.addQuery("obj.order_status", new SysMap("order_status", mapin), "in");
                }
                //已取消
                if (order_status.equals("order_cancel")) {
                    ofqo.addQuery("obj.order_status",
                        new SysMap("order_status", Integer.valueOf(0)), "=");
                }
            }
            if (!CommUtil.null2String(goods_name).equals("")) {
                mv.addObject("goods_name", goods_name);
                GoodsCartQueryObject gcqo = new GoodsCartQueryObject(currentPage, mv, "createtime",
                    "desc");
                gcqo.addQuery("obj.goods.goods_name", new SysMap("goods_name", "%" + goods_name
                                                                               + "%"), "like");
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
            ofqo.setPageSize(4);
            mv.addObject("order_status", order_status);
            IPageList pList = this.orderFormService.list(ofqo);
            CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);

        } else {
            mv = new JModelAndView("h5/login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            String url = CommUtil.getURL(request);
            mv.addObject("url", url + "/mobile/daohang.htm");
            mv.addObject("storeId", storeId);
        }
        mv.addObject("syState", syState);
        return mv;
    }

    @SecurityMapping(title = "买家订单列表", value = "/mobile/buyer/order.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/order_ajax.htm" })
    public ModelAndView order_ajax(HttpServletRequest request, HttpServletResponse response,
                                   String currentPage, String order_id, String beginTime,
                                   String endTime, String order_status, String goods_name) {
        User user = SecurityUserHolder.getCurrentUser();
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/buyer_order_ajax.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if (user != null) {
            OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv, "createtime",
                "desc");
            ofqo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser()
                .getId()), "=");

            if (!CommUtil.null2String(order_status).equals("")) {

                //待付款 待收货 已完成 已取消
                //待付款
                if (order_status.equals("order_submit")) {
                    List<Integer> mapin = new ArrayList<Integer>();
                    mapin.add(10);//待付款
                    mapin.add(15);//线下支付待审核
                    ofqo.addQuery("obj.order_status", new SysMap("order_status", mapin), "in");
                }
                //待收货
                if (order_status.equals("order_shipping")) {
                    List<Integer> mapin = new ArrayList<Integer>();
                    mapin.add(16);//货到付款待发货
                    mapin.add(20);//已付款
                    mapin.add(30);//已发货
                    ofqo.addQuery("obj.order_status", new SysMap("order_status", mapin), "in");
                }
                //已完成
                if (order_status.equals("order_finish")) {
                    List<Integer> mapin = new ArrayList<Integer>();
                    mapin.add(40);//已收货
                    mapin.add(45);//买家申请退货
                    mapin.add(46);//退货中
                    mapin.add(47);//退货完成，已结束
                    mapin.add(48);//卖家拒绝退货
                    mapin.add(49);//退货失败
                    mapin.add(50);//已完成,已评价
                    mapin.add(60);//已结束
                    mapin.add(65);//已结束，不可评价
                    ofqo.addQuery("obj.order_status", new SysMap("order_status", mapin), "in");
                }
                //已取消
                if (order_status.equals("order_cancel")) {
                    ofqo.addQuery("obj.order_status",
                        new SysMap("order_status", Integer.valueOf(0)), "=");
                }
            }
            if (!CommUtil.null2String(goods_name).equals("")) {
                mv.addObject("goods_name", goods_name);
                GoodsCartQueryObject gcqo = new GoodsCartQueryObject(currentPage, mv, "createtime",
                    "desc");
                gcqo.addQuery("obj.goods.goods_name", new SysMap("goods_name", "%" + goods_name
                                                                               + "%"), "like");
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
            ofqo.setPageSize(4);
            mv.addObject("order_status", order_status);
            IPageList pList = this.orderFormService.noLastList(ofqo);
            CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);

        }

        return mv;
    }

    @SecurityMapping(title = "买家退款列表", value = "/mobile/buyer/refund.htm", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/refund.htm" })
    public ModelAndView refund(HttpServletRequest request, HttpServletResponse response, String id,
                               String currentPage, String data_type, String data, String beginTime,
                               String endTime, @PathVariable String storeId, String syState) {
        User user = SecurityUserHolder.getCurrentUser();
        ModelAndView mv = null;
        if (user != null) {

            mv = new JModelAndView("user/default/usercenter/h5/buyerrefund.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0,
                request, response);
            RefundLogQueryObject qo = new RefundLogQueryObject(currentPage, mv, "createtime",
                "desc");
            qo.setPageSize(Integer.valueOf(8));
            qo.addQuery("obj.of.user.id", new SysMap("refund_user", user.getId()), "=");
            if (!CommUtil.null2String(data).equals("")) {
                qo.addQuery("obj.of.order_id", new SysMap("order_id", data), "=");
            }
            if (!CommUtil.null2String(beginTime).equals("")) {
                qo.addQuery("obj.createtime",
                    new SysMap("beginTime", CommUtil.formatDate(beginTime)), ">=");
            }
            if (!CommUtil.null2String(endTime).equals("")) {
                qo.addQuery("obj.createtime", new SysMap("endTime", CommUtil.formatDate(endTime)),
                    "<=");
            }
            qo.setPageSize(5);
            IPageList pList = this.refundLogService.list(qo);
            CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
            mv.addObject("data_type", data_type);
            mv.addObject("data", data);
            mv.addObject("beginTime", beginTime);
            mv.addObject("endTime", endTime);
        } else {
            String targetUrl = CommUtil.getURL(request) + "/mobile/buyer/refund.htm";
            mv = new JModelAndView("h5/login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("url", targetUrl);
            mv.addObject("storeId", storeId);
        }
        mv.addObject("syState", syState);

        return mv;
    }

    @SecurityMapping(title = "买家退款列表", value = "/mobile/buyer/refund.htm", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/refund_ajax.htm" })
    public ModelAndView refund_ajax(HttpServletRequest request, HttpServletResponse response,
                                    String id, String currentPage, String data_type, String data,
                                    String beginTime, String endTime) {
        User user = SecurityUserHolder.getCurrentUser();
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/buyerrefund_ajax.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if (user != null) {
            RefundLogQueryObject qo = new RefundLogQueryObject(currentPage, mv, "createtime",
                "desc");
            qo.setPageSize(Integer.valueOf(8));
            qo.addQuery("obj.of.user.id", new SysMap("refund_user", user.getId()), "=");
            if (!CommUtil.null2String(data).equals("")) {
                qo.addQuery("obj.of.order_id", new SysMap("order_id", data), "=");
            }
            if (!CommUtil.null2String(beginTime).equals("")) {
                qo.addQuery("obj.createtime",
                    new SysMap("beginTime", CommUtil.formatDate(beginTime)), ">=");
            }
            if (!CommUtil.null2String(endTime).equals("")) {
                qo.addQuery("obj.createtime", new SysMap("endTime", CommUtil.formatDate(endTime)),
                    "<=");
            }
            qo.setPageSize(5);
            IPageList pList = this.refundLogService.noLastlist(qo);
            CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
            mv.addObject("data_type", data_type);
            mv.addObject("data", data);
            mv.addObject("beginTime", beginTime);
            mv.addObject("endTime", endTime);
        }
        return mv;
    }

    @SecurityMapping(title = "买家订单详情", value = "/mobile/buyer/order_view.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/order_view.htm" })
    public ModelAndView order_view(HttpServletRequest request, HttpServletResponse response,
                                   String id, @PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/order_view.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (SecurityUserHolder.getCurrentUser() == null) {
            mv = new JModelAndView("h5/login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("storeId", storeId);
            return mv;
        }
        if (obj.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {
            mv.addObject("obj", obj);
            TransInfo transInfo = query_ship_getData(CommUtil.null2String(obj.getId()));
            mv.addObject("transInfo", transInfo);
        } else {
            mv = new JModelAndView("user/default/usercenter/h5/error.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0,
                request, response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/order.htm");
        }
        return mv;
    }

    private TransInfo query_ship_getData(String id) {
        TransInfo info = new TransInfo();
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        try {
            String query_url = "http://api.kuaidi100.com/api?id="
                               + this.configService.getSysConfig().getKuaidi_id() + "&com="
                               + (obj.getEc() != null ? obj.getEc().getCompany_mark() : "")
                               + "&nu=" + obj.getShipCode() + "&show=0&muti=1&order=asc";
            URL url = new URL(query_url);
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
        }
        return info;
    }

    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/orderpingjia.htm" })
    public ModelAndView orderpingjia(HttpServletRequest request, HttpServletResponse response,
                                     String currentPage, String order_id, String beginTime,
                                     String endTime, String order_status, String fl) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/order_pingjia.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        EvaluateQueryObject ofqo = new EvaluateQueryObject(currentPage, mv, "createtime", "desc");
        if (fl.equals("0")) {
            ofqo.addQuery("obj.evaluate_user.id", new SysMap("user_id", SecurityUserHolder
                .getCurrentUser().getId()), "=");
        }
        if (fl.equals("1")) {
            ofqo.addQuery("obj.evaluate_seller_user.id", new SysMap("user_id", SecurityUserHolder
                .getCurrentUser().getId()), "=");
        }
        ofqo.addQuery("obj.of.order_id", new SysMap("order_id", order_id), "=");
        IPageList pList = evaluateService.list(ofqo);
        if (pList.getResult() != null && pList.getResult().size() > 0) {
            mv.addObject("obj", pList.getResult().get(0));
        }

        mv.addObject("fl", fl);
        return mv;
    }

    @SecurityMapping(title = "订单取消", value = "/mobile/buyer/order_cancel.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/order_cancel.htm" })
    public ModelAndView order_cancel(HttpServletRequest request, HttpServletResponse response,
                                     String id, String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/buyer_order_cancel.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (obj.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())
            && obj.getOrder_status() == 10) {
            mv.addObject("obj", obj);
            mv.addObject("currentPage", currentPage);
        } else {
            mv = new JModelAndView("user/default/usercenter/h5/error.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0,
                request, response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！只能取消待付款的订单");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/order.htm");
        }
        return mv;
    }

    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/order_cancel_save.htm" })
    public String order_cancel_save(HttpServletRequest request, HttpServletResponse response,
                                    String id, String currentPage, String state_info,
                                    String other_state_info) throws Exception {

        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (obj.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())
            && obj.getOrder_status() == 10) {
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
                .sendMessageUtil(Constant.STORE_ORDERFORM_URL_EDIT, write2JsonStr, "买家取消订单");

            try {

                if (this.configService.getSysConfig().isEmailEnable()) {
                    send_email(request, obj, "email_toseller_order_cancel_notify");
                }
                if (this.configService.getSysConfig().isSmsEnbale()) {
                    send_sms(request, obj, obj.getStore().getUser().getMobile(),
                        "sms_toseller_order_cancel_notify");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "redirect:" + CommUtil.getURL(request) + "/mobile/buyer/order.htm";
    }

    private void send_sms(HttpServletRequest request, OrderForm order, String mobile, String mark) {
        try {
            com.javamalls.platform.domain.Template template = this.templateService
                .getObjByProperty("mark", mark);
            if (template != null && template.isOpen()) {
                /*  String path = request.getSession().getServletContext().getRealPath("/") + "/vm/";
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

                  String content = writer.toString();
                  this.msgTools.sendSMS(mobile, content);*/
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

                this.msgTools.sendSMS(mobile, template.getTitle(), map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void send_email(HttpServletRequest request, OrderForm order, String mark) {
        try {
            com.javamalls.platform.domain.Template template = this.templateService
                .getObjByProperty("mark", mark);
            if (template != null && template.isOpen()) {
                String email = order.getStore().getUser().getEmail();
                String subject = template.getTitle();
                String path = request.getSession().getServletContext().getRealPath("/") + "/vm/";
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

                String content = writer.toString();
                this.msgTools.sendEmail(email, subject, content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SecurityMapping(title = "删除订单信息", value = "/mobile/buyer/order_delete.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/order_delete.htm" })
    public String order_delete(HttpServletRequest request, HttpServletResponse response, String id,
                               String currentPage) throws Exception {
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if ((obj.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId()))
            && (obj.getOrder_status() == 0)) {
            for (GoodsCart gc : obj.getGcs()) {
                gc.getGsps().clear();
                this.goodsCartService.delete(gc.getId());
            }
            this.orderFormService.delete(obj.getId());
        }
        return "redirect:" + CommUtil.getURL(request) + "/mobile/buyer/order.htm?currentPage="
               + currentPage;
    }

    @SecurityMapping(title = "买家退货申请", value = "/mobile/buyer/order_return_apply.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/order_return_apply.htm" })
    public ModelAndView order_return_apply(HttpServletRequest request,
                                           HttpServletResponse response, String id, String view) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/order_return_apply.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (obj.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {
            mv.addObject("obj", obj);
            if ((view != null) && (!view.equals(""))) {
                mv.addObject("view", Boolean.valueOf(true));
            }
        } else {
            mv = new JModelAndView("user/default/usercenter/h5/error.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0,
                request, response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/order.htm");
        }
        mv.addObject("math", new MathTool());
        mv.addObject("values", State.REFUND_REASON.getValues());
        mv.addObject("keys", State.REFUND_REASON.getKeys());
        if (obj.getRefund_reason() != null) {
            mv.addObject("reason", State.getValue(State.REFUND_REASON, obj.getRefund_reason()));
        } else {
            mv.addObject("reason", "");
        }
        return mv;
    }

   
    @SecurityMapping(title = "买家退货申请保存", value = "/mobile/buyer/order_return_apply_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/order_return_apply_save.htm" })
    public String order_return_apply_save(HttpServletRequest request, HttpServletResponse response,
                                          String id, String currentPage, String return_content,
                                          String refund_reason) throws Exception {
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (obj.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {
            obj.setOrder_status(45);
            obj.setReturn_content(return_content);
            obj.setRefund_reason(Integer.valueOf(refund_reason == null || refund_reason.equals("") ? "0"
                : refund_reason));
            this.orderFormService.update(obj);

            //退货申请日志
            OrderFormLog ofl = new OrderFormLog();
            ofl.setCreatetime(new Date());
            ofl.setLog_info("订货商申请退货");
            ofl.setLog_user(SecurityUserHolder.getCurrentUser());
            ofl.setOf(obj);
            this.orderFormLogService.save(ofl);

            if (this.configService.getSysConfig().isEmailEnable()) {
                send_email(request, obj, "email_toseller_order_return_apply_notify");
            }
            if (this.configService.getSysConfig().isSmsEnbale()) {
                send_sms(request, obj, obj.getUser().getMobile(),
                    "sms_toseller_order_return_apply_notify");
            }
        }
        return "redirect:" + CommUtil.getURL(request) + "/mobile/buyer/order.htm?currentPage="
               + currentPage;
    }
    @SecurityMapping(title = "买家取消订单申请", value = "/mobile/buyer/order_cancel_apply.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/order_cancel_apply.htm" })
    public ModelAndView order_cancel_apply(HttpServletRequest request,
                                           HttpServletResponse response, String id, String view) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/order_cancel_apply.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (obj.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {
            mv.addObject("obj", obj);
            if ((view != null) && (!view.equals(""))) {
                mv.addObject("view", Boolean.valueOf(true));
            }
        } else {
            mv = new JModelAndView("user/default/usercenter/h5/error.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0,
                request, response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/order.htm");
        }
        mv.addObject("math", new MathTool());
        mv.addObject("values", State.CANCEL_REASON.getValues());
        mv.addObject("keys", State.CANCEL_REASON.getKeys());
        mv.addObject("reason", "");
       /* if (obj.getRefund_reason() != null) {
            mv.addObject("reason", State.getValue(State.CANCEL_REASON, obj.getRefund_reason()));
        } else {
            mv.addObject("reason", "");
        }*/
        return mv;
    }
    @SecurityMapping(title = "买家取消订单申请保存", value = "/mobile/buyer/order_cancel_apply_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/order_cancel_apply_save.htm" })
    public String order_cancel_apply_save(HttpServletRequest request, HttpServletResponse response,
                                          String id, String currentPage, String cancel_content,
                                          String cancel_reason) throws Exception {
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (obj.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {
            obj.setOrder_status(25);
            this.orderFormService.update(obj);
            OrderFormCancelAudit audit = new OrderFormCancelAudit();
            audit.setCancel_content(cancel_content);
            audit.setCancel_reason(Integer.valueOf(cancel_reason == null || cancel_reason.equals("") ? "1"
                    : cancel_reason));
            audit.setCreatetime(new Date());
            audit.setDisabled(false);
            audit.setOf(obj);
            audit.setStore(obj.getStore());
            audit.setState(1);
            this.orderFormCancelAuditService.save(audit);
            //取消订单申请日志
            OrderFormLog ofl = new OrderFormLog();
            ofl.setCreatetime(new Date());
            ofl.setLog_info("订货商申请取消订单");
            ofl.setLog_user(SecurityUserHolder.getCurrentUser());
            ofl.setOf(obj);
            this.orderFormLogService.save(ofl);

            if (this.configService.getSysConfig().isEmailEnable()) {
                send_email(request, obj, "email_toseller_order_return_apply_notify");
            }
            if (this.configService.getSysConfig().isSmsEnbale()) {
                send_sms(request, obj, obj.getUser().getMobile(),
                    "sms_toseller_order_return_apply_notify");
            }
        }
        return "redirect:" + CommUtil.getURL(request) + "/mobile/buyer/order.htm?currentPage="
               + currentPage;
    }

    @SecurityMapping(title = "买家退货物流信息", value = "/mobile/buyer/order_return_ship.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/order_return_ship.htm" })
    public ModelAndView order_return_ship(HttpServletRequest request, HttpServletResponse response,
                                          String id, String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/order_return_ship.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (obj.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {
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
            mv = new JModelAndView("user/default/usercenter/h5/error.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0,
                request, response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile//buyer/order.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "买家退货物流信息保存", value = "/mobile/buyer/order_return_ship_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/order_return_ship_save.htm" })
    public String order_return_ship_save(HttpServletRequest request, HttpServletResponse response,
                                         String id, String currentPage, String ec_id,
                                         String return_shipCode) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/order_return_apply_view.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        ExpressCompany ec = this.expressCompayService.getObjById(CommUtil.null2Long(ec_id));
        obj.setReturn_ec(ec);
        obj.setReturn_shipCode(return_shipCode);
        this.orderFormService.update(obj);
        return "redirect:" + CommUtil.getURL(request) + "/mobile/buyer/order.htm?currentPage="
               + currentPage;
    }

    @SecurityMapping(title = "收货确认", value = "/mobile/buyer/order_cofirm.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/order_cofirm.htm" })
    public ModelAndView order_cofirm(HttpServletRequest request, HttpServletResponse response,
                                     String id, String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/buyer_order_cofirm.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (obj.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {
            mv.addObject("obj", obj);
            mv.addObject("currentPage", currentPage);
        } else {
            mv = new JModelAndView("user/default/usercenter/h5/error.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0,
                request, response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer//buyer/order.htm");
        }

        return mv;
    }

    @SecurityMapping(title = "收货确认保存", value = "/mobile/buyer/order_cofirm_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/order_cofirm_save.htm" })
    public String order_cofirm_save(HttpServletRequest request, HttpServletResponse response,
                                    String id, String currentPage) throws Exception {
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (obj.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {
            obj.setOrder_status(40);
            boolean ret = this.orderFormService.update(obj);
            if (ret) {

                OrderFormLog ofl = new OrderFormLog();
                ofl.setCreatetime(new Date());
                ofl.setLog_info("确认收货");
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
                    "买家确认订单");

                if (this.configService.getSysConfig().isEmailEnable()) {
                    send_email(request, obj, "email_toseller_order_receive_ok_notify");
                }
                /*if (this.configService.getSysConfig().isSmsEnbale()) {
                    send_sms(request, obj, obj.getStore().getUser().getMobile(),
                        "sms_toseller_order_receive_ok_notify");
                }*/

            }
        }

        String url = "redirect:" + CommUtil.getURL(request) + "/mobile/buyer/order.htm";
        return url;
    }

    @SecurityMapping(title = "虚拟商品信息", value = "/mobile/buyer/order_seller_intro.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/order_seller_intro.htm" })
    public ModelAndView order_seller_intro(HttpServletRequest request,
                                           HttpServletResponse response, String id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/order_seller_intro.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (obj.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {
            mv.addObject("obj", obj);
        }
        return mv;
    }

    @SecurityMapping(title = "买家评价", value = "/mobile/buyer/order_evaluate.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/store/{storeId}.htm/mobile/buyer/order_evaluate.htm" })
    public ModelAndView order_evaluate(HttpServletRequest request, HttpServletResponse response,
                                       String id, @PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/buyer_order_evaluate.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (SecurityUserHolder.getCurrentUser() == null) {
            mv = new JModelAndView("h5/login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("storeId", storeId);
            return mv;
        }
        if (obj.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {
            mv.addObject("obj", obj);
            if (obj.getOrder_status() >= 50) {
                mv = new JModelAndView("user/default/usercenter/h5/success.html",
                    this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
                    request, response);
                mv.addObject("op_title", "订单已经评价！");
                mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
            }
        } else {
            mv = new JModelAndView("user/default/usercenter/h5/error.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
                request, response);
            mv.addObject("op_title", "您没有编号为" + id + "的订单！");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/order.htm");
        }

        return mv;
    }

    @SecurityMapping(title = "买家评价保存", value = "/mobile/buyer/order_evaluate_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/order_evaluate_save.htm" })
    public ModelAndView order_evaluate_save(HttpServletRequest request,
                                            HttpServletResponse response, String id)
                                                                                    throws Exception {
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (obj.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {
            if (obj.getOrder_status() == 40) {
                obj.setOrder_status(50);
                this.orderFormService.update(obj);
                OrderFormLog ofl = new OrderFormLog();
                ofl.setCreatetime(new Date());
                ofl.setLog_info("评价订单");
                ofl.setLog_user(SecurityUserHolder.getCurrentUser());
                ofl.setOf(obj);
                this.orderFormLogService.save(ofl);
                Evaluate eva = null;
                for (GoodsCart gc : obj.getGcs()) {
                    eva = new Evaluate();
                    eva.setCreatetime(new Date());
                    eva.setEvaluate_goods(gc.getGoods());
                    eva.setEvaluate_info(request.getParameter("evaluate_info_" + gc.getId()));
                    eva.setEvaluate_buyer_val(CommUtil.null2Int(request
                        .getParameter("evaluate_buyer_val" + gc.getId())));
                    eva.setDescription_evaluate(BigDecimal.valueOf(CommUtil.null2Double(request
                        .getParameter("description_evaluate" + gc.getId()))));
                    eva.setService_evaluate(BigDecimal.valueOf(CommUtil.null2Double(request
                        .getParameter("service_evaluate" + gc.getId()))));
                    eva.setShip_evaluate(BigDecimal.valueOf(CommUtil.null2Double(request
                        .getParameter("ship_evaluate" + gc.getId()))));
                    eva.setEvaluate_type("goods");
                    eva.setEvaluate_user(SecurityUserHolder.getCurrentUser());
                    eva.setOf(obj);
                    eva.setGoods_spec(gc.getSpec_info());
                    this.evaluateService.save(eva);
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("store_id", obj.getStore().getId());
                    List<Evaluate> evas = this.evaluateService.query(
                        "select obj from Evaluate obj where obj.of.store.id=:store_id", params, -1,
                        -1);
                    double store_evaluate1 = 0.0D;
                    double store_evaluate1_total = 0.0D;
                    double description_evaluate = 0.0D;
                    double description_evaluate_total = 0.0D;
                    double service_evaluate = 0.0D;
                    double service_evaluate_total = 0.0D;
                    double ship_evaluate = 0.0D;
                    double ship_evaluate_total = 0.0D;
                    DecimalFormat df = new DecimalFormat("0.0");
                    for (Evaluate eva1 : evas) {
                        store_evaluate1_total = store_evaluate1_total
                                                + eva1.getEvaluate_buyer_val();

                        description_evaluate_total = description_evaluate_total
                                                     + CommUtil.null2Double(eva1
                                                         .getDescription_evaluate());

                        service_evaluate_total = service_evaluate_total
                                                 + CommUtil.null2Double(eva1.getService_evaluate());

                        ship_evaluate_total = ship_evaluate_total
                                              + CommUtil.null2Double(eva1.getShip_evaluate());
                    }
                    store_evaluate1 = CommUtil.null2Double(df.format(store_evaluate1_total
                                                                     / evas.size()));
                    description_evaluate = CommUtil.null2Double(df
                        .format(description_evaluate_total / evas.size()));
                    service_evaluate = CommUtil.null2Double(df.format(service_evaluate_total
                                                                      / evas.size()));
                    ship_evaluate = CommUtil.null2Double(df.format(ship_evaluate_total
                                                                   / evas.size()));
                    Store store = obj.getStore();
                    store.setStore_credit(store.getStore_credit() + eva.getEvaluate_buyer_val());
                    this.storeService.update(store);
                    params.clear();
                    params.put("store_id", store.getId());
                    Object sps = this.storePointService.query(
                        "select obj from StorePoint obj where obj.store.id=:store_id", params, -1,
                        -1);
                    StorePoint point = null;
                    if (((List) sps).size() > 0) {
                        point = (StorePoint) ((List) sps).get(0);
                    } else {
                        point = new StorePoint();
                    }
                    point.setCreatetime(new Date());
                    point.setStore(store);
                    point.setDescription_evaluate(BigDecimal.valueOf(description_evaluate));
                    point.setService_evaluate(BigDecimal.valueOf(service_evaluate));
                    point.setShip_evaluate(BigDecimal.valueOf(ship_evaluate));
                    point.setStore_evaluate1(BigDecimal.valueOf(store_evaluate1));
                    if (((List) sps).size() > 0) {
                        this.storePointService.update(point);
                    } else {
                        this.storePointService.save(point);
                    }
                    User user = obj.getUser();
                    user.setIntegral(user.getIntegral()
                                     + this.configService.getSysConfig().getIndentComment());
                    this.userService.update(user);
                }
            }
            if (this.configService.getSysConfig().isEmailEnable()) {
                send_email(request, obj, "email_toseller_evaluate_ok_notify");
            }
        }

        List<SettleLog> sls = this.settleLogService.query(
            "select obj from SettleLog obj where obj.order.id=" + obj.getId(), null, -1, -1);
        for (SettleLog sl : sls) {
            sl.setStatus(2);
            settleLogService.update(sl);
        }

        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("op_title", "订单评价成功！");
        mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/order.htm");

        return mv;
    }

    @SecurityMapping(title = "买家退款列表", value = "/mobile/buyer/refund_view.htm", rtype = "seller", rname = "退款管理", rcode = "refund_seller", rgroup = "客户服务")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/refund_view.htm" })
    public ModelAndView refund_view(HttpServletRequest request, HttpServletResponse response,
                                    String id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/buyerrefund_view.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        RefundLog obj = this.refundLogService.getObjById(CommUtil.null2Long(id));
        mv.addObject("obj", obj);
        return mv;
    }

    @SecurityMapping(title = "买家投诉发起", value = "/mobile/buyer/complaint_handle.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/complaint_handle.htm" })
    public ModelAndView complaint_handle(HttpServletRequest request, HttpServletResponse response,
                                         String order_id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/complaint_handle.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
        Calendar calendar = Calendar.getInstance();
        calendar.add(6, -this.configService.getSysConfig().getComplaint_time());
        boolean result = true;
        if ((of.getOrder_status() == 60) && (of.getFinishTime().before(calendar.getTime()))) {
            result = false;
        }
        boolean result1 = true;
        if (of.getComplaints().size() > 0) {
            for (Complaint complaint : of.getComplaints()) {
                if (complaint.getFrom_user().getId()
                    .equals(SecurityUserHolder.getCurrentUser().getId())) {
                    result1 = false;
                }
            }
        }
        if (result) {
            if (result1) {
                Complaint obj = new Complaint();
                obj.setFrom_user(SecurityUserHolder.getCurrentUser());
                obj.setStatus(0);
                obj.setType("buyer");
                obj.setOf(of);
                obj.setTo_user(of.getStore().getUser());
                mv.addObject("obj", obj);
                Object params = new HashMap();
                ((Map) params).put("type", "buyer");
                List<ComplaintSubject> css = this.complaintSubjectService.query(
                    "select obj from ComplaintSubject obj where obj.type=:type", (Map) params, -1,
                    -1);
                mv.addObject("css", css);
            } else {
                mv = new JModelAndView("user/default/usercenter/h5/error.html",
                    this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0,
                    request, response);
                mv.addObject("op_title", "该订单已经投诉，不允许重复投诉");
                mv.addObject("url", CommUtil.getURL(request) + "/buyer/order.htm");
            }
        } else {
            mv = new JModelAndView("user/default/usercenter/h5/error.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0,
                request, response);
            mv.addObject("op_title", "该订单已经超过投诉有效期，不能投诉");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/order.htm");
        }
        return mv;
    }

    /**
     * 测试使用
     */
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/order_finish.htm" })
    public ModelAndView order_finish(HttpServletRequest request, HttpServletResponse response,
                                     String order_id) {
        ModelAndView mv = new JModelAndView("h5/order_finish.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        OrderForm objById = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
        mv.addObject("obj", objById);
        return mv;
    }

}
