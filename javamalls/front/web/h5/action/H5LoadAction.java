package com.javamalls.front.web.h5.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.domain.Accessory;
import com.javamalls.platform.domain.Area;
import com.javamalls.platform.domain.Consult;
import com.javamalls.platform.domain.Evaluate;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.GoodsBrand;
import com.javamalls.platform.domain.GoodsTypeProperty;
import com.javamalls.platform.domain.MobileGoods;
import com.javamalls.platform.domain.SysConfig;
import com.javamalls.platform.domain.query.ConsultQueryObject;
import com.javamalls.platform.domain.query.EvaluateQueryObject;
import com.javamalls.platform.domain.query.GoodsQueryObject;
import com.javamalls.platform.domain.query.MobileGoodsQueryObject;
import com.javamalls.platform.service.IAreaService;
import com.javamalls.platform.service.IConsultService;
import com.javamalls.platform.service.IEvaluateService;
import com.javamalls.platform.service.IGoodsBrandService;
import com.javamalls.platform.service.IGoodsClassService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IGoodsSpecPropertyService;
import com.javamalls.platform.service.IGoodsTypePropertyService;
import com.javamalls.platform.service.IMobileGoodsService;
import com.javamalls.platform.service.IStoreClassService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

/**根据父id加载地区   公用
 *                       
 * @Filename: LoadAction.java
 * @Version: 1.0
 * @Author: 刘杰
 * @Email: attay125@163.com
 *
 */
@Controller
public class H5LoadAction {
    @Autowired
    private IAreaService              areaService;
    @Autowired
    private IEvaluateService          evaluateService;
    @Autowired
    private ISysConfigService         configService;
    @Autowired
    private IUserConfigService        userConfigService;
    @Autowired
    private IConsultService           consultService;
    @Autowired
    private IGoodsService             goodsService;
    @Autowired
    private IGoodsClassService        goodsClassService;
    @Autowired
    private IGoodsBrandService        brandService;
    @Autowired
    private IGoodsSpecPropertyService goodsSpecPropertyService;
    @Autowired
    private IGoodsTypePropertyService goodsTypePropertyService;
    @Autowired
    private IStoreClassService        storeClassService;
    @Autowired
    private IMobileGoodsService       mobileGoodsService;

    @RequestMapping({ "/mobile/load_area.htm" })
    public void load_area(HttpServletRequest request, HttpServletResponse response, String pid) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pid", Long.valueOf(Long.parseLong(pid)));
        List<Area> areas = this.areaService.query(
            "select obj from Area obj where obj.parent.id=:pid", params, -1, -1);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Area area : areas) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", area.getId());
            map.put("areaName", area.getAreaName());
            list.add(map);
        }
        writeData(response, list);
    }

    /**商品评价
     * @param request
     * @param response
     * @param id
     * @param goods_id
     * @param currentPage
     * @return
     */
    @RequestMapping({ "/mobile/load_goods_evaluation.htm" })
    public void load_goods_evaluation(HttpServletRequest request, HttpServletResponse response,
                                      String id, String goods_id, String evaluate_buyer_val,
                                      String currentPage) {
        ModelAndView mv = new JModelAndView("default/h5/detail_comment.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);

        EvaluateQueryObject qo = new EvaluateQueryObject(currentPage, mv, "createtime", "desc");

        qo.addQuery("obj.evaluate_goods.id", new SysMap("goods_id", CommUtil.null2Long(goods_id)),
            "=");
        qo.addQuery("obj.evaluate_type", new SysMap("evaluate_type", "goods"), "=");
        qo.addQuery("obj.evaluate_status", new SysMap("evaluate_status", Integer.valueOf(0)), "=");
        if ("".equals(CommUtil.null2String(evaluate_buyer_val))) {
            evaluate_buyer_val = "1";
        }
        qo.addQuery("obj.evaluate_buyer_val",
            new SysMap("evaluate_buyer_val", Integer.valueOf(evaluate_buyer_val)), "=");
        qo.setPageSize(Integer.valueOf(10));
        IPageList objs = this.evaluateService.list(qo);
        List<Evaluate> evalList = objs.getResult();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Evaluate evaluate : evalList) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", evaluate.getId());
            map.put("evaluateInfo", evaluate.getEvaluate_info());
            map.put("userName", evaluate.getEvaluate_user().getUserName());
            map.put("createTime", CommUtil.formatShortDate(evaluate.getCreatetime()));
            map.put("currentPage", currentPage);
            list.add(map);
        }
        this.writeData(response, list);
    }

    /**根据商品加载商品咨询
     * @param request
     * @param response
     * @param id
     * @param goods_id
     * @param currentPage
     * @return
     */
    @RequestMapping({ "/mobile/load_goods_consult.htm" })
    public void load_goods_consult(HttpServletRequest request, HttpServletResponse response,
                                   String id, String goods_id, String currentPage) {
        ModelAndView mv = new JModelAndView("default/h5/detail_consulation.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        ConsultQueryObject qo = new ConsultQueryObject(currentPage, mv, "createtime", "desc");
        qo.addQuery("obj.goods.id", new SysMap("goods_id", CommUtil.null2Long(goods_id)), "=");
        qo.setPageSize(Integer.valueOf(10));
        IPageList<Consult> pList = this.consultService.list(qo);
        CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + "/mobile/goods_consult.htm",
            "", "", pList, mv);
        List<Consult> consults = pList.getResult();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        String userName = null;
        String replyTime = null;
        for (Consult consult : consults) {
            userName = "匿名";
            replyTime = "";
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", consult.getId());
            map.put("consult_content", consult.getConsult_content());
            if (!"".equals(CommUtil.null2String(consult.getReply_time())))
                replyTime = CommUtil.formatShortDate(consult.getReply_time());
            if (consult.getConsult_user() != null)
                userName = CommUtil.sensitiveInfo(consult.getConsult_user().getUserName(),
                    "userName");
            map.put("userName", userName);
            map.put("createTime", CommUtil.formatShortDate(consult.getCreatetime()));
            map.put("consult_reply", CommUtil.null2String(consult.getConsult_reply()));
            map.put("replyTime", replyTime);
            map.put("currentPage", currentPage);
            list.add(map);
        }
        this.writeData(response, list);
    }

    /**店铺商品列表
     * @param request
     * @param response
     * @param gc_id
     * @param currentPage
     * @param orderBy
     * @param orderType
     * @param store_price_begin
     * @param store_price_end
     * @param brand_ids
     * @param gs_ids
     * @param properties
     * @param op
     * @param goods_name
     * @param area_name
     * @param area_id
     * @param goods_view
     * @param all_property_status
     * @param detail_property_status
     * @return
     */
    @RequestMapping({ "/mobile/load_store_goods_list.htm" })
    public void store_goods_list(HttpServletRequest request, HttpServletResponse response,
                                 String gc_id, String currentPage, String orderBy,
                                 String orderType, String goods_current_price_begin,
                                 String goods_current_price_end, String brand_ids, String gs_ids,
                                 String properties, String op, String goods_name, String area_name,
                                 String area_id, String goods_view, String all_property_status,
                                 String detail_property_status, String store_id) {
        SysConfig config = this.configService.getSysConfig();
        ModelAndView mv = new JModelAndView("h5/product_list.html", config,
            this.userConfigService.getUserConfig(), 1, request, response);

        if ((orderBy == null) || (orderBy.equals(""))) {
            orderBy = "createtime";
        }
        if ((op != null) && (!op.equals(""))) {
            mv.addObject("op", op);
        }
        if (this.configService.getSysConfig().isZtc_status()) {
            orderBy = "ztc_dredge_price desc,obj." + orderBy;
        }
        GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
        if (!"".equals(CommUtil.null2String(store_id))) {
            gqo.addQuery("obj.goods_store.id",
                new SysMap("store_id", CommUtil.null2Long(store_id)), "=");
            mv.addObject("store_id", store_id);
        }
        if ((goods_current_price_begin != null) && (!goods_current_price_begin.equals(""))) {
            gqo.addQuery("obj.goods_current_price", new SysMap("goods_current_price_begin",
                BigDecimal.valueOf(CommUtil.null2Double(goods_current_price_begin))), ">=");
            mv.addObject("goods_current_price_begin", goods_current_price_begin);
        }
        if ((goods_current_price_end != null) && (!goods_current_price_end.equals(""))) {
            gqo.addQuery("obj.goods_current_price", new SysMap("goods_current_price_end",
                BigDecimal.valueOf(CommUtil.null2Double(goods_current_price_end))), "<=");
            mv.addObject("goods_current_price_end", goods_current_price_end);
        }
        if ((goods_name != null) && (!goods_name.equals(""))) {
            gqo.addQuery("obj.goods_name", new SysMap("name", "%" + goods_name.trim() + "%"),
                "like");
            mv.addObject("goods_name", goods_name);
        }
        if ((area_id != null) && (!area_id.equals(""))) {
            Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
            mv.addObject("area", area);
            Set<Long> area_ids = getAreaChildIds(area);
            Map<String, Object> p_area = new HashMap<String, Object>();
            p_area.put("area_ids", area_ids);
            gqo.addQuery("obj.goods_store.area.id in (:area_ids)", p_area);
        }
        if ((area_name != null) && (!area_name.equals(""))) {
            mv.addObject("area_name", area_name);
            Map<String, Object> like_area = new HashMap<String, Object>();
            like_area.put("area_name", area_name + "%");
            List<Area> likes_areas = this.areaService.query(
                "select obj from Area obj where obj.areaName like:area_name", like_area, -1, -1);
            Set<Long> like_area_ids = getArrayAreaChildIds(likes_areas);
            like_area.clear();
            like_area.put("like_area_ids", like_area_ids);
            gqo.addQuery("obj.goods_store.area.id in (:like_area_ids)", like_area);
        }
        gqo.addQuery("obj.goods_store.store_status",
            new SysMap("store_status", Integer.valueOf(2)), "=");
        gqo.setPageSize(Constent.GOODS_LIST_PAGE_SIZE);
        gqo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(0)), "=");
        List<Map<String, Object>> goods_property = new ArrayList<Map<String, Object>>();
        if (!CommUtil.null2String(brand_ids).equals("")) {
            String[] brand_id_list = brand_ids.substring(0).split("\\|");
            if (brand_id_list.length == 1) {
                String brand_id = brand_id_list[0];
                String[] brand_info_list = brand_id.split(",");
                gqo.addQuery("obj.goods_brand.id",
                    new SysMap("brand_id", CommUtil.null2Long(brand_info_list[0])), "=", "and");
                Map<String, Object> map = new HashMap<String, Object>();
                GoodsBrand brand = this.brandService.getObjById(CommUtil
                    .null2Long(brand_info_list[0]));
                map.put("name", "品牌");
                map.put("value", brand.getName());
                map.put("type", "brand");
                map.put("id", brand.getId());
                goods_property.add(map);
            } else {
                for (int i = 0; i < brand_id_list.length; i++) {
                    String brand_id = brand_id_list[i];
                    if (i == 0) {
                        String[] brand_info_list = brand_id.split(",");
                        gqo.addQuery("and (obj.goods_brand.id=" +

                        CommUtil.null2Long(brand_info_list[0]), null);
                        Map<String, Object> map = new HashMap<String, Object>();
                        GoodsBrand brand = this.brandService.getObjById(CommUtil
                            .null2Long(brand_info_list[0]));
                        map.put("name", "品牌");
                        map.put("value", brand.getName());
                        map.put("type", "brand");
                        map.put("id", brand.getId());
                        goods_property.add(map);
                    } else if (i == brand_id_list.length - 1) {
                        String[] brand_info_list = brand_id.split(",");
                        gqo.addQuery("or obj.goods_brand.id=" +

                        CommUtil.null2Long(brand_info_list[0]) + ")", null);
                        Map<String, Object> map = new HashMap<String, Object>();
                        GoodsBrand brand = this.brandService.getObjById(CommUtil
                            .null2Long(brand_info_list[0]));
                        map.put("name", "品牌");
                        map.put("value", brand.getName());
                        map.put("type", "brand");
                        map.put("id", brand.getId());
                        goods_property.add(map);
                    } else {
                        String[] brand_info_list = brand_id.split(",");
                        gqo.addQuery("or obj.goods_brand.id=" +

                        CommUtil.null2Long(brand_info_list[0]), null);
                        Map<String, Object> map = new HashMap<String, Object>();
                        GoodsBrand brand = this.brandService.getObjById(CommUtil
                            .null2Long(brand_info_list[0]));
                        map.put("name", "品牌");
                        map.put("value", brand.getName());
                        map.put("type", "brand");
                        map.put("id", brand.getId());
                        goods_property.add(map);
                    }
                }
            }
            mv.addObject("brand_ids", brand_ids);
        }
        if (!CommUtil.null2String(properties).equals("")) {
            String[] properties_list = properties.substring(1).split("\\|");
            for (int i = 0; i < properties_list.length; i++) {
                String property_info = properties_list[i];
                String[] property_info_list = property_info.split(",");
                GoodsTypeProperty gtp = this.goodsTypePropertyService.getObjById(CommUtil
                    .null2Long(property_info_list[0]));

                Map<String, Object> p_map = new HashMap<String, Object>();
                p_map.put("gtp_name" + i, "%" + gtp.getName().trim() + "%");
                p_map.put("gtp_value" + i, "%" + property_info_list[1].trim() + "%");
                gqo.addQuery("and (obj.goods_property like :gtp_name" + i
                             + " and obj.goods_property like :gtp_value" + i + ")", p_map);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("name", gtp.getName());
                map.put("value", property_info_list[1]);
                map.put("type", "properties");
                map.put("id", gtp.getId());
                goods_property.add(map);
            }
            mv.addObject("properties", properties);
        }
        gqo.addQuery("obj.disabled", new SysMap("disabled", false), "=");

        IPageList pList = this.goodsService.list(gqo);
        List<Goods> goodsList = pList.getResult();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        String imgPath = null;
        String goods_url = null;
        String activity = null;
        Accessory accessory = config.getGoodsImage();
        String webPath = mv.getModelMap().get("webPath").toString();
        String imageWebServer = mv.getModelMap().get("imageWebServer").toString();
        for (Goods goods : goodsList) {
            imgPath = imageWebServer + File.separator + accessory.getPath() + File.separator
                      + accessory.getName();
            if (goods.getGoods_main_photo() != null) {
                imgPath = imageWebServer + File.separator + goods.getGoods_main_photo().getPath()
                          + File.separator + goods.getGoods_main_photo().getName() + "_middle."
                          + goods.getGoods_main_photo().getExt();
            }

            goods_url = webPath + "/mobile/goods_" + goods.getId() + ".htm";
            if (config.isSecond_domain_open()) {
                goods_url = "http://" + goods.getGoods_store().getStore_second_domain() + "."
                            + CommUtil.generic_domain(request) + "/mobile/goods_" + goods.getId()
                            + ".htm";
            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", goods.getId());
            map.put("imgPath", imgPath);
            map.put("goods_url", goods_url);
            map.put("price", CommUtil.null2Double(goods.getGoods_current_price()));
            activity = "";
            if (goods.getGroup_buy() == 2)
                activity = "[团购]";
            map.put("group_buy", activity);
            activity = "";
            if (goods.getActivity_status() == 2)
                activity = "[活动]";
            map.put("activity_status", activity);
            activity = "";
            if (goods.getBargain_status() == 2)
                activity = "[特价]";
            map.put("bargain_status", activity);
            activity = "";
            if (goods.getDelivery_status() == 2)
                activity = "[买一送一]";
            map.put("delivery_status", activity);
            map.put("goods_name", CommUtil.substring(goods.getGoods_name(), 25));
            map.put("currentPage", currentPage);
            list.add(map);
        }
        this.writeData(response, list);
    }

    /**加载首页商品
     * @param request
     * @param response
     * @param currentPage
     * @param orderBy
     * @param orderType
     */
    @RequestMapping({ "/mobile/load_index_goods_list.htm" })
    public void load_index_goods_list(HttpServletRequest request, HttpServletResponse response,
                                      String currentPage, String orderBy, String orderType) {
        SysConfig config = this.configService.getSysConfig();
        ModelAndView mv = new JModelAndView("h5/index.html", config,
            this.userConfigService.getUserConfig(), 1, request, response);

        MobileGoodsQueryObject qo = new MobileGoodsQueryObject(currentPage, mv, "sort", "asc");
        qo.setPageSize(Constent.INDEX_GOODS_PAGE_SIZE);
        qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");

        IPageList<MobileGoods> pList = this.mobileGoodsService.list(qo);
        List<MobileGoods> mgList = pList.getResult();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        String imgPath = null;
        String goods_url = null;
        String activity = null;
        Accessory accessory = config.getGoodsImage();
        String webPath = mv.getModelMap().get("webPath").toString();
        String imageWebServer = mv.getModelMap().get("imageWebServer").toString();
        for (MobileGoods mg : mgList) {
            Goods goods = mg.getGoods();
            imgPath = imageWebServer + File.separator + accessory.getPath() + File.separator
                      + accessory.getName();
            if (goods.getGoods_main_photo() != null) {
                imgPath = imageWebServer + File.separator + goods.getGoods_main_photo().getPath()
                          + File.separator + goods.getGoods_main_photo().getName() + "_middle."
                          + goods.getGoods_main_photo().getExt();
            }

            goods_url = webPath + "/mobile/goods_" + goods.getId() + ".htm";
            if (config.isSecond_domain_open()) {
                goods_url = "http://" + goods.getGoods_store().getStore_second_domain() + "."
                            + CommUtil.generic_domain(request) + "/mobile/goods_" + goods.getId()
                            + ".htm";
            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", goods.getId());
            map.put("imgPath", imgPath);
            map.put("goods_url", goods_url);
            map.put("price", CommUtil.null2Double(goods.getGoods_current_price()));
            activity = "";
            if (goods.getGroup_buy() == 2)
                activity = "[团购]";
            map.put("group_buy", activity);
            activity = "";
            if (goods.getActivity_status() == 2)
                activity = "[活动]";
            map.put("activity_status", activity);
            activity = "";
            if (goods.getBargain_status() == 2)
                activity = "[特价]";
            map.put("bargain_status", activity);
            activity = "";
            if (goods.getDelivery_status() == 2)
                activity = "[买一送一]";
            map.put("delivery_status", activity);
            map.put("goods_name", CommUtil.substring(goods.getGoods_name(), 25));
            map.put("currentPage", currentPage);
            list.add(map);
        }
        this.writeData(response, list);
    }

    private Set<Long> getArrayAreaChildIds(List<Area> areas) {
        Set<Long> ids = new HashSet<Long>();
        for (Area area : areas) {
            ids.add(area.getId());
            for (Area are : area.getChilds()) {
                Set<Long> cids = getAreaChildIds(are);
                for (Long cid : cids) {
                    ids.add(cid);
                }
            }
        }
        return ids;
    }

    private Set<Long> getAreaChildIds(Area area) {
        Set<Long> ids = new HashSet<Long>();
        ids.add(area.getId());
        Iterator localIterator2;
        for (Area are : area.getChilds()) {
            Set<Long> cids = getAreaChildIds(are);
            for (Long cid : cids) {
                ids.add(cid);
            }
        }
        return ids;
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
}
