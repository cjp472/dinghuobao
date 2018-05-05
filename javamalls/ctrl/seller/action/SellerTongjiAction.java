package com.javamalls.ctrl.seller.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.service.IQueryService;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

@Controller
public class SellerTongjiAction {
    @Autowired
    private ISysConfigService  configService;
    @Autowired
    private IUserConfigService userConfigService;
    @Autowired
    private IQueryService      gueryService;

    @SecurityMapping(title = "店铺总流量", value = "/seller/tongji_liuliang_shop.htm*", rtype = "seller", rname = "流量统计", rcode = "tongji_seller", rgroup = "统计")
    @RequestMapping({ "/seller/tongji_liuliang_shop.htm" })
    public ModelAndView tongji_liuliang_shop(HttpServletRequest request,
                                             HttpServletResponse response, String currentPage,
                                             String beginTime, String endTime) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/tongji_liuliang_shop.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);

        // 是否显示统计结果
        boolean showflag = false;

        // 只有点击查询按钮后才统计
        if (beginTime != null && endTime != null) {
            // 查询条件trim
            beginTime = beginTime.trim();
            endTime = endTime.trim();

            String all_date = "'";
            String all_ac = "";
            long dateInterval = this.DateInterval(beginTime, endTime);
            if (dateInterval < 0) {
                mv.addObject("beginTime", beginTime);
                mv.addObject("endTime", endTime);
                mv.addObject("showflag", false);

                return mv;
            }

            // 当前店铺id
            long now_store_id = SecurityUserHolder.getCurrentUser().getStore().getId();
            try {
                // 一个月以内时按日统计
                if (dateInterval <= 30) {
                    for (int i = 0; i < dateInterval + 1; i++) {
                        if (i == 0) {
                            all_date += this.addDate(beginTime, i, "dd");
                        } else {
                            all_date += "', '" + this.addDate(beginTime, i, "dd");
                        }

                        String thisday = this.addDate(beginTime, i, "yyyy-MM-dd");

                        // 查询条件和参数初始化
                        String ser_cnd = "";
                        Map params = new HashMap();
                        // 查询条件-访问时间
                        if (!CommUtil.null2String(beginTime).equals("")) {
                            ser_cnd += " and unix_timestamp(ta.actime) >= unix_timestamp('"
                                       + thisday + " 00:00:00')";
                        }
                        if (!CommUtil.null2String(endTime).equals("")) {
                            ser_cnd += " and unix_timestamp(ta.actime) <= unix_timestamp('"
                                       + thisday + " 23:59:59')";
                        }

                        // 统计被访问过的本店商品总数
                        String strSql1 = "";
                        strSql1 = "select count(ta.tid) " + " from jm_track t"
                                  + " inner join jm_goods g" + " on t.goodsid = g.id"
                                  + " inner join jm_track_actime ta" + " on t.id = ta.tid"
                                  + " where g.goods_store_id=" + now_store_id + ser_cnd;
                        List temp_info = gueryService.executeNativeQuery(strSql1, params, -1, -1);
                        String strTemp = temp_info.get(0).toString();
                        if (i == 0) {
                            all_ac += strTemp;
                        } else {
                            all_ac += "," + strTemp;
                        }

                    }
                }
                // 超过1个月时按月统计
                else {
                    long monthInterval = this.getMonthInterval(beginTime, endTime);
                    if (monthInterval < 0) {
                        mv.addObject("beginTime", beginTime);
                        mv.addObject("endTime", endTime);
                        mv.addObject("showflag", false);

                        return mv;
                    }

                    for (int i = 0; i < monthInterval + 1; i++) {
                        if (i == 0) {
                            all_date += this.addMonth(beginTime, i, "yy年MM月");
                        } else {
                            all_date += "', '" + this.addMonth(beginTime, i, "yy年MM月");
                        }

                        String firstday = this.addMonth(beginTime, i, "yyyy-MM") + "-01";
                        String lastday = this.getLastDayOfMonth(firstday);

                        // 查询条件和参数初始化
                        String ser_cnd = "";
                        Map params = new HashMap();
                        // 查询条件-访问时间
                        if (!CommUtil.null2String(beginTime).equals("")) {
                            ser_cnd += " and unix_timestamp(ta.actime) >= unix_timestamp('"
                                       + firstday + " 00:00:00')";
                        }
                        if (!CommUtil.null2String(endTime).equals("")) {
                            ser_cnd += " and unix_timestamp(ta.actime) <= unix_timestamp('"
                                       + lastday + " 23:59:59')";
                        }

                        // 统计被访问过的本店商品总数
                        String strSql1 = "";
                        strSql1 = "select count(ta.tid) " + " from jm_track t"
                                  + " inner join jm_goods g" + " on t.goodsid = g.id"
                                  + " inner join jm_track_actime ta" + " on t.id = ta.tid"
                                  + " where g.goods_store_id=" + now_store_id + ser_cnd;
                        List temp_info = gueryService.executeNativeQuery(strSql1, params, -1, -1);
                        String strTemp = temp_info.get(0).toString();
                        if (i == 0) {
                            all_ac += strTemp;
                        } else {
                            all_ac += "," + strTemp;
                        }
                    }

                }

                all_date += "'";
                showflag = true;
                mv.addObject("all_date", all_date);
                mv.addObject("all_ac", all_ac);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 查询条件
        mv.addObject("beginTime", beginTime);
        mv.addObject("endTime", endTime);
        mv.addObject("showflag", showflag);

        return mv;
    }

    @SecurityMapping(title = "商品流量排名", value = "/seller/tongji_liuliang_goods.htm*", rtype = "seller", rname = "流量统计", rcode = "tongji_seller", rgroup = "统计")
    @RequestMapping({ "/seller/tongji_liuliang_goods.htm" })
    public ModelAndView tongji_liuliang_goods(HttpServletRequest request,
                                              HttpServletResponse response, String currentPage,
                                              String beginTime, String endTime) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/tongji_liuliang_goods.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);

        // 是否显示统计结果
        boolean showflag = false;
        // 分页参数
        int pages = 0;
        int one_page_num = 10;
        int all_stores_cnt = 0;
        int now_page = 1;
        int begin_index = 0;
        if (currentPage != null && !"".equals(currentPage)) {
            now_page = Integer.parseInt(currentPage);
        }
        begin_index = (now_page - 1) * one_page_num;

        // 只有点击查询按钮后才统计
        if (beginTime != null && endTime != null) {
            // 查询条件trim
            beginTime = beginTime.trim();
            endTime = endTime.trim();

            // 查询条件和参数初始化
            String ser_cnd = "";
            Map params = new HashMap();

            // 查询条件-下单时间
            if (!CommUtil.null2String(beginTime).equals("")) {
                ser_cnd += " and unix_timestamp(ta.actime) >= unix_timestamp('" + beginTime
                           + " 00:00:00')";
            }
            if (!CommUtil.null2String(endTime).equals("")) {
                ser_cnd += " and unix_timestamp(ta.actime) <= unix_timestamp('" + endTime
                           + " 23:59:59')";
            }

            // 当前店铺id
            long now_store_id = SecurityUserHolder.getCurrentUser().getStore().getId();

            // 统计被访问过的本店商品总数
            String strSql1 = "";
            strSql1 = "select count(DISTINCT t.goodsid) " + " from jm_track t"
                      + " inner join jm_goods g" + " on t.goodsid = g.id"
                      + " inner join jm_track_actime ta" + " on t.id = ta.tid"
                      + " where g.goods_store_id=" + now_store_id + ser_cnd;
            List temp_info = gueryService.executeNativeQuery(strSql1, params, -1, -1);
            all_stores_cnt = Integer.parseInt(temp_info.get(0).toString());
            // 计算分页数
            double temp_pages = (double) all_stores_cnt / (double) one_page_num;
            pages = (int) Math.ceil(temp_pages);

            // 分别统计本店商品的访问数
            String strSql = "";
            strSql = "select" + "  t.goodsid," + "  g.goods_name,"
                     + "  count(ta.tid) as all_cnt_num" + " from jm_track t"
                     + " inner join jm_goods g" + " on t.goodsid = g.id"
                     + " inner join jm_track_actime ta" + " on t.id = ta.tid"
                     + " where g.goods_store_id=" + now_store_id + ser_cnd + " group by t.goodsid"
                     + " order by all_cnt_num desc";

            ArrayList<Object[]> stores_info = (ArrayList) gueryService.executeNativeQuery(strSql,
                params, begin_index, one_page_num);
            // 商品名
            String goods_name = "'";
            // 总访问数
            String all_ac = "";

            int stores_count = stores_info.size();
            for (int i = 0; i < stores_count; i++) {
                Object[] store_info = stores_info.get(i);
                if (i == 0) {
                    goods_name += store_info[1].toString();
                    all_ac += store_info[2].toString();
                } else {
                    goods_name += "', '" + store_info[1].toString();
                    all_ac += ", " + store_info[2].toString();
                }

            }
            goods_name += "'";

            mv.addObject("goods_name", goods_name);
            mv.addObject("all_ac", all_ac);
            mv.addObject("gotoPageFormAtagHTML", CommUtil.showPageFormAtagHtml(now_page, pages));

            showflag = true;
        }

        // 查询条件
        mv.addObject("beginTime", beginTime);
        mv.addObject("endTime", endTime);
        mv.addObject("showflag", showflag);

        return mv;
    }

    @SecurityMapping(title = "店铺总销量", value = "/seller/tongji_xiaoliang_shop.htm*", rtype = "seller", rname = "销量统计", rcode = "tongji_seller", rgroup = "统计")
    @RequestMapping({ "/seller/tongji_xiaoliang_shop.htm" })
    public ModelAndView tongji_xiaoliang_shop(HttpServletRequest request,
                                              HttpServletResponse response, String currentPage,
                                              String beginTime, String endTime) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/tongji_xiaoliang_shop.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);

        // 是否显示统计结果
        boolean showflag = false;

        // 只有点击查询按钮后才统计
        if (beginTime != null && endTime != null) {
            // 查询条件trim
            beginTime = beginTime.trim();
            endTime = endTime.trim();

            String all_date = "'";
            String all_ac = "";
            long dateInterval = this.DateInterval(beginTime, endTime);
            if (dateInterval < 0) {
                mv.addObject("beginTime", beginTime);
                mv.addObject("endTime", endTime);
                mv.addObject("showflag", false);

                return mv;
            }

            // 当前店铺id
            long now_store_id = SecurityUserHolder.getCurrentUser().getStore().getId();
            try {
                // 一个月以内时按日统计
                if (dateInterval <= 30) {
                    for (int i = 0; i < dateInterval + 1; i++) {
                        if (i == 0) {
                            all_date += this.addDate(beginTime, i, "dd");
                        } else {
                            all_date += "', '" + this.addDate(beginTime, i, "dd");
                        }

                        String thisday = this.addDate(beginTime, i, "yyyy-MM-dd");

                        // 查询条件和参数初始化
                        String ser_cnd = "";
                        Map params = new HashMap();
                        // 查询条件-下单时间
                        if (!CommUtil.null2String(beginTime).equals("")) {
                            ser_cnd += " and unix_timestamp(o.createtime) >= unix_timestamp('"
                                       + thisday + " 00:00:00')";
                        }
                        if (!CommUtil.null2String(endTime).equals("")) {
                            ser_cnd += " and unix_timestamp(o.createtime) <= unix_timestamp('"
                                       + thisday + " 23:59:59')";
                        }

                        // 统计本店卖出的商品总数
                        String strSql1 = "";
                        strSql1 = "select" + "  COALESCE(sum(sc.count),0) as all_cnt_num"
                                  + " from jm_goods g" + " inner join jm_goods_shopcart sc"
                                  + " on g.id = sc.goods_id" + " inner join jm_order o"
                                  + " on sc.of_id = o.id" + " where o.order_status>=20"
                                  + " and o.disabled=0" + " and g.goods_store_id=" + now_store_id
                                  + ser_cnd;
                        List temp_info = gueryService.executeNativeQuery(strSql1, params, -1, -1);
                        String strTemp = temp_info.get(0).toString();
                        if (i == 0) {
                            all_ac += strTemp;
                        } else {
                            all_ac += "," + strTemp;
                        }

                    }
                }
                // 超过1个月时按月统计
                else {
                    long monthInterval = this.getMonthInterval(beginTime, endTime);
                    if (monthInterval < 0) {
                        mv.addObject("beginTime", beginTime);
                        mv.addObject("endTime", endTime);
                        mv.addObject("showflag", false);

                        return mv;
                    }

                    for (int i = 0; i < monthInterval + 1; i++) {
                        if (i == 0) {
                            all_date += this.addMonth(beginTime, i, "yy年MM月");
                        } else {
                            all_date += "', '" + this.addMonth(beginTime, i, "yy年MM月");
                        }

                        String firstday = this.addMonth(beginTime, i, "yyyy-MM") + "-01";
                        String lastday = this.getLastDayOfMonth(firstday);

                        // 查询条件和参数初始化
                        String ser_cnd = "";
                        Map params = new HashMap();
                        // 查询条件-下单时间
                        if (!CommUtil.null2String(beginTime).equals("")) {
                            ser_cnd += " and unix_timestamp(o.createtime) >= unix_timestamp('"
                                       + firstday + " 00:00:00')";
                        }
                        if (!CommUtil.null2String(endTime).equals("")) {
                            ser_cnd += " and unix_timestamp(o.createtime) <= unix_timestamp('"
                                       + lastday + " 23:59:59')";
                        }

                        // 统计被访问过的本店商品总数
                        String strSql1 = "";
                        strSql1 = "select" + "  COALESCE(sum(sc.count),0) as all_cnt_num"
                                  + " from jm_goods g" + " inner join jm_goods_shopcart sc"
                                  + " on g.id = sc.goods_id" + " inner join jm_order o"
                                  + " on sc.of_id = o.id" + " where o.order_status>=20"
                                  + " and o.disabled=0" + " and g.goods_store_id=" + now_store_id
                                  + ser_cnd;
                        List temp_info = gueryService.executeNativeQuery(strSql1, params, -1, -1);
                        String strTemp = temp_info.get(0).toString();
                        if (i == 0) {
                            all_ac += strTemp;
                        } else {
                            all_ac += "," + strTemp;
                        }
                    }

                }

                all_date += "'";
                showflag = true;
                mv.addObject("all_date", all_date);
                mv.addObject("all_ac", all_ac);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 查询条件
        mv.addObject("beginTime", beginTime);
        mv.addObject("endTime", endTime);
        mv.addObject("showflag", showflag);

        return mv;
    }

    @SecurityMapping(title = "商品销量排名", value = "/seller/tongji_xiaoliang_goods.htm*", rtype = "seller", rname = "销量统计", rcode = "tongji_seller", rgroup = "统计")
    @RequestMapping({ "/seller/tongji_xiaoliang_goods.htm" })
    public ModelAndView tongji_xiaoliang_goods(HttpServletRequest request,
                                               HttpServletResponse response, String currentPage,
                                               String beginTime, String endTime) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/tongji_xiaoliang_goods.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);

        // 是否显示统计结果
        boolean showflag = false;
        // 分页参数
        int pages = 0;
        int one_page_num = 10;
        int all_stores_cnt = 0;
        int now_page = 1;
        int begin_index = 0;
        if (currentPage != null && !"".equals(currentPage)) {
            now_page = Integer.parseInt(currentPage);
        }
        begin_index = (now_page - 1) * one_page_num;

        // 只有点击查询按钮后才统计
        if (beginTime != null && endTime != null) {
            // 查询条件trim
            beginTime = beginTime.trim();
            endTime = endTime.trim();

            // 查询条件和参数初始化
            String ser_cnd = "";
            Map params = new HashMap();

            // 查询条件-下单时间
            if (!CommUtil.null2String(beginTime).equals("")) {
                ser_cnd += " and unix_timestamp(o.createtime) >= unix_timestamp('" + beginTime
                           + " 00:00:00')";
            }
            if (!CommUtil.null2String(endTime).equals("")) {
                ser_cnd += " and unix_timestamp(o.createtime) <= unix_timestamp('" + endTime
                           + " 23:59:59')";
            }

            // 当前店铺id
            long now_store_id = SecurityUserHolder.getCurrentUser().getStore().getId();

            // 统计已卖出本店商品总数
            String strSql1 = "";
            strSql1 = "select count(DISTINCT g.id) " + " from jm_goods g"
                      + " inner join jm_goods_shopcart sc" + " on g.id = sc.goods_id"
                      + " inner join jm_order o" + " on sc.of_id = o.id"
                      + " where o.order_status>=20" + " and o.disabled=0"
                      + " and g.goods_store_id=" + now_store_id + ser_cnd;
            List temp_info = gueryService.executeNativeQuery(strSql1, params, -1, -1);
            all_stores_cnt = Integer.parseInt(temp_info.get(0).toString());
            // 计算分页数
            double temp_pages = (double) all_stores_cnt / (double) one_page_num;
            pages = (int) Math.ceil(temp_pages);

            // 分别统计已卖出本店商品
            String strSql = "";
            strSql = "select" + "  g.id," + "  g.goods_name,"
                     + "  COALESCE(sum(sc.count),0) as all_cnt_num" + " from jm_goods g"
                     + " inner join jm_goods_shopcart sc" + " on g.id = sc.goods_id"
                     + " inner join jm_order o" + " on sc.of_id = o.id"
                     + " where o.order_status>=20" + " and o.disabled=0" + " and g.goods_store_id="
                     + now_store_id + ser_cnd + " group by g.id" + " order by all_cnt_num desc";

            ArrayList<Object[]> stores_info = (ArrayList) gueryService.executeNativeQuery(strSql,
                params, begin_index, one_page_num);
            // 商品名
            String goods_name = "'";
            // 总销量
            String all_ac = "";

            int stores_count = stores_info.size();
            for (int i = 0; i < stores_count; i++) {
                Object[] store_info = stores_info.get(i);
                if (i == 0) {
                    goods_name += store_info[1].toString();
                    all_ac += store_info[2].toString();
                } else {
                    goods_name += "', '" + store_info[1].toString();
                    all_ac += ", " + store_info[2].toString();
                }

            }
            goods_name += "'";

            mv.addObject("goods_name", goods_name);
            mv.addObject("all_ac", all_ac);
            mv.addObject("gotoPageFormAtagHTML", CommUtil.showPageFormAtagHtml(now_page, pages));

            showflag = true;
        }

        // 查询条件
        mv.addObject("beginTime", beginTime);
        mv.addObject("endTime", endTime);
        mv.addObject("showflag", showflag);

        return mv;
    }

    @SecurityMapping(title = "购买率统计", value = "/seller/tongji_goumailv.htm*", rtype = "seller", rname = "购买率统计", rcode = "tongji_seller", rgroup = "统计")
    @RequestMapping({ "/seller/tongji_goumailv.htm" })
    public ModelAndView tongji_goumailv(HttpServletRequest request, HttpServletResponse response,
                                        String currentPage, String beginTime, String endTime) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/tongji_goumailv.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);

        // 是否显示统计结果
        boolean showflag = false;

        // 只有点击查询按钮后才统计
        if (beginTime != null && endTime != null) {
            // 查询条件trim
            beginTime = beginTime.trim();
            endTime = endTime.trim();

            String all_date = "'";
            String all_ac = "";
            long dateInterval = this.DateInterval(beginTime, endTime);
            if (dateInterval < 0) {
                mv.addObject("beginTime", beginTime);
                mv.addObject("endTime", endTime);
                mv.addObject("showflag", false);

                return mv;
            }

            // 当前店铺id
            long now_store_id = SecurityUserHolder.getCurrentUser().getStore().getId();
            try {
                // 一个月以内时按日统计
                if (dateInterval <= 30) {
                    for (int i = 0; i < dateInterval + 1; i++) {
                        if (i == 0) {
                            all_date += this.addDate(beginTime, i, "dd");
                        } else {
                            all_date += "', '" + this.addDate(beginTime, i, "dd");
                        }

                        String thisday = this.addDate(beginTime, i, "yyyy-MM-dd");

                        // 查询条件和参数初始化
                        String ser_cnd = "";
                        Map params = new HashMap();
                        // 查询条件-访问时间
                        if (!CommUtil.null2String(beginTime).equals("")) {
                            ser_cnd += " and unix_timestamp(ta.actime) >= unix_timestamp('"
                                       + thisday + " 00:00:00')";
                        }
                        if (!CommUtil.null2String(endTime).equals("")) {
                            ser_cnd += " and unix_timestamp(ta.actime) <= unix_timestamp('"
                                       + thisday + " 23:59:59')";
                        }

                        // 统计被访问过的本店商品总数
                        String strSql1 = "";
                        strSql1 = "select count(ta.tid) " + " from jm_track t"
                                  + " inner join jm_goods g" + " on t.goodsid = g.id"
                                  + " inner join jm_track_actime ta" + " on t.id = ta.tid"
                                  + " where g.goods_store_id=" + now_store_id + ser_cnd;
                        List temp_info1 = gueryService.executeNativeQuery(strSql1, params, -1, -1);
                        float fTemp1 = Float.parseFloat(temp_info1.get(0).toString());

                        // 查询条件和参数初始化
                        ser_cnd = "";
                        params = new HashMap();
                        // 查询条件-下单时间
                        if (!CommUtil.null2String(beginTime).equals("")) {
                            ser_cnd += " and unix_timestamp(o.createtime) >= unix_timestamp('"
                                       + thisday + " 00:00:00')";
                        }
                        if (!CommUtil.null2String(endTime).equals("")) {
                            ser_cnd += " and unix_timestamp(o.createtime) <= unix_timestamp('"
                                       + thisday + " 23:59:59')";
                        }
                        // 统计本店卖出的商品总数
                        String strSql2 = "";
                        strSql2 = "select" + "  COALESCE(sum(sc.count),0) as all_cnt_num"
                                  + " from jm_goods g" + " inner join jm_goods_shopcart sc"
                                  + " on g.id = sc.goods_id" + " inner join jm_order o"
                                  + " on sc.of_id = o.id" + " where o.order_status>=20"
                                  + " and o.disabled=0" + " and g.goods_store_id=" + now_store_id
                                  + ser_cnd;
                        List temp_info2 = gueryService.executeNativeQuery(strSql2, params, -1, -1);
                        float fTemp2 = Float.parseFloat(temp_info2.get(0).toString());

                        double c = 0;
                        if (fTemp1 > 0) {
                            c = (double) (fTemp2 / fTemp1);//这样为保持2位
                        }
                        String strTemp = String.format("%.3f", c);
                        if (i == 0) {
                            all_ac += strTemp;
                        } else {
                            all_ac += "," + strTemp;
                        }

                    }
                }
                // 超过1个月时按月统计
                else {
                    long monthInterval = this.getMonthInterval(beginTime, endTime);
                    if (monthInterval < 0) {
                        mv.addObject("beginTime", beginTime);
                        mv.addObject("endTime", endTime);
                        mv.addObject("showflag", false);

                        return mv;
                    }

                    for (int i = 0; i < monthInterval + 1; i++) {
                        if (i == 0) {
                            all_date += this.addMonth(beginTime, i, "yy年MM月");
                        } else {
                            all_date += "', '" + this.addMonth(beginTime, i, "yy年MM月");
                        }

                        String firstday = this.addMonth(beginTime, i, "yyyy-MM") + "-01";
                        String lastday = this.getLastDayOfMonth(firstday);

                        // 查询条件和参数初始化
                        String ser_cnd = "";
                        Map params = new HashMap();
                        // 查询条件-访问时间
                        if (!CommUtil.null2String(beginTime).equals("")) {
                            ser_cnd += " and unix_timestamp(ta.actime) >= unix_timestamp('"
                                       + firstday + " 00:00:00')";
                        }
                        if (!CommUtil.null2String(endTime).equals("")) {
                            ser_cnd += " and unix_timestamp(ta.actime) <= unix_timestamp('"
                                       + lastday + " 23:59:59')";
                        }

                        // 统计被访问过的本店商品总数
                        String strSql1 = "";
                        strSql1 = "select count(ta.tid) " + " from jm_track t"
                                  + " inner join jm_goods g" + " on t.goodsid = g.id"
                                  + " inner join jm_track_actime ta" + " on t.id = ta.tid"
                                  + " where g.goods_store_id=" + now_store_id + ser_cnd;
                        List temp_info1 = gueryService.executeNativeQuery(strSql1, params, -1, -1);
                        float fTemp1 = Float.parseFloat(temp_info1.get(0).toString());

                        // 查询条件和参数初始化
                        ser_cnd = "";
                        params = new HashMap();
                        // 查询条件-下单时间
                        if (!CommUtil.null2String(beginTime).equals("")) {
                            ser_cnd += " and unix_timestamp(o.createtime) >= unix_timestamp('"
                                       + firstday + " 00:00:00')";
                        }
                        if (!CommUtil.null2String(endTime).equals("")) {
                            ser_cnd += " and unix_timestamp(o.createtime) <= unix_timestamp('"
                                       + lastday + " 23:59:59')";
                        }
                        // 统计本店卖出的商品总数
                        String strSql2 = "";
                        strSql2 = "select" + "  COALESCE(sum(sc.count),0) as all_cnt_num"
                                  + " from jm_goods g" + " inner join jm_goods_shopcart sc"
                                  + " on g.id = sc.goods_id" + " inner join jm_order o"
                                  + " on sc.of_id = o.id" + " where o.order_status>=20"
                                  + " and o.disabled=0" + " and g.goods_store_id=" + now_store_id
                                  + ser_cnd;
                        List temp_info2 = gueryService.executeNativeQuery(strSql2, params, -1, -1);
                        float fTemp2 = Float.parseFloat(temp_info2.get(0).toString());

                        double c = 0;
                        if (fTemp1 > 0) {
                            c = (double) (fTemp2 / fTemp1);//这样为保持2位
                        }
                        String strTemp = String.format("%.3f", c);
                        if (i == 0) {
                            all_ac += strTemp;
                        } else {
                            all_ac += "," + strTemp;
                        }
                    }

                }

                all_date += "'";
                showflag = true;
                mv.addObject("all_date", all_date);
                mv.addObject("all_ac", all_ac);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 查询条件
        mv.addObject("beginTime", beginTime);
        mv.addObject("endTime", endTime);
        mv.addObject("showflag", showflag);

        return mv;
    }

    /**
     * 得到两日期相差几天
     * 
     * @param String
     * @return
     */
    public long DateInterval(String beginDate, String endDate) {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date1 = ft.parse(beginDate);
            Date date2 = ft.parse(endDate);

            long quot = date2.getTime() - date1.getTime();
            quot = quot / 1000 / 60 / 60 / 24;
            return quot;
        } catch (Exception e) {
            return 0;
        }
    }

    // 日期增加天数
    public String addDate(String strDate, int n, String outFormat) throws ParseException {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");

        Date dt = ft.parse(strDate);
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dt);
        //rightNow.add(Calendar.YEAR,-1);//日期减1年
        //rightNow.add(Calendar.MONTH,3);//日期加3个月
        rightNow.add(Calendar.DAY_OF_YEAR, n);//日期加n天
        Date dt1 = rightNow.getTime();

        SimpleDateFormat outft = new SimpleDateFormat(outFormat);
        String reStr = outft.format(dt1);
        return reStr;
    }

    /**
     * 得到两日期相差几个月
     * 
     * @param String
     * @return
     */
    public long getMonthInterval(String startDate, String endDate) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        long monthday;
        try {
            //开始日期
            Date startDate1 = f.parse(startDate);
            Calendar starCal = Calendar.getInstance();
            starCal.setTime(startDate1);

            int sYear = starCal.get(Calendar.YEAR);
            int sMonth = starCal.get(Calendar.MONTH);
            int sDay = starCal.get(Calendar.DATE);

            //结束日期
            Date endDate1 = f.parse(endDate);
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(endDate1);

            int eYear = endCal.get(Calendar.YEAR);
            int eMonth = endCal.get(Calendar.MONTH);
            int eDay = endCal.get(Calendar.DATE);

            monthday = ((eYear - sYear) * 12 + (eMonth - sMonth));

            //if (sDay < eDay) {
            //    monthday = monthday + 1;
            //}
            return monthday;
        } catch (ParseException e) {
            monthday = 0;
        }
        return monthday;
    }

    //增加月
    public String addMonth(String strDate, int n, String outFormat) throws ParseException {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");

        Date dt = ft.parse(strDate);
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dt);
        rightNow.add(Calendar.MONTH, n); //日期加n个月
        Date dt1 = rightNow.getTime();

        SimpleDateFormat outft = new SimpleDateFormat(outFormat);
        String reStr = outft.format(dt1);
        return reStr;
    }

    /** 
     * 返回当月最后一天的日期  yyyy-MM-dd
     * @throws ParseException 
     */
    public String getLastDayOfMonth(String strDate) throws ParseException {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        Date dt = ft.parse(strDate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        //calendar.set(Calendar.DATE, calendar.getMaximum(Calendar.DATE));
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));

        String reStr = ft.format(calendar.getTime());
        return reStr;
    }

}
