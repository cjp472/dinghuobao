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

import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.front.web.tools.GroupViewTools;
import com.javamalls.platform.domain.Accessory;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.GoodsBrand;
import com.javamalls.platform.domain.GoodsCart;
import com.javamalls.platform.domain.Group;
import com.javamalls.platform.domain.GroupArea;
import com.javamalls.platform.domain.GroupClass;
import com.javamalls.platform.domain.GroupGoods;
import com.javamalls.platform.domain.GroupPriceRange;
import com.javamalls.platform.domain.SysConfig;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.query.GoodsQueryObject;
import com.javamalls.platform.domain.query.GroupGoodsQueryObject;
import com.javamalls.platform.domain.query.GroupQueryObject;
import com.javamalls.platform.service.IGoodsBrandService;
import com.javamalls.platform.service.IGoodsCartService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IGroupAreaService;
import com.javamalls.platform.service.IGroupClassService;
import com.javamalls.platform.service.IGroupGoodsService;
import com.javamalls.platform.service.IGroupPriceRangeService;
import com.javamalls.platform.service.IGroupService;
import com.javamalls.platform.service.IOrderFormService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;

/**团购
 *                       
 * @Filename: GroupViewAction.java
 * @Version: 2.7.0
 * @Author: 王阳
 * @Email: wangyang@163.com
 *
 */
@Controller
public class H5GroupViewAction {
    @Autowired
    private ISysConfigService       configService;
    @Autowired
    private IUserConfigService      userConfigService;
    @Autowired
    private IGroupService           groupService;
    @Autowired
    private IGroupAreaService       groupAreaService;
    @Autowired
    private IGroupPriceRangeService groupPriceRangeService;
    @Autowired
    private IGroupClassService      groupClassService;
    @Autowired
    private IGroupGoodsService      groupGoodsService;
    @Autowired
    private IGoodsService           goodsService;
    @Autowired
    private IOrderFormService       orderFormService;
    @Autowired
    private IGoodsCartService       goodsCartService;
    @Autowired
    private IUserService            userService;
    @Autowired
    private GroupViewTools          groupViewTools;
    @Autowired
    private IGoodsBrandService      goodsBrandService;

    /**团购列表
     * @param request
     * @param response
     * @param currentPage
     * @param orderBy
     * @param orderType
     * @param gc_id
     * @param gpr_id
     * @param ga_id
     * @param gcb_id
     * @return
     */
    @RequestMapping({ "/mobile/group.htm" })
    public ModelAndView group(HttpServletRequest request, HttpServletResponse response,
                              String currentPage, String orderBy, String orderType, String gc_id,
                              String gpr_id, String ga_id, String gcb_id) {
        ModelAndView mv = new JModelAndView("h5/group_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("beginTime", new Date());
        params.put("endTime", new Date());
        List<Group> groups = this.groupService
            .query(
                "select obj from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime and obj.status=0",
                params, -1, -1);
        if (groups.size() > 0) {
            GroupGoodsQueryObject ggqo = new GroupGoodsQueryObject(currentPage, mv, orderBy,
                orderType);
            ggqo.setPageSize(Constent.GROUP_LIST_PAGE_SIZE);
            ggqo.addQuery("obj.group.id", new SysMap("group_id", ((Group) groups.get(0)).getId()),
                "=");

            if ((gc_id != null) && (!gc_id.equals(""))) {
                ggqo.addQuery("obj.gg_gc.id", new SysMap("gc_id", CommUtil.null2Long(gc_id)), "=");
            }

            if ((ga_id != null) && (!ga_id.equals(""))) {
                ggqo.addQuery("obj.gg_ga.id", new SysMap("ga_id", CommUtil.null2Long(ga_id)), "=");
                mv.addObject("ga_id", ga_id);
            }

            if ((gcb_id != null) && (!gcb_id.equals(""))) {
                //				ggqo.addQuery("obj.gg_goods.gc.goodsType.gbs.id", new SysMap(
                //						"gcb_id", CommUtil.null2Long(gcb_id)), "=");

                //根据品牌过滤
                ggqo.addQuery("obj.gg_goods.goods_brand.id",
                    new SysMap("gcb_id", CommUtil.null2Long(gcb_id)), "=");

                mv.addObject("gcb_id", gcb_id);
            }
            GroupPriceRange gpr = this.groupPriceRangeService
                .getObjById(CommUtil.null2Long(gpr_id));
            if (gpr != null) {
                ggqo.addQuery("obj.gg_price",
                    new SysMap("begin_price", BigDecimal.valueOf(gpr.getGpr_begin())), ">=");
                ggqo.addQuery("obj.gg_price",
                    new SysMap("end_price", BigDecimal.valueOf(gpr.getGpr_end())), "<=");
            }
            ggqo.addQuery("obj.gg_status", new SysMap("gg_status", Integer.valueOf(1)), "=");
            IPageList pList = this.groupGoodsService.list(ggqo);
            CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
            List<GroupClass> gcs = this.groupClassService
                .query(
                    "select obj from GroupClass obj where obj.parent.id is null order by obj.gc_sequence asc",
                    null, -1, -1);
            List<GroupPriceRange> gprs = this.groupPriceRangeService.query(
                "select obj from GroupPriceRange obj order by obj.gpr_begin asc", null, -1, -1);

            List<GroupArea> gas = this.groupAreaService
                .query(
                    "select obj from GroupArea obj where obj.parent.id is null order by obj.ga_sequence asc",
                    null, -1, -1);
            mv.addObject("gas", gas);

            mv.addObject("gprs", gprs);
            mv.addObject("gcs", gcs);
            mv.addObject("group", groups.get(0));
            if ((orderBy == null) || (orderBy.equals(""))) {
                orderBy = "createtime";
            }
            if ((orderType == null) || (orderType.equals(""))) {
                orderType = "desc";
            }
            mv.addObject("order_type",
                CommUtil.null2String(orderBy) + "_" + CommUtil.null2String(orderType));
            mv.addObject("gc_id", gc_id);
            mv.addObject("gpr_id", gpr_id);

            params.clear();
            params.put("audit", Integer.valueOf(1));
            List<GoodsBrand> brands = this.goodsBrandService.query(
                "select obj from GoodsBrand obj where obj.audit=:audit order by obj.sequence asc",
                params, -1, -1);
            mv.addObject("gcb", brands);
        }
        return mv;
    }

    @RequestMapping({ "/mobile/load_group_list.htm" })
    public void load_group_list(HttpServletRequest request, HttpServletResponse response,
                                String currentPage, String orderBy, String orderType, String gc_id,
                                String gpr_id, String ga_id, String gcb_id) {
        SysConfig config = this.configService.getSysConfig();
        ModelAndView mv = new JModelAndView("h5/group_list.html", config,
            this.userConfigService.getUserConfig(), 1, request, response);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("beginTime", new Date());
        params.put("endTime", new Date());
        List<Group> groups = this.groupService
            .query(
                "select obj from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime and obj.status=0",
                params, -1, -1);
        if (groups.size() > 0) {
            Group group = groups.get(0);
            GroupGoodsQueryObject ggqo = new GroupGoodsQueryObject(currentPage, mv, orderBy,
                orderType);
            ggqo.setPageSize(Constent.GROUP_LIST_PAGE_SIZE);
            ggqo.addQuery("obj.group.id", new SysMap("group_id", group.getId()), "=");

            if ((gc_id != null) && (!gc_id.equals(""))) {
                ggqo.addQuery("obj.gg_gc.id", new SysMap("gc_id", CommUtil.null2Long(gc_id)), "=");
            }

            if ((ga_id != null) && (!ga_id.equals(""))) {
                ggqo.addQuery("obj.gg_ga.id", new SysMap("ga_id", CommUtil.null2Long(ga_id)), "=");
                mv.addObject("ga_id", ga_id);
            }

            if ((gcb_id != null) && (!gcb_id.equals(""))) {
                //              ggqo.addQuery("obj.gg_goods.gc.goodsType.gbs.id", new SysMap(
                //                      "gcb_id", CommUtil.null2Long(gcb_id)), "=");

                //根据品牌过滤
                ggqo.addQuery("obj.gg_goods.goods_brand.id",
                    new SysMap("gcb_id", CommUtil.null2Long(gcb_id)), "=");

                mv.addObject("gcb_id", gcb_id);
            }
            GroupPriceRange gpr = this.groupPriceRangeService
                .getObjById(CommUtil.null2Long(gpr_id));
            if (gpr != null) {
                ggqo.addQuery("obj.gg_price",
                    new SysMap("begin_price", BigDecimal.valueOf(gpr.getGpr_begin())), ">=");
                ggqo.addQuery("obj.gg_price",
                    new SysMap("end_price", BigDecimal.valueOf(gpr.getGpr_end())), "<=");
            }
            ggqo.addQuery("obj.gg_status", new SysMap("gg_status", Integer.valueOf(1)), "=");
            IPageList pList = this.groupGoodsService.list(ggqo);
            List<GroupGoods> ggList = pList.getResult();

            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            String imgPath = null;
            String goods_url = null;
            Accessory accessory = config.getGoodsImage();
            String webPath = mv.getModelMap().get("webPath").toString();
            String imageWebServer = mv.getModelMap().get("imageWebServer").toString();
            for (GroupGoods mg : ggList) {

                Goods goods = mg.getGg_goods();

                imgPath = imageWebServer + File.separator + accessory.getPath() + File.separator
                          + accessory.getName();
                if (goods.getGoods_main_photo() != null) {
                    imgPath = imageWebServer + File.separator
                              + goods.getGoods_main_photo().getPath() + File.separator
                              + goods.getGoods_main_photo().getName() + "_middle."
                              + goods.getGoods_main_photo().getExt();
                }

                goods_url = webPath + "/mobile/group_info_view_" + mg.getId() + ".htm";
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", mg.getId());
                map.put("imgPath", imgPath);
                map.put("group_url", goods_url);
                map.put("gg_price", CommUtil.null2Double(mg.getGg_price()));
                map.put("gg_rebate", CommUtil.null2Double(mg.getGg_rebate()));
                map.put("buyer_count",
                    CommUtil.null2Double((mg.getGg_def_count() + mg.getGg_vir_count())));
                map.put("goods_name", CommUtil.substring(goods.getGoods_name(), 25));
                map.put("currentPage", currentPage);
                list.add(map);
            }
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

    }

    @RequestMapping({ "/mobile/group_info_view.htm" })
    public ModelAndView group_view(HttpServletRequest request, HttpServletResponse response,
                                   String id) {
        ModelAndView mv = new JModelAndView("h5/group_view.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        GroupGoods obj = this.groupGoodsService.getObjById(CommUtil.null2Long(id));
        User user = SecurityUserHolder.getCurrentUser();
        boolean view = false;
        if ((obj.getGroup().getBeginTime().before(new Date()))
            && (obj.getGroup().getEndTime().after(new Date()))) {
            view = true;
        }
        if ((user != null) && (user.getUserRole().indexOf("ADMIN") >= 0)) {
            view = true;
        }
        if (view) {
            mv.addObject("obj", obj);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("beginTime", new Date());
            params.put("endTime", new Date());
            params.put("status", Integer.valueOf(0));
            List<Group> groups = this.groupService
                .query(
                    "select obj from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime and obj.status=:status",
                    params, -1, -1);
            if (groups.size() > 0) {
                GroupGoodsQueryObject ggqo = new GroupGoodsQueryObject("1", mv, "gg_recommend",
                    "desc");
                ggqo.addQuery("obj.gg_status", new SysMap("gg_status", Integer.valueOf(1)), "=");
                ggqo.addQuery("obj.group.id", new SysMap("group_id", obj.getGroup().getId()), "=");
                ggqo.addQuery("obj.id", new SysMap("goods_id", obj.getId()), "!=");
                ggqo.setPageSize(Integer.valueOf(4));
                IPageList pList = this.groupGoodsService.list(ggqo);
                mv.addObject("hot_ggs", pList.getResult());
                mv.addObject("group", groups.get(0));
            }
            GoodsQueryObject gqo = new GoodsQueryObject("1", mv, "createtime", "desc");
            gqo.addQuery("obj.goods_store.id", new SysMap("store_id", obj.getGg_goods()
                .getGoods_store().getId()), "=");
            gqo.addQuery("obj.goods_recommend",
                new SysMap("goods_recommend", Boolean.valueOf(true)), "=");
            gqo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(0)), "=");
            gqo.setPageSize(Integer.valueOf(2));
            gqo.addQuery("obj.disabled", new SysMap("disabled", false), "=");

            mv.addObject("recommend_goods", this.goodsService.list(gqo).getResult());
            params.clear();
            params.put("gg", obj);
            List<GoodsCart> gc_list = this.goodsCartService.query(
                "select obj from GoodsCart obj where :gg member of obj.goods.group_goods_list",
                params, 0, 4);
            List<GoodsCart> gcs = new ArrayList<GoodsCart>();
            for (GoodsCart gc : gc_list) {
                if (!gcs.contains(gc)) {
                    gcs.add(gc);
                }
            }
            mv.addObject("gcs", gcs);
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "团购商品参数错误");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
        }
        return mv;
    }

    @RequestMapping({ "/mobile/group_head.htm" })
    public ModelAndView group_head(HttpServletRequest request, HttpServletResponse response,
                                   String ga_id) {
        ModelAndView mv = new JModelAndView("group_head.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        List<GroupArea> gas = this.groupAreaService
            .query(
                "select obj from GroupArea obj where obj.parent.id is null order by obj.ga_sequence asc",
                null, -1, -1);
        mv.addObject("gas", gas);
        if ((ga_id != null) && (!ga_id.equals(""))) {
            mv.addObject("ga", this.groupAreaService.getObjById(CommUtil.null2Long(ga_id))
                .getGa_name());
        } else {
            mv.addObject("ga", "全国");
        }
        return mv;
    }

    @RequestMapping({ "/mobile/group_list.htm" })
    public ModelAndView group_list(HttpServletRequest request, HttpServletResponse response,
                                   String currentPage, String time, Long groupid) {
        ModelAndView mv = new JModelAndView("group_list.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        GroupQueryObject gqo = new GroupQueryObject(currentPage, mv, "createtime", "desc");
        if (time.equals("soon")) {
            gqo.addQuery("obj.beginTime", new SysMap("beginTime", new Date()), ">");
        }

        if (time.equals("history")) {
            gqo.addQuery("obj.endTime", new SysMap("endTime", new Date()), "<");
        }
        IPageList pList = this.groupService.list(gqo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        mv.addObject("time", time);
        mv.addObject("groupid", groupid);
        mv.addObject("groupViewTools", this.groupViewTools);
        return mv;
    }
}
