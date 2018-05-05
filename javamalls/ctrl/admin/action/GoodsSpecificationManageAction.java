package com.javamalls.ctrl.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
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

import com.easyjf.beans.BeanUtils;
import com.easyjf.beans.BeanWrapper;
import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.WebForm;
import com.javamalls.base.tools.database.DatabaseTools;
import com.javamalls.ctrl.admin.tools.StoreTools;
import com.javamalls.platform.domain.Accessory;
import com.javamalls.platform.domain.GoodsSpecProperty;
import com.javamalls.platform.domain.GoodsSpecification;
import com.javamalls.platform.domain.query.GoodsSpecificationQueryObject;
import com.javamalls.platform.service.IAccessoryService;
import com.javamalls.platform.service.IGoodsSpecPropertyService;
import com.javamalls.platform.service.IGoodsSpecificationService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

/**商品规格管理
 *                       
 * @Filename: GoodsSpecificationManageAction.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class GoodsSpecificationManageAction {
    @Autowired
    private ISysConfigService          configService;
    @Autowired
    private IUserConfigService         userConfigService;
    @Autowired
    private IAccessoryService          accessoryService;
    @Autowired
    private IGoodsSpecificationService goodsSpecService;
    @Autowired
    private IGoodsSpecPropertyService  goodsSpecPropertyService;
    @Autowired
    private DatabaseTools              databaseTools;
    @Autowired
    private StoreTools                 shopTools;

    @SecurityMapping(title = "商品规格列表", value = "/admin/goods_spec_list.htm*", rtype = "admin", rname = "规格管理", rcode = "goods_spec", rgroup = "商品")
    @RequestMapping({ "/admin/goods_spec_list.htm" })
    public ModelAndView list(HttpServletRequest request, HttpServletResponse response,
                             String currentPage, String orderBy, String orderType) {
        String msg = request.getParameter("msg");
        ModelAndView mv = new JModelAndView("admin/blue/goods_spec_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        GoodsSpecificationQueryObject qo = new GoodsSpecificationQueryObject(currentPage, mv,
            orderBy, orderType);
        WebForm wf = new WebForm();
        wf.toQueryPo(request, qo, GoodsSpecification.class, mv);
        qo.setOrderBy("sequence");
        qo.setOrderType("asc");
        qo.addQuery("obj.disabled", new SysMap("disabled",false), "=");
        IPageList pList = this.goodsSpecService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        mv.addObject("shopTools", this.shopTools);
        mv.addObject("msg", msg);
        return mv;
    }

    @SecurityMapping(title = "商品规格添加", value = "/admin/goods_spec_add.htm*", rtype = "admin", rname = "规格管理", rcode = "goods_spec", rgroup = "商品")
    @RequestMapping({ "/admin/goods_spec_add.htm" })
    public ModelAndView add(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("admin/blue/goods_spec_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        return mv;
    }

    @SecurityMapping(title = "商品规格编辑", value = "/admin/goods_spec_edit.htm*", rtype = "admin", rname = "规格管理", rcode = "goods_spec", rgroup = "商品")
    @RequestMapping({ "/admin/goods_spec_edit.htm" })
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response, String id,
                             String currentPage) {
        ModelAndView mv = new JModelAndView("admin/blue/goods_spec_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if ((id != null) && (!id.equals(""))) {
            GoodsSpecification goodsSpecification = this.goodsSpecService.getObjById(Long
                .valueOf(Long.parseLong(id)));
            mv.addObject("obj", goodsSpecification);
            mv.addObject("currentPage", currentPage);
            mv.addObject("edit", Boolean.valueOf(true));
        }
        return mv;
    }

    @SecurityMapping(title = "商品规格保存", value = "/admin/goods_spec_save.htm*", rtype = "admin", rname = "规格管理", rcode = "goods_spec", rgroup = "商品")
    @RequestMapping({ "/admin/goods_spec_save.htm" })
    public ModelAndView save(HttpServletRequest request, HttpServletResponse response, String id,
                             String cmd, String count, String add_url, String list_url,
                             String currentPage) {
        WebForm wf = new WebForm();
        GoodsSpecification goodsSpecification = null;
        if (id.equals("")) {
            goodsSpecification = (GoodsSpecification) wf.toPo(request, GoodsSpecification.class);
            goodsSpecification.setCreatetime(new Date());
        } else {
            GoodsSpecification obj = this.goodsSpecService.getObjById(Long.valueOf(Long
                .parseLong(id)));
            goodsSpecification = (GoodsSpecification) wf.toPo(request, obj);
        }
        if (id.equals("")) {
            this.goodsSpecService.save(goodsSpecification);
        } else {
            this.goodsSpecService.update(goodsSpecification);
        }
        genericProperty(request, goodsSpecification, count);
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("list_url", list_url + "?currentPage=" + currentPage);
        mv.addObject("op_title", "保存商品规格成功");
        if (add_url != null) {
            mv.addObject("add_url", add_url);
        }
        return mv;
    }

    private void clearProperty(HttpServletRequest request, GoodsSpecification spec) {
        for (GoodsSpecProperty property : spec.getProperties()) {
            property.setSpec(null);
            Accessory img = property.getSpecImage();
            CommUtil.del_acc(request, img);
            property.setSpecImage(null);
            this.goodsSpecPropertyService.delete(property.getId());
        }
    }

    private void genericProperty(HttpServletRequest request, GoodsSpecification spec, String count) {
        for (int i = 1; i <= CommUtil.null2Int(count); i++) {
            Integer sequence = Integer.valueOf(CommUtil.null2Int(request.getParameter("sequence_"
                                                                                      + i)));
            String value = CommUtil.null2String(request.getParameter("value_" + i));
            if ((sequence != null) && (!sequence.equals("")) && (value != null)
                && (!value.equals(""))) {
                String id = CommUtil.null2String(request.getParameter("id_" + i));
                GoodsSpecProperty property = null;
                if ((id != null) && (!id.equals(""))) {
                    property = this.goodsSpecPropertyService.getObjById(Long.valueOf(Long
                        .parseLong(id)));
                } else {
                    property = new GoodsSpecProperty();
                }
                property.setCreatetime(new Date());
                property.setSequence(sequence.intValue());
                property.setSpec(spec);
                property.setValue(value);
                String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
                String saveFilePathName = request.getSession().getServletContext().getRealPath("/")
                                          + uploadFilePath + File.separator + "spec";
                Map map = new HashMap();
                try {
                    String fileName = property.getSpecImage() == null ? "" : property
                        .getSpecImage().getName();
                    map = CommUtil.saveFileToServer(request, "specImage_" + i, saveFilePathName,
                        fileName, null);
                    if (fileName.equals("")) {
                        if (map.get("fileName") != "") {
                            Accessory specImage = new Accessory();
                            specImage.setName(CommUtil.null2String(map.get("fileName")));
                            specImage.setExt(CommUtil.null2String(map.get("mime")));
                            specImage.setSize(CommUtil.null2Float(map.get("fileSize")));
                            specImage.setPath(uploadFilePath + "/spec");
                            specImage.setWidth(CommUtil.null2Int(map.get("width")));
                            specImage.setHeight(CommUtil.null2Int(map.get("height")));
                            specImage.setCreatetime(new Date());
                            this.accessoryService.save(specImage);
                            property.setSpecImage(specImage);
                        }
                    } else if (map.get("fileName") != "") {
                        Accessory specImage = property.getSpecImage();
                        specImage.setName(CommUtil.null2String(map.get("fileName")));
                        specImage.setExt(CommUtil.null2String(map.get("mime")));
                        specImage.setSize(CommUtil.null2Float(map.get("fileSize")));
                        specImage.setPath(uploadFilePath + "/spec");
                        specImage.setWidth(CommUtil.null2Int(map.get("width")));
                        specImage.setHeight(CommUtil.null2Int(map.get("height")));
                        specImage.setCreatetime(new Date());
                        this.accessoryService.update(specImage);
                        property.setSpecImage(specImage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (id.equals("")) {
                    this.goodsSpecPropertyService.save(property);
                } else {
                    this.goodsSpecPropertyService.update(property);
                }
            }
        }
    }

    @SecurityMapping(title = "商品规格删除", value = "/admin/goods_spec_del.htm*", rtype = "admin", rname = "规格管理", rcode = "goods_spec", rgroup = "商品")
    @RequestMapping({ "/admin/goods_spec_del.htm" })
    public String delete(HttpServletRequest request, String mulitId, String currentPage) {
        String msg = "delsuccess";
        try {

            String[] ids = mulitId.split(",");
            for (String id : ids) {
                if (!id.equals("")) {
                    GoodsSpecification obj = this.goodsSpecService.getObjById(Long.valueOf(Long
                        .parseLong(id)));
                    clearProperty(request, obj);
                    obj.getTypes().clear();
                    this.goodsSpecService.delete(Long.valueOf(Long.parseLong(id)));
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            msg = "delerror";
        }
        return "redirect:goods_spec_list.htm?currentPage=" + currentPage + "&msg=" + msg;
    }

    @SecurityMapping(title = "商品规格属性AJAX删除", value = "/admin/goods_property_delete.htm*", rtype = "admin", rname = "规格管理", rcode = "goods_spec", rgroup = "商品")
    @RequestMapping({ "/admin/goods_property_delete.htm" })
    public void goods_property_delete(HttpServletRequest request, HttpServletResponse response,
                                      String id) {
        boolean ret = true;
        if (!id.equals("")) {
            this.databaseTools.execute("delete from jm_goods_spec where spec_id=" +

            id);
            this.databaseTools.execute("delete from jm_shopcart_gsp where gsp_id=" +

            id);
            GoodsSpecProperty property = this.goodsSpecPropertyService.getObjById(Long.valueOf(Long
                .parseLong(id)));
            property.setSpec(null);
            Accessory img = property.getSpecImage();
            CommUtil.del_acc(request, img);
            property.setSpecImage(null);
            ret = this.goodsSpecPropertyService.delete(property.getId());
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

    @SecurityMapping(title = "商品规格AJAX更新", value = "/admin/goods_spec_ajax.htm*", rtype = "admin", rname = "规格管理", rcode = "goods_spec", rgroup = "商品")
    @RequestMapping({ "/admin/goods_spec_ajax.htm" })
    public void ajax(HttpServletRequest request, HttpServletResponse response, String id,
                     String fieldName, String value) throws ClassNotFoundException {
        GoodsSpecification obj = this.goodsSpecService.getObjById(Long.valueOf(Long.parseLong(id)));
        Field[] fields = GoodsSpecification.class.getDeclaredFields();
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
        this.goodsSpecService.update(obj);
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

    @RequestMapping({ "/admin/goods_spec_verify.htm" })
    public void goods_spec_verify(HttpServletRequest request, HttpServletResponse response,
                                  String name, String id) {
        boolean ret = true;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", name);
        params.put("id", CommUtil.null2Long(id));
        List<GoodsSpecification> gss = this.goodsSpecService.query(
            "select obj from GoodsSpecification obj where obj.name=:name and obj.id!=:id", params,
            -1, -1);
        if ((gss != null) && (gss.size() > 0)) {
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
}
