package com.javamalls.ctrl.admin.action;

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

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.domain.Navigation;
import com.javamalls.platform.domain.SysConfig;
import com.javamalls.platform.service.INavigationService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

/**基础设置管理
 *                       
 * @Filename: OperationManageAction.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class OperationManageAction {
    @Autowired
    private ISysConfigService  configService;
    @Autowired
    private IUserConfigService userConfigService;
    @Autowired
    private INavigationService navigationService;

    @SecurityMapping(title = "基本设置", value = "/admin/operation_base_set.htm*", rtype = "admin", rname = "基本设置", rcode = "operation_base", rgroup = "运营")
    @RequestMapping({ "/admin/operation_base_set.htm" })
    public ModelAndView operation_base_set(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("admin/blue/operation_base_setting.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        return mv;
    }

    @SecurityMapping(title = "基本设置保存", value = "/admin/base_set_save.htm*", rtype = "admin", rname = "基本设置", rcode = "operation_base", rgroup = "运营")
    @RequestMapping({ "/admin/base_set_save.htm" })
    public ModelAndView base_set_save(HttpServletRequest request, HttpServletResponse response,
                                      String id, String integral, String integralStore,
                                      String voucher, String deposit, String gold,
                                      String goldMarketValue, String groupBuy) {
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        SysConfig config = this.configService.getSysConfig();
        config.setIntegral(CommUtil.null2Boolean(integral));
        config.setIntegralStore(CommUtil.null2Boolean(integralStore));
        config.setVoucher(CommUtil.null2Boolean(voucher));
        config.setDeposit(CommUtil.null2Boolean(deposit));
        config.setGold(CommUtil.null2Boolean(gold));
        config.setGoldMarketValue(CommUtil.null2Int(goldMarketValue));
        config.setGroupBuy(CommUtil.null2Boolean(groupBuy));
        if (id.equals("")) {
            this.configService.save(config);
        } else {
            this.configService.update(config);
        }
        if (config.isIntegralStore()) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("url", "integral.htm");
            List<Navigation> navs = this.navigationService.query(
                "select obj from Navigation obj where obj.url=:url", params, -1, -1);
            if (navs.size() == 0) {
                Navigation nav = new Navigation();
                nav.setCreatetime(new Date());
                nav.setDisplay(true);
                nav.setLocation(0);
                nav.setNew_win(1);
                nav.setSequence(2);
                nav.setSysNav(true);
                nav.setTitle("积分商城");
                nav.setType("diy");
                nav.setUrl("integral.htm");
                nav.setOriginal_url("integral.htm");
                this.navigationService.save(nav);
            }
        } else {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("url", "integral.htm");
            List<Navigation> navs = this.navigationService.query(
                "select obj from Navigation obj where obj.url=:url", params, -1, -1);
            for (Navigation nav : navs) {
                this.navigationService.delete(nav.getId());
            }
        }
        if (config.isGroupBuy()) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("url", "group.htm");
            List<Navigation> navs = this.navigationService.query(
                "select obj from Navigation obj where obj.url=:url", params, -1, -1);
            if (navs.size() == 0) {
                Navigation nav = new Navigation();
                nav.setCreatetime(new Date());
                nav.setDisplay(true);
                nav.setLocation(0);
                nav.setNew_win(1);
                nav.setSequence(3);
                nav.setSysNav(true);
                nav.setTitle("团购");
                nav.setType("diy");
                nav.setUrl("group.htm");
                nav.setOriginal_url("group.htm");
                this.navigationService.save(nav);
            }
        } else {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("url", "group.htm");
            List<Navigation> navs = this.navigationService.query(
                "select obj from Navigation obj where obj.url=:url", params, -1, -1);
            for (Navigation nav : navs) {
                this.navigationService.delete(nav.getId());
            }
        }
        mv.addObject("op_title", "保存基本设置成功");
        mv.addObject("list_url", CommUtil.getURL(request) + "/admin/operation_base_set.htm");
        return mv;
    }

    @SecurityMapping(title = "积分规则", value = "/admin/operation_integral_rule.htm*", rtype = "admin", rname = "积分规则", rcode = "integral_rule", rgroup = "运营")
    @RequestMapping({ "/admin/operation_integral_rule.htm" })
    public ModelAndView integral_rule(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("admin/blue/operation_integral_rule.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        return mv;
    }

    @SecurityMapping(title = "积分规则保存", value = "/admin/integral_rule_save.htm*", rtype = "admin", rname = "积分设置", rcode = "integral_rule", rgroup = "运营")
    @RequestMapping({ "/admin/integral_rule_save.htm" })
    public ModelAndView integral_rule_save(HttpServletRequest request,
                                           HttpServletResponse response, String id,
                                           String memberRegister, String memberDayLogin,
                                           String indentComment, String consumptionRatio,
                                           String everyIndentLimit) {
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        SysConfig config = this.configService.getSysConfig();
        config.setMemberRegister(CommUtil.null2Int(memberRegister));
        config.setMemberDayLogin(CommUtil.null2Int(memberDayLogin));
        config.setIndentComment(CommUtil.null2Int(indentComment));
        config.setConsumptionRatio(CommUtil.null2Int(consumptionRatio));
        config.setEveryIndentLimit(CommUtil.null2Int(everyIndentLimit));
        if ("".equals(id) || null == id) {
            this.configService.save(config);
        } else {
            this.configService.update(config);
        }
        mv.addObject("op_title", "保存积分设置成功");
        mv.addObject("list_url", CommUtil.getURL(request) + "/admin/operation_integral_rule.htm");
        return mv;
    }
}
