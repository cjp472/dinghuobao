package com.javamalls.ctrl.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
import com.javamalls.payment.chinabank.h5.util.JsonUtil;
import com.javamalls.platform.domain.Area;
import com.javamalls.platform.domain.CompanyInfo;
import com.javamalls.platform.domain.MobileVerifyCode;
import com.javamalls.platform.domain.SnsFriend;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.query.SnsFriendQueryObject;
import com.javamalls.platform.domain.query.UserQueryObject;
import com.javamalls.platform.service.IAreaService;
import com.javamalls.platform.service.ICompanyInfoService;
import com.javamalls.platform.service.IMobileVerifyCodeService;
import com.javamalls.platform.service.ISnsFriendService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.ITemplateService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;
import com.utils.SendReqAsync;

/**卖家中心  密码  手机、邮箱修改等
 *                       
 * @Filename: AccountBuyerAction.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class AccountSellerAction {
    @Autowired
    private ISysConfigService        configService;
    @Autowired
    private IUserConfigService       userConfigService;
    @Autowired
    private IUserService             userService;
    @Autowired
    private IMobileVerifyCodeService mobileverifycodeService;

    @Autowired
    private ISnsFriendService        sndFriendService;
    @Autowired
    private ITemplateService         templateService;
    @Autowired
    private IAreaService             areaService;

    @Autowired
    private ICompanyInfoService      companyInfoService;
    @Autowired
    private SendReqAsync             sendReqAsync;

    @Autowired
    private MsgTools                 msgTools;
    private static final String      DEFAULT_AVATAR_FILE_EXT     = ".jpg";
    private static BASE64Decoder     _decoder                    = new BASE64Decoder();
    public static final String       OPERATE_RESULT_CODE_SUCCESS = "200";
    public static final String       OPERATE_RESULT_CODE_FAIL    = "400";

    @SecurityMapping(title = "个人信息获取下级地区ajax", value = "/buyer/account_getAreaChilds.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/seller/account_getAreaChilds.htm" })
    public ModelAndView account_getAreaChilds(HttpServletRequest request,
                                              HttpServletResponse response, String parent_id) {
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/seller/account_area_chlids.html",
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

    @SecurityMapping(title = "密码修改", value = "/seller/account_password.htm*", rtype = "seller", rname = "用户中心", rcode = "user_center_seller", rgroup = "用户中心")
    @RequestMapping({ "/seller/account_password.htm" })
    public ModelAndView account_password(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller/account_password.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("user",
            this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId()));
        return mv;
    }

    @SecurityMapping(title = "密码修改保存", value = "/seller/account_password_save.htm*", rtype = "seller", rname = "用户中心", rcode = "user_center_seller", rgroup = "用户中心")
    @RequestMapping({ "/seller/account_password_save.htm" })
    public void account_password_save(HttpServletRequest request, HttpServletResponse response,
                                      String old_password, String new_password) throws Exception {
        /*ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);*/
        boolean ret = true;
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        if (user.getPassword().equals(Md5Encrypt.md5(old_password).toLowerCase())) {
            user.setPassword(Md5Encrypt.md5(new_password).toLowerCase());
            this.userService.update(user);
            //  mv.addObject("op_title", "密码修改成功");
            send_sms(request, "sms_tobuyer_pws_modify_notify");
        } else {
            /*  mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                  this.userConfigService.getUserConfig(), 1, request, response);
              mv.addObject("op_title", "原始密码输入错误，修改失败");*/
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
    }

    @SecurityMapping(title = "邮箱修改保存", value = "/seller/account_email_save.htm*", rtype = "seller", rname = "用户中心", rcode = "user_center_seller", rgroup = "用户中心")
    @RequestMapping({ "/seller/account_email_save.htm" })
    public ModelAndView account_email_save(HttpServletRequest request,
                                           HttpServletResponse response, String password,
                                           String email) {
        ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        if (user.getPassword().equals(Md5Encrypt.md5(password).toLowerCase())) {
            user.setEmail(email);
            this.userService.update(user);
            mv.addObject("op_title", "邮箱修改成功");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/account_password.htm");

            //调用用户接口
            String write2JsonStr = JsonUtil.write2JsonStr(user);
            sendReqAsync.sendMessageUtil(Constant.USER_INTEFACE_URL_EDIT, write2JsonStr, "修改会员");
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "密码输入错误，邮箱修改失败");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/account_password.htm");
        }
        // mv.addObject("url", CommUtil.getURL(request) +
        // "/buyer/account_email.htm");
        return mv;
    }

    @SecurityMapping(title = "手机号码保存", value = "/seller/account_mobile_save.htm*", rtype = "seller", rname = "用户中心", rcode = "user_center_seller", rgroup = "用户中心")
    @RequestMapping({ "/seller/account_mobile_save.htm" })
    public void account_mobile_save(HttpServletRequest request, HttpServletResponse response,
                                    String mobile_verify_number, String mobile) throws Exception {
        /*ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(),
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

            send_sms(request, "sms_tobuyer_mobilebind_notify");

            //调用用户接口
            String write2JsonStr = JsonUtil.write2JsonStr(user);
            sendReqAsync.sendMessageUtil(Constant.USER_INTEFACE_URL_EDIT, write2JsonStr, "修改会员");
            //    mv.addObject("url", CommUtil.getURL(request) + "/seller/account_password.htm");
        } else {
            /*mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "验证码错误，手机绑定失败");
            // mv.addObject("url", CommUtil.getURL(request) +
            // "/buyer/account_mobile.htm");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/account_password.htm");*/
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
        //  return mv;
    }

    @SecurityMapping(title = "手机短信发送", value = "/seller/account_mobile_sms.htm*", rtype = "seller", rname = "用户中心", rcode = "user_center_seller", rgroup = "用户中心")
    @RequestMapping({ "/seller/account_mobile_sms.htm" })
    public void account_mobile_sms(HttpServletRequest request, HttpServletResponse response,
                                   String type, String mobile) throws UnsupportedEncodingException {
        String ret = "100";
        if (type.equals("mobile_vetify_code")) {
            String code = CommUtil.randomInt(6).toUpperCase();
            /* String content = "尊敬的" + SecurityUserHolder.getCurrentUser().getUserName()
                              + "您好，您在试图修改" + this.configService.getSysConfig().getWebsiteName()
                              + "用户绑定手机，手机验证码为：" + code + "。["
                              + this.configService.getSysConfig().getTitle() + "]";*/
            com.javamalls.platform.domain.Template template = this.templateService
                .getObjByProperty("mark", "sms_tobuyer_mobilemodify_notify");
            if (this.configService.getSysConfig().isSmsEnbale()) {
                if ((template != null) && (template.isOpen())) {
                    Map<String, String> map = new HashMap<String, String>();
                    User currentUser = SecurityUserHolder.getCurrentUser();

                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("mobile", mobile);
                    params.put("id", currentUser.getId());
                    List<User> users = this.userService
                        .query(
                            "select obj from User obj where obj.disabled=false and obj.userRole='BUYER_SELLER' and (obj.mobile=:mobile or obj.userName=:mobile) and obj.id!=:id",
                            params, -1, -1);
                    if ((users != null) && (users.size() > 0)) {
                        ret = "301";
                    } else {
                        String userName = currentUser.getUserName();
                        if (currentUser.getTrueName() != null
                            && !"".equals(currentUser.getTrueName())) {
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
    @RequestMapping({ "/seller/friend.htm" })
    public ModelAndView account_friend(HttpServletRequest request, HttpServletResponse response,
                                       String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller/account_friend.html",
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
    @RequestMapping({ "/seller/friend_add.htm" })
    public ModelAndView friend_add(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/seller/account_friend_search.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        List<Area> areas = this.areaService.query(
            "select obj from Area obj where obj.parent.id is null", null, -1, -1);
        mv.addObject("areas", areas);
        return mv;
    }

    @SecurityMapping(title = "搜索用户", value = "/buyer/account_friend_search.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/seller/account_friend_search.htm" })
    public ModelAndView friend_search(HttpServletRequest request, HttpServletResponse response,
                                      String userName, String area_id, String sex, String years,
                                      String currentPage) {
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/seller/account_friend_search.html",
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
    @RequestMapping({ "/seller/friend_add_save.htm" })
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
    @RequestMapping({ "/seller/friend_del.htm" })
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
    @RequestMapping({ "/seller/account_bind.htm" })
    public ModelAndView account_bind(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/account_bind.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        mv.addObject("user", user);
        return mv;
    }

    @SecurityMapping(title = "账号解除绑定", value = "/buyer/account_bind_cancel.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/seller/account_bind_cancel.htm" })
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

    /**
     * 公司基本信息维护
     * @param request
     * @param response
     * @return
     */
    @SecurityMapping(title = "公司信息维护", value = "/seller/account_company.htm*", rtype = "selller", rname = "用户中心", rcode = "user_center_seller", rgroup = "用户中心")
    @RequestMapping({ "/seller/account_company.htm" })
    public ModelAndView account_company(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller/company_info.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", SecurityUserHolder.getCurrentUser().getId());
        List<CompanyInfo> list = this.companyInfoService.query(
            "select obj from CompanyInfo obj where obj.disabled=false and obj.user.id=:userId",
            map, 0, 1);
        if (list != null && list.size() > 0) {
            CompanyInfo companyInfo = list.get(0);
            mv.addObject("obj", companyInfo);
        }
        List<Area> areas = this.areaService.query(
            "select obj from Area obj where obj.parent.id is null", null, -1, -1);
        String areaJson = JsonUtil.write2JsonStr(areas);

        mv.addObject("areaJson", areaJson);
        return mv;
    }

    @SecurityMapping(title = "公司信息修改保存", value = "/seller/account_company_save.htm*", rtype = "seller", rname = "用户中心", rcode = "user_center_seller", rgroup = "用户中心")
    @RequestMapping({ "/seller/account_company_save.htm" })
    public ModelAndView account_company_save(HttpServletRequest request,
                                             HttpServletResponse response, String id, String area_id)
                                                                                                     throws Exception {
        ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        WebForm wf = new WebForm();
        if (id != null && !"".equals(id)) {//编辑
            CompanyInfo obj = this.companyInfoService.getObjById(CommUtil.null2Long(id));
            CompanyInfo company = (CompanyInfo) wf.toPo(request, obj);
            Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
            company.setArea(area);
            this.companyInfoService.save(company);

            //调用接口
            String str = JsonUtil.write2JsonStr(company);

            sendReqAsync.sendMessageUtil(Constant.COMPANY_INTEFACE_URL_EDIT, str, "修改会员");

        } else {//新增
            User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
            CompanyInfo company = (CompanyInfo) wf.toPo(request, CompanyInfo.class);
            company.setCreatetime(new Date());
            company.setDisabled(false);
            company.setUser(user);
            Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
            company.setArea(area);
            this.companyInfoService.save(company);

            //调用接口
            String str = JsonUtil.write2JsonStr(company);
            sendReqAsync.sendMessageUtil(Constant.COMPANY_INTEFACE_URL_ADD, str, "新增会员");

        }

        mv.addObject("url", CommUtil.getURL(request) + "/seller/account_company.htm");
        return mv;
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
                    /* String path = request.getSession().getServletContext().getRealPath("/")
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

                     String content = writer.toString();*/
                    Map<String, String> map = new HashMap<String, String>();
                    String userName = user.getUsername();
                    if (user.getTrueName() != null && !"".equals(user.getTrueName())) {
                        userName = user.getTrueName();
                    }
                    map.put("buyerName", userName);
                    map.put("storeName", this.configService.getSysConfig().getTitle());
                    if ("sms_tobuyer_mobilebind_notify".equals(mark)) {
                        map.put("user_mobile", user.getMobile());
                    }
                    this.msgTools.sendSMS(mobile, template.getTitle(), map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
