package com.javamalls.ctrl.seller.action;

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
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.domain.query.PredepositLogQueryObject;
import com.javamalls.platform.service.IPredepositLogService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;

@Controller
public class PredepositSellerAction {
    @Autowired
    private ISysConfigService     configService;
    @Autowired
    private IUserConfigService    userConfigService;
    @Autowired
    private IPredepositLogService predepositLogService;
    @Autowired
    private IUserService          userService;


    
    @SecurityMapping(title = "会员收入明细", value = "/buyer/predeposit_log.htm*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
    @RequestMapping({ "/seller/predeposit_log.htm" })
    public ModelAndView predeposit_log(HttpServletRequest request, HttpServletResponse response,
                                       String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller/seller_predeposit_log.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if (this.configService.getSysConfig().isDeposit()) {
            PredepositLogQueryObject qo = new PredepositLogQueryObject(currentPage, mv,
                "createtime", "desc");
            qo.addQuery("obj.pd_log_user.id", new SysMap("user_id", SecurityUserHolder
                .getCurrentUser().getId()), "=");
            IPageList pList = this.predepositLogService.list(qo);
            CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
            mv.addObject("user",
                this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId()));
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "系统未开启预存款");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
        }
        return mv;
    }
}
