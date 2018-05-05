package com.javamalls.ctrl.admin.action;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.WebForm;
import com.javamalls.platform.domain.ReportSubject;
import com.javamalls.platform.domain.ReportType;
import com.javamalls.platform.domain.query.ReportSubjectQueryObject;
import com.javamalls.platform.service.IReportSubjectService;
import com.javamalls.platform.service.IReportTypeService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

@Controller
public class ReportSubjectManageAction {
    @Autowired
    private ISysConfigService     configService;
    @Autowired
    private IUserConfigService    userConfigService;
    @Autowired
    private IReportSubjectService reportsubjectService;
    @Autowired
    private IReportTypeService    reportTypeService;

    @SecurityMapping(title = "举报主题列表", value = "/admin/reportsubject_list.htm*", rtype = "admin", rname = "举报管理", rcode = "report_manage", rgroup = "交易")
    @RequestMapping({ "/admin/reportsubject_list.htm" })
    public ModelAndView list(HttpServletRequest request, HttpServletResponse response,
                             String currentPage, String orderBy, String orderType) {
        ModelAndView mv = new JModelAndView("admin/blue/reportsubject_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        String params = "";
        ReportSubjectQueryObject qo = new ReportSubjectQueryObject(currentPage, mv, orderBy,
            orderType);
        WebForm wf = new WebForm();
        wf.toQueryPo(request, qo, ReportSubject.class, mv);
        IPageList pList = this.reportsubjectService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/admin/reportsubject_list.htm", "", params,
            pList, mv);
        return mv;
    }

    @SecurityMapping(title = "举报主题增加", value = "/admin/reportsubject_add.htm*", rtype = "admin", rname = "举报管理", rcode = "report_manage", rgroup = "交易")
    @RequestMapping({ "/admin/reportsubject_add.htm" })
    public ModelAndView add(HttpServletRequest request, HttpServletResponse response,
                            String currentPage) {
        ModelAndView mv = new JModelAndView("admin/blue/reportsubject_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        List<ReportType> types = this.reportTypeService.query(
            "select obj from ReportType obj order by obj.createtime desc", null, -1, -1);
        mv.addObject("types", types);
        mv.addObject("currentPage", currentPage);
        return mv;
    }

    @SecurityMapping(title = "举报主题编辑", value = "/admin/reportsubject_edit.htm*", rtype = "admin", rname = "举报管理", rcode = "report_manage", rgroup = "交易")
    @RequestMapping({ "/admin/reportsubject_edit.htm" })
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response, String id,
                             String currentPage) {
        ModelAndView mv = new JModelAndView("admin/blue/reportsubject_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if ((id != null) && (!id.equals(""))) {
            ReportSubject reportsubject = this.reportsubjectService.getObjById(Long.valueOf(Long
                .parseLong(id)));
            List<ReportType> types = this.reportTypeService.query(
                "select obj from ReportType obj order by obj.createtime desc", null, -1, -1);
            mv.addObject("types", types);
            mv.addObject("obj", reportsubject);
            mv.addObject("currentPage", currentPage);
            mv.addObject("edit", Boolean.valueOf(true));
        }
        return mv;
    }

    @SecurityMapping(title = "举报主题保存", value = "/admin/reportsubject_save.htm*", rtype = "admin", rname = "举报管理", rcode = "report_manage", rgroup = "交易")
    @RequestMapping({ "/admin/reportsubject_save.htm" })
    public ModelAndView save(HttpServletRequest request, HttpServletResponse response, String id,
                             String currentPage, String cmd, String list_url, String add_url,
                             String type_id) {
        WebForm wf = new WebForm();
        ReportSubject reportsubject = null;
        if (id.equals("")) {
            reportsubject = (ReportSubject) wf.toPo(request, ReportSubject.class);
            reportsubject.setCreatetime(new Date());
        } else {
            ReportSubject obj = this.reportsubjectService.getObjById(Long.valueOf(Long
                .parseLong(id)));
            reportsubject = (ReportSubject) wf.toPo(request, obj);
        }
        ReportType type = this.reportTypeService.getObjById(CommUtil.null2Long(type_id));
        reportsubject.setType(type);
        if (id.equals("")) {
            this.reportsubjectService.save(reportsubject);
        } else {
            this.reportsubjectService.update(reportsubject);
        }
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("list_url", list_url);
        mv.addObject("op_title", "保存举报主题成功");
        if (add_url != null) {
            mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
        }
        return mv;
    }

    @SecurityMapping(title = "举报主题删除", value = "/admin/reportsubject_del.htm*", rtype = "admin", rname = "举报管理", rcode = "report_manage", rgroup = "交易")
    @RequestMapping({ "/admin/reportsubject_del.htm" })
    public String delete(HttpServletRequest request, HttpServletResponse response, String mulitId,
                         String currentPage) {
        String[] ids = mulitId.split(",");
        for (String id : ids) {
            if (!id.equals("")) {
                ReportSubject reportsubject = this.reportsubjectService.getObjById(Long
                    .valueOf(Long.parseLong(id)));
                this.reportsubjectService.delete(Long.valueOf(Long.parseLong(id)));
            }
        }
        return "redirect:reportsubject_list.htm?currentPage=" + currentPage;
    }
}
