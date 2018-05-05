package com.javamalls.ctrl.h5.buyer.action;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.domain.IntegralGoods;
import com.javamalls.platform.domain.IntegralGoodsCart;
import com.javamalls.platform.domain.IntegralGoodsOrder;
import com.javamalls.platform.domain.IntegralLog;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.query.IntegralGoodsOrderQueryObject;
import com.javamalls.platform.service.IIntegralGoodsOrderService;
import com.javamalls.platform.service.IIntegralGoodsService;
import com.javamalls.platform.service.IIntegralLogService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;

@Controller
public class H5IntegralOrderBuyerAction {
    @Autowired
    private ISysConfigService          configService;
    @Autowired
    private IUserConfigService         userConfigService;
    @Autowired
    private IIntegralGoodsService      integralGoodsService;
    @Autowired
    private IIntegralGoodsOrderService integralGoodsOrderService;
    @Autowired
    private IUserService               userService;
    @Autowired
    private IIntegralLogService        integralLogService;

    @SecurityMapping(title = "买家订单列表", value = "/mobile/buyer/integral_order_list.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/integral_order_list.htm" })
    public ModelAndView integral_order_list(HttpServletRequest request,
                                            HttpServletResponse response, String currentPage,String order_status,@PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/integral_order_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if (this.configService.getSysConfig().isIntegralStore()) {

            String url = CommUtil.getURL(request);
            if(SecurityUserHolder.getCurrentUser()==null){
            	mv = new JModelAndView("h5/login.html", this.configService.getSysConfig(),
                        this.userConfigService.getUserConfig(), 1, request, response);
            	mv.addObject("storeId", storeId);
            	return mv;
            }
            
            IntegralGoodsOrderQueryObject qo = new IntegralGoodsOrderQueryObject(currentPage, mv,
                "createtime", "desc");
            qo.addQuery("obj.igo_user.id", new SysMap("user_id", SecurityUserHolder
                .getCurrentUser().getId()), "=");
            
            if (!CommUtil.null2String(order_status).equals("")) {
	            if (order_status.equals("order_submit")) {
	                qo.addQuery("obj.igo_status", new SysMap("igo_status", Integer.valueOf(0)),
	                    "=");
	            }
	            if (order_status.equals("order_receive")) {
	                qo.addQuery("obj.igo_status", new SysMap("igo_status", Integer.valueOf(40)),
	                    "=");
	            }
	            if (order_status.equals("order_shipping")) {
	                qo.addQuery("obj.igo_status", new SysMap("igo_status", Integer.valueOf(30)),
	                    "=");
	            }
	          
	            if (order_status.equals("order_cancel")) {
	                qo.addQuery("obj.igo_status", new SysMap("igo_status", Integer.valueOf(-1)),
	                    "=");
	            }
	        }
            mv.addObject("order_status", order_status);
            qo.setPageSize(4);
            IPageList pList = this.integralGoodsOrderService.list(qo);
            CommUtil.saveIPageList2ModelAndView(url + "/mobile/buyer/integral_order_list.htm", "",
                "", pList, mv);
        } else {
            mv = new JModelAndView("user/default/usercenter/h5/error.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0,
                request, response);
            mv.addObject("op_title", "系统未开启积分商城");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/index.htm");
        }
        return mv;
    }
    
    @SecurityMapping(title = "买家订单列表", value = "/mobile/buyer/integral_order_list.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/mobile/buyer/integral_order_list_ajax.htm" })
    public ModelAndView integral_order_list_ajax(HttpServletRequest request,
                                            HttpServletResponse response, String currentPage,String order_status) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/integral_order_list_ajax.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if (this.configService.getSysConfig().isIntegralStore()) {

            String url = CommUtil.getURL(request);
            if(SecurityUserHolder.getCurrentUser()!=null){            
            
            IntegralGoodsOrderQueryObject qo = new IntegralGoodsOrderQueryObject(currentPage, mv,
                "createtime", "desc");
            qo.addQuery("obj.igo_user.id", new SysMap("user_id", SecurityUserHolder
                .getCurrentUser().getId()), "=");
            
            if (!CommUtil.null2String(order_status).equals("")) {
	            if (order_status.equals("order_submit")) {
	                qo.addQuery("obj.igo_status", new SysMap("igo_status", Integer.valueOf(0)),
	                    "=");
	            }
	            if (order_status.equals("order_receive")) {
	                qo.addQuery("obj.igo_status", new SysMap("igo_status", Integer.valueOf(40)),
	                    "=");
	            }
	            if (order_status.equals("order_shipping")) {
	                qo.addQuery("obj.igo_status", new SysMap("igo_status", Integer.valueOf(30)),
	                    "=");
	            }
	          
	            if (order_status.equals("order_cancel")) {
	                qo.addQuery("obj.igo_status", new SysMap("igo_status", Integer.valueOf(-1)),
	                    "=");
	            }
	        }
            mv.addObject("order_status", order_status);
            qo.setPageSize(4);
            IPageList pList = this.integralGoodsOrderService.noLastList(qo);
            CommUtil.saveIPageList2ModelAndView(url + "/mobile/buyer/integral_order_list.htm", "",
                "", pList, mv);
            }
        }
        return mv;
    }

    @SecurityMapping(title = "取消订单", value = "/mobile/buyer/integral_order_cancel.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/mobile/buyer/integral_order_cancel.htm" })
    public ModelAndView integral_order_cancel(HttpServletRequest request,
                                              HttpServletResponse response, String id,
                                              String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        IntegralGoodsOrder obj = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(id));
        if (obj != null) {
            if (obj.getIgo_user().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {
                obj.setIgo_status(-1);
                this.integralGoodsOrderService.update(obj);
                for (IntegralGoodsCart igc : obj.getIgo_gcs()) {
                    IntegralGoods goods = igc.getGoods();
                    goods.setIg_goods_count(goods.getIg_goods_count() + igc.getCount());
                    this.integralGoodsService.update(goods);
                }
                User user = obj.getIgo_user();
                user.setIntegral(user.getIntegral() + obj.getIgo_total_integral());
                this.userService.update(user);
                IntegralLog log = new IntegralLog();
                log.setCreatetime(new Date());
                log.setContent("取消" + obj.getIgo_order_sn() + "积分兑换，返还积分");
                log.setIntegral(obj.getIgo_total_integral());
                log.setIntegral_user(obj.getIgo_user());
                log.setOperate_user(SecurityUserHolder.getCurrentUser());
                log.setType("integral_order");
                this.integralLogService.save(log);
                mv.addObject("op_title", "积分兑换取消成功");
                mv.addObject("url", CommUtil.getURL(request)
                                    + "/mobile/buyer/integral_order_list.htm");
                return mv;
            }
        }
        mv = new JModelAndView("user/default/usercenter/h5/error.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("op_title", "参数错误，无该订单");
        mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/integral_order_list.htm");
        return mv;
    }

    @SecurityMapping(title = "确认收货", value = "/mobile/buyer/integral_order_cofirm.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/mobile/buyer/integral_order_cofirm.htm" })
    public ModelAndView integral_order_cofirm(HttpServletRequest request,
                                              HttpServletResponse response, String id,
                                              String currentPage) {
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/h5/integral_order_cofirm.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        IntegralGoodsOrder obj = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(id));
        if (obj != null) {
            if (obj.getIgo_user().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {
                mv.addObject("obj", obj);
                return mv;
            }
        }
        mv = new JModelAndView("user/default/usercenter/h5/error.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("op_title", "参数错误，无该订单");
        mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/integral_order_list.htm");
        return mv;
    }

    @SecurityMapping(title = "积分订单详情", value = "/mobile/buyer/integral_order_view.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/mobile/buyer/integral_order_view.htm" })
    public ModelAndView integral_order_view(HttpServletRequest request,
                                            HttpServletResponse response, String id,
                                            String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/integral_order_view.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        IntegralGoodsOrder obj = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(id));
        if (obj != null) {
            if (obj.getIgo_user().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {
                mv.addObject("obj", obj);
                mv.addObject("currentPage", currentPage);
                return mv;
            }
        }
        mv = new JModelAndView("error.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        mv.addObject("op_title", "参数错误，无该订单");
        mv.addObject("url", CommUtil.getURL(request) + "/buyer/integral_order_list.htm");
        return mv;
    }

    @SecurityMapping(title = "确认收货保存", value = "/mobile/buyer/integral_order_cofirm_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/mobile/buyer/integral_order_cofirm_save.htm" })
    public ModelAndView integral_order_cofirm_save(HttpServletRequest request,
                                                   HttpServletResponse response, String id,
                                                   String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        IntegralGoodsOrder obj = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(id));
        if (obj != null) {
            if (obj.getIgo_user().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {
                obj.setIgo_status(40);
                this.integralGoodsOrderService.update(obj);
                mv.addObject("op_title", "确认收货成功");
                mv.addObject("url", CommUtil.getURL(request)
                                    + "/mobile/buyer/integral_order_list.htm");
                return mv;
            }
        }
        mv = new JModelAndView("user/default/usercenter/h5/error.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("op_title", "参数错误，无该订单");
        mv.addObject("url", CommUtil.getURL(request)
                            + "/mobile/buyer/integral_order_list.htm?currentPage=" + currentPage);
        return mv;
    }
}
