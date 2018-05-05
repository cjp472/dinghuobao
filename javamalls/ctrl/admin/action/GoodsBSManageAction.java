package com.javamalls.ctrl.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.WebForm;
import com.javamalls.platform.domain.Accessory;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.GoodsBrand;
import com.javamalls.platform.domain.GoodsBrandCategory;
import com.javamalls.platform.domain.GoodsClass;
import com.javamalls.platform.domain.GoodsType;
import com.javamalls.platform.domain.query.GoodsBrandQueryObject;
import com.javamalls.platform.service.IAccessoryService;
import com.javamalls.platform.service.IGoodsBrandCategoryService;
import com.javamalls.platform.service.IGoodsBrandService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

@Controller
public class GoodsBSManageAction {
    @Autowired
    private ISysConfigService          configService;
    @Autowired
    private IUserConfigService         userConfigService;
    @Autowired
    private IGoodsBrandService         goodsBrandService;
    @Autowired
    private IAccessoryService          accessoryService;
    @Autowired
    private IGoodsService              goodsService;
    @Autowired
    private IGoodsBrandCategoryService goodsBrandCategoryService;

    @SecurityMapping(title = "品牌列表", value = "/admin/goods_bs_list.htm*", rtype = "admin", rname = "品牌", rcode = "goods_bs", rgroup = "商品")
    @RequestMapping({ "/admin/goods_bs_list.htm" })
    public ModelAndView goods_bs_list(HttpServletRequest request, HttpServletResponse response,
                                      String currentPage, String orderBy, String orderType,
                                      String name, String category) {
        ModelAndView mv = new JModelAndView("admin/blue/goods_bs_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }

        GoodsBrandQueryObject qo = new GoodsBrandQueryObject(currentPage, mv, orderBy, orderType);
        qo.addQuery("obj.audit", new SysMap("audit", Integer.valueOf(1)), "=");
        qo.addQuery("obj.btype", new SysMap("btype", Integer.valueOf(1)), "=");
        qo.setOrderBy("sequence");
        qo.setOrderType("asc");
        if (!CommUtil.null2String(name).equals("")) {
            qo.addQuery("obj.name", new SysMap("name", "%" + name.trim() + "%"), "like");
        }
        if (!CommUtil.null2String(category).equals("")) {
            qo.addQuery("obj.category.name", new SysMap("category", "%" + category.trim() + "%"),
                "like");
        }
        IPageList pList = this.goodsBrandService.list(qo);

        CommUtil.saveIPageList2ModelAndView(url + "/admin/goods_bs_list.htm", "", "", pList, mv);
        mv.addObject("name", name);
        return mv;
    }

    @SecurityMapping(title = "商品品牌添加", value = "/admin/goods_bs_brand_add.htm*", rtype = "admin", rname = "品牌管理", rcode = "goods_bs", rgroup = "商品")
    @RequestMapping({ "/admin/goods_bs_brand_add.htm" })
    public ModelAndView add(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("admin/blue/goods_bs_brand_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        List<GoodsBrandCategory> categorys = this.goodsBrandCategoryService.query(
            "select obj from GoodsBrandCategory obj", null, -1, -1);
        mv.addObject("categorys", categorys);
        return mv;
    }

    @SecurityMapping(title = "商品品牌编辑", value = "/admin/goods_bs_brand_edit.htm*", rtype = "admin", rname = "品牌管理", rcode = "goods_bs", rgroup = "商品")
    @RequestMapping({ "/admin/goods_bs_brand_edit.htm" })
    public ModelAndView brand_edit(HttpServletRequest request, HttpServletResponse response,
                                   String id) {
        ModelAndView mv = new JModelAndView("admin/blue/goods_bs_brand_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if ((id != null) && (!id.equals(""))) {
            GoodsBrand goodsBrand = this.goodsBrandService.getObjById(Long.valueOf(Long
                .parseLong(id)));
            mv.addObject("obj", goodsBrand);
        }
        List<GoodsBrandCategory> categorys = this.goodsBrandCategoryService.query(
            "select obj from GoodsBrandCategory obj", null, -1, -1);
        mv.addObject("categorys", categorys);
        mv.addObject("edit", Boolean.valueOf(true));
        return mv;
    }

    @SecurityMapping(title = "商品品牌保存", value = "/admin/goods_bs_brand_save.htm*", rtype = "admin", rname = "品牌管理", rcode = "goods_bs", rgroup = "商品")
    @RequestMapping({ "/admin/goods_bs_brand_save.htm" })
    public ModelAndView brand_save(HttpServletRequest request, HttpServletResponse response,
                                   String id, String cmd, String list_url, String add_url,
                                   String cat_name) {
        WebForm wf = new WebForm();
        GoodsBrand goodsBrand = null;
        if (id.equals("")) {
            goodsBrand = (GoodsBrand) wf.toPo(request, GoodsBrand.class);
            goodsBrand.setBtype(0);
            goodsBrand.setCreatetime(new Date());
            goodsBrand.setAudit(1);
            goodsBrand.setUserStatus(0);
        } else {
            GoodsBrand obj = this.goodsBrandService.getObjById(Long.valueOf(Long.parseLong(id)));
            goodsBrand = (GoodsBrand) wf.toPo(request, obj);
        }
        GoodsBrandCategory cat=null;
        if(cat_name!=null){
        	cat = this.goodsBrandCategoryService.getObjByProperty("name", cat_name);
        } 
        if (cat == null) {
            cat = new GoodsBrandCategory();
            cat.setCreatetime(new Date());
            cat.setName(cat_name);
            this.goodsBrandCategoryService.save(cat);
            goodsBrand.setCategory(cat);
        } else {
            goodsBrand.setCategory(cat);
        }
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
                goodsBrand.setBrandLogo(photo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (id.equals("")) {
            this.goodsBrandService.save(goodsBrand);
        } else {
            this.goodsBrandService.update(goodsBrand);
        }
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("list_url", list_url);
        mv.addObject("op_title", "保存品牌成功");
        if (add_url != null) {
            mv.addObject("add_url", add_url);
        }
        return mv;
    }

    @SecurityMapping(title = "商品品牌删除", value = "/admin/goods_bs_brand_del.htm*", rtype = "admin", rname = "品牌管理", rcode = "goods_bs", rgroup = "商品")
    @RequestMapping({ "/admin/goods_bs_brand_del.htm" })
    public String brand_delete(HttpServletRequest request, String mulitId, String audit,
                               String currentPage) {
        String[] ids = mulitId.split(",");
        for (String id : ids) {
            if (!id.equals("")) {
                GoodsBrand brand = this.goodsBrandService.getObjById(Long.valueOf(Long
                    .parseLong(id)));
                CommUtil.del_acc(request, brand.getBrandLogo());
                for (Goods goods : brand.getGoods_list()) {
                    goods.setGoods_brand(null);
                    this.goodsService.update(goods);
                }
                for (GoodsType type : brand.getTypes()) {
                    type.getGbs().remove(brand);
                }
                this.goodsBrandService.delete(Long.valueOf(Long.parseLong(id)));
            }
        }
        String returnUrl = "redirect:goods_bs_list.htm?currentPage=" + currentPage;
        /*
        if ((audit != null) && (!audit.equals(""))) {
          returnUrl = 
            "redirect:goods_brand_audit.htm?currentPage=" + currentPage;
        }
        */
        return returnUrl;
    }

    @RequestMapping({ "/admin/goods_bs_brand_verify.htm" })
    public void goods_brand_verify(HttpServletRequest request, HttpServletResponse response,
                                   String name, String id) {
        boolean ret = true;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", name);
        params.put("id", CommUtil.null2Long(id));
        List<GoodsBrand> gcs = this.goodsBrandService.query(
            "select obj from GoodsBrand obj where obj.name=:name and obj.id!=:id", params, -1, -1);
        if ((gcs != null) && (gcs.size() > 0)) {
            ret = false;
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

    private Set<Long> genericIds(GoodsClass gc) {
        Set<Long> ids = new HashSet<Long>();
        ids.add(gc.getId());
        for (GoodsClass child : gc.getChilds()) {
            Set<Long> cids = genericIds(child);
            for (Long cid : cids) {
                ids.add(cid);
            }
            ids.add(child.getId());
        }
        return ids;
    }
}
