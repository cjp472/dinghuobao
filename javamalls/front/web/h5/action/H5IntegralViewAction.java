package com.javamalls.front.web.h5.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.ctrl.admin.tools.PaymentTools;
import com.javamalls.payment.tools.PayTools;
import com.javamalls.platform.domain.Accessory;
import com.javamalls.platform.domain.Address;
import com.javamalls.platform.domain.Area;
import com.javamalls.platform.domain.IntegralGoods;
import com.javamalls.platform.domain.IntegralGoodsCart;
import com.javamalls.platform.domain.IntegralGoodsOrder;
import com.javamalls.platform.domain.IntegralLog;
import com.javamalls.platform.domain.Payment;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.query.IntegralGoodsQueryObject;
import com.javamalls.platform.service.IAddressService;
import com.javamalls.platform.service.IAreaService;
import com.javamalls.platform.service.IIntegralGoodsCartService;
import com.javamalls.platform.service.IIntegralGoodsOrderService;
import com.javamalls.platform.service.IIntegralGoodsService;
import com.javamalls.platform.service.IIntegralLogService;
import com.javamalls.platform.service.IPaymentService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;

/**积分
 *                       
 * @Filename: IntegralViewAction.java
 * @Version: 2.7.0
 * @Author: 刘杰
 * @Email: attay125@163.com
 *
 */
@Controller
public class H5IntegralViewAction {
    @Autowired
    private ISysConfigService          configService;
    @Autowired
    private IUserConfigService         userConfigService;
    @Autowired
    private IIntegralGoodsService      integralGoodsService;
    @Autowired
    private IUserService               userService;
    @Autowired
    private IAddressService            addressService;
    @Autowired
    private IIntegralGoodsOrderService integralGoodsOrderService;
    @Autowired
    private IIntegralGoodsCartService  integralGoodsCartService;
    @Autowired
    private IPaymentService            paymentService;
    @Autowired
    private IIntegralLogService        integralLogService;
    @Autowired
    private IAreaService               areaService;
    @Autowired
    private PaymentTools               paymentTools;
    @Autowired
    private PayTools                   payTools;

    @SecurityMapping(title = "订单支付详情", value = "/mobile/integral_order_pay_view.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/mobile/integral_order_pay_view.htm" })
    public ModelAndView integral_order_pay_view(HttpServletRequest request,
                                                HttpServletResponse response, String id) {
        ModelAndView mv = new JModelAndView("h5/integral_exchange4.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        IntegralGoodsOrder obj = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(id));
        if (obj.getIgo_status() == 0) {
            mv.addObject("obj", obj);
            mv.addObject("paymentTools", this.paymentTools);
            mv.addObject("url", CommUtil.getURL(request));
        } else if (obj.getIgo_status() < 0) {
            mv = new JModelAndView("user/default/usercenter/h5/error.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0,
                request, response);
            mv.addObject("op_title", "该订单已经取消！");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/integral_order_list.htm");
        } else {
            mv = new JModelAndView("user/default/usercenter/h5/error.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0,
                request, response);
            mv.addObject("op_title", "该订单已经付款！");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/integral_order_list.htm");
        }
        return mv;
    }

    @RequestMapping({ "/mobile/integral.htm" })
    public ModelAndView integral(HttpServletRequest request, HttpServletResponse response,
                                 String goods_current_price_begin, String goods_current_price_end,
                                 String orderBy, String orderType, String currentPage,
                                 String createtime, String recommend, String ig_goods_integral) {
        if ("".equals(CommUtil.null2String(currentPage)))
            currentPage = "1";
        ModelAndView mv = new JModelAndView("h5/integral.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);

        if (orderBy == null || orderBy.equals("")) {
            orderBy = "createtime";
        }
        if (orderType == null || orderType.equals("")) {
            orderType = "asc";
        }

        IntegralGoodsQueryObject gqo = new IntegralGoodsQueryObject(currentPage, mv, orderBy,
            orderType);

        if (this.configService.getSysConfig().isIntegralStore()) {

            gqo.addQuery("ig_show", new SysMap("show", Boolean.valueOf(true)), "=");

            if (recommend != null && !recommend.equals("")) {
                gqo.addQuery("obj.ig_recommend", new SysMap(recommend, Boolean.valueOf(recommend)),
                    "=");
                mv.addObject("ig_recommend", recommend);
            } else if (ig_goods_integral != null && !ig_goods_integral.equals("")) {
                gqo.addQuery("obj.ig_goods_integral",
                    new SysMap("ig_goods_integral", CommUtil.null2Int(ig_goods_integral)), "=");
                mv.addObject("ig_goods_integral", ig_goods_integral);
            }
            if (goods_current_price_begin != null && !goods_current_price_begin.equals("")) {
                gqo.addQuery("obj.ig_goods_integral", new SysMap("goods_current_price_begin",
                    CommUtil.null2Int((goods_current_price_begin))), ">=");
                mv.addObject("goods_current_price_begin", goods_current_price_begin);
            }
            if (goods_current_price_end != null && !goods_current_price_end.equals("")) {
                gqo.addQuery("obj.ig_goods_integral", new SysMap("goods_current_price_end",
                    CommUtil.null2Int(goods_current_price_end)), "<=");
                mv.addObject("goods_current_price_end", goods_current_price_end);
            }

            gqo.setPageSize(Integer.valueOf(2));
            IPageList objs = this.integralGoodsService.list(gqo);
            CommUtil.saveIPageList2ModelAndView("", "", "", objs, mv);
        } else {
            mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "系统未开启积分商城");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
        }
        return mv;
    }

    @RequestMapping({ "/mobile/integral_gift_view.htm" })
    public ModelAndView integral_view(HttpServletRequest request, HttpServletResponse response,
                                      String id) {
        ModelAndView mv = new JModelAndView("h5/integral_view.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        if (this.configService.getSysConfig().isIntegralStore()) {
            IntegralGoods obj = this.integralGoodsService.getObjById(CommUtil.null2Long(id));
            obj.setIg_click_count(obj.getIg_click_count() + 1);
            this.integralGoodsService.update(obj);
            List<IntegralGoodsCart> gcs = this.integralGoodsCartService.query(
                "select obj from IntegralGoodsCart obj order by obj.createtime desc", null, 0, 20);
            mv.addObject("gcs", gcs);

            mv.addObject("obj", obj);
            mv.addObject("view_url", CommUtil.getURL(request) + "/integral_view" + id + ".htm");
        } else {
            mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "系统未开启积分商城");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
        }
        return mv;
    }

    @RequestMapping({ "/mobile/integral_list.htm" })
    public void integral_list(HttpServletRequest request, HttpServletResponse response,
                              String currentPage, String orderBy, String orderType,
                              String rang_begin, String rang_end, String goods_current_price_begin,
                              String goods_current_price_end, String createtime, String recommend,
                              String ig_goods_integral) {
        ModelAndView mv = new JModelAndView("", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);

        if (orderBy == null || orderBy.equals("")) {
            orderBy = "createtime";
        }
        if (orderType == null || orderType.equals("")) {
            orderType = "asc";
        }

        IntegralGoodsQueryObject gqo = new IntegralGoodsQueryObject(currentPage, mv, orderBy,
            orderType);

        if (this.configService.getSysConfig().isIntegralStore()) {

            gqo.addQuery("ig_show", new SysMap("show", Boolean.valueOf(true)), "=");

            if (recommend != null && !recommend.equals("")) {
                gqo.addQuery("obj.ig_recommend", new SysMap(recommend, Boolean.valueOf(recommend)),
                    "=");
                mv.addObject("ig_recommend", recommend);
            } else if (ig_goods_integral != null && !ig_goods_integral.equals("")) {
                gqo.addQuery("obj.ig_goods_integral",
                    new SysMap("ig_goods_integral", CommUtil.null2Int(ig_goods_integral)), "=");
                mv.addObject("ig_goods_integral", ig_goods_integral);
            }
            if (goods_current_price_begin != null && !goods_current_price_begin.equals("")) {
                gqo.addQuery("obj.ig_goods_integral", new SysMap("goods_current_price_begin",
                    CommUtil.null2Int((goods_current_price_begin))), ">=");
                mv.addObject("goods_current_price_begin", goods_current_price_begin);
            }
            if (goods_current_price_end != null && !goods_current_price_end.equals("")) {
                gqo.addQuery("obj.ig_goods_integral", new SysMap("goods_current_price_end",
                    CommUtil.null2Int(goods_current_price_end)), "<=");
                mv.addObject("goods_current_price_end", goods_current_price_end);
            }

            gqo.setPageSize(Integer.valueOf(2));
            IPageList objs = this.integralGoodsService.list(gqo);
            List<IntegralGoods> evalList = objs.getResult();
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            String imgPath = null;
            String goods_url = null;
            Accessory accessory = this.configService.getSysConfig().getGoodsImage();
            String webPath = mv.getModelMap().get("webPath").toString();
            String imageWebServer = mv.getModelMap().get("imageWebServer").toString();
            Map<String, Object> map = null;
            for (IntegralGoods evaluate : evalList) {
                imgPath = imageWebServer + File.separator + accessory.getPath() + File.separator
                          + accessory.getName();
                if (evaluate.getIg_goods_img() != null) {
                    imgPath = imageWebServer + File.separator
                              + evaluate.getIg_goods_img().getPath() + File.separator
                              + evaluate.getIg_goods_img().getName();
                }

                goods_url = webPath + "/mobile/integral_gift_view_" + evaluate.getId() + ".htm";
                map = new HashMap<String, Object>();
                map.put("id", evaluate.getId());
                map.put("imgPath", imgPath);
                map.put("goods_url", goods_url);
                map.put("ig_goods_name", evaluate.getIg_goods_name());
                map.put("ig_goods_price", evaluate.getIg_goods_price());
                map.put("ig_goods_integral", evaluate.getIg_goods_integral());
                map.put("createTime", CommUtil.formatShortDate(evaluate.getCreatetime()));
                map.put("currentPage", currentPage);
                list.add(map);
            }
            this.writeData(response, list);
        }
    }

    private void writeData(HttpServletResponse response, List<Map<String, Object>> list) {
        String temp = Json.toJson(list, JsonFormat.compact());
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //积分兑换
    @SecurityMapping(title = "积分兑换第一步", value = "/mobile/integral_exchange1.htm*", rtype = "buyer", rname = "积分兑换", rcode = "integral_exchange", rgroup = "积分兑换")
    @RequestMapping({ "/mobile/integral_exchange1.htm" })
    public ModelAndView integral_exchange1(HttpServletRequest request,
                                           HttpServletResponse response, String id,
                                           String exchange_count) {
        ModelAndView mv = new JModelAndView("h5/integral_exchange1.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        if (this.configService.getSysConfig().isIntegralStore()) {
            IntegralGoods obj = this.integralGoodsService.getObjById(CommUtil.null2Long(id));
            int exchange_status = 0;
            if (obj != null) {
                if ((exchange_count == null) || (exchange_count.equals(""))) {
                    exchange_count = "1";
                }
                if (obj.getIg_goods_count() < CommUtil.null2Int(exchange_count)) {
                    exchange_status = -1;
                    mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                        this.userConfigService.getUserConfig(), 1, request, response);
                    mv.addObject("op_title", "库存数量不足，重新选择兑换数量");
                    mv.addObject("url", CommUtil.getURL(request) + "/mobile/integral_gift_view_"
                                        + id + ".htm");
                }
                if (obj.isIg_limit_type()) {
                    if (obj.getIg_limit_count() < CommUtil.null2Int(exchange_count)) {
                        exchange_status = -2;
                        mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                            this.userConfigService.getUserConfig(), 1, request, response);
                        mv.addObject("op_title", "限制最多兑换" + obj.getIg_limit_count() + "，重新选择兑换数量");
                        mv.addObject("url", CommUtil.getURL(request)
                                            + "/mobile/integral_gift_view_" + id + ".htm");
                    }
                }
                int cart_total_integral = obj.getIg_goods_integral()
                                          * CommUtil.null2Int(exchange_count);
                User user = this.userService
                    .getObjById(SecurityUserHolder.getCurrentUser().getId());
                if (user.getIntegral() < cart_total_integral) {
                    exchange_status = -3;
                    mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                        this.userConfigService.getUserConfig(), 1, request, response);
                    mv.addObject("op_title", "您的积分不足");
                    mv.addObject("url", CommUtil.getURL(request) + "/mobile/integral_gift_view_"
                                        + id + ".htm");
                }
                if ((obj.getIg_begin_time() != null)
                    && (obj.getIg_end_time() != null)
                    && ((obj.getIg_begin_time().after(new Date())) || (obj.getIg_end_time()
                        .before(new Date())))) {
                    exchange_status = -4;
                    mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                        this.userConfigService.getUserConfig(), 1, request, response);
                    mv.addObject("op_title", "兑换已经过期");
                    mv.addObject("url", CommUtil.getURL(request) + "/mobile/integral_gift_view_"
                                        + id + ".htm");
                }
            }
            if (exchange_status == 0) {
                List<IntegralGoodsCart> integral_goods_cart = (List) request.getSession(false)
                    .getAttribute("integral_goods_cart");
                if (integral_goods_cart == null) {
                    integral_goods_cart = new ArrayList();
                }
                boolean add = obj != null;
                for (IntegralGoodsCart igc : integral_goods_cart) {
                    if (igc.getGoods().getId().toString().equals(id)) {
                        add = false;
                        if (igc.getCount() != CommUtil.null2Int(exchange_count)) {
                            //igc.setId(CommUtil.null2Long((id)));
                            igc.setCount(CommUtil.null2Int(exchange_count));
                            igc.setIntegral(CommUtil.null2Int(exchange_count)
                                            * igc.getGoods().getIg_goods_integral());
                        }
                        break;
                    }
                }
                if (add) {
                    IntegralGoodsCart gc = new IntegralGoodsCart();
                    gc.setCreatetime(new Date());
                    gc.setCount(CommUtil.null2Int(exchange_count));
                    gc.setGoods(obj);
                    gc.setTrans_fee(obj.getIg_transfee());
                    gc.setIntegral(CommUtil.null2Int(exchange_count) * obj.getIg_goods_integral());
                    integral_goods_cart.add(gc);
                }
                request.getSession(false).setAttribute("integral_goods_cart", integral_goods_cart);
                int total_integral = 0;
                for (IntegralGoodsCart igc : integral_goods_cart) {
                    total_integral += igc.getIntegral();
                }
                mv.addObject("total_integral", Integer.valueOf(total_integral));
                mv.addObject("integral_cart", integral_goods_cart);
                mv.addObject("exchange_count", exchange_count);
                mv.addObject("user",
                    this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId()));
            }
        } else {
            mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "系统未开启积分商城");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
        }
        return mv;
    }

    @RequestMapping({ "/mobile/integral_cart_remove.htm" })
    public void integral_cart_remove(HttpServletRequest request, HttpServletResponse response,
                                     String id) {
        List<IntegralGoodsCart> igcs = (List) request.getSession(false).getAttribute(
            "integral_goods_cart");
        for (IntegralGoodsCart igc : igcs) {
            if (igc.getGoods().getId().toString().equals(id)) {
                igcs.remove(igc);
                break;
            }
        }
        int total_integral = 0;
        for (IntegralGoodsCart igc : igcs) {
            total_integral += igc.getIntegral();
        }
        request.getSession(false).setAttribute("integral_goods_cart", igcs);
        Object map = new HashMap();
        ((Map) map).put("status", Integer.valueOf(100));
        ((Map) map).put("total_integral", Integer.valueOf(total_integral));
        ((Map) map).put("size", Integer.valueOf(igcs.size()));
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(Json.toJson(map, JsonFormat.compact()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SecurityMapping(title = "积分兑换第二步", value = "/mobile/integral_exchange2.htm*", rtype = "buyer", rname = "积分兑换", rcode = "integral_exchange", rgroup = "积分兑换")
    @RequestMapping({ "/mobile/integral_exchange2.htm" })
    public ModelAndView integral_exchange2(HttpServletRequest request,
                                           HttpServletResponse response, String id,
                                           String exchange_count) {
        ModelAndView mv = new JModelAndView("h5/integral_exchange2.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        if (this.configService.getSysConfig().isIntegralStore()) {
            List<IntegralGoodsCart> igcs = (List) request.getSession(false).getAttribute(
                "integral_goods_cart");
            if (igcs != null) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
                List<Address> addrs = this.addressService.query(
                    "select obj from Address obj where obj.disabled = 0 and obj.user.id=:user_id",
                    params, -1, -1);
                mv.addObject("addrs", addrs);
                mv.addObject("igcs", igcs == null ? new ArrayList() : igcs);
                int total_integral = 0;
                double trans_fee = 0.0D;
                for (IntegralGoodsCart igc : igcs) {
                    total_integral += igc.getIntegral();
                    trans_fee = CommUtil.null2Double(igc.getTrans_fee()) + trans_fee;
                }
                mv.addObject("trans_fee", Double.valueOf(trans_fee));
                mv.addObject("total_integral", Integer.valueOf(total_integral));
                String integral_order_session = CommUtil.randomString(32);
                mv.addObject("integral_order_session", integral_order_session);
                request.getSession(false).setAttribute("integral_order_session",
                    integral_order_session);
                Object areas = this.areaService.query(
                    "select obj from Area obj where obj.parent.id is null", null, -1, -1);
                mv.addObject("areas", areas);
            } else {
                mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "兑换购物车为空");
                mv.addObject("url", CommUtil.getURL(request) + "/mobile/integral.htm");
            }
        } else {
            mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "系统未开启积分商城");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
        }
        return mv;
    }

    @RequestMapping({ "/mobile/integral_adjust_count.htm" })
    public void integral_adjust_count(HttpServletRequest request, HttpServletResponse response,
                                      String goods_id, String count) {
        List<IntegralGoodsCart> igcs = (List) request.getSession(false).getAttribute(
            "integral_goods_cart");
        IntegralGoodsCart obj = null;
        int num = CommUtil.null2Int(count);
        IntegralGoods ig;
        for (IntegralGoodsCart igc : igcs) {
            if (igc.getGoods().getId().toString().equals(goods_id)) {
                ig = igc.getGoods();
                if (num > ig.getIg_goods_count()) {
                    num = ig.getIg_goods_count();
                }
                if ((ig.isIg_limit_type()) && (ig.getIg_limit_count() < num)) {
                    num = ig.getIg_limit_count();
                }
                igc.setCount(num);
                igc.setIntegral(igc.getGoods().getIg_goods_integral()
                                * CommUtil.null2Int(Integer.valueOf(num)));
                obj = igc;
                break;
            }
        }
        int total_integral = 0;
        for (IntegralGoodsCart igc : igcs) {
            total_integral += igc.getIntegral();
        }
        request.getSession(false).setAttribute("integral_goods_cart", igcs);
        Object map = new HashMap();
        ((Map) map).put("total_integral", Integer.valueOf(total_integral));
        ((Map) map).put("integral", Integer.valueOf(obj.getIntegral()));
        ((Map) map).put("count", Integer.valueOf(num));
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(Json.toJson(map, JsonFormat.compact()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SecurityMapping(title = "积分兑换第三步", value = "/mobile/integral_exchange3.htm*", rtype = "buyer", rname = "积分兑换", rcode = "integral_exchange", rgroup = "积分兑换")
    @RequestMapping({ "/mobile/integral_exchange3.htm" })
    public ModelAndView integral_exchange3(HttpServletRequest request,
                                           HttpServletResponse response, String addr_id,
                                           String igo_msg, String integral_order_session,
                                           String area_id, String trueName, String area_info,
                                           String zip, String telephone, String mobile) {
        ModelAndView mv = new JModelAndView("h5/integral_exchange3.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        if (this.configService.getSysConfig().isIntegralStore()) {
            List<IntegralGoodsCart> igcs = (List) request.getSession(false).getAttribute(
                "integral_goods_cart");
            String integral_order_session1 = CommUtil.null2String(request.getSession(false)
                .getAttribute("integral_order_session"));
            if (integral_order_session1.equals("")) {
                mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "订单已经过期");
                mv.addObject("url", CommUtil.getURL(request) + "/mobile/integral.htm");
            } else if (integral_order_session1.equals(integral_order_session.trim())) {
                if (igcs != null) {
                    int total_integral = 0;
                    double trans_fee = 0.0D;
                    for (IntegralGoodsCart igc : igcs) {
                        total_integral += igc.getIntegral();
                        trans_fee = CommUtil.null2Double(igc.getTrans_fee()) + trans_fee;
                    }
                    IntegralGoodsOrder order = new IntegralGoodsOrder();
                    Address addr = null;
                    if (addr_id.equals("new")) {
                        addr = new Address();
                        addr.setCreatetime(new Date());
                        Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
                        addr.setArea_info(area_info);
                        addr.setMobile(mobile);
                        addr.setTelephone(telephone);
                        addr.setTrueName(trueName);
                        addr.setZip(zip);
                        addr.setArea(area);
                        addr.setUser(SecurityUserHolder.getCurrentUser());
                        this.addressService.save(addr);
                    } else {
                        addr = this.addressService.getObjById(CommUtil.null2Long(addr_id));
                    }
                    order.setCreatetime(new Date());
                    order.setIgo_addr(addr);
                    order.setIgo_gcs(igcs);
                    order.setIgo_msg(igo_msg);
                    User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser()
                        .getId());
                    order.setIgo_order_sn("jmf" + CommUtil.formatTime("yyyyMMddHHmmss", new Date())
                                          + user.getId());
                    order.setIgo_user(user);
                    order.setIgo_trans_fee(BigDecimal.valueOf(trans_fee));
                    order.setIgo_total_integral(total_integral);
                    for (IntegralGoodsCart igc : igcs) {
                        igc.setOrder(order);
                    }
                    if (trans_fee == 0.0D) {
                        order.setIgo_status(20);
                        order.setIgo_pay_time(new Date());
                        order.setIgo_payment("no_fee");
                        this.integralGoodsOrderService.save(order);
                        for (IntegralGoodsCart igc : order.getIgo_gcs()) {
                            IntegralGoods goods = igc.getGoods();
                            goods.setIg_goods_count(goods.getIg_goods_count() - igc.getCount());
                            goods.setIg_exchange_count(goods.getIg_exchange_count()
                                                       + igc.getCount());
                            this.integralGoodsService.update(goods);
                        }
                        request.getSession(false).removeAttribute("integral_goods_cart");
                        mv.addObject("url", CommUtil.getURL(request) + "/mobile/integral.htm");
                        mv.addObject("order", order);
                    } else {
                        order.setIgo_status(0);
                        this.integralGoodsOrderService.save(order);
                        mv = new JModelAndView("h5/integral_exchange4.html",
                            this.configService.getSysConfig(),
                            this.userConfigService.getUserConfig(), 1, request, response);
                        mv.addObject("obj", order);
                        mv.addObject("paymentTools", this.paymentTools);
                    }
                    user.setIntegral(user.getIntegral() - order.getIgo_total_integral());
                    this.userService.update(user);

                    IntegralLog log = new IntegralLog();
                    log.setCreatetime(new Date());
                    log.setContent("兑换商品消耗积分");
                    log.setIntegral(-order.getIgo_total_integral());
                    log.setIntegral_user(user);
                    log.setType("integral_order");
                    this.integralLogService.save(log);
                    request.getSession(false).removeAttribute("integral_goods_cart");
                } else {
                    mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                        this.userConfigService.getUserConfig(), 1, request, response);
                    mv.addObject("op_title", "兑换购物车为空");
                    mv.addObject("url", CommUtil.getURL(request) + "/mobile/integral.htm");
                }
            } else {
                mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "参数错误，订单提交失败");
                mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
            }
        } else {
            mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "系统未开启积分商城");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "积分订单支付", value = "/mobile/integral_order_pay.htm*", rtype = "buyer", rname = "积分兑换", rcode = "integral_exchange", rgroup = "积分兑换")
    @RequestMapping({ "/mobile/integral_order_pay.htm" })
    public ModelAndView integral_order_pay(HttpServletRequest request,
                                           HttpServletResponse response, String payType,
                                           String integral_order_id) {
        ModelAndView mv = null;
        IntegralGoodsOrder order = this.integralGoodsOrderService.getObjById(CommUtil
            .null2Long(integral_order_id));
        if (order.getIgo_status() == 0) {
            if (CommUtil.null2String(payType).equals("")) {
                mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "支付方式错误！");
                mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
            } else {
                order.setIgo_payment(payType);
                this.integralGoodsOrderService.update(order);
                if (payType.equals("balance")) {
                    mv = new JModelAndView("h5/integral_balance_pay.html",
                        this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
                        1, request, response);
                    User u = this.userService.getObjById(SecurityUserHolder.getCurrentUser()
                            .getId());
                        if (CommUtil.subtract(order.getIgo_trans_fee(), u.getAvailableBalance()) > 0) {
                            mv.addObject("flag", true);
                        } else {
                            mv.addObject("flag", false);
                        }
                        mv.addObject("of", order);
                } else if (payType.equals("outline")) {
                    mv = new JModelAndView("h5/integral_outline_pay.html",
                        this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
                        1, request, response);
                    String integral_pay_session = CommUtil.randomString(32);
                    request.getSession(false).setAttribute("integral_pay_session",
                        integral_pay_session);
                    mv.addObject("paymentTools", this.paymentTools);
                    mv.addObject("integral_pay_session", integral_pay_session);
                    //  mv.addObject("store", order.get.get.getStore());
                    //                  mv.addObject("srore",order.getStore());
                    mv.addObject("of", order);
                } else {
                    mv = new JModelAndView("h5/line_pay.html", this.configService.getSysConfig(),
                        this.userConfigService.getUserConfig(), 1, request, response);
                    mv.addObject("payType", payType);
                    mv.addObject("url", CommUtil.getURL(request));
                    mv.addObject("payTools", this.payTools);
                    mv.addObject("type", "integral");
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("install", Boolean.valueOf(true));
                    params.put("mark", payType);
                    params.put("type", "admin");
                    List<Payment> payments = this.paymentService
                        .query(
                            "select obj from Payment obj where obj.install=:install and obj.mark=:mark and obj.type=:type",
                            params, -1, -1);
                    mv.addObject("payment_id",
                        payments.size() > 0 ? ((Payment) payments.get(0)).getId() : new Payment());
                }
                mv.addObject("integral_order_id", integral_order_id);
            }
        } else {
            mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "该订单不能进行付款！");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "积分订单支付结果", value = "/mobile/integral_order_finish.htm*", rtype = "buyer", rname = "积分兑换", rcode = "integral_exchange", rgroup = "积分兑换")
    @RequestMapping({ "/mobile/integral_order_finish.htm" })
    public ModelAndView integral_order_finish(HttpServletRequest request,
                                              HttpServletResponse response, String order_id) {
        ModelAndView mv = new JModelAndView("h5/integral_order_finish.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        IntegralGoodsOrder obj = this.integralGoodsOrderService.getObjById(CommUtil
            .null2Long(order_id));
        mv.addObject("obj", obj);
        return mv;
    }

    @SecurityMapping(title = "订单线下支付", value = "/mobile/integral_order_pay_outline.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/mobile/integral_order_pay_outline.htm" })
    public ModelAndView integral_order_pay_outline(HttpServletRequest request,
                                                   HttpServletResponse response, String payType,
                                                   String integral_order_id, String igo_pay_msg,
                                                   String integral_pay_session) {
        ModelAndView mv = new JModelAndView("h5/success.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        String integral_pay_session1 = CommUtil.null2String(request.getSession(false).getAttribute(
            "integral_pay_session"));
        if (integral_pay_session1.equals(integral_pay_session)) {
            IntegralGoodsOrder order = this.integralGoodsOrderService.getObjById(CommUtil
                .null2Long(integral_order_id));
            order.setIgo_pay_msg(igo_pay_msg);
            order.setIgo_payment("outline");
            order.setIgo_pay_time(new Date());
            order.setIgo_status(10);
            this.integralGoodsOrderService.update(order);
            request.getSession(false).removeAttribute("pay_session");
            mv.addObject("op_title", "线下支付提交成功，等待审核！");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/integral_order_list.htm");
        } else {
            mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "订单已经支付，禁止重复支付！");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/integral_order_list.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "订单预付款支付", value = "/mobile/integral_order_pay_balance.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/mobile/integral_order_pay_balance.htm" })
    public ModelAndView integral_order_pay_balance(HttpServletRequest request,
                                                   HttpServletResponse response, String payType,
                                                   String integral_order_id, String igo_pay_msg) {
        ModelAndView mv = new JModelAndView("h5/success.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        IntegralGoodsOrder order = this.integralGoodsOrderService.getObjById(CommUtil
            .null2Long(integral_order_id));
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        if (CommUtil.null2Double(user.getAvailableBalance()) > CommUtil.null2Double(order
            .getIgo_trans_fee())) {
            order.setIgo_pay_msg(igo_pay_msg);
            order.setIgo_status(20);
            order.setIgo_payment("balance");
            order.setIgo_pay_time(new Date());
            boolean ret = this.integralGoodsOrderService.update(order);
            if (ret) {
                user.setAvailableBalance(BigDecimal.valueOf(CommUtil.subtract(
                    user.getAvailableBalance(), order.getIgo_trans_fee())));
                this.userService.update(user);
                for (IntegralGoodsCart igc : order.getIgo_gcs()) {
                    IntegralGoods goods = igc.getGoods();
                    goods.setIg_goods_count(goods.getIg_goods_count() - igc.getCount());
                    goods.setIg_exchange_count(goods.getIg_exchange_count() + igc.getCount());
                    this.integralGoodsService.update(goods);
                }
            }
            mv.addObject("op_title", "预付款支付成功！");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/integral_order_list.htm");
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "可用余额不足，支付失败！");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/integral_order_list.htm");
        }
        return mv;
    }
}
