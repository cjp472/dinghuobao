package com.javamalls.ctrl.admin.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.easyjf.beans.BeanUtils;
import com.easyjf.beans.BeanWrapper;
import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.constant.Constant;
import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.WebForm;
import com.javamalls.base.tools.database.DatabaseTools;
import com.javamalls.ctrl.admin.tools.MsgTools;
import com.javamalls.front.web.tools.GoodsViewTools;
import com.javamalls.lucene.LuceneUtil;
import com.javamalls.lucene.LuceneVo;
import com.javamalls.platform.domain.Evaluate;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.GoodsBrand;
import com.javamalls.platform.domain.GoodsCart;
import com.javamalls.platform.domain.GoodsClass;
import com.javamalls.platform.domain.GoodsItem;
import com.javamalls.platform.domain.GoodsSpecProperty;
import com.javamalls.platform.domain.Message;
import com.javamalls.platform.domain.OrderForm;
import com.javamalls.platform.domain.OrderFormPayLog;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.query.GoodsItemQueryObject;
import com.javamalls.platform.domain.query.GoodsQueryObject;
import com.javamalls.platform.service.IEvaluateService;
import com.javamalls.platform.service.IGoodsBrandService;
import com.javamalls.platform.service.IGoodsCartService;
import com.javamalls.platform.service.IGoodsClassService;
import com.javamalls.platform.service.IGoodsItemService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IGoodsSpecPropertyService;
import com.javamalls.platform.service.IMessageService;
import com.javamalls.platform.service.IOrderFormLogService;
import com.javamalls.platform.service.IOrderFormPayLogService;
import com.javamalls.platform.service.IOrderFormService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.ITemplateService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;

/**商品管理
 *                       
 * @Filename: GoodsManageAction.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class GoodsManageAction {
    @Autowired
    private ISysConfigService         configService;
    @Autowired
    private IUserConfigService        userConfigService;
    @Autowired
    private IGoodsService             goodsService;
    @Autowired
    private IGoodsBrandService        goodsBrandService;
    @Autowired
    private IGoodsClassService        goodsClassService;
    @Autowired
    private ITemplateService          templateService;
    @Autowired
    private IUserService              userService;
    @Autowired
    private IMessageService           messageService;
    @Autowired
    private MsgTools                  msgTools;
    @Autowired
    private DatabaseTools             databaseTools;
    @Autowired
    private IEvaluateService          evaluateService;
    @Autowired
    private IGoodsCartService         goodsCartService;
    @Autowired
    private IOrderFormService         orderFormService;
    @Autowired
    private IOrderFormLogService      orderFormLogService;
    @Autowired
    private IGoodsItemService         goodsItemService;
    @Autowired
    private GoodsViewTools            goodsViewTools;
    @Autowired
    private IGoodsSpecPropertyService goodsSpecPropertyService;
    @Autowired
    private IOrderFormPayLogService   orderFormPayLogService;

    @SecurityMapping(title = "商品列表", value = "/admin/goods_list.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
    @RequestMapping({ "/admin/goods_list.htm" })
    public ModelAndView goods_list(HttpServletRequest request, HttpServletResponse response,
                                   String currentPage, String orderBy, String orderType,
                                   String goods_name, String brandid, String gcid,
                                   String store_recommend, String cannotdelflag) {

        ModelAndView mv = new JModelAndView("admin/blue/goods_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        String params = "";
        GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
        WebForm wf = new WebForm();
        wf.toQueryPo(request, qo, Goods.class, mv);
        qo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(-2)), ">");

        if (null != brandid && !"".equals(brandid))
            qo.addQuery("obj.goods_brand.id", new SysMap("brandid", Long.valueOf(brandid)), "=");

        if (null != goods_name && !"".equals(goods_name)) {
            qo.addQuery("obj.goods_name", new SysMap("goodsname", "%" + goods_name.trim() + "%"),
                "like");
        }

        if (null != gcid && !"".equals(gcid)) {
            qo.addQuery("obj.gc.id", new SysMap("gcid", Long.valueOf(gcid)), "=");
        }

        if (null != store_recommend && !"".equals(store_recommend)) {
            qo.addQuery("obj.store_recommend",
                new SysMap("store_recommend", Boolean.valueOf(store_recommend)), "=");
        }
        qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");

        IPageList pList = this.goodsService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/admin/goods_list.htm", "", params, pList, mv);
        List<GoodsBrand> gbs = this.goodsBrandService.query(
            "select obj from GoodsBrand obj order by obj.sequence asc", null, -1, -1);
        List<GoodsClass> gcs = this.goodsClassService.query(
            "select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc",
            null, -1, -1);
        mv.addObject("gcs", gcs);
        mv.addObject("gbs", gbs);
        mv.addObject("goods_name", goods_name);
        mv.addObject("brandid", brandid);
        mv.addObject("gcid", gcid);
        mv.addObject("store_recommend", store_recommend);

        // 删除失败标识为1时javascript提示
        if ("1".equals(cannotdelflag)) {
            mv.addObject("js", "alert('该商品已被售出，无法删除。请执行下架操作！');");
        }

        return mv;
    }

    @SecurityMapping(title = "违规商品列表", value = "/admin/goods_outline.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
    @RequestMapping({ "/admin/goods_outline.htm" })
    public ModelAndView goods_outline(HttpServletRequest request, HttpServletResponse response,
                                      String currentPage, String orderBy, String orderType,
                                      String goods_name, String brandid) {
        ModelAndView mv = new JModelAndView("admin/blue/goods_outline.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        String params = "";
        GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
        WebForm wf = new WebForm();
        wf.toQueryPo(request, qo, Goods.class, mv);
        qo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(-2)), "=");

        if (null != brandid && !"".equals(brandid))
            qo.addQuery("obj.goods_brand.id", new SysMap("brandid", Long.valueOf(brandid)), "=");

        if (null != goods_name && !"".equals(goods_name)) {
            qo.addQuery("obj.goods_name", new SysMap("goodsname", "%" + goods_name.trim() + "%"),
                "like");
        }

        qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");

        IPageList pList = this.goodsService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/admin/goods_list.htm", "", params, pList, mv);
        List<GoodsBrand> gbs = this.goodsBrandService.query(
            "select obj from GoodsBrand obj order by obj.sequence asc", null, -1, -1);
        List<GoodsClass> gcs = this.goodsClassService.query(
            "select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc",
            null, -1, -1);
        mv.addObject("gcs", gcs);
        mv.addObject("gbs", gbs);
        mv.addObject("goods_name", goods_name);
        mv.addObject("brandid", brandid);
        return mv;
    }

    @SecurityMapping(title = "商品添加", value = "/admin/goods_add.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
    @RequestMapping({ "/admin/goods_add.htm" })
    public ModelAndView goods_add(HttpServletRequest request, HttpServletResponse response,
                                  String currentPage) {
        ModelAndView mv = new JModelAndView("admin/blue/goods_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("currentPage", currentPage);
        return mv;
    }

    @SecurityMapping(title = "商品编辑", value = "/admin/goods_edit.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
    @RequestMapping({ "/admin/goods_edit.htm" })
    public ModelAndView goods_edit(HttpServletRequest request, HttpServletResponse response,
                                   String id, String currentPage) {
        ModelAndView mv = new JModelAndView("admin/blue/goods_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if ((id != null) && (!id.equals(""))) {
            Goods goods = this.goodsService.getObjById(Long.valueOf(Long.parseLong(id)));
            mv.addObject("obj", goods);
            mv.addObject("currentPage", currentPage);
        }
        return mv;
    }

    @SecurityMapping(title = "商品保存", value = "/admin/goods_save.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
    @RequestMapping({ "/admin/goods_save.htm" })
    public ModelAndView goods_save(HttpServletRequest request, HttpServletResponse response,
                                   String id, String currentPage, String cmd, String list_url,
                                   String add_url) {
        WebForm wf = new WebForm();
        Goods goods = null;
        if (id.equals("")) {
            goods = (Goods) wf.toPo(request, Goods.class);
            goods.setCreatetime(new Date());
        } else {
            Goods obj = this.goodsService.getObjById(Long.valueOf(Long.parseLong(id)));
            goods = (Goods) wf.toPo(request, obj);
        }
        if (id.equals("")) {
            this.goodsService.save(goods);
        } else {
            this.goodsService.update(goods);
        }
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("list_url", list_url);
        mv.addObject("op_title", "保存商品成功");
        if (add_url != null) {
            mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
        }
        return mv;
    }

    @SecurityMapping(title = "商品删除", value = "/admin/goods_del.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
    @RequestMapping({ "/admin/goods_del.htm" })
    public String goods_del(HttpServletRequest request, String mulitId) throws Exception {
        try {
            String[] ids = mulitId.split(",");
            for (String id : ids) {
                if (!id.equals("")) {
                    Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("gid", goods.getId());
                    List<GoodsCart> goodCarts = this.goodsCartService.query(
                        "select obj from GoodsCart obj where obj.goods.id = :gid", map, -1, -1);
                    Long of_id;
                    for (GoodsCart gc : goodCarts) {
                        this.goodsCartService.delete(gc.getId());
                        OrderForm oftmp = gc.getOf();
                        if (oftmp != null) {
                            of_id = gc.getOf().getId();
                            OrderForm of = this.orderFormService.getObjById(of_id);
                            if (of.getGcs().size() == 0) {
                                this.orderFormService.delete(of_id);
                            }
                        }
                    }
                    List<Evaluate> evaluates = goods.getEvaluates();
                    for (Evaluate e : evaluates) {
                        this.evaluateService.delete(e.getId());
                    }
                    goods.getGoods_ugcs().clear();
                    goods.getGoods_ugcs().clear();
                    goods.getGoods_photos().clear();
                    goods.getGoods_ugcs().clear();
                    goods.getGoods_specs().clear();
                    this.goodsService.delete(goods.getId());

                    String goods_lucene_path = System.getProperty("user.dir") + File.separator
                                               + "luence" + File.separator + "goods";
                    File file = new File(goods_lucene_path);
                    if (!file.exists()) {
                        CommUtil.createFolder(goods_lucene_path);
                    }
                    LuceneUtil lucene = LuceneUtil.instance();
                    LuceneUtil.setIndex_path(goods_lucene_path);
                    lucene.delete_index(CommUtil.null2String(id));

                    send_site_msg(request, "msg_toseller_goods_delete_by_admin_notify", goods
                        .getGoods_store().getUser(), goods, "商城存在违规");
                }
            }
        } catch (RuntimeException e) {
            //因外键关联删除失败时返回删除失败flag
            //e.printStackTrace();
            //throw e;
            return "redirect:goods_list.htm?cannotdelflag=1";
        }
        return "redirect:goods_list.htm";
    }

    private void send_site_msg(HttpServletRequest request, String mark, User user, Goods goods,
                               String reason) throws Exception {
        com.javamalls.platform.domain.Template template = this.templateService.getObjByProperty(
            "mark", mark);
        if (template != null && template.isOpen()) {
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
            context.put("reason", reason);
            context.put("user", user);
            context.put("config", this.configService.getSysConfig());
            context.put("send_time", CommUtil.formatLongDate(new Date()));
            StringWriter writer = new StringWriter();
            blank.merge(context, writer);

            String content = writer.toString();
            User fromUser = this.userService.getObjByProperty("userName", "admin");
            Message msg = new Message();
            msg.setCreatetime(new Date());
            msg.setContent(content);
            msg.setFromUser(fromUser);
            msg.setTitle(template.getTitle());
            msg.setToUser(user);
            msg.setType(0);
            this.messageService.save(msg);
            CommUtil.deleteFile(path + "temp.vm");
            writer.flush();
            writer.close();
        }
    }

    @SecurityMapping(title = "商品AJAX更新", value = "/admin/goods_ajax.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
    @RequestMapping({ "/admin/goods_ajax.htm" })
    public void ajax(HttpServletRequest request, HttpServletResponse response, String id,
                     String fieldName, String value) throws ClassNotFoundException {
        Goods obj = this.goodsService.getObjById(Long.valueOf(Long.parseLong(id)));
        Field[] fields = Goods.class.getDeclaredFields();
        BeanWrapper wrapper = new BeanWrapper(obj);
        Object val = null;
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                Class clz = Class.forName("java.lang.String");
                if (field.getType().getName().equals("int")) {
                    clz = Class.forName("java.lang.Integer");
                }
                if (field.getType().getName().equals("boolean")) {
                    clz = Class.forName("java.lang.Boolean");
                }
                if (!value.equals("")) {
                    val = BeanUtils.convertType(value, clz);
                } else {
                    val = Boolean.valueOf(!CommUtil.null2Boolean(wrapper
                        .getPropertyValue(fieldName)));
                }
                wrapper.setPropertyValue(fieldName, val);
            }
        }
        if (fieldName.equals("store_recommend")) {
            if (obj.isStore_recommend()) {
                obj.setStore_recommend_time(new Date());
            } else {
                obj.setStore_recommend_time(null);
            }
        }
        this.goodsService.update(obj);
        if (obj.getGoods_status() == 0) {
            String goods_lucene_path = System.getProperty("user.dir") + File.separator + "luence"
                                       + File.separator + "goods";
            File file = new File(goods_lucene_path);
            if (!file.exists()) {
                CommUtil.createFolder(goods_lucene_path);
            }
            LuceneVo vo = new LuceneVo();
            vo.setVo_id(obj.getId());
            vo.setVo_title(obj.getGoods_name());
            vo.setVo_content(obj.getGoods_details());
            vo.setVo_type("goods");
            vo.setVo_store_price(CommUtil.null2Double(obj.getStore_price()));
            vo.setVo_add_time(obj.getCreatetime().getTime());
            vo.setVo_goods_salenum(obj.getGoods_salenum());
            vo.setVo_goods_collect(obj.getGoods_collect());
            LuceneUtil lucene = LuceneUtil.instance();
            LuceneUtil.setIndex_path(goods_lucene_path);
            lucene.update(CommUtil.null2String(obj.getId()), vo);
        } else {
            String goods_lucene_path = System.getProperty("user.dir") + File.separator + "luence"
                                       + File.separator + "goods";
            File file = new File(goods_lucene_path);
            if (!file.exists()) {
                CommUtil.createFolder(goods_lucene_path);
            }
            LuceneUtil lucene = LuceneUtil.instance();
            LuceneUtil.setIndex_path(goods_lucene_path);
            lucene.delete_index(CommUtil.null2String(id));
        }
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(val.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印商品时，根据商品ID查找货品
     * @param request
     * @param response
     * @param currentPage
     * @param orderBy
     * @param orderType
     * @return
     */
    @RequestMapping({ "/seller/print_sku_list.htm" })
    public ModelAndView print_sku_list(HttpServletRequest request, HttpServletResponse response,
                                       String currentPage, String orderBy, String orderType,
                                       @RequestParam String goods_id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller/print_sku_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }

        if (SecurityUserHolder.getCurrentUser() == null) {
            mv = new JModelAndView("login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            return mv;
        }
        String params = "";
        GoodsItemQueryObject qo = new GoodsItemQueryObject(currentPage, mv, orderBy, orderType);

        qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");
        qo.addQuery("obj.goods.goods_store.id", new SysMap("store_id", SecurityUserHolder
            .getCurrentUser().getStore().getId()), "=");

        qo.addQuery("obj.goods.disabled", new SysMap("goodsdisabled", false), "=");
        qo.addQuery("obj.goods.goods_status", new SysMap("goods_status", 0), "=");

        qo.addQuery("obj.goods.id", new SysMap("goods_id", CommUtil.null2Long(goods_id)), "=");

        mv.addObject("goods_id", goods_id);
        qo.setPageSize(1000);
        IPageList pList = this.goodsItemService.list(qo);
        CommUtil.saveIPageList2ModelAndView(null, "", params, pList, mv);
        mv.addObject("goodsViewTools", goodsViewTools);
        return mv;
    }

    /**
     * 打印货品，货品详情页面
     * @param request
     * @param response
     * @param currentPage
     * @param orderBy
     * @param orderType
     * @return
     */
    @RequestMapping({ "/seller/sku_detail_print.htm" })
    public ModelAndView sku_detail_print(HttpServletRequest request, HttpServletResponse response,
                                         String currentPage, String orderBy, String orderType,
                                         @RequestParam String goods_id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller/sku_detail_print.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }

        if (SecurityUserHolder.getCurrentUser() == null) {
            mv = new JModelAndView("login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            return mv;
        }

        //获取所有的货品
        String params = "";
        GoodsItemQueryObject qo = new GoodsItemQueryObject(currentPage, mv, orderBy, orderType);

        qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");
        qo.addQuery("obj.goods.goods_store.id", new SysMap("store_id", SecurityUserHolder
            .getCurrentUser().getStore().getId()), "=");

        qo.addQuery("obj.goods.disabled", new SysMap("goodsdisabled", false), "=");
        qo.addQuery("obj.goods.goods_status", new SysMap("goods_status", 0), "=");

        qo.addQuery("obj.goods.id", new SysMap("goods_id", CommUtil.null2Long(goods_id)), "=");

        mv.addObject("goods_id", goods_id);
        qo.setPageSize(1000);
        IPageList pList = this.goodsItemService.list(qo);
        List<GoodsItem> goodsItemList = pList.getResult();

        List<GoodsItem> goodsItemResultList = new ArrayList<GoodsItem>();

        if (goodsItemList != null && goodsItemList.size() > 0) {
            for (GoodsItem goodsItem : goodsItemList) {
                //根据货品中 规格信息 分别查找其 颜色 和 尺码
                String specIds = goodsItem.getSpec_combination();
                if (CommUtil.isNotNull(specIds)) {
                    String[] specArray = specIds.split("_");
                    //颜色
                    String color;
                    //尺码
                    String size;
                    for (String str : specArray) {
                        GoodsSpecProperty prop = goodsSpecPropertyService.getObjById(CommUtil
                            .null2Long(str));
                        if (prop.getSpec().getName().equals("颜色")) {
                            color = prop.getValue();
                            goodsItem.setColor(color);

                        } else if (prop.getSpec().getName().equals("尺码")) {
                            size = prop.getValue();
                            goodsItem.setSize(size);
                        }

                    }
                }
                goodsItemResultList.add(goodsItem);
            }
        }

        mv.addObject("itemList", goodsItemResultList);

        return mv;
    }

    /**
     * 打印实体店订单
     * @param request
     * @param response
     * @param currentPage
     * @param orderBy
     * @param orderType
     * @return
     */
    @RequestMapping({ "/seller/phystore_order_print.htm" })
    public ModelAndView phystore_order_print(HttpServletRequest request,
                                             HttpServletResponse response,
                                             @RequestParam String order_id) {
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/seller/phystore_order_print.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }

        if (SecurityUserHolder.getCurrentUser() == null) {
            mv = new JModelAndView("login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            return mv;
        }

        OrderForm orderForm = orderFormService.getObjById(CommUtil.null2Long(order_id));

        List<OrderFormPayLog> orderFormPayLogs = this.orderFormPayLogService.query(
            "select obj from OrderFormPayLog obj where obj.of.id=" + order_id, null, -1, -1);
        //支付方式
        String payType = "";
        List<String> payTypeList = new ArrayList<String>();
        if (orderFormPayLogs != null && orderFormPayLogs.size() > 0) {
            for (OrderFormPayLog log : orderFormPayLogs) {
                payTypeList.add(Constant.PHYSTORE_PAY_TYPE.get(log.getPay_child_class()));
            }
        }
        payType = CommUtil.makeListToString(payTypeList, "、");

        mv.addObject("obj", orderForm);
        mv.addObject("payType", payType);

        return mv;
    }
}
