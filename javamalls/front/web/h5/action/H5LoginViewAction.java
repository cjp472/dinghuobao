package com.javamalls.front.web.h5.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.constant.Constant;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.Md5Encrypt;
import com.javamalls.ctrl.admin.tools.MsgTools;
import com.javamalls.front.web.tools.ImageViewTools;
import com.javamalls.payment.chinabank.h5.util.JsonUtil;
import com.javamalls.platform.domain.Album;
import com.javamalls.platform.domain.IntegralLog;
import com.javamalls.platform.domain.MobileVerifyCode;
import com.javamalls.platform.domain.Payment;
import com.javamalls.platform.domain.Role;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.UserStoreRelation;
import com.javamalls.platform.service.IAlbumService;
import com.javamalls.platform.service.IIntegralLogService;
import com.javamalls.platform.service.IMobileVerifyCodeService;
import com.javamalls.platform.service.IPaymentService;
import com.javamalls.platform.service.IRoleService;
import com.javamalls.platform.service.ISnsFriendService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.ITemplateService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;
import com.javamalls.platform.service.IUserStoreRelationService;
import com.utils.SendReqAsync;

/**登录    注册
 *                       
 * @Filename: LoginViewAction.java
 * @Version: 1.0
 * @Author: 刘杰
 * @Email: attay125@163.com
 *
 */
@Controller
public class H5LoginViewAction {
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
    private ITemplateService          templateService;
    @Autowired
    private IStoreService             storeService;
    @Autowired
    private MsgTools                  msgTools;
    @Autowired
    private IMobileVerifyCodeService  mobileverifycodeService;
    @Autowired
    private ISnsFriendService         sndFriendService;
    @Autowired
    private IUserStoreRelationService userStoreRelationService;

    @Autowired
    private SendReqAsync              sendReqAsync;
    @Autowired
    private IPaymentService			  paymentService;
    
    @RequestMapping({ "/store/{storeId}.htm/mobile/user/login.htm" })
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response, String url,
                              @PathVariable String storeId) throws UnsupportedEncodingException {
        ModelAndView mv = new JModelAndView("h5/login.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        request.getSession(false).removeAttribute("verify_number");
        boolean domain_error = CommUtil.null2Boolean(request.getSession(false).getAttribute(
            "domain_error"));
        if ((url != null) && (!url.equals(""))) {
            request.getSession(false).setAttribute("refererUrl",
                new String(url.getBytes(), "utf-8"));
        }
        if (domain_error) {
            mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
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
        mv.addObject("storeId", storeId);
        return mv;
    }

    //登录成功
    @RequestMapping({ "/store/{storeId}.htm/mobile/user_login_success.htm" })
    public ModelAndView user_login_success(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = CommUtil.getURL(request) + "/mobile/daohang.htm";

        String jm_view_type = CommUtil.null2String(request.getSession(false).getAttribute(
            Constent.WEB_TYPE_KEY));
        if ((jm_view_type != null) && (!jm_view_type.equals("")) && (jm_view_type.equals("weixin"))) {
            String store_id = CommUtil.null2String(request.getSession(false).getAttribute(
                "store_id"));
            mv = new JModelAndView("weixin/success.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            url = CommUtil.getURL(request) + "/weixin/index.htm?store_id=" + store_id;
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
        Payment payment =null;
        String wx_appId ="";
   	 	List<Payment> list = this.paymentService.query("" +
				" select obj from Payment obj where obj.disabled = false and obj.mark='weixin_wap' and obj.type='admin' ", null, -1, -1);
     	if(list!=null&&list.size()>0){
     		payment = list.get(0);
     		wx_appId = payment.getWeixin_appId();
     	}
     	 User user = SecurityUserHolder.getCurrentUser();
         mv.addObject("user", user);
     	mv.addObject("wx_appId", wx_appId);
     	if(user.getWx_openid()!=null&&!"".equals(user.getWx_openid())){
     		mv.addObject("areLogin",false);
     	}else{
     		mv.addObject("areLogin",true);
     	}
     	
        mv.addObject("url", url);
        return mv;
    }

    //注册
    @RequestMapping({ "/store/{storeId}.htm/mobile/register.htm" })
    public ModelAndView register(HttpServletRequest request, HttpServletResponse response,
                                 @PathVariable String storeId, String cus_ser_code) {
        ModelAndView mv = new JModelAndView("h5/register.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        request.getSession(false).removeAttribute("verify_number");
        mv.addObject("type", "0");
        mv.addObject("storeId", storeId);

        //客服代码，从session里获取
        cus_ser_code = (String) request.getSession().getAttribute(Constent.CUS_SER_CODE);
        mv.addObject("cus_ser_code", cus_ser_code);
        return mv;
    }

    //注册成功
    @RequestMapping({ "/store/{storeId}.htm/mobile/register_finish.htm" })
    public String buyer_register_finish(HttpServletRequest request, HttpServletResponse response,
                                        String userName, String password, String sms_code,
                                        @PathVariable String storeId) throws HttpException,
                                                                     IOException {
        boolean reg = true;

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
                "select obj from User obj where   obj.disabled=false and (obj.userName=:userName or obj.mobile=:mobile) ",
                params, 0, 1);
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
            //    user.setUserOfstore(Integer.valueOf(storeId));
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
            /*   try{
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
                   + password
                   + "&encode=true&is_reg=true&is_mobile=true&jm_view_type=mobile&jm_store_id="
                   + storeId;
        }
        return "redirect:" + CommUtil.getURL(request) + "/mobile/register.htm";
    }

    //注册成功，完善个人信息
    @RequestMapping({ "/store/{storeId}.htm/mobile/after_reg_login.htm" })
    public ModelAndView after_reg_login(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = CommUtil.getURL(request) + "/mobile/buyer/account_password.htm";

        mv.addObject("op_title", "注册成功，请完善个人信息");
        mv.addObject("url", url);
        return mv;
    }

    /**
     * 买家注册时短信发送
     * @param request
     * @param response
     * @param type
     * @param mobile
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = { "/store/{storeId}.htm/mobile/buyer/register_mobile_sms.htm" })
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
                    "select obj from User obj where obj.disabled=false and (obj.userName=:userName or obj.mobile=:mobile) ",
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

    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer_forget.htm" })
    public ModelAndView buyer_forget(HttpServletRequest request, HttpServletResponse response,
                                     @PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("h5/buyer_forget.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        request.getSession(false).removeAttribute("verify_number");
        mv.addObject("type", "0");
        mv.addObject("storeId", storeId);
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
    @RequestMapping(value = { "/store/{storeId}.htm/mobile/buyer/forget_mobile_sms.htm" })
    public void forget_mobile_sms(HttpServletRequest request, HttpServletResponse response,
                                  String type, String mobile, @PathVariable String storeId)
                                                                                           throws UnsupportedEncodingException {
        String ret = "100";
        if (type.equals("mobile_vetify_code")) {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("userName", mobile);
            param.put("mobile", mobile);
            List<User> users = this.userService
                .query(
                    "select obj from User obj where obj.disabled=false  and (obj.userName=:userName or obj.mobile=:mobile) ",
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

    //找回密码成功
    @RequestMapping({ "/store/{storeId}.htm/mobile/forget_finish.htm" })
    public ModelAndView buyer_forget_finish(HttpServletRequest request,
                                            HttpServletResponse response, String userName,
                                            String password, String sms_code,
                                            @PathVariable String storeId) throws HttpException,
                                                                         IOException {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = CommUtil.getURL(request) + "/mobile/user/login.htm";
        boolean reg = true;

        //短信验证码
        MobileVerifyCode mvc = this.mobileverifycodeService.getObjByProperty("mobile", userName);
        if ((mvc != null) && (mvc.getCode().equalsIgnoreCase(sms_code))) {
            reg = true;
        } else {
            reg = false;
        }

        if (reg) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("userName", userName);
            params.put("mobile", userName);
            List<User> users = this.userService
                .query(
                    "select obj from User obj where   obj.disabled=false and (obj.userName=:userName or obj.mobile=:mobile) ",
                    params, 0, 1);
            if ((users != null) && (users.size() > 0)) {
                User user = users.get(0);
                user.setPassword(Md5Encrypt.md5(password).toLowerCase());
                this.userService.update(user);

                mv.addObject("op_title", "密码重置成功");
                mv.addObject("url", url);
            }
        } else {
            mv.addObject("op_title", "密码重置失败");
            mv.addObject("url", url);
        }
        return mv;
    }

}
