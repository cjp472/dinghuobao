package com.javamalls.front.web.h5.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.front.web.tools.AreaViewTools;
import com.javamalls.front.web.tools.GoodsViewTools;
import com.javamalls.front.web.tools.StoreViewTools;
import com.javamalls.platform.domain.Favorite;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.GoodsRetrieve;
import com.javamalls.platform.domain.GoodsRetrieveProperty;
import com.javamalls.platform.domain.GoodsSpecProperty;
import com.javamalls.platform.domain.GoodsSpecification;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.StoreClass;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.UserGoodsClass;
import com.javamalls.platform.domain.query.GoodsQueryObject;
import com.javamalls.platform.service.IGoodsRetrievePropertyService;
import com.javamalls.platform.service.IGoodsRetrieveService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IStoreClassService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserGoodsClassService;

/**
 *                       
 * @Filename: H5StoreViewAction.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: fanguangzhou@javamalls.com
 *
 */
@Controller
public class H5StoreViewAction {

    @Autowired
    private ISysConfigService             configService;
    @Autowired
    private IUserConfigService            userConfigService;
    @Autowired
    private IStoreService                 storeService;
    @Autowired
    private IStoreClassService            storeClassService;
    @Autowired
    private IGoodsService                 goodsService;
    @Autowired
    private IUserGoodsClassService        userGoodsClassService;
    @Autowired
    private IGoodsRetrieveService         goodsRetrieveService;
    @Autowired
    private IGoodsRetrievePropertyService goodsRetrievePropertyService;
    @Autowired
    private AreaViewTools                 areaViewTools;
    @Autowired
    private GoodsViewTools                goodsViewTools;
    @Autowired
    private StoreViewTools                storeViewTools;

    @RequestMapping({ "/mobile/store.htm" })
    public ModelAndView store(HttpServletRequest request, HttpServletResponse response, String id) {
        String serverName = request.getServerName().toLowerCase();
        Store store = null;
        if ((id == null) && (serverName.indexOf(".") >= 0)
            && (serverName.indexOf(".") != serverName.lastIndexOf("."))
            && (this.configService.getSysConfig().isSecond_domain_open())) {
            String secondDomain = serverName.substring(0, serverName.indexOf("."));
            store = this.storeService.getObjByProperty("store_second_domain", secondDomain);
        } else {
            store = this.storeService.getObjById(CommUtil.null2Long(id));
        }
        if (store == null) {
            ModelAndView mv = new JModelAndView("/h5/error.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
                request, response);
            mv.addObject("op_title", "不存在该店铺信息");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
            return mv;
        }
        String template = "default";
        if ((store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
            template = store.getTemplate();
        }
        ModelAndView mv = new JModelAndView(template + "/h5/store_index.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        if (store.getStore_status() == 2) {
            add_store_common_info(mv, store);
            mv.addObject("store", store);
            mv.addObject("nav_id", "store_index");
            boolean favsFlag = false;
            List<Favorite> favs = store.getFavs();
            if (null != SecurityUserHolder.getCurrentUser()) {
                User currentUser = SecurityUserHolder.getCurrentUser();
                if (favs != null && favs.size() > 0) {
                    for (Favorite f : favs) {
                        if (f.getType() == 1) {
                            if (f.getUser().getId().equals(currentUser.getId())) {
                                favsFlag = true;
                            }
                        }

                    }
                }
            }
            mv.addObject("favsFlag", favsFlag);
        } else {
            mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "店铺已经关闭或者未开通店铺");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
        }
        generic_evaluate(store, mv);
        return mv;
    }

    private void add_store_common_info(ModelAndView mv, Store store) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", store.getUser().getId());
        params.put("display", Boolean.valueOf(true));
        List<UserGoodsClass> ugcs = this.userGoodsClassService
            .query(
                "select obj from UserGoodsClass obj where obj.user.id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc",
                params, -1, -1);
        mv.addObject("ugcs", ugcs);
        params.clear();
        params.put("recommend", Boolean.valueOf(true));
        params.put("goods_store_id", store.getId());
        params.put("goods_status", Integer.valueOf(0));
        List<Goods> goods_recommend = this.goodsService
            .query(
                "select obj from Goods obj where obj.goods_recommend=:recommend and obj.goods_store.id=:goods_store_id and obj.goods_status=:goods_status order by obj.createtime desc",
                params, 0, 6);
        if (goods_recommend != null && goods_recommend.size() > 1) {//只显示双数
            if (goods_recommend.size() % 2 != 0)
                goods_recommend.remove(goods_recommend.size() - 1);
            mv.addObject("goods_recommend", goods_recommend);
        }

        params.clear();
        params.put("goods_store_id", store.getId());
        params.put("goods_status", Integer.valueOf(0));
        List<Goods> goods_new = this.goodsService
            .query(
                "select obj from Goods obj where obj.goods_store.id=:goods_store_id and obj.goods_status=:goods_status order by obj.createtime desc ",
                params, 0, 6);
        if (goods_new != null && goods_new.size() > 1) {//只显示双数
            if (goods_new.size() % 2 != 0)
                goods_new.remove(goods_new.size() - 1);
            mv.addObject("goods_new", goods_new);
        }
        mv.addObject("goodsViewTools", this.goodsViewTools);
        mv.addObject("storeViewTools", this.storeViewTools);
        mv.addObject("areaViewTools", this.areaViewTools);
    }

    private void generic_evaluate(Store store, ModelAndView mv) {
        double description_result = 0.0D;
        double service_result = 0.0D;
        double ship_result = 0.0D;
        if ((store != null) && (store.getSc() != null) && (store.getPoint() != null)) {
            StoreClass sc = this.storeClassService.getObjById(store.getSc().getId());
            float description_evaluate = CommUtil.null2Float(sc.getDescription_evaluate());
            float service_evaluate = CommUtil.null2Float(sc.getService_evaluate());
            float ship_evaluate = CommUtil.null2Float(sc.getShip_evaluate());
            float store_description_evaluate = CommUtil.null2Float(store.getPoint()
                .getDescription_evaluate());
            float store_service_evaluate = CommUtil.null2Float(store.getPoint()
                .getService_evaluate());
            float store_ship_evaluate = CommUtil.null2Float(store.getPoint().getShip_evaluate());

            description_result = CommUtil.div(
                Float.valueOf(store_description_evaluate - description_evaluate),
                Float.valueOf(description_evaluate));
            service_result = CommUtil.div(Float.valueOf(store_service_evaluate - service_evaluate),
                Float.valueOf(service_evaluate));
            ship_result = CommUtil.div(Float.valueOf(store_ship_evaluate - ship_evaluate),
                Float.valueOf(ship_evaluate));
        }
        if (description_result > 0.0D) {
            mv.addObject("description_css", "better");
            mv.addObject("description_type", "高于");
            mv.addObject(
                "description_result",

                CommUtil.null2String(Double.valueOf(CommUtil.mul(
                    Double.valueOf(description_result), Integer.valueOf(100)) > 100.0D ? 100.0D
                    : CommUtil.mul(Double.valueOf(description_result), Integer.valueOf(100))))
                        + "%");
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

    /**
     * 店铺搜索页面
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/store/{id}.htm/mobile/store_search.htm" })
    public ModelAndView showsearch(HttpServletRequest request, HttpServletResponse response,
                                   @PathVariable String id, String keyword) {
        ModelAndView mv = new JModelAndView("default/h5/store_search.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        try {
            Store store = this.storeService.getObjById(CommUtil.null2Long(id));
            if (store == null) {
                mv = new JModelAndView("/h5/error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "不存在该店铺信息");
                mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
                return mv;
            }

            /*
            * 判断是否开启了限制关闭
            */
            int logon_access_state = store.getLogon_access_state();
            if (logon_access_state == 1) {
                //开启了，限制登录后访问
                if (SecurityUserHolder.getCurrentUser() == null) {
                    mv = new JModelAndView("h5/login.html", this.configService.getSysConfig(),
                        this.userConfigService.getUserConfig(), 1, request, response);
                    mv.addObject("storeId", id);
                    return mv;
                }
            }

            /*    Map<String, Object> params = new HashMap<String, Object>();
                params.put("user_id", store.getUser().getId());
                params.put("display", Boolean.valueOf(true));
                List<UserGoodsClass> ugcs = this.userGoodsClassService
                    .query(
                        "select obj from UserGoodsClass obj where obj.user.id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc",
                        params, -1, -1);
                mv.addObject("ugcs", ugcs);*/
            mv.addObject("storeId", id);
            mv.addObject("keyword", keyword);

            //由于没有专门维护热门搜索的，热门搜索根据点击了显示
            //gqo.addQuery("obj.goods_store.id", new SysMap("goods_store_id", store.getId()), "=");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("storeId", CommUtil.null2Long(id));
            List<Goods> query = this.goodsService
                .query(
                    "select obj from Goods obj where obj.disabled=false and obj.goods_status=0 and obj.goods_store.id=:storeId order by obj.goods_click desc",
                    map, 0, 5);
            mv.addObject("hot_search_goods", query);
        } catch (Exception e) {
            mv = new JModelAndView("/h5/error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "不存在该店铺信息");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
            e.printStackTrace();
        }
        return mv;
    }

    @RequestMapping({ "/store/{store_id}.htm/mobile/store_search_list.htm" })
    public ModelAndView search(HttpServletRequest request, HttpServletResponse response,
                               @PathVariable String store_id, String keyword, String currentPage,
                               String orderBy, String orderType, String gc_id, String retrieve) {
        ModelAndView mv = new JModelAndView("default/h5/store_search_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);

        try {
            UserGoodsClass ugc = this.userGoodsClassService.getObjById(CommUtil.null2Long(gc_id));
            String template = "default";
            Store store = this.storeService.getObjById(CommUtil.null2Long(store_id));
            if (store != null) {
                if ((store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
                    template = store.getTemplate();
                }
                mv = new JModelAndView(template + "/h5/store_search_list.html",
                    this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
                    request, response);
                if (orderBy == null || "".equals(orderBy)) {
                    orderBy = "createtime";
                }
                if (orderType == null || "".equals(orderType)) {
                    orderType = "desc";
                }
                GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
                gqo.addQuery("obj.goods_store.id", new SysMap("goods_store_id", store.getId()), "=");
                if (ugc != null) {
                    Set<Long> ids = genericUserGcIds(ugc);
                    List<UserGoodsClass> ugc_list = new ArrayList<UserGoodsClass>();
                    for (Long g_id : ids) {
                        UserGoodsClass temp_ugc = this.userGoodsClassService.getObjById(g_id);
                        ugc_list.add(temp_ugc);
                    }
                    if (ugc_list != null && ugc_list.size() > 0) {
                        gqo.addQuery2("ugc", ugc, "obj.goods_ugcs", "member of");
                        for (int i = 0; i < ugc_list.size(); i++) {
                            if (i == ugc_list.size() - 1) {
                                gqo.addQuery("ugc" + i, ugc_list.get(i), "obj.goods_ugcs ) ",
                                    "member of", "or");
                            } else {
                                gqo.addQuery("ugc" + i, ugc_list.get(i), "obj.goods_ugcs",
                                    "member of", "or");
                            }

                        }

                    } else {
                        gqo.addQuery("ugc", ugc, "obj.goods_ugcs", "member of");
                    }
                } else {
                    ugc = new UserGoodsClass();
                    ugc.setClassName("全部商品");
                    mv.addObject("ugc", ugc);
                }
                if (keyword != null && !"".equals(keyword)) {
                    gqo.addQuery("obj.goods_name", new SysMap("goods_name", "%" + keyword + "%"),
                        "like");
                }

                gqo.setPageSize(Integer.valueOf(12));

                gqo.addQuery("obj.disabled", new SysMap("disabled", false), "=");
                gqo.addQuery("obj.goods_status", new SysMap("goods_status", 0), "=");

                //检索属性 
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("disabled", false);
                params.put("type", 1);
                List<GoodsRetrieve> goodsRetrieveList = this.goodsRetrieveService
                    .query(
                        "select obj from GoodsRetrieve obj where obj.disabled=:disabled and obj.type=:type",
                        params, -1, -1);
                List<GoodsRetrieve> goodsRetrieveList2 = new ArrayList<GoodsRetrieve>();
                for (GoodsRetrieve goodsRetrieve : goodsRetrieveList) {
                    if (goodsRetrieve.getGoodsRetrievePropertieList() != null
                        && goodsRetrieve.getGoodsRetrievePropertieList().size() > 0) {
                        List<GoodsRetrieveProperty> goodsRetrievePropertieList = new ArrayList<GoodsRetrieveProperty>();
                        for (GoodsRetrieveProperty goodsRetrieveProperty : goodsRetrieve
                            .getGoodsRetrievePropertieList()) {
                            if (goodsRetrieveProperty.getStore().getId() == CommUtil
                                .null2Long(store_id)) {
                                goodsRetrievePropertieList.add(goodsRetrieveProperty);
                            }
                        }
                        goodsRetrieve.setGoodsRetrievePropertieList(goodsRetrievePropertieList);
                    }
                    if (goodsRetrieve.getGoodsRetrievePropertieList() != null
                        && goodsRetrieve.getGoodsRetrievePropertieList().size() > 0) {
                        goodsRetrieveList2.add(goodsRetrieve);
                    }

                }
                mv.addObject("goodsRetrieveList2", goodsRetrieveList2);

                //通过检索属性筛选           
                List<GoodsRetrieveProperty> goodsRetrievePropertieList = new ArrayList<GoodsRetrieveProperty>();
                if (retrieve != null && !retrieve.equals("")) {
                    String[] retrieveIds = retrieve.split(",");
                    int num = 0;
                    for (String retrieveProperty : retrieveIds) {
                        if (!retrieveProperty.equals("")) {
                            gqo.addQuery("CONCAT(';',obj.retrieval_ids)", new SysMap(
                                "retrieval_ids_" + num, "%;" + retrieveProperty + ";%"), "like");
                            GoodsRetrieveProperty goodsRetrieveProperty = goodsRetrievePropertyService
                                .getObjById(CommUtil.null2Long(retrieveProperty));
                            goodsRetrievePropertieList.add(goodsRetrieveProperty);
                            num++;
                        }
                    }

                }
                mv.addObject("retrieve", retrieve);
                mv.addObject("goodsRetrievePropertieList", goodsRetrievePropertieList);

                IPageList pList = this.goodsService.list(gqo);
                String url = this.configService.getSysConfig().getAddress();
                if ((url == null) || (url.equals(""))) {
                    url = CommUtil.getURL(request);
                }
                CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);

                mv.addObject("store_id", store_id);

                mv.addObject("ugc", ugc);
                mv.addObject("store", store);
                mv.addObject("keyword", keyword);
                mv.addObject("goodsViewTools", goodsViewTools);

                return mv;
            }
        } catch (Exception e) {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "请求参数错误");
            mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
            e.printStackTrace();
        }

        return mv;
    }

    @RequestMapping({ "/store/{store_id}.htm/mobile/store_search_list_ajax.htm" })
    public ModelAndView search_ajax(HttpServletRequest request, HttpServletResponse response,
                                    @PathVariable String store_id, String keyword,
                                    String currentPage, String orderBy, String orderType,
                                    String gc_id, String retrieve) {
        ModelAndView mv = new JModelAndView("default/h5/store_search_list_ajax.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        try {
            UserGoodsClass ugc = this.userGoodsClassService.getObjById(CommUtil.null2Long(gc_id));
            String template = "default";
            Store store = this.storeService.getObjById(CommUtil.null2Long(store_id));
            if (store != null) {
                if ((store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
                    template = store.getTemplate();
                }
                mv = new JModelAndView(template + "/h5/store_search_list_ajax.html",
                    this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
                    request, response);
                if (orderBy == null || "".equals(orderBy)) {
                    orderBy = "createtime";
                }
                if (orderType == null || "".equals(orderType)) {
                    orderType = "desc";
                }
                GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
                gqo.addQuery("obj.goods_store.id", new SysMap("goods_store_id", store.getId()), "=");
                if (ugc != null) {
                    Set<Long> ids = genericUserGcIds(ugc);
                    List<UserGoodsClass> ugc_list = new ArrayList<UserGoodsClass>();
                    for (Long g_id : ids) {
                        UserGoodsClass temp_ugc = this.userGoodsClassService.getObjById(g_id);
                        ugc_list.add(temp_ugc);
                    }
                    if (ugc_list != null && ugc_list.size() > 0) {
                        gqo.addQuery2("ugc", ugc, "obj.goods_ugcs", "member of");
                        for (int i = 0; i < ugc_list.size(); i++) {
                            if (i == ugc_list.size() - 1) {
                                gqo.addQuery("ugc" + i, ugc_list.get(i), "obj.goods_ugcs ) ",
                                    "member of", "or");
                            } else {
                                gqo.addQuery("ugc" + i, ugc_list.get(i), "obj.goods_ugcs",
                                    "member of", "or");
                            }
                        }

                    } else {
                        gqo.addQuery("ugc", ugc, "obj.goods_ugcs", "member of");
                    }
                } else {
                    ugc = new UserGoodsClass();
                    ugc.setClassName("全部商品");
                    mv.addObject("ugc", ugc);
                }
                if (keyword != null && !"".equals(keyword)) {
                    gqo.addQuery("obj.goods_name", new SysMap("goods_name", "%" + keyword + "%"),
                        "like");
                }

                //通过检索属性筛选           
                List<GoodsRetrieveProperty> goodsRetrievePropertieList = new ArrayList<GoodsRetrieveProperty>();
                if (retrieve != null && !retrieve.equals("")) {
                    String[] retrieveIds = retrieve.split(",");
                    int num = 0;
                    for (String retrieveProperty : retrieveIds) {
                        if (!retrieveProperty.equals("")) {
                            gqo.addQuery("CONCAT(';',obj.retrieval_ids)", new SysMap(
                                "retrieval_ids_" + num, "%;" + retrieveProperty + ";%"), "like");
                            GoodsRetrieveProperty goodsRetrieveProperty = goodsRetrievePropertyService
                                .getObjById(CommUtil.null2Long(retrieveProperty));
                            goodsRetrievePropertieList.add(goodsRetrieveProperty);
                            num++;
                        }
                    }

                }
                mv.addObject("retrieve", retrieve);
                mv.addObject("goodsRetrievePropertieList", goodsRetrievePropertieList);

                gqo.setPageSize(Integer.valueOf(12));

                gqo.addQuery("obj.disabled", new SysMap("disabled", false), "=");
                gqo.addQuery("obj.goods_status", new SysMap("goods_status", 0), "=");

                IPageList pList = this.goodsService.nolastlist(gqo);
                String url = this.configService.getSysConfig().getAddress();
                if ((url == null) || (url.equals(""))) {
                    url = CommUtil.getURL(request);
                }
                CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
                mv.addObject("ugc", ugc);
                mv.addObject("store", store);
                mv.addObject("keyword", keyword);
                mv.addObject("goodsViewTools", goodsViewTools);
                return mv;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    //加载多规格的货品
    @RequestMapping({ "/store/{storeId}.htm/mobile/store_goods_cart_ajax.htm",
            "/mobile/store_goods_cart_ajax.htm" })
    public ModelAndView store_goods_cart_ajax(HttpServletRequest request,
                                              HttpServletResponse response, String id) {
        Goods obj = this.goodsService.getObjById(Long.valueOf(Long.parseLong(id)));
        //已上架
        ModelAndView mv = new JModelAndView("default/h5/store_search_list_item.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);

        mv.addObject("obj", obj);
        List<GoodsSpecification> spec = goodsViewTools.generic_spec(id);
        mv.addObject("specs", spec);
        List<Long> firstlist = new ArrayList<Long>();
        //两级规格
        if (spec != null && spec.size() == 2) {
            for (int i = 0; i < obj.getGoods_specs().size(); i++) {
                GoodsSpecProperty gsp = obj.getGoods_specs().get(i);
                if (gsp.getSpec().getId() == spec.get(0).getId()) {
                    firstlist.add(gsp.getId());
                }
            }
        }
        mv.addObject("firstlist", firstlist);
        mv.addObject("goodsViewTools", goodsViewTools);

        return mv;

    }

}
