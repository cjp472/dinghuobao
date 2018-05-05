package com.javamalls.ctrl.admin.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.easyjf.beans.BeanUtils;
import com.easyjf.beans.BeanWrapper;
import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.Md5Encrypt;
import com.javamalls.base.tools.WebForm;
import com.javamalls.ctrl.admin.tools.AreaManageTools;
import com.javamalls.platform.domain.Accessory;
import com.javamalls.platform.domain.Album;
import com.javamalls.platform.domain.Area;
import com.javamalls.platform.domain.CompanyInfo;
import com.javamalls.platform.domain.Evaluate;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.GoodsCart;
import com.javamalls.platform.domain.GoodsLabel;
import com.javamalls.platform.domain.Message;
import com.javamalls.platform.domain.OrderForm;
import com.javamalls.platform.domain.Role;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.StoreClass;
import com.javamalls.platform.domain.StoreGrade;
import com.javamalls.platform.domain.StoreGradeLog;
import com.javamalls.platform.domain.SysConfig;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.Warehouse;
import com.javamalls.platform.domain.query.CompanyQueryObject;
import com.javamalls.platform.domain.query.StoreGradeLogQueryObject;
import com.javamalls.platform.domain.query.StoreQueryObject;
import com.javamalls.platform.service.IAccessoryService;
import com.javamalls.platform.service.IAlbumService;
import com.javamalls.platform.service.IAreaService;
import com.javamalls.platform.service.ICompanyInfoService;
import com.javamalls.platform.service.IEvaluateService;
import com.javamalls.platform.service.IGoodsCartService;
import com.javamalls.platform.service.IGoodsLabelService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IMessageService;
import com.javamalls.platform.service.IOrderFormService;
import com.javamalls.platform.service.IRoleService;
import com.javamalls.platform.service.IStoreClassService;
import com.javamalls.platform.service.IStoreGradeLogService;
import com.javamalls.platform.service.IStoreGradeService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.ITemplateService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;
import com.javamalls.platform.service.IWarehouseService;

/**店铺管理
 *                       
 * @Filename: StoreManageAction.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class StoreManageAction {
    @Autowired
    private ISysConfigService     configService;
    @Autowired
    private IUserConfigService    userConfigService;
    @Autowired
    private IStoreService         storeService;
    @Autowired
    private IStoreGradeService    storeGradeService;
    @Autowired
    private IStoreClassService    storeClassService;
    @Autowired
    private IAreaService          areaService;
    @Autowired
    private IUserService          userService;
    @Autowired
    private IRoleService          roleService;
    @Autowired
    private IGoodsService         goodsService;
    @Autowired
    private AreaManageTools       areaManageTools;
    @Autowired
    private ITemplateService      templateService;
    @Autowired
    private IMessageService       messageService;
    @Autowired
    private IStoreGradeLogService storeGradeLogService;
    @Autowired
    private IEvaluateService      evaluateService;
    @Autowired
    private IGoodsCartService     goodsCartService;
    @Autowired
    private IOrderFormService     orderFormService;
    @Autowired
    private IAccessoryService     accessoryService;
    @Autowired
    private IAlbumService         albumService;
    @Autowired
    private ICompanyInfoService   companyInfoService;
    @Autowired
    private IWarehouseService     warehouseService;
    @Autowired
    private IGoodsLabelService     goodsLabelService;

    @SecurityMapping(title = "店铺列表", value = "/admin/store_type.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({ "/admin/store_type.htm" })
    public ModelAndView store_list(HttpServletRequest request, HttpServletResponse response,
                                   String currentPage, String orderBy, String orderType,
                                   String store_name, String gradeid, String store_status) {
        String msg = request.getParameter("msg");
        ModelAndView mv = new JModelAndView("admin/blue/store_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        String params = "";
        StoreQueryObject qo = new StoreQueryObject(currentPage, mv, orderBy, orderType);
        WebForm wf = new WebForm();

        if (null != store_name && !"".equals(store_name))
            qo.addQuery("obj.store_name", new SysMap("store_name", "%" + store_name + "%"), "like");

        if (null != gradeid && !"".equals(gradeid))
            qo.addQuery("obj.grade.id", new SysMap("gradeid", Long.valueOf(gradeid)), "=");

        if (null != store_status && !"".equals(store_status))
            qo.addQuery("obj.store_status", new SysMap("status", Integer.valueOf(store_status)),
                "=");

        wf.toQueryPo(request, qo, Store.class, mv);
        IPageList pList = this.storeService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/admin/store_type.htm", "", params, pList, mv);
        List<StoreGrade> grades = this.storeGradeService.query(
            "select obj from StoreGrade obj order by obj.sequence asc", null, -1, -1);
        mv.addObject("grades", grades);
        mv.addObject("store_name", store_name);
        mv.addObject("gradeid", gradeid);
        mv.addObject("store_status", store_status);
        mv.addObject("msg", msg);
        return mv;
    }

    @SecurityMapping(title = "店铺添加1", value = "/admin/store_add.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({ "/admin/store_add.htm" })
    public ModelAndView store_add(HttpServletRequest request, HttpServletResponse response,
                                  String currentPage) {
        ModelAndView mv = new JModelAndView("admin/blue/store_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("currentPage", currentPage);
        return mv;
    }

    @SecurityMapping(title = "店铺添加2", value = "/admin/store_new.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({ "/admin/store_new.htm" })
    public ModelAndView store_new(HttpServletRequest request, HttpServletResponse response,
                                  String currentPage, String userName, String list_url,
                                  String add_url) {
        ModelAndView mv = new JModelAndView("admin/blue/store_new.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        User user = this.userService.getObjByProperty("userName", userName);
        Store store = null;
        if (user != null) {
            if (user.getStore() != null) {
                store = this.storeService.getObjByProperty("id", user.getStore().getId());
            }
        }
        if (user == null) {
            mv = new JModelAndView("admin/blue/tip.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 0, request, response);
            mv.addObject("op_tip", "不存在该用户");
            mv.addObject("list_url", list_url);
        } else if (store == null) {
            List<StoreClass> scs = this.storeClassService
                .query(
                    "select obj from StoreClass obj where obj.parent.id is null order by obj.sequence asc",
                    null, -1, -1);
            List<Area> areas = this.areaService.query(
                "select obj from Area obj where obj.parent.id is null", null, -1, -1);
            List<StoreGrade> grades = this.storeGradeService.query(
                "select obj from StoreGrade obj order by obj.sequence asc", null, -1, -1);
            mv.addObject("grades", grades);
            mv.addObject("areas", areas);
            mv.addObject("scs", scs);
            mv.addObject("currentPage", currentPage);
            mv.addObject("user", user);
        } else {
            mv = new JModelAndView("admin/blue/tip.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 0, request, response);
            mv.addObject("op_tip", "该用户已经开通店铺");
            mv.addObject("list_url", add_url);
        }
        return mv;
    }

    @SecurityMapping(title = "店铺编辑", value = "/admin/store_edit.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({ "/admin/store_edit.htm" })
    public ModelAndView store_edit(HttpServletRequest request, HttpServletResponse response,
                                   String id, String currentPage) {
        ModelAndView mv = new JModelAndView("admin/blue/store_edit.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if ((id != null) && (!id.equals(""))) {
            Store store = this.storeService.getObjById(Long.valueOf(Long.parseLong(id)));
            List<StoreClass> scs = this.storeClassService
                .query(
                    "select obj from StoreClass obj where obj.parent.id is null order by obj.sequence asc",
                    null, -1, -1);
            List<Area> areas = this.areaService.query(
                "select obj from Area obj where obj.parent.id is null", null, -1, -1);
            mv.addObject("areas", areas);
            mv.addObject("scs", scs);
            mv.addObject("obj", store);
            mv.addObject("currentPage", currentPage);
            mv.addObject("edit", Boolean.valueOf(true));
            if (store.getArea() != null) {
                mv.addObject("area_info", this.areaManageTools.generic_area_info(store.getArea()));
            }
        }
        return mv;
    }

    @SecurityMapping(title = "店铺保存", value = "/admin/store_save.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({ "/admin/store_save.htm" })
    public ModelAndView store_save(HttpServletRequest request, HttpServletResponse response,
                                   String id, String area_id, String sc_id, String grade_id,
                                   String user_id, String store_status, String currentPage,
                                   String cmd, String list_url, String add_url, String ispawn,
                                   String isrepairdeposit, String isloan, String isrecommend,
                                   String card_approve_ck, String realstore_approve_ck)
                                                                                       throws Exception {
        WebForm wf = new WebForm();
        Store store = null;
        if (id.equals("")) {
            store = (Store) wf.toPo(request, Store.class);
            store.setCreatetime(new Date());
        } else {
            Store obj = this.storeService.getObjById(Long.valueOf(Long.parseLong(id)));
            store = (Store) wf.toPo(request, obj);
        }
        store.setCard_approve("on".equals(card_approve_ck) ? true : false);
        store.setRealstore_approve("on".equals(realstore_approve_ck) ? true : false);

        Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
        store.setArea(area);
        StoreClass sc = this.storeClassService.getObjById(Long.valueOf(Long.parseLong(sc_id)));
        store.setSc(sc);
        store.setTemplate("default");
        if ((grade_id != null) && (!grade_id.equals(""))) {
            store
                .setGrade(this.storeGradeService.getObjById(Long.valueOf(Long.parseLong(grade_id))));
        }
        if ((store_status != null) && (!store_status.equals(""))) {
            store.setStore_status(CommUtil.null2Int(store_status));
        } else {
            store.setStore_status(2);
        }
        if (store.isStore_recommend()) {
            store.setStore_recommend_time(new Date());
        } else {
            store.setStore_recommend_time(null);
        }
        store.setDelivery_begin_time(new Date());
        store.setDelivery_end_time(new Date(2114, 11, 29));
        store.setCombin_begin_time(new Date());
        store.setCombin_end_time(new Date(2114, 11, 29));
        if (id.equals("")) {
            this.storeService.save(store);
        } else {
            this.storeService.update(store);
        }
        if ((user_id != null) && (!user_id.equals(""))) {
            User user = this.userService.getObjById(Long.valueOf(Long.parseLong(user_id)));
            user.setStore(store);
            user.setUserRole("BUYER_SELLER");

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("type", "SELLER");
            List<Role> roles = this.roleService.query(
                "select obj from Role obj where obj.type=:type", params, -1, -1);
            user.getRoles().addAll(roles);
            this.userService.update(user);
        }
        if ((!id.equals("")) && (store.getStore_status() == 3)) {
            send_site_msg(request, "msg_toseller_store_closed_notify", store);
        }
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("list_url", list_url);
        mv.addObject("op_title", "保存店铺成功");
        if (add_url != null) {
            mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
        }
        return mv;
    }

    private void send_site_msg(HttpServletRequest request, String mark, Store store)
                                                                                    throws Exception {
        com.javamalls.platform.domain.Template template = this.templateService.getObjByProperty(
            "mark", mark);
        if (template != null && template.isOpen()) {
            String path = request.getRealPath("/") + "/vm/";
            PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
                path + "msg.vm", false), "UTF-8"));
            pwrite.print(template.getContent());
            pwrite.flush();
            pwrite.close();

            Properties p = new Properties();
            p.setProperty("file.resource.loader.path", request.getRealPath("/") + "vm"
                                                       + File.separator);
            p.setProperty("input.encoding", "UTF-8");
            p.setProperty("output.encoding", "UTF-8");
            Velocity.init(p);
            org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm", "UTF-8");
            VelocityContext context = new VelocityContext();
            context.put("reason", store.getViolation_reseaon());
            context.put("user", store.getUser());
            context.put("config", this.configService.getSysConfig());
            context.put("send_time", CommUtil.formatLongDate(new Date()));
            StringWriter writer = new StringWriter();
            blank.merge(context, writer);
            String content = writer.toString();
            User fromUser = this.userService.getObjByProperty("userName", "admin");
            Message msg = new Message();
            msg.setCreatetime(new Date());
            msg.setContent(content);
            msg.setFromUser(fromUser);
            msg.setTitle(template.getTitle());
            msg.setToUser(store.getUser());
            msg.setType(0);
            this.messageService.save(msg);
            CommUtil.deleteFile(path + "msg.vm");
            writer.flush();
            writer.close();
        }
    }

    @SecurityMapping(title = "店铺删除", value = "/admin/store_del.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({ "/admin/store_del.htm" })
    public String store_del(HttpServletRequest request, String mulitId) throws Exception {
        String msg = "delsuccess";
        try {
            String[] ids = mulitId.split(",");
            for (String id : ids) {
                if (!id.equals("")) {
                    Store store = this.storeService.getObjById(Long.valueOf(Long.parseLong(id)));
                    if (store.getUser() != null) {
                        store.getUser().setStore(null);
                    }
                    List<GoodsCart> goodCarts;
                    for (Goods goods : store.getGoods_list()) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("gid", goods.getId());
                        goodCarts = this.goodsCartService.query(
                            "select obj from GoodsCart obj where obj.goods.id = :gid", map, -1, -1);
                        Long ofid = null;
                        Map<String, Object> map2;
                        List<GoodsCart> goodCarts2;
                        for (GoodsCart gc : goodCarts) {
                            gc.getGsps().clear();
                            this.goodsCartService.delete(gc.getId());
                            if (gc.getOf() != null) {
                                map2 = new HashMap<String, Object>();
                                ofid = gc.getOf().getId();
                                map2.put("ofid", ofid);
                                goodCarts2 = this.goodsCartService.query(
                                    "select obj from GoodsCart obj where obj.of.id = :ofid", map2,
                                    -1, -1);
                                if (goodCarts2.size() == 0) {
                                    this.orderFormService.delete(ofid);
                                }
                            }
                        }
                        List<Evaluate> evaluates = goods.getEvaluates();
                        for (Evaluate e : evaluates) {
                            this.evaluateService.delete(e.getId());
                        }
                        goods.getGoods_ugcs().clear();
                        Accessory acc = goods.getGoods_main_photo();
                        if (acc != null) {
                            acc.setAlbum(null);
                            Album album = acc.getCover_album();
                            if (album != null) {
                                album.setAlbum_cover(null);
                                this.albumService.update(album);
                            }
                            this.accessoryService.update(acc);
                        }
                        for (Accessory acc1 : goods.getGoods_photos()) {
                            if (acc1 != null) {
                                acc1.setAlbum(null);
                                Album album = acc1.getCover_album();
                                if (album != null) {
                                    album.setAlbum_cover(null);
                                    this.albumService.update(album);
                                }
                                acc1.setCover_album(null);
                                this.accessoryService.update(acc1);
                            }
                        }
                        goods.getCombin_goods().clear();
                        this.goodsService.delete_wuli(goods.getId());
                    }
                    for (OrderForm of : store.getOfs()) {
                        for (GoodsCart gc : of.getGcs()) {
                            gc.getGsps().clear();
                            this.goodsCartService.delete(gc.getId());
                        }
                        this.orderFormService.delete(of.getId());
                    }
                    this.storeService.delete(CommUtil.null2Long(id));
                    send_site_msg(request, "msg_toseller_goods_delete_by_admin_notify", store);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg = "delerror";
        }
        return "redirect:/admin/store_type.htm?msg=" + msg;
    }

    @SecurityMapping(title = "店铺AJAX更新", value = "/admin/store_ajax.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({ "/admin/store_ajax.htm" })
    public void store_ajax(HttpServletRequest request, HttpServletResponse response, String id,
                           String fieldName, String value) throws ClassNotFoundException {
        Store obj = this.storeService.getObjById(Long.valueOf(Long.parseLong(id)));
        Field[] fields = Store.class.getDeclaredFields();
        BeanWrapper wrapper = new BeanWrapper(obj);
        Object val = null;
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                Class clz = Class.forName("java.lang.String");
                if (field.getType().getName().equals("int")) {
                    clz = Class.forName("java.lang.Integer");
                }
                if (field.getType().getName().equals("boolean")) {
                    clz = Class.forName("java.lang.Boolean");
                }
                if (!value.equals("")) {
                    val = BeanUtils.convertType(value, clz);
                } else {
                    val = Boolean.valueOf(!CommUtil.null2Boolean(wrapper
                        .getPropertyValue(fieldName)));
                }
                wrapper.setPropertyValue(fieldName, val);
            }
        }
        if (fieldName.equals("store_recommend")) {
            if (obj.isStore_recommend()) {
                obj.setStore_recommend_time(new Date());
            } else {
                obj.setStore_recommend_time(null);
            }
        }
        this.storeService.update(obj);
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(val.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SecurityMapping(title = "卖家信用", value = "/admin/store_base.htm*", rtype = "admin", rname = "基本设置", rcode = "admin_store_base", rgroup = "店铺")
    @RequestMapping({ "/admin/store_base.htm" })
    public ModelAndView store_base(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("admin/blue/store_base_set.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        return mv;
    }

    @SecurityMapping(title = "卖家信用保存", value = "/admin/store_set_save.htm*", rtype = "admin", rname = "基本设置", rcode = "admin_store_base", rgroup = "店铺")
    @RequestMapping({ "/admin/store_set_save.htm" })
    public ModelAndView store_set_save(HttpServletRequest request, HttpServletResponse response,
                                       String id, String list_url, String store_allow) {
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        SysConfig sc = this.configService.getSysConfig();
        sc.setStore_allow(CommUtil.null2Boolean(store_allow));
        Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i <= 29; i++) {
            map.put("creditrule" + i,
                Integer.valueOf(CommUtil.null2Int(request.getParameter("creditrule" + i))));
        }
        String creditrule = Json.toJson(map, JsonFormat.compact());
        sc.setCreditrule(creditrule);
        if (id.equals("")) {
            this.configService.save(sc);
        } else {
            this.configService.update(sc);
        }
        mv.addObject("list_url", list_url);
        mv.addObject("op_title", "保存店铺设置成功");
        return mv;
    }

    @SecurityMapping(title = "店铺模板", value = "/admin/store_template.htm*", rtype = "admin", rname = "店铺模板", rcode = "admin_store_template", rgroup = "店铺")
    @RequestMapping({ "/admin/store_template.htm" })
    public ModelAndView store_template(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("admin/blue/store_template.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("path", request.getRealPath("/"));
        mv.addObject("separator", File.separator);
        return mv;
    }

    @SecurityMapping(title = "店铺模板增加", value = "/admin/store_template_add.htm*", rtype = "admin", rname = "店铺模板", rcode = "admin_store_template", rgroup = "店铺")
    @RequestMapping({ "/admin/store_template_add.htm" })
    public ModelAndView store_template_add(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("admin/blue/store_template_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        return mv;
    }

    @SecurityMapping(title = "店铺模板保存", value = "/admin/store_template_save.htm*", rtype = "admin", rname = "店铺模板", rcode = "admin_store_template", rgroup = "店铺")
    @RequestMapping({ "/admin/store_template_save.htm" })
    public ModelAndView store_template_save(HttpServletRequest request,
                                            HttpServletResponse response, String id,
                                            String list_url, String templates) {
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        SysConfig sc = this.configService.getSysConfig();
        sc.setTemplates(templates);
        if (id.equals("")) {
            this.configService.save(sc);
        } else {
            this.configService.update(sc);
        }
        mv.addObject("list_url", list_url);
        mv.addObject("op_title", "店铺模板设置成功");
        return mv;
    }

    @SecurityMapping(title = "店铺升级列表", value = "/admin/store_gradelog_list.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({ "/admin/store_gradelog_list.htm" })
    public ModelAndView store_gradelog_list(HttpServletRequest request,
                                            HttpServletResponse response, String currentPage,
                                            String orderBy, String orderType, String store_name,
                                            String grade_id, String store_grade_status) {
        ModelAndView mv = new JModelAndView("admin/blue/store_gradelog_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        String params = "";
        StoreGradeLogQueryObject qo = new StoreGradeLogQueryObject(currentPage, mv, orderBy,
            orderType);
        if (!CommUtil.null2String(store_name).equals("")) {
            qo.addQuery("obj.store.store_name", new SysMap("store_name", "%" + store_name + "%"),
                "like");
            mv.addObject("store_name", store_name);
        }
        if (CommUtil.null2Long(grade_id).longValue() != -1L) {
            qo.addQuery("obj.store.update_grade.id",
                new SysMap("grade_id", CommUtil.null2Long(grade_id)), "=");
            mv.addObject("grade_id", grade_id);
        }
        if (!CommUtil.null2String(store_grade_status).equals("")) {
            qo.addQuery(
                "obj.store_grade_status",
                new SysMap("store_grade_status", Integer.valueOf(CommUtil
                    .null2Int(store_grade_status))), "=");
            mv.addObject("store_grade_status", store_grade_status);
        }
        IPageList pList = this.storeGradeLogService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/admin/store_type.htm", "", params, pList, mv);
        List<StoreGrade> grades = this.storeGradeService.query(
            "select obj from StoreGrade obj order by obj.sequence asc", null, -1, -1);
        mv.addObject("grades", grades);
        return mv;
    }

    @SecurityMapping(title = "店铺升级编辑", value = "/admin/store_gradelog_edit.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({ "/admin/store_gradelog_edit.htm" })
    public ModelAndView store_gradelog_edit(HttpServletRequest request,
                                            HttpServletResponse response, String currentPage,
                                            String id) {
        ModelAndView mv = new JModelAndView("admin/blue/store_gradelog_edit.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        StoreGradeLog obj = this.storeGradeLogService.getObjById(CommUtil.null2Long(id));
        mv.addObject("obj", obj);
        mv.addObject("currentPage", currentPage);
        return mv;
    }

    @SecurityMapping(title = "店铺升级保存", value = "/admin/store_gradelog_save.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({ "/admin/store_gradelog_save.htm" })
    public ModelAndView store_gradelog_save(HttpServletRequest request,
                                            HttpServletResponse response, String currentPage,
                                            String id, String cmd, String list_url)
                                                                                   throws Exception {
        WebForm wf = new WebForm();
        StoreGradeLog obj = this.storeGradeLogService.getObjById(CommUtil.null2Long(id));
        StoreGradeLog log = (StoreGradeLog) wf.toPo(request, obj);
        log.setLog_edit(true);
        log.setCreatetime(new Date());
        boolean ret = this.storeGradeLogService.update(log);
        if (ret) {
            Store store = log.getStore();
            if (log.getStore_grade_status() == 1) {
                store.setGrade(store.getUpdate_grade());
            }
            store.setUpdate_grade(null);
            this.storeService.update(store);
        }
        if (log.getStore_grade_status() == 1) {
            send_site_msg(request, "msg_toseller_store_update_allow_notify", log.getStore());
        }
        if (log.getStore_grade_status() == -1) {
            send_site_msg(request, "msg_toseller_store_update_refuse_notify", log.getStore());
        }
        send_site_msg(request, "msg_toseller_store_update_allow_notify", log.getStore());
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("list_url", list_url);
        mv.addObject("op_title", "保存店铺成功");
        return mv;
    }

    @SecurityMapping(title = "店铺升级日志查看", value = "/admin/store_gradelog_view.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({ "/admin/store_gradelog_view.htm" })
    public ModelAndView store_gradelog_view(HttpServletRequest request,
                                            HttpServletResponse response, String currentPage,
                                            String id) {
        ModelAndView mv = new JModelAndView("admin/blue/store_gradelog_view.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        StoreGradeLog obj = this.storeGradeLogService.getObjById(CommUtil.null2Long(id));
        mv.addObject("obj", obj);
        mv.addObject("currentPage", currentPage);
        return mv;
    }

    /**
     * 供货商管理
     * @param request
     * @param response
     * @param currentPage
     * @param orderBy
     * @param orderType
     * @return
     */
    @SecurityMapping(title = "供货商列表", value = "/admin/store_type.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({ "/admin/user_company.htm" })
    public ModelAndView user_company(HttpServletRequest request, HttpServletResponse response,
                                     String currentPage, String orderBy, String orderType,
                                     String mobile, String company_name, String contact_name,
                                     String trade_name, String store_status) {
        ModelAndView mv = new JModelAndView("admin/blue/company_info_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        String params = "";
        CompanyQueryObject qo = new CompanyQueryObject(currentPage, mv, orderBy, orderType);
        qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");
        if (mobile != null && !"".equals(mobile)) {
            qo.addQuery("obj.user.mobile", new SysMap("mobile", "%" + mobile + "%"), "like");
        }
        mv.addObject("mobile", mobile);
        if (company_name != null && !"".equals(company_name)) {
            qo.addQuery("obj.company_name", new SysMap("company_name", "%" + company_name + "%"),
                "like");
        }
        mv.addObject("company_name", company_name);
        if (contact_name != null && !"".equals(contact_name)) {
            qo.addQuery("obj.contact_name", new SysMap("contact_name", "%" + contact_name + "%"),
                "like");
        }
        mv.addObject("contact_name", contact_name);

        if (trade_name != null && !"".equals(trade_name)) {
            qo.addQuery("obj.trade_name", new SysMap("trade_name", "%" + trade_name + "%"), "like");
        }
        mv.addObject("trade_name", trade_name);

        if (store_status != null && !"".equals(store_status)) {
            qo.addQuery("obj.user.store.store_status",
                new SysMap("store_status", Integer.valueOf(store_status)), "=");
        }
        mv.addObject("store_status", store_status);

        IPageList pList = this.companyInfoService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/admin/user_company.htm", "", params, pList, mv);
        return mv;
    }

    /**
     * 供货商管理
     * @param request
     * @param response
     * @param currentPage
     * @param orderBy
     * @param orderType
     * @return
     */
    @SecurityMapping(title = "供货商", value = "/admin/store_type.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({ "/admin/user_company_add.htm" })
    public ModelAndView user_company_add(HttpServletRequest request, HttpServletResponse response,
                                         String currentPage, String orderBy, String orderType,
                                         String id) {
        ModelAndView mv = new JModelAndView("admin/blue/company_info_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        List<Area> areas = this.areaService.query(
            "select obj from Area obj where obj.parent.id is null", null, -1, -1);

        mv.addObject("areas", areas);
        if (id != null && !"".equals(id)) {
            CompanyInfo companyInfo = this.companyInfoService.getObjById(CommUtil.null2Long(id));
            mv.addObject("obj", companyInfo);
            mv.addObject("edit", Boolean.valueOf(true));
        }
        return mv;
    }

    @SecurityMapping(title = "供应商保存", value = "/admin/store_save.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({ "/admin/company_info_save.htm" })
    @Transactional
    public ModelAndView company_info_save(HttpServletRequest request, HttpServletResponse response,
                                          String id, String list_url, String add_url,
                                          String currentPage, String userName, String password,
                                          String area_id, String validitybegin, String validity,
                                          String store_status) throws Exception {
        WebForm wf = new WebForm();
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("list_url", list_url);
        mv.addObject("op_title", "保存店铺成功");

        CompanyInfo company = null;
        boolean reg = true;
        if ("".equals(id)) {
            //1、保存用户信息
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("userName", userName);
            params.put("mobile", userName);
            List<User> users = this.userService
                .query(
                    "select obj from User obj where obj.disabled=false and obj.userRole='BUYER_SELLER' and (obj.userName=:userName or obj.mobile=:mobile)",
                    params, -1, -1);
            if ((users != null) && (users.size() > 0)) {
                reg = false;
            }
            if (reg) {
                User user = new User();
                user.setUserName(userName);
                user.setMobile(userName);
                user.setUserRole("BUYER");
                user.setCreatetime(new Date());
                user.setPassword(Md5Encrypt.md5(password).toLowerCase());
                params.clear();
                params.put("type", "BUYER");
                List<Role> roles = this.roleService.query(
                    "select obj from Role obj where obj.type=:type", params, -1, -1);
                user.getRoles().addAll(roles);
                this.userService.save(user);

                Album album = new Album();
                album.setCreatetime(new Date());
                album.setAlbum_default(true);
                album.setAlbum_name("默认相册");
                album.setAlbum_sequence(-10000);
                album.setUser(user);
                this.albumService.save(album);

                //2、生成店铺
                Store store = new Store();
                StoreGrade grade = this.storeGradeService.getObjById(Long.valueOf(Long
                    .parseLong("1")));//默认基本
                store.setGrade(grade);
                store.setTemplate("default");
                store.setCreatetime(new Date());

                store.setDelivery_begin_time(new Date());
                store.setDelivery_end_time(new Date(2114, 11, 29));
                store.setCombin_begin_time(new Date());
                store.setCombin_end_time(new Date(2114, 11, 29));
                store.setStore_status(CommUtil.null2Int(store_status));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                if (validitybegin != null && !"".equals(validitybegin)) {
                    Date date = sdf.parse(validitybegin);
                    store.setValiditybegin(date);
                } else {
                    store.setValiditybegin(new Date());
                    ;
                }
                if (validity != null && !"".equals(validity)) {
                    Date date = sdf.parse(validity);
                    store.setValidity(date);
                }
                this.storeService.save(store);

                
              //保存商店并且审核通过自动加入三个商品标签
                if (CommUtil.null2Int(store_status)==2) {
                	//1热卖商品
					GoodsLabel goodsLabel1=new GoodsLabel();
					goodsLabel1.setCreatetime(new Date());
					goodsLabel1.setSequence(0);
					goodsLabel1.setStatus(0);
					goodsLabel1.setStore(store);
					goodsLabel1.setDisabled(false);
					goodsLabel1.setName("热卖商品");
					goodsLabel1.setCreateuser(user);
					goodsLabelService.save(goodsLabel1);
					//2新品上市
					GoodsLabel goodsLabel2=new GoodsLabel();
					goodsLabel2.setCreatetime(new Date());
					goodsLabel2.setSequence(0);
					goodsLabel2.setStatus(0);
					goodsLabel2.setStore(store);
					goodsLabel2.setDisabled(false);
					goodsLabel2.setName("新品上市");
					goodsLabel2.setCreateuser(user);
					goodsLabelService.save(goodsLabel2);
					//3推荐商品
					GoodsLabel goodsLabel3=new GoodsLabel();
					goodsLabel3.setCreatetime(new Date());
					goodsLabel3.setSequence(0);
					goodsLabel3.setStatus(0);
					goodsLabel3.setStore(store);
					goodsLabel3.setDisabled(false);
					goodsLabel3.setName("推荐商品");
					goodsLabel3.setCreateuser(user);
					goodsLabelService.save(goodsLabel3);
				}
                
                user.setStore(store);

                if (user.getUserRole().equals("BUYER")) {
                    user.setUserRole("BUYER_SELLER");
                }
                if (user.getUserRole().equals("ADMIN")) {
                    user.setUserRole("ADMIN_BUYER_SELLER");
                }
                Map storeparams = new HashMap();
                storeparams.put("type", "SELLER");
                List<Role> storeroles = this.roleService.query(
                    "select obj from Role obj where obj.type=:type", storeparams, -1, -1);
                user.getRoles().addAll(storeroles);
                this.userService.update(user);

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
                //3、保存公司信息
                company = (CompanyInfo) wf.toPo(request, CompanyInfo.class);
                company.setCreatetime(new Date());
                company.setDisabled(false);
                company.setUser(user);
                Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
                company.setArea(area);
                this.companyInfoService.save(company);
            } else {
                mv.addObject("list_url", add_url);
                mv.addObject("op_title", "登录账号不能重复");
            }

        } else {
            //审核编辑店铺信息
            //获取供应商公司信息
            CompanyInfo obj = this.companyInfoService.getObjById(CommUtil.null2Long(id));
            //1、保存用户信息
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("userName", userName);
            params.put("mobile", userName);
            params.put("userId", obj.getUser().getId());
            List<User> users = this.userService
                .query(
                    "select obj from User obj where obj.disabled=false and obj.userRole='BUYER_SELLER' and (obj.userName=:userName or obj.mobile=:mobile) and obj.id!=:userId",
                    params, -1, -1);
            if ((users != null) && (users.size() > 0)) {
                reg = false;
            }
            if (reg) {
                User user = obj.getUser();
                user.setUserName(userName);
                user.setMobile(userName);
                if (password != null && !"".equals(password)) {
                    user.setPassword(Md5Encrypt.md5(password).toLowerCase());
                }
                this.userService.save(user);
                Store store = user.getStore();
                if (store != null) {
                    store.setStore_status(CommUtil.null2Int(store_status));
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    if (validitybegin != null && !"".equals(validitybegin)) {
                        Date date = sdf.parse(validitybegin);
                        store.setValiditybegin(date);
                    } else {
                        store.setValiditybegin(new Date());
                    }
                    if (validity != null && !"".equals(validity)) {
                        Date date = sdf.parse(validity);
                        store.setValidity(date);
                    }
                    this.storeService.save(store);
                    //商店审核通过自动加入三个商品标签
                    
                    
                    Map<String , Object> mapGoodslabel=new HashMap<String, Object>();
                    mapGoodslabel.put("store_id", CommUtil.null2Long(store.getId()));
                    List<GoodsLabel> listGoodsLabel=goodsLabelService.query(
                    		"select obj from GoodsLabel obj where obj.disabled=0 and obj.store.id=:store_id "
                    		+" and obj.status=0  order by obj.sequence ASC  ", mapGoodslabel, -1, -1);
                    if (listGoodsLabel!=null && listGoodsLabel.size()<1) {
                    	if (CommUtil.null2Int(store_status)==2) {
                        	//1热卖商品
    						GoodsLabel goodsLabel1=new GoodsLabel();
    						goodsLabel1.setCreatetime(new Date());
    						goodsLabel1.setSequence(0);
    						goodsLabel1.setStatus(0);
    						goodsLabel1.setStore(store);
    						goodsLabel1.setDisabled(false);
    						goodsLabel1.setName("热卖商品");
    						goodsLabel1.setCreateuser(store.getUser());
    						goodsLabelService.save(goodsLabel1);
    						//2新品上市
    						GoodsLabel goodsLabel2=new GoodsLabel();
    						goodsLabel2.setCreatetime(new Date());
    						goodsLabel2.setSequence(0);
    						goodsLabel2.setStatus(0);
    						goodsLabel2.setStore(store);
    						goodsLabel2.setDisabled(false);
    						goodsLabel2.setName("新品上市");
    						goodsLabel2.setCreateuser(store.getUser());
    						goodsLabelService.save(goodsLabel2);
    						//3推荐商品
    						GoodsLabel goodsLabel3=new GoodsLabel();
    						goodsLabel3.setCreatetime(new Date());
    						goodsLabel3.setSequence(0);
    						goodsLabel3.setStatus(0);
    						goodsLabel3.setStore(store);
    						goodsLabel3.setDisabled(false);
    						goodsLabel3.setName("推荐商品");
    						goodsLabel3.setCreateuser(store.getUser());
    						goodsLabelService.save(goodsLabel3);
    					}
					}
                    
                    
                }
                company = (CompanyInfo) wf.toPo(request, obj);
                Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
                company.setArea(area);
                this.companyInfoService.update(company);
            } else {
                mv.addObject("list_url", add_url);
                mv.addObject("op_title", "登录账号不能重复");
            }

        }

        return mv;
    }

    @SecurityMapping(title = "供货删除", value = "/admin/store_del.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
    @RequestMapping({ "/admin/company_info_del.htm" })
    public String company_info_del(HttpServletRequest request, String mulitId, String currentPage)
                                                                                                  throws Exception {
        String msg = "delsuccess";
        try {
            String[] ids = mulitId.split(",");
            for (String id : ids) {
                CompanyInfo companyInfo = this.companyInfoService
                    .getObjById(CommUtil.null2Long(id));
                if (companyInfo != null) {
                    User user = companyInfo.getUser();
                    if (user != null) {
                        user.setDisabled(true);
                        this.userService.update(user);
                        Store store = user.getStore();
                        store.setDisabled(true);
                        store.setStore_status(3);
                        this.storeService.update(store);
                    }
                    companyInfo.setDisabled(true);
                    this.companyInfoService.update(companyInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg = "delerror";
        }
        return "redirect:/admin/user_company.htm?currentPage=" + currentPage;
    }
}
