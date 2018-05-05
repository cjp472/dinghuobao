package com.javamalls.ctrl.admin.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.POIUtil;
import com.javamalls.base.tools.TimeUtil;
import com.javamalls.platform.domain.OrderForm;
import com.javamalls.platform.domain.PredepositLog;
import com.javamalls.platform.domain.SettleAccunts;
import com.javamalls.platform.domain.SettleLog;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.query.SettleLogQueryObject;
import com.javamalls.platform.service.IOrderFormService;
import com.javamalls.platform.service.IPredepositLogService;
import com.javamalls.platform.service.ISettleAccountsService;
import com.javamalls.platform.service.ISettleLogService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;

/**
 * 结算设置settlePage.htm
 * 
 * @Filename: SettleAccountsAction.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 * 
 */
@Controller
public class SettleLogAction {
    @Autowired
    private ISysConfigService      configService;
    @Autowired
    private IUserConfigService     userConfigService;
    @Autowired
    private ISettleLogService      settleLogService;
    @Autowired
    private ISettleAccountsService settleAccountsService;
    @Autowired
    private IOrderFormService      orderFormService;
    @Autowired
    private IUserService           userService;
    @Autowired
    private IPredepositLogService  predepositLogService;

    private Logger                 log = Logger.getLogger(this.getClass());

    /**
     * 导出Excel
     */
    @RequestMapping({ "/admin/exportExcel.htm" })
    public synchronized void exportExcel(HttpServletRequest request, HttpServletResponse response,
                                         String currentPage, String orderBy, String orderType,
                                         String type, String settle_code, String start_count,
                                         String end_count, String settle_date, String end_date) {

        ModelAndView mv = new JModelAndView("admin/blue/settle_log_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        IPageList pList = querySettleLogList(currentPage, orderBy, orderType, type, settle_code,
            start_count, end_count, settle_date, end_date, mv, 10000, null);
        List<SettleLog> list = pList.getResult();

        String templatePath = POIUtil
            .getFilePath("com/javamalls/ctrl/admin/template/jm_settle_log.xls");
        File excel = new File(templatePath);
        POIFSFileSystem poifs = null;
        OutputStream out = null;
        HSSFSheet sheet = null;
        try {
            log.debug("导出的文件模板：" + templatePath);
            poifs = new POIFSFileSystem(new FileInputStream(excel));
            HSSFWorkbook workbook = new HSSFWorkbook(poifs);
            // 向workbook中填充记录
            sheet = workbook.getSheetAt(0);
            workbook.setSheetName(workbook.getSheetIndex(sheet), "结算导出数据");
            fillSheet(workbook, list);

            // 导出,设置response的contentType,header
            response.setContentType("application/x-download");
            String fileName = (list != null && list.size() > 0 ? "【"
                                                                 + CommUtil.substring(list.get(0)
                                                                     .getCode(), 15) + "】" + "等"
                                                                 + list.size() + "个结算单" : "没有记录")
                              + ".xls";
            response.setHeader("Content-disposition",
                "attachment;filename=" + new String(fileName.getBytes(), "ISO8859-1"));
            out = response.getOutputStream();
            workbook.write(out);

            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void fillSheet(HSSFWorkbook workbook, List<SettleLog> list) {
        POIUtil.setTitle("结算单管理", workbook, 0);
        writeExcel(workbook, list);
    }

    /**
     * 向excel中写入结帐单
     * 
     * @param reprot
     * @param work
     */
    private void writeExcel(HSSFWorkbook work, List<SettleLog> list) {
        HSSFSheet sheet = work.getSheetAt(0);
        this.fillData(sheet, list);
    }

    private void fillData(HSSFSheet sheet, List<SettleLog> list) {

        Map<String, Object> style = new HashMap<String, Object>();
        style.put("fontName", "微软雅黑");
        style.put("fontHeight", (short) 200);
        style.put("alignment", HSSFCellStyle.ALIGN_CENTER);
        style.put("verticalAlignment", HSSFCellStyle.VERTICAL_CENTER);

        if (list != null && list.size() > 0) {
            int rownum = 2;// 从第三行开始
            for (SettleLog sg : list) {
                HSSFCell cell = null;
                HSSFRow row = sheet.createRow(rownum);
                int columnNum = 0;

                // 序号
                cell = row.createCell(columnNum++);
                cell.setCellValue(new HSSFRichTextString(rownum - 1 + ""));
                POIUtil.setCellStyle(cell, sheet.getWorkbook(), style);

                // 结算流水号
                cell = row.createCell(columnNum++);
                cell.setCellValue(new HSSFRichTextString(sg.getCode()));

                // 商家名称
                cell = row.createCell(columnNum++);
                cell.setCellValue(new HSSFRichTextString(sg.getOrder().getStore().getStore_name()));

                // 销售金额
                cell = row.createCell(columnNum++);
                cell.setCellValue(new HSSFRichTextString(sg.getSale_account().toString()));

                // 销售佣金
                cell = row.createCell(columnNum++);
                cell.setCellValue(new HSSFRichTextString(sg.getSale_yongjin().toString()));

                // 结算金额
                cell = row.createCell(columnNum++);
                cell.setCellValue(new HSSFRichTextString(sg.getSettle_account().toString()));

                // 结帐单状态 1 未结 2 已结
                cell = row.createCell(columnNum++);
                cell.setCellValue(new HSSFRichTextString(
                    (sg.getStatus() == 1 || sg.getStatus() == null) ? "未结" : "已结"));
                POIUtil.setCellStyle(cell, sheet.getWorkbook(), style);

                // 入账时间
                String incomeTime = "";
                if (sg != null && sg.getIncome_time() != null)
                    incomeTime = new SimpleDateFormat("yyyy-MM-dd E").format(sg.getIncome_time());
                cell = row.createCell(columnNum++);
                cell.setCellValue(new HSSFRichTextString(incomeTime));

                // 操作人
                cell = row.createCell(columnNum++);
                cell.setCellValue(new HSSFRichTextString(sg.getUser().getUsername().toString()));

                // 商家名称
                cell = row.createCell(columnNum++);
                cell.setCellValue(new HSSFRichTextString(sg.getStore_user_name()));

                rownum++;
            }
        }
    }

    /**
     * 结帐单列表
     * 
     * @param request
     * @param response
     * @param currentPage
     * @param orderBy
     * @param orderType
     * @param ig_goods_name
     * @param ig_show
     * @return
     */
    @SecurityMapping(title = "结帐单列表", value = "/admin/settle_log_list.htm*", rtype = "admin", rname = "结算管理", rcode = "jiesuan_mn", rgroup = "统计结算")
    @RequestMapping({ "/admin/settle_log_list.htm" })
    public ModelAndView settle_log_list(HttpServletRequest request, HttpServletResponse response,
                                        String currentPage, String orderBy, String orderType,
                                        String type, String settle_code, String start_count,
                                        String end_count, String settle_date, String end_date,
                                        String storeId) {
        String mvurl = "admin/blue/settle_log_list.html";
        if (storeId != null && !"".equals(storeId)) {
            mvurl = "user/default/usercenter/mybill.html";
        }
        ModelAndView mv = new JModelAndView("admin/blue/settle_log_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if (storeId != null && !"".equals(storeId)) {
            mv.addObject("storeId", Long.valueOf(storeId));
        }

        // 平台销售总额、 平台总销售佣金、应向商家结算金额查询不加状态
        List<Map<String, Object>> logs = this.settleLogService
            .query("select new map(sum(obj.sale_account) as sale_account,"
                   + "sum(obj.sale_yongjin) as sale_yongjin,"
                   + "sum(obj.settle_account) as settle_account) "
                   + " from SettleLog obj where obj.disabled = 0 ");

        if (logs != null && logs.size() > 0) {
            Map<String, Object> m = logs.get(0);
            String sale_account = CommUtil.null2String(m.get("sale_account"));
            String sale_yongjin = CommUtil.null2String(m.get("sale_yongjin"));
            String settle_account = CommUtil.null2String(m.get("settle_account"));
            mv.addObject("sale_account", sale_account);
            mv.addObject("sale_yongjin", sale_yongjin);
            mv.addObject("settle_account", settle_account);
        }

        // 实际向商家结算金额状态等于4（已结算）
        List<Map<String, Object>> ass = this.settleLogService
            .query("select new map(sum(obj.settle_account) as sjzf) "
                   + " from SettleLog obj where obj.status = 4 and obj.disabled = 0 ");

        if (ass != null && ass.size() > 0) {
            Map<String, Object> m = ass.get(0);
            String sa = CommUtil.null2String(m.get("sjzf")).equals("") ? "0" : CommUtil
                .null2String(m.get("sjzf"));
            mv.addObject("sjzf", sa);
        }

        String msg = "今天不是本月结算日，不能结算！";
        if (type != null && !"".equals(type)) {
            int month = Integer.valueOf(TimeUtil.getMonth());
            SettleAccunts settleAccunts = null;
            List<SettleAccunts> settles = settleAccountsService.query(
                "select obj from SettleAccunts obj where obj.month = " + month, null, -1, -1);
            if (settles != null && settles.size() > 0) {
                settleAccunts = settles.get(0);
                String day = TimeUtil.getDay();
                mv.addObject("settleDate",
                    CommUtil.substringStartEnd(settleAccunts.getSettle_date()));
                boolean flag = settleAccunts.getSettle_date().contains("," + day + ",");
                if (!flag) {// 非结算日
                    mv.addObject("msg", msg);
                    // settle_date = TimeUtil.getYearMonth();
                    IPageList pList = querySettleLogList(currentPage, orderBy, orderType, type,
                        settle_code, start_count, end_count, settle_date, end_date, mv, null,
                        storeId);
                    CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
                } else {// 是结算日
                    IPageList pList = querySettleLogList(currentPage, orderBy, orderType, type,
                        settle_code, start_count, end_count, settle_date, end_date, mv, null,
                        storeId);
                    CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
                }
            }
        } else {
            mv.addObject("msg", msg);
        }

        mv.addObject("type", type);
        mv.addObject("storeId", storeId);
        String curDay = TimeUtil.getChineseToDay();
        mv.addObject("curDay", curDay);
        return mv;
    }

    private IPageList querySettleLogList(String currentPage, String orderBy, String orderType,
                                         String type, String settle_code, String start_count,
                                         String end_count, String settle_date, String end_date,
                                         ModelAndView mv, Integer pageSize, String storeId) {

        String queryTime = "obj.createtime";
        if (Integer.valueOf(type) > 2)
            queryTime = "obj.income_time";
        SettleLogQueryObject qo = new SettleLogQueryObject(currentPage, mv, orderBy, orderType);
        if (pageSize != null)
            qo.setPageSize(pageSize);
        qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");
        qo.addQuery("obj.status", new SysMap("status", Integer.valueOf(type)), "=");
        if (settle_code != null && !"".equals(settle_code)) {
            qo.addQuery("obj.code", new SysMap("code", settle_code), "=");
            mv.addObject("settle_code", settle_code);
        }
        if (storeId != null && !"".equals(storeId)) {
            qo.addQuery("obj.order.store.id", new SysMap("storeId", Long.valueOf(storeId)), "=");
        }

        if (start_count != null && !"".equals(start_count)) {
            qo.addQuery("obj.settle_account",
                new SysMap("start_account", BigDecimal.valueOf(CommUtil.null2Double(start_count))),
                ">=");
            mv.addObject("start_count", start_count);
        }
        if (end_count != null && !"".equals(end_count)) {
            qo.addQuery("obj.settle_account",
                new SysMap("end_account", BigDecimal.valueOf(CommUtil.null2Double(end_count))),
                "<=");
            mv.addObject("end_count", end_count);
        }
        if (!CommUtil.null2String(settle_date).equals("")) {
            qo.addQuery(queryTime, new SysMap("income_time", CommUtil.formatDate(settle_date)),
                ">=");
            mv.addObject("settle_date", settle_date);
        }
        if (!CommUtil.null2String(end_date).equals("")) {
            qo.addQuery(queryTime, new SysMap("end_date", CommUtil.formatDate(end_date)), "<=");
            mv.addObject("end_date", end_date);
        }
        if (CommUtil.null2String(end_date).equals("")
            && CommUtil.null2String(settle_date).equals("")) {
            qo.addQuery(queryTime,
                new SysMap("income_time", CommUtil.formatDate(TimeUtil.getYearMonth())), ">=");
        }
        SettleLog a = new SettleLog();
        qo.setOrderBy(orderBy);
        qo.setOrderType(orderType);
        IPageList pList = this.settleLogService.list(qo);
        return pList;
    }

    /**
     * 结帐单列表
     * 
     * @param request
     * @param response
     * @param settleLog
     * @return
     */
    @RequestMapping({ "/admin/settlePage.htm" })
    public ModelAndView settlePage(HttpServletRequest request, HttpServletResponse response,
                                   String id, String type) {
        ModelAndView mv = new JModelAndView("admin/blue/settle.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        try {
            SettleLog settleLog = settleLogService.getObjById(Long.valueOf(id));
            mv.addObject("settleLog", settleLog);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        mv.addObject("type", type);
        return mv;
    }

    /**
     * 结帐单列表
     * 
     * @param request
     * @param response
     * @param settleLog
     * @return
     */
    @RequestMapping({ "/admin/settlePageinfo.htm" })
    public ModelAndView settlePageInfo(HttpServletRequest request, HttpServletResponse response,
                                       String id, String type) {
        ModelAndView mv = new JModelAndView("admin/blue/settleinfo.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        try {
            SettleLog settleLog = settleLogService.getObjById(Long.valueOf(id));
            mv.addObject("settleLog", settleLog);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        mv.addObject("type", type);
        return mv;
    }

    /**
     * 删除结算单
     * 
     * @param request
     * @param response
     * @param currentPage
     * @param mulitId
     * @return
     */
    @RequestMapping({ "/admin/settle_log_del.htm" })
    public String settle_del(HttpServletRequest request, HttpServletResponse response,
                             String currentPage, String mulitId, String type) {
        if (mulitId != null && !"".equals(mulitId)) {
            String[] ids = mulitId.split(",");
            // List<Long> list = new ArrayList<Long>();
            for (int i = 0; i < ids.length; i++) {
                SettleLog settleLog = this.settleLogService.getObjById(Long.valueOf(ids[i]));
                settleLog.setDisabled(true);
                this.settleLogService.update(settleLog);
                // list.add(Long.valueOf(ids[i]));
            }
            // this.settleLogService.batchDelete(list);
        }
        return "redirect:/admin/settle_log_list.htm?type=" + type + "&currentPage=" + currentPage;
    }

    /**
     * 结算
     * 
     * @param request
     * @param response
     * @param currentPage
     * @param id
     * @return
     */
    @RequestMapping({ "/admin/settle.htm" })
    public String settle(HttpServletRequest request, HttpServletResponse response,
                         String currentPage, String id, String type, String sk_user,
                         String hk_user, String hk_type, String remark) {

        SettleLog settleLog = this.settleLogService.getObjById(Long.valueOf(id));
        settleLog.setHk_type(hk_type);
        settleLog.setSk_user(sk_user);
        settleLog.setHk_user(hk_user);
        if (null != remark && !"".equals(remark)) {
            settleLog.setRemark(remark);
        }
        OrderForm orderForm = orderFormService.getObjById(settleLog.getOrder().getId());

        User seller = this.userService.getObjById(orderForm.getStore().getUser().getId());
        //结算到可用余额里和结算到冻结余额里只能用一个
        seller.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(seller.getAvailableBalance(),
            settleLog.getSale_account())));
        /*//结算到冻结余额里
        user.setFreezeBlance(BigDecimal.valueOf(CommUtil.subtract(user.getFreezeBlance(),
            settleLog.getSale_account())));*/
        PredepositLog log = new PredepositLog();
        log.setCreatetime(new Date());
        log.setPd_log_user(seller);
        log.setPd_op_type("增加");
        log.setPd_log_amount(orderForm.getTotalPrice());
        log.setPd_log_info("平台结算增加预存款,订单号" + orderForm.getOrder_id());
        log.setPd_type("可用预存款");

        settleLog.setStatus(4);
        // settleLog.setUser(user);
        settleLog.setIncome_time(new Date());
        this.settleLogService.update(settleLog);
        this.predepositLogService.save(log);

        orderForm.setOrder_status(270);
        this.orderFormService.update(orderForm);
        this.userService.update(seller);

        return "redirect:/admin/settle_log_list.htm?type=" + type + "&currentPage=" + currentPage;
    }
}
