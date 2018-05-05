package com.javamalls.ctrl.admin.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.domain.Consult;
import com.javamalls.platform.domain.query.ConsultQueryObject;
import com.javamalls.platform.service.IConsultService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

/**咨询管理
 *                       
 * @Filename: ConsultManageAction.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class ConsultManageAction {
    @Autowired
    private ISysConfigService  configService;
    @Autowired
    private IUserConfigService userConfigService;
    @Autowired
    private IConsultService    consultService;

    @SecurityMapping(title = "咨询列表", value = "/admin/consult_list.htm*", rtype = "admin", rname = "咨询管理", rcode = "consult_admin", rgroup = "交易")
    @RequestMapping({ "/admin/consult_list.htm" })
    public ModelAndView list(HttpServletRequest request, HttpServletResponse response,
                             String currentPage, String orderBy, String orderType,
                             String consult_user_userName, String consult_content) {
        ModelAndView mv = new JModelAndView("admin/blue/consult_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        String params = "";
        ConsultQueryObject qo = new ConsultQueryObject(currentPage, mv, orderBy, orderType);
        qo.setPageSize(Integer.valueOf(1));
        if ((consult_user_userName != null) && (!consult_user_userName.equals(""))) {
            qo.addQuery("obj.consult_user.userName",
                new SysMap("userName", CommUtil.null2String(consult_user_userName).trim()), "=");
        }
        if ((consult_content != null) && (!consult_content.equals(""))) {
            qo.addQuery("obj.consult_content", new SysMap("consult_content", "%" + consult_content
                                                                             + "%"), "like");
        }
        IPageList pList = this.consultService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/admin/consult_list.htm", "", params, pList, mv);
        mv.addObject("consult_user_userName", consult_user_userName);
        mv.addObject("consult_content", consult_content);
        return mv;
    }

    @SecurityMapping(title = "咨询删除", value = "/admin/consult_del.htm*", rtype = "admin", rname = "咨询管理", rcode = "consult_admin", rgroup = "交易")
    @RequestMapping({ "/admin/consult_del.htm" })
    public String delete(HttpServletRequest request, HttpServletResponse response, String mulitId,
                         String currentPage) {
        String[] ids = mulitId.split(",");
        for (String id : ids) {
            if (!id.equals("")) {
                Consult consult = this.consultService.getObjById(Long.valueOf(Long.parseLong(id)));
                if (consult != null)
                    this.consultService.delete(Long.valueOf(Long.parseLong(id)));
            }
        }
        return "redirect:consult_list.htm?currentPage=" + currentPage;
    }
}
