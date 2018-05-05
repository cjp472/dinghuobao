package com.javamalls.ctrl.admin.action;

import java.util.ArrayList;
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
import com.javamalls.base.service.IQueryService;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

/**统计管理
 *                       
 * @Filename: TongjiAction.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class TongjiAction {
    @Autowired
    private ISysConfigService  configService;
    @Autowired
    private IUserConfigService userConfigService;
    @Autowired
    private IQueryService      gueryService;

    @SecurityMapping(title = "按店铺统计", value = "/admin/tongji_order_store.htm*", rtype = "admin", rname = "平台订单统计", rcode = "tongji_order", rgroup = "统计结算")
    @RequestMapping({ "/admin/tongji_order_store.htm" })
    public ModelAndView tongji_order_store(HttpServletRequest request,
                                           HttpServletResponse response, String currentPage,
                                           String tongji_type, String beginTime, String endTime,
                                           String search_name) {
        ModelAndView mv = new JModelAndView("admin/blue/tongji_order_store.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);

        // 是否显示统计结果
        boolean showflag = false;
        // 分页参数
        int pages = 0;
        int one_page_num = 5;
        int all_stores_cnt = 0;
        int now_page = 1;
        int begin_index = 0;
        if (currentPage != null && !"".equals(currentPage)) {
            now_page = Integer.parseInt(currentPage);
        }
        begin_index = (now_page - 1) * one_page_num;

        // 只有点击查询按钮后才统计
        if (tongji_type != null && beginTime != null && endTime != null) {
            // 查询条件trim
            if (search_name != null) {
                search_name = search_name.trim();
            }
            beginTime = beginTime.trim();
            endTime = endTime.trim();

            // 查询条件和参数初始化
            String ser_cnd = "";
            Map<String, Object> params = new HashMap<String, Object>();

            // 统计店铺总数
            // 查询条件-店铺名
            if (!"".equals(search_name)) {
                ser_cnd += " and store_name like '%" + search_name + "%' ";
                //params.put("search_name", search_name);
            }
            List temp_info = gueryService.executeNativeQuery(
                "select count(id) from jm_store where disabled=0" + ser_cnd, params, -1, -1);
            all_stores_cnt = Integer.parseInt(temp_info.get(0).toString());
            // 计算分页数
            double temp_pages = (double) all_stores_cnt / (double) one_page_num;
            pages = (int) Math.ceil(temp_pages);

            // 统计订单数量
            ser_cnd = "";
            params = new HashMap<String, Object>();
            String cnd_tj = "";
            // 查询条件-店铺名
            if (!"".equals(search_name)) {
                ser_cnd += " and s.store_name like '%" + search_name + "%' ";
            }
            // 查询条件-下单时间
            if (!CommUtil.null2String(beginTime).equals("")) {
                cnd_tj += " and unix_timestamp(o.createtime) >= unix_timestamp('" + beginTime
                          + " 00:00:00')";
            }
            if (!CommUtil.null2String(endTime).equals("")) {
                cnd_tj += " and unix_timestamp(o.createtime) <= unix_timestamp('" + endTime
                          + " 23:59:59')";
            }

            String strSql = "";
            if ("2".equals(tongji_type)) {
                // 统计金额
                strSql = "select" + "  s.id," + "  s.store_name," + "  COALESCE(sum(case when 1=1 "
                         + cnd_tj + " then o.totalPrice end),0) as all_cnt_num,"
                         + "  COALESCE(sum(case when o.order_status=50 " + cnd_tj
                         + " then o.totalPrice end),0) as cnt_over,"
                         + "  COALESCE(sum(case when o.order_status=0 " + cnd_tj
                         + " then o.totalPrice end ),0) as cnt_cancel,"
                         + "  COALESCE(sum(case when o.order_status<>0 " + cnd_tj
                         + " and o.order_status<>50 then o.totalPrice end ),0) as cnt_other"
                         + " from jm_store s" + " left join jm_order o" + " on s.id = o.store_id"
                         + " where s.disabled=0" + " and (o.disabled=0 or o.disabled is null)"
                         + ser_cnd + " group by s.id" + " order by all_cnt_num desc";
            } else {
                // 统计数量
                strSql = "select" + "  s.id," + "  s.store_name," + "  count(case when 1=1 "
                         + cnd_tj + " then o.id end) as all_cnt_num,"
                         + "  count(case when o.order_status=50 " + cnd_tj
                         + " then o.id end) as cnt_over," + "  count(case when o.order_status=0 "
                         + cnd_tj + " then o.id end ) as cnt_cancel,"
                         + "  count(case when o.order_status<>0 " + cnd_tj
                         + " and o.order_status<>50 then o.id end ) as cnt_other"
                         + " from jm_store s" + " left join jm_order o" + " on s.id = o.store_id"
                         + " where s.disabled=0" + " and (o.disabled=0 or o.disabled is null)"
                         + ser_cnd + " group by s.id" + " order by all_cnt_num desc";
            }
            ArrayList<Object[]> stores_info = (ArrayList) gueryService.executeNativeQuery(strSql,
                params, begin_index, one_page_num);
            // 店铺名
            String stores_name = "'";
            // 所有订单
            String all_order = "";
            // 已完成订单
            String over_order = "";
            // 已取消订单
            String cancel_order = "";
            // 交易进行中订单
            String onding_order = "";
            int stores_count = stores_info.size();
            for (int i = 0; i < stores_count; i++) {
                Object[] store_info = stores_info.get(i);
                if (i == 0) {
                	String str_store="-";
                	if(store_info[1]!=null){
                		str_store=store_info[1].toString();
                	}
                    stores_name += str_store;
                    all_order += store_info[2].toString();
                    over_order += store_info[3].toString();
                    cancel_order += store_info[4].toString();
                    onding_order += store_info[5].toString();
                } else {
                	String str_store="-";
                	if(store_info[1]!=null){
                		str_store=store_info[1].toString();
                	}
                    stores_name += str_store;
                    all_order += ", " + store_info[2].toString();
                    over_order += ", " + store_info[3].toString();
                    cancel_order += ", " + store_info[4].toString();
                    onding_order += ", " + store_info[5].toString();
                }

            }
            stores_name += "'";

            mv.addObject("stores_name", stores_name);
            mv.addObject("all_order", all_order);
            mv.addObject("over_order", over_order);
            mv.addObject("cancel_order", cancel_order);
            mv.addObject("onding_order", onding_order);
            mv.addObject("gotoPageFormAtagHTML", CommUtil.showPageFormAtagHtml(now_page, pages));

            showflag = true;
        }

        // 查询条件
        mv.addObject("tongji_type", tongji_type);
        mv.addObject("beginTime", beginTime);
        mv.addObject("endTime", endTime);
        mv.addObject("search_name", search_name);
        mv.addObject("showflag", showflag);

        return mv;
    }

    @SecurityMapping(title = "按品牌统计", value = "/admin/tongji_order_brand.htm*", rtype = "admin", rname = "平台订单统计", rcode = "tongji_order", rgroup = "统计结算")
    @RequestMapping({ "/admin/tongji_order_brand.htm" })
    public ModelAndView tongji_order_brand(HttpServletRequest request,
                                           HttpServletResponse response, String currentPage,
                                           String tongji_type, String beginTime, String endTime,
                                           String search_name) {
        ModelAndView mv = new JModelAndView("admin/blue/tongji_order_brand.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);

        // 是否显示统计结果
        boolean showflag = false;
        // 分页参数
        int pages = 0;
        int one_page_num = 5;
        int all_stores_cnt = 0;
        int now_page = 1;
        int begin_index = 0;
        if (currentPage != null && !"".equals(currentPage)) {
            now_page = Integer.parseInt(currentPage);
        }
        begin_index = (now_page - 1) * one_page_num;

        // 只有点击查询按钮后才统计
        if (tongji_type != null && beginTime != null && endTime != null) {
            // 查询条件trim
            if (search_name != null) {
                search_name = search_name.trim();
            }
            beginTime = beginTime.trim();
            endTime = endTime.trim();

            // 查询条件和参数初始化
            String ser_cnd = "";
            Map<String, Object> params = new HashMap<String, Object>();

            // 查询条件-品牌名
            if (!"".equals(search_name)) {
                ser_cnd += " and b.name like '%" + search_name + "%' ";
            }
            // 查询条件-下单时间
            if (!CommUtil.null2String(beginTime).equals("")) {
                ser_cnd += " and unix_timestamp(o.createtime) >= unix_timestamp('" + beginTime
                           + " 00:00:00')";
            }
            if (!CommUtil.null2String(endTime).equals("")) {
                ser_cnd += " and unix_timestamp(o.createtime) <= unix_timestamp('" + endTime
                           + " 23:59:59')";
            }

            // 统计品牌总数
            String strSql1 = "";
            strSql1 = "select count(DISTINCT b.id) " + " from jm_goods_brand b"
                      + " inner join jm_goods g" + " on b.id = g.goods_brand_id"
                      + " inner join jm_goods_shopcart sc" + " on g.id = sc.goods_id"
                      + " inner join jm_order o" + " on sc.of_id = o.id" + " where b.disabled=0"
                      + " and (o.disabled=0 or o.disabled is null)" + ser_cnd;
            List temp_info = gueryService.executeNativeQuery(strSql1, params, -1, -1);
            all_stores_cnt = Integer.parseInt(temp_info.get(0).toString());
            // 计算分页数
            double temp_pages = (double) all_stores_cnt / (double) one_page_num;
            pages = (int) Math.ceil(temp_pages);

            // 统计订单数量
            String strSql = "";
            if ("2".equals(tongji_type)) {
                // 统计金额
                strSql = "select"
                         + "  b.id,"
                         + "  b.name,"
                         + "  COALESCE(sum(case when 1=1 then o.totalPrice end),0) as all_cnt_num,"
                         + "  COALESCE(sum(case when o.order_status=50 then o.totalPrice end),0) as cnt_over,"
                         + "  COALESCE(sum(case when o.order_status=0 then o.totalPrice end ),0) as cnt_cancel,"
                         + "  COALESCE(sum(case when o.order_status<>0 and o.order_status<>50 then o.totalPrice end ),0) as cnt_other"
                         + " from jm_goods_brand b" + " inner join jm_goods g"
                         + " on b.id = g.goods_brand_id" + " inner join jm_goods_shopcart sc"
                         + " on g.id = sc.goods_id" + " inner join jm_order o"
                         + " on sc.of_id = o.id" + " where b.disabled=0"
                         + " and (o.disabled=0 or o.disabled is null)" + ser_cnd + " group by b.id"
                         + " order by all_cnt_num desc";
            } else {
                // 统计数量
                strSql = "select"
                         + "  b.id,"
                         + "  b.name,"
                         + "  count(case when 1=1 then o.id end) as all_cnt_num,"
                         + "  count(case when o.order_status=50 then o.id end) as cnt_over,"
                         + "  count(case when o.order_status=0 then o.id end ) as cnt_cancel,"
                         + "  count(case when o.order_status<>0 and o.order_status<>50 then o.id end ) as cnt_other"
                         + " from jm_goods_brand b" + " inner join jm_goods g"
                         + " on b.id = g.goods_brand_id" + " inner join jm_goods_shopcart sc"
                         + " on g.id = sc.goods_id" + " inner join jm_order o"
                         + " on sc.of_id = o.id" + " where b.disabled=0"
                         + " and (o.disabled=0 or o.disabled is null)" + ser_cnd + " group by b.id"
                         + " order by all_cnt_num desc";
            }
            ArrayList<Object[]> stores_info = (ArrayList) gueryService.executeNativeQuery(strSql,
                params, begin_index, one_page_num);
            // 店铺名
            String stores_name = "'";
            // 所有订单
            String all_order = "";
            // 已完成订单
            String over_order = "";
            // 已取消订单
            String cancel_order = "";
            // 交易进行中订单
            String onding_order = "";
            int stores_count = stores_info.size();
            for (int i = 0; i < stores_count; i++) {
                Object[] store_info = stores_info.get(i);
                if (i == 0) {
                    stores_name += store_info[1].toString();
                    all_order += store_info[2].toString();
                    over_order += store_info[3].toString();
                    cancel_order += store_info[4].toString();
                    onding_order += store_info[5].toString();
                } else {
                    stores_name += "', '" + store_info[1].toString();
                    all_order += ", " + store_info[2].toString();
                    over_order += ", " + store_info[3].toString();
                    cancel_order += ", " + store_info[4].toString();
                    onding_order += ", " + store_info[5].toString();
                }

            }
            stores_name += "'";

            mv.addObject("stores_name", stores_name);
            mv.addObject("all_order", all_order);
            mv.addObject("over_order", over_order);
            mv.addObject("cancel_order", cancel_order);
            mv.addObject("onding_order", onding_order);
            mv.addObject("gotoPageFormAtagHTML", CommUtil.showPageFormAtagHtml(now_page, pages));

            showflag = true;
        }

        // 查询条件
        mv.addObject("tongji_type", tongji_type);
        mv.addObject("beginTime", beginTime);
        mv.addObject("endTime", endTime);
        mv.addObject("search_name", search_name);
        mv.addObject("showflag", showflag);

        return mv;
    }

    @SecurityMapping(title = "最受欢迎的商品", value = "/admin/tongji_best_goods.htm*", rtype = "admin", rname = "平台订单统计", rcode = "tongji_order", rgroup = "统计结算")
    @RequestMapping({ "/admin/tongji_best_goods.htm" })
    public ModelAndView tongji_best_goods(HttpServletRequest request, HttpServletResponse response,
                                          String currentPage, String beginTime, String endTime) {
        ModelAndView mv = new JModelAndView("admin/blue/tongji_best_goods.html",
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
            Map<String, Object> params = new HashMap<String, Object>();

            // 查询条件-下单时间
            if (!CommUtil.null2String(beginTime).equals("")) {
                ser_cnd += " and unix_timestamp(o.createtime) >= unix_timestamp('" + beginTime
                           + " 00:00:00')";
            }
            if (!CommUtil.null2String(endTime).equals("")) {
                ser_cnd += " and unix_timestamp(o.createtime) <= unix_timestamp('" + endTime
                           + " 23:59:59')";
            }

            // 统计已售(包含待发货)商品总数
            String strSql1 = "";
            strSql1 = "select count(DISTINCT g.id) " + " from jm_goods g"
                      + " inner join jm_goods_shopcart sc" + " on g.id = sc.goods_id"
                      + " inner join jm_order o" + " on sc.of_id = o.id"
                      + " where o.order_status>=20" + " and o.disabled=0" + ser_cnd;
            List temp_info = gueryService.executeNativeQuery(strSql1, params, -1, -1);
            all_stores_cnt = Integer.parseInt(temp_info.get(0).toString());
            // 计算分页数
            double temp_pages = (double) all_stores_cnt / (double) one_page_num;
            pages = (int) Math.ceil(temp_pages);

            // 分别统计已售(包含待发货)商品数量
            String strSql = "";
            strSql = "select" + "  g.id," + "  g.goods_name,"
                     + "  COALESCE(sum(sc.count),0) as all_cnt_num" + " from jm_goods g"
                     + " inner join jm_goods_shopcart sc" + " on g.id = sc.goods_id"
                     + " inner join jm_order o" + " on sc.of_id = o.id"
                     + " where o.order_status>=20" + " and o.disabled=0" + ser_cnd
                     + " group by g.id" + " order by all_cnt_num desc";

            ArrayList<Object[]> stores_info = (ArrayList) gueryService.executeNativeQuery(strSql,
                params, begin_index, one_page_num);
            // 店铺名
            String goods_name = "'";
            // 已售总数
            String all_order = "";

            int stores_count = stores_info.size();
            for (int i = 0; i < stores_count; i++) {
                Object[] store_info = stores_info.get(i);
                if (i == 0) {
                    goods_name += store_info[1].toString();
                    all_order += store_info[2].toString();
                } else {
                    goods_name += "', '" + store_info[1].toString();
                    all_order += ", " + store_info[2].toString();
                }

            }
            goods_name += "'";

            mv.addObject("goods_name", goods_name);
            mv.addObject("all_order", all_order);
            mv.addObject("gotoPageFormAtagHTML", CommUtil.showPageFormAtagHtml(now_page, pages));

            showflag = true;
        }

        // 查询条件
        mv.addObject("beginTime", beginTime);
        mv.addObject("endTime", endTime);
        mv.addObject("showflag", showflag);

        return mv;
    }

    @SecurityMapping(title = "最受欢迎的店铺", value = "/admin/tongji_best_shop.htm*", rtype = "admin", rname = "平台订单统计", rcode = "tongji_order", rgroup = "统计结算")
    @RequestMapping({ "/admin/tongji_best_shop.htm" })
    public ModelAndView tongji_best_shop(HttpServletRequest request, HttpServletResponse response,
                                         String currentPage, String beginTime, String endTime) {
        ModelAndView mv = new JModelAndView("admin/blue/tongji_best_shop.html",
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
            Map<String, Object> params = new HashMap<String, Object>();

            // 查询条件-下单时间
            if (!CommUtil.null2String(beginTime).equals("")) {
                ser_cnd += " and unix_timestamp(o.createtime) >= unix_timestamp('" + beginTime
                           + " 00:00:00')";
            }
            if (!CommUtil.null2String(endTime).equals("")) {
                ser_cnd += " and unix_timestamp(o.createtime) <= unix_timestamp('" + endTime
                           + " 23:59:59')";
            }

            // 统计卖过东西的店铺总数
            String strSql1 = "";
            strSql1 = "select count(DISTINCT o.store_id) " + " from jm_goods g"
                      + " inner join jm_goods_shopcart sc" + " on g.id = sc.goods_id"
                      + " inner join jm_order o" + " on sc.of_id = o.id"
                      + " where o.order_status>=20" + " and o.disabled=0" + ser_cnd;
            List temp_info = gueryService.executeNativeQuery(strSql1, params, -1, -1);
            all_stores_cnt = Integer.parseInt(temp_info.get(0).toString());
            // 计算分页数
            double temp_pages = (double) all_stores_cnt / (double) one_page_num;
            pages = (int) Math.ceil(temp_pages);

            // 分别统计店铺已售(包含待发货)商品数量
            String strSql = "";
            strSql = "select" + "  o.store_id," + "  s.store_name,"
                     + "  COALESCE(sum(sc.count),0) as all_cnt_num" + " from jm_goods g"
                     + " inner join jm_goods_shopcart sc" + " on g.id = sc.goods_id"
                     + " inner join jm_order o" + " on sc.of_id = o.id" + " inner join jm_store s"
                     + " on o.store_id = s.id" + " where o.order_status>=20" + " and o.disabled=0"
                     + " and s.disabled=0" + ser_cnd + " group by o.store_id"
                     + " order by all_cnt_num desc";

            ArrayList<Object[]> stores_info = (ArrayList) gueryService.executeNativeQuery(strSql,
                params, begin_index, one_page_num);
            // 店铺名
            String store_name = "'";
            // 已售总数
            String all_order = "";

            int stores_count = stores_info.size();
            for (int i = 0; i < stores_count; i++) {
                Object[] store_info = stores_info.get(i);
                if (i == 0) {
                    store_name += store_info[1].toString();
                    all_order += store_info[2].toString();
                } else {
                    store_name += "', '" + store_info[1].toString();
                    all_order += ", " + store_info[2].toString();
                }

            }
            store_name += "'";

            mv.addObject("store_name", store_name);
            mv.addObject("all_order", all_order);
            mv.addObject("gotoPageFormAtagHTML", CommUtil.showPageFormAtagHtml(now_page, pages));

            showflag = true;
        }

        // 查询条件
        mv.addObject("beginTime", beginTime);
        mv.addObject("endTime", endTime);
        mv.addObject("showflag", showflag);

        return mv;
    }
}
