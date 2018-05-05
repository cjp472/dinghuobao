package com.javamalls.ctrl.admin.action;

import java.io.File;
import java.io.IOException;
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
import com.javamalls.base.tools.WebForm;
import com.javamalls.platform.domain.Accessory;
import com.javamalls.platform.domain.Advert;
import com.javamalls.platform.domain.AdvertPosition;
import com.javamalls.platform.domain.GoldLog;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.query.AdvertPositionQueryObject;
import com.javamalls.platform.domain.query.AdvertQueryObject;
import com.javamalls.platform.service.IAccessoryService;
import com.javamalls.platform.service.IAdvertPositionService;
import com.javamalls.platform.service.IAdvertService;
import com.javamalls.platform.service.IGoldLogService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;

/**广告管理
 *                       
 * @Filename: AdvertManageAction.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class AdvertManageAction {
    @Autowired
    private ISysConfigService      configService;
    @Autowired
    private IUserConfigService     userConfigService;
    @Autowired
    private IAdvertService         advertService;
    @Autowired
    private IAdvertPositionService advertPositionService;
    @Autowired
    private IAccessoryService      accessoryService;
    @Autowired
    private IUserService           userService;
    @Autowired
    private IGoldLogService        goldLogService;

    @SecurityMapping(title = "广告列表", value = "/admin/advert_list.htm*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
    @RequestMapping({ "/admin/advert_list.htm" })
    public ModelAndView advert_list(HttpServletRequest request, HttpServletResponse response,
                                    String currentPage, String orderBy, String orderType,
                                    String ad_title) {
        ModelAndView mv = new JModelAndView("admin/blue/advert_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        AdvertQueryObject qo = new AdvertQueryObject(currentPage, mv, orderBy, orderType);
        qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");
        if (!CommUtil.null2String(ad_title).equals("")) {
            qo.addQuery("obj.ad_title", new SysMap("ad_title", "%" + ad_title.trim() + "%"), "like");
        }
        IPageList pList = this.advertService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        mv.addObject("ad_title", ad_title);
        return mv;
    }

    @SecurityMapping(title = "待审批广告列表", value = "/admin/advert_list_audit.htm*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
    @RequestMapping({ "/admin/advert_list_audit.htm" })
    public ModelAndView advert_list_audit(HttpServletRequest request, HttpServletResponse response,
                                          String currentPage, String orderBy, String orderType,
                                          String ad_title) {
        ModelAndView mv = new JModelAndView("admin/blue/advert_list_audit.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        AdvertQueryObject qo = new AdvertQueryObject(currentPage, mv, orderBy, orderType);
        qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");
        if (!CommUtil.null2String(ad_title).equals("")) {
            qo.addQuery("obj.ad_title", new SysMap("ad_title", "%" + ad_title.trim() + "%"), "like");
        }
        qo.addQuery("obj.ad_status", new SysMap("ad_status", Integer.valueOf(0)), "=");
        IPageList pList = this.advertService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        mv.addObject("ad_title", ad_title);
        return mv;
    }

    @SecurityMapping(title = "广告增加", value = "/admin/advert_add.htm*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
    @RequestMapping({ "/admin/advert_add.htm" })
    public ModelAndView advert_add(HttpServletRequest request, HttpServletResponse response,
                                   String currentPage) {
        ModelAndView mv = new JModelAndView("admin/blue/advert_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        List<AdvertPosition> aps = this.advertPositionService.query(
            "select obj from AdvertPosition obj", null, -1, -1);
        mv.addObject("aps", aps);
        mv.addObject("currentPage", currentPage);
        return mv;
    }

    @SecurityMapping(title = "广告编辑", value = "/admin/advert_edit.htm*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
    @RequestMapping({ "/admin/advert_edit.htm" })
    public ModelAndView advert_edit(HttpServletRequest request, HttpServletResponse response,
                                    String id, String currentPage) {
        ModelAndView mv = new JModelAndView("admin/blue/advert_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if ((id != null) && (!id.equals(""))) {
            Advert advert = this.advertService.getObjById(Long.valueOf(Long.parseLong(id)));
            List<AdvertPosition> aps = this.advertPositionService.query(
                "select obj from AdvertPosition obj", null, -1, -1);
            mv.addObject("aps", aps);
            mv.addObject("obj", advert);
            mv.addObject("currentPage", currentPage);
            mv.addObject("edit", Boolean.valueOf(true));
        }
        return mv;
    }

    @SecurityMapping(title = "广告查看", value = "/admin/advert_view.htm*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
    @RequestMapping({ "/admin/advert_view.htm" })
    public ModelAndView advert_view(HttpServletRequest request, HttpServletResponse response,
                                    String id, String currentPage) {
        ModelAndView mv = new JModelAndView("admin/blue/advert_view.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if ((id != null) && (!id.equals(""))) {
            Advert advert = this.advertService.getObjById(Long.valueOf(Long.parseLong(id)));
            mv.addObject("obj", advert);
            mv.addObject("currentPage", currentPage);
        }
        return mv;
    }

    @SecurityMapping(title = "广告审核", value = "/admin/advert_audit.htm*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
    @RequestMapping({ "/admin/advert_audit.htm" })
    public ModelAndView advert_audit(HttpServletRequest request, HttpServletResponse response,
                                     String id, String ad_status, String currentPage) {
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Advert obj = this.advertService.getObjById(CommUtil.null2Long(id));
        obj.setAd_status(CommUtil.null2Int(ad_status));
        this.advertService.update(obj);
        if ((obj.getAd_status() == 1) && (obj.getAd_ap().getAp_show_type() == 0)) {
            AdvertPosition ap = obj.getAd_ap();
            ap.setAp_use_status(1);
            this.advertPositionService.update(ap);
        }
        if (obj.getAd_status() == -1) {
            User user = obj.getAd_user();
            user.setGold(user.getGold() + obj.getAd_gold());
            this.userService.update(user);
            GoldLog log = new GoldLog();
            log.setCreatetime(new Date());
            log.setGl_content("广告审核失败，恢复金币");
            log.setGl_count(obj.getAd_gold());
            log.setGl_user(obj.getAd_user());
            log.setGl_type(0);
            this.goldLogService.save(log);
        }
        mv.addObject("op_title", "广告审核成功");
        mv.addObject("list_url", CommUtil.getURL(request)
                                 + "/admin/advert_list_audit.htm?currentPage=" + currentPage);
        return mv;
    }

    @SecurityMapping(title = "广告保存", value = "/admin/advert_save.htm*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
    @RequestMapping({ "/admin/advert_save.htm" })
    public ModelAndView advert_save(HttpServletRequest request, HttpServletResponse response,
                                    String id, String ad_ap_id, String currentPage,
                                    String ad_begin_time, String ad_end_time) {
        WebForm wf = new WebForm();
        Advert advert = null;
        if (id.equals("")) {
            advert = (Advert) wf.toPo(request, Advert.class);
            advert.setCreatetime(new Date());
            advert.setAd_user(SecurityUserHolder.getCurrentUser());
        } else {
            Advert obj = this.advertService.getObjById(Long.valueOf(Long.parseLong(id)));
            advert = (Advert) wf.toPo(request, obj);
        }
        AdvertPosition ap = this.advertPositionService.getObjById(CommUtil.null2Long(ad_ap_id));
        advert.setAd_ap(ap);
        advert.setAd_begin_time(CommUtil.formatDate(ad_begin_time));
        advert.setAd_end_time(CommUtil.formatDate(ad_end_time));
        String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
        String saveFilePathName = request.getSession().getServletContext().getRealPath("/")
                                  + uploadFilePath + File.separator + "advert";
        Map map = new HashMap();
        String fileName = "";
        if (advert.getAd_acc() != null) {
            fileName = advert.getAd_acc().getName();
        }
        try {
            map = CommUtil.saveFileToServer(request, "acc", saveFilePathName, null, null);
            Accessory acc = null;
            if (fileName.equals("")) {
                if (map.get("fileName") != "") {
                    acc = new Accessory();
                    acc.setName(CommUtil.null2String(map.get("fileName")));
                    acc.setExt(CommUtil.null2String(map.get("mime")));
                    acc.setSize(CommUtil.null2Float(map.get("fileSize")));
                    acc.setPath(uploadFilePath + "/advert");
                    acc.setWidth(CommUtil.null2Int(map.get("width")));
                    acc.setHeight(CommUtil.null2Int(map.get("height")));
                    acc.setCreatetime(new Date());
                    this.accessoryService.save(acc);
                    advert.setAd_acc(acc);
                }
            } else if (map.get("fileName") != "") {
                acc = advert.getAd_acc();
                acc.setName(CommUtil.null2String(map.get("fileName")));
                acc.setExt(CommUtil.null2String(map.get("mime")));
                acc.setSize(CommUtil.null2Float(map.get("fileSize")));
                acc.setPath(uploadFilePath + "/advert");
                acc.setWidth(CommUtil.null2Int(map.get("width")));
                acc.setHeight(CommUtil.null2Int(map.get("height")));
                acc.setCreatetime(new Date());
                this.accessoryService.update(acc);
                advert.setAd_acc(acc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (id.equals("")) {
            this.advertService.save(advert);
        } else {
            this.advertService.update(advert);
        }
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("list_url", CommUtil.getURL(request) + "/admin/advert_list.htm?currentPage="
                                 + currentPage);
        mv.addObject("op_title", "保存广告成功");
        mv.addObject("add_url", CommUtil.getURL(request) + "/admin/advert_add.htm?currentPage="
                                + currentPage);
        return mv;
    }

    @SecurityMapping(title = "广告删除", value = "/admin/advert_del.htm*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
    @RequestMapping({ "/admin/advert_del.htm" })
    public String advert_del(HttpServletRequest request, HttpServletResponse response,
                             String mulitId, String currentPage) {
        String[] ids = mulitId.split(",");
        for (String id : ids) {
            if (!id.equals("")) {
                Advert advert = this.advertService.getObjById(Long.valueOf(Long.parseLong(id)));
                if (advert.getAd_status() != 1) {
                    CommUtil.del_acc(request, advert.getAd_acc());
                    this.advertService.delete(Long.valueOf(Long.parseLong(id)));
                }
            }
        }
        return "redirect:advert_list.htm?currentPage=" + currentPage;
    }

    @SecurityMapping(title = "广告位添加", value = "/admin/adv_pos_add.htm*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
    @RequestMapping({ "/admin/adv_pos_add.htm" })
    public ModelAndView adv_pos_add(HttpServletRequest request, HttpServletResponse response,
                                    String id, String currentPage) {
        ModelAndView mv = new JModelAndView("admin/blue/adv_pos_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        List<Advert> advs = this.advertService.query("select obj from Advert obj", null, -1, -1);
        mv.addObject("advs", advs);
        return mv;
    }

    @SecurityMapping(title = "广告位保存", value = "/admin/adv_pos_save.htm*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
    @RequestMapping({ "/admin/adv_pos_save.htm" })
    public ModelAndView adv_pos_save(HttpServletRequest request, HttpServletResponse response,
                                     String id, String currentPage, String cmd, String list_url,
                                     String add_url) {
        WebForm wf = new WebForm();
        AdvertPosition ap = null;
        if (id.equals("")) {
            ap = (AdvertPosition) wf.toPo(request, AdvertPosition.class);
            ap.setCreatetime(new Date());
        } else {
            AdvertPosition obj = this.advertPositionService.getObjById(Long.valueOf(Long
                .parseLong(id)));
            ap = (AdvertPosition) wf.toPo(request, obj);
        }
        String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
        String saveFilePathName = request.getSession().getServletContext().getRealPath("/")
                                  + uploadFilePath + File.separator + "advert";
        Map map = new HashMap();
        String fileName = "";
        if (ap.getAp_acc() != null) {
            fileName = ap.getAp_acc().getName();
        }
        try {
            map = CommUtil.saveFileToServer(request, "acc", saveFilePathName, fileName, null);
            Accessory acc = null;
            if (fileName.equals("")) {
                if (map.get("fileName") != "") {
                    acc = new Accessory();
                    acc.setName(CommUtil.null2String(map.get("fileName")));
                    acc.setExt(CommUtil.null2String(map.get("mime")));
                    acc.setSize(CommUtil.null2Float(map.get("fileSize")));
                    acc.setPath(uploadFilePath + "/advert");
                    acc.setWidth(CommUtil.null2Int(map.get("width")));
                    acc.setHeight(CommUtil.null2Int(map.get("height")));
                    acc.setCreatetime(new Date());
                    this.accessoryService.save(acc);
                    ap.setAp_acc(acc);
                }
            } else if (map.get("fileName") != "") {
                acc = ap.getAp_acc();
                acc.setName(CommUtil.null2String(map.get("fileName")));
                acc.setExt(CommUtil.null2String(map.get("mime")));
                acc.setSize(CommUtil.null2Float(map.get("fileSize")));
                acc.setPath(uploadFilePath + "/advert");
                acc.setWidth(CommUtil.null2Int(map.get("width")));
                acc.setHeight(CommUtil.null2Int(map.get("height")));
                acc.setCreatetime(new Date());
                this.accessoryService.update(acc);
                ap.setAp_acc(acc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (id.equals("")) {
            this.advertPositionService.save(ap);
        } else {
            this.advertPositionService.update(ap);
        }
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("list_url", list_url);
        mv.addObject("op_title", "保存广告位成功");
        if (add_url != null) {
            mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
        }
        return mv;
    }

    @SecurityMapping(title = "广告位列表", value = "/admin/adv_pos_list.htm*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
    @RequestMapping({ "/admin/adv_pos_list.htm" })
    public ModelAndView adv_pos_list(HttpServletRequest request, HttpServletResponse response,
                                     String currentPage, String orderBy, String orderType,
                                     String ap_title) {
        ModelAndView mv = new JModelAndView("admin/blue/adv_pos_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        AdvertPositionQueryObject qo = new AdvertPositionQueryObject(currentPage, mv, orderBy,
            orderType);
        
        qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");
        if (!CommUtil.null2String(ap_title).equals("")) {
            qo.addQuery("obj.ap_title", new SysMap("ap_title", "%" + ap_title + "%"), "like");
        }
        IPageList pList = this.advertPositionService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        mv.addObject("ap_title", ap_title);
        return mv;
    }

    @SecurityMapping(title = "广告位编辑", value = "/admin/adv_pos_edit.htm*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
    @RequestMapping({ "/admin/adv_pos_edit.htm" })
    public ModelAndView adv_pos_edit(HttpServletRequest request, HttpServletResponse response,
                                     String id, String currentPage) {
        ModelAndView mv = new JModelAndView("admin/blue/adv_pos_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if ((id != null) && (!id.equals(""))) {
            AdvertPosition obj = this.advertPositionService.getObjById(Long.valueOf(Long
                .parseLong(id)));
            mv.addObject("obj", obj);
            mv.addObject("currentPage", currentPage);
            mv.addObject("edit", Boolean.valueOf(true));
        }
        return mv;
    }

    @SecurityMapping(title = "广告位删除", value = "/admin/adv_pos_del.htm*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
    @RequestMapping({ "/admin/adv_pos_del.htm" })
    public String adv_pos_del(HttpServletRequest request, HttpServletResponse response,
                              String mulitId, String currentPage) {
        String[] ids = mulitId.split(",");
        for (String id : ids) {
            if (!id.equals("")) {
                AdvertPosition ap = this.advertPositionService.getObjById(Long.valueOf(Long
                    .parseLong(id)));
                if (ap.getAp_sys_type() != 0) {
                    CommUtil.del_acc(request, ap.getAp_acc());
                    this.advertPositionService.delete(Long.valueOf(Long.parseLong(id)));
                }
            }
        }
        return "redirect:adv_pos_list.htm?currentPage=" + currentPage;
    }
}
