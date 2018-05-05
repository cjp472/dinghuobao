package com.javamalls.ctrl.seller.action;

import java.util.Date;

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
import com.javamalls.base.tools.WebForm;
import com.javamalls.platform.domain.PredepositCash;
import com.javamalls.platform.domain.PredepositLog;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.query.PredepositCashQueryObject;
import com.javamalls.platform.service.IPredepositCashService;
import com.javamalls.platform.service.IPredepositLogService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;

@Controller
public class PredepositCashSellerAction {
    @Autowired
    private ISysConfigService      configService;
    @Autowired
    private IUserConfigService     userConfigService;
    @Autowired
    private IPredepositCashService predepositCashService;
    @Autowired
    private IPredepositLogService  predepositLogService;
    @Autowired
    private IUserService           userService;

    @SecurityMapping(title = "提现管理", value = "/buyer/buyer_cash.htm*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
    @RequestMapping({ "/seller/buyer_cash.htm" })
    public ModelAndView buyer_cash(HttpServletRequest request, HttpServletResponse response,
                                   String id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller/buyer_cash.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if (!this.configService.getSysConfig().isDeposit()) {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "系统未开启预存款");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
        } else {
            mv.addObject(
                "availableBalance",
                Double.valueOf(CommUtil.null2Double(this.userService.getObjById(
                    SecurityUserHolder.getCurrentUser().getId()).getAvailableBalance())));
        }
        return mv;
    }

    @SecurityMapping(title = "提现管理保存", value = "/buyer/buyer_cash_save.htm*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
    @RequestMapping({ "/seller/buyer_cash_save.htm" })
    public ModelAndView buyer_cash_save(HttpServletRequest request, HttpServletResponse response,
                                        String id, String currentPage) {
        ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        WebForm wf = new WebForm();
        PredepositCash obj = (PredepositCash) wf.toPo(request, PredepositCash.class);
        obj.setCash_sn("cash" + CommUtil.formatTime("yyyyMMddHHmmss", new Date())
                       + SecurityUserHolder.getCurrentUser().getId());
        obj.setCreatetime(new Date());
        obj.setCash_user(SecurityUserHolder.getCurrentUser());
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        if (CommUtil.null2Double(obj.getCash_amount()) <= CommUtil.null2Double(user
            .getAvailableBalance())) {
            this.predepositCashService.save(obj);

            PredepositLog log = new PredepositLog();
            log.setCreatetime(new Date());
            log.setPd_log_amount(obj.getCash_amount());
            log.setPd_log_info("申请提现");
            log.setPd_log_user(obj.getCash_user());
            log.setPd_op_type("提现");
            log.setPd_type("可用预存款");
            log.setPredepositCash(obj);
            this.predepositLogService.save(log);
            mv.addObject("op_title", "提现申请成功");
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "提现金额大于用户余额，提现失败");
        }
        mv.addObject("url", CommUtil.getURL(request) + "/seller/buyer_cash.htm");

        return mv;
    }

    @SecurityMapping(title = "提现管理", value = "/buyer/buyer_cash_list.htm*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
    @RequestMapping({ "/seller/buyer_cash_list.htm" })
    public ModelAndView buyer_cash_list(HttpServletRequest request, HttpServletResponse response,
                                        String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller/buyer_cash_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if (!this.configService.getSysConfig().isDeposit()) {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "系统未开启预存款");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
        } else {
            PredepositCashQueryObject qo = new PredepositCashQueryObject(currentPage, mv,
                "createtime", "desc");
            qo.addQuery("obj.cash_user.id", new SysMap("user_id", SecurityUserHolder
                .getCurrentUser().getId()), "=");
            IPageList pList = this.predepositCashService.list(qo);
            CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        }
        return mv;
    }

    @SecurityMapping(title = "会员提现详情", value = "/buyer/buyer_cash_view.htm*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
    @RequestMapping({ "/seller/buyer_cash_view.htm" })
    public ModelAndView buyer_cash_view(HttpServletRequest request, HttpServletResponse response,
                                        String id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller/buyer_cash_view.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if (this.configService.getSysConfig().isDeposit()) {
            PredepositCash obj = this.predepositCashService.getObjById(CommUtil.null2Long(id));
            mv.addObject("obj", obj);
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "系统未开启预存款");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
        }
        return mv;
    }
}
