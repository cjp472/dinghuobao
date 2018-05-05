package com.javamalls.ctrl.buyer.action;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import sun.misc.BASE64Decoder;

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.constant.Constant;
import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.Md5Encrypt;
import com.javamalls.base.tools.WebForm;
import com.javamalls.ctrl.admin.tools.MsgTools;
import com.javamalls.front.web.h5.action.Constent;
import com.javamalls.payment.chinabank.h5.util.JsonUtil;
import com.javamalls.platform.domain.Accessory;
import com.javamalls.platform.domain.Area;
import com.javamalls.platform.domain.CompanyInfo;
import com.javamalls.platform.domain.GoodsClass;
import com.javamalls.platform.domain.MobileVerifyCode;
import com.javamalls.platform.domain.Role;
import com.javamalls.platform.domain.SnsFriend;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.StoreGrade;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.UserGoodsClass;
import com.javamalls.platform.domain.Warehouse;
import com.javamalls.platform.domain.query.SnsFriendQueryObject;
import com.javamalls.platform.domain.query.UserQueryObject;
import com.javamalls.platform.service.IAccessoryService;
import com.javamalls.platform.service.IAreaService;
import com.javamalls.platform.service.ICompanyInfoService;
import com.javamalls.platform.service.IGoodsClassService;
import com.javamalls.platform.service.IMobileVerifyCodeService;
import com.javamalls.platform.service.IRoleService;
import com.javamalls.platform.service.ISnsFriendService;
import com.javamalls.platform.service.IStoreGradeService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.ITemplateService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserGoodsClassService;
import com.javamalls.platform.service.IUserService;
import com.javamalls.platform.service.IWarehouseService;
import com.utils.SendReqAsync;

/**买家中心  个人信息修改  手机、邮箱修改等
 *                       
 * @Filename: AccountBuyerAction.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class AccountBuyerAction {
    @Autowired
    private ISysConfigService        configService;
    @Autowired
    private IUserConfigService       userConfigService;
    @Autowired
    private IUserService             userService;
    @Autowired
    private IMobileVerifyCodeService mobileverifycodeService;
    @Autowired
    private IAccessoryService        accessoryService;
    @Autowired
    private ISnsFriendService        sndFriendService;
    @Autowired
    private ITemplateService         templateService;
    @Autowired
    private IAreaService             areaService;
    @Autowired
    private MsgTools                 msgTools;
    @Autowired
    private SendReqAsync             sendReqAsync;
    @Autowired
    private IStoreService            storeService;

    @Autowired
    private IStoreGradeService       storeGradeService;
    @Autowired
    private IWarehouseService        warehouseService;
    @Autowired
    private ICompanyInfoService      companyInfoService;
    @Autowired
    private IGoodsClassService       goodsClassService;
    @Autowired
    private IUserGoodsClassService   userGoodsClassService;
    @Autowired
    private IRoleService             roleService;

    private static final String      DEFAULT_AVATAR_FILE_EXT     = ".jpg";
    private static BASE64Decoder     _decoder                    = new BASE64Decoder();
    public static final String       OPERATE_RESULT_CODE_SUCCESS = "200";
    public static final String       OPERATE_RESULT_CODE_FAIL    = "400";

    @SecurityMapping(title = "个人信息导航", value = "/buyer/account_nav.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/buyer/account_nav.htm" })
    public ModelAndView account_nav(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/account_nav.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String op = CommUtil.null2String(request.getAttribute("op"));
        mv.addObject("op", op);
        mv.addObject("user",
            this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId()));
        return mv;
    }

    @SecurityMapping(title = "个人信息", value = "/buyer/account.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/buyer/account.htm" })
    public ModelAndView account(HttpServletRequest request, HttpServletResponse response,
                                @PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/account.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("user",
            this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId()));

        // 加载品牌
        /*   List<GoodsBrand> gbs = this.goodsBrandService.query(
               "select obj from GoodsBrand obj where btype=1 and audit=1 order by obj.sequence asc",
               null, -1, -1);
           mv.addObject("gbs", gbs);*/

        List<Area> areas = this.areaService.query(
            "select obj from Area obj where obj.parent.id is null", null, -1, -1);
        mv.addObject("areas", areas);
        return mv;
    }

    /**
     * 申请成为分销商买家
     * @param request
     * @param response
     * @param storeId
     * @return
     */
    @SecurityMapping(title = "申请分销商", value = "/buyer/account.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/buyer/buyerseller_apply.htm" })
    public ModelAndView seller_apply(HttpServletRequest request, HttpServletResponse response,
                                     @PathVariable String storeId) {

        String url = "user/default/usercenter/buyerseller_apply.html";
        String loginUrl = "buyer/buyer_login.html";
        //判断终端类型
        String jm_view_type = CommUtil.null2String(request.getSession(false).getAttribute(
            Constent.WEB_TYPE_KEY));
        if (CommUtil.isNotNull(jm_view_type) && jm_view_type.equals("mobile")) {
            url = "user/default/usercenter/h5/h5_buyerseller_apply.html";
            loginUrl = "h5/login.html";
        }

        ModelAndView mv = new JModelAndView(url, this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        User currentUser = SecurityUserHolder.getCurrentUser();
        if (currentUser == null) {
            mv = new JModelAndView(loginUrl, this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), JModelAndView.SHOP_PATH, request, response);
            return mv;
        }

        List<Area> areas = this.areaService.query(
            "select obj from Area obj where obj.parent.id is null", null, -1, -1);
        mv.addObject("areas", areas);
        User user = this.userService.getObjById(currentUser.getId());
        mv.addObject("companyInfo", user.getCompanyInfo());
        Store store = user.getStore();
        if (store != null) {
            mv.addObject("store_status", user.getStore().getStore_status());
        }

        return mv;
    }

    /**
     * 申请分销商保存
     * @param request
     * @param response
     * @param area_id
     * @param birthday
     * @return
     */
    @SecurityMapping(title = "申请分销商保存", value = "/buyer/account_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/buyer/buyerseller_apply_save.htm" })
    public ModelAndView buyerseller_apply_save(HttpServletRequest request,
                                               HttpServletResponse response, String area_id,
                                               String companyId) {

        String url = "buyer_success.html";
        String loginUrl = "buyer/buyer_login.html";
        //判断终端类型
        String jm_view_type = CommUtil.null2String(request.getSession(false).getAttribute(
            Constent.WEB_TYPE_KEY));
        if (CommUtil.isNotNull(jm_view_type) && jm_view_type.equals("mobile")) {
            url = "h5/success.html";
            loginUrl = "h5/login.html";
        }

        ModelAndView mv = new JModelAndView(url, this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        if (SecurityUserHolder.getCurrentUser() == null) {
            mv = new JModelAndView(loginUrl, this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), JModelAndView.SHOP_PATH, request, response);
            return mv;
        }
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        if (companyId == null || "".equals(companyId)) {
            //2、生成店铺
            Store store = new Store();
            StoreGrade grade = this.storeGradeService.getObjById(Long.valueOf(Long.parseLong("1")));//默认基本
            store.setGrade(grade);
            store.setTemplate("default");
            store.setCreatetime(new Date());

            store.setDelivery_begin_time(new Date());
            store.setDelivery_end_time(new Date(2114, 11, 29));
            store.setCombin_begin_time(new Date());
            store.setCombin_end_time(new Date(2114, 11, 29));
            store.setStore_status(CommUtil.null2Int(1));//待审核

            store.setLogon_access_state(1);//是限制登录后访问商城0不限制，1限制

            store.setValiditybegin(new Date());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, 10);
            store.setValidity(calendar.getTime());
            this.storeService.save(store);

            user.setStore(store);

            if (user.getUserRole().equals("BUYER")) {
                user.setUserRole("BUYER_SELLER");
            }

            Map storeparams = new HashMap();
            storeparams.put("type", "SELLER");
            List<Role> storeroles = this.roleService.query(
                "select obj from Role obj where obj.type=:type", storeparams, -1, -1);
            user.getRoles().addAll(storeroles);
            this.userService.update(user);

            //用户调用接口
            String userjson = JsonUtil.write2JsonStr(user);
            sendReqAsync.sendMessageUtil(Constant.USER_INTEFACE_URL_EDIT, userjson, "编辑会员");

            //用户调用接口
            String storejson = JsonUtil.write2JsonStr(store);
            sendReqAsync.sendMessageUtil(Constant.STORE_INTEFACE_URL_ADD, storejson, "新增店铺");

            //创建默认仓库
            try {
                Warehouse warehouse = new Warehouse();
                warehouse.setCreatetime(new Date());
                warehouse.setDisabled(false);
                warehouse.setManager(user.getTrueName());
                warehouse.setName("默认仓");
                warehouse.setStatus(1);
                warehouse.setStore(store);
                this.warehouseService.save(warehouse);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //3、保存公司信息
            WebForm wf = new WebForm();
            CompanyInfo company = (CompanyInfo) wf.toPo(request, CompanyInfo.class);
            company.setCreatetime(new Date());
            company.setDisabled(false);
            company.setUser(user);
            Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
            company.setArea(area);
            this.companyInfoService.save(company);

            //用户调用接口
            String companyjson = JsonUtil.write2JsonStr(company);
            sendReqAsync.sendMessageUtil(Constant.COMPANY_INTEFACE_URL_ADD, companyjson, "新增公司");

            //4、导入平台商品分类
            Map goodClassmap = new HashMap();
            goodClassmap.put("disabled", false);
            goodClassmap.put("level", 0);
            List<GoodsClass> goodClassList = this.goodsClassService.query(
                "select obj from GoodsClass obj where obj.disabled=:disabled and obj.level=:level",
                goodClassmap, -1, -1);

            for (GoodsClass goodsClass : goodClassList) {
                UserGoodsClass userGoodsClass = new UserGoodsClass();
                List<UserGoodsClass> userGoodsClassList = new ArrayList<UserGoodsClass>();
                userGoodsClass.setDisabled(false);
                userGoodsClass.setCreatetime(new Date());
                userGoodsClass.setLevel(goodsClass.getLevel());
                userGoodsClass.setUser(user);
                userGoodsClass.setClassName(goodsClass.getClassName());
                this.userGoodsClassService.save(userGoodsClass);
                //查询二级分类
                List<GoodsClass> goodClassList2 = goodsClass.getChilds();
                for (GoodsClass goodsClass2 : goodClassList2) {
                    UserGoodsClass userGoodsClass2 = new UserGoodsClass();
                    List<UserGoodsClass> userGoodsClassList2 = new ArrayList<UserGoodsClass>();
                    userGoodsClass2.setDisabled(false);
                    userGoodsClass2.setCreatetime(new Date());
                    userGoodsClass2.setLevel(goodsClass2.getLevel());
                    userGoodsClass2.setUser(user);
                    userGoodsClass2.setClassName(goodsClass2.getClassName());
                    userGoodsClass2.setParent(userGoodsClass);
                    userGoodsClassList.add(userGoodsClass2);
                    this.userGoodsClassService.save(userGoodsClass2);
                    //查询三级分类
                    Map goodClassmap3 = new HashMap();
                    List<GoodsClass> goodClassList3 = goodsClass2.getChilds();
                    for (GoodsClass goodsClass3 : goodClassList3) {
                        UserGoodsClass userGoodsClass3 = new UserGoodsClass();
                        userGoodsClass3.setDisabled(false);
                        userGoodsClass3.setCreatetime(new Date());
                        userGoodsClass3.setLevel(goodsClass3.getLevel());
                        userGoodsClass3.setUser(user);
                        userGoodsClass3.setClassName(goodsClass3.getClassName());
                        userGoodsClass3.setParent(userGoodsClass2);
                        userGoodsClassList2.add(userGoodsClass3);
                        this.userGoodsClassService.save(userGoodsClass3);
                    }
                    userGoodsClass2.setChilds(userGoodsClassList2);
                    this.userGoodsClassService.update(userGoodsClass2);
                }
                userGoodsClass.setChilds(userGoodsClassList);
                this.userGoodsClassService.update(userGoodsClass);
            }
        } else {
            Store store = user.getStore();
            if (store != null) {
                if (store.getStore_status() == -1) {
                    store.setStore_status(1);
                }

                WebForm wf = new WebForm();
                CompanyInfo obj = this.companyInfoService.getObjById(CommUtil.null2Long(companyId));
                CompanyInfo company = (CompanyInfo) wf.toPo(request, obj);
                Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
                company.setArea(area);
                this.companyInfoService.update(company);

                //用户调用接口
                String companyjson = JsonUtil.write2JsonStr(company);
                sendReqAsync.sendMessageUtil(Constant.COMPANY_INTEFACE_URL_EDIT, companyjson,
                    "编辑公司");
            }
        }

        mv.addObject("op_title", "已成功提交申请");
        mv.addObject("url", CommUtil.getURL(request) + "/buyer/buyerseller_apply.htm");
        return mv;
    }

    @SecurityMapping(title = "个人信息获取下级地区ajax", value = "/buyer/account_getAreaChilds.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/buyer/account_getAreaChilds.htm" })
    public ModelAndView account_getAreaChilds(HttpServletRequest request,
                                              HttpServletResponse response, String parent_id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/account_area_chlids.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("parent_id", CommUtil.null2Long(parent_id));
        List<Area> childs = this.areaService.query(
            "select obj from Area obj where obj.parent.id=:parent_id", map, -1, -1);
        if (childs.size() > 0) {
            mv.addObject("childs", childs);
        }
        return mv;
    }

    @SecurityMapping(title = "个人信息获取下级地区ajax", value = "/buyer/account_getAreaChilds.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/buyer/account_getAreaChilds.htm" })
    public ModelAndView account_buyer_getAreaChilds(HttpServletRequest request,
                                                    HttpServletResponse response, String parent_id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/account_area_chlids.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("parent_id", CommUtil.null2Long(parent_id));
        List<Area> childs = this.areaService.query(
            "select obj from Area obj where obj.parent.id=:parent_id", map, -1, -1);
        if (childs.size() > 0) {
            mv.addObject("childs", childs);
        }
        return mv;
    }

    @SecurityMapping(title = "个人信息保存", value = "/buyer/account_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/buyer/account_save.htm" })
    public ModelAndView account_save(HttpServletRequest request, HttpServletResponse response,
                                     String area_id, String birthday) {
        ModelAndView mv = new JModelAndView("buyer_success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        WebForm wf = new WebForm();
        User u = SecurityUserHolder.getCurrentUser();
        User user = (User) wf.toPo(request, u);
        if ((area_id != null) && (!area_id.equals(""))) {
            Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
            user.setArea(area);
        }
        if ((birthday != null) && (!birthday.equals(""))) {
            String[] y = birthday.split("-");
            Calendar calendar = new GregorianCalendar();
            int years = calendar.get(1) - CommUtil.null2Int(y[0]);
            user.setYears(years);
        }
        this.userService.update(user);

        //调用接口
        String write2JsonStr = JsonUtil.write2JsonStr(user);
        sendReqAsync.sendMessageUtil(Constant.USER_INTEFACE_URL_EDIT, write2JsonStr, "修改会员");

        mv.addObject("op_title", "个人信息修改成功");
        mv.addObject("url", CommUtil.getURL(request) + "/buyer/account.htm");
        return mv;
    }

    @SecurityMapping(title = "密码修改", value = "/buyer/account_password.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/buyer/account_password.htm" })
    public ModelAndView account_password(HttpServletRequest request, HttpServletResponse response,
                                         @PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/account_password.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("user",
            this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId()));
        mv.addObject("storeId", storeId);
        return mv;
    }

    @SecurityMapping(title = "密码修改保存", value = "/buyer/account_password_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/buyer/account_password_save.htm" })
    public void account_password_save(HttpServletRequest request, HttpServletResponse response,
                                      String old_password, String new_password,
                                      @PathVariable String storeId) throws Exception {

        boolean ret = true;
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        if (user.getPassword().equals(Md5Encrypt.md5(old_password).toLowerCase())) {
            user.setPassword(Md5Encrypt.md5(new_password).toLowerCase());
            this.userService.update(user);
            //            mv.addObject("op_title", "密码修改成功");
            send_sms(request, "sms_tobuyer_pws_modify_notify", storeId);
        } else {
            /*mv = new JModelAndView("buyer_error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "原始密码输入错误，修改失败");*/
            ret = false;
        }
        //        mv.addObject("url", CommUtil.getURL(request) + "/buyer/account_password.htm");
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

    @SecurityMapping(title = "邮箱修改", value = "/buyer/account_email.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/buyer/account_email.htm" })
    public ModelAndView account_email(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/account_email.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        return mv;
    }

    @SecurityMapping(title = "邮箱修改保存", value = "/buyer/account_email_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/buyer/account_email_save.htm" })
    public ModelAndView account_email_save(HttpServletRequest request,
                                           HttpServletResponse response, String password,
                                           String email) {
        ModelAndView mv = new JModelAndView("buyer/buyer_success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        if (user.getPassword().equals(Md5Encrypt.md5(password).toLowerCase())) {
            user.setEmail(email);
            this.userService.update(user);
            mv.addObject("op_title", "邮箱修改成功");
            mv.addObject("url", CommUtil.getURL(request) + "/buyer/account.htm");

            //调用接口
            String write2JsonStr = JsonUtil.write2JsonStr(user);
            sendReqAsync.sendMessageUtil(Constant.USER_INTEFACE_URL_EDIT, write2JsonStr, "修改会员");

        } else {
            mv = new JModelAndView("buyer_error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "密码输入错误，邮箱修改失败");
            mv.addObject("url", CommUtil.getURL(request) + "/buyer/account_password.htm");
        }
        // mv.addObject("url", CommUtil.getURL(request) +
        // "/buyer/account_email.htm");
        return mv;
    }

    @SecurityMapping(title = "图像修改", value = "/buyer/account_avatar.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/buyer/account_avatar.htm" })
    public ModelAndView account_avatar(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/account_avatar.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("user",
            this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId()));
        mv.addObject("url", CommUtil.getURL(request));
        return mv;
    }

    @SecurityMapping(title = "图像上传", value = "/buyer/upload_avatar.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/buyer/upload_avatar.htm" })
    public void upload_avatar(HttpServletRequest request, HttpServletResponse response)
                                                                                       throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0L);
        try {
            String filePath = request.getSession().getServletContext().getRealPath("")
                              + "/upload/avatar";
            File uploadDir = new File(filePath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            String customParams = CommUtil.null2String(request.getParameter("custom_params"));

            String imageType = CommUtil.null2String(request.getParameter("image_type"));
            if ("".equals(imageType)) {
                imageType = ".jpg";
            }
            String bigAvatarContent = CommUtil.null2String(request.getParameter("big_avatar"));
            User user = SecurityUserHolder.getCurrentUser();
            String bigAvatarName = SecurityUserHolder.getCurrentUser().getId() + "_big";

            saveImage(filePath, imageType, bigAvatarContent, bigAvatarName);
            Accessory photo = new Accessory();
            if (user.getPhoto() != null) {
                photo = user.getPhoto();
            } else {
                photo.setCreatetime(new Date());
                photo.setWidth(100);
                photo.setHeight(100);
            }
            photo.setName(bigAvatarName + imageType);
            photo.setExt(imageType);
            photo.setPath(this.configService.getSysConfig().getUploadFilePath() + "/avatar");
            if (user.getPhoto() == null) {
                this.accessoryService.save(photo);
            } else {
                this.accessoryService.update(photo);
            }
            user.setPhoto(photo);
            this.userService.update(user);

            response.setContentType("text/xml");

            response.getWriter().write("200");
        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("text/xml");

            response.getWriter().write("400");
        }
    }

    private void saveImage(String filePath, String imageType, String avatarContent,
                           String avatarName) throws IOException {
        avatarContent = CommUtil.null2String(avatarContent);
        if (!"".equals(avatarContent)) {
            if ("".equals(avatarName)) {
                avatarName = UUID.randomUUID().toString() + ".jpg";
            } else {
                avatarName = avatarName + imageType;
            }
            byte[] data = _decoder.decodeBuffer(avatarContent);
            File f = new File(filePath + File.separator + avatarName);
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(f));
            dos.write(data);
            dos.flush();
            dos.close();
        }
    }

    @SecurityMapping(title = "手机号码修改", value = "/buyer/account_mobile.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/buyer/account_mobile.htm" })
    public ModelAndView account_mobile(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/account_mobile.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("url", CommUtil.getURL(request));
        return mv;
    }

    @SecurityMapping(title = "手机号码保存", value = "/buyer/account_mobile_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/buyer/account_mobile_save.htm" })
    public void account_mobile_save(HttpServletRequest request, HttpServletResponse response,
                                    String mobile_verify_number, String mobile,
                                    @PathVariable String storeId) throws Exception {
        /* ModelAndView mv = new JModelAndView("buyer/buyer_success.html", this.configService.getSysConfig(),
             this.userConfigService.getUserConfig(), 1, request, response);*/
        boolean ret = true;
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        MobileVerifyCode mvc = this.mobileverifycodeService.getObjByProperty("mobile", mobile);
        if ((mvc != null) && (mvc.getCode().equalsIgnoreCase(mobile_verify_number))) {
            user.setMobile(mobile);
            user.setUserName(mobile);
            this.userService.update(user);
            this.mobileverifycodeService.delete(mvc.getId());
            //   mv.addObject("op_title", "手机绑定成功");

            send_sms(request, "sms_tobuyer_mobilebind_notify", storeId);

            //调用接口
            String write2JsonStr = JsonUtil.write2JsonStr(user);
            sendReqAsync.sendMessageUtil(Constant.USER_INTEFACE_URL_EDIT, write2JsonStr, "修改会员");
            //     mv.addObject("url", CommUtil.getURL(request) + "/buyer/account.htm");
        } else {
            /*   mv = new JModelAndView("buyer_error.html", this.configService.getSysConfig(),
                   this.userConfigService.getUserConfig(), 1, request, response);
               mv.addObject("op_title", "验证码错误，手机绑定失败");*/
            // mv.addObject("url", CommUtil.getURL(request) +
            // "/buyer/account_mobile.htm");
            //    mv.addObject("url", CommUtil.getURL(request) + "/buyer/account_password.htm");
            ret = false;
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
        // return mv;
    }

    @SecurityMapping(title = "手机短信发送", value = "/buyer/account_mobile_sms.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping(value = { "/buyer/account_mobile_sms.htm",
            "/store/{storeId}.htm/buyer/account_mobile_sms.htm" })
    public void account_mobile_sms(HttpServletRequest request, HttpServletResponse response,
                                   String type, String mobile, @PathVariable String storeId)
                                                                                            throws UnsupportedEncodingException {
        String ret = "100";
        if (type.equals("mobile_vetify_code")) {
            String code = CommUtil.randomInt(6).toUpperCase();

            com.javamalls.platform.domain.Template template = this.templateService
                .getObjByProperty("mark", "sms_tobuyer_mobilemodify_notify");
            if (this.configService.getSysConfig().isSmsEnbale()) {
                if ((template != null) && (template.isOpen())) {
                    Map<String, String> map = new HashMap<String, String>();
                    User currentUser = SecurityUserHolder.getCurrentUser();
                    String userName = currentUser.getUserName();
                    if (currentUser.getTrueName() != null && !"".equals(currentUser.getTrueName())) {
                        userName = currentUser.getTrueName();
                    }

                    map.put("code", code);
                    boolean ret1 = this.msgTools.sendSMS(mobile, template.getTitle(), map);
                    if (ret1) {
                        MobileVerifyCode mvc = this.mobileverifycodeService.getObjByProperty(
                            "mobile", mobile);
                        if (mvc == null) {
                            mvc = new MobileVerifyCode();
                        }
                        mvc.setCreatetime(new Date());
                        mvc.setCode(code);
                        mvc.setMobile(mobile);
                        this.mobileverifycodeService.update(mvc);
                    } else {
                        ret = "200";
                    }
                } else {
                    ret = "200";
                }
            } else {
                ret = "300";
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
    }

    @SecurityMapping(title = "好友管理", value = "/buyer/friend.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/buyer/friend.htm" })
    public ModelAndView account_friend(HttpServletRequest request, HttpServletResponse response,
                                       String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/account_friend.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        SnsFriendQueryObject qo = new SnsFriendQueryObject(currentPage, mv, "createtime", "desc");
        qo.addQuery("obj.fromUser.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser()
            .getId()), "=");
        IPageList pList = this.sndFriendService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        return mv;
    }

    @SecurityMapping(title = "好友添加", value = "/buyer/friend_add.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/buyer/friend_add.htm" })
    public ModelAndView friend_add(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/account_friend_search.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        List<Area> areas = this.areaService.query(
            "select obj from Area obj where obj.parent.id is null", null, -1, -1);
        mv.addObject("areas", areas);
        return mv;
    }

    @SecurityMapping(title = "搜索用户", value = "/buyer/account_friend_search.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/buyer/account_friend_search.htm" })
    public ModelAndView friend_search(HttpServletRequest request, HttpServletResponse response,
                                      String userName, String area_id, String sex, String years,
                                      String currentPage, @PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/account_friend_search.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        UserQueryObject qo = new UserQueryObject(currentPage, mv, "createtime", "desc");
        qo.addQuery("obj.userRole", new SysMap("userRole", "ADMIN"), "!=");
        if ((userName != null) && (!userName.equals(""))) {
            mv.addObject("userName", userName);
            qo.addQuery("obj.userName", new SysMap("userName", "%" + userName + "%"), "like");
        }
        if ((years != null) && (!years.equals(""))) {
            mv.addObject("years", years);
            if (years.equals("18")) {
                qo.addQuery("obj.years",
                    new SysMap("years", Integer.valueOf(CommUtil.null2Int(years))), "<=");
            }
            if (years.equals("50")) {
                qo.addQuery("obj.years",
                    new SysMap("years", Integer.valueOf(CommUtil.null2Int(years))), ">=");
            }
            if ((!years.equals("18")) && (!years.equals("50"))) {
                String[] y = years.split("~");
                qo.addQuery("obj.years",
                    new SysMap("years", Integer.valueOf(CommUtil.null2Int(y[0]))), ">=");
                qo.addQuery("obj.years",
                    new SysMap("years2", Integer.valueOf(CommUtil.null2Int(y[1]))), "<=");
            }
        }
        if ((sex != null) && (!sex.equals(""))) {
            mv.addObject("sex", sex);
            qo.addQuery("obj.sex", new SysMap("sex", Integer.valueOf(CommUtil.null2Int(sex))), "=");
        }
        if ((area_id != null) && (!area_id.equals(""))) {
            Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
            mv.addObject("area", area);
            qo.addQuery("obj.area.id", new SysMap("area_id", CommUtil.null2Long(area_id)), "=");
        }
        qo.setPageSize(Integer.valueOf(18));
        qo.addQuery("obj.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser().getId()),
            "!=");

        IPageList pList = this.userService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        List<Area> areas = this.areaService.query(
            "select obj from Area obj where obj.parent.id is null", null, -1, -1);
        mv.addObject("areas", areas);
        return mv;
    }

    @SecurityMapping(title = "好友添加", value = "/buyer/friend_add_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/buyer/friend_add_save.htm" })
    public void friend_add_save(HttpServletRequest request, HttpServletResponse response,
                                String user_id) {
        boolean flag = false;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", CommUtil.null2Long(user_id));
        params.put("uid", SecurityUserHolder.getCurrentUser().getId());
        List<SnsFriend> sfs = this.sndFriendService.query(
            "select obj from SnsFriend obj where obj.fromUser.id=:uid and obj.toUser.id=:user_id",
            params, -1, -1);
        if (sfs.size() == 0) {
            SnsFriend friend = new SnsFriend();
            friend.setCreatetime(new Date());
            friend.setFromUser(SecurityUserHolder.getCurrentUser());
            friend.setToUser(this.userService.getObjById(CommUtil.null2Long(user_id)));
            flag = this.sndFriendService.save(friend);
        }
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(flag);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SecurityMapping(title = "好友删除", value = "/buyer/friend_del.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/buyer/friend_del.htm" })
    public void friend_del(HttpServletRequest request, HttpServletResponse response, String id) {
        this.sndFriendService.delete(CommUtil.null2Long(id));
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SecurityMapping(title = "账号绑定", value = "/buyer/account_bind.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/buyer/account_bind.htm" })
    public ModelAndView account_bind(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/account_bind.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        mv.addObject("user", user);
        return mv;
    }

    @SecurityMapping(title = "账号解除绑定", value = "/buyer/account_bind_cancel.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/buyer/account_bind_cancel.htm" })
    public String account_bind_cancel(HttpServletRequest request, HttpServletResponse response,
                                      String account) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/account_bind.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        if (CommUtil.null2String(account).equals("qq")) {
            user.setQq_openid(null);
        }
        if (CommUtil.null2String(account).equals("sina")) {
            user.setSina_openid(null);
        }
        this.userService.update(user);
        return "redirect:account_bind.htm";
    }

    private void send_sms(HttpServletRequest request, String mark, String storeId) {
        try {
            com.javamalls.platform.domain.Template template = this.templateService
                .getObjByProperty("mark", mark);
            if ((template != null) && (template.isOpen())) {
                User user = this.userService
                    .getObjById(SecurityUserHolder.getCurrentUser().getId());
                String mobile = user.getMobile();
                if ((mobile != null) && (!mobile.equals(""))) {/*
                                                               String path = request.getSession().getServletContext().getRealPath("/")
                                                               + "/vm/";
                                                               PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
                                                               new FileOutputStream(path + "msg.vm", false), "UTF-8"));
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
                                                               context.put("user", user);
                                                               context.put("config", this.configService.getSysConfig());
                                                               context.put("send_time", CommUtil.formatLongDate(new Date()));
                                                               context.put("webPath", CommUtil.getURL(request));
                                                               StringWriter writer = new StringWriter();
                                                               blank.merge(context, writer);

                                                               String content = writer.toString();
                                                               this.msgTools.sendSMS(mobile, content);
                                                               */
                    Map<String, String> map = new HashMap<String, String>();
                    Store store = this.storeService.getObjById(CommUtil.null2Long(storeId));
                    String userName = user.getUserName();
                    if (user.getTrueName() != null && !"".equals(user.getTrueName())) {
                        userName = user.getTrueName();
                    }
                    map.put("buyerName", userName);
                    if (store != null) {
                        map.put("storeName", store.getStore_name());
                    }
                    map.put("user_mobile", user.getMobile());
                    this.msgTools.sendSMS(mobile, template.getTitle(), map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
