package com.javamalls.ctrl.h5.buyer.action;

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
import com.javamalls.platform.domain.query.ConsultQueryObject;
import com.javamalls.platform.service.IConsultService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

/**咨询管理
 *                       
 * @Filename: ConsultBuyerAction.java
 * @Version: 2.7.0
 * @Author: 刘杰
 * @Email: attay125@163.com
 *
 */
@Controller
public class H5ConsultBuyerAction {
    @Autowired
    private ISysConfigService  configService;
    @Autowired
    private IUserConfigService userConfigService;
    @Autowired
    private IConsultService    consultService;

    @SecurityMapping(title = "买家咨询列表", value = "/mobile/buyer/consult.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/consult.htm" })
    public ModelAndView consult(HttpServletRequest request, HttpServletResponse response,
                                String reply, String currentPage,@PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/buyer_consult.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        //登录后使用
        if( SecurityUserHolder.getCurrentUser()==null){
        		mv = new JModelAndView("h5/login.html", this.configService.getSysConfig(),
                      this.userConfigService.getUserConfig(), 1, request, response);
        		mv.addObject("storeId", storeId);
                return mv;
        }
        
        ConsultQueryObject qo = new ConsultQueryObject(currentPage, mv, "createtime", "desc");
        if (!CommUtil.null2String(reply).equals("")) {
            qo.addQuery("obj.reply",
                new SysMap("reply", Boolean.valueOf(CommUtil.null2Boolean(reply))), "=");
        }
        qo.addQuery("obj.consult_user.id", new SysMap("consult_user", SecurityUserHolder
            .getCurrentUser().getId()), "=");
        IPageList pList = this.consultService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        mv.addObject("reply", CommUtil.null2String(reply));
        return mv;
    }
    @SecurityMapping(title = "买家咨询列表", value = "/mobile/buyer/consult.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/mobile/buyer/consult_ajax.htm" })
    public ModelAndView consult_ajax(HttpServletRequest request, HttpServletResponse response,
                                String reply, String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/h5/buyer_consult_ajax.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        ConsultQueryObject qo = new ConsultQueryObject(currentPage, mv, "createtime", "desc");
        if (!CommUtil.null2String(reply).equals("")) {
            qo.addQuery("obj.reply",
                new SysMap("reply", Boolean.valueOf(CommUtil.null2Boolean(reply))), "=");
        }
        qo.addQuery("obj.consult_user.id", new SysMap("consult_user", SecurityUserHolder
            .getCurrentUser().getId()), "=");
        IPageList pList = this.consultService.nolastlist(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        mv.addObject("reply", CommUtil.null2String(reply));
        return mv;
    }
}
