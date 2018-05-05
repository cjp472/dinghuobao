package com.javamalls.ctrl.seller.action;

 

import java.io.Serializable;
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
import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.WebForm;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.Strategy;
import com.javamalls.platform.domain.query.StrategyQueryObject;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.IStrategyService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

/**价格策略管理
 *                       
 * @Filename: StrategySellerAction.java
 * @Version:
 * @Author: cjl
 *
 */
@Controller
public class StrategySellerAction {
    @Autowired
    private ISysConfigService      configService;
    @Autowired
    private IUserConfigService     userConfigService;
    @Autowired
    private IStrategyService       strategyService;
    @Autowired
    private IStoreService		   storeService;
  
    @SecurityMapping(title = "价格策略添加", value = "/seller/strategy_list.htm*", rtype = "seller", rname = "价格策略管理", rcode = "strategy", rgroup = "商品管理")
    @RequestMapping({ "/seller/strategy_list.htm" })
    public ModelAndView strategy_list(HttpServletRequest request, HttpServletResponse response,
                                 String currentPage, String orderBy, String orderType) {
        ModelAndView mv = new JModelAndView("seller/strategy_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
            JModelAndView.SHOP_PATH, request, response);
        
        StrategyQueryObject qo = new StrategyQueryObject(currentPage, mv, orderBy, orderType);
        qo.addQuery("obj.store.id", new SysMap("storeId",SecurityUserHolder.getCurrentUser()
                .getStore().getId()), "=");
        qo.addQuery("obj.disabled", new SysMap("disabled",false), "=");
        qo.addQuery("obj.strategy_type", new SysMap("strategy_type",2),"=");//查折扣
        IPageList pList = null;
        try {
            pList = this.strategyService.list(qo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        return mv;
    }
    
    @SecurityMapping(title = "价格策略添加", value = "/seller/strategy_add.htm*", rtype = "seller", rname = "价格策略管理", rcode = "strategy", rgroup = "商品管理")
    @RequestMapping({ "/seller/strategy_add.htm" })
    public ModelAndView strategy_add(HttpServletRequest request, HttpServletResponse response,
                                      String currentPage) {
        ModelAndView mv = new JModelAndView("seller/strategy_add.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
                JModelAndView.SHOP_PATH, request, response);
        mv.addObject("currentPage", currentPage);
        return mv;
    }
    @SecurityMapping(title = "价格策略编辑", value = "/seller/strategy_edit.htm*", rtype = "seller", rname = "价格策略理", rcode = "strategy", rgroup = "商品管理")
    @RequestMapping({ "/seller/strategy_edit.htm" })
    public ModelAndView strategy_edit(HttpServletRequest request, HttpServletResponse response,
                                       String id, String currentPage) {
    	 ModelAndView mv = new JModelAndView("seller/strategy_add.html",
                 this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
                 JModelAndView.SHOP_PATH, request, response);
        if ((id != null) && (!id.equals(""))) {
            Strategy strategy = this.strategyService.getObjById(Long
                .valueOf(Long.parseLong(id)));
            mv.addObject("obj", strategy);
            mv.addObject("currentPage", currentPage);
            mv.addObject("edit", Boolean.valueOf(true));
        }
        return mv;
    }
    @SecurityMapping(title = "价格策略保存", value = "/seller/strategy_save.htm*", rtype = "seller", rname = "价格策略管理", rcode = "strategy", rgroup = "商品管理")
    @RequestMapping({ "/seller/strategy_save.htm" })
    public ModelAndView strategy_save(HttpServletRequest request, HttpServletResponse response,
                                       String id, String currentPage, String cmd) {
        WebForm wf = new WebForm();
        Strategy strategy = null;
        if (id.equals("")) {
        	strategy = (Strategy) wf.toPo(request, Strategy.class);
        	strategy.setCreatetime(new Date());
        } else {
        	Strategy obj = this.strategyService.getObjById(Long.valueOf(Long
                .parseLong(id)));
        	strategy = (Strategy) wf.toPo(request, obj);
        }
        Store store = this.storeService.getObjByProperty("id", SecurityUserHolder.getCurrentUser()
            .getStore().getId());
        strategy.setStore(store);
        if (id.equals("")) {
            this.strategyService.save(strategy);
        } else {
            this.strategyService.update(strategy);
        }
        ModelAndView mv = new JModelAndView("user/default/usercenter/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("url", CommUtil.getURL(request) + "/seller/strategy_list.htm");
        mv.addObject("op_title", "保存策略成功");
        return mv;
    }
    
    @SecurityMapping(title = "价格策略删除", value = "/seller/strategy_del.htm*", rtype = "seller", rname = "价格策略管理", rcode = "strategy", rgroup = "商品管理")
    @RequestMapping({ "/seller/strategy_del.htm" })
    public String strategy_del(HttpServletRequest request, String mulitId) {
        String[] ids = mulitId.split(",");
        List<Serializable> listids =  new ArrayList<Serializable>();
        try{
	    	 for (String id : ids) {
	             if (!id.equals("")) {
	             	listids.add(CommUtil.null2Long(id));
	             }
	         }
	         this.strategyService.batchDelete(listids);
        }catch(Exception e){
        }
        return "redirect:"+CommUtil.getURL(request) + "/seller/strategy_list.htm";
    }
}
