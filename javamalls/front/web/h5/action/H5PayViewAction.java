package com.javamalls.front.web.h5.action;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import sun.misc.BASE64Decoder;

import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.ctrl.admin.tools.MsgTools;
import com.javamalls.payment.alipay.config.AlipayConfig;
import com.javamalls.payment.alipay.util.AlipayCore;
import com.javamalls.payment.alipay.util.AlipayNotify;
import com.javamalls.payment.alipay.util.MD5;
import com.javamalls.payment.chinabank.h5.util.AsynNotificationReqDto;
import com.javamalls.payment.chinabank.h5.util.BASE64;
import com.javamalls.payment.chinabank.h5.util.DESUtil;
import com.javamalls.payment.tenpay.util.XMLUtil;
import com.javamalls.platform.domain.GoldLog;
import com.javamalls.platform.domain.GoldRecord;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.GoodsCart;
import com.javamalls.platform.domain.GoodsItem;
import com.javamalls.platform.domain.GoodsSpecProperty;
import com.javamalls.platform.domain.GroupGoods;
import com.javamalls.platform.domain.IntegralGoods;
import com.javamalls.platform.domain.IntegralGoodsCart;
import com.javamalls.platform.domain.IntegralGoodsOrder;
import com.javamalls.platform.domain.OrderForm;
import com.javamalls.platform.domain.OrderFormLog;
import com.javamalls.platform.domain.Payment;
import com.javamalls.platform.domain.Predeposit;
import com.javamalls.platform.domain.PredepositLog;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.WarehouseGoodsItem;
import com.javamalls.platform.service.IGoldLogService;
import com.javamalls.platform.service.IGoldRecordService;
import com.javamalls.platform.service.IGoodsCartService;
import com.javamalls.platform.service.IGoodsItemService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IGroupGoodsService;
import com.javamalls.platform.service.IIntegralGoodsOrderService;
import com.javamalls.platform.service.IIntegralGoodsService;
import com.javamalls.platform.service.IOrderFormLogService;
import com.javamalls.platform.service.IOrderFormService;
import com.javamalls.platform.service.IPaymentService;
import com.javamalls.platform.service.IPredepositLogService;
import com.javamalls.platform.service.IPredepositService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.ITemplateService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;
import com.javamalls.platform.service.IWarehouseGoodsItemService;

@Controller
public class H5PayViewAction {

    private static final Logger        logger = Logger.getLogger(H5PayViewAction.class);

    @Autowired
    private ISysConfigService          configService;
    @Autowired
    private IUserConfigService         userConfigService;
    @Autowired
    private IOrderFormService          orderFormService;
    @Autowired
    private IOrderFormLogService       orderFormLogService;
    @Autowired
    private IPredepositService         predepositService;
    @Autowired
    private IPredepositLogService      predepositLogService;
    @Autowired
    private IGoldRecordService         goldRecordService;
    @Autowired
    private IGoldLogService            goldLogService;
    @Autowired
    private IUserService               userService;
    @Autowired
    private IPaymentService            paymentService;
    @Autowired
    private IIntegralGoodsOrderService integralGoodsOrderService;
    @Autowired
    private IIntegralGoodsService      integralGoodsService;
    @Autowired
    private IGroupGoodsService         groupGoodsService;
    @Autowired
    private IGoodsService              goodsService;
    @Autowired
    private IGoodsCartService          goodsCartService;
    @Autowired
    private ITemplateService           templateService;
    @Autowired
    private MsgTools                   msgTools;

    @Autowired
    private IGoodsItemService          goodsItemService;
    @Autowired
    private IWarehouseGoodsItemService warehouseGoodsItemService;

    /**手机支付宝同步回调函数
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = { "/mobile/alipay_return.htm",
            "/store/{id}.htm/mobile/alipay_return.htm" })
    public ModelAndView aplipay_return(HttpServletRequest request, HttpServletResponse response)
                                                                                                throws Exception {

        logger.info("手机端支付宝同步回调开始,alipay_return");

        ModelAndView mv = new JModelAndView("h5/order_finish.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        String trade_no = request.getParameter("trade_no");
        String order_no = request.getParameter("out_trade_no");
        String total_fee = request.getParameter("total_fee");
        String subject = request.getParameter("subject");

        logger.info("手机端支付宝同步回调：参数trade_no" + trade_no + ",order_no:" + order_no + ",total_fee:"
                    + total_fee + ",subject:" + subject);

        String key = order_no;
        //支付类型  goods
        String type = key.split("_")[1];
        order_no = key.split("_")[0];
        String trade_status = request.getParameter("trade_status");
        if ("".equals(CommUtil.null2String(trade_status)))
            trade_status = new String(request.getParameter("result").getBytes("ISO-8859-1"),
                "UTF-8");
        OrderForm order = null;
        if (type.equals("goods")) {
            order = this.orderFormService.getObjById(CommUtil.null2Long(order_no));
        }

        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = valueStr + values[i] + ",";
            }
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        AlipayConfig config = new AlipayConfig();
        if (type.equals("goods")) {
            config.setKey(order.getPayment().getSafeKey());
            config.setPartner(order.getPayment().getPartner());
            config.setSeller_email(order.getPayment().getSeller_email());
        }

        config.setNotify_url(CommUtil.getURL(request) + "/mobile/alipay_notify.htm");
        config.setReturn_url(CommUtil.getURL(request) + "/mobile/alipay_return.htm");

        String format = "xml";
        String v = "2.0";
        params.put("service", "alipay.wap.auth.authAndExecute");
        params.put("partner", config.getPartner());
        params.put("_input_charset", config.getInput_charset());
        params.put("sec_id", config.getSign_type());
        params.put("format", format);
        params.put("v", v);
        params.put("req_data", Constent.PAY_ORDER_TYPE.get("req_data" + key));
        params.remove("out_trade_no");
        params.remove("request_token");
        params.remove("result");
        params.remove("trade_no");

        Map<String, String> sParaNew = AlipayCore.paraFilter(params);
        String preSignStr = AlipayCore.createLinkString(sParaNew);
        String sign = MD5.sign(preSignStr, config.getKey(), config.getInput_charset());
        params.put("sign", sign);
        boolean verify_result = AlipayNotify.verify(config, params);
        if (verify_result) {
            if ((type.equals("goods"))
                && ((trade_status.equals("WAIT_SELLER_SEND_GOODS"))
                    || (trade_status.equals("TRADE_FINISHED"))
                    || (trade_status.equals("TRADE_SUCCESS")) || "SUCCESS".equals(trade_status
                    .toUpperCase()))) {
                if (order.getOrder_status() != 20) {
                    order.setOrder_status(20);
                    order.setOut_order_id(trade_no);
                    order.setPayTime(new Date());
                    order.setShipTime(new Date());
                    this.orderFormService.update(order);

                    update_goods_inventory(order);
                    OrderFormLog ofl = new OrderFormLog();
                    ofl.setCreatetime(new Date());
                    ofl.setLog_info("支付宝在线支付");
                    ofl.setLog_user(order.getUser());
                    ofl.setOf(order);
                    this.orderFormLogService.save(ofl);
                    if (this.configService.getSysConfig().isEmailEnable()) {
                        send_order_email(request, order, order.getUser().getEmail(),
                            "email_tobuyer_online_pay_ok_notify");
                        send_order_email(request, order, order.getStore().getUser().getEmail(),
                            "email_toseller_online_pay_ok_notify");
                    }
                    if (this.configService.getSysConfig().isSmsEnbale()) {
                        send_order_sms(request, order, order.getUser().getMobile(),
                            "sms_tobuyer_online_pay_ok_notify");
                        send_order_sms(request, order, order.getStore().getUser().getMobile(),
                            "sms_toseller_online_pay_ok_notify");
                    }

                }
                mv.addObject("obj", order);
            }

        } else {
            mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "支付宝回调失败！");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
        }
        return mv;
    }

    /**支付宝手机支付异步回调
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = { "/mobile/alipay_notify.htm",
            "/store/{id}.htm/mobile/alipay_notify.htm" })
    public void alipay_notify(HttpServletRequest request, HttpServletResponse response)
                                                                                       throws Exception {

        logger.info("手机端支付宝异步回调开始,alipay_notify");
        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = valueStr + values[i] + ",";
            }
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        //out_trade_no 从params里的   notify_data里 获取，然后转换成xml 

        String notifyData = params.get("notify_data");
        //将该参数转换成xml 去掉最后一个逗号
        Map<String, String> xmlMap = XMLUtil.doXMLParse(notifyData.substring(0,
            notifyData.length() - 1));

        String trade_no = xmlMap.get("trade_no");
        String order_no = xmlMap.get("out_trade_no");
        String total_fee = xmlMap.get("total_fee");
        String subject = xmlMap.get("subject");

        logger.info("手机端支付宝异步回调：参数trade_no" + trade_no + ",order_no:" + order_no + ",total_fee:"
                    + total_fee + ",subject:" + subject);

        String key = order_no;
        String type = key.split("_")[1];
        order_no = key.split("_")[0];
        String trade_status = xmlMap.get("trade_status");
        OrderForm order = null;

        if (type.equals("goods")) {
            order = this.orderFormService.getObjById(CommUtil.null2Long(order_no));
        }

        AlipayConfig config = new AlipayConfig();
        if (type.equals("goods")) {
            config.setKey(order.getPayment().getSafeKey());
            config.setPartner(order.getPayment().getPartner());
            config.setSeller_email(order.getPayment().getSeller_email());
        }

        config.setNotify_url(CommUtil.getURL(request) + "/mobile/alipay_notify.htm");
        config.setReturn_url(CommUtil.getURL(request) + "/mobile/alipay_return.htm");

        String format = "xml";
        String v = "2.0";
        params.put("service", "alipay.wap.auth.authAndExecute");
        params.put("partner", config.getPartner());
        params.put("_input_charset", config.getInput_charset());
        params.put("sec_id", config.getSign_type());
        params.put("format", format);
        params.put("v", v);
        params.put("req_data", Constent.PAY_ORDER_TYPE.get("req_data" + key));
        params.remove("out_trade_no");
        params.remove("request_token");
        params.remove("result");
        params.remove("trade_no");

        Map<String, String> sParaNew = AlipayCore.paraFilter(params);
        String preSignStr = AlipayCore.createLinkString(sParaNew);
        String sign = MD5.sign(preSignStr, config.getKey(), config.getInput_charset());
        params.put("sign", sign);
        boolean verify_result = AlipayNotify.verify(config, params);
        if (verify_result) {
            if ((type.equals("goods"))
                && ((trade_status.equals("WAIT_SELLER_SEND_GOODS"))
                    || (trade_status.equals("TRADE_FINISHED"))
                    || (trade_status.equals("TRADE_SUCCESS")) || "SUCCESS".equals(trade_status
                    .toUpperCase())) && (order.getOrder_status() < 20)) {
                order.setOrder_status(20);
                order.setOut_order_id(trade_no);
                order.setPayTime(new Date());
                order.setShipTime(new Date());
                this.orderFormService.update(order);

                update_goods_inventory(order);
                OrderFormLog ofl = new OrderFormLog();
                ofl.setCreatetime(new Date());
                ofl.setLog_info("手机支付宝在线支付");
                ofl.setLog_user(order.getUser());
                ofl.setOf(order);
                this.orderFormLogService.save(ofl);
                if (this.configService.getSysConfig().isEmailEnable()) {
                    send_order_email(request, order, order.getUser().getEmail(),
                        "email_tobuyer_online_pay_ok_notify");
                    send_order_email(request, order, order.getStore().getUser().getEmail(),
                        "email_toseller_online_pay_ok_notify");
                }
                if (this.configService.getSysConfig().isSmsEnbale()) {
                    send_order_sms(request, order, order.getUser().getMobile(),
                        "sms_tobuyer_online_pay_ok_notify");
                    send_order_sms(request, order, order.getStore().getUser().getMobile(),
                        "sms_toseller_online_pay_ok_notify");
                }

            }

            response.setContentType("text/plain");
            response.setHeader("Cache-Control", "no-cache");
            response.setCharacterEncoding("UTF-8");
            try {
                PrintWriter writer = response.getWriter();
                writer.print("success");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            response.setContentType("text/plain");
            response.setHeader("Cache-Control", "no-cache");
            response.setCharacterEncoding("UTF-8");
            try {
                PrintWriter writer = response.getWriter();
                writer.print("fail");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**手机网银+ 支付成功同步回调函数
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping({ "/mobile/chinabank_return.htm" })
    public ModelAndView chinabank_return(HttpServletRequest request, HttpServletResponse response)
                                                                                                  throws Exception {
        ModelAndView mv = new JModelAndView("h5/order_finish.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
            response);
        mv.addObject("op_title", "网银在线支付成功！");
        mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
        //同步只返回了这两参数
        String token = request.getParameter("token");
        String tradeNum = request.getParameter("tradeNum");
        String order_id = tradeNum.split("_")[1];
        String type = tradeNum.split("_")[2];

        OrderForm order = null;
        Predeposit obj = null;
        GoldRecord gold = null;
        IntegralGoodsOrder ig_order = null;
        String pay_status = "20";
        if (type.equals("goods")) {
            order = this.orderFormService.getObjById(CommUtil.null2Long(order_id.trim()));
            mv.addObject("obj", order);
            pay_status = order.getOrder_status() + "";
        }
        if ((type.equals("cash")) || (type.equals("gold")) || (type.equals("integral"))) {
            Map<String, Object> q_params = new HashMap<String, Object>();
            q_params.put("install", Boolean.valueOf(true));
            if (type.equals("cash")) {
                obj = this.predepositService.getObjById(CommUtil.null2Long(order_id));
                pay_status = obj.getPd_pay_status() + "";
                q_params.put("mark", obj.getPd_payment());
                mv = new JModelAndView("h5/success.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "充值" + obj.getPd_amount() + "成功");
                mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/predeposit_list.htm");
            }
            if (type.equals("gold")) {
                gold = this.goldRecordService.getObjById(CommUtil.null2Long(order_id));
                pay_status = gold.getGold_pay_status() + "";
                q_params.put("mark", gold.getGold_payment());
                mv = new JModelAndView("h5/success.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "兑换" + gold.getGold_count() + "金币成功");
                mv.addObject("url", CommUtil.getURL(request)
                                    + "/mobile/seller/gold_record_list.htm");
            }
            if (type.equals("integral")) {
                ig_order = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(order_id));
                pay_status = ig_order.getIgo_status() + "";
                q_params.put("mark", ig_order.getIgo_payment());
                mv = new JModelAndView("h5/integral_order_finish.html",
                    this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
                    request, response);
                mv.addObject("obj", ig_order);
            }
        }

        /*if (!"20".equals(pay_status)) {
            if (type.equals("goods")) {
                order.setOrder_status(20);
                order.setPayTime(new Date());
                order.setShipTime(new Date());
                this.orderFormService.update(order);
                update_goods_inventory(order);
                OrderFormLog ofl = new OrderFormLog();
                ofl.setCreatetime(new Date());
                ofl.setLog_info("网银在线支付");
                ofl.setLog_user(order.getUser());
                ofl.setOf(order);
                this.orderFormLogService.save(ofl);
                mv.addObject("obj", order);
                if (this.configService.getSysConfig().isEmailEnable()) {
                    send_order_email(request, order, order.getUser().getEmail(),
                        "email_tobuyer_online_pay_ok_notify");
                    send_order_email(request, order, order.getStore().getUser().getEmail(),
                        "email_toseller_online_pay_ok_notify");
                }
                if (this.configService.getSysConfig().isSmsEnbale()) {
                    send_order_sms(request, order, order.getUser().getMobile(),
                        "sms_tobuyer_online_pay_ok_notify");
                    send_order_sms(request, order, order.getStore().getUser().getMobile(),
                        "sms_toseller_online_pay_ok_notify");
                }
                update_goods_bind(order, null);

                User seller = order.getStore().getUser();
                double seller_availableBalance = order.getTotalPrice().doubleValue();

                List<GoodsCart> gcs = order.getGcs();
                double brokerage;
                double shop_availableBalance = 0;

                for (GoodsCart gc : gcs) {
                    brokerage = gc.getGoods().getGc().getBrokerage();
                    if (brokerage > 0) {
                        shop_availableBalance = shop_availableBalance
                                                + CommUtil.null2Double(gc.getPrice())
                                                * CommUtil.null2Double(gc.getCount())
                                                * CommUtil.div(brokerage, 100);
                    }
                }

                seller_availableBalance = seller_availableBalance - shop_availableBalance;
                seller.setFreezeBlance(BigDecimal.valueOf(CommUtil.add(seller.getFreezeBlance(),
                    seller_availableBalance)));
                this.userService.update(seller);

                PredepositLog log = new PredepositLog();
                log.setCreatetime(new Date());
                log.setPd_log_user(seller);
                log.setPd_op_type("增加");
                log.setPd_log_amount(BigDecimal.valueOf(CommUtil
                    .null2Double(seller_availableBalance)));
                log.setPd_log_info("订单" + order.getOrder_id() + "增加冻结预存款");
                log.setPd_type("冻结预存款");
                this.predepositLogService.save(log);
            }
            if (type.endsWith("cash")) {
                obj.setPd_status(1);
                obj.setPd_pay_status(2);
                this.predepositService.update(obj);
                User user = this.userService.getObjById(obj.getPd_user().getId());
                user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
                    user.getAvailableBalance(), obj.getPd_amount())));
                this.userService.update(user);
                PredepositLog log = new PredepositLog();
                log.setCreatetime(new Date());
                log.setPd_log_amount(obj.getPd_amount());
                log.setPd_log_user(obj.getPd_user());
                log.setPd_op_type("充值");
                log.setPd_type("可用预存款");
                log.setPd_log_info("网银在线支付");
                this.predepositLogService.save(log);
                mv = new JModelAndView("h5/success.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "充值" + obj.getPd_amount() + "成功");
                mv.addObject("url", CommUtil.getURL(request) + "/mobile/buyer/predeposit_list.htm");
            }
            GoldLog log;
            if (type.equals("gold")) {
                gold.setGold_status(1);
                gold.setGold_pay_status(2);
                this.goldRecordService.update(gold);
                User user = this.userService.getObjById(gold.getGold_user().getId());
                user.setGold(user.getGold() + gold.getGold_count());
                this.userService.update(user);
                log = new GoldLog();
                log.setCreatetime(new Date());
                log.setGl_payment(gold.getGold_payment());
                log.setGl_content("网银在线支付");
                log.setGl_money(gold.getGold_money());
                log.setGl_count(gold.getGold_count());
                log.setGl_type(0);
                log.setGl_user(gold.getGold_user());
                log.setGr(gold);
                this.goldLogService.save(log);
                mv = new JModelAndView("h5/success.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "兑换" + gold.getGold_count() + "金币成功");
                mv.addObject("url", CommUtil.getURL(request)
                                    + "/mobile/seller/gold_record_list.htm");
            }
            if (type.equals("integral")) {
                ig_order.setIgo_status(20);
                ig_order.setIgo_pay_time(new Date());
                ig_order.setIgo_payment("bill");
                this.integralGoodsOrderService.update(ig_order);
                for (IntegralGoodsCart igc : ig_order.getIgo_gcs()) {
                    IntegralGoods goods = igc.getGoods();
                    goods.setIg_goods_count(goods.getIg_goods_count() - igc.getCount());
                    goods.setIg_exchange_count(goods.getIg_exchange_count() + igc.getCount());
                    this.integralGoodsService.update(goods);
                }
                mv = new JModelAndView("h5/integral_order_finish.html",
                    this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1,
                    request, response);
                mv.addObject("obj", ig_order);
            }
        } else {
            mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "网银在线支付失败！");
            mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
        }*/
        return mv;
    }

    /**手机网银+ 支付成功异步回调函数
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping({ "/mobile/chinabank_notify.htm" })
    public void chinabank_notify(HttpServletRequest request, HttpServletResponse response)
                                                                                          throws Exception {
        //返回信息
        String result = "failure";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("type", "admin");
        params.put("mark", "chinabank_wap");
        List<Payment> payments = this.paymentService.query(
            "select obj from Payment obj where obj.type=:type and obj.mark=:mark", params, -1, -1);
        Payment payment = new Payment();
        if (payments != null && payments.size() > 0) {
            payment = (Payment) payments.get(0);
        }
        //商户支付请求/交易查询/退款 RSA加密私钥(由商户生成,并将对应的公钥发给网银)
        //        String rsaPrivateKey = payment.getChinabank_rsa_key();
        //获取配置密钥
        String desKey = payment.getChinabank_des_key();
        String md5Key = payment.getChinabank_key();
        //获取通知原始信息
        String resp = request.getParameter("resp");
        try {
            //首先对Base64编码的数据进行解密
            byte[] decryptBASE64Arr = BASE64.decode(resp);
            //解析XML
            AsynNotificationReqDto dto = parseXML(decryptBASE64Arr);
            //验证签名
            String ownSign = generateSign(dto.getVersion(), dto.getMerchant(), dto.getTerminal(),
                dto.getData(), md5Key);
            if (dto.getSign().equals(ownSign)) {

                //对Data数据进行解密
                byte[] rsaKey = decryptBASE64(desKey);
                String decryptArr = DESUtil.decrypt(dto.getData(), rsaKey, "utf-8");
                //解密出来的数据也为XML文档，可以用dom4j解析
                dto.setData(decryptArr);

                String order_id = dto.getOrderId();
                if ("".equals(CommUtil.null2String(order_id)) || !order_id.contains("_"))
                    order_id = paseText(decryptArr);
                /*
                    Object objSign = Constent.CHINA_BANK_PAY.get(order_id);
                    String signStr = SignUtil.sign(objSign, rsaPrivateKey);
                    if (!dto.getSign().equals(signStr)) {
                        //验签不对
                    }
                */
                //验签成功，业务处理

                String type = "";
                if (order_id.contains("_")) {
                    type = order_id.split("_")[2];
                    order_id = order_id.split("_")[1];
                }

                OrderForm order = null;
                Predeposit obj = null;
                GoldRecord gold = null;
                IntegralGoodsOrder ig_order = null;
                String pay_status = "20";
                if (type.equals("goods")) {
                    order = this.orderFormService.getObjById(CommUtil.null2Long(order_id.trim()));
                    pay_status = order.getOrder_status() + "";
                }
                if ((type.equals("cash")) || (type.equals("gold")) || (type.equals("integral"))) {
                    Map<String, Object> q_params = new HashMap<String, Object>();
                    q_params.put("install", Boolean.valueOf(true));
                    if (type.equals("cash")) {
                        obj = this.predepositService.getObjById(CommUtil.null2Long(order_id));
                        pay_status = obj.getPd_pay_status() + "";
                        q_params.put("mark", obj.getPd_payment());
                    }
                    if (type.equals("gold")) {
                        gold = this.goldRecordService.getObjById(CommUtil.null2Long(order_id));
                        pay_status = gold.getGold_pay_status() + "";
                        q_params.put("mark", gold.getGold_payment());
                    }
                    if (type.equals("integral")) {
                        ig_order = this.integralGoodsOrderService.getObjById(CommUtil
                            .null2Long(order_id));
                        pay_status = ig_order.getIgo_status() + "";
                        q_params.put("mark", ig_order.getIgo_payment());
                    }
                    q_params.put("type", "admin");
                }

                if (!"20".equals(pay_status)) {
                    if (type.equals("goods")) {
                        order.setOrder_status(20);
                        order.setPayTime(new Date());
                        order.setShipTime(new Date());
                        this.orderFormService.update(order);
                        update_goods_inventory(order);
                        OrderFormLog ofl = new OrderFormLog();
                        ofl.setCreatetime(new Date());
                        ofl.setLog_info("网银在线支付");
                        ofl.setLog_user(order.getUser());
                        ofl.setOf(order);
                        this.orderFormLogService.save(ofl);
                        if (this.configService.getSysConfig().isEmailEnable()) {
                            send_order_email(request, order, order.getUser().getEmail(),
                                "email_tobuyer_online_pay_ok_notify");
                            send_order_email(request, order, order.getStore().getUser().getEmail(),
                                "email_toseller_online_pay_ok_notify");
                        }
                        if (this.configService.getSysConfig().isSmsEnbale()) {
                            send_order_sms(request, order, order.getUser().getMobile(),
                                "sms_tobuyer_online_pay_ok_notify");
                            send_order_sms(request, order, order.getStore().getUser().getMobile(),
                                "sms_toseller_online_pay_ok_notify");
                        }
                        update_goods_bind(order, null);

                        User seller = order.getStore().getUser();
                        double seller_availableBalance = order.getTotalPrice().doubleValue();

                        List<GoodsCart> gcs = order.getGcs();
                        double brokerage;
                        double shop_availableBalance = 0;

                        /*for (GoodsCart gc : gcs) {
                            brokerage = gc.getGoods().getGc().getBrokerage();
                            if (brokerage > 0) {
                                shop_availableBalance = shop_availableBalance
                                                        + CommUtil.null2Double(gc.getPrice())
                                                        * CommUtil.null2Double(gc.getCount())
                                                        * CommUtil.div(brokerage, 100);
                            }
                        }*/

                        seller_availableBalance = seller_availableBalance - shop_availableBalance;
                        seller.setFreezeBlance(BigDecimal.valueOf(CommUtil.add(
                            seller.getFreezeBlance(), seller_availableBalance)));
                        this.userService.update(seller);

                        PredepositLog log = new PredepositLog();
                        log.setCreatetime(new Date());
                        log.setPd_log_user(seller);
                        log.setPd_op_type("增加");
                        log.setPd_log_amount(BigDecimal.valueOf(CommUtil
                            .null2Double(seller_availableBalance)));
                        log.setPd_log_info("订单" + order.getOrder_id() + "增加冻结预存款");
                        log.setPd_type("冻结预存款");
                        this.predepositLogService.save(log);
                    }
                    if (type.endsWith("cash")) {
                        obj.setPd_status(1);
                        obj.setPd_pay_status(2);
                        this.predepositService.update(obj);
                        User user = this.userService.getObjById(obj.getPd_user().getId());
                        user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
                            user.getAvailableBalance(), obj.getPd_amount())));
                        this.userService.update(user);
                        PredepositLog log = new PredepositLog();
                        log.setCreatetime(new Date());
                        log.setPd_log_amount(obj.getPd_amount());
                        log.setPd_log_user(obj.getPd_user());
                        log.setPd_op_type("充值");
                        log.setPd_type("可用预存款");
                        log.setPd_log_info("网银在线支付");
                        this.predepositLogService.save(log);
                    }
                    GoldLog log;
                    if (type.equals("gold")) {
                        gold.setGold_status(1);
                        gold.setGold_pay_status(2);
                        this.goldRecordService.update(gold);
                        User user = this.userService.getObjById(gold.getGold_user().getId());
                        user.setGold(user.getGold() + gold.getGold_count());
                        this.userService.update(user);
                        log = new GoldLog();
                        log.setCreatetime(new Date());
                        log.setGl_payment(gold.getGold_payment());
                        log.setGl_content("网银在线支付");
                        log.setGl_money(gold.getGold_money());
                        log.setGl_count(gold.getGold_count());
                        log.setGl_type(0);
                        log.setGl_user(gold.getGold_user());
                        log.setGr(gold);
                        this.goldLogService.save(log);
                    }
                    if (type.equals("integral")) {
                        ig_order.setIgo_status(20);
                        ig_order.setIgo_pay_time(new Date());
                        ig_order.setIgo_payment("bill");
                        this.integralGoodsOrderService.update(ig_order);
                        for (IntegralGoodsCart igc : ig_order.getIgo_gcs()) {
                            IntegralGoods goods = igc.getGoods();
                            goods.setIg_goods_count(goods.getIg_goods_count() - igc.getCount());
                            goods.setIg_exchange_count(goods.getIg_exchange_count()
                                                       + igc.getCount());
                            this.integralGoodsService.update(goods);
                        }
                    }
                }
                result = "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**手机网银+ 支付失败同步回调函数
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping({ "/mobile/chinabank_fail.htm" })
    public ModelAndView chinabank_fail(HttpServletRequest request, HttpServletResponse response)
                                                                                                throws Exception {
        ModelAndView mv = new JModelAndView("h5/error.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        mv.addObject("op_title", "网银在线支付失败！");
        mv.addObject("url", CommUtil.getURL(request) + "/mobile/index.htm");
        return mv;
    }

    //付款后，如果有捆绑商品，按店铺拆分为多个  
    private void update_goods_bind(OrderForm order, String trade_no) {
        Map<Long, OrderForm> mp = new HashMap<Long, OrderForm>();
        int n = 0;

        for (int i = order.getGcs().size() - 1; i >= 0; i--) {
            GoodsCart gc = order.getGcs().get(i);
            if ("bind".equals(gc.getCart_type())) {
                Long storeid = gc.getGoods().getGoods_store().getId();
                OrderForm of = mp.get(storeid);

                order.getGcs().remove(gc);
                order
                    .setTotalPrice(BigDecimal.valueOf(CommUtil.subtract(
                        order.getTotalPrice(),
                        BigDecimal.valueOf(Double.parseDouble(gc.getPrice().toString())
                                           * gc.getCount()))));
                if (of != null) {
                    of.setTotalPrice(BigDecimal.valueOf(Double.parseDouble(of.getTotalPrice()
                        .toString()) + Double.parseDouble(gc.getPrice().toString()) * gc.getCount()));
                    of.getGcs().add(gc);
                } else {

                    n++;

                    of = new OrderForm();
                    of.setAddr(order.getAddr());
                    of.setAuto_confirm_email(order.isAuto_confirm_email());
                    of.setAuto_confirm_sms(order.isAuto_confirm_sms());
                    of.setCreatetime(order.getCreatetime());
                    of.setDisabled(order.isDisabled());
                    of.setEc(order.getEc());
                    of.setFinishTime(order.getFinishTime());

                    of.setOrder_type(order.getOrder_type());
                    of.setOrder_id(order.getOrder_id() + "-" + i);
                    of.setOrder_status(order.getOrder_status());
                    of.setOut_order_id(trade_no);
                    of.setPay_msg(order.getPay_msg());
                    of.setPayment(order.getPayment());
                    of.setPayTime(order.getPayTime());
                    of.setStore(gc.getGoods().getGoods_store());
                    of.setUser(order.getUser());

                    of.setTotalPrice(BigDecimal.valueOf(Double
                        .parseDouble(gc.getPrice().toString()) * gc.getCount()));
                    of.getGcs().add(gc);

                    mp.put(storeid, of);

                }

            }
        }

        if (mp.size() > 0) {
            for (OrderForm of : mp.values()) {
                this.orderFormService.save(of);
                for (GoodsCart gc : of.getGcs()) {
                    gc.setOf(of);
                    this.goodsCartService.save(gc);
                }
            }
            this.orderFormService.update(order);
        }
    }

    private void update_goods_inventory(OrderForm order) {
        for (GoodsCart gc : order.getGcs()) {
            Goods goods = gc.getGoods();
            if ((goods.getGroup() != null) && (goods.getGroup_buy() == 2)) {
                for (GroupGoods gg : goods.getGroup_goods_list()) {
                    if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
                        gg.setGg_def_count(gg.getGg_def_count() + gc.getCount());
                        gg.setGg_count(gg.getGg_count() - gc.getCount());
                        this.groupGoodsService.update(gg);
                    }
                }
            }
            List<String> gsps = new ArrayList<String>();
            for (GoodsSpecProperty gsp : gc.getGsps()) {
                gsps.add(gsp.getId().toString());
            }
            String[] gsp_list = new String[gsps.size()];
            gsps.toArray(gsp_list);
            goods.setGoods_salenum(goods.getGoods_salenum() + gc.getCount());
            String inventory_type = goods.getInventory_type() == null ? "all" : goods
                .getInventory_type();
            Map<String, Object> temp;
            //减库存    为了保持库存统一总库存和规格属性库存都要减
            /**
             * 订货宝库存更新-在线支付支付
             */
            // goods.setGoods_inventory(goods.getGoods_inventory() - gc.getCount());
            if (!inventory_type.equals("all")) {/*
                                                List<HashMap> list = (List) Json.fromJson(ArrayList.class,
                                                CommUtil.null2String(goods.getGoods_inventory_detail()));
                                                for (Iterator localIterator4 = list.iterator(); localIterator4.hasNext();) {
                                                temp = (Map) localIterator4.next();
                                                String[] temp_ids = CommUtil.null2String(temp.get("id")).split("_");
                                                Arrays.sort(temp_ids);
                                                Arrays.sort(gsp_list);
                                                if (Arrays.equals(temp_ids, gsp_list)) {
                                                temp.put("count",
                                                Integer.valueOf(CommUtil.null2Int(temp.get("count")) - gc.getCount()));
                                                }
                                                }
                                                goods.setGoods_inventory_detail(Json.toJson(list, JsonFormat.compact()));
                                                */
                List<GoodsItem> goods_items = new ArrayList<GoodsItem>();
                HashMap<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("goodsId", goods.getId());
                goods_items = this.goodsItemService
                    .query(
                        "select obj from GoodsItem obj where obj.disabled=false and  obj.goods.id =:goodsId",
                        paramMap, -1, -1);
                if (goods_items != null && goods_items.size() > 0) {
                    if (goods_items.size() > 1) {
                        for (GoodsItem item : goods_items) {
                            String[] temp_ids = item.getSpec_combination().split("_");
                            Arrays.sort(temp_ids);
                            Arrays.sort(gsp_list);
                            if (Arrays.equals(temp_ids, gsp_list)) {
                                item.setGoods_inventory(item.getGoods_inventory() - gc.getCount());
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("goodsItem_id", item.getId());
                                //更新库存商品的库存
                                List<WarehouseGoodsItem> list = this.warehouseGoodsItemService
                                    .query(
                                        "select obj from WarehouseGoodsItem obj where obj.goods_item.id=:goodsItem_id",
                                        map, 0, 1);
                                if (list != null && list.size() > 0) {
                                    WarehouseGoodsItem warehouseGoodsItem = list.get(0);
                                    warehouseGoodsItem.setWarehoust_number(warehouseGoodsItem
                                        .getWarehoust_number() - gc.getCount());
                                    this.warehouseGoodsItemService.update(warehouseGoodsItem);

                                }
                            }
                        }
                    } else {
                        GoodsItem item = goods_items.get(0);
                        item.setGoods_inventory(item.getGoods_inventory() - gc.getCount());
                        //商品库存更新
                        goods.setGoods_inventory(item.getGoods_inventory());
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("goodsItem_id", item.getId());
                        //更新库存商品的库存
                        List<WarehouseGoodsItem> list = this.warehouseGoodsItemService
                            .query(
                                "select obj from WarehouseGoodsItem obj where obj.goods_item.id=:goodsItem_id",
                                map, 0, 1);
                        if (list != null && list.size() > 0) {
                            WarehouseGoodsItem warehouseGoodsItem = list.get(0);
                            warehouseGoodsItem.setWarehoust_number(warehouseGoodsItem
                                .getWarehoust_number() - gc.getCount());
                            this.warehouseGoodsItemService.update(warehouseGoodsItem);

                        }
                    }
                }
                goods.setGoods_item_list(goods_items);
            }
            for (GroupGoods gg : goods.getGroup_goods_list()) {
                if ((gg.getGroup().getId().equals(goods.getGroup().getId()))
                    && (gg.getGg_count() == 0)) {
                    goods.setGroup_buy(3);
                }
            }
            this.goodsService.update(goods);
        }
    }

    //XML解析为Java对象
    private static AsynNotificationReqDto parseXML(byte[] xmlString) {
        Document document = null;
        try {
            InputStream is = new ByteArrayInputStream(xmlString);
            SAXReader sax = new SAXReader(false);
            document = sax.read(is);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        AsynNotificationReqDto dto = new AsynNotificationReqDto();
        Element rootElement = document.getRootElement();
        if (null == rootElement) {
            return dto;
        }
        Element versionEliment = rootElement.element("VERSION");
        if (null != versionEliment) {
            dto.setVersion(versionEliment.getText());
        }
        Element merchantEliment = rootElement.element("MERCHANT");
        if (null != merchantEliment) {
            dto.setMerchant(merchantEliment.getText());
        }
        Element terminalEliment = rootElement.element("TERMINAL");
        if (null != terminalEliment) {
            dto.setTerminal(terminalEliment.getText());
        }
        Element orderIdEliment = rootElement.element("ID");
        if (null != orderIdEliment) {
            dto.setOrderId(orderIdEliment.getText());
        }
        Element datalEliment = rootElement.element("DATA");
        if (null != datalEliment) {
            dto.setData(datalEliment.getText());
        }
        Element signEliment = rootElement.element("SIGN");
        if (null != signEliment) {
            dto.setSign(signEliment.getText());
        }
        return dto;
    }

    //对Base64进行解密
    public static byte[] decryptBASE64(String key) throws Exception {
        return (new BASE64Decoder()).decodeBuffer(key);
    }

    /**
     * 签名
     */
    public static String generateSign(String version, String merchant, String terminal,
                                      String data, String md5Key) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(version);
        sb.append(merchant);
        sb.append(terminal);
        sb.append(data);
        String sign = "";
        sign = md5(sb.toString(), md5Key);
        return sign;
    }

    public static String md5(String text, String salt) throws Exception {
        byte[] bytes = (text + salt).getBytes();

        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(bytes);
        bytes = messageDigest.digest();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if ((bytes[i] & 0xff) < 0x10) {
                sb.append("0");
            }
            sb.append(Long.toString(bytes[i] & 0xff, 16));
        }
        return sb.toString().toLowerCase();
    }

    private void send_order_email(HttpServletRequest request, OrderForm order, String email,
                                  String mark) {
        try {

            com.javamalls.platform.domain.Template template = this.templateService
                .getObjByProperty("mark", mark);
            if ((template != null) && (template.isOpen())) {
                String subject = template.getTitle();
                String path = request.getSession().getServletContext().getRealPath("/") + "/vm/";
                PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
                    path + "msg.vm", false), "UTF-8"));
                pwrite.print(template.getContent());
                pwrite.flush();
                pwrite.close();

                Properties p = new Properties();
                p.setProperty("file.resource.loader.path", request.getRealPath("/") + "vm"
                                                           + File.separator);
                p.setProperty("input.encoding", "UTF-8");
                p.setProperty("output.encoding", "UTF-8");
                Velocity.init(p);
                org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm", "UTF-8");
                VelocityContext context = new VelocityContext();
                context.put("buyer", order.getUser());
                context.put("seller", order.getStore().getUser());
                context.put("config", this.configService.getSysConfig());
                context.put("send_time", CommUtil.formatLongDate(new Date()));
                context.put("webPath", CommUtil.getURL(request));
                context.put("order", order);
                StringWriter writer = new StringWriter();
                blank.merge(context, writer);

                String content = writer.toString();
                this.msgTools.sendEmail(email, subject, content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void send_order_sms(HttpServletRequest request, OrderForm order, String mobile,
                                String mark) {
        try {

            com.javamalls.platform.domain.Template template = this.templateService
                .getObjByProperty("mark", mark);
            if ((template != null) && (template.isOpen())) {
                /* String path = request.getSession().getServletContext().getRealPath("/") + "/vm/";
                 PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
                     path + "msg.vm", false), "UTF-8"));
                 pwrite.print(template.getContent());
                 pwrite.flush();
                 pwrite.close();

                 Properties p = new Properties();
                 p.setProperty("file.resource.loader.path", request.getRealPath("/") + "vm"
                                                            + File.separator);
                 p.setProperty("input.encoding", "UTF-8");
                 p.setProperty("output.encoding", "UTF-8");
                 Velocity.init(p);
                 org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm", "UTF-8");
                 VelocityContext context = new VelocityContext();
                 context.put("buyer", order.getUser());
                 context.put("seller", order.getStore().getUser());
                 context.put("config", this.configService.getSysConfig());
                 context.put("send_time", CommUtil.formatLongDate(new Date()));
                 context.put("webPath", CommUtil.getURL(request));
                 context.put("order", order);
                 StringWriter writer = new StringWriter();
                 blank.merge(context, writer);

                 String content = writer.toString();*/

                Map<String, String> map = new HashMap<String, String>();
                Store store = order.getStore();

                String userName = order.getUser().getUserName();
                if (order.getUser().getTrueName() != null
                    && !"".equals(order.getUser().getTrueName())) {
                    userName = order.getUser().getTrueName();
                }
                map.put("buyerName", userName);
                map.put("order_id", order.getOrder_id());
                map.put("paymentName", order.getPayment().getName());
                if (template.getMark().contains("tobuyer")) {
                    map.put("storeName", store.getStore_name());
                } else {
                    map.put("sellerName", store.getUser().getUserName());
                }
                this.msgTools.sendSMS(mobile, template.getTitle(), map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String paseText(String strString) {
        if (strString.endsWith(","))
            strString = strString.substring(0, strString.length() - 1);
        Document document = null;
        try {
            byte[] xmlString = strString.getBytes("UTF-8");
            InputStream is = new ByteArrayInputStream(xmlString);
            SAXReader sax = new SAXReader(false);
            document = sax.read(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String order_no = "";
        Element rootElement = document.getRootElement();
        if (null != rootElement) {
            Iterator iter = rootElement.elementIterator("TRADE");
            while (iter.hasNext()) {

                Element recordEle = (Element) iter.next();
                order_no = recordEle.elementTextTrim("ID");
            }
        }
        return order_no;
    }
}
