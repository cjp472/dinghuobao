package com.javamalls.front.web.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.constant.Constant;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.Md5Encrypt;
import com.javamalls.base.tools.WebForm;
import com.javamalls.ctrl.admin.tools.MsgTools;
import com.javamalls.front.web.h5.action.Constent;
import com.javamalls.front.web.tools.ImageViewTools;
import com.javamalls.payment.chinabank.h5.util.JsonUtil;
import com.javamalls.platform.domain.Album;
import com.javamalls.platform.domain.Area;
import com.javamalls.platform.domain.CompanyInfo;
import com.javamalls.platform.domain.GoodsClass;
import com.javamalls.platform.domain.IntegralLog;
import com.javamalls.platform.domain.MobileVerifyCode;
import com.javamalls.platform.domain.Role;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.StoreGrade;
import com.javamalls.platform.domain.SysConfig;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.UserGoodsClass;
import com.javamalls.platform.domain.UserStoreRelation;
import com.javamalls.platform.domain.Warehouse;
import com.javamalls.platform.service.IAlbumService;
import com.javamalls.platform.service.IAreaService;
import com.javamalls.platform.service.ICompanyInfoService;
import com.javamalls.platform.service.IGoodsClassService;
import com.javamalls.platform.service.IIntegralLogService;
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
import com.javamalls.platform.service.IUserStoreRelationService;
import com.javamalls.platform.service.IWarehouseService;
import com.utils.SendReqAsync;

/**登录    注册
 *                       
 * @Filename: LoginViewAction.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class LoginViewAction {
    @Autowired
    private ISysConfigService         configService;
    @Autowired
    private IUserConfigService        userConfigService;
    @Autowired
    private IRoleService              roleService;
    @Autowired
    private IUserService              userService;
    @Autowired
    private IIntegralLogService       integralLogService;
    @Autowired
    private IAlbumService             albumService;
    @Autowired
    private ImageViewTools            imageViewTools;
    @Autowired
    private IStoreService             storeService;
    @Autowired
    private MsgTools                  msgTools;
    @Autowired
    private ITemplateService          templateService;
    @Autowired
    private IMobileVerifyCodeService  mobileverifycodeService;
    @Autowired
    private ISnsFriendService         sndFriendService;
    @Autowired
    private IAreaService              areaService;
    @Autowired
    private IStoreGradeService        storeGradeService;
    @Autowired
    private IWarehouseService         warehouseService;
    @Autowired
    private ICompanyInfoService       companyInfoService;
    @Autowired
    private IGoodsClassService        goodsClassService;
    @Autowired
    private IUserGoodsClassService    userGoodsClassService;
    @Autowired
    private IUserStoreRelationService userStoreRelationService;

    @Autowired
    private SendReqAsync              sendReqAsync;

    @RequestMapping({ "/user/login.htm" })
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response, String url)
                                                                                                   throws UnsupportedEncodingException {
        ModelAndView mv = new JModelAndView("login.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), JModelAndView.SHOP_PATH, request, response);
        request.getSession(false).removeAttribute("verify_number");
        boolean domain_error = CommUtil.null2Boolean(request.getSession(false).getAttribute(
            "domain_error"));
        if ((url != null) && (!url.equals(""))) {
            request.getSession(false).setAttribute("refererUrl",
                new String(url.getBytes(), "utf-8"));
        }
        if (domain_error) {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
        } else {
            mv.addObject("imageViewTools", this.imageViewTools);
        }
        mv.addObject("uc_logout_js", request.getSession(false).getAttribute("uc_logout_js"));
        String jm_view_type = CommUtil.null2String(request.getSession(false).getAttribute(
            Constent.WEB_TYPE_KEY));
        if ((jm_view_type != null) && (!jm_view_type.equals("")) && (jm_view_type.equals("weixin"))) {
            String store_id = CommUtil.null2String(request.getSession(false).getAttribute(
                "store_id"));
            mv = new JModelAndView("weixin/success.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "退出成功！");
            mv.addObject("url", CommUtil.getURL(request) + "/weixin/index.htm?store_id=" + store_id);
        }
        return mv;
    }

    /**
     * 
     * @param request
     * @param response
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping({ "/store/{storeId}.htm/user/login.htm" })
    public ModelAndView buyer_login(HttpServletRequest request, HttpServletResponse response,
                                    String url, @PathVariable String storeId)
                                                                             throws UnsupportedEncodingException {
        ModelAndView mv = new JModelAndView("buyer/buyer_login.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
            JModelAndView.SHOP_PATH, request, response);
        request.getSession(false).removeAttribute("verify_number");
        boolean domain_error = CommUtil.null2Boolean(request.getSession(false).getAttribute(
            "domain_error"));
        Store store = new Store();
        try {
            if (storeId != null && !"".equals(storeId)) {
                store = this.storeService.getObjById(Long.valueOf(storeId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mv.addObject("store", store);

        if ((url != null) && (!url.equals(""))) {
            request.getSession(false).setAttribute("refererUrl",
                new String(url.getBytes(), "utf-8"));
            mv.addObject("url", url);
        }
        if (domain_error) {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
        } else {
            mv.addObject("imageViewTools", this.imageViewTools);
        }
        mv.addObject("uc_logout_js", request.getSession(false).getAttribute("uc_logout_js"));
        return mv;
    }

    @RequestMapping({ "/register.htm" })
    public ModelAndView register(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("register.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        request.getSession(false).removeAttribute("verify_number");
        request.getSession(false).removeAttribute("register_user");
        return mv;
    }

    /**
     * 卖家注册时短信发送
     * @param request
     * @param response
     * @param type
     * @param mobile
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = { "/seller/register_mobile_sms.htm" })
    public void account_mobile_sms(HttpServletRequest request, HttpServletResponse response,
                                   String type, String mobile) throws UnsupportedEncodingException {
        String ret = "100";
        if (type.equals("mobile_vetify_code")) {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("userName", mobile);
            param.put("mobile", mobile);
            List<User> users = this.userService
                .query(
                    "select obj from User obj where obj.disabled=false  and (obj.userName=:userName or obj.mobile=:mobile)",
                    param, 0, 1);
            if (users != null && users.size() > 0) {
                ret = "350";
            } else {
                String code = CommUtil.randomInt(6).toUpperCase();
                com.javamalls.platform.domain.Template template = this.templateService
                    .getObjByProperty("mark", "sms_toseller_register_notify");
                if (this.configService.getSysConfig().isSmsEnbale()) {
                    if ((template != null) && (template.isOpen())) {
                        Map<String, String> map = new HashMap<String, String>();

                        map.put("code", code);
                        //map.put("product", configService.getSysConfig().getTitle());
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

    /**
     * 买家注册时短信发送
     * @param request
     * @param response
     * @param type
     * @param mobile
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = { "/store/{storeId}.htm/buyer/register_mobile_sms.htm" })
    public void account_buyer_mobile_sms(HttpServletRequest request, HttpServletResponse response,
                                         String type, String mobile, @PathVariable String storeId)
                                                                                                  throws UnsupportedEncodingException {
        String ret = "100";
        if (type.equals("mobile_vetify_code")) {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("userName", mobile);
            param.put("mobile", mobile);
            List<User> users = this.userService
                .query(
                    "select obj from User obj where obj.disabled=false  and (obj.userName=:userName or obj.mobile=:mobile)",
                    param, 0, 1);
            if (users != null && users.size() > 0) {
                ret = "350";
            } else {
                String code = CommUtil.randomInt(6).toUpperCase();
                com.javamalls.platform.domain.Template template = this.templateService
                    .getObjByProperty("mark", "sms_toseller_register_notify");
                if (this.configService.getSysConfig().isSmsEnbale()) {
                    if ((template != null) && (template.isOpen())) {
                        Map<String, String> map = new HashMap<String, String>();

                        map.put("code", code);
                        Store store = this.storeService.getObjById(CommUtil.null2Long(storeId));
                        //map.put("product", store.getStore_name());
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

    /**
     * 买家注册
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/store/{storeId}.htm/buyer_register.htm" })
    public ModelAndView buyer_register(HttpServletRequest request, HttpServletResponse response,
                                       @PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("buyer/buyer_register.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        Store store = this.storeService.getObjById(CommUtil.null2Long(storeId));
        mv.addObject("store", store);
        request.getSession(false).removeAttribute("verify_number");
        return mv;
    }

    /**
     * 卖家注册第一步
     * @param request
     * @param response
     * @param userName
     * @param password
     * @param email
     * @param code
     * @return
     * @throws HttpException
     * @throws IOException
     */
    @RequestMapping({ "/register_second.htm" })
    public String register_second(HttpServletRequest request, HttpServletResponse response,
                                  String userName, String password, String code, String sms_code,
                                  Map<String, String> dataMap) throws HttpException, IOException {
        boolean reg = true;

        User user2 = (User) request.getSession(false).getAttribute("register_user");
        //直接访问时session中有user则直接跳转
        if (user2 != null) {
            List<Area> areas = this.areaService.query(
                "select obj from Area obj where obj.parent.id is null", null, -1, -1);
            String areaJson = JsonUtil.write2JsonStr(areas);

            dataMap.put("areaJson", areaJson);
            return "WEB-INF/templates/zh_cn/shop/register_second.html";
        }

        if (userName == null || "".equals(userName.trim())) {
            return "redirect:register.htm";
        }

        if (password == null || "".equals(password.trim())) {
            return "redirect:register.htm";
        }
        if (code == null || "".equals(code.trim())) {
            return "redirect:register.htm";
        }
        if (sms_code == null || "".equals(sms_code.trim())) {
            return "redirect:register.htm";
        }

        if ((code != null) && (!code.equals(""))) {
            code = CommUtil.filterHTML(code);
        }
        //图像验证码
        if (this.configService.getSysConfig().isSecurityCodeRegister()) {
            if (!request.getSession(false).getAttribute("verify_number").equals(code)) {
                reg = false;
            }
        }

        //短信验证码
        MobileVerifyCode mvc = this.mobileverifycodeService.getObjByProperty("mobile", userName);
        if ((mvc != null) && (mvc.getCode().equalsIgnoreCase(sms_code))) {
            reg = true;
        } else {
            reg = false;
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userName", userName);
        params.put("mobile", userName);
        List<User> users = this.userService
            .query(
                "select obj from User obj where obj.disabled=false and obj.userRole='BUYER_SELLER' and (obj.userName=:userName or obj.mobile=:mobile) ",
                params, -1, -1);
        if ((users != null) && (users.size() > 0)) {
            reg = false;
        }

        if (reg) {
            User user = new User();
            user.setUserName(userName);
            user.setUserRole("BUYER");
            user.setCreatetime(new Date());
            user.setMobile(userName);
            user.setPassword(password);
            user.setCus_ser_code(request.getParameter("cus_ser_code"));
            request.getSession().setAttribute("register_user", user);//将注册用户信息放到session中
            //清除验证码
            this.mobileverifycodeService.delete(mvc.getId());

            List<Area> areas = this.areaService.query(
                "select obj from Area obj where obj.parent.id is null", null, -1, -1);
            String areaJson = JsonUtil.write2JsonStr(areas);

            dataMap.put("areaJson", areaJson);

            return "WEB-INF/templates/zh_cn/shop/register_second.html";
        } else {//不符合条件重新注册
            request.getSession(false).removeAttribute("verify_number");
            request.getSession(false).removeAttribute("register_user");
            //清除验证码
            this.mobileverifycodeService.delete(mvc.getId());
            return "redirect:register.htm";

        }

    }

    //供货商注册
    @SuppressWarnings("deprecation")
    @RequestMapping({ "/register_finish.htm" })
    @Transactional
    public String register_finish(HttpServletRequest request, HttpServletResponse response,
                                  String area_id) throws HttpException, IOException {
        boolean reg = true;
        User user = (User) request.getSession(false).getAttribute("register_user");
        if (user == null) {
            reg = false;
        }
        if (reg) {
            String password = user.getPassword();
            // Map<String, Object> params = new HashMap<String, Object>();
            //保存用户信息
            user.setPassword(Md5Encrypt.md5(password).toLowerCase());
            /* params.put("type", "BUYER");
             List<Role> roles = this.roleService.query(
                 "select obj from Role obj where obj.type=:type", params, -1, -1);
             user.getRoles().addAll(roles);*/
            this.userService.save(user);

            Album album = new Album();
            album.setCreatetime(new Date());
            album.setAlbum_default(true);
            album.setAlbum_name("默认相册");
            album.setAlbum_sequence(-10000);
            album.setUser(user);
            this.albumService.save(album);

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
            ;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, 10);
            store.setValidity(calendar.getTime());
            this.storeService.save(store);

            user.setStore(store);

            /* if (user.getUserRole().equals("BUYER")) {
                 user.setUserRole("BUYER_SELLER");
             }
             if (user.getUserRole().equals("ADMIN")) {
                 user.setUserRole("ADMIN_BUYER_SELLER");
             }*/
            user.setUserRole("SELLER");

            Map storeparams = new HashMap();
            storeparams.put("type", "SELLER");
            List<Role> storeroles = this.roleService.query(
                "select obj from Role obj where obj.type=:type", storeparams, -1, -1);
            user.getRoles().addAll(storeroles);
            this.userService.update(user);

            //用户调用接口
            String userjson = JsonUtil.write2JsonStr(user);
            sendReqAsync.sendMessageUtil(Constant.USER_INTEFACE_URL_ADD, userjson, "新增会员");

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

            request.getSession(false).removeAttribute("verify_number");
            request.getSession(false).removeAttribute("register_user");
            return "redirect:jm_login.htm?username=" + CommUtil.encode(user.getUsername())
                   + "&password=" + password + "&encode=true&is_reg=true";
        }
        return "redirect:register.htm";
    }

    /**
     * 买家注册
     * @param request
     * @param response
     * @param userName
     * @param password
     * @param sms_code
     * @param code
     * @param storeId
     * @return
     * @throws HttpException
     * @throws IOException
     */
    @RequestMapping({ "/store/{storeId}.htm/buyer_register_finish.htm" })
    public String buyer_register_finish(HttpServletRequest request, HttpServletResponse response,
                                        String userName, String password, String sms_code,
                                        String code, @PathVariable String storeId)
                                                                                  throws HttpException,
                                                                                  IOException {
        boolean reg = true;
        if ((code != null) && (!code.equals(""))) {
            code = CommUtil.filterHTML(code);
        }
        //图像验证码
        if (this.configService.getSysConfig().isSecurityCodeRegister()) {
            if (!request.getSession(false).getAttribute("verify_number").equals(code)) {
                reg = false;
            }
        }

        //短信验证码
        MobileVerifyCode mvc = this.mobileverifycodeService.getObjByProperty("mobile", userName);
        if ((mvc != null) && (mvc.getCode().equalsIgnoreCase(sms_code))) {
            reg = true;
        } else {
            reg = false;
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userName", userName);
        params.put("mobile", userName);

        List<User> users = this.userService
            .query(
                "select obj from User obj where   obj.disabled=false  and (obj.userName=:userName or obj.mobile=:mobile) ",
                params, -1, -1);
        if ((users != null) && (users.size() > 0)) {
            reg = false;
        }
        if (reg) {
            User user = new User();
            user.setUserName(userName);
            user.setUserRole("BUYER");
            user.setCreatetime(new Date());
            user.setMobile(userName);
            user.setPassword(Md5Encrypt.md5(password).toLowerCase());
            user.setCus_ser_code(request.getParameter("cus_ser_code"));
            //     user.setUserOfstore(Integer.valueOf(storeId));
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
            /*  try{
              	 SnsFriend friend = new SnsFriend();
                   friend.setCreatetime(new Date());
                   friend.setFromUser(user);
                   friend.setToUser(this.storeService.getObjById(Long.valueOf(storeId)).getUser());
                   this.sndFriendService.save(friend);
              }catch(Exception e){
              	e.printStackTrace();
              }*/

            Store platStore = this.storeService.getObjByProperty("platform", true);//自营店铺
            //和自营店铺B0建立供采关系
            UserStoreRelation userStoreRelation = new UserStoreRelation();
            userStoreRelation.setUser(user);
            userStoreRelation.setStore(platStore);
            userStoreRelation.setStatus(2); //默认审核通过
            userStoreRelation.setCreatetime(new Date());
            userStoreRelationService.save(userStoreRelation);

            //调用用户接口
            String write2JsonStr = JsonUtil.write2JsonStr(user);
            sendReqAsync.sendMessageUtil(Constant.USER_INTEFACE_URL_ADD, write2JsonStr, "新增会员");

            return "redirect:jm_login.htm?username=" + CommUtil.encode(userName) + "&password="
                   + password + "&encode=true&is_reg=true&jm_store_id=" + storeId;
        }
        return "redirect:buyer_register.htm";
    }

    /**
     * 卖家登录后进入卖家首页
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/user_login_success.htm" })
    public ModelAndView user_login_success(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        String url = CommUtil.getURL(request) + "/seller/index.htm";
        String jm_view_type = CommUtil.null2String(request.getSession(false).getAttribute(
            Constent.WEB_TYPE_KEY));
        if ((jm_view_type != null) && (!jm_view_type.equals("")) && (jm_view_type.equals("weixin"))) {
            String store_id = CommUtil.null2String(request.getSession(false).getAttribute(
                "store_id"));
            mv = new JModelAndView("weixin/success.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            url = CommUtil.getURL(request) + "/weixin/index.htm?store_id=" + store_id;
        }
        //手机h5登录
        if ((jm_view_type != null) && (!jm_view_type.equals("")) && (jm_view_type.equals("mobile"))) {
            mv = new JModelAndView("h5/success.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            url = CommUtil.getURL(request) + "/mobile/index.htm";
        }
        HttpSession session = request.getSession(false);
        if ((session.getAttribute("refererUrl") != null)
            && (!session.getAttribute("refererUrl").equals(""))) {
            url = (String) session.getAttribute("refererUrl");
            session.removeAttribute("refererUrl");
        }
        if (this.configService.getSysConfig().isUc_bbs()) {
            String uc_login_js = CommUtil.null2String(request.getSession(false).getAttribute(
                "uc_login_js"));
            mv.addObject("uc_login_js", uc_login_js);
        }
        String bind = CommUtil.null2String(request.getSession(false).getAttribute("bind"));
        if (!bind.equals("")) {
            mv = new JModelAndView(bind + "_login_bind.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            User user = SecurityUserHolder.getCurrentUser();
            mv.addObject("user", user);
            request.getSession(false).removeAttribute("bind");
        }
        mv.addObject("op_title", "登录成功");
        mv.addObject("url", url);
        return mv;
    }

    /**
     * 买家登录后进入买家首页
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/store/{storeId}.htm/user_login_success.htm" })
    public ModelAndView user_login_success(HttpServletRequest request,
                                           HttpServletResponse response,
                                           @PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("buyer/buyer_success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        String url = CommUtil.getURL(request) + "/buyer/index.htm";

        String jm_view_type = CommUtil.null2String(request.getSession(false).getAttribute(
            Constent.WEB_TYPE_KEY));
        if ((jm_view_type != null) && (!jm_view_type.equals("")) && (jm_view_type.equals("weixin"))) {
            String store_id = CommUtil.null2String(request.getSession(false).getAttribute(
                "store_id"));
            mv = new JModelAndView("weixin/success.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            url = CommUtil.getURL(request) + "/weixin/index.htm?store_id=" + store_id;
        }
        //手机h5登录
        if ((jm_view_type != null) && (!jm_view_type.equals("")) && (jm_view_type.equals("mobile"))) {
            mv = new JModelAndView("h5/success.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            url = CommUtil.getURL(request) + "/mobile/index.htm";
        }
        HttpSession session = request.getSession(false);
        if ((session.getAttribute("refererUrl") != null)
            && (!session.getAttribute("refererUrl").equals(""))) {
            String url1 = (String) session.getAttribute("refererUrl");
            //if (!url1.contains("/store/")) {
            url = url1;
            //}
            session.removeAttribute("refererUrl");
        }
        if (this.configService.getSysConfig().isUc_bbs()) {
            String uc_login_js = CommUtil.null2String(request.getSession(false).getAttribute(
                "uc_login_js"));
            mv.addObject("uc_login_js", uc_login_js);
        }
        String bind = CommUtil.null2String(request.getSession(false).getAttribute("bind"));
        if (!bind.equals("")) {
            mv = new JModelAndView(bind + "_login_bind.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            User user = SecurityUserHolder.getCurrentUser();
            mv.addObject("user", user);
            request.getSession(false).removeAttribute("bind");
        }
        mv.addObject("op_title", "登录成功");
        mv.addObject("url", url);

        return mv;
    }

    @RequestMapping({ "/after_reg_login.htm" })
    public ModelAndView after_reg_login(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        String url = CommUtil.getURL(request) + "/seller/index.htm";

        mv.addObject("op_title", "注册成功，请完善个人信息");
        mv.addObject("url", url);
        return mv;
    }

    @RequestMapping({ "/store/{storeId}.htm/after_reg_login.htm" })
    public ModelAndView after_reg_login(HttpServletRequest request, HttpServletResponse response,
                                        @PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("/buyer/buyer_success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        String url = CommUtil.getURL(request) + "/buyer/account.htm";

        mv.addObject("op_title", "注册成功，请完善个人信息");
        mv.addObject("url", url);
        return mv;
    }

    @RequestMapping({ "/user_dialog_login.htm" })
    public ModelAndView user_dialog_login(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user_dialog_login.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        return mv;
    }

    /**
     * 买家找回密码时短信发送
     * @param request
     * @param response
     * @param type
     * @param mobile
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = { "/store/{storeId}.htm/buyer/forget_mobile_sms.htm" })
    public void forget_sms(HttpServletRequest request, HttpServletResponse response, String type,
                           String mobile, @PathVariable String storeId)
                                                                       throws UnsupportedEncodingException {
        String ret = "100";
        if (type.equals("mobile_vetify_code")) {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("userName", mobile);
            param.put("mobile", mobile);
            List<User> users = this.userService
                .query(
                    "select obj from User obj where obj.disabled=false and (obj.userRole='BUYER' or  obj.userRole='BUYER_SELLER') and (obj.userName=:userName or obj.mobile=:mobile) ",
                    param, 0, 1);
            if (users == null || users.size() == 0) {//该账号不存在
                ret = "350";
            } else {
                String code = CommUtil.randomInt(6).toUpperCase();
                com.javamalls.platform.domain.Template template = this.templateService
                    .getObjByProperty("mark", "sms_forget_receive_ok_notify");
                if (this.configService.getSysConfig().isSmsEnbale()) {
                    if ((template != null) && (template.isOpen())) {
                        Map<String, String> map = new HashMap<String, String>();

                        map.put("code", code);
                        Store store = this.storeService.getObjById(CommUtil.null2Long(storeId));
                        //map.put("product", store.getStore_name());
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

    /**
     * 卖找回密码时短信发送
     * @param request
     * @param response
     * @param type
     * @param mobile
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = { "/seller/forget_mobile_sms.htm" })
    public void forget_seller_sms(HttpServletRequest request, HttpServletResponse response,
                                  String type, String mobile) throws UnsupportedEncodingException {
        String ret = "100";
        if (type.equals("mobile_vetify_code")) {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("userName", mobile);
            param.put("mobile", mobile);
            Long count = this.userService
                .queryCount(
                    "select count(obj) from User obj where obj.disabled=false and (obj.userRole='BUYER_SELLER' or obj.userRole='SELLER') and (obj.userName=:userName or obj.mobile=:mobile)",
                    param);
            if (count == null || count == 0) {//该账号不存在
                ret = "350";
            } else {
                String code = CommUtil.randomInt(6).toUpperCase();
                com.javamalls.platform.domain.Template template = this.templateService
                    .getObjByProperty("mark", "sms_forget_receive_ok_notify");
                SysConfig sysConfig = this.configService.getSysConfig();
                if (sysConfig.isSmsEnbale()) {
                    if ((template != null) && (template.isOpen())) {
                        Map<String, String> map = new HashMap<String, String>();

                        map.put("code", code);
                        //map.put("product", sysConfig.getTitle());
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

    @RequestMapping(value = { "/userJson.htm" })
    public void forget_seller_sms(HttpServletRequest request, HttpServletResponse response) {
        /*	Store store2 = this.storeService.getObjById(Long.parseLong("33"));
        	Store store = new Store();
        	 Area area=new Area();
             area.setAreaName("北京");
             area.setId(Long.parseLong("10000"));
             area.setDisabled(false);
        	store.setArea(area);
        	  User user = new User();
              user.setUserName("18233581234");
              user.setUserRole("BUYER");
              user.setCreatetime(new Date());
              user.setMobile("18233581234");
              user.setPassword(Md5Encrypt.md5("123456").toLowerCase());
              user.setUserOfstore(Integer.valueOf(2));
              store.setUser(user);
          
          
              store.setTemplate("default");
              store.setCreatetime(new Date());
          
              store.setDelivery_begin_time(new Date());
              store.setDelivery_end_time(new Date(2114, 11, 29));
              store.setCombin_begin_time(new Date());
              store.setCombin_end_time(new Date(2114, 11, 29));
              store.setStore_status(CommUtil.null2Int(1));//待审核
                  
              	store.setValiditybegin(new Date());;
                  Calendar calendar=Calendar.getInstance();
                  calendar.setTime(new Date());
                  calendar.add(Calendar.DAY_OF_MONTH, 10);
                  store.setValidity(calendar.getTime());*/

        /*     
            
               
              //创建默认仓库
              try {
        			Warehouse warehouse=new Warehouse();
        			warehouse.setCreatetime(new Date());
        			warehouse.setDisabled(false);
        			warehouse.setManager(user.getTrueName());
        			warehouse.setName("默认仓");
        			warehouse.setStatus(1);
        			warehouse.setId(Long.parseLong("1"));
        		} catch (Exception e) {
        			e.printStackTrace();
        		}
        store.setcom*/
        CompanyInfo company = new CompanyInfo();
        Area area = new Area();
        area.setAreaName("北京");
        area.setId(Long.parseLong("10000"));
        area.setDisabled(false);
        CompanyInfo objById = this.companyInfoService.getObjById(Long.parseLong("5"));
        objById.setArea(area);
        User user = new User();
        user.setUserName("18233581234");
        user.setUserRole("BUYER");
        user.setCreatetime(new Date());
        user.setMobile("18233581234");
        user.setPassword(Md5Encrypt.md5("123456").toLowerCase());
        //     user.setUserOfstore(Integer.valueOf(2));
        objById.setUser(user);
        System.out.println(JsonUtil.write2JsonStr(objById));
    }

    @RequestMapping({ "/seller/testJson.htm" })
    public void testJson(HttpServletRequest request, HttpServletResponse response, String parent_id)
                                                                                                    throws IOException {
        // 读取请求内容
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),
            "UTF-8"));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        // 将资料解码
        String reqBody = sb.toString();
        System.out.println("响应：" + reqBody);
        String str = URLDecoder.decode(reqBody, "UTF-8");
        System.out.println("=====" + str + "===");
    }
}
