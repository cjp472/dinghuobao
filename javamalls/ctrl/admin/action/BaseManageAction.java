package com.javamalls.ctrl.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.WebForm;
import com.javamalls.base.tools.database.DatabaseTools;
import com.javamalls.ctrl.admin.tools.MsgTools;
import com.javamalls.front.web.h5.action.Constent;
import com.javamalls.platform.domain.Accessory;
import com.javamalls.platform.domain.IntegralLog;
import com.javamalls.platform.domain.Sms;
import com.javamalls.platform.domain.StoreStat;
import com.javamalls.platform.domain.SysConfig;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.service.IAccessoryService;
import com.javamalls.platform.service.IIntegralLogService;
import com.javamalls.platform.service.ISmsService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.IStoreStatService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;

/**登录   退出    商城设置等操作
 *                       
 * @Filename: BaseManageAction.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class BaseManageAction {
    @Autowired
    private ISysConfigService   configService;
    @Autowired
    private IUserConfigService  userConfigService;
    @Autowired
    private IUserService        userService;
    @Autowired
    private IAccessoryService   accessoryService;
    @Autowired
    private ISmsService         smsService;
    @Autowired
    private IStoreService       storeService;
    @Autowired
    private IIntegralLogService integralLogService;
    @Autowired
    private DatabaseTools       databaseTools;
    @Autowired
    private IStoreStatService   storeStatService;
    @Autowired
    private MsgTools            msgTools;

    @RequestMapping({ "/login_success.htm" })
    public void login_success(HttpServletRequest request, HttpServletResponse response)
                                                                                       throws IOException {
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());

        //以下不能去掉，主要是将store load进去，否则后面使用SecurityUserHolder.getCurrentUser().getStore()会报错。
        if (user.getStore() != null) {
            SecurityUserHolder.getCurrentUser().setStore(
                this.storeService.getObjByProperty("id", user.getStore().getId()));
        }

        if ((this.configService.getSysConfig().isIntegral())
            && ((user.getLoginDate() == null) || (user.getLoginDate().before(CommUtil
                .formatDate(CommUtil.formatShortDate(new Date())))))) {
            user.setIntegral(user.getIntegral()
                             + this.configService.getSysConfig().getMemberDayLogin());
            IntegralLog log = new IntegralLog();
            log.setCreatetime(new Date());
            log.setContent("用户" + CommUtil.formatLongDate(new Date()) + "登录增加"
                           + this.configService.getSysConfig().getMemberDayLogin() + "分");
            log.setIntegral(this.configService.getSysConfig().getMemberRegister());
            log.setIntegral_user(user);
            log.setType("login");
            this.integralLogService.save(log);
        }
        user.setLoginDate(new Date());
        user.setLoginIp(CommUtil.getIpAddr(request));
        user.setLoginCount(user.getLoginCount() + 1);
        this.userService.update(user);
        HttpSession session = request.getSession(false);
        session.setAttribute("user", user);
        session.setAttribute("lastLoginDate", new Date());
        session.setAttribute("loginIp", CommUtil.getIpAddr(request));
        session.setAttribute("login", Boolean.valueOf(true));
        String role = user.getUserRole();
        String url = CommUtil.getURL(request) + "/user_login_success.htm";
        /**
         * 兼容IP/store/id访问
         */
        String jm_store_id = (String) request.getSession().getAttribute("jm_store_id");
        if (jm_store_id != null && !"".equals(jm_store_id) && !"0".equals(jm_store_id)) {
            url = CommUtil.getURL(request) + "/store/" + jm_store_id
                  + ".htm/user_login_success.htm";
        }

        String jm_view_type = CommUtil.null2String(request.getSession(false).getAttribute(
            Constent.WEB_TYPE_KEY));
        //h5登录
        if ((jm_view_type != null) && (!jm_view_type.equals("")) && (jm_view_type.equals("mobile"))) {
            url = CommUtil.getURL(request) + "/mobile/user_login_success.htm";
            if (jm_store_id != null && !"".equals(jm_store_id) && !"0".equals(jm_store_id)) {
                url = CommUtil.getURL(request) + "/store/" + jm_store_id
                      + ".htm/mobile/user_login_success.htm";
            }
        }
        // 如果是注册后登录，提示完善个人信息。
        if (session.getAttribute("THE_DO_IS_REG") != null
            && "true".equals(session.getAttribute("THE_DO_IS_REG"))) {
            url = CommUtil.getURL(request) + "/after_reg_login.htm";

            if (jm_store_id != null && !"".equals(jm_store_id) && !"0".equals(jm_store_id)) {
                url = CommUtil.getURL(request) + "/store/" + jm_store_id
                      + ".htm/after_reg_login.htm";
            }

            session.removeAttribute("THE_DO_IS_REG");
        }
        // 如果是手机注册后登录，提示完善个人信息。
        if (session.getAttribute("THE_DO_IS_REG_MOBILE") != null
            && "true".equals(session.getAttribute("THE_DO_IS_REG_MOBILE"))) {
            url = CommUtil.getURL(request) + "/mobile/after_reg_login.htm";

            if (jm_store_id != null && !"".equals(jm_store_id) && !"0".equals(jm_store_id)) {
                url = CommUtil.getURL(request) + "/store/" + jm_store_id
                      + ".htm/mobile/after_reg_login.htm";
            }

            session.removeAttribute("THE_DO_IS_REG_MOBILE");
        }
        String jm_login_role = (String) session.getAttribute("jm_login_role");
        if (this.configService.getSysConfig().isSecond_domain_open()) {
            Cookie jm_user_session = new Cookie("jm_user_session", user.getId().toString());
            jm_user_session.setDomain(CommUtil.generic_domain(request));

            response.addCookie(jm_user_session);
        }
        boolean jm_login = CommUtil.null2Boolean(session.getAttribute("jm_login"));
        if (jm_login) {
            response.setContentType("text/plain");
            response.setHeader("Cache-Control", "no-cache");
            response.setCharacterEncoding("UTF-8");
            try {
                PrintWriter writer = response.getWriter();
                writer.print("success");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if ((jm_login_role.equals("admin")) && (role.indexOf("ADMIN") >= 0)) {
                url = CommUtil.getURL(request) + "/admin/index.htm";
                request.getSession(false).setAttribute("admin_login", Boolean.valueOf(true));
            }
            /*//h5登录
            if ((jm_view_type != null) && (!jm_view_type.equals(""))
                && (jm_view_type.equals("mobile"))) {
                url = CommUtil.getURL(request) + "/mobile/index.htm";
                if (jm_store_id != null && !"".equals(jm_store_id) && !"0".equals(jm_store_id)) {
                    //url=CommUtil.getURL(request) +"/store/"+jm_store_id+".htm//mobile/index.htm";
                    url = CommUtil.getURL(request) + "/store/" + jm_store_id
                          + ".htm//mobile/frontindex.htm";

                }
            }*/

            response.sendRedirect(url);
        }
    }

    @RequestMapping({ "/store/{storeId}.htm/login_success.htm" })
    public void buyer_login_success(HttpServletRequest request, HttpServletResponse response)
                                                                                             throws IOException {
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());

        //以下不能去掉，主要是将store load进去，否则后面使用SecurityUserHolder.getCurrentUser().getStore()会报错。
        if (user.getStore() != null) {
            SecurityUserHolder.getCurrentUser().setStore(
                this.storeService.getObjByProperty("id", user.getStore().getId()));
        }

        if ((this.configService.getSysConfig().isIntegral())
            && ((user.getLoginDate() == null) || (user.getLoginDate().before(CommUtil
                .formatDate(CommUtil.formatShortDate(new Date())))))) {
            user.setIntegral(user.getIntegral()
                             + this.configService.getSysConfig().getMemberDayLogin());
            IntegralLog log = new IntegralLog();
            log.setCreatetime(new Date());
            log.setContent("用户" + CommUtil.formatLongDate(new Date()) + "登录增加"
                           + this.configService.getSysConfig().getMemberDayLogin() + "分");
            log.setIntegral(this.configService.getSysConfig().getMemberRegister());
            log.setIntegral_user(user);
            log.setType("login");
            this.integralLogService.save(log);
        }
        user.setLoginDate(new Date());
        user.setLoginIp(CommUtil.getIpAddr(request));
        user.setLoginCount(user.getLoginCount() + 1);
        this.userService.update(user);
        HttpSession session = request.getSession(false);
        session.setAttribute("user", user);
        session.setAttribute("lastLoginDate", new Date());
        session.setAttribute("loginIp", CommUtil.getIpAddr(request));
        session.setAttribute("login", Boolean.valueOf(true));
        String role = user.getUserRole();
        String url = CommUtil.getURL(request) + "/user_login_success.htm";

        String jm_view_type = CommUtil.null2String(request.getSession(false).getAttribute(
            Constent.WEB_TYPE_KEY));
        //h5登录
        if ((jm_view_type != null) && (!jm_view_type.equals("")) && (jm_view_type.equals("mobile"))) {
            url = CommUtil.getURL(request) + "/mobile/user_login_success.htm";
        }
        // 如果是注册后登录，提示完善个人信息。
        if (session.getAttribute("THE_DO_IS_REG") != null
            && "true".equals(session.getAttribute("THE_DO_IS_REG"))) {
            url = CommUtil.getURL(request) + "/after_reg_login.htm";
            session.removeAttribute("THE_DO_IS_REG");
        }
        // 如果是手机注册后登录，提示完善个人信息。
        if (session.getAttribute("THE_DO_IS_REG_MOBILE") != null
            && "true".equals(session.getAttribute("THE_DO_IS_REG_MOBILE"))) {
            url = CommUtil.getURL(request) + "/mobile/after_reg_login.htm";
            session.removeAttribute("THE_DO_IS_REG_MOBILE");
        }
        String jm_login_role = (String) session.getAttribute("jm_login_role");
        if (this.configService.getSysConfig().isSecond_domain_open()) {
            Cookie jm_user_session = new Cookie("jm_user_session", user.getId().toString());
            jm_user_session.setDomain(CommUtil.generic_domain(request));

            response.addCookie(jm_user_session);
        }
        boolean jm_login = CommUtil.null2Boolean(session.getAttribute("jm_login"));
        if (jm_login) {
            response.setContentType("text/plain");
            response.setHeader("Cache-Control", "no-cache");
            response.setCharacterEncoding("UTF-8");
            try {
                PrintWriter writer = response.getWriter();
                writer.print("success");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if ((jm_login_role.equals("admin")) && (role.indexOf("ADMIN") >= 0)) {
                url = CommUtil.getURL(request) + "/admin/index.htm";
                request.getSession(false).setAttribute("admin_login", Boolean.valueOf(true));
            }
            /*//h5登录
            if ((jm_view_type != null) && (!jm_view_type.equals(""))
                && (jm_view_type.equals("mobile"))) {
                url = CommUtil.getURL(request) + "/mobile/index.htm";
            }*/

            response.sendRedirect(url);
        }
    }

    @RequestMapping({ "/logout_success.htm" })
    public void logout_success(HttpServletRequest request, HttpServletResponse response)
                                                                                        throws IOException {
        HttpSession session = request.getSession(false);
        String targetUrl = CommUtil.getURL(request) + "/user/login.htm";
        String jm_view_type = CommUtil.null2String(session.getAttribute(Constent.WEB_TYPE_KEY));

        String jm_store_id = (String) request.getSession().getAttribute("jm_store_id");
        /**
         * 兼容IP/store/id访问
         */
        if (jm_store_id != null && !"".equals(jm_store_id) && !"0".equals(jm_store_id)) {
            targetUrl = CommUtil.getURL(request) + "/store/" + jm_store_id + ".htm/user/login.htm";

            //手机h5登录
            if ((jm_view_type != null) && (!jm_view_type.equals(""))
                && ("mobile".equals(jm_view_type))) {
                targetUrl = CommUtil.getURL(request) + "/store/" + jm_store_id
                            + ".htm/mobile/user/login.htm";
            }

            session.removeAttribute("jm_store_id");
        }
        session.removeAttribute("user");
        session.removeAttribute("login");
        session.removeAttribute("role");
        session.removeAttribute("cart");
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
            .getSession(false).removeAttribute("user");
        if (this.configService.getSysConfig().isSecond_domain_open()) {
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("jm_user_session")) {
                    cookie.setMaxAge(0);
                    cookie.setValue("");
                    cookie.setDomain(CommUtil.generic_domain(request));
                    response.addCookie(cookie);
                }
            }
        }
        response.sendRedirect(targetUrl);
    }

    @RequestMapping({ "/store/{storeId}.htm/logout_success.htm" })
    public void buyer_logout_success(HttpServletRequest request, HttpServletResponse response)
                                                                                              throws IOException {
        HttpSession session = request.getSession(false);
        String targetUrl = CommUtil.getURL(request) + "/user/login.htm";
        String jm_view_type = CommUtil.null2String(session.getAttribute(Constent.WEB_TYPE_KEY));
        //手机h5登录
        if ((jm_view_type != null) && (!jm_view_type.equals("")) && ("mobile".equals(jm_view_type))) {

            targetUrl = CommUtil.getURL(request) + "/mobile/user/login.htm";
        }
        String jm_store_id = (String) request.getSession().getAttribute("jm_store_id");
        if (jm_store_id != null && !"".equals(jm_store_id) && !"0".equals(jm_store_id)) {
            session.removeAttribute("jm_store_id");
        }
        session.removeAttribute("user");
        session.removeAttribute("login");
        session.removeAttribute("role");
        session.removeAttribute("cart");
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
            .getSession(false).removeAttribute("user");
        if (this.configService.getSysConfig().isSecond_domain_open()) {
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("jm_user_session")) {
                    cookie.setMaxAge(0);
                    cookie.setValue("");
                    cookie.setDomain(CommUtil.generic_domain(request));
                    response.addCookie(cookie);
                }
            }
        }
        response.sendRedirect(targetUrl);
    }

    @RequestMapping({ "/login_error.htm" })
    public ModelAndView login_error(HttpServletRequest request, HttpServletResponse response) {
        String jm_login_role = (String) request.getSession(false).getAttribute("jm_login_role");
        ModelAndView mv = new JModelAndView("error.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        String jm_view_type = CommUtil.null2String(request.getSession(false).getAttribute(
            Constent.WEB_TYPE_KEY));
        if ((jm_view_type != null) && (!jm_view_type.equals(""))) {
            if (jm_view_type.equals("weixin")) {
                String store_id = CommUtil.null2String(request.getSession(false).getAttribute(
                    "store_id"));
                mv = new JModelAndView("weixin/error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("url", CommUtil.getURL(request) + "/weixin/index.htm?store_id="
                                    + store_id);
            }
            //手机h5登录
            if ((jm_view_type != null) && (!jm_view_type.equals(""))
                && (jm_view_type.equals("mobile"))) {
                mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                String url = CommUtil.getURL(request) + "/mobile/user/login.htm";

                String jm_store_id = (String) request.getSession().getAttribute("jm_store_id");
                /**
                 * 兼容IP/store/id访问
                 */
                if (jm_store_id != null && !"".equals(jm_store_id) && !"0".equals(jm_store_id)) {
                    url = CommUtil.getURL(request) + "/store/" + jm_store_id
                          + ".htm/mobile/user/login.htm";
                    request.getSession().removeAttribute("jm_store_id");
                }
                mv.addObject("url", url);
            }
        } else {
            if (jm_login_role == null) {
                jm_login_role = "user";
            }
            if (jm_login_role.equals("admin")) {
                mv = new JModelAndView("admin/blue/login_error.html",
                    this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0,
                    request, response);
            } else {
                /**
                 * 兼容IP/store/id访问
                 */
                String jm_store_id = (String) request.getSession().getAttribute("jm_store_id");
                /**
                 * 兼容IP/store/id访问
                 */
                if (jm_store_id != null && !"".equals(jm_store_id) && !"0".equals(jm_store_id)) {
                    mv.addObject("url", CommUtil.getURL(request) + "/store/" + jm_store_id
                                        + ".htm/user/login.htm");
                    request.getSession().removeAttribute("jm_store_id");
                } else {
                    mv.addObject("url", CommUtil.getURL(request) + "/user/login.htm");
                }

            }
        }
        mv.addObject("op_title", "登录失败");
        return mv;
    }

    @RequestMapping({ "/store/{storeId}.htm/login_error.htm" })
    public ModelAndView buyer_login_error(HttpServletRequest request, HttpServletResponse response) {
        String jm_login_role = (String) request.getSession(false).getAttribute("jm_login_role");
        ModelAndView mv = new JModelAndView("buyer/error.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        String jm_view_type = CommUtil.null2String(request.getSession(false).getAttribute(
            Constent.WEB_TYPE_KEY));
        if ((jm_view_type != null) && (!jm_view_type.equals(""))) {
            if (jm_view_type.equals("weixin")) {
                String store_id = CommUtil.null2String(request.getSession(false).getAttribute(
                    "store_id"));
                mv = new JModelAndView("weixin/error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("url", CommUtil.getURL(request) + "/weixin/index.htm?store_id="
                                    + store_id);
            }
            //手机h5登录
            if ((jm_view_type != null) && (!jm_view_type.equals(""))
                && (jm_view_type.equals("mobile"))) {
                mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                String url = CommUtil.getURL(request) + "/mobile/user/login.htm";
                mv.addObject("url", url);
            }
        } else {
            if (jm_login_role == null) {
                jm_login_role = "user";
            }
            if (jm_login_role.equals("admin")) {
                mv = new JModelAndView("admin/blue/login_error.html",
                    this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0,
                    request, response);
            } else {
                mv.addObject("url", CommUtil.getURL(request) + "/user/login.htm");
            }
        }
        mv.addObject("op_title", "登录失败");
        return mv;
    }

    @SecurityMapping(title = "商城后台管理", value = "/admin/index.htm*", rtype = "admin", rname = "商城后台管理", rcode = "admin_index", display = false, rgroup = "设置")
    @RequestMapping({ "/admin/index.htm" })
    public ModelAndView manage(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("admin/blue/manage.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if (this.configService.getSysConfig().isUc_bbs()) {
            String uc_login_js = CommUtil.null2String(request.getSession(false).getAttribute(
                "uc_login_js"));
            mv.addObject("uc_login_js", uc_login_js);
        }

        return mv;
    }

    @SecurityMapping(title = "欢迎页面", value = "/admin/welcome.htm*", rtype = "admin", rname = "欢迎页面", rcode = "admin_index", display = false, rgroup = "设置")
    @RequestMapping({ "/admin/welcome.htm" })
    public ModelAndView welcome(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("admin/blue/welcome.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Properties props = System.getProperties();
        mv.addObject("os", props.getProperty("os.name"));
        mv.addObject("java_version", props.getProperty("java.version"));
        mv.addObject("shop_version", Integer.valueOf(20140301));
        mv.addObject("database_version", this.databaseTools.queryDatabaseVersion());
        mv.addObject("web_server_version", request.getSession(false).getServletContext()
            .getServerInfo());
        List<StoreStat> stats = this.storeStatService.query("select obj from StoreStat obj", null,
            -1, -1);
        StoreStat stat = null;
        if (stats.size() > 0) {
            stat = (StoreStat) stats.get(0);
        } else {
            stat = new StoreStat();
        }
        mv.addObject("stat", stat);
        return mv;
    }

    @SecurityMapping(title = "关于我们", value = "/admin/aboutus.htm*", rtype = "admin", rname = "关于我们", rcode = "admin_index", display = false, rgroup = "设置")
    @RequestMapping({ "/admin/aboutus.htm" })
    public ModelAndView aboutus(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("admin/blue/aboutus.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        return mv;
    }

    @SecurityMapping(title = "站点设置", value = "/admin/set_site.htm*", rtype = "admin", rname = "站点设置", rcode = "admin_set_site", rgroup = "设置")
    @RequestMapping({ "/admin/set_site.htm" })
    public ModelAndView site_set(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("admin/blue/set_site_setting.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        return mv;
    }

    @SecurityMapping(title = "上传设置", value = "/admin/set_image.htm*", rtype = "admin", rname = "上传设置", rcode = "admin_set_image", rgroup = "设置")
    @RequestMapping({ "/admin/set_image.htm" })
    public ModelAndView set_image(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("admin/blue/set_image_setting.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        return mv;
    }

    @SecurityMapping(title = "保存商城配置", value = "/admin/sys_config_save.htm*", rtype = "admin", display = false, rname = "保存商城配置", rcode = "admin_config_save", rgroup = "设置")
    @RequestMapping({ "/admin/sys_config_save.htm" })
    public ModelAndView sys_config_save(HttpServletRequest request, HttpServletResponse response,
                                        String id, String list_url, String op_title, Sms sms) {
        SysConfig obj = this.configService.getSysConfig();
        WebForm wf = new WebForm();
        SysConfig sysConfig = null;
        if (id.equals("")) {
            sysConfig = (SysConfig) wf.toPo(request, SysConfig.class);
            sysConfig.setCreatetime(new Date());
        } else {
            sysConfig = (SysConfig) wf.toPo(request, obj);
        }
        String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
        String saveFilePathName = request.getSession().getServletContext().getRealPath("/")
                                  + uploadFilePath + File.separator + "system";
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            String fileName = this.configService.getSysConfig().getWebsiteLogo() == null ? ""
                : this.configService.getSysConfig().getWebsiteLogo().getName();
            map = CommUtil.saveFileToServer(request, "websiteLogo", saveFilePathName, fileName,
                null);
            if (fileName.equals("")) {
                if (map.get("fileName") != "") {
                    Accessory logo = new Accessory();
                    logo.setName(CommUtil.null2String(map.get("fileName")));
                    logo.setExt((String) map.get("mime"));
                    logo.setSize(((Float) map.get("fileSize")).floatValue());
                    logo.setPath(uploadFilePath + "/system");
                    logo.setWidth(((Integer) map.get("width")).intValue());
                    logo.setHeight(((Integer) map.get("height")).intValue());
                    logo.setCreatetime(new Date());
                    this.accessoryService.save(logo);
                    sysConfig.setWebsiteLogo(logo);
                }
            } else if (map.get("fileName") != "") {
                Accessory logo = sysConfig.getWebsiteLogo();
                logo.setName(CommUtil.null2String(map.get("fileName")));
                logo.setExt(CommUtil.null2String(map.get("mime")));
                logo.setSize(CommUtil.null2Float(map.get("fileSize")));
                logo.setPath(uploadFilePath + "/system");
                logo.setWidth(CommUtil.null2Int(map.get("width")));
                logo.setHeight(CommUtil.null2Int(map.get("height")));
                this.accessoryService.update(logo);
                sysConfig.setWebsiteLogo(logo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        map.clear();
        try {
            map = CommUtil.saveFileToServer(request, "goodsImage", saveFilePathName, null, null);
            String fileName = sysConfig.getGoodsImage().getName();
            if (fileName.equals("")) {
                if (map.get("fileName") != "") {
                    Accessory photo = new Accessory();
                    photo.setName(CommUtil.null2String(map.get("fileName")));
                    photo.setExt(CommUtil.null2String(map.get("mime")));
                    photo.setSize(CommUtil.null2Float(map.get("fileSize")));
                    photo.setPath(uploadFilePath);
                    photo.setWidth(CommUtil.null2Int(map.get("width")));
                    photo.setHeight(CommUtil.null2Int(map.get("heigh")));
                    photo.setCreatetime(new Date());
                    this.accessoryService.save(photo);
                    sysConfig.setGoodsImage(photo);
                }
            } else if (map.get("fileName") != "") {
                Accessory photo = sysConfig.getGoodsImage();
                photo.setName(CommUtil.null2String(map.get("fileName")));
                photo.setExt(CommUtil.null2String(map.get("mime")));
                photo.setSize(CommUtil.null2Float(map.get("fileSize")));
                photo.setPath(uploadFilePath + "/system");
                photo.setWidth(CommUtil.null2Int(map.get("width")));
                photo.setHeight(CommUtil.null2Int(map.get("height")));
                this.accessoryService.update(photo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        map.clear();
        try {
            map = CommUtil.saveFileToServer(request, "storeImage", saveFilePathName, null, null);
            String fileName = sysConfig.getStoreImage().getName();
            if (fileName.equals("")) {
                if (map.get("fileName") != "") {
                    Accessory photo = new Accessory();
                    photo.setName((String) map.get("fileName"));
                    photo.setExt((String) map.get("mime"));
                    photo.setSize(((Float) map.get("fileSize")).floatValue());
                    photo.setPath(uploadFilePath);
                    photo.setWidth(((Integer) map.get("width")).intValue());
                    photo.setHeight(((Integer) map.get("heigh")).intValue());
                    photo.setCreatetime(new Date());
                    this.accessoryService.save(photo);
                    sysConfig.setStoreImage(photo);
                }
            } else if (map.get("fileName") != "") {
                Accessory photo = sysConfig.getStoreImage();
                photo.setName(CommUtil.null2String(map.get("fileName")));
                photo.setExt(CommUtil.null2String(map.get("mime")));
                photo.setSize(CommUtil.null2Float(map.get("fileSize")));
                photo.setPath(uploadFilePath + "/system");
                photo.setWidth(CommUtil.null2Int(map.get("width")));
                photo.setHeight(CommUtil.null2Int(map.get("height")));
                this.accessoryService.update(photo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        map.clear();
        try {
            map = CommUtil.saveFileToServer(request, "memberIcon", saveFilePathName, null, null);
            String fileName = sysConfig.getMemberIcon().getName();
            if (fileName.equals("")) {
                if (map.get("fileName") != "") {
                    Accessory photo = new Accessory();
                    photo.setName((String) map.get("fileName"));
                    photo.setExt((String) map.get("mime"));
                    photo.setSize(((Float) map.get("fileSize")).floatValue());
                    photo.setPath(uploadFilePath);
                    photo.setWidth(((Integer) map.get("width")).intValue());
                    photo.setHeight(((Integer) map.get("heigh")).intValue());
                    photo.setCreatetime(new Date());
                    this.accessoryService.save(photo);
                    sysConfig.setMemberIcon(photo);
                }
            } else if (map.get("fileName") != "") {
                Accessory photo = sysConfig.getMemberIcon();
                photo.setName(CommUtil.null2String(map.get("fileName")));
                photo.setExt(CommUtil.null2String(map.get("mime")));
                photo.setSize(CommUtil.null2Float(map.get("fileSize")));
                photo.setPath(uploadFilePath + "/system");
                photo.setWidth(CommUtil.null2Int(map.get("width")));
                photo.setHeight(CommUtil.null2Int(map.get("height")));
                this.accessoryService.update(photo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (id.equals("")) {
            this.configService.save(sysConfig);
        } else {
            this.configService.update(sysConfig);
        }
        for (int i = 0; i < 4; i++) {
            try {
                map.clear();
                String fileName = "";
                if (sysConfig.getLogin_imgs().size() > i) {
                    fileName = ((Accessory) sysConfig.getLogin_imgs().get(i)).getName();
                }
                map = CommUtil.saveFileToServer(request, "img" + i, saveFilePathName, fileName,
                    null);
                if (fileName.equals("")) {
                    if (map.get("fileName") != "") {
                        Accessory img = new Accessory();
                        img.setName(CommUtil.null2String(map.get("fileName")));
                        img.setExt((String) map.get("mime"));
                        img.setSize(((Float) map.get("fileSize")).floatValue());
                        img.setPath(uploadFilePath + "/system");
                        img.setWidth(((Integer) map.get("width")).intValue());
                        img.setHeight(((Integer) map.get("height")).intValue());
                        img.setCreatetime(new Date());
                        img.setConfig(sysConfig);
                        this.accessoryService.save(img);
                    }
                } else if (map.get("fileName") != "") {
                    Accessory img = (Accessory) sysConfig.getLogin_imgs().get(i);
                    img.setName(CommUtil.null2String(map.get("fileName")));
                    img.setExt(CommUtil.null2String(map.get("mime")));
                    img.setSize(CommUtil.null2Float(map.get("fileSize")));
                    img.setPath(uploadFilePath + "/system");
                    img.setWidth(CommUtil.null2Int(map.get("width")));
                    img.setHeight(CommUtil.null2Int(map.get("height")));
                    img.setConfig(sysConfig);
                    this.accessoryService.update(img);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("op_title", op_title);
        mv.addObject("list_url", list_url);
        if (null != sms && null != sms.getHttpaddr()) {
            // 短信接口信息保存
            sms.setId(1L);
            if (null != this.smsService.getObjById(1L)) {
                this.smsService.update(sms);
            }
            mv.addObject("sms", sms);
        }
        return mv;
    }

    @SecurityMapping(title = "Email设置", value = "/admin/set_email.htm*", rtype = "admin", rname = "Email设置", rcode = "admin_set_email", rgroup = "设置")
    @RequestMapping({ "/admin/set_email.htm" })
    public ModelAndView set_email(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("admin/blue/set_email_setting.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        return mv;
    }

    @SecurityMapping(title = "短信设置", value = "/admin/set_sms.htm*", rtype = "admin", rname = "短信设置", rcode = "admin_set_sms", rgroup = "设置")
    @RequestMapping({ "/admin/set_sms.htm" })
    public ModelAndView set_sms(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("admin/blue/set_sms_setting.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Sms sms = this.smsService.getObjById(1L);
        if (null != sms) {
            mv.addObject("sms", sms);
        }
        return mv;
    }

    @SecurityMapping(title = "SEO设置", value = "/admin/set_seo.htm*", rtype = "admin", rname = "SEO设置", rcode = "admin_set_seo", rgroup = "设置")
    @RequestMapping({ "/admin/set_seo.htm" })
    public ModelAndView set_seo(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("admin/blue/set_seo_setting.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        return mv;
    }

    @SecurityMapping(title = "QQ互联登录", value = "/admin/set_site_qq.htm*", rtype = "admin", rname = "二级域名设置", rcode = "admin_set_second_domain", rgroup = "设置")
    @RequestMapping({ "/admin/set_site_qq.htm" })
    public ModelAndView set_site_qq(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("admin/blue/set_second_domain.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        return mv;
    }

    @SecurityMapping(title = "分润设置", value = "/admin/set_fenrun.htm*", rtype = "admin", rname = "分润管理", rcode = "admin_set_fenrun", rgroup = "设置")
    @RequestMapping({ "/admin/set_fenrun.htm" })
    public ModelAndView set_fenrun(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("admin/blue/set_fenrun.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        return mv;
    }

    @SecurityMapping(title = "保存分润设置", value = "/admin/set_fenrun_save.htm*", rtype = "admin", rname = "分润管理", rcode = "admin_set_fenrun", rgroup = "设置")
    @RequestMapping({ "/admin/set_fenrun_save.htm" })
    public ModelAndView set_fenrun_save(HttpServletRequest request, HttpServletResponse response,
                                        String id, String alipay_fenrun, String balance_fenrun) {
        SysConfig obj = this.configService.getSysConfig();
        WebForm wf = new WebForm();
        SysConfig config = null;
        if (id.equals("")) {
            config = (SysConfig) wf.toPo(request, SysConfig.class);
            config.setCreatetime(new Date());
        } else {
            config = (SysConfig) wf.toPo(request, obj);
        }
        config.setAlipay_fenrun(CommUtil.null2Int(alipay_fenrun));
        config.setBalance_fenrun(CommUtil.null2Int(balance_fenrun));
        if (id.equals("")) {
            this.configService.save(config);
        } else {
            this.configService.update(config);
        }
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("op_title", "分润设置成功");
        mv.addObject("list_url", CommUtil.getURL(request) + "/admin/set_fenrun.htm");
        return mv;
    }

    @RequestMapping({ "/admin/logout.htm" })
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        User user = SecurityUserHolder.getCurrentUser();

        // 记录ip和时间
        if (user != null && session != null) {
            user.setLastLoginDate((Date) session.getAttribute("lastLoginDate"));
            user.setLastLoginIp((String) session.getAttribute("loginIp"));
            this.userService.update(user);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                SecurityContextHolder.getContext().getAuthentication().getPrincipal(),
                SecurityContextHolder.getContext().getAuthentication().getCredentials(),
                user.get_common_Authorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        if (session != null) {
            // 清除session
            session.invalidate();
        }

        return "redirect:/admin/index.htm";
    }

    @RequestMapping({ "/admin/login.htm" })
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("admin/blue/login.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        User user = SecurityUserHolder.getCurrentUser();
        if (user != null) {
            mv.addObject("user", user);
        }
        return mv;
    }

    @RequestMapping({ "/success.htm" })
    public ModelAndView success(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        mv.addObject("op_title", request.getSession(false).getAttribute("op_title"));
        mv.addObject("url", request.getSession(false).getAttribute("url"));
        request.getSession(false).removeAttribute("op_title");
        request.getSession(false).removeAttribute("url");
        return mv;
    }

    @RequestMapping({ "/error.htm" })
    public ModelAndView error(HttpServletRequest request, HttpServletResponse response) {
        User user = SecurityUserHolder.getCurrentUser();
        ModelAndView mv = new JModelAndView("error.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        if ((user != null) && (user.getUserRole().equalsIgnoreCase("ADMIN"))) {
            mv = new JModelAndView("admin/blue/error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 0, request, response);
        }
        mv.addObject("op_title", request.getSession(false).getAttribute("op_title"));
        mv.addObject("list_url", request.getSession(false).getAttribute("url"));
        mv.addObject("url", request.getSession(false).getAttribute("url"));
        request.getSession(false).removeAttribute("op_title");
        request.getSession(false).removeAttribute("url");
        return mv;
    }

    @RequestMapping({ "/exception.htm" })
    public ModelAndView exception(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        ModelAndView mv = new JModelAndView("error.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        if ((user != null) && (user.getUserRole().equalsIgnoreCase("ADMIN"))) {
            mv = new JModelAndView("admin/blue/exception.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 0, request, response);
        } else {
            mv.addObject("op_title", "系统出现异常");
            mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
        }
        return mv;
    }

    @RequestMapping({ "/authority.htm" })
    public ModelAndView authority(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("admin/blue/authority.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        boolean domain_error = CommUtil.null2Boolean(request.getSession(false).getAttribute(
            "domain_error"));
        if (domain_error) {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "域名绑定错误");
        }
        return mv;
    }

    @RequestMapping({ "/voice.htm" })
    public ModelAndView voice(HttpServletRequest request, HttpServletResponse response) {
        return new JModelAndView("include/flash/soundPlayer.swf",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), request,
            response);
    }

    @RequestMapping({ "/getCode.htm" })
    public void getCode(HttpServletRequest request, HttpServletResponse response)
                                                                                 throws IOException {
        HttpSession session = request.getSession(false);
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter writer = response.getWriter();
        writer.print("result=true&code=" + (String) session.getAttribute("verify_number"));
    }

    @RequestMapping({ "/editor.htm" })
    public ModelAndView editor(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("admin/blue/editor_test.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        return mv;
    }

    @RequestMapping({ "/upload.htm" })
    public void upload(HttpServletRequest request, HttpServletResponse response)
                                                                                throws ClassNotFoundException {
        String saveFilePathName = request.getSession().getServletContext().getRealPath("/")
                                  + this.configService.getSysConfig().getUploadFilePath()
                                  + File.separator + "common";
        String webPath = request.getContextPath().equals("/") ? "" : request.getContextPath();
        if ((this.configService.getSysConfig().getAddress() != null)
            && (!this.configService.getSysConfig().getAddress().equals(""))) {
            webPath = this.configService.getSysConfig().getAddress() + webPath;
        }
        JSONObject obj = new JSONObject();
        try {
            Map<String, Object> map = CommUtil.saveFileToServer(request, "imgFile",
                saveFilePathName, null, null);
            String url = webPath + "/" + this.configService.getSysConfig().getUploadFilePath()
                         + "/common/" + map.get("fileName");
            obj.put("error", Integer.valueOf(0));
            obj.put("url", url);
        } catch (IOException e) {
            obj.put("error", Integer.valueOf(1));
            obj.put("message", e.getMessage());
            e.printStackTrace();
        }
        response.setContentType("text/html");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(obj.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping({ "/js.htm" })
    public ModelAndView js(HttpServletRequest request, HttpServletResponse response, String js) {
        ModelAndView mv = new JModelAndView("resources/js/" + js + ".js",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 2, request,
            response);
        return mv;
    }

    @RequestMapping({ "/admin/test_mail.htm" })
    public void test_email(HttpServletResponse response, String email) {
        String subject = this.configService.getSysConfig().getTitle() + "测试邮件";
        boolean ret = this.msgTools.sendEmail(email, subject, subject);
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

    @RequestMapping({ "/admin/test_sms.htm" })
    public void test_sms(HttpServletResponse response, String mobile)
                                                                     throws UnsupportedEncodingException {
        String content = this.configService.getSysConfig().getTitle() + "测试短信,如果您收到短信，说明发送成功！";
        Map<String, String> map = new HashMap<String, String>();
        map.put("customer", "张先生");
        boolean ret = this.msgTools.sendSMS(mobile, "SMS_123737606", map);
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

    @RequestMapping({ "/admin/user_msg_save.htm" })
    public void user_msg_save(HttpServletResponse response, String msg) throws HttpException,
                                                                       IOException {
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod("/admin/user_msg.htm");// wtw
        method.addParameter("msg", msg);
        int status = client.executeMethod(method);
        boolean ret = false;
        if (status == 200) {
            ret = true;
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

    @SecurityMapping(title = "websiteCss设置", value = "/admin/set_websiteCss.htm*", rtype = "admin", rname = "Email设置", rcode = "admin_set_websiteCss", rgroup = "设置")
    @RequestMapping({ "/admin/set_websiteCss.htm" })
    public void set_websiteCss(HttpServletRequest request, HttpServletResponse response,
                               String webcss) {
        SysConfig obj = this.configService.getSysConfig();
        if ((!webcss.equals("blue")) && (!webcss.equals("black"))) {
            webcss = "blue";
        }
        obj.setWebsiteCss(webcss);
        this.configService.update(obj);
    }
}
