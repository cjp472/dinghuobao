package com.javamalls.ctrl.seller.action;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.constant.Constant;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.FileUtil;
import com.javamalls.base.tools.QRCodeEncoderHandler;
import com.javamalls.base.tools.WebForm;
import com.javamalls.front.web.tools.AreaViewTools;
import com.javamalls.front.web.tools.StoreViewTools;
import com.javamalls.payment.chinabank.h5.util.JsonUtil;
import com.javamalls.platform.domain.Accessory;
import com.javamalls.platform.domain.Area;
import com.javamalls.platform.domain.Role;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.StoreClass;
import com.javamalls.platform.domain.StoreGrade;
import com.javamalls.platform.domain.StoreGradeLog;
import com.javamalls.platform.domain.StoreSlide;
import com.javamalls.platform.domain.SysConfig;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.Warehouse;
import com.javamalls.platform.service.IAccessoryService;
import com.javamalls.platform.service.IAreaService;
import com.javamalls.platform.service.IRoleService;
import com.javamalls.platform.service.IStoreClassService;
import com.javamalls.platform.service.IStoreGradeLogService;
import com.javamalls.platform.service.IStoreGradeService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.IStoreSlideService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;
import com.javamalls.platform.service.IWarehouseService;
import com.utils.SendReqAsync;

@Controller
public class StoreSellerAction {
    @Autowired
    private ISysConfigService     configService;
    @Autowired
    private IUserConfigService    userConfigService;
    @Autowired
    private IStoreGradeService    storeGradeService;
    @Autowired
    private IAreaService          areaService;
    @Autowired
    private IStoreClassService    storeClassService;
    @Autowired
    private IStoreService         storeService;
    @Autowired
    private IUserService          userService;
    @Autowired
    private IRoleService          roleService;
    @Autowired
    private IAccessoryService     accessoryService;
    @Autowired
    private IStoreGradeLogService storeGradeLogService;
    @Autowired
    private IStoreSlideService    storeSlideService;
    @Autowired
    private StoreViewTools        storeTools;
    @Autowired
    private AreaViewTools         areaViewTools;
    @Autowired
    private IWarehouseService     warehouseService;
    @Autowired
    private SendReqAsync          sendReqAsync;

    @SecurityMapping(title = "申请店铺第一步", value = "/seller/store_create_first.htm*", rtype = "buyer", rname = "申请店铺", rcode = "create_store", rgroup = "申请店铺")
    @RequestMapping({ "/seller/store_create_first.htm" })
    public ModelAndView store_create_first(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = null;
        int store_status = 0;
        Store store = null;
        if (SecurityUserHolder.getCurrentUser().getStore() != null) {
            store = this.storeService.getObjByProperty("id", SecurityUserHolder.getCurrentUser()
                .getStore().getId());
        }
        if (store != null) {
            store_status = store.getStore_status();
        }
        if (this.configService.getSysConfig().isStore_allow()) {
            if (store_status == 0) {
                mv = new JModelAndView("store_create_first.html",
                    this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
                    request, response);
                List<StoreGrade> sgs = this.storeGradeService.query(
                    "select obj from StoreGrade obj order by obj.sequence asc", null, -1, -1);
                mv.addObject("sgs", sgs);
                mv.addObject("storeTools", this.storeTools);
            }
            if (store_status == 1) {
                mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "您的店铺正在审核中");
                mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
            }
            if (store_status == 2) {
                mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "您已经开通店铺");
                mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
            }
            if (store_status == 3) {
                mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "您的店铺已经被关闭");
                mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
            }
            if (store_status == 5) {
                mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "您的店铺还未开通");
                mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
            }
            if (store_status == -1) {
                mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "您的店铺未通过审核");
                mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
            }
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "系统暂时关闭了申请店铺");
            mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "申请店铺第二步", value = "/seller/store_create_second.htm*", rtype = "buyer", rname = "申请店铺", rcode = "create_store", rgroup = "申请店铺")
    @RequestMapping({ "/seller/store_create_second.htm" })
    public ModelAndView store_create_second(HttpServletRequest request,
                                            HttpServletResponse response, String grade_id) {
        ModelAndView mv = null;
        Store store = null;
        if (SecurityUserHolder.getCurrentUser().getStore() != null) {
            store = this.storeService.getObjByProperty("id", SecurityUserHolder.getCurrentUser()
                .getStore().getId());
        }
        int store_status = store == null ? 0 : store.getStore_status();
        if (this.configService.getSysConfig().isStore_allow()) {
            if ((grade_id == null) || (grade_id.equals(""))) {
                mv = new JModelAndView("store_create_first.html",
                    this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
                    request, response);
                List<StoreGrade> sgs = this.storeGradeService.query(
                    "select obj from StoreGrade obj order by obj.sequence asc", null, -1, -1);
                mv.addObject("sgs", sgs);
                mv.addObject("storeTools", this.storeTools);
            } else {
                if (store_status == 0) {
                    mv = new JModelAndView("store_create_second.html",
                        this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
                        1, request, response);
                    List<Area> areas = this.areaService.query(
                        "select obj from Area obj where obj.parent.id is null", null, -1, -1);
                    List<StoreClass> scs = this.storeClassService.query(
                        "select obj from StoreClass obj where obj.parent.id is null", null, -1, -1);
                    String store_create_session = CommUtil.randomString(32);
                    request.getSession(false).setAttribute("store_create_session",
                        store_create_session);
                    mv.addObject("store_create_session", store_create_session);
                    mv.addObject("scs", scs);
                    mv.addObject("areas", areas);
                    mv.addObject("grade_id", grade_id);
                }
                if (store_status == 1) {
                    mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                        this.userConfigService.getUserConfig(), 1, request, response);
                    mv.addObject("op_title", "您的店铺正在审核中");
                    mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
                }
                if (store_status == 2) {
                    mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                        this.userConfigService.getUserConfig(), 1, request, response);
                    mv.addObject("op_title", "您已经开通店铺");
                    mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
                }
                if (store_status == 3) {
                    mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                        this.userConfigService.getUserConfig(), 1, request, response);
                    mv.addObject("op_title", "您的店铺已经被关闭");
                    mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
                }
                if (store_status == 5) {
                    mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                        this.userConfigService.getUserConfig(), 1, request, response);
                    mv.addObject("op_title", "您的店铺还未开通");
                    mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
                }
                if (store_status == -1) {
                    mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                        this.userConfigService.getUserConfig(), 1, request, response);
                    mv.addObject("op_title", "您的店铺未通过审核");
                    mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
                }
            }
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "系统暂时关闭了申请店铺");
            mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "申请店铺完成", value = "/seller/store_create_finish.htm*", rtype = "buyer", rname = "申请店铺", rcode = "create_store", rgroup = "申请店铺")
    @RequestMapping({ "/seller/store_create_finish.htm" })
    public ModelAndView store_create_finish(HttpServletRequest request,
                                            HttpServletResponse response, String sc_id,
                                            String grade_id, String area_id,
                                            String store_create_session, String isrecommend) {
        ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        String store_create_session1 = CommUtil.null2String(request.getSession(false).getAttribute(
            "store_create_session"));
        if ((!store_create_session1.equals(""))
            && (store_create_session1.equals(store_create_session))) {

            Store user_store = null;
            if (SecurityUserHolder.getCurrentUser().getStore() != null) {
                user_store = this.storeService.getObjByProperty("id", SecurityUserHolder
                    .getCurrentUser().getStore().getId());
            }
            int store_status = user_store == null ? 0 : user_store.getStore_status();
            if (store_status == 0) {
                WebForm wf = new WebForm();
                Store store = (Store) wf.toPo(request, Store.class);
                StoreClass sc = this.storeClassService.getObjById(Long.valueOf(Long
                    .parseLong(sc_id)));
                store.setSc(sc);
                StoreGrade grade = this.storeGradeService.getObjById(Long.valueOf(Long
                    .parseLong(grade_id)));
                store.setGrade(grade);
                Area area = this.areaService.getObjById(Long.valueOf(Long.parseLong(area_id)));
                store.setArea(area);
                store.setTemplate("default");
                store.setCreatetime(new Date());
                store.setStore_second_domain("shop"
                                             + SecurityUserHolder.getCurrentUser().getId()
                                                 .toString());
                store.setDelivery_begin_time(new Date());
                store.setDelivery_end_time(new Date(2114, 11, 29));
                store.setCombin_begin_time(new Date());
                store.setCombin_end_time(new Date(2114, 11, 29));

                /*
                 * 上传身份证
                 * */
                String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
                String saveFilePathName = request.getSession().getServletContext().getRealPath("/")
                                          + uploadFilePath + "/user_card";
                try {
                    Map map = new HashMap();
                    map = CommUtil.saveFileToServer(request, "card_file", saveFilePathName, null,
                        null);

                    Accessory card = new Accessory();
                    if (!"".equals(map.get("fileName"))) {
                        card.setName(CommUtil.null2String(map.get("fileName")));
                        card.setExt(CommUtil.null2String(map.get("mime")));
                        card.setSize(CommUtil.null2Float(map.get("fileSize")));
                        card.setPath(uploadFilePath + "/user_card");
                        card.setWidth(CommUtil.null2Int(map.get("width")));
                        card.setHeight(CommUtil.null2Int(map.get("height")));
                        this.accessoryService.save(card);
                        store.setCard(card);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                /*
                 * 上传店铺认证
                 */
                String saveFilePathName2 = request.getSession().getServletContext()
                    .getRealPath("/")
                                           + uploadFilePath + "/store_card";

                try {
                    Map map2 = new HashMap();
                    map2 = CommUtil.saveFileToServer(request, "license_file", saveFilePathName2,
                        null, null);

                    Accessory license = new Accessory();
                    if (!"".equals(map2.get("fileName"))) {
                        license.setName(CommUtil.null2String(map2.get("fileName")));
                        license.setExt(CommUtil.null2String(map2.get("mime")));
                        license.setSize(CommUtil.null2Float(map2.get("fileSize")));
                        license.setPath(uploadFilePath + "/store_card");
                        license.setWidth(CommUtil.null2Int(map2.get("width")));
                        license.setHeight(CommUtil.null2Int(map2.get("height")));
                        this.accessoryService.save(license);
                        store.setStore_license(license);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                this.storeService.save(store);

                String path = request.getSession().getServletContext().getRealPath("/")
                              + File.separator + "upload" + File.separator + "store"
                              + File.separator + store.getId();
                CommUtil.createFolder(path);

                String store_url = CommUtil.getURL(request) + "/store_" + store.getId() + ".htm";
                QRCodeEncoderHandler handler = new QRCodeEncoderHandler();
                handler.encoderQRCode(store_url, path + "/code.png");
                User user = this.userService
                    .getObjById(SecurityUserHolder.getCurrentUser().getId());
                user.setStore(store);
                if (store.getGrade().isAudit()) {
                    store.setStore_status(1);
                } else {
                    store.setStore_status(2);
                }
                if (user.getUserRole().equals("BUYER")) {
                    user.setUserRole("BUYER_SELLER");
                }
                if (user.getUserRole().equals("ADMIN")) {
                    user.setUserRole("ADMIN_BUYER_SELLER");
                }
                Map params = new HashMap();
                params.put("type", "SELLER");
                List<Role> roles = this.roleService.query(
                    "select obj from Role obj where obj.type=:type", params, -1, -1);
                user.getRoles().addAll(roles);
                this.userService.update(user);
                SecurityUserHolder.getCurrentUser().setStore(store);
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                    SecurityContextHolder.getContext().getAuthentication().getPrincipal(),
                    SecurityContextHolder.getContext().getAuthentication().getCredentials(),
                    user.get_common_Authorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                mv.addObject("op_title", "店铺申请成功");

                //创建默认仓库
                try {
                    Warehouse warehouse = new Warehouse();
                    warehouse.setCreatetime(new Date());
                    warehouse.setDisabled(false);
                    warehouse.setManager(user.getTrueName());
                    warehouse.setName("默认仓");
                    warehouse.setStatus(1);
                    warehouse.setStore(store);
                    this.warehouseService.save(warehouse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                if (store_status == 1) {
                    mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                        this.userConfigService.getUserConfig(), 1, request, response);
                    mv.addObject("op_title", "您的店铺正在审核中");
                    mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
                }
                if (store_status == 2) {
                    mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                        this.userConfigService.getUserConfig(), 1, request, response);
                    mv.addObject("op_title", "您已经开通店铺");
                    mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
                }
                if (store_status == 3) {
                    mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                        this.userConfigService.getUserConfig(), 1, request, response);
                    mv.addObject("op_title", "您的店铺已经被关闭");
                    mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
                }
                if (store_status == 5) {
                    mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                        this.userConfigService.getUserConfig(), 1, request, response);
                    mv.addObject("op_title", "您的店铺还未开通");
                    mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
                }

                if (store_status == -1) {
                    mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                        this.userConfigService.getUserConfig(), 1, request, response);
                    mv.addObject("op_title", "您的店铺未通过审核");
                    mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
                }
            }
            mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
            request.getSession(false).removeAttribute("store_create_session");
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "表单已经失效");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "店铺设置", value = "/seller/store_set.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/store_set.htm" })
    public ModelAndView store_set(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/store_set.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Store store = this.storeService.getObjByProperty("id", SecurityUserHolder.getCurrentUser()
            .getStore().getId());
        mv.addObject("store", store);
        mv.addObject("areaViewTools", this.areaViewTools);
        List<Area> areas = this.areaService.query(
            "select obj from Area obj where obj.parent.id is null", null, -1, -1);
        mv.addObject("areas", areas);
        return mv;
    }

    @SecurityMapping(title = "店铺设置保存", value = "/seller/store_set_save.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/store_set_save.htm" })
    public ModelAndView store_set_save(HttpServletRequest request, HttpServletResponse response,
                                       String area_id, String store_second_domain) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Store store = this.storeService.getObjByProperty("id", SecurityUserHolder.getCurrentUser()
            .getStore().getId());
        WebForm wf = new WebForm();
        wf.toPo(request, store);

        String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
        String saveFilePathName = request.getSession().getServletContext().getRealPath("/")
                                  + uploadFilePath + "/store_logo";
        Map map = new HashMap();
        try {
            String fileName = store.getStore_logo() == null ? "" : store.getStore_logo().getName();
            map = CommUtil.saveFileToServer(request, "logo", saveFilePathName, fileName, null);
            if (fileName.equals("")) {
                if (map.get("fileName") != "") {
                    Accessory store_logo = new Accessory();
                    store_logo.setName(CommUtil.null2String(map.get("fileName")));
                    store_logo.setExt(CommUtil.null2String(map.get("mime")));
                    store_logo.setSize(CommUtil.null2Float(map.get("fileSize")));
                    store_logo.setPath(uploadFilePath + "/store_logo");
                    store_logo.setWidth(CommUtil.null2Int(map.get("width")));
                    store_logo.setHeight(CommUtil.null2Int(map.get("height")));
                    store_logo.setCreatetime(new Date());
                    this.accessoryService.save(store_logo);
                    store.setStore_logo(store_logo);
                }
            } else if (map.get("fileName") != "") {
                Accessory store_logo = store.getStore_logo();
                store_logo.setName(CommUtil.null2String(map.get("fileName")));
                store_logo.setExt(CommUtil.null2String(map.get("mime")));
                store_logo.setSize(CommUtil.null2Float(map.get("fileSize")));
                store_logo.setPath(uploadFilePath + "/store_logo");
                store_logo.setWidth(CommUtil.null2Int(map.get("width")));
                store_logo.setHeight(CommUtil.null2Int(map.get("height")));
                this.accessoryService.update(store_logo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveFilePathName =

        request.getSession().getServletContext().getRealPath("/")
                + this.configService.getSysConfig().getUploadFilePath() + "/store_banner";
        try {
            String fileName = store.getStore_banner() == null ? "" : store.getStore_banner()
                .getName();
            map = CommUtil.saveFileToServer(request, "banner", saveFilePathName, fileName, null);
            if (fileName.equals("")) {
                if (map.get("fileName") != "") {
                    Accessory store_banner = new Accessory();
                    store_banner.setName(CommUtil.null2String(map.get("fileName")));
                    store_banner.setExt(CommUtil.null2String(map.get("mime")));
                    store_banner.setSize(CommUtil.null2Float(map.get("fileSize")));
                    store_banner.setPath(uploadFilePath + "/store_banner");
                    store_banner.setWidth(CommUtil.null2Int(map.get("width")));
                    store_banner.setHeight(CommUtil.null2Int(map.get("height")));
                    store_banner.setCreatetime(new Date());
                    this.accessoryService.save(store_banner);
                    store.setStore_banner(store_banner);
                }
            } else if (map.get("fileName") != "") {
                Accessory store_banner = store.getStore_banner();
                store_banner.setName(CommUtil.null2String(map.get("fileName")));
                store_banner.setExt(CommUtil.null2String(map.get("mime")));
                store_banner.setSize(CommUtil.null2Float(map.get("fileSize")));
                store_banner.setPath(uploadFilePath + "/store_banner");
                store_banner.setWidth(CommUtil.null2Int(map.get("width")));
                store_banner.setHeight(CommUtil.null2Int(map.get("height")));
                this.accessoryService.update(store_banner);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //保存二维码
        saveFilePathName = request.getSession().getServletContext().getRealPath("/")
                           + this.configService.getSysConfig().getUploadFilePath()
                           + "/weixin_qr_img";
        try {
            String fileName = store.getStore_banner() == null ? "" : store.getStore_banner()
                .getName();
            map = CommUtil.saveFileToServer(request, "weixin_qr_img", saveFilePathName, fileName,
                null);
            if (fileName.equals("")) {
                if (map.get("fileName") != "") {
                    Accessory weixin_qr_img = new Accessory();
                    weixin_qr_img.setName(CommUtil.null2String(map.get("fileName")));
                    weixin_qr_img.setExt(CommUtil.null2String(map.get("mime")));
                    weixin_qr_img.setSize(CommUtil.null2Float(map.get("fileSize")));
                    weixin_qr_img.setPath(uploadFilePath + "/weixin_qr_img");
                    weixin_qr_img.setWidth(CommUtil.null2Int(map.get("width")));
                    weixin_qr_img.setHeight(CommUtil.null2Int(map.get("height")));
                    weixin_qr_img.setCreatetime(new Date());
                    this.accessoryService.save(weixin_qr_img);
                    store.setWeixin_qr_img(weixin_qr_img);
                }
            } else if (map.get("fileName") != "") {
                Accessory weixin_qr_img = store.getStore_banner();
                weixin_qr_img.setName(CommUtil.null2String(map.get("fileName")));
                weixin_qr_img.setExt(CommUtil.null2String(map.get("mime")));
                weixin_qr_img.setSize(CommUtil.null2Float(map.get("fileSize")));
                weixin_qr_img.setPath(uploadFilePath + "/store_banner");
                weixin_qr_img.setWidth(CommUtil.null2Int(map.get("width")));
                weixin_qr_img.setHeight(CommUtil.null2Int(map.get("height")));
                this.accessoryService.update(weixin_qr_img);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
        store.setArea(area);
        if (this.configService.getSysConfig().isSecond_domain_open()) {
            if ((this.configService.getSysConfig().getDomain_allow_count() > store
                .getDomain_modify_count())
                && (!CommUtil.null2String(store_second_domain).equals(""))) {
                if (!store_second_domain.equals(store.getStore_second_domain())) {
                    store.setStore_second_domain(store_second_domain);
                    store.setDomain_modify_count(store.getDomain_modify_count() + 1);
                }
            }
        }
        //如果店铺为审核不通过的状态，让提交时，应再次变为等待审核状态。
        if (store.getStore_status() == -1) {
            store.setStore_status(1);//待审核
        }
        this.storeService.update(store);
        System.out.println(store.getArea());
        //调用接口
        String write2JsonStr = JsonUtil.write2JsonStr(store);
        sendReqAsync.sendMessageUtil(Constant.STORE_INTEFACE_URL_EDIT, write2JsonStr, "修改店铺");

        mv.addObject("op_title", "商城设置成功");
        mv.addObject("url", CommUtil.getURL(request) + "/seller/store_set.htm");
        return mv;
    }

    @SecurityMapping(title = "店铺域名设置", value = "/seller/storedomainName_set.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_domainname_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/storedomainName_set.htm" })
    public ModelAndView storedomainName_set(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/storedomainName_set.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Store store = this.storeService.getObjByProperty("id", SecurityUserHolder.getCurrentUser()
            .getStore().getId());
        Date date = store.getDomainName_updateTime();
        String str = "1";//默认显示
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            Date date2 = calendar.getTime();
            if (date2.getTime() > new Date().getTime()) {
                str = "0";//不满一天
            }
        }
        String domainName = "";
        if (store != null && store.getDomainName_info() != null
            && !"".equals(store.getDomainName_info())) {
            String[] strs = store.getDomainName_info().split("\\.");
            if (strs != null && strs.length > 0) {
                domainName = strs[0];
            }
        }
        mv.addObject("domainName", domainName);
        mv.addObject("domainShow", str);
        mv.addObject("store", store);
        return mv;
    }

    @SecurityMapping(title = "店铺设置保存", value = "/seller/storedomainName_set_save.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_domainname_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/storedomainName_set_save.htm" })
    public ModelAndView storedomainName_set_save(HttpServletRequest request,
                                                 HttpServletResponse response,
                                                 String domainName_info) {

        SysConfig sysConfig = this.configService.getSysConfig();
        ModelAndView mv = new JModelAndView("success.html", sysConfig,
            this.userConfigService.getUserConfig(), 1, request, response);
        Store store = this.storeService.getObjByProperty("id", SecurityUserHolder.getCurrentUser()
            .getStore().getId());

        store.setDomainName_info(domainName_info + sysConfig.getFront_web_after_path());
        store.setDomainName_updateTime(new Date());
        this.storeService.update(store);

        if (store != null && store.getDomainName_info() != null) {
            FileUtil.saveNginxFile(store.getDomainName_info(), "" + store.getId());
        }

        mv.addObject("op_title", "店铺域名设置成功");
        mv.addObject("url", CommUtil.getURL(request) + "/seller/storedomainName_set.htm");
        return mv;
    }

    @SecurityMapping(title = "店铺地图", value = "/seller/store_map.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/store_map.htm" })
    public ModelAndView store_map(HttpServletRequest request, HttpServletResponse response,
                                  String map_type) {
        Store store = this.storeService.getObjByProperty("id", SecurityUserHolder.getCurrentUser()
            .getStore().getId());
        if (CommUtil.null2String(map_type).equals("")) {
            if ((store.getMap_type() != null) && (!store.getMap_type().equals(""))) {
                map_type = store.getMap_type();
            } else {
                map_type = "baidu";
            }
        }
        ModelAndView mv = new JModelAndView("user/default/usercenter/store_" + map_type
                                            + "_map.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        mv.addObject("store", store);
        return mv;
    }

    @SecurityMapping(title = "店铺地图保存", value = "/seller/store_map_save.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/store_map_save.htm" })
    public ModelAndView store_map_save(HttpServletRequest request, HttpServletResponse response,
                                       String store_lat, String store_lng) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Store store = this.storeService.getObjByProperty("id", SecurityUserHolder.getCurrentUser()
            .getStore().getId());
        store.setStore_lat(BigDecimal.valueOf(CommUtil.null2Double(store_lat)));
        store.setStore_lng(BigDecimal.valueOf(CommUtil.null2Double(store_lng)));
        this.storeService.update(store);
        mv.addObject("op_title", "商城设置成功");
        mv.addObject("url", CommUtil.getURL(request) + "/seller/store_map.htm");
        return mv;
    }

    @SecurityMapping(title = "主题设置", value = "/seller/store_theme.htm*", rtype = "seller", rname = "主题设置", rcode = "store_theme_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/store_theme.htm" })
    public ModelAndView store_theme(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/store_theme.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Store store = this.storeService.getObjByProperty("id", SecurityUserHolder.getCurrentUser()
            .getStore().getId());
        mv.addObject("store", store);
        return mv;
    }

    @SecurityMapping(title = "主题设置", value = "/seller/store_theme_save.htm*", rtype = "seller", rname = "主题设置", rcode = "store_theme_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/store_theme_save.htm" })
    public String store_theme_set(HttpServletRequest request, HttpServletResponse response,
                                  String theme) {
        Store store = this.storeService.getObjByProperty("id", SecurityUserHolder.getCurrentUser()
            .getStore().getId());
        if (store.getGrade().getTemplates().indexOf(theme) >= 0) {
            store.setTemplate(theme);
            this.storeService.update(store);
        }
        return "redirect:store_theme.htm";
    }

    @SecurityMapping(title = "店铺认证", value = "/seller/store_approve.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/store_approve.htm" })
    public ModelAndView store_approve(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/store_approve.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Store store = this.storeService.getObjByProperty("id", SecurityUserHolder.getCurrentUser()
            .getStore().getId());
        mv.addObject("store", store);
        return mv;
    }

    @SecurityMapping(title = "店铺认证保存", value = "/seller/store_approve_save.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/store_approve_save.htm" })
    public String store_approve_save(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/store_approve.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Store store = this.storeService.getObjByProperty("id", SecurityUserHolder.getCurrentUser()
            .getStore().getId());

        String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
        String saveFilePathName = request.getSession().getServletContext().getRealPath("/")
                                  + uploadFilePath;
        Map map = new HashMap();
        try {
            String fileName = store.getCard() == null ? "" : store.getCard().getName();
            map = CommUtil.saveFileToServer(request, "card_img", saveFilePathName + File.separator
                                                                 + "card", fileName, null);
            if (fileName.equals("")) {
                if (map.get("fileName") != "") {
                    Accessory card = new Accessory();
                    card.setName(CommUtil.null2String(map.get("fileName")));
                    card.setExt(CommUtil.null2String(map.get("mime")));
                    card.setSize(CommUtil.null2Float(map.get("fileSize")));
                    card.setPath(uploadFilePath + "/card");
                    card.setWidth(CommUtil.null2Int(map.get("width")));
                    card.setHeight(CommUtil.null2Int(map.get("height")));
                    card.setCreatetime(new Date());
                    this.accessoryService.save(card);
                    store.setCard(card);
                }
            } else if (map.get("fileName") != "") {
                Accessory card = store.getCard();
                card.setName(CommUtil.null2String(map.get("fileName")));
                card.setExt(CommUtil.null2String(map.get("mime")));
                card.setSize(CommUtil.null2Float(map.get("fileSize")));
                card.setPath(uploadFilePath + "/card");
                card.setWidth(CommUtil.null2Int(map.get("width")));
                card.setHeight(CommUtil.null2Int(map.get("height")));
                this.accessoryService.update(card);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String fileName = store.getStore_license() == null ? "" : store.getStore_license()
                .getName();
            map = CommUtil.saveFileToServer(request, "license_img", saveFilePathName
                                                                    + File.separator + "license",
                fileName, null);
            if (fileName.equals("")) {
                if (map.get("fileName") != "") {
                    Accessory store_license = new Accessory();
                    store_license.setName(CommUtil.null2String(map.get("fileName")));
                    store_license.setExt(CommUtil.null2String(map.get("mime")));
                    store_license.setSize(CommUtil.null2Float(map.get("fileSize")));
                    store_license.setPath(uploadFilePath + "/license");
                    store_license.setWidth(CommUtil.null2Int(map.get("width")));
                    store_license.setHeight(CommUtil.null2Int(map.get("height")));
                    store_license.setCreatetime(new Date());
                    this.accessoryService.save(store_license);
                    store.setStore_license(store_license);
                }
            } else if (map.get("fileName") != "") {
                Accessory store_license = store.getStore_license();
                store_license.setName(CommUtil.null2String(map.get("fileName")));
                store_license.setExt(CommUtil.null2String(map.get("mime")));
                store_license.setSize(CommUtil.null2Float(map.get("fileSize")));
                store_license.setPath(uploadFilePath + "/license");
                store_license.setWidth(CommUtil.null2Int(map.get("width")));
                store_license.setHeight(CommUtil.null2Int(map.get("height")));
                this.accessoryService.update(store_license);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.storeService.update(store);
        return "redirect:store_approve.htm";
    }

    @SecurityMapping(title = "店铺升级", value = "/seller/store_grade.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/store_grade.htm" })
    public ModelAndView store_grade(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/store_grade.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Store store = this.storeService.getObjByProperty("id", SecurityUserHolder.getCurrentUser()
            .getStore().getId());
        List<StoreGrade> sgs = this.storeGradeService.query(
            "select obj from StoreGrade obj order by obj.sequence asc", null, -1, -1);
        mv.addObject("sgs", sgs);
        mv.addObject("store", store);
        if (store.getUpdate_grade() != null) {
            /*mv = new JModelAndView("user/default/usercenter/error.html", 
              this.configService.getSysConfig(), 
              this.userConfigService.getUserConfig(), 0, request, 
              response);*/
            mv.addObject("op_title", "您的店铺升级正在审核中");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/store_set.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "店铺升级申请完成", value = "/seller/store_grade_finish.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/store_grade_finish.htm" })
    public ModelAndView store_grade_finish(HttpServletRequest request,
                                           HttpServletResponse response, String grade_id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Store store = this.storeService.getObjByProperty("id", SecurityUserHolder.getCurrentUser()
            .getStore().getId());

        store.setUpdate_grade(this.storeGradeService.getObjById(CommUtil.null2Long(grade_id)));
        this.storeService.update(store);
        StoreGradeLog grade_log = new StoreGradeLog();
        grade_log.setCreatetime(new Date());
        grade_log.setStore(store);
        this.storeGradeLogService.save(grade_log);
        mv.addObject("op_title", "申请提交成功");
        mv.addObject("url", CommUtil.getURL(request) + "/seller/store_set.htm");
        return mv;
    }

    @SecurityMapping(title = "店铺幻灯", value = "/seller/store_slide.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/store_slide.htm" })
    public ModelAndView store_slide(HttpServletRequest request, HttpServletResponse response,
                                    String type) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/store_slide.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Store store = this.storeService.getObjByProperty("id", SecurityUserHolder.getCurrentUser()
            .getStore().getId());
        mv.addObject("store", store);
        if (type != null && "1".equals(type)) {
            mv.addObject("type", 1);
            List<StoreSlide> list = new ArrayList<StoreSlide>();
            List<StoreSlide> slides = store.getSlides();
            if (slides != null && slides.size() > 0) {
                for (StoreSlide storeSlide : slides) {
                    if (storeSlide.getType() != null && storeSlide.getType() == 1) {
                        list.add(storeSlide);
                    }
                }

            }
            mv.addObject("slides", list);
        } else {
            mv.addObject("type", 0);
            List<StoreSlide> list = new ArrayList<StoreSlide>();
            List<StoreSlide> slides = store.getSlides();
            if (slides != null && slides.size() > 0) {
                for (StoreSlide storeSlide : slides) {
                    if (storeSlide.getType() == null || storeSlide.getType() == 0) {
                        list.add(storeSlide);
                    }
                }

            }
            mv.addObject("slides", list);
        }
        return mv;
    }

    @SecurityMapping(title = "店铺幻灯保存", value = "/seller/store_slide_save.htm*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/store_slide_save.htm" })
    public String store_slide_save(HttpServletRequest request, HttpServletResponse response,
                                   String type) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/store_slide.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if (SecurityUserHolder.getCurrentUser() != null) {
            Store store = this.storeService.getObjByProperty("id", SecurityUserHolder
                .getCurrentUser().getStore().getId());
            String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
            String saveFilePathName = request.getSession().getServletContext().getRealPath("/")
                                      + uploadFilePath + File.separator + "store_slide";

            List<StoreSlide> list = new ArrayList<StoreSlide>();
            if (type != null && "1".equals(type)) {
                mv.addObject("type", 1);
                List<StoreSlide> slides = store.getSlides();
                if (slides != null && slides.size() > 0) {
                    for (StoreSlide storeSlide : slides) {
                        if (storeSlide.getType() != null && storeSlide.getType() == 1) {
                            list.add(storeSlide);
                        }
                    }

                }
            } else {
                mv.addObject("type", 0);
                List<StoreSlide> slides = store.getSlides();
                if (slides != null && slides.size() > 0) {
                    for (StoreSlide storeSlide : slides) {
                        if (storeSlide.getType() == null || storeSlide.getType() == 0) {
                            list.add(storeSlide);
                        }
                    }

                }
            }
            for (int i = 1; i <= 5; i++) {
                Map map = new HashMap();
                String fileName = "";
                StoreSlide slide = null;
                if (list.size() >= i) {
                    fileName = ((StoreSlide) list.get(i - 1)).getAcc().getName();
                    slide = (StoreSlide) list.get(i - 1);
                }
                try {
                    map = CommUtil.saveFileToServer(request, "acc" + i, saveFilePathName, fileName,
                        null);
                    Accessory acc = null;
                    if (fileName.equals("")) {
                        if (map.get("fileName") != "") {
                            acc = new Accessory();
                            acc.setName(CommUtil.null2String(map.get("fileName")));
                            acc.setExt(CommUtil.null2String(map.get("mime")));
                            acc.setSize(CommUtil.null2Float(map.get("fileSize")));
                            acc.setPath(uploadFilePath + "/store_slide");
                            acc.setWidth(CommUtil.null2Int(map.get("width")));
                            acc.setHeight(CommUtil.null2Int(map.get("height")));
                            acc.setCreatetime(new Date());
                            this.accessoryService.save(acc);
                        }
                    } else if (map.get("fileName") != "") {
                        acc = slide.getAcc();
                        acc.setName(CommUtil.null2String(map.get("fileName")));
                        acc.setExt(CommUtil.null2String(map.get("mime")));
                        acc.setSize(CommUtil.null2Float(map.get("fileSize")));
                        acc.setPath(uploadFilePath + "/store_slide");
                        acc.setWidth(CommUtil.null2Int(map.get("width")));
                        acc.setHeight(CommUtil.null2Int(map.get("height")));
                        acc.setCreatetime(new Date());
                        this.accessoryService.update(acc);
                    } else if (map.get("fileName") == "" && slide != null) {
                        //这种情况下是修改地址
                        acc = new Accessory();//补上这个让修改地址能进入下面的  update
                    }
                    if (acc != null) {
                        if (slide == null) {
                            slide = new StoreSlide();
                            slide.setAcc(acc);
                            slide.setCreatetime(new Date());
                            slide.setStore(store);

                        }
                        String str = request.getParameter("acc_url" + i);
                        slide.setUrl(str);
                        //String type = request.getParameter("type" + i);
                        slide.setType(CommUtil.null2Int(type));//轮播类型

                        String del = request.getParameter("del_" + i);
                        if ((acc.getName() == null || "".equals(acc.getName())) && "1".equals(del)) {//没有上传图片
                            if (slide.getId() != null) {
                                this.storeSlideService.delete(slide.getId());
                            }
                        } else {
                            if (slide.getId() == null) {
                                this.storeSlideService.save(slide);
                            } else {
                                this.storeSlideService.update(slide);
                            }
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            mv.addObject("store", store);
        }
        //return mv;
        return "redirect:store_slide.htm?type=" + type;
    }
}
