package com.javamalls.ctrl.seller.action;

import java.io.File;
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
import org.springframework.web.bind.annotation.ResponseBody;
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
import com.javamalls.platform.domain.GoodsBrand;
import com.javamalls.platform.domain.query.GoodsBrandQueryObject;
import com.javamalls.platform.service.IAccessoryService;
import com.javamalls.platform.service.IGoodsBrandService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.vo.GoodsBrandJsonVo;
import com.utils.SendReqAsync;


/**卖家中心  品牌申请、查看等
 *                       
 * @Filename: GoodsBrandSellerAction.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class GoodsBrandSellerAction {
    @Autowired
    private ISysConfigService          configService;
    @Autowired
    private IUserConfigService         userConfigService;
    @Autowired
    private IGoodsBrandService         goodsBrandService;
    @Autowired
    private IAccessoryService          accessoryService;
    @Autowired
    private SendReqAsync sendReqAsync;

    @SecurityMapping(title = "卖家品牌列表", value = "/seller/usergoodsbrand_list.htm*", rtype = "seller", rname = "品牌申请", rcode = "usergoodsbrand_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/usergoodsbrand_list.htm" })
    public ModelAndView usergoodsbrand_list(HttpServletRequest request,
                                            HttpServletResponse response, String currentPage,
                                            String orderBy, String orderType) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/usergoodsbrand_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        GoodsBrandQueryObject qo = new GoodsBrandQueryObject(currentPage, mv, orderBy, orderType);
        //qo.addQuery("obj.user.id", new SysMap("uid", SecurityUserHolder.getCurrentUser().getId()),"=");
        qo.addQuery("obj.store.id", new SysMap("store_id", SecurityUserHolder.getCurrentUser().getStore().getId()),"=");
        qo.addQuery("obj.disabled", new SysMap("disabled", false),"=");
        qo.setOrderBy("createtime");
        qo.setOrderType("desc");
        IPageList pList = this.goodsBrandService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        return mv;
    }

    @SecurityMapping(title = "卖家品牌添加", value = "/seller/usergoodsbrand_add.htm*", rtype = "seller", rname = "品牌申请", rcode = "usergoodsbrand_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/usergoodsbrand_add.htm" })
    public ModelAndView usergoodsbrand_add(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/usergoodsbrand_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        return mv;
    }

    @SecurityMapping(title = "卖家品牌编辑", value = "/seller/usergoodsbrand_edit.htm*", rtype = "seller", rname = "品牌申请", rcode = "usergoodsbrand_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/usergoodsbrand_edit.htm" })
    public ModelAndView usergoodsbrand_edit(HttpServletRequest request,
                                            HttpServletResponse response, String id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/usergoodsbrand_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if ((id != null) && (!id.equals(""))) {
            GoodsBrand goodsBrand = this.goodsBrandService.getObjById(Long.valueOf(Long
                .parseLong(id)));
            mv.addObject("obj", goodsBrand);
        }
        mv.addObject("edit", Boolean.valueOf(true));
        return mv;
    }

    @SecurityMapping(title = "卖家品牌删除", value = "/seller/usergoodsbrand_dele.htm*", rtype = "seller", rname = "品牌申请", rcode = "usergoodsbrand_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/usergoodsbrand_dele.htm" })
    public String usergoodsbrand_dele(HttpServletRequest request, String id, String currentPage) {
        if (!id.equals("")) {
            GoodsBrand brand = this.goodsBrandService.getObjById(Long.valueOf(Long.parseLong(id)));
            brand.setDisabled(true);
            this.goodsBrandService.update(brand);
            
            Map<String,Object> map=new HashMap<String, Object>();
            map.put("id", brand.getId());
            String write2JsonStr = JsonUtil.write2JsonStr(map);
            sendReqAsync.sendMessageUtil(Constant.STORE_BRAND_URL_DEL, write2JsonStr,"删除品牌");
        }
        return "redirect:usergoodsbrand_list.htm?currentPage=" + currentPage;
    }

    @RequestMapping({ "/seller/usergoodsbrand_valid.htm" })
    @ResponseBody
    public void usergoodsbrand_valid(HttpServletRequest request, HttpServletResponse response,String name,String id) {
        int ret = 0;
        if (id==null || id=="") {
            if(name!="" && name!=null && name!="undefined"){
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("disabled", false);
                map.put("store_id", SecurityUserHolder.getCurrentUser().getStore().getId());
                map.put("name", name);
                String sql = "select obj from GoodsBrand obj where obj.disabled=:disabled and obj.store.id=:store_id and obj.name =:name";
                sql += " order by id desc";
                List<GoodsBrand> goodsBrandList = this.goodsBrandService.query(sql, map, -1, -1);
                if (goodsBrandList.size()>0) {
                    ret = 1;//品牌已经存在
                }else{
                    ret = 2;//符合 条件可以添加
                }
            }
        }else{
            if(name!="" && name!=null && name!="undefined"){
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("disabled", false);
                map.put("store_id", SecurityUserHolder.getCurrentUser().getStore().getId());
                map.put("name", name);
                map.put("id", Long.valueOf(Long.parseLong(id)));
                String sql = "select obj from GoodsBrand obj where obj.disabled=:disabled and obj.store.id=:store_id and obj.name =:name and id!=:id";
                sql += " order by id desc";
                List<GoodsBrand> goodsBrandList = this.goodsBrandService.query(sql, map, -1, -1);
                if (goodsBrandList.size()>0) {
                    ret = 1;//品牌已经存在
                }else{
                    ret = 2;//符合 条件可以添加
                }
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
    
    @SecurityMapping(title = "卖家品牌保存", value = "/seller/usergoodsbrand_save.htm*", rtype = "seller", rname = "添加品牌", rcode = "usergoodsbrand_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/usergoodsbrand_save.htm" })
    public String usergoodsbrand_save(HttpServletRequest request, HttpServletResponse response,
                                      String id, String cmd, String btype,
                                      String list_url, String add_url,String name) {
        if(name!=""){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("disabled", false);
            map.put("store_id", SecurityUserHolder.getCurrentUser().getStore().getId());
            map.put("name", name);
            String sql = "select obj from GoodsBrand obj where obj.disabled=:disabled and obj.store.id=:store_id and obj.name =:name";
            sql += " order by id desc";
            List<GoodsBrand> goodsBrandList = this.goodsBrandService.query(sql, map, -1, -1);
            if (goodsBrandList.size()>0) {
                return "redirect:usergoodsbrand_list.htm";
            }
        }
        WebForm wf = new WebForm();
        GoodsBrand goodsBrand = null;
       /* Long oldgbcID = 0L;*/
        if (id.equals("")) {
            goodsBrand = (GoodsBrand) wf.toPo(request, GoodsBrand.class);
            goodsBrand.setCreatetime(new Date());
            goodsBrand.setAudit(1);
            goodsBrand.setUserStatus(0);
            goodsBrand.setUser(SecurityUserHolder.getCurrentUser());
            goodsBrand.setStore(SecurityUserHolder.getCurrentUser().getStore());
        } else {
            GoodsBrand obj = this.goodsBrandService.getObjById(Long.valueOf(Long.parseLong(id)));
            /*if (obj.getCategory() != null) {
                oldgbcID = obj.getCategory().getId();
            } else {
                oldgbcID = -1L;
            }*/
            goodsBrand = (GoodsBrand) wf.toPo(request, obj);
        }

       /* GoodsBrandCategory cat = this.goodsBrandCategoryService.getObjByProperty("name", cat_name);
        if (cat == null) {
            cat = new GoodsBrandCategory();
            cat.setCreatetime(new Date());
            cat.setName(cat_name);
            this.goodsBrandCategoryService.save(cat);
            goodsBrand.setCategory(cat);
        } else {
            goodsBrand.setCategory(cat);
            //非新增判断前后id是否相同
            if (oldgbcID.equals(goodsBrand.getCategory().getId())) {
                oldgbcID = 0L;
            }
        }*/

        String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
        String saveFilePathName = request.getSession().getServletContext().getRealPath("/")
                                  + uploadFilePath + File.separator + "brand";
        Map map = new HashMap();
        try {
            String fileName = goodsBrand.getBrandLogo() == null ? "" : goodsBrand.getBrandLogo()
                .getName();
            map = CommUtil.saveFileToServer(request, "brandLogo", saveFilePathName, fileName, null);
            if (fileName.equals("")) {
                if (map.get("fileName") != "") {
                    Accessory photo = new Accessory();
                    photo.setName(CommUtil.null2String(map.get("fileName")));
                    photo.setExt(CommUtil.null2String(map.get("mime")));
                    photo.setSize(CommUtil.null2Float(map.get("fileSize")));
                    photo.setPath(uploadFilePath + "/brand");
                    photo.setWidth(CommUtil.null2Int(map.get("width")));
                    photo.setHeight(CommUtil.null2Int(map.get("height")));
                    photo.setCreatetime(new Date());
                    this.accessoryService.save(photo);
                    goodsBrand.setBrandLogo(photo);
                }
            } else if (map.get("fileName") != "") {
                Accessory photo = goodsBrand.getBrandLogo();
                photo.setName(CommUtil.null2String(map.get("fileName")));
                photo.setExt(CommUtil.null2String(map.get("mime")));
                photo.setSize(CommUtil.null2Float(map.get("fileSize")));
                photo.setPath(uploadFilePath + "/brand");
                photo.setWidth(CommUtil.null2Int(map.get("width")));
                photo.setHeight(CommUtil.null2Int(map.get("height")));
                photo.setCreatetime(new Date());
                this.accessoryService.update(photo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (id.equals("")) {
            this.goodsBrandService.save(goodsBrand);
            
            //服装接口
            
            GoodsBrandJsonVo vo=new GoodsBrandJsonVo();
            vo.setId(goodsBrand.getId());
            vo.setDisabled(goodsBrand.isDisabled());
            vo.setCreatetime(goodsBrand.getCreatetime());
            vo.setFirst_word(goodsBrand.getFirst_word());
            vo.setName(goodsBrand.getName());
            vo.setRemark(goodsBrand.getRemark());
            vo.setStoreId(goodsBrand.getStore().getId());
            vo.setUserStatus(goodsBrand.getUserStatus());
            Accessory brandLogo = goodsBrand.getBrandLogo();
            if(brandLogo!=null){
            	vo.setBrandLogo(brandLogo.getPath()+"/"+brandLogo.getName());
            }
            
            String write2JsonStr = JsonUtil.write2JsonStr(vo);
            sendReqAsync.sendMessageUtil(Constant.STORE_BRAND_URL_ADD, write2JsonStr,"新增品牌");
        } else {
            this.goodsBrandService.update(goodsBrand);
            //服装接口
            GoodsBrandJsonVo vo=new GoodsBrandJsonVo();
            vo.setId(goodsBrand.getId());
            vo.setDisabled(goodsBrand.isDisabled());
            vo.setCreatetime(goodsBrand.getCreatetime());
            vo.setFirst_word(goodsBrand.getFirst_word());
            vo.setName(goodsBrand.getName());
            vo.setRemark(goodsBrand.getRemark());
            vo.setStoreId(goodsBrand.getStore().getId());
            vo.setUserStatus(goodsBrand.getUserStatus());
            Accessory brandLogo = goodsBrand.getBrandLogo();
            if(brandLogo!=null){
            	vo.setBrandLogo(brandLogo.getPath()+"/"+brandLogo.getName());
            }
            
            String write2JsonStr = JsonUtil.write2JsonStr(vo);
            sendReqAsync.sendMessageUtil(Constant.STORE_BRAND_URL_EDIT, write2JsonStr,"修改品牌");
        }
        return "redirect:usergoodsbrand_list.htm";
    }

}
