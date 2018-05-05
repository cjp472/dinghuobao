package com.javamalls.front.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
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
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.GoodsLabel;
import com.javamalls.platform.domain.GoodslabelGoods;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.StoreClass;
import com.javamalls.platform.domain.StoreNavigation;
import com.javamalls.platform.domain.StorePartner;
import com.javamalls.platform.domain.StoreSlide;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.UserGoodsClass;
import com.javamalls.platform.domain.UserStoreRelation;
import com.javamalls.platform.domain.query.EvaluateQueryObject;
import com.javamalls.platform.domain.query.GoodsQueryObject;
import com.javamalls.platform.domain.query.StoreQueryObject;
import com.javamalls.platform.service.IEvaluateService;
import com.javamalls.platform.service.IGoodsLabelService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IGoodslabelGoodsService;
import com.javamalls.platform.service.IStoreClassService;
import com.javamalls.platform.service.IStoreNavigationService;
import com.javamalls.platform.service.IStorePartnerService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserGoodsClassService;
import com.javamalls.platform.service.IUserStoreRelationService;

/**店铺
 * 
 *                       
 * @Filename: StoreViewAction.java
 * @Version: 2.7.0
 * @Author: 桑森林
 * @Email: sangslmail@163.com
 *
 */
@Controller
public class StoreViewAction {
	private static final Logger logger = Logger.getLogger(StoreViewAction.class);
    @Autowired
    private ISysConfigService         configService;
    @Autowired
    private IUserConfigService        userConfigService;
    @Autowired
    private IStoreService             storeService;
    @Autowired
    private IStoreClassService        storeClassService;
    @Autowired
    private IGoodsService             goodsService;
    @Autowired
    private IUserGoodsClassService    userGoodsClassService;
    @Autowired
    private IStoreNavigationService   storenavigationService;
    @Autowired
    private IStorePartnerService      storepartnerService;
    @Autowired
    private IEvaluateService          evaluateService;
    @Autowired
    private AreaViewTools             areaViewTools;
    @Autowired
    private GoodsViewTools            goodsViewTools;
    @Autowired
    private StoreViewTools            storeViewTools;
    @Autowired
    private IUserStoreRelationService userStoreRelationService;
    @Autowired
    private IGoodsLabelService 		  goodsLabelService;
    @Autowired
    private IGoodslabelGoodsService 		  goodsLabelGoodsService;

    /**
     * 平台首页||店铺列表
     */
    @RequestMapping(value = { "/store/{id}.htm/frontindex.htm" })
    public ModelAndView Index(HttpServletRequest request, HttpServletResponse response,
                              @PathVariable String id, String currentPage, String orderBy,
                              String orderType, String store_name, String copyright_info) {

        ModelAndView mv = new JModelAndView("buyer/searchstorelist.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
            JModelAndView.SHOP_PATH, request, response);
        //店铺列表
        GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);

        gqo.addQuery("obj.disabled", new SysMap("disabled", false), "=");
        gqo.addQuery("obj.store_status", new SysMap("store_status", 2), "=");
        /*gqo.addQuery("obj.copyright_info", new SysMap("copyright_info", copyright_info), "=");
        gqo.addQuery("obj.store_name", new SysMap("store_name", store_name), "like");*/
        if (store_name != null && store_name != "") {
            gqo.addQuery("obj.store_name like '%" + store_name + "%'", null);
        }
        if (copyright_info != null && copyright_info != "") {
            gqo.addQuery("obj.copyright_info like '%" + copyright_info + "%'", null);
        }

        gqo.setPageSize(Integer.valueOf(20));
        IPageList pList = this.storeService.list(gqo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        mv.addObject("id", id);
        mv.addObject("store_name", store_name);
        mv.addObject("copyright_info", copyright_info);

        //如果用户登录就将用户id传到前台
        User user = SecurityUserHolder.getCurrentUser();
        if (user == null) {
            mv.addObject("user_id", 0);
        } else {
            mv.addObject("user_id", user.getId());
        }

        return mv;
    }

    /**
     * 店铺首页||供应商首页||新订货宝首页
     * @param request
     * @param response
     * @param id
     * @return
     */
    @RequestMapping(value = { "/store/{id}.htm", "/store/{id}.htm/index.htm", "/store/{id}.htm/" })
    public ModelAndView store(HttpServletRequest request, HttpServletResponse response,
                              @PathVariable String id) {
    	 User user = SecurityUserHolder.getCurrentUser();
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
            ModelAndView mv = new JModelAndView("storeclose.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
                request, response);
            mv.addObject("msg", "");
            return mv;
        }

        /*
         * 判断是否开启了限制关闭
         */
        int logon_access_state = store.getLogon_access_state();
        if (logon_access_state == 1) {
            //开启了，限制登录后访问
            if (SecurityUserHolder.getCurrentUser() == null) {
                ModelAndView mv = new JModelAndView("buyer/buyer_login.html",
                    this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
                    JModelAndView.SHOP_PATH, request, response);
                mv.addObject("store", store);
                return mv;
            }
        }

        String template = "default";
        if ((store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
            template = store.getTemplate();
        }
        ModelAndView mv = new JModelAndView(template + "/store_index.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        /*
         * 店铺商品的所有标签
         */
        Map<String , Object> mapGoodslabel=new HashMap<String, Object>();
        mapGoodslabel.put("store_id", CommUtil.null2Long(id));
        List<GoodsLabel> listGoodsLabel=goodsLabelService.query(
        		"select obj from GoodsLabel obj where obj.disabled=0 and obj.store.id=:store_id "
        		+" and obj.status=0  order by obj.sequence ASC  ", mapGoodslabel, -1, -1);
        
        
        List<GoodslabelGoods> listGoodslabelGoods= new ArrayList<GoodslabelGoods>();
        
        Map<String , Object> mapGoods= new HashMap<String, Object>();
    	mapGoods.put("store_id", CommUtil.null2Long(id));
    	
        for (GoodsLabel goodsLabel : listGoodsLabel) {
        	//if(user!=null && !user.equals("")){
        		 mapGoods.put("goodsLabel_id", goodsLabel.getId());
    			 listGoodslabelGoods=goodsLabelGoodsService.query(
    					"select obj.goods from GoodslabelGoods obj where obj.disabled=0 and obj.goodslabel.id=:goodsLabel_id "
    					+ "and obj.goods.goods_store.id=:store_id and obj.goods.disabled=0 and obj.goods.goods_status =0 order by obj.createtime desc ", mapGoods, 0, 8);
    			 goodsLabel.setGoodslabelGoodsList(listGoodslabelGoods);
        	//}
		}
       
        mv.addObject("listGoodsLabel", listGoodsLabel);
        
        /*
         * 判断店铺是否存在供采关系
         */
       
        if (user != null) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("user_id", user.getId());
            map.put("store_id", CommUtil.null2Long(id));
            map.put("status", 2);
            /*map.put("copyright_info", copyright_info);
            map.put("store_name", store_name);*/
            String sql = "select obj from UserStoreRelation obj where obj.user.id=:user_id and obj.store.id=:store_id and obj.status=:status ";
            List<UserStoreRelation> userStoreRelationList = this.userStoreRelationService.query(
                sql, map, -1, -1);
            if (userStoreRelationList != null && userStoreRelationList.size() == 1) {
                mv.addObject("userStoreRelation", userStoreRelationList.get(0));
            }
        }

        if (store.getStore_status() == 2) {
            add_store_common_info(mv, store);
            mv.addObject("store", store);
            mv.addObject("nav_id", "store_index");

            //查询动态导航
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("store_id", store.getId());
            params.put("display", Boolean.valueOf(true));
            List<StoreNavigation> navs = this.storenavigationService
                .query(
                    "select obj from StoreNavigation obj where obj.store.id=:store_id and obj.display=:display order by obj.sequence asc",
                    params, -1, -1);
            mv.addObject("navs", navs);

        } else {

            mv = new JModelAndView("storeclose.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            String msg = "";
            if (store.getStore_status() == 1 || store.getStore_status() == -1) {
                msg = "系统正在升级";
            }
            if (store.getStore_status() == 3) {
                msg = "系统已经关闭";
            }
            if (store.getStore_status() == 5) {
                msg = "系统已经关闭";
            }
            mv.addObject("msg", msg);
        }
        //    generic_evaluate(store, mv);

        List<StoreSlide> list = new ArrayList<StoreSlide>();
        List<StoreSlide> slides = store.getSlides();
        if (slides != null && slides.size() > 0) {
            for (StoreSlide storeSlide : slides) {
                if (storeSlide.getType() == null || storeSlide.getType() == 0) {
                    list.add(storeSlide);
                }
            }

        }
        mv.addObject("slides", list);
        mv.addObject("goodsViewTools", this.goodsViewTools);
        return mv;
    }

    @RequestMapping({ "/store_left.htm" })
    public ModelAndView store_left(HttpServletRequest request, HttpServletResponse response) {
        Store store = this.storeService.getObjById(CommUtil.null2Long(request.getAttribute("id")));
        String template = "default";
        if ((store != null) && (store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
            template = store.getTemplate();
        }
        ModelAndView mv = new JModelAndView(template + "/store_left.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        mv.addObject("store", store);
        add_store_common_info(mv, store);
        generic_evaluate(store, mv);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("store_id", store.getId());
        List<StorePartner> partners = this.storepartnerService
            .query(
                "select obj from StorePartner obj where obj.store.id=:store_id order by obj.sequence asc",
                params, -1, -1);
        mv.addObject("partners", partners);
        mv.addObject("goodsViewTools", this.goodsViewTools);
        return mv;
    }

    @RequestMapping({ "/store_left1.htm" })
    public ModelAndView store_left1(HttpServletRequest request, HttpServletResponse response) {
        Store store = this.storeService.getObjById(CommUtil.null2Long(request.getAttribute("id")));
        String template = "default";
        if ((store != null) && (store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
            template = store.getTemplate();
        }
        ModelAndView mv = new JModelAndView(template + "/store_left1.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        mv.addObject("store", store);
        add_store_common_info(mv, store);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("store_id", store.getId());
        List<StorePartner> partners = this.storepartnerService
            .query(
                "select obj from StorePartner obj where obj.store.id=:store_id order by obj.sequence asc",
                params, -1, -1);
        mv.addObject("partners", partners);
        return mv;
    }

    @RequestMapping({ "/store_left2.htm" })
    public ModelAndView store_left2(HttpServletRequest request, HttpServletResponse response) {
        Store store = this.storeService.getObjById(CommUtil.null2Long(request.getAttribute("id")));
        String template = "default";
        if ((store != null) && (store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
            template = store.getTemplate();
        }
        ModelAndView mv = new JModelAndView(template + "/store_left2.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        mv.addObject("store", store);
        add_store_common_info(mv, store);
        return mv;
    }

    @RequestMapping({ "/store/{storeId}.htm/store_nav.htm", "/store_nav.htm" })
    public ModelAndView store_nav(HttpServletRequest request, HttpServletResponse response,
                                  @PathVariable String storeId) {
        Long id = CommUtil.null2Long(storeId);
        Store store = this.storeService.getObjById(id);
        String template = "default";
        if ((store != null) && (store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
            template = store.getTemplate();
        }
        ModelAndView mv = new JModelAndView(template + "/store_nav.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        if (store == null) {
            return mv;
        }
        if (store.getStore_status() == 2) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("store_id", store.getId());
            params.put("display", Boolean.valueOf(true));
            List<StoreNavigation> navs = this.storenavigationService
                .query(
                    "select obj from StoreNavigation obj where obj.store.id=:store_id and obj.display=:display order by obj.sequence asc",
                    params, -1, -1);
            mv.addObject("navs", navs);
            //    mv.addObject("store", store);
            /*   String goods_view = CommUtil.null2String(request.getAttribute("goods_view"));
               mv.addObject("goods_view", Boolean.valueOf(CommUtil.null2Boolean(goods_view)));
               mv.addObject("goods_id", CommUtil.null2String(request.getAttribute("goods_id")));*/
            /*  mv.addObject("goods_list",
                  Boolean.valueOf(CommUtil.null2Boolean(request.getAttribute("goods_list"))));*/
        }
        return mv;
    }

    @RequestMapping({ "/store_credit.htm" })
    public ModelAndView store_credit(HttpServletRequest request, HttpServletResponse response,
                                     String id) {
        Store store = this.storeService.getObjById(CommUtil.null2Long(id));
        String template = "default";
        if ((store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
            template = store.getTemplate();
        }
        ModelAndView mv = new JModelAndView(template + "/store_credit.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        if (store.getStore_status() == 2) {
            EvaluateQueryObject qo = new EvaluateQueryObject("1", mv, "createtime", "desc");
            qo.addQuery("obj.of.store.id", new SysMap("store_id", store.getId()), "=");
            IPageList pList = this.evaluateService.list(qo);
            CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + "/store_eva.htm", "",
                "", pList, mv);
            mv.addObject("store", store);
            mv.addObject("nav_id", "store_credit");
            mv.addObject("storeViewTools", this.storeViewTools);
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "店铺信息错误");
            mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
        }
        return mv;
    }

    @RequestMapping({ "/store_eva.htm" })
    public ModelAndView store_eva(HttpServletRequest request, HttpServletResponse response,
                                  String id, String currentPage, String eva_val) {
        Store store = this.storeService.getObjById(Long.valueOf(Long.parseLong(id)));
        String template = "default";
        if ((store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
            template = store.getTemplate();
        }
        ModelAndView mv = new JModelAndView(template + "/store_eva.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        if (store.getStore_status() == 2) {
            EvaluateQueryObject qo = new EvaluateQueryObject(currentPage, mv, "createtime", "desc");
            qo.addQuery("obj.evaluate_goods.goods_store.id", new SysMap("store_id", store.getId()),
                "=");
            if (!CommUtil.null2String(eva_val).equals("")) {
                qo.addQuery("obj.evaluate_buyer_val",
                    new SysMap("evaluate_buyer_val", Integer.valueOf(CommUtil.null2Int(eva_val))),
                    "=");
            }
            IPageList pList = this.evaluateService.list(qo);
            CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + "/store_eva.htm", "",
                "&eva_val=" + CommUtil.null2String(eva_val), pList, mv);
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "店铺信息错误");
            mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
        }
        return mv;
    }

    @RequestMapping({ "/store/{storeId}.htm/store_detail.htm" })
    public ModelAndView store_info(HttpServletRequest request, HttpServletResponse response,
                                   @PathVariable String storeId) {
        Store store = this.storeService.getObjById(Long.valueOf(Long.parseLong(storeId)));
        String template = "default";
        if ((store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
            template = store.getTemplate();
        }
        ModelAndView mv = new JModelAndView(template + "/store_info.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        if (store.getStore_status() == 2) {
            mv.addObject("store", store);
            mv.addObject("nav_id", "store_info");
            mv.addObject("areaViewTools", this.areaViewTools);
        } else {
            mv = new JModelAndView("buyer_error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "店铺信息错误");
            mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
        }
        return mv;
    }

    @RequestMapping({ "/store_url.htm" })
    public ModelAndView store_url(HttpServletRequest request, HttpServletResponse response,
                                  String id) {
        StoreNavigation nav = this.storenavigationService.getObjById(CommUtil.null2Long(id));
        String template = "default";
        if ((nav.getStore().getTemplate() != null) && (!nav.getStore().getTemplate().equals(""))) {
            template = nav.getStore().getTemplate();
        }
        ModelAndView mv = new JModelAndView(template + "/store_url.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        mv.addObject("store", nav.getStore());
        mv.addObject("nav", nav);
        mv.addObject("nav_id", nav.getId());
        return mv;
    }

    private void add_store_common_info(ModelAndView mv, Store store) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", store.getUser().getId());
        params.put("display", Boolean.valueOf(true));
        List<UserGoodsClass> ugcs = this.userGoodsClassService
            .query(
                "select obj from UserGoodsClass obj where obj.user.id=:user_id and  obj.disabled=false and obj.display=:display and obj.parent.id is null order by obj.sequence asc",
                params, -1, -1);
        mv.addObject("ugcs", ugcs);
        params.clear();
        params.put("recommend", Boolean.valueOf(true));
        params.put("goods_store_id", store.getId());
        params.put("goods_status", Integer.valueOf(0));
        List<Goods> goods_recommend = this.goodsService
            .query(
                "select obj from Goods obj where obj.disabled=false and obj.goods_recommend=:recommend and obj.goods_store.id=:goods_store_id and obj.goods_status=:goods_status order by obj.createtime desc",
                params, 0, 8);
        params.clear();
        params.put("goods_store_id", store.getId());
        params.put("goods_news_status", Integer.valueOf(1));
        params.put("goods_status", Integer.valueOf(0));
        List<Goods> goods_new = this.goodsService
            .query(
                "select obj from Goods obj where obj.disabled=false and obj.goods_store.id=:goods_store_id and obj.goods_news_status=:goods_news_status and obj.goods_status=:goods_status order by obj.createtime desc ",
                params, 0, 8);

        params.clear();
        params.put("goods_store_id", store.getId());
        params.put("goods_hot_status", Integer.valueOf(1));
        params.put("goods_status", Integer.valueOf(0));
        List<Goods> goods_hot = this.goodsService
            .query(
                "select obj from Goods obj where obj.disabled=false and obj.goods_store.id=:goods_store_id and obj.goods_hot_status=:goods_hot_status and obj.goods_status=:goods_status order by obj.createtime desc ",
                params, 0, 8);
        mv.addObject("goods_recommend", goods_recommend);
        mv.addObject("goods_new", goods_new);
        mv.addObject("goods_hot", goods_hot);
        mv.addObject("goodsViewTools", this.goodsViewTools);
        mv.addObject("storeViewTools", this.storeViewTools);
        mv.addObject("areaViewTools", this.areaViewTools);
    }

    @RequestMapping({ "/store_type.htm" })
    public ModelAndView store_list(HttpServletRequest request, HttpServletResponse response,
                                   String id, String sc_id, String currentPage, String orderType,
                                   String store_name, String store_ower, String type) {
        ModelAndView mv = new JModelAndView("store_list.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        List<StoreClass> scs = this.storeClassService.query(
            "select obj from StoreClass obj where obj.parent.id is null order by obj.sequence asc",
            null, -1, -1);
        mv.addObject("scs", scs);
        StoreQueryObject sqo = new StoreQueryObject(currentPage, mv, "store_credit", orderType);
        if ((sc_id != null) && (!sc_id.equals(""))) {
            sqo.addQuery("obj.sc.id", new SysMap("sc_id", CommUtil.null2Long(sc_id)), "=");
        }
        if ((store_name != null) && (!store_name.equals(""))) {
            sqo.addQuery("obj.store_name", new SysMap("store_name", "%" + store_name + "%"), "like");
            mv.addObject("store_name", store_name);
        }
        if ((store_ower != null) && (!store_ower.equals(""))) {
            sqo.addQuery("obj.store_ower", new SysMap("store_ower", "%" + store_ower + "%"), "like");
            mv.addObject("store_ower", store_ower);
        }
        sqo.addQuery("obj.store_status", new SysMap("store_status", Integer.valueOf(2)), "=");
        IPageList pList = this.storeService.list(sqo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        mv.addObject("storeViewTools", this.storeViewTools);
        mv.addObject("type", type);
        return mv;
    }

    @RequestMapping({ "/store/{storeId}.htm/store_goods_search.htm" })
    public ModelAndView store_goods_search(HttpServletRequest request,
                                           HttpServletResponse response, String keyword,
                                           String store_id, String currentPage,
                                           @PathVariable String storeId) {
        Store store = this.storeService.getObjById(Long.valueOf(Long.parseLong(storeId)));
        String template = "default";
        if ((store.getTemplate() != null) && (!store.getTemplate().equals(""))) {
            template = store.getTemplate();
        }
        ModelAndView mv = new JModelAndView(template + "/store_goods_search.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, null, null);
        gqo.addQuery("obj.goods_store.id", new SysMap("store_id", CommUtil.null2Long(storeId)), "=");
        gqo.addQuery("obj.goods_name", new SysMap("goods_name", "%" + keyword + "%"), "like");
        gqo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(0)), "=");
        gqo.setPageSize(Integer.valueOf(20));
        gqo.addQuery("obj.disabled", new SysMap("disabled", false), "=");

        IPageList pList = this.goodsService.list(gqo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        mv.addObject("keyword", keyword);
        mv.addObject("store", store);
        return mv;
    }

    @RequestMapping({ "/store/{storeId}.htm/store_head.htm", "/store_head.htm" })
    public ModelAndView store_head(HttpServletRequest request, HttpServletResponse response,
                                   @PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("store_head.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        Store store = this.storeService.getObjById(CommUtil.null2Long(storeId));
        generic_evaluate(store, mv);
        mv.addObject("store", store);
        mv.addObject("storeViewTools", this.storeViewTools);
        return mv;
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

}
