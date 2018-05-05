package com.javamalls.ctrl.seller.action;

 

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.math.BigDecimal;
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
import com.javamalls.base.tools.WebForm;
import com.javamalls.ctrl.admin.tools.StoreTools;
import com.javamalls.front.web.tools.GoodsViewTools;
import com.javamalls.front.web.tools.StoreViewTools;
import com.javamalls.platform.domain.Accessory;
import com.javamalls.platform.domain.GoodsItem;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.StoreNavigation;
import com.javamalls.platform.domain.Strategy;
import com.javamalls.platform.domain.StrategyGoodsItem;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.UserGoodsClass;
import com.javamalls.platform.domain.query.GoodsItemQueryObject;
import com.javamalls.platform.domain.query.GoodsQueryObject;
import com.javamalls.platform.domain.query.StrategyQueryObject;
import com.javamalls.platform.service.IGoodsItemService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.IStrategyGoodsItemService;
import com.javamalls.platform.service.IStrategyService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserGoodsClassService;
import com.javamalls.platform.service.IUserService;

/**价格策略货品管理
 *                       
 * @Filename: StrategyGoodsItemSellerAction.java
 * @Version:
 * @Author: cjl
 *
 */
@Controller
public class StrategyGoodsItemSellerAction {
    @Autowired
    private ISysConfigService      configService;
    @Autowired
    private IUserConfigService     userConfigService;
    @Autowired
    private IStrategyService       strategyService;
    @Autowired
    private IStoreService		   storeService;
    @Autowired
    private IUserService		   userService;
    @Autowired
    private IUserGoodsClassService userGoodsClassService;
    @Autowired
    private IGoodsService		   goodsService;
    @Autowired
    private IGoodsItemService	   goodsItemService;
    @Autowired
    private StoreTools             storeTools;
    @Autowired
    private GoodsViewTools         goodsViewTools;
    @Autowired
    private IStrategyGoodsItemService strategyGoodsItemService;
  
    @SecurityMapping(title = "价格策略商品设置", value = "/seller/strategy_goods_item.htm*", rtype = "seller", rname = "价格策略商品", rcode = "strategy_goods_item", rgroup = "店铺管理")
    @RequestMapping({ "/seller/strategy_goods_item.htm" })
    public ModelAndView strategy_goods_item(HttpServletRequest request, HttpServletResponse response,
                              String currentPage, String orderBy, String orderType,
                              String goods_name, String user_class_id,String strategy_id) {
        ModelAndView mv = new JModelAndView("seller/strategy_goods_item.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
                JModelAndView.SHOP_PATH, request, response);
        //根据id查询策略
        Strategy strategy = this.strategyService.getObjById(CommUtil.null2Long(strategy_id));
        mv.addObject("strategy", strategy);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        List<StrategyGoodsItem> strategyGoodsItems = strategy.getStrategyGoodsItems();
        HashMap<Long,StrategyGoodsItem> strategyGoodsItems_map = new HashMap<Long,StrategyGoodsItem>();
        if(strategyGoodsItems!=null&&strategyGoodsItems.size()>0){
        	for(StrategyGoodsItem sgi:strategyGoodsItems){
        		strategyGoodsItems_map.put(sgi.getGoods_item().getId(), sgi);
        	}
        }
        //价格策略对应的商品列表 map集合
        mv.addObject("strategyGoodsItems_map", strategyGoodsItems_map);
        
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
        String params = "";
        GoodsItemQueryObject qo  = new GoodsItemQueryObject(currentPage, mv, orderBy, orderType);
        qo.addQuery("obj.goods.goods_status", new SysMap("goods_status", Integer.valueOf(0)), "=");
        qo.addQuery("obj.goods.goods_store.id", new SysMap("goods_store_id", user.getStore().getId()),
            "=");
        qo.setOrderBy("createtime");
        qo.setOrderType("desc");
        if ((goods_name != null) && (!goods_name.equals(""))) {
            qo.addQuery("obj.goods.goods_name", new SysMap("goods_name", "%" + goods_name + "%"), "like");
            mv.addObject("goods_name", goods_name);
            params+="&goods_name="+goods_name;
        }
      /* if ((user_class_id != null) && (!user_class_id.equals(""))) {
            UserGoodsClass ugc = this.userGoodsClassService.getObjById(Long.valueOf(Long
                .parseLong(user_class_id)));
            qo.addQuery("ugc", ugc, "obj.goods.goods_ugcs", "member of");
            
            mv.addObject("myuser_class_id", user_class_id);
            
            params+="&user_class_id="+user_class_id;
        }*/
        qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");

        IPageList pList = this.goodsItemService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/seller/strategy_goods_item.htm", "", params, pList, mv);
        mv.addObject("storeTools", this.storeTools);
        mv.addObject("goodsViewTools", this.goodsViewTools);
        return mv;
    }
    /**
     * 价格策略商品设置商品价格 保存
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/seller/strategy_goods_item_save.htm" })
    public void strategy_goods_item_save(HttpServletRequest request, HttpServletResponse response,String price,String strategy_id,String goods_item_id) {
        Map<String,Object> paramMap = new HashMap<String,Object>();
        String flag = "false";
        PrintWriter out = null;
    	try{
    		 out = response.getWriter();
    		 paramMap.put("strategy_id", CommUtil.null2Long(strategy_id));
    	     paramMap.put("goods_item_id", CommUtil.null2Long(goods_item_id));
    	     BigDecimal b_price = new BigDecimal(price);
    	     List<StrategyGoodsItem> list = this.strategyGoodsItemService.query(
    	    			"select obj from StrategyGoodsItem obj where obj.strategy.id =:strategy_id and obj.goods_item.id =:goods_item_id", paramMap, -1, -1);
    	     if(list!=null&&list.size()>0){
    	    	 StrategyGoodsItem obj = list.get(0);
    	    	 obj.setPrice(b_price);
    	    	 this.strategyGoodsItemService.update(obj);
    	     }else{
    	    	 Strategy strategy = this.strategyService.getObjById(CommUtil.null2Long(strategy_id));
    	    	 GoodsItem goodsItem = this.goodsItemService.getObjById(CommUtil.null2Long(goods_item_id));
    	    	 StrategyGoodsItem obj = new StrategyGoodsItem();
    	    	 obj.setCreatetime(new Date());
    	    	 obj.setDisabled(false);
    	    	 obj.setPrice(b_price);
    	    	 obj.setGoods_item(goodsItem);
    	    	 obj.setGoods(goodsItem.getGoods());
    	    	 obj.setStrategy(strategy);
    	    	 this.strategyGoodsItemService.save(obj);
    	     }
    	    flag = "true";
    	    out.print(flag);
    	    out.close();
    	} catch (Exception e) {
           // e.printStackTrace();
            flag = "false";
            out.print(flag);
            out.close();
        }
        
        
    }
    /**
     * 价格策略商品设置商品价格 清楚
     * @param request
     * @param response
     * @param strategy_id
     * @param goods_item_id
     */
    @RequestMapping({ "/seller/strategy_goods_item_clear.htm" })
    public void strategy_goods_item_clear(HttpServletRequest request, HttpServletResponse response,String strategy_id,String goods_item_id) {
        Map<String,Object> paramMap = new HashMap<String,Object>();
        String flag = "false";
        PrintWriter out = null;
    	try{
    		 out = response.getWriter();
    		 paramMap.put("strategy_id", CommUtil.null2Long(strategy_id));
    	     paramMap.put("goods_item_id", CommUtil.null2Long(goods_item_id));
    	     List<StrategyGoodsItem> list = this.strategyGoodsItemService.query(
    	    			"select obj from StrategyGoodsItem obj where obj.strategy.id =:strategy_id and obj.goods_item.id =:goods_item_id", paramMap, -1, -1);
    	    if(list!=null&&list.size()>0){
    	    	List<Serializable> listids = new ArrayList<Serializable>();
    	    	for(StrategyGoodsItem sgi:list){
    	    		listids.add(sgi.getId());
    	    	}
    	    	this.strategyGoodsItemService.batchDelete(listids);
    	    }
    	     flag = "true";
    	    out.print(flag);
    	    out.close();
    	} catch (Exception e) {
           // e.printStackTrace();
            flag = "false";
            out.print(flag);
            out.close();
        }
    }
    
}
