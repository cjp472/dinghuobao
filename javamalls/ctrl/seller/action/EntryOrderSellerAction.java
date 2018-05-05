package com.javamalls.ctrl.seller.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.javamalls.front.web.tools.GoodsViewTools;
import com.javamalls.front.web.tools.StoreViewTools;
import com.javamalls.platform.domain.EntryOrder;
import com.javamalls.platform.domain.EntryOrderDetail;
import com.javamalls.platform.domain.ExpressCompany;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.GoodsCart;
import com.javamalls.platform.domain.GoodsItem;

import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.WarehouseGoodsItem;
import com.javamalls.platform.domain.query.EntryOrderDetailQueryObject;
import com.javamalls.platform.domain.query.EntryOrderQueryObject;
import com.javamalls.platform.domain.query.GoodsItemQueryObject;
import com.javamalls.platform.domain.query.UserQueryObject;
import com.javamalls.platform.service.IEntryOrderDetailService;
import com.javamalls.platform.service.IEntryOrderService;
import com.javamalls.platform.service.IExpressCompanyService;
import com.javamalls.platform.service.IGoodsItemService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;
import com.javamalls.platform.service.IWarehouseGoodsItemService;
import com.javamalls.platform.service.impl.EntryOrderDetailServiceImpl;

@Controller
public class EntryOrderSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private IEntryOrderService entryOrderService;
	@Autowired
	private IExpressCompanyService expressCompanyService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoodsItemService goodsItemService;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IUserService userService;
	@Autowired
	private IEntryOrderDetailService entryOrderDetailService;
	@Autowired
	private IWarehouseGoodsItemService warehouseGoodsItemService;
	@Autowired
	private IGoodsService goodsService;

	@SecurityMapping(title = "出入单列表", value = "/seller/entryOrder.htm*", rtype = "entryOrder", rname = "出入单管理", rcode = "entryOrder_seller", rgroup = "出入单管理")
	@RequestMapping({ "/seller/entryOrder.htm" })
	public ModelAndView order(HttpServletRequest request, HttpServletResponse response, String currentPage,
			Integer type, String ordernumber, String beginTime, String endTime) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/seller_order_entry.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		EntryOrderQueryObject enqo = new EntryOrderQueryObject(currentPage, mv, "createtime", "desc");

		User user = SecurityUserHolder.getCurrentUser();
		enqo.addQuery("obj.outStore.id", new SysMap("id", user.getStore().getId()), "=");

		if (!CommUtil.null2String(type).equals("")) {
			enqo.addQuery("obj.type", new SysMap("type", type), "=");
		}

		if (!CommUtil.null2String(ordernumber).equals("")) {
			enqo.addQuery("obj.ordernumber", new SysMap("ordernumber", "%" + ordernumber + "%"), "like");
		}
		if (!CommUtil.null2String(beginTime).equals("")) {
			enqo.addQuery("obj.createtime", new SysMap("beginTime", CommUtil.formatMaxDate(beginTime + " 00:00:00")),
					">=");
		}
		if (!CommUtil.null2String(endTime).equals("")) {
			enqo.addQuery("obj.endtime", new SysMap("endTime", CommUtil.formatMaxDate(endTime + " 23:59:59")), "<=");
		}

		enqo.addQuery("obj.disabled", new SysMap("disabled", false), "=");
		IPageList pList = this.entryOrderService.list(enqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("storeViewTools", this.storeViewTools);
		mv.addObject("ordernumber", ordernumber);
		mv.addObject("type", type);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		return mv;
	}

	@SecurityMapping(title = "添加出货单", value = "/seller/entry_order_add.htm*", rtype = "entryOrder", rname = "出入单管理", rcode = "entryOrder_seller", rgroup = "出入单管理")
	@RequestMapping({ "/seller/entry_order_add.htm" })
	public ModelAndView entry_order_add(HttpServletRequest request, HttpServletResponse response) {
		User user = SecurityUserHolder.getCurrentUser();
		ModelAndView mv = new JModelAndView("user/default/usercenter/seller/seller_order_entry_add.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		if (user == null) {
			mv = new JModelAndView("login.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			return mv;
		}
		// 快递公司
		List<ExpressCompany> listExpressCompany = expressCompanyService.query(
				"select obj from ExpressCompany obj where obj.disabled=0 and obj.company_status=0 order by obj.company_sequence Asc",
				null, -1, -1);
		// 入货商户
		Map<String, Object> params = new HashMap<String, Object>();

		/* params.put("store_id", user.getStore().getId()); */
		List<Store> listStore = storeService
				.query("select obj from Store obj where obj.disabled=0 and obj.store_status=2 and obj.id!="
						+ user.getStore().getId() + " order by obj.createtime desc", params, -1, -1);
		// 出入单编号
		mv.addObject("ordernumber",
				SecurityUserHolder.getCurrentUser().getId() + CommUtil.formatTime("yyyyMMddHHmmss", new Date()));
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formatDate = df.format(date);
		mv.addObject("nowTime", formatDate);
		mv.addObject("listExpressCompany", listExpressCompany);
		mv.addObject("listStore", listStore);
		mv.addObject("user", user);
		return mv;
	}

	// 商品的选择
	@RequestMapping({ "/seller/entry_order_goods_choose.htm" })
	public ModelAndView entry_order_goods_choose(HttpServletRequest request, HttpServletResponse response,
			String currentPage, String orderBy, String orderType, String goods_name, String buyer_id) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/seller/seller_order_entry_goodsChoose.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
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
		qo.addQuery("obj.goods_inventory", new SysMap("goods_inventory", 0), ">");
		qo.addQuery("obj.goods.goods_store.id",
				new SysMap("store_id", SecurityUserHolder.getCurrentUser().getStore().getId()), "=");

		qo.addQuery("obj.goods.disabled", new SysMap("goodsdisabled", false), "=");
		qo.addQuery("obj.goods.goods_status", new SysMap("goods_status", 0), "=");

		if (goods_name != null && !"".equals(goods_name)) {

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("goods_name", "%" + goods_name + "%");
			map.put("bar_code", "%" + goods_name + "%");
			qo.addQuery(" (obj.goods.goods_name like :goods_name or obj.bar_code like :bar_code  ) ", map);

		}
		mv.addObject("goods_name", goods_name);
		IPageList pList = this.goodsItemService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/seller_entry_order_goodsChoose.html", "", params, pList, mv);
		mv.addObject("buyer_id", buyer_id);
		mv.addObject("goodsViewTools", goodsViewTools);
		return mv;
	}

	/**
	 * 出入单客户选择
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param type
	 * @param quserName
	 * @return
	 */
	@RequestMapping({ "/seller/entry_order_clientChoose.htm" })
	public ModelAndView storeUser(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String type, String quserName) {
		ModelAndView mv = new JModelAndView("user/default/usercenter/seller/seller_order_entry_clientchoose.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		UserQueryObject qo = new UserQueryObject(currentPage, mv, orderBy, orderType);

		qo.addQuery("obj.userRole", new SysMap("userRole", "ADMIN"), "!=");
		qo.addQuery("obj.parent.id is null ", null);
		if (quserName != null && !"".equals(quserName)) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("client_name", "%" + quserName + "%");
			map.put("mobile", "%" + quserName + "%");
			map.put("trueName", "%" + quserName + "%");
			qo.addQuery(
					" (obj.client_name like :client_name or obj.mobile like :mobile or obj.trueName like :trueName ) ",
					map);
		}

		qo.addQuery("obj.disabled ", new SysMap("disabled", false), "=");
		mv.addObject("quserName", quserName);
		qo.addQuery("obj.store.id !=null", null);
		qo.addQuery("obj.store.store_status=2", null);
		User user=SecurityUserHolder.getCurrentUser();
		//qo.addQuery("obj.id !="+user.getId(),null);
		qo.addQuery("obj.id ", new SysMap("id", user.getId()), "!=");
		IPageList pList = null;
		try {
			pList = this.userService.list(qo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		CommUtil.saveIPageList2ModelAndView(url + "/seller/entry_order_clientChoose.htm", "", "", pList, mv);
		return mv;
	}


	@SecurityMapping(title = "出入单保存", value = "/entry_order_save.htm*", rtype = "entryOrder", rname = "出入单管理", rcode = "entryOrder_seller", rgroup = "出入单管理")
	@RequestMapping({ "/seller/entry_order_save.htm" })
	public ModelAndView valet_order_save(HttpServletRequest request, HttpServletResponse response, String nowTime,
			String ordernumber, String type, String exCompany, String expressCompanyNumber, String client) {
		ModelAndView mv = new JModelAndView("success.html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		if (user == null) {
			mv = new JModelAndView("login.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			return mv;
		} else {
			mv.addObject("op_title", "出入单保存成功！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/entryOrder.htm");
		}
		// 保存出入单
		EntryOrder entryOrder = new EntryOrder();

		entryOrder.setCreatetime(CommUtil.formatMaxDate(nowTime));
		entryOrder.setCreateuser(user);
		entryOrder.setDisabled(false);
		entryOrder.setType(CommUtil.null2Int(type));
		// 快递公司
		ExpressCompany expressCompany = expressCompanyService.getObjById(CommUtil.null2Long(exCompany));
		entryOrder.setCourierCompany(expressCompany);
		entryOrder.setOrdernumber(ordernumber);
		entryOrder.setEndtime(new Date());
		entryOrder.setOutStore(user.getStore());
		entryOrder.setStatus(0);
		User userInStore = userService.getObjById(CommUtil.null2Long(client));
		entryOrder.setInStore(userInStore.getStore());
		entryOrder.setCouriernumber(expressCompanyNumber);
		entryOrderService.save(entryOrder);
		String[] numbers = request.getParameterValues("number");
		String[] itemIds = request.getParameterValues("itemId");
		entryOrderService.saveEntryOrderAndGoodsAndGoodsItem( numbers,itemIds,entryOrder,userInStore);
		return mv;
	}

	@SecurityMapping(title = "出货单详情", value = "/seller/entry_order_detail.htm*", rtype = "entryOrder", rname = "出入单管理", rcode = "entryOrder_seller", rgroup = "出入单管理")
	@RequestMapping({ "/seller/entry_order_detail.htm" })
	public ModelAndView entry_order_detail(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage, String orderBy, String orderType) {
		User user = SecurityUserHolder.getCurrentUser();
		ModelAndView mv = new JModelAndView("user/default/usercenter/seller/seller_order_entry_detail.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		if (user == null) {
			mv = new JModelAndView("login.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			return mv;
		}
		// 出入单
		EntryOrder entryOrder = entryOrderService.getObjById(CommUtil.null2Long(id));
		if (entryOrder != null && !entryOrder.equals("")) {
			EntryOrderDetailQueryObject qo = new EntryOrderDetailQueryObject(currentPage, mv, orderBy, orderType);

			qo.addQuery("obj.entryOrder.id", new SysMap("id", entryOrder.getId()), "=");
			qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");
			IPageList pList = null;
			try {
				pList = this.entryOrderDetailService.list(qo);
				CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);

			} catch (Exception e) {
				e.printStackTrace();
			}
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("entryOrderId", entryOrder.getId());
			List<EntryOrderDetail> list = entryOrderDetailService.query(
					"select obj from EntryOrderDetail obj where obj.disabled=0 and obj.entryOrder.id=:entryOrderId",
					paramMap, -1, -1);
			int num = 0;
			int allNum = 0;
			for (int i = 0; i < list.size(); i++) {
				num = list.get(i).getNumber();
				allNum += num;

			}
			mv.addObject("allNum", allNum);

			BigDecimal price = null;
			BigDecimal allPrice = new BigDecimal("0");

			for (int i = 0; i < list.size(); i++) {
				price = list.get(i).getGoods().getStore_price().multiply(new BigDecimal(list.get(i).getNumber()));
				allPrice = price.add(allPrice);
			}
			mv.addObject("allPrice", allPrice);

		}
		// 快递公司
		List<ExpressCompany> listExpressCompany = expressCompanyService.query(
				"select obj from ExpressCompany obj where obj.disabled=0 and obj.company_status=0 order by obj.company_sequence Asc",
				null, -1, -1);
		// 入货商户
		Map<String, Object> params = new HashMap<String, Object>();

		/* params.put("store_id", user.getStore().getId()); */
		List<Store> listStore = storeService
				.query("select obj from Store obj where obj.disabled=0 and obj.store_status=2 and obj.id!="
						+ user.getStore().getId() + " order by obj.createtime desc", params, -1, -1);
		mv.addObject("listExpressCompany", listExpressCompany);
		mv.addObject("listStore", listStore);
		mv.addObject("user", user);
		mv.addObject("entryOrder", entryOrder);
		return mv;
	}

}
