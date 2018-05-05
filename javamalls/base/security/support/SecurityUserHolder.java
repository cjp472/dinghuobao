package com.javamalls.base.security.support;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.domain.User;

/**获取当前登录用户
 *                       
 * @Filename: SecurityUserHolder.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
public class SecurityUserHolder {
    public static User getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            if ((SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User)) {
                return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            }
        }
        User user = null;
        if (RequestContextHolder.getRequestAttributes() != null) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();
            if (request.getSession(false) == null)
                return null;
            user = null != request.getSession(false).getAttribute("user") ? (User) request
                .getSession(false).getAttribute("user") : null;

            Cookie[] cookies = request.getCookies();
            String id = "";
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("jm_user_session")) {
                        id = CommUtil.null2String(cookie.getValue());
                    }
                }
            }
            if (id.equals("")) {
                user = null;
            }
        }
        return user;
    }
}
