package com.javamalls.front.web.h5.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.service.IQueryService;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.WebForm;
import com.javamalls.ctrl.admin.tools.UserTools;
import com.javamalls.ctrl.seller.Tools.TransportTools;
import com.javamalls.front.web.tools.AreaViewTools;
import com.javamalls.front.web.tools.GoodsViewTools;
import com.javamalls.front.web.tools.StoreViewTools;
import com.javamalls.platform.domain.Address;
import com.javamalls.platform.domain.Area;
import com.javamalls.platform.domain.Consult;
import com.javamalls.platform.domain.Evaluate;
import com.javamalls.platform.domain.Favorite;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.GoodsBrand;
import com.javamalls.platform.domain.GoodsClass;
import com.javamalls.platform.domain.GoodsItem;
import com.javamalls.platform.domain.GoodsSpecProperty;
import com.javamalls.platform.domain.GoodsStepPrice;
import com.javamalls.platform.domain.GoodsTypeProperty;
import com.javamalls.platform.domain.Group;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.StoreClass;
import com.javamalls.platform.domain.Strategy;
import com.javamalls.platform.domain.StrategyGoodsItem;
import com.javamalls.platform.domain.Track;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.UserGoodsClass;
import com.javamalls.platform.domain.query.ConsultQueryObject;
import com.javamalls.platform.domain.query.EvaluateQueryObject;
import com.javamalls.platform.domain.query.GoodsCartQueryObject;
import com.javamalls.platform.domain.query.GoodsQueryObject;
import com.javamalls.platform.service.IAddressService;
import com.javamalls.platform.service.IAreaService;
import com.javamalls.platform.service.IConsultService;
import com.javamalls.platform.service.IEvaluateService;
import com.javamalls.platform.service.IFavoriteService;
import com.javamalls.platform.service.IGoodsBrandService;
import com.javamalls.platform.service.IGoodsCartService;
import com.javamalls.platform.service.IGoodsClassService;
import com.javamalls.platform.service.IGoodsItemService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IGoodsSpecPropertyService;
import com.javamalls.platform.service.IGoodsStepPriceService;
import com.javamalls.platform.service.IGoodsTypePropertyService;
import com.javamalls.platform.service.IOrderFormService;
import com.javamalls.platform.service.IStoreClassService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.IStrategyGoodsItemService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.ITrackService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserGoodsClassService;
import com.javamalls.platform.service.IUserService;
import com.javamalls.platform.service.IUserStoreRelationService;

/**商品列表、详情、咨询等相关操作
 *                       
 * @Filename: GoodsViewAction.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class H5GoodsViewAction {

    @Autowired
    private ISysConfigService         configService;
    @Autowired
    private IUserConfigService        userConfigService;
    @Autowired
    private IGoodsService             goodsService;
    @Autowired
    private IGoodsClassService        goodsClassService;
    @Autowired
    private IUserGoodsClassService    userGoodsClassService;
    @Autowired
    private IStoreService             storeService;
    @Autowired
    private IEvaluateService          evaluateService;
    @Autowired
    private IGoodsCartService         goodsCartService;
    @Autowired
    private IConsultService           consultService;
    @Autowired
    private IGoodsBrandService        brandService;
    @Autowired
    private IGoodsSpecPropertyService goodsSpecPropertyService;
    @Autowired
    private IGoodsTypePropertyService goodsTypePropertyService;
    @Autowired
    private IAreaService              areaService;
    @Autowired
    private IStoreClassService        storeClassService;
    @Autowired
    private AreaViewTools             areaViewTools;
    @Autowired
    private GoodsViewTools            goodsViewTools;
    @Autowired
    private StoreViewTools            storeViewTools;
    @Autowired
    private TransportTools            transportTools;
    @Autowired
    private ITrackService             trackServ;
    @Autowired
    private IQueryService             gueryService;
    @Autowired
    private IAddressService           addressService;
    @Autowired
    private IGoodsItemService goodsItemService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IFavoriteService favoriteService;
    @Autowired
    private IStrategyGoodsItemService strategyGoodsItemService;
    @Autowired
    private IGoodsStepPriceService goodsStepPriceService;
    @Autowired
    private IUserStoreRelationService	userStoreRelationService;
    
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
    @RequestMapping({ "/store/{store_id}.htm/mobile/store_goods_list.htm" })
    public ModelAndView store_goods_list(HttpServletRequest request, HttpServletResponse response,
                                         String gc_id, String currentPage, String orderBy,
                                         String orderType, String goods_current_price_begin,
                                         String goods_current_price_end, String brand_ids,
                                         String gs_ids, String properties, String op,
                                         String goods_name, String area_name, String area_id,
                                         String goods_view, String all_property_status,
                                         String detail_property_status,@PathVariable String store_id) {
    
    	ModelAndView mv = new JModelAndView("h5/class_product_list.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
                response);
        if ((orderBy == null) || (orderBy.equals(""))) {
            orderBy = "createtime";
        }
        if ((op != null) && (!op.equals(""))) {
            mv.addObject("op", op);
        }
        String orderBy1 = orderBy;
      
        GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
        UserGoodsClass ugc = this.userGoodsClassService.getObjById(CommUtil.null2Long(gc_id));
        if (ugc != null) {
            Set<Long> ids = genericUserGcIds(ugc);
            List<UserGoodsClass> ugc_list = new ArrayList<UserGoodsClass>();
            for (Long g_id : ids) {
                UserGoodsClass temp_ugc = this.userGoodsClassService.getObjById(g_id);
                ugc_list.add(temp_ugc);
            }
            gqo.addQuery("ugc", ugc, "obj.goods_ugcs", "member of");
            for (int i = 0; i < ugc_list.size(); i++) {
                gqo.addQuery("ugc" + i, ugc_list.get(i), "obj.goods_ugcs", "member of", "or");
            }
        }
    /*    Map<String, Object> paras = new HashMap<String, Object>();
        String gc_ids = request.getParameter("gc_ids");
        mv.addObject("gc_ids", gc_ids);
        if (StringUtils.isNotBlank(gc_ids)) {
            ids = new HashSet<Long>();
            ids.add(Long.parseLong(gc_ids));
            GoodsClass gcids = this.goodsClassService.getObjById(CommUtil.null2Long(gc_ids));
            mv.addObject("gcids", gcids.getClassName());
        } else {
            if (gc != null)
                mv.addObject("gcids", gc.getClassName());
        }*/
        if (!"".equals(CommUtil.null2String(store_id))) {
            gqo.addQuery("obj.goods_store.id",
                new SysMap("store_id", CommUtil.null2Long(store_id)), "=");
            mv.addObject("store_id", store_id);
        }
        // 分类
      /*  if (ids != null) {
            paras.put("ids", ids);
            gqo.addQuery("obj.gc.id in (:ids)", paras);
        }*/
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
        gqo.setPageSize(Constent.CLASS_GOODS_LIST_PAGE_SIZE);
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
        if (!CommUtil.null2String(gs_ids).equals("")) {
            List<List<GoodsSpecProperty>> gsp_lists = generic_gsp(gs_ids);
            for (int j = 0; j < gsp_lists.size(); j++) {
                List<GoodsSpecProperty> gsp_list = (List) gsp_lists.get(j);
                if (gsp_list.size() == 1) {
                    GoodsSpecProperty gsp = (GoodsSpecProperty) gsp_list.get(0);
                    gqo.addQuery("gsp" + j, gsp, "obj.goods_specs", "member of", "and");
                    Map<String, Object> map = new HashMap<String, Object>();
                    if (null != gsp) {
                        map.put("name", gsp.getSpec().getName());
                        map.put("value", gsp.getValue());
                        map.put("type", "gs");
                        map.put("id", gsp.getId());
                        goods_property.add(map);
                    }

                } else {
                    for (int i = 0; i < gsp_list.size(); i++) {
                        if (i == 0) {
                            GoodsSpecProperty gsp = (GoodsSpecProperty) gsp_list.get(i);
                            gqo.addQuery("gsp" + j + i, gsp, "obj.goods_specs", "member of", "and(");
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("name", gsp.getSpec().getName());
                            map.put("value", gsp.getValue());
                            map.put("type", "gs");
                            map.put("id", gsp.getId());
                            goods_property.add(map);
                        } else if (i == gsp_list.size() - 1) {
                            GoodsSpecProperty gsp = (GoodsSpecProperty) gsp_list.get(i);
                            gqo.addQuery("gsp" + j + i, gsp, "obj.goods_specs)", "member of", "or");
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("name", gsp.getSpec().getName());
                            map.put("value", gsp.getValue());
                            map.put("type", "gs");
                            map.put("id", gsp.getId());
                            goods_property.add(map);
                        } else {
                            GoodsSpecProperty gsp = (GoodsSpecProperty) gsp_list.get(i);
                            gqo.addQuery("gsp" + j + i, gsp, "obj.goods_specs", "member of", "or");
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("name", gsp.getSpec().getName());
                            map.put("value", gsp.getValue());
                            map.put("type", "gs");
                            map.put("id", gsp.getId());
                            goods_property.add(map);
                        }
                    }
                }
            }
            mv.addObject("gs_ids", gs_ids);
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
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("common", Boolean.valueOf(true));
        List<Area> areas = this.areaService.query(
            "select obj from Area obj where obj.common=:common order by sequence asc", params, -1,
            -1);
        mv.addObject("areas", areas);
        gqo.addQuery("obj.disabled", new SysMap("disabled", false), "=");

        IPageList pList = this.goodsService.list(gqo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        mv.addObject("gc", ugc);
        mv.addObject("orderBy", orderBy1);
        mv.addObject("user_viewed_goods",
            request.getSession(false).getAttribute("user_viewed_goods"));
        mv.addObject("goods_property", goods_property);
        if (CommUtil.null2String(goods_view).equals("list")) {
            goods_view = "list";
        } else {
            goods_view = "thumb";
        }

        if ((detail_property_status != null) && (!detail_property_status.equals(""))) {
            mv.addObject("detail_property_status", detail_property_status);
            String[] temp_str = detail_property_status.split(",");
            Map<String, Object> pro_map = new HashMap<String, Object>();
            List<String> pro_list = new ArrayList<String>();
            for (String property_status : temp_str) {
                if ((property_status != null) && (!property_status.equals(""))) {
                    String[] mark = property_status.split("_");
                    pro_map.put(mark[0], mark[1]);
                    pro_list.add(mark[0]);
                }
            }
            mv.addObject("pro_list", pro_list);
            mv.addObject("pro_map", pro_map);
        }
        mv.addObject("goods_view", goods_view);
    
     

        return mv;
    }
    
    @RequestMapping({ "/store/{storeId}.htm/mobile/store_goods_list_ajax.htm" })
    public ModelAndView store_goods_list_ajax(HttpServletRequest request, HttpServletResponse response,
                                         String gc_id, String currentPage, String orderBy,
                                         String orderType, String goods_current_price_begin,
                                         String goods_current_price_end, String brand_ids,
                                         String gs_ids, String properties, String op,
                                         String goods_name, String area_name, String area_id,
                                         String goods_view, String all_property_status,
                                         String detail_property_status, String store_id) {
       /* ModelAndView mv = new JModelAndView("h5/product_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);*/
    	ModelAndView mv = new JModelAndView("h5/search_product_list_ajax.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
                response);
        if ((orderBy == null) || (orderBy.equals(""))) {
            orderBy = "createtime";
        }
        if ((op != null) && (!op.equals(""))) {
            mv.addObject("op", op);
        }
        String orderBy1 = orderBy;
        if (this.configService.getSysConfig().isZtc_status()) {
            orderBy = "ztc_dredge_price desc,obj." + orderBy;
        }
        GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
        GoodsClass gc = this.goodsClassService.getObjById(CommUtil.null2Long(gc_id));
        Set<Long> ids = null;
        if (gc != null) {
            mv.addObject("gc", gc);
            ids = genericIds(gc);
        }
        Map<String, Object> paras = new HashMap<String, Object>();
        String gc_ids = request.getParameter("gc_ids");
        mv.addObject("gc_ids", gc_ids);
        if (StringUtils.isNotBlank(gc_ids)) {
            ids = new HashSet<Long>();
            ids.add(Long.parseLong(gc_ids));
            GoodsClass gcids = this.goodsClassService.getObjById(CommUtil.null2Long(gc_ids));
            mv.addObject("gcids", gcids.getClassName());
        } else {
            if (gc != null)
                mv.addObject("gcids", gc.getClassName());
        }
        if (!"".equals(CommUtil.null2String(store_id))) {
            gqo.addQuery("obj.goods_store.id",
                new SysMap("store_id", CommUtil.null2Long(store_id)), "=");
            mv.addObject("store_id", store_id);
        }
        // 分类
        if (ids != null) {
            paras.put("ids", ids);
            gqo.addQuery("obj.gc.id in (:ids)", paras);
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
        gqo.setPageSize(Constent.CLASS_GOODS_LIST_PAGE_SIZE);
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
        if (!CommUtil.null2String(gs_ids).equals("")) {
            List<List<GoodsSpecProperty>> gsp_lists = generic_gsp(gs_ids);
            for (int j = 0; j < gsp_lists.size(); j++) {
                List<GoodsSpecProperty> gsp_list = (List) gsp_lists.get(j);
                if (gsp_list.size() == 1) {
                    GoodsSpecProperty gsp = (GoodsSpecProperty) gsp_list.get(0);
                    gqo.addQuery("gsp" + j, gsp, "obj.goods_specs", "member of", "and");
                    Map<String, Object> map = new HashMap<String, Object>();
                    if (null != gsp) {
                        map.put("name", gsp.getSpec().getName());
                        map.put("value", gsp.getValue());
                        map.put("type", "gs");
                        map.put("id", gsp.getId());
                        goods_property.add(map);
                    }

                } else {
                    for (int i = 0; i < gsp_list.size(); i++) {
                        if (i == 0) {
                            GoodsSpecProperty gsp = (GoodsSpecProperty) gsp_list.get(i);
                            gqo.addQuery("gsp" + j + i, gsp, "obj.goods_specs", "member of", "and(");
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("name", gsp.getSpec().getName());
                            map.put("value", gsp.getValue());
                            map.put("type", "gs");
                            map.put("id", gsp.getId());
                            goods_property.add(map);
                        } else if (i == gsp_list.size() - 1) {
                            GoodsSpecProperty gsp = (GoodsSpecProperty) gsp_list.get(i);
                            gqo.addQuery("gsp" + j + i, gsp, "obj.goods_specs)", "member of", "or");
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("name", gsp.getSpec().getName());
                            map.put("value", gsp.getValue());
                            map.put("type", "gs");
                            map.put("id", gsp.getId());
                            goods_property.add(map);
                        } else {
                            GoodsSpecProperty gsp = (GoodsSpecProperty) gsp_list.get(i);
                            gqo.addQuery("gsp" + j + i, gsp, "obj.goods_specs", "member of", "or");
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("name", gsp.getSpec().getName());
                            map.put("value", gsp.getValue());
                            map.put("type", "gs");
                            map.put("id", gsp.getId());
                            goods_property.add(map);
                        }
                    }
                }
            }
            mv.addObject("gs_ids", gs_ids);
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
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("common", Boolean.valueOf(true));
        List<Area> areas = this.areaService.query(
            "select obj from Area obj where obj.common=:common order by sequence asc", params, -1,
            -1);
        mv.addObject("areas", areas);
        gqo.addQuery("obj.disabled", new SysMap("disabled", false), "=");

        IPageList pList = this.goodsService.nolastlist(gqo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        mv.addObject("gc", gc);
        mv.addObject("orderBy", orderBy1);
        mv.addObject("user_viewed_goods",
            request.getSession(false).getAttribute("user_viewed_goods"));
        mv.addObject("goods_property", goods_property);
        if (CommUtil.null2String(goods_view).equals("list")) {
            goods_view = "list";
        } else {
            goods_view = "thumb";
        }
        if (this.configService.getSysConfig().isZtc_status()) {
            List<Goods> ztc_goods = null;
            Map<String, Object> ztc_map = new HashMap<String, Object>();
            ztc_map.put("ztc_status", Integer.valueOf(3));
            ztc_map.put("now_date", new Date());
            ztc_map.put("ztc_gold", Integer.valueOf(0));
            if (this.configService.getSysConfig().getZtc_goods_view() == 0) {
                ztc_goods = this.goodsService
                    .query(
                        "select obj from Goods obj where obj.ztc_status =:ztc_status and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold order by obj.ztc_dredge_price desc",
                        ztc_map, 0, 5);
            }
            if (this.configService.getSysConfig().getZtc_goods_view() == 1) {
                ztc_map.put("gc_ids", ids);
                ztc_goods = this.goodsService
                    .query(
                        "select obj from Goods obj where obj.ztc_status =:ztc_status and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold and obj.gc.id in (:gc_ids) order by obj.ztc_dredge_price desc",
                        ztc_map, 0, 5);
            }
            mv.addObject("ztc_goods", ztc_goods);
        }
        if ((detail_property_status != null) && (!detail_property_status.equals(""))) {
            mv.addObject("detail_property_status", detail_property_status);
            String[] temp_str = detail_property_status.split(",");
            Map<String, Object> pro_map = new HashMap<String, Object>();
            List<String> pro_list = new ArrayList<String>();
            for (String property_status : temp_str) {
                if ((property_status != null) && (!property_status.equals(""))) {
                    String[] mark = property_status.split("_");
                    pro_map.put(mark[0], mark[1]);
                    pro_list.add(mark[0]);
                }
            }
            mv.addObject("pro_list", pro_list);
            mv.addObject("pro_map", pro_map);
        }
        mv.addObject("goods_view", goods_view);
        mv.addObject("all_property_status", all_property_status);
        /**
         * 加入所有品牌
         */

        return mv;
    }
    

    /**商品列表
     * @param request
     * @param response
     * @param gc_id
     * @param store_id
     * @param recommend
     * @param currentPage
     * @param orderBy
     * @param orderType
     * @param begin_price
     * @param end_price
     * @return
     */
    @RequestMapping({ "/store/{storeId}.htm/mobile/goods_list.htm" })
    public ModelAndView goods_list(HttpServletRequest request, HttpServletResponse response,
                                   String gc_id, String store_id, String recommend,
                                   String currentPage, String orderBy, String orderType,
                                   String begin_price, String end_price) {
        UserGoodsClass ugc = this.userGoodsClassService.getObjById(CommUtil.null2Long(gc_id));
        String template = "default";
        Store store = this.storeService.getObjById(CommUtil.null2Long(store_id));
        if (store != null) {
            if ((store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
                template = store.getTemplate();
            }
            ModelAndView mv = new JModelAndView(template + "/mobile/goods_list.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
                request, response);
            GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
            gqo.addQuery("obj.goods_store.id", new SysMap("goods_store_id", store.getId()), "=");
            if (ugc != null) {
                Set<Long> ids = genericUserGcIds(ugc);
                List<UserGoodsClass> ugc_list = new ArrayList<UserGoodsClass>();
                for (Long g_id : ids) {
                    UserGoodsClass temp_ugc = this.userGoodsClassService.getObjById(g_id);
                    ugc_list.add(temp_ugc);
                }
                gqo.addQuery("ugc", ugc, "obj.goods_ugcs", "member of");
                for (int i = 0; i < ugc_list.size(); i++) {
                    gqo.addQuery("ugc" + i, ugc_list.get(i), "obj.goods_ugcs", "member of", "or");
                }
            } else {
                ugc = new UserGoodsClass();
                ugc.setClassName("全部商品");
                mv.addObject("ugc", ugc);
            }
            if ((recommend != null) && (!recommend.equals(""))) {
                gqo.addQuery(
                    "obj.goods_recommend",
                    new SysMap("goods_recommend", Boolean.valueOf(CommUtil.null2Boolean(recommend))),
                    "=");
            }
            gqo.setPageSize(Integer.valueOf(20));
            if ((begin_price != null) && (!begin_price.equals(""))) {
                gqo.addQuery(
                    "obj.store_price",
                    new SysMap("begin_price", BigDecimal.valueOf(CommUtil.null2Double(begin_price))),
                    ">=");
            }
            if ((end_price != null) && (!end_price.equals(""))) {
                gqo.addQuery("obj.store_price",
                    new SysMap("end_price", BigDecimal.valueOf(CommUtil.null2Double(end_price))),
                    "<=");
            }
            gqo.addQuery("obj.disabled", new SysMap("disabled", false), "=");

            IPageList pList = this.goodsService.list(gqo);
            String url = this.configService.getSysConfig().getAddress();
            if ((url == null) || (url.equals(""))) {
                url = CommUtil.getURL(request);
            }
            CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
            mv.addObject("ugc", ugc);
            mv.addObject("store", store);
            mv.addObject("recommend", recommend);
            mv.addObject("begin_price", begin_price);
            mv.addObject("end_price", end_price);
            mv.addObject("goodsViewTools", this.goodsViewTools);
            mv.addObject("storeViewTools", this.storeViewTools);
            mv.addObject("areaViewTools", this.areaViewTools);
            return mv;
        }
        ModelAndView mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        mv.addObject("op_title", "请求参数错误");
        mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
        return mv;
    }

    private Set<Long> genericUserGcIds(UserGoodsClass ugc) {
        Set<Long> ids = new HashSet<Long>();
        ids.add(ugc.getId());
        for (UserGoodsClass child : ugc.getChilds()) {
            Set<Long> cids = genericUserGcIds(child);
            for (Long cid : cids) {
                ids.add(cid);
            }
            ids.add(child.getId());
        }
        return ids;
    }

    /**查看商品并记录足迹
     * @param request
     * @param response
     * @param id
     * @return
     */
    @RequestMapping({ "/store/{storeId}.htm/mobile/goods.htm" })
    public ModelAndView goods(HttpServletRequest request, HttpServletResponse response, String id,@PathVariable String storeId) {
        ModelAndView mv = null;
        Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
        //已上架
        if (obj.getGoods_status() == 0) {
            String template = "default";
            if ((obj.getGoods_store().getTemplate() != null)
                && (!obj.getGoods_store().getTemplate().equals(""))) {
                template = obj.getGoods_store().getTemplate();
            }
            mv = new JModelAndView(template + "/h5/goods.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
            obj.setGoods_click(obj.getGoods_click() + 1);
            if ((this.configService.getSysConfig().isZtc_status()) && (obj.getZtc_status() == 2)) {
                obj.setZtc_click_num(obj.getZtc_click_num() + 1);
            }
            if ((obj.getGroup() != null) && (obj.getGroup_buy() == 2)) {
                Group group = obj.getGroup();
                if (group.getEndTime().before(new Date())) {
                    obj.setGroup(null);
                    obj.setGroup_buy(0);
                    obj.setGoods_current_price(obj.getStore_price());
                }
            } else {
              
            	Double minPrice = CommUtil.null2Double(0);
                Double maxPrice = CommUtil.null2Double(0);
                HashMap<String,Object> paramMap = new HashMap<String,Object>();
                paramMap.put("goodsId", obj.getId());
                List<GoodsItem> goods_items = new ArrayList<GoodsItem>();
                goods_items = this.goodsItemService.query(
                		"select obj from GoodsItem obj where   obj.goods.id =:goodsId and obj.disabled = false", paramMap, -1, -1);
               /* //若用户登录根据策略查询货品价格列表
                HashMap<Long,StrategyGoodsItem> strategyGoodsItems_map = new HashMap<Long,StrategyGoodsItem>();
                if(SecurityUserHolder.getCurrentUser()!=null&&SecurityUserHolder.getCurrentUser().getStrategy()!=null){
                	User crrcUser = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
                	Strategy strategy = crrcUser.getStrategy();
                	if(strategy.isDisabled()==false&&strategy.getStatus()==1){
	                	List<StrategyGoodsItem>	strategyGoodsItems = strategy.getStrategyGoodsItems();
	                	if(strategyGoodsItems!=null&&strategyGoodsItems.size()>0){
	                		for(StrategyGoodsItem sgi:strategyGoodsItems){
	                			strategyGoodsItems_map.put(sgi.getGoods_item().getId(), sgi);
	                		}
	                	}
                	}
                }*/
                mv.addObject("qujian", "qujian");
                if(goods_items!=null&&goods_items.size()>0){
                   for(GoodsItem temp:goods_items){
                	     double goodsPrice;
                	     BigDecimal price = this.goodsViewTools.getStrategyPrice(temp.getGoods(), temp.getId().toString());
                	     goodsPrice = price.doubleValue();
                	     if (minPrice > goodsPrice|| minPrice == 0)
	                            minPrice =goodsPrice;
	                     if (goodsPrice > maxPrice)
	                            maxPrice = goodsPrice;
                	     /*if(strategyGoodsItems_map.get(temp.getId())!=null){
	           				StrategyGoodsItem sgi = strategyGoodsItems_map.get(temp.getId());
	           				goodsPrice = sgi.getPrice().doubleValue();
	           				if (minPrice > goodsPrice|| minPrice == 0)
	                            minPrice =goodsPrice;
	                        if (goodsPrice > maxPrice)
	                            maxPrice = goodsPrice;
	           			  }else{
	           				if (minPrice > temp.getGoods_price().doubleValue()|| minPrice == 0)
	                            minPrice =temp.getGoods_price().doubleValue();
	                        if (temp.getGoods_price().doubleValue() > maxPrice)
	                            maxPrice = temp.getGoods_price().doubleValue();
	           			  }
                		*/
             	   }
                   if(goods_items.size()==1&&goods_items.get(0).getStatus()==0){
                	   mv.addObject("qujian", "");
                   }
                }
               
                DecimalFormat df = new DecimalFormat("######0.00");
                mv.addObject("minPrice", df.format(minPrice));
                mv.addObject("maxPrice", df.format(maxPrice));
            }
            this.goodsService.update(obj);
            
            
            // Store_status 状态
            // <option value="2">正常营业</option>
            // <option value="3">违规关闭</option>
            // <option value="1">等待审核</option>
            // <option value="-1">审核拒绝</option>

            if (obj.getGoods_store().getStore_status() == 2) {//正常营业
                mv.addObject("obj", obj);
                mv.addObject("store", obj.getGoods_store());
                Map<String, Object> params = new HashMap<String, Object>();
                /*        params.put("user_id", obj.getGoods_store().getUser().getId());
                params.put("display", Boolean.valueOf(true));
                List<UserGoodsClass> ugcs = this.userGoodsClassService
                    .query(
                        "select obj from UserGoodsClass obj where obj.user.id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc",
                        params, -1, -1);
                mv.addObject("ugcs", ugcs);*/
          /*      GoodsQueryObject gqo = new GoodsQueryObject();
                gqo.setPageSize(Integer.valueOf(4));
                gqo.addQuery("obj.goods_store.id", new SysMap("store_id", obj.getGoods_store()
                    .getId()), "=");
                gqo.addQuery("obj.goods_recommend",
                    new SysMap("goods_recommend", Boolean.valueOf(true)), "=");
                gqo.addQuery("obj.id", new SysMap("id", obj.getId()), "!=");
                gqo.setOrderBy("createtime");
                gqo.setOrderType("desc");
                gqo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(0)),
                    "=");
                mv.addObject("goods_recommend_list", this.goodsService.list(gqo).getResult());*/
            /*    params.clear();
                params.put("goods_id", obj.getId());
                params.put("evaluate_type", "buyer");
                List<Evaluate> evas = this.evaluateService
                    .query(
                        "select obj from Evaluate obj where obj.evaluate_goods.id=:goods_id and obj.evaluate_type=:evaluate_type",
                        params, -1, -1);
                mv.addObject("eva_count", Integer.valueOf(evas.size()));*/
                mv.addObject("goodsViewTools", this.goodsViewTools);
                mv.addObject("storeViewTools", this.storeViewTools);
                mv.addObject("areaViewTools", this.areaViewTools);
                mv.addObject("transportTools", this.transportTools);

                List<Goods> user_viewed_goods = (List) request.getSession(false).getAttribute(
                    "user_viewed_goods");
                if (user_viewed_goods == null) {
                    user_viewed_goods = new ArrayList<Goods>();
                }
                boolean add = true;
                for (Goods goods : user_viewed_goods) {
                    if (goods.getId().equals(obj.getId())) {
                        add = false;
                        break;
                    }
                }
                if (add) {
                    if (user_viewed_goods.size() >= 4) {
                        user_viewed_goods.set(1, obj);
                    } else {
                        user_viewed_goods.add(obj);
                    }
                }
                request.getSession(false).setAttribute("user_viewed_goods", user_viewed_goods);

                String current_city = "全国";
                /*IpAddress ipAddr = IpAddress.getInstance();
                String current_ip = CommUtil.getIpAddr(request);
                String current_city = ipAddr.IpStringToAddress(current_ip);
                if ((current_city == null) || (current_city.equals(""))) {
                    current_city = "全国";
                }*/
                mv.addObject("current_city", current_city);

                List<Area> areas = this.areaService
                    .query(
                        "select obj from Area obj where obj.parent.id is null order by obj.sequence asc",
                        null, -1, -1);
                mv.addObject("areas", areas);
            //    generic_evaluate(obj.getGoods_store(), mv);
            } else {
            	mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                          this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "店铺暂时关闭中");
                mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
            }
        } else {
        	 mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                      this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "该商品未上架，不允许查看");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
        }

        // 保存用户足迹
        User user =  SecurityUserHolder.getCurrentUser();
        if (null !=user) {
        	/*
	         * 判断店铺是否存在供采关系
	         */
	        if (user!=null) {
	            Map<String, Object> map1 = new HashMap<String, Object>();
	            map1.put("user_id",  user.getId());
	            map1.put("store_id", CommUtil.null2Long(storeId));
	            map1.put("status", 2);
	   
	            String sql = "select count(obj) from UserStoreRelation obj where obj.user.id=:user_id and obj.store.id=:store_id and obj.status=:status ";
	            long relationCount = this.userStoreRelationService.queryCount(sql, map1);
	            mv.addObject("relationCount", relationCount);
	          
	        }
            Map<String, Object> params = new HashMap<String, Object>();
          //查询商品收藏状态
            params.put("user_id", user.getId());
            params.put("goods_id", CommUtil.null2Long(id));
            List<Favorite> list = this.favoriteService.query(
                "select obj from Favorite obj where obj.user.id=:user_id and obj.goods.id=:goods_id",
                params, 0, 1);
            if(list!=null&&list.size()>0){
            	mv.addObject("favsFlag", true);
            }else{
            	mv.addObject("favsFlag", false);
            }
            params.clear();
            params.put("user", user);
            params.put("goods", obj);
            List<Track> tracks = this.trackServ
                .query("select obj from Track obj where obj.user=:user and obj.goods=:goods",
                    params, 0, 1);
            if (tracks.size() > 0) {
                // 更新
                Track t1 = (Track) tracks.get(0);
                Long cnt = t1.getAc_cnt() == null ? 0 : t1.getAc_cnt();
                t1.setAc_cnt(cnt + 1);
                t1.setCreatetime(new Date());
                this.trackServ.update(t1);

                String strSql = "INSERT INTO jm_track_actime VALUES (" + t1.getId().toString()
                                + ", NOW());";
                gueryService.executeNativeSQL(strSql);
            } else {
                // 新增
                Track t1 = new Track(obj, SecurityUserHolder.getCurrentUser());
                t1.setAc_cnt((long) 1);
                t1.setCreatetime(new Date());
                this.trackServ.save(t1);

                long newid = t1.getId();
                String strSql = "INSERT INTO jm_track_actime VALUES (" + newid + ", NOW());";
                gueryService.executeNativeSQL(strSql);
            }

        }

        return mv;
    }

    @RequestMapping({ "/store/{storeId}.htm/mobile/load_good_area.htm" })
    public void load_area(HttpServletRequest request, HttpServletResponse response, String id) {
        StringBuilder jsonArea = new StringBuilder();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("parent_id", CommUtil.null2Long(id));
        List<Area> childs = this.areaService.query(
            "select obj from Area obj where obj.parent.id=:parent_id", map, -1, -1);
        if (childs != null && childs.size() > 0) {
            jsonArea.append("[");
            for (int i = 0; i < childs.size(); i++) {
                if (i != 0 && i != childs.size())
                    jsonArea.append(",");
                Area area = childs.get(i);
                jsonArea.append("{").append("\"id\":").append(area.getId())
                    .append(",\"areaName\":\"").append(area.getAreaName()).append("\"}");
                //                jsonArea = Json.toJson(childs);
            }
            jsonArea.append("]");
            /*JSONArray array = new JSONArray();
            array.addAll(childs);
            jsonArea = array.toJSONString();*/
        }
        response.setContentType("json/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(jsonArea.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    /**直通车商品列表
     * @param request
     * @param response
     * @param currentPage
     * @param orderBy
     * @param orderType
     * @param goods_view
     * @return
     */
    @RequestMapping({ "/store/{storeId}.htm/mobile/ztc_goods_list.htm" })
    public ModelAndView ztc_goods_list(HttpServletRequest request, HttpServletResponse response,
                                       String currentPage, String orderBy, String orderType,
                                       String goods_view) {
        ModelAndView mv = new JModelAndView("ztc_goods_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
        gqo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(0)), "=");
        gqo.addQuery("obj.ztc_status", new SysMap("ztc_status", Integer.valueOf(3)), "=");
        gqo.addQuery("obj.ztc_begin_time", new SysMap("ztc_begin_time", new Date()), "<=");
        gqo.addQuery("obj.ztc_gold", new SysMap("ztc_gold", Integer.valueOf(0)), ">");
        gqo.setOrderBy("ztc_dredge_price");
        gqo.setOrderType("desc");
        gqo.setPageSize(Integer.valueOf(20));
        gqo.addQuery("obj.disabled", new SysMap("disabled", false), "=");

        IPageList pList = this.goodsService.list(gqo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        mv.addObject("goods_view", goods_view);
        mv.addObject("user_viewed_goods",
            request.getSession(false).getAttribute("user_viewed_goods"));
        return mv;
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

    private List<List<GoodsSpecProperty>> generic_gsp(String gs_ids) {
        List<List<GoodsSpecProperty>> list = new ArrayList<List<GoodsSpecProperty>>();
        String[] gs_id_list = gs_ids.substring(1).split("\\|");
        for (String gd_id_info : gs_id_list) {
            String[] gs_info_list = gd_id_info.split(",");
            GoodsSpecProperty gsp = this.goodsSpecPropertyService.getObjById(CommUtil
                .null2Long(gs_info_list[0]));
            boolean create = true;
            for (List<GoodsSpecProperty> gsp_list : list) {
                for (GoodsSpecProperty gsp_temp : gsp_list) {
                    if (gsp_temp.getSpec().getId().equals(gsp.getSpec().getId())) {
                        gsp_list.add(gsp);
                        create = false;
                        break;
                    }
                }
            }
            if (create) {
                List<GoodsSpecProperty> gsps = new ArrayList<GoodsSpecProperty>();
                gsps.add(gsp);
                list.add(gsps);
            }
        }
        return list;
    }

    /**商品评价
     * @param request
     * @param response
     * @param id
     * @param goods_id
     * @param currentPage
     * @return
     */
    @RequestMapping({ "/store/{storeId}.htm/mobile/goods_evaluation.htm" })
    public ModelAndView goods_evaluation(HttpServletRequest request, HttpServletResponse response,
                                         String id, String goods_id, String evaluate_buyer_val,
                                         String currentPage) {
        String template = "default";
        Store store = this.storeService.getObjById(CommUtil.null2Long(id));
        if (store != null) {
            template = store.getTemplate();
        }
        ModelAndView mv = new JModelAndView(template + "/h5/detail_comment.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        EvaluateQueryObject qo = new EvaluateQueryObject(currentPage, mv, "createtime", "desc");
        qo.addQuery("obj.evaluate_goods.id", new SysMap("goods_id", CommUtil.null2Long(goods_id)),
            "=");
        qo.addQuery("obj.evaluate_type", new SysMap("evaluate_type", "goods"), "=");
        qo.addQuery("obj.evaluate_status", new SysMap("evaluate_status", Integer.valueOf(0)), "=");
        IPageList pList = this.evaluateService.list(qo);
        countEvaluate(pList.getResult(), mv);
        if ("".equals(CommUtil.null2String(evaluate_buyer_val))) {
            evaluate_buyer_val = "1";
        }
        qo.addQuery("obj.evaluate_buyer_val",
            new SysMap("evaluate_buyer_val", Integer.valueOf(evaluate_buyer_val)), "=");
        qo.setPageSize(Integer.valueOf(10));
        IPageList objs = this.evaluateService.list(qo);
        CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
                                            + "/mobile/goods_evaluation.htm", "", "", objs, mv);
        mv.addObject("storeViewTools", this.storeViewTools);
        mv.addObject("store", store);
        Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
        mv.addObject("goods", goods);
        mv.addObject("evaluate_buyer_val", evaluate_buyer_val);
        mv.addObject("goodsViewTools", this.goodsViewTools);
        return mv;
    }

    public void countEvaluate(List<Evaluate> list, ModelAndView mv) {
        if (list != null && list.size() > 0) {
            /*List<Evaluate> goodList = new ArrayList<Evaluate>();
            List<Evaluate> zhongList = new ArrayList<Evaluate>();
            List<Evaluate> chaList = new ArrayList<Evaluate>();*/
            BigDecimal goodEval = BigDecimal.ZERO;
            BigDecimal zhongEval = BigDecimal.ZERO;
            BigDecimal chaEval = BigDecimal.ZERO;
            for (Evaluate eval : list) {
                if (eval.getEvaluate_buyer_val() == 1) {
                    //goodList.add(eval);
                    goodEval = goodEval.add(BigDecimal.valueOf(1));
                }
                if (eval.getEvaluate_buyer_val() == 0) {
                    //zhongList.add(eval);
                    zhongEval = zhongEval.add(BigDecimal.valueOf(1));
                }
                if (eval.getEvaluate_buyer_val() == -1) {
                    //chaList.add(eval);
                    chaEval = chaEval.add(BigDecimal.valueOf(1));
                }
            }
            mv.addObject("evalSize", list.size());
            /*mv.addObject("goodList", goodList);
            mv.addObject("zhongList", zhongList);
            mv.addObject("chaList", chaList);*/
            mv.addObject("goodEvalCount", goodEval);
            mv.addObject("zhongEvalCount", zhongEval);
            mv.addObject("chaEvalCount", chaEval);
            if (goodEval != BigDecimal.ZERO)
                goodEval.divide(BigDecimal.valueOf(list.size()), 2)
                    .multiply(BigDecimal.valueOf(100)).intValue();
            if (zhongEval != BigDecimal.ZERO)
                zhongEval.divide(BigDecimal.valueOf(list.size()), 2)
                    .multiply(BigDecimal.valueOf(100)).intValue();
            if (chaEval != BigDecimal.ZERO)
                chaEval.divide(BigDecimal.valueOf(list.size()), 2)
                    .multiply(BigDecimal.valueOf(100)).intValue();
            mv.addObject("goodEval", goodEval);
            mv.addObject("zhongEval", zhongEval);
            mv.addObject("chaEval", chaEval);
        }
    }

    /**商品详情
     * @param request
     * @param response
     * @param id
     * @param goods_id
     * @return
     */
    @RequestMapping({ "/store/{storeId}.htm/mobile/goods_detail.htm" })
    public ModelAndView goods_detail(HttpServletRequest request, HttpServletResponse response,
                                     String id, String goods_id) {
        String template = "default";
        Store store = this.storeService.getObjById(CommUtil.null2Long(id));
        if (store != null) {
            template = store.getTemplate();
        }
        ModelAndView mv = new JModelAndView(template + "/h5/goods_detail.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
        mv.addObject("obj", goods);
      //  generic_evaluate(goods.getGoods_store(), mv);
        mv.addObject("goodsViewTools", this.goodsViewTools);
        //this.userTools.query_user();
        return mv;
    }

    /**查找购物车
     * @param request
     * @param response
     * @param id
     * @param goods_id
     * @param currentPage
     * @return
     */
    @RequestMapping({ "/store/{storeId}.htm/mobile/goods_order.htm" })
    public ModelAndView goods_order(HttpServletRequest request, HttpServletResponse response,
                                    String id, String goods_id, String currentPage) {
        String template = "default";
        Store store = this.storeService.getObjById(CommUtil.null2Long(id));
        if (store != null) {
            template = store.getTemplate();
        }
        ModelAndView mv = new JModelAndView(template + "/mobile/goods_order.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        GoodsCartQueryObject qo = new GoodsCartQueryObject(currentPage, mv, "createtime", "desc");
        qo.addQuery("obj.goods.id", new SysMap("goods_id", CommUtil.null2Long(goods_id)), "=");
        qo.addQuery("obj.of.order_status", new SysMap("order_status", Integer.valueOf(20)), ">=");
        qo.setPageSize(Integer.valueOf(8));
        IPageList pList = this.goodsCartService.list(qo);
        CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + "/mobile/goods_order.htm",
            "", "", pList, mv);
        mv.addObject("storeViewTools", this.storeViewTools);
        return mv;
    }

    /**根据商品加载商品咨询
     * @param request
     * @param response
     * @param id
     * @param goods_id
     * @param currentPage
     * @return
     */
    @RequestMapping({ "/store/{storeId}.htm/mobile/goods_consult.htm" })
    public ModelAndView goods_consult(HttpServletRequest request, HttpServletResponse response,
                                      String id, String goods_id, String currentPage) {
        String template = "default";
        Store store = this.storeService.getObjById(CommUtil.null2Long(id));
        if (store != null) {
            template = store.getTemplate();
        }
        ModelAndView mv = new JModelAndView(template + "/h5/detail_consulation.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        ConsultQueryObject qo = new ConsultQueryObject(currentPage, mv, "createtime", "desc");
        qo.addQuery("obj.goods.id", new SysMap("goods_id", CommUtil.null2Long(goods_id)), "=");
        qo.setPageSize(Integer.valueOf(10));
        IPageList pList = this.consultService.list(qo);
        CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + "/mobile/goods_consult.htm",
            "", "", pList, mv);
        mv.addObject("storeViewTools", this.storeViewTools);
        mv.addObject("goodsViewTools", this.goodsViewTools);
        mv.addObject("goods_id", goods_id);
        Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
        mv.addObject("goods", goods);
        if (SecurityUserHolder.getCurrentUser() == null) {
            mv.addObject("login", true);
        }
        return mv;
    }

    /**商品咨询保存
     * @param request
     * @param response
     * @param goods_id
     * @param consult_content
     * @param consult_email
     * @param Anonymous
     * @param consult_code
     * @return
     */
    @RequestMapping({ "/store/{storeId}.htm/mobile/goods_consult_save.htm" })
    public String goods_consult_save(HttpServletRequest request, HttpServletResponse response,
                                     String goods_id, String consult_content, String consult_email,
                                     String Anonymous, String consult_code) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String verify_number = CommUtil.null2String(request.getSession(false).getAttribute(
            "consult_code"));
        boolean visit_consult = true;
        if (!this.configService.getSysConfig().isVisitorConsult()) {
            if (SecurityUserHolder.getCurrentUser() == null) {
                visit_consult = false;
            }
            if (CommUtil.null2Boolean(Anonymous)) {
                visit_consult = false;
            }
        }
        Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
        if (visit_consult) {
            if (CommUtil.null2String(consult_code).equals(verify_number)) {
                Consult obj = new Consult();
                obj.setCreatetime(new Date());
                obj.setConsult_content(consult_content);
                obj.setConsult_email(consult_email);
                if (!CommUtil.null2Boolean(Anonymous)) {
                    obj.setConsult_user(SecurityUserHolder.getCurrentUser());
                    mv.addObject("op_title", "咨询发布成功");
                }
                obj.setGoods(goods);
                this.consultService.save(obj);
                request.getSession(false).removeAttribute("consult_code");
            } else {
                mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "验证码错误，咨询发布失败");
            }
        } else {
            mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "不允许游客咨询");
        }
        mv.addObject("url", CommUtil.getURL(request) + "/mobile/goods_consult.htm?goods_id="
                            + goods_id + "&id=" + goods.getGoods_store().getId());
        return "redirect:/mobile/goods_consult.htm?goods_id=" + goods_id + "&id="
               + goods.getGoods_store().getId();
    }

    /**根据商品加载商品价格和库存   点击商品规格加载商品价格
     * @param request
     * @param response
     * @param gsp
     * @param id
     */
    @RequestMapping({ "/store/{storeId}.htm/mobile/load_goods_gsp.htm" })
    public void load_goods_gsp(HttpServletRequest request, HttpServletResponse response,
                               String gsp, String id,@PathVariable String storeId) {
        Map<String, Object> map = new HashMap<String, Object>();
        int count = 0;
        BigDecimal price = null;
        //double market_price =0.0D;
        String gooods_itemId="";
        List<GoodsItem> goods_items = new ArrayList<GoodsItem>();
        String[] gsp_ids = gsp.split(",");
        
    	Arrays.sort(gsp_ids,new Comparator<String>() {
			@Override
			public int compare(String arg0, String arg1) {
				return Integer.valueOf(arg0).compareTo(Integer.valueOf(arg1));
			}
		});
    	
    	String spec_combination = "";
        for(int i=0;i<gsp_ids.length;i++){
    	   spec_combination  =spec_combination+gsp_ids[i]+"_";
        }
        HashMap<String,Object> paramMap = new HashMap<String,Object>();
        List<GoodsStepPrice> steplist=new ArrayList<GoodsStepPrice>();
        paramMap.put("goodsId", CommUtil.null2Long(id));
        paramMap.put("spec_combination", spec_combination);
        goods_items = this.goodsItemService.query(
        		"select obj from GoodsItem obj where obj.spec_combination =:spec_combination and obj.goods.id =:goodsId", paramMap, -1, -1);
        /*//若用户登录根据策略查询货品价格列表
        HashMap<Long,StrategyGoodsItem> strategyGoodsItems_map = new HashMap<Long,StrategyGoodsItem>();
        if(SecurityUserHolder.getCurrentUser()!=null){
        	User crrcUser = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        	if(crrcUser.getStrategy()!=null){
        		Strategy strategy = crrcUser.getStrategy();
        		if(strategy.isDisabled()==false&&strategy.getStatus()==1){
	            	List<StrategyGoodsItem>	strategyGoodsItems = strategy.getStrategyGoodsItems();
	            	if(strategyGoodsItems!=null&&strategyGoodsItems.size()>0){
	            		for(StrategyGoodsItem sgi:strategyGoodsItems){
	            			strategyGoodsItems_map.put(sgi.getGoods_item().getId(), sgi);
	            		}
	            	}
        		}
        	}
        	
        }*/
        if(goods_items!=null&&goods_items.size()>0){
        	for(GoodsItem item:goods_items){
     		   if(spec_combination.equals(item.getSpec_combination())){
     			   count = item.getGoods_inventory();
     			  price = this.goodsViewTools.getStrategyPrice(item.getGoods(), item.getId().toString());
     			   /*if(strategyGoodsItems_map.get(item.getId())!=null){
     				  StrategyGoodsItem sgi = strategyGoodsItems_map.get(item.getId());
     				  price = sgi.getPrice().doubleValue();
     			   }else{
     				  price = item.getGoods_price().doubleValue();
     				 User user=SecurityUserHolder.getCurrentUser();
  			        //判断店铺是否存在供采关系
  			        if (user!=null) {
  			            Map<String, Object> map1 = new HashMap<String, Object>();
  			            map1.put("user_id",  user.getId());
  			            map1.put("store_id", CommUtil.null2Long(storeId));
  			            map1.put("status", 2);
  			    
  			            String sql = "select count(obj) from UserStoreRelation obj where obj.user.id=:user_id and obj.store.id=:store_id and obj.status=:status ";
  			            long relationCount = this.userStoreRelationService.queryCount(sql, map1);
  			            if(relationCount>0){
  			            	  price = item.getGoods_price().doubleValue();//有供采关系是显示销售价
  			            	  //存在供采关系是显示市场价格
  			            	  map.put("marketPrice", item.getMarket_price());
  			            	  
  			            }
  			          
  			        }
     				  
     			   }*/
     			  gooods_itemId=item.getId()+"";
     		   }
     	   }
        }
       
      
        DecimalFormat df = new DecimalFormat("######0.00");
        map.put("count", Integer.valueOf(count));
        map.put("price", df.format(price));
        map.put("gooods_itemId", gooods_itemId);
        map.put("steplist", steplist);
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
    
    
    /**买家根据商品加载商品价格和库存   点击商品规格加载商品价格
     * 单规格价格策略
     * @param request
     * @param response
     * @param gsp
     * @param id
     */
    @RequestMapping({ "/store/{storeId}.htm/mobile/load_goods_single_gsp.htm" })
    public void load_buyer_single_goods_gsp(HttpServletRequest request, HttpServletResponse response,
                                String id,@PathVariable String storeId) {
       /// Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
        Map<String, Object> map = new HashMap<String, Object>();
        int count = 0;
        BigDecimal price = null;
       
        String gooods_itemId="";
        List<GoodsStepPrice> steplist=new ArrayList<GoodsStepPrice>();
        Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
        List<GoodsItem> goods_item_list = goods.getGoods_item_list();
        //若用户登录根据策略查询货品价格列表
        if(goods_item_list!=null&&goods_item_list.size()>0){
    		GoodsItem goodsItem = goods_item_list.get(0);
    		gooods_itemId=goodsItem.getId()+"";
    			count=goodsItem.getGoods_inventory();
    			price = this.goodsViewTools.getStrategyPrice(goods, gooods_itemId);
    	}else{//不存在该货品
    		price=goods.getGoods_current_price();
			count=0;
    	}
   
        DecimalFormat df = new DecimalFormat("######0.00");
        map.put("count", Integer.valueOf(count));
        map.put("price", df.format(price));
        map.put("gooods_itemId", gooods_itemId);
        map.put("steplist", steplist);
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

    /**根据商品和城市计算运费
     * @param request
     * @param response
     * @param city_name
     * @param goods_id
     */
    @RequestMapping({ "/store/{storeId}.htm/mobile/trans_fee.htm" })
    public void trans_fee(HttpServletRequest request, HttpServletResponse response,
                          String city_name, String goods_id) {
        Map<String, Object> map = new HashMap<String, Object>();
        Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
        float mail_fee = 0.0F;
        float express_fee = 0.0F;
        float ems_fee = 0.0F;
        if (goods.getTransport() != null) {
            mail_fee = this.transportTools.cal_goods_trans_fee(
                CommUtil.null2String(goods.getTransport().getId()), "mail",
                CommUtil.null2String(goods.getGoods_weight()),
                CommUtil.null2String(goods.getGoods_volume()), city_name);
            express_fee = this.transportTools.cal_goods_trans_fee(
                CommUtil.null2String(goods.getTransport().getId()), "express",
                CommUtil.null2String(goods.getGoods_weight()),
                CommUtil.null2String(goods.getGoods_volume()), city_name);
            ems_fee = this.transportTools.cal_goods_trans_fee(
                CommUtil.null2String(goods.getTransport().getId()), "ems",
                CommUtil.null2String(goods.getGoods_weight()),
                CommUtil.null2String(goods.getGoods_volume()), city_name);
        }
        map.put("mail_fee", Float.valueOf(mail_fee));
        map.put("express_fee", Float.valueOf(express_fee));
        map.put("ems_fee", Float.valueOf(ems_fee));
        map.put("current_city_info", CommUtil.substring(city_name, 5));
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

    @RequestMapping({ "/store/{storeId}.htm/mobile/goods_share.htm" })
    public ModelAndView goods_share(HttpServletRequest request, HttpServletResponse response,
                                    String goods_id) {
        ModelAndView mv = new JModelAndView("goods_share.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
        mv.addObject("obj", goods);
        return mv;
    }

    private Set<Long> genericIds(GoodsClass gc) {
        Set<Long> ids = new HashSet<Long>();
        ids.add(gc.getId());
        for (GoodsClass child : gc.getChilds()) {
            Set<Long> cids = genericIds(child);
            for (Long cid : cids) {
                ids.add(cid);
            }
            ids.add(child.getId());
        }
        return ids;
    }

    /**店铺评分
     * @param store
     * @param mv
     */
    private void generic_evaluate(Store store, ModelAndView mv) {
        double description_result = 0.0D;
        double service_result = 0.0D;
        double ship_result = 0.0D;
        if (store.getSc() != null) {
            StoreClass sc = this.storeClassService.getObjById(store.getSc().getId());
            float description_evaluate = CommUtil.null2Float(sc.getDescription_evaluate());
            float service_evaluate = CommUtil.null2Float(sc.getService_evaluate());
            float ship_evaluate = CommUtil.null2Float(sc.getShip_evaluate());
            if (store.getPoint() != null) {
                float store_description_evaluate = CommUtil.null2Float(store.getPoint()
                    .getDescription_evaluate());
                float store_service_evaluate = CommUtil.null2Float(store.getPoint()
                    .getService_evaluate());
                float store_ship_evaluate = CommUtil
                    .null2Float(store.getPoint().getShip_evaluate());

                description_result = CommUtil.div(
                    Float.valueOf(store_description_evaluate - description_evaluate),
                    Float.valueOf(description_evaluate));
                service_result = CommUtil.div(
                    Float.valueOf(store_service_evaluate - service_evaluate),
                    Float.valueOf(service_evaluate));
                ship_result = CommUtil.div(Float.valueOf(store_ship_evaluate - ship_evaluate),
                    Float.valueOf(ship_evaluate));
            }
        }
        if (description_result > 0.0D) {
            mv.addObject("description_css", "better");
            mv.addObject("description_type", "高于");
            mv.addObject(
                "description_result",
                CommUtil.null2String(Double.valueOf(CommUtil.mul(
                    Double.valueOf(description_result), Integer.valueOf(100)))) + "%");
        }
        if (description_result == 0.0D) {
            mv.addObject("description_css", "better");
            mv.addObject("description_type", "持平");
            mv.addObject("description_result", "-----");
        }
        if (description_result < 0.0D) {
            mv.addObject("description_css", "lower");
            mv.addObject("description_type", "低于");
            mv.addObject(
                "description_result",
                CommUtil.null2String(Double.valueOf(CommUtil.mul(
                    Double.valueOf(-description_result), Integer.valueOf(100))))
                        + "%");
        }
        if (service_result > 0.0D) {
            mv.addObject("service_css", "better");
            mv.addObject("service_type", "高于");
            mv.addObject(
                "service_result",
                CommUtil.null2String(Double.valueOf(CommUtil.mul(Double.valueOf(service_result),
                    Integer.valueOf(100)))) + "%");
        }
        if (service_result == 0.0D) {
            mv.addObject("service_css", "better");
            mv.addObject("service_type", "持平");
            mv.addObject("service_result", "-----");
        }
        if (service_result < 0.0D) {
            mv.addObject("service_css", "lower");
            mv.addObject("service_type", "低于");
            mv.addObject(
                "service_result",
                CommUtil.null2String(Double.valueOf(CommUtil.mul(Double.valueOf(-service_result),
                    Integer.valueOf(100)))) + "%");
        }
        if (ship_result > 0.0D) {
            mv.addObject("ship_css", "better");
            mv.addObject("ship_type", "高于");
            mv.addObject(
                "ship_result",
                CommUtil.null2String(Double.valueOf(CommUtil.mul(Double.valueOf(ship_result),
                    Integer.valueOf(100)))) + "%");
        }
        if (ship_result == 0.0D) {
            mv.addObject("ship_css", "better");
            mv.addObject("ship_type", "持平");
            mv.addObject("ship_result", "-----");
        }
        if (ship_result < 0.0D) {
            mv.addObject("ship_css", "lower");
            mv.addObject("ship_type", "低于");
            mv.addObject(
                "ship_result",
                CommUtil.null2String(Double.valueOf(CommUtil.mul(Double.valueOf(-ship_result),
                    Integer.valueOf(100)))) + "%");
        }
    }

    @RequestMapping({ "/store/{storeId}.htm/mobile/addr_save.htm" })
    public void addr_save(HttpServletRequest request, HttpServletResponse response, String id,
                          String area_id, String currentPage) {
        boolean flag = false;
        WebForm wf = new WebForm();
        Address address = null;
        address = (Address) wf.toPo(request, Address.class);
        address.setCreatetime(new Date());
        address.setUser(SecurityUserHolder.getCurrentUser());
        Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
        address.setArea(area);
        this.addressService.save(address);
        flag = true;
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            if (flag) {
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", address.getId());
                map.put("trueName", address.getTrueName());
                map.put("mobile",
                    address.getMobile() == null ? address.getTelephone() : address.getMobile());
                map.put(
                    "areaName",
                    area.getAreaName() + area.getParent().getAreaName()
                            + area.getParent().getParent().getAreaName() + " "
                            + address.getArea_info());
                list.add(map);
                String temp = Json.toJson(list, JsonFormat.compact());
                PrintWriter writer = response.getWriter();
                writer.print(temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
