package com.javamalls.ctrl.seller.action;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.javamalls.platform.domain.Accessory;
import com.javamalls.platform.domain.Complaint;
import com.javamalls.platform.domain.ComplaintSubject;
import com.javamalls.platform.domain.OrderForm;
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
public class ComplaintSellerAction {
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

    @SecurityMapping(title = "卖家投诉发起", value = "/seller/complaint_handle.htm*", rtype = "seller", rname = "投诉管理", rcode = "complaint_seller", rgroup = "客户服务")
    @RequestMapping({ "/seller/complaint_handle.htm" })
    public ModelAndView complaint_handle(HttpServletRequest request, HttpServletResponse response,
                                         String order_id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/complaint_handle.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
        Calendar calendar = Calendar.getInstance();
        calendar.add(6, -this.configService.getSysConfig().getComplaint_time());
        boolean result = true;
        if ((of.getOrder_status() == 60) && (of.getFinishTime().before(calendar.getTime()))) {
            result = false;
        }
        boolean result1 = true;
        if (of.getComplaints().size() > 0) {
            for (Complaint complaint : of.getComplaints()) {
                if (complaint.getFrom_user().getId()
                    .equals(SecurityUserHolder.getCurrentUser().getId())) {
                    result1 = false;
                }
            }
        }
        if (result) {
            if (result1) {
                Complaint obj = new Complaint();
                obj.setFrom_user(SecurityUserHolder.getCurrentUser());
                obj.setStatus(0);
                obj.setType("seller");
                obj.setOf(of);
                obj.setTo_user(of.getUser());
                mv.addObject("obj", obj);
                Object params = new HashMap();
                ((Map) params).put("type", "seller");
                List<ComplaintSubject> css = this.complaintSubjectService.query(
                    "select obj from ComplaintSubject obj where obj.type=:type", (Map) params, -1,
                    -1);
                mv.addObject("css", css);
            } else {
                mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "该订单已经投诉，不允许重复投诉");
                mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
            }
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "该订单已经超过投诉有效期，不能投诉");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "卖家被投诉列表", value = "/seller/complaint.htm*", rtype = "seller", rname = "投诉管理", rcode = "complaint_seller", rgroup = "客户服务")
    @RequestMapping({ "/seller/complaint.htm" })
    public ModelAndView complaint_seller(HttpServletRequest request, HttpServletResponse response,
                                         String currentPage, String status) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller_complaint.html",
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

    @SecurityMapping(title = "卖家查看投诉详情", value = "/seller/complaint_view.htm*", rtype = "seller", rname = "投诉管理", rcode = "complaint_seller", rgroup = "客户服务")
    @RequestMapping({ "/seller/complaint_view.htm" })
    public ModelAndView complaint_view(HttpServletRequest request, HttpServletResponse response,
                                       String id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller_complaint_view.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Complaint obj = this.complaintService.getObjById(CommUtil.null2Long(id));
        if ((obj.getFrom_user().getId().equals(SecurityUserHolder.getCurrentUser().getId())) ||

        (obj.getTo_user().getId().equals(SecurityUserHolder.getCurrentUser().getId()))) {
            mv.addObject("obj", obj);
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "参数错误，不存在该投诉");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/complaint.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "卖家查看投诉详情", value = "/seller/complaint_appeal.htm*", rtype = "seller", rname = "投诉管理", rcode = "complaint_seller", rgroup = "客户服务")
    @RequestMapping({ "/seller/complaint_appeal.htm" })
    public ModelAndView complaint_appeal(HttpServletRequest request, HttpServletResponse response,
                                         String id, String to_user_content) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Complaint obj = this.complaintService.getObjById(CommUtil.null2Long(id));
        obj.setStatus(2);
        obj.setTo_user_content(to_user_content);
        obj.setAppeal_time(new Date());
        String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
        String saveFilePathName = request.getSession().getServletContext().getRealPath("/")
                                  + uploadFilePath + File.separator + "complaint";
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            map = CommUtil.saveFileToServer(request, "img1", saveFilePathName, null, null);
            if (map.get("fileName") != "") {
                Accessory to_acc1 = new Accessory();
                to_acc1.setName(CommUtil.null2String(map.get("fileName")));
                to_acc1.setExt(CommUtil.null2String(map.get("mime")));
                to_acc1.setSize(CommUtil.null2Float(map.get("fileSize")));
                to_acc1.setPath(uploadFilePath + "/complaint");
                to_acc1.setWidth(CommUtil.null2Int(map.get("width")));
                to_acc1.setHeight(CommUtil.null2Int(map.get("height")));
                to_acc1.setCreatetime(new Date());
                this.accessoryService.save(to_acc1);
                obj.setTo_acc1(to_acc1);
            }
            map.clear();
            map = CommUtil.saveFileToServer(request, "img2", saveFilePathName, null, null);
            if (map.get("fileName") != "") {
                Accessory to_acc2 = new Accessory();
                to_acc2.setName(CommUtil.null2String(map.get("fileName")));
                to_acc2.setExt(CommUtil.null2String(map.get("mime")));
                to_acc2.setSize(CommUtil.null2Float(map.get("fileSize")));
                to_acc2.setPath(uploadFilePath + "/complaint");
                to_acc2.setWidth(CommUtil.null2Int(map.get("width")));
                to_acc2.setHeight(CommUtil.null2Int(map.get("height")));
                to_acc2.setCreatetime(new Date());
                this.accessoryService.save(to_acc2);
                obj.setTo_acc2(to_acc2);
            }
            map.clear();
            map = CommUtil.saveFileToServer(request, "img3", saveFilePathName, null, null);
            if (map.get("fileName") != "") {
                Accessory to_acc3 = new Accessory();
                to_acc3.setName(CommUtil.null2String(map.get("fileName")));
                to_acc3.setExt(CommUtil.null2String(map.get("mime")));
                to_acc3.setSize(CommUtil.null2Float(map.get("fileSize")));
                to_acc3.setPath(uploadFilePath + "/complaint");
                to_acc3.setWidth(CommUtil.null2Int(map.get("width")));
                to_acc3.setHeight(CommUtil.null2Int(map.get("height")));
                to_acc3.setCreatetime(new Date());
                this.accessoryService.save(to_acc3);
                obj.setTo_acc3(to_acc3);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.complaintService.update(obj);
        mv.addObject("op_title", "申诉提交成功");
        mv.addObject("url", CommUtil.getURL(request) + "/seller/complaint.htm");
        return mv;
    }
}
