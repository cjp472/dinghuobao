package com.javamalls.ctrl.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
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
import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.WebForm;
import com.javamalls.ctrl.admin.tools.StoreTools;
import com.javamalls.front.web.tools.GoodsViewTools;
import com.javamalls.front.web.tools.StoreViewTools;
import com.javamalls.platform.domain.Area;
import com.javamalls.platform.domain.GoodsBrand;
import com.javamalls.platform.domain.PurchaseOrder;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.Supplier;
import com.javamalls.platform.domain.Warehouse;
import com.javamalls.platform.domain.query.GoodsItemQueryObject;
import com.javamalls.platform.domain.query.StorePurchaseQueryObject;
import com.javamalls.platform.domain.query.StoreSupplierQueryObject;
import com.javamalls.platform.domain.query.StoreWarehouseQueryObject;
import com.javamalls.platform.domain.query.WarehouseGoodsItemQueryObject;
import com.javamalls.platform.service.IAreaService;
import com.javamalls.platform.service.IGoodsBrandService;
import com.javamalls.platform.service.IGoodsItemService;
import com.javamalls.platform.service.IPurchaseOrderService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.ISupplierService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IWarehouseGoodsItemService;
import com.javamalls.platform.service.IWarehouseService;


@Controller
public class StoreWarehouseGoodsItemSellerAction {
    @Autowired
    private ISysConfigService       	configService;
    @Autowired
    private IUserConfigService      	userConfigService;
    @Autowired
    private IWarehouseService 			warehouseService ;
    @Autowired
    private IWarehouseGoodsItemService 	warehouseGoodsItemService;
    @Autowired
    private IStoreService           	storeService;
    @Autowired
    private ISupplierService 			supplierService;
    @Autowired
    private IAreaService       			areaService;
    @Autowired
    private IPurchaseOrderService 		purchaseOrderService;
    @Autowired
    private IGoodsItemService 			goodsItemService;
    @Autowired
    private StoreTools                	storeTools;
    @Autowired
    private StoreViewTools            	storeViewTools;
    @Autowired
    private GoodsViewTools            	goodsViewTools;
    @Autowired
    private IGoodsBrandService			goodsBrandService;

    @SecurityMapping(title = "仓库商品库存管理", value = "/seller/warehouse_goods_item_list.htm*", rtype = "seller", rname = "管理", rcode = "store_warehouse_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/warehouse_goods_item_list.htm" })
    public ModelAndView warehouse_goods_item_list(HttpServletRequest request, HttpServletResponse response,
                                  String currentPage, String orderBy, String orderType,String warehouse_id,
                                  String keyword,String goods_status,String goods_brand_id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller/warehouse_goods_item_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        String params = "";
        WarehouseGoodsItemQueryObject qo = new WarehouseGoodsItemQueryObject(currentPage, mv, orderBy,
            orderType);
        qo.addQuery("obj.store.id", new SysMap("store_id",SecurityUserHolder.getCurrentUser()
                .getStore().getId()), "=");
        //搜索：仓库id
        if(warehouse_id!=null&&!"".equals(warehouse_id)){
        	qo.addQuery("obj.warehouse.id", new SysMap("warehouse_id",CommUtil.null2Long(warehouse_id)), "=");
        	mv.addObject("warehouse_id", warehouse_id);
        }
        //搜索：关键字（商品名称、商品编码、条形码、自编码）
        if(keyword!=null&&!"".equals(keyword)){
        	/*//商品名称
        	qo.addQuery("obj.goods_item.goods.goods_name", new SysMap("goods_name", "%" + keyword.trim() + "%"),
                    "like");
        	//条形码
        	qo.addQuery("obj.goods_item.bar_code", new SysMap("bar_code", "%" + keyword.trim() + "%"),
                    "like");
        	//自编码
        	qo.addQuery("obj.goods_item.self_code", new SysMap("self_code", "%" + keyword.trim() + "%"),
                    "like");
         */
        	Map<String, Object> p_map = new HashMap<String, Object>();
            p_map.put("goods_name", "%" + keyword.trim() + "%");
            p_map.put("bar_code", "%" + keyword.trim() + "%");
            p_map.put("self_code", "%" + keyword.trim() + "%");
            qo.addQuery("and (obj.goods_item.goods.goods_name like :goods_name " +
            		"or obj.goods_item.bar_code like:bar_code " +
            		"or obj.goods_item.self_code like:self_code)", p_map);
        	mv.addObject("keyword", keyword);
        }
        //搜索：商品状态
        if(goods_status!=null&&!"".equals(goods_status)){
        	qo.addQuery("obj.goods_item.goods.goods_status", new SysMap("goods_status",CommUtil.null2Int(goods_status)),"=");	
        	mv.addObject("goods_status", goods_status);
        }
        //搜索：商品品牌
        if(goods_brand_id!=null&&!"".equals(goods_brand_id)){
        	qo.addQuery("obj.goods_item.goods.goods_brand.id", new SysMap("goods_brand_id",CommUtil.null2Long(goods_brand_id)), "=");
        	mv.addObject("goods_brand_id", goods_brand_id);
        }
        IPageList pList = this.warehouseGoodsItemService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/seller/warehouse_goods_item_list.htm", "", params, pList, mv);
        //商品品牌列表
        List<GoodsBrand> gbs = this.goodsBrandService
                .query(
                    "select obj from GoodsBrand obj where audit=1 order by obj.first_word asc,obj.name asc",
                    null, -1, -1);
        mv.addObject("gbs", gbs);
        mv.addObject("storeTools", this.storeTools);
        mv.addObject("goodsViewTools", this.goodsViewTools);
        return mv;
    }

}
