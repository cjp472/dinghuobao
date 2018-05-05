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
import com.javamalls.platform.domain.Complaint;
import com.javamalls.platform.domain.query.ComplaintQueryObject;
import com.javamalls.platform.service.IAccessoryService;
import com.javamalls.platform.service.IComplaintService;
import com.javamalls.platform.service.IComplaintSubjectService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IOrderFormService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

/**投诉管理
 *                       
 * @Filename: ComplaintSellerAction.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class H5ComplaintSellerAction {
    @Autowired
    private ISysConfigService        configService;
    @Autowired
    private IUserConfigService       userConfigService;
    @Autowired
    private IComplaintService        complaintService;
    @Autowired
    private IComplaintSubjectService complaintSubjectService;
    @Autowired
    private IOrderFormService        orderFormService;
    @Autowired
    private IGoodsService            goodsService;
    @Autowired
    private IAccessoryService        accessoryService;

    @SecurityMapping(title = "卖家被投诉列表", value = "/mobile/seller/complaint.htm*", rtype = "seller", rname = "投诉管理", rcode = "complaint_seller", rgroup = "客户服务")
    @RequestMapping({ "/mobile/seller/complaint.htm" })
    public ModelAndView complaint_seller(HttpServletRequest request, HttpServletResponse response,
                                         String currentPage, String status) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/seller_complaint.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        ComplaintQueryObject qo = new ComplaintQueryObject(currentPage, mv, "createtime", "desc");
        qo.addQuery("obj.to_user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser()
            .getId()), "=");
        if (!CommUtil.null2String(status).equals("")) {
            qo.addQuery("obj.status",
                new SysMap("status", Integer.valueOf(CommUtil.null2Int(status))), "=");
        } else {
            qo.addQuery("obj.status", new SysMap("status", Integer.valueOf(0)), ">=");
        }
        IPageList pList = this.complaintService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        mv.addObject("status", status);
        return mv;
    }

    @SecurityMapping(title = "卖家查看投诉详情", value = "/mobile/seller/complaint_view.htm*", rtype = "seller", rname = "投诉管理", rcode = "complaint_seller", rgroup = "客户服务")
    @RequestMapping({ "/mobile/seller/complaint_view.htm" })
    public ModelAndView complaint_view(HttpServletRequest request, HttpServletResponse response,
                                       String id) {
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/h5/seller_complaint_view.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Complaint obj = this.complaintService.getObjById(CommUtil.null2Long(id));
        if ((obj.getFrom_user().getId().equals(SecurityUserHolder.getCurrentUser().getId())) ||

        (obj.getTo_user().getId().equals(SecurityUserHolder.getCurrentUser().getId()))) {
            mv.addObject("obj", obj);
        } else {
            mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 0, request, response);
            mv.addObject("op_title", "参数错误，不存在该投诉");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/seller/complaint.htm");
        }
        return mv;
    }

}
