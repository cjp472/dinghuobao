package com.javamalls.ctrl.admin.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
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
import com.javamalls.base.tools.Md5Encrypt;
import com.javamalls.base.tools.WebForm;
import com.javamalls.ctrl.admin.tools.StoreTools;
import com.javamalls.platform.domain.Album;
import com.javamalls.platform.domain.Area;
import com.javamalls.platform.domain.Evaluate;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.GoodsCart;
import com.javamalls.platform.domain.Message;
import com.javamalls.platform.domain.Role;
import com.javamalls.platform.domain.StoreGrade;
import com.javamalls.platform.domain.SysConfig;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.query.UserQueryObject;
import com.javamalls.platform.service.IAlbumService;
import com.javamalls.platform.service.IAreaService;
import com.javamalls.platform.service.IEvaluateService;
import com.javamalls.platform.service.IGoodsCartService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IMessageService;
import com.javamalls.platform.service.IOrderFormService;
import com.javamalls.platform.service.IRoleService;
import com.javamalls.platform.service.IStoreGradeService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;

/**
 *                     会员管理  
 * @Filename: UserManageAction.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class UserManageAction {
    @Autowired
    private ISysConfigService    configService;
    @Autowired
    private IUserConfigService   userConfigService;
    @Autowired
    private IUserService         userService;
    @Autowired
    private IRoleService         roleService;
    @Autowired
    private IStoreGradeService   storeGradeService;
    @Autowired
    private IMessageService      messageService;
    @Autowired
    private IAlbumService        albumService;
    @Autowired
    private IEvaluateService     evaluateService;
    @Autowired
    private IGoodsCartService    goodsCartService;
    @Autowired
    private IOrderFormService    orderFormService;
    @Autowired
    private IGoodsService        goodsService;
    @Autowired
    private StoreTools           storeTools;
    @Autowired
    private IAreaService         areaService;

    @SecurityMapping(title = "会员添加", value = "/admin/user_add.htm*", rtype = "admin", rname = "会员管理", rcode = "user_manage", rgroup = "会员")
    @RequestMapping({ "/admin/user_add.htm" })
    public ModelAndView user_add(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("admin/blue/user_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        return mv;
    }

    @SecurityMapping(title = "会员编辑", value = "/admin/user_edit.htm*", rtype = "admin", rname = "会员管理", rcode = "user_manage", rgroup = "会员")
    @RequestMapping({ "/admin/user_edit.htm" })
    public ModelAndView user_edit(HttpServletRequest request, HttpServletResponse response,
                                  String id, String op) {
        ModelAndView mv = new JModelAndView("admin/blue/user_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("obj", this.userService.getObjById(Long.valueOf(Long.parseLong(id))));
        mv.addObject("edit", Boolean.valueOf(true));
        return mv;
    }

    @SecurityMapping(title = "会员列表", value = "/admin/user_list.htm*", rtype = "admin", rname = "会员管理", rcode = "user_manage", rgroup = "会员")
    @RequestMapping({ "/admin/user_list.htm" })
    public ModelAndView user_list(HttpServletRequest request, HttpServletResponse response,
                                  String currentPage, String orderBy, String orderType,
                                  String condition, String value) {
        String msg = request.getParameter("msg");

        ModelAndView mv = new JModelAndView("admin/blue/user_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);

        if ("".equals(CommUtil.null2String(orderBy))) {
            orderBy = "loginDate";
            orderType = "desc";
        }
        UserQueryObject uqo = new UserQueryObject(currentPage, mv, orderBy, orderType);
        WebForm wf = new WebForm();
        wf.toQueryPo(request, uqo, User.class, mv);
        uqo.addQuery("obj.userRole", new SysMap("userRole", "ADMIN"), "!=");

        if (condition != null)
            mv.addObject("condition", condition);
        if (value != null)
            mv.addObject("value", value);

        if (condition != null && (!value.equals(""))) {

            if (condition.equals("userName")) {
                uqo.addQuery("obj.userName", new SysMap("userName", "%" + value + "%"), "like");
            }
            if (condition.equals("email")) {
                uqo.addQuery("obj.email", new SysMap("email", "%" + value + "%"), "like");
            }
            if (condition.equals("trueName")) {
                uqo.addQuery("obj.trueName", new SysMap("trueName", "%" + value + "%"), "like");
            }
        }
        uqo.addQuery("obj.parent.id is null ", null);
        IPageList pList = this.userService.list(uqo);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        CommUtil.saveIPageList2ModelAndView(url + "/admin/user_list.htm", "", "", pList, mv);
        mv.addObject("userRole", "USER");
        mv.addObject("storeTools", this.storeTools);
        mv.addObject("msg", msg);
        return mv;
    }

    @SecurityMapping(title = "会员保存", value = "/admin/user_save.htm*", rtype = "admin", rname = "会员管理", rcode = "user_manage", rgroup = "会员")
    @RequestMapping({ "/admin/user_save.htm" })
    public ModelAndView user_save(HttpServletRequest request, HttpServletResponse response,
                                  String id, String role_ids, String list_url, String add_url,
                                  String password) {
        WebForm wf = new WebForm();
        User user = null;
        if (id.equals("")) {
            user = (User) wf.toPo(request, User.class);
            user.setCreatetime(new Date());
        } else {
            User u = this.userService.getObjById(Long.valueOf(Long.parseLong(id)));
            user = (User) wf.toPo(request, u);
        }
        if ((password != null) && (!password.equals(""))) {
            user.setPassword(Md5Encrypt.md5(password).toLowerCase());
        }
        if (id.equals("")) {
            user.setUserRole("BUYER");
            user.getRoles().clear();
            Map<String, Object> params = new HashMap<String, Object>();
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
        } else {
            this.userService.update(user);
        }
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("list_url", list_url);
        mv.addObject("op_title", "保存用户成功");
        if (add_url != null) {
            mv.addObject("add_url", add_url);
        }
        return mv;
    }

    @SecurityMapping(title = "会员删除", value = "/admin/user_del.htm*", rtype = "admin", rname = "会员管理", rcode = "user_manage", rgroup = "会员")
    @RequestMapping({ "/admin/user_del.htm" })
    public String user_del(HttpServletRequest request, String mulitId, String currentPage) {
        String msg = "delsuccess";
        try {
            String[] ids = mulitId.split(",");
            for (String id : ids) {
                if (!id.equals("")) {
                    User parent = this.userService.getObjById(Long.valueOf(Long.parseLong(id)));
                    if (!parent.getUsername().equals("admin")) {
                        Long ofid;
                        for (User user : parent.getChilds()) {
                            user.getRoles().clear();
                            if (user.getStore() != null) {
                                for (Goods goods : user.getStore().getGoods_list()) {
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    map.put("gid", goods.getId());
                                    List<GoodsCart> goodCarts = this.goodsCartService.query(
                                        "select obj from GoodsCart obj where obj.goods.id = :gid",
                                        map, -1, -1);
                                    ofid = null;
                                    Map<String, Object> map2;
                                    for (GoodsCart gc : goodCarts) {
                                        ofid = gc.getOf().getId();
                                        this.goodsCartService.delete(gc.getId());
                                        map2 = new HashMap<String, Object>();
                                        map2.put("ofid", ofid);
                                        List<GoodsCart> goodCarts2 = this.goodsCartService
                                            .query(
                                                "select obj from GoodsCart obj where obj.of.id = :ofid",
                                                map2, -1, -1);
                                        if (goodCarts2.size() == 0) {
                                            this.orderFormService.delete(ofid);
                                        }
                                    }
                                    List<Evaluate> evaluates = goods.getEvaluates();
                                    for (Evaluate e : evaluates) {
                                        this.evaluateService.delete(e.getId());
                                    }
                                    goods.getGoods_ugcs().clear();
                                    this.goodsService.delete(goods.getId());
                                }
                            }
                            this.userService.delete(user.getId());
                        }
                        parent.getRoles().clear();
                        if (parent.getStore() != null) {
                            for (Goods goods : parent.getStore().getGoods_list()) {
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("gid", goods.getId());
                                List<GoodsCart> goodCarts = this.goodsCartService.query(
                                    "select obj from GoodsCart obj where obj.goods.id = :gid", map,
                                    -1, -1);
                                Map<String, Object> map2;
                                for (GoodsCart gc : goodCarts) {
                                    ofid = gc.getOf().getId();
                                    this.goodsCartService.delete(gc.getId());
                                    map2 = new HashMap<String, Object>();
                                    map2.put("ofid", ofid);
                                    Object goodCarts2 = this.goodsCartService.query(
                                        "select obj from GoodsCart obj where obj.of.id = :ofid",
                                        map2, -1, -1);
                                    if (((List) goodCarts2).size() == 0) {
                                        this.orderFormService.delete(ofid);
                                    }
                                }
                                List<Evaluate> evaluates = goods.getEvaluates();
                                for (Evaluate e : evaluates) {
                                    this.evaluateService.delete(e.getId());
                                }
                                goods.getGoods_ugcs().clear();
                                this.goodsService.delete(goods.getId());
                            }
                        }
                        this.userService.delete(parent.getId());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg = "delerror";
        }
        return "redirect:user_list.htm?currentPage=" + currentPage + "&msg=" + msg;
    }

    @SecurityMapping(title = "会员通知", value = "/admin/user_msg.htm*", rtype = "admin", rname = "会员通知", rcode = "user_msg", rgroup = "会员")
    @RequestMapping({ "/admin/user_msg.htm" })
    public ModelAndView user_msg(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("admin/blue/user_msg.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        List<StoreGrade> grades = this.storeGradeService.query(
            "select obj from StoreGrade obj order by obj.sequence asc", null, -1, -1);
        mv.addObject("grades", grades);
        List<Area> areas = this.areaService.query(
            "select obj from Area obj where obj.parent.id is null", null, -1, -1);
        mv.addObject("areas", areas);
        return mv;
    }

    @SecurityMapping(title = "会员通知发送", value = "/admin/user_msg_send.htm*", rtype = "admin", rname = "会员通知", rcode = "user_msg", rgroup = "会员")
    @RequestMapping({ "/admin/user_msg_send.htm" })
    public ModelAndView user_msg_send(HttpServletRequest request, HttpServletResponse response,
                                      String type, String list_url, String users, String grades,
                                      String content) throws IOException {
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        List<User> user_list = new ArrayList<User>();
        if (type.equals("all_user")) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("userRole", "ADMIN");
            user_list = this.userService
                .query(
                    "select obj from User obj where obj.userRole!=:userRole order by obj.createtime desc",
                    params, -1, -1);
        }
        if (type.equals("the_user")) {
            String usertype = request.getParameter("usertype");
            if (usertype.equals("usertypename")) {
                List<String> user_names = CommUtil.str2list(users);
                for (String user_name : user_names) {
                    User user = this.userService.getObjByProperty("userName", user_name);
                    user_list.add(user);
                }
            } else {
                String areas_province = request.getParameter("areas_province");
                String areas_city = request.getParameter("areas_city");
                String area_id = request.getParameter("area_id");// 地区
                String user_credit = request.getParameter("user_credit");// 等级
                String sex = request.getParameter("sex");// 性别
                StringBuffer sql = new StringBuffer("select obj from User obj where 1=1 ");
                Map<String, Object> paramarea = new HashMap<String, Object>();
                if (!"".equals(user_credit) && null != user_credit) {
                    sql.append(" and obj.user_credit=:user_credit ");
                    paramarea.put("user_credit", Integer.valueOf(user_credit));
                }
                if (!"".equals(sex) && null != sex) {
                    sql.append(" and obj.sex=:sex ");
                    paramarea.put("sex", Integer.valueOf(sex));
                }
                Set<Long> area_ids = new HashSet<Long>();
                if (!"".equals(area_id) && null != area_id&&!"请选择...".equals(area_id)) {
                    area_ids.add(Long.valueOf(area_id));
                } else {
                    Map<String, Object> paramMap = new HashMap<String, Object>();
                    List<Area> areas2 = new ArrayList<Area>();
                    if (!"".equals(areas_city) && null != areas_city) {
                        paramMap.put("area_id", Long.valueOf(areas_city));
                        areas2 = this.areaService.query(
                            "select obj from Area obj where obj.id=:area_id",
                            paramMap, -1, -1);
                        area_ids = getArrayAreaChildIds2(areas2);
                    } else {
                        // areas_province
                        if (!"请选择...".equals(areas_province)) {
                            paramMap.put("area_id", Long.valueOf(areas_province));
                            areas2 = this.areaService.query(
                                "select obj from Area obj where obj.id=:area_id",
                                paramMap, -1, -1);
                            area_ids = getArrayAreaChildIds(areas2);
                        }
                    }
                }

                if (area_ids.size() > 0) {
                    paramarea.put("area_ids", area_ids);
                    sql.append(" and obj.area.id in (:area_ids) ");
                }

                user_list = this.userService.query(sql.toString(), paramarea, -1, -1);

            }
        }
        if (type.equals("all_store")) {
            user_list = this.userService
                .query(
                    "select obj from User obj where obj.store.id is not null order by obj.createtime desc",
                    null, -1, -1);
        }
        Set<Long> store_grade_ids;
        if (type.equals("the_store")) {
            Map<String, Object> params = new HashMap<String, Object>();
            store_grade_ids = new TreeSet<Long>();
            String[] arrayOfString = grades.split(",");
            for (int i = 0; i < arrayOfString.length; i++) {
                String grade = arrayOfString[i];
                store_grade_ids.add(Long.valueOf(Long.parseLong(grade)));
            }
            params.put("store_grade_ids", store_grade_ids);
            user_list = this.userService.query(
                "select obj from User obj where obj.store.grade.id in(:store_grade_ids)", params, -1, -1);
        }
        for (User user : user_list) {
            Message msg = new Message();
            msg.setCreatetime(new Date());
            msg.setContent(content);
            msg.setFromUser(SecurityUserHolder.getCurrentUser());
            msg.setToUser(user);
            this.messageService.save(msg);
        }
        mv.addObject("op_title", "会员通知发送成功");
        mv.addObject("list_url", list_url);
        return mv;
    }

    private Set<Long> getArrayAreaChildIds2(List<Area> areas) {
        Set<Long> ids = new HashSet<Long>();
        for (Area area : areas) {
            ids.add(area.getId());
        }
        return ids;
    }

    private Set<Long> getArrayAreaChildIds(List<Area> areas) {
        Set<Long> ids = new HashSet<Long>();
        for (Area area : areas) {
            ids.add(area.getId());
            for (Area are : area.getChilds()) {
                Set<Long> cids = getAreaChildIds(are);
                for (Long cid : cids) {
                    ids.add(cid);
                }
            }
        }
        return ids;
    }

    private Set<Long> getAreaChildIds(Area area) {
        Set<Long> ids = new HashSet<Long>();
        ids.add(area.getId());
        Iterator localIterator2;
        for (Area are : area.getChilds()) {
            Set<Long> cids = getAreaChildIds(are);
            for (Long cid : cids) {
                ids.add(cid);
            }
        }
        return ids;
    }

    @SecurityMapping(title = "会员信用", value = "/admin/user_creditrule.htm*", rtype = "admin", rname = "会员信用", rcode = "user_creditrule", rgroup = "会员")
    @RequestMapping({ "/admin/user_creditrule.htm" })
    public ModelAndView user_creditrule(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("admin/blue/user_creditrule.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        return mv;
    }

    @SecurityMapping(title = "买家信用保存", value = "/admin/user_creditrule_save.htm*", rtype = "admin", rname = "会员信用", rcode = "user_creditrule", rgroup = "会员")
    @RequestMapping({ "/admin/user_creditrule_save.htm" })
    public ModelAndView user_creditrule_save(HttpServletRequest request,
                                             HttpServletResponse response, String id,
                                             String list_url) {
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        SysConfig sc = this.configService.getSysConfig();
        Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i <= 29; i++) {
            map.put("creditrule" + i,
                Integer.valueOf(CommUtil.null2Int(request.getParameter("creditrule" + i))));
        }
        String user_creditrule = Json.toJson(map, JsonFormat.compact());
        sc.setUser_creditrule(user_creditrule);
        if (id.equals("")) {
            this.configService.save(sc);
        } else {
            this.configService.update(sc);
        }
        mv.addObject("list_url", list_url);
        mv.addObject("op_title", "保存会员信用成功");
        return mv;
    }
}
