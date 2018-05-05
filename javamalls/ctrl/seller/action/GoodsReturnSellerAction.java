package com.javamalls.ctrl.seller.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.javamalls.platform.domain.GoodsReturn;
import com.javamalls.platform.domain.query.GoodsReturnQueryObject;
import com.javamalls.platform.service.IGoodsReturnItemService;
import com.javamalls.platform.service.IGoodsReturnService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

@Controller
public class GoodsReturnSellerAction {
    @Autowired
    private ISysConfigService       configService;
    @Autowired
    private IUserConfigService      userConfigService;
    @Autowired
    private IGoodsReturnService     goodsReturnService;
    @Autowired
    private IGoodsReturnItemService goodsReturnItemService;

    @SecurityMapping(title = "卖家退货列表", value = "/seller/goods_return.htm*", rtype = "seller", rname = "退货管理", rcode = "goods_return_seller", rgroup = "客户服务")
    @RequestMapping({ "/seller/goods_return.htm" })
    public ModelAndView refund(HttpServletRequest request, HttpServletResponse response, String id,
                               String currentPage, String data_type, String data, String beginTime,
                               String endTime) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/goods_return.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        GoodsReturnQueryObject qo = new GoodsReturnQueryObject(currentPage, mv, "createtime",
            "desc");
        qo.setPageSize(Integer.valueOf(30));
        if (!CommUtil.null2String(data).equals("")) {
            if (CommUtil.null2String(data_type).equals("order_id")) {
                qo.addQuery("obj.of.order_id", new SysMap("order_id", data), "=");
            }
            if (CommUtil.null2String(data_type).equals("buyer_name")) {
                qo.addQuery("obj.of.user.userName", new SysMap("userName", data), "=");
            }
        }
        if (!CommUtil.null2String(beginTime).equals("")) {
            qo.addQuery("obj.createtime", new SysMap("beginTime", CommUtil.formatMaxDate(beginTime+" 00:00:00")),
                ">=");
        }
        if (!CommUtil.null2String(endTime).equals("")) {
            qo.addQuery("obj.createtime", new SysMap("endTime", CommUtil.formatMaxDate(endTime+" 23:59:58")), "<=");
        }
        qo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser()
            .getId()), "=");
        IPageList pList = this.goodsReturnService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        mv.addObject("data_type", data_type);
        mv.addObject("data", data);
        mv.addObject("beginTime", beginTime);
        mv.addObject("endTime", endTime);
        return mv;
    }

    @SecurityMapping(title = "卖家退款列表", value = "/seller/return_view.htm*", rtype = "seller", rname = "退货管理", rcode = "goods_return_seller", rgroup = "客户服务")
    @RequestMapping({ "/seller/return_view.htm" })
    public ModelAndView return_view(HttpServletRequest request, HttpServletResponse response,
                                    String id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/return_view.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        GoodsReturn obj = this.goodsReturnService.getObjById(CommUtil.null2Long(id));
        mv.addObject("obj", obj);
        return mv;
    }
}
