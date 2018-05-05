package com.javamalls.ctrl.admin.action;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.security.SecurityManager;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.Md5Encrypt;
import com.javamalls.base.tools.WebForm;
import com.javamalls.base.tools.database.DatabaseTools;
import com.javamalls.ctrl.buyer.action.AccountBuyerAction;
import com.javamalls.ctrl.buyer.action.AddressBuyerAction;
import com.javamalls.ctrl.buyer.action.BaseBuyerAction;
import com.javamalls.ctrl.buyer.action.ComplaintBuyerAction;
import com.javamalls.ctrl.buyer.action.ConsultBuyerAction;
import com.javamalls.ctrl.buyer.action.CouponBuyerAction;
import com.javamalls.ctrl.buyer.action.FavoriteBuyerAction;
import com.javamalls.ctrl.buyer.action.HomePageBuyerAction;
import com.javamalls.ctrl.buyer.action.IntegralOrderBuyerAction;
import com.javamalls.ctrl.buyer.action.MessageBuyerAction;
import com.javamalls.ctrl.buyer.action.OrderBuyerAction;
import com.javamalls.ctrl.buyer.action.PredepositBuyerAction;
import com.javamalls.ctrl.buyer.action.PredepositCashBuyerAction;
import com.javamalls.ctrl.buyer.action.ReportBuyerAction;
import com.javamalls.ctrl.seller.action.ActivitySellerAction;
import com.javamalls.ctrl.seller.action.AdvertSellerAction;
import com.javamalls.ctrl.seller.action.AlbumSellerAction;
import com.javamalls.ctrl.seller.action.BargainSellerAction;
import com.javamalls.ctrl.seller.action.BaseSellerAction;
import com.javamalls.ctrl.seller.action.BindSellerAction;
import com.javamalls.ctrl.seller.action.CombinSellerAction;
import com.javamalls.ctrl.seller.action.ComplaintSellerAction;
import com.javamalls.ctrl.seller.action.ConsultSellerAction;
import com.javamalls.ctrl.seller.action.DeliverySellerAction;
import com.javamalls.ctrl.seller.action.GoldSellerAction;
import com.javamalls.ctrl.seller.action.GoodsBrandSellerAction;
import com.javamalls.ctrl.seller.action.GoodsClassSellerAction;
import com.javamalls.ctrl.seller.action.GoodsReturnSellerAction;
import com.javamalls.ctrl.seller.action.GoodsSellerAction;
import com.javamalls.ctrl.seller.action.GroupSellerAction;
import com.javamalls.ctrl.seller.action.OrderSellerAction;
import com.javamalls.ctrl.seller.action.PaymentSellerAction;
import com.javamalls.ctrl.seller.action.RefundSellerAction;
import com.javamalls.ctrl.seller.action.SellerTongjiAction;
import com.javamalls.ctrl.seller.action.SpareGoodsSellerAction;
import com.javamalls.ctrl.seller.action.StoreNavSellerAction;
import com.javamalls.ctrl.seller.action.StorePartnerManageAction;
import com.javamalls.ctrl.seller.action.StoreSellerAction;
import com.javamalls.ctrl.seller.action.SubAccountSellerAction;
import com.javamalls.ctrl.seller.action.TaobaoSellerAction;
import com.javamalls.ctrl.seller.action.TransportSellerAction;
import com.javamalls.ctrl.seller.action.WaterMarkSellerAction;
import com.javamalls.ctrl.seller.action.ZtcSellerAction;
import com.javamalls.front.web.action.ActivityViewAction;
import com.javamalls.front.web.action.AdvertViewAction;
import com.javamalls.front.web.action.ArticleViewAction;
import com.javamalls.front.web.action.BargainViewAction;
import com.javamalls.front.web.action.BrandViewAction;
import com.javamalls.front.web.action.CartViewAction;
import com.javamalls.front.web.action.ChattingViewAction;
import com.javamalls.front.web.action.DeliveryViewAction;
import com.javamalls.front.web.action.DocumentViewAction;
import com.javamalls.front.web.action.FavoriteViewAction;
import com.javamalls.front.web.action.GoodsViewAction;
import com.javamalls.front.web.action.GroupViewAction;
import com.javamalls.front.web.action.IndexViewAction;
import com.javamalls.front.web.action.InstallViewAction;
import com.javamalls.front.web.action.InsuranceViewAction;
import com.javamalls.front.web.action.IntegralViewAction;
import com.javamalls.front.web.action.LoadAction;
import com.javamalls.front.web.action.LoginViewAction;
import com.javamalls.front.web.action.PayViewAction;
import com.javamalls.front.web.action.SearchViewAction;
import com.javamalls.front.web.action.SpareGoodsViewAction;
import com.javamalls.front.web.action.StoreViewAction;
import com.javamalls.front.web.action.UCViewAction;
import com.javamalls.front.web.action.VerifyAction;
import com.javamalls.platform.domain.Res;
import com.javamalls.platform.domain.Role;
import com.javamalls.platform.domain.RoleGroup;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.query.UserQueryObject;
import com.javamalls.platform.service.IOrderFormService;
import com.javamalls.platform.service.IResService;
import com.javamalls.platform.service.IRoleGroupService;
import com.javamalls.platform.service.IRoleService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;

/**管理员管理
 *                       
 * @Filename: AdminManageAction.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class AdminManageAction implements ServletContextAware {
    private ServletContext     servletContext;
    @Autowired
    private IUserService       userService;
    @Autowired
    private ISysConfigService  configService;
    @Autowired
    private IUserConfigService userConfigService;
    @Autowired
    private IRoleService       roleService;
    @Autowired
    private IOrderFormService  orderFormService;
    @Autowired
    private IRoleGroupService  roleGroupService;
    @Autowired
    private DatabaseTools      databaseTools;
    @Autowired
    SecurityManager            securityManager;
    @Autowired
    private IResService        resService;

    @SecurityMapping(title = "管理员列表", value = "/admin/admin_list.htm*", rtype = "admin", rname = "管理员管理", rcode = "admin_manage", rgroup = "设置")
    @RequestMapping({ "/admin/admin_list.htm" })
    public ModelAndView admin_list(String currentPage, String orderBy, String orderType,
                                   HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("admin/blue/admin_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        UserQueryObject uqo = new UserQueryObject(currentPage, mv, orderBy, orderType);
        WebForm wf = new WebForm();
        wf.toQueryPo(request, uqo, User.class, mv);
        uqo.addQuery("obj.userRole", new SysMap("userRole", "ADMIN"), "=");
        uqo.addQuery("obj.userRole", new SysMap("userRole1", "ADMIN_BUYER_SELLER"), "=", "or");
        IPageList pList = this.userService.list(uqo);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        CommUtil.saveIPageList2ModelAndView(url + "/admin/admin_list.htm", "", "", pList, mv);
        mv.addObject("userRole", "ADMIN");
        return mv;
    }

    @SecurityMapping(title = "管理员添加", value = "/admin/admin_add.htm*", rtype = "admin", rname = "管理员管理", rcode = "admin_manage", rgroup = "设置")
    @RequestMapping({ "/admin/admin_add.htm" })
    public ModelAndView admin_add(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("admin/blue/admin_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("type", "ADMIN");
        List<RoleGroup> rgs = this.roleGroupService
            .query(
                "select obj from RoleGroup obj where obj.type=:type and obj.disabled='0' order by obj.sequence asc",
                params, -1, -1);
        mv.addObject("rgs", rgs);
        mv.addObject("op", "admin_add");
        return mv;
    }

    @SecurityMapping(title = "管理员编辑", value = "/admin/admin_edit.htm*", rtype = "admin", rname = "管理员管理", rcode = "admin_manage", rgroup = "设置")
    @RequestMapping({ "/admin/admin_edit.htm" })
    public ModelAndView admin_edit(HttpServletRequest request, HttpServletResponse response,
                                   String id, String op) {
        ModelAndView mv = new JModelAndView("admin/blue/admin_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("type", "ADMIN");
        List<RoleGroup> rgs = this.roleGroupService
            .query(
                "select obj from RoleGroup obj where obj.type=:type and obj.disabled = 0 order by obj.sequence asc",
                params, -1, -1);
        if ((id != null) && (!id.equals(""))) {
            User user = this.userService.getObjById(Long.valueOf(Long.parseLong(id)));
            mv.addObject("obj", user);
        }
        mv.addObject("rgs", rgs);
        mv.addObject("op", op);
        return mv;
    }

    @SecurityMapping(title = "管理员保存", value = "/admin/admin_save.htm*", rtype = "admin", rname = "管理员管理", rcode = "admin_manage", rgroup = "设置")
    @RequestMapping({ "/admin/admin_save.htm" })
    public ModelAndView admin_save(HttpServletRequest request, HttpServletResponse response,
                                   String id, String role_ids, String list_url, String add_url) {
        WebForm wf = new WebForm();
        User user = null;
        if (id.equals("")) {
            user = (User) wf.toPo(request, User.class);
            user.setCreatetime(new Date());
        } else {
            User u = this.userService.getObjById(Long.valueOf(Long.parseLong(id)));
            user = (User) wf.toPo(request, u);
        }
        if ((user.getPassword() == null) || (user.getPassword().equals(""))) {
            user.setPassword("123456");
            user.setPassword(Md5Encrypt.md5(user.getPassword()).toLowerCase());
        } else if (id.equals("")) {
            user.setPassword(Md5Encrypt.md5(user.getPassword()).toLowerCase());
        }
        user.getRoles().clear();
        if (user.getUserRole().equalsIgnoreCase("ADMIN")) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("display", Boolean.valueOf(false));
            params.put("type", "ADMIN");
            params.put("type1", "BUYER");
            List<Role> roles = this.roleService
                .query(
                    "select obj from Role obj where (obj.display=:display and obj.type=:type) or obj.type=:type1",
                    params, -1, -1);
            user.getRoles().addAll(roles);
        }
        String[] rids = role_ids.split(",");
        for (String rid : rids) {
            if (!rid.equals("")) {
                Role role = this.roleService.getObjById(Long.valueOf(Long.parseLong(rid)));
                user.getRoles().add(role);
            }
        }
        if (id.equals("")) {
            this.userService.save(user);
        } else {
            this.userService.update(user);
        }
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("list_url", list_url);
        mv.addObject("op_title", "保存管理员成功");
        if (add_url != null) {
            mv.addObject("add_url", add_url);
        }
        return mv;
    }

    @SecurityMapping(title = "管理员删除", value = "/admin/admin_del.htm*", rtype = "admin", rname = "管理员管理", rcode = "admin_manage", rgroup = "设置")
    @RequestMapping({ "/admin/admin_del.htm" })
    public String admin_del(HttpServletRequest request, String mulitId, String currentPage) {
        String[] ids = mulitId.split(",");
        //判断当前用户不在ids中
        boolean flag = true;
        User usera = SecurityUserHolder.getCurrentUser();
        for (String id : ids) {
            if (id.equals(usera.getId().toString())) {
                flag = false;
            }
        }
        if (flag) {
            for (String id : ids) {
                if (!id.equals("")) {
                    User user = this.userService.getObjById(Long.valueOf(Long.parseLong(id)));
                    if (!user.getUsername().equals("admin")) {
                        this.databaseTools.execute("delete from jm_sys_log where user_id=" +

                        id);
                        this.databaseTools.execute("delete from jm_user_role where user_id=" +

                        id);
                        this.userService.delete(user.getId());
                    }
                }
            }
        }
        return "redirect:admin_list.htm?currentPage=" + currentPage;
    }

    @SecurityMapping(title = "管理员修改密码", value = "/admin/admin_pws.htm*", rtype = "admin", rname = "商城后台管理", rcode = "admin_index", display = false, rgroup = "设置")
    @RequestMapping({ "/admin/admin_pws.htm" })
    public ModelAndView admin_pws(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("admin/blue/admin_pws.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("user",
            this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId()));
        return mv;
    }

    @SecurityMapping(title = "管理员密码保存", value = "/admin/admin_pws_save.htm*", rtype = "admin", rname = "商城后台管理", rcode = "admin_index", display = false, rgroup = "设置")
    @RequestMapping({ "/admin/admin_pws_save.htm" })
    public ModelAndView admin_pws_save(HttpServletRequest request, HttpServletResponse response,
                                       String old_password, String password) {
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        User user = SecurityUserHolder.getCurrentUser();
        if (Md5Encrypt.md5(old_password).toLowerCase().equals(user.getPassword())) {
            user.setPassword(Md5Encrypt.md5(password).toLowerCase());
            this.userService.update(user);
            mv.addObject("op_title", "修改密码成功");
        } else {
            mv = new JModelAndView("admin/blue/error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 0, request, response);
            mv.addObject("op_title", "原密码错误");
        }
        mv.addObject("list_url", CommUtil.getURL(request) + "/admin/admin_pws.htm");
        return mv;
    }

    @RequestMapping({ "/admin/init_role.htm" })
    public String init_role() {
        User current_user = SecurityUserHolder.getCurrentUser();
        if ((current_user != null) && (current_user.getUserRole().indexOf("ADMIN") >= 0)
            && (current_user.getUsername().equals("admin"))) {
            this.databaseTools.execute("delete from jm_role_res");
            this.databaseTools.execute("delete from jm_res");
            this.databaseTools.execute("delete from jm_user_role");
            this.databaseTools.execute("delete from jm_role");
            this.databaseTools.execute("delete from jm_rolegroup");
            List<Class> clzs = new ArrayList<Class>();

            clzs.add(ActivityManageAction.class);
            clzs.add(AdminManageAction.class);
            clzs.add(AdvertManageAction.class);
            clzs.add(AreaManageAction.class);
            clzs.add(ArticleClassManageAction.class);
            clzs.add(ArticleManageAction.class);
            clzs.add(BargainManageAction.class);
            clzs.add(BaseManageAction.class);
            clzs.add(CacheManageAction.class);
            clzs.add(CombinManageAction.class);
            clzs.add(ComplaintManageAction.class);
            clzs.add(ComplaintSubjectManageAction.class);
            clzs.add(ConsultManageAction.class);
            clzs.add(CouponManageAction.class);
            clzs.add(DatabaseManageAction.class);
            clzs.add(DeliveryManageAction.class);
            clzs.add(DocumentManageAction.class);
            clzs.add(EvaluateManageAction.class);
            clzs.add(ExpressCompanyManageAction.class);
            clzs.add(GoldRecordManageAction.class);
            clzs.add(GoodsBrandManageAction.class);
            clzs.add(GoodsBSManageAction.class);
            clzs.add(GoodsClassManageAction.class);
            clzs.add(GoodsFloorManageAction.class);
            clzs.add(GoodsManageAction.class);
            clzs.add(GoodsSpecificationManageAction.class);
            clzs.add(GoodsTypeManageAction.class);
            clzs.add(GroupAreaManageAction.class);
            clzs.add(GroupClassManageAction.class);
            clzs.add(GroupManageAction.class);
            clzs.add(GroupPriceRangeManageAction.class);
            clzs.add(ImageManageAction.class);
            clzs.add(IntegralGoodsManageAction.class);
            clzs.add(IntegralLogManageAction.class);
            clzs.add(LuceneManageAction.class);
            clzs.add(NavigationManageAction.class);
            clzs.add(OperationManageAction.class);
            clzs.add(OrderManageAction.class);
            clzs.add(PartnerManageAction.class);
            clzs.add(PaymentManageAction.class);
            clzs.add(PredepositCashManageAction.class);
            clzs.add(PredepositLogManageAction.class);
            clzs.add(PredepositManageAction.class);
            clzs.add(ReportManageAction.class);
            clzs.add(ReportSubjectManageAction.class);
            clzs.add(ReportTypeManageAction.class);
            clzs.add(SettleAccountsAction.class);
            clzs.add(SettleLogAction.class);
            clzs.add(SnsManageAction.class);
            clzs.add(SpareGoodsFloorManageAction.class);
            clzs.add(SpareGoodsManageAction.class);
            clzs.add(StoreClassManageAction.class);
            clzs.add(StoreGradeManageAction.class);
            clzs.add(StoreManageAction.class);
            clzs.add(TemplateManageAction.class);
            clzs.add(TongjiAction.class);
            clzs.add(TransAreaManageAction.class);
            clzs.add(UcenterManageAction.class);
            clzs.add(UserManageAction.class);
            clzs.add(ZtcManageAction.class);
            clzs.add(ActivitySellerAction.class);
            clzs.add(AdvertSellerAction.class);
            clzs.add(AlbumSellerAction.class);
            clzs.add(BargainSellerAction.class);
            clzs.add(BaseSellerAction.class);
            clzs.add(BindSellerAction.class);
            clzs.add(CombinSellerAction.class);
            clzs.add(ComplaintSellerAction.class);
            clzs.add(ConsultSellerAction.class);
            clzs.add(DeliverySellerAction.class);
            clzs.add(GoldSellerAction.class);
            clzs.add(GoodsBrandSellerAction.class);
            clzs.add(GoodsClassSellerAction.class);
            clzs.add(GoodsReturnSellerAction.class);
            clzs.add(GoodsSellerAction.class);
            clzs.add(GroupSellerAction.class);
            clzs.add(OrderSellerAction.class);
            clzs.add(PaymentSellerAction.class);
            clzs.add(RefundSellerAction.class);
            clzs.add(SellerTongjiAction.class);
            clzs.add(SpareGoodsSellerAction.class);
            clzs.add(StoreNavSellerAction.class);
            clzs.add(StorePartnerManageAction.class);
            clzs.add(StoreSellerAction.class);
            clzs.add(SubAccountSellerAction.class);
            clzs.add(TaobaoSellerAction.class);
            clzs.add(TransportSellerAction.class);
            clzs.add(WaterMarkSellerAction.class);
            clzs.add(ZtcSellerAction.class);
            clzs.add(AccountBuyerAction.class);
            clzs.add(AddressBuyerAction.class);
            clzs.add(BaseBuyerAction.class);
            clzs.add(ComplaintBuyerAction.class);
            clzs.add(ConsultBuyerAction.class);
            clzs.add(CouponBuyerAction.class);
            clzs.add(FavoriteBuyerAction.class);
            clzs.add(HomePageBuyerAction.class);
            clzs.add(IntegralOrderBuyerAction.class);
            clzs.add(MessageBuyerAction.class);
            clzs.add(OrderBuyerAction.class);
            clzs.add(PredepositBuyerAction.class);
            clzs.add(PredepositCashBuyerAction.class);
            clzs.add(ReportBuyerAction.class);
            clzs.add(ActivityViewAction.class);
            clzs.add(AdvertViewAction.class);
            clzs.add(ArticleViewAction.class);
            clzs.add(BargainViewAction.class);
            clzs.add(BrandViewAction.class);
            clzs.add(CartViewAction.class);
            clzs.add(ChattingViewAction.class);
            clzs.add(DeliveryViewAction.class);
            clzs.add(DocumentViewAction.class);
            clzs.add(FavoriteViewAction.class);
            clzs.add(GoodsViewAction.class);
            clzs.add(GroupViewAction.class);
            clzs.add(IndexViewAction.class);
            clzs.add(InstallViewAction.class);
            clzs.add(InsuranceViewAction.class);
            clzs.add(IntegralViewAction.class);
            clzs.add(LoadAction.class);
            clzs.add(LoginViewAction.class);
            clzs.add(PayViewAction.class);
            clzs.add(SearchViewAction.class);
            clzs.add(SpareGoodsViewAction.class);
            clzs.add(StoreViewAction.class);
            clzs.add(UCViewAction.class);
            clzs.add(VerifyAction.class);

            int sequence = 0;
            Annotation[] annotation;
            for (Class clz : clzs) {
                try {
                    Method[] ms = clz.getMethods();
                    for (Method m : ms) {
                        annotation = m.getAnnotations();
                        for (Annotation tag : annotation) {
                            if (SecurityMapping.class.isAssignableFrom(tag.annotationType())) {
                                String value = ((SecurityMapping) tag).value();
                                Map<String, Object> params = new HashMap<String, Object>();
                                params.put("value", value);
                                List<Res> ress = this.resService.query(
                                    "select obj from Res obj where obj.value=:value", params, -1,
                                    -1);
                                if (ress.size() == 0) {
                                    Res res = new Res();
                                    res.setResName(((SecurityMapping) tag).title());
                                    res.setValue(value);
                                    res.setType("URL");
                                    res.setCreatetime(new Date());
                                    this.resService.save(res);
                                    String rname = ((SecurityMapping) tag).rname();
                                    String roleCode = ((SecurityMapping) tag).rcode();
                                    if (roleCode.indexOf("ROLE_") != 0) {
                                        roleCode = ("ROLE_" + roleCode).toUpperCase();
                                    }
                                    params.clear();
                                    params.put("roleCode", roleCode);
                                    List<Role> roles = this.roleService.query(
                                        "select obj from Role obj where obj.roleCode=:roleCode",
                                        params, -1, -1);
                                    Role role = null;
                                    if (roles.size() > 0) {
                                        role = (Role) roles.get(0);
                                    }
                                    if (role == null) {
                                        role = new Role();
                                        role.setRoleName(rname);
                                        role.setRoleCode(roleCode.toUpperCase());
                                    }
                                    role.getReses().add(res);
                                    res.getRoles().add(role);
                                    role.setCreatetime(new Date());
                                    role.setDisplay(((SecurityMapping) tag).display());
                                    role.setType(((SecurityMapping) tag).rtype().toUpperCase());

                                    String groupName = ((SecurityMapping) tag).rgroup();
                                    RoleGroup rg = this.roleGroupService.getObjByProperty("name",
                                        groupName);
                                    if (rg == null) {
                                        rg = new RoleGroup();
                                        rg.setCreatetime(new Date());
                                        rg.setName(groupName);
                                        rg.setSequence(sequence);
                                        rg.setType(role.getType());
                                        this.roleGroupService.save(rg);
                                    }
                                    role.setRg(rg);
                                    this.roleService.save(role);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sequence++;
            }
            User user = this.userService.getObjByProperty("userName", "admin");
            Object params = new HashMap();
            List<Role> roles = this.roleService.query(
                "select obj from Role obj order by obj.createtime desc", null, -1, -1);
            if (user == null) {
                user = new User();
                user.setUserName("admin");
                user.setUserRole("ADMIN");
                user.setPassword(Md5Encrypt.md5("123456").toLowerCase());
                for (Role role : roles) {
                    if (!role.getType().equalsIgnoreCase("SELLER")) {
                        user.getRoles().add(role);
                    }
                }
                this.userService.save(user);
            } else {
                for (Role role : roles) {
                    if (!role.getType().equals("SELLER")) {
                        System.out.println(role.getRoleName() + " " + role.getType() + " "
                                           + role.getRoleCode());
                        user.getRoles().add(role);
                    }
                }
                this.userService.update(user);
            }
            ((Map) params).clear();
            ((Map) params).put("display", Boolean.valueOf(false));
            ((Map) params).put("type", "ADMIN");
            List<Role> admin_roles = this.roleService.query(
                "select obj from Role obj where obj.display=:display and obj.type=:type",
                (Map) params, -1, -1);
            ((Map) params).clear();
            ((Map) params).put("type", "BUYER");
            Object buyer_roles = this.roleService.query(
                "select obj from Role obj where obj.type=:type", (Map) params, -1, -1);
            ((Map) params).clear();
            ((Map) params).put("userRole", "ADMIN");
            ((Map) params).put("userName", "admin");
            List<User> admins = this.userService
                .query(
                    "select obj from User obj where obj.userRole=:userRole and obj.userName!=:userName",
                    (Map) params, -1, -1);
            for (User admin : admins) {
                admin.getRoles().addAll(admin_roles);
                admin.getRoles().addAll((Collection) buyer_roles);
                this.userService.update(admin);
            }
            ((Map) params).clear();
            ((Map) params).put("userRole", "BUYER");
            List<User> buyers = this.userService.query(
                "select obj from User obj where obj.userRole=:userRole", (Map) params, -1, -1);
            for (User buyer : buyers) {
                buyer.getRoles().addAll((Collection) buyer_roles);
                this.userService.update(buyer);
            }
            ((Map) params).clear();
            ((Map) params).put("type1", "BUYER");
            ((Map) params).put("type2", "SELLER");
            List<Role> seller_roles = this.roleService.query(
                "select obj from Role obj where (obj.type=:type1 or obj.type=:type2)",
                (Map) params, -1, -1);
            ((Map) params).clear();
            ((Map) params).put("userRole1", "BUYER_SELLER");
            ((Map) params).put("userRole2", "ADMIN_BUYER_SELLER");
            ((Map) params).put("userRole3", "ADMIN");
            ((Map) params).put("userName", "admin");
            List<User> sellers = this.userService
                .query(
                    "select obj from User obj where (obj.userRole=:userRole1 or obj.userRole=:userRole2 or obj.userRole=:userRole3) and obj.userName!=:userName ",
                    (Map) params, -1, -1);
            for (User seller : sellers) {
                seller.getRoles().addAll((Collection) buyer_roles);
                seller.getRoles().addAll(seller_roles);
                this.userService.update(seller);
            }
            Object urlAuthorities = this.securityManager.loadUrlAuthorities();
            this.servletContext.setAttribute("urlAuthorities", urlAuthorities);
            return "redirect:admin_list.htm";
        }
        return "redirect:login.htm";
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
