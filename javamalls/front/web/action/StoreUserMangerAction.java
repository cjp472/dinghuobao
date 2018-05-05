package com.javamalls.front.web.action;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.constant.Constant;
import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.service.IQueryService;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.POIUtil;
import com.javamalls.payment.chinabank.h5.util.JsonUtil;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.UserStoreRelation;
import com.javamalls.platform.domain.query.UserStoreRelationQueryObject;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserStoreRelationService;
import com.javamalls.platform.vo.UserStoreRelationVo;
import com.utils.SendReqAsync;

/**会员管理
 * 
 *                       
 * @Filename: StoreUserMangerAction.java
 * @Version: 2.7.0
 * @Author: 桑森林
 * @Email: sangslmail@163.com
 *
 */
@Controller
public class StoreUserMangerAction {
    private static final Logger       logger = Logger.getLogger(StoreUserMangerAction.class);
    @Autowired
    private ISysConfigService         configService;
    @Autowired
    private IUserConfigService        userConfigService;
    @Autowired
    private IUserStoreRelationService userStoreRelationService;
    @Autowired
    private SendReqAsync              sendReqAsync;
    @Autowired
    private IStoreService             storeService;
    @Autowired
    private IQueryService             queryService;

    /**
     * 会员列表
     * @throws Exception 
     */
    @SecurityMapping(title = "会员列表", value = "/seller/storeUserNew.htm*", rtype = "seller", rname = "会员管理", rcode = "store_user_seller", rgroup = "会员管理")
    @RequestMapping({ "/seller/storeUserNew.htm" })
    public ModelAndView storeUserNew(HttpServletRequest request, HttpServletResponse response,
                                     String currentPage, String orderBy, String orderType,
                                     String type, String quserName, Integer status)
                                                                                   throws Exception {
        ModelAndView mv = new JModelAndView("seller/store_member_list_new.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
            JModelAndView.SHOP_PATH, request, response);
        if (SecurityUserHolder.getCurrentUser() == null) {
            mv = new JModelAndView("login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            return mv;
        }
        if (status == null && status != 1 && status != 2) {
            throw new Exception("会员列表传参出现异常");
        }

        String beginTime = request.getParameter("beginTime");
        String endTime = request.getParameter("endTime");

        String cus_ser_code = request.getParameter("cus_ser_code");

        //判断是不是要查看审核列表
        if (status == 1) {
            mv = new JModelAndView("seller/auditUserList.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), JModelAndView.SHOP_PATH, request, response);
        }

        if (orderBy == null || orderBy.equals("")) {
            orderBy = "id";
        }
        UserStoreRelationQueryObject qo = new UserStoreRelationQueryObject(currentPage, mv,
            orderBy, orderType);
        qo.addQuery("obj.status", new SysMap("status", 0), "!=");
        qo.addQuery(
            "obj.store.id",
            new SysMap("store_id", CommUtil.null2Long(SecurityUserHolder.getCurrentUser()
                .getStore().getId()
                                                      + "")), "=");
        if (quserName != null && !"".equals(quserName)) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("client_name", "%" + quserName + "%");
            map.put("mobile", "%" + quserName + "%");
            qo.addQuery(
                " (obj.user.client_name like :client_name or obj.user.userName like :mobile  ) ",
                map);
        }

        if (!CommUtil.null2String(beginTime).equals("")) {
            qo.addQuery("obj.user.createtime",
                new SysMap("beginTime", CommUtil.formatMaxDate(beginTime + " 00:00:00")), ">=");
        }
        if (!CommUtil.null2String(endTime).equals("")) {
            qo.addQuery("obj.user.createtime",
                new SysMap("endTime", CommUtil.formatMaxDate(endTime + " 23:59:59")), "<=");
        }

        if (CommUtil.isNotNull(cus_ser_code)) {
            qo.addQuery("obj.user.cus_ser_code", new SysMap("cus_ser_code", cus_ser_code), "=");
        }

        mv.addObject("quserName", quserName);
        mv.addObject("cus_ser_code", cus_ser_code);
        mv.addObject("beginTime", beginTime);
        mv.addObject("endTime", endTime);

        mv.addObject("status", status);
        IPageList pList = null;
        try {
            pList = this.userStoreRelationService.list(qo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        return mv;
    }

    /**
     * 审核会员
     */
    @RequestMapping("/seller/AuditstoreUserNew.htm")
    @ResponseBody
    public void AuditstoreUserNew(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        String status = request.getParameter("status");
        logger.info("正在审核会员");
        int ret = 0;//未找到数据
        UserStoreRelation userStoreRelation = userStoreRelationService.getObjById(CommUtil
            .null2Long(id));
        if (userStoreRelation != null && (status.equals("2") || status.equals("3"))) {
            if (userStoreRelation.getStatus() == 1) {
                userStoreRelation.setStatus(Integer.parseInt(status));
                Boolean flag = userStoreRelationService.update(userStoreRelation);
                if (flag) {
                    logger.info("审核成功");
                    ret = 1;

                    //调用接口
                    UserStoreRelationVo vo = new UserStoreRelationVo();
                    vo.setStatus(userStoreRelation.getStatus());
                    vo.setUserId(userStoreRelation.getUser().getId());
                    vo.setStoreId(userStoreRelation.getStore().getId());
                    String write2JsonStr = JsonUtil.write2JsonStr(vo);
                    sendReqAsync.sendMessageUtil(Constant.STORE_USER_RELATION_URL_ADD,
                        write2JsonStr, "新增供采关系");

                } else {
                    ret = 2;
                    logger.info("审核更新失败");
                }
            } else {
                ret = 3;
                logger.info("该状态无法审核");
            }

        } else {
            logger.info("审核会员传参出现异常");
        }

        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //会员信息导出
    @RequestMapping("/seller/member_exportExcel.htm")
    public synchronized void member_exportExcel(ModelAndView mv, HttpServletRequest request,
                                                HttpServletResponse response, String currentPage,
                                                String orderBy, String orderType, Integer pageSize,
                                                String type, String quserName, Integer status)
                                                                                              throws Exception {
        logger.info("########## 会员信息导出开始【Start】：时间：" + CommUtil.formatDate(new Date()));
        String format = "yyyy-MM-dd HH:mm:ss";

        String beginTime = request.getParameter("beginTime");
        String endTime = request.getParameter("endTime");

        //获取店铺ID
        User loginUser = SecurityUserHolder.getCurrentUser();
        Long storeId = loginUser.getStore().getId();

        if (CommUtil.isNullOrEmpty(storeId)) {
            throw new Exception("店铺ID为空");
        }

        String cus_ser_code = request.getParameter("cus_ser_code");

        Long store_id = SecurityUserHolder.getCurrentUser().getStore().getId();

        List<Object[]> list = null;
        OutputStream out = null;
        SXSSFWorkbook workbook = null;
        SXSSFSheet sheet = null; //工作表对象  
        String userAgent = request.getHeader("USER-AGENT");
        try {
            workbook = new SXSSFWorkbook(100);
            sheet = (SXSSFSheet) workbook.createSheet("会员数据");
            // 向workbook中填充记录
            String titleName = "会员信息";
            String[] titleNames = { "序号", "登录名", "手机号", "真实姓名", "客户生日", "客服代码", "注册时间" };

            int all_cnt = getCount(cus_ser_code, quserName, store_id, beginTime, endTime);

            if (all_cnt == 0) {
                fillSheet(sheet, titleName, titleNames, null, 2);
            } else {
                //计算分页数
                logger.info("总大小：" + all_cnt);

                //wf 20170811  ，以下sql 使用分页 limit ,查询时间是不使用 limit的及时倍。故去掉limit；
                pageSize = 50000;
                double temp_pages = (double) all_cnt / (double) pageSize;
                int pages = (int) Math.ceil(temp_pages);
                int beginIndex = 0;
                int rowNum = 0;
                //for (int i = 0; i < pages; i++) {
                beginIndex = 0;
                //log.info("beginIndex==>" + beginIndex);
                //log.info("第" + (i + 1) + "次开始====>" + CommUtil.getCurrentTime());
                list = orderGoodsList(cus_ser_code, quserName, storeId, beginTime, endTime);
                rowNum = 2;
                //log.info("rowNum==>" + rowNum);
                fillSheet(sheet, titleName, titleNames, list, rowNum);
                //log.info("第" + (i + 1) + "次结束====>" + CommUtil.getCurrentTime());
                list.clear();
            }
            list = null;
            // }

            // 导出,设置response的contentType, header
            response.setContentType("application/vnd.ms-excel");
            Date date = new Date(System.currentTimeMillis());
            String fileName = "会员信息" + date.getTime() + ".xlsx";

            if (userAgent.contains("MSIE") || userAgent.contains("Trident")
                || userAgent.contains("Edge")) {
                fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
            } else {
                //非IE浏览器的处理：
                fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            }
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);
            logger.info("fillsheet结束！");
            out = response.getOutputStream();
            logger.info("response.getOutputStream()结束！");
            workbook.write(out);
            logger.info("workbook.write(out)结束！");
            out.flush();
            logger.info("out.flush()结束！");
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

        logger.info("########## 会员信息导出结束【End】：时间：" + CommUtil.formatDate(new Date()));
    }

    private int getCount(String cus_ser_code, String quserName, Long storeId, String beginTime,
                         String endTime) {
        if (SecurityUserHolder.getCurrentUser() == null) {
            return 0;
        }
        String sql = getCountSQL()
                     + getCommonSQL(cus_ser_code, quserName, storeId, beginTime, endTime);
        List list = queryService.executeNativeQuery(sql, null, -1, -1);
        if (list != null && list.size() > 0) {
            return Integer.parseInt(list.get(0).toString().trim());
        }
        return 0;
    }

    private List<Object[]> orderGoodsList(String cus_ser_code, String quserName, Long storeId,
                                          String beginTime, String endTime) {
        if (SecurityUserHolder.getCurrentUser() == null) {
            return null;
        }
        String sql = getDetailSQL()
                     + getCommonSQL(cus_ser_code, quserName, storeId, beginTime, endTime);
        List<Object[]> list = (ArrayList) queryService.executeNativeQuery(sql, null, -1, -1);

        return list;
    }

    private String getCountSQL() {
        return "select count(u.id)";
    }

    private String getDetailSQL() {
        StringBuffer sBuffer = new StringBuffer();
        sBuffer
            .append("select distinct u.userName, u.mobile,u.trueName,u.birthday,u.cus_ser_code,u.createtime ");

        return sBuffer.toString();
    }

    private String getCommonSQL(String cus_ser_code, String quserName, Long storeId,
                                String beginTime, String endTime) {
        StringBuffer sBuffer = new StringBuffer();
        sBuffer
            .append(" from jm_user_store_relation t ,jm_user u where u.id = t.user_id and t.`status` = 2 ");

        //只查询本店铺的信息
        sBuffer.append(" and t.store_id  = ").append(storeId);

        if (CommUtil.isNotNull(quserName)) {
            sBuffer.append(" and ( u.client_name like '%" + quserName + "%' or u.userName like '%"
                           + quserName + "%'  )");
        }

        if (!CommUtil.null2String(beginTime).equals("")) {
            sBuffer.append(" and u.createtime >= str_to_date('")
                .append(CommUtil.null2String(beginTime)).append("', '%Y-%m-%d')");
        }
        if (!CommUtil.null2String(endTime).equals("")) {
            endTime += " 23:59:59";
            sBuffer.append(" and u.createtime <= str_to_date('")
                .append(CommUtil.null2String(endTime)).append("', '%Y-%m-%d %H:%i:%s')");
        }

        if (CommUtil.isNotNull(cus_ser_code)) {
            sBuffer.append(" and  u.cus_ser_code = '" + cus_ser_code + "'");
        }

        sBuffer.append(" order by t.createtime desc");
        return sBuffer.toString();
    }

    private void fillSheet(SXSSFSheet sheet, String titleName, String[] titleNames,
                           List<Object[]> list, int rowNum) {
        if (2 == rowNum) {
            POIUtil.setTitle(sheet, titleName, titleNames);
        }
        this.fillData(sheet, list, rowNum);
    }

    private void fillData(SXSSFSheet sheet, List<Object[]> list, int rowNum) {

        if (list != null && list.size() > 0 && rowNum >= 2) {

            for (Object[] objs : list) {
                Cell cell = null;
                Row row = sheet.createRow(rowNum);
                int columnNum = 0;
                // 序号
                cell = row.createCell(columnNum++);
                cell.setCellValue(new XSSFRichTextString(rowNum - 1 + ""));
                for (int index = 0; index < objs.length; index++) {
                    cell = row.createCell(columnNum++);
                    cell.setCellValue(new XSSFRichTextString(CommUtil.null2String(objs[index])));

                }
                if (rowNum % 100 == 0) {
                    try {
                        sheet.flushRows();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                rowNum++;
            }
        }
    }

}
