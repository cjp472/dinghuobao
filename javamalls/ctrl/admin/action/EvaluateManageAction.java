package com.javamalls.ctrl.admin.action;

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
import com.javamalls.platform.domain.Evaluate;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.query.EvaluateQueryObject;
import com.javamalls.platform.service.IEvaluateService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;

/**商品评价管理
 *                       
 * @Filename: EvaluateManageAction.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class EvaluateManageAction {
    @Autowired
    private ISysConfigService  configService;
    @Autowired
    private IUserConfigService userConfigService;
    @Autowired
    private IEvaluateService   evaluateService;
    @Autowired
    private IUserService       userService;
    @Autowired
    private IStoreService      storeService;

    @SecurityMapping(title = "商品评价列表", value = "/admin/evaluate_list.htm*", rtype = "admin", rname = "商品评价", rcode = "evaluate_admin", rgroup = "交易")
    @RequestMapping({ "/admin/evaluate_list.htm" })
    public ModelAndView evaluate_list(HttpServletRequest request, HttpServletResponse response,
                                      String currentPage, String orderBy, String orderType,
                                      String goods_name, String userName) {
        ModelAndView mv = new JModelAndView("admin/blue/evaluate_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        EvaluateQueryObject qo = new EvaluateQueryObject(currentPage, mv, orderBy, orderType);
        if (!CommUtil.null2String(goods_name).equals("")) {
            qo.addQuery("obj.evaluate_goods.goods_name", new SysMap("goods_name", "%" + goods_name
                                                                                  + "%"), "like");
        }
        if (!CommUtil.null2String(userName).equals("")) {
            qo.addQuery("obj.evaluate_user.userName", new SysMap("evaluate_user", userName), "=");
        }
        mv.addObject("goods_name", goods_name);
        mv.addObject("userName", userName);
        IPageList pList = this.evaluateService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        return mv;
    }

    @SecurityMapping(title = "商品评价编辑", value = "/admin/evaluate_edit.htm*", rtype = "admin", rname = "商品评价", rcode = "evaluate_admin", rgroup = "交易")
    @RequestMapping({ "/admin/evaluate_edit.htm" })
    public ModelAndView evaluate_edit(HttpServletRequest request, HttpServletResponse response,
                                      String currentPage, String id) {
        ModelAndView mv = new JModelAndView("admin/blue/evaluate_edit.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Evaluate obj = this.evaluateService.getObjById(CommUtil.null2Long(id));
        mv.addObject("obj", obj);
        return mv;
    }

    @SecurityMapping(title = "商品评价编辑保存", value = "/admin/evaluate_save.htm*", rtype = "admin", rname = "商品评价", rcode = "evaluate_admin", rgroup = "交易")
    @RequestMapping({ "/admin/evaluate_save.htm" })
    public ModelAndView evaluate_save(HttpServletRequest request, HttpServletResponse response,
                                      String currentPage, String id, String evaluate_status,
                                      String evaluate_admin_info, String list_url, String edit) {
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Evaluate obj = this.evaluateService.getObjById(CommUtil.null2Long(id));
        obj.setEvaluate_admin_info(evaluate_admin_info);
        obj.setEvaluate_status(CommUtil.null2Int(evaluate_status));
        this.evaluateService.update(obj);
        if ((CommUtil.null2Boolean(edit)) && (obj.getEvaluate_status() == 2)) {
            User user = obj.getEvaluate_user();
            Store store = obj.getEvaluate_seller_user().getStore();
            user.setUser_credit(user.getUser_credit() - obj.getEvaluate_buyer_val());
            this.userService.update(user);
            store.setStore_credit(store.getStore_credit() - obj.getEvaluate_seller_val());
            this.storeService.update(store);
        }
        mv.addObject("list_url", list_url);
        mv.addObject("op_title", "商品评价编辑成功");
        return mv;
    }
}
