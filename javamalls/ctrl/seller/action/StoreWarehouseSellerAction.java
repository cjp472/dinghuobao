package com.javamalls.ctrl.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.WebForm;
import com.javamalls.platform.domain.Area;
import com.javamalls.platform.domain.GoodsItem;
import com.javamalls.platform.domain.PurchaseOrder;
import com.javamalls.platform.domain.PurchaseOrderGoodsItem;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.Supplier;
import com.javamalls.platform.domain.Warehouse;
import com.javamalls.platform.domain.WarehouseGoodsItem;
import com.javamalls.platform.domain.query.GoodsItemQueryObject;
import com.javamalls.platform.domain.query.StorePurchaseQueryObject;
import com.javamalls.platform.domain.query.StoreSupplierQueryObject;
import com.javamalls.platform.domain.query.StoreWarehouseQueryObject;
import com.javamalls.platform.service.IAreaService;
import com.javamalls.platform.service.IGoodsItemService;
import com.javamalls.platform.service.IPurchaseGoodsItemService;
import com.javamalls.platform.service.IPurchaseOrderService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.ISupplierService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IWarehouseGoodsItemService;
import com.javamalls.platform.service.IWarehouseService;

/**
 * 
 * @author hanmj
 * 默认仓库管理  Controller
 * 仓库管理，供应商管理，采购入库管理
 *
 */
@Controller
public class StoreWarehouseSellerAction {
    @Autowired
    private ISysConfigService       configService;
    @Autowired
    private IUserConfigService      userConfigService;
    @Autowired
    private IWarehouseService warehouseService ;
    @Autowired
    private IStoreService           storeService;
    @Autowired
    private ISupplierService supplierService;
    @Autowired
    private IAreaService       areaService;
    @Autowired
    private IPurchaseOrderService purchaseOrderService;
    @Autowired
    private IGoodsItemService goodsItemService;
    @Autowired
    private IPurchaseGoodsItemService purchaseGoodsItemService;
    @Autowired
    private IWarehouseGoodsItemService warehouseGoodsItemService;
    
    

    @SecurityMapping(title = "仓库管理", value = "/seller/store_warehouse.htm*", rtype = "seller", rname = "管理", rcode = "store_warehouse", rgroup = "店铺设置")
    @RequestMapping({ "/seller/store_warehouse.htm" })
    public ModelAndView store_warehouse(HttpServletRequest request, HttpServletResponse response,
                                  String currentPage, String orderBy, String orderType) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller/store_warehouse.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        String params = "";
        StoreWarehouseQueryObject qo = new StoreWarehouseQueryObject(currentPage, mv, orderBy,
            orderType);
     
        qo.addQuery("obj.store.id", new SysMap("store_id",SecurityUserHolder.getCurrentUser()
                .getStore().getId()), "=");

        IPageList pList = this.warehouseService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/seller/store_warehouse.htm", "", params, pList, mv);
        return mv;
    }


    @SecurityMapping(title = "仓库编辑", value = "/seller/store_warehouse_edit.htm*", rtype = "seller", rname = "部门管理", rcode = "store_warehouse", rgroup = "店铺设置")
    @RequestMapping({ "/seller/store_warehouse_edit.htm" })
    public ModelAndView store_warehouse_edit(HttpServletRequest request, HttpServletResponse response,
                                       String id, String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller/store_warehouse_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if ((id != null) && (!id.equals(""))) {
            Warehouse warehouse = this.warehouseService.getObjById(Long
                .valueOf(Long.parseLong(id)));
            mv.addObject("obj", warehouse);
            mv.addObject("currentPage", currentPage);
            mv.addObject("edit", Boolean.valueOf(true));
        }
        return mv;
    }

    @SecurityMapping(title = "仓库保存", value = "/seller/store_warehouse_save.htm*", rtype = "seller", rname = "仓库管理", rcode = "store_warehouse", rgroup = "店铺设置")
    @RequestMapping({ "/seller/store_warehouse_save.htm" })
    public ModelAndView store_warehouse_save(HttpServletRequest request, HttpServletResponse response,
                                       String id, String currentPage, String cmd) {
        WebForm wf = new WebForm();
        Warehouse warehouse = null;
        if (id.equals("")) {
        	warehouse = (Warehouse) wf.toPo(request, Warehouse.class);
        	warehouse.setCreatetime(new Date());
        } else {
        	Warehouse obj = this.warehouseService.getObjById(Long.valueOf(Long
                .parseLong(id)));
        	warehouse = (Warehouse) wf.toPo(request, obj);
        }
        Store store = this.storeService.getObjByProperty("id", SecurityUserHolder.getCurrentUser()
            .getStore().getId());
        warehouse.setStore(store);
        if (id.equals("")) {
            this.warehouseService.save(warehouse);
        } else {
            this.warehouseService.update(warehouse);
        }
        ModelAndView mv = new JModelAndView("user/default/usercenter/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("url", CommUtil.getURL(request) + "/seller/store_warehouse.htm");
        mv.addObject("op_title", "保存仓库成功");
        return mv;
    }
    
    @SecurityMapping(title = "供应商管理", value = "/seller/store_supplier.htm*", rtype = "seller", rname = "管理", rcode = "store_supplier_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/store_supplier.htm" })
    public ModelAndView store_supplier(HttpServletRequest request, HttpServletResponse response,
                                  String currentPage, String orderBy, String orderType,String name,String contact,String mobile) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller/store_supplier.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        
        if(SecurityUserHolder.getCurrentUser()==null){
        	 mv = new JModelAndView("login.html",
                    this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
                    response);
        	return mv;
        }
        String params = "";
        StoreSupplierQueryObject qo = new StoreSupplierQueryObject(currentPage, mv, orderBy,
            orderType);
     
        qo.addQuery("obj.disabled", new SysMap("disabled",false ),"=");
        qo.addQuery("obj.store.id", new SysMap("store_id",SecurityUserHolder.getCurrentUser()
                .getStore().getId()), "=");
        if(name!=null&&!"".equals(name)){
        	qo.addQuery("obj.name", new SysMap("name","%"+name+"%" ),"like");
        }
        mv.addObject("name", name);
        if(contact!=null&&!"".equals(contact)){
        	qo.addQuery("obj.contact", new SysMap("contact","%"+contact+"%" ),"like");
        }
        mv.addObject("contact", contact);
        if(mobile!=null&&!"".equals(mobile)){
        	qo.addQuery("obj.mobile", new SysMap("mobile",mobile+"%" ),"like");
        }
        mv.addObject("mobile", mobile);

        IPageList pList = this.supplierService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/seller/store_supplier.htm", "", params, pList, mv);
        return mv;
    }
    
    @SecurityMapping(title = "供应商编辑", value = "/seller/store_supplier_edit.htm*", rtype = "seller", rname = "供应商管理", rcode = "store_supplier_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/store_supplier_edit.htm" })
    public ModelAndView store_supplier_edit(HttpServletRequest request, HttpServletResponse response,
                                       String id, String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller/store_supplier_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        List<Area> areas = this.areaService.query(
                "select obj from Area obj where obj.parent.id is null", null, -1, -1);
            mv.addObject("areas", areas);
        if ((id != null) && (!id.equals(""))) {
            Supplier supplier = this.supplierService.getObjById(Long
                .valueOf(Long.parseLong(id)));
            mv.addObject("obj", supplier);
            mv.addObject("currentPage", currentPage);
        }
        return mv;
    }

    @SecurityMapping(title = "供应商保存", value = "/seller/store_supplier_save.htm*", rtype = "seller", rname = "供应商管理", rcode = "store_supplier_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/store_supplier_save.htm" })
    public ModelAndView store_supplier_save(HttpServletRequest request, HttpServletResponse response,
                                       String id, String currentPage, String area_id) {
        WebForm wf = new WebForm();
        Supplier supplier = null;
        if (id==null||"".equals(id)) {
        	supplier = (Supplier) wf.toPo(request, Supplier.class);
        	supplier.setCreatetime(new Date());
        } else {
        	Supplier obj = this.supplierService.getObjById(Long.valueOf(Long
                .parseLong(id)));
        	supplier = (Supplier) wf.toPo(request, obj);
        }
        Store store = this.storeService.getObjByProperty("id", SecurityUserHolder.getCurrentUser()
            .getStore().getId());
        Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
        supplier.setArea(area);
        supplier.setStore(store);
        if (id.equals("")) {
            this.supplierService.save(supplier);
        } else {
            this.supplierService.update(supplier);
        }
        ModelAndView mv = new JModelAndView("user/default/usercenter/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("url", CommUtil.getURL(request) + "/seller/store_supplier.htm");
        mv.addObject("op_title", "供应商保存成功");
        return mv;
    }
    
    @SecurityMapping(title = "供应商删除", value = "/seller/store_supplier.htm*", rtype = "seller", rname = "供应商管理", rcode = "store_supplier_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/store_supplier_remove.htm" })
    public void store_supplier_remove(HttpServletRequest request, HttpServletResponse response,
                                       String id, String currentPage) {
    	boolean ret = false;
        if (id!=null&&!"".equals(id))  {
        	Supplier obj = this.supplierService.getObjById(Long.valueOf(Long
                .parseLong(id)));
        	obj.setDisabled(true);
        	 this.supplierService.update(obj);
        	 ret=true;
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
    
    /**
     * 验证供应商编号唯一
     * @param request
     * @param response
     * @param code
     * @param id
     * @param userOfstore
     */
    @RequestMapping({ "/seller/verify_supplierCode.htm" })
    public void verify_supplierCode(HttpServletRequest request, HttpServletResponse response,
                             String code, String id,String userOfstore) {
        boolean ret = true;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("code", code);
        params.put("id", CommUtil.null2Long(id));
        params.put("userOfstore", SecurityUserHolder.getCurrentUser().getStore().getId());
      
        List<Supplier> list=this.supplierService.query(
        		"select obj from Supplier obj where obj.disabled =false and obj.code=:code and obj.id!=:id and obj.store.id=:userOfstore", params, -1, -1);
        if ((list != null) && (list.size() > 0)) {
            ret = false;
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
    
    @SecurityMapping(title = "采购入库管理", value = "/seller/store_purchase.htm*", rtype = "seller", rname = "管理", rcode = "store_purchase_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/store_purchase.htm" })
    public ModelAndView store_purchase(HttpServletRequest request, HttpServletResponse response,
                                  String currentPage, String orderBy, String orderType
                                  ,String code,String storage_time,String warehouse_id,String agentName,String supplier_id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller/store_purchase.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        
        if(SecurityUserHolder.getCurrentUser()==null){
        	 mv = new JModelAndView("login.html",
                    this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
                    response);
        	return mv;
        }
        
        Long storeId = SecurityUserHolder.getCurrentUser().getStore().getId();
        Map<String, Object> map=new HashMap<String, Object>();
        map.put("storeId",storeId);
        List<Warehouse> warehouselist = this.warehouseService.query(
        		"select obj from Warehouse obj where obj.disabled=false and obj.store.id=:storeId", map, -1, -1);
        mv.addObject("warehouselist", warehouselist);
        map.clear();
        map.put("storeId",storeId);
        List<Supplier> supplierlist = this.supplierService.query(
        		"select obj from Supplier obj where obj.disabled=false and obj.store.id=:storeId", map, -1, -1);
        mv.addObject("supplierlist", supplierlist);
        
        String params = "";
        StorePurchaseQueryObject qo = new StorePurchaseQueryObject(currentPage, mv, orderBy,
            orderType);
     
        qo.addQuery("obj.disabled", new SysMap("disabled",false ),"=");
        qo.addQuery("obj.store.id", new SysMap("store_id",SecurityUserHolder.getCurrentUser()
                .getStore().getId()), "=");
        if(code!=null&&!"".equals(code)){
        	 qo.addQuery("obj.code", new SysMap("code",code+"%" ),"like");
        }
        mv.addObject("code",code);
        if(storage_time!=null&&!"".equals(storage_time)){
        	 SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        	 Date date = null;
			try {
				date = sdf.parse(storage_time);
			} catch (ParseException e) {
				e.printStackTrace();
			}
        	 qo.addQuery("obj.storage_time", new SysMap("storage_time", date),"=");
        }
        mv.addObject("storage_time", storage_time);
        if(warehouse_id!=null&&!"".equals(warehouse_id)){
        	 qo.addQuery("obj.warehouse.id", new SysMap("warehouse_id",Long.valueOf(warehouse_id) ),"=");
        }
        mv.addObject("warehouse_id", warehouse_id);
        if(agentName!=null&&!"".equals(agentName)){
       	 qo.addQuery("obj.agent.trueName", new SysMap("agentName","%"+agentName+"%" ),"like");
       }
       mv.addObject("agentName", agentName);
       
       if(supplier_id!=null&&!"".equals(supplier_id)){
       	 qo.addQuery("obj.supplier.id", new SysMap("supplier_id",Long.valueOf(supplier_id)),"=");
       }
       mv.addObject("supplier_id", supplier_id);

        IPageList pList = this.purchaseOrderService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/seller/store_purchase.htm", "", params, pList, mv);
        return mv;
    }
    
    @SecurityMapping(title = "采购入库编辑", value = "/seller/store_purchase_edit.htm*", rtype = "seller", rname = "商品入库管理", rcode = "store_purchase_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/store_purchase_edit.htm" })
    public ModelAndView store_purchase_edit(HttpServletRequest request, HttpServletResponse response,
                                       String id, String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller/store_purchase_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if(SecurityUserHolder.getCurrentUser()==null){
       	 mv = new JModelAndView("login.html",
                   this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
                   response);
       	return mv;
       }
        Long storeId = SecurityUserHolder.getCurrentUser().getStore().getId();
        Map<String, Object> map=new HashMap<String, Object>();
        map.put("storeId",storeId);
        List<Warehouse> warehouselist = this.warehouseService.query(
        		"select obj from Warehouse obj where obj.disabled=false and obj.store.id=:storeId", map, -1, -1);
        mv.addObject("warehouselist", warehouselist);
        map.clear();
        map.put("storeId",storeId);
        List<Supplier> supplierlist = this.supplierService.query(
        		"select obj from Supplier obj where obj.disabled=false and obj.store.id=:storeId", map, -1, -1);
        mv.addObject("supplierlist", supplierlist);
        
        if ((id != null) && (!id.equals(""))) {
            PurchaseOrder purchaseOrder = this.purchaseOrderService.getObjById(Long
                .valueOf(Long.parseLong(id)));
            mv.addObject("obj", purchaseOrder);
            mv.addObject("currentPage", currentPage);
        }
        return mv;
    }
    
    @SecurityMapping(title = "采购入库保存", value = "/seller/store_purchase_save.htm*", rtype = "seller", rname = "商品入库管理", rcode = "store_purchase_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/store_purchase_save.htm" })
    @Transactional
    public ModelAndView store_purchase_save(HttpServletRequest request, HttpServletResponse response,
                                       String warehouse_id, String supplier_id, String area_id) {
    	  ModelAndView mv = new JModelAndView("user/default/usercenter/success.html",
    	            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
    	            response);
        WebForm wf = new WebForm();
        PurchaseOrder purchaseOrder = null;
        if(SecurityUserHolder.getCurrentUser()==null){
          	 mv = new JModelAndView("login.html",
                      this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
                      response);
          	return mv;
          }
        purchaseOrder = (PurchaseOrder) wf.toPo(request, PurchaseOrder.class);
        Date nowdate = new Date();
        purchaseOrder.setCreatetime(nowdate);
        purchaseOrder.setDisabled(false);
        SimpleDateFormat sdf=new SimpleDateFormat("yyMMddHHmmss");
        purchaseOrder.setCode("PB"+sdf.format(new Date())+CommUtil.randomInt(3));
        Warehouse warehouse=new Warehouse();
        if(warehouse_id!=null&&!"".equals(warehouse_id)){
        	warehouse = this.warehouseService.getObjById(Long.valueOf(warehouse_id));
        	purchaseOrder.setWarehouse(warehouse);
        }
        purchaseOrder.setAgent(SecurityUserHolder.getCurrentUser());
        if(supplier_id!=null&&!"".equals(supplier_id)){
        	Supplier supplier = this.supplierService.getObjById(Long.valueOf(supplier_id));
        	purchaseOrder.setSupplier(supplier);
        }
        
        
        Store store = this.storeService.getObjByProperty("id", SecurityUserHolder.getCurrentUser()
            .getStore().getId());
        purchaseOrder.setStore(store);
        
        this.purchaseOrderService.save(purchaseOrder);
        //保存订单货品，更新货品库存
        String[] itemIds = request.getParameterValues("itemId");
        if(itemIds!=null&&itemIds.length>0){
        	for(int i=0;i<itemIds.length;i++){
        		String numbers = request.getParameter("number_"+itemIds[i]);//入库商品数量
        		String purchase_prices = request.getParameter("purchase_price_"+itemIds[i]);//采购价格
        		String remarks = request.getParameter("remark_"+itemIds[i]);//备注
        	
        		
        		GoodsItem goodsItem = this.goodsItemService.getObjById(Long.valueOf(itemIds[i]));
        		goodsItem.setGoods_inventory(goodsItem.getGoods_inventory()+Integer.valueOf(numbers));//更新库存数量
        		goodsItem.setPurchase_price(new BigDecimal(purchase_prices));//更新进货价
        		goodsItem.getGoods().setStorage_status(1);//更新商品的入库状态
        		this.goodsItemService.update(goodsItem);
        		
        		//保存库存商品
        		PurchaseOrderGoodsItem orderGoodsItem=new PurchaseOrderGoodsItem();
        		orderGoodsItem.setCreatetime(nowdate);
        		orderGoodsItem.setDisabled(false);
        		orderGoodsItem.setGoods_item(goodsItem);
        		orderGoodsItem.setGoods_name(goodsItem.getGoods().getGoods_name());
        		orderGoodsItem.setNumber(Integer.valueOf(numbers));//本次入库数量
        		orderGoodsItem.setPurchase_price(new BigDecimal(purchase_prices));
        		orderGoodsItem.setPurchaseOrder(purchaseOrder);
        		orderGoodsItem.setRemark(remarks);
        		orderGoodsItem.setSpec_info(goodsItem.getSpec_info());
        		orderGoodsItem.setUnits(goodsItem.getGoods().getGoods_units());
        		this.purchaseGoodsItemService.save(orderGoodsItem);
        		
        		//更新库存货品
        		Map<String, Object> paramMap=new HashMap<String, Object>();
        		paramMap.put("goods_item_id", goodsItem.getId());
        		paramMap.put("warehouseId", Long.valueOf(warehouse_id));
        		List<WarehouseGoodsItem> list = this.warehouseGoodsItemService.query("select obj from WarehouseGoodsItem obj where obj.goods_item.id=:goods_item_id and obj.warehouse.id=:warehouseId ", paramMap, -1, -1);
        		if(list!=null&&list.size()>0){
        			WarehouseGoodsItem warehouseGoodsItem = list.get(0);
        			warehouseGoodsItem.setWarehoust_number(warehouseGoodsItem.getWarehoust_number()+Integer.valueOf(numbers));
        			this.warehouseGoodsItemService.update(warehouseGoodsItem);
        		}else{
        			WarehouseGoodsItem warehouseGoodsItem =new WarehouseGoodsItem();
        			warehouseGoodsItem.setCreatetime(nowdate);
        			warehouseGoodsItem.setDisabled(false);
        			warehouseGoodsItem.setGoods_item(goodsItem);
        			warehouseGoodsItem.setHigh_invetory(0);
        			warehouseGoodsItem.setMin_invetory(0);
        			warehouseGoodsItem.setStore(store);
        			warehouseGoodsItem.setWarehouse(warehouse);
        			warehouseGoodsItem.setWarehoust_number(Integer.valueOf(numbers));
        			this.warehouseGoodsItemService.save(warehouseGoodsItem);
        		}
        	}
        }
        
       
      
        mv.addObject("url", CommUtil.getURL(request) + "/seller/store_purchase.htm");
        mv.addObject("op_title", "入库单保存成功");
        return mv;
    }
    
    
    /**
     * 采购入库货品选择
     * @param request
     * @param response
     * @param currentPage
     * @param orderBy
     * @param orderType
     * @return
     */
    @RequestMapping({ "/seller/purchase_goods_choose.htm" })
    public ModelAndView purchase_goods_choose(HttpServletRequest request, HttpServletResponse response,
                                  String currentPage, String orderBy, String orderType,String goods_name) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller/store_purchase_goods.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        
        if(SecurityUserHolder.getCurrentUser()==null){
        	 mv = new JModelAndView("login.html",
                    this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
                    response);
        	return mv;
        }
        String params = "";
        GoodsItemQueryObject qo = new GoodsItemQueryObject(currentPage, mv, orderBy,orderType);
     
        qo.addQuery("obj.disabled", new SysMap("disabled",false ),"=");
        qo.addQuery("obj.goods.goods_store.id", new SysMap("store_id",SecurityUserHolder.getCurrentUser()
                .getStore().getId()), "=");
        
        qo.addQuery("obj.goods.disabled",new SysMap("goodsdisabled",false ),"=");
        
        if(goods_name!=null&&!"".equals(goods_name)){
        	 qo.addQuery("obj.goods.goods_name", new SysMap("goods_name","%"+goods_name+"%"),"like");
        }
        mv.addObject("goods_name", goods_name);
        IPageList pList = this.goodsItemService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/seller/purchase_goods_choose.htm", "", params, pList, mv);
        return mv;
    }
    
    @SecurityMapping(title = "采购入库查看", value = "/seller/store_purchase_edit.htm*", rtype = "seller", rname = "商品入库管理", rcode = "store_purchase_seller", rgroup = "店铺设置")
    @RequestMapping({ "/seller/store_purchase_view.htm" })
    public ModelAndView store_purchase_view(HttpServletRequest request, HttpServletResponse response,
                                       String id, String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller/store_purchase_view.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if(SecurityUserHolder.getCurrentUser()==null){
       	 mv = new JModelAndView("login.html",
                   this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
                   response);
       	 return mv;
        }
        if ((id != null) && (!id.equals(""))) {
            PurchaseOrder purchaseOrder = this.purchaseOrderService.getObjById(Long
                .valueOf(Long.parseLong(id)));
            mv.addObject("obj", purchaseOrder);
        }
        return mv;
    }
    
    @RequestMapping({ "/seller/store_purchase_view_ajax.htm" })
    public ModelAndView store_purchase_view_ajax(HttpServletRequest request, HttpServletResponse response,
                                       String id, String currentPage) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/seller/store_purchase_view_ajax.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if(SecurityUserHolder.getCurrentUser()==null){
       	 mv = new JModelAndView("login.html",
                   this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request,
                   response);
       	 return mv;
        }
        Map<String, Object> paramMap=new HashMap<String, Object>();
        paramMap.put("orderId", Long.valueOf(id));
       List<PurchaseOrderGoodsItem> list = this.purchaseGoodsItemService.query("select obj from PurchaseOrderGoodsItem obj where obj.purchaseOrder.id=:orderId", paramMap, -1, -1);
       mv.addObject("orderItemList", list); 
       return mv;
    }
}
