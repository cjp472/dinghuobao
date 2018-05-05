package com.javamalls.ctrl.h5.buyer.action;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.WebForm;
import com.javamalls.base.tools.database.DatabaseTools;
import com.javamalls.platform.domain.Address;
import com.javamalls.platform.domain.Area;
import com.javamalls.platform.domain.query.AddressQueryObject;
import com.javamalls.platform.service.IAddressService;
import com.javamalls.platform.service.IAreaService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

@Controller
public class H5AddressBuyerAction {
    @Autowired
    private ISysConfigService  configService;
    @Autowired
    private IUserConfigService userConfigService;
    @Autowired
    private IAddressService    addressService;
    @Autowired
    private IAreaService       areaService;
    @Autowired
    private DatabaseTools      databaseTools;

    @SecurityMapping(title = "收货地址列表", value = "/mobile/buyer/address.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/address.htm" })
    public ModelAndView address(HttpServletRequest request, HttpServletResponse response,
                                String currentPage, String orderBy, String orderType,@PathVariable String storeId,String syState) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/address.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = CommUtil.getURL(request);
        //登录后使用
        if( SecurityUserHolder.getCurrentUser()==null){
        		mv = new JModelAndView("h5/login.html", this.configService.getSysConfig(),
                      this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("url", url + "/mobile/daohang.htm");
                mv.addObject("storeId", storeId);
                return mv;
        }
        String params = "";
        AddressQueryObject qo = new AddressQueryObject(currentPage, mv, orderBy, orderType);
        qo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser()
            .getId()), "=");

        qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");

        IPageList pList = this.addressService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/mobile/buyer/address.htm", "", params, pList,
            mv);
        List<Area> areas = this.areaService.query(
            "select obj from Area obj where obj.parent.id is null", null, -1, -1);
        mv.addObject("areas", areas);
        mv.addObject("syState", syState);
        return mv;
    }

    @SecurityMapping(title = "新增收货地址", value = "/mobile/buyer/address_add.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/address_add.htm" })
    public ModelAndView address_add(HttpServletRequest request, HttpServletResponse response,
                                    String currentPage,@PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/address_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
      //登录后使用
         if( SecurityUserHolder.getCurrentUser()==null){
        		mv = new JModelAndView("h5/login.html", this.configService.getSysConfig(),
                      this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("storeId", storeId);
                return mv;
        }
        List<Area> areas = this.areaService.query(
            "select obj from Area obj where obj.parent.id is null", null, -1, -1);
        mv.addObject("areas", areas);
        mv.addObject("currentPage", currentPage);
        return mv;
    }

    @SecurityMapping(title = "收货地址保存", value = "/mobile/buyer/address_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/address_save.htm" })
    public String address_save(HttpServletRequest request, HttpServletResponse response, String id,
                               String area_id, String currentPage) {
        WebForm wf = new WebForm();
        Address address = null;
        
        if ("".equals(id)) {
            address = (Address) wf.toPo(request, Address.class);
            address.setCreatetime(new Date());
        } else {
            Address obj = this.addressService.getObjById(Long.valueOf(Long.parseLong(id)));
            address = (Address) wf.toPo(request, obj);
        }
        //登录后使用
        if( SecurityUserHolder.getCurrentUser()==null){
        	return "redirect:"+CommUtil.getURL(request)+"/mobile/user/login.htm";
        }
        
        address.setUser(SecurityUserHolder.getCurrentUser());
        Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
        address.setArea(area);
        if (id.equals("")) {
            this.addressService.save(address);
        } else {
            this.addressService.update(address);
        }
        return "redirect:"+CommUtil.getURL(request)+"/mobile/buyer/address.htm";
    }

    @SecurityMapping(title = "删除收货地址", value = "/mobile/buyer/address_del.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/address_del.htm" })
    public String address_del(HttpServletRequest request, HttpServletResponse response,
                              String mulitId, String currentPage) {
    	//登录后使用
        if( SecurityUserHolder.getCurrentUser()==null){
        	return "redirect:"+CommUtil.getURL(request)+"/mobile/user/login.htm";
        }
        
        String[] ids = mulitId.split(",");
        for (String id : ids) {
            if (!id.equals("")) {
                Address address = this.addressService.getObjById(Long.valueOf(Long.parseLong(id)));
                if (address != null)
                    this.addressService.delete(Long.valueOf(Long.parseLong(id)));
            }
        }
        return "redirect:"+CommUtil.getURL(request)+"/mobile/buyer/address.htm";
    }

    @SecurityMapping(title = "编辑收货地址", value = "/mobile/buyer/address_edit.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/address_edit.htm" })
    public ModelAndView address_edit(HttpServletRequest request, HttpServletResponse response,
                                     String id, String currentPage,@PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/address_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        //登录后使用
        if( SecurityUserHolder.getCurrentUser()==null){
       		mv = new JModelAndView("h5/login.html", this.configService.getSysConfig(),
                     this.userConfigService.getUserConfig(), 1, request, response);
               mv.addObject("storeId", storeId);
               return mv;
       }
        List<Area> areas = this.areaService.query(
            "select obj from Area obj where obj.parent.id is null", null, -1, -1);
        Address obj = this.addressService.getObjById(CommUtil.null2Long(id));
        mv.addObject("obj", obj);
        mv.addObject("areas", areas);
        mv.addObject("currentPage", currentPage);
        return mv;
    }
}
