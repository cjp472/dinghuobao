package com.javamalls.ctrl.seller.action;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.constant.Constant;
import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.WebForm;
import com.javamalls.ctrl.admin.tools.StoreTools;
import com.javamalls.ctrl.seller.Tools.TransportTools;
import com.javamalls.excel.util.ExcelRead;
import com.javamalls.excel.util.ExcelUtil;
import com.javamalls.front.web.tools.GoodsViewTools;
import com.javamalls.front.web.tools.StoreViewTools;
import com.javamalls.lucene.LuceneUtil;
import com.javamalls.lucene.LuceneVo;
import com.javamalls.payment.chinabank.h5.util.JsonUtil;
import com.javamalls.platform.domain.Accessory;
import com.javamalls.platform.domain.Album;
import com.javamalls.platform.domain.Evaluate;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.GoodsBrand;
import com.javamalls.platform.domain.GoodsClass;
import com.javamalls.platform.domain.GoodsClassStaple;
import com.javamalls.platform.domain.GoodsItem;
import com.javamalls.platform.domain.GoodsLabel;
import com.javamalls.platform.domain.GoodsRetrieve;
import com.javamalls.platform.domain.GoodsRetrieveProperty;
import com.javamalls.platform.domain.GoodsSpecProperty;
import com.javamalls.platform.domain.GoodsSpecification;
import com.javamalls.platform.domain.GoodsType;
import com.javamalls.platform.domain.GoodslabelGoods;
import com.javamalls.platform.domain.Payment;
import com.javamalls.platform.domain.Report;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.StoreGrade;
import com.javamalls.platform.domain.Transport;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.UserGoodsClass;
import com.javamalls.platform.domain.WaterMark;
import com.javamalls.platform.domain.query.AccessoryQueryObject;
import com.javamalls.platform.domain.query.GoodsQueryObject;
import com.javamalls.platform.domain.query.ReportQueryObject;
import com.javamalls.platform.domain.query.TransportQueryObject;
import com.javamalls.platform.service.IAccessoryService;
import com.javamalls.platform.service.IAlbumService;
import com.javamalls.platform.service.IEvaluateService;
import com.javamalls.platform.service.IGoodsBrandService;
import com.javamalls.platform.service.IGoodsClassService;
import com.javamalls.platform.service.IGoodsClassStapleService;
import com.javamalls.platform.service.IGoodsItemService;
import com.javamalls.platform.service.IGoodsLabelService;
import com.javamalls.platform.service.IGoodsRetrievePropertyService;
import com.javamalls.platform.service.IGoodsRetrieveService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IGoodsSpecPropertyService;
import com.javamalls.platform.service.IGoodsTypePropertyService;
import com.javamalls.platform.service.IGoodsTypeService;
import com.javamalls.platform.service.IGoodslabelGoodsService;
import com.javamalls.platform.service.IPaymentService;
import com.javamalls.platform.service.IReportService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.ITransportService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserGoodsClassService;
import com.javamalls.platform.service.IUserService;
import com.javamalls.platform.service.IWaterMarkService;
import com.javamalls.platform.vo.GoodsJsonVo;
import com.javamalls.platform.vo.RemoveIdJsonVo;
import com.utils.SendReqAsync;

@Controller
public class GoodsSellerAction {
    @Autowired
    private ISysConfigService             configService;
    @Autowired
    private IUserConfigService            userConfigService;
    @Autowired
    private IGoodsClassService            goodsClassService;
    @Autowired
    private IGoodsTypeService             goodsTypeService;
    @Autowired
    private IGoodsClassStapleService      goodsclassstapleService;
    @Autowired
    private IUserService                  userService;
    @Autowired
    private IAccessoryService             accessoryService;
    @Autowired
    private IUserGoodsClassService        userGoodsClassService;
    @Autowired
    private IGoodsService                 goodsService;
    @Autowired
    private IStoreService                 storeService;
    @Autowired
    private IGoodsBrandService            goodsBrandService;
    @Autowired
    private IGoodsItemService             goodsItemService;
    @Autowired
    private IGoodsSpecPropertyService     specPropertyService;
    @Autowired
    private IGoodsTypePropertyService     goodsTypePropertyService;
    @Autowired
    private IWaterMarkService             waterMarkService;
    @Autowired
    private IAlbumService                 albumService;
    @Autowired
    private IReportService                reportService;
    @Autowired
    private IEvaluateService              evaluateService;
    @Autowired
    private ITransportService             transportService;
    @Autowired
    private IPaymentService               paymentService;
    @Autowired
    private TransportTools                transportTools;
    @Autowired
    private StoreTools                    storeTools;
    @Autowired
    private StoreViewTools                storeViewTools;
    @Autowired
    private GoodsViewTools                goodsViewTools;
    @Autowired
    private IGoodsRetrieveService         goodsRetrieveService;
    @Autowired
    private IGoodsRetrievePropertyService goodsRetrievePropertyService;
    @Autowired
    private SendReqAsync                  sendReqAsync;
    @Autowired
    private IGoodsLabelService            goodsLabelService;
    @Autowired
    private IGoodslabelGoodsService       goodsLabelGoodsService;
    
    @SecurityMapping(title = "发布商品第一步", value = "/seller/add_goods_first.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/add_goods_first.htm" })
    public ModelAndView add_goods_first(HttpServletRequest request, HttpServletResponse response,
                                        String id) {
        ModelAndView mv = new JModelAndView("error.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        List<Payment> payments = new ArrayList<Payment>();
        Map<String, Object> params = new HashMap<String, Object>();
        if (this.configService.getSysConfig().getConfig_payment_type() == 1) {
            params.put("type", "admin");
            params.put("install", Boolean.valueOf(true));
            payments = this.paymentService.query(
                "select obj from Payment obj where obj.type=:type and obj.install=:install",
                params, -1, -1);
        } else {
            params.put("store_id", user.getStore().getId());
            params.put("install", Boolean.valueOf(true));
            payments = this.paymentService
                .query(
                    "select obj from Payment obj where obj.store.id=:store_id and obj.install=:install",
                    params, -1, -1);
        }
        if (payments.size() == 0) {
            mv.addObject("op_title", "请至少开通一种支付方式");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/payment.htm");
            return mv;
        }
        request.getSession(false).removeAttribute("goods_class_info");
        int store_status = user.getStore() == null ? 0 : user.getStore().getStore_status();
        if (store_status == 2) {
            StoreGrade grade = user.getStore().getGrade();
            int user_goods_count = user.getStore().getGoods_list().size();
            if ((grade.getGoodsCount() == 0) || (user_goods_count < grade.getGoodsCount())) {
                List<GoodsClass> gcs = this.goodsClassService
                    .query(
                        "select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc",
                        null, -1, -1);
                mv = new JModelAndView("user/default/usercenter/add_goods_first.html",
                    this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0,
                    request, response);
                params.clear();
                params.put("store_id", user.getStore().getId());
                List<GoodsClassStaple> staples = this.goodsclassstapleService
                    .query(
                        "select obj from GoodsClassStaple obj where obj.store.id=:store_id order by obj.createtime desc",
                        params, -1, -1);
                mv.addObject("staples", staples);
                mv.addObject("gcs", gcs);
                mv.addObject("id", CommUtil.null2String(id));
            } else {
                mv.addObject("op_title", "您的店铺等级只允许上传" + grade.getGoodsCount() + "件商品!");
                mv.addObject("url", CommUtil.getURL(request) + "/seller/store_grade.htm");
            }
        }
        if (store_status == 0) {
            mv.addObject("op_title", "您尚未开通店铺，不能发布商品");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
        }
        if (store_status == 1) {
            mv.addObject("op_title", "您的店铺在审核中，不能发布商品");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
        }
        if (store_status == 3) {
            mv.addObject("op_title", "您的店铺已被关闭，不能发布商品");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
        }
        if (store_status == 5) {
            mv.addObject("op_title", "您的店铺延期开通，不能发布商品");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
        }
        if (store_status == -1) {
            mv.addObject("op_title", "您的店铺未通过审核，不能发布商品");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "商品运费模板分页显示", value = "/seller/goods_transport.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/goods_transport.htm" })
    public ModelAndView goods_transport(HttpServletRequest request, HttpServletResponse response,
                                        String currentPage, String orderBy, String orderType,
                                        String ajax) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/goods_transport.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if (CommUtil.null2Boolean(ajax)) {
            mv = new JModelAndView("user/default/usercenter/goods_transport_list.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0,
                request, response);
        }
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        String params = "";
        TransportQueryObject qo = new TransportQueryObject(currentPage, mv, orderBy, orderType);
        Store store = store = this.userService.getObjById(
            SecurityUserHolder.getCurrentUser().getId()).getStore();
        qo.addQuery("obj.store.id", new SysMap("store_id", store.getId()), "=");
        qo.setPageSize(Integer.valueOf(1));
        IPageList pList = this.transportService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/seller/goods_transport.htm", "", params, pList,
            mv);
        mv.addObject("transportTools", this.transportTools);
        return mv;
    }

    //根据模板查询规格
    @SecurityMapping(title = "发布商品第二步", value = "/seller/add_goods_second.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/add_goods_second_specquery.htm" })
    public ModelAndView add_goods_second_specquery(HttpServletRequest request,
                                                   HttpServletResponse response, String id) {
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/add_goods_second_specquery.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        GoodsType objById = new GoodsType();
        try {
            objById = this.goodsTypeService.getObjById(Long.valueOf(id));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mv.addObject("goodsType", objById);
        return mv;
    }

    @SecurityMapping(title = "发布商品第二步", value = "/seller/add_goods_second.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/add_goods_second.htm" })
    public ModelAndView add_goods_second(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("error.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        if (SecurityUserHolder.getCurrentUser() == null) {
            mv = new JModelAndView("login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            return mv;
        }
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        int store_status = this.storeService.getObjByProperty("id", user.getStore().getId())
            .getStore_status();
        if (store_status == 2) {
            mv = new JModelAndView("user/default/usercenter/add_goods_second.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0,
                request, response);

            //查询模板

            Map<String, Object> queryMap = new HashMap<String, Object>();
            queryMap.put("store_id", user.getStore().getId());
            List<GoodsType> goodsTypes = this.goodsTypeService
                .query(
                    "select obj from GoodsType obj where obj.disabled = false and obj.store_id=:store_id",
                    queryMap, -1, -1);
            mv.addObject("goodsTypes", goodsTypes);

            List<GoodsBrand> gbs = this.goodsBrandService
                .query(
                    "select obj from GoodsBrand obj where  obj.disabled = false and obj.audit=1 and obj.store.id=:store_id order by obj.first_word asc,obj.name asc",
                    queryMap, -1, -1);
            mv.addObject("gbs", gbs);
            String path = request.getSession().getServletContext().getRealPath("/")
                          + File.separator + "upload" + File.separator + "store" + File.separator
                          + user.getStore().getId();
            double img_remain_size = 0.0D;
            if (user.getStore().getGrade().getSpaceSize() > 0.0F) {
                img_remain_size = user.getStore().getGrade().getSpaceSize()
                                  - CommUtil.div(Double.valueOf(CommUtil.fileSize(new File(path))),
                                      Integer.valueOf(1024));
            }
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("user_id", user.getId());
            params.put("display", Boolean.valueOf(true));
            List<UserGoodsClass> ugcs = this.userGoodsClassService
                .query(
                    "select obj from UserGoodsClass obj where obj.disabled = false and obj.user.id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc",
                    params, -1, -1);

            mv.addObject("ugcs", ugcs);
            //查询扩展属性
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("disabled", false);
            map.put("type", 2);
            String sql = "select obj from GoodsRetrieve obj where disabled=:disabled and type=:type order by sequence desc,id asc";
            List<GoodsRetrieve> goodsRetrieves = this.goodsRetrieveService.query(sql, map, -1, -1);
            mv.addObject("extendedAttributes", goodsRetrieves);

            mv.addObject("img_remain_size", Double.valueOf(img_remain_size));
            mv.addObject("imageSuffix", this.storeViewTools.genericImageSuffix(this.configService
                .getSysConfig().getImageSuffix()));
            String goods_session = CommUtil.randomString(32);
            mv.addObject("goods_session", goods_session);
            request.getSession(false).setAttribute("goods_session", goods_session);
        }
        if (store_status == 0) {
            mv.addObject("op_title", "您尚未开通店铺，不能发布商品");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
        }
        if (store_status == 1) {
            mv.addObject("op_title", "您的店铺在审核中，不能发布商品");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
        }
        if (store_status == 3) {
            mv.addObject("op_title", "您的店铺已被关闭，不能发布商品");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
        }
        if (store_status == 5) {
            mv.addObject("op_title", "您的店铺延期开通，不能发布商品");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
        }
        if (store_status == -1) {
            mv.addObject("op_title", "您的店铺未通过审核，不能发布商品");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
        }
        Map<String, Object> mapGoodsLabel = new HashMap<String, Object>();
        //登录用户
        if (user!=null && !"".equals(user)) {
    		if (user.getSalesManState()!=null && user.getSalesManState()==1) {
    			mapGoodsLabel.put("user_id", user.getParent().getId());
			}else{
				mapGoodsLabel.put("user_id", user.getId());
			}
		}
        List<GoodsLabel> listGoodsLabel=goodsLabelService.query(
        		"select obj from GoodsLabel obj where obj.disabled = false and obj.status = 0 and obj.createuser.id=:user_id order by sequence  ASC",
        		mapGoodsLabel, -1, -1);
        mv.addObject("listGoodsLabel", listGoodsLabel);
        return mv;
    }

    //查询检索属性
    @SecurityMapping(title = "产品规格显示", value = "/seller/goods_inventory.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/query_serach_propery.htm" })
    public ModelAndView query_serach_propery(HttpServletRequest request,
                                             HttpServletResponse response) {
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/add_goods_search_propery.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        //查询检索属性
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("disabled", false);
        map.put("type", 1);
        String sql = "select obj from GoodsRetrieve obj where disabled=:disabled and type=:type order by sequence desc";
        List<GoodsRetrieve> goodsRetrieves = this.goodsRetrieveService.query(sql, map, -1, -1);
        Map<String, List<GoodsRetrieveProperty>> retriePro = new HashMap<String, List<GoodsRetrieveProperty>>();
        if (goodsRetrieves != null && goodsRetrieves.size() > 0) {
            Store store = SecurityUserHolder.getCurrentUser().getStore();
            for (GoodsRetrieve goodsRetrieve : goodsRetrieves) {
                //查询属性值
                Map<String, Object> qmap = new HashMap<String, Object>();
                qmap.put("store_id", store.getId());
                qmap.put("retrieve_id", goodsRetrieve.getId());
                List<GoodsRetrieveProperty> list = goodsRetrievePropertyService
                    .query(
                        "select obj from GoodsRetrieveProperty obj where obj.disabled=false and obj.store.id=:store_id and obj.retrieve.id=:retrieve_id ",
                        qmap, -1, -1);
                if (list != null && list.size() > 0) {
                    retriePro.put(goodsRetrieve.getName(), list);
                }
            }

        }
        mv.addObject("retriePro", retriePro);
        return mv;
    }

    @SecurityMapping(title = "产品规格显示", value = "/seller/goods_inventory.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/goods_inventory.htm" })
    public ModelAndView goods_inventory(HttpServletRequest request, HttpServletResponse response,
                                        String goods_spec_ids, String store_price) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/goods_inventory.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String[] spec_ids = goods_spec_ids.split(",");
        List<GoodsSpecProperty> gsps = new ArrayList<GoodsSpecProperty>();

        GoodsSpecProperty gsp;
        for (String spec_id : spec_ids) {
            if (!spec_id.equals("")) {
                gsp = this.specPropertyService.getObjById(Long.valueOf(Long.parseLong(spec_id)));
                gsps.add(gsp);
            }
        }
        Set<GoodsSpecification> specs = new HashSet<GoodsSpecification>();
        for (GoodsSpecProperty gsp2 : gsps) {
            specs.add(gsp2.getSpec());
        }

        /*for (GoodsSpecification spec : specs) {
            spec.getProperties().clear();
            for (GoodsSpecProperty gsp3 : spec.getProperties()) {
                if (gsp3.getSpec().getId().equals(spec.getId())) {
                    spec.getProperties().add(gsp3);
                }
            }
        }*/

        GoodsSpecification[] spec_list = (GoodsSpecification[]) specs
            .toArray(new GoodsSpecification[specs.size()]);
        Arrays.sort(spec_list, new Comparator() {
            public int compare(Object obj1, Object obj2) {
                GoodsSpecification a = (GoodsSpecification) obj1;
                GoodsSpecification b = (GoodsSpecification) obj2;
                if (a.getSequence() == b.getSequence()) {
                    return 0;
                }
                return a.getSequence() > b.getSequence() ? 1 : -1;
            }
        });
        Object gsp_list = generic_spec_property(specs, spec_ids);
        mv.addObject("specs", Arrays.asList(spec_list));
        mv.addObject("gsps", gsp_list);
        mv.addObject("spec_ids", spec_ids);
        mv.addObject("store_price", CommUtil.null2Double(store_price));
        return mv;
    }

    /*
     * 库存编辑
     */
    @SecurityMapping(title = "产品规格显示", value = "/seller/goods_inventory.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/goods_inventory_edit.htm" })
    public ModelAndView goods_inventory_edit(HttpServletRequest request,
                                             HttpServletResponse response, String goodsId) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/goods_inventory_edit.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if (SecurityUserHolder.getCurrentUser() == null) {
            mv = new JModelAndView("login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            return mv;
        }
        Goods objById = this.goodsService.getObjById(CommUtil.null2Long(goodsId));
        mv.addObject("goods", objById);
        return mv;
    }

    /*
     * 库存编辑
     */
    @SecurityMapping(title = "产品规格显示", value = "/seller/goods_inventory.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/goods_inventory_save.htm" })
    @Transactional
    public void goods_inventory_save(HttpServletRequest request, HttpServletResponse response,
                                     String id) {

        String str = "0";
        if (SecurityUserHolder.getCurrentUser() == null) {
            str = "1";
        }
        Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
        if (goods != null && "0".equals(str)) {
            List<GoodsItem> goods_item_list = goods.getGoods_item_list();
            if (goods_item_list != null && goods_item_list.size() > 1) {
                for (GoodsItem goodsItem : goods_item_list) {
                    String inventory_count = request.getParameter("inventory_count_"
                                                                  + goodsItem.getId());
                    goodsItem.setGoods_inventory(CommUtil.null2Int(inventory_count));
                    this.goodsItemService.update(goodsItem);
                }
            } else if (goods_item_list != null && goods_item_list.size() == 1) {
                GoodsItem goodsItem = goods_item_list.get(0);
                String goods_inventory = request.getParameter("goods_inventory");
                goodsItem.setGoods_inventory(CommUtil.null2Int(goods_inventory));
                goods.setGoods_inventory(CommUtil.null2Int(goods_inventory));

                this.goodsItemService.update(goodsItem);
                this.goodsService.update(goods);
            }
        }

        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static GoodsSpecProperty[][] list2group(List<List<GoodsSpecProperty>> list) {
        GoodsSpecProperty[][] gps = new GoodsSpecProperty[list.size()][];
        for (int i = 0; i < list.size(); i++) {
            gps[i] = ((GoodsSpecProperty[]) ((List) list.get(i))
                .toArray(new GoodsSpecProperty[((List) list.get(i)).size()]));
        }
        return gps;
    }

    private List<List<GoodsSpecProperty>> generic_spec_property(Set<GoodsSpecification> specs,
                                                                String[] spec_ids) {
        List<List<GoodsSpecProperty>> result_list = new ArrayList<List<GoodsSpecProperty>>();
        List<List<GoodsSpecProperty>> list = new ArrayList<List<GoodsSpecProperty>>();
        int max = 1;
        List<GoodsSpecProperty> goodsps = null;
        for (GoodsSpecification spec : specs) {
            goodsps = new ArrayList<GoodsSpecProperty>();
            for (GoodsSpecProperty goodsSpecProperty : spec.getProperties()) {
                for (String id : spec_ids) {
                    if (CommUtil.null2Long(id).longValue() == goodsSpecProperty.getId().longValue()) {
                        goodsps.add(goodsSpecProperty);
                    }
                }
            }
            list.add(goodsps);
        }
        GoodsSpecProperty[][] gsps = list2group(list);
        for (int i = 0; i < gsps.length; i++) {
            max *= gsps[i].length;
        }
        for (int i = 0; i < max; i++) {
            List<GoodsSpecProperty> temp_list = new ArrayList<GoodsSpecProperty>();
            int temp = 1;
            for (int j = 0; j < gsps.length; j++) {
                temp *= gsps[j].length;
                temp_list.add(j, gsps[j][(i / (max / temp) % gsps[j].length)]);
            }
            GoodsSpecProperty[] temp_gsps = (GoodsSpecProperty[]) temp_list
                .toArray(new GoodsSpecProperty[temp_list.size()]);
            Arrays.sort(temp_gsps, new Comparator() {
                public int compare(Object obj1, Object obj2) {
                    GoodsSpecProperty a = (GoodsSpecProperty) obj1;
                    GoodsSpecProperty b = (GoodsSpecProperty) obj2;
                    if (a.getSpec().getSequence() == b.getSpec().getSequence()) {
                        return 0;
                    }
                    return a.getSpec().getSequence() > b.getSpec().getSequence() ? 1 : -1;
                }
            });
            result_list.add(Arrays.asList(temp_gsps));
        }
        return result_list;
    }

    @RequestMapping({ "/seller/swf_upload.htm" })
    public void swf_upload(HttpServletRequest request, HttpServletResponse response,
                           String user_id, String album_id) {
        User user = this.userService.getObjById(CommUtil.null2Long(user_id));
        String path = this.storeTools.createUserFolder(request, this.configService.getSysConfig(),
            user.getStore());
        String url = this.storeTools.createUserFolderURL(this.configService.getSysConfig(),
            user.getStore());
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest.getFile("imgFile");
        double fileSize = Double.valueOf(file.getSize()).doubleValue();
        fileSize /= 1048576.0D;
        double csize = CommUtil.fileSize(new File(path));
        double remainSpace = 0.0D;
        if (user.getStore().getGrade().getSpaceSize() != 0.0F) {
            remainSpace = (user.getStore().getGrade().getSpaceSize() * 1024.0F - csize) * 1024.0D;
        } else {
            remainSpace = 10000000.0D;
        }
        Map<String, Object> json_map = new HashMap<String, Object>();
        if (remainSpace > fileSize) {
            try {
                Map<String, Object> map = CommUtil.saveFileToServer(request, "imgFile", path, null,
                    null);
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("store_id", user.getStore().getId());
                List<WaterMark> wms = this.waterMarkService.query(
                    "select obj from WaterMark obj where obj.store.id=:store_id", params, -1, -1);
                if (wms.size() > 0) {
                    WaterMark mark = (WaterMark) wms.get(0);
                    if (mark.isWm_image_open()) {
                        String pressImg = request.getSession().getServletContext().getRealPath("")
                                          + File.separator + mark.getWm_image().getPath()
                                          + File.separator + mark.getWm_image().getName();
                        String targetImg = path + File.separator + map.get("fileName");
                        int pos = mark.getWm_image_pos();
                        float alpha = mark.getWm_image_alpha();
                        CommUtil.waterMarkWithImage(pressImg, targetImg, pos, alpha);
                    }
                    if (mark.isWm_text_open()) {
                        String targetImg = path + File.separator + map.get("fileName");
                        int pos = mark.getWm_text_pos();
                        String text = mark.getWm_text();
                        String markContentColor = mark.getWm_text_color();
                        CommUtil.waterMarkWithText(targetImg, targetImg, text, markContentColor,
                            new Font(mark.getWm_text_font(), 1, mark.getWm_text_font_size()), pos,
                            100.0F);
                    }
                }
                Accessory image = new Accessory();
                image.setCreatetime(new Date());
                image.setExt((String) map.get("mime"));
                image.setPath(url);
                image.setWidth(CommUtil.null2Int(map.get("width")));
                image.setHeight(CommUtil.null2Int(map.get("height")));
                image.setName(CommUtil.null2String(map.get("fileName")));
                image.setUser(user);
                Album album = null;
                if ((album_id != null) && (!album_id.equals(""))) {
                    album = this.albumService.getObjById(CommUtil.null2Long(album_id));
                } else {
                    album = this.albumService.getDefaultAlbum(CommUtil.null2Long(user_id));
                    if (album == null) {
                        album = new Album();
                        album.setCreatetime(new Date());
                        album.setAlbum_name("默认相册");
                        album.setAlbum_sequence(-10000);
                        album.setAlbum_default(true);
                        this.albumService.save(album);
                    }
                }
                image.setAlbum(album);
                this.accessoryService.save(image);
                json_map.put("url", CommUtil.getURL(request) + "/" + url + "/" + image.getName());
                json_map.put("id", image.getId());
                json_map.put("remainSpace",
                    Double.valueOf(remainSpace == 10000.0D ? 0.0D : remainSpace));

                String ext = image.getExt().indexOf(".") < 0 ? "." + image.getExt() : image
                    .getExt();
                String source = request.getSession().getServletContext().getRealPath("/")
                                + image.getPath() + File.separator + image.getName();
                String target = source + "_small" + ext;
                CommUtil.createSmall(source, target, this.configService.getSysConfig()
                    .getSmallWidth(), this.configService.getSysConfig().getSmallHeight());

                String midext = image.getExt().indexOf(".") < 0 ? "." + image.getExt() : image
                    .getExt();
                String midtarget = source + "_middle" + ext;
                CommUtil.createSmall(source, midtarget, this.configService.getSysConfig()
                    .getMiddleWidth(), this.configService.getSysConfig().getMiddleHeight());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            json_map.put("url", "");
            json_map.put("id", "");
            json_map.put("remainSpace", Integer.valueOf(0));
        }
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(Json.toJson(json_map));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SecurityMapping(title = "商品图片删除", value = "/seller/goods_image_del.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/goods_image_del.htm" })
    public void goods_image_del(HttpServletRequest request, HttpServletResponse response,
                                String image_id) {
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        String path = this.storeTools.createUserFolder(request, this.configService.getSysConfig(),
            user.getStore());
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            Accessory img = this.accessoryService.getObjById(CommUtil.null2Long(image_id));
            for (Goods goods : img.getGoods_main_list()) {
                goods.setGoods_main_photo(null);
                this.goodsService.update(goods);
            }
            for (Goods goods1 : img.getGoods_list()) {
                goods1.getGoods_photos().remove(img);
                this.goodsService.update(goods1);
            }
            boolean ret = this.accessoryService.delete(img.getId());
            if (ret) {
                CommUtil.del_acc(request, img);
            }
            double csize = CommUtil.fileSize(new File(path));
            double remainSpace = 10000.0D;
            if (user.getStore().getGrade().getSpaceSize() != 0.0F) {
                remainSpace = CommUtil.div(
                    Double.valueOf(user.getStore().getGrade().getSpaceSize() * 1024.0F - csize),
                    Integer.valueOf(1024));
            }
            map.put("result", Boolean.valueOf(ret));
            map.put("remainSpace", Double.valueOf(remainSpace));
            PrintWriter writer = response.getWriter();
            writer.print(Json.toJson(map));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String clearContent(String inputString) {
        String htmlStr = inputString;
        String textStr = "";
        try {
            String regEx_script = "<[//s]*?script[^>]*?>[//s//S]*?<[//s]*?///[//s]*?script[//s]*?>";
            String regEx_style = "<[//s]*?style[^>]*?>[//s//S]*?<[//s]*?///[//s]*?style[//s]*?>";
            String regEx_html = "<[^>]+>";
            String regEx_html1 = "<[^>]+";
            Pattern p_script = Pattern.compile(regEx_script, 2);
            Matcher m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll("");

            Pattern p_style = Pattern.compile(regEx_style, 2);
            Matcher m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll("");

            Pattern p_html = Pattern.compile(regEx_html, 2);
            Matcher m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll("");

            Pattern p_html1 = Pattern.compile(regEx_html1, 2);
            Matcher m_html1 = p_html1.matcher(htmlStr);
            htmlStr = m_html1.replaceAll("");

            textStr = htmlStr;
        } catch (Exception e) {
            System.err.println("Html2Text: " + e.getMessage());
        }
        return textStr;
    }

    @SecurityMapping(title = "发布商品第三步", value = "/seller/add_goods_finish.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/add_goods_finish.htm" })
    @Transactional
    public ModelAndView add_goods_finish(HttpServletRequest request, HttpServletResponse response,
                                         String id, String goods_class_id, String image_ids,
                                         String goods_main_img_id, String user_class_ids,
                                         String goods_brand_id, String goods_spec_ids,
                                         String goods_properties, String intentory_details,
                                         String goods_session, String transport_type,
                                         String transport_id, String goods_status,
                                         String goods_hot_status, String goods_news_status,
                                         String goods_recommend, String searchAttribute,
                                         String extendedAttribute, String supplierAttr,
                                         String goods_inventory,String label_ids) {
        ModelAndView mv = null;
        String goods_session1 = CommUtil.null2String(request.getSession(false).getAttribute(
            "goods_session"));
        if (goods_session1.equals("")) {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "禁止重复提交表单");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
        } else if (goods_session1.equals(goods_session)) {
            if ((id == null) || (id.equals(""))) {
                mv = new JModelAndView("user/default/usercenter/add_goods_finish.html",
                    this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0,
                    request, response);
            } else {
                mv = new JModelAndView("success.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), 1, request, response);
                mv.addObject("op_title", "商品编辑成功");
                User currentUser = SecurityUserHolder.getCurrentUser();
                if (currentUser != null) {
                    Store store = currentUser.getStore();
                    if (store.getDomainName_info() != null
                        && !"".equals(store.getDomainName_info())) {
                        mv.addObject("url", "http://" + store.getDomainName_info()
                                            + "/goods.htm?id=" + id);
                    } else {
                        mv.addObject("url", CommUtil.getURL(request) + "/store/" + store.getId()
                                            + ".htm/goods.htm?id=" + id);
                    }
                } else {
                    mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
                }

            }
            WebForm wf = new WebForm();
            Goods goods = null;
            if (id.equals("")) {
                goods = (Goods) wf.toPo(request, Goods.class);
                goods.setGoods_current_price(goods.getStore_price());

                goods.setCreatetime(new Date());
                User user = this.userService
                    .getObjById(SecurityUserHolder.getCurrentUser().getId());
                goods.setGoods_store(user.getStore());
            } else {
                Goods obj = this.goodsService.getObjById(Long.valueOf(Long.parseLong(id)));
                goods = (Goods) wf.toPo(request, obj);
                goods.setGoods_current_price(goods.getStore_price());

            }

            if (goods_status == null || "".equals(goods_status)) {
                goods.setGoods_status(1);
            } else {
                goods.setGoods_status(CommUtil.null2Int(goods_status));
            }
            goods.setGoods_hot_status(CommUtil.null2Int(goods_hot_status));
            goods.setGoods_news_status(CommUtil.null2Int(goods_news_status));
            if (goods_recommend != null && "true".equals(goods_recommend)) {
                goods.setGoods_recommend(true);
            } else {
                goods.setGoods_recommend(false);
            }

            if ((goods.getCombin_status() != 2) && (goods.getDelivery_status() != 2)
                && (goods.getBargain_status() != 2) && (goods.getActivity_status() != 2)) {
                goods.setGoods_current_price(goods.getStore_price());
            }
            goods.setGoods_name(clearContent(goods.getGoods_name()));
            /*GoodsClass gc = this.goodsClassService.getObjById(Long.valueOf(Long
                .parseLong(goods_class_id)));
            goods.setGc(gc);*/
            Accessory main_img = null;
            if ((goods_main_img_id != null) && (!goods_main_img_id.equals(""))) {
                main_img = this.accessoryService.getObjById(Long.valueOf(Long
                    .parseLong(goods_main_img_id)));
            }
            goods.setGoods_main_photo(main_img);
            goods.getGoods_ugcs().clear();
            String[] ugc_ids = user_class_ids.split(",");
            int localUserGoodsClass2 = ugc_ids.length;
            for (int localUserGoodsClass1 = 0; localUserGoodsClass1 < localUserGoodsClass2; localUserGoodsClass1++) {
                String ugc_id = ugc_ids[localUserGoodsClass1];
                if (!ugc_id.equals("")) {
                    UserGoodsClass ugc = this.userGoodsClassService.getObjById(Long.valueOf(Long
                        .parseLong(ugc_id)));
                    goods.getGoods_ugcs().add(ugc);
                }
            }
            String[] img_ids = image_ids.split(",");
            goods.getGoods_photos().clear();
            int localUserGoodsClass3 = img_ids.length;
            for (localUserGoodsClass2 = 0; localUserGoodsClass2 < localUserGoodsClass3; localUserGoodsClass2++) {
                String img_id = img_ids[localUserGoodsClass2];
                if (!img_id.equals("")) {
                    Accessory img = this.accessoryService.getObjById(Long.valueOf(Long
                        .parseLong(img_id)));
                    goods.getGoods_photos().add(img);
                }
            }
            if ((goods_brand_id != null) && (!goods_brand_id.equals(""))) {
                GoodsBrand goods_brand = this.goodsBrandService.getObjById(Long.valueOf(Long
                    .parseLong(goods_brand_id)));
                goods.setGoods_brand(goods_brand);
            }
            goods.getGoods_specs().clear();
            String[] spec_ids = goods_spec_ids.split(",");
            int ugc = spec_ids.length;
            for (localUserGoodsClass3 = 0; localUserGoodsClass3 < ugc; localUserGoodsClass3++) {
                String spec_id = spec_ids[localUserGoodsClass3];
                if (!spec_id.equals("")) {
                    GoodsSpecProperty gsp = this.specPropertyService.getObjById(Long.valueOf(Long
                        .parseLong(spec_id)));
                    goods.getGoods_specs().add(gsp);
                }
            }
            Object maps = new ArrayList();
            String[] properties = goods_properties.split(";");
            int gsp = properties.length;
            String[] list;
            for (int img = 0; img < gsp; img++) {
                String property = properties[img];
                if (!property.equals("")) {
                    list = property.split(",");
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("id", list[0]);
                    map.put("val", list[1]);
                    map.put(
                        "name",
                        this.goodsTypePropertyService.getObjById(
                            Long.valueOf(Long.parseLong(list[0]))).getName());
                    ((List) maps).add(map);
                }
            }
            goods.setGoods_property(Json.toJson(maps, JsonFormat.compact()));
            ((List) maps).clear();

            //   goods.setGoods_inventory_detail(Json.toJson(maps, JsonFormat.compact()));

            //设置检索属性
            goods.setRetrieval_ids(searchAttribute);
            //设置扩展属性
            List<HashMap<String, Object>> extMaps = new ArrayList<HashMap<String, Object>>();
            if (extendedAttribute != null && !"".equals(extendedAttribute)) {
                String[] strings = extendedAttribute.split(";");

                for (int i = 0; i < strings.length; i++) {
                    String[] strs = strings[i].split(",");
                    if (strs != null && strs.length == 3) {
                        HashMap<String, Object> smap = new HashMap<String, Object>();
                        smap.put("id", strs[0]);
                        smap.put("name", strs[1]);
                        smap.put("val", strs[2]);
                        extMaps.add(smap);
                    }
                }
            }

            //设置供货商信息
            Map<String, Object> supplierMaps = new HashMap<String, Object>();
            if (supplierAttr != null && !"".equals(supplierAttr)) {
                String[] strs = supplierAttr.split(";");
                for (int i = 0; i < strs.length; i++) {
                    String[] ss = strs[i].split(",");
                    if (ss != null && ss.length == 2) {
                        supplierMaps.put(ss[0], ss[1]);
                    }
                }
            }
            goods.setSupplier_info(Json.toJson(supplierMaps, JsonFormat.compact()));

            goods.setExtendedAttributes(Json.toJson(extMaps, JsonFormat.compact()));

            if (CommUtil.null2Int(transport_type) == 0) {
                Transport trans = this.transportService
                    .getObjById(CommUtil.null2Long(transport_id));
                goods.setTransport(trans);
            }
            if (CommUtil.null2Int(transport_type) == 1) {
                goods.setTransport(null);
            }
            goods.setGoods_inventory(CommUtil.null2Int(goods_inventory));

            Date now = new Date();
            if (id.equals("")) {
                goods.setStorage_status(0);
                this.goodsService.save(goods);
                //商品与商品标签关联
                if (label_ids!=null && !"".equals(label_ids)) {
                	String[] labelIds = label_ids.split(",");
                    for (String label_id : labelIds) {
                        if (!label_id.equals("")) {
                        	//商品标签
                            GoodsLabel goodsLabel = this.goodsLabelService.getObjById(Long.valueOf(Long.parseLong(label_id)));
                            //商品标签与商品关联
                            GoodslabelGoods goodslabelGoods=new GoodslabelGoods();
                            goodslabelGoods.setCreatetime(now);
                            goodslabelGoods.setDisabled(false);
                            goodslabelGoods.setGoods(goods);
                            goodslabelGoods.setGoodslabel(goodsLabel);
                            this.goodsLabelGoodsService.save(goodslabelGoods);
                        }
                    }
                    
				}
           	 	
                
                // List<GoodsItem> goodsItemlist=new ArrayList<GoodsItem>();
                List<GoodsItem> goodsItems = new ArrayList<GoodsItem>();
                if (intentory_details != null && !"".equals(intentory_details)) {
                    String[] inventory_list = intentory_details.split(";");

                    if (inventory_list != null && inventory_list.length > 0) {
                        for (gsp = 0; gsp < inventory_list.length; gsp++) {
                            String inventory = inventory_list[gsp];
                            if (!inventory.equals("")) {
                                String[] listarr = inventory.split(",");
                                /* Map<String, Object> map = new HashMap<String, Object>();
                                 map.put("id", listarr[0]);
                                 map.put("count", listarr[1]);
                                 map.put("price", listarr[2]);
                                 ((List) maps).add(map);*/
                                GoodsItem item = new GoodsItem();
                                item.setCreatetime(now);
                                item.setDisabled(false);
                                item.setSpec_combination(listarr[0]);
                                item.setGoods_inventory(Integer.valueOf(listarr[1]));
                                item.setGoods_price(new BigDecimal(listarr[2]));
                                item.setBar_code(listarr[3]);
                                item.setSelf_code(listarr[4]);
                                item.setSpec_info(listarr[5]);
                                //item.setMarket_price(new BigDecimal(listarr[6]));
                                item.setDist_price(new BigDecimal(listarr[6]));//分销价
                                item.setPurchase_price(new BigDecimal(listarr[7]));//进货价
                                item.setGoods(goods);
                                item.setStep_price_state(0);
                                item.setStatus(1);
                                this.goodsItemService.save(item);

                                goodsItems.add(item);
                            }
                        }
                    }
                } else {
                    GoodsItem item = new GoodsItem();
                    item.setCreatetime(now);
                    item.setDisabled(false);
                    item.setGoods_inventory(CommUtil.null2Int(goods_inventory));
                    String string = request.getParameter("store_price");
                    if (string == null || "".equals(string)) {
                        string = "0";
                    }
                    item.setGoods_price(new BigDecimal(string));
                    String string2 = request.getParameter("goods_price");
                    if (string2 == null || "".equals(string2)) {
                        string2 = "0";
                    }
                    //item.setMarket_price(new BigDecimal(string2));
                    item.setDist_price(new BigDecimal(string2));//分销价
                    item.setPurchase_price(new BigDecimal(string2));//进货价
                    item.setBar_code(request.getParameter("bar_code"));
                    item.setSelf_code(request.getParameter("self_code"));
                    item.setGoods(goods);
                    item.setStatus(0);
                    item.setStep_price_state(0);
                    this.goodsItemService.save(item);

                    goodsItems.add(item);
                }
                //  goods.setGoods_item_list(goodsItemlist);

                //调用服装接口
                GoodsJsonVo goodsJsonVo = new GoodsJsonVo();
                goodsJsonVo.setId(goods.getId());
                goodsJsonVo.setCreatetime(goods.getCreatetime());
                goodsJsonVo.setDisabled(goods.isDisabled());
                goodsJsonVo.setSeo_keywords(goods.getSeo_keywords());
                goodsJsonVo.setSeo_description(goods.getSeo_description());
                goodsJsonVo.setGoods_name(goods.getGoods_name());
                goodsJsonVo.setGoods_price(goods.getGoods_price());
                goodsJsonVo.setStore_price(goods.getStore_price());
                goodsJsonVo.setGoods_inventory(goods.getGoods_inventory());
                goodsJsonVo.setInventory_type(goods.getInventory_type());
                goodsJsonVo.setGoods_salenum(goods.getGoods_salenum());
                goodsJsonVo.setGoods_details(goods.getGoods_details());
                goodsJsonVo.setGoods_recommend(goods.isGoods_recommend());
                goodsJsonVo.setGoods_click(goods.getGoods_click());
                goodsJsonVo.setGoods_collect(goods.getGoods_collect());

                goodsJsonVo.setGoods_store_id(goods.getGoods_store().getId());

                goodsJsonVo.setGoods_status(goods.getGoods_status());
                goodsJsonVo.setGoods_transfee(goods.getGoods_transfee());
                Accessory goods_main_photo = goods.getGoods_main_photo();
                if (goods_main_photo != null) {
                    goodsJsonVo.setGoods_main_photo_url(goods_main_photo.getPath() + "/"
                                                        + goods_main_photo.getName());
                }
                List<Accessory> goods_photos = goods.getGoods_photos();
                String goods_pic_url = "";
                if (goods_photos != null && goods_photos.size() > 0) {
                    for (Accessory accessory : goods_photos) {
                        goods_pic_url += accessory.getPath() + "/" + accessory.getName() + ";";
                    }
                }
                goodsJsonVo.setGoods_photos_url(goods_pic_url);
                List<UserGoodsClass> goods_ugcs = goods.getGoods_ugcs();
                if (goods_ugcs != null && goods_ugcs.size() > 0) {
                    goodsJsonVo.setGoods_ugcs_id(goods_ugcs.get(0).getId());
                }
                List<GoodsSpecProperty> goods_specs = goods.getGoods_specs();
                String str_spec = "";
                if (goods_specs != null && goods_specs.size() > 0) {
                    for (GoodsSpecProperty goodsSpecProperty : goods_specs) {
                        str_spec += goodsSpecProperty.getId() + ";";
                    }
                }
                goodsJsonVo.setGoods_specs(str_spec);
                GoodsBrand goods_brand = goods.getGoods_brand();
                if (goods_brand != null) {
                    goodsJsonVo.setGoods_brand_id(goods_brand.getId());
                }
                goodsJsonVo.setGoods_current_price(goods.getGoods_current_price());
                goodsJsonVo.setMail_trans_fee(goods.getMail_trans_fee());
                goodsJsonVo.setExpress_trans_fee(goods.getExpress_trans_fee());
                goodsJsonVo.setEms_trans_fee(goods.getEms_trans_fee());
                goodsJsonVo.setGoods_item_list(goodsItems);
                goodsJsonVo.setGoodsTypeId(goods.getGoodsTypeId());
                goodsJsonVo.setStorage_status(goods.getStorage_status());
                goodsJsonVo.setGoods_units(goods.getGoods_units());
                goodsJsonVo.setGoods_news_status(goods.getGoods_news_status());
                goodsJsonVo.setGoods_hot_status(goods.getGoods_hot_status());
                goodsJsonVo.setTotal_weight(goods.getTotal_weight());
                goodsJsonVo.setType_ratio(goods.getType_ratio());
                goodsJsonVo.setColor_ratio(goods.getColor_ratio());
                goodsJsonVo.setSize_ratio(goods.getSize_ratio());
                goodsJsonVo.setSingle_weight(goods.getSingle_weight());
                goodsJsonVo.setGoods_type(goods.getGoods_type());
                goodsJsonVo.setRetrieval_ids(goods.getRetrieval_ids());
                goodsJsonVo.setExtendedAttributes(goods.getExtendedAttributes());

                String write2JsonStr = JsonUtil.write2JsonStr(goodsJsonVo);

                sendReqAsync.sendMessageUtil(Constant.GOODS_URL_ADD, write2JsonStr, "新增商品");

                String goods_lucene_path = System.getProperty("user.dir") + File.separator
                                           + "luence" + File.separator + "goods";
                File file = new File(goods_lucene_path);
                if (!file.exists()) {
                    CommUtil.createFolder(goods_lucene_path);
                }
                LuceneVo vo = new LuceneVo();
                vo.setVo_id(goods.getId());
                vo.setVo_title(goods.getGoods_name());
                vo.setVo_content(goods.getGoods_details());
                vo.setVo_type("goods");
                vo.setVo_store_price(CommUtil.null2Double(goods.getStore_price()));
                vo.setVo_add_time(goods.getCreatetime().getTime());
                vo.setVo_goods_salenum(goods.getGoods_salenum());
                vo.setVo_goods_collect(goods.getGoods_collect());
                LuceneUtil lucene = LuceneUtil.instance();
                LuceneUtil.setIndex_path(goods_lucene_path);
                lucene.writeIndex(vo);
            } else {
                //还未入库,
                /* 	if(goods.getStorage_status()==0){
                 		List<GoodsItem> item_list1 = goods.getGoods_item_list();
                 		if(item_list1!=null&&item_list1.size()>0){
                 			GoodsItem goodsItem2 = item_list1.get(0);
                 			//如果不是无规格商品
                 			if(goodsItem2.getSpec_combination()!=null&&!"".equals(goodsItem2.getSpec_combination())){
                 				for (GoodsItem goodsItem : item_list1) {
                     				this.goodsItemService.delete(goodsItem.getId());
                				}
                 				
                 				goods.getGoods_item_list().clear();
                 			}
                 		}else{
                 			goods.getGoods_item_list().clear();
                 			
                 		}
                 	}*/

                //判断货品

                this.goodsService.update(goods);
                //逻辑删除 goods与goodsLable的关联
                Map<String, Object> glgMap = new HashMap<String, Object>();
                glgMap.put("goods_id", goods.getId());
                List<GoodslabelGoods> listGoodslabelGoods=goodsLabelGoodsService.query(
                		" select obj from GoodslabelGoods obj where obj.disabled = false and obj.goods.id=:goods_id", glgMap, -1, -1);
                if (listGoodslabelGoods!=null && listGoodslabelGoods.size()>0) {
                	for (GoodslabelGoods goodslabelGoods : listGoodslabelGoods) {
                		goodslabelGoods.setDisabled(true);
                		goodsLabelGoodsService.update(goodslabelGoods);
					}
					
				}
                //商品与商品标签关联
                if (label_ids!=null && !"".equals(label_ids)) {
                	String[] labelIds = label_ids.split(",");
                    for (String label_id : labelIds) {
                        if (!label_id.equals("")) {
                        	//商品标签
                            GoodsLabel goodsLabel = this.goodsLabelService.getObjById(Long.valueOf(Long.parseLong(label_id)));
                            //商品标签与商品关联
                            	GoodslabelGoods goodslabelGoods=new GoodslabelGoods();
                                goodslabelGoods.setCreatetime(now);
                                goodslabelGoods.setDisabled(false);
                                goodslabelGoods.setGoods(goods);
                                goodslabelGoods.setGoodslabel(goodsLabel);
                                this.goodsLabelGoodsService.save(goodslabelGoods);
                        }
                    }
                    
				}
                List<GoodsItem> item_list = goods.getGoods_item_list();

                if (intentory_details != null && !"".equals(intentory_details)) {

                    String[] inventory_list = intentory_details.split(";");
                    List<Long> item_list1 = new ArrayList<Long>();
                    for (int j = 0; j < inventory_list.length; j++) {
                        String inventory = inventory_list[j];
                        if (inventory != null && !inventory.equals("")) {
                            String[] listarr = inventory.split(",");
                            GoodsItem item = new GoodsItem();

                            for (int i = 0; i < item_list.size(); i++) {
                                if (listarr[0].equals(item_list.get(i).getSpec_combination())) {
                                    item = item_list.get(i);
                                    item_list1.add(item.getId());
                                    break;
                                }
                            }

                            item.setSpec_combination(listarr[0]);
                            item.setGoods_inventory(Integer.valueOf(listarr[1]));
                            item.setGoods_price(new BigDecimal(listarr[2]));
                            item.setBar_code(listarr[3]);
                            item.setSelf_code(listarr[4]);
                            item.setSpec_info(listarr[5]);
                            //item.setMarket_price(new BigDecimal(listarr[6]));
                            item.setDist_price(new BigDecimal(listarr[6]));//分销价
                            item.setPurchase_price(new BigDecimal(listarr[7]));
                            item.setStep_price_state(0);
                            if (item.getId() == null || item.getId() == 0) {
                                item.setCreatetime(now);
                                item.setDisabled(false);
                                item.setGoods(goods);
                                item.setStatus(1);
                                this.goodsItemService.save(item);
                            } else {
                                this.goodsItemService.update(item);
                            }

                        }
                    }
                    for (int i = 0; i < item_list.size(); i++) {
                        boolean flag = true;
                        if (item_list1.contains(item_list.get(i).getId())) {
                            flag = false;
                        }
                        if (flag) {
                            this.goodsItemService.delete(item_list.get(i).getId());
                        }
                    }
                } else {

                    //没有选择模板是编辑
                    GoodsItem item = new GoodsItem();
                    if (item_list != null && item_list.size() > 0) {
                        item = goods.getGoods_item_list().get(0);
                    } else {
                        item.setCreatetime(now);
                        item.setDisabled(false);
                    }
                    item.setGoods_inventory(CommUtil.null2Int(goods_inventory));

                    String string = request.getParameter("store_price");
                    if (string == null || "".equals(string)) {
                        string = "0";
                    }
                    item.setGoods_price(new BigDecimal(string));
                    String string2 = request.getParameter("goods_price");
                    if (string2 == null || "".equals(string2)) {
                        string2 = "0";
                    }
                    //item.setMarket_price(new BigDecimal(string2));
                    item.setDist_price(new BigDecimal(string2));//分销价
                    item.setPurchase_price(new BigDecimal(string2));
                    item.setBar_code(request.getParameter("bar_code"));
                    item.setSelf_code(request.getParameter("self_code"));
                    item.setGoods(goods);
                    item.setStatus(0);
                    item.setStep_price_state(0);
                    this.goodsItemService.update(item);
                }

                //调用服装接口
                GoodsJsonVo goodsJsonVo = new GoodsJsonVo();
                goodsJsonVo.setId(goods.getId());
                goodsJsonVo.setCreatetime(goods.getCreatetime());
                goodsJsonVo.setDisabled(goods.isDisabled());
                goodsJsonVo.setSeo_keywords(goods.getSeo_keywords());
                goodsJsonVo.setSeo_description(goods.getSeo_description());
                goodsJsonVo.setGoods_name(goods.getGoods_name());
                goodsJsonVo.setGoods_price(goods.getGoods_price());
                goodsJsonVo.setStore_price(goods.getStore_price());
                goodsJsonVo.setGoods_inventory(goods.getGoods_inventory());
                goodsJsonVo.setInventory_type(goods.getInventory_type());
                goodsJsonVo.setGoods_salenum(goods.getGoods_salenum());
                goodsJsonVo.setGoods_details(goods.getGoods_details());
                goodsJsonVo.setGoods_recommend(goods.isGoods_recommend());
                goodsJsonVo.setGoods_click(goods.getGoods_click());
                goodsJsonVo.setGoods_collect(goods.getGoods_collect());

                goodsJsonVo.setGoods_store_id(goods.getGoods_store().getId());

                goodsJsonVo.setGoods_status(goods.getGoods_status());
                goodsJsonVo.setGoods_transfee(goods.getGoods_transfee());
                Accessory goods_main_photo = goods.getGoods_main_photo();
                if (goods_main_photo != null) {
                    goodsJsonVo.setGoods_main_photo_url(goods_main_photo.getPath() + "/"
                                                        + goods_main_photo.getName());
                }
                List<Accessory> goods_photos = goods.getGoods_photos();
                String goods_pic_url = "";
                if (goods_photos != null && goods_photos.size() > 0) {
                    for (Accessory accessory : goods_photos) {
                        goods_pic_url += accessory.getPath() + "/" + accessory.getName() + ";";
                    }
                }
                goodsJsonVo.setGoods_photos_url(goods_pic_url);
                List<UserGoodsClass> goods_ugcs = goods.getGoods_ugcs();
                if (goods_ugcs != null && goods_ugcs.size() > 0) {
                    goodsJsonVo.setGoods_ugcs_id(goods_ugcs.get(0).getId());
                }
                List<GoodsSpecProperty> goods_specs = goods.getGoods_specs();
                String str_spec = "";
                if (goods_specs != null && goods_specs.size() > 0) {
                    for (GoodsSpecProperty goodsSpecProperty : goods_specs) {
                        str_spec += goodsSpecProperty.getId() + ";";
                    }
                }
                goodsJsonVo.setGoods_specs(str_spec);
                GoodsBrand goods_brand = goods.getGoods_brand();
                if (goods_brand != null) {
                    goodsJsonVo.setGoods_brand_id(goods_brand.getId());
                }
                goodsJsonVo.setGoods_current_price(goods.getGoods_current_price());
                goodsJsonVo.setMail_trans_fee(goods.getMail_trans_fee());
                goodsJsonVo.setExpress_trans_fee(goods.getExpress_trans_fee());
                goodsJsonVo.setEms_trans_fee(goods.getEms_trans_fee());
                goodsJsonVo.setGoods_item_list(goods.getGoods_item_list());
                goodsJsonVo.setGoodsTypeId(goods.getGoodsTypeId());
                goodsJsonVo.setStorage_status(goods.getStorage_status());
                goodsJsonVo.setGoods_units(goods.getGoods_units());
                goodsJsonVo.setGoods_news_status(goods.getGoods_news_status());
                goodsJsonVo.setGoods_hot_status(goods.getGoods_hot_status());
                goodsJsonVo.setTotal_weight(goods.getTotal_weight());
                goodsJsonVo.setType_ratio(goods.getType_ratio());
                goodsJsonVo.setColor_ratio(goods.getColor_ratio());
                goodsJsonVo.setSize_ratio(goods.getSize_ratio());
                goodsJsonVo.setSingle_weight(goods.getSingle_weight());
                goodsJsonVo.setGoods_type(goods.getGoods_type());
                goodsJsonVo.setRetrieval_ids(goods.getRetrieval_ids());
                goodsJsonVo.setExtendedAttributes(goods.getExtendedAttributes());

                String write2JsonStr = JsonUtil.write2JsonStr(goodsJsonVo);

                sendReqAsync.sendMessageUtil(Constant.GOODS_URL_EDIT, write2JsonStr, "编辑商品");

                String goods_lucene_path = System.getProperty("user.dir") + File.separator
                                           + "luence" + File.separator + "goods";
                File file = new File(goods_lucene_path);
                if (!file.exists()) {
                    CommUtil.createFolder(goods_lucene_path);
                }
                LuceneVo vo = new LuceneVo();
                vo.setVo_id(goods.getId());
                vo.setVo_title(goods.getGoods_name());
                vo.setVo_content(goods.getGoods_details());
                vo.setVo_type("goods");
                vo.setVo_store_price(CommUtil.null2Double(goods.getStore_price()));
                vo.setVo_add_time(goods.getCreatetime().getTime());
                vo.setVo_goods_salenum(goods.getGoods_salenum());
                vo.setVo_goods_collect(goods.getGoods_collect());
                LuceneUtil lucene = LuceneUtil.instance();
                LuceneUtil.setIndex_path(goods_lucene_path);
                lucene.update(CommUtil.null2String(goods.getId()), vo);
            }
            mv.addObject("obj", goods);
            request.getSession(false).removeAttribute("goods_session");
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "参数错误");
            mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
        }
        return mv;
    }

    @SecurityMapping(title = "加载商品分类", value = "/seller/load_goods_class.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/load_goods_class.htm" })
    public void load_goods_class(HttpServletRequest request, HttpServletResponse response,
                                 String pid, String session) {
        GoodsClass obj = this.goodsClassService.getObjById(CommUtil.null2Long(pid));
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (obj != null) {
            for (GoodsClass gc : obj.getChilds()) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", gc.getId());
                map.put("className", gc.getClassName());
                list.add(map);
            }
            if (CommUtil.null2Boolean(session)) {
                request.getSession(false).setAttribute("goods_class_info", obj);
            }
        }
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(Json.toJson(list));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SecurityMapping(title = "添加用户常用商品分类", value = "/seller/load_goods_class.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/add_goods_class_staple.htm" })
    public void add_goods_class_staple(HttpServletRequest request, HttpServletResponse response) {
        String ret = "error";
        if (request.getSession(false).getAttribute("goods_class_info") != null) {
            GoodsClass gc = (GoodsClass) request.getSession(false).getAttribute("goods_class_info");
            Map params = new HashMap();
            User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
            params.put("store_id", user.getStore().getId());
            params.put("gc_id", gc.getId());
            List<GoodsClassStaple> gcs = this.goodsclassstapleService
                .query(
                    "select obj from GoodsClassStaple obj where obj.store.id=:store_id and obj.gc.id=:gc_id",
                    params, -1, -1);
            if (gcs.size() == 0) {
                GoodsClassStaple staple = new GoodsClassStaple();
                staple.setCreatetime(new Date());
                staple.setGc(gc);
                String name = generic_goods_class_info(gc);
                staple.setName(name.substring(0, name.length() - 1));
                staple.setStore(user.getStore());
                boolean flag = this.goodsclassstapleService.save(staple);
                if (flag) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("name", staple.getName());
                    map.put("id", staple.getId());
                    ret = Json.toJson(map);
                }
            }
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

    @SecurityMapping(title = "删除用户常用商品分类", value = "/seller/del_goods_class_staple.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/del_goods_class_staple.htm" })
    public void del_goods_class_staple(HttpServletRequest request, HttpServletResponse response,
                                       String id) {
        boolean ret = this.goodsclassstapleService.delete(Long.valueOf(Long.parseLong(id)));
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

    @SecurityMapping(title = "根据用户常用商品分类加载分类信息", value = "/seller/del_goods_class_staple.htm*", rtype = "seller", rname = "商品发布", rcode = "goods_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/load_goods_class_staple.htm" })
    public void load_goods_class_staple(HttpServletRequest request, HttpServletResponse response,
                                        String id, String name) {
        GoodsClass obj = null;
        if ((id != null) && (!id.equals(""))) {
            obj = this.goodsclassstapleService.getObjById(Long.valueOf(Long.parseLong(id))).getGc();
        }
        if ((name != null) && (!name.equals(""))) {
            obj = this.goodsClassService.getObjByProperty("className", name);
        }
        List<List<Map>> list = new ArrayList();
        if (obj != null) {
            request.getSession(false).setAttribute("goods_class_info", obj);
            Map params = new HashMap();
            List<Map> second_list = new ArrayList();
            List<Map> third_list = new ArrayList();
            List<Map> other_list = new ArrayList();
            if (obj.getLevel() == 2) {
                params.put("pid", obj.getParent().getParent().getId());
                List<GoodsClass> second_gcs = this.goodsClassService
                    .query(
                        "select obj from GoodsClass obj where obj.parent.id=:pid order by obj.sequence asc",
                        params, -1, -1);
                for (GoodsClass gc : second_gcs) {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("id", gc.getId());
                    map.put("className", gc.getClassName());
                    second_list.add(map);
                }
                params.clear();
                params.put("pid", obj.getParent().getId());
                List<GoodsClass> third_gcs = this.goodsClassService
                    .query(
                        "select obj from GoodsClass obj where obj.parent.id=:pid order by obj.sequence asc",
                        params, -1, -1);
                for (GoodsClass gc : third_gcs) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("id", ((GoodsClass) gc).getId());
                    map.put("className", ((GoodsClass) gc).getClassName());
                    third_list.add(map);
                }
            }
            if (obj.getLevel() == 1) {
                params.clear();
                params.put("pid", obj.getParent().getId());
                List<GoodsClass> third_gcs = this.goodsClassService
                    .query(
                        "select obj from GoodsClass obj where obj.parent.id=:pid order by obj.sequence asc",
                        params, -1, -1);
                for (GoodsClass gc : third_gcs) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("id", gc.getId());
                    map.put("className", gc.getClassName());
                    second_list.add(map);
                }
            }
            Map<String, Object> map = new HashMap<String, Object>();
            String staple_info = generic_goods_class_info(obj);
            map.put("staple_info", staple_info.substring(0, staple_info.length() - 1));
            other_list.add(map);

            list.add(second_list);
            list.add(third_list);
            list.add(other_list);
        }
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(Json.toJson(list, JsonFormat.compact()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generic_goods_class_info(GoodsClass gc) {
        String goods_class_info = gc.getClassName() + ">";
        if (gc.getParent() != null) {
            String class_info = generic_goods_class_info(gc.getParent());
            goods_class_info = class_info + goods_class_info;
        }
        return goods_class_info;
    }

    @SecurityMapping(title = "出售中的商品列表", value = "/seller/goods.htm*", rtype = "seller", rname = "出售中的商品", rcode = "goods_list_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/goods.htm" })
    public ModelAndView goods(HttpServletRequest request, HttpServletResponse response,
                              String currentPage, String orderBy, String orderType,
                              String goods_name, String user_class_id, String goods_status,
                              String goods_brands, String goods_label, String user_goods_type) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/goods.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        String params = "";
        GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
        //商品状态
        if (goods_status != null && !"".equals(goods_status)) {
            qo.addQuery("obj.goods_status",
                new SysMap("goods_status", Integer.valueOf(goods_status)), "=");
        } else {
            qo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(-1)), ">");
        }
        mv.addObject("goods_status", goods_status);
        //商品分类 店铺分类
        Map<String, Object> goodsClassParams = new HashMap<String, Object>();
        goodsClassParams.put("user_id", user.getId());
        goodsClassParams.put("display", Boolean.valueOf(true));
        List<UserGoodsClass> ugcs = this.userGoodsClassService
            .query(
                "select obj from UserGoodsClass obj where obj.disabled = false and obj.user.id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc",
                goodsClassParams, -1, -1);

        mv.addObject("ugcs", ugcs);

        //品牌
        if (goods_brands != null && !"".equals(goods_brands)) {
            qo.addQuery("obj.goods_brand.id",
                new SysMap("goods_brands", Long.valueOf(goods_brands)), "=");
        }
        mv.addObject("goods_brands", goods_brands);
        //标签
        if (goods_label != null && !"".equals(goods_label)) {
            if ("1".equals(goods_label)) {//推荐
                qo.addQuery("obj.goods_recommend", new SysMap("goods_recommend", true), "=");
            } else if ("2".equals(goods_label)) {//热销
                qo.addQuery("obj.goods_hot_status", new SysMap("goods_hot_status", 1), "=");
            } else if ("3".equals(goods_label)) {//新品
                qo.addQuery("obj.goods_news_status", new SysMap("goods_news_status", 1), "=");
            }
        }
        mv.addObject("goods_label", goods_label);

        qo.addQuery("obj.goods_store.id", new SysMap("goods_store_id", user.getStore().getId()),
            "=");
        qo.setOrderBy("createtime");
        qo.setOrderType("desc");
        //商品名称
        if ((goods_name != null) && (!goods_name.equals(""))) {
            qo.addQuery("obj.goods_name", new SysMap("goods_name", "%" + goods_name + "%"), "like");

            mv.addObject("goods_name", goods_name);
            params += "&goods_name=" + goods_name;
        }
        if ((user_class_id != null) && (!user_class_id.equals(""))) {
            UserGoodsClass ugc = this.userGoodsClassService.getObjById(Long.valueOf(Long
                .parseLong(user_class_id)));
            qo.addQuery("ugc", ugc, "obj.goods_ugcs", "member of");

            mv.addObject("myuser_class_id", user_class_id);

            params += "&user_class_id=" + user_class_id;
        }
        if (user_goods_type != null && !"".equals(user_goods_type)) {
            qo.addQuery("obj.goods_type",
                new SysMap("goods_type", CommUtil.null2Int(user_goods_type)), "=");
            mv.addObject("user_goods_type", user_goods_type);
        }
        qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");

        IPageList pList = this.goodsService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/seller/goods.htm", "", params, pList, mv);
        mv.addObject("storeTools", this.storeTools);
        mv.addObject("goodsViewTools", this.goodsViewTools);
        return mv;
    }

    @SecurityMapping(title = "仓库中的商品列表", value = "/seller/goods_storage.htm*", rtype = "seller", rname = "仓库中的商品", rcode = "goods_storage_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/goods_storage.htm" })
    public ModelAndView goods_storage(HttpServletRequest request, HttpServletResponse response,
                                      String currentPage, String orderBy, String orderType,
                                      String goods_name, String user_class_id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/goods_storage.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        String params = "";
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
        qo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(1)), "=");
        qo.addQuery("obj.goods_store.id", new SysMap("goods_store_id", user.getStore().getId()),
            "=");
        qo.setOrderBy("goods_seller_time");
        qo.setOrderType("desc");
        if ((goods_name != null) && (!goods_name.equals(""))) {
            qo.addQuery("obj.goods_name", new SysMap("goods_name", "%" + goods_name + "%"), "like");

            mv.addObject("goods_name", goods_name);
            params += "&goods_name=" + goods_name;
        }
        if ((user_class_id != null) && (!user_class_id.equals(""))) {
            UserGoodsClass ugc = this.userGoodsClassService.getObjById(Long.valueOf(Long
                .parseLong(user_class_id)));
            qo.addQuery("ugc", ugc, "obj.goods_ugcs", "member of");

            mv.addObject("myuser_class_id", user_class_id);
            params += "&user_class_id=" + user_class_id;
        }
        qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");

        IPageList pList = this.goodsService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/seller/goods_storage.htm", "", params, pList,
            mv);
        mv.addObject("storeTools", this.storeTools);
        mv.addObject("goodsViewTools", this.goodsViewTools);
        return mv;
    }

    @SecurityMapping(title = "违规下架商品", value = "/seller/goods_out.htm*", rtype = "seller", rname = "违规下架商品", rcode = "goods_out_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/goods_out.htm" })
    public ModelAndView goods_out(HttpServletRequest request, HttpServletResponse response,
                                  String currentPage, String orderBy, String orderType,
                                  String goods_name, String user_class_id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/goods_out.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        String params = "";
        GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
        qo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(-2)), "=");
        qo.addQuery("obj.goods_store.id", new SysMap("goods_store_id", user.getStore().getId()),
            "=");
        qo.setOrderBy("goods_seller_time");
        qo.setOrderType("desc");
        if ((goods_name != null) && (!goods_name.equals(""))) {
            qo.addQuery("obj.goods_name", new SysMap("goods_name", "%" + goods_name + "%"), "like");

            mv.addObject("goods_name", goods_name);
            params += "&goods_name=" + goods_name;
        }
        if ((user_class_id != null) && (!user_class_id.equals(""))) {
            UserGoodsClass ugc = this.userGoodsClassService.getObjById(Long.valueOf(Long
                .parseLong(user_class_id)));
            qo.addQuery("ugc", ugc, "obj.goods_ugcs", "member of");

            mv.addObject("myuser_class_id", user_class_id);
            params += "&user_class_id=" + user_class_id;
        }
        qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");

        IPageList pList = this.goodsService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/seller/goods_out.htm", "", params, pList, mv);
        mv.addObject("storeTools", this.storeTools);
        mv.addObject("goodsViewTools", this.goodsViewTools);
        return mv;
    }

    @SecurityMapping(title = "商品编辑", value = "/seller/goods_edit.htm*", rtype = "seller", rname = "商品编辑", rcode = "goods_edit_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/goods_edit.htm" })
    public ModelAndView goods_edit(HttpServletRequest request, HttpServletResponse response,
                                   String id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/add_goods_second.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Goods obj = this.goodsService.getObjById(Long.valueOf(Long.parseLong(id)));
        if (obj.getGoods_store().getId()
            .equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
            User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
            String path = request.getSession().getServletContext().getRealPath("/")
                          + File.separator + "upload" + File.separator + "store" + File.separator
                          + user.getStore().getId();
            double img_remain_size = user.getStore().getGrade().getSpaceSize()
                                     - CommUtil.div(
                                         Double.valueOf(CommUtil.fileSize(new File(path))),
                                         Integer.valueOf(1024));
            Map params = new HashMap();
            params.put("user_id", user.getId());
            params.put("display", Boolean.valueOf(true));
            List<UserGoodsClass> ugcs = this.userGoodsClassService
                .query(
                    "select obj from UserGoodsClass obj where obj.disabled=false and obj.user.id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc",
                    params, -1, -1);
            AccessoryQueryObject aqo = new AccessoryQueryObject();
            aqo.setPageSize(Integer.valueOf(8));
            aqo.addQuery("obj.user.id", new SysMap("user_id", user.getId()), "=");
            aqo.setOrderBy("createtime");
            aqo.setOrderType("desc");
            IPageList pList = this.accessoryService.list(aqo);
            String photo_url = CommUtil.getURL(request) + "/seller/load_photo.htm";

            Map<String, Object> queryMap = new HashMap<String, Object>();
            queryMap.put("store_id", user.getStore().getId());
            List<GoodsType> goodsTypes = this.goodsTypeService
                .query(
                    "select obj from GoodsType obj where obj.disabled = false and obj.store_id=:store_id",
                    queryMap, -1, -1);
            mv.addObject("goodsTypes", goodsTypes);

            List<GoodsBrand> gbs = this.goodsBrandService
                .query(
                    "select obj from GoodsBrand obj where  obj.disabled = false and obj.audit=1 and obj.store.id=:store_id order by obj.first_word asc,obj.name asc",
                    queryMap, -1, -1);
            mv.addObject("gbs", gbs);

            GoodsBrand gbobj = obj.getGoods_brand();
            if (gbobj != null) {
                Map mp = new HashMap();
                mp.put("brandid", gbobj.getId());
            }

            //查询扩展属性
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("disabled", false);
            map.put("type", 2);
            String sql = "select obj from GoodsRetrieve obj where disabled=:disabled and type=:type order by sequence desc,id asc";
            List<GoodsRetrieve> goodsRetrieves = this.goodsRetrieveService.query(sql, map, -1, -1);
            mv.addObject("extendedAttributes", goodsRetrieves);

            mv.addObject("photos", pList.getResult());
            mv.addObject("gotoPageAjaxHTML",
                CommUtil.showPageAjaxHtml(photo_url, "", pList.getCurrentPage(), pList.getPages()));
            mv.addObject("ugcs", ugcs);
            mv.addObject("img_remain_size", Double.valueOf(img_remain_size));
            mv.addObject("obj", obj);
            if (request.getSession(false).getAttribute("goods_class_info") != null) {
                GoodsClass session_gc = (GoodsClass) request.getSession(false).getAttribute(
                    "goods_class_info");
                GoodsClass gc = this.goodsClassService.getObjById(session_gc.getId());
                mv.addObject("goods_class_info", this.storeTools.generic_goods_class_info(gc));
                mv.addObject("goods_class", gc);
                request.getSession(false).removeAttribute("goods_class_info");
            } else if (obj.getGc() != null) {
                mv.addObject("goods_class_info",
                    this.storeTools.generic_goods_class_info(obj.getGc()));
                mv.addObject("goods_class", obj.getGc());
            }
            String goods_session = CommUtil.randomString(32);
            mv.addObject("goods_session", goods_session);
            request.getSession(false).setAttribute("goods_session", goods_session);
            mv.addObject("imageSuffix", this.storeViewTools.genericImageSuffix(this.configService
                .getSysConfig().getImageSuffix()));

            Integer goodtypeId = obj.getGoodsTypeId();//模板Id
            String barCode = "";
            String selfCode = "";

            if (goodtypeId == null || goodtypeId == 0 || goodtypeId == -1) {//表示没有使用模板
                List<GoodsItem> goods_item_list = obj.getGoods_item_list();

                if (goods_item_list != null && goods_item_list.size() > 0) {
                    GoodsItem goodsItem = goods_item_list.get(0);
                    barCode = goodsItem.getBar_code();
                    selfCode = goodsItem.getSelf_code();
                }
            }

            mv.addObject("barCode", barCode);
            mv.addObject("selfCode", selfCode);
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "您没有该商品信息！");
            mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
        }
        //所有商品的标签
        Map<String, Object> mapGoodsLabel = new HashMap<String, Object>();
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        if (user!=null && !"".equals(user)) {
    		if (user.getSalesManState()!=null && user.getSalesManState()==1) {
    			mapGoodsLabel.put("user_id",  user.getParent().getId());
			}else{
				mapGoodsLabel.put("user_id", user.getId());
			}
		}
        List<GoodsLabel> listGoodsLabel=goodsLabelService.query( 
        		"select obj from GoodsLabel obj where obj.disabled = false and obj.status = 0 and obj.createuser.id=:user_id order by sequence  ASC ",
        		mapGoodsLabel, -1, -1);
        
        Map<String, Object> mapGoodslabelGoods = new HashMap<String, Object>();
        mapGoodslabelGoods.put("id", obj.getId());
        List<Long> listGoodslabelId=goodsLabelGoodsService.queryGoodsLabelLongId(
        		" select obj.goodslabel.id from GoodslabelGoods obj where obj.disabled = false and obj.goods.id=:id", mapGoodslabelGoods, -1, -1);
        Map<Long, Object> maplistGoodslabelId =new HashMap<Long, Object>();
        for (Long goodslabelId : listGoodslabelId) {
        	maplistGoodslabelId.put(goodslabelId, goodslabelId);
		}
        mv.addObject("listGoodsLabel", listGoodsLabel);
        mv.addObject("maplistGoodslabelId", maplistGoodslabelId);
        return mv;
    }

    @SecurityMapping(title = "商品上下架", value = "/seller/goods_sale.htm*", rtype = "seller", rname = "商品上下架", rcode = "goods_sale_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/goods_sale.htm" })
    public String goods_sale(HttpServletRequest request, HttpServletResponse response,
                             String mulitId, String goods_status1) {
        String url = "/seller/goods.htm";
        String[] ids = mulitId.split(",");
        for (String id : ids) {
            if (!id.equals("") && goods_status1 != null
                && ("0".equals(goods_status1) || "1".equals(goods_status1))) {
                Goods goods = this.goodsService.getObjById(Long.valueOf(Long.parseLong(id)));
                if (goods.getGoods_store().getId()
                    .equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
                    int goods_status = Integer.valueOf(goods_status1);
                    goods.setGoods_status(Integer.valueOf(goods_status));
                    this.goodsService.update(goods);
                    if (goods_status == 0) {
                        url = "/seller/goods.htm";

                        String goods_lucene_path = System.getProperty("user.dir") + File.separator
                                                   + "luence" + File.separator + "goods";
                        File file = new File(goods_lucene_path);
                        if (!file.exists()) {
                            CommUtil.createFolder(goods_lucene_path);
                        }
                        LuceneVo vo = new LuceneVo();
                        vo.setVo_id(goods.getId());
                        vo.setVo_title(goods.getGoods_name());
                        vo.setVo_content(goods.getGoods_details());
                        vo.setVo_type("goods");
                        vo.setVo_store_price(CommUtil.null2Double(goods.getStore_price()));
                        vo.setVo_add_time(goods.getCreatetime().getTime());
                        vo.setVo_goods_salenum(goods.getGoods_salenum());
                        vo.setVo_goods_collect(goods.getGoods_collect());
                        LuceneUtil lucene = LuceneUtil.instance();
                        LuceneUtil.setIndex_path(goods_lucene_path);
                        lucene.update(CommUtil.null2String(goods.getId()), vo);
                    } else {
                        String goods_lucene_path = System.getProperty("user.dir") + File.separator
                                                   + "luence" + File.separator + "goods";
                        File file = new File(goods_lucene_path);
                        if (!file.exists()) {
                            CommUtil.createFolder(goods_lucene_path);
                        }
                        LuceneUtil lucene = LuceneUtil.instance();
                        lucene.delete_index(CommUtil.null2String(goods.getId()));
                    }
                }
            }
        }
        return "redirect:" + url;
    }

    @SecurityMapping(title = "商品删除", value = "/seller/goods_del.htm*", rtype = "seller", rname = "商品删除", rcode = "goods_del_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/goods_del.htm" })
    public ModelAndView goods_del(HttpServletRequest request, HttpServletResponse response,
                                  String mulitId, String op) throws UnsupportedEncodingException {

        String url = "/seller/goods.htm";
        if (CommUtil.null2String(op).equals("storage")) {
            url = "/seller/goods_storage.htm";
        }
        if (CommUtil.null2String(op).equals("out")) {
            url = "/seller/goods_out.htm";
        }
        url = CommUtil.getURL(request) + url;
        ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        try {

            String[] ids = mulitId.split(",");
            for (String id : ids) {
                if (!id.equals("")) {
                    Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
                    if (goods.getGoods_store().getId()
                        .equals(SecurityUserHolder.getCurrentUser().getStore().getId())) {
                        /*
                         * 删除商品时订单不删除
                         *   Map<String, Object> map = new HashMap<String, Object>();
                        map.put("gid", goods.getId());
                        List<GoodsCart> goodCarts = this.goodsCartService.query(
                            "select obj from GoodsCart obj where obj.goods.id = :gid", map, -1, -1);
                         Long ofid = null;
                        Long of_id;
                        for (GoodsCart gc : goodCarts) {
                            of_id = gc.getOf().getId();
                            this.goodsCartService.delete(gc.getId());
                            OrderForm of = this.orderFormService.getObjById(of_id);
                            if (of.getGcs().size() == 0) {
                                this.orderFormService.delete(of_id);
                            }
                        }*/
                        List<Evaluate> evaluates = goods.getEvaluates();
                        for (Evaluate e : evaluates) {
                            this.evaluateService.delete(e.getId());
                        }
                        goods.getGoods_ugcs().clear();
                        goods.getGoods_ugcs().clear();
                        goods.getGoods_photos().clear();
                        goods.getGoods_ugcs().clear();
                        goods.getGoods_specs().clear();
                        goods.getEvaluates().clear();
                        goods.getEvas().clear();
                        this.goodsService.delete(goods.getId());

                        //服装接口
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("id", goods.getId());
                        String write2JsonStr = JsonUtil.write2JsonStr(map);
                        sendReqAsync.sendMessageUtil(Constant.GOODS_URL_DEL, write2JsonStr, "商品删除");

                        String goods_lucene_path = System.getProperty("user.dir") + File.separator
                                                   + "luence" + File.separator + "goods";
                        File file = new File(goods_lucene_path);
                        if (!file.exists()) {
                            CommUtil.createFolder(goods_lucene_path);
                        }
                        LuceneUtil lucene = LuceneUtil.instance();
                        LuceneUtil.setIndex_path(goods_lucene_path);
                        lucene.delete_index(CommUtil.null2String(id));
                    }
                }
            }

            //            return "redirect:" + url;
            mv.addObject("op_title", "删除成功");
            mv.addObject("url", url);
            return mv;
        } catch (RuntimeException e) {
            //因外键关联删除失败时返回删除失败flag
            //e.printStackTrace();
            //throw e;
            /*String ENCODING = "utf-8";
            String strInfo = URLEncoder.encode("已售出商品无法删除", ENCODING);
            return "redirect:/gotoErrorPage.htm?op_title=" + strInfo + "&url="
                   + CommUtil.getURL(request) + url;*/
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "已售出商品无法删除");
            mv.addObject("url", url);
            return mv;
        }
    }

    @RequestMapping({ "/gotoErrorPage.htm" })
    public ModelAndView gotoErrorPage(HttpServletRequest request, HttpServletResponse response,
                                      String op_title, String url) {

        ModelAndView mv = new JModelAndView("error.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        mv.addObject("op_title", op_title);
        mv.addObject("url", url);
        return mv;
    }

    @SecurityMapping(title = "被举报禁售商品", value = "/seller/goods_report.htm*", rtype = "seller", rname = "被举报禁售商品", rcode = "goods_report_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/goods_report.htm" })
    public ModelAndView goods_report(HttpServletRequest request, HttpServletResponse response,
                                     String currentPage, String orderBy, String orderType) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/goods_report.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        ReportQueryObject qo = new ReportQueryObject(currentPage, mv, orderBy, orderType);
        qo.addQuery("obj.goods.goods_store.id", new SysMap("store_id", SecurityUserHolder
            .getCurrentUser().getStore().getId()), "=");
        qo.addQuery("obj.result", new SysMap("result", Integer.valueOf(1)), "=");
        IPageList pList = this.reportService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        return mv;
    }

    @SecurityMapping(title = "举报图片查看", value = "/seller/report_img.htm*", rtype = "seller", rname = "被举报禁售商品", rcode = "goods_report_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/report_img.htm" })
    public ModelAndView report_img(HttpServletRequest request, HttpServletResponse response,
                                   String id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/report_img.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Report obj = this.reportService.getObjById(CommUtil.null2Long(id));
        mv.addObject("obj", obj);
        return mv;
    }

    @RequestMapping({ "/seller/goods_img_album.htm" })
    public ModelAndView goods_img_album(HttpServletRequest request, HttpServletResponse response,
                                        String currentPage, String type) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/" + type + ".html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        AccessoryQueryObject aqo = new AccessoryQueryObject(currentPage, mv, "createtime", "desc");
        aqo.setPageSize(Integer.valueOf(16));
        aqo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser()
            .getId()), "=");
        aqo.setOrderBy("createtime");
        aqo.setOrderType("desc");
        IPageList pList = this.accessoryService.list(aqo);
        String photo_url = CommUtil.getURL(request) + "/seller/goods_img_album.htm";
        mv.addObject("photos", pList.getResult());
        mv.addObject("gotoPageAjaxHTML",
            CommUtil.showPageAjaxHtml(photo_url, "", pList.getCurrentPage(), pList.getPages()));

        return mv;
    }

    //商品导入
    /*
     * 库存编辑
     */

    /**
     * 商品导入页面
     * @param request
     * @param response
     * @param goodsId
     * @return
     */
    @RequestMapping({ "/seller/importGoodsUI.htm" })
    public ModelAndView importGoodsUI(HttpServletRequest request, HttpServletResponse response,
                                      String goodsId) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller/importGoodsUI.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if (SecurityUserHolder.getCurrentUser() == null) {
            mv = new JModelAndView("login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            return mv;
        }
        return mv;
    }

    @RequestMapping({ "/seller/importGoods.htm" })
    public void importGoods(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam(value = "upfile") MultipartFile upfile, String goods_type) {

        String str = "0";
        if (SecurityUserHolder.getCurrentUser() == null) {
            str = "1";
        }

        if ("0".equals(str)) {
            String postfix = ExcelUtil.getPostfix(upfile.getOriginalFilename());
            if (!ExcelUtil.EMPTY.equals(postfix)) {
                if (ExcelUtil.OFFICE_EXCEL_2003_POSTFIX.equals(postfix)
                    || ExcelUtil.OFFICE_EXCEL_2010_POSTFIX.equals(postfix)) {
                    //读取头部，判断模板与分类是否对应
                    ExcelRead excelRead = new ExcelRead();
                    List<String> readExcelTitle = excelRead.readExcelTitle(upfile);

                    if (CommUtil.null2Int(goods_type) == 0) {//单件商品
                        if (readExcelTitle != null
                            && readExcelTitle.size() == Constant.SINGLE_GOODS_NUM) {
                            User user = this.userService.getObjById(SecurityUserHolder
                                .getCurrentUser().getId());
                            this.goodsService.importSingleGoods(user, upfile, request);
                        } else {
                            str = "3";//模板与类型不对应
                        }
                    } else if (CommUtil.null2Int(goods_type) == 1) {//整包商品
                        if (readExcelTitle != null
                            && readExcelTitle.size() == Constant.PACKGET_GOODS_NUM) {
                            User user = this.userService.getObjById(SecurityUserHolder
                                .getCurrentUser().getId());
                            this.goodsService.importPackageGoods(user, upfile, request);
                        } else {
                            str = "3";//模板与类型不对应
                        }

                    } else if (CommUtil.null2Int(goods_type) == 2) {//走份商品
                        if (readExcelTitle != null
                            && readExcelTitle.size() == Constant.SHARE_GOODS_NUM) {
                            User user = this.userService.getObjById(SecurityUserHolder
                                .getCurrentUser().getId());
                            this.goodsService.importShareGoods(user, upfile, request);
                        } else {
                            str = "3";//模板与类型不对应
                        }

                    }
                    /*	List<ArrayList<String>> readExcel = excelRead.readExcel(upfile);
                    	System.out.println(readExcel);*/

                } else {
                    str = "2";//文件格式不正确
                }
            } else {
                str = "2";//文件格式不正确
            }
        }

        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //导入图片

    @RequestMapping({ "/seller/importGoodsPics.htm" })
    public void importGoodsPics(HttpServletRequest request, HttpServletResponse response,
                                String user_id, String album_id) {

        if (SecurityUserHolder.getCurrentUser() != null) {
            User user = SecurityUserHolder.getCurrentUser();
            String photo_path = this.storeTools.createUserFolder(request,
                this.configService.getSysConfig(), user.getStore());
            String photo_url = this.storeTools.createUserFolderURL(
                this.configService.getSysConfig(), user.getStore());
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest.getFile("imgFile");
            String upload_img_name = file.getOriginalFilename();

            Map<String, String> pmap = (HashMap<String, String>) request.getSession(false)
                .getAttribute("upload_pic_map");
            if (pmap == null) {
                pmap = new HashMap<String, String>();
            }
            try {
                Map<String, Object> map = CommUtil.saveFileToServer2(request, "imgFile",
                    photo_path, upload_img_name, null);
                map.put("photo_url", photo_url);
                /*       Accessory image = new Accessory();
                       image.setCreatetime(new Date());
                       image.setExt((String) map.get("mime"));
                       image.setPath(photo_url);
                       image.setWidth(CommUtil.null2Int(map.get("width")));
                       image.setHeight(CommUtil.null2Int(map.get("height")));
                       image.setName(CommUtil.null2String(map.get("fileName")));
                       image.setUser(user);*/
                //    this.accessoryService.save(image);
                pmap.put(map.get("oldName") + "", JsonUtil.write2JsonStr(map));
            } catch (IOException e) {
                e.printStackTrace();
            }
            request.getSession(false).setAttribute("upload_pic_map", pmap);
        }
    }

}
