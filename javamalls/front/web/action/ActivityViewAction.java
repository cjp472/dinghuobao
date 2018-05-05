package com.javamalls.front.web.action;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.domain.Activity;
import com.javamalls.platform.domain.query.ActivityGoodsQueryObject;
import com.javamalls.platform.service.IActivityGoodsService;
import com.javamalls.platform.service.IActivityService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

@Controller
public class ActivityViewAction {

    @Autowired
    private ISysConfigService     configService;
    @Autowired
    private IUserConfigService    userConfigService;
    @Autowired
    private IActivityService      activityService;
    @Autowired
    private IActivityGoodsService activityGoodsService;
    
    /**
     * 活动
     * @param request
     * @param response
     * @param id
     * @param currentPage
     * @return
     */
    @RequestMapping({ "/activity.htm" })
    public ModelAndView activity(HttpServletRequest request, HttpServletResponse response,
                                 String id, String currentPage) {
        ModelAndView mv = new JModelAndView("activity.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        Activity act = this.activityService.getObjById(CommUtil.null2Long(id));
        ActivityGoodsQueryObject qo = new ActivityGoodsQueryObject(currentPage, mv, "createtime",
            "desc");
        qo.setPageSize(Integer.valueOf(24));
        qo.addQuery("obj.ag_status", new SysMap("ag_status", Integer.valueOf(1)), "=");
        if (act != null) {
            qo.addQuery("obj.act.id", new SysMap("act_id", act.getId()), "=");
        }
        qo.addQuery("obj.act.ac_status", new SysMap("ac_status", Integer.valueOf(1)), "=");
        qo.addQuery("obj.act.ac_begin_time", new SysMap("ac_begin_time", new Date()), "<=");
        qo.addQuery("obj.act.ac_end_time", new SysMap("ac_end_time", new Date()), ">=");
        qo.addQuery("obj.ag_goods.goods_status", new SysMap("goods_status", Integer.valueOf(0)),
            "=");
        IPageList pList = this.activityGoodsService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        mv.addObject("act", act);
        return mv;
    }
}
