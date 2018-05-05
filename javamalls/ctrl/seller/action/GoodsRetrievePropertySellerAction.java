package com.javamalls.ctrl.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.domain.CommonEntity;
import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.domain.GoodsRetrieve;
import com.javamalls.platform.domain.GoodsRetrieveProperty;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.query.GoodsRetrievePropertyQueryObject;
import com.javamalls.platform.service.IGoodsRetrievePropertyService;
import com.javamalls.platform.service.IGoodsRetrieveService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

/**检索属性
 * 
 *                       
 * @Filename: GoodsRetrieveSellerAction.java
 * @Version: 2.7.0
 * @Author: 桑森林
 * @Email: sangslmail@163.com
 *
 */
@Controller
public class GoodsRetrievePropertySellerAction extends CommonEntity {
    @Autowired
    private ISysConfigService             configService;
    @Autowired
    private IUserConfigService            userConfigService;
    @Autowired
    private IGoodsRetrievePropertyService goodsRetrievePropertyService;
    @Autowired
    private IGoodsRetrieveService         goodsRetrieveService;

    @RequestMapping({ "/seller/usergoodsRetrieveProperty.htm" })
    public ModelAndView usergoodsRetrieveProperty(HttpServletRequest request,
                                                  HttpServletResponse response, String currentPage,
                                                  String orderBy, String orderType) {
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/usergoodsretrieveproperty.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if (SecurityUserHolder.getCurrentUser() == null) {
            mv = new JModelAndView("login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            return mv;
        }
        GoodsRetrievePropertyQueryObject qo = new GoodsRetrievePropertyQueryObject(currentPage, mv,
            orderBy, orderType);
        //qo.addQuery("obj.user.id", new SysMap("uid", SecurityUserHolder.getCurrentUser().getId()),"=");
        qo.addQuery("obj.store.id", new SysMap("store_id", SecurityUserHolder.getCurrentUser()
            .getStore().getId()), "=");
        qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");
        qo.setOrderBy("createtime");
        qo.setOrderType("desc");
        IPageList pList = this.goodsRetrievePropertyService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        return mv;
    }

    @RequestMapping({ "/seller/usergoodsretrieveproperty_add.htm" })
    public ModelAndView usergoodsretrieveproperty_add(HttpServletRequest request,
                                                      HttpServletResponse response) {
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/usergoodsretrieveproperty_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if (SecurityUserHolder.getCurrentUser() == null) {
            mv = new JModelAndView("buyer/buyer_login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), JModelAndView.SHOP_PATH, request, response);
            return mv;
        }
        return mv;
    }

    @RequestMapping({ "/seller/usergoodsretrieveproperty_add_changeType.htm" })
    public ModelAndView usergoodsretrieveproperty_add_changeType(HttpServletRequest request,
                                                                 HttpServletResponse response,
                                                                 Integer type, Model model) {
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/usergoodsretrieveproperty_add_changeType.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String sql = null;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("disabled", false);
        map.put("type", type);
        List<GoodsRetrieve> goodsRetrieves = new ArrayList<GoodsRetrieve>();
        if (type == null || type == 0) {

        } else {
            sql = "select obj from GoodsRetrieve obj where disabled=:disabled and type=:type";
            goodsRetrieves = this.goodsRetrieveService.query(sql, map, -1, -1);
        }
        model.addAttribute("goodsRetrieves", goodsRetrieves);

        return mv;
    }

    @RequestMapping({ "/seller/usergoodsretrieveproperty_save.htm" })
    public void usergoodsretrieveproperty_save(HttpServletRequest request,
                                               HttpServletResponse response, String retrieveId,
                                               String value, Long goodsRetrievePropertyId) {
        int ret = 0;//未登录
        User user = SecurityUserHolder.getCurrentUser();
        if (user == null) {

        } else if (retrieveId == null || retrieveId.equals("") || value == null || value.equals("")) {
            ret = 1;//传参错误
        } else {
            //判断名字是否存在

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("disabled", false);
            map.put("retrieve", CommUtil.null2Long(retrieveId));
            map.put("store", user.getStore().getId());
            map.put("value", value);
            String sql = "select obj from GoodsRetrieveProperty obj where obj.disabled=:disabled and obj.retrieve.id=:retrieve and obj.store.id =:store and obj.value=:value";
            List<GoodsRetrieveProperty> goodsRetrievesPropertyList = this.goodsRetrievePropertyService
                .query(sql, map, -1, -1);
            if (goodsRetrievePropertyId == null || goodsRetrievePropertyId == 0
                || goodsRetrievePropertyId.equals("")) {
                //添加
                if (goodsRetrievesPropertyList.size() > 0) {
                    ret = 4;//名称已经存在，添加失败
                } else {
                    try {
                        //添加
                        GoodsRetrieveProperty goodsRetrieveProperty = new GoodsRetrieveProperty();
                        goodsRetrieveProperty.setCreatetime(new Date());
                        goodsRetrieveProperty.setDisabled(false);
                        goodsRetrieveProperty.setRetrieve(goodsRetrieveService.getObjById(CommUtil
                            .null2Long(retrieveId)));
                        goodsRetrieveProperty.setSequence(0);
                        goodsRetrieveProperty.setValue(value);
                        goodsRetrieveProperty.setStore(user.getStore());
                        goodsRetrievePropertyService.save(goodsRetrieveProperty);
                        ret = 2;//保存成功
                    } catch (Exception e) {
                        ret = 3;//保存失败
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    //添加
                    GoodsRetrieveProperty goodsRetrieveProperty = goodsRetrievePropertyService
                        .getObjById(goodsRetrievePropertyId);
                    goodsRetrieveProperty.setRetrieve(goodsRetrieveService.getObjById(CommUtil
                        .null2Long(retrieveId)));
                    goodsRetrieveProperty.setValue(value);
                    goodsRetrievePropertyService.update(goodsRetrieveProperty);
                    ret = 2;//保存成功
                } catch (Exception e) {
                    ret = 3;//保存失败
                    // TODO Auto-generated catch block
                    e.printStackTrace();
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

    @RequestMapping({ "/seller/usergoodsretrieveproperty_edit.htm" })
    public ModelAndView usergoodsretrieveproperty_edit(HttpServletRequest request,
                                                       HttpServletResponse response,
                                                       Long goodsRetrievePropertyId)
                                                                                    throws Exception {
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/usergoodsretrieveproperty_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if (SecurityUserHolder.getCurrentUser() == null) {
            mv = new JModelAndView("login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            return mv;
        }
        if (goodsRetrievePropertyId == null || goodsRetrievePropertyId == 0) {
            throw new Exception("未找到参数");
        } else {
            GoodsRetrieveProperty goodsRetrieveProperty = goodsRetrievePropertyService
                .getObjById(goodsRetrievePropertyId);

            String sql = null;
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("disabled", false);
            map.put("type", goodsRetrieveProperty.getRetrieve().getType());
            sql = "select obj from GoodsRetrieve obj where disabled=:disabled and type=:type";
            List<GoodsRetrieve> goodsRetrieves = this.goodsRetrieveService.query(sql, map, -1, -1);

            mv.addObject("goodsRetrieves", goodsRetrieves);
            mv.addObject("goodsRetrieveProperty", goodsRetrieveProperty);
        }
        return mv;
    }

    @RequestMapping({ "/seller/usergoodsretrieveproperty_del.htm" })
    public void usergoodsretrieveproperty_del(HttpServletRequest request,
                                              HttpServletResponse response,
                                              Long goodsRetrievePropertyId) throws Exception {
        Integer ret = 0;
        if (SecurityUserHolder.getCurrentUser() != null) {
            if (goodsRetrievePropertyId == null || goodsRetrievePropertyId == 0) {
                ret = 1;
                throw new Exception("未找到参数");
            } else {
                try {
                    GoodsRetrieveProperty goodsRetrieveProperty = goodsRetrievePropertyService
                        .getObjById(goodsRetrievePropertyId);
                    goodsRetrieveProperty.setDisabled(true);
                    goodsRetrievePropertyService.update(goodsRetrieveProperty);
                    ret = 2;
                } catch (Exception e) {
                    ret = 3;
                    e.printStackTrace();
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

}
