package com.javamalls.base.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.AuthenticationException;
import org.springframework.security.ui.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.javamalls.base.tools.CommUtil;

/**登录过滤器  
 *                       
 * @Filename: LoginUrlEntryPoint.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Component
public class LoginUrlEntryPoint implements AuthenticationEntryPoint {
    public void commence(ServletRequest req, ServletResponse res,
                         AuthenticationException authException) throws IOException,
                                                               ServletException {
        String targetUrl = null;
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String url = request.getRequestURI();
        if ((request.getQueryString() != null) && (!request.getQueryString().equals(""))) {
            url = url + "?" + request.getQueryString();
        }
        request.getSession(false).setAttribute("refererUrl", url);
        if (url.indexOf("/admin/") >= 0) {
            targetUrl = CommUtil.getURL(request) + "/admin/login.htm";
        } else if (url.indexOf("/mobile/") >= 0) {
            targetUrl = CommUtil.getURL(request) + "/mobile/user/login.htm";
        } else {
            targetUrl = CommUtil.getURL(request) + "/user/login.htm";
        }
        response.sendRedirect(targetUrl);
    }
}
