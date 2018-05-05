package com.javamalls.front.web.h5.action;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nutz.json.Json;
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
import com.javamalls.base.tools.Md5Encrypt;
import com.javamalls.ctrl.admin.tools.MsgTools;
import com.javamalls.front.web.tools.GoodsViewTools;
import com.javamalls.front.web.tools.NavViewTools;
import com.javamalls.front.web.tools.StoreViewTools;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.GoodsCart;
import com.javamalls.platform.domain.GoodsClass;
import com.javamalls.platform.domain.GoodsLabel;
import com.javamalls.platform.domain.GoodslabelGoods;
import com.javamalls.platform.domain.HomepageNavigation;
import com.javamalls.platform.domain.MobileGoods;
import com.javamalls.platform.domain.OrderForm;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.StoreCart;
import com.javamalls.platform.domain.StoreSlide;
import com.javamalls.platform.domain.SysConfig;
import com.javamalls.platform.domain.Track;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.UserGoodsClass;
import com.javamalls.platform.domain.UserStoreRelation;
import com.javamalls.platform.domain.query.MobileGoodsQueryObject;
import com.javamalls.platform.domain.query.StoreQueryObject;
import com.javamalls.platform.domain.query.UserTrackQueryObject;
import com.javamalls.platform.domain.virtual.TransInfo;
import com.javamalls.platform.service.IGoodsCartService;
import com.javamalls.platform.service.IGoodsClassService;
import com.javamalls.platform.service.IGoodsLabelService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IGoodslabelGoodsService;
import com.javamalls.platform.service.IHomepageNavigationService;
import com.javamalls.platform.service.IMobileGoodsService;
import com.javamalls.platform.service.IOrderFormService;
import com.javamalls.platform.service.IStoreCartService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.ITrackService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserGoodsClassService;
import com.javamalls.platform.service.IUserService;
import com.javamalls.platform.service.IUserStoreRelationService;

/**加载首页
 *                       
 * @Filename: IndexViewAction.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class H5IndexViewAction {
    @Autowired
    private ISysConfigService         configService;
    @Autowired
    private IUserConfigService        userConfigService;
    @Autowired
    private IGoodsClassService        goodsClassService;
    @Autowired
    private IUserService              userService;
    @Autowired
    private IGoodsService             goodsService;

    @Autowired
    private IStoreCartService         storeCartService;
    @Autowired
    private IGoodsCartService         goodsCartService;
    @Autowired
    private NavViewTools              navTools;
    @Autowired
    private GoodsViewTools            goodsViewTools;
    @Autowired
    private StoreViewTools            storeViewTools;
    @Autowired
    private MsgTools                  msgTools;
    @Autowired
    private IMobileGoodsService       mobileGoodsService;
    @Autowired
    private IOrderFormService         orderFormService;
    @Autowired
    private ITrackService             trackServ;
    @Autowired
    private IStoreService             storeService;
    @Autowired
    private IUserGoodsClassService    userGoodsClassService;
    @Autowired
    private IUserStoreRelationService userStoreRelationService;
    @Autowired
    private IGoodsLabelService 		  goodsLabelService;
    @Autowired
    private IGoodslabelGoodsService 		  goodsLabelGoodsService;
    @Autowired
    private IHomepageNavigationService 		  homepageNavigationService;

    /**
     * 老版的搜索页面
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/mobile/search.htm" })
    public ModelAndView head(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("h5/commons/_search.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        mv.addObject("goods", "goods");
        return mv;
    }

    /**
     * 搜索页面
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/mobile/showsearch.htm" })
    public ModelAndView showsearch(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("h5/commons/_showsearch.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        //热门搜索hotkeyword
        String search = request.getParameter("search");
        mv.addObject("search", search);
        //历史记录
        mv.addObject("goods", "goods");
        return mv;
    }

    /**
     * 订货宝h5
     * @param request
     * @param response
     * @param currentPage
     * @return
     */
    @RequestMapping({ "/store/{id}.htm/mobile/index.htm" })
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response,
                              String currentPage, @PathVariable String id) {
        request.getSession(false).setAttribute(Constent.WEB_TYPE_KEY, "mobile");
        SysConfig config = this.configService.getSysConfig();
        ModelAndView mv = new JModelAndView("h5/index.html", config,
            this.userConfigService.getUserConfig(), 1, request, response);
        User user = SecurityUserHolder.getCurrentUser();
        /*
         * 店铺商品的所有标签
         */
        Map<String , Object> mapObj=new HashMap<String, Object>();
        mapObj.put("store_id", CommUtil.null2Long(id));
        List<GoodsLabel> listGoodsLabel=goodsLabelService.query(
        		"select obj from GoodsLabel obj where obj.disabled=0 and obj.store.id=:store_id "
        		+" and obj.status=0  order by obj.sequence ASC  ", mapObj, -1, -1);
        
        /*
         *店铺 与 商品 关联 
         */
        List<GoodslabelGoods> listGoodslabelGoods= new ArrayList<GoodslabelGoods>();
        
        Map<String , Object> mapGoods= new HashMap<String, Object>();
    	mapGoods.put("store_id", CommUtil.null2Long(id));
    	
        for (GoodsLabel goodsLabel : listGoodsLabel) {
        	//if(user!=null && !user.equals("")){
        		 mapGoods.put("goodsLabel_id", goodsLabel.getId());
    			 listGoodslabelGoods=goodsLabelGoodsService.query(
    					"select obj.goods from GoodslabelGoods obj where obj.disabled=0 and obj.goodslabel.id=:goodsLabel_id "
    					+ "and obj.goods.goods_store.id=:store_id and obj.goods.disabled=0 and obj.goods.goods_status=0 order by obj.createtime desc ", mapGoods, 0, 6);
    			 goodsLabel.setGoodslabelGoodsList(listGoodslabelGoods);
        	//}
		}
       
        mv.addObject("listGoodsLabel", listGoodsLabel);
        //首页导航
        List<HomepageNavigation> listHomepageNavigation=homepageNavigationService.query(
        		"select obj from HomepageNavigation obj where obj.disabled=0 and obj.store.id=:store_id "
        		+"  order by obj.createtime ASC  ", mapObj, -1, -1);
        mv.addObject("listHomepageNavigation", listHomepageNavigation);
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
            mv = new JModelAndView("storeclose.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("msg", "");
            return mv;
        }

        //如果传递过来的参数有客服代码，则存储在sesson里
        String cus_ser_code = request.getParameter(Constent.CUS_SER_CODE);
        if (CommUtil.isNotNull(cus_ser_code)) {
            request.getSession().setAttribute(Constent.CUS_SER_CODE, cus_ser_code);
        }

        // * 判断是否开启了限制关闭
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

            List<StoreSlide> list = new ArrayList<StoreSlide>();
            List<StoreSlide> slides = store.getSlides();
            if (slides != null && slides.size() > 0) {
                for (StoreSlide storeSlide : slides) {
                    if (storeSlide.getType() != null && storeSlide.getType() == 1) {
                        list.add(storeSlide);
                    }
                }

            }
            mv.addObject("slides", list);
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
        mv.addObject("goodsViewTools", this.goodsViewTools);
        return mv;
    }

    /**
     * 平台首页||店铺列表
     */
    @RequestMapping(value = { "/store/{id}.htm/mobile/frontindex.htm" })
    public ModelAndView Index(HttpServletRequest request, HttpServletResponse response,
                              @PathVariable String id, String currentPage, String orderBy,
                              String orderType, String keyword) {

        ModelAndView mv = new JModelAndView("h5/frontindex.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
            JModelAndView.SHOP_PATH, request, response);
        mv.addObject("id", id);
        mv.addObject("keyword", keyword);
        // mv.addObject("copyright_info", copyright_info);

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
     * 平台首页||店铺列表  分页
     * @param request
     * @param response
     * @param currentPage
     * @param orderBy
     * @param orderType
     * @param store_name
     * @return
     */
    @RequestMapping({ "/mobile/frontindex_store_ajax.htm" })
    public ModelAndView frontindex_store_ajax(HttpServletRequest request,
                                              HttpServletResponse response, String currentPage,
                                              String orderBy, String orderType, String keyword) {
        ModelAndView mv = new JModelAndView("h5/frontindex_store_ajax.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        //店铺列表
        StoreQueryObject gqo = new StoreQueryObject(currentPage, mv, orderBy, orderType);
        gqo.setPageSize(6);
        gqo.addQuery("obj.disabled", new SysMap("disabled", false), "=");
        gqo.addQuery("obj.store_status", new SysMap("store_status", 2), "=");
        /*gqo.addQuery("obj.copyright_info", new SysMap("copyright_info", copyright_info), "=");
        gqo.addQuery("obj.store_name", new SysMap("store_name", store_name), "like");*/
        if (keyword != null && keyword != "") {
            gqo.addQuery("obj.store_name like '%" + keyword + "%'", null);
            gqo.addQuery("or obj.user.companyInfo.company_name like '%" + keyword + "%'", null);
        }
        /*if (copyright_info!=null && copyright_info!="") {
             gqo.addQuery("obj.copyright_info like '%"+copyright_info+"%'", null);
         }   */

        IPageList pList = this.storeService.noLastList(gqo);
        mv.addObject("objs", pList.getResult());
        return mv;
    }

    /**
     * 店铺搜索页面
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/store/{id}.htm/mobile/frontstore_search.htm" })
    public ModelAndView frontshowsearch(HttpServletRequest request, HttpServletResponse response,
                                        @PathVariable String id, String keyword) {
        ModelAndView mv = new JModelAndView("default/h5/frontstore_search.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        try {
            Store store = this.storeService.getObjById(CommUtil.null2Long(id));
            if (store == null) {
                mv = new JModelAndView("/h5/error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "不存在该店铺信息");
                mv.addObject("url", CommUtil.getURL(request) + "/mobile/frontindex.htm");
                return mv;
            }
            mv.addObject("storeId", id);
            mv.addObject("keyword", keyword);
        } catch (Exception e) {
            mv = new JModelAndView("/h5/error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "不存在该店铺信息");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/frontindex.htm");
            e.printStackTrace();
        }
        return mv;
    }

    private void add_store_common_info(ModelAndView mv, Store store) {
        Map<String, Object> params = new HashMap<String, Object>();

        params.clear();
        params.put("recommend", Boolean.valueOf(true));
        params.put("goods_store_id", store.getId());
        params.put("goods_status", Integer.valueOf(0));
        List<Goods> goods_recommend = this.goodsService
            .query(
                "select obj from Goods obj where obj.disabled=false and obj.goods_recommend=:recommend and obj.goods_store.id=:goods_store_id and obj.goods_status=:goods_status order by obj.createtime desc",
                params, 0, 6);
        params.clear();
        params.put("goods_store_id", store.getId());
        params.put("goods_news_status", Integer.valueOf(1));
        params.put("goods_status", Integer.valueOf(0));
        List<Goods> goods_new = this.goodsService
            .query(
                "select obj from Goods obj where obj.disabled=false and obj.goods_store.id=:goods_store_id and obj.goods_news_status=:goods_news_status and obj.goods_status=:goods_status order by obj.createtime desc ",
                params, 0, 6);

        params.clear();
        params.put("goods_store_id", store.getId());
        params.put("goods_hot_status", Integer.valueOf(1));
        params.put("goods_status", Integer.valueOf(0));
        List<Goods> goods_hot = this.goodsService
            .query(
                "select obj from Goods obj where obj.disabled=false and obj.goods_store.id=:goods_store_id and obj.goods_hot_status=:goods_hot_status and obj.goods_status=:goods_status order by obj.createtime desc ",
                params, 0, 6);
        mv.addObject("goods_recommend", goods_recommend);
        mv.addObject("goods_new", goods_new);
        mv.addObject("goods_hot", goods_hot);
        mv.addObject("goodsViewTools", this.goodsViewTools);
        mv.addObject("storeViewTools", this.storeViewTools);
    }

    @RequestMapping({ "/mobile/index_goods.htm" })
    public ModelAndView advert_invoke(HttpServletRequest request, HttpServletResponse response,
                                      String currentPage) {
        ModelAndView mv = new JModelAndView("h5/index_goods.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        MobileGoodsQueryObject qo = new MobileGoodsQueryObject(currentPage, mv, "sort", "asc");
        qo.setPageSize(Constent.INDEX_GOODS_PAGE_SIZE);
        qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");
        IPageList<MobileGoods> pList = this.mobileGoodsService.noLastList(qo);
        String ids = null;//记录未上架商品的id
        List<Goods> list = new ArrayList<Goods>();
        if (pList != null && pList.getResult() != null) {
            for (MobileGoods mg : pList.getResult()) {
                Goods goods = mg.getGoods();
                if (goods.getGoods_status() == 0) {
                    list.add(mg.getGoods());
                } else {
                    ids += mg.getId() + ",";
                }
            }
        }
        if (ids != null) {//从h5首页商品中删除未上加的商品
            ids = ids.substring(0, ids.length() - 1);
            mobileGoodsService.remove(ids);
        }
        mv.addObject("objs", list);
        return mv;
    }

    @RequestMapping({ "/mobile/close.htm" })
    public ModelAndView close(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("close.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        return mv;
    }

    @RequestMapping({ "/mobile/404.htm" })
    public ModelAndView error404(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("h5/404.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        String jm_view_type = CommUtil.null2String(request.getSession(false) == null ? null
            : request.getSession(false).getAttribute(Constent.WEB_TYPE_KEY));
        if ((jm_view_type != null) && (!jm_view_type.equals("")) && (jm_view_type.equals("weixin"))) {
            String store_id = CommUtil.null2String(request.getSession(false) == null ? null
                : request.getSession(false).getAttribute("store_id"));
            mv = new JModelAndView("weixin/404.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/weixin/index.htm?store_id="
                                + store_id);
        }
        return mv;
    }

    @RequestMapping({ "/mobile/500.htm" })
    public ModelAndView error500(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("h5/500.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        String jm_view_type = CommUtil.null2String(request.getSession(false).getAttribute(
            Constent.WEB_TYPE_KEY));
        if ((jm_view_type != null) && (!jm_view_type.equals("")) && (jm_view_type.equals("weixin"))) {
            String store_id = CommUtil.null2String(request.getSession(false).getAttribute(
                "store_id"));
            mv = new JModelAndView("weixin/500.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/weixin/index.htm?store_id="
                                + store_id);
        }
        return mv;
    }

    @RequestMapping({ "/mobile/goods_class.htm" })
    public ModelAndView goods_class(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("goods_class.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("display", Boolean.valueOf(true));
        List<GoodsClass> gcs = this.goodsClassService
            .query(
                "select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
                params, -1, -1);
        mv.addObject("gcs", gcs);
        return mv;
    }

    @RequestMapping({ "/mobile/forget.htm" })
    public ModelAndView forget(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("forget.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        SysConfig config = this.configService.getSysConfig();
        if (!config.isEmailEnable()) {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "系统关闭邮件功能，不能找回密码");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
        }
        return mv;
    }

    @RequestMapping({ "/mobile/find_pws.htm" })
    public ModelAndView find_pws(HttpServletRequest request, HttpServletResponse response,
                                 String userName, String email, String code) {
        ModelAndView mv = new JModelAndView("h5/success.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        HttpSession session = request.getSession(false);
        String verify_number = (String) session.getAttribute("verify_number");
        if (code.toUpperCase().equals(verify_number)) {
            User user = this.userService.getObjByProperty("userName", userName);
            if (null != user && null != user.getEmail() && user.getEmail().equals(email.trim())) {
                String pws = CommUtil.randomString(6).toLowerCase();
                String subject = this.configService.getSysConfig().getTitle() + "密码找回邮件";
                String content = user.getUsername() + ",您好！您通过密码找回功能重置密码，新密码为：" + pws;
                boolean ret = this.msgTools.sendEmail(email, subject, content);
                if (ret) {
                    user.setPassword(Md5Encrypt.md5(pws));
                    this.userService.update(user);
                    mv.addObject("op_title", "新密码已经发送到邮箱:<font color=red>" + email
                                             + "</font>，请查收后重新登录");
                    mv.addObject("url", CommUtil.getURL(request) + "/mobile/user/login.htm");
                } else {
                    mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                        this.userConfigService.getUserConfig(), 1, request, response);
                    mv.addObject("op_title", "邮件发送失败，密码暂未执行重置");
                    mv.addObject("url", CommUtil.getURL(request) + "/mobile/forget.htm");
                }
            } else {
                mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "用户名、邮箱不匹配");
                mv.addObject("url", CommUtil.getURL(request) + "/mobile/forget.htm");
            }
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "验证码不正确");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/forget.htm");
        }
        return mv;
    }

    @RequestMapping({ "/mobile/switch_recommend_goods.htm" })
    public ModelAndView switch_recommend_goods(HttpServletRequest request,
                                               HttpServletResponse response,
                                               int recommend_goods_random) {
        ModelAndView mv = new JModelAndView("switch_recommend_goods.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("store_recommend", Boolean.valueOf(true));
        params.put("goods_status", Integer.valueOf(0));
        List<Goods> store_reommend_goods_list = this.goodsService
            .query(
                "select obj from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.store_recommend_time desc",
                params, -1, -1);
        List<Goods> store_reommend_goods = new ArrayList<Goods>();
        int begin = recommend_goods_random * 5;
        if (begin > store_reommend_goods_list.size() - 1) {
            begin = 0;
        }
        int max = begin + 5;
        if (max > store_reommend_goods_list.size()) {
            begin -= max - store_reommend_goods_list.size();
            max--;
        }
        for (int i = 0; i < store_reommend_goods_list.size(); i++) {
            if ((i >= begin) && (i < max)) {
                store_reommend_goods.add((Goods) store_reommend_goods_list.get(i));
            }
        }
        mv.addObject("store_reommend_goods", store_reommend_goods);
        return mv;
    }

    @RequestMapping({ "/mobile/outline.htm" })
    public ModelAndView outline(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("error.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        mv.addObject("op_title", "该用户在其他地点登录，您被迫下线！");
        mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
        return mv;
    }

    /**
     * 订货宝-商品分类
     * */

    @RequestMapping({ "/store/{storeId}.htm/mobile/goodsClass.htm" })
    public ModelAndView goodsClass(HttpServletRequest request, HttpServletResponse response,
                                   @PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("h5/good_fenlei.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        mv.addObject("navTools", this.navTools);
        Store store = this.storeService.getObjById(CommUtil.null2Long(storeId));
        if (store != null) {
            /*
               * 判断是否开启了限制关闭
               */
            int logon_access_state = store.getLogon_access_state();
            if (logon_access_state == 1) {
                //开启了，限制登录后访问
                if (SecurityUserHolder.getCurrentUser() == null) {
                    mv = new JModelAndView("h5/login.html", this.configService.getSysConfig(),
                        this.userConfigService.getUserConfig(), 1, request, response);
                    mv.addObject("storeId", storeId);
                    return mv;
                }
            }

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("user_id", store.getUser().getId());
            params.put("display", Boolean.valueOf(true));
            List<UserGoodsClass> ugcs = this.userGoodsClassService
                .query(
                    "select obj from UserGoodsClass obj where obj.disabled=false and obj.user.id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc",
                    params, -1, -1);
            mv.addObject("gcs", ugcs);
        }
        return mv;
    }

    @RequestMapping({ "/mobile/top.htm" })
    public ModelAndView mobileTop(HttpServletRequest request, HttpServletResponse response,
                                  String title, String url, String cla, String goods_id) {
        ModelAndView mv = new JModelAndView("h5/commons/_header.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);

        String clazz = "logo";
        if (!"".equals(CommUtil.null2String(cla)))
            clazz = cla;

        List<GoodsCart> list = new ArrayList<GoodsCart>();
        List<StoreCart> cart = new ArrayList<StoreCart>();
        List<StoreCart> user_cart = new ArrayList<StoreCart>();
        List<StoreCart> cookie_cart = new ArrayList<StoreCart>();
        User user = null;
        if (SecurityUserHolder.getCurrentUser() != null) {
            user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        }
        String cart_session_id = "";
        Map<String, Object> params = new HashMap<String, Object>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("cart_session_id")) {
                    cart_session_id = CommUtil.null2String(cookie.getValue());
                }
            }
        }
        if (user != null) {
            if (!cart_session_id.equals("")) {
                if (user.getStore() != null) {
                    params.clear();
                    params.put("cart_session_id", cart_session_id);
                    params.put("user_id", user.getId());
                    params.put("sc_status", Integer.valueOf(0));
                    params.put("store_id", user.getStore().getId());
                    List<StoreCart> store_cookie_cart = this.storeCartService
                        .query(
                            "select obj from StoreCart obj fetch all properties where (obj.cart_session_id=:cart_session_id or obj.user.id=:user_id) and obj.sc_status=:sc_status and obj.store.id=:store_id",
                            params, -1, -1);
                    for (StoreCart sc : store_cookie_cart) {
                        for (GoodsCart gc : ((StoreCart) sc).getGcs()) {
                            gc.getGsps().clear();
                            this.goodsCartService.delete(gc.getId());
                        }
                        this.storeCartService.delete(((StoreCart) sc).getId());
                    }
                }
                params.clear();
                params.put("cart_session_id", cart_session_id);
                params.put("sc_status", Integer.valueOf(0));
                cookie_cart = this.storeCartService
                    .query(
                        "select obj from StoreCart obj fetch all properties where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status",
                        params, -1, -1);

                params.clear();
                params.put("user_id", user.getId());
                params.put("sc_status", Integer.valueOf(0));
                user_cart = this.storeCartService
                    .query(
                        "select obj from StoreCart obj fetch all properties where obj.user.id=:user_id and obj.sc_status=:sc_status",
                        params, -1, -1);
            } else {
                params.clear();
                params.put("user_id", user.getId());
                params.put("sc_status", Integer.valueOf(0));
                user_cart = this.storeCartService
                    .query(
                        "select obj from StoreCart obj fetch all properties where obj.user.id=:user_id and obj.sc_status=:sc_status",
                        params, -1, -1);
            }
        } else if (!cart_session_id.equals("")) {
            params.clear();
            params.put("cart_session_id", cart_session_id);
            params.put("sc_status", Integer.valueOf(0));
            cookie_cart = this.storeCartService
                .query(
                    "select obj from StoreCart obj fetch all properties where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status",
                    params, -1, -1);
        }
        for (StoreCart sc : user_cart) {
            boolean sc_add = true;
            for (StoreCart sc1 : cart) {
                if (sc1.getStore().getId().equals(sc.getStore().getId())) {
                    sc_add = false;
                }
            }
            if (sc_add) {
                cart.add(sc);
            }
        }
        boolean sc_add;
        for (StoreCart sc : cookie_cart) {
            sc_add = true;
            for (StoreCart sc1 : cart) {
                if (sc1.getStore().getId().equals(sc.getStore().getId())) {
                    sc_add = false;
                    for (GoodsCart gc : sc.getGcs()) {
                        gc.setSc(sc1);
                        this.goodsCartService.update(gc);
                    }
                    this.storeCartService.delete(sc.getId());
                }
            }
            if (sc_add) {
                cart.add(sc);
            }
        }
        if (cart != null) {
            for (StoreCart sc : cart) {
                if (sc != null) {
                    list.addAll(sc.getGcs());
                }
            }
        }
        float total_price = 0.0F;
        for (GoodsCart gc : list) {
            Goods goods = this.goodsService.getObjById(gc.getGoods().getId());
            if (CommUtil.null2String(gc.getCart_type()).equals("combin")) {
                total_price = CommUtil.null2Float(goods.getCombin_price());
            } else if (CommUtil.null2String(gc.getCart_type()).equals("storeempty")) {
                total_price = CommUtil.null2Float(goods.getStoreempty_price());
            } else if (CommUtil.null2String(gc.getCart_type()).equals("dingjin")) {
                total_price = CommUtil.null2Float(goods.getDeposit_price());
            } else {
                total_price = CommUtil.null2Float(Double.valueOf(CommUtil.mul(
                    Integer.valueOf(gc.getCount()), goods.getGoods_current_price())))
                              + total_price;
            }
        }
        mv.addObject("total_price", Float.valueOf(total_price));
        mv.addObject("cart", list);

        mv.addObject("title", title);
        mv.addObject("url", url);
        mv.addObject("goods_id", CommUtil.null2String(goods_id));
        mv.addObject("clazz", clazz);
        return mv;
    }

    @RequestMapping(value = { "/mobile/footer.htm", "/store/{storeId}.htm/mobile/footer.htm" })
    public ModelAndView footer(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("h5/commons/_footer.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        return mv;
    }

    @RequestMapping({ "/store/{store_id}.htm/mobile/daohang.htm" })
    public ModelAndView daohang(HttpServletRequest request, HttpServletResponse response,
                                @PathVariable String store_id) {
        User user = SecurityUserHolder.getCurrentUser();
        ModelAndView mv = null;
        if (user != null) {
            mv = new JModelAndView("h5/commons/_usercenter.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
                request, response);
            Map<String , Object> mapObj=new HashMap<String, Object>();
            mapObj.put("store_id", CommUtil.null2Long(store_id));
            //首页导航
            List<HomepageNavigation> listHomepageNavigation=homepageNavigationService.query(
            		"select obj from HomepageNavigation obj where obj.disabled=0 and obj.store.id=:store_id "
            		+"  order by obj.createtime ASC  ", mapObj, -1, -1);
            mv.addObject("listHomepageNavigation", listHomepageNavigation);
            /*  UserTrackQueryObject qo = new UserTrackQueryObject("1", mv, "createtime", "desc");
              qo.setPageSize(6);
              qo.addQuery("obj.user.id", new SysMap("userid", user.getId()), "=");
              qo.setOrderBy("createtime");
              IPageList pList = this.trackServ.list(qo);*/
            mv.addObject("storeViewTools", storeViewTools);
            /* List<Track> tracks = pList.getResult();
             if (tracks != null && tracks.size() > 1) {//只显示双数
                 if (tracks.size() % 2 != 0)
                     tracks.remove(tracks.size() - 1);
                 mv.addObject("objs", tracks);
             }*/
        } else {
            mv = new JModelAndView("h5/login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            String url = CommUtil.getURL(request);
            mv.addObject("url", url + "/mobile/daohang.htm");
        }
        mv.addObject("storeId", store_id);
        return mv;
    }

    /**
     * 此路径只是临时的，待页面搞好了，就弃用，直接用楼上的方法
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/store/{storeId}.htm/mobile/daohang1.htm" })
    public ModelAndView daohang1(HttpServletRequest request, HttpServletResponse response,
                                 @PathVariable String storeId) {
        User user = SecurityUserHolder.getCurrentUser();
        ModelAndView mv = null;
        if (user != null) {
            mv = new JModelAndView("h5/commons/_daohang.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            UserTrackQueryObject qo = new UserTrackQueryObject("1", mv, "createtime", "desc");
            qo.setPageSize(6);
            qo.addQuery("obj.user.id", new SysMap("userid", user.getId()), "=");
            qo.setOrderBy("createtime");
            IPageList pList = this.trackServ.list(qo);
            mv.addObject("storeViewTools", storeViewTools);
            List<Track> tracks = pList.getResult();
            if (tracks != null && tracks.size() > 1) {//只显示双数
                if (tracks.size() % 2 != 0)
                    tracks.remove(tracks.size() - 1);
                mv.addObject("objs", tracks);
            }
        } else {
            mv = new JModelAndView("h5/login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            String url = CommUtil.getURL(request);
            mv.addObject("url", url + "/mobile/daohang.htm");
        }
        mv.addObject("storeId", storeId);
        return mv;
    }

    @RequestMapping({ "/store/{storeId}.htm/mobile/accountmanage.htm" })
    public ModelAndView accountmanage(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/u_daohang_two.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        return mv;
    }

    @RequestMapping({ "/mobile/ordermanage.htm" })
    public ModelAndView ordermanage(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/u_order_two.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        return mv;
    }

    @RequestMapping({ "/mobile/badmanage.htm" })
    public ModelAndView badmanage(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/u_badmanage_two.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        return mv;
    }

    @RequestMapping({ "/mobile/buyer/historymanage.htm" })
    public ModelAndView historymanage(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/u_historymanage_two.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        return mv;
    }

    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/query_ship.htm" })
    public ModelAndView query_ship(HttpServletRequest request, HttpServletResponse response,
                                   String id, @PathVariable String storeId) {
        User user = SecurityUserHolder.getCurrentUser();
        ModelAndView mv = null;
        if (user != null) {
            mv = new JModelAndView("user/default/usercenter/h5/order_zz.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0,
                request, response);
            OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
            TransInfo info = query_ship_getData(id);
            mv.addObject("obj", obj);
            mv.addObject("transInfo", info);
        } else {
            mv = new JModelAndView("h5/login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            String url = CommUtil.getURL(request);
            mv.addObject("url", url + "/mobile/daohang.htm");
        }
        mv.addObject("storeId", storeId);
        return mv;
    }

    private TransInfo query_ship_getData(String id) {
        TransInfo info = new TransInfo();
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(id));
        try {
            URL url = new URL("http://api.kuaidi100.com/api?id="
                              + this.configService.getSysConfig().getKuaidi_id() + "&com="
                              + (obj.getEc() != null ? obj.getEc().getCompany_mark() : "") + "&nu="
                              + obj.getShipCode() + "&show=0&muti=1&order=asc");
            URLConnection con = url.openConnection();
            con.setAllowUserInteraction(false);
            InputStream urlStream = url.openStream();
            String type = URLConnection.guessContentTypeFromStream(urlStream);
            String charSet = null;
            if (type == null) {
                type = con.getContentType();
            }
            if ((type == null) || (type.trim().length() == 0)
                || (type.trim().indexOf("text/html") < 0)) {
                return info;
            }
            if (type.indexOf("charset=") > 0) {
                charSet = type.substring(type.indexOf("charset=") + 8);
            }
            byte[] b = new byte[10000];
            int numRead = urlStream.read(b);
            String content = new String(b, 0, numRead, charSet);
            while (numRead != -1) {
                numRead = urlStream.read(b);
                if (numRead != -1) {
                    String newContent = new String(b, 0, numRead, charSet);
                    content = content + newContent;
                }
            }
            info = (TransInfo) Json.fromJson(TransInfo.class, content);
            urlStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }
}
