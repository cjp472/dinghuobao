package com.javamalls.ctrl.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
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
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.StoreDepartment;
import com.javamalls.platform.domain.StoreNavigation;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.UserConfig;
import com.javamalls.platform.domain.UserGoodsClass;
import com.javamalls.platform.domain.query.StoreDepartmentQueryObject;
import com.javamalls.platform.domain.query.StoreNavigationQueryObject;
import com.javamalls.platform.service.IStoreDepartmentService;
import com.javamalls.platform.service.IStoreNavigationService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;

/**
 * 
 * @author cjl
 * 部门列表  Controller
 *
 */
@Controller
public class StoreDepartmentSellerAction {
    @Autowired
    private ISysConfigService       configService;
    @Autowired
    private IUserConfigService      userConfigService;
    @Autowired
    private IStoreDepartmentService storeDepartmentService ;
    @Autowired
    private IStoreService           storeService;
    @Autowired
    private IUserService userService;

    @SecurityMapping(title = "供应商部门管理", value = "/seller/store_department.htm*", rtype = "seller", rname = "部门管理", rcode = "store_department_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/store_department.htm" }) 
    public ModelAndView store_department(HttpServletRequest request, HttpServletResponse response,
                                  String currentPage, String orderBy, String orderType) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/store_department.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        String params = "";
        StoreDepartmentQueryObject qo = new StoreDepartmentQueryObject(currentPage, mv, orderBy,
            orderType);
        Store store = this.storeService.getObjByProperty("id", SecurityUserHolder.getCurrentUser()
            .getStore().getId());
        qo.addQuery("obj.store.id", new SysMap("store_id", store.getId()), "=");
        qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");
        qo.addQuery("obj.level", new SysMap("level",0),"=");
        IPageList pList = this.storeDepartmentService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/seller/store_department.htm", "", params, pList, mv);
        return mv;
    }
 
    @SecurityMapping(title = "供应商部门添加", value = "/seller/store_department_add.htm*", rtype = "seller", rname = "部门管理", rcode = "store_department_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/store_department_add.htm" })
    public ModelAndView store_department_add(HttpServletRequest request, HttpServletResponse response,
                                      String currentPage,String pid) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/store_department_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Store store = this.storeService.getObjByProperty("id", SecurityUserHolder.getCurrentUser().getStore().getId());
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("storeId", store.getId());
        List<StoreDepartment> sds = this.storeDepartmentService
            .query(
                "select obj from StoreDepartment obj where obj.parent.id is null and obj.disabled=false and obj.store.id = :storeId order by obj.sequence asc",
                map, -1, -1);
        if (pid!=null&&!CommUtil.null2String(pid).equals("")) {
        	StoreDepartment parent = this.storeDepartmentService.getObjById(CommUtil.null2Long(pid));
        	StoreDepartment obj = new StoreDepartment();
            obj.setParent(parent);
            mv.addObject("obj", obj);
        }
        mv.addObject("sds", sds);
        mv.addObject("currentPage", currentPage);
        return mv;
    }

    @SecurityMapping(title = "供应商部门编辑", value = "/seller/store_department_edit.htm*", rtype = "seller", rname = "部门管理", rcode = "store_department_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/store_department_edit.htm" })
    public ModelAndView store_department_edit(HttpServletRequest request, HttpServletResponse response,
                                       String id, String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/store_department_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Store store = this.storeService.getObjByProperty("id", SecurityUserHolder.getCurrentUser().getStore().getId());
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("storeId", store.getId());
        List<StoreDepartment> sds = this.storeDepartmentService
            .query(
                "select obj from StoreDepartment obj where obj.parent.id is null and obj.disabled=false and obj.store.id = :storeId order by obj.sequence asc",
                map, -1, -1);
        if ((id != null) && (!id.equals(""))) {
            StoreDepartment storeDepartment = this.storeDepartmentService.getObjById(Long
                .valueOf(Long.parseLong(id)));
            mv.addObject("obj", storeDepartment);
            mv.addObject("currentPage", currentPage);
            mv.addObject("edit", Boolean.valueOf(true));
        }
        mv.addObject("sds", sds);
        return mv;
    }

    @SecurityMapping(title = "供应商部门保存", value = "/seller/store_department_save.htm*", rtype = "seller", rname = "部门管理", rcode = "store_department_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/store_department_save.htm" })
    public ModelAndView store_department_save(HttpServletRequest request, HttpServletResponse response,
                                       String id, String currentPage, String cmd,String pid) {
        WebForm wf = new WebForm();
        StoreDepartment storeDepartment = null;
        if (id.equals("")) {
        	storeDepartment = (StoreDepartment) wf.toPo(request, StoreDepartment.class);
        	storeDepartment.setCreatetime(new Date());
        } else {
        	StoreDepartment obj = this.storeDepartmentService.getObjById(Long.valueOf(Long
                .parseLong(id)));
        	storeDepartment = (StoreDepartment) wf.toPo(request, obj);
        }
        Store store = this.storeService.getObjByProperty("id", SecurityUserHolder.getCurrentUser()
            .getStore().getId());
        storeDepartment.setStore(store);
        if (!pid.equals("")) {
        	StoreDepartment parent = this.storeDepartmentService.getObjById(Long.valueOf(Long
                .parseLong(pid)));
        	storeDepartment.setParent(parent);
        	int level = parent.getLevel()+1;
        	storeDepartment.setLevel(level);
        }
        
        if (id.equals("")) {
            this.storeDepartmentService.save(storeDepartment);
        } else {
            this.storeDepartmentService.update(storeDepartment);
        }
        ModelAndView mv = new JModelAndView("user/default/usercenter/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("url", CommUtil.getURL(request) + "/seller/store_department.htm");
        mv.addObject("op_title", "保存部门成功");
        return mv;
    }

    @SecurityMapping(title = "供应商部门删除", value = "/seller/store_department_del.htm*", rtype = "seller", rname = "部门管理", rcode = "store_department_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/store_department_del.htm" })
    public String store_department_del(HttpServletRequest request, HttpServletResponse response,
                                String mulitId, String currentPage) {
        String[] ids = mulitId.split(",");
        for (String id : ids) {
            if (!id.equals("")) {
                StoreDepartment storeDepartment = this.storeDepartmentService.getObjById(Long
                    .valueOf(Long.parseLong(id)));
                storeDepartment.setDisabled(true);//逻辑删除
                this.storeDepartmentService.update(storeDepartment);
            }
        }
        return "redirect:store_department.htm?currentPage=" + currentPage;
    }
    
    @RequestMapping({ "/seller/verifyDepart.htm" })
    public void buyer_validate_code(HttpServletRequest request, HttpServletResponse response,
                              String id) {
        boolean ret = true;
        Map<String, Object> params = new HashMap<String, Object>();
        if(id!=null){
	        params.put("departId",CommUtil.null2Long(id));
	        params.put("disabled", false);
	        List<User> users = this.userService.query(
	        		"select obj from User obj where obj.disabled =:disabled and obj.department.id=:departId", params, 0, 1);
	        if ((users != null) && (users.size() > 0)) {
	            ret = false;
	        }
        }
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
