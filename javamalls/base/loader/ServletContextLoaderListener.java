package com.javamalls.base.loader;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.javamalls.base.security.SecurityManager;

/**加载资源
 *                       
 * @Filename: ServletContextLoaderListener.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
public class ServletContextLoaderListener implements ServletContextListener {
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        SecurityManager securityManager = getSecurityManager(servletContext);
        //加载资源
        Map<String, String> urlAuthorities = securityManager.loadUrlAuthorities();
        servletContext.setAttribute("urlAuthorities", urlAuthorities);
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        servletContextEvent.getServletContext().removeAttribute("urlAuthorities");
    }

    protected SecurityManager getSecurityManager(ServletContext servletContext) {
        return (SecurityManager) WebApplicationContextUtils
            .getWebApplicationContext(servletContext).getBean("securityManager");
    }
}
