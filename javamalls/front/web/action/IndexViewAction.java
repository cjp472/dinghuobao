package com.javamalls.front.web.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import com.javamalls.base.tools.Md5Encrypt;
import com.javamalls.ctrl.admin.tools.MsgTools;
import com.javamalls.front.web.h5.action.Constent;
import com.javamalls.front.web.tools.GoodsFloorViewTools;
import com.javamalls.front.web.tools.GoodsViewTools;
import com.javamalls.front.web.tools.NavViewTools;
import com.javamalls.front.web.tools.StoreViewTools;
import com.javamalls.platform.domain.Article;
import com.javamalls.platform.domain.BargainGoods;
import com.javamalls.platform.domain.DeliveryGoods;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.GoodsBrand;
import com.javamalls.platform.domain.GoodsCart;
import com.javamalls.platform.domain.GoodsClass;
import com.javamalls.platform.domain.GoodsFloor;
import com.javamalls.platform.domain.Group;
import com.javamalls.platform.domain.GroupGoods;
import com.javamalls.platform.domain.Message;
import com.javamalls.platform.domain.MobileVerifyCode;
import com.javamalls.platform.domain.Partner;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.StoreCart;
import com.javamalls.platform.domain.SysConfig;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.query.GoodsQueryObject;
import com.javamalls.platform.service.IArticleService;
import com.javamalls.platform.service.IBargainGoodsService;
import com.javamalls.platform.service.IDeliveryGoodsService;
import com.javamalls.platform.service.IGoodsBrandService;
import com.javamalls.platform.service.IGoodsCartService;
import com.javamalls.platform.service.IGoodsClassService;
import com.javamalls.platform.service.IGoodsFloorService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IGroupGoodsService;
import com.javamalls.platform.service.IGroupService;
import com.javamalls.platform.service.IMessageService;
import com.javamalls.platform.service.IMobileVerifyCodeService;
import com.javamalls.platform.service.IPartnerService;
import com.javamalls.platform.service.IStoreCartService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;

/**加载首页
 *                       
 * @Filename: IndexViewAction.java
 * @Version: 4.1
 * @Author: 范光洲
 *  
 *
 */
@Controller
public class IndexViewAction {
    @Autowired
    private ISysConfigService     configService;
    @Autowired
    private IUserConfigService    userConfigService;
    @Autowired
    private IGoodsClassService    goodsClassService;
    @Autowired
    private IGoodsBrandService    goodsBrandService;
    @Autowired
    private IPartnerService       partnerService;
    @Autowired
    private IUserService          userService;
    @Autowired
    private IArticleService       articleService;
    @Autowired
    private IMessageService       messageService;
    @Autowired
    private IStoreService         storeService;
    @Autowired
    private IGoodsService         goodsService;
    @Autowired
    private IGroupGoodsService    groupGoodsService;
    @Autowired
    private IGroupService         groupService;
    @Autowired
    private IGoodsFloorService    goodsFloorService;
    @Autowired
    private IBargainGoodsService  bargainGoodsService;
    @Autowired
    private IDeliveryGoodsService deliveryGoodsService;
    @Autowired
    private IStoreCartService     storeCartService;
    @Autowired
    private IGoodsCartService     goodsCartService;
    @Autowired
    private NavViewTools          navTools;
    @Autowired
    private GoodsViewTools        goodsViewTools;
    @Autowired
    private StoreViewTools        storeViewTools;
    @Autowired
    private MsgTools              msgTools;
    @Autowired
    private GoodsFloorViewTools   gf_tools;
    @Autowired
    private IMobileVerifyCodeService mobileverifycodeService;
    
    private Logger                log = Logger.getLogger(this.getClass());
    
    
    
    /**
     * 商城PC端顶部
     * @param request
     * @param response
     * @return
     */
   /* @RequestMapping({ "/top.htm" })
    public ModelAndView top(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("top.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        List<Message> msgs = new ArrayList<Message>();
        
         * 如果用户已登录，获取站内消息
         
        if (SecurityUserHolder.getCurrentUser() != null) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("status", Integer.valueOf(0));
            params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
            msgs = this.messageService
                .query(
                    "select obj from Message obj where obj.parent.id is null and (obj.status=:status and obj.toUser.id=:user_id) or (obj.reply_status=1 and obj.fromUser.id=:user_id) or (obj.reply_status=2 and obj.toUser.id=:user_id) ",
                    params, -1, -1);
        }
        Store store = null;
        
         * 获取用户店铺信息
         
        if (SecurityUserHolder.getCurrentUser() != null) {
            if (SecurityUserHolder.getCurrentUser().getStore() != null) {
                store = this.storeService.getObjByProperty("id", SecurityUserHolder
                    .getCurrentUser().getStore().getId());
            }

        }
        mv.addObject("store", store);
        mv.addObject("navTools", this.navTools);
        mv.addObject("msgs", msgs);
        
         * 获取购物车信息
         
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
        return mv;
    }*/
    
    @RequestMapping({ "/store/{storeId}.htm/buyer_top.htm" })
    public ModelAndView buyer_top(HttpServletRequest request, HttpServletResponse response,@PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("buyer_top.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
       // List<Message> msgs = new ArrayList<Message>();
        long msgCount=0;
         /* 如果用户已登录，获取站内消息*/
         
        if (SecurityUserHolder.getCurrentUser() != null) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("status", Integer.valueOf(0));
            params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
            msgCount = this.messageService
                .queryCount(
                    "select count(obj) from Message obj where obj.parent.id is null and (obj.status=:status and obj.toUser.id=:user_id) or (obj.reply_status=1 and obj.fromUser.id=:user_id) or (obj.reply_status=2 and obj.toUser.id=:user_id) ",
                    params);
        }
        Store store = null;
        
         /* 获取用户店铺信息*/
        try{ 
	        if (storeId != null&&!"".equals(storeId)) {
	           store = this.storeService.getObjById(Long.valueOf(storeId));
	        }
        }catch(Exception e){
        	e.printStackTrace();
        }
        mv.addObject("store", store);
        mv.addObject("navTools", this.navTools);
        mv.addObject("msgCount", msgCount);
       
        return mv;
    }
    
    /**
     * 新订货商城PC端seller顶部,原代码在上边
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/top.htm" })
    public ModelAndView seller_top(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("seller_top.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        List<Message> msgs = new ArrayList<Message>();
        /*
         * 如果用户已登录，获取站内消息
         */
        if (SecurityUserHolder.getCurrentUser() != null) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("status", Integer.valueOf(0));
            params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
            msgs = this.messageService
                .query(
                    "select obj from Message obj where obj.parent.id is null and (obj.status=:status and obj.toUser.id=:user_id) or (obj.reply_status=1 and obj.fromUser.id=:user_id) or (obj.reply_status=2 and obj.toUser.id=:user_id) ",
                    params, -1, -1);
        }
        Store store = null;
        /*
         * 获取用户店铺信息
         */
        if (SecurityUserHolder.getCurrentUser() != null) {
            if (SecurityUserHolder.getCurrentUser().getStore() != null) {
                store = this.storeService.getObjByProperty("id", SecurityUserHolder
                    .getCurrentUser().getStore().getId());
            }

        }
        mv.addObject("store", store);
        mv.addObject("navTools", this.navTools);
        mv.addObject("msgs", msgs);
        
        return mv;
    }
    
    
    /**
     * 商城PC端顶部-购物车不能点击悬浮
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/ordertop.htm" })
    public ModelAndView ordertop(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("top2.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        List<Message> msgs = new ArrayList<Message>();
        /*
         * 如果用户已登录，获取站内消息
         */
        if (SecurityUserHolder.getCurrentUser() != null) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("status", Integer.valueOf(0));
            params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
            msgs = this.messageService
                .query(
                    "select obj from Message obj where obj.parent.id is null and (obj.status=:status and obj.toUser.id=:user_id) or (obj.reply_status=1 and obj.fromUser.id=:user_id) or (obj.reply_status=2 and obj.toUser.id=:user_id) ",
                    params, -1, -1);
        }
        Store store = null;
        /*
         * 获取用户店铺信息
         */
        if (SecurityUserHolder.getCurrentUser() != null) {
            if (SecurityUserHolder.getCurrentUser().getStore() != null) {
                store = this.storeService.getObjByProperty("id", SecurityUserHolder
                    .getCurrentUser().getStore().getId());
            }

        }
        mv.addObject("store", store);
        mv.addObject("navTools", this.navTools);
        mv.addObject("msgs", msgs);
        /*
         * 获取购物车信息
         */
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
        return mv;
    }
    
    /**
     * 右侧栅栏
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/rightbar.htm" })
    public ModelAndView rightbar(HttpServletRequest request, HttpServletResponse response) {
    	 ModelAndView mv = new JModelAndView("rightbar.html", this.configService.getSysConfig(),
    	            this.userConfigService.getUserConfig(), 1, request, response);
    	 
    	 /*
          * 获取购物车信息
          */
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
         return mv;
    }
    /**
     * 全部产品分类
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/nav.htm" })
    public ModelAndView nav(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("nav.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        mv.addObject("navTools", this.navTools);
     /*   Map<String, Boolean> params = new HashMap<String, Boolean>();
        params.put("display", Boolean.valueOf(true));
        List<GoodsClass> gcs = this.goodsClassService
            .query(
                "select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
                params, 0, 15);
        mv.addObject("gcs", gcs);
        
        Map<String,Object> paramMap = new HashMap<String, Object>();
        paramMap.put("display", Boolean.valueOf(true));
        paramMap.put("mark", "news");
        //获取首页文章列表
        List<Article> news = this.articleService.query(
            "select obj from Article obj where 1=1 "
                    + "and obj.display=:display and obj.articleClass.mark=:mark "
                    + "order by obj.createtime desc", paramMap, 0, 8);
        mv.addObject("news", news);*/
        return mv;
    }
    
    /**
     * 买家全部产品分类
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/store/{storeId}.htm/buyer_nav.htm" })
    public ModelAndView nav(HttpServletRequest request, HttpServletResponse response,@PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("nav.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        mv.addObject("navTools", this.navTools);
    
        return mv;
    }
    /**
     * 全部产品分类 V5 首页
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/nav_index.htm" })
    public ModelAndView nav_index(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("nav_index.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        mv.addObject("navTools", this.navTools);
        Map<String, Boolean> params = new HashMap<String, Boolean>();
        params.put("display", Boolean.valueOf(true));
        List<GoodsClass> gcs = this.goodsClassService
            .query(
                "select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
                params, 0, 15);
        mv.addObject("gcs", gcs);
        
        Map<String,Object> paramMap = new HashMap<String, Object>();
        paramMap.put("display", Boolean.valueOf(true));
        paramMap.put("mark", "news");
        //获取首页文章列表
        List<Article> news = this.articleService.query(
            "select obj from Article obj where 1=1 "
                    + "and obj.display=:display and obj.articleClass.mark=:mark "
                    + "order by obj.createtime desc", paramMap, 0, 8);
        mv.addObject("news", news);
        return mv;
    }
    /**
     * 全部产品分类1
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/nav1.htm" })
    public ModelAndView nav1(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("nav1.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("display", Boolean.valueOf(true));
        List<GoodsClass> gcs = this.goodsClassService
            .query(
                "select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
                params, 0, 15);
        mv.addObject("gcs", gcs);
        mv.addObject("navTools", this.navTools);
        return mv;
    }
    /**
     * 商城搜索框部分.
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/head.htm" })
    public ModelAndView head(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("head.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
      /*  String type = CommUtil.null2String(request.getAttribute("type"));
        mv.addObject("type", type.equals("") ? "goods" : type);
        User currentUser = SecurityUserHolder.getCurrentUser();
        Store store=null;
        if(currentUser!=null&&currentUser.getStore()!=null){
        	store=this.storeService.getObjById(currentUser.getStore().getId());
        }
        mv.addObject("store", store);*/
        return mv;
    }

    /**
     * 买家的head部分
     * @param request
     * @param response
     * @param storeId
     * @return
     */
    @RequestMapping({ "/store/{storeId}.htm/buyer_head.htm" })
    public ModelAndView buyer_head(HttpServletRequest request, HttpServletResponse response,@PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("buyer/buyer_head.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        String type = CommUtil.null2String(request.getAttribute("type"));
        mv.addObject("type", type.equals("") ? "goods" : type);
        Store store=null;
        if(storeId!=null&&!"".equals(storeId)){
        	store=this.storeService.getObjById(Long.parseLong(storeId));
        }
        mv.addObject("store", store);
        
        
        List<GoodsCart> list = new ArrayList<GoodsCart>();
        List<StoreCart> cart = new ArrayList<StoreCart>();
        List<StoreCart> user_cart = new ArrayList<StoreCart>();
        User currentUser = SecurityUserHolder.getCurrentUser() ;
        User user = null;
        if (currentUser!= null) {
            user = this.userService.getObjById(currentUser.getId());
        }

        Map<String, Object> params = new HashMap<String, Object>();
    
        //买家
        if (user != null) {
                params.clear();
                params.put("user_id", user.getId());
                params.put("sc_status", Integer.valueOf(0));
                user_cart = this.storeCartService
                    .query(
                        "select obj from StoreCart obj fetch all properties where obj.user.id=:user_id and obj.sc_status=:sc_status",
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

        if (cart != null) {
            for (StoreCart sc : cart) {
                if (sc != null) {
                    list.addAll(sc.getGcs());
                }
            }
        }
        mv.addObject("cart", list);
        return mv;
    }
    
    /**
     * 付款header
     * @param request
     * @param response
     * @param storeId
     * @return
     */
    @RequestMapping({ "/store/{storeId}.htm/buyer_pay_head.htm" })
    public ModelAndView buyer_pay_head(HttpServletRequest request, HttpServletResponse response,@PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("buyer/buyer_pay_head.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
      
        Store store=null;
        if(storeId!=null&&!"".equals(storeId)){
        	store=this.storeService.getObjById(Long.parseLong(storeId));
        }
        mv.addObject("store", store);
        
        return mv;
    }
    
    /**
     * 买家的head部分
     * @param request
     * @param response
     * @param storeId
     * @return
     */
    @RequestMapping({ "/store/{storeId}.htm/buyer_cart_head.htm" })
    public ModelAndView buyer_cart_head(HttpServletRequest request, HttpServletResponse response,@PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("buyer/buyer_cart_head.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
       
        Store store=null;
        if(storeId!=null&&!"".equals(storeId)){
        	store=this.storeService.getObjById(Long.parseLong(storeId));
        }
        mv.addObject("store", store);
        return mv;
    }
    
    @RequestMapping({ "/login_head.htm" })
    public ModelAndView login_head(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("login_head.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        return mv;
    }
    /**
     * 商城首页楼层信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/floor.htm" })
    public ModelAndView floor(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("floor.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("gf_display", Boolean.valueOf(true));
        List<GoodsFloor> floors = this.goodsFloorService
            .query(
                "select obj from GoodsFloor obj where obj.gf_display=:gf_display and obj.parent.id is null order by obj.gf_sequence asc",
                params, -1, -1);
        mv.addObject("floors", floors);
        //把GoodsFloorViewTools的实例传出去  在floor.html页面调用其方法
        mv.addObject("gf_tools", this.gf_tools);
        mv.addObject("url", CommUtil.getURL(request));
        return mv;
    }
    /**
     * 导航栏信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/commonHeader.htm" })
    public ModelAndView commonHeader(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("commonHeader.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        mv.addObject("navTools", this.navTools);
        Map<String, Boolean> params = new HashMap<String, Boolean>();
        params.put("display", Boolean.valueOf(true));
        List<GoodsClass> gcs = this.goodsClassService
            .query(
                "select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
                params, 0, 15);
        mv.addObject("gcs", gcs);
        return mv;
    }
    /**
     * 商城PC端尾部1
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/footer.htm" })
    public ModelAndView footer(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("footer.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        mv.addObject("navTools", this.navTools);

    /*    Map<String, String> params = new HashMap<String, String>();
        params.put("mark", "news");
        List<ArticleClass> acs = this.articleClassService.query(
            "select obj from ArticleClass obj where obj.parent.id is null "
                    + "and obj.mark!=:mark and sysClass=1 order by obj.sequence asc", params, 0, 5);
        mv.addObject("acs", acs);
        params.clear();
        //获取友情连接列表
        
        List<Partner> text_partners = this.partnerService
            .query("select obj from Partner obj where obj.image.id "
                   + "is null order by obj.sequence asc", params, -1, -1);
        mv.addObject("text_partners", text_partners);*/
       //mv.addObject("store", new Store());
        return mv;
    }
    
    /**
     * 商城PC端尾部1
     * @param request
     * @param response
     * @return
   
    @RequestMapping({ "/store/{storeId}.htm/footer.htm" })
    public ModelAndView buyer_footer(HttpServletRequest request, HttpServletResponse response,@PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("footer.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        mv.addObject("navTools", this.navTools);
        Store store=null;
        try{
        	store = this.storeService.getObjById(Long.valueOf(storeId));
        }catch(Exception e){
        	e.printStackTrace();
        }
        mv.addObject("store", store);
        return mv;
    }  */
    /**
     * 商城PC端尾部1
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/store/{storeId}.htm/buyer_footer.htm" })
    public ModelAndView buyer_footer1(HttpServletRequest request, HttpServletResponse response,@PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("buyer_footer.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        mv.addObject("navTools", this.navTools);
        Store store=null;
        try{
        	store = this.storeService.getObjById(Long.valueOf(storeId));
        }catch(Exception e){
        	e.printStackTrace();
        }
        mv.addObject("store", store);
        
        
        return mv;
    }
    /**
     * 商城PC端尾部 mini
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/mini_footer.htm" })
    public ModelAndView minifooter(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("mini_footer.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        mv.addObject("navTools", this.navTools);
        User currentUser = SecurityUserHolder.getCurrentUser();
        Store store=null;
        if(currentUser!=null&&currentUser.getStore()!=null){
        	store=this.storeService.getObjById(currentUser.getStore().getId());
        }
        mv.addObject("store", store);
        return mv;
    }
    /**
     * 商城PC端首页
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/index.htm" })
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
    	request.getSession(false).setAttribute(Constent.WEB_TYPE_KEY, "");
        String type = (String) request.getSession(false).getAttribute(Constent.WEB_TYPE_KEY);
        
        //如果是手机设备，则跳转至手机端首页。
        if ("mobile".equals(type)) {
            SysConfig config = this.configService.getSysConfig();
            ModelAndView mv = new JModelAndView("h5/error.html", config,
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "正在为您跳转至首页");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
            return mv;
        }
        
        String  centUrl = "/user/login.htm";
        //若已经登录
	      	//若买家
	    	//若卖家
	    //若未登录
        	if(SecurityUserHolder.getCurrentUser() != null){
        		User user = SecurityUserHolder.getCurrentUser();
        		if(user!=null&&user.getStore()!=null){
        			centUrl = "/seller/index.htm";
        		}/*else{
        			centUrl = "/buyer/index.htm";
        		} */
        	} 
        try{
			response.sendRedirect(CommUtil.getURL(request)+centUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        //指定View的模版文件为index.html
        ModelAndView mv = new JModelAndView("index.html", this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("display", Boolean.valueOf(true));
        params.clear();
        params.put("audit", Integer.valueOf(1));
        params.put("recommend", Boolean.valueOf(true));
        //获取商品品牌列表
        List<GoodsBrand> gbs = this.goodsBrandService.query(
            "select obj from GoodsBrand obj where obj.audit=:audit "
                    + "and obj.recommend=:recommend order by obj.sequence", params, -1, -1);
        mv.addObject("gbs", gbs);
        params.clear();
        //获取合作伙伴列表
        List<Partner> img_partners = this.partnerService.query(
            "select obj from Partner obj where obj.image.id is "
                    + "not null order by obj.sequence asc", params, -1, -1);
        mv.addObject("img_partners", img_partners);
        List<Partner> text_partners = this.partnerService
            .query("select obj from Partner obj where obj.image.id "
                   + "is null order by obj.sequence asc", params, -1, -1);
        mv.addObject("text_partners", text_partners);
        params.clear();
        params.put("display", Boolean.valueOf(true));
        params.put("mark", "news");
        //获取首页文章列表
       /* List<Article> news = this.articleService.query(
            "select obj from Article obj where 1=1 "
                    + "and obj.display=:display and obj.articleClass.mark=:mark "
                    + "order by obj.createtime desc", params, 0, 5);
        mv.addObject("news", news);*/
        /*
         * 首页商品推荐规则
         * ------------------------------------------------------
         * 如果用户未登录，则显示商城推荐的商品
         * 如果用户已登录，则推荐其近一个月收藏的商品类目及近一个月浏览商品对应分类的商品
         * ------------------------------------------------------
         * 用户收藏商品推荐规则
         * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
         * 找出用户收藏的商品对应的类目（最多五个，尽可能多），
         * 推荐其该类目下的商品（每个类目最多三个商品）
         * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
         */
        GoodsQueryObject go = new GoodsQueryObject();
        List<Object> recomGoods = new ArrayList<Object>();
        long s1 = System.currentTimeMillis();
        if (null == SecurityUserHolder.getCurrentUser()) {
            log.debug("用户未登录，查询商城推荐商品");
            go.addQuery("obj.store_recommend",
                new SysMap("store_recommend", Boolean.valueOf(true)), "=");
            go.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(0)), "=");
            go.setOrderBy("store_recommend_time");
            go.setPageSize(8);
            go.setOrderType("desc");
            go.addQuery("obj.disabled", new SysMap("disabled", false), "=");

            IPageList ip = this.goodsService.list(go);
            if (null != ip.getResult() && ip.getResult().size() > 0)
                recomGoods = ip.getResult();
        } else {
            log.debug("用户已登录，查询收藏的商品");
            User user = SecurityUserHolder.getCurrentUser();
            //用户收藏的商品分类
            params.clear();
            params.put("userid", user.getId());

            Calendar cal = Calendar.getInstance();
            params.put("cur", cal.getTime());
            cal.add(Calendar.MONTH, -1);
            params.put("pre", cal.getTime());

            List<Goods> favoriteGoods = this.goodsService
                .query(
                    "select g from Favorite as fa,Goods as g where fa.user.id=:userid "
                            + "and fa.createtime>=:pre and fa.createtime<=:cur and fa.goods.id=g.id and g.goods_status=0",
                    params, -1, -1);
            Map<Long, Long> gcid = new HashMap<Long, Long>();
            int i = 0;
            for (Goods goods : favoriteGoods) {
                if (i > 5)
                    break;
                if (goods.getGc() == null || null != gcid.get(goods.getGc().getId()))
                    continue;
                log.debug("收藏商品所属分类：" + goods.getGc().getClassName());
                go.addQuery("obj.gc.id", new SysMap("gcid", goods.getGc().getId()), "=");
                go.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(0)), "=");
                go.setPageSize(3);
                go.addQuery("obj.disabled", new SysMap("disabled", false), "=");

                IPageList fc = this.goodsService.list(go);
                recomGoods.addAll(fc.getResult());
                if (recomGoods.size() >= 8)
                    break;
                gcid.put(goods.getGc().getId(), goods.getGc().getId());
                i++;
            }
            //如果收藏商品分类下商品不足8个，则使用用户足迹查找同类商品
            if (recomGoods.size() < 8) {
                log.debug("收藏商品分类下的商品不足8个，查询用户足迹");
                params.clear();
                params.put("userid", user.getId());

                //一个月内的足迹商品
                Calendar cal2 = Calendar.getInstance();
                params.put("cur", cal2.getTime());
                cal2.add(Calendar.MONTH, -1);
                params.put("pre", cal2.getTime());

                List<Goods> trackGoods = this.goodsService
                    .query(
                        "select g from Track as tr,Goods as g where tr.user.id=:userid "
                                + "and tr.createtime>=:pre and tr.createtime<=:cur and tr.goods.id=g.id and g.goods_status=0",
                        params, -1, -1);
                int j = 0;
                go = new GoodsQueryObject();
                for (Goods goods : trackGoods) {
                    if (j > 5)
                        break;
                    if (goods.getGc() == null || null != gcid.get(goods.getGc().getId()))
                        continue;
                    log.debug("用户足迹商品所属分类：" + goods.getGc().getClassName());
                    go.addQuery("obj.gc.id", new SysMap("gcid", goods.getGc().getId()), "=");
                    go.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(0)),
                        "=");
                    go.setPageSize(3);
                    go.addQuery("obj.disabled", new SysMap("disabled", false), "=");

                    IPageList fc = this.goodsService.list(go);
                    recomGoods.addAll(fc.getResult());
                    if (recomGoods.size() >= 8)
                        break;
                    gcid.put(goods.getGc().getId(), goods.getGc().getId());
                    j++;
                }
            }
            if (recomGoods.size() < 8) {
                log.debug("用户收藏和用户足迹下的商品类目的同类商品均不足8个，使用商城推荐填充");
                go = new GoodsQueryObject();
                go.addQuery("obj.store_recommend",
                    new SysMap("store_recommend", Boolean.valueOf(true)), "=");
                go.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(0)), "=");
                go.setOrderBy("store_recommend_time");
                go.setPageSize(8);
                go.setOrderType("desc");
                go.addQuery("obj.disabled", new SysMap("disabled", false), "=");

                IPageList ip = this.goodsService.list(go);
                if (ip != null && ip.getResult() != null) {
                    for (Object obj : ip.getResult()) {
                        if (null == gcid.get(((Goods) obj).getId()))
                            recomGoods.add(obj);
                        if (recomGoods.size() >= 8)
                            break;
                    }
                }

            }
        }
        log.debug("首页商城推荐商品数量：" + recomGoods.size());
        mv.addObject("store_reommend_goods", recomGoods);
        long s2 = System.currentTimeMillis();
        log.debug("首页推荐查询完毕，共耗时" + (s2 - s1) + "毫秒");

        mv.addObject(
            "store_reommend_goods_count",
            Double.valueOf(Math.ceil(CommUtil.div(Integer.valueOf(recomGoods.size()),
                Integer.valueOf(5)))));
        mv.addObject("goodsViewTools", this.goodsViewTools);
        mv.addObject("storeViewTools", this.storeViewTools);
        if (SecurityUserHolder.getCurrentUser() != null) {
            mv.addObject("user",
                this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId()));
        }
        params.clear();
        params.put("beginTime", new Date());
        params.put("endTime", new Date());
        List<Group> groups = this.groupService.query(
            "select obj from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime",
            params, -1, -1);
        if (groups.size() > 0) {
            params.clear();
            params.put("gg_status", Integer.valueOf(1));
            params.put("gg_recommend", Integer.valueOf(1));
            params.put("group_id", ((Group) groups.get(0)).getId());
            List<GroupGoods> ggs = this.groupGoodsService
                .query(
                    "select obj from GroupGoods obj where obj.gg_status=:gg_status and "
                            + "obj.gg_recommend=:gg_recommend and obj.group.id=:group_id order by obj.gg_recommend_time desc",
                    params, 0, 5);
            mv.addObject("groups", ggs);
        }
        params.clear();
        params.put("bg_time", CommUtil.formatDate(CommUtil.formatShortDate(new Date())));
        params.put("bg_status", Integer.valueOf(1));
        List<BargainGoods> bgs = this.bargainGoodsService
            .query(
                "select obj from BargainGoods obj where obj.bg_time=:bg_time and obj.bg_status=:bg_status",
                params, 0, 5);
        mv.addObject("bgs", bgs);
        params.clear();
        params.put("d_status", Integer.valueOf(1));
        params.put("d_begin_time", new Date());
        params.put("d_end_time", new Date());
        List<DeliveryGoods> dgs = this.deliveryGoodsService
            .query(
                "select obj from DeliveryGoods obj where obj.d_status=:d_status and "
                        + "obj.d_begin_time<=:d_begin_time and obj.d_end_time>=:d_end_time order by obj.d_audit_time desc",
                params, 0, 5);
        mv.addObject("dgs", dgs);
        return mv;
    }
    /**
     * 系统维护中
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/close.htm" })
    public ModelAndView close(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("close.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        return mv;
    }
    /**
     * 商城404错误
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/404.htm" })
    public ModelAndView error404(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("404.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        String jm_view_type = CommUtil.null2String(request.getSession(false) == null ? null
            : request.getSession(false).getAttribute(Constent.WEB_TYPE_KEY));
        mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
        if ((jm_view_type != null) && (!jm_view_type.equals("")) && (jm_view_type.equals("mobile"))) {
            mv = new JModelAndView("h5/404.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
        }
        /*if ((jm_view_type != null) && (!jm_view_type.equals("")) && (jm_view_type.equals("weixin"))) {
            String store_id = CommUtil.null2String(request.getSession(false) == null ? null
                : request.getSession(false).getAttribute("store_id"));
            mv = new JModelAndView("weixin/404.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("url", CommUtil.getURL(request) + "/weixin/index.htm?store_id=" + store_id);
        }*/
        return mv;
    }
    /**
     * 商城500错误
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/500.htm" })
    public ModelAndView error500(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("500.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        String jm_view_type = CommUtil.null2String(request.getSession(false).getAttribute(
            Constent.WEB_TYPE_KEY));
        
        mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
        if ((jm_view_type != null) && (!jm_view_type.equals("")) && (jm_view_type.equals("mobile"))) {
            mv = new JModelAndView("h5/500.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
        }
        if ((jm_view_type != null) && (!jm_view_type.equals("")) && (jm_view_type.equals("weixin"))) {
            String store_id = CommUtil.null2String(request.getSession(false).getAttribute(
                "store_id"));
            mv = new JModelAndView("weixin/500.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("url", CommUtil.getURL(request) + "/weixin/index.htm?store_id=" + store_id);
        }
        return mv;
    }
    /**
     * 产品分类
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/goods_class.htm" })
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

    /**
     * 卖家忘记密码
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/forget.htm" })
    public ModelAndView forget(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("forget.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        
        request.getSession(false).removeAttribute("forget_seller_user");//清除session
        return mv;
    }
    
    /**
     * 买家忘记密码第二步
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/forget_next.htm" })
    public ModelAndView forget_next(HttpServletRequest request, HttpServletResponse response
    		,String userName,String sms_code) {
        ModelAndView mv = new JModelAndView("forget_next.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
     
        boolean reg = true;
        //短信验证码
        MobileVerifyCode mvc = this.mobileverifycodeService.getObjByProperty("mobile", userName);
        if ((mvc != null) && (mvc.getCode().equalsIgnoreCase(sms_code))) {
            reg = true;
        }else{
        	reg = false;
        }
        
        if(reg){//验证成功
        	request.getSession().setAttribute("forget_seller_user", userName);
        }else{//验证失败
        	mv = new JModelAndView("forget.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
        	request.getSession(false).removeAttribute("forget_seller_user");//清除session
        }
        return mv;
    }
    
    /**
     * 买家忘记密码第三步
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/forget_finish.htm" })
    public ModelAndView forget_finish(HttpServletRequest request, HttpServletResponse response
    		,String password) {
        ModelAndView mv = new JModelAndView("forget_finish.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);

        String userName = (String)request.getSession().getAttribute("forget_seller_user");
        String url = CommUtil.getURL(request) + "/user/login.htm";
        if (userName!=null&&!"".equals(userName)) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("userName", userName);
            params.put("mobile", userName);
            List<User> users = this.userService
                    .query("select obj from User obj where obj.disabled=false and obj.userRole='BUYER_SELLER' and (obj.userName=:userName or obj.mobile=:mobile)", params,
                        0, 1);
            if ((users != null) && (users.size() > 0)) {
               User user=users.get(0);
               user.setPassword(Md5Encrypt.md5(password).toLowerCase());
               this.userService.update(user);
               
               mv.addObject("op_title", "密码重置成功");
               mv.addObject("url", url);
               
               request.getSession(false).removeAttribute("forget_seller_user");//清除session
            }
        }else{
        	mv = new JModelAndView("forget.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
        	request.getSession(false).removeAttribute("forget_seller_user");//清除session
        }
        
        return mv;
        
    }

    
    
    /**
     * 买家忘记密码
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/store/{storeId}.htm/buyer_forget.htm" })
    public ModelAndView buyer_forget(HttpServletRequest request, HttpServletResponse response,@PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("buyer/buyer_forget.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        Store store = this.storeService.getObjById(CommUtil.null2Long(storeId));
        mv.addObject("store", store);
        request.getSession(false).removeAttribute("forget_buyer_user_"+storeId);//清除session
        return mv;
    }
    
    /**
     * 买家忘记密码第二步
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/store/{storeId}.htm/buyer_forget_next.htm" })
    public ModelAndView buyer_forget_next(HttpServletRequest request, HttpServletResponse response,@PathVariable String storeId
    		,String userName,String sms_code) {
        ModelAndView mv = new JModelAndView("buyer/buyer_forget_next.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        Store store = this.storeService.getObjById(CommUtil.null2Long(storeId));
     
        boolean reg = true;
        //短信验证码
        MobileVerifyCode mvc = this.mobileverifycodeService.getObjByProperty("mobile", userName);
        if ((mvc != null) && (mvc.getCode().equalsIgnoreCase(sms_code))) {
            reg = true;
        }else{
        	reg = false;
        }
        
        if(reg){//验证成功
        	request.getSession().setAttribute("forget_buyer_user_"+storeId, userName);
        }else{//验证失败
        	mv = new JModelAndView("buyer/buyer_forget.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
        	request.getSession(false).removeAttribute("forget_buyer_user_"+storeId);//清除session
        }
        mv.addObject("store", store);
        return mv;
    }
    
    /**
     * 买家忘记密码第三步
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/store/{storeId}.htm/buyer_forget_finish.htm" })
    public ModelAndView buyer_forget_finish(HttpServletRequest request, HttpServletResponse response,@PathVariable String storeId
    		,String password) {
        ModelAndView mv = new JModelAndView("buyer/buyer_forget_finish.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        Store store = this.storeService.getObjById(CommUtil.null2Long(storeId));

        String userName = (String)request.getSession().getAttribute("forget_buyer_user_"+storeId);
        String url = CommUtil.getURL(request) + "/user/login.htm";
        if (userName!=null&&!"".equals(userName)) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("userName", userName);
            params.put("mobile", userName);
            List<User> users = this.userService.query(
                "select obj from User obj where   obj.disabled=false and obj.userRole='BUYER' and (obj.userName=:userName or obj.mobile=:mobile) ", params, 0, 1);
            if ((users != null) && (users.size() > 0)) {
               User user=users.get(0);
               user.setPassword(Md5Encrypt.md5(password).toLowerCase());
               this.userService.update(user);
               
               mv.addObject("op_title", "密码重置成功");
               mv.addObject("url", url);
               
               request.getSession(false).removeAttribute("forget_buyer_user_"+storeId);//清除session
            }
        }else{
        	mv = new JModelAndView("buyer/buyer_forget.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
        	request.getSession(false).removeAttribute("forget_buyer_user_"+storeId);//清除session
        }
        
        mv.addObject("store", store);
        return mv;
        
    }

    @RequestMapping({ "/find_pws.htm" })
    public ModelAndView find_pws(HttpServletRequest request, HttpServletResponse response,
                                 String userName, String email, String code) {
        ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(),
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
                    mv.addObject("url", CommUtil.getURL(request) + "/user/login.htm");
                } else {
                    mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                        this.userConfigService.getUserConfig(), 1, request, response);
                    mv.addObject("op_title", "邮件发送失败，密码暂未执行重置");
                    mv.addObject("url", CommUtil.getURL(request) + "/forget.htm");
                }
            } else {
                mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "用户名、邮箱不匹配");
                mv.addObject("url", CommUtil.getURL(request) + "/forget.htm");
            }
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "验证码不正确");
            mv.addObject("url", CommUtil.getURL(request) + "/forget.htm");
        }
        return mv;
    }
    
    @RequestMapping({ "/store/{storeId}.htm/buyer_find_pws.htm" })
    public ModelAndView buyer_find_pws(HttpServletRequest request, HttpServletResponse response,
                                 String userName, String email, String code,@PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("buyer/buyer_success.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        HttpSession session = request.getSession(false);
        String verify_number = (String) session.getAttribute("verify_number");
  
        if (storeId!=null&&!"".equals(storeId)&&code.toUpperCase().equals(verify_number)) {
           
            String querySql="select obj from User obj where obj.disabled=false and obj.userName=:userName ";
            Map<String, Object> map=new HashMap<String, Object>();
            map.put("userName", userName);
            List<User> userlist = this.userService.query(querySql, map, 0, 1);
            if(userlist!=null&&userlist.size()>0){
            	User user=userlist.get(0);
            	
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
	                    mv.addObject("url", CommUtil.getURL(request) + "/user/login.htm");
	                } else {
	                    mv = new JModelAndView("buyer/buyer_error.html", this.configService.getSysConfig(),
	                        this.userConfigService.getUserConfig(), 1, request, response);
	                    mv.addObject("op_title", "邮件发送失败，密码暂未执行重置");
	                    mv.addObject("url", CommUtil.getURL(request) + "/buyer_forget.htm");
	                }
	            } else {
	                mv = new JModelAndView("buyer/buyer_error.html", this.configService.getSysConfig(),
	                    this.userConfigService.getUserConfig(), 1, request, response);
	                mv.addObject("op_title", "用户名、邮箱不匹配");
	                mv.addObject("url", CommUtil.getURL(request) + "/buyer_forget.htm");
	            }
           }else{
        	   		mv = new JModelAndView("buyer/buyer_error.html", this.configService.getSysConfig(),
	                    this.userConfigService.getUserConfig(), 1, request, response);
	                mv.addObject("op_title", "用户名、邮箱不匹配");
	                mv.addObject("url", CommUtil.getURL(request) + "/buyer_forget.htm");
           }
        } else {
            mv = new JModelAndView("buyer/buyer_error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "验证码不正确");
            mv.addObject("url", CommUtil.getURL(request) + "/buyer_forget.htm");
        }
        return mv;
    }

    @RequestMapping({ "/switch_recommend_goods.htm" })
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

    @RequestMapping({ "/outline.htm" })
    public ModelAndView outline(HttpServletRequest request, HttpServletResponse response) {
        String view = "error.html";
        String jm_view_type = CommUtil.null2String(request.getSession(false).getAttribute(
            Constent.WEB_TYPE_KEY));
        String targetUrl = CommUtil.getURL(request) + "/index.htm";
        //手机h5登录
        if ((jm_view_type != null) && (!jm_view_type.equals("")) && ("mobile".equals(jm_view_type))) {

            targetUrl = CommUtil.getURL(request) + "/mobile/user/login.htm";
            view = "h5/error.html";
        }
        ModelAndView mv = new JModelAndView(view, this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        mv.addObject("op_title", "该用户在其他地点登录，您被迫下线！");

        mv.addObject("url", targetUrl);
        return mv;
    }
}
