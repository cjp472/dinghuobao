package com.javamalls.ctrl.seller.action;

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
import com.javamalls.base.constant.Constant;
import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.WebForm;
import com.javamalls.ctrl.admin.tools.StoreTools;
import com.javamalls.payment.chinabank.h5.util.JsonUtil;
import com.javamalls.platform.domain.GoodsBrand;
import com.javamalls.platform.domain.GoodsBrandCategory;
import com.javamalls.platform.domain.GoodsClass;
import com.javamalls.platform.domain.GoodsSpecification;
import com.javamalls.platform.domain.GoodsType;
import com.javamalls.platform.domain.GoodsTypeProperty;
import com.javamalls.platform.domain.query.GoodsTypeQueryObject;
import com.javamalls.platform.service.IGoodsBrandCategoryService;
import com.javamalls.platform.service.IGoodsBrandService;
import com.javamalls.platform.service.IGoodsClassService;
import com.javamalls.platform.service.IGoodsSpecificationService;
import com.javamalls.platform.service.IGoodsTypePropertyService;
import com.javamalls.platform.service.IGoodsTypeService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.utils.SendReqAsync;

/**商品类型管理
 * 
 *                       
 * @Filename: GoodsTypeManageAction.java
 * @Version: 2.7.0
 * @Author: 桑森林
 * @Email: sangslmail@163.com
 *
 */
@Controller
public class GoodsTypeSellerManageAction {
    @Autowired
    private ISysConfigService          configService;
    @Autowired
    private IUserConfigService         userConfigService;
    @Autowired
    private IGoodsTypeService          goodsTypeService;
    @Autowired
    private IGoodsBrandService         goodsBrandService;
    @Autowired
    private IGoodsBrandCategoryService goodsBrandCategoryService;
    @Autowired
    private IGoodsSpecificationService goodsSpecificationService;
    @Autowired
    private IGoodsTypePropertyService  goodsTypePropertyService;
    @Autowired
    private IGoodsClassService         goodsClassService;
    @Autowired
    private StoreTools                 shopTools;
    @Autowired
    private SendReqAsync sendReqAsync;

    /**
     * 商品类型列表
     * @param request
     * @param response
     * @param currentPage
     * @param orderBy
     * @param orderType
     * @return
     */
    @RequestMapping({ "/seller/goods_type_list.htm" })
    public ModelAndView list(HttpServletRequest request, HttpServletResponse response,
                             String currentPage, String orderBy, String orderType) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/goods_type_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if(SecurityUserHolder.getCurrentUser()==null){
            mv = new JModelAndView("login.html",
                      this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
                      response);
           return mv;
        }
        GoodsTypeQueryObject qo = new GoodsTypeQueryObject(currentPage, mv, orderBy, orderType);
        WebForm wf = new WebForm();
        wf.toQueryPo(request, qo, GoodsType.class, mv);
        qo.setOrderBy("sequence");
        qo.setOrderType("asc");
        qo.addQuery("obj.disabled", new SysMap("disabled",false), "=");
        qo.addQuery("obj.store_id", new SysMap("store_id",SecurityUserHolder.getCurrentUser().getStore().getId()), "=");
        IPageList pList = this.goodsTypeService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        return mv;
    }

    /**
     * 商品类型添加
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/seller/goods_type_add.htm" })
    public ModelAndView add(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/goods_type_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if(SecurityUserHolder.getCurrentUser()==null){
            mv = new JModelAndView("login.html",
                      this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
                      response);
           return mv;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("disabled", false);
        params.put("store_id", SecurityUserHolder.getCurrentUser().getStore().getId());
        List<GoodsSpecification> gss = this.goodsSpecificationService.query(
            "select obj from GoodsSpecification obj where disabled=:disabled and store_id=:store_id order by obj.sequence asc", params, -1, -1);
        mv.addObject("gss", gss);
        mv.addObject("shopTools", this.shopTools);
        return mv;
    }

    /**
     * 商品类型编辑
     * @param request
     * @param response
     * @param id
     * @param currentPage
     * @return
     */
    @RequestMapping({ "/seller/goods_type_edit.htm" })
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response, String id,
                             String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/goods_type_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if ((id != null) && (!id.equals(""))) {
            GoodsType goodsType = this.goodsTypeService
                .getObjById(Long.valueOf(Long.parseLong(id)));
            List<GoodsBrandCategory> gbcs = this.goodsBrandCategoryService.query(
                "select obj from GoodsBrandCategory obj order by sequence asc", null, -1, -1);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("disabled", false);
            params.put("store_id", SecurityUserHolder.getCurrentUser().getStore().getId());
            List<GoodsSpecification> gss = this.goodsSpecificationService.query(
                "select obj from GoodsSpecification obj where disabled=:disabled and store_id=:store_id order by obj.sequence asc", params, -1, -1);
            mv.addObject("gss", gss);
            mv.addObject("gbcs", gbcs);
            mv.addObject("shopTools", this.shopTools);
            mv.addObject("obj", goodsType);
            mv.addObject("edit", Boolean.valueOf(true));
        }
        return mv;
    }

    /**
     * 商品类型保存
     * @param request
     * @param response
     * @param id
     * @param cmd
     * @param currentPage
     * @param list_url
     * @param add_url
     * @param spec_ids
     * @param brand_ids
     * @param count
     * @return
     */
    @RequestMapping({ "/seller/goods_type_save.htm" })
    public ModelAndView save(HttpServletRequest request, HttpServletResponse response, String id,
                             String cmd, String currentPage, String list_url, String add_url,
                             String spec_ids, String brand_ids, String count) {
        WebForm wf = new WebForm();
        GoodsType goodsType = null;
        if (id==null || id.equals("")) {
            goodsType = (GoodsType) wf.toPo(request, GoodsType.class);
            goodsType.setCreatetime(new Date());
            goodsType.setStore_id( SecurityUserHolder.getCurrentUser().getStore().getId());
        } else {
            GoodsType obj = this.goodsTypeService.getObjById(Long.valueOf(Long.parseLong(id)));
            goodsType = (GoodsType) wf.toPo(request, obj);
        }
        goodsType.getGss().clear();
        goodsType.getGbs().clear();
        String[] gs_ids = spec_ids.split(",");
        GoodsSpecification gs;
        for (String gs_id : gs_ids) {
            if (!gs_id.equals("")) {
                gs = this.goodsSpecificationService.getObjById(Long.valueOf(Long.parseLong(gs_id)));
                goodsType.getGss().add(gs);
            }
        }
        String[] gb_ids = brand_ids.split(",");
        for (String gb_id : gb_ids) {
            if (!gb_id.equals("")) {
                GoodsBrand gb = this.goodsBrandService.getObjById(Long.valueOf(Long
                    .parseLong(gb_id)));
                goodsType.getGbs().add(gb);
            }
        }
        if (id.equals("")) {
            this.goodsTypeService.save(goodsType);
            //调用规格模板接口
            String write2JsonStr = JsonUtil.write2JsonStr(goodsType);
            sendReqAsync.sendMessageUtil(Constant.STORE_GOODSTYPE_URL_ADD, write2JsonStr,"新增规格模板");
            System.out.println(write2JsonStr);
        } else {
            this.goodsTypeService.update(goodsType);
            //调用规格模板接口
            String write2JsonStr = JsonUtil.write2JsonStr(goodsType);
            sendReqAsync.sendMessageUtil(Constant.STORE_GOODSTYPE_URL_EDIT, write2JsonStr,"编辑规格模板");
            System.out.println(write2JsonStr);
        }
        genericProperty(request, goodsType, count);
        JModelAndView mv = new JModelAndView("user/default/usercenter/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        ((ModelAndView) mv).addObject("url", list_url + "?currentPage=" + currentPage);
        ((ModelAndView) mv).addObject("op_title", "保存商品类型成功");
        if (add_url != null) {
            ((ModelAndView) mv).addObject("add_url", add_url);
        }
        return mv;
    }

    public void genericProperty(HttpServletRequest request, GoodsType type, String count) {
        for (int i = 1; i <= CommUtil.null2Int(count); i++) {
            int sequence = CommUtil.null2Int(request.getParameter("gtp_sequence_" + i));
            String name = CommUtil.null2String(request.getParameter("gtp_name_" + i));
            String value = CommUtil.null2String(request.getParameter("gtp_value_" + i));
            boolean display = CommUtil.null2Boolean(request.getParameter("gtp_display_" + i));
            if ((!name.equals("")) && (!value.equals(""))) {
                GoodsTypeProperty gtp = null;
                String id = CommUtil.null2String(request.getParameter("gtp_id_" + i));
                if (id.equals("")) {
                    gtp = new GoodsTypeProperty();
                } else {
                    gtp = this.goodsTypePropertyService
                        .getObjById(Long.valueOf(Long.parseLong(id)));
                }
                gtp.setCreatetime(new Date());
                gtp.setDisplay(display);
                gtp.setGoodsType(type);
                gtp.setName(name);
                gtp.setSequence(sequence);
                gtp.setValue(value);
                if (id.equals("")) {
                    this.goodsTypePropertyService.save(gtp);
                } else {
                    this.goodsTypePropertyService.update(gtp);
                }
            }
        }
    }

    /**
     * 商品类型删除
     * @param request
     * @param mulitId
     * @param currentPage
     * @return
     */
    @RequestMapping({ "/seller/goods_type_del.htm" })
    public String delete(HttpServletRequest request, String mulitId, String currentPage) {
        String[] ids = mulitId.split(",");
        for (String id : ids) {
            if (!id.equals("")) {
                GoodsType goodsType = this.goodsTypeService.getObjById(Long.valueOf(Long
                    .parseLong(id)));
                goodsType.getGbs().clear();
                goodsType.getGss().clear();
                for (GoodsClass gc : goodsType.getGcs()) {
                    gc.setGoodsType(null);
                    this.goodsClassService.update(gc);
                }
                goodsType.setDisabled(true);
                this.goodsTypeService.update(goodsType);//改为逻辑删除
                //调用规格模板接口
               // String write2JsonStr = JsonUtil.write2JsonStr(goodsType);
                Map<String, Object> map=new HashMap<String, Object>();
                map.put("id", goodsType.getId());
                String write2JsonStr = JsonUtil.write2JsonStr(map);
                sendReqAsync.sendMessageUtil(Constant.STORE_GOODSTYPE_URL_DEL,write2JsonStr,"删除规格模板");
               /* this.goodsTypeService.delete(Long.valueOf(Long.parseLong(id)));*/
            }
        }
        return "redirect:goods_type_list.htm?currentPage=" + currentPage;
    }

    /**
     * 商品类型属性AJAX删除
     * @param request
     * @param response
     * @param id
     */
    @RequestMapping({ "/seller/goods_type_property_delete.htm" })
    public void goods_type_property_delete(HttpServletRequest request,
                                           HttpServletResponse response, String id) {
        boolean ret = true;
        if (!id.equals("")) {
            GoodsTypeProperty property = this.goodsTypePropertyService.getObjById(Long.valueOf(Long
                .parseLong(id)));
            property.setGoodsType(null);
            ret = this.goodsTypePropertyService.delete(property.getId());
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

    /**
     * 商品类型AJAX更新
     * @param request
     * @param response
     * @param id
     * @param fieldName
     * @param value
     * @throws ClassNotFoundException
     */
    @RequestMapping({ "/seller/goods_type_ajax.htm" })
    public void ajax(HttpServletRequest request, HttpServletResponse response, String id,
                     String fieldName, String value) throws ClassNotFoundException {
        GoodsType obj = this.goodsTypeService.getObjById(Long.valueOf(Long.parseLong(id)));
        Field[] fields = GoodsType.class.getDeclaredFields();
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
        this.goodsTypeService.update(obj);
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

    @RequestMapping({ "/seller/goods_type_verify.htm" })
    public void goods_type_verify(HttpServletRequest request, HttpServletResponse response,
                                  String name, String id) {
        boolean ret = true;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", name);
        params.put("id", CommUtil.null2Long(id));
        params.put("store_id", SecurityUserHolder.getCurrentUser().getStore().getId());
        params.put("disabled", false);
        List<GoodsType> gts = this.goodsTypeService.query(
            "select obj from GoodsType obj where obj.disabled=:disabled and store_id=:store_id and obj.name=:name and obj.id!=:id", params, -1, -1);
        if ((gts != null) && (gts.size() > 0)) {
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
