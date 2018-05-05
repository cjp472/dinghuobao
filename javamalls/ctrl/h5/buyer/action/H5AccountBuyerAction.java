package com.javamalls.ctrl.h5.buyer.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.constant.Constant;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.Md5Encrypt;
import com.javamalls.base.tools.WebForm;
import com.javamalls.ctrl.admin.tools.MsgTools;
import com.javamalls.payment.chinabank.h5.util.JsonUtil;
import com.javamalls.platform.domain.Area;
import com.javamalls.platform.domain.GoodsBrand;
import com.javamalls.platform.domain.MobileVerifyCode;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.service.IAreaService;
import com.javamalls.platform.service.IGoodsBrandService;
import com.javamalls.platform.service.IMobileVerifyCodeService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.ITemplateService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;
import com.utils.SendReqAsync;

/**
 * 买家中心 个人信息修改 手机、邮箱修改等
 * 
 * @Filename: AccountBuyerAction.java
 * @Version: 1.0
 * @Author: 刘杰
 * @Email: attay125@163.com
 * 
 */
@Controller
public class H5AccountBuyerAction {
    @Autowired
    private ISysConfigService        configService;
    @Autowired
    private IUserConfigService       userConfigService;
    @Autowired
    private IUserService             userService;
    @Autowired
    private IMobileVerifyCodeService mobileverifycodeService;

    @Autowired
    private ITemplateService         templateService;
    @Autowired
    private IAreaService             areaService;
    @Autowired
    private IGoodsBrandService       goodsBrandService;
    @Autowired
    private IStoreService            storeService;
    @Autowired
    private SendReqAsync             sendReqAsync;

    @Autowired
    private MsgTools                 msgTools;
    public static final String       OPERATE_RESULT_CODE_SUCCESS = "200";
    public static final String       OPERATE_RESULT_CODE_FAIL    = "400";

    @SecurityMapping(title = "密码修改", value = "/mobile/buyer/account_password.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/account_password.htm" })
    public ModelAndView account_password(HttpServletRequest request, HttpServletResponse response,
                                         @PathVariable String storeId) {
        User user = SecurityUserHolder.getCurrentUser();
        ModelAndView mv = null;
        if (user != null) {
            mv = new JModelAndView("user/default/usercenter/h5/account_password.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0,
                request, response);
            mv.addObject("user",
                this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId()));
        } else {
            String targetUrl = CommUtil.getURL(request) + "/mobile/buyer/account_password.htm";
            mv = new JModelAndView("h5/login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("url", targetUrl);
            mv.addObject("storeId", storeId);
        }
        return mv;
    }

    @SecurityMapping(title = "密码修改保存", value = "/mobile/buyer/account_password_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/account_password_save.htm" })
    public ModelAndView account_password_save(HttpServletRequest request,
                                              HttpServletResponse response, String old_password,
                                              String new_password, @PathVariable String storeId)
                                                                                                throws Exception {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        //WebForm wf = new WebForm();

        if (SecurityUserHolder.getCurrentUser() != null) {
            User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
            if (user.getPassword().equals(Md5Encrypt.md5(old_password).toLowerCase())) {
                user.setPassword(Md5Encrypt.md5(new_password).toLowerCase());
                this.userService.update(user);
                mv.addObject("op_title", "密码修改成功，请重新登录");
                send_sms(request, "sms_tobuyer_pws_modify_notify", storeId);
                mv.addObject("url", CommUtil.getURL(request) + "/jm_logout.htm?jm_store_id="
                                    + storeId);
            } else {
                mv = new JModelAndView("user/default/usercenter/h5/error.html",
                    this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0,
                    request, response);
                mv.addObject("op_title", "原始密码输入错误，修改失败");
                mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/account_password.htm");
            }
        } else {
            mv = new JModelAndView("h5/login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("storeId", storeId);
        }

        return mv;
    }

    @SecurityMapping(title = "邮箱修改", value = "/mobile/buyer/account_email.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/account_email.htm" })
    public ModelAndView account_email(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/account_email.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String backState = request.getParameter("backState");
        mv.addObject("backState", backState);
        return mv;
    }

    @SecurityMapping(title = "邮箱修改保存", value = "/mobile/buyer/account_email_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/account_email_save.htm" })
    public ModelAndView account_email_save(HttpServletRequest request,
                                           HttpServletResponse response, String password,
                                           String email, @PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String backState = request.getParameter("backState");

        if (SecurityUserHolder.getCurrentUser() != null) {
            User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
            if (user.getPassword().equals(Md5Encrypt.md5(password).toLowerCase())) {
                user.setEmail(email);
                this.userService.update(user);
                //调用接口
                String write2JsonStr = JsonUtil.write2JsonStr(user);
                sendReqAsync
                    .sendMessageUtil(Constant.USER_INTEFACE_URL_EDIT, write2JsonStr, "修改会员");

                mv.addObject("op_title", "邮箱修改成功");
                mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/account_password.htm");
            } else {
                mv = new JModelAndView("user/default/usercenter/h5/error.html",
                    this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0,
                    request, response);
                mv.addObject("op_title", "密码输入错误，邮箱修改失败");
                mv.addObject("url", CommUtil.getURL(request)
                                    + "/mobile/buyer/account_email.htm?backState=" + backState);
            }
        } else {
            mv = new JModelAndView("h5/login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("storeId", storeId);
        }
        return mv;
    }

    @SecurityMapping(title = "手机号码修改", value = "/mobile/buyer/account_phone.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/account_phone.htm" })
    public ModelAndView account_mobile(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("/user/default/usercenter/h5/account_phone.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String backState = request.getParameter("backState");
        mv.addObject("backState", backState);
        mv.addObject("url", CommUtil.getURL(request));
        return mv;
    }

    @SecurityMapping(title = "手机号码保存", value = "/mobile/buyer/account_mobile_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/account_mobile_save.htm" })
    public ModelAndView account_mobile_save(HttpServletRequest request,
                                            HttpServletResponse response,
                                            String mobile_verify_number, String mobile,
                                            @PathVariable String storeId) throws Exception {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String backState = request.getParameter("backState");

        MobileVerifyCode mvc = this.mobileverifycodeService.getObjByProperty("mobile", mobile);
        if (SecurityUserHolder.getCurrentUser() != null) {
            User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
            if ((mvc != null) && (mvc.getCode().equalsIgnoreCase(mobile_verify_number))) {
                user.setMobile(mobile);
                user.setUserName(mobile);
                this.userService.update(user);
                this.mobileverifycodeService.delete(mvc.getId());
                mv.addObject("op_title", "手机绑定成功");

                send_sms(request, "sms_tobuyer_mobilebind_notify", storeId);

                //调用接口
                String write2JsonStr = JsonUtil.write2JsonStr(user);
                sendReqAsync
                    .sendMessageUtil(Constant.USER_INTEFACE_URL_EDIT, write2JsonStr, "修改会员");

                mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/account_password.htm");
            } else {
                mv = new JModelAndView("user/default/usercenter/h5/error.html",
                    this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0,
                    request, response);
                mv.addObject("op_title", "验证码错误，手机绑定失败");
                mv.addObject("url", CommUtil.getURL(request)
                                    + "/mobile/buyer/account_phone.htm?backState=" + backState);
            }
        } else {
            mv = new JModelAndView("h5/login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("storeId", storeId);
        }
        return mv;
    }

    @SecurityMapping(title = "手机短信发送", value = "/mobile/buyer/account_mobile_sms.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/account_mobile_sms.htm" })
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

    private void send_sms(HttpServletRequest request, String mark) {
        try {
            com.javamalls.platform.domain.Template template = this.templateService
                .getObjByProperty("mark", mark);
            if ((template != null) && (template.isOpen())) {
                User user = this.userService
                    .getObjById(SecurityUserHolder.getCurrentUser().getId());
                String mobile = user.getMobile();
                if ((mobile != null) && (!mobile.equals(""))) {
                    /*  String path = request.getSession().getServletContext().getRealPath("/")
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
                      this.msgTools.sendSMS(mobile, content);*/
                    Map<String, String> map = new HashMap<String, String>();
                    String userName = user.getUsername();
                    if (user.getTrueName() != null && !"".equals(user.getTrueName())) {
                        userName = user.getTrueName();
                    }
                    map.put("buyerName", userName);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
                    map.put("send_time", sdf.format(new Date()));
                    map.put("storeName", "平台");
                    this.msgTools.sendSMS(mobile, template.getTitle(), map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SecurityMapping(title = "个人信息", value = "/mobile/buyer/account.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/account.htm" })
    public ModelAndView account(HttpServletRequest request, HttpServletResponse response,
                                @PathVariable String storeId) {
        User user = SecurityUserHolder.getCurrentUser();
        ModelAndView mv = null;
        if (user != null) {
            mv = new JModelAndView("user/default/usercenter/h5/account.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0,
                request, response);
            mv.addObject("user", this.userService.getObjById(user.getId()));

            // 加载品牌
            List<GoodsBrand> gbs = this.goodsBrandService
                .query(
                    "select obj from GoodsBrand obj where btype=1 and audit=1 order by obj.sequence asc",
                    null, -1, -1);
            mv.addObject("gbs", gbs);

            List<Area> areas = this.areaService.query(
                "select obj from Area obj where obj.parent.id is null", null, -1, -1);
            mv.addObject("areas", areas);
        } else {
            String targetUrl = CommUtil.getURL(request) + "/mobile/buyer/account.htm";
            mv = new JModelAndView("h5/login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("url", targetUrl);
            mv.addObject("storeId", storeId);
        }
        return mv;
    }

    @SecurityMapping(title = "个人信息获取下级地区ajax", value = "/mobile/buyer/account_getAreaChilds.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/account_getAreaChilds.htm" })
    public ModelAndView account_getAreaChilds(HttpServletRequest request,
                                              HttpServletResponse response, String parent_id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/account_area_chlids.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("parent_id", CommUtil.null2Long(parent_id));
        List<Area> childs = this.areaService.query(
            "select obj from Area obj where obj.parent.id=:parent_id", map, -1, -1);
        if (childs.size() > 0) {
            mv.addObject("childs", childs);
        }
        String areaMark = request.getParameter("areaMark");
        if (areaMark != null) {
            if ("province".equals(areaMark)) {
                areaMark = "city";
            } else if ("province".equals(areaMark)) {
                areaMark = "area";
            }
        }
        mv.addObject("areaMark", areaMark);
        return mv;
    }

    @SecurityMapping(title = "个人信息保存", value = "/mobile/buyer/account_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/account_save.htm" })
    public ModelAndView account_save(HttpServletRequest request, HttpServletResponse response,
                                     String area_id, String birthday) {
        ModelAndView mv = new JModelAndView("/user/default/usercenter/h5/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
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
        mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/account.htm");
        return mv;
    }

    private void send_sms(HttpServletRequest request, String mark, String storeId) {
        try {
            com.javamalls.platform.domain.Template template = this.templateService
                .getObjByProperty("mark", mark);
            if ((template != null) && (template.isOpen())) {
                User user = this.userService
                    .getObjById(SecurityUserHolder.getCurrentUser().getId());
                String mobile = user.getMobile();
                if ((mobile != null) && (!mobile.equals(""))) {
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
