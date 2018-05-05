package com.javamalls.ctrl.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.javamalls.platform.domain.Role;
import com.javamalls.platform.domain.RoleGroup;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.query.UserQueryObject;
import com.javamalls.platform.service.IEvaluateService;
import com.javamalls.platform.service.IGoodsCartService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IOrderFormLogService;
import com.javamalls.platform.service.IOrderFormService;
import com.javamalls.platform.service.IRoleGroupService;
import com.javamalls.platform.service.IRoleService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;
import com.sun.org.apache.bcel.internal.Constants;

@Controller
public class SubAccountSellerAction {
    @Autowired
    private ISysConfigService    configService;
    @Autowired
    private IUserConfigService   userConfigService;
    @Autowired
    private IUserService         userService;
    @Autowired
    private IStoreService        storeService;
    @Autowired
    private IRoleGroupService    roleGroupService;
    @Autowired
    private IRoleService         roleService;
    @Autowired
    private IEvaluateService     evaluateService;
    @Autowired
    private IGoodsCartService    goodsCartService;
    @Autowired
    private IOrderFormService    orderFormService;
    @Autowired
    private IOrderFormLogService orderFormLogService;
    @Autowired
    private IGoodsService        goodsService;

    @SecurityMapping(title = "子账户列表", value = "/seller/sub_account_list.htm*", rtype = "seller", rname = "子账户管理", rcode = "sub_account_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/sub_account_list.htm" })
    public ModelAndView sub_account_list(HttpServletRequest request, HttpServletResponse response,
                                         String currentPage, String orderBy, String orderType) {
        if (SecurityUserHolder.getCurrentUser().getParent() != null) {
            ModelAndView mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "子账户不能添加子账户");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
            return mv;
        }

        ModelAndView mv = new JModelAndView("user/default/usercenter/sub_account_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Store store = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId())
            .getStore();
        mv.addObject("store", store);
        UserQueryObject uqo = new UserQueryObject(currentPage, mv, orderBy, orderType);
        uqo.addQuery("obj.parent.id", new SysMap("user_ids", SecurityUserHolder.getCurrentUser()
            .getId()), "=");
        uqo.addQuery("obj.disabled", new SysMap("disabled", false), "=");
        //子账户
        uqo.addQuery("obj.salesManState", new SysMap("salesManState",Integer.valueOf(2)),"=");
        IPageList pList = this.userService.list(uqo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        return mv;
    }

    @SecurityMapping(title = "子账户添加", value = "/seller/sub_account_add.htm*", rtype = "seller", rname = "子账户管理", rcode = "sub_account_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/sub_account_add.htm" })
    public ModelAndView sub_account_add(HttpServletRequest request, HttpServletResponse response) {
        if (SecurityUserHolder.getCurrentUser().getParent() != null) {
            ModelAndView mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "子账户不能添加子账户");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
            return mv;
        }

        ModelAndView mv = new JModelAndView("user/default/usercenter/sub_account_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        Store store = user.getStore();
        if (store == null) {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "您尚未开设店铺");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
        }
       /* if (user.getChilds().size() >= store.getGrade().getAcount_num()) {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "您的店铺等级不能继续添加子账户,请升级店铺等级");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/store_grade.htm");
        }*/
        
        mv.addObject("store", store);
        Map params = new HashMap();
        params.put("type", "SELLER");
        List<RoleGroup> rgs = this.roleGroupService
            .query(
                "select obj from RoleGroup obj where obj.type=:type and obj.disabled = 0 order by obj.sequence asc, obj.createtime asc",
                params, -1, -1);
        mv.addObject("rgs", rgs);
        return mv;
    }

    @SecurityMapping(title = "子账户编辑", value = "/seller/sub_account_edit.htm*", rtype = "seller", rname = "子账户管理", rcode = "sub_account_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/sub_account_edit.htm" })
    public ModelAndView sub_account_edit(HttpServletRequest request, HttpServletResponse response,
                                         String id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/sub_account_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        Store store = user.getStore();
        if (store == null) {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "您尚未开设店铺");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
        }
        mv.addObject("store", store);
        Map params = new HashMap();
        params.put("type", "SELLER");
        List<RoleGroup> rgs = this.roleGroupService.query(
            "select obj from RoleGroup obj where obj.type=:type and obj.disabled = 0 order by obj.sequence asc, obj.createtime asc",
            params, -1, -1);
        mv.addObject("rgs", rgs);
        mv.addObject("obj", this.userService.getObjById(CommUtil.null2Long(id)));
        return mv;
    }

    private String clearContent(String inputString) {
        String htmlStr = inputString;
        String textStr = "";
        try {
            String regEx_script = "<[//s]*?script[^>]*?>[//s//S]*?<[//s]*?///[//s]*?script[//s]*?>";
            String regEx_style = "<[//s]*?style[^>]*?>[//s//S]*?<[//s]*?///[//s]*?style[//s]*?>";
            String regEx_html = "<[^>]+>";
            String regEx_html1 = "<[^>]+";
            Pattern p_script = Pattern.compile(regEx_script, 2);
            Matcher m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll("");

            Pattern p_style = Pattern.compile(regEx_style, 2);
            Matcher m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll("");

            Pattern p_html = Pattern.compile(regEx_html, 2);
            Matcher m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll("");

            Pattern p_html1 = Pattern.compile(regEx_html1, 2);
            Matcher m_html1 = p_html1.matcher(htmlStr);
            htmlStr = m_html1.replaceAll("");

            textStr = htmlStr;
        } catch (Exception e) {
            System.err.println("Html2Text: " + e.getMessage());
        }
        return textStr;
    }

    @SecurityMapping(title = "子账户保存", value = "/seller/sub_account_save.htm*", rtype = "seller", rname = "子账户管理", rcode = "sub_account_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/sub_account_save.htm" })
    public void sub_account_save(HttpServletRequest request, HttpServletResponse response,
                                 String id, String userName, String trueName, String sex,
                                 String birthday, String QQ, String telephone, String mobile,
                                 String password, String role_ids) {
        boolean ret = true;
        String msg = "保存成功";
        User parent = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        userName = clearContent(userName);
       if (CommUtil.null2String(id).equals("")) {
            User user = new User();
            user.setCreatetime(new Date());
            user.setUserName(userName);
            user.setTrueName(trueName);
            user.setSex(CommUtil.null2Int(sex));
            user.setBirthday(CommUtil.formatDate(birthday));
            user.setQQ(QQ);
            user.setMobile(mobile);
            user.setTelephone(telephone);
            user.setParent(parent);
            user.setUserRole("BUYER_SELLER");
            user.setPassword(Md5Encrypt.md5(password).toLowerCase());
            user.setSalesManState(2);
            Map params = new HashMap();
            params.put("type", "BUYER");
            List<Role> roles = this.roleService.query(
                "select obj from Role obj where obj.type=:type", params, -1, -1);
            user.getRoles().addAll(roles);
            for (String role_id : role_ids.split(",")) {
                if (!role_id.equals("")) {
                    Role role = this.roleService.getObjById(CommUtil.null2Long(role_id));
                    user.getRoles().add(role);
                }
            }
            ret = this.userService.save(user);
        } else {
            User user = this.userService.getObjById(CommUtil.null2Long(id));
            user.setUserName(userName);
            user.setTrueName(trueName);
            user.setSex(CommUtil.null2Int(sex));
            user.setBirthday(CommUtil.formatDate(birthday));
            user.setQQ(QQ);
            user.setMobile(mobile);
            user.setTelephone(telephone);
            user.setSalesManState(2);
            user.getRoles().clear();
            Map params = new HashMap();
            params.put("type", "BUYER");
            List<Role> roles = this.roleService.query(
                "select obj from Role obj where obj.type=:type", params, -1, -1);
            user.getRoles().addAll(roles);
            for (String role_id : role_ids.split(",")) {
                if (!role_id.equals("")) {
                    Role role = this.roleService.getObjById(CommUtil.null2Long(role_id));
                    user.getRoles().add(role);
                }
            }
            ret = this.userService.update(user);
            msg = "更新成功";
        }
        Map map = new HashMap();
        map.put("ret", Boolean.valueOf(ret));
        map.put("msg", msg);
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(Json.toJson(map, JsonFormat.compact()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SecurityMapping(title = "子账户删除", value = "/seller/sub_account_del.htm*", rtype = "seller", rname = "子账户管理", rcode = "sub_account_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/sub_account_del.htm" })
    public String sub_account_del(HttpServletRequest request, HttpServletResponse response,
                                  String mulitId) {
        User user = this.userService.getObjById(CommUtil.null2Long(mulitId));
        user.getRoles().clear();
        user.setDisabled(true);
        this.userService.update(user);
        return "redirect:sub_account_list.htm";
    }
}
