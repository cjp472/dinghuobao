package com.javamalls.base.mv;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.HttpInclude;
import com.javamalls.platform.domain.SysConfig;
import com.javamalls.platform.domain.UserConfig;

/**重写ModelAndView
 *                       
 * @Filename: JModelAndView.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
public class JModelAndView extends ModelAndView {

    public static final int SYSTEM_PATH  = 0;
    public static final int SHOP_PATH    = 1;
    public static final int DEFAULT_PATH = 2;

    /* //内部测试环境：前台地址
    public static String    FRONT_WEB_PATH       = "http://youpin.manager.sway365.com";
    //后缀
    public static String    FRONT_WEB_AFTER_PATH = ".manager.sway365.com";
    
    //开发环境： 平台域名（自营店铺域名）
    public static String    FRONT_WEB_PATH       = "http://localhost:8080/store/40.htm";
    //后缀
    public static String    FRONT_WEB_AFTER_PATH = ".manager.sway365.com";
    
    //线上环境 ： 平台域名（自营店铺域名）
    public static String    FRONT_WEB_PATH       = "http://sway.wohayo.shop";
    //后缀
    public static String    FRONT_WEB_AFTER_PATH = ".wohayo.shop";
    */

    public JModelAndView(String viewName) {
        super.setViewName(viewName);
    }

    public JModelAndView(String viewName, SysConfig config, UserConfig uconfig,
                         HttpServletRequest request, HttpServletResponse response) {
        String contextPath = request.getContextPath().equals("/") ? "" : request.getContextPath();
        String storePath = CommUtil.getURL(request);

        String port = ":" + CommUtil.null2Int(Integer.valueOf(request.getServerPort()));
        if ((config.isSecond_domain_open())
            && (!CommUtil.generic_domain(request).equals("localhost"))) {
            storePath = "http://www." + CommUtil.generic_domain(request) + port + contextPath;
        }
        String webPath = config.getAddress();
        super.setViewName(viewName);
        super.addObject("domainPath", CommUtil.generic_domain(request));
        if ((config.getImageWebServer() != null) && (!config.getImageWebServer().equals(""))) {
            super.addObject("imageWebServer", config.getImageWebServer());
        } else {
            super.addObject("imageWebServer", webPath);
        }
        super.addObject("storePath", storePath);
        super.addObject("webPath", webPath);
        super.addObject("frontWebPath", config.getFront_web_path());
        super.addObject("frontWebAfterPath", config.getFront_web_after_path());
        super.addObject("config", config);
        super.addObject("uconfig", uconfig);
        super.addObject("user", SecurityUserHolder.getCurrentUser());
        super.addObject("httpInclude", new HttpInclude(request, response));
        String query_url = "";
        if ((request.getQueryString() != null) && (!request.getQueryString().equals(""))) {
            query_url = "?" + request.getQueryString();
        }
        super.addObject("current_url", request.getRequestURI() + query_url);
        boolean second_domain_view = false;
        String serverName = request.getServerName().toLowerCase();
        if ((serverName.indexOf("www.") < 0) && (serverName.indexOf(".") >= 0)
            && (serverName.indexOf(".") != serverName.lastIndexOf("."))
            && (config.isSecond_domain_open())) {
            String secondDomain = serverName.substring(0, serverName.indexOf("."));
            second_domain_view = true;
            super.addObject("secondDomain", secondDomain);
        }
        super.addObject("second_domain_view", Boolean.valueOf(second_domain_view));
    }

    @SuppressWarnings("static-access")
    public JModelAndView(String viewName, SysConfig config, UserConfig uconfig, int type,
                         HttpServletRequest request, HttpServletResponse response) {
        if (config.getSysLanguage() != null) {
            if (config.getSysLanguage().equals("zh_cn")) {
                if (type == this.SYSTEM_PATH) {
                    super.setViewName("WEB-INF/templates/zh_cn/system/" + viewName);
                }
                if (type == this.SHOP_PATH) {
                    super.setViewName("WEB-INF/templates/zh_cn/shop/" + viewName);
                }
                if (type > 1) {
                    super.setViewName(viewName);
                }
            } else {
                if (type == 0) {
                    super.setViewName("WEB-INF/templates/" + config.getSysLanguage() + "/system/"
                                      + viewName);
                }
                if (type == 1) {
                    super.setViewName("WEB-INF/templates/" + config.getSysLanguage() + "/shop/"
                                      + viewName);
                }
                if (type > 1) {
                    super.setViewName(viewName);
                }
            }
        } else {
            super.setViewName(viewName);
        }
        super.addObject("CommUtil", new CommUtil());
        String contextPath = request.getContextPath().equals("/") ? "" : request.getContextPath();
        String storePath = CommUtil.getURL(request);

        String port = ":" + CommUtil.null2Int(Integer.valueOf(request.getServerPort()));
        if ((config.isSecond_domain_open())
            && (!CommUtil.generic_domain(request).equals("localhost"))) {
            storePath = "http://www." + CommUtil.generic_domain(request) + port + contextPath;
        }
        String webPath = config.getAddress();
        super.addObject("domainPath", CommUtil.generic_domain(request));
        super.addObject("webPath", webPath);
        if ((config.getImageWebServer() != null) && (!config.getImageWebServer().equals(""))) {
            super.addObject("imageWebServer", config.getImageWebServer());
        } else {
            super.addObject("imageWebServer", webPath);
        }
        super.addObject("storePath", storePath);
        super.addObject("frontWebPath", config.getFront_web_path());
        super.addObject("frontWebAfterPath", config.getFront_web_after_path());
        super.addObject("config", config);
        super.addObject("uconfig", uconfig);
        super.addObject("user", SecurityUserHolder.getCurrentUser());
        super.addObject("httpInclude", new HttpInclude(request, response));
        String query_url = "";
        if ((request.getQueryString() != null) && (!request.getQueryString().equals(""))) {
            query_url = "?" + request.getQueryString();
        }
        super.addObject("current_url", request.getRequestURI() + query_url);
        boolean second_domain_view = false;
        String serverName = request.getServerName().toLowerCase();
        if ((serverName.indexOf("www.") < 0) && (serverName.indexOf(".") >= 0)
            && (serverName.indexOf(".") != serverName.lastIndexOf("."))
            && (config.isSecond_domain_open())) {
            String secondDomain = serverName.substring(0, serverName.indexOf("."));
            second_domain_view = true;
            super.addObject("secondDomain", secondDomain);
        }
        super.addObject("second_domain_view", Boolean.valueOf(second_domain_view));
    }

}
