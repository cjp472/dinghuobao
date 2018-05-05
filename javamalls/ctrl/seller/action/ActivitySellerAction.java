package com.javamalls.ctrl.seller.action;

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
import com.javamalls.platform.domain.Activity;
import com.javamalls.platform.domain.ActivityGoods;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.query.ActivityGoodsQueryObject;
import com.javamalls.platform.domain.query.ActivityQueryObject;
import com.javamalls.platform.service.IActivityGoodsService;
import com.javamalls.platform.service.IActivityService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

/**活动管理
 *                       
 * @Filename: ActivitySellerAction.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class ActivitySellerAction {
    @Autowired
    private ISysConfigService     configService;
    @Autowired
    private IUserConfigService    userConfigService;
    @Autowired
    private IActivityService      activityService;
    @Autowired
    private IActivityGoodsService activityGoodsService;
    @Autowired
    private IGoodsService         goodsService;

    @SecurityMapping(title = "活动列表", value = "/seller/activity.htm*", rtype = "seller", rname = "活动管理", rcode = "activity_seller", rgroup = "促销管理")
    @RequestMapping({ "/seller/activity.htm" })
    public ModelAndView activity(HttpServletRequest request, HttpServletResponse response,
                                 String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/activity.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        ActivityQueryObject qo = new ActivityQueryObject(currentPage, mv, "createtime", "desc");
        qo.addQuery("obj.ac_status", new SysMap("ac_status", Integer.valueOf(1)), "=");
        qo.addQuery("obj.ac_begin_time", new SysMap("ac_begin_time", new Date()), "<=");
        qo.addQuery("obj.ac_end_time", new SysMap("ac_end_time", new Date()), ">=");
        IPageList pList = this.activityService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        return mv;
    }

    @SecurityMapping(title = "申请参加活动", value = "/seller/activity_apply.htm*", rtype = "seller", rname = "活动管理", rcode = "activity_seller", rgroup = "促销管理")
    @RequestMapping({ "/seller/activity_apply.htm" })
    public ModelAndView activity_apply(HttpServletRequest request, HttpServletResponse response,
                                       String id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/activity_apply.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Activity act = this.activityService.getObjById(CommUtil.null2Long(id));
        mv.addObject("act", act);
        String activity_session = CommUtil.randomString(32);
        mv.addObject("activity_session", activity_session);
        request.getSession(false).setAttribute("activity_session", activity_session);
        return mv;
    }

    @SecurityMapping(title = "活动商品加载", value = "/seller/activity_goods.htm*", rtype = "seller", rname = "活动管理", rcode = "activity_seller", rgroup = "促销管理")
    @RequestMapping({ "/seller/activity_goods.htm" })
    public void activity_goods(HttpServletRequest request, HttpServletResponse response,
                               String goods_name) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("goods_name", "%" + goods_name.trim() + "%");
        params.put("goods_status", Integer.valueOf(0));
        params.put("store_id", SecurityUserHolder.getCurrentUser().getStore().getId());
        params.put("group_buy", Integer.valueOf(0));
        params.put("activity_status", Integer.valueOf(0));
        params.put("delivery_status", Integer.valueOf(0));
        params.put("combin_status", Integer.valueOf(0));
        List<Goods> goods_list = this.goodsService
            .query(
                "select obj from Goods obj where obj.goods_name like :goods_name and obj.goods_status=:goods_status and obj.goods_store.id=:store_id and obj.group_buy =:group_buy and obj.activity_status =:activity_status and obj.delivery_status=:delivery_status and obj.combin_status=:combin_status order by obj.createtime desc",
                params, -1, -1);
        List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
        for (Goods goods : goods_list) {
            Map<String, Object> map = new HashMap<String, Object>();
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

    @SecurityMapping(title = "申请参加活动", value = "/seller/activity_apply_save.htm*", rtype = "seller", rname = "活动管理", rcode = "activity_seller", rgroup = "促销管理")
    @RequestMapping({ "/seller/activity_apply_save.htm" })
    public ModelAndView activity_apply_save(HttpServletRequest request,
                                            HttpServletResponse response, String goods_ids,
                                            String act_id, String activity_session) {
        ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        if ((goods_ids != null) && (!goods_ids.equals(""))) {
            String activity_session1 = CommUtil.null2String(request.getSession(false).getAttribute(
                "activity_session"));
            if ((!activity_session1.equals("")) && (activity_session1.equals(activity_session))) {
                request.getSession(false).removeAttribute("activity_session");
                Activity act = this.activityService.getObjById(CommUtil.null2Long(act_id));

                BigDecimal num = BigDecimal.valueOf(0.1D);
                String[] ids = goods_ids.split(",");
                for (String id : ids) {
                    if (!id.equals("")) {
                        ActivityGoods ag = new ActivityGoods();
                        ag.setCreatetime(new Date());
                        Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
                        ag.setAg_goods(goods);
                        goods.setActivity_status(1);
                        this.goodsService.update(goods);

                        //自营无需审核
                        if (goods.getGoods_store() != null && goods.getGoods_store().isPlatform()) {
                            ag.setAg_status(1);
                        } else {
                            ag.setAg_status(0);
                        }

                        ag.setAct(act);

                        ag.setAg_price(num.multiply(act.getAc_rebate()).multiply(
                            goods.getStore_price()));
                        this.activityGoodsService.save(ag);
                    }
                }
                mv.addObject("op_title", "申请参加活动成功");
                mv.addObject("url", CommUtil.getURL(request) + "/seller/activity_goods_list.htm");
            } else {
                mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "禁止重复提交活动申请");
                mv.addObject("url", CommUtil.getURL(request) + "/seller/activity.htm");
            }
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "至少选择一件商品");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/activity.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "活动商品列表", value = "/seller/activity_goods_list.htm*", rtype = "seller", rname = "活动管理", rcode = "activity_seller", rgroup = "促销管理")
    @RequestMapping({ "/seller/activity_goods_list.htm" })
    public ModelAndView activity_goods_list(HttpServletRequest request,
                                            HttpServletResponse response, String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/activity_goods_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        ActivityGoodsQueryObject qo = new ActivityGoodsQueryObject(currentPage, mv, "createtime",
            "desc");
        qo.addQuery("obj.ag_goods.goods_store.id", new SysMap("store_id", SecurityUserHolder
            .getCurrentUser().getStore().getId()), "=");
        qo.setPageSize(Integer.valueOf(30));
        IPageList pList = this.activityGoodsService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        return mv;
    }
}
