package com.javamalls.platform.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import shaded.com.google.gson.JsonObject;
import shaded.org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.javamalls.base.basedao.IGenericDAO;
import com.javamalls.base.query.GenericPageList;
import com.javamalls.base.query.PageObject;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.ctrl.admin.tools.MsgTools;
import com.javamalls.payment.weixin.UniformOrderResult;
import com.javamalls.payment.weixin.WeixinApi;
import com.javamalls.payment.weixin.WeixinPayNotify;
import com.javamalls.payment.weixin.support.JaxbUtil;
import com.javamalls.payment.weixin.util.HttpUtil;
import com.javamalls.payment.weixin.vo.ResultMsg;
import com.javamalls.platform.domain.Accessory;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.GoodsCart;
import com.javamalls.platform.domain.GoodsItem;
import com.javamalls.platform.domain.GoodsSpecProperty;
import com.javamalls.platform.domain.GroupGoods;
import com.javamalls.platform.domain.OrderForm;
import com.javamalls.platform.domain.OrderFormLog;
import com.javamalls.platform.domain.Payment;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.SysConfig;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.WarehouseGoodsItem;
import com.javamalls.platform.service.IAccessoryService;
import com.javamalls.platform.service.IGoodsCartService;
import com.javamalls.platform.service.IGoodsItemService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IGroupGoodsService;
import com.javamalls.platform.service.IOrderFormLogService;
import com.javamalls.platform.service.IOrderFormService;
import com.javamalls.platform.service.IPaymentService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.ITemplateService;
import com.javamalls.platform.service.IUserService;
import com.javamalls.platform.service.IWarehouseGoodsItemService;
import com.javamalls.platform.service.IWeixinService;
import com.javamalls.payment.weixin.UniformOrder;

@Service
@Transactional
public class WexinServiceImpl implements IWeixinService {
	private static final Logger logger = Logger.getLogger(IWeixinService.class);
	@Autowired
    private ISysConfigService                  configService;
	@Autowired
	private IStoreService					   storeService;
	@Autowired
	private IOrderFormService					orderFormService;
	@Autowired
	private IPaymentService						paymentService;
	@Autowired
	private IOrderFormLogService				orderFormLogService;
	@Autowired
    private IGroupGoodsService         groupGoodsService;
    @Autowired
    private IGoodsService              goodsService;
    @Autowired
    private IGoodsCartService          goodsCartService;
    @Autowired
    private IGoodsItemService		  goodsItemService;
    @Autowired
    private IWarehouseGoodsItemService warehouseGoodsItemService;
    @Autowired
    private ITemplateService           templateService;
    @Autowired
    private MsgTools                   msgTools;
    @Autowired
    private IUserService				userService;
	 /**
     * 获得微信js授权
     * @param appId
     * @return
     */
    public String getJsTicket(String appId_userId,Payment payment){
    	CacheManager manager  = CacheManager.create();
    	// Cache cache =manager.getCache("access_token");
    	 Ehcache cache = manager.getEhcache("access_token");
         Element element = cache.get(appId_userId);
         String jsTicket ="";
         
         if(null == element){
             //获取(防止并发重复获取token失效)
            synchronized (this){
            	 element = cache.get(appId_userId);
                if(null!=element){
                    return element.getObjectValue().toString();
                }
               // WxCompanyExample example = new WxCompanyExample();
               // example.createCriteria().andAppIdEqualTo(appId);
                //List<WxCompany> wxCompanies = wxCompanyMapper.selectByExample(example);
                //WxCompany wxCompany = wxCompanies.get(0);
                jsTicket = doJsTicket(payment,element,0);
                //插入缓存
                element = new Element(appId_userId,jsTicket);
                cache.put(element);
            }
        }else{
        	jsTicket = element.getObjectValue().toString();
        }
        return jsTicket;
    }
    public String doJsTicket(Payment payment,Element element,int count){
    	String urlJsticket = WeixinApi.URL_JSTICKET;
        String token = this.configService.getSysConfig().getAccess_token();
        if(token==null||"".equals(token)){
        	token = this.upateWxAccessToken(payment, 0);
        }
        urlJsticket = String.format(urlJsticket,token);

        String result = HttpUtil.execute(urlJsticket, null, null);
        JSONObject object = JSON.parseObject(result);
        //获取ticket失败:{"errcode":42001,"errmsg":"access_token expired hint: [2aJ0927vr69!]"}
        if(object.containsKey("errcode") && !object.get("errcode").toString().equals("0")){
        	if(CommUtil.null2Int(object.get("errcode").toString())==42001){//ACCESS_TOKEN过期，
        		logger.error("获取ticket失败【ACCESS_TOKEN过期，重新获取】:"+result);
        		//重新获取token
        		token = this.upateWxAccessToken(payment, 0);
        		count++;
        		return doJsTicket(payment,element,count);
        	}else{
        		 logger.error("获取ticket失败:"+result);
                 return null;
        	}
           
        }else{
            String jsTicket = object.get("ticket").toString();
            logger.debug("获取ticket成功:" + result);
            return jsTicket;
           
        }
    }
	@Override
	public ResultMsg initJsApiConfig(String url,Payment payment,User user, HttpServletRequest request) {
		 ResultMsg rmg = new ResultMsg();
	        Map<String,Object> map = Maps.newHashMap();
	        String appId =payment.getWeixin_appId();
	       
	        if(user!=null){
	        	String appId_userId = appId+"_"+user.getId();
	        	//获取ticket
	            String jsTicket = this.getJsTicket(appId_userId,payment);
	            //随机数
	            String noncestr =CommUtil.generateNonce_str();
	            //时间戳
	            long time = new Date().getTime();
	            long time1=Long.parseLong(String.valueOf(System.currentTimeMillis()).toString().substring(0,10));
	            String waitSign = "jsapi_ticket="+jsTicket+"&noncestr="+noncestr+"&timestamp="+time1+"&url="+url;
	            System.out.println("----------waitSign----------");
	            System.out.println(waitSign);
	            //签名
	            String sign = CommUtil.SHA1(waitSign);
	            System.out.println("-----------签名-：---"+sign);
	            rmg.setResult(true);
	            map.put("appId",appId);
	            map.put("timestamp",time1);
	            map.put("nonceStr",noncestr);
	            map.put("signature",sign);
	            map.put("url", url);
	            rmg.setData(map);
	        }else{
	        	rmg.setResult(false);
	        	rmg.setMsg("请您先登录!");
	        }
	        return rmg;
	}

	 
	@Override
	public ResultMsg togenerateOrder(Payment payment,String out_trade_no, BigDecimal total_price,
			String code, String openid, String notify_url) {
		  ResultMsg rmg = new ResultMsg();
	        rmg.setResult(true);
	        //生成统一下单参数
	        UniformOrder ufo = buildUniformOrder(payment,out_trade_no,total_price,code,openid,notify_url);
	        if(null!=ufo){
	            rmg.setData(ufo);
	        }else{
	            rmg.setResult(false);
	            rmg.setMsg("服务器异常");
	        }
	        return rmg;
	}
	/**
	    *  -微信预下单
	    */
	   public ResultMsg generateOrder(OrderForm order, String code, String openid, String notify_url){
		   ResultMsg rmg = new ResultMsg();
	        rmg.setResult(false);
	        //校验参数
	        if (order == null) {
	            rmg.setMsg("订单未查询到信息");
	            logger.error("WexinServiceImpl generateOrder()订单未查询到信息");
	            return rmg;
	        }
	        if (code == null || "".equals(code)) {
	            rmg.setMsg("code参数不能为空");
	            logger.error("WexinServiceImpl generateOrder() code参数不能为空");
	            return rmg;
	        }
	        if (openid == null || "".equals(openid)) {
	            rmg.setMsg("openid参数不能为空");
	            logger.error("WexinServiceImpl generateOrder() openid参数不能为空");
	            return rmg;
	        }
	        if (notify_url == null || "".equals(notify_url)) {
	            rmg.setMsg("notify_url参数不能为空");
	            logger.error("WexinServiceImpl generateOrder() notify_url参数不能为空");
	            return rmg;
	        }

	        try {
	        	rmg = this.togenerateOrder(order.getPayment(),order.getOrder_id(), order.getTotalPrice(), code, openid,
		                notify_url);
	        } catch (Exception e) {
	            e.printStackTrace();
	            rmg.setResult(false);
	            return rmg;
	        }
	        //微信支付-生成预订单
	        if (rmg.getResult()) {
	            UniformOrder order1 = (UniformOrder) rmg.getData();
	            try {
	                rmg = this.toUniformOrder(order1,order.getPayment());
	                return rmg;
	            } catch (Exception e) {
	                e.printStackTrace();
	                rmg.setResult(false);
	                rmg.setMsg("下单失败：code=100");
	                logger.error("WexinServiceImpl generateOrder() 下单失败：code=100",e);
	                return rmg;
	            }
	        }
	        return rmg;
	   }
	   /**
	    * 微信支付下单操作
	    */
	   @Override
		public ResultMsg toUniformOrder(UniformOrder uniformOrder,Payment payment){

	        ResultMsg rmg = new ResultMsg();
	        String out_trade_no = uniformOrder.getOut_trade_no();


	        String urlUniformOrder = WeixinApi.URL_UNIFORM_ORDER;
	        String xml = JaxbUtil.convertToXml(uniformOrder);


	        logger.info("请求的xml:"+xml);

	        String content = HttpUtil.execute(urlUniformOrder, "post", xml);

	        if(StringUtils.isNotBlank(content)){
	            UniformOrderResult uniformOrderResult = JaxbUtil.converyToJavaBean(content, UniformOrderResult.class);

	            logger.info("返回的xml:"+content);

	            String return_code = uniformOrderResult.getReturn_code();
	            String result_code = uniformOrderResult.getResult_code();
	            if(StringUtils.isNotBlank(return_code) && StringUtils.isNotBlank(result_code)
	                    && "SUCCESS".equals(return_code) && "SUCCESS".equals(result_code)){
	                rmg.setResult(true);
	                rmg.setMsg("下单成功");
	                String prepay_id = uniformOrderResult.getPrepay_id();
	                String appid = uniformOrderResult.getAppid();
	                Map<String,Object> map = Maps.newHashMap();
	                long time = new Date().getTime();
	                String nonceStr = CommUtil.generateNonce_str();
	                map.put("package","prepay_id="+prepay_id);
	                map.put("nonceStr",nonceStr);
	                map.put("timeStamp",time);
	                map.put("appId",appid);
	                map.put("signType","MD5");
	 
	              
	                //获得商户号密钥
	                String MchSecret = payment.getWeixin_partnerKey();
	                 
	                String paySign = CommUtil.generatePaySign(map, MchSecret);
	                System.out.println(map.toString());
	                System.out.println("生成下单签名："+paySign);
	                map.put("sign",paySign);
	                rmg.setData(map);
	            }else{
	                rmg.setResult(false);
	                rmg.setMsg("下单失败");
	                logger.error("统一下单失败："+uniformOrderResult.toString());
	            }
	        }else{
	            rmg.setResult(false);
	            rmg.setMsg("下单失败");
	            logger.error("下单失败");
	        }
	        return rmg;
		}
	 /**
     * 标品订单
     * 构造统一下单参数
     * out_trade_no:交易流水号
     * total_price:价格（单位元）
     * @param client
     * @param orders
     * @return
     */
    public UniformOrder buildUniformOrder(Payment payment,String out_trade_no,BigDecimal total_price,String code,String openid,String notify_url){
        
    	  UniformOrder ufo = new UniformOrder();
          ufo.setOpenid(openid);
          ufo.setAppid(payment.getWeixin_appId());
          ufo.setMch_id(payment.getWeixin_partnerId());
          ufo.setBody("out_trade_no-"+out_trade_no+"-total_price-"+total_price);
          //微信支付回调URL；
          ufo.setNotify_url(notify_url);
          ufo.setOut_trade_no(out_trade_no);
          int total_fee = total_price.multiply(new BigDecimal(100)).intValue();
          ufo.setTotal_fee(total_fee);
          ufo.setTrade_type("JSAPI");
          String str = CommUtil.generateNonce_str();
          ufo.setNonce_str(str);
          String sign = CommUtil.generateSign(ufo,payment.getWeixin_partnerKey(),UniformOrder.class);
          ufo.setSign(sign);
          return ufo;
    }
    /**
     * 
     * 微信支付回调函数-订单
     * @param notify
     * @param request
     * @return
     */
    public ResultMsg wxNotify_url(WeixinPayNotify notify,HttpServletRequest request){
    	String trade_no = notify.getOut_trade_no();//交易流水号
    	OrderForm order = this.orderFormService.getObjByProperty("order_id",trade_no);
        ResultMsg rmg = new ResultMsg();
        rmg.setResult(false);
        logger.info("订单支付回调：" + notify.toString());
        String result_code = notify.getResult_code();
        String return_code = notify.getReturn_code();
        //判断支付结果
        if (StringUtils.isNotBlank(return_code) && StringUtils.isNotBlank(result_code)
            && "SUCCESS".equals(return_code) && "SUCCESS".equals(result_code)) {
            ////校验sign
            String appid = notify.getAppid();
            String sign = CommUtil.generateSign(notify, order.getPayment().getWeixin_partnerKey(),
                WeixinPayNotify.class);
            logger.info("加密后的sign:" + sign);
            if (sign.equals(notify.getSign())) {
                rmg.setResult(true);
            }
        }

       
        //更新订单状态为已支付
        if (rmg.getResult()) {
            logger.info("-回调函数 ：---------------------------------");
            System.out.println("trade_no: " + trade_no);
            //根据在线咨询编号查询在线咨询订单信息
            
            if (order.getOrder_status() != 20) {
                order.setOrder_status(20);
                order.setOut_order_id(trade_no);
                order.setPayTime(new Date());
                this.orderFormService.update(order);
                update_goods_inventory(order);
                OrderFormLog ofl = new OrderFormLog();
                ofl.setCreatetime(new Date());
                ofl.setLog_info("微信在线支付");
                ofl.setLog_user(order.getUser());
                ofl.setOf(order);
                this.orderFormLogService.save(ofl);
                try{
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
                }catch(Exception e){
                	logger.error("订单回调后，短信或邮件通知异常：",e);
                }
                
                update_goods_bind(order, trade_no);
            }
        }
        //响应微信服务器
        String xml = "<xml>" + "<return_code>SUCCESS</return_code>" + "<return_msg>OK</return_msg>"
                     + "</xml>";
        rmg.setMsg(xml);
        return rmg;
    }

    /**
     * 接口获取微信access_token
     * 并更新到数据库中
     * @return
     */
	@Override
    public String upateWxAccessToken(Payment payment,int count){
		if(payment==null){
			 logger.debug("参数Payment为null" );
			return null;
		}
		 if(count<3){
			 String urlAccesstoken = WeixinApi.URL_ACCESSTOKEN;
	         urlAccesstoken = String.format(urlAccesstoken,payment.getWeixin_appId(),payment.getWeixin_appSecret());
	         String result = HttpUtil.execute(urlAccesstoken, null, null);
	         JSONObject object = JSON.parseObject(result);
	         if(object.containsKey("errcode") && !object.get("errcode").toString().equals("0")){
	             logger.error("获取access_token失败:"+result);
	             count++;
	             String accessToken = upateWxAccessToken(payment,count);
	             return accessToken;
	         }else{
	             String accessToken = object.get("access_token").toString();
	             if("admin".equals(payment.getType())){
	            	 SysConfig config = this.configService.getSysConfig();
	            	 config.setAccess_token(accessToken);
	            	 this.configService.update(config);
	             }else if("user".equals(payment.getType())&&payment.getStore()!=null){
	            	 Store store = this.storeService.getObjById(payment.getStore().getId());
	            	 store.setAccess_token(accessToken);
	            	 this.storeService.update(store);
	             }else{
	            	 logger.error("获取access_token失败:" + result);
		             return accessToken;
	             }
	             logger.debug("获取access_token成功:" + result);
	             return accessToken;
	         }
		 }else{
			 logger.debug("获取access_token连续三次失败----" );
			 return null;
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
	                 HashMap<String,Object> paramMap = new HashMap<String,Object>();
	                 paramMap.put("goodsId", goods.getId());
	                 goods_items = this.goodsItemService.query(
	                 		"select obj from GoodsItem obj where obj.disabled=false and  obj.goods.id =:goodsId", paramMap, -1, -1);
	                 if(goods_items!=null&&goods_items.size()>0){
	                	 if(goods_items.size()>1){
			                 	for(GoodsItem item:goods_items){
			              		 String[] temp_ids = item.getSpec_combination().split("_");
			              		 Arrays.sort(temp_ids);
			                     Arrays.sort(gsp_list);
			                     if (Arrays.equals(temp_ids, gsp_list)) {
			                         item.setGoods_inventory(item.getGoods_inventory()-gc.getCount());
			                         Map<String,Object> map=new HashMap<String, Object>();
			                         map.put("goodsItem_id", item.getId());
			                         //更新库存商品的库存
			                         List<WarehouseGoodsItem> list = this.warehouseGoodsItemService.query(
			                        		 "select obj from WarehouseGoodsItem obj where obj.goods_item.id=:goodsItem_id", map, 0, 1);
			                         if(list!=null&&list.size()>0){
			                        	 WarehouseGoodsItem warehouseGoodsItem = list.get(0);
			                        	 warehouseGoodsItem.setWarehoust_number(warehouseGoodsItem.getWarehoust_number()-gc.getCount());
			                        	 this.warehouseGoodsItemService.update(warehouseGoodsItem);
			                        	 
			                         }
			                     }
			              	   }
	                	 }else{
	                		 GoodsItem item = goods_items.get(0);
	                		 item.setGoods_inventory(item.getGoods_inventory()-gc.getCount());
	                		 //商品库存更新
	                		 goods.setGoods_inventory(item.getGoods_inventory());
	                         Map<String,Object> map=new HashMap<String, Object>();
	                         map.put("goodsItem_id", item.getId());
	                         //更新库存商品的库存
	                         List<WarehouseGoodsItem> list = this.warehouseGoodsItemService.query(
	                        		 "select obj from WarehouseGoodsItem obj where obj.goods_item.id=:goodsItem_id", map, 0, 1);
	                         if(list!=null&&list.size()>0){
	                        	 WarehouseGoodsItem warehouseGoodsItem = list.get(0);
	                        	 warehouseGoodsItem.setWarehoust_number(warehouseGoodsItem.getWarehoust_number()-gc.getCount());
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
	   private void send_order_email(HttpServletRequest request, OrderForm order, String email,
               String mark) throws Exception {
			com.javamalls.platform.domain.Template template = this.templateService.getObjByProperty(
			"mark", mark);
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
}

private void send_order_sms(HttpServletRequest request, OrderForm order, String mobile,
             String mark) throws Exception {
	com.javamalls.platform.domain.Template template = this.templateService.getObjByProperty(
	"mark", mark);
	if ((template != null) && (template.isOpen())) {
	/*   String path = request.getSession().getServletContext().getRealPath("/") + "/vm/";
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
	Map<String, String> map=new HashMap<String, String>();
	Store store = order.getStore();
	
	String userName=order.getUser().getUserName();
	if(order.getUser().getTrueName()!=null&&!"".equals(order.getUser().getTrueName())){
	userName=order.getUser().getTrueName();
	}
	map.put("buyerName",userName);
	map.put("order_id", order.getOrder_id());
	map.put("paymentName", order.getPayment().getName());
	if(template.getMark().contains("tobuyer")){
	map.put("storeName", store.getStore_name());
	}else{
	map.put("sellerName", store.getUser().getUserName());
	}
	this.msgTools.sendSMS(mobile, template.getTitle(),map);
	}
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
/**
	 * 用户绑定微信
	 * 获取openid，并更新到用户中.
	 */
	public ResultMsg	bindWxOpenIdToMember(String code,Long userId,Payment payment){
		ResultMsg rmg = new ResultMsg();
		rmg.setResult(false);
		rmg.setMsg("绑定失败");
		//1，校验参数
		if(code==null||"".equals(code)){
			rmg.setMsg("code参数为空");
			return rmg;
		}
		if(userId==null){
			rmg.setMsg("会员id为空");
			return rmg;
		}
		if(payment==null){
			rmg.setMsg("微信appId为空");
			return rmg;
		}
		User user = this.userService.getObjById(userId);
		if(user==null){
			rmg.setMsg("会员为空");
			return rmg;
		}
		
		//2,获取openid
		String openid = this.getWxOpenIdOrToken(code, 0,payment);
		if(openid==null||"".equals(openid)){
			rmg.setMsg("未获取到openid");
			return rmg;
		}
		User wxUser = this.userService.getObjByProperty("wx_openid", openid);
		if(wxUser!=null&&!wxUser.getId().equals(user.getId())){
			rmg.setMsg("此微信已经关联其他账号");
			return rmg;
		}
		try{
			//将openid更新到用户中
			user.setWx_openid(openid);
			this.userService.update(user);
			rmg.setResult(true);
			rmg.setMsg("绑定微信成功");
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e);
			rmg.setResult(false);
			rmg.setMsg("绑定微信异常");
			return rmg;
		}
		
		return rmg;
	}
	/**
     * 根据code获取openid与access_token
     * @param code
     * @param type 0：获取openid ；     1：获取access_token
     * @return
     */
    @Override
    public String   getWxOpenIdOrToken(String code,int type,Payment payment){
    	 String str = null;
    	try{
        	String urlAuthorizationToken = WeixinApi.URL_AUTHORIZATION_TOKEN;
            urlAuthorizationToken = String.format(urlAuthorizationToken,payment.getWeixin_appId(),payment.getWeixin_appSecret(),code);
            String result = HttpUtil.execute(urlAuthorizationToken, null, null);
            JSONObject object = JSON.parseObject(result);
           
            if(object.containsKey("errcode") && !object.get("errcode").toString().equals("0")){
            	System.out.println("根据code获取access_token失败:"+result);
            	return str;
            }else{
            	if(0==type){
            		str = object.get("openid").toString();
            		 System.out.println("根据code获取openid成功:" + result);
            	}else if(1 == type){
            		str = object.get("access_token").toString();
            		  System.out.println("根据code获取access_token成功:" + result);
            	} 
            }
    	}catch(Exception e){
    		return null; 
    	}
    	 return str;
    }
}
