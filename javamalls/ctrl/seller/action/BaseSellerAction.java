package com.javamalls.ctrl.seller.action;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.Md5Encrypt;
import com.javamalls.base.tools.WebForm;
import com.javamalls.ctrl.seller.Tools.MenuTools;
import com.javamalls.front.web.tools.AreaViewTools;
import com.javamalls.front.web.tools.OrderViewTools;
import com.javamalls.front.web.tools.StoreViewTools;
import com.javamalls.payment.chinabank.h5.util.JsonUtil;
import com.javamalls.platform.domain.Accessory;
import com.javamalls.platform.domain.Album;
import com.javamalls.platform.domain.Area;
import com.javamalls.platform.domain.Article;
import com.javamalls.platform.domain.Coupon;
import com.javamalls.platform.domain.CouponInfo;
import com.javamalls.platform.domain.IntegralLog;
import com.javamalls.platform.domain.Message;
import com.javamalls.platform.domain.Role;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.StoreVIP;
import com.javamalls.platform.domain.Strategy;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.UserStrategy;
import com.javamalls.platform.domain.query.CouponInfoQueryObject;
import com.javamalls.platform.domain.query.CouponQueryObject;
import com.javamalls.platform.domain.query.MessageQueryObject;
import com.javamalls.platform.domain.query.StoreVIPQueryObject;
import com.javamalls.platform.domain.query.UserQueryObject;
import com.javamalls.platform.service.IAccessoryService;
import com.javamalls.platform.service.IAlbumService;
import com.javamalls.platform.service.IAreaService;
import com.javamalls.platform.service.IArticleService;
import com.javamalls.platform.service.ICouponInfoService;
import com.javamalls.platform.service.ICouponService;
import com.javamalls.platform.service.IIntegralLogService;
import com.javamalls.platform.service.IMessageService;
import com.javamalls.platform.service.IRoleService;
import com.javamalls.platform.service.ISettleAccountsService;
import com.javamalls.platform.service.ISnsFriendService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.IStoreVIPService;
import com.javamalls.platform.service.IStrategyService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;
import com.javamalls.platform.service.IUserStrategyService;

/**优惠券管理
 *                       
 * @Filename: BaseSellerAction.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class BaseSellerAction {
    @Autowired
    private ISysConfigService      configService;
    @Autowired
    private IUserConfigService     userConfigService;
    @Autowired
    private IUserService           userService;
    @Autowired
    private IMessageService        messageService;
    @Autowired
    private IStoreService          storeService;
    @Autowired
    private IArticleService        articleService;
    @Autowired
    private StoreViewTools         storeViewTools;
    @Autowired
    private OrderViewTools         orderViewTools;
    @Autowired
    private AreaViewTools          areaViewTools;
    @Autowired
    private MenuTools              menuTools;
    @Autowired
    private ICouponService         couponService;
    @Autowired
    private IStoreVIPService       storeVIPServ;
    @Autowired
    private IAccessoryService      accessoryService;
    @Autowired
    private ICouponInfoService     couponinfoService;
    @Autowired
    private ISettleAccountsService settleAccountsService;
    @Autowired
    private IRoleService           roleService;
    @Autowired
    private IAlbumService          albumService;
    @Autowired
    private ISnsFriendService      sndFriendService;
    @Autowired
    private IIntegralLogService    integralLogService;
    @Autowired
    private IStrategyService       strategyService;
    @Autowired
    private IAreaService           areaService;
    @Autowired
    private IUserStrategyService   userStrategyService;

    @SecurityMapping(title = "新增优惠券", value = "/seller/modifyCoupon.htm*", rtype = "seller", rname = "优惠券", rcode = "user_center_seller", rgroup = "优惠券管理")
    @RequestMapping({ "/seller/modifyCoupon.htm" })
    public ModelAndView modifyCoupon(HttpServletRequest request, HttpServletResponse response,
                                     @RequestParam Long id) {
        ModelAndView mv = new JModelAndView("seller/coupon_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
            JModelAndView.SHOP_PATH, request, response);

        Coupon cou = this.couponService.getObjById(id);
        mv.addObject("obj", cou);
        return mv;
    }

    /**
     * 发放优惠券
     * 
     * @param request
     * @param response
     * @param id
     * @return
     */
    @SecurityMapping(title = "去发放优惠券", value = "/seller/sendcoupon.htm*", rtype = "seller", rname = "优惠券", rcode = "user_center_seller", rgroup = "优惠券管理")
    @RequestMapping({ "/seller/sendcoupon.htm" })
    public ModelAndView sendcoupon(HttpServletRequest request, HttpServletResponse response,
                                   @RequestParam Long userid, @RequestParam Long storeid) {
        ModelAndView mv = new JModelAndView("seller/sendcoupon.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
            JModelAndView.SHOP_PATH, request, response);
        mv.addObject("userid", userid.toString());
        mv.addObject("storeid", storeid);
        return mv;
    }

    /**
     * 发放优惠券
     * 
     * @param request
     * @param response
     * @param userid
     * @param couponid
     */
    @SecurityMapping(title = "发放优惠券", value = "/sendCoupon.htm*", rtype = "seller", rname = "优惠券", rcode = "user_center_seller", rgroup = "优惠券管理")
    @RequestMapping({ "/sendCoupon.htm" })
    public void sendCoupon(HttpServletRequest request, HttpServletResponse response,
                           @RequestParam String userids, @RequestParam String couponid,
                           @RequestParam Integer num) {
        PrintWriter writer = null;
        String s = null;
        Map<String, String> msg = new HashMap<String, String>();
        try {

            String[] arr = userids.split(",");
            for (String userid : arr) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("userid", Long.valueOf(userid));

                // 店铺会员
                List<StoreVIP> storeVIPs = this.storeVIPServ.query(
                    "select obj from StoreVIP obj where obj.id=:userid", params, -1, -1);

                // 关联优惠券与会员
                if (null != storeVIPs && storeVIPs.size() > 0) {
                    StoreVIP storeVip = storeVIPs.get(0);
                    Coupon coupon = this.couponService.getObjById(Long.valueOf(couponid));

                    // 保存优惠券发放信息
                    for (int i = 0; i < num; i++) {
                        CouponInfo info = new CouponInfo();
                        info.setCreatetime(new Date());
                        info.setCoupon(coupon);
                        info.setCoupon_sn(UUID.randomUUID().toString());
                        info.setUser(storeVip.getUser());
                        this.couponinfoService.save(info);
                    }

                } else {
                    msg.put("msg", "发放优惠券失败");
                }

            }

            msg.put("msg", "发放优惠券成功！");
            response.setContentType("text/plain");
            response.setHeader("Cache-Control", "no-cache");
            response.setCharacterEncoding("UTF-8");
            writer = response.getWriter();
        } catch (Exception e) {
            e.printStackTrace();
            msg.put("msg", "发放优惠券失败");
        }
        s = Json.toJson(msg);
        if (null != writer)
            writer.print(s);
    }

    /**
     * 加载店铺优惠券
     * 
     * @param request
     * @param response
     */
    @RequestMapping({ "/loadCoupon.htm" })
    public void loadCoupon(HttpServletRequest request, HttpServletResponse response,
                           @RequestParam String storeId) {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("store", Long.valueOf(storeId));

        List<Coupon> coupons = this.couponService.query(
            "select obj from Coupon obj where obj.store.id=:store", params, -1, -1);

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Coupon cou : coupons) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", cou.getId());
            map.put("name", cou.getCoupon_name());
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

    /**
     * 保存优惠券
     * 
     * @param request
     * @param response
     * @param currentPage
     * @return
     */
    @SecurityMapping(title = "创建优惠券", value = "/seller/coupon_save.htm*", rtype = "seller", rname = "优惠券", rcode = "user_center_seller", rgroup = "优惠券管理")
    @RequestMapping({ "/seller/coupon_save.htm" })
    public ModelAndView coupon_save(HttpServletRequest request, HttpServletResponse response,
                                    String currentPage) {
        WebForm wf = new WebForm();

        Coupon coupon = (Coupon) wf.toPo(request, Coupon.class);
        coupon.setCreatetime(new Date());
        coupon.setStore(SecurityUserHolder.getCurrentUser().getStore());// 商家

        String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
        String saveFilePathName = request.getSession().getServletContext().getRealPath("/")
                                  + uploadFilePath + File.separator + "coupon";
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            map = CommUtil.saveFileToServer(request, "coupon_img", saveFilePathName, null, null);
            if (map.get("fileName") != "") {
                Accessory coupon_acc = new Accessory();
                coupon_acc.setName(CommUtil.null2String(map.get("fileName")));
                coupon_acc.setExt((String) map.get("mime"));
                coupon_acc.setSize(((Float) map.get("fileSize")).floatValue());
                coupon_acc.setPath(uploadFilePath + "/coupon");
                coupon_acc.setWidth(CommUtil.null2Int(map.get("width")));
                coupon_acc.setHeight(CommUtil.null2Int(map.get("height")));
                coupon_acc.setCreatetime(new Date());
                this.accessoryService.save(coupon_acc);
                String pressImg = saveFilePathName + File.separator + coupon_acc.getName();
                String targetImg = saveFilePathName + File.separator + coupon_acc.getName() + "."
                                   + coupon_acc.getExt();
                if (!CommUtil.fileExist(saveFilePathName)) {
                    CommUtil.createFolder(saveFilePathName);
                }
                try {
                    Font font = new Font("Garamond", 1, 75);
                    CommUtil.waterMarkWithText(pressImg, targetImg, this.configService
                        .getSysConfig().getCurrency_code() + coupon.getCoupon_amount(), "#FF7455",
                        font, 24, 75, 1.0F);
                    font = new Font("宋体", 0, 15);
                    CommUtil.waterMarkWithText(targetImg, targetImg,
                        "满 " + coupon.getCoupon_order_amount() + " 减", "#726960", font, 95, 90,
                        1.0F);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                coupon.setCoupon_acc(coupon_acc);
            } else {
                String pressImg = request.getSession().getServletContext().getRealPath("")
                                  + File.separator + "resources" + File.separator + "style"
                                  + File.separator + "common" + File.separator + "template"
                                  + File.separator + "coupon_template.jpg";
                String targetImgPath = request.getSession().getServletContext().getRealPath("")
                                       + File.separator + uploadFilePath + File.separator
                                       + "coupon" + File.separator;
                if (!CommUtil.fileExist(targetImgPath)) {
                    CommUtil.createFolder(targetImgPath);
                }
                String targetImgName = UUID.randomUUID().toString() + ".jpg";
                try {
                    Font font = new Font("Garamond", 1, 75);
                    CommUtil.waterMarkWithText(
                        pressImg,
                        targetImgPath + targetImgName,
                        this.configService.getSysConfig().getCurrency_code()
                                + coupon.getCoupon_amount(), "#FF7455", font, 24, 75, 1.0F);
                    font = new Font("宋体", 0, 15);
                    CommUtil.waterMarkWithText(targetImgPath + targetImgName, targetImgPath
                                                                              + targetImgName,
                        "满 " + coupon.getCoupon_order_amount() + " 减", "#726960", font, 95, 90,
                        1.0F);
                } catch (Exception localException1) {
                }
                Accessory coupon_acc = new Accessory();
                coupon_acc.setName(targetImgName);
                coupon_acc.setExt("jpg");
                coupon_acc.setPath(uploadFilePath + "/coupon");
                coupon_acc.setCreatetime(new Date());
                this.accessoryService.save(coupon_acc);
                coupon.setCoupon_acc(coupon_acc);
            }
        } catch (IOException e) {
            e.printStackTrace();
            ModelAndView mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), JModelAndView.SHOP_PATH, request, response);
            mv.addObject("op_title", "添加失败，请联系管理员");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/storeyhq.htm");

            return mv;
        }
        if (coupon.getId() != null) {
            this.couponService.update(coupon);
        } else {
            this.couponService.save(coupon);
        }
        ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), JModelAndView.SHOP_PATH, request, response);
        mv.addObject("op_title", "成功添加优惠券【" + coupon.getCoupon_name() + "】");
        mv.addObject("url", CommUtil.getURL(request) + "/seller/storeyhq.htm");

        return mv;
    }

    /**
     * 增加优惠券
     * 
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/seller/coupon_add.htm" })
    public ModelAndView coupon_add(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("seller/coupon_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
            JModelAndView.SHOP_PATH, request, response);
        return mv;
    }

    /**
     * 店铺会员
     * 
     * @param request
     * @param response
     * @param currentPage
     * @param orderBy
     * @param orderType
     * @param type
     * @return
     */
    @RequestMapping({ "/seller/storevip.htm" })
    public ModelAndView storevip(HttpServletRequest request, HttpServletResponse response,
                                 String currentPage, String orderBy, String orderType, String type) {
        ModelAndView mv = new JModelAndView("seller/member_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
            JModelAndView.SHOP_PATH, request, response);
        StoreVIPQueryObject qo = new StoreVIPQueryObject(currentPage, mv, orderBy, orderType);

        qo.addQuery("obj.store.id", new SysMap("store", SecurityUserHolder.getCurrentUser()
            .getStore().getId()), "=");
        // qo.setPageSize(Integer.valueOf(10));
        IPageList pList = null;
        try {
            pList = this.storeVIPServ.list(qo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mv.addObject("objs", pList.getResult());
        return mv;
    }

    /**
     * 新订货宝店铺会员
     * 
     * @param request
     * @param response
     * @param currentPage
     * @param orderBy
     * @param orderType
     * @param type
     * @return
     */
    @SecurityMapping(title = "客户列表", value = "/seller/storeUser.htm*", rtype = "seller", rname = "客户管理", rcode = "store_user_seller", rgroup = "客户管理")
    @RequestMapping({ "/seller/storeUser.htm" })
    public ModelAndView storeUser(HttpServletRequest request, HttpServletResponse response,
                                  String currentPage, String orderBy, String orderType,
                                  String type, String quserName) {
        ModelAndView mv = new JModelAndView("seller/store_member_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
            JModelAndView.SHOP_PATH, request, response);
        UserQueryObject qo = new UserQueryObject(currentPage, mv, orderBy, orderType);

        qo.addQuery("obj.userRole", new SysMap("userRole", "ADMIN"), "!=");
        qo.addQuery("obj.parent.id is null ", null);
        if (quserName != null && !"".equals(quserName)) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("client_name", "%" + quserName + "%");
            map.put("mobile", "%" + quserName + "%");
            qo.addQuery(" (obj.client_name like :client_name or obj.userName like :mobile  ) ", map);
        }
        mv.addObject("quserName", quserName);
        // qo.setPageSize(Integer.valueOf(10));
        IPageList pList = null;
        try {
            pList = this.userService.list(qo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        return mv;
    }

    /**
     * 新增客户
     * @param request
     * @param response
     * @param currentPage
     * @param orderBy
     * @param orderType
     * @param type
     * @param quserName
     * @return
     */
    @SecurityMapping(title = "客户列表", value = "/seller/storeUser.htm*", rtype = "seller", rname = "客户管理", rcode = "store_user_seller", rgroup = "客户管理")
    @RequestMapping({ "/seller/storeUserAdd.htm" })
    public ModelAndView storeUserAdd(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("seller/store_member_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
            JModelAndView.SHOP_PATH, request, response);
        //获取业务员列表
        HashMap<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("parentId", SecurityUserHolder.getCurrentUser().getId());
        List<User> salemans = this.userService
            .query(
                "select obj from User obj where obj.parent.id=:parentId and obj.disabled = 0 and obj.salesManState = 1",
                paramMap, -1, -1);
        mv.addObject("salemans", salemans);
        List<Area> areas = this.areaService.query(
            "select obj from Area obj where obj.parent.id is null", null, -1, -1);
        String areaJson = JsonUtil.write2JsonStr(areas);
        mv.addObject("areaJson", areaJson);
        return mv;
    }

    /**
     * 指派业务员
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/seller/chooseSalemans.htm" })
    public ModelAndView chooseSalemans(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("seller/choose_salemans.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
            JModelAndView.SHOP_PATH, request, response);
        //获取业务员列表
        HashMap<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("parentId", SecurityUserHolder.getCurrentUser().getId());
        List<User> salemans = this.userService
            .query(
                "select obj from User obj where obj.parent.id=:parentId and obj.disabled = 0 and obj.salesManState = 1",
                paramMap, -1, -1);
        mv.addObject("salemans", salemans);
        String clientId = request.getParameter("clientId");
        mv.addObject("clientId", clientId);
        return mv;
    }

    /**
     * 指派业务员保存
     * @param request
     * @param response
     * @param salesMan_id
     * @param clientId
     * @return
     */
    @RequestMapping({ "/seller/chooseSalemansSave.htm" })
    public String chooseSalemansSave(HttpServletRequest request, HttpServletResponse response,
                                     String salesMan_id, String clientId) {
        if (salesMan_id != null && !"".equals(salesMan_id) && clientId != null
            && !"".equals(clientId)) {
            User salesMan = this.userService.getObjById(CommUtil.null2Long(salesMan_id));
            String[] clients = clientId.split(",");
            for (int i = 0; i < clients.length; i++) {
                User clientUser = this.userService.getObjById(Long.valueOf(clients[i]));
                clientUser.setSalesMan(salesMan);
                this.userService.update(clientUser);
            }
        }
        return "redirect:storeUserNew.htm?status=2";
    }

    /**
     * 为客户指定价格策略
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/seller/chooseStrategy.htm" })
    public ModelAndView chooseStrategy(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("seller/choose_strategys.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
            JModelAndView.SHOP_PATH, request, response);
        //获取价格策略列表
        User currUser = SecurityUserHolder.getCurrentUser();
        User user = null;
        if (currUser.getParent() != null) {
            user = currUser.getParent();
        } else {
            user = currUser;
        }

        HashMap<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("storeId", user.getStore().getId());
        List<Strategy> strategys = this.strategyService
            .query(
                "select obj from Strategy obj where obj.disabled=false and obj.store.id =:storeId and obj.status = 1",
                paramMap, -1, -1);
        mv.addObject("strategys", strategys);
        String clientId = request.getParameter("clientId");
        mv.addObject("clientId", clientId);
        return mv;
    }

    /**
     * 为客户指定价格策略保存
     * @param request
     * @param response
     * @param strategy_id
     * @param clientId
     * @return
     */
    @RequestMapping({ "/seller/chooseStrategysSave.htm" })
    public String chooseStrategysSave(HttpServletRequest request, HttpServletResponse response,
                                      String strategy_id, String clientId) {
        if (clientId != null && !"".equals(clientId)) {
            Strategy strategy = null;

            if (strategy_id != null && !"-1".equals(strategy_id)) {
                strategy = this.strategyService.getObjById(CommUtil.null2Long(strategy_id));
            }
            String[] clients = clientId.split(",");
            for (int i = 0; i < clients.length; i++) {
                User clientUser = this.userService.getObjById(Long.valueOf(clients[i]));
                UserStrategy userStrategy = this.userStrategyService.getUserStrategyByUserIdAndStoreId(clientUser.getId(), strategy.getStore().getId());
                if(userStrategy==null){
                	userStrategy = new UserStrategy();
                	userStrategy.setCreatetime(new Date());
                	userStrategy.setDisabled(false);
                	userStrategy.setStore_id(strategy.getStore().getId());
                	userStrategy.setStrategy(strategy);
                	userStrategy.setUser(clientUser);
                	this.userStrategyService.save(userStrategy);
                }
               // clientUser.setStrategy(strategy);
               // this.userService.update(clientUser);
            }
        }
        return "redirect:distributor_apply_list.htm";
    }

    /**
     * 新增客户保存操作
     * @param request
     * @param response
     * @param userName
     * @param storeId
     * @param email
     * @param mobile
     * @param password
     * @param trueName
     * @return
     */
    @SecurityMapping(title = "客户列表", value = "/seller/storeUser.htm*", rtype = "seller", rname = "客户管理", rcode = "store_user_seller", rgroup = "客户管理")
    @RequestMapping({ "/seller/storeUserSave.htm" })
    public ModelAndView storeUserSave(HttpServletRequest request, HttpServletResponse response,
                                      String id, String client_name, String userName, String email,
                                      String password,String cus_ser_code, String trueName, String salesMan_id,
                                      String sex, String accountName, String bankName,
                                      String bankAccount, String taxpayer_number,
                                      String header_invoice, String disabled, String QQ,
                                      String birthday, String area_id) {
        ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        boolean reg = true;

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userName", userName);
        params.put("mobile", userName);
        params.put("id", CommUtil.null2Long(id));
        List<User> users = this.userService
            .query(
                "select obj from User obj where obj.id!=:id and (obj.userName=:userName or obj.mobile=:mobile) and obj.disabled=false ",
                params, -1, -1);
        if ((users != null) && (users.size() > 0)) {
            reg = false;
        }
        if (userName == null || "".equals(userName)) {
            reg = false;
        }
        if (reg) {
            if (id == null || "".equals(id)) {//新增
                User user = new User();
                user.setClient_name(client_name);
                user.setUserName(userName);
                user.setMobile(userName);
                user.setUserRole("BUYER");
                user.setCreatetime(new Date());
                user.setEmail(email);
                user.setTrueName(trueName);
                user.setSex(CommUtil.null2Int(sex));
                user.setPassword(Md5Encrypt.md5(password).toLowerCase());
                user.setBankName(bankName);
                user.setBankAccount(bankAccount);
                user.setCus_ser_code(cus_ser_code);
                user.setAccountName(accountName);
                user.setTaxpayer_number(taxpayer_number);
                user.setHeader_invoice(header_invoice);
                user.setDisabled(CommUtil.null2Boolean(disabled));
                user.setQQ(QQ);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date parse;
                try {
                    parse = sdf.parse(birthday);
                    user.setBirthday(parse);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
                if (area != null) {
                    user.setArea(area);
                }

                //     user.setUserOfstore(Integer.valueOf(storeId));
                //客户所属业务员
                params.clear();
                User salesMan = this.userService.getObjById(CommUtil.null2Long(salesMan_id));
                user.setSalesMan(salesMan);
                params.clear();
                params.put("type", "BUYER");
                List<Role> roles = this.roleService.query(
                    "select obj from Role obj where obj.type=:type", params, -1, -1);
                user.getRoles().addAll(roles);
                if (this.configService.getSysConfig().isIntegral()) {
                    user.setIntegral(this.configService.getSysConfig().getMemberRegister());
                    this.userService.save(user);
                    IntegralLog log = new IntegralLog();
                    log.setCreatetime(new Date());
                    log.setContent("用户注册增加" + this.configService.getSysConfig().getMemberRegister()
                                   + "分");
                    log.setIntegral(this.configService.getSysConfig().getMemberRegister());
                    log.setIntegral_user(user);
                    log.setType("reg");
                    this.integralLogService.save(log);
                } else {
                    this.userService.save(user);
                }
                Album album = new Album();
                album.setCreatetime(new Date());
                album.setAlbum_default(true);
                album.setAlbum_name("默认相册");
                album.setAlbum_sequence(-10000);
                album.setUser(user);
                this.albumService.save(album);
                request.getSession(false).removeAttribute("verify_number");
                /*
                 * 买家注册时自动添加卖家为好友
                 */
                /* try{
                 	 SnsFriend friend = new SnsFriend();
                      friend.setCreatetime(new Date());
                      friend.setFromUser(user);
                      friend.setToUser(this.storeService.getObjById(Long.valueOf(storeId)).getUser());
                      this.sndFriendService.save(friend);
                 }catch(Exception e){
                 	e.printStackTrace();
                 }*/
            } else {
                User user = this.userService.getObjById(CommUtil.null2Long(id));
                if (user != null) {
                    user.setClient_name(client_name);
                    user.setUserName(userName);
                    user.setMobile(userName);
                    user.setCus_ser_code(cus_ser_code);
                    user.setEmail(email);
                    user.setTrueName(trueName);
                    user.setSex(CommUtil.null2Int(sex));
                    if (password != null && !"".equals(password)) {
                        user.setPassword(Md5Encrypt.md5(password).toLowerCase());
                    }
                    user.setBankName(bankName);
                    user.setBankAccount(bankAccount);
                    user.setAccountName(accountName);
                    user.setTaxpayer_number(taxpayer_number);
                    user.setHeader_invoice(header_invoice);
                    user.setDisabled(CommUtil.null2Boolean(disabled));
                    user.setQQ(QQ);
                    if (birthday != null && !"".equals(birthday)) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date parse;
                        try {
                            parse = sdf.parse(birthday);
                            user.setBirthday(parse);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                    Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
                    if (area != null) {
                        user.setArea(area);
                    }

                    //客户所属业务员
                    params.clear();
                    User salesMan = this.userService.getObjById(CommUtil.null2Long(salesMan_id));
                    user.setSalesMan(salesMan);
                    this.userService.update(user);
                }

            }

        }
        mv.addObject("op_title", "客户信息添加成功");
        mv.addObject("url", CommUtil.getURL(request) + "/seller/storeUserNew.htm?status=2");
        return mv;

    }
    /**
     * 新订货宝店铺会员详情编辑
     * 
     * @param request
     * @param response
     * @param currentPage
     * @param orderBy
     * @param orderType
     * @param type
     * @return
     */
    @SecurityMapping(title = "客户列表", value = "/seller/storeUser.htm*", rtype = "seller", rname = "客户管理", rcode = "store_user_seller", rgroup = "客户管理")
    @RequestMapping({ "/seller/storeUserEdit.htm" })
    public ModelAndView storeUserEdit(HttpServletRequest request, HttpServletResponse response,
                                        String id) {
        ModelAndView mv = new JModelAndView("seller/store_member_edit.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
            JModelAndView.SHOP_PATH, request, response);

        List<Area> areas2 = this.areaService.query(
            "select obj from Area obj where obj.parent.id is null", null, -1, -1);

        String areaJson = JsonUtil.write2JsonStr(areas2);
        mv.addObject("areaJson", areaJson);

        User storeUser = new User();
        try {
            storeUser = this.userService.getObjById(Long.valueOf(id));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mv.addObject("obj", storeUser);

        HashMap<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("parentId", SecurityUserHolder.getCurrentUser().getId());
        List<User> salemans = this.userService
            .query(
                "select obj from User obj where obj.parent.id=:parentId and obj.disabled = 0 and obj.salesManState = 1",
                paramMap, -1, -1);
        mv.addObject("salemans", salemans);

        return mv;
    }
    /**
     * 新订货宝店铺会员详情
     * 
     * @param request
     * @param response
     * @param currentPage
     * @param orderBy
     * @param orderType
     * @param type
     * @return
     */
    @SecurityMapping(title = "客户列表", value = "/seller/storeUser.htm*", rtype = "seller", rname = "客户管理", rcode = "store_user_seller", rgroup = "客户管理")
    @RequestMapping({ "/seller/storeUserDetail.htm" })
    public ModelAndView storeUserDetail(HttpServletRequest request, HttpServletResponse response,
                                        String id) {
        ModelAndView mv = new JModelAndView("seller/store_member_detail.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
            JModelAndView.SHOP_PATH, request, response);

        List<Area> areas2 = this.areaService.query(
            "select obj from Area obj where obj.parent.id is null", null, -1, -1);

        String areaJson = JsonUtil.write2JsonStr(areas2);
        mv.addObject("areaJson", areaJson);

        User storeUser = new User();
        try {
            storeUser = this.userService.getObjById(Long.valueOf(id));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mv.addObject("obj", storeUser);

        HashMap<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("parentId", SecurityUserHolder.getCurrentUser().getId());
        List<User> salemans = this.userService
            .query(
                "select obj from User obj where obj.parent.id=:parentId and obj.disabled = 0 and obj.salesManState = 1",
                paramMap, -1, -1);
        mv.addObject("salemans", salemans);

        return mv;
    }

    /**
     * 店铺优惠券列表
     * 
     * @param request
     * @param response
     * @param currentPage
     * @param orderBy
     * @param orderType
     * @param coupon_name
     * @param coupon_begin_time
     * @param coupon_end_time
     * @return
     */
    @RequestMapping({ "/seller/storeyhq.htm" })
    public ModelAndView storeyhq(HttpServletRequest request, HttpServletResponse response,
                                 String currentPage, String orderBy, String orderType,
                                 String coupon_name, String coupon_begin_time,
                                 String coupon_end_time) {
        ModelAndView mv = new JModelAndView("seller/coupon_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
            JModelAndView.SHOP_PATH, request, response);

        CouponQueryObject qo = new CouponQueryObject(currentPage, mv, orderBy, orderType);
        // 只要本店铺的优惠券，过滤平台的
        qo.addQuery("obj.store.id", new SysMap("storeid", SecurityUserHolder.getCurrentUser()
            .getStore().getId()), "=");
        if (!CommUtil.null2String(coupon_name).equals("")) {
            qo.addQuery("obj.coupon_name", new SysMap("coupon_name", "%" + coupon_name + "%"),
                "like");
        }
        if (!CommUtil.null2String(coupon_begin_time).equals("")) {
            qo.addQuery("obj.coupon_begin_time",
                new SysMap("coupon_begin_time", CommUtil.formatDate(coupon_begin_time)), ">=");
        }
        if (!CommUtil.null2String(coupon_end_time).equals("")) {
            qo.addQuery("obj.coupon_end_time",
                new SysMap("coupon_end_time", CommUtil.formatDate(coupon_end_time)), "<=");
        }
        IPageList pList = this.couponService.list(qo);
        mv.addObject("coupons", pList.getResult());
        return mv;
    }

    /**
     * 发放对象列表
     * 
     * @param request
     * @param response
     * @param currentPage
     * @param orderBy
     * @param orderType
     * @param coupon_id
     * @return
     */
    @RequestMapping({ "/seller/coupon_info_list.htm" })
    public ModelAndView coupon_info_list(HttpServletRequest request, HttpServletResponse response,
                                         String currentPage, String orderBy, String orderType,
                                         String coupon_id) {
        ModelAndView mv = new JModelAndView("seller/coupon_info_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
            JModelAndView.SHOP_PATH, request, response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        String params = "";
        CouponInfoQueryObject qo = new CouponInfoQueryObject(currentPage, mv, orderBy, orderType);
        qo.addQuery("obj.coupon.id", new SysMap("coupon_id", CommUtil.null2Long(coupon_id)), "=");
        IPageList pList = this.couponinfoService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", params, pList, mv);
        mv.addObject("coupon_id", coupon_id);
        return mv;
    }

    @SecurityMapping(title = "卖家中心", value = "/seller/index.htm*", rtype = "seller", rname = "卖家中心", rcode = "user_center_seller", rgroup = "用户中心")
    @RequestMapping({ "/seller/index.htm" })
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller_index.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        User user;
        List<Message> msgs;
        List<Article> mjbz;
        try {
            user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
            msgs = new ArrayList<Message>();
            Map<String, Object> params = new HashMap<String, Object>();

            MessageQueryObject qo = new MessageQueryObject();
            /*qo.addQuery("obj.type", new SysMap("type", Integer.valueOf(CommUtil.null2Int(1))),
                "=");
            qo.addQuery("obj.toUser.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser()
                .getId()), "=");*/
            qo.addQuery("obj.fromUser.id", new SysMap("user_id", SecurityUserHolder
                .getCurrentUser().getId()), "=");
            qo.addQuery("obj.reply_status =1", null);
            qo.setOrderBy("createtime");
            qo.setOrderType("desc");
            qo.setCurrentPage(Integer.valueOf(CommUtil.null2Int(0)));
            qo.setPageSize(4);
            IPageList pList = this.messageService.list(qo);
            msgs = pList.getResult();

            params.clear();
            params.put("class_mark", "notice");
            params.put("display", Boolean.valueOf(true));
            List<Article> articles = this.articleService.query(
                "select obj from Article obj where obj.articleClass.mark"
                        + "=:class_mark and obj.display=:display order by obj.createtime desc",
                params, 0, 6);
            mv.addObject("articles", articles);

            params.clear();
            params.put("class_mark", "mjbz");
            params.put("display", true);
            mjbz = this.articleService.query(
                "select obj from Article obj where obj.articleClass.mark=:class_mark "
                        + "and obj.display=:display order by obj.createtime desc", params, 0, 6);
            // 结账信息
            /*   params.clear();
               params.put("month", Calendar.getInstance().get(Calendar.MONTH) + 1);
               List<SettleAccunts> sets = this.settleAccountsService.query(
                   "select obj from SettleAccunts obj where obj.month=:month", params, -1, -1);
               if (sets.size() > 0)
                   mv.addObject("settleAcc", sets.get(0));*/
            //昨天
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            Date time = calendar.getTime();
            String last_date = sdf.format(time);
            mv.addObject("last_date", last_date);
            //今月
            Calendar cal = Calendar.getInstance();//获取当前日期
            cal.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
            Date beginDate = cal.getTime();
            String beginformat = sdf.format(beginDate);
            mv.addObject("now_month_begin", beginformat);
            cal.add(Calendar.MONTH, 1);//月增加1天
            cal.add(Calendar.DAY_OF_MONTH, -1);//日期倒数一日,既得到本月最后一天
            Date endDate = cal.getTime();
            String endformat = sdf.format(endDate);
            mv.addObject("now_month_end", endformat);

            //上月
            Calendar cal2 = Calendar.getInstance();//获取当前日期
            cal2.add(Calendar.MONTH, -1);//上个月

            cal2.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
            Date beginDate2 = cal2.getTime();
            String beginformat2 = sdf.format(beginDate2);
            mv.addObject("last_month_begin", beginformat2);
            cal2.add(Calendar.MONTH, 1);//月增加1天
            cal2.add(Calendar.DAY_OF_MONTH, -1);//日期倒数一日,既得到本月最后一天
            Date endDate2 = cal2.getTime();
            String endformat2 = sdf.format(endDate2);
            mv.addObject("last_month_end", endformat2);
            //今天
            Date date = new Date();
            String format = sdf.format(date);
            mv.addObject("now_date", format);

            mv.addObject("orderViewTools", orderViewTools);
            mv.addObject("mjbz", mjbz);
            mv.addObject("user", user);
            mv.addObject("store", user.getStore());
            mv.addObject("msgs", msgs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mv.addObject("storeViewTools", this.storeViewTools);
        mv.addObject("orderViewTools", this.orderViewTools);
        mv.addObject("areaViewTools", this.areaViewTools);
        return mv;
    }

    @SecurityMapping(title = "卖家中心导航", value = "/seller/seller_nav.htm*", rtype = "seller", rname = "卖家中心导航", rcode = "user_center_seller", rgroup = "用户中心")
    @RequestMapping({ "/seller/seller_nav.htm" })
    public ModelAndView nav(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller_nav.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        int store_status = 0;
        Store store = null;
        if (user.getStore() != null) {
            store = this.storeService.getObjByProperty("id", user.getStore().getId());
        }

        if (store != null) {
            store_status = store.getStore_status();
        }
        String op = CommUtil.null2String(request.getAttribute("op"));
        mv.addObject("op", op);
        mv.addObject("store_status", Integer.valueOf(store_status));

        mv.addObject("user", user);
        mv.addObject("store", user.getStore());
        return mv;
    }

    @SecurityMapping(title = "卖家中心导航", value = "/seller/nav_head.htm*", rtype = "seller", rname = "卖家中心导航", rcode = "user_center_seller", rgroup = "用户中心")
    @RequestMapping({ "/seller/nav_head.htm" })
    public ModelAndView nav_head(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller_head.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String type = CommUtil.null2String(request.getAttribute("type"));
        mv.addObject("type", type.equals("") ? "goods" : type);
        mv.addObject("menuTools", this.menuTools);
        return mv;
    }

    @SecurityMapping(title = "卖家中心快捷功能设置", value = "/seller/store_quick_menu.htm*", rtype = "seller", rname = "用户中心", rcode = "user_center_seller", rgroup = "用户中心")
    @RequestMapping({ "/seller/store_quick_menu.htm" })
    public ModelAndView store_quick_menu(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/store_quick_menu.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        return mv;
    }

    @SecurityMapping(title = "卖家中心快捷功能设置保存", value = "/seller/store_quick_menu_save.htm*", rtype = "seller", rname = "用户中心", rcode = "user_center_seller", rgroup = "用户中心")
    @RequestMapping({ "/seller/store_quick_menu_save.htm" })
    public ModelAndView store_quick_menu_save(HttpServletRequest request,
                                              HttpServletResponse response, String menus) {
        String[] menu_navs = menus.split(",");
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (String menu_nav : menu_navs) {
            if (!menu_nav.equals("")) {
                String[] infos = menu_nav.split("\\|");
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("menu_url", infos[0]);
                map.put("menu_name", infos[1]);
                list.add(map);
            }
        }
        user.setStore_quick_menu(Json.toJson(list, JsonFormat.compact()));
        this.userService.update(user);
        ModelAndView mv = new JModelAndView("user/default/usercenter/store_quick_menu_info.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("user", user);
        mv.addObject("menuTools", this.menuTools);
        return mv;
    }

}
