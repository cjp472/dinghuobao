package com.javamalls.ctrl.h5.buyer.action;

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
import com.javamalls.platform.domain.query.CouponInfoQueryObject;
import com.javamalls.platform.service.ICouponInfoService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

@Controller
public class H5CouponBuyerAction {
    @Autowired
    private ISysConfigService  configService;
    @Autowired
    private IUserConfigService userConfigService;
    @Autowired
    private ICouponInfoService couponInfoService;

    @SecurityMapping(title = "买家优惠券列表", value = " *", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/coupon.htm" })
    public ModelAndView coupon(HttpServletRequest request, HttpServletResponse response,
                               String reply, String currentPage,@PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/buyer_coupon.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if(SecurityUserHolder.getCurrentUser()==null){
        	mv = new JModelAndView("h5/login.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
        	mv.addObject("storeId", storeId);
        	return mv;
        }
        
        CouponInfoQueryObject qo = new CouponInfoQueryObject(currentPage, mv, "createtime", "desc");
        qo.setPageSize(8);
        qo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser()
            .getId()), "=");
        IPageList pList = this.couponInfoService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        return mv;
    }
    
    @SecurityMapping(title = "买家优惠券列表", value = " *", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/mobile/buyer/coupon_ajax.htm" })
    public ModelAndView coupon_ajax(HttpServletRequest request, HttpServletResponse response,
                               String reply, String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/buyer_coupon_ajax.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        CouponInfoQueryObject qo = new CouponInfoQueryObject(currentPage, mv, "createtime", "desc");
        qo.setPageSize(8);
        qo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser()
            .getId()), "=");
        IPageList pList = this.couponInfoService.nolastlist(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        return mv;
    }
}
