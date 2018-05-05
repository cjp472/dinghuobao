package com.javamalls.ctrl.h5.buyer.action;

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
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.domain.Report;
import com.javamalls.platform.domain.query.ReportQueryObject;
import com.javamalls.platform.service.IAccessoryService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IReportService;
import com.javamalls.platform.service.IReportSubjectService;
import com.javamalls.platform.service.IReportTypeService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;

/**举报管理
 *                       
 * @Filename: ReportBuyerAction.java
 * @Version: 2.7.0
 * @Author: 刘杰
 * @Email: attay125@163.com
 *
 */
@Controller
public class H5ReportBuyerAction {
    @Autowired
    private ISysConfigService     configService;
    @Autowired
    private IUserConfigService    userConfigService;
    @Autowired
    private IGoodsService         goodsService;
    @Autowired
    private IReportTypeService    reportTypeService;
    @Autowired
    private IReportSubjectService reportSubjectService;
    @Autowired
    private IReportService        reportService;
    @Autowired
    private IAccessoryService     accessoryService;
    @Autowired
    private IUserService          userService;

    @SecurityMapping(title = "买家举报列表", value = "/mobile/buyer/report.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/mobile/buyer/report.htm" })
    public ModelAndView report(HttpServletRequest request, HttpServletResponse response,
                               String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/report.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        ReportQueryObject rqo = new ReportQueryObject(currentPage, mv, null, null);
        rqo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser()
            .getId()), "=");
        IPageList pList = this.reportService.list(rqo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        return mv;
    }

    @SecurityMapping(title = "买家举报详情", value = "/mobile/buyer/report_view.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/mobile/buyer/report_view.htm" })
    public ModelAndView report_view(HttpServletRequest request, HttpServletResponse response,
                                    String id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/report_view.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Report obj = this.reportService.getObjById(CommUtil.null2Long(id));
        mv.addObject("obj", obj);
        return mv;
    }

    @SecurityMapping(title = "买家取消举报", value = "/mobile/buyer/report_cancel.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/mobile/buyer/report_cancel.htm" })
    public String report_cancel(HttpServletRequest request, HttpServletResponse response,
                                String id, String currentPage) {
        Report obj = this.reportService.getObjById(CommUtil.null2Long(id));
        obj.setStatus(-1);
        this.reportService.update(obj);
        return "redirect:/mobile/buyer/report.htm?currentPage=" + currentPage;
    }
}
