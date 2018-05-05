package com.javamalls.front.web.h5.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.constant.Constant;
import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.WebForm;
import com.javamalls.ctrl.admin.tools.MsgTools;
import com.javamalls.ctrl.admin.tools.PaymentTools;
import com.javamalls.ctrl.seller.Tools.TransportTools;
import com.javamalls.front.web.tools.AreaViewTools;
import com.javamalls.front.web.tools.GoodsViewTools;
import com.javamalls.payment.chinabank.h5.util.JsonUtil;
import com.javamalls.payment.tools.PayTools;
import com.javamalls.platform.domain.Address;
import com.javamalls.platform.domain.Area;
import com.javamalls.platform.domain.CouponInfo;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.GoodsCart;
import com.javamalls.platform.domain.GoodsItem;
import com.javamalls.platform.domain.GoodsSpecProperty;
import com.javamalls.platform.domain.GoodsStepPrice;
import com.javamalls.platform.domain.GroupGoods;
import com.javamalls.platform.domain.OrderForm;
import com.javamalls.platform.domain.OrderFormLog;
import com.javamalls.platform.domain.Payment;
import com.javamalls.platform.domain.PredepositLog;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.StoreCart;
import com.javamalls.platform.domain.Strategy;
import com.javamalls.platform.domain.StrategyGoodsItem;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.query.AddressQueryObject;
import com.javamalls.platform.service.IAddressService;
import com.javamalls.platform.service.IAreaService;
import com.javamalls.platform.service.ICouponInfoService;
import com.javamalls.platform.service.IGoodsCartService;
import com.javamalls.platform.service.IGoodsItemService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IGoodsSpecPropertyService;
import com.javamalls.platform.service.IGoodsStepPriceService;
import com.javamalls.platform.service.IGroupGoodsService;
import com.javamalls.platform.service.IOrderFormLogService;
import com.javamalls.platform.service.IOrderFormService;
import com.javamalls.platform.service.IPaymentService;
import com.javamalls.platform.service.IPredepositLogService;
import com.javamalls.platform.service.IStoreCartService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.IStrategyGoodsItemService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.ITemplateService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;
import com.javamalls.platform.vo.GoodsCartVo;
import com.javamalls.platform.vo.OrderFormAddVo;
import com.utils.SendReqAsync;

/**购物车
 *                       
 * @Filename: CartViewAction.java
 * @Version: 2.7.0
 * @Author: 王阳
 * @Email: wangyang@163.com
 *
 */
@Controller
public class H5CartViewAction {
    private static final Logger       logger = Logger.getLogger(H5CartViewAction.class);
    @Autowired
    private ISysConfigService         configService;
    @Autowired
    private IUserConfigService        userConfigService;
    @Autowired
    private IGoodsService             goodsService;
    @Autowired
    private IGoodsSpecPropertyService goodsSpecPropertyService;
    @Autowired
    private IAddressService           addressService;
    @Autowired
    private IAreaService              areaService;
    @Autowired
    private IPaymentService           paymentService;
    @Autowired
    private IOrderFormService         orderFormService;
    @Autowired
    private IGoodsCartService         goodsCartService;
    @Autowired
    private IStoreService             storeService;
    @Autowired
    private IOrderFormLogService      orderFormLogService;
    @Autowired
    private IUserService              userService;
    @Autowired
    private ITemplateService          templateService;
    @Autowired
    private IPredepositLogService     predepositLogService;
    @Autowired
    private IGroupGoodsService        groupGoodsService;
    @Autowired
    private ICouponInfoService        couponInfoService;
    @Autowired
    private IStoreCartService         storeCartService;
    @Autowired
    private MsgTools                  msgTools;
    @Autowired
    private PaymentTools              paymentTools;
    @Autowired
    private PayTools                  payTools;
    @Autowired
    private TransportTools            transportTools;
    @Autowired
    private GoodsViewTools            goodsViewTools;

    @Autowired
    private AreaViewTools             areaViewTools;
    @Autowired
    private IGoodsItemService         goodsItemService;
    @Autowired
    private IStrategyGoodsItemService strategyGoodsItemService;
    @Autowired
    private IGoodsStepPriceService    goodsStepPriceService;
    @Autowired
    private SendReqAsync              sendReqAsync;

    private List<StoreCart> cart_calc(HttpServletRequest request) {

        List<StoreCart> cart = new ArrayList<StoreCart>();
        List<StoreCart> user_cart = new ArrayList<StoreCart>(); // 登录用户添加到购物车
        //  List<StoreCart> cookie_cart = new ArrayList<StoreCart>();// 游客将商品添加到购物车

        // 取当前用户
        User user = null;
        if (SecurityUserHolder.getCurrentUser() != null) {
            user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        }
        // 取购物车cookieid
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
            if (!"".equals(cart_session_id)) {
                // 如果该用户有开店铺，则将购物车中他自己的商品清除
                if (user.getStore() != null) {
                    params.clear();
                    params.put("cart_session_id", cart_session_id);
                    params.put("user_id", user.getId());
                    params.put("sc_status", Integer.valueOf(0));
                    params.put("store_id", user.getStore().getId());
                    List<StoreCart> store_cookie_cart = this.storeCartService
                        .query(
                            "select obj from StoreCart obj where (obj.cart_session_id=:cart_session_id or obj.user.id=:user_id) and obj.sc_status=:sc_status and obj.store.id=:store_id",
                            params, -1, -1);
                    for (StoreCart sc : store_cookie_cart) {
                        for (GoodsCart gc : ((StoreCart) sc).getGcs()) {
                            gc.getGsps().clear();
                            this.goodsCartService.delete(gc.getId());
                        }
                        this.storeCartService.delete(((StoreCart) sc).getId());
                    }
                }
                /*   params.clear();
                   params.put("cart_session_id", cart_session_id);
                   params.put("sc_status", Integer.valueOf(0));
                   cookie_cart = this.storeCartService
                       .query(
                           "select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status",
                           params, -1, -1);*/

                params.clear();
                params.put("user_id", user.getId());
                params.put("sc_status", Integer.valueOf(0));
                user_cart = this.storeCartService
                    .query(
                        "select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status",
                        params, -1, -1);
            } else {
                params.clear();
                params.put("user_id", user.getId());
                params.put("sc_status", Integer.valueOf(0));
                user_cart = this.storeCartService
                    .query(
                        "select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status",
                        params, -1, -1);
            }
        } /*else if (!"".equals(cart_session_id)) {
            // 游客购物车
            params.clear();
            params.put("cart_session_id", cart_session_id);
            params.put("sc_status", Integer.valueOf(0));
            cookie_cart = this.storeCartService
                .query(
                    "select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status",
                    params, -1, -1);
          }*/

        // cart里是按店铺分组存放的

        // 将user_cart中的添加进cart中，过滤掉相同的店铺。
        for (StoreCart sc : user_cart) {
            boolean sc_add = true;
            for (StoreCart sc1 : cart) {
                if (sc1.getStore().getId().equals(sc.getStore().getId())) {
                    sc_add = false;
                    break;
                }
            }
            if (sc_add) {
                cart.add(sc);
            }
        }

        // 将cookie_cart中的添加进cart中
        /*    for (StoreCart sc : cookie_cart) {
                boolean sc_add = true;
                for (StoreCart sc1 : cart) {
                    if (sc1.getStore().getId().equals(sc.getStore().getId())) {
                        sc_add = false;
                        // GoodsCart对应购买的某商品，包括数量
                        // 如StoreCart已经存在，则将cookie_cart中的商品合并，cookie_cart中原StoreCart删除
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
            }*/
        return cart;
    }

    @RequestMapping({ "/store/{storeId}.htm/mobile/cart_menu_detail.htm" })
    public ModelAndView cart_menu_detail(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("cart_menu_detail.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        List<StoreCart> cart = cart_calc(request);
        List<GoodsCart> list = new ArrayList<GoodsCart>();
        if (cart != null) {
            for (StoreCart sc : cart) {
                if (sc != null) {
                    list.addAll(sc.getGcs());
                }
            }
        }
        float total_price = 0.0F;
        for (GoodsCart gc : list) {
            float item_price = 0.0F;
            int item_num = 0;

            Goods goods = this.goodsService.getObjById(gc.getGoods().getId());
            if (CommUtil.null2String(gc.getCart_type()).equals("combin")) {// 组合套餐取组合价
                item_price = CommUtil.null2Float(goods.getCombin_price());
            } else if (CommUtil.null2String(gc.getCart_type()).equals("storeempty")) {// 无库存预定价
                item_price = CommUtil.null2Float(goods.getStoreempty_price());
            } else if (CommUtil.null2String(gc.getCart_type()).equals("dingjin")) {// 定金
                item_price = CommUtil.null2Float(goods.getDeposit_price());
            } else if (CommUtil.null2String(gc.getCart_type()).equals( // 捆绑商品
                "bind")) {
                item_price = CommUtil.null2Float(goods.getGoods_current_price())
                             * CommUtil.null2Float(goods.getBinddiscount());
            } else {
                //item_price = CommUtil.null2Float(goods.getGoods_current_price());
                item_price = CommUtil.null2Float(gc.getPrice());
                // total_price =
                // CommUtil.null2Float(Double.valueOf(CommUtil.mul(Integer.valueOf(gc.getCount()),
                // goods.getGoods_current_price()))) + total_price;
            }
            item_num = Integer.valueOf(gc.getCount());

            total_price += CommUtil.null2Float(Double.valueOf(CommUtil.mul(item_num, item_price)));
        }
        mv.addObject("total_price", Float.valueOf(total_price));
        mv.addObject("cart", list);
        return mv;
    }

    /**购物车以店铺进行分组   
     * StoreCart 为购物车组  不存放商品存放购买人、店铺、游客信息和购买总金额
     * GoodsCart 为组下面的购物车存放购物的商品及相关规格属性
     * @param request
     * @param response
     * @param id
     * @param count
     * @param price
     * @param gsp
     * @param buy_type
     */
    @RequestMapping({ "/store/{storeId}.htm/mobile/add_goods_cart.htm" })
    public void add_goods_cart(HttpServletRequest request, HttpServletResponse response, String id,
                               String count, String price, String gsp, String buy_type) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (CommUtil.null2Int(count) > 0) {
            String cart_session_id = "";
            // 取sessionid
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("cart_session_id")) {
                        cart_session_id = CommUtil.null2String(cookie.getValue());
                    }
                }
            }
            // 没有就生成一个
            if (cart_session_id.equals("")) {
                cart_session_id = UUID.randomUUID().toString();
                Cookie cookie = new Cookie("cart_session_id", cart_session_id);
                cookie.setDomain(CommUtil.generic_domain(request));
                response.addCookie(cookie);
            }
            List<StoreCart> cart = new ArrayList<StoreCart>();
            List<StoreCart> user_cart = new ArrayList<StoreCart>();
            //游客购物车
            //   List<StoreCart> cookie_cart = new ArrayList<StoreCart>();
            // 当前登录用户
            User user = SecurityUserHolder.getCurrentUser();

            Map<String, Object> params = new HashMap<String, Object>();
            StoreCart sc;
            //已登录用户
            if (user != null) {
                if (!cart_session_id.equals("")) {
                    //如果当前登录用户已开店  检查购物车中是否有自己店铺的商品有就删除
                    if (user.getStore() != null) {
                        params.clear();
                        params.put("cart_session_id", cart_session_id);
                        params.put("user_id", user.getId());
                        params.put("sc_status", Integer.valueOf(0));
                        params.put("store_id", user.getStore().getId());
                        List<StoreCart> store_cookie_cart = this.storeCartService
                            .query(
                                "select obj from StoreCart obj where (obj.cart_session_id=:cart_session_id or obj.user.id=:user_id) and obj.sc_status=:sc_status and obj.store.id=:store_id",
                                params, -1, -1);
                        for (Iterator localIterator1 = store_cookie_cart.iterator(); localIterator1
                            .hasNext();) {
                            sc = (StoreCart) localIterator1.next();
                            for (GoodsCart gc : sc.getGcs()) {
                                gc.getGsps().clear();//清除购物车商品 关联的规格属性
                                this.goodsCartService.delete(gc.getId());//清除购物车商品
                            }
                            //清除购物车组的信息（当前用户在自己店铺购物的记录）
                            this.storeCartService.delete(sc.getId());
                        }
                    }
                    /*     params.clear();
                         params.put("cart_session_id", cart_session_id);
                         params.put("sc_status", Integer.valueOf(0));
                         //查找游客购物车组
                         cookie_cart = this.storeCartService
                             .query(
                                 "select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status",
                                 params, -1, -1);*/

                    params.clear();
                    params.put("user_id", user.getId());
                    params.put("sc_status", Integer.valueOf(0));
                    //登录用户购物车组
                    user_cart = this.storeCartService
                        .query(
                            "select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status",
                            params, -1, -1);
                } else {
                    params.clear();
                    params.put("user_id", user.getId());
                    params.put("sc_status", Integer.valueOf(0));
                    user_cart = this.storeCartService
                        .query(
                            "select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status",
                            params, -1, -1);
                }
            }/* else if (!cart_session_id.equals("")) {
                //游客购物车组
                params.clear();
                params.put("cart_session_id", cart_session_id);
                params.put("sc_status", Integer.valueOf(0));
                cookie_cart = this.storeCartService
                    .query(
                        "select obj from StoreCart obj where obj.cart_session_id=:cart_session_id and obj.sc_status=:sc_status",
                        params, -1, -1);
             }*/
            //添加或更新购物车 
            for (StoreCart sc2 : user_cart) {
                boolean sc_add = true;
                for (StoreCart sc1 : cart) {
                    if (sc1.getStore().getId().equals(sc2.getStore().getId())) {
                        sc_add = false;
                    }
                }
                if (sc_add) {
                    cart.add(sc2);
                }
            }
            /*  for (StoreCart sc3 : cookie_cart) {
                  boolean sc_add = true;
                  for (StoreCart sc1 : cart) {
                      if (sc1.getStore().getId().equals(sc3.getStore().getId())) {
                          sc_add = false;
                          for (GoodsCart gc : sc3.getGcs()) {
                              gc.setSc(sc1);
                              this.goodsCartService.update(gc);
                          }
                          this.storeCartService.delete(sc3.getId());
                      }
                  }
                  if (sc_add) {
                      cart.add(sc3);
                  }
              }*/

            String[] gsp_ids = null;
            if (gsp != null && !"".equals(gsp_ids)) {
                gsp_ids = gsp.split(",");
                Arrays.sort(gsp_ids, new Comparator<String>() {
                    @Override
                    public int compare(String arg0, String arg1) {
                        return Integer.valueOf(arg0).compareTo(Integer.valueOf(arg1));
                    }
                });
            }

            boolean add = true;
            double total_price = 0.0D;
            double price_show = 0.0D;
            int total_count = 0;
            Iterator localIterator4 = cart.iterator();
            String[] gsp_ids1;
            //判断购物车中的商品和加入的是否是同一商品
            while (localIterator4.hasNext()) {
                sc = (StoreCart) localIterator4.next();

                Iterator localIterator5 = sc.getGcs().iterator();
                while (localIterator5.hasNext()) {
                    GoodsCart gc = (GoodsCart) localIterator5.next();

                    if ((gsp_ids != null) && (gsp_ids.length > 0) && (gc.getGsps() != null)
                        && (gc.getGsps().size() > 0)) {
                        gsp_ids1 = new String[gc.getGsps().size()];
                        for (int i = 0; i < gc.getGsps().size(); i++) {
                            gsp_ids1[i] = (gc.getGsps().get(i) != null ? ((GoodsSpecProperty) gc
                                .getGsps().get(i)).getId().toString() : "");
                        }
                        Arrays.sort(gsp_ids1, new Comparator<String>() {
                            @Override
                            public int compare(String arg0, String arg1) {
                                return Integer.valueOf(arg0).compareTo(Integer.valueOf(arg1));
                            }
                        });
                        if ((gc.getGoods().getId().toString().equals(id))
                            && (Arrays.equals(gsp_ids, gsp_ids1))) {
                            add = false;
                            double mulPrice = getMulPrice(id, count, user, gsp_ids);
                            if (mulPrice > 0) {
                                gc.setPrice(BigDecimal.valueOf(mulPrice));
                                price_show = mulPrice;
                            }
                            gc.setCount(CommUtil.null2Int(count));
                            this.goodsCartService.update(gc);
                        }
                    } else if (gc.getGoods().getId().toString().equals(id)) {//没有规格
                        add = false;
                        gc.setCount(CommUtil.null2Int(count));

                        //查询该货品是否有价格策略
                        double singlePrice = getSinglePrice(id, count, user);
                        if (singlePrice > 0) {
                            gc.setPrice(BigDecimal.valueOf(singlePrice));
                            price_show = singlePrice;
                        }
                        this.goodsCartService.update(gc);
                    }
                }
            }
            if (add) {
                Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
                String type = "save";
                sc = new StoreCart();
                for (StoreCart sc1 : cart) {
                    if (sc1.getStore().getId().equals(goods.getGoods_store().getId())) {
                        sc = sc1;
                        type = "update";
                        break;
                    }
                }
                sc.setStore(goods.getGoods_store());
                if (((String) type).equals("save")) {
                    sc.setCreatetime(new Date());
                    this.storeCartService.save(sc);
                } else {
                    this.storeCartService.update(sc);
                }
                GoodsCart gc = new GoodsCart();
                gc.setCreatetime(new Date());
                //price为传进来的   为了安全重新从数据库取价格和库存
                BigDecimal goodsPrice = goods.getStore_price();
                Integer goodsCount = CommUtil.null2Int(goods.getGoods_inventory());

                List<GoodsItem> goods_items = new ArrayList<GoodsItem>();
                String spec_combination = "";
                //购买商品的货品信息 wf
                GoodsItem goodsItem = null;

                if (gsp != null && !"".equals(gsp)) {
                    Arrays.sort(gsp_ids, new Comparator<String>() {
                        @Override
                        public int compare(String arg0, String arg1) {
                            return Integer.valueOf(arg0).compareTo(Integer.valueOf(arg1));
                        }
                    });
                    for (int i = 0; i < gsp_ids.length; i++) {
                        spec_combination = spec_combination + gsp_ids[i] + "_";
                    }

                    HashMap<String, Object> paramMap = new HashMap<String, Object>();
                    paramMap.put("goodsId", goods.getId());
                    paramMap.put("spec_combination", spec_combination);
                    goods_items = this.goodsItemService
                        .query(
                            "select obj from GoodsItem obj where obj.spec_combination =:spec_combination and obj.goods.id =:goodsId",
                            paramMap, -1, -1);
                } else {
                    HashMap<String, Object> paramMap = new HashMap<String, Object>();
                    paramMap.put("goodsId", goods.getId());
                    goods_items = this.goodsItemService.query(
                        "select obj from GoodsItem obj where obj.goods.id =:goodsId", paramMap, -1,
                        -1);

                }
                /*//若用户登录根据策略查询货品价格列表
                HashMap<Long, StrategyGoodsItem> strategyGoodsItems_map = new HashMap<Long, StrategyGoodsItem>();
                if (SecurityUserHolder.getCurrentUser() != null
                    && SecurityUserHolder.getCurrentUser().getStrategy() != null
                    && SecurityUserHolder.getCurrentUser().getStrategy().isDisabled() == false
                    && SecurityUserHolder.getCurrentUser().getStrategy().getStatus() == 1) {
                    User crrcUser = this.userService.getObjById(SecurityUserHolder.getCurrentUser()
                        .getId());
                    Strategy strategy = crrcUser.getStrategy();
                    if (strategy.isDisabled() == false && strategy.getStatus() == 1) {
                        List<StrategyGoodsItem> strategyGoodsItems = strategy
                            .getStrategyGoodsItems();
                        if (strategyGoodsItems != null && strategyGoodsItems.size() > 0) {
                            for (StrategyGoodsItem sgi : strategyGoodsItems) {
                                strategyGoodsItems_map.put(sgi.getGoods_item().getId(), sgi);
                            }
                        }
                    }
                }*/
                if (goods_items != null && goods_items.size() > 0) {
                    if (spec_combination != null && !"".equals(spec_combination)) {
                        for (GoodsItem item : goods_items) {
                            if (spec_combination.equals(item.getSpec_combination())) {
                                goodsCount = item.getGoods_inventory();
                                goodsPrice = this.goodsViewTools.getStrategyPrice(goods, item.getId().toString());
                                /*//wf 
                                goodsItem = item;
                                if (strategyGoodsItems_map.get(item.getId()) != null) {
                                    StrategyGoodsItem sgi = strategyGoodsItems_map
                                        .get(item.getId());
                                    goodsPrice = sgi.getPrice().doubleValue();
                                } else {
                                    goodsPrice = item.getGoods_price().doubleValue();

                                    
                                }*/
                            }
                        }
                    } else {
                        GoodsItem item = goods_items.get(0);
                        goodsCount = item.getGoods_inventory();
                        goodsPrice = this.goodsViewTools.getStrategyPrice(goods, item.getId().toString());
                        //wf 
                       /* goodsItem = item;
                        if (strategyGoodsItems_map.get(item.getId()) != null) {
                            StrategyGoodsItem sgi = strategyGoodsItems_map.get(item.getId());
                            goodsPrice = sgi.getPrice().doubleValue();
                        } else {
                            goodsPrice = item.getGoods_price().doubleValue();
                            
                        }*/
                    }
                }

                if ("".equals(CommUtil.null2String(buy_type))
                    || "undefined".equals(CommUtil.null2String(buy_type))) {
                    goodsCount = Integer.valueOf(count) > goodsCount ? goodsCount : Integer
                        .valueOf(count);
                    gc.setCount(CommUtil.null2Int(goodsCount));
                    gc.setPrice(BigDecimal.valueOf(CommUtil.null2Double(goodsPrice)));// 商品总价，这里是单价*数量
                    price_show = goodsPrice.doubleValue();
                } else if ("combin".equals(CommUtil.null2String(buy_type))) {
                    gc.setCount(1);
                    gc.setCart_type("combin");
                    gc.setPrice(goods.getCombin_price());
                } else if ("storeempty".equals(CommUtil.null2String(buy_type))) {
                    gc.setCount(CommUtil.null2Int(count));
                    gc.setCart_type("storeempty");
                    gc.setPrice(goods.getStoreempty_price());
                } else if ("dingjin".equals(CommUtil.null2String(buy_type))) {
                    gc.setCount(CommUtil.null2Int(count));
                    gc.setCart_type("dingjin");
                    gc.setPrice(goods.getDeposit_price());
                }

                gc.setSpec_id(gsp);
                gc.setGoods(goods);

                //wf
                gc.setGoodsItem(goodsItem);

                /* if (goods.getDelivery_status() == 2 && goods.getDg() != null
                     && goods.getDg().getD_delivery_goods() != null) {
                     gc.setD_delivery_goods(goods.getDg().getD_delivery_goods());
                 }*/

                String spec_info = "";
                GoodsSpecProperty spec_property;
                for (String gsp_id : gsp_ids) {
                    spec_property = this.goodsSpecPropertyService.getObjById(CommUtil
                        .null2Long(gsp_id));
                    ((GoodsCart) gc).getGsps().add(spec_property);
                    if (spec_property != null) {
                        spec_info = spec_property.getSpec().getName() + ":"
                                    + spec_property.getValue() + " " + spec_info;
                    }
                }
                ((GoodsCart) gc).setSc(sc);
                ((GoodsCart) gc).setSpec_info(spec_info);
                this.goodsCartService.save((GoodsCart) gc);
                sc.getGcs().add(gc);
                double cart_total_price = 0.0D;

                Iterator<GoodsCart> xxx = sc.getGcs().iterator();
                while (xxx.hasNext()) {
                    GoodsCart gc1 = (GoodsCart) xxx.next();
                    if (CommUtil.null2String(gc1.getCart_type()).equals("")) {
                        cart_total_price = cart_total_price + CommUtil.null2Double(gc1.getPrice())
                                           * gc1.getCount();
                    } else if (CommUtil.null2String(gc1.getCart_type()).equals("combin")) {
                        cart_total_price = cart_total_price
                                           + CommUtil.null2Double(gc1.getGoods().getCombin_price())
                                           * gc1.getCount();
                    } else if (CommUtil.null2String(gc1.getCart_type()).equals("storeempty")) {
                        cart_total_price = cart_total_price
                                           + CommUtil.null2Double(gc1.getGoods()
                                               .getStoreempty_price()) * gc1.getCount();
                    } else if (CommUtil.null2String(gc1.getCart_type()).equals("dingjin")) {
                        cart_total_price = cart_total_price
                                           + CommUtil
                                               .null2Double(gc1.getGoods().getDeposit_price())
                                           * gc1.getCount();
                    } else if (CommUtil.null2String(gc1.getCart_type()).equals("bind")) {
                        cart_total_price = cart_total_price
                                           + CommUtil.null2Double(gc1.getGoods()
                                               .getGoods_current_price())
                                           * CommUtil.null2Double(gc1.getGoods().getBinddiscount())
                                           * gc1.getCount();
                    }
                }
                sc.setTotal_price(BigDecimal.valueOf(CommUtil.formatMoney(Double
                    .valueOf(cart_total_price)))); // 购物车中某店铺总价
                if (user == null) {
                    sc.setCart_session_id(cart_session_id);
                } else {
                    sc.setUser(user);
                }
                if (((String) type).equals("save")) {
                    sc.setCreatetime(new Date());
                    this.storeCartService.save(sc);
                } else {
                    this.storeCartService.update(sc);
                }
                boolean cart_add = true;
                for (StoreCart sc1 : cart) {
                    if (sc1.getStore().getId().equals(sc.getStore().getId())) {
                        cart_add = false;
                    }
                }
                if (cart_add) {
                    cart.add(sc);
                }

                // 开始：捆绑商品功能     检查商品是否有捆绑商品有就一起加入购物车
                if (goods.getBind_status() == 2) {
                    for (Goods bindgoods : goods.getBind_goods()) {
                        type = "save";

                        gc = new GoodsCart();
                        gc.setCreatetime(new Date());
                        gc.setCount(CommUtil.null2Int(count));
                        gc.setPrice(BigDecimal.valueOf(CommUtil.null2Double(bindgoods
                            .getBinddiscount())
                                                       * CommUtil.null2Double(bindgoods
                                                           .getGoods_current_price())));
                        gc.setCart_type("bind");

                        gc.setGoods(bindgoods);
                        spec_info = "";
                        ((GoodsCart) gc).setSc(sc);
                        ((GoodsCart) gc).setSpec_info(spec_info);
                        this.goodsCartService.save((GoodsCart) gc);
                        sc.getGcs().add(gc);

                        cart_total_price = 0.0D;
                        xxx = sc.getGcs().iterator();
                        while (xxx.hasNext()) {
                            GoodsCart gc1 = (GoodsCart) xxx.next();
                            if (CommUtil.null2String(gc1.getCart_type()).equals("")) {
                                cart_total_price = cart_total_price
                                                   + CommUtil.null2Double(gc1.getGoods()
                                                       .getGoods_current_price()) * gc1.getCount();
                            } else if (CommUtil.null2String(gc1.getCart_type()).equals("combin")) {
                                cart_total_price = cart_total_price
                                                   + CommUtil.null2Double(gc1.getGoods()
                                                       .getCombin_price()) * gc1.getCount();
                            } else if (CommUtil.null2String(gc1.getCart_type())
                                .equals("storeempty")) {
                                cart_total_price = cart_total_price
                                                   + CommUtil.null2Double(gc1.getGoods()
                                                       .getStoreempty_price()) * gc1.getCount();
                            } else if (CommUtil.null2String(gc1.getCart_type()).equals("dingjin")) {
                                cart_total_price = cart_total_price
                                                   + CommUtil.null2Double(gc1.getGoods()
                                                       .getDeposit_price()) * gc1.getCount();
                            } else if (CommUtil.null2String(gc1.getCart_type()).equals("bind")) {
                                cart_total_price = cart_total_price
                                                   + CommUtil.null2Double(gc1.getGoods()
                                                       .getGoods_current_price())
                                                   * CommUtil.null2Double(gc1.getGoods()
                                                       .getBinddiscount()) * gc1.getCount();
                            }
                        }
                        sc.setTotal_price(BigDecimal.valueOf(CommUtil.formatMoney(Double
                            .valueOf(cart_total_price)))); // 购物车中某店铺总价
                        if (((String) type).equals("save")) {
                            sc.setCreatetime(new Date());
                            this.storeCartService.save(sc);
                        } else {
                            this.storeCartService.update(sc);
                        }
                        cart_add = true;
                        for (StoreCart sc1 : cart) {
                            if (sc1.getStore().getId().equals(sc.getStore().getId())) {
                                cart_add = false;
                            }
                        }
                        if (cart_add) {
                            cart.add(sc);
                        }
                    }
                }
                // 结束：捆绑商品功能
            }
            // 购物车总价
            int goods_total_count = 0;
            for (StoreCart sc1 : cart) {
                total_count += sc1.getGcs().size();
                double total_price2 = 0.0D;
                for (GoodsCart gc1 : sc1.getGcs()) {
                    total_price = total_price
                                  + CommUtil.mul(gc1.getPrice(), Integer.valueOf(gc1.getCount()));
                    total_price2 = total_price2
                                   + CommUtil.mul(gc1.getPrice(), Integer.valueOf(gc1.getCount()));
                    goods_total_count += Integer.valueOf(gc1.getCount());
                }
                if (!add) {
                    sc1.setTotal_price(BigDecimal.valueOf(CommUtil.formatMoney(Double
                        .valueOf(total_price2))));
                    this.storeCartService.update(sc1);
                }
            }
            map.put("count", Integer.valueOf(total_count));
            map.put("total_price", Double.valueOf(total_price));
            map.put("goods_total_count", goods_total_count);
            map.put("price_show", Double.valueOf(price_show));
        } else {
            count = "0";
            map.put("count", "0");
            map.put("goods_total_count", 0);
        }

        String ret = Json.toJson(map, JsonFormat.compact());
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double getMulPrice(String id, String count, User user, String[] gsp_ids) {
        double mulPrice = 0.0D;
        List<GoodsItem> goods_items = new ArrayList<GoodsItem>();
        String spec_combination = "";
        for (int i = 0; i < gsp_ids.length; i++) {
            spec_combination = spec_combination + gsp_ids[i] + "_";
        }

        HashMap<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("goodsId", CommUtil.null2Long(id));
        paramMap.put("spec_combination", spec_combination);
        goods_items = this.goodsItemService
            .query(
                "select obj from GoodsItem obj where obj.disabled=false and obj.spec_combination =:spec_combination and obj.goods.id =:goodsId",
                paramMap, -1, -1);

        if (goods_items != null && goods_items.size() > 0) {
            GoodsItem item = goods_items.get(0);
            User currentUser = this.userService.getObjById(user.getId());
            Strategy strategy = currentUser.getStrategy();
            if (currentUser != null && strategy != null && strategy.isDisabled() == false
                && strategy.getStatus() == 1) {
                //查询策略的货品的价格
                paramMap.clear();
                paramMap.put("strategyId", user.getStrategy().getId());
                paramMap.put("goodItemId", item.getId());
                List<StrategyGoodsItem> list = this.strategyGoodsItemService
                    .query(
                        "select obj from StrategyGoodsItem obj where obj.strategy.id=:strategyId and obj.goods_item.id=:goodItemId",
                        paramMap, 0, 1);
                if (list == null || list.size() == 0) {//如果没有策略则查询阶梯定价
                    if (item != null && item.getStep_price_state() == 1) {
                        Map<String, Object> querymap = new HashMap<String, Object>();
                        querymap.put("goods_item_id", CommUtil.null2Int(item.getId()));
                        querymap.put("begin_num", CommUtil.null2Int(count));
                        querymap.put("end_num", CommUtil.null2Int(count));
                        String sql = "select obj from GoodsStepPrice obj where obj.disabled=false and obj.goods_item_id=:goods_item_id and (obj.begin_num<=:begin_num and obj.end_num>=:end_num)";
                        List<GoodsStepPrice> steplist = this.goodsStepPriceService.query(sql,
                            querymap, -1, -1);
                        if (steplist != null && steplist.size() > 0) {
                            mulPrice = steplist.get(0).getPrice().doubleValue();
                        }
                    }
                }
            } else {
                if (item != null && item.getStep_price_state() == 1) {
                    Map<String, Object> querymap = new HashMap<String, Object>();
                    querymap.put("goods_item_id", CommUtil.null2Int(item.getId()));
                    querymap.put("begin_num", CommUtil.null2Int(count));
                    querymap.put("end_num", CommUtil.null2Int(count));
                    String sql = "select obj from GoodsStepPrice obj where obj.disabled=false and obj.goods_item_id=:goods_item_id and (obj.begin_num<=:begin_num and obj.end_num>=:end_num)";
                    List<GoodsStepPrice> steplist = this.goodsStepPriceService.query(sql, querymap,
                        -1, -1);
                    if (steplist != null && steplist.size() > 0) {
                        mulPrice = steplist.get(0).getPrice().doubleValue();
                    }
                }
            }
        }
        return mulPrice;
    }

    private double getSinglePrice(String id, String count, User user) {
        User currentUser = this.userService.getObjById(user.getId());
        Strategy strategy = currentUser.getStrategy();
        Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
        List<GoodsItem> goods_item_list = goods.getGoods_item_list();
        double price2 = 0.0D;
        if (goods_item_list != null && goods_item_list.size() > 0) {
            GoodsItem item = goods_item_list.get(0);

            if (currentUser != null && strategy != null && strategy.isDisabled() == false
                && strategy.getStatus() == 1) {
                //查询策略的货品的价格
                Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("strategyId", user.getStrategy().getId());
                paramMap.put("goodItemId", item.getId());
                List<StrategyGoodsItem> list = this.strategyGoodsItemService
                    .query(
                        "select obj from StrategyGoodsItem obj where obj.strategy.id=:strategyId and obj.goods_item.id=:goodItemId",
                        paramMap, 0, 1);
                logger.debug(list + "===************");

                if (list == null || list.size() == 0) {//如果没有策略则查询阶梯定价
                    if (item != null && item.getStep_price_state() == 1) {
                        Map<String, Object> querymap = new HashMap<String, Object>();
                        querymap.put("goods_item_id", CommUtil.null2Int(item.getId()));
                        querymap.put("begin_num", CommUtil.null2Int(count));
                        querymap.put("end_num", CommUtil.null2Int(count));
                        String sql = "select obj from GoodsStepPrice obj where obj.disabled=false and obj.goods_item_id=:goods_item_id and (obj.begin_num<=:begin_num and obj.end_num>=:end_num)";
                        List<GoodsStepPrice> steplist = this.goodsStepPriceService.query(sql,
                            querymap, -1, -1);
                        if (steplist != null && steplist.size() > 0) {
                            price2 = steplist.get(0).getPrice().doubleValue();
                        }
                    }
                }
            } else {
                if (item != null && item.getStep_price_state() == 1) {
                    Map<String, Object> querymap = new HashMap<String, Object>();
                    querymap.put("goods_item_id", CommUtil.null2Int(item.getId()));
                    querymap.put("begin_num", CommUtil.null2Int(count));
                    querymap.put("end_num", CommUtil.null2Int(count));
                    String sql = "select obj from GoodsStepPrice obj where obj.disabled=false and obj.goods_item_id=:goods_item_id and (obj.begin_num<=:begin_num and obj.end_num>=:end_num)";
                    List<GoodsStepPrice> steplist = this.goodsStepPriceService.query(sql, querymap,
                        -1, -1);
                    if (steplist != null && steplist.size() > 0) {
                        price2 = steplist.get(0).getPrice().doubleValue();
                    }
                }
            }
        }
        return price2;
    }

    /**删除购物车商品
     * @param request
     * @param response
     * @param id
     * @param store_id
     */
    @RequestMapping({ "/store/{storeId}.htm/mobile/remove_goods_cart.htm" })
    public void remove_goods_cart(HttpServletRequest request, HttpServletResponse response,
                                  String id, String store_id) {
        GoodsCart gc = this.goodsCartService.getObjById(CommUtil.null2Long(id));
        StoreCart the_sc = gc.getSc();
        gc.getGsps().clear();
        // 捆绑商品同时处理
        String bindids = "";
        String bindcount = "";
        if (gc.getGoods().getBind_status() == 2) {
            for (Goods goodsitem : gc.getGoods().getBind_goods()) {
                for (GoodsCart gcitem : the_sc.getGcs()) {
                    if (gcitem.getGoods().getId().longValue() == goodsitem.getId().longValue()) {
                        bindids += gcitem.getId() + "-";
                        int newcount = gcitem.getCount() - gc.getCount();
                        if (newcount < 0)
                            newcount = 0;
                        bindcount += newcount + "-";
                        break;
                    }
                }
            }
            if (bindids.endsWith("-"))
                bindids = bindids.substring(0, bindids.length() - 1);
            if (bindcount.endsWith("-"))
                bindcount = bindcount.substring(0, bindcount.length() - 1);
        }
        // 捆绑商品结束

        this.goodsCartService.delete(CommUtil.null2Long(id));
        if (the_sc.getGcs().size() == 0) {
            this.storeCartService.delete(the_sc.getId());
        }
        List<StoreCart> cart = cart_calc(request);
        double total_price = 0.0D;
        double sc_total_price = 0.0D;
        double count = 0.0D;
        for (StoreCart sc2 : cart) {
            for (GoodsCart gc1 : sc2.getGcs()) {
                total_price = CommUtil.null2Double(gc1.getPrice()) * gc1.getCount() + total_price;
                count += 1.0D;
                if ((store_id != null) && (!store_id.equals(""))
                    && (sc2.getStore().getId().toString().equals(store_id))) {
                    sc_total_price = sc_total_price + CommUtil.null2Double(gc1.getPrice())
                                     * gc1.getCount();
                    sc2.setTotal_price(BigDecimal.valueOf(sc_total_price));
                }
            }
            this.storeCartService.update(sc2);
        }
        request.getSession(false).setAttribute("cart", cart);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("count", Double.valueOf(count));
        map.put("total_price", Double.valueOf(total_price));
        map.put("sc_total_price", Double.valueOf(sc_total_price));
        map.put("bindids", bindids);
        map.put("bindcount", bindcount);
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

    @RequestMapping({ "/store/{storeId}.htm/mobile/goods_count_adjust.htm" })
    public void goods_count_adjust(HttpServletRequest request, HttpServletResponse response,
                                   String cart_id, String store_id, String count) {
        List<StoreCart> cart = cart_calc(request);

        double goods_total_price = 0.0D;
        String error = "100";
        Goods goods = null;
        String cart_type = "";
        int precount = 0;
        String spec_id = "";
        String message = "";
        int inventoryCount = 0;//库存数量
        for (StoreCart sc : cart) {
            for (GoodsCart gc : sc.getGcs()) {
                if (gc.getId().toString().equals(cart_id)) {
                    goods = gc.getGoods();
                    precount = gc.getCount();
                    cart_type = CommUtil.null2String(gc.getCart_type());
                    spec_id = CommUtil.null2String(gc.getSpec_id());
                }
            }
        }

        if (!"".equals(CommUtil.null2String(spec_id)) && goods != null) {
            //有规格时
            /* List<HashMap<String, Object>> list = (List<HashMap<String, Object>>) Json.fromJson(
                 ArrayList.class, goods.getGoods_inventory_detail());*/
            List<GoodsItem> goods_item_list = goods.getGoods_item_list();
            String[] gspIds = spec_id.split(",");
            for (GoodsItem temp : goods_item_list) {
                String[] temp_ids = CommUtil.null2String(temp.getSpec_combination()).split("_");
                Arrays.sort(gspIds);
                Arrays.sort(temp_ids);
                if (Arrays.equals(gspIds, temp_ids)) {
                    inventoryCount = temp.getGoods_inventory();
                    if (CommUtil.null2Int(temp.getGoods_inventory()) < CommUtil.null2Int(count)) {
                        count = CommUtil.null2String(temp.getGoods_inventory());
                        message = "当前规格库存为：" + count;
                    }
                }
            }
        } else {
            //没有规格时
            GoodsItem goodsItem = goods.getGoods_item_list().get(0);
            inventoryCount = goodsItem.getGoods_inventory();
            if (CommUtil.null2Int(count) > inventoryCount) {
                count = CommUtil.null2String(inventoryCount);
                message = "当前规格库存为：" + count;
            }
        }
        if (cart_type.equals("")) {
            if (goods.getGroup_buy() == 2) {
                GroupGoods gg = new GroupGoods();
                for (GroupGoods gg1 : goods.getGroup_goods_list()) {
                    if (gg1.getGg_goods().equals(goods.getId())) {
                        gg = gg1;
                    }
                }
                if (gg.getGg_count() >= CommUtil.null2Int(count)) {

                    for (StoreCart sc : cart) {
                        List<GoodsCart> gcs = sc.getGcs();
                        for (int i = 0; i < gcs.size(); i++) {
                            GoodsCart gc = gcs.get(i);
                            GoodsCart gc1 = gc;
                            if (gc.getId().toString().equals(cart_id)) {
                                ((StoreCart) sc).setTotal_price(BigDecimal.valueOf(CommUtil.add(
                                    ((StoreCart) sc).getTotal_price(),
                                    Double.valueOf((CommUtil.null2Int(count) - gc.getCount())
                                                   * CommUtil.null2Double(gc.getPrice())))));
                                gc.setCount(CommUtil.null2Int(count));
                                gc1 = gc;
                                ((StoreCart) sc).getGcs().remove(gc);
                                ((StoreCart) sc).getGcs().add(gc1);
                                goods_total_price = CommUtil.null2Double(gc1.getPrice())
                                                    * gc1.getCount();
                                this.storeCartService.update((StoreCart) sc);
                            }
                        }
                    }

                } else {
                    error = "300";
                }
            } else if (inventoryCount >= CommUtil.null2Int(count)) {
                for (StoreCart sc : cart) {
                    List<GoodsCart> gcs = sc.getGcs();
                    if (sc.getStore().getId().equals(CommUtil.null2Long(store_id))) {//找到更改数量的店铺后跳出
                        BigDecimal sc_total_price = new BigDecimal(0);
                        List<GoodsCart> gcs2 = new ArrayList<GoodsCart>(gcs);
                        for (int i = 0; i < gcs2.size(); i++) {
                            GoodsCart gc = gcs2.get(i);
                            GoodsCart gc1 = gc;
                            if (gc.getId().toString().equals(cart_id)) {
                                gc.setCount(CommUtil.null2Int(count));
                                gc1 = gc;
                                sc.getGcs().remove(gc);
                                sc.getGcs().add(gc1);
                                goods_total_price = Double.parseDouble(gc1.getPrice().toString())
                                                    * gc1.getCount();

                                sc_total_price = sc_total_price.add(BigDecimal
                                    .valueOf(goods_total_price));
                                this.storeCartService.update(sc);
                            } else {
                                sc_total_price = sc_total_price.add(BigDecimal.valueOf(Double
                                    .parseDouble(gc.getPrice().toString()) * gc.getCount()));
                            }
                        }

                        sc.setTotal_price(sc_total_price.setScale(2, BigDecimal.ROUND_HALF_EVEN));
                        this.storeCartService.update(sc);
                        break;
                    }

                }
            } else {
                error = "200";
            }
        }
        if (cart_type.equals("combin")) {
            if (inventoryCount >= CommUtil.null2Int(count)) {

                for (StoreCart sc : cart) {
                    List<GoodsCart> gcs = sc.getGcs();
                    for (int i = 0; i < gcs.size(); i++) {
                        GoodsCart gc = (GoodsCart) sc.getGcs().get(i);
                        GoodsCart gc1 = (GoodsCart) gc;
                        if (((GoodsCart) gc).getId().toString().equals(cart_id)) {
                            sc.setTotal_price(BigDecimal.valueOf(CommUtil.add(sc.getTotal_price(),
                                Float.valueOf((CommUtil.null2Int(count) - ((GoodsCart) gc)
                                    .getCount())
                                              * CommUtil.null2Float(((GoodsCart) gc).getGoods()
                                                  .getCombin_price())))));
                            ((GoodsCart) gc).setCount(CommUtil.null2Int(count));
                            gc1 = (GoodsCart) gc;
                            sc.getGcs().remove(gc);
                            sc.getGcs().add(gc1);
                            goods_total_price = Double.parseDouble(gc1.getPrice().toString())
                                                * gc1.getCount();
                            this.storeCartService.update(sc);
                        }
                    }
                }
            } else {
                error = "200";
            }
        }
        if (cart_type.equals("dingjin")) {
            if (inventoryCount >= CommUtil.null2Int(count)) {
                for (StoreCart sc : cart) {
                    List<GoodsCart> gcs = sc.getGcs();
                    for (int i = 0; i < gcs.size(); i++) {
                        GoodsCart gc = (GoodsCart) sc.getGcs().get(i);
                        GoodsCart gc1 = (GoodsCart) gc;
                        if (((GoodsCart) gc).getId().toString().equals(cart_id)) {
                            sc.setTotal_price(BigDecimal.valueOf(CommUtil.add(sc.getTotal_price(),
                                Float.valueOf((CommUtil.null2Int(count) - ((GoodsCart) gc)
                                    .getCount())
                                              * CommUtil.null2Float(((GoodsCart) gc).getGoods()
                                                  .getDeposit_price())))));
                            ((GoodsCart) gc).setCount(CommUtil.null2Int(count));
                            gc1 = (GoodsCart) gc;
                            sc.getGcs().remove(gc);
                            sc.getGcs().add(gc1);
                            goods_total_price = Double.parseDouble(gc1.getPrice().toString())
                                                * gc1.getCount();
                            this.storeCartService.update(sc);
                        }
                    }
                }
            } else {
                error = "200";
            }
        }
        if (cart_type.equals("storeempty")) {
            for (StoreCart sc : cart) {
                List<GoodsCart> gcs = sc.getGcs();
                for (int i = 0; i < gcs.size(); i++) {
                    GoodsCart gc = (GoodsCart) sc.getGcs().get(i);
                    GoodsCart gc1 = (GoodsCart) gc;
                    if (((GoodsCart) gc).getId().toString().equals(cart_id)) {
                        sc.setTotal_price(BigDecimal.valueOf(CommUtil.add(
                            sc.getTotal_price(),
                            Float.valueOf((CommUtil.null2Int(count) - ((GoodsCart) gc).getCount())
                                          * CommUtil.null2Float(((GoodsCart) gc).getGoods()
                                              .getStoreempty_price())))));
                        ((GoodsCart) gc).setCount(CommUtil.null2Int(count));
                        gc1 = (GoodsCart) gc;
                        sc.getGcs().remove(gc);
                        sc.getGcs().add(gc1);
                        goods_total_price = Double.parseDouble(gc1.getPrice().toString())
                                            * gc1.getCount();
                        this.storeCartService.update(sc);
                    }
                }
            }
        } else if (cart_type.equals("bind")) {
            if (inventoryCount >= CommUtil.null2Int(count)) {
                for (StoreCart sc : cart) {
                    List<GoodsCart> gcs = sc.getGcs();
                    for (int i = 0; i < gcs.size(); i++) {
                        GoodsCart gc = (GoodsCart) sc.getGcs().get(i);
                        GoodsCart gc1 = gc;
                        if (gc.getId().toString().equals(cart_id)) {
                            sc.setTotal_price(BigDecimal.valueOf(CommUtil.add(
                                sc.getTotal_price(),
                                Double.valueOf((CommUtil.null2Int(count) - gc.getCount())
                                               * Double.parseDouble(gc.getPrice().toString())))));
                            gc.setCount(CommUtil.null2Int(count));
                            gc1 = gc;
                            sc.getGcs().remove(gc);
                            sc.getGcs().add(gc1);
                            goods_total_price = Double.parseDouble(gc1.getPrice().toString())
                                                * gc1.getCount();
                            this.storeCartService.update(sc);
                        }
                    }
                }
            } else {
                error = "200";
            }
        }

        // 捆绑商品同时处理
        String bindids = "";
        String bindcount = "";

        if (goods.getBind_status() == 2) {
            for (StoreCart sc : cart) {
                if (sc.getStore().getId().equals(CommUtil.null2Long(store_id))) {
                    List<GoodsCart> gcs = sc.getGcs();
                    for (Goods goodsitem : goods.getBind_goods()) {
                        for (int i = 0; i < gcs.size(); i++) {
                            GoodsCart gc = (GoodsCart) sc.getGcs().get(i);
                            if (gc.getGoods().getId().longValue() == goodsitem.getId().longValue()) {
                                bindids += gc.getId() + "-";
                                int newcount = gc.getCount()
                                               + (CommUtil.null2Int(count) - precount);
                                if (newcount < 0)
                                    newcount = 0;
                                bindcount += newcount + "-";
                                break;
                            }
                        }
                    }
                }
                break;
            }
            if (bindids.endsWith("-"))
                bindids = bindids.substring(0, bindids.length() - 1);
            if (bindcount.endsWith("-"))
                bindcount = bindcount.substring(0, bindcount.length() - 1);
        }

        // 捆绑商品结束

        DecimalFormat df = new DecimalFormat("0.00");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("count", count);

        BigDecimal sc_total_price = new BigDecimal(0);
        for (StoreCart storeCart : cart) {
            if (storeCart.getStore().getId().equals(CommUtil.null2Long(store_id))) {
                sc_total_price = sc_total_price.add(storeCart.getTotal_price());
            }
        }
        map.put("sc_total_price", Double.valueOf(df.format(sc_total_price)));
        map.put("goods_total_price", Double.valueOf(df.format(goods_total_price)));
        map.put("error", error);

        map.put("bindids", bindids);
        map.put("bindcount", bindcount);
        map.put("message", message);

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

    @SecurityMapping(title = "查看购物车", value = "/mobile/goods_cart1.htm*", rtype = "buyer", rname = "购物流程1", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/store/{storeId}.htm/mobile/goods_cart1.htm" })
    public ModelAndView goods_cart1(HttpServletRequest request, HttpServletResponse response,
                                    @PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("h5/cart1.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        User user = SecurityUserHolder.getCurrentUser();
        if (user != null) {
            List<StoreCart> cart = cart_calc(request);
            if (cart != null) {
                Store store = user.getStore() != null ? SecurityUserHolder.getCurrentUser()
                    .getStore() : null;
                if (store != null) {
                    for (StoreCart sc : cart) {
                        if (sc.getStore().getId().equals(store.getId())) {
                            for (GoodsCart gc : sc.getGcs()) {
                                gc.getGsps().clear();
                                this.goodsCartService.delete(gc.getId());
                            }
                            sc.getGcs().clear();
                            this.storeCartService.delete(sc.getId());
                        }
                    }
                }

                mv.addObject("cartSize", cart.size());
                request.getSession(false).setAttribute("cart", cart);
                mv.addObject("cart", cart);
                mv.addObject("goodsViewTools", this.goodsViewTools);
            } else {
                mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "购物车信息为空");
                mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
            }
            /* if (this.configService.getSysConfig().isZtc_status()) {
                 List<Goods> ztc_goods = null;
                 Map<String, Object> ztc_map = new HashMap<String, Object>();
                 ztc_map.put("ztc_status", Integer.valueOf(3));
                 ztc_map.put("now_date", new Date());
                 ztc_map.put("ztc_gold", Integer.valueOf(0));
                 Object goods = this.goodsService
                     .query(
                         "select obj from Goods obj where obj.ztc_status =:ztc_status and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold order by obj.ztc_dredge_price desc",
                         ztc_map, -1, -1);

                 ztc_goods = randomZtcGoods((List) goods);
                 mv.addObject("ztc_goods", ztc_goods);
             }*/
        } else {
            mv = new JModelAndView("h5/login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("storeId", storeId);
        }
        return mv;
    }

    private List<Goods> randomZtcGoods(List<Goods> goods) {
        Random random = new Random();
        int random_num = 0;
        int num = 0;
        if (goods.size() - 8 > 0) {
            num = goods.size() - 8;
            random_num = random.nextInt(num);
        }
        Map<String, Object> ztc_map = new HashMap<String, Object>();
        ztc_map.put("ztc_status", Integer.valueOf(3));
        ztc_map.put("now_date", new Date());
        ztc_map.put("ztc_gold", Integer.valueOf(0));
        List<Goods> ztc_goods = this.goodsService
            .query(
                "select obj from Goods obj where obj.ztc_status =:ztc_status and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold order by obj.ztc_dredge_price desc",

                ztc_map, random_num, 8);
        Collections.shuffle(ztc_goods);
        return ztc_goods;
    }

    @SecurityMapping(title = "确认购物车填写地址", value = "/mobile/goods_cart2.htm*", rtype = "buyer", rname = "购物流程2", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/store/{storeId}.htm/mobile/goods_cart2.htm" })
    public ModelAndView goods_cart2(HttpServletRequest request, HttpServletResponse response,
                                    String store_id) {
        ModelAndView mv = new JModelAndView("h5/cart2.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        List<StoreCart> cart = cart_calc(request);
        StoreCart sc = null;
        if (cart != null) {
            for (StoreCart sc1 : cart) {
                if (sc1.getStore().getId().equals(CommUtil.null2Long(store_id))) {
                    sc = sc1;
                    break;
                }
            }
        }
        if (sc != null) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
            Object addrs = this.addressService.query(
                "select obj from Address obj where obj.disabled = 0 "
                        + "and obj.user.id=:user_id order by obj.createtime desc", params, 0, 1);
            mv.addObject("addrs", addrs);
            if ((store_id == null) || (store_id.equals(""))) {
                store_id = sc.getStore().getId().toString();
            }
            String cart_session = CommUtil.randomString(32);
            request.getSession(false).setAttribute("cart_session", cart_session);
            params.clear();
            params.put("coupon_order_amount", sc.getTotal_price());
            params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
            params.put("coupon_begin_time", new Date());
            params.put("coupon_end_time", new Date());
            params.put("status", Integer.valueOf(0));
            List<CouponInfo> couponinfos = this.couponInfoService.query(
                "select obj from CouponInfo obj where "
                        + "obj.coupon.coupon_order_amount<=:coupon_order_amount "
                        + "and obj.status=:status and obj.user.id=:user_id and "
                        + "obj.coupon.coupon_begin_time<=:coupon_begin_time and "
                        + "obj.coupon.coupon_end_time>=:coupon_end_time", params, -1, -1);
            mv.addObject("couponinfos", couponinfos);
            mv.addObject("sc", sc);
            mv.addObject("cart_session", cart_session);
            mv.addObject("store_id", store_id);
            mv.addObject("transportTools", this.transportTools);
            mv.addObject("goodsViewTools", this.goodsViewTools);

            boolean goods_delivery = false;
            List<GoodsCart> goodCarts = sc.getGcs();
            for (GoodsCart gc : goodCarts) {
                if (gc.getGoods().getGoods_choice_type() == 0) {
                    goods_delivery = true;
                    break;
                }
            }
            mv.addObject("goods_delivery", Boolean.valueOf(goods_delivery));
            mv.addObject("areaViewTools", this.areaViewTools);
        } else {
            mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "购物车信息为空");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
        }
        List<Area> areas = this.areaService.query(
            "select obj from Area obj where obj.parent.id is null", null, -1, -1);
        mv.addObject("areas", areas);
        return mv;
    }

    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/cart_address_ajax.htm" })
    public ModelAndView address(HttpServletRequest request, HttpServletResponse response,
                                String currentPage, String orderBy, String orderType) {
        ModelAndView mv = new JModelAndView("h5/cart_address_ajax.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        //登录后使用
        if (SecurityUserHolder.getCurrentUser() != null) {
            String params = "";
            AddressQueryObject qo = new AddressQueryObject(currentPage, mv, orderBy, orderType);
            qo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser()
                .getId()), "=");

            qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");

            IPageList pList = this.addressService.list(qo);
            CommUtil.saveIPageList2ModelAndView(url + "/mobile/buyer/address.htm", "", params,
                pList, mv);
            List<Area> areas = this.areaService.query(
                "select obj from Area obj where obj.parent.id is null", null, -1, -1);
            mv.addObject("areas", areas);
        }
        String addr_id = request.getParameter("addr_id");
        mv.addObject("addr_id", addr_id);
        return mv;
    }

    @SecurityMapping(title = "完成订单提交进入支付", value = "/mobile/goods_cart3.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/store/{storeId}.htm/mobile/goods_cart3.htm" })
    public ModelAndView goods_cart3(HttpServletRequest request, HttpServletResponse response,
                                    String cart_session, String store_id, String addr_id,
                                    String coupon_id) throws Exception {
        ModelAndView mv = new JModelAndView("h5/cart3.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        String cart_session1 = (String) request.getSession(false).getAttribute("cart_session");
        List<StoreCart> cart = cart_calc(request);
        if (cart != null && cart.size() > 0) {
            if (CommUtil.null2String(cart_session1).equals(cart_session)) {
                request.getSession(false).removeAttribute("cart_session");
                WebForm wf = new WebForm();
                OrderForm of = (OrderForm) wf.toPo(request, OrderForm.class);
                of.setCreatetime(new Date());
                of.setOrder_id(SecurityUserHolder.getCurrentUser().getId()
                               + CommUtil.formatTime("yyyyMMddHHmmss", new Date()));
                Address addr = this.addressService.getObjById(CommUtil.null2Long(addr_id));
                of.setAddr(addr);
                of.setOrder_status(10);
                of.setUser(SecurityUserHolder.getCurrentUser());
                of.setStore(this.storeService.getObjById(CommUtil.null2Long(store_id)));
                of.setTotalPrice(BigDecimal.valueOf(CommUtil.add(of.getGoods_amount(),
                    of.getShip_price())));
                if (!CommUtil.null2String(coupon_id).equals("")) {
                    CouponInfo ci = this.couponInfoService
                        .getObjById(CommUtil.null2Long(coupon_id));
                    ci.setStatus(1);
                    this.couponInfoService.update(ci);
                    of.setCi(ci);
                    of.setTotalPrice(BigDecimal.valueOf(CommUtil.subtract(of.getTotalPrice(), ci
                        .getCoupon().getCoupon_amount())));
                }
                of.setOrder_type("mobile");
                // 保存订单
                this.orderFormService.save(of);
                // 添加店铺会员

                GoodsCart gc;
                for (StoreCart sc : cart) {
                    if (sc.getStore().getId().toString().equals(store_id)) {
                        for (Iterator localIterator2 = sc.getGcs().iterator(); localIterator2
                            .hasNext();) {
                            gc = (GoodsCart) localIterator2.next();
                            gc.setOf(of);
                            this.goodsCartService.update(gc);
                        }
                        sc.setCart_session_id(null);
                        sc.setUser(SecurityUserHolder.getCurrentUser());
                        sc.setSc_status(1);
                        this.storeCartService.update(sc);
                        break;
                    }
                }
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (int i = 0; i < cookies.length; i++) {
                        Cookie cookie = cookies[i];
                        if (cookie.getName().equals("cart_session_id")) {
                            cookie.setDomain(CommUtil.generic_domain(request));
                            cookie.setValue("");
                            cookie.setMaxAge(0);
                            response.addCookie(cookie);
                        }
                    }
                }

                logger.info("买家提交订单调用接口【开始】");
                //调用订单接口
                OrderFormAddVo orderFormAddVo = new OrderFormAddVo();
                orderFormAddVo.setAddr(of.getAddr());
                orderFormAddVo.setCreatetime(of.getCreatetime());
                orderFormAddVo.setDelivery_date(of.getDelivery_date());
                orderFormAddVo.setDisabled(of.isDisabled());
                orderFormAddVo.setEc(of.getEc());
                /*orderFormAddVo.setGcs(of.getGcs());*/
                orderFormAddVo.setGoods_amount(of.getGoods_amount());
                orderFormAddVo.setId(of.getId());
                orderFormAddVo.setInvoice(of.getInvoice());
                orderFormAddVo.setInvoiceType(of.getInvoiceType());
                orderFormAddVo.setMsg(of.getMsg());
                orderFormAddVo.setOrder_id(of.getOrder_id());
                orderFormAddVo.setOrder_status(of.getOrder_status());
                orderFormAddVo.setOrder_type(of.getOrder_type());
                orderFormAddVo.setPayment(of.getPayment());
                orderFormAddVo.setPayTime(of.getPayTime());
                orderFormAddVo.setReturn_content(of.getReturn_content());
                orderFormAddVo.setReturn_shipCode(of.getReturn_shipCode());
                orderFormAddVo.setReturn_shipTime(of.getReturn_shipTime());
                orderFormAddVo.setShip_price(of.getShip_price());
                orderFormAddVo.setShipCode(of.getShipCode());
                orderFormAddVo.setShipTime(of.getShipTime());
                orderFormAddVo.setStore_id(of.getStore().getId());
                orderFormAddVo.setTotalPrice(of.getTotalPrice());
                orderFormAddVo.setTransport(of.getTransport());
                orderFormAddVo.setUser_id(of.getUser().getId());
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("of_id", of.getId());
                String sql = "select obj from GoodsCart obj where obj.of.id=:of_id";
                List<GoodsCart> gcs = goodsCartService.query(sql, map, -1, -1);
                List<GoodsCartVo> goodsCartVos = new ArrayList<GoodsCartVo>();
                for (GoodsCart goodsCart : gcs) {
                    GoodsCartVo goodsCartVo = new GoodsCartVo();
                    goodsCartVo.setCount(goodsCart.getCount());
                    goodsCartVo.setCreatetime(goodsCart.getCreatetime());
                    goodsCartVo.setDisabled(goodsCart.isDisabled());
                    goodsCartVo.setGoods_id(goodsCart.getGoods().getId());
                    goodsCartVo.setId(goodsCart.getId());
                    goodsCartVo.setPrice(goodsCart.getPrice());
                    goodsCartVo.setSc_id(goodsCart.getSc().getId());
                    goodsCartVo.setSpec_id(goodsCart.getSpec_id());
                    goodsCartVo.setSpec_info(goodsCart.getSpec_info());
                    goodsCartVos.add(goodsCartVo);
                }
                orderFormAddVo.setGcs(goodsCartVos);
                String write2JsonStr = JsonUtil.write2JsonStr(orderFormAddVo);
                sendReqAsync.sendMessageUtil(Constant.STORE_ORDERFORM_URL_ADD, write2JsonStr,
                    "买家提交订单");
                logger.info("买家提交订单调用接口【结束】");

                OrderFormLog ofl = new OrderFormLog();
                ofl.setCreatetime(new Date());
                ofl.setOf(of);
                ofl.setLog_info("提交订单");
                ofl.setLog_user(SecurityUserHolder.getCurrentUser());
                this.orderFormLogService.save(ofl);
                mv.addObject("of", of);
                mv.addObject("paymentTools", this.paymentTools);
                if (this.configService.getSysConfig().isEmailEnable()) {
                    send_email(request, of, of.getUser().getEmail(),
                        "email_tobuyer_order_submit_ok_notify");
                }
                if (this.configService.getSysConfig().isSmsEnbale()) {
                    send_sms(request, of, of.getUser().getMobile(),
                        "sms_tobuyer_order_submit_ok_notify");
                }
            } else {
                mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "订单已经失效");
                mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
            }
        } else {
            mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "订单信息错误");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "订单支付详情", value = "/mobile/order_pay_view.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/store/{storeId}.htm/mobile/order_pay_view.htm" })
    public ModelAndView order_pay_view(HttpServletRequest request, HttpServletResponse response,
                                       String id) {
        ModelAndView mv = new JModelAndView("h5/cart3.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(id));
        if (of.getOrder_status() == 10) {
            mv.addObject("of", of);
            mv.addObject("paymentTools", this.paymentTools);
            mv.addObject("url", CommUtil.getURL(request));
        } else if (of.getOrder_status() < 10) {
            mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "该订单已经取消！");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
        } else {
            mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "该订单已经付款！");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "订单支付", value = "/mobile/order_pay.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/store/{storeId}.htm/mobile/order_pay.htm" })
    public ModelAndView order_pay(HttpServletRequest request, HttpServletResponse response,
                                  String payType, String order_id) {
        ModelAndView mv = null;
        OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
        if (of.getOrder_status() == 10) {
            if (CommUtil.null2String(payType).equals("")) {
                mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "请选择支付方式！");
                mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/order.htm");
            } else {
                List<Payment> payments = new ArrayList<Payment>();
                Map<String, Object> params = new HashMap<String, Object>();
                //获取支付方式
                if (this.configService.getSysConfig().getConfig_payment_type() == 1) {
                    //获取平台配置的支付方式
                    params.put("mark", payType);
                    params.put("type", "admin");
                    payments = this.paymentService.query(
                        "select obj from Payment obj where obj.mark=:mark and obj.type=:type",
                        params, -1, -1);
                } else {
                    //获取店铺配置的支付方式
                    params.put("store_id", of.getStore().getId());
                    params.put("mark", payType);
                    payments = this.paymentService
                        .query(
                            "select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id",
                            params, -1, -1);
                }
                of.setPayment((Payment) payments.get(0));
                this.orderFormService.update(of);
                if (payType.equals("balance")) {//预存款支付
                    mv = new JModelAndView("h5/balance_pay.html",
                        this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
                        1, request, response);
                    //of.totalPrice>user.availableBalance
                    User u = this.userService.getObjById(SecurityUserHolder.getCurrentUser()
                        .getId());
                    if (CommUtil.subtract(of.getTotalPrice(), u.getAvailableBalance()) > 0) {
                        mv.addObject("flag", true);
                    } else {
                        mv.addObject("flag", false);
                    }
                    mv.addObject("of", of);

                } else if (payType.equals("outline")) {//线下支付
                    mv = new JModelAndView("h5/outline_pay.html",
                        this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
                        1, request, response);
                    String pay_session = CommUtil.randomString(32);
                    request.getSession(false).setAttribute("pay_session", pay_session);
                    mv.addObject("paymentTools", this.paymentTools);
                    mv.addObject("pay_session", pay_session);
                    //Area area = of.getStore().getArea();
                    mv.addObject("srore", of.getStore());
                    mv.addObject("store_id", of.getStore().getId());
                    mv.addObject("of", of);
                } else if (payType.equals("payafter")) {//货到付款
                    mv = new JModelAndView("h5/payafter_pay.html",
                        this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
                        1, request, response);
                    String pay_session = CommUtil.randomString(32);
                    request.getSession(false).setAttribute("pay_session", pay_session);
                    mv.addObject("paymentTools", this.paymentTools);
                    mv.addObject("pay_session", pay_session);
                    mv.addObject("of", of);
                } else {//线上支付
                    mv = new JModelAndView("h5/line_pay.html", this.configService.getSysConfig(),
                        this.userConfigService.getUserConfig(), 1, request, response);
                    mv.addObject("payType", payType);
                    mv.addObject("url", CommUtil.getURL(request));
                    mv.addObject("payTools", this.payTools);
                    mv.addObject("type", "goods");
                    mv.addObject("payment_id", of.getPayment().getId());
                }
                mv.addObject("order_id", order_id);
            }
        } else {
            mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "该订单不能进行付款！");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "订单线下支付", value = "/mobile/order_pay_outline.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/store/{storeId}.htm/mobile/order_pay_outline.htm" })
    public ModelAndView order_pay_outline(HttpServletRequest request, HttpServletResponse response,
                                          String payType, String order_id, String pay_msg,
                                          String pay_session) throws Exception {
        ModelAndView mv = new JModelAndView("h5/success.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        String pay_session1 = CommUtil.null2String(request.getSession(false).getAttribute(
            "pay_session"));
        if (pay_session1.equals(pay_session)) {
            OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
            of.setPay_msg(pay_msg);
            Map<String, Object> params = new HashMap<String, Object>();
            List<Payment> payments = null;
            if (this.configService.getSysConfig().getConfig_payment_type() == 1) {
                params.put("mark", "outline");
                params.put("type", "admin");
                payments = this.paymentService.query(
                    "select obj from Payment obj where obj.mark=:mark and obj.type=:type", params,
                    -1, -1);
            } else {
                params.put("mark", "outline");
                params.put("store_id", of.getStore().getId());
                payments = this.paymentService.query(
                    "select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id",
                    params, -1, -1);
            }
            if (payments.size() > 0) {
                of.setPayment((Payment) payments.get(0));
                of.setPayTime(new Date());
            }
            of.setOrder_status(15);
            this.orderFormService.update(of);
            if (this.configService.getSysConfig().isSmsEnbale()) {
                send_sms(request, of, of.getStore().getUser().getMobile(),
                    "sms_toseller_outline_pay_ok_notify");
            }
            if (this.configService.getSysConfig().isEmailEnable()) {
                send_email(request, of, of.getStore().getUser().getEmail(),
                    "email_toseller_outline_pay_ok_notify");
            }
            OrderFormLog ofl = new OrderFormLog();
            ofl.setCreatetime(new Date());
            ofl.setLog_info("提交线下支付申请");
            ofl.setLog_user(SecurityUserHolder.getCurrentUser());
            ofl.setOf(of);
            this.orderFormLogService.save(ofl);
            request.getSession(false).removeAttribute("pay_session");
            mv.addObject("op_title", "线下支付提交成功，等待卖家审核！");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/order.htm");
        } else {
            mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "订单已经支付，禁止重复支付！");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/order.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "订单货到付款", value = "/mobile/order_pay_payafter.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/store/{storeId}.htm/mobile/order_pay_payafter.htm" })
    public ModelAndView order_pay_payafter(HttpServletRequest request,
                                           HttpServletResponse response, String payType,
                                           String order_id, String pay_msg, String pay_session)
                                                                                               throws Exception {
        ModelAndView mv = new JModelAndView("h5/success.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        String pay_session1 = CommUtil.null2String(request.getSession(false).getAttribute(
            "pay_session"));
        if (pay_session1.equals(pay_session)) {
            OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
            of.setPay_msg(pay_msg);

            Map<String, Object> params = new HashMap<String, Object>();
            List<Payment> payments = null;
            if (this.configService.getSysConfig().getConfig_payment_type() == 1) {
                params.put("mark", "payafter");
                params.put("type", "admin");
                payments = this.paymentService.query(
                    "select obj from Payment obj where obj.mark=:mark and obj.type=:type", params,
                    -1, -1);
            } else {
                params.put("mark", "payafter");
                params.put("store_id", of.getStore().getId());
                payments = this.paymentService.query(
                    "select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id",
                    params, -1, -1);
            }
            if (payments.size() > 0) {
                of.setPayment((Payment) payments.get(0));
                of.setPayTime(new Date());
            }
            of.setOrder_status(16);
            this.orderFormService.update(of);
            if (this.configService.getSysConfig().isSmsEnbale()) {
                send_sms(request, of, of.getStore().getUser().getMobile(),
                    "sms_toseller_payafter_pay_ok_notify");
            }
            if (this.configService.getSysConfig().isEmailEnable()) {
                send_email(request, of, of.getStore().getUser().getEmail(),
                    "email_toseller_payafter_pay_ok_notify");
            }
            OrderFormLog ofl = new OrderFormLog();
            ofl.setCreatetime(new Date());
            ofl.setLog_info("提交货到付款申请");
            ofl.setLog_user(SecurityUserHolder.getCurrentUser());
            ofl.setOf(of);
            this.orderFormLogService.save(ofl);
            request.getSession(false).removeAttribute("pay_session");
            mv.addObject("op_title", "货到付款提交成功，等待卖家发货！");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/order.htm");
        } else {
            mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "订单已经支付，禁止重复支付！");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/order.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "订单预付款支付", value = "/mobile/order_pay_balance.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/store/{storeId}.htm/mobile/order_pay_balance.htm" })
    public ModelAndView order_pay_balance(HttpServletRequest request, HttpServletResponse response,
                                          String payType, String order_id, String pay_msg)
                                                                                          throws Exception {
        ModelAndView mv = new JModelAndView("h5/success.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        if (CommUtil.null2Double(user.getAvailableBalance()) > CommUtil.null2Double(of
            .getTotalPrice())) {
            of.setPay_msg(pay_msg);
            of.setOrder_status(20);
            Map<String, Object> params = new HashMap<String, Object>();

            List<Payment> payments = null;
            if (this.configService.getSysConfig().getConfig_payment_type() == 1) {
                params.put("mark", "balance");
                params.put("type", "admin");
                payments = this.paymentService.query(
                    "select obj from Payment obj where obj.mark=:mark and obj.type=:type", params,
                    -1, -1);
            } else {
                params.put("mark", "balance");
                params.put("store_id", of.getStore().getId());
                payments = this.paymentService.query(
                    "select obj from Payment obj where obj.mark=:mark and obj.store.id=:store_id",
                    params, -1, -1);
            }
            if (payments.size() > 0) {
                of.setPayment((Payment) payments.get(0));
                of.setPayTime(new Date());
            }
            boolean ret = this.orderFormService.update(of);
            if (this.configService.getSysConfig().isEmailEnable()) {
                send_email(request, of, of.getStore().getUser().getEmail(),
                    "email_toseller_balance_pay_ok_notify");
                send_email(request, of, of.getStore().getUser().getEmail(),
                    "email_tobuyer_balance_pay_ok_notify");
            }
            if (this.configService.getSysConfig().isSmsEnbale()) {
                send_sms(request, of, of.getStore().getUser().getMobile(),
                    "sms_toseller_balance_pay_ok_notify");
                send_sms(request, of, of.getUser().getMobile(), "sms_tobuyer_balance_pay_ok_notify");
            }
            if (ret) {

                update_goods_bind(of, null);

                user.setAvailableBalance(BigDecimal.valueOf(CommUtil.subtract(
                    user.getAvailableBalance(), of.getTotalPrice())));
                user.setFreezeBlance(BigDecimal.valueOf(CommUtil.add(user.getFreezeBlance(),
                    of.getTotalPrice())));
                this.userService.update(user);
                PredepositLog log = new PredepositLog();
                log.setCreatetime(new Date());
                log.setPd_log_user(user);
                log.setPd_op_type("消费");
                log.setPd_log_amount(BigDecimal.valueOf(-CommUtil.null2Double(of.getTotalPrice())));
                log.setPd_log_info("订单" + of.getOrder_id() + "购物减少可用预存款");
                log.setPd_type("可用预存款");
                this.predepositLogService.save(log);
                for (GoodsCart gc : of.getGcs()) {
                    Goods goods = gc.getGoods();
                    if ((goods.getGroup() != null) && (goods.getGroup_buy() == 2)) {
                        for (GroupGoods gg : goods.getGroup_goods_list()) {
                            if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
                                gg.setGg_count(gg.getGg_count() - gc.getCount());
                                gg.setGg_def_count(gg.getGg_def_count() + gc.getCount());
                                this.groupGoodsService.update(gg);
                            }
                        }
                    }
                    List<String> gsps = new ArrayList<String>();
                    for (GoodsSpecProperty gsp : gc.getGsps()) {
                        gsps.add(gsp.getId().toString());
                    }
                    String[] gsp_list = new String[gsps.size()];
                    gsps.toArray(gsp_list);
                    goods.setGoods_salenum(goods.getGoods_salenum() + gc.getCount());
                    Map<String, Object> temp;
                    //减库存    为了保持库存统一总库存和规格属性库存都要减
                    goods.setGoods_inventory(goods.getGoods_inventory() - gc.getCount());

                    if (!goods.getInventory_type().equals("all")) {
                        Object list = (List) Json.fromJson(ArrayList.class,
                            goods.getGoods_inventory_detail());
                        for (Iterator localIterator4 = ((List) list).iterator(); localIterator4
                            .hasNext();) {
                            temp = (Map) localIterator4.next();
                            String[] temp_ids = CommUtil.null2String(temp.get("id")).split("_");
                            Arrays.sort(temp_ids);
                            Arrays.sort(gsp_list);
                            if (Arrays.equals(temp_ids, gsp_list)) {
                                temp.put(
                                    "count",
                                    Integer.valueOf(CommUtil.null2Int(temp.get("count"))
                                                    - gc.getCount()));
                            }
                        }
                        goods.setGoods_inventory_detail(Json.toJson(list, JsonFormat.compact()));
                    }
                    for (GroupGoods gg : goods.getGroup_goods_list()) {
                        if (goods.getGroup() != null
                            && (gg.getGroup().getId().equals(goods.getGroup().getId()))
                            && (gg.getGg_count() == 0)) {
                            goods.setGroup_buy(3);
                        }
                    }
                    this.goodsService.update(goods);
                }
            }
            OrderFormLog ofl = new OrderFormLog();
            ofl.setCreatetime(new Date());
            ofl.setLog_info("预付款支付");
            ofl.setLog_user(SecurityUserHolder.getCurrentUser());
            ofl.setOf(of);
            this.orderFormLogService.save(ofl);
            mv.addObject("op_title", "预付款支付成功！");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/order.htm");
        } else {
            mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "可用余额不足，支付失败！");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/order.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "订单支付结果", value = "/mobile/order_finish.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/store/{storeId}.htm/mobile/order_finish.htm" })
    public ModelAndView order_finish(HttpServletRequest request, HttpServletResponse response,
                                     String order_id) {
        ModelAndView mv = new JModelAndView("order_finish.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        OrderForm obj = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
        mv.addObject("obj", obj);
        return mv;
    }

    @SecurityMapping(title = "地址新增", value = "/mobile/cart_address.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/store/{storeId}.htm/mobile/cart_address.htm" })
    public ModelAndView cart_address(HttpServletRequest request, HttpServletResponse response,
                                     String id, String store_id) {
        ModelAndView mv = new JModelAndView("cart_address.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        List<Area> areas = this.areaService.query(
            "select obj from Area obj where obj.parent.id is null", null, -1, -1);
        mv.addObject("areas", areas);
        mv.addObject("store_id", store_id);
        return mv;
    }

    @SecurityMapping(title = "购物车中收货地址保存", value = "/mobile/cart_address_save.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/store/{storeId}.htm/mobile/cart_address_save.htm" })
    public void cart_address_save(HttpServletRequest request, HttpServletResponse response,
                                  String id, String area_id, String store_id) {
        WebForm wf = new WebForm();
        boolean ret = true;
        try {
            Address address = null;
            if (id.equals("")) {
                address = (Address) wf.toPo(request, Address.class);
                address.setCreatetime(new Date());
            } else {
                Address obj = this.addressService.getObjById(Long.valueOf(Long.parseLong(id)));
                address = (Address) wf.toPo(request, obj);
            }
            address.setUser(SecurityUserHolder.getCurrentUser());
            Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
            address.setArea(area);
            if (id.equals("")) {
                this.addressService.save(address);
            } else {
                this.addressService.update(address);
            }
        } catch (Exception e) {
            ret = false;
            e.printStackTrace();
        }
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @SecurityMapping(title = "地址切换", value = "/mobile/order_address.htm*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
    @RequestMapping({ "/store/{storeId}.htm/mobile/order_address.htm" })
    public void order_address(HttpServletRequest request, HttpServletResponse response,
                              String addr_id, String store_id) {
        List<StoreCart> cart = (List) request.getSession(false).getAttribute("cart");
        StoreCart sc = null;
        if (cart != null) {
            for (StoreCart sc1 : cart) {
                if (sc1.getStore().getId().equals(CommUtil.null2Long(store_id))) {
                    sc = sc1;
                    break;
                }
            }
        }
        Address addr = this.addressService.getObjById(CommUtil.null2Long(addr_id));
        Object sms = this.transportTools.query_cart_trans(sc,
            CommUtil.null2String(addr.getArea().getId()));

        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(Json.toJson(sms, JsonFormat.compact()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send_email(HttpServletRequest request, OrderForm order, String email, String mark) {
        try {
            com.javamalls.platform.domain.Template template = this.templateService
                .getObjByProperty("mark", mark);
            if ((template != null) && (template.isOpen())) {
                String subject = template.getTitle();
                String path = request.getSession().getServletContext().getRealPath("")
                              + File.separator + "vm" + File.separator;
                if (!CommUtil.fileExist(path)) {
                    CommUtil.createFolder(path);
                }
                PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
                    path + "msg.vm", false), "UTF-8"));
                pwrite.print(template.getContent());
                pwrite.flush();
                pwrite.close();

                Properties p = new Properties();
                p.setProperty("file.resource.loader.path", request.getRealPath("/mobile/") + "vm"
                                                           + File.separator);
                p.setProperty("input.encoding", "UTF-8");
                p.setProperty("output.encoding", "UTF-8");
                Velocity.init(p);
                org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm", "UTF-8");
                VelocityContext context = new VelocityContext();
                context.put("buyer", order.getUser());
                context.put("seller", order.getStore().getUser());
                context.put("config", this.configService.getSysConfig());
                context.put("send_time", CommUtil.formatLongDate(new Date()));
                context.put("webPath", CommUtil.getURL(request));
                context.put("order", order);
                StringWriter writer = new StringWriter();
                blank.merge(context, writer);

                String content = writer.toString();
                this.msgTools.sendEmail(email, subject, content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void send_sms(HttpServletRequest request, OrderForm order, String mobile, String mark) {
        // 应该try catch起来，不能因为提醒功能导致下面的流程跑不下去
        try {
            com.javamalls.platform.domain.Template template = this.templateService
                .getObjByProperty("mark", mark);
            if ((template != null) && (template.isOpen())) {
                /*  String path = request.getSession().getServletContext().getRealPath("")
                                + File.separator + "vm" + File.separator;
                  if (!CommUtil.fileExist(path)) {
                      CommUtil.createFolder(path);
                  }
                  PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
                      path + "msg.vm", false), "UTF-8"));
                  pwrite.print(template.getContent());
                  pwrite.flush();
                  pwrite.close();

                  Properties p = new Properties();
                  p.setProperty("file.resource.loader.path", request.getRealPath("/") + "vm"
                                                             + File.separator);
                  p.setProperty("input.encoding", "UTF-8");
                  p.setProperty("output.encoding", "UTF-8");
                  Velocity.init(p);
                  org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm", "UTF-8");
                  VelocityContext context = new VelocityContext();
                  context.put("buyer", order.getUser());
                  context.put("seller", order.getStore().getUser());
                  context.put("config", this.configService.getSysConfig());
                  context.put("send_time", CommUtil.formatLongDate(new Date()));
                  context.put("webPath", CommUtil.getURL(request));
                  context.put("order", order);
                  StringWriter writer = new StringWriter();
                  blank.merge(context, writer);

                  String content = writer.toString();*/
                Map<String, String> map = new HashMap<String, String>();
                User buyer = order.getUser();
                String buyerName = buyer.getUserName();
                if (buyer.getTrueName() != null && !"".equals(buyer.getTrueName())) {
                    buyerName = buyer.getTrueName();
                }
                map.put("buyerName", buyerName);
                User seller = order.getStore().getUser();
                String sellerName = seller.getUserName();
                if (seller.getTrueName() != null && !"".equals(seller.getTrueName())) {
                    sellerName = seller.getTrueName();
                }
                map.put("sellerName", sellerName);
                map.put("order_id", order.getOrder_id());
                this.msgTools.sendSMS(mobile, template.getTitle(), map);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 付款后，如果有捆绑商品，按店铺拆分为多个
    private void update_goods_bind(OrderForm order, String trade_no) {
        Map<Long, OrderForm> mp = new HashMap<Long, OrderForm>();
        int n = 0;

        for (int i = order.getGcs().size() - 1; i >= 0; i--) {
            GoodsCart gc = order.getGcs().get(i);
            if ("bind".equals(gc.getCart_type())) {
                Long storeid = gc.getGoods().getGoods_store().getId();
                OrderForm of = mp.get(storeid);

                order.getGcs().remove(gc);
                order
                    .setTotalPrice(BigDecimal.valueOf(CommUtil.subtract(
                        order.getTotalPrice(),
                        BigDecimal.valueOf(Double.parseDouble(gc.getPrice().toString())
                                           * gc.getCount()))));
                if (of != null) {
                    of.setTotalPrice(BigDecimal.valueOf(Double.parseDouble(of.getTotalPrice()
                        .toString()) + Double.parseDouble(gc.getPrice().toString()) * gc.getCount()));
                    of.getGcs().add(gc);
                } else {

                    n++;

                    of = new OrderForm();
                    of.setAddr(order.getAddr());
                    of.setAuto_confirm_email(order.isAuto_confirm_email());
                    of.setAuto_confirm_sms(order.isAuto_confirm_sms());
                    of.setCreatetime(order.getCreatetime());
                    of.setDisabled(order.isDisabled());
                    of.setEc(order.getEc());
                    of.setFinishTime(order.getFinishTime());

                    of.setOrder_type(order.getOrder_type());
                    of.setOrder_id(order.getOrder_id() + "-" + i);
                    of.setOrder_status(order.getOrder_status());
                    of.setOut_order_id(trade_no);
                    of.setPay_msg(order.getPay_msg());
                    of.setPayment(order.getPayment());
                    of.setPayTime(order.getPayTime());
                    of.setStore(gc.getGoods().getGoods_store());
                    of.setUser(order.getUser());

                    of.setTotalPrice(BigDecimal.valueOf(Double
                        .parseDouble(gc.getPrice().toString()) * gc.getCount()));
                    of.getGcs().add(gc);

                    mp.put(storeid, of);

                }

            }
        }
        if (mp.size() > 0) {
            for (OrderForm of : mp.values()) {
                this.orderFormService.save(of);
                for (GoodsCart gc : of.getGcs()) {
                    gc.setOf(of);
                    this.goodsCartService.save(gc);
                }
            }
            this.orderFormService.update(order);
        }
    }

    /**
     * 批量加入购物车
     * 购物车以店铺进行分组   
     * StoreCart 为购物车组  不存放商品存放购买人、店铺、游客信息和购买总金额
     * GoodsCart 为组下面的购物车存放购物的商品及相关规格属性
     * @param request
     * @param response
     * @param id
     * @param count
     * @param price
     * @param gsp
     * @param buy_type
     */
    @RequestMapping({ "/store/{storeId}.htm/mobile/add_goods_cartAll.htm" })
    public void add_goods_cartAll(HttpServletRequest request, HttpServletResponse response,
                                  String id, String[] gspAll, String buy_type) {
        Map<String, Object> map = new HashMap<String, Object>();
        boolean flag = true;
        if (gspAll != null && gspAll.length > 0) {
            for (int k = 0; k < gspAll.length; k++) {
                String count = request.getParameter("goodsCount_" + gspAll[k]);

                if (count != null && !"".equals(count) && CommUtil.null2Int(count) > 0) {
                    flag = false;
                    String gsp = gspAll[k];
                    String cart_session_id = "";
                    // 取sessionid
                    Cookie[] cookies = request.getCookies();
                    if (cookies != null) {
                        for (Cookie cookie : cookies) {
                            if (cookie.getName().equals("cart_session_id")) {
                                cart_session_id = CommUtil.null2String(cookie.getValue());
                            }
                        }
                    }
                    // 没有就生成一个
                    if (cart_session_id.equals("")) {
                        cart_session_id = UUID.randomUUID().toString();
                        Cookie cookie = new Cookie("cart_session_id", cart_session_id);
                        cookie.setDomain(CommUtil.generic_domain(request));
                        response.addCookie(cookie);
                    }
                    List<StoreCart> cart = new ArrayList<StoreCart>();
                    List<StoreCart> user_cart = new ArrayList<StoreCart>();

                    // 当前登录用户
                    User user = SecurityUserHolder.getCurrentUser();

                    Map<String, Object> params = new HashMap<String, Object>();
                    StoreCart sc;
                    //已登录用户
                    if (user != null) {
                        if (!cart_session_id.equals("")) {
                            //如果当前登录用户已开店  检查购物车中是否有自己店铺的商品有就删除
                            if (user.getStore() != null) {
                                params.clear();
                                params.put("cart_session_id", cart_session_id);
                                params.put("user_id", user.getId());
                                params.put("sc_status", Integer.valueOf(0));
                                params.put("store_id", user.getStore().getId());
                                List<StoreCart> store_cookie_cart = this.storeCartService
                                    .query(
                                        "select obj from StoreCart obj where (obj.cart_session_id=:cart_session_id or obj.user.id=:user_id) and obj.sc_status=:sc_status and obj.store.id=:store_id",
                                        params, -1, -1);
                                for (Iterator localIterator1 = store_cookie_cart.iterator(); localIterator1
                                    .hasNext();) {
                                    sc = (StoreCart) localIterator1.next();
                                    for (GoodsCart gc : sc.getGcs()) {
                                        gc.getGsps().clear();//清除购物车商品 关联的规格属性
                                        this.goodsCartService.delete(gc.getId());//清除购物车商品
                                    }
                                    //清除购物车组的信息（当前用户在自己店铺购物的记录）
                                    this.storeCartService.delete(sc.getId());
                                }
                            }

                            params.clear();
                            params.put("user_id", user.getId());
                            params.put("sc_status", Integer.valueOf(0));
                            //登录用户购物车组
                            user_cart = this.storeCartService
                                .query(
                                    "select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status",
                                    params, -1, -1);
                        } else {
                            params.clear();
                            params.put("user_id", user.getId());
                            params.put("sc_status", Integer.valueOf(0));
                            user_cart = this.storeCartService
                                .query(
                                    "select obj from StoreCart obj where obj.user.id=:user_id and obj.sc_status=:sc_status",
                                    params, -1, -1);
                        }
                    }

                    //添加或更新购物车 
                    for (StoreCart sc2 : user_cart) {
                        boolean sc_add = true;
                        for (StoreCart sc1 : cart) {
                            if (sc1.getStore().getId().equals(sc2.getStore().getId())) {
                                sc_add = false;
                            }
                        }
                        if (sc_add) {
                            cart.add(sc2);
                        }
                    }

                    String[] gsp_ids = null;
                    if (gsp != null && !"".equals(gsp_ids)) {
                        gsp_ids = gsp.split(",");
                        Arrays.sort(gsp_ids, new Comparator<String>() {
                            @Override
                            public int compare(String arg0, String arg1) {
                                return Integer.valueOf(arg0).compareTo(Integer.valueOf(arg1));
                            }
                        });
                    }

                    boolean add = true;
                    double total_price = 0.0D;

                    int total_count = 0;
                    Iterator localIterator4 = cart.iterator();
                    String[] gsp_ids1;
                    //判断购物车中的商品和加入的是否是同一商品
                    while (localIterator4.hasNext()) {
                        sc = (StoreCart) localIterator4.next();

                        Iterator localIterator5 = sc.getGcs().iterator();
                        while (localIterator5.hasNext()) {
                            GoodsCart gc = (GoodsCart) localIterator5.next();

                            if ((gsp_ids != null) && (gsp_ids.length > 0) && (gc.getGsps() != null)
                                && (gc.getGsps().size() > 0)) {
                                gsp_ids1 = new String[gc.getGsps().size()];
                                for (int i = 0; i < gc.getGsps().size(); i++) {
                                    gsp_ids1[i] = (gc.getGsps().get(i) != null ? ((GoodsSpecProperty) gc
                                        .getGsps().get(i)).getId().toString() : "");
                                }
                                Arrays.sort(gsp_ids1, new Comparator<String>() {
                                    @Override
                                    public int compare(String arg0, String arg1) {
                                        return Integer.valueOf(arg0).compareTo(
                                            Integer.valueOf(arg1));
                                    }
                                });
                                if ((gc.getGoods().getId().toString().equals(id))
                                    && (Arrays.equals(gsp_ids, gsp_ids1))) {
                                    add = false;
                                    gc.setCount(CommUtil.null2Int(count));
                                    double mulPrice = getMulPrice(id, count, user, gsp_ids);
                                    if (mulPrice > 0) {
                                        gc.setPrice(BigDecimal.valueOf(mulPrice));
                                    }
                                    this.goodsCartService.update(gc);
                                }
                            } else if (gc.getGoods().getId().toString().equals(id)) {//没有规格
                                add = false;

                                //查询该货品是否有价格策略
                                double singlePrice = getSinglePrice(id, count, user);
                                if (singlePrice > 0) {
                                    gc.setPrice(BigDecimal.valueOf(singlePrice));
                                }
                                gc.setCount(CommUtil.null2Int(count));

                                this.goodsCartService.update(gc);
                            }
                        }
                    }
                    if (add) {
                        Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
                        String type = "save";
                        sc = new StoreCart();
                        for (StoreCart sc1 : cart) {
                            if (sc1.getStore().getId().equals(goods.getGoods_store().getId())) {
                                sc = sc1;
                                type = "update";
                                break;
                            }
                        }
                        sc.setStore(goods.getGoods_store());
                        if (((String) type).equals("save")) {
                            sc.setCreatetime(new Date());
                            this.storeCartService.save(sc);
                        } else {
                            this.storeCartService.update(sc);
                        }
                        GoodsCart gc = new GoodsCart();
                        gc.setCreatetime(new Date());
                        //price为传进来的   为了安全重新从数据库取价格和库存
                        Double goodsPrice = CommUtil.null2Double(goods.getStore_price());
                        Integer goodsCount = CommUtil.null2Int(goods.getGoods_inventory());

                        List<GoodsItem> goods_items = new ArrayList<GoodsItem>();
                        String spec_combination = "";
                        if (gsp != null && !"".equals(gsp)) {
                            Arrays.sort(gsp_ids, new Comparator<String>() {
                                @Override
                                public int compare(String arg0, String arg1) {
                                    return Integer.valueOf(arg0).compareTo(Integer.valueOf(arg1));
                                }
                            });
                            for (int i = 0; i < gsp_ids.length; i++) {
                                spec_combination = spec_combination + gsp_ids[i] + "_";
                            }

                            HashMap<String, Object> paramMap = new HashMap<String, Object>();
                            paramMap.put("goodsId", goods.getId());
                            paramMap.put("spec_combination", spec_combination);
                            goods_items = this.goodsItemService
                                .query(
                                    "select obj from GoodsItem obj where obj.disabled=false and obj.spec_combination =:spec_combination and obj.goods.id =:goodsId",
                                    paramMap, -1, -1);
                        } else {
                            HashMap<String, Object> paramMap = new HashMap<String, Object>();
                            paramMap.put("goodsId", goods.getId());
                            goods_items = this.goodsItemService.query(
                                "select obj from GoodsItem obj where obj.goods.id =:goodsId",
                                paramMap, -1, -1);

                        }

                        /**
                         * 价格策略
                         */
                        if (goods_items != null && goods_items.size() > 0) {
                            if (spec_combination != null && !"".equals(spec_combination)) {//有规格
                                for (GoodsItem item : goods_items) {
                                    //	System.out.println(spec_combination+"+++++++++++"+item.getSpec_combination());
                                    if (spec_combination.equals(item.getSpec_combination())) {
                                        goodsCount = item.getGoods_inventory();
                                        //  System.out.println(goodsCount+"---------");
                                        if (user != null && user.getStrategy() != null
                                            && user.getStrategy().isDisabled() == false
                                            && user.getStrategy().getStatus() == 1) {
                                            //查询策略的货品的价格
                                            Map<String, Object> paramMap = new HashMap<String, Object>();
                                            paramMap.put("strategyId", user.getStrategy().getId());
                                            paramMap.put("goodItemId", item.getId());
                                            List<StrategyGoodsItem> list = this.strategyGoodsItemService
                                                .query(
                                                    "select obj from StrategyGoodsItem obj where obj.strategy.id=:strategyId and obj.goods_item.id=:goodItemId",
                                                    paramMap, 0, 1);

                                            if (list != null && list.size() > 0) {
                                                StrategyGoodsItem sgi = list.get(0);
                                                goodsPrice = sgi.getPrice().doubleValue();
                                            } else {
                                                goodsPrice = item.getGoods_price().doubleValue();

                                                //多规格价格
                                                //***************没有策略时查询阶梯价格***********
                                                /*if(item.getStep_price_state()==1){
                                                      Map<String, Object> querymap=new HashMap<String, Object>();
                                                     querymap.put("goods_item_id",CommUtil.null2Int(item.getId()));
                                                     querymap.put("begin_num",CommUtil.null2Int(count));
                                                     querymap.put("end_num", CommUtil.null2Int(count));
                                                      String sql="select obj from GoodsStepPrice obj where obj.disabled=false and obj.goods_item_id=:goods_item_id and (obj.begin_num<=:begin_num and obj.end_num>=:end_num)";
                                                     
                                                     List<GoodsStepPrice> steplist = this.goodsStepPriceService.query(sql, querymap, -1, -1);
                                                     if(steplist!=null&&steplist.size()>0){
                                                  	goodsPrice = steplist.get(0).getPrice().doubleValue();
                                                     }
                                                }*/
                                            }
                                        } else {
                                            goodsPrice = item.getGoods_price().doubleValue();

                                            //***************没有策略时查询阶梯价格***********
                                            /*  if(item.getStep_price_state()==1){
                                                    Map<String, Object> querymap=new HashMap<String, Object>();
                                                   querymap.put("goods_item_id",CommUtil.null2Int(item.getId()));
                                                   querymap.put("begin_num",CommUtil.null2Int(count));
                                                   querymap.put("end_num", CommUtil.null2Int(count));
                                                    String sql="select obj from GoodsStepPrice obj where obj.disabled=false and obj.goods_item_id=:goods_item_id and (obj.begin_num<=:begin_num and obj.end_num>=:end_num)";
                                                   
                                                   List<GoodsStepPrice> steplist = this.goodsStepPriceService.query(sql, querymap, -1, -1);
                                                   if(steplist!=null&&steplist.size()>0){
                                                	goodsPrice = steplist.get(0).getPrice().doubleValue();
                                                   }
                                              }*/

                                        }

                                    }
                                }
                            } else {//没有规格
                                GoodsItem item = goods_items.get(0);
                                goodsCount = item.getGoods_inventory();

                                if (user != null && user.getStrategy() != null) {
                                    //查询策略的货品的价格
                                    Map<String, Object> paramMap = new HashMap<String, Object>();
                                    paramMap.put("strategyId", user.getStrategy().getId());
                                    paramMap.put("goodItemId", item.getId());
                                    List<StrategyGoodsItem> list = this.strategyGoodsItemService
                                        .query(
                                            "select obj from StrategyGoodsItem obj where obj.strategy.id=:strategyId and obj.goods_item.id=:goodItemId",
                                            paramMap, 0, 1);

                                    if (list != null && list.size() > 0) {//货品加入策略
                                        StrategyGoodsItem sgi = list.get(0);
                                        goodsPrice = sgi.getPrice().doubleValue();
                                    } else {//货品没有加入策略
                                        goodsPrice = item.getGoods_price().doubleValue();
                                        //***************没有策略时查询阶梯价格***********
                                        /* if(item.getStep_price_state()==1){
                                               Map<String, Object> querymap=new HashMap<String, Object>();
                                              querymap.put("goods_item_id",CommUtil.null2Int(item.getId()));
                                              querymap.put("begin_num",CommUtil.null2Int(count));
                                              querymap.put("end_num", CommUtil.null2Int(count));
                                               String sql="select obj from GoodsStepPrice obj where obj.disabled=false and obj.goods_item_id=:goods_item_id and (obj.begin_num<=:begin_num and obj.end_num>=:end_num)";
                                              
                                              List<GoodsStepPrice> steplist = this.goodsStepPriceService.query(sql, querymap, -1, -1);
                                              if(steplist!=null&&steplist.size()>0){
                                           	goodsPrice = steplist.get(0).getPrice().doubleValue();
                                              }
                                         }*/
                                    }
                                } else {//没有策略
                                    goodsPrice = item.getGoods_price().doubleValue();
                                    //***************没有策略时查询阶梯价格***********
                                    /*if(item.getStep_price_state()==1){
                                          Map<String, Object> querymap=new HashMap<String, Object>();
                                         querymap.put("goods_item_id",CommUtil.null2Int(item.getId()));
                                         querymap.put("begin_num",CommUtil.null2Int(count));
                                         querymap.put("end_num", CommUtil.null2Int(count));
                                          String sql="select obj from GoodsStepPrice obj where obj.disabled=false and obj.goods_item_id=:goods_item_id and (obj.begin_num<=:begin_num and obj.end_num>=:end_num)";
                                         
                                         List<GoodsStepPrice> steplist = this.goodsStepPriceService.query(sql, querymap, -1, -1);
                                         if(steplist!=null&&steplist.size()>0){
                                      	goodsPrice = steplist.get(0).getPrice().doubleValue();
                                         }
                                    }*/

                                }
                            }
                        }

                        if ("".equals(CommUtil.null2String(buy_type))
                            || "undefined".equals(CommUtil.null2String(buy_type))) {
                            //	System.out.println(count+"==========="+goodsCount);
                            goodsCount = Integer.valueOf(count) > goodsCount ? goodsCount : Integer
                                .valueOf(count);
                            gc.setCount(CommUtil.null2Int(goodsCount));
                            gc.setPrice(BigDecimal.valueOf(CommUtil.null2Double(goodsPrice)));// 商品总价，这里是单价*数量
                        } else if ("combin".equals(CommUtil.null2String(buy_type))) {
                            gc.setCount(1);
                            gc.setCart_type("combin");
                            gc.setPrice(goods.getCombin_price());
                        } else if ("storeempty".equals(CommUtil.null2String(buy_type))) {
                            gc.setCount(CommUtil.null2Int(count));
                            gc.setCart_type("storeempty");
                            gc.setPrice(goods.getStoreempty_price());
                        } else if ("dingjin".equals(CommUtil.null2String(buy_type))) {
                            gc.setCount(CommUtil.null2Int(count));
                            gc.setCart_type("dingjin");
                            gc.setPrice(goods.getDeposit_price());
                        }

                        gc.setSpec_id(gsp);
                        gc.setGoods(goods);

                        /*if (goods.getDelivery_status() == 2 && goods.getDg() != null
                            && goods.getDg().getD_delivery_goods() != null) {
                            gc.setD_delivery_goods(goods.getDg().getD_delivery_goods());
                        }*/

                        String spec_info = "";
                        GoodsSpecProperty spec_property;
                        for (String gsp_id : gsp_ids) {
                            spec_property = this.goodsSpecPropertyService.getObjById(CommUtil
                                .null2Long(gsp_id));
                            ((GoodsCart) gc).getGsps().add(spec_property);
                            if (spec_property != null) {
                                spec_info = spec_property.getSpec().getName() + ":"
                                            + spec_property.getValue() + " " + spec_info;
                            }
                        }
                        ((GoodsCart) gc).setSc(sc);
                        ((GoodsCart) gc).setSpec_info(spec_info);
                        this.goodsCartService.save((GoodsCart) gc);
                        sc.getGcs().add(gc);
                        double cart_total_price = 0.0D;

                        Iterator<GoodsCart> xxx = sc.getGcs().iterator();
                        while (xxx.hasNext()) {
                            GoodsCart gc1 = (GoodsCart) xxx.next();
                            if (CommUtil.null2String(gc1.getCart_type()).equals("")) {
                                cart_total_price = cart_total_price
                                                   + CommUtil.null2Double(gc1.getPrice())
                                                   * gc1.getCount();
                            } else if (CommUtil.null2String(gc1.getCart_type()).equals("combin")) {
                                cart_total_price = cart_total_price
                                                   + CommUtil.null2Double(gc1.getGoods()
                                                       .getCombin_price()) * gc1.getCount();
                            } else if (CommUtil.null2String(gc1.getCart_type())
                                .equals("storeempty")) {
                                cart_total_price = cart_total_price
                                                   + CommUtil.null2Double(gc1.getGoods()
                                                       .getStoreempty_price()) * gc1.getCount();
                            } else if (CommUtil.null2String(gc1.getCart_type()).equals("dingjin")) {
                                cart_total_price = cart_total_price
                                                   + CommUtil.null2Double(gc1.getGoods()
                                                       .getDeposit_price()) * gc1.getCount();
                            } else if (CommUtil.null2String(gc1.getCart_type()).equals("bind")) {
                                cart_total_price = cart_total_price
                                                   + CommUtil.null2Double(gc1.getGoods()
                                                       .getGoods_current_price())
                                                   * CommUtil.null2Double(gc1.getGoods()
                                                       .getBinddiscount()) * gc1.getCount();
                            }
                        }
                        sc.setTotal_price(BigDecimal.valueOf(CommUtil.formatMoney(Double
                            .valueOf(cart_total_price)))); // 购物车中某店铺总价
                        if (user == null) {
                            sc.setCart_session_id(cart_session_id);
                        } else {
                            sc.setUser(user);
                        }
                        if (((String) type).equals("save")) {
                            sc.setCreatetime(new Date());
                            this.storeCartService.save(sc);
                        } else {
                            this.storeCartService.update(sc);
                        }
                        boolean cart_add = true;
                        for (StoreCart sc1 : cart) {
                            if (sc1.getStore().getId().equals(sc.getStore().getId())) {
                                cart_add = false;
                            }
                        }
                        if (cart_add) {
                            cart.add(sc);
                        }

                        // 开始：捆绑商品功能     检查商品是否有捆绑商品有就一起加入购物车
                        if (goods.getBind_status() == 2) {
                            for (Goods bindgoods : goods.getBind_goods()) {

                                type = "save";

                                gc = new GoodsCart();
                                gc.setCreatetime(new Date());
                                gc.setCount(CommUtil.null2Int(count));
                                gc.setPrice(BigDecimal.valueOf(CommUtil.null2Double(bindgoods
                                    .getBinddiscount())
                                                               * CommUtil.null2Double(bindgoods
                                                                   .getGoods_current_price())));
                                gc.setCart_type("bind");

                                gc.setGoods(bindgoods);
                                spec_info = "";
                                ((GoodsCart) gc).setSc(sc);
                                ((GoodsCart) gc).setSpec_info(spec_info);
                                this.goodsCartService.save((GoodsCart) gc);
                                sc.getGcs().add(gc);

                                cart_total_price = 0.0D;
                                xxx = sc.getGcs().iterator();
                                while (xxx.hasNext()) {
                                    GoodsCart gc1 = (GoodsCart) xxx.next();
                                    if (CommUtil.null2String(gc1.getCart_type()).equals("")) {
                                        cart_total_price = cart_total_price
                                                           + CommUtil.null2Double(gc1.getGoods()
                                                               .getGoods_current_price())
                                                           * gc1.getCount();
                                    } else if (CommUtil.null2String(gc1.getCart_type()).equals(
                                        "combin")) {
                                        cart_total_price = cart_total_price
                                                           + CommUtil.null2Double(gc1.getGoods()
                                                               .getCombin_price()) * gc1.getCount();
                                    } else if (CommUtil.null2String(gc1.getCart_type()).equals(
                                        "storeempty")) {
                                        cart_total_price = cart_total_price
                                                           + CommUtil.null2Double(gc1.getGoods()
                                                               .getStoreempty_price())
                                                           * gc1.getCount();
                                    } else if (CommUtil.null2String(gc1.getCart_type()).equals(
                                        "dingjin")) {
                                        cart_total_price = cart_total_price
                                                           + CommUtil.null2Double(gc1.getGoods()
                                                               .getDeposit_price())
                                                           * gc1.getCount();
                                    } else if (CommUtil.null2String(gc1.getCart_type()).equals(
                                        "bind")) {
                                        cart_total_price = cart_total_price
                                                           + CommUtil.null2Double(gc1.getGoods()
                                                               .getGoods_current_price())
                                                           * CommUtil.null2Double(gc1.getGoods()
                                                               .getBinddiscount()) * gc1.getCount();
                                    }
                                }
                                sc.setTotal_price(BigDecimal.valueOf(CommUtil.formatMoney(Double
                                    .valueOf(cart_total_price)))); // 购物车中某店铺总价
                                if (((String) type).equals("save")) {
                                    sc.setCreatetime(new Date());
                                    this.storeCartService.save(sc);
                                } else {
                                    this.storeCartService.update(sc);
                                }
                                cart_add = true;
                                for (StoreCart sc1 : cart) {
                                    if (sc1.getStore().getId().equals(sc.getStore().getId())) {
                                        cart_add = false;
                                    }
                                }
                                if (cart_add) {
                                    cart.add(sc);
                                }
                            }
                        }
                        // 结束：捆绑商品功能
                    }
                    // 购物车总价
                    int goods_total_count = 0;
                    for (StoreCart sc1 : cart) {
                        total_count += sc1.getGcs().size();
                        double total_price2 = 0.0D;
                        for (GoodsCart gc1 : sc1.getGcs()) {
                            total_price = total_price
                                          + CommUtil.mul(gc1.getPrice(),
                                              Integer.valueOf(gc1.getCount()));
                            total_price2 = total_price2
                                           + CommUtil.mul(gc1.getPrice(),
                                               Integer.valueOf(gc1.getCount()));
                            goods_total_count += Integer.valueOf(gc1.getCount());
                        }
                        if (!add) {
                            sc1.setTotal_price(BigDecimal.valueOf(CommUtil.formatMoney(Double
                                .valueOf(total_price2))));
                            this.storeCartService.update(sc1);
                        }

                    }
                    map.put("count", Integer.valueOf(total_count));
                    map.put("total_price", Double.valueOf(total_price));
                    map.put("goods_total_count", goods_total_count);

                }
            }
        }
        if (flag) {
            map.put("count", "0");
            map.put("total_price", "0");
            map.put("goods_total_count", "0");
        }
        String ret = Json.toJson(map, JsonFormat.compact());
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化商品数量
     */
    @RequestMapping({ "/store/{storeId}.htm/mobile/init_cartCount.htm" })
    public void init_cartCount(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>();
        User user = SecurityUserHolder.getCurrentUser();
        if (user != null) {
            List<StoreCart> cart = cart_calc(request);
            if (cart != null) {
                // 购物车总价
                int goods_total_count = 0;
                double total_price = 0.0D;
                int total_count = 0;
                for (StoreCart sc1 : cart) {
                    total_count += sc1.getGcs().size();
                    for (GoodsCart gc1 : sc1.getGcs()) {
                        total_price = total_price
                                      + CommUtil.mul(gc1.getPrice(),
                                          Integer.valueOf(gc1.getCount()));
                        goods_total_count += Integer.valueOf(gc1.getCount());
                    }
                }
                map.put("count", Integer.valueOf(total_count));
                map.put("total_price", Double.valueOf(total_price));
                map.put("goods_total_count", goods_total_count);
                List<String> list = new ArrayList<String>();
                if (cart != null && cart.size() > 0 && cart.get(0).getGcs() != null) {
                    for (GoodsCart gc : cart.get(0).getGcs()) {
                        if (gc.getSpec_id() == null || "".equals(gc.getSpec_id())) {//单规格
                            String str = gc.getGoods().getId() + ";" + gc.getCount();
                            list.add(str);
                        }
                    }
                    map.put("cartList", list);
                }
                /* String write2JsonStr = JsonUtil.write2JsonStr(map);
                 System.out.println(write2JsonStr);*/

            } else {
                map.put("count", "0");
                map.put("total_price", "0");
                map.put("goods_total_count", "0");
            }
        } else {
            map.put("count", "0");
            map.put("total_price", "0");
            map.put("goods_total_count", "0");
        }

        String ret = Json.toJson(map, JsonFormat.compact());
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping({ "/store/{storeId}.htm/mobile/init_mulitCountShow.htm" })
    public void init_mulitCountShow(HttpServletRequest request, HttpServletResponse response,
                                    String goodsId, @PathVariable String storeId) {
        Map<String, Object> map = new HashMap<String, Object>();
        User user = SecurityUserHolder.getCurrentUser();
        List<String> cartlist = new ArrayList<String>();
        if (user != null) {
            Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goodsId));
            List<StoreCart> cart = cart_calc(request);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("userId", user.getId());
            params.put("storeId", CommUtil.null2Long(storeId));
            params.put("goodsId", CommUtil.null2Long(goodsId));
            List<GoodsCart> list = this.goodsCartService
                .query(
                    "select obj from GoodsCart obj where obj.disabled=false and obj.goods.id=:goodsId and obj.sc.user.id=:userId and obj.sc.store.id=:storeId and obj.sc.sc_status=0",
                    params, -1, -1);
            List<GoodsItem> goods_item_list = goods.getGoods_item_list();

            if (list != null && list.size() > 0 && goods_item_list != null
                && goods_item_list.size() > 0) {
                for (GoodsCart goodsCart : list) {
                    String spec_id = goodsCart.getSpec_id();
                    if (spec_id != null && spec_id.contains(",")) {//多规格
                        String[] gsp_ids = spec_id.split(",");
                        Arrays.sort(gsp_ids, new Comparator<String>() {
                            @Override
                            public int compare(String arg0, String arg1) {
                                return Integer.valueOf(arg0).compareTo(Integer.valueOf(arg1));
                            }
                        });

                        String spec_combination = "";
                        for (int i = 0; i < gsp_ids.length; i++) {
                            spec_combination = spec_combination + gsp_ids[i] + "_";
                        }
                        for (GoodsItem gc : goods_item_list) {
                            if (spec_combination.equals(gc.getSpec_combination())) {
                                String str = gc.getId() + ";" + goodsCart.getCount();
                                cartlist.add(str);
                                break;
                            }
                        }
                    } else if (spec_id != null && !"".equals(spec_id)) {//一维规格
                        for (GoodsItem gc : goods_item_list) {
                            if ((spec_id + "_").equals(gc.getSpec_combination())) {
                                String str = gc.getId() + ";" + goodsCart.getCount();
                                cartlist.add(str);
                                break;
                            }
                        }
                    } else if (spec_id == null || "".equals(spec_id)) {
                        String str = goods_item_list.get(0).getId() + ";" + goodsCart.getCount();
                        cartlist.add(str);
                    }
                }
            }

        }
        map.put("cartlist", cartlist);

        String ret = Json.toJson(map, JsonFormat.compact());
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
