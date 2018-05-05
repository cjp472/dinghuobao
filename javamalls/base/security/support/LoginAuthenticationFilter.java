package com.javamalls.base.security.support;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;
import org.springframework.security.util.TextUtils;

import com.javamalls.base.tools.CommUtil;
import com.javamalls.front.web.h5.action.Constent;
import com.javamalls.platform.service.ISysConfigService;

/**登录
 *                       
 * @Filename: LoginAuthenticationFilter.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
public class LoginAuthenticationFilter extends AuthenticationProcessingFilter {
    @Autowired
    private ISysConfigService configService;

    public Authentication attemptAuthentication(HttpServletRequest request)
                                                                           throws AuthenticationException {
        String jm_view_type = request.getParameter(Constent.WEB_TYPE_KEY);
        String jm_login_role = request.getParameter("jm_login_role");
        String jm_store_id = request.getParameter("jm_store_id");
        if(jm_store_id==null||"".equals(jm_store_id)){
        	jm_store_id="0";
        }
        if ((jm_login_role == null) || (jm_login_role.equals(""))) {
            jm_login_role = "user";
        }
        HttpSession session = request.getSession();
        session.setAttribute(Constent.WEB_TYPE_KEY, jm_view_type);
        session.setAttribute("jm_login_role", jm_login_role);
        session.setAttribute("jm_login",
            Boolean.valueOf(CommUtil.null2Boolean(request.getParameter("jm_login"))));
        session.setAttribute("jm_store_id",jm_store_id);//店铺id
        
        boolean flag = true;
        //登录时去掉验证码
       /* if (session.getAttribute("verify_number") != null) {
            String code = request.getParameter("code") != null ? request.getParameter("code")
                .toUpperCase() : "";
            if (!session.getAttribute("verify_number").equals(code)) {
                flag = false;
            }
        }*/
        if (!flag) {
            String username = obtainUsername(request);
            String password = "";

            if (!"".equals(CommUtil.null2String(obtainPassword(request)))
                && "mobile".equals(jm_view_type)) {
                password = obtainPassword(request);
            }
            username = username.trim();
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                username, password);
            if ((session != null) || (getAllowSessionCreation())) {
                request.getSession().setAttribute("SPRING_SECURITY_LAST_USERNAME",
                    TextUtils.escapeEntities(username));
            }
            setDetails(request, authRequest);
            return getAuthenticationManager().authenticate(authRequest);
        }
        String username = "";
        if (CommUtil.null2Boolean(request.getParameter("encode"))) {
            username = CommUtil.decode(obtainUsername(request)) + "," + jm_login_role+","+jm_store_id;
        } else {
            username = obtainUsername(request) + "," + jm_login_role+","+jm_store_id;
        }
        String password = obtainPassword(request);
        username = username.trim();
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
            username, password);
        if ((session != null) || (getAllowSessionCreation())) {
            request.getSession().setAttribute("SPRING_SECURITY_LAST_USERNAME",
                TextUtils.escapeEntities(username));

            // 是否注册后登录
            if (request.getParameter("is_reg") != null
                && "true".equals(request.getParameter("is_reg"))) {
                request.getSession().setAttribute("THE_DO_IS_REG", "true");
            }
            // 是否手机注册后登录
            if (request.getParameter("is_mobile") != null
                && "true".equals(request.getParameter("is_mobile"))) {
                request.getSession().setAttribute("THE_DO_IS_REG_MOBILE", "true");
            }
        }

        setDetails(request, authRequest);
        return getAuthenticationManager().authenticate(authRequest);
    }

    protected void onSuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              Authentication authResult) throws IOException {
        request.getSession(false).removeAttribute("verify_number");

        super.onSuccessfulAuthentication(request, response, authResult);
    }

    protected void onUnsuccessfulAuthentication(HttpServletRequest request,
                                                HttpServletResponse response,
                                                AuthenticationException failed) throws IOException {
        //        String uri = request.getRequestURI();
        super.onUnsuccessfulAuthentication(request, response, failed);
    }
}
