package com.javamalls.ctrl.buyer.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.WebForm;
import com.javamalls.front.web.tools.OrderViewTools;
import com.javamalls.front.web.tools.StoreViewTools;
import com.javamalls.platform.domain.Dynamic;
import com.javamalls.platform.domain.Evaluate;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.HomePageGoodsClass;
import com.javamalls.platform.domain.Message;
import com.javamalls.platform.domain.SnsAttention;
import com.javamalls.platform.domain.SnsFriend;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.query.DynamicQueryObject;
import com.javamalls.platform.domain.query.FavoriteQueryObject;
import com.javamalls.platform.service.IDynamicService;
import com.javamalls.platform.service.IEvaluateService;
import com.javamalls.platform.service.IFavoriteService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IHomePageGoodsClassService;
import com.javamalls.platform.service.IMessageService;
import com.javamalls.platform.service.ISnsAttentionService;
import com.javamalls.platform.service.ISnsFriendService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;

/**买家中心桌面管理
 *                       
 * @Filename: BaseBuyerAction.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class BaseBuyerAction {
    @Autowired
    private ISysConfigService          configService;
    @Autowired
    private IUserConfigService         userConfigService;
    @Autowired
    private IMessageService            messageService;
    @Autowired
    private StoreViewTools             storeViewTools;
    @Autowired
    private OrderViewTools             orderViewTools;
    @Autowired
    private IDynamicService            dynamicService;
    @Autowired
    private ISnsFriendService          snsFriendService;
    @Autowired
    private IFavoriteService           favService;
    @Autowired
    private IStoreService              storeService;
    @Autowired
    private IGoodsService              goodsService;
    @Autowired
    private IEvaluateService           evaluateService;
    @Autowired
    private ISnsAttentionService       SnsAttentionService;
    @Autowired
    private IHomePageGoodsClassService HomeGoodsClassService;
    @Autowired
    private IUserService               userService;

    @SecurityMapping(title = "买家中心", value = "/buyer/index.htm*", rtype = "buyer", rname = "买家中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/buyer/index.htm" })
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response,
                              String currentPage, String orderBy, String orderType, String type,@PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/buyer_index.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
       long count=0;
        User currentUser = SecurityUserHolder.getCurrentUser() ;
        if (currentUser != null) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("status", Integer.valueOf(0));
            params.put("user_id", currentUser.getId());
            count = this.messageService
                .queryCount(
                    "select count(obj) from Message obj where obj.parent.id is null and (obj.status=:status and obj.toUser.id=:user_id) or (obj.reply_status=1 and obj.fromUser.id=:user_id) or (obj.reply_status=2 and obj.toUser.id=:user_id) ",
                    params);
        }
        mv.addObject("msgscount", count);
        mv.addObject("storeViewTools", this.storeViewTools);
        mv.addObject("orderViewTools", this.orderViewTools);

      /*  DynamicQueryObject qo = new DynamicQueryObject(currentPage, mv, orderBy, orderType);
        qo.addQuery("obj.display", new SysMap("display", Boolean.valueOf(true)), "=");
        if ((type == null) || (type.equals(""))) {
            type = "2";
        }
        if (type.equals("1")) {
            qo.addQuery("obj.user.id", new SysMap("uid", SecurityUserHolder.getCurrentUser()
                .getId()), "=");
        }
        if (type.equals("2")) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("f_uid", SecurityUserHolder.getCurrentUser().getId());
            List<SnsFriend> myFriends = this.snsFriendService.query(
                "select obj from SnsFriend obj where obj.fromUser.id=:f_uid", map, -1, -1);
            Set<Long> ids = getSnsFriendToUserIds(myFriends);
            Map<String, Object> paras = new HashMap<String, Object>();
            paras.put("ids", null);
            if (myFriends.size() > 0) {
                paras.put("ids", ids);
            }
            qo.addQuery("obj.user.id in (:ids)", paras);
        }
        if (type.equals("3")) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("uid", SecurityUserHolder.getCurrentUser().getId());
            List<SnsAttention> SnsAttentions = this.SnsAttentionService.query(
                "select obj from SnsAttention obj where obj.fromUser.id=:uid ", params, -1, -1);
            Set<Long> ids = getSnsAttentionToUserIds(SnsAttentions);
            params.clear();
            params.put("ids", ids);
            if ((ids != null) && (ids.size() > 0)) {
                qo.addQuery("obj.user.id in (:ids)", params);
            }
        }
        if (type.equals("4")) {
            qo.addQuery("obj.user.id", new SysMap("uid", SecurityUserHolder.getCurrentUser()
                .getId()), "=");
            qo.addQuery("obj.store.id is not null", null);
        }*/

        /* 买家评价 */
      /*  Map<String, Object> params = new HashMap<String, Object>();
        params.put("userid", SecurityUserHolder.getCurrentUser().getId());
        // 最近一周
        Calendar week = Calendar.getInstance();
        week.add(Calendar.WEEK_OF_MONTH, -1);
        params.put("createtime", week.getTime());

        params.put("ev", 1);
        List<Evaluate> weeks0 = this.evaluateService.query(
            "select obj from Evaluate obj where obj.evaluate_user.id=:userid "
                    + "and obj.evaluate_seller_user is not null and obj.createtime>="
                    + ":createtime and obj.evaluate_seller_val=:ev", params, -1, -1);

        params.put("ev", 0);
        List<Evaluate> weeks1 = this.evaluateService.query(
            "select obj from Evaluate obj where obj.evaluate_user.id=:userid "
                    + "and obj.evaluate_seller_user is not null and obj.createtime>="
                    + ":createtime and obj.evaluate_seller_val=:ev", params, -1, -1);

        params.put("ev", -1);
        List<Evaluate> weeks2 = this.evaluateService.query(
            "select obj from Evaluate obj where obj.evaluate_user.id=:userid "
                    + "and obj.evaluate_seller_user is not null and obj.createtime>="
                    + ":createtime and obj.evaluate_seller_val=:ev", params, -1, -1);

        // 最近一个月
        Calendar month = Calendar.getInstance();
        month.add(Calendar.MONTH, -1);
        params.put("createtime", month.getTime());

        params.put("ev", 1);
        List<Evaluate> months0 = this.evaluateService.query(
            "select obj from Evaluate obj where obj.evaluate_user.id=:userid "
                    + "and obj.evaluate_seller_user is not null and obj.createtime>="
                    + ":createtime and obj.evaluate_seller_val=:ev", params, -1, -1);

        params.put("ev", 0);
        List<Evaluate> months1 = this.evaluateService.query(
            "select obj from Evaluate obj where obj.evaluate_user.id=:userid "
                    + "and obj.evaluate_seller_user is not null and obj.createtime>="
                    + ":createtime and obj.evaluate_seller_val=:ev", params, -1, -1);

        params.put("ev", -1);
        List<Evaluate> months2 = this.evaluateService.query(
            "select obj from Evaluate obj where obj.evaluate_user.id=:userid "
                    + "and obj.evaluate_seller_user is not null and obj.createtime>="
                    + ":createtime and obj.evaluate_seller_val=:ev", params, -1, -1);

        // 最近6个月
        Calendar sixmonth = Calendar.getInstance();
        sixmonth.add(Calendar.MONTH, -6);
        params.put("createtime", sixmonth.getTime());

        params.put("ev", 1);
        List<Evaluate> sixmonths0 = this.evaluateService.query(
            "select obj from Evaluate obj where obj.evaluate_user.id=:userid "
                    + "and obj.evaluate_seller_user is not null and obj.createtime>="
                    + ":createtime and obj.evaluate_seller_val=:ev", params, -1, -1);

        params.put("ev", 0);
        List<Evaluate> sixmonths1 = this.evaluateService.query(
            "select obj from Evaluate obj where obj.evaluate_user.id=:userid "
                    + "and obj.evaluate_seller_user is not null and obj.createtime>="
                    + ":createtime and obj.evaluate_seller_val=:ev", params, -1, -1);

        params.put("ev", -1);
        List<Evaluate> sixmonths2 = this.evaluateService.query(
            "select obj from Evaluate obj where obj.evaluate_user.id=:userid "
                    + "and obj.evaluate_seller_user is not null and obj.createtime>="
                    + ":createtime and obj.evaluate_seller_val=:ev", params, -1, -1);

        // 6个月前
        Calendar sixbefore = Calendar.getInstance();
        sixbefore.add(Calendar.MONTH, -6);
        params.put("createtime", sixbefore.getTime());

        params.put("ev", 1);
        List<Evaluate> sixbefores0 = this.evaluateService.query(
            "select obj from Evaluate obj where obj.evaluate_user.id=:userid "
                    + "and obj.evaluate_seller_user is not null and obj.createtime<="
                    + ":createtime and obj.evaluate_seller_val=:ev", params, -1, -1);

        params.put("ev", 0);
        List<Evaluate> sixbefores1 = this.evaluateService.query(
            "select obj from Evaluate obj where obj.evaluate_user.id=:userid "
                    + "and obj.evaluate_seller_user is not null and obj.createtime<="
                    + ":createtime and obj.evaluate_seller_val=:ev", params, -1, -1);

        params.put("ev", -1);
        List<Evaluate> sixbefores2 = this.evaluateService.query(
            "select obj from Evaluate obj where obj.evaluate_user.id=:userid "
                    + "and obj.evaluate_seller_user is not null and obj.createtime<="
                    + ":createtime and obj.evaluate_seller_val=:ev", params, -1, -1);

        mv.addObject("weeks0", weeks0.size());
        mv.addObject("weeks1", weeks1.size());
        mv.addObject("weeks2", weeks2.size());
        mv.addObject("months0", months0.size());
        mv.addObject("months1", months1.size());
        mv.addObject("months2", months2.size());
        mv.addObject("sixmonths0", sixmonths0.size());
        mv.addObject("sixmonths1", sixmonths1.size());
        mv.addObject("sixmonths2", sixmonths2.size());
        mv.addObject("sixbefores0", sixbefores0.size());
        mv.addObject("sixbefores1", sixbefores1.size());
        mv.addObject("sixbefores2", sixbefores2.size());

        params.clear();
        params.put("userid", SecurityUserHolder.getCurrentUser().getId());
        params.put("ev", 1);
        List<Evaluate> total = this.evaluateService.query(
            "select obj from Evaluate obj where obj.evaluate_user.id=:userid and "
                    + "obj.evaluate_seller_user is not null and obj.evaluate_seller_val=:ev",
            params, -1, -1);

        params.clear();
        params.put("userid", SecurityUserHolder.getCurrentUser().getId());
        List<Evaluate> count = this.evaluateService.query(
            "select obj from Evaluate obj where obj.evaluate_user.id=:userid  "
                    + "and obj.evaluate_seller_user is not null", params, -1, -1);

        if (count.size() < 1)
            mv.addObject("rate", 0);
        else
            mv.addObject("rate", CommUtil.div(total.size(), count.size()) * 100);
*/
      /*  qo.addQuery("obj.locked", new SysMap("locked", Boolean.valueOf(false)), "=");
        qo.addQuery("obj.dissParent.id is null", null);
        qo.setOrderBy("createtime");
        qo.setOrderType("desc");
        qo.setPageSize(Integer.valueOf(10));
        IPageList pList = this.dynamicService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);*/
   /*     List<Object> list = new ArrayList<Object>();
        for (int i = 1; i <= 120; i++) {
            list.add(Integer.valueOf(i));
        }*/
        mv.addObject("type", type);
     /*   mv.addObject("emoticons", list);*/

        mv.addObject("user",
            this.userService.getObjById(currentUser.getId()));
        return mv;
    }

    private Set<Long> getSnsAttentionToUserIds(List<SnsAttention> SnsAttentions) {
        Set<Long> ids = new HashSet<Long>();
        for (SnsAttention attention : SnsAttentions) {
            ids.add(attention.getToUser().getId());
        }
        return ids;
    }

    @SecurityMapping(title = "买家中心导航", value = "/buyer/buyer_nav.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/buyer/buyer_nav.htm" })
    public ModelAndView nav(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/buyer_nav.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String op = CommUtil.null2String(request.getAttribute("op"));
        mv.addObject("op", op);
        return mv;
    }
    
    /**
     * 订货买家中心
     * @param request
     * @param response
     * @return
     */
    @SecurityMapping(title = "买家中心导航", value = "/buyer/buyer_nav.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/buyer/buyer_nav.htm" })
    public ModelAndView buyer_nav(HttpServletRequest request, HttpServletResponse response,String op) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/buyer_nav.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        op = CommUtil.null2String(op);
        mv.addObject("op", op);
        return mv;
    }

    @SecurityMapping(title = "买家中心导航", value = "/buyer/buyer_head.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/buyer/buyer_head.htm" })
    public ModelAndView head(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/buyer_head.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        return mv;
    }
    
    /**
     * 无此权限提示 跳转
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/buyer/authority.htm" })
    public ModelAndView authority(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("error.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        mv.addObject("op_title", "您没有该项操作权限");
        mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
        return mv;
    }
    /**
     * 无此权限提示 ajax
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/ajax/ajax_authority.htm" })
    public ModelAndView ajax_authority(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("ajax_error.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        mv.addObject("op_title", "您没有该项操作权限");
        return mv;
    }

    private Set<Long> getSnsFriendToUserIds(List<SnsFriend> myfriends) {
        Set<Long> ids = new HashSet<Long>();
        if (myfriends.size() > 0) {
            for (SnsFriend friend : myfriends) {
                ids.add(friend.getToUser().getId());
            }
        }
        return ids;
    }

    @SecurityMapping(title = "动态发布保存", value = "/buyer/dynamic_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/buyer/dynamic_save.htm" })
    public ModelAndView dynamic_save(HttpServletRequest request, HttpServletResponse response,
                                     String content, String currentPage, String orderBy,
                                     String orderType, String store_id, String goods_id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/dynamic_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        WebForm wf = new WebForm();
        Dynamic dynamic = (Dynamic) wf.toPo(request, Dynamic.class);
        dynamic.setCreatetime(new Date());
        dynamic.setUser(SecurityUserHolder.getCurrentUser());
        dynamic.setContent(content);
        dynamic.setDisplay(true);
        if ((store_id != null) && (!store_id.equals(""))) {
            Store store = this.storeService.getObjById(CommUtil.null2Long(store_id));
            dynamic.setStore(store);
        }
        if ((goods_id != null) && (!goods_id.equals(""))) {
            Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
            dynamic.setGoods(goods);

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("uid", SecurityUserHolder.getCurrentUser().getId());
            params.put("gc_id", goods.getGc().getId());
            List<HomePageGoodsClass> hgcs = this.HomeGoodsClassService
                .query(
                    "select obj from HomePageGoodsClass obj where obj.user.id=:uid and obj.gc.id=:gc_id",
                    params, -1, -1);
            if (hgcs.size() == 0) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("uid", SecurityUserHolder.getCurrentUser().getId());
                HomePageGoodsClass hpgc = new HomePageGoodsClass();
                hpgc.setCreatetime(new Date());
                hpgc.setUser(SecurityUserHolder.getCurrentUser());
                hpgc.setGc(goods.getGc());
                this.HomeGoodsClassService.save(hpgc);
            }
        }
        this.dynamicService.save(dynamic);

        DynamicQueryObject qo = new DynamicQueryObject(currentPage, mv, orderBy, orderType);
        qo.addQuery("obj.dissParent.id is null", null);
        qo.setOrderBy("createtime");
        qo.setOrderType("desc");
        qo.setPageSize(Integer.valueOf(10));
        IPageList pList = this.dynamicService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        return mv;
    }

    @SecurityMapping(title = "删除动态", value = "/buyer/dynamic_del.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/buyer/dynamic_del.htm" })
    public ModelAndView dynamic_ajax_del(HttpServletRequest request, HttpServletResponse response,
                                         String id, String currentPage, String orderBy,
                                         String orderType) {
        if (!id.equals("")) {
            Dynamic dynamic = this.dynamicService.getObjById(Long.valueOf(Long.parseLong(id)));
            if (dynamic != null)
                this.dynamicService.delete(Long.valueOf(Long.parseLong(id)));
        }
        ModelAndView mv = new JModelAndView("user/default/usercenter/dynamic_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        DynamicQueryObject qo = new DynamicQueryObject(currentPage, mv, orderBy, orderType);
        qo.addQuery("obj.dissParent.id is null", null);
        qo.setOrderBy("createtime");
        qo.setOrderType("desc");
        qo.setPageSize(Integer.valueOf(10));
        IPageList pList = this.dynamicService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        return mv;
    }

    @SecurityMapping(title = "ajax回复保存方法", value = "/buyer/dynamic_ajax_reply.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/buyer/dynamic_ajax_reply.htm" })
    public ModelAndView dynamic_ajax_reply(HttpServletRequest request,
                                           HttpServletResponse response, String parent_id,
                                           String fieldName, String reply_content)
                                                                                  throws ClassNotFoundException {
        ModelAndView mv = new JModelAndView("user/default/usercenter/dynamic_childs_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        WebForm wf = new WebForm();
        Dynamic dynamic = (Dynamic) wf.toPo(request, Dynamic.class);
        Dynamic parent = null;
        if ((parent_id != null) && (!parent_id.equals(""))) {
            parent = this.dynamicService.getObjById(Long.valueOf(Long.parseLong(parent_id)));
            dynamic.setDissParent(parent);
            this.dynamicService.update(parent);
            dynamic.setDissParent(parent);
        }
        dynamic.setCreatetime(new Date());
        dynamic.setUser(SecurityUserHolder.getCurrentUser());
        dynamic.setContent(reply_content);
        this.dynamicService.save(dynamic);
        mv.addObject("obj", parent);
        return mv;
    }

    @SecurityMapping(title = "ajax赞动态方法", value = "/buyer/dynamic_ajax_praise.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/buyer/dynamic_ajax_praise.htm" })
    public void dynamic_ajax_praise(HttpServletRequest request, HttpServletResponse response,
                                    String dynamic_id) throws ClassNotFoundException {
        Dynamic dynamic = this.dynamicService.getObjById(Long.valueOf(Long.parseLong(dynamic_id)));
        dynamic.setPraiseNum(dynamic.getPraiseNum() + 1);
        this.dynamicService.update(dynamic);
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(dynamic.getPraiseNum());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SecurityMapping(title = "ajax转发动态保存方法", value = "/buyer/dynamic_ajax_turn.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/buyer/dynamic_ajax_turn.htm" })
    public ModelAndView dynamic_ajax_turn(HttpServletRequest request, HttpServletResponse response,
                                          String dynamic_id, String content, String currentPage,
                                          String orderType, String orderBy)
                                                                           throws ClassNotFoundException {
        ModelAndView mv = new JModelAndView("user/default/usercenter/dynamic_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Dynamic dynamic = this.dynamicService.getObjById(Long.valueOf(Long.parseLong(dynamic_id)));
        dynamic.setTurnNum(dynamic.getTurnNum() + 1);
        this.dynamicService.update(dynamic);
        Dynamic turn = new Dynamic();
        turn.setCreatetime(new Date());
        turn.setContent(content + "//转自" + dynamic.getUser().getUserName() + ":"
                        + dynamic.getContent());
        turn.setUser(SecurityUserHolder.getCurrentUser());
        this.dynamicService.save(turn);

        DynamicQueryObject qo = new DynamicQueryObject(currentPage, mv, orderBy, orderType);
        qo.addQuery("obj.dissParent.id is null", null);
        qo.setOrderBy("createtime");
        qo.setOrderType("desc");
        qo.setPageSize(Integer.valueOf(10));
        IPageList pList = this.dynamicService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        return mv;
    }

    @SecurityMapping(title = "删除动态下方自己发布的评论", value = "/buyer/dynamic_reply_del.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/buyer/dynamic_reply_del.htm" })
    public ModelAndView dynamic_reply_del(HttpServletRequest request, HttpServletResponse response,
                                          String id, String parent_id) {
        if (!id.equals("")) {
            Dynamic dynamic = this.dynamicService.getObjById(Long.valueOf(Long.parseLong(id)));
            if (dynamic != null)
                this.dynamicService.delete(Long.valueOf(Long.parseLong(id)));
        }
        ModelAndView mv = new JModelAndView("user/default/usercenter/dynamic_childs_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if ((parent_id != null) && (!parent_id.equals(""))) {
            Dynamic obj = this.dynamicService.getObjById(CommUtil.null2Long(parent_id));
            mv.addObject("obj", obj);
        }
        return mv;
    }

    @SecurityMapping(title = "用户分享收藏店铺列表", value = "/buyer/fav_store_list.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/buyer/fav_store_list.htm" })
    public ModelAndView fav_store_list(HttpServletRequest request, HttpServletResponse response,
                                       String currentPage, String orderBy, String orderType) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/fav_store_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        FavoriteQueryObject qo = new FavoriteQueryObject(currentPage, mv, orderBy, orderType);
        qo.addQuery("obj.user.id", new SysMap("uid", SecurityUserHolder.getCurrentUser().getId()),
            "=");
        qo.addQuery("obj.type", new SysMap("type", Integer.valueOf(1)), "=");
        qo.setPageSize(Integer.valueOf(4));
        IPageList pList = this.favService.list(qo);
        mv.addObject("objs", pList.getResult());
        String Ajax_url = CommUtil.getURL(request) + "/buyer/fav_store_list_ajax.htm";
        mv.addObject("gotoPageAjaxHTML",
            CommUtil.showPageAjaxHtml(Ajax_url, "", pList.getCurrentPage(), pList.getPages()));
        return mv;
    }

    @SecurityMapping(title = "用户分享收藏店铺ajax列表", value = "/buyer/fav_store_list_ajax.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/buyer/fav_store_list_ajax.htm" })
    public ModelAndView fav_store_list_ajax(HttpServletRequest request,
                                            HttpServletResponse response, String currentPage,
                                            String orderBy, String orderType) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/fav_store_list_ajax.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        FavoriteQueryObject qo = new FavoriteQueryObject(currentPage, mv, orderBy, orderType);
        qo.addQuery("obj.user.id", new SysMap("uid", SecurityUserHolder.getCurrentUser().getId()),
            "=");
        qo.addQuery("obj.type", new SysMap("type", Integer.valueOf(1)), "=");
        qo.setPageSize(Integer.valueOf(4));
        IPageList pList = this.favService.list(qo);
        mv.addObject("objs", pList.getResult());
        String Ajax_url = CommUtil.getURL(request) + "/buyer/fav_store_list_ajax.htm";
        mv.addObject("gotoPageAjaxHTML",
            CommUtil.showPageAjaxHtml(Ajax_url, "", pList.getCurrentPage(), pList.getPages()));
        return mv;
    }

    @SecurityMapping(title = "用户分享收藏商品列表", value = "/buyer/fav_goods_list.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/buyer/fav_goods_list.htm" })
    public ModelAndView fav_goods_list(HttpServletRequest request, HttpServletResponse response,
                                       String currentPage, String orderBy, String orderType) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/fav_goods_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        FavoriteQueryObject qo = new FavoriteQueryObject(currentPage, mv, orderBy, orderType);
        qo.addQuery("obj.user.id", new SysMap("uid", SecurityUserHolder.getCurrentUser().getId()),
            "=");
        qo.addQuery("obj.type", new SysMap("type", Integer.valueOf(0)), "=");
        qo.setPageSize(Integer.valueOf(4));
        IPageList pList = this.favService.list(qo);
        mv.addObject("objs", pList.getResult());
        String Ajax_url = CommUtil.getURL(request) + "/buyer/fav_goods_list_ajax.htm";
        mv.addObject("gotoPageAjaxHTML",
            CommUtil.showPageAjaxHtml(Ajax_url, "", pList.getCurrentPage(), pList.getPages()));
        return mv;
    }

    @SecurityMapping(title = "用户分享收藏商品ajax列表", value = "/buyer/fav_goods_list_ajax.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/buyer/fav_goods_list_ajax.htm" })
    public ModelAndView fav_goods_list_ajax(HttpServletRequest request,
                                            HttpServletResponse response, String currentPage,
                                            String orderBy, String orderType) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/fav_goods_list_ajax.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        FavoriteQueryObject qo = new FavoriteQueryObject(currentPage, mv, orderBy, orderType);
        qo.addQuery("obj.user.id", new SysMap("uid", SecurityUserHolder.getCurrentUser().getId()),
            "=");
        qo.addQuery("obj.type", new SysMap("type", Integer.valueOf(0)), "=");
        qo.setPageSize(Integer.valueOf(4));
        IPageList pList = this.favService.list(qo);
        mv.addObject("objs", pList.getResult());
        String Ajax_url = CommUtil.getURL(request) + "/buyer/fav_goods_list_ajax.htm";
        mv.addObject("gotoPageAjaxHTML",
            CommUtil.showPageAjaxHtml(Ajax_url, "", pList.getCurrentPage(), pList.getPages()));
        return mv;
    }
}
