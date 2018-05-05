package com.javamalls.front.web.action;

import java.util.ArrayList;
import java.util.Calendar;
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

import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.domain.BargainGoods;
import com.javamalls.platform.domain.query.BargainGoodsQueryObject;
import com.javamalls.platform.service.IBargainGoodsService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

/**折扣商品
 *                       
 * @Filename: BargainViewAction.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class BargainViewAction {

    @Autowired
    private ISysConfigService    configService;
    @Autowired
    private IUserConfigService   userConfigService;
    @Autowired
    private IBargainGoodsService bargainGoodsService;

    @RequestMapping({ "/bargain.htm" })
    public ModelAndView bargain(HttpServletRequest request, HttpServletResponse response,
                                String bg_time, String currentPage, String orderBy, String orderType) {
        ModelAndView mv = new JModelAndView("bargain.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        BargainGoodsQueryObject bqo = new BargainGoodsQueryObject(currentPage, mv, orderBy,
            orderType);
        if (CommUtil.null2String(bg_time).equals("")) {
            bqo.addQuery("obj.bg_time",
                new SysMap("bg_time", CommUtil.formatDate(CommUtil.formatShortDate(new Date()))),
                "=");
        } else {
            bqo.addQuery("obj.bg_time", new SysMap("bg_time", CommUtil.formatDate(bg_time)), "=");
        }
        bqo.addQuery("obj.bg_status", new SysMap("bg_status", Integer.valueOf(1)), "=");
        bqo.setPageSize(Integer.valueOf(20));
        IPageList pList = this.bargainGoodsService.list(bqo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);

        Map<String, Object> params = new HashMap<String, Object>();
        Calendar cal = Calendar.getInstance();
        if (CommUtil.null2String(bg_time).equals("")) {
            bg_time = CommUtil.formatShortDate(new Date());
        }
        cal.setTime(CommUtil.formatDate(bg_time));
        cal.add(6, 1);
        params.put("bg_time", CommUtil.formatDate(CommUtil.formatShortDate(cal.getTime())));
        params.put("bg_status", Integer.valueOf(1));
        List<BargainGoods> bgs = this.bargainGoodsService
            .query(
                "select obj from BargainGoods obj where obj.bg_time=:bg_time and obj.bg_status=:bg_status order by audit_time desc",
                params, 0, 5);
        mv.addObject("bgs", bgs);
        int day_count = this.configService.getSysConfig().getBargain_validity();
        List<Date> dates = new ArrayList<Date>();
        for (int i = 0; i < day_count; i++) {
            cal = Calendar.getInstance();
            cal.add(6, i);
            dates.add(cal.getTime());
        }
        mv.addObject("dates", dates);
        mv.addObject("bg_time", bg_time);
        return mv;
    }
}
