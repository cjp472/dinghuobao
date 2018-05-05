package com.javamalls.platform.service.impl;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.javamalls.base.query.GenericPageList;
import com.javamalls.base.query.PageObject;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.dao.EntryOrderDao;
import com.javamalls.platform.domain.EntryOrder;
import com.javamalls.platform.domain.EntryOrderDetail;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.GoodsItem;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.service.IEntryOrderDetailService;
import com.javamalls.platform.service.IEntryOrderService;
import com.javamalls.platform.service.IGoodsItemService;
import com.javamalls.platform.service.IGoodsService;

/**
 * 出入单服务
 * 
 * @Filename: EntryOrderServiceImpl.java
 * @Version: 1.0
 * 
 */
@Service
@Transactional
public class EntryOrderServiceImpl implements IEntryOrderService {
	private static Logger log = LogManager.getLogger(EntryOrderServiceImpl.class);

	@Resource(name = "entryOrderDao")
	private EntryOrderDao entryOrderDao;
	@Autowired
	private IGoodsItemService goodsItemService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IEntryOrderDetailService entryOrderDetailService;

	@Override
	public boolean save(EntryOrder entryOrder) {
		try {
			this.entryOrderDao.save(entryOrder);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public EntryOrder getObjById(Long id) {
		EntryOrder result = (EntryOrder) this.entryOrderDao.get(id);
		if (result != null) {
			return result;
		}
		return null;
	}

	/**
	 * 逻辑删除
	 **/
	public boolean delete(Long id) {
		try {
			EntryOrder obj = this.getObjById(CommUtil.null2Long(id));
			obj.setDisabled(true);
			this.entryOrderDao.update(obj);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean batchDelete(List<Serializable> ids) {
		for (Serializable id : ids) {
			delete((Long) id);
		}
		return true;
	}

	public IPageList list(IQueryObject properties) {
		if (properties == null) {
			return null;
		}
		String query = properties.getQuery();
		Map params = properties.getParameters();
		GenericPageList pList = new GenericPageList(EntryOrder.class, query, params, this.entryOrderDao);
		if (properties != null) {
			PageObject pageObj = properties.getPageObj();
			if (pageObj != null) {
				pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj.getCurrentPage().intValue(),
						pageObj.getPageSize() == null ? 0 : pageObj.getPageSize().intValue());
			}
		} else {
			pList.doList(0, -1);
		}
		return pList;
	}

	public boolean update(EntryOrder obj) {
		try {
			this.entryOrderDao.update(obj);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public List<EntryOrder> query(String query, Map params, int begin, int max) {
		return this.entryOrderDao.query(query, params, begin, max);
	}

	@Override
	@Transactional
	public boolean saveEntryOrderAndGoodsAndGoodsItem(String[] numbers, String[] itemIds, EntryOrder entryOrder, User userInStore) {
		User user = SecurityUserHolder.getCurrentUser();
		if (itemIds != null && itemIds.length > 0) {
			Date now = new Date();
			for (int i = 0; i < itemIds.length; i++) {
				GoodsItem goodsItem = this.goodsItemService.getObjById(CommUtil.null2Long(itemIds[i]));
				if (goodsItem != null && !goodsItem.equals("")) {
					// 更新原来商店的库存
					goodsItem.setGoods_inventory(goodsItem.getGoods_inventory() - CommUtil.null2Int(numbers[i]));
					goodsItemService.update(goodsItem);
					Goods goods = goodsService.getObjById(goodsItem.getGoods().getId());
					goods.setGoods_inventory(goods.getGoods_inventory() - CommUtil.null2Int(numbers[i]));
					goodsService.update(goods);
					// 入库单明细
					EntryOrderDetail entryOrderDetail = new EntryOrderDetail();
					entryOrderDetail.setCreatetime(now);
					entryOrderDetail.setDisabled(false);
					entryOrderDetail.setNumber(CommUtil.null2Int(numbers[i]));
					entryOrderDetail.setEntryOrder(entryOrder);
					entryOrderDetail.setCreateuser(user);
					entryOrderDetail.setGoods(goodsItem.getGoods());
					entryOrderDetail.setSpec_info(goodsItem.getSpec_info());
					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put("goodsId", goodsItem.getGoods().getId());
					paramMap.put("storeId", userInStore.getStore().getId());
					List<EntryOrderDetail> listEntryOrderDetail = entryOrderDetailService.query(
							" select obj from EntryOrderDetail obj where obj.disabled=0 and obj.goods.id=:goodsId "
									+ "and obj.goods.goods_status=0 and obj.entryOrder.inStore.id=:storeId",
							paramMap, -1, -1);
					// 判断此商品是否已经添加到该商铺
					if (listEntryOrderDetail != null && listEntryOrderDetail.size() > 0) {
						// 获取商品直接更新库存
						Goods oldGoods = goodsService.getObjById(listEntryOrderDetail.get(0).getNew_goods().getId());
						oldGoods.setGoods_inventory(oldGoods.getGoods_inventory() + CommUtil.null2Int(numbers[i]));
						goodsService.update(oldGoods);

						Map<String, Object> map = new HashMap<String, Object>();
						map.put("goodsId", oldGoods.getId());
						List<GoodsItem> oldGoodsItemList = goodsItemService.query(
								"select obj from GoodsItem obj where obj.disabled=0 and obj.goods.id=:goodsId", map, -1,
								-1);
						if (oldGoodsItemList != null && oldGoodsItemList.size() > 0) {
							oldGoodsItemList.get(0).setGoods_inventory(
									oldGoodsItemList.get(0).getGoods_inventory() + CommUtil.null2Int(numbers[i]));
							goodsItemService.update(oldGoodsItemList.get(0));
						}
						// 入库单明细

						entryOrderDetail.setNew_goods(oldGoods);
					} else {
						// 入库商家添加新的商品到新的商店
						Goods goodsInNew = new Goods();
						goodsInNew.setCreatetime(new Date());
						goodsInNew.setDisabled(false);
						goodsInNew.setGoods_click(goods.getGoods_click());
						goodsInNew.setGoods_details(goods.getGoods_details());
						// 库存
						goodsInNew.setGoods_inventory(CommUtil.null2Int(numbers[i]));
						goodsInNew.setGoods_inventory_detail(goods.getGoods_inventory_detail());
						goodsInNew.setGoods_name(goods.getGoods_name());
						goodsInNew.setDist_price(goods.getDist_price());
						goodsInNew.setGoods_price(goods.getGoods_price());
						goodsInNew.setGoods_property(goods.getGoods_property());
						goodsInNew.setGoods_salenum(goods.getGoods_salenum());
						goodsInNew.setGoods_seller_time(goods.getGoods_seller_time());
						goodsInNew.setGoods_serial(goods.getGoods_serial());
						goodsInNew.setGoods_status(goods.getGoods_status());
						goodsInNew.setGoods_transfee(goods.getGoods_transfee());
						goodsInNew.setGoods_weight(goods.getGoods_weight());
						goodsInNew.setInventory_type(goods.getInventory_type());
						goodsInNew.setSeo_description(goods.getSeo_description());
						goodsInNew.setSeo_keywords(goods.getSeo_keywords());
						goodsInNew.setStore_price(goods.getStore_price());
						goodsInNew.setGc(goods.getGc());
						goodsInNew.setGoods_brand(goods.getGoods_brand());
						goodsInNew.setGoods_main_photo(goods.getGoods_main_photo());
						// 商店ID
						goodsInNew.setGoods_store(userInStore.getStore());
						goodsInNew.setGoods_collect(goods.getGoods_collect());
						goodsInNew.setActivity_status(goods.getActivity_status());
						goodsInNew.setBargain_status(goods.getBargain_status());
						goodsInNew.setDelivery_status(goods.getDelivery_status());
						goodsInNew.setGoods_current_price(goods.getGoods_current_price());
						goodsInNew.setGoods_volume(goods.getGoods_volume());
						goodsInNew.setEms_trans_fee(goods.getEms_trans_fee());
						goodsInNew.setExpress_trans_fee(goods.getExpress_trans_fee());
						goodsInNew.setTransport(goods.getTransport());
						goodsInNew.setCombin_status(goods.getCombin_status());
						goodsInNew.setGoodsTypeId(goods.getGoodsTypeId());
						goodsInNew.setStorage_status(goods.getStorage_status());
						goodsInNew.setGoods_units(goods.getGoods_units());
						goodsInNew.setGoods_news_status(goods.getGoods_news_status());
						goodsInNew.setGoods_hot_status(goods.getGoods_hot_status());
						goodsInNew.setTotal_weight(goods.getTotal_weight());
						goodsInNew.setType_ratio(goods.getType_ratio());
						goodsInNew.setColor_ratio(goods.getColor_ratio());
						goodsInNew.setSize_ratio(goods.getSize_ratio());
						goodsInNew.setGoods_type(goods.getGoods_type());
						goodsInNew.setSingle_weight(goods.getSingle_weight());
						goodsInNew.setRetrieval_ids(goods.getRetrieval_ids());
						goodsInNew.setExtendedAttributes(goods.getExtendedAttributes());
						goodsInNew.setSupplier_info(goods.getSupplier_info());
						goodsService.save(goodsInNew);
						// 入库商家添加新的商品到GoodsItem
						GoodsItem goodsItemNew = new GoodsItem();
						goodsItemNew.setCreatetime(new Date());
						goodsItemNew.setDisabled(false);
						goodsItemNew.setSpec_combination(goodsItem.getSpec_combination());
						goodsItemNew.setGoods_inventory(CommUtil.null2Int(numbers[i]));
						goodsItemNew.setGoods_price(goodsItem.getGoods_price());
						goodsItemNew.setSpec_info(goodsItem.getSpec_info());
						goodsItemNew.setBar_code(goodsItem.getBar_code());
						goodsItemNew.setSelf_code(goodsItem.getSelf_code());
						goodsItemNew.setGoods(goodsInNew);
						goodsItemNew.setStatus(goodsItem.getStatus());
						goodsItemNew.setDist_price(goodsItem.getDist_price());
						goodsItemNew.setMarket_price(goodsItem.getMarket_price());
						goodsItemNew.setPurchase_price(goodsItem.getPurchase_price());
						goodsItemNew.setStep_price_state(goodsItem.getStep_price_state());
						goodsItemService.save(goodsItemNew);

						entryOrderDetail.setNew_goods(goodsInNew);

					}
					entryOrderDetailService.save(entryOrderDetail);
				}
			}
			return true;
		}
		return false;
	}

}