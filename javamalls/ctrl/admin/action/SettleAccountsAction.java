package com.javamalls.ctrl.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.TimeUtil;
import com.javamalls.ctrl.admin.tools.MsgTools;
import com.javamalls.platform.domain.SettleAccunts;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.query.SettleAccountsQueryObject;
import com.javamalls.platform.service.ISettleAccountsService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;

/**结算设置
 *                       
 * @Filename: SettleAccountsAction.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class SettleAccountsAction {
    @Autowired
    private ISysConfigService      configService;
    @Autowired
    private IUserService           userService;
    @Autowired
    private IUserConfigService     userConfigService;
    @Autowired
    private ISettleAccountsService settleAccountsService;
    @Autowired
    private MsgTools               msgTools;
    @Autowired
    private IStoreService          storeService;

    /**结算设置列表
     * @param request
     * @param response
     * @param currentPage
     * @param orderBy
     * @param orderType
     * @param ig_goods_name
     * @param ig_show
     * @return
     */
    @SecurityMapping(title = "结算设置列表", value = "/admin/settle_list.htm*", rtype = "admin", rname = "结算设置", rcode = "jiesuan_set", rgroup = "统计结算")
    @RequestMapping({ "/admin/settle_list.htm" })
    public ModelAndView settle_list(HttpServletRequest request, HttpServletResponse response,
                                    String currentPage, String orderBy, String orderType,
                                    String ig_goods_name, String ig_show) {
        ModelAndView mv = new JModelAndView("admin/blue/settle_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        SettleAccountsQueryObject qo = new SettleAccountsQueryObject(currentPage, mv, orderBy,
            orderType);
        qo.setOrderBy(orderBy);
        qo.setOrderType(orderType);
        IPageList pList = this.settleAccountsService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        return mv;
    }

    /**设置结算页面
     * @param request
     * @param response
     * @param id
     * @param settleAccunts
     * @return
     */
    @SecurityMapping(title = "设置结算页面", value = "/admin/settle_set.htm*", rtype = "admin", rname = "结算设置", rcode = "jiesuan_set", rgroup = "统计结算")
    @RequestMapping({ "/admin/settle_set.htm" })
    public ModelAndView settle_set(HttpServletRequest request, HttpServletResponse response,
                                   String id, SettleAccunts settleAccunts) {

        ModelAndView mv = new JModelAndView("admin/blue/settle_set.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        try {
            String lastDay = TimeUtil.getMaxDayOfMonth(Integer.valueOf(TimeUtil.getYear()),
                Integer.valueOf(TimeUtil.getMonth()));
            String curDay = TimeUtil.getChineseToDay();
            if (id != null && !"".equals(id)) {
                settleAccunts = settleAccountsService.getObjById(Long.valueOf(id));
            } else {
                int month = Integer.valueOf(TimeUtil.getMonth());
                List<SettleAccunts> settles = settleAccountsService.query(
                    "select obj from SettleAccunts obj where obj.month = " + month, null, -1, -1);
                if (settles != null && settles.size() > 0) {
                    settleAccunts = settles.get(0);
                    if (settleAccunts == null) {
                        settleAccunts = new SettleAccunts();
                        String content = "本月的结算日期为1日，请于结算日申请结算。";
                        settleAccunts.setMsg_content(content);
                    }
                }
                settleAccunts.setYongjin(0);
                settleAccunts.setMsg_type(1);
            }
            mv.addObject("lastDay", lastDay);
            mv.addObject("settle_date", settleAccunts.getSettle_date());
            mv.addObject("curDay", curDay);
            mv.addObject("obj", settleAccunts);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return mv;
    }

    /**添加或修改
     * @param request
     * @param response
     * @param settleAccunts
     * @return
     */
    @RequestMapping({ "/admin/settle_save.htm" })
    public String settle_save(HttpServletRequest request, HttpServletResponse response,
                              SettleAccunts settleAccunts, String userName) {
        try {
            /*List<User> users = this.userService.query(
            		"from User obj where obj.userRole = 'ADMIN' and obj.userName = '" + userName + "'", null, -1, -1);
            if(users != null && users.size() > 0 && users.size() == 1){
            	User u = users.get(0);
            	settleAccunts.setUser(u);
            }*/
            User user = SecurityUserHolder.getCurrentUser();
            if (user == null)
                return "redirect:/admin/login.htm";
            Long id = settleAccunts.getId();
            settleAccunts.setCreatetime(new Date());
            //			settleAccunts.setUser();
            settleAccunts.setOpt_user(user.getId());
            settleAccunts.setMonth(Integer.valueOf(TimeUtil.getMonth()));
            if (id != null && !"".equals(id)) {
                settleAccunts.setUpdatetime(new Date());
                settleAccountsService.update(settleAccunts);
            } else {
                settleAccountsService.save(settleAccunts);
            }
            /*if (this.configService.getSysConfig().isSmsEnbale()) {
                List<Store> stores = storeService.query("select obj from Store obj", null, -1, -1);
                if (stores != null && stores.size() > 0) {
                    for (int i = 0; i < stores.size(); i++) {
                        String mobile = stores.get(i).getStore_telephone();
                        if (mobile != null && !"".equals(mobile))
                            this.msgTools.sendSMS(mobile, settleAccunts.getMsg_content());
                    }
                }
            }*/
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return "redirect:/admin/settle_list.htm";
    }

    /**删除
     * @param request
     * @param response
     * @param currentPage
     * @param id
     * @return
     */
    @RequestMapping({ "/admin/settle_del.htm" })
    public String settle_del(HttpServletRequest request, HttpServletResponse response,
                             String currentPage, String mulitId) {
        if (mulitId != null && !"".equals(mulitId)) {
            String[] ids = mulitId.split(",");
            List<Long> list = new ArrayList<Long>();
            for (int i = 0; i < ids.length; i++) {
                list.add(Long.valueOf(ids[i]));
            }
            this.settleAccountsService.batchDelete(list);
        }
        return "redirect:/admin/settle_list.htm?currentPage=" + currentPage;
    }

    /**根据用户名查找用户
     * @param request
     * @param response
     * @param userName
     */
    @RequestMapping({ "/admin/settle_search.htm" })
    public void settle_search(HttpServletRequest request, HttpServletResponse response,
                              String userName) {
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            List<User> users = this.userService.query(
                "from User obj where obj.userRole like '%ADMIN%' and obj.userName = '" + userName
                        + "'", null, -1, -1);
            PrintWriter writer = response.getWriter();
            if (users != null && users.size() > 0 && users.size() == 1) {
                writer.print("success");
            } else {
                writer.print("error");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
