package com.javamalls.ctrl.h5.buyer.action;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
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
import com.javamalls.platform.domain.ComplaintGoods;
import com.javamalls.platform.domain.ComplaintSubject;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.OrderForm;
import com.javamalls.platform.domain.query.ComplaintQueryObject;
import com.javamalls.platform.service.IAccessoryService;
import com.javamalls.platform.service.IComplaintService;
import com.javamalls.platform.service.IComplaintSubjectService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IOrderFormService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;

/**投诉
 *                       
 * @Filename: ComplaintBuyerAction.java
 * @Version: 2.7.0
 * @Author: 刘杰
 * @Email: attay125@163.com
 *
 */
@Controller
public class H5ComplaintBuyerAction {
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
    @Autowired
    private IUserService             userService;

    //发起投诉
    @SecurityMapping(title = "买家投诉列表", value = "/mobile/buyer/complaint.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/mobile/buyer/complaint.htm" })
    public ModelAndView complaint(HttpServletRequest request, HttpServletResponse response,
                                  String currentPage, String status) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/buyer_complaint.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        ComplaintQueryObject qo = new ComplaintQueryObject(currentPage, mv, "createtime", "desc");
        qo.addQuery("obj.from_user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser()
            .getId()), "=");
        if (!CommUtil.null2String(status).equals("")) {
            qo.addQuery("obj.status",
                new SysMap("status", Integer.valueOf(CommUtil.null2Int(status))), "=");
        }
        IPageList pList = this.complaintService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        mv.addObject("status", status);
        return mv;
    }

    @SecurityMapping(title = "买家取消投诉", value = "/mobile/buyer/complaint_cancel.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/mobile/buyer/complaint_cancel.htm" })
    public String complaint_cancel(HttpServletRequest request, HttpServletResponse response,
                                   String id, String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        this.complaintService.delete(CommUtil.null2Long(id));

        return "redirect:/mobile/buyer/complaint.htm?currentPage=" + currentPage;
    }

    @SecurityMapping(title = "买家查看投诉详情", value = "/mobile/buyer/complaint_view.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/mobile/buyer/complaint_view.htm" })
    public ModelAndView complaint_view(HttpServletRequest request, HttpServletResponse response,
                                       String id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/complaint_view.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Complaint obj = this.complaintService.getObjById(CommUtil.null2Long(id));
        if ((obj.getFrom_user().getId().equals(SecurityUserHolder.getCurrentUser().getId())) ||

        (obj.getTo_user().getId().equals(SecurityUserHolder.getCurrentUser().getId()))) {
            mv.addObject("obj", obj);
        } else {
            mv = new JModelAndView("user/default/usercenter/h5/error.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
                request, response);
            mv.addObject("op_title", "参数错误，不存在该投诉");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/complaint.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "投诉图片", value = "/mobile/buyer/complaint_img.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/mobile/buyer/complaint_img.htm" })
    public ModelAndView complaint_img(HttpServletRequest request, HttpServletResponse response,
                                      String id, String type) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/complaint_img.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Complaint obj = this.complaintService.getObjById(CommUtil.null2Long(id));
        mv.addObject("type", type);
        mv.addObject("obj", obj);
        return mv;
    }

    @SecurityMapping(title = "买家投诉列表", value = "/mobile/buyer/complaint_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/mobile/buyer/complaint_save.htm" })
    public String complaint_save(HttpServletRequest request, HttpServletResponse response,
                                 String order_id, String cs_id, String from_user_content,
                                 String goods_ids, String to_user_id, String type) {
        Complaint obj = new Complaint();
        obj.setCreatetime(new Date());
        ComplaintSubject cs = this.complaintSubjectService.getObjById(CommUtil.null2Long(cs_id));
        OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
        obj.setCs(cs);
        obj.setFrom_user_content(from_user_content);
        obj.setFrom_user(SecurityUserHolder.getCurrentUser());
        obj.setTo_user(this.userService.getObjById(CommUtil.null2Long(to_user_id)));
        obj.setType(type);
        obj.setOf(of);
        String[] goods_id_list = goods_ids.split(",");
        for (String goods_id : goods_id_list) {
            Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
            ComplaintGoods cg = new ComplaintGoods();
            cg.setCreatetime(new Date());
            cg.setComplaint(obj);
            cg.setGoods(goods);
            cg.setContent(CommUtil.null2String(request.getParameter("content_" + goods_id)));
            obj.getCgs().add(cg);
        }
        String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
        String saveFilePathName = request.getSession().getServletContext().getRealPath("/")
                                  + uploadFilePath + File.separator + "complaint";
        Object map = new HashMap();
        try {
            map = CommUtil.saveFileToServer(request, "img1", saveFilePathName, null, null);
            if (((Map) map).get("fileName") != "") {
                Accessory from_acc1 = new Accessory();
                from_acc1.setName(CommUtil.null2String(((Map) map).get("fileName")));
                from_acc1.setExt(CommUtil.null2String(((Map) map).get("mime")));
                from_acc1.setSize(CommUtil.null2Float(((Map) map).get("fileSize")));
                from_acc1.setPath(uploadFilePath + "/complaint");
                from_acc1.setWidth(CommUtil.null2Int(((Map) map).get("width")));
                from_acc1.setHeight(CommUtil.null2Int(((Map) map).get("height")));
                from_acc1.setCreatetime(new Date());
                this.accessoryService.save(from_acc1);
                obj.setFrom_acc1(from_acc1);
            }
            ((Map) map).clear();
            map = CommUtil.saveFileToServer(request, "img2", saveFilePathName, null, null);
            if (((Map) map).get("fileName") != "") {
                Accessory from_acc2 = new Accessory();
                from_acc2.setName(CommUtil.null2String(((Map) map).get("fileName")));
                from_acc2.setExt(CommUtil.null2String(((Map) map).get("mime")));
                from_acc2.setSize(CommUtil.null2Float(((Map) map).get("fileSize")));
                from_acc2.setPath(uploadFilePath + "/complaint");
                from_acc2.setWidth(CommUtil.null2Int(((Map) map).get("width")));
                from_acc2.setHeight(CommUtil.null2Int(((Map) map).get("height")));
                from_acc2.setCreatetime(new Date());
                this.accessoryService.save(from_acc2);
                obj.setFrom_acc2(from_acc2);
            }
            ((Map) map).clear();
            map = CommUtil.saveFileToServer(request, "img3", saveFilePathName, null, null);
            if (((Map) map).get("fileName") != "") {
                Accessory from_acc3 = new Accessory();
                from_acc3.setName(CommUtil.null2String(((Map) map).get("fileName")));
                from_acc3.setExt(CommUtil.null2String(((Map) map).get("mime")));
                from_acc3.setSize(CommUtil.null2Float(((Map) map).get("fileSize")));
                from_acc3.setPath(uploadFilePath + "/complaint");
                from_acc3.setWidth(CommUtil.null2Int(((Map) map).get("width")));
                from_acc3.setHeight(CommUtil.null2Int(((Map) map).get("height")));
                from_acc3.setCreatetime(new Date());
                this.accessoryService.save(from_acc3);
                obj.setFrom_acc3(from_acc3);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.complaintService.save(obj);
        return "redirect:/mobile/buyer/complaint.htm";
    }

}
