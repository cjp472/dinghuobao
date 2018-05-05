package com.javamalls.ctrl.seller.action;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.service.IQueryService;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.POIUtil;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.enums.OrderStatusEnum;
import com.javamalls.platform.service.IGoodsCartService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;

/**
 * 前台统计--订单商品列表
 * 导出excel
 *                       
 * @Filename: OrderGoodsTongJiAction.java
 * @Version: 1.0
 * @Author: 李明
 * @Email: limn_xmj@163.com
 *
 */
@Controller
public class OrderGoodsTongJiAction {

    protected static final Log log = LogFactory.getLog(OrderGoodsTongJiAction.class);
    @Autowired
    private ISysConfigService  configService;
    @Autowired
    private IUserConfigService userConfigService;
    @Autowired
    private IGoodsCartService  goodsCartService;
    @Autowired
    private IUserService       userService;
    @Autowired
    private IQueryService      gueryService;

    @SecurityMapping(title = "订单商品导出", value = "/seller/order_goods_exportExcel.htm*", rtype = "youran", rname = "订单商品导出", rcode = "tongji_order_goods_youran", rgroup = "统计（自营）")
    @RequestMapping("/seller/order_goods_exportExcel.htm")
    public synchronized void order_goods_exportExcel(ModelAndView mv, HttpServletRequest request,
                                                     HttpServletResponse response,
                                                     String currentPage, String orderBy,
                                                     String orderType, String order_status,
                                                     String payment, String order_goods_type,
                                                     String beginTime, String endTime,
                                                     String buyer_userName, String mcmc_name,
                                                     String businessMan_name, String order_id,
                                                     String goods_name, Integer pageSize)
                                                                                         throws Exception {
        log.info("########## 订单商品导出开始【Start】：时间：" + CommUtil.formatDate(new Date()));
        String format = "yyyy-MM-dd HH:mm:ss";

        //获取店铺ID
        User loginUser = SecurityUserHolder.getCurrentUser();
        Long storeId = loginUser.getStore().getId();

        if (CommUtil.isNullOrEmpty(storeId)) {
            throw new Exception("店铺ID为空");
        }

        /*if (!CommUtil.isNotNull(beginTime)) {
            beginTime = CommUtil.formatDate(format, CommUtil.getThisMonth().getTime());
        }

        if (!CommUtil.isNotNull(endTime)) {
            endTime = CommUtil.formatDate(format, new Date());
        }
        */
        if (!CommUtil.null2String(order_status).equals("")) {
            order_status = String.valueOf(OrderStatusEnum.getEnumIndexByName(order_status));
        }

        List<Object[]> list = null;
        OutputStream out = null;
        SXSSFWorkbook workbook = null;
        SXSSFSheet sheet = null; //工作表对象  
        String userAgent = request.getHeader("USER-AGENT");
        try {
            workbook = new SXSSFWorkbook(100);
            sheet = (SXSSFSheet) workbook.createSheet("订单商品导出数据");
            // 向workbook中填充记录
            String titleName = "订单商品";
            String[] titleNames = { "序号", "订单编号", "商品名称", "商品规格", "数量", "供应商名称", "条码", "自编码",
                    "销售价", "进货价", "会员登录账号", "会员属性", "收货人", "收货人联系电话", "收货地址", "客服代码", "支付方式",
                    "订单状态", "支付时间", "订单总价", "下单时间" };

            //会员属性，定义为两种属性，一个普通用户会员，一个是分销商会员。（备注：普通用户会员指的是只能在商城买东西权限，没有卖东西的权限。分销商会员指的是可以在商城买卖的权限）
            int all_cnt = getCount(order_status, payment, order_goods_type, beginTime, endTime,
                storeId, buyer_userName, mcmc_name, businessMan_name, order_id, goods_name);

            if (all_cnt == 0) {
                fillSheet(sheet, titleName, titleNames, null, 2);
            } else {
                //计算分页数
                log.info("总大小：" + all_cnt);

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
                list = orderGoodsList(order_status, payment, order_goods_type, beginTime, endTime,
                    storeId, buyer_userName, mcmc_name, businessMan_name, order_id, goods_name,
                    beginIndex, pageSize);
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
            String fileName = "订单商品" + date.getTime() + ".xlsx";

            if (userAgent.contains("MSIE") || userAgent.contains("Trident")
                || userAgent.contains("Edge")) {
                fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
            } else {
                //非IE浏览器的处理：
                fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            }
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);
            log.info("fillsheet结束！");
            out = response.getOutputStream();
            log.info("response.getOutputStream()结束！");
            workbook.write(out);
            log.info("workbook.write(out)结束！");
            out.flush();
            log.info("out.flush()结束！");
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

        log.info("########## 订单商品导出结束【End】：时间：" + CommUtil.formatDate(new Date()));
    }

    private int getCount(String order_status, String payment, String order_goods_type,
                         String beginTime, String endTime, Long storeId, String buyer_userName,
                         String mcmc_name, String businessMan_name, String order_id,
                         String goods_name) {
        if (SecurityUserHolder.getCurrentUser() == null) {
            return 0;
        }
        String sql = getCountSQL()
                     + getCommonSQL(order_status, payment, order_goods_type, beginTime, endTime,
                         storeId, buyer_userName, mcmc_name, businessMan_name, order_id, goods_name);
        List list = gueryService.executeNativeQuery(sql, null, -1, -1);
        if (list != null && list.size() > 0) {
            return Integer.parseInt(list.get(0).toString().trim());
        }
        return 0;
    }

    private List<Object[]> orderGoodsList(String order_status, String payment,
                                          String order_goods_type, String beginTime,
                                          String endTime, Long storeId, String buyer_userName,
                                          String mcmc_name, String businessMan_name,
                                          String order_id, String goods_name, int beginIndex,
                                          int pageSize) {
        if (SecurityUserHolder.getCurrentUser() == null) {
            return null;
        }
        String sql = getDetailSQL()
                     + getCommonSQL(order_status, payment, order_goods_type, beginTime, endTime,
                         storeId, buyer_userName, mcmc_name, businessMan_name, order_id, goods_name);
        List<Object[]> list = (ArrayList) gueryService.executeNativeQuery(sql, null, -1, -1);

        return list;
    }

    private String getCountSQL() {
        return "select count(gs.id)";
    }

    private String getDetailSQL() {
        StringBuffer sBuffer = new StringBuffer();
        sBuffer
            .append("SELECT of.order_id AS orderNo, g.goods_name AS goodsName, gs.spec_info AS goodsSpecInfo, ");
        sBuffer
            .append("gs.count AS count,case when g.supplier_info='{}' then '' else SUBSTRING_INDEX(SUBSTRING_INDEX(g.supplier_info,'supplier_name\":\"',-1),'\"',1) end AS storeName, item.bar_code AS barCode, ");
        sBuffer
            .append("item.self_code AS selfCode, gs.price, item.purchase_price AS purchasePrice,");
        sBuffer
            .append(" u.userName, IF ( u.userRole = 'BUYER_SELLER', '分销商会员','普通会员' ) AS userType, ");
        sBuffer
            .append("addr.trueName, addr.mobile, ")
            .append(
                "(select CONCAT(a1.areaName,a2.areaName,a3.areaName,addr.area_info ) from jm_area a3,jm_area a2 ,jm_area a1  ")
            .append(
                "         where a3.parent_id = a2.id  and a2.parent_id = a1.id   and a3.`level` = 2  and a3.id =addr.area_id ) as areaName, ")
            .append(" u.cus_ser_code AS cusSerCode,");
        sBuffer.append(" p.mark, of.order_status, of.payTime, of.totalPrice, of.createtime ");

        return sBuffer.toString();
    }

    private String getCommonSQL(String order_status, String payment, String order_goods_type,
                                String beginTime, String endTime, Long storeId,
                                String buyer_userName, String mcmc_name, String businessMan_name,
                                String order_id, String goods_name) {
        StringBuffer sBuffer = new StringBuffer();
        sBuffer.append(" from jm_goods_shopcart gs");
        sBuffer.append(" left join jm_goods_item item on item.id = gs.goods_item_id ");
        sBuffer.append(" left join jm_order of on gs.of_id = of.id ");
        sBuffer.append(" left join jm_addr addr on addr.id = of.addr_id ");
        sBuffer.append(" left join jm_payment p on of.payment_id = p.id  ");
        sBuffer.append(" left join jm_user u on of.user_id = u.id  ");
        sBuffer.append(" left join jm_goods g on gs.goods_id = g.id   ");
        sBuffer.append(" left join jm_goods_brand gb on g.goods_brand_id=gb.id ");
        sBuffer.append(" left join jm_store s on g.goods_store_id = s.id    ");
        sBuffer.append(" where gs.of_id is not null  ");

        //只查询本店铺的信息
        sBuffer.append(" and s.id = ").append(storeId);

        /* if (!CommUtil.null2String(order_status).equals("")) {
             sBuffer.append(" and of.order_status = ").append(
                 Integer.valueOf(CommUtil.null2Int(order_status)));
         }*/
        if (!CommUtil.null2String(payment).equals("")) {
            sBuffer.append(" and p.mark = '").append(payment).append("'");
        }
        if (!CommUtil.null2String(beginTime).equals("")) {
            sBuffer.append(" and of.createtime >= str_to_date('")
                .append(CommUtil.null2String(beginTime)).append("', '%Y-%m-%d')");
        }
        if (!CommUtil.null2String(endTime).equals("")) {
            endTime += " 23:59:59";
            sBuffer.append(" and of.createtime <= str_to_date('")
                .append(CommUtil.null2String(endTime)).append("', '%Y-%m-%d %H:%i:%s')");
        }

        if (!CommUtil.null2String(buyer_userName).equals("")) {
            sBuffer.append(" and u.userName like '%").append(buyer_userName).append("%'");
        }
        if (!CommUtil.null2String(order_id).equals("")) {
            sBuffer.append(" and of.order_id like '%").append(order_id).append("%'");
        }
        if (!CommUtil.null2String(order_status).equals("")) {
            sBuffer.append(" and of.order_status =").append(order_status);
        }

        sBuffer.append(" order by gs.createtime desc");
        return sBuffer.toString();
    }

    private int getDifferenceDay(String beginTime, String endTime) {
        long milsecPerDay = 1000 * 60 * 60 * 24; //每天的毫秒数
        Date beginDate = CommUtil.formatDate(beginTime);
        Date endDate = CommUtil.formatDate(endTime);
        if (beginDate != null && endDate != null) {
            return (int) ((endDate.getTime() - beginDate.getTime()) / milsecPerDay);
        }
        return -1;
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
                    //订单状态
                    if (17 == index || 19 == index) {
                        //支付时间 17 
                        //下单时间19 
                        cell.setCellValue(covertObjToStr(objs[index], "datetime"));
                    } else if (15 == index) {
                        //支付方式15  
                        cell.setCellValue(covertObjToStr(objs[index], "convertPaymentMark"));
                    } else if (16 == index) {
                        //订单状态16 convertOrderStatus
                        cell.setCellValue(covertObjToStr(objs[index], "convertOrderStatus"));
                    } else if (3 == index || 7 == index || 8 == index || 18 == index) {
                        //导出为数字格式
                        cell.setCellValue(CommUtil.null2Double(objs[index]));
                    } else {
                        //"订单编号", 0
                        //"商品名称", 1  
                        //"商品规格" 2 , 
                        //"数量" 3 , 
                        //"供应商名称" 4 ,
                        //"条码" 5 , 
                        //"自编码" 6 ,
                        //"销售价" 7 ,
                        //"进货价" 8 , 
                        //"会员登录账号" 9 ,
                        //"会员属性" 10 , 
                        //"收货人" 11 , 
                        //"收货人联系电话" 12 ,
                        //"收货地址" 13 , 
                        //"客服代码" 14 ,
                        //"支付方式" 15 , 
                        //"订单状态" 16 , 
                        //"支付时间" 17 , 
                        //"订单总价" 18 , 
                        //"下单时间" 19 
                        cell.setCellValue(new XSSFRichTextString(CommUtil.null2String(objs[index])));
                    }

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

    private XSSFRichTextString covertObjToStr(Object obj, String string) {
        if (!CommUtil.isNotNull(obj)) {
            return new XSSFRichTextString("");
        }
        if ("convertWorkFlowStatus".equals(string)) {
            return new XSSFRichTextString(convertWorkFlowStatus(Integer.parseInt(String
                .valueOf(obj).trim())));
        } else if ("convertPaymentMark".equals(string)) {
            return new XSSFRichTextString(convertPaymentMark(String.valueOf(obj).trim()));
        } else if ("convertOrderStatus".equals(string)) {
            return new XSSFRichTextString(convertOrderStatus(Integer.parseInt(String.valueOf(obj)
                .trim())));
        } else if ("datetime".equals(string)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return new XSSFRichTextString(dateFormat.format(obj));
        }
        return new XSSFRichTextString("");
    }

    //审核状态转换
    private String convertWorkFlowStatus(Integer status) {
        String s = "";
        if (status != null) {
            switch (status) {
                case 0:
                    s = "审核中";
                    break;
                case 1:
                    s = "审核通过";
                    break;
                case 2:
                    s = "已驳回";
                    break;
                default:
                    break;
            }
        }
        return s;
    }

    //订单状态转换
    private String convertOrderStatus(Integer order_status) {
        String orderStatus = "";
        if (order_status != null) {
            switch (order_status) {
                case 0:
                    orderStatus = "已取消";
                    break;
                case 10:
                    orderStatus = "待付款";
                    break;
                case 15:
                    orderStatus = "线下支付待审核";
                    break;
                case 16:
                    orderStatus = "货到付款待发货";
                    break;
                case 20:
                    orderStatus = "已付款";
                    break;
                case 30:
                    orderStatus = "已发货";
                    break;
                case 40:
                    orderStatus = "已收货";
                    break;
                case 45:
                    orderStatus = "买家申请退货";
                    break;
                case 46:
                    orderStatus = "退货中";
                    break;
                case 47:
                    orderStatus = "退货完成，已结束";
                    break;
                case 48:
                    orderStatus = "卖家拒绝退货";
                    break;
                case 49:
                    orderStatus = "退货失败";
                    break;
                case 50:
                    orderStatus = "已完成,已评价";
                    break;
                case 60:
                    orderStatus = "已结束";
                    break;
                case 65:
                    orderStatus = "已结束，不可评价";
                    break;
                case 265:
                    orderStatus = "已申请结算";
                    break;
                case 270:
                    orderStatus = "已结算";
                    break;
                default:
                    break;
            }
        }
        return orderStatus;
    }

    //支付方式转换
    private String convertPaymentMark(String payment_mark) {
        String paymentMark = "";
        if (payment_mark != null) {
            if ("alipay".equals(payment_mark))
                paymentMark = "支付宝";
            else if ("chinabank".equals(payment_mark))
                paymentMark = "网银在线";
            else if ("outline".equals(payment_mark))
                paymentMark = "线下支付";
            else if ("balance".equals(payment_mark))
                paymentMark = "预存款支付";
            else if ("weixin_wap".equals(payment_mark))
                paymentMark = "微信支付";
            else if ("unionpay".equals(payment_mark))
                paymentMark = "银联在线";
            else if ("paelse".equals(payment_mark))
                paymentMark = "货到付款";
            else if ("ccb".equals(payment_mark))
                paymentMark = "善付通";
            else
                paymentMark = "未支付";
        }
        return paymentMark;
    }

}