package com.javamalls.ctrl.seller.action;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.ctrl.admin.tools.StoreTools;
import com.javamalls.front.web.tools.GoodsViewTools;
import com.javamalls.platform.domain.GoodsItem;
import com.javamalls.platform.domain.GoodsStepPrice;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.query.GoodsItemQueryObject;
import com.javamalls.platform.service.IGoodsItemService;
import com.javamalls.platform.service.IGoodsStepPriceService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;

@Controller
public class GoodsStepPriceSellerAction {
    @Autowired
    private ISysConfigService         configService;
    @Autowired
    private IUserConfigService        userConfigService;
    @Autowired
    private IUserService              userService;
    @Autowired
    private StoreTools                storeTools;
    @Autowired
    private GoodsViewTools            goodsViewTools;
    @Autowired
    private IGoodsItemService goodsItemService;
    @Autowired
    private IGoodsStepPriceService goodsStepPriceService;



    @RequestMapping({ "/seller/goods_step_price.htm" })
    public ModelAndView goods_storage(HttpServletRequest request, HttpServletResponse response,
                                      String currentPage, String orderBy, String orderType,
                                      String goods_name, String user_class_id) {
    	   ModelAndView mv = new JModelAndView("seller/step_goods_item.html",
                   this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
                   JModelAndView.SHOP_PATH, request, response);
        if(SecurityUserHolder.getCurrentUser()==null){
        	mv = new JModelAndView("login.html", this.configService.getSysConfig(),
                    this.userConfigService.getUserConfig(), JModelAndView.SHOP_PATH, request, response);
        	return mv;
        }
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        String params = "";
        User user = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
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
        qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");

        IPageList pList = this.goodsItemService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/seller/goods_step_price.htm", "", params, pList, mv);
        mv.addObject("storeTools", this.storeTools);
        mv.addObject("goodsViewTools", this.goodsViewTools);
        mv.addObject("goods_name", goods_name);
        return mv;
    }
    
    /**
     * 货品价格区间设置
     * @param request
     * @param response
     * @param currentPage
     * @param orderBy
     * @param orderType
     * @return
     */
    @RequestMapping({ "/seller/goods_step_price_add.htm" })
    public ModelAndView goods_step_price_add(HttpServletRequest request, HttpServletResponse response,
                                  String goodsItemId) {
    	ModelAndView mv = new JModelAndView("seller/step_goods_item_add.html",
                 this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
                 JModelAndView.SHOP_PATH, request, response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        
       
      
        Map<String, Object> map=new HashMap<String, Object>();
        map.put("goods_item_id",CommUtil.null2Int(goodsItemId));
        String sql="select obj from GoodsStepPrice obj where obj.disabled=false and obj.goods_item_id=:goods_item_id";
       
        List<GoodsStepPrice> list = this.goodsStepPriceService.query(sql, map, -1, -1);
        mv.addObject("goodsItemId", goodsItemId);
        mv.addObject("stepList", list);
        return mv;
    }
    
    @RequestMapping({ "/seller/goods_step_price_save.htm" })
    public void goods_step_price_save(HttpServletRequest request, HttpServletResponse response,
                                  GoodsStepPrice goodsStepPrice) {
    
    	 if(SecurityUserHolder.getCurrentUser()!=null){
    		 if(goodsStepPrice.getId()==null||"".equals(goodsStepPrice.getId())||goodsStepPrice.getId()==0){
    			 	goodsStepPrice.setCreatetime(new Date());
    			 	goodsStepPrice.setDisabled(false);
    			 	GoodsItem item = this.goodsItemService.getObjById(CommUtil.null2Long(goodsStepPrice.getGoods_item_id()));
    	        	item.setStep_price_state(1);//是否设置阶梯报价：0未设置，1已设置
    			 	this.goodsStepPriceService.save(goodsStepPrice);
    			 	this.goodsItemService.update(item);
    	      }else{
    	        	this.goodsStepPriceService.update(goodsStepPrice);
    	      }
         }
    	   Map<String, Object> maps = new HashMap<String, Object>();
    	   maps.put("stepId", goodsStepPrice.getId());
           response.setContentType("text/plain");
           response.setHeader("Cache-Control", "no-cache");
           response.setCharacterEncoding("UTF-8");
           try {
               PrintWriter writer = response.getWriter();
               writer.print(Json.toJson(maps, JsonFormat.compact()));
           } catch (IOException e) {
               e.printStackTrace();
           }
        
    }

    @RequestMapping({ "/seller/goods_step_price_remove.htm" })
    public void goods_step_price_remove(HttpServletRequest request, HttpServletResponse response,
                                  String id,String goods_item_id) {
    	
    	boolean flag=true;
    	 if(SecurityUserHolder.getCurrentUser()!=null){
    		 flag = this.goodsStepPriceService.delete(CommUtil.null2Long(id));
    		 if(flag){
    		      Map<String, Object> map=new HashMap<String, Object>();
    		        map.put("goods_item_id",CommUtil.null2Int(goods_item_id));
    		        String sql="select obj from GoodsStepPrice obj where obj.disabled=false and obj.goods_item_id=:goods_item_id";
    		        List<GoodsStepPrice> list = this.goodsStepPriceService.query(sql, map, -1, -1);
    		        if(list==null||list.size()==0){
    		        	GoodsItem item = this.goodsItemService.getObjById(CommUtil.null2Long(goods_item_id));
    		        	item.setStep_price_state(0);
    		        	flag = this.goodsItemService.update(item);
    		        }
    		 }
         }
    	   Map<String, Object> maps = new HashMap<String, Object>();
    	   maps.put("stepId",flag);
           response.setContentType("text/plain");
           response.setHeader("Cache-Control", "no-cache");
           response.setCharacterEncoding("UTF-8");
           try {
               PrintWriter writer = response.getWriter();
               writer.print(Json.toJson(maps, JsonFormat.compact()));
           } catch (IOException e) {
               e.printStackTrace();
           }
        
    }
}
