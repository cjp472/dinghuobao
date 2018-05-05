package com.javamalls.ctrl.seller.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.constant.Constant;
import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.WebForm;
import com.javamalls.payment.chinabank.h5.util.JsonUtil;
import com.javamalls.platform.domain.Accessory;
import com.javamalls.platform.domain.GoodsLabel;
import com.javamalls.platform.domain.HomepageNavigation;
import com.javamalls.platform.domain.Partner;
import com.javamalls.platform.domain.SysConfig;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.UserGoodsClass;
import com.javamalls.platform.domain.query.GoodsLabelQueryObject;
import com.javamalls.platform.domain.query.HomepageNavigationQueryObject;
import com.javamalls.platform.domain.query.UserGoodsClassQueryObject;
import com.javamalls.platform.service.IAccessoryService;
import com.javamalls.platform.service.IGoodsLabelService;
import com.javamalls.platform.service.IHomepageNavigationService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserGoodsClassService;
import com.javamalls.platform.service.IUserService;
import com.javamalls.platform.vo.RemoveIdJsonVo;
import com.javamalls.platform.vo.UserGoodsClassJsonVo;
import com.utils.SendReqAsync;
/**
 * 首页的导航图标Action
 * @author zmw
 *
 */
@Controller
public class HomepageNavigationSellerAction {
    @Autowired
    private ISysConfigService      configService;
    @Autowired
    private IUserConfigService     userConfigService;
    @Autowired
    private IGoodsLabelService goodsLabelService;
    @Autowired
    private SendReqAsync           sendReqAsync;
    @Autowired
    private IAccessoryService   accessoryService;
    @Autowired
    private IHomepageNavigationService           homepageNavigationService;
    @SecurityMapping(title = "移动端首页导航列表", value = "seller/mobile_homepageNavigation_list.htm*", rtype = "seller", rname = "移动端首页导航", rcode = "usergoodsclass_seller", rgroup = "移动端首页导航管理")
    @RequestMapping({ "seller/mobile_homepageNavigation_list.htm" })
    public ModelAndView usergoodsclass_list(HttpServletRequest request,
                                            HttpServletResponse response, String currentPage,
                                            String orderBy, String orderType) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/homepageNavigation_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        User user=SecurityUserHolder.getCurrentUser();
        if (user == null) {
             mv = new JModelAndView("buyer/buyer_login.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
                JModelAndView.SHOP_PATH, request, response);
            return mv;
        }        
        String params = "";
        HomepageNavigationQueryObject qo = new HomepageNavigationQueryObject(currentPage, mv, "createtime", "desc");
        qo.setPageSize(Integer.valueOf(10));
        WebForm wf = new WebForm();
        wf.toQueryPo(request, qo, HomepageNavigation.class, mv);
        qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");
        Long store_id=null;
        if (user.getSalesManState()!=null && user.getSalesManState()==1) {
        	store_id=user.getParent().getStore().getId();
		}else{
			store_id=user.getStore().getId();
		}
        qo.addQuery("obj.store.id", new SysMap("store_id", store_id), "=");
       
        IPageList pList = this.homepageNavigationService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/seller/mobile_homepageNavigation_list.htm", "", params,
            pList, mv);
        return mv;
    }

    @SecurityMapping(title = "移动端首页导航保存", value = "/seller/mobileHomepageNavigation_save.htm*", rtype = "seller", rname = "移动端首页导航", rcode = "usergoodsclass_seller", rgroup = "移动端首页导航管理")
    @RequestMapping({ "/seller/mobileHomepageNavigation_save.htm" })
    public String homepageNavigation_save(HttpServletRequest request, HttpServletResponse response,
    		 String name,String address) {
    	User  user=SecurityUserHolder.getCurrentUser();
         HomepageNavigation homepageNavigation = new HomepageNavigation();
         String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
         String saveFilePathName = request.getSession().getServletContext().getRealPath("/")
                                   + uploadFilePath + File.separator + "system";
         Map<String, Object> map = new HashMap<String, Object>();
         String fileName="homePageNavigation";
         try {
			     map = CommUtil.saveFileToServer(request, "navigationPic", saveFilePathName, fileName,
			         null);
		                Accessory logo = new Accessory();
		                logo.setName(CommUtil.null2String(map.get("fileName")));
		                logo.setExt(CommUtil.null2String(map.get("mime")));
		                logo.setSize(CommUtil.null2Float(map.get("fileSize")));
		                logo.setPath(uploadFilePath + "/system");
		                logo.setWidth(CommUtil.null2Int(map.get("width")));
		                logo.setHeight(CommUtil.null2Int(map.get("height")));
		                logo.setCreatetime(new Date());
		                this.accessoryService.save(logo);
		                homepageNavigation.setAddress(address);
		                homepageNavigation.setCreatetime(new Date());
		                homepageNavigation.setName(name);
		                homepageNavigation.setNavigationPic(logo);
		                if (user!=null && !user.equals("")) {
		                	homepageNavigation.setCreateuser(user);
						}
		                if (user.getStore()!=null) {
							homepageNavigation.setStore(user.getStore());
							
						}else if(user.getSalesManState()!=null && user.getSalesManState()==1){//子账号
							if (user.getParent().getStore()!=null) {
								homepageNavigation.setStore(user.getParent().getStore());
							}
						}
		                this.homepageNavigationService.save(homepageNavigation);
			     
		} catch (Exception e) {
			e.printStackTrace();
		}
         map.clear();
    
        return "redirect:mobile_homepageNavigation_list.htm";
    }
    @SecurityMapping(title = "移动端首页导航编辑保存", value = "/seller/homepageNavigation_edit_save.htm*", rtype = "seller", rname = "移动端首页导航", rcode = "usergoodsclass_seller", rgroup = "移动端首页导航管理")
    @RequestMapping({ "/seller/mobile_homepageNavigation_edit_save.htm" })
    public String homepageNavigation_edit_save(HttpServletRequest request, HttpServletResponse response,
    		 String name,String address,String id) {
    	User user=SecurityUserHolder.getCurrentUser();
        HomepageNavigation obj = this.homepageNavigationService.getObjById(CommUtil.null2Long(id));
        WebForm wf = new WebForm();
       // obj = (HomepageNavigation) wf.toPo(request, obj);
        String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
        String saveFilePathName = request.getSession().getServletContext().getRealPath("/")
                + uploadFilePath;
        String fileName = obj.getNavigationPic().getName() == null ? "" : obj.getNavigationPic().getName();
        Map<String, Object> map = new HashMap<String, Object>();
        try {
			map = CommUtil.saveFileToServer(request, "navigationPic", saveFilePathName, fileName,
			         null);
			
			if (fileName.equals("")) {
                if (map.get("fileName") != "") {
                    Accessory photo = new Accessory();
                    photo.setName(CommUtil.null2String(map.get("fileName")));
                    photo.setExt(CommUtil.null2String(map.get("mime")));
                    photo.setSize(CommUtil.null2Float(map.get("fileSize")));
                    photo.setPath(uploadFilePath);
                    photo.setWidth(CommUtil.null2Int(map.get("width")));
                    photo.setHeight(CommUtil.null2Int(map.get("height")));
                    photo.setCreatetime(new Date());
                    this.accessoryService.save(photo);
                    obj.setNavigationPic(photo);
                }
            } else if (map.get("fileName") != "") {
                Accessory photo = obj.getNavigationPic();
                photo.setName(CommUtil.null2String(map.get("fileName")));
                photo.setExt(CommUtil.null2String(map.get("mime")));
                photo.setSize(CommUtil.null2Float(map.get("fileSize")));
                photo.setPath(uploadFilePath);
                photo.setWidth(CommUtil.null2Int(map.get("width")));
                photo.setHeight(CommUtil.null2Int(map.get("height")));
                this.accessoryService.update(photo);
                obj.setNavigationPic(photo);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
    	if (obj!=null) {
    		obj.setUpdatetime(new Date());
    		obj.setName(name);
    		obj.setUpdateUser(user);
    		obj.setAddress(address);
    		homepageNavigationService.update(obj);
		}
        return "redirect:mobile_homepageNavigation_list.htm";
    }

    @SecurityMapping(title = "移动端首页导航删除", value = "/seller/homepageNavigation_del.htm*", rtype = "seller", rname = "移动端首页导航", rcode = "usergoodsclass_seller", rgroup = "移动端首页导航管理")
    @RequestMapping({ "/seller/mobile_homepageNavigation_del.htm" })
    public String homepageNavigation_del(HttpServletRequest request, String mulitId) {
        String[] ids = mulitId.split(",");
        for (String id : ids) {
            if (!id.equals("")) {
            	HomepageNavigation obj = this.homepageNavigationService.getObjById(CommUtil.null2Long(id));
                List<RemoveIdJsonVo> dellist = new ArrayList<RemoveIdJsonVo>();

                obj.setDisabled(true);
                this.homepageNavigationService.update(obj);
                RemoveIdJsonVo vo = new RemoveIdJsonVo();
                vo.setId(obj.getId());
                dellist.add(vo);
                if (dellist != null && dellist.size() > 0) {
                    String write2JsonStr = JsonUtil.write2JsonStr(dellist);
                    sendReqAsync.sendMessageUtil(Constant.STORE_GOODSCLASS_URL_DEL, write2JsonStr,
                        "删除移动端首页导航");

                }
            }
        }
        return "redirect:mobile_homepageNavigation_list.htm";
    }

    @SecurityMapping(title = "新增移动端首页导航", value = "/seller/homepageNavigation_add.htm*", rtype = "seller", rname = "移动端首页导航", rcode = "usergoodsclass_seller", rgroup = "移动端首页导航管理")
    @RequestMapping({ "/seller/mobile_homepageNavigation_add.htm" })
    public ModelAndView homepageNavigation_add(HttpServletRequest request,
                                           HttpServletResponse response, String currentPage
                                           ) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/homepageNavigation_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("currentPage", currentPage);
        return mv;
    }

    @SecurityMapping(title = "编辑移动端首页导航", value = "/seller/homepageNavigation_edit.htm*", rtype = "seller", rname = "移动端首页导航", rcode = "usergoodsclass_seller", rgroup = "移动端首页导航管理")
    @RequestMapping({ "/seller/mobile_homepageNavigation_edit.htm" })
    public ModelAndView homepageNavigation_edit(HttpServletRequest request,
                                            HttpServletResponse response, String currentPage,
                                            String id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/homepageNavigation_edit.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
       
        HomepageNavigation obj = this.homepageNavigationService.getObjById(CommUtil.null2Long(id));
        mv.addObject("obj", obj);
        mv.addObject("currentPage", currentPage);
        return mv;
    }
}