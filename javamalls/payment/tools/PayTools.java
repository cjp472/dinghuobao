package com.javamalls.payment.tools;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.javamalls.base.constant.Globals;
import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.Md5Encrypt;
import com.javamalls.front.web.h5.action.Constent;
import com.javamalls.payment.alipay.config.AlipayConfig;
import com.javamalls.payment.alipay.services.AlipayService;
import com.javamalls.payment.alipay.util.AlipaySubmit;
import com.javamalls.payment.alipay.util.UtilDate;
import com.javamalls.payment.bill.config.BillConfig;
import com.javamalls.payment.bill.services.BillService;
import com.javamalls.payment.bill.util.BillCore;
import com.javamalls.payment.bill.util.MD5Util;
import com.javamalls.payment.chinabank.h5.util.PaySignEntity;
import com.javamalls.payment.chinabank.h5.util.SignUtil;
import com.javamalls.payment.chinabank.util.ChinaBankSubmit;
import com.javamalls.payment.paypal.PaypalTools;
import com.javamalls.payment.weixin.tools.WeixinPayTools;
import com.javamalls.platform.domain.GoldRecord;
import com.javamalls.platform.domain.IntegralGoodsOrder;
import com.javamalls.platform.domain.OrderForm;
import com.javamalls.platform.domain.Payment;
import com.javamalls.platform.domain.Predeposit;
import com.javamalls.platform.domain.SysConfig;
import com.javamalls.platform.service.IGoldRecordService;
import com.javamalls.platform.service.IIntegralGoodsOrderService;
import com.javamalls.platform.service.IOrderFormService;
import com.javamalls.platform.service.IPaymentService;
import com.javamalls.platform.service.IPredepositService;
import com.javamalls.platform.service.ISysConfigService;

/**支付
 *                       
 * @Filename: PayTools.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Component
public class PayTools {

    private static final Logger        logger = Logger.getLogger(PayTools.class);
    @Autowired
    private IPaymentService            paymentService;
    @Autowired
    private IOrderFormService          orderFormService;
    @Autowired
    private IPredepositService         predepositService;
    @Autowired
    private IGoldRecordService         goldRecordService;
    @Autowired
    private IIntegralGoodsOrderService integralGoodsOrderService;
    @Autowired
    private ISysConfigService          configService;
    @Autowired
    private WeixinPayTools			   weixinPayTools;

    public String genericAlipay(String url, String payment_id, String type, String id) {
        String result = "";
        OrderForm of = null;
        if (type.equals("goods")) {
            of = this.orderFormService.getObjById(CommUtil.null2Long(id));
        }

        Payment payment = this.paymentService.getObjById(CommUtil.null2Long(payment_id));
        if (payment == null) {
            payment = new Payment();
        }
        int interfaceType = payment.getInterfaceType();
        AlipayConfig config = new AlipayConfig();
        Map<String, String> params = new HashMap<String, String>();
        params.put("type", "admin");
        params.put("mark", "alipay");
        List<Payment> payments = this.paymentService.query(
            "select obj from Payment obj where obj.type=:type and obj.mark=:mark", params, -1, -1);
        Payment shop_payment = new Payment();
        if (payments.size() > 0) {
            shop_payment = (Payment) payments.get(0);
        }
        if ((!CommUtil.null2String(payment.getSafeKey()).equals(""))
            && (!CommUtil.null2String(payment.getPartner()).equals(""))) {
            config.setKey(payment.getSafeKey());
            config.setPartner(payment.getPartner());
        } else {
            config.setKey(shop_payment.getSafeKey());
            config.setPartner(shop_payment.getPartner());
        }
        config.setSeller_email(payment.getSeller_email());
        config.setNotify_url(url + "/alipay_notify.htm");
        config.setReturn_url(url + "/aplipay_return.htm");

        SysConfig sys_config = this.configService.getSysConfig();
        if (sys_config.getAlipay_fenrun() == 1) {
            interfaceType = 0;
        }
        if (interfaceType == 0) {
            if (sys_config.getAlipay_fenrun() == 1) {
                config.setKey(shop_payment.getSafeKey());
                config.setPartner(shop_payment.getPartner());
                config.setSeller_email(shop_payment.getSeller_email());
            }
            String out_trade_no = "";
            String subject = "";
            String body = type;
            String total_fee = "";
            if (type.equals("goods")) {
                out_trade_no = of.getId().toString();
                subject = of.getOrder_id();
                total_fee = CommUtil.null2String(of.getTotalPrice());
            }

            String paymethod = "";
            String defaultbank = "";
            String anti_phishing_key = "";
            String exter_invoke_ip = "";
            String extra_common_param = type;
            String buyer_email = "";
            String show_url = "";
            String royalty_type = "10";

            String royalty_parameters = "";
            if ((type.equals("goods")) && (sys_config.getAlipay_fenrun() == 1)) {
                double fenrun_rate = CommUtil.null2Double(shop_payment.getAlipay_divide_rate());
                double apliay_rate = CommUtil.null2Double(shop_payment.getAlipay_rate()) / 100.0D;
                double shop_fee = CommUtil.null2Double(total_fee) * (1.0D - apliay_rate);
                shop_fee *= fenrun_rate;
                double seller_fee = CommUtil.null2Double(total_fee) * (1.0D - apliay_rate)
                                    - shop_fee;
                royalty_parameters = payment.getSeller_email()
                                     + "^"
                                     + String.format("%.2f",
                                         new Object[] { Double.valueOf(seller_fee) }) + "^商家";
            }
            Map<String, String> sParaTemp = new HashMap<String, String>();
            sParaTemp.put("payment_type", "1");
            sParaTemp.put("out_trade_no", out_trade_no);
            sParaTemp.put("subject", subject);
            sParaTemp.put("body", body);
            sParaTemp.put("total_fee", total_fee);
            sParaTemp.put("show_url", show_url);
            sParaTemp.put("paymethod", paymethod);
            sParaTemp.put("defaultbank", defaultbank);
            sParaTemp.put("anti_phishing_key", anti_phishing_key);
            sParaTemp.put("exter_invoke_ip", exter_invoke_ip);
            sParaTemp.put("extra_common_param", extra_common_param);
            sParaTemp.put("buyer_email", buyer_email);
            if ((type.equals("goods")) && (sys_config.getAlipay_fenrun() == 1)) {
                sParaTemp.put("royalty_type", royalty_type);
                sParaTemp.put("royalty_parameters", royalty_parameters);
            }

            result = AlipayService.create_direct_pay_by_user(config, sParaTemp);
        }
        if (interfaceType == 1) {
            String out_trade_no = "";
            String subject = "";
            String body = type;
            String total_fee = "";
            if (type.equals("goods")) {
                out_trade_no = of.getId().toString();
                subject = of.getOrder_id();
                total_fee = CommUtil.null2String(of.getTotalPrice());
            }

            String price = String.valueOf(total_fee);
            String logistics_fee = "0.00";
            String logistics_type = "EXPRESS";
            String logistics_payment = "SELLER_PAY";
            String quantity = "1";
            String extra_common_param = "";
            String receive_name = "";
            String receive_address = "";
            String receive_zip = "";
            String receive_phone = "";
            String receive_mobile = "";

            String show_url = "";

            Map<String, String> sParaTemp = new HashMap<String, String>();
            sParaTemp.put("payment_type", "1");
            sParaTemp.put("show_url", show_url);
            sParaTemp.put("out_trade_no", out_trade_no);
            sParaTemp.put("subject", subject);
            sParaTemp.put("body", body);
            sParaTemp.put("price", price);
            sParaTemp.put("logistics_fee", logistics_fee);
            sParaTemp.put("logistics_type", logistics_type);
            sParaTemp.put("logistics_payment", logistics_payment);
            sParaTemp.put("quantity", quantity);
            sParaTemp.put("extra_common_param", extra_common_param);
            sParaTemp.put("receive_name", receive_name);
            sParaTemp.put("receive_address", receive_address);
            sParaTemp.put("receive_zip", receive_zip);
            sParaTemp.put("receive_phone", receive_phone);
            sParaTemp.put("receive_mobile", receive_mobile);

            result = AlipayService.create_partner_trade_by_buyer(config, sParaTemp);
        }
        if (interfaceType == 2) {
            String subject = "";
            String out_trade_no = "";
            String body = type;
            String total_fee = "";
            if (type.equals("goods")) {
                out_trade_no = of.getId().toString();
                subject = of.getOrder_id();
                total_fee = CommUtil.null2String(of.getTotalPrice());
            }

            String price = String.valueOf(total_fee);
            String logistics_fee = "0.00";
            String logistics_type = "EXPRESS";
            String logistics_payment = "SELLER_PAY";
            String quantity = "1";
            String extra_common_param = "";
            String receive_name = "";
            String receive_address = "";
            String receive_zip = "";
            String receive_phone = "";
            String receive_mobile = "";

            String show_url = "";

            Map<String, String> sParaTemp = new HashMap<String, String>();
            sParaTemp.put("payment_type", "1");
            sParaTemp.put("show_url", show_url);
            sParaTemp.put("out_trade_no", out_trade_no);
            sParaTemp.put("subject", subject);
            sParaTemp.put("body", body);
            sParaTemp.put("price", price);
            sParaTemp.put("logistics_fee", logistics_fee);
            sParaTemp.put("logistics_type", logistics_type);
            sParaTemp.put("logistics_payment", logistics_payment);
            sParaTemp.put("quantity", quantity);
            sParaTemp.put("extra_common_param", extra_common_param);
            sParaTemp.put("receive_name", receive_name);
            sParaTemp.put("receive_address", receive_address);
            sParaTemp.put("receive_zip", receive_zip);
            sParaTemp.put("receive_phone", receive_phone);
            sParaTemp.put("receive_mobile", receive_mobile);

            result = AlipayService.trade_create_by_buyer(config, sParaTemp);
        }

        logger.info("PC端支付宝支付，组装请求数据：" + result);
        return result;
    }

    public String generic99Bill(String url, String payment_id, String type, String id)
                                                                                      throws UnsupportedEncodingException {
        String result = "";
        OrderForm of = null;
        Predeposit obj = null;
        GoldRecord gold = null;
        IntegralGoodsOrder ig_order = null;
        if (type.equals("goods")) {
            of = this.orderFormService.getObjById(CommUtil.null2Long(id));
        }
        if (type.equals("cash")) {
            obj = this.predepositService.getObjById(CommUtil.null2Long(id));
        }
        if (type.equals("gold")) {
            gold = this.goldRecordService.getObjById(CommUtil.null2Long(id));
        }
        if (type.equals("integral")) {
            ig_order = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(id));
        }
        Payment payment = this.paymentService.getObjById(CommUtil.null2Long(payment_id));
        if (payment == null) {
            payment = new Payment();
        }
        BillConfig config = new BillConfig(payment.getMerchantAcctId(), payment.getRmbKey(),
            payment.getPid());

        String merchantAcctId = config.getMerchantAcctId();
        String key = config.getKey();
        String inputCharset = "1";
        String bgUrl = url + "/bill_return.htm";
        String version = "v2.0";
        String language = "1";
        String signType = "1";

        String payerName = SecurityUserHolder.getCurrentUser().getUserName();

        String payerContactType = "1";

        String payerContact = "";

        String orderId = "";
        if (type.equals("goods")) {
            orderId = of.getOrder_id();
        }
        if (type.equals("cash")) {
            orderId = obj.getPd_sn();
        }
        if (type.equals("gold")) {
            orderId = gold.getGold_sn();
        }
        if (type.equals("integral")) {
            orderId = ig_order.getIgo_order_sn();
        }
        String orderAmount = "";
        if (type.equals("goods")) {
            orderAmount = String
                .valueOf((int) Math.floor(CommUtil.null2Double(of.getTotalPrice()) * 100.0D));
        }
        if (type.equals("cash")) {
            orderAmount = String
                .valueOf((int) Math.floor(CommUtil.null2Double(obj.getPd_amount()) * 100.0D));
        }
        if (type.equals("gold")) {
            orderAmount = String.valueOf((int) Math.floor(CommUtil.null2Double(Integer.valueOf(gold
                .getGold_money())) * 100.0D));
        }
        if (type.equals("integral")) {
            orderAmount = String.valueOf((int) Math.floor(CommUtil.null2Double(ig_order
                .getIgo_trans_fee()) * 100.0D));
        }
        String orderTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        String productName = "";
        if (type.equals("goods")) {
            productName = of.getOrder_id();
        }
        if (type.equals("cash")) {
            productName = obj.getPd_sn();
        }
        if (type.equals("gold")) {
            productName = gold.getGold_sn();
        }
        if (type.equals("integral")) {
            productName = ig_order.getIgo_order_sn();
        }
        String productNum = "1";

        String productId = "";

        String productDesc = "";

        String ext1 = "";
        if (type.equals("goods")) {
            ext1 = of.getId().toString();
        }
        if (type.equals("cash")) {
            ext1 = obj.getId().toString();
        }
        if (type.equals("gold")) {
            ext1 = gold.getId().toString();
        }
        if (type.equals("integral")) {
            ext1 = ig_order.getId().toString();
        }
        String ext2 = type;

        String payType = "00";

        String redoFlag = "0";

        String pid = "";
        if (config.getPid() != null) {
            pid = config.getPid();
        }
        String signMsgVal = "";
        signMsgVal = BillCore.appendParam(signMsgVal, "inputCharset", inputCharset);
        signMsgVal = BillCore.appendParam(signMsgVal, "bgUrl", bgUrl);
        signMsgVal = BillCore.appendParam(signMsgVal, "version", version);
        signMsgVal = BillCore.appendParam(signMsgVal, "language", language);
        signMsgVal = BillCore.appendParam(signMsgVal, "signType", signType);
        signMsgVal = BillCore.appendParam(signMsgVal, "merchantAcctId", merchantAcctId);
        signMsgVal = BillCore.appendParam(signMsgVal, "payerName", payerName);
        signMsgVal = BillCore.appendParam(signMsgVal, "payerContactType", payerContactType);
        signMsgVal = BillCore.appendParam(signMsgVal, "payerContact", payerContact);
        signMsgVal = BillCore.appendParam(signMsgVal, "orderId", orderId);
        signMsgVal = BillCore.appendParam(signMsgVal, "orderAmount", orderAmount);
        signMsgVal = BillCore.appendParam(signMsgVal, "orderTime", orderTime);
        signMsgVal = BillCore.appendParam(signMsgVal, "productName", productName);
        signMsgVal = BillCore.appendParam(signMsgVal, "productNum", productNum);
        signMsgVal = BillCore.appendParam(signMsgVal, "productId", productId);
        signMsgVal = BillCore.appendParam(signMsgVal, "productDesc", productDesc);
        signMsgVal = BillCore.appendParam(signMsgVal, "ext1", ext1);
        signMsgVal = BillCore.appendParam(signMsgVal, "ext2", ext2);
        signMsgVal = BillCore.appendParam(signMsgVal, "payType", payType);
        signMsgVal = BillCore.appendParam(signMsgVal, "redoFlag", redoFlag);
        signMsgVal = BillCore.appendParam(signMsgVal, "pid", pid);
        signMsgVal = BillCore.appendParam(signMsgVal, "key", key);

        String signMsg = MD5Util.md5Hex(signMsgVal.getBytes("UTF-8")).toUpperCase();

        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("inputCharset", inputCharset);
        sParaTemp.put("bgUrl", bgUrl);
        sParaTemp.put("version", version);
        sParaTemp.put("language", language);
        sParaTemp.put("signType", signType);
        sParaTemp.put("signMsg", signMsg);
        sParaTemp.put("merchantAcctId", merchantAcctId);
        sParaTemp.put("payerName", payerName);
        sParaTemp.put("payerContactType", payerContactType);
        sParaTemp.put("payerContact", payerContact);
        sParaTemp.put("orderId", orderId);
        sParaTemp.put("orderAmount", orderAmount);
        sParaTemp.put("orderTime", orderTime);
        sParaTemp.put("productName", productName);
        sParaTemp.put("productNum", productNum);
        sParaTemp.put("productId", productId);
        sParaTemp.put("productDesc", productDesc);
        sParaTemp.put("ext1", ext1);
        sParaTemp.put("ext2", ext2);
        sParaTemp.put("payType", payType);
        sParaTemp.put("redoFlag", redoFlag);
        sParaTemp.put("pid", pid);
        result = BillService.buildForm(config, sParaTemp, "post", "确定");
        return result;
    }

    public String genericChinaBank(String url, String payment_id, String type, String id) {
        OrderForm of = null;
        Predeposit obj = null;
        GoldRecord gold = null;
        IntegralGoodsOrder ig_order = null;
        String v_oid = "";
        String v_amount = "";
        String remark1 = "";
        if (type.equals("goods")) {
            of = this.orderFormService.getObjById(CommUtil.null2Long(id));
            v_oid = of.getOrder_id();
            v_amount = CommUtil.null2String(of.getTotalPrice());
            remark1 = of.getId().toString() + "_" + type;
        }
        if (type.equals("cash")) {
            obj = this.predepositService.getObjById(CommUtil.null2Long(id));
            v_oid = obj.getPd_sn();
            v_amount = CommUtil.null2String(obj.getPd_amount());
            remark1 = obj.getId().toString() + "_" + type;
        }
        if (type.equals("gold")) {
            gold = this.goldRecordService.getObjById(CommUtil.null2Long(id));
            v_oid = gold.getGold_sn();
            v_amount = CommUtil.null2String(Integer.valueOf(gold.getGold_money()));
            remark1 = gold.getId().toString() + "_" + type;
        }
        if (type.equals("integral")) {
            ig_order = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(id));
            v_oid = ig_order.getIgo_order_sn();
            v_amount = CommUtil.null2String(ig_order.getIgo_trans_fee());
            remark1 = ig_order.getId().toString() + "_" + type;
        }
        Payment payment = this.paymentService.getObjById(CommUtil.null2Long(payment_id));
        if (payment == null) {
            payment = new Payment();
        }
        List<SysMap> list = new ArrayList<SysMap>();
        String v_mid = payment.getChinabank_account();
        String key = payment.getChinabank_key();
        String v_url = url + "/chinabank_return.htm";
        String v_moneytype = "CNY";
        String temp = v_amount + v_moneytype + v_oid + v_mid + v_url + key;
        String v_md5info = Md5Encrypt.md5(temp).toUpperCase();
        String remark2 = "[url:=" + url + "/chinabank_notify.htm]";
        list.add(new SysMap("v_mid", v_mid));
        list.add(new SysMap("key", key));
        list.add(new SysMap("v_url", v_url));
        list.add(new SysMap("v_oid", v_oid));
        list.add(new SysMap("v_amount", v_amount));
        list.add(new SysMap("v_moneytype", v_moneytype));
        list.add(new SysMap("v_md5info", v_md5info));
        list.add(new SysMap("remark1", remark1));
        list.add(new SysMap("remark2", remark2));
        String ret = ChinaBankSubmit.buildForm(list, null);
        return ret;
    }

    public String genericPaypal(String url, String payment_id, String type, String id) {
        OrderForm of = null;
        Predeposit obj = null;
        GoldRecord gold = null;
        IntegralGoodsOrder ig_order = null;
        if (type.equals("goods")) {
            of = this.orderFormService.getObjById(CommUtil.null2Long(id));
        }
        if (type.equals("cash")) {
            obj = this.predepositService.getObjById(CommUtil.null2Long(id));
        }
        if (type.equals("gold")) {
            gold = this.goldRecordService.getObjById(CommUtil.null2Long(id));
        }
        if (type.equals("integral")) {
            ig_order = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(id));
        }
        Payment payment = this.paymentService.getObjById(CommUtil.null2Long(payment_id));
        if (payment == null) {
            payment = new Payment();
        }
        List<SysMap> sms = new ArrayList<SysMap>();
        String business = payment.getPaypal_userId();
        sms.add(new SysMap("business", business));
        String return_url = url + "/paypal_return.htm";
        String notify_url = url + "/paypal_return.htm";
        sms.add(new SysMap("return", return_url));
        String item_name = "";
        if (type.equals("goods")) {
            item_name = of.getOrder_id();
        }
        if (type.equals("cash")) {
            item_name = obj.getPd_sn();
        }
        if (type.equals("gold")) {
            item_name = gold.getGold_sn();
        }
        if (type.equals("integral")) {
            item_name = ig_order.getIgo_order_sn();
        }
        sms.add(new SysMap("item_name", item_name));
        String amount = "";
        String item_number = "";
        if (type.equals("goods")) {
            amount = CommUtil.null2String(of.getTotalPrice());
            item_number = of.getOrder_id();
        }
        if (type.equals("cash")) {
            amount = CommUtil.null2String(obj.getPd_amount());
            item_number = obj.getPd_sn();
        }
        if (type.equals("gold")) {
            amount = CommUtil.null2String(Integer.valueOf(gold.getGold_money()));
            item_number = gold.getGold_sn();
        }
        if (type.equals("integral")) {
            amount = CommUtil.null2String(ig_order.getIgo_trans_fee());
            item_number = ig_order.getIgo_order_sn();
        }
        sms.add(new SysMap("amount", amount));
        sms.add(new SysMap("notify_url", notify_url));
        sms.add(new SysMap("cmd", "_xclick"));
        sms.add(new SysMap("currency_code", payment.getCurrency_code()));
        sms.add(new SysMap("item_number", item_number));

        String custom = "";
        if (type.equals("goods")) {
            custom = of.getId().toString();
        }
        if (type.equals("cash")) {
            custom = obj.getId().toString();
        }
        if (type.equals("gold")) {
            custom = gold.getId().toString();
        }
        if (type.equals("integral")) {
            custom = ig_order.getId().toString();
        }
        custom = custom + "," + type;
        sms.add(new SysMap("custom", custom));
        String ret = PaypalTools.buildForm(sms);
        return ret;
    }

    public String genericAlipayWap(String url, String payment_id, String type, String id)
                                                                                         throws Exception {
        String result = "";
        OrderForm of = null;

        Payment payment = this.paymentService.getObjById(CommUtil.null2Long(payment_id));
        if (payment == null) {
            payment = new Payment();
        }
        //        int interfaceType = payment.getInterfaceType();
        AlipayConfig config = new AlipayConfig();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("type", "admin");
        params.put("mark", "alipay_wap");
        List<Payment> payments = this.paymentService.query(
            "select obj from Payment obj where obj.type=:type and obj.mark=:mark", params, -1, -1);
        Payment shop_payment = new Payment();
        if (payments.size() > 0) {
            shop_payment = (Payment) payments.get(0);
        }
        if ((!CommUtil.null2String(payment.getSafeKey()).equals(""))
            && (!CommUtil.null2String(payment.getPartner()).equals(""))) {
            config.setKey(payment.getSafeKey());
            config.setPartner(payment.getPartner());
        } else {
            config.setKey(shop_payment.getSafeKey());
            config.setPartner(shop_payment.getPartner());
        }
        config.setSeller_email(payment.getSeller_email());

        String format = "xml";

        String v = "2.0";

        String req_id = UtilDate.getOrderNum();
        //异步通知页面路径
        String notify_url = url + "/mobile/alipay_notify.htm";
        config.setNotify_url(notify_url);
        //支付成功中转路径
        String call_back_url = url + "/mobile/alipay_return.htm";
        config.setReturn_url(call_back_url);
        //支付中断
        String merchant_url = url + "/mobile/index.htm";

        String seller_email = payment.getSeller_email();

        String out_trade_no = "";
        String subject = Globals.DEFAULT_WBESITE_NAME;
        String total_fee = "";
        String trade_no = "";
        if (type.equals("goods")) {
            of = this.orderFormService.getObjById(CommUtil.null2Long(id));
            out_trade_no = of.getId().toString();
            trade_no = of.getOrder_id();
            subject += "订单号为：" + trade_no;
            total_fee = CommUtil.null2String(of.getTotalPrice());
        }

        //该值必须保证唯一 ，原来是订单'ID_goods' 因为开发和测试环境如果有id相同的订单已经支付过,会重复导致无法交易。
        String key = out_trade_no + "_" + type + "_" + trade_no;
        out_trade_no = key;
        Constent.PAY_ORDER_TYPE.put(key, type);

        String req_dataToken = "<direct_trade_create_req><notify_url>" + notify_url
                               + "</notify_url><call_back_url>" + call_back_url
                               + "</call_back_url><seller_account_name>" + seller_email
                               + "</seller_account_name><out_trade_no>" + out_trade_no
                               + "</out_trade_no><subject>" + subject + "</subject><total_fee>"
                               + total_fee + "</total_fee><merchant_url>" + merchant_url
                               + "</merchant_url><pay_body>" + type + "</pay_body><trade_no>"
                               + trade_no + "</trade_no></direct_trade_create_req>";

        Map<String, String> sParaTempToken = new HashMap<String, String>();
        sParaTempToken.put("service", "alipay.wap.trade.create.direct");
        sParaTempToken.put("partner", config.getPartner());
        sParaTempToken.put("_input_charset", config.getInput_charset());
        sParaTempToken.put("sec_id", config.getSign_type());
        sParaTempToken.put("format", format);
        sParaTempToken.put("v", v);
        sParaTempToken.put("req_id", req_id);
        sParaTempToken.put("req_data", req_dataToken);

        logger.info("支付宝提交参数：" + sParaTempToken);

        String sHtmlTextToken = AlipaySubmit.buildRequest(config, "wap", sParaTempToken, "", "");

        sHtmlTextToken = URLDecoder.decode(sHtmlTextToken, config.getInput_charset());

        String request_token = AlipaySubmit.getRequestToken(config, sHtmlTextToken);

        String req_data = "<auth_and_execute_req><request_token>" + request_token
                          + "</request_token></auth_and_execute_req>";

        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("service", "alipay.wap.auth.authAndExecute");
        sParaTemp.put("partner", config.getPartner());
        sParaTemp.put("_input_charset", config.getInput_charset());
        sParaTemp.put("sec_id", config.getSign_type());
        sParaTemp.put("format", format);
        sParaTemp.put("v", v);
        sParaTemp.put("req_data", req_data);
        Constent.PAY_ORDER_TYPE.put("req_data" + key, req_data);

        String WAP_ALIPAY_GATEWAY_NEW = "http://wappaygw.alipay.com/service/rest.htm?";
        result = AlipaySubmit.buildForm(config, sParaTemp, WAP_ALIPAY_GATEWAY_NEW, "get", "确认");
		
		logger.info("手机端支付宝支付，组装请求数据：" + result);
        return result;
    }

    /**
     * 微信下单
     * @param url
     * @param payment_id
     * @param type ：goods、cash、gold、integral
     * @param id：订单OrderForm的id
     * @return
     * @throws Exception
     */
    public String genericWeixinWap(String url, String payment_id, String type, String id)
            throws Exception {
    	String result = "";
    	OrderForm of = this.orderFormService.getObjById(CommUtil.null2Long(id));
    	Payment payment = this.paymentService.getObjById(CommUtil.null2Long(payment_id));
    	if(of==null){
    		return null;
    	}
        if (payment == null) {
           return null;
        }
        //
        of.setPayment(payment);
        this.orderFormService.update(of);
        //2,微信下单
        String redirect_uri = url+"/mobile/choiceDoWxPay?id=" + of.getId();
        result = this.weixinPayTools.weixinPayMobileUrl(of,payment, "商品订单", redirect_uri);
    	return result;
	}
    
    public String generic99BillWap(String url, String payment_id, String type, String id)
                                                                                         throws UnsupportedEncodingException {
        String result = "";
        OrderForm of = null;
        Predeposit obj = null;
        GoldRecord gold = null;
        IntegralGoodsOrder ig_order = null;
        if (type.equals("goods")) {
            of = this.orderFormService.getObjById(CommUtil.null2Long(id));
        }
        if (type.equals("cash")) {
            obj = this.predepositService.getObjById(CommUtil.null2Long(id));
        }
        if (type.equals("gold")) {
            gold = this.goldRecordService.getObjById(CommUtil.null2Long(id));
        }
        if (type.equals("integral")) {
            ig_order = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(id));
        }
        Payment payment = this.paymentService.getObjById(CommUtil.null2Long(payment_id));
        if (payment == null) {
            payment = new Payment();
        }
        BillConfig config = new BillConfig(payment.getMerchantAcctId(), payment.getRmbKey(),
            payment.getPid());

        String merchantAcctId = config.getMerchantAcctId();
        String key = config.getKey();
        String inputCharset = "1";
        String bgUrl = url + "/weixin/bill_return.htm";
        String version = "v2.0";
        String language = "1";
        String signType = "1";

        String payerName = SecurityUserHolder.getCurrentUser().getUserName();

        String payerContactType = "1";

        String payerContact = "";

        String orderId = "";
        if (type.equals("goods")) {
            orderId = of.getOrder_id();
        }
        if (type.equals("cash")) {
            orderId = obj.getPd_sn();
        }
        if (type.equals("gold")) {
            orderId = gold.getGold_sn();
        }
        if (type.equals("integral")) {
            orderId = ig_order.getIgo_order_sn();
        }
        String orderAmount = "";
        if (type.equals("goods")) {
            orderAmount = String
                .valueOf((int) Math.floor(CommUtil.null2Double(of.getTotalPrice()) * 100.0D));
        }
        if (type.equals("cash")) {
            orderAmount = String
                .valueOf((int) Math.floor(CommUtil.null2Double(obj.getPd_amount()) * 100.0D));
        }
        if (type.equals("gold")) {
            orderAmount = String.valueOf((int) Math.floor(CommUtil.null2Double(Integer.valueOf(gold
                .getGold_money())) * 100.0D));
        }
        if (type.equals("integral")) {
            orderAmount = String.valueOf((int) Math.floor(CommUtil.null2Double(ig_order
                .getIgo_trans_fee()) * 100.0D));
        }
        String orderTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        String productName = "";
        if (type.equals("goods")) {
            productName = of.getOrder_id();
        }
        if (type.equals("cash")) {
            productName = obj.getPd_sn();
        }
        if (type.equals("gold")) {
            productName = gold.getGold_sn();
        }
        if (type.equals("integral")) {
            productName = ig_order.getIgo_order_sn();
        }
        String productNum = "1";

        String productId = "";

        String productDesc = "";

        String ext1 = "";
        if (type.equals("goods")) {
            ext1 = of.getId().toString();
        }
        if (type.equals("cash")) {
            ext1 = obj.getId().toString();
        }
        if (type.equals("gold")) {
            ext1 = gold.getId().toString();
        }
        if (type.equals("integral")) {
            ext1 = ig_order.getId().toString();
        }
        String ext2 = type;

        String payType = "00";

        String redoFlag = "0";

        String pid = "";
        if (config.getPid() != null) {
            pid = config.getPid();
        }
        String signMsgVal = "";
        signMsgVal = BillCore.appendParam(signMsgVal, "inputCharset", inputCharset);
        signMsgVal = BillCore.appendParam(signMsgVal, "bgUrl", bgUrl);
        signMsgVal = BillCore.appendParam(signMsgVal, "version", version);
        signMsgVal = BillCore.appendParam(signMsgVal, "language", language);
        signMsgVal = BillCore.appendParam(signMsgVal, "signType", signType);
        signMsgVal = BillCore.appendParam(signMsgVal, "merchantAcctId", merchantAcctId);
        signMsgVal = BillCore.appendParam(signMsgVal, "payerName", payerName);
        signMsgVal = BillCore.appendParam(signMsgVal, "payerContactType", payerContactType);
        signMsgVal = BillCore.appendParam(signMsgVal, "payerContact", payerContact);
        signMsgVal = BillCore.appendParam(signMsgVal, "orderId", orderId);
        signMsgVal = BillCore.appendParam(signMsgVal, "orderAmount", orderAmount);
        signMsgVal = BillCore.appendParam(signMsgVal, "orderTime", orderTime);
        signMsgVal = BillCore.appendParam(signMsgVal, "productName", productName);
        signMsgVal = BillCore.appendParam(signMsgVal, "productNum", productNum);
        signMsgVal = BillCore.appendParam(signMsgVal, "productId", productId);
        signMsgVal = BillCore.appendParam(signMsgVal, "productDesc", productDesc);
        signMsgVal = BillCore.appendParam(signMsgVal, "ext1", ext1);
        signMsgVal = BillCore.appendParam(signMsgVal, "ext2", ext2);
        signMsgVal = BillCore.appendParam(signMsgVal, "payType", payType);
        signMsgVal = BillCore.appendParam(signMsgVal, "redoFlag", redoFlag);
        signMsgVal = BillCore.appendParam(signMsgVal, "pid", pid);
        signMsgVal = BillCore.appendParam(signMsgVal, "key", key);

        String signMsg = MD5Util.md5Hex(signMsgVal.getBytes("UTF-8")).toUpperCase();

        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("inputCharset", inputCharset);
        sParaTemp.put("bgUrl", bgUrl);
        sParaTemp.put("version", version);
        sParaTemp.put("language", language);
        sParaTemp.put("signType", signType);
        sParaTemp.put("signMsg", signMsg);
        sParaTemp.put("merchantAcctId", merchantAcctId);
        sParaTemp.put("payerName", payerName);
        sParaTemp.put("payerContactType", payerContactType);
        sParaTemp.put("payerContact", payerContact);
        sParaTemp.put("orderId", orderId);
        sParaTemp.put("orderAmount", orderAmount);
        sParaTemp.put("orderTime", orderTime);
        sParaTemp.put("productName", productName);
        sParaTemp.put("productNum", productNum);
        sParaTemp.put("productId", productId);
        sParaTemp.put("productDesc", productDesc);
        sParaTemp.put("ext1", ext1);
        sParaTemp.put("ext2", ext2);
        sParaTemp.put("payType", payType);
        sParaTemp.put("redoFlag", redoFlag);
        sParaTemp.put("pid", pid);
        result = BillService.buildForm(config, sParaTemp, "post", "确定");
        return result;
    }

    public String genericChinaBankWap(String url, String payment_id, String type, String id) {
        List<SysMap> list = new ArrayList<SysMap>();

        String serverUrl = "https://m.wangyin.com/wepay/web/pay";
        makeChinaBankWapAttr(list, url, payment_id, type, id);

        String ret = ChinaBankSubmit.buildForm(list, serverUrl);
        return ret;
    }

    public void makeChinaBankWapAttr(List<SysMap> list, String url, String payment_id, String type,
                                     String id) {
        try {
            OrderForm of = null;
            Predeposit obj = null;
            GoldRecord gold = null;
            IntegralGoodsOrder ig_order = null;
            Payment payment = this.paymentService.getObjById(CommUtil.null2Long(payment_id));
            if (payment == null) {
                throw new Exception("无交效的支付方式");
            }
            String tradeNum = "";
            String tradeName = Globals.DEFAULT_WBESITE_NAME;
            String tradeAmount = "0";
            if (type.equals("goods")) {
                of = this.orderFormService.getObjById(CommUtil.null2Long(id));
                tradeNum = of.getOrder_id() + "_" + of.getId();
                tradeName += " 购物";
                tradeAmount = CommUtil.null2String(of.getTotalPrice());
            }
            if (type.equals("cash")) {
                obj = this.predepositService.getObjById(CommUtil.null2Long(id));
                tradeNum = obj.getPd_sn() + "_" + obj.getId();
                tradeName += " 充值";
                tradeAmount = CommUtil.null2String(obj.getPd_amount());
            }
            if (type.equals("gold")) {
                gold = this.goldRecordService.getObjById(CommUtil.null2Long(id));
                tradeNum = gold.getGold_sn() + "_" + gold.getId();
                tradeName += " 购买金币";
                tradeAmount = CommUtil.null2String(Integer.valueOf(gold.getGold_money()));
            }
            if (type.equals("integral")) {
                ig_order = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(id));
                tradeNum = ig_order.getIgo_order_sn() + "_" + ig_order.getId();
                tradeName += " 积分对换";
                tradeAmount = CommUtil.null2String(ig_order.getIgo_trans_fee());
            }
            tradeNum += "_" + type;
            //单位是分 必须转换成分否则签名错误
            tradeAmount = new BigDecimal(tradeAmount).multiply(new BigDecimal(100)).intValue() + "";
            String return_url = url + "/mobile/chinabank_return.htm";
            String fail_url = url + "/mobile/chinabank_fail.htm";
            String notify_url = url + "/mobile/chinabank_notify.htm";
            String tradeTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String merchantNum = payment.getChinabank_account();
            String rsaPrivateKey = payment.getChinabank_rsa_key();
            //商户支付请求/交易查询/退款 RSA加密私钥(由商户生成,并将对应的公钥发给网银)
            //            String rsaPrivateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALXf6twUqul1TATO+5nA66p2wjnRd+g96IXpfV6Sf8WXxwizGj+L19LQYRBXpZHmRh82prJ48d0FcHboCiN8pKutnuZrrKYhvORysOc5bVli0hcCn1TfYDoUWJ1UhjUQloqZKWjUz6LV9QY6bIZ1W4+Hmw6HK1bfFwUq0WzIGkJNAgMBAAECgYBlIFQeev9tP+M86TnMjBB9f/sO2wGpCIM5slIbO6n/3By3IZ7+pmsitOrDg3h0X22t/V1C7yzMkDGwa+T3Rl7ogwc4UNVj0ZQorOTx3OEPx3nP1yT3zmJ9djKaHKAmee4XmhQHdqqIuMT2XQaqatBzcsnP+Jnw/WVOsIJIqMeFAQJBAP9yq4hE+UfM/YSXZ5JR33k9RolUUq8S/elmeJIDo/3N2qDmzLjOr9iEZHxioc8JOxubtZ0BxA+NdfKz4v0BSpkCQQC2RIrAPRj9vOk6GfT9W1hbJ4GdnzTb+4vp3RDQQ3x9JGXzWFlg8xJT1rNgM8R95Gkxn3KGnYHJQTLlCsIy2FnVAkAWXolM3pVhxz6wHL4SHx9Ns6L4payz7hrUFIgcaTs0H5G0o2FsEZVuhXFzPwPiaHGHomQOAriTkBSzEzOeaj2JAkEAtYUFefZfETQ2QbrgFgIGuKFboJKRnhOif8G9oOvU6vx43CS8vqTVN9G2yrRDl+0GJnlZIV9zhe78tMZGKUT2EQJAHQawBKGlXlMe49Fo24yOy5DvKeohobjYqzJAtbqaAH7iIQTpOZx91zUcL/yG4dWS6r+wGO7Z1RKpupOJLKG3lA==";
            //获取商户 DESkey
            //        String desKey = "ta4E/aspLA3lgFGKmNDNRYU92RkZ4w2t";

            PaySignEntity wePayMerchantSignReqDTO = new PaySignEntity();
            wePayMerchantSignReqDTO.setVersion("1.0");
            wePayMerchantSignReqDTO.setToken(null);
            wePayMerchantSignReqDTO.setMerchantNum(merchantNum);
            wePayMerchantSignReqDTO.setTradeNum(tradeNum);
            wePayMerchantSignReqDTO.setTradeTime(tradeTime);
            wePayMerchantSignReqDTO.setTradeName(tradeName);
            wePayMerchantSignReqDTO.setCurrency("CNY");
            wePayMerchantSignReqDTO.setMerchantRemark(id);
            wePayMerchantSignReqDTO.setTradeAmount(tradeAmount);
            //这个参数用来区分订单类型请匆修改参数值
            wePayMerchantSignReqDTO.setTradeDescription(type);
            wePayMerchantSignReqDTO.setSuccessCallbackUrl(return_url);
            wePayMerchantSignReqDTO.setFailCallbackUrl(fail_url);
            wePayMerchantSignReqDTO.setNotifyUrl(notify_url);

            //            Constent.CHINA_BANK_PAY.put(tradeNum, wePayMerchantSignReqDTO);
            String signStr = SignUtil.sign(wePayMerchantSignReqDTO, rsaPrivateKey);

            list.add(new SysMap("version", "1.0"));
            list.add(new SysMap("token", null));
            list.add(new SysMap("merchantSign", signStr));
            list.add(new SysMap("merchantNum", merchantNum));
            list.add(new SysMap("merchantRemark", id));
            list.add(new SysMap("tradeNum", tradeNum));
            list.add(new SysMap("tradeName", tradeName));
            list.add(new SysMap("tradeDescription", type));
            list.add(new SysMap("tradeTime", tradeTime));
            list.add(new SysMap("tradeAmount", tradeAmount));
            list.add(new SysMap("currency", "CNY"));
            list.add(new SysMap("notifyUrl", notify_url));
            list.add(new SysMap("successCallbackUrl", return_url));
            list.add(new SysMap("failCallbackUrl", fail_url));

            /*User user = SecurityUserHolder.getCurrentUser();
            Constent.PAY_ORDER_TYPE.put("type" + user.getId(), type);*/

            /*list.add(new SysMap("version", "2.0"));
            list.add(new SysMap("token", null));
            list.add(new SysMap("merchantSign", signStr));
            list.add(new SysMap("merchantNum", merchantNum));
            list.add(new SysMap("merchantRemark", null));
            list.add(new SysMap("tradeNum", DESUtil.encrypt(tradeNum, desKey, "UTF-8")));
            list.add(new SysMap("tradeName", DESUtil.encrypt(tradeName, desKey, "UTF-8")));
            list.add(new SysMap("tradeDescription", null));
            list.add(new SysMap("tradeTime", DESUtil.encrypt(tradeTime, desKey, "UTF-8")));
            list.add(new SysMap("tradeAmount", DESUtil.encrypt(tradeAmount, desKey, "UTF-8")));
            list.add(new SysMap("currency", DESUtil.encrypt("CNY", desKey, "UTF-8")));
            list.add(new SysMap("notifyUrl", DESUtil.encrypt(notify_url, desKey, "UTF-8")));
            list.add(new SysMap("successCallbackUrl", DESUtil.encrypt(return_url, desKey, "UTF-8")));
            list.add(new SysMap("failCallbackUrl", DESUtil.encrypt(fail_url, desKey, "UTF-8")));
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String genericPaypalWap(String url, String payment_id, String type, String id) {
        OrderForm of = null;
        Predeposit obj = null;
        GoldRecord gold = null;
        IntegralGoodsOrder ig_order = null;
        if (type.equals("goods")) {
            of = this.orderFormService.getObjById(CommUtil.null2Long(id));
        }
        if (type.equals("cash")) {
            obj = this.predepositService.getObjById(CommUtil.null2Long(id));
        }
        if (type.equals("gold")) {
            gold = this.goldRecordService.getObjById(CommUtil.null2Long(id));
        }
        if (type.equals("integral")) {
            ig_order = this.integralGoodsOrderService.getObjById(CommUtil.null2Long(id));
        }
        Payment payment = this.paymentService.getObjById(CommUtil.null2Long(payment_id));
        if (payment == null) {
            payment = new Payment();
        }
        List<SysMap> sms = new ArrayList<SysMap>();
        String business = payment.getPaypal_userId();
        sms.add(new SysMap("business", business));
        String return_url = url + "/weixin/paypal_return.htm";
        String notify_url = url + "/weixin/paypal_return.htm";
        sms.add(new SysMap("return", return_url));
        String item_name = "";
        if (type.equals("goods")) {
            item_name = of.getOrder_id();
        }
        if (type.equals("cash")) {
            item_name = obj.getPd_sn();
        }
        if (type.equals("gold")) {
            item_name = gold.getGold_sn();
        }
        if (type.equals("integral")) {
            item_name = ig_order.getIgo_order_sn();
        }
        sms.add(new SysMap("item_name", item_name));
        String amount = "";
        String item_number = "";
        if (type.equals("goods")) {
            amount = CommUtil.null2String(of.getTotalPrice());
            item_number = of.getOrder_id();
        }
        if (type.equals("cash")) {
            amount = CommUtil.null2String(obj.getPd_amount());
            item_number = obj.getPd_sn();
        }
        if (type.equals("gold")) {
            amount = CommUtil.null2String(Integer.valueOf(gold.getGold_money()));
            item_number = gold.getGold_sn();
        }
        if (type.equals("integral")) {
            amount = CommUtil.null2String(ig_order.getIgo_trans_fee());
            item_number = ig_order.getIgo_order_sn();
        }
        sms.add(new SysMap("amount", amount));
        sms.add(new SysMap("notify_url", notify_url));
        sms.add(new SysMap("cmd", "_xclick"));
        sms.add(new SysMap("currency_code", payment.getCurrency_code()));
        sms.add(new SysMap("item_number", item_number));

        String custom = "";
        if (type.equals("goods")) {
            custom = of.getId().toString();
        }
        if (type.equals("cash")) {
            custom = obj.getId().toString();
        }
        if (type.equals("gold")) {
            custom = gold.getId().toString();
        }
        if (type.equals("integral")) {
            custom = ig_order.getId().toString();
        }
        custom = custom + "," + type;
        sms.add(new SysMap("custom", custom));
        String ret = PaypalTools.buildForm(sms);
        return ret;
    }
}
