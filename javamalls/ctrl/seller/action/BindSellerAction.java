package com.javamalls.ctrl.seller.action;

import java.math.BigDecimal;

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
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.query.GoodsQueryObject;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;

/**捆绑销售管理
 *                       
 * @Filename: BindSellerAction.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class BindSellerAction {
    @Autowired
    private ISysConfigService  configService;
    @Autowired
    private IUserConfigService userConfigService;
    @Autowired
    private IUserService       userService;
    @Autowired
    private IStoreService      storeService;
    @Autowired
    private IGoodsService      goodsService;

    @SecurityMapping(title = "捆绑销售", value = "/seller/bind.htm*", rtype = "seller", rname = "捆绑销售", rcode = "bind_seller", rgroup = "促销管理")
    @RequestMapping({ "/seller/bind.htm" })
    public ModelAndView bind(HttpServletRequest request, HttpServletResponse response,
                             String currentPage, String orderBy, String orderType) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/bind.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
        qo.addQuery("obj.goods_store.id", new SysMap("store_id", user.getStore().getId()), "=");
        qo.addQuery("obj.bind_status", new SysMap("bind_status", Integer.valueOf(0)), ">");
        qo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(0)), "=");
        qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");

        IPageList pList = this.goodsService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        return mv;
    }

    @SecurityMapping(title = "申请捆绑销售", value = "/seller/bind_apply.htm*", rtype = "seller", rname = "捆绑销售", rcode = "bind_seller", rgroup = "促销管理")
    @RequestMapping({ "/seller/bind_apply.htm" })
    public ModelAndView bind_apply(HttpServletRequest request, HttpServletResponse response,
                                   String id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/bind_apply.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String bind_session = CommUtil.randomString(32);
        mv.addObject("bind_session", bind_session);
        request.getSession(false).setAttribute("bind_session", bind_session);
        return mv;
    }

    @SecurityMapping(title = "销售捆绑编辑", value = "/seller/bind_edit.htm*", rtype = "seller", rname = "捆绑销售", rcode = "bind_seller", rgroup = "促销管理")
    @RequestMapping({ "/seller/bind_edit.htm" })
    public ModelAndView bind_edit(HttpServletRequest request, HttpServletResponse response,
                                  String id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/bind_apply.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String bind_session = CommUtil.randomString(32);
        mv.addObject("bind_session", bind_session);
        request.getSession(false).setAttribute("bind_session", bind_session);
        Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
        mv.addObject("obj", obj);
        return mv;
    }

    @SecurityMapping(title = "捆绑销售保存", value = "/seller/bind_save.htm*", rtype = "seller", rname = "捆绑销售", rcode = "bind_seller", rgroup = "促销管理")
    @RequestMapping({ "/seller/bind_save.htm" })
    public ModelAndView bind_save(HttpServletRequest request, HttpServletResponse response,
                                  String currentPage, String orderBy, String orderType,
                                  String bind_session, String bind_begin_time,
                                  String bind_end_time, String bind_main_goods_id,
                                  String bind_goods_ids, String bind_price, String id) {
        ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        String bind_session1 = CommUtil.null2String(request.getSession(false).getAttribute(
            "bind_session"));
        if (bind_session1.equals("")) {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "捆绑销售保存失败");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/bind.htm");
            return mv;
        }
        if (bind_session1.equals(bind_session)) {
            request.getSession(false).removeAttribute("bind_session");
            Goods goods = this.goodsService.getObjById(CommUtil.null2Long(bind_main_goods_id));
            for (String goods_id : bind_goods_ids.split(",")) {
                Goods bind_goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
                goods.getBind_goods().add(bind_goods);
                goods.setBind_status(2);
            }
            this.goodsService.update(goods);

            if ((id != null) && (!id.equals("")) && (!goods.getId().equals(CommUtil.null2Long(id)))) {
                goods = this.goodsService.getObjById(CommUtil.null2Long(id));
                goods.setBind_status(0);
                goods.getBind_goods().clear();
                this.goodsService.update(goods);
            }
        }
        mv.addObject("op_title", "捆绑销售保存成功");
        mv.addObject("url", CommUtil.getURL(request) + "/seller/bind.htm?currentPage="
                            + currentPage);
        return mv;
    }

    @SecurityMapping(title = "捆绑销售删除", value = "/seller/bind_del.htm*", rtype = "seller", rname = "捆绑销售", rcode = "bind_seller", rgroup = "促销管理")
    @RequestMapping({ "/seller/bind_del.htm" })
    public String delivery_del(HttpServletRequest request, HttpServletResponse response,
                               String currentPage, String mulitId) {
        for (String id : mulitId.split(",")) {
            if (!CommUtil.null2String(id).equals("")) {
                Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
                User user = this.userService
                    .getObjById(SecurityUserHolder.getCurrentUser().getId());
                goods.setBind_status(0);
                goods.getBind_goods().clear();
                this.goodsService.update(goods);
            }
        }
        return "redirect:bind.htm?currentPage=" + currentPage;
    }

    @SecurityMapping(title = "捆绑销售套餐购买", value = "/seller/bind_buy.htm*", rtype = "seller", rname = "捆绑销售", rcode = "bind_seller", rgroup = "促销管理")
    @RequestMapping({ "/seller/bind_buy.htm" })
    public ModelAndView bind_buy(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/bind_buy.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        mv.addObject("user", user);
        return mv;
    }

    @SecurityMapping(title = "加载商品", value = "/seller/bind_goods.htm*", rtype = "seller", rname = "捆绑销售", rcode = "bind_seller", rgroup = "促销管理")
    @RequestMapping({ "/seller/bind_goods.htm" })
    public ModelAndView bind_goods(HttpServletRequest request, HttpServletResponse response,
                                   String goods_name, String currentPage, String target_id,
                                   String goods_type) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/bind_goods.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if (target_id.equals("main_goods_list")) {
            goods_type = "main";
        }

        GoodsQueryObject qo = new GoodsQueryObject();

        if (CommUtil.null2String(goods_type).equals("main")) {
            mv = new JModelAndView("user/default/usercenter/bind_main_goods.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0,
                request, response);

            User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
            Store store = user.getStore();

            qo.setCurrentPage(Integer.valueOf(CommUtil.null2Int(currentPage)));
            if (!CommUtil.null2String(goods_name).equals("")) {
                qo.addQuery("obj.goods_name",
                    new SysMap("goods_name", "%" + CommUtil.null2String(goods_name) + "%"), "like");
            }
            qo.addQuery("obj.bind_status", new SysMap("bind_status", Integer.valueOf(0)), "=");
            qo.addQuery("obj.goods_store.id", new SysMap("store_id", store.getId()), "=");
            qo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(0)), "=");
            qo.addQuery("obj.group_buy", new SysMap("group_buy", Integer.valueOf(0)), "=");
            qo.addQuery("obj.activity_status", new SysMap("activity_status", Integer.valueOf(0)),
                "=");
            qo.addQuery("obj.delivery_status", new SysMap("delivery_status", Integer.valueOf(0)),
                "=");
        } else {
            qo.setCurrentPage(Integer.valueOf(CommUtil.null2Int(currentPage)));
            if (!CommUtil.null2String(goods_name).equals("")) {
                qo.addQuery("obj.goods_name",
                    new SysMap("goods_name", "%" + CommUtil.null2String(goods_name) + "%"), "like");
            }
            qo.addQuery("obj.binddiscount", new SysMap("binddiscount", BigDecimal.valueOf(0)), ">");
            qo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(0)), "=");
            qo.addQuery("obj.group_buy", new SysMap("group_buy", Integer.valueOf(0)), "=");
            qo.addQuery("obj.activity_status", new SysMap("activity_status", Integer.valueOf(0)),
                "=");
            qo.addQuery("obj.delivery_status", new SysMap("delivery_status", Integer.valueOf(0)),
                "=");
        }
        qo.setPageSize(Integer.valueOf(15));
        qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");

        IPageList pList = this.goodsService.list(qo);
        CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + "/seller/bind_goods.htm",
            "", "&goods_name=" + goods_name, pList, mv);
        mv.addObject("target_id", target_id);
        return mv;
    }
}
