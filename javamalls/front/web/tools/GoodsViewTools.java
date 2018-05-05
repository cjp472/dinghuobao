package com.javamalls.front.web.tools;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.GoodsBrand;
import com.javamalls.platform.domain.GoodsClass;
import com.javamalls.platform.domain.GoodsItem;
import com.javamalls.platform.domain.GoodsSpecProperty;
import com.javamalls.platform.domain.GoodsSpecification;
import com.javamalls.platform.domain.Strategy;
import com.javamalls.platform.domain.StrategyGoodsItem;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.UserGoodsClass;
import com.javamalls.platform.domain.UserStoreDistributor;
import com.javamalls.platform.service.IGoodsBrandService;
import com.javamalls.platform.service.IGoodsClassService;
import com.javamalls.platform.service.IGoodsItemService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IStrategyGoodsItemService;
import com.javamalls.platform.service.IUserGoodsClassService;
import com.javamalls.platform.service.IUserService;
import com.javamalls.platform.service.IUserStoreDistributorService;

/**商品
 *                       
 * @Filename: GoodsViewTools.java
 * @Version: 2.7.0
 * @Author: 王阳
 * @Email: wangyang@163.com
 *
 */
@Component
public class GoodsViewTools {
    @Autowired
    private IGoodsService          goodsService;
    @Autowired
    private IGoodsClassService     goodsClassService;
    @Autowired
    private IUserGoodsClassService userGoodsClassService;
    @Autowired
    private IStrategyGoodsItemService strategyGoodsItemService;
    @Autowired
    private IUserService      userService;

    @Autowired
    private IGoodsItemService      goodsItemService;
    @Autowired
    private IGoodsBrandService      goodsBrandService;
    @Autowired
    private IUserStoreDistributorService userStoreDistributorService;
    
    public List<GoodsSpecification> generic_spec(String id) {
        List<GoodsSpecification> specs = new ArrayList<GoodsSpecification>();
        if ((id != null) && (!id.equals(""))) {
            Goods goods = this.goodsService.getObjById(Long.valueOf(Long.parseLong(id)));
            for (GoodsSpecProperty gsp : goods.getGoods_specs()) {
                GoodsSpecification spec = gsp.getSpec();
                if (!specs.contains(spec)) {
                    specs.add(spec);
                }
            }
        }
        Collections.sort(specs, new Comparator() {
            public int compare(Object gs1, Object gs2) {
                return ((GoodsSpecification) gs1).getSequence()
                       - ((GoodsSpecification) gs2).getSequence();
            }
        });
        return specs;
    }

    public List<UserGoodsClass> query_user_class(String pid) {
        List<UserGoodsClass> list = new ArrayList<UserGoodsClass>();
        if ((pid == null) || (pid.equals(""))) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("uid", SecurityUserHolder.getCurrentUser().getId());
            list = this.userGoodsClassService
                .query(
                    "select obj from UserGoodsClass obj where obj.parent.id is null and obj.user.id = :uid order by obj.sequence asc",
                    map, -1, -1);
        } else {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("pid", Long.valueOf(Long.parseLong(pid)));
            params.put("uid", SecurityUserHolder.getCurrentUser().getId());
            list = this.userGoodsClassService
                .query(
                    "select obj from UserGoodsClass obj where obj.parent.id=:pid and obj.user.id = :uid order by obj.sequence asc",
                    params, -1, -1);
        }
        return list;
    }

    public List<Goods> query_with_gc(String gc_id, int count) {
        List<Goods> list = new ArrayList<Goods>();
        GoodsClass gc = this.goodsClassService.getObjById(CommUtil.null2Long(gc_id));
        if (gc != null) {
            Set<Long> ids = genericIds(gc);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("ids", ids);
            params.put("goods_status", Integer.valueOf(0));
            list = this.goodsService
                .query(
                    "select obj from Goods obj where obj.gc.id in (:ids) and obj.goods_status=:goods_status order by obj.goods_click desc",
                    params, 0, count);
        }
        return list;
    }

    private Set<Long> genericIds(GoodsClass gc) {
        Set<Long> ids = new HashSet<Long>();
        ids.add(gc.getId());
        for (GoodsClass child : gc.getChilds()) {
            Set<Long> cids = genericIds(child);
            for (Long cid : cids) {
                ids.add(cid);
            }
            ids.add(child.getId());
        }
        return ids;
    }

    public List<Goods> sort_sale_goods(String store_id, int count) {
        List<Goods> list = new ArrayList<Goods>();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("store_id", CommUtil.null2Long(store_id));
        params.put("goods_status", Integer.valueOf(0));
        list = this.goodsService
            .query(
                "select obj from Goods obj where obj.disabled=false and  obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_salenum desc",
                params, 0, count);
        return list;
    }

    public List<Goods> sort_collect_goods(String store_id, int count) {
        List<Goods> list = new ArrayList<Goods>();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("store_id", CommUtil.null2Long(store_id));
        params.put("goods_status", Integer.valueOf(0));
        list = this.goodsService
            .query(
                "select obj from Goods obj where obj.disabled=false and  obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_collect desc",
                params, 0, count);
        return list;
    }

    public List<Goods> query_combin_goods(String id) {
        return this.goodsService.getObjById(CommUtil.null2Long(id)).getCombin_goods();
    }
    
    /**
     * 获取商品价格
     * @param user
     * @param goodsId
     * @param itemId
        1、未登录 ，只展示销售价；
		3、已登录非店铺分销商： 只展示销售价；    
		4、已登录是分销商，未设置价格策略 ： 展示分销价；
		5、已登录是分销商，有价格策略  : 展示分销价*价格策略折扣
     * @return
     */
    public BigDecimal  getStrategyPrice(Goods goods,String itemId){
    	User user = SecurityUserHolder.getCurrentUser();
    	GoodsItem goodsItem = null;
    	if(itemId!=null&&!"".equals(itemId)){
    		goodsItem = this.goodsItemService.getObjById(CommUtil.null2Long(itemId));
    	}else{
    		List<GoodsItem> goods_item_list = goods.getGoods_item_list();
    		if(goods_item_list!=null&&goods_item_list.size()>0){
        		goodsItem = goods_item_list.get(0);
        	} 
    	}
    	
    	if(user!=null){//已登录
    		user = this.userService.getObjById(user.getId());
    		//判断是否为分销商
    		Map<String,Object> map = new HashMap<String,Object>();
    		map.put("userId", user.getId());
    		Long storeId= goods.getGoods_store().getId();
    		map.put("storeId",storeId);
    		List<UserStoreDistributor> distributors = this.userStoreDistributorService.query("" +
    				" select obj from UserStoreDistributor obj where obj.disabled = 0 " +
    				" and obj.user.id=:userId and obj.store.id =:storeId and obj.status=2", map, -1, -1);
    		if(distributors!=null&&distributors.size()>0){//是分销商
    			//判断是否有价格策略
    			Strategy strategy = user.getStrategy(storeId);
    			if(strategy!=null){//5、已登录是分销商，有价格策略  : 展示分销价*价格策略折扣
    				BigDecimal discount = strategy.getDiscount().divide(new BigDecimal(100),2,BigDecimal.ROUND_HALF_UP);//折扣百分比
    				BigDecimal dist_price = goods.getDist_price();
    				if(goodsItem!=null){
    					dist_price = goodsItem.getDist_price();
    				}
    				BigDecimal price = dist_price.multiply(discount).setScale(2, BigDecimal.ROUND_HALF_UP);
    				return price;
    			}else{//4、已登录是分销商，未设置价格策略 ： 展示分销价；
    				BigDecimal price = goods.getDist_price();
    				if(goodsItem!=null){
    					price = goodsItem.getDist_price();
    				}
    	    		return price;
    			}
    		}else{//3、已登录非店铺分销商： 只展示销售价；
    			BigDecimal price = goods.getStore_price();
    			if(goodsItem!=null){
    				price = goodsItem.getGoods_price();
				}
        		return price;
    		}
    	}else{//1、未登录 ，只展示销售价；
    		BigDecimal price = goods.getStore_price();
    		if(goodsItem!=null){
				price = goodsItem.getGoods_price();
			}
    		return price;
    	}
    }
    
    /**
     * 已废弃此方法
     * 根据价格策略获取商品实际价格
     * @param userId
     * @param goodsId
     * @return
     */
    public GoodsItem strategyPrice(String userId,String goodsId){
    	Goods goods = this.goodsService.getObjById(CommUtil.null2Long(goodsId));
    	List<GoodsItem> goods_item_list = goods.getGoods_item_list();
    	GoodsItem item=new GoodsItem();
    	if(goods_item_list!=null&&goods_item_list.size()>0){
    		GoodsItem goodsItem = goods_item_list.get(0);
    		User user = this.userService.getObjById(CommUtil.null2Long(userId));
    		Strategy strategy = user.getStrategy();
			if(user!=null&&strategy!=null&&strategy.isDisabled()==false&&strategy.getStatus()==1){
    			//查询策略的货品的价格
    			Map<String, Object> paramMap=new HashMap<String, Object>();
    			paramMap.put("strategyId", user.getStrategy().getId());
    			paramMap.put("goodItemId", goodsItem.getId());
    			List<StrategyGoodsItem> list = this.strategyGoodsItemService.query(
    					"select obj from StrategyGoodsItem obj where obj.strategy.id=:strategyId and obj.goods_item.id=:goodItemId", paramMap, 0, 1);
    			if(list!=null&&list.size()>0){//此商品有策略
    				item.setGoods_price(list.get(0).getPrice());//销售价
    				item.setMarket_price(goodsItem.getMarket_price());//市场价
        			item.setStatus(goodsItem.getStatus());
        			item.setGoods_inventory(goodsItem.getGoods_inventory());
        			item.setId(goodsItem.getId());
    			}else{
    				item.setGoods_price(goodsItem.getGoods_price());//销售价
    				item.setMarket_price(goodsItem.getMarket_price());//市场价
        			item.setStatus(goodsItem.getStatus());
        			item.setGoods_inventory(goodsItem.getGoods_inventory());
        			item.setId(goodsItem.getId());
    			}
    		}else{//没有策略
    			item.setGoods_price(goodsItem.getGoods_price());//销售价
    			item.setMarket_price(goodsItem.getMarket_price());//市场价
    			item.setStatus(goodsItem.getStatus());
    			item.setGoods_inventory(goodsItem.getGoods_inventory());
    			item.setId(goodsItem.getId());
    		}
    	}else{
    		item.setGoods_price(goods.getStore_price());//销售价
    		item.setMarket_price(goods.getGoods_price());//市场价
			item.setStatus(0);
    	}
    	return item;
    }
    
    /**
     * 根据价格策略获取商品实际价格
     * @param userId
     * @param goodsId
     * @return
     */
    public GoodsItem strategyPricebyItem(String userId,String itemId){
    
    	GoodsItem item=new GoodsItem();
    	GoodsItem goodsItem=this.goodsItemService.getObjById(CommUtil.null2Long(itemId));
    	if(goodsItem!=null){
    		if(userId!=null&&!"".equals(userId)){
	    		User user = this.userService.getObjById(CommUtil.null2Long(userId));
	    		Strategy strategy = user.getStrategy();
    			if(user!=null&&strategy!=null&&strategy.isDisabled()==false&&strategy.getStatus()==1){
	    			//查询策略的货品的价格
	    			Map<String, Object> paramMap=new HashMap<String, Object>();
	    			paramMap.put("strategyId", user.getStrategy().getId());
	    			paramMap.put("goodItemId", goodsItem.getId());
	    			List<StrategyGoodsItem> list = this.strategyGoodsItemService.query(
	    					"select obj from StrategyGoodsItem obj where obj.strategy.id=:strategyId and obj.goods_item.id=:goodItemId", paramMap, 0, 1);
	    		//	System.out.println(list+"=================");
	    			if(list!=null&&list.size()>0){//此商品有策略
	    				item.setGoods_price(list.get(0).getPrice());
	        			item.setStatus(goodsItem.getStatus());
	        			item.setGoods_inventory(goodsItem.getGoods_inventory());
	    			}else{
	    				item.setGoods_price(goodsItem.getGoods_price());
	        			item.setStatus(goodsItem.getStatus());
	        			item.setGoods_inventory(goodsItem.getGoods_inventory());
	    			}
	    		}else{//没有策略
	    			item.setGoods_price(goodsItem.getGoods_price());
	    			item.setStatus(goodsItem.getStatus());
	    			item.setGoods_inventory(goodsItem.getGoods_inventory());
	    		}
    		}else{//没有登录
    			item.setGoods_price(goodsItem.getGoods_price());
    			item.setStatus(goodsItem.getStatus());
    			item.setGoods_inventory(goodsItem.getGoods_inventory());
    		}
    	}
    	return item;
    }
    
    public List<GoodsBrand> query_goods_brand() {
    	
    	 List<GoodsBrand> gbs = this.goodsBrandService
                 .query(
                     "select obj from GoodsBrand obj where audit=1 order by obj.first_word asc,obj.name asc",
                     null, -1, -1);
    	 return gbs;
    	 
    }
}
