package com.javamalls.ctrl.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
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
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.query.GoodsQueryObject;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;

@Controller
public class ZtcSellerAction {
    @Autowired
    private ISysConfigService  configService;
    @Autowired
    private IUserConfigService userConfigService;
    @Autowired
    private IGoodsService      goodsService;
    @Autowired
    private IUserService       userService;

    @SecurityMapping(title = "竞价直通车申请", value = "/seller/ztc_apply.htm*", rtype = "seller", rname = "竞价直通车", rcode = "ztc_seller", rgroup = "促销管理")
    @RequestMapping({ "/seller/ztc_apply.htm" })
    public ModelAndView ztc_apply(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/ztc_apply.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if (!this.configService.getSysConfig().isZtc_status()) {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "系统未开启竞价直通车");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
        }
        String ztc_session = CommUtil.randomString(32);
        mv.addObject("ztc_session", ztc_session);
        request.getSession(false).setAttribute("ztc_session", ztc_session);
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        mv.addObject("user", user);
        return mv;
    }

    @SecurityMapping(title = "竞价直通车商品加载", value = "/seller/ztc_goods.htm*", rtype = "seller", rname = "竞价直通车", rcode = "ztc_seller", rgroup = "促销管理")
    @RequestMapping({ "/seller/ztc_goods.htm" })
    public void ztc_goods(HttpServletRequest request, HttpServletResponse response,
                          String goods_name) {
        Map params = new HashMap();
        params.put("goods_name", "%" + goods_name.trim() + "%");
        params.put("goods_status", Integer.valueOf(0));
        params.put("store_id", SecurityUserHolder.getCurrentUser().getStore().getId());
        params.put("ztc_status", Integer.valueOf(0));
        List<Goods> goods_list = this.goodsService
            .query(
                "select obj from Goods obj where obj.goods_name like :goods_name and obj.goods_store.id=:store_id and obj.ztc_status=:ztc_status and obj.goods_status=:goods_status  order by obj.createtime desc",
                params, -1, -1);
        List<Map> maps = new ArrayList();
        for (Goods goods : goods_list) {
            Map map = new HashMap();
            map.put("goods_name", goods.getGoods_name());
            map.put("goods_id", goods.getId());
            maps.add(map);
        }
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(Json.toJson(maps, JsonFormat.compact()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SecurityMapping(title = "竞价直通车申请保存", value = "/seller/ztc_apply_save.htm*", rtype = "seller", rname = "竞价直通车", rcode = "ztc_seller", rgroup = "促销管理")
    @RequestMapping({ "/seller/ztc_apply_save.htm" })
    public ModelAndView ztc_apply_save(HttpServletRequest request, HttpServletResponse response,
                                       String goods_id, String ztc_price, String ztc_begin_time,
                                       String ztc_gold, String ztc_session) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if (!this.configService.getSysConfig().isZtc_status()) {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "系统未开启竞价直通车");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
        } else {
            String ztc_session1 = CommUtil.null2String(request.getSession(false).getAttribute(
                "ztc_session"));
            if ((!ztc_session1.equals(""))
                && (ztc_session1.equals(CommUtil.null2String(ztc_session)))) {
                request.getSession(false).removeAttribute("ztc_session");
                Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
                goods.setZtc_status(1);
                goods.setZtc_pay_status(1);
                goods.setZtc_begin_time(CommUtil.formatDate(ztc_begin_time));
                goods.setZtc_gold(CommUtil.null2Int(ztc_gold));
                goods.setZtc_price(CommUtil.null2Int(ztc_price));
                goods.setZtc_apply_time(new Date());
                this.goodsService.update(goods);
                mv.addObject("op_title", "竞价直通车申请成功,等待审核");
                mv.addObject("url", CommUtil.getURL(request) + "/seller/ztc_list.htm");
            } else {
                mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "不允许重复提交申请");
                mv.addObject("url", CommUtil.getURL(request) + "/seller/ztc_apply.htm");
            }
        }
        return mv;
    }

    @SecurityMapping(title = "竞价直通车申请列表", value = "/seller/ztc_apply_list.htm*", rtype = "seller", rname = "竞价直通车", rcode = "ztc_seller", rgroup = "促销管理")
    @RequestMapping({ "/seller/ztc_apply_list.htm" })
    public ModelAndView ztc_apply_list(HttpServletRequest request, HttpServletResponse response,
                                       String currentPage, String goods_name) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/ztc_apply_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if (!this.configService.getSysConfig().isZtc_status()) {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "系统未开启竞价直通车");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
        } else {
            GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, "ztc_begin_time", "desc");
            qo.addQuery("obj.goods_store.id", new SysMap("store_id", SecurityUserHolder
                .getCurrentUser().getStore().getId()), "=");
            //qo.addQuery("obj.ztc_status", new SysMap("ztc_status", Integer.valueOf(1)), "=");
            if (!CommUtil.null2String(goods_name).equals("")) {
                qo.addQuery("obj.goods_name", new SysMap("goods_name", "%" + goods_name.trim()
                                                                       + "%"), "like");
            }
            qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");

            IPageList pList = this.goodsService.list(qo);
            CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
            mv.addObject("goods_name", goods_name);
        }
        return mv;
    }

    @SecurityMapping(title = "竞价直通车商品列表", value = "/seller/ztc_list.htm*", rtype = "seller", rname = "竞价直通车", rcode = "ztc_seller", rgroup = "促销管理")
    @RequestMapping({ "/seller/ztc_list.htm" })
    public ModelAndView ztc_list(HttpServletRequest request, HttpServletResponse response,
                                 String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/ztc_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if (!this.configService.getSysConfig().isZtc_status()) {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "系统未开启竞价直通车");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
        } else {
            GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, "ztc_apply_time", "desc");
            qo.addQuery("obj.goods_store.id", new SysMap("store_id", SecurityUserHolder
                .getCurrentUser().getStore().getId()), "=");
            qo.addQuery("obj.ztc_status", new SysMap("ztc_status", Integer.valueOf(2)), ">=");
            qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");

            IPageList pList = this.goodsService.list(qo);
            CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        }
        return mv;
    }

    @SecurityMapping(title = "竞价直通车申请查看", value = "/seller/ztc_apply_view.htm*", rtype = "seller", rname = "竞价直通车", rcode = "ztc_seller", rgroup = "促销管理")
    @RequestMapping({ "/seller/ztc_apply_view.htm" })
    public ModelAndView ztc_apply_view(HttpServletRequest request, HttpServletResponse response,
                                       String id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/ztc_apply_view.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if (!this.configService.getSysConfig().isZtc_status()) {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "系统未开启竞价直通车");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
        } else {
            Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
            if (obj.getGoods_store().getId()
                .equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
                mv.addObject("obj", obj);
            } else {
                mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "参数错误，不存在该竞价直通车信息");
                mv.addObject("url", CommUtil.getURL(request) + "/seller/ztc_list.htm");
            }
        }
        return mv;
    }
}
