package com.javamalls.front.web.tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.domain.Accessory;
import com.javamalls.platform.domain.Advert;
import com.javamalls.platform.domain.AdvertPosition;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.GoodsBrand;
import com.javamalls.platform.domain.GoodsClass;
import com.javamalls.platform.service.IAccessoryService;
import com.javamalls.platform.service.IAdvertPositionService;
import com.javamalls.platform.service.IAdvertService;
import com.javamalls.platform.service.IGoodsBrandService;
import com.javamalls.platform.service.IGoodsClassService;
import com.javamalls.platform.service.IGoodsFloorService;
import com.javamalls.platform.service.IGoodsService;

/**商品楼层
 *                       
 * @Filename: GoodsFloorViewTools.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Component
public class GoodsFloorViewTools {
    @Autowired
    private IGoodsFloorService     goodsFloorService;
    @Autowired
    private IGoodsService          goodsService;
    @Autowired
    private IGoodsClassService     goodsClassService;
    @Autowired
    private IAccessoryService      accessoryService;
    @Autowired
    private IAdvertPositionService advertPositionService;
    @Autowired
    private IAdvertService         advertService;
    @Autowired
    private IGoodsBrandService     goodsBrandService;

    public List<GoodsClass> generic_gf_gc(String json) {
        List<GoodsClass> gcs = new ArrayList();
        if ((json != null) && (!json.equals(""))) {
            List<Map> list = (List) Json.fromJson(List.class, json);
            for (Map map : list) {
                GoodsClass the_gc = this.goodsClassService.getObjById(CommUtil.null2Long(map
                    .get("pid")));
                if (the_gc != null) {
                    int count = CommUtil.null2Int(map.get("gc_count"));
                    GoodsClass gc = new GoodsClass();
                    gc.setId(the_gc.getId());
                    gc.setClassName(the_gc.getClassName());
                    for (int i = 1; i <= count; i++) {
                        GoodsClass child = this.goodsClassService.getObjById(CommUtil.null2Long(map
                            .get("gc_id" + i)));
                        gc.getChilds().add(child);
                    }
                    gcs.add(gc);
                }
            }
        }
        return gcs;
    }

    /**在floor.html页面中调用
     * @param json
     * @param count  
     * @return
     */
    public List<Goods> generic_goods(String json, int count) {
        List<Goods> goods_list = new ArrayList<Goods>();
        if ((json != null) && (!json.equals(""))) {
            Map map = (Map) Json.fromJson(Map.class, json);
            for (int i = 1; i <= count; i++) {
                String key = "goods_id" + i;

                Goods goods = this.goodsService.getObjById(CommUtil.null2Long(map.get(key)));
                // 过滤没上架的
                //				System.out.println(map.get(key)+"============"+goods.getGoods_status()+"  === "+i);
                if (goods != null && goods.getGoods_status() == 0) {
                    goods_list.add(goods);
                }
            }
        }
        return goods_list;
    }

    public Map generic_goods_list(String json) {
        Map map = new HashMap();
        map.put("list_title", "商品排行");
        if ((json != null) && (!json.equals(""))) {
            Map list = (Map) Json.fromJson(Map.class, json);
            map.put("list_title", CommUtil.null2String(list.get("list_title")));
            map.put("goods1",
                this.goodsService.getObjById(CommUtil.null2Long(list.get("goods_id1"))));
            map.put("goods2",
                this.goodsService.getObjById(CommUtil.null2Long(list.get("goods_id2"))));
            map.put("goods3",
                this.goodsService.getObjById(CommUtil.null2Long(list.get("goods_id3"))));
            map.put("goods4",
                this.goodsService.getObjById(CommUtil.null2Long(list.get("goods_id4"))));
            map.put("goods5",
                this.goodsService.getObjById(CommUtil.null2Long(list.get("goods_id5"))));
            map.put("goods6",
                this.goodsService.getObjById(CommUtil.null2Long(list.get("goods_id6"))));
        }
        return map;
    }

    public String generic_adv(String web_url, String json) {
        String template = "<div style='float:left;overflow:hidden;'>";
        if ((json != null) && (!json.equals(""))) {
            Map map = (Map) Json.fromJson(Map.class, json);
            if (CommUtil.null2String(map.get("adv_id")).equals("")) {
                Accessory img = this.accessoryService.getObjById(CommUtil.null2Long(map
                    .get("acc_id")));
                if (img != null) {
                    String url = CommUtil.null2String(map.get("acc_url"));
                    template = template + "<a href='" + url + "' target='_blank'><img src='"
                               + web_url + "/" + img.getPath() + "/" + img.getName() + "' /></a>";
                }
            } else {
                AdvertPosition ap = this.advertPositionService.getObjById(CommUtil.null2Long(map
                    .get("adv_id")));
                AdvertPosition obj = new AdvertPosition();
                obj.setAp_type(ap.getAp_type());
                obj.setAp_status(ap.getAp_status());
                obj.setAp_show_type(ap.getAp_show_type());
                obj.setAp_width(ap.getAp_width());
                obj.setAp_height(ap.getAp_height());
                List<Advert> advs = new ArrayList();
                for (Advert temp_adv : ap.getAdvs()) {
                    if ((temp_adv.getAd_status() == 1)
                        && (temp_adv.getAd_begin_time().before(new Date()))
                        && (temp_adv.getAd_end_time().after(new Date()))) {
                        advs.add(temp_adv);
                    }
                }
                if (advs.size() > 0) {
                    if (obj.getAp_type().equals("img")) {
                        if (obj.getAp_show_type() == 0) {
                            obj.setAp_acc(((Advert) advs.get(0)).getAd_acc());
                            obj.setAp_acc_url(((Advert) advs.get(0)).getAd_url());
                            obj.setAdv_id(CommUtil.null2String(((Advert) advs.get(0)).getId()));
                        }
                        if (obj.getAp_show_type() == 1) {
                            Random random = new Random();
                            int i = random.nextInt(advs.size());
                            obj.setAp_acc(((Advert) advs.get(i)).getAd_acc());
                            obj.setAp_acc_url(((Advert) advs.get(i)).getAd_url());
                            obj.setAdv_id(CommUtil.null2String(((Advert) advs.get(i)).getId()));
                        }
                    }
                } else {
                    obj.setAp_acc(ap.getAp_acc());
                    obj.setAp_text(ap.getAp_text());
                    obj.setAp_acc_url(ap.getAp_acc_url());
                    Advert adv = new Advert();
                    adv.setAd_url(obj.getAp_acc_url());
                    adv.setAd_acc(ap.getAp_acc());
                    obj.getAdvs().add(adv);
                }

                if (obj.getAp_acc() != null) {
                    template = template + "<a href='" + obj.getAp_acc_url()
                               + "' target='_blank'><img src='" + web_url + "/"
                               + obj.getAp_acc().getPath() + "/" + obj.getAp_acc().getName()
                               + "' /></a>";
                }
            }
        }
        template = template + "</div>";
        return template;
    }
    public String generic_adv_h(String web_url, String json,String json1) {
    	//<div class='left2' style='background:url($!webPath/resources/image/aa1.png) no-repeat;'>
    	
    	/*<div class="left2" style="background:url($!webPath/resources/image/aa1.png) no-repeat;">
    	
    </div> */
        String template = " ";
        if ((json != null) && (!json.equals(""))) {
            Map<String, Object> map = (Map<String, Object>) Json.fromJson(Map.class, json);
            if (CommUtil.null2String(map.get("adv_id")).equals("")) {
                Accessory img = this.accessoryService.getObjById(CommUtil.null2Long(map
                    .get("acc_id")));
                if (img != null) {
                    String url = CommUtil.null2String(map.get("acc_url"));
                    template = template +"<div class='left2' style='background:url("+ web_url + "/" + img.getPath() + "/" + img.getName() +") no-repeat;'>";
                     
                }
            } else {
                AdvertPosition ap = this.advertPositionService.getObjById(CommUtil.null2Long(map
                    .get("adv_id")));
                AdvertPosition obj = new AdvertPosition();
                obj.setAp_type(ap.getAp_type());
                obj.setAp_status(ap.getAp_status());
                obj.setAp_show_type(ap.getAp_show_type());
                obj.setAp_width(ap.getAp_width());
                obj.setAp_height(ap.getAp_height());
                List<Advert> advs = new ArrayList<Advert>();
                for (Advert temp_adv : ap.getAdvs()) {
                    if ((temp_adv.getAd_status() == 1)
                        && (temp_adv.getAd_begin_time().before(new Date()))
                        && (temp_adv.getAd_end_time().after(new Date()))) {
                        advs.add(temp_adv);
                    }
                }
                if (advs.size() > 0) {
                    if (obj.getAp_type().equals("img")) {
                        if (obj.getAp_show_type() == 0) {
                            obj.setAp_acc(((Advert) advs.get(0)).getAd_acc());
                            obj.setAp_acc_url(((Advert) advs.get(0)).getAd_url());
                            obj.setAdv_id(CommUtil.null2String(((Advert) advs.get(0)).getId()));
                        }
                        if (obj.getAp_show_type() == 1) {
                            Random random = new Random();
                            int i = random.nextInt(advs.size());
                            obj.setAp_acc(((Advert) advs.get(i)).getAd_acc());
                            obj.setAp_acc_url(((Advert) advs.get(i)).getAd_url());
                            obj.setAdv_id(CommUtil.null2String(((Advert) advs.get(i)).getId()));
                        }
                    }
                } else {
                    obj.setAp_acc(ap.getAp_acc());
                    obj.setAp_text(ap.getAp_text());
                    obj.setAp_acc_url(ap.getAp_acc_url());
                    Advert adv = new Advert();
                    adv.setAd_url(obj.getAp_acc_url());
                    adv.setAd_acc(ap.getAp_acc());
                    obj.getAdvs().add(adv);
                }
                if (obj.getAp_acc() != null) {
                    /*template = template + "<a href='" + obj.getAp_acc_url()
                               + "' target='_blank'><img src='" + web_url + "/"
                               + obj.getAp_acc().getPath() + "/" + obj.getAp_acc().getName()
                               + "' /></a>";*/
                	 template = template +" <div class='left2' style='background:url("+ web_url + "/" + obj.getAp_acc().getPath() + "/" + obj.getAp_acc().getName() +") no-repeat;'>";
                     
                }
            }
             
            template =template+ "<ul>";
            List<GoodsClass> gc = generic_gf_gc(json1);
            if(gc!=null){
            	for(GoodsClass g:gc){
            		template =template+"<li class='li3'>";
                	template = template+"<a href='"+web_url+"/store_goods_type_"+g.getId()+".htm' target='_blank'>"+g.getClassName()+"</a>";
                	template = template+"</li>";
            	}
            	
            }
            
            template = template + "</ul></div>";
        }
        //template = template + "</div>";
        return template;
    }
    
    public String generic_adv_v5(String web_url, String json,String json1) {
    	//<div class='left2' style='background:url($!webPath/resources/image/aa1.png) no-repeat;'>
    	
    	/*<div class="left2" style="background:url($!webPath/resources/image/aa1.png) no-repeat;">
    	
    </div> */
    	
    	/**
    	 * <ul style="left: -540px; width: 810px;">
								<!--   -->
								<li>
									<a href="list?cat_id=306" target="_blank" style="background: url(http://images.68dsw.com) no-repeat center center">
										<img src="http://images.68dsw.com/shop/0/2016/06/12/14656999876647.jpg" data-original="http://images.68dsw.com/shop/0/2016/06/12/14656999876647.jpg" width="270" height="475" style="display: inline;">
									</a>
								</li>
							</ul>
							<div class="focus-btn">
								<span style="opacity: 0.3;"> 
							</div>
    	 */
        String template = "<ul style='left: -540px; width: 810px;'>";
        if ((json != null) && (!json.equals(""))) {
            Map<String, Object> map = (Map<String, Object>) Json.fromJson(Map.class, json);
            if (CommUtil.null2String(map.get("adv_id")).equals("")) {
                Accessory img = this.accessoryService.getObjById(CommUtil.null2Long(map
                    .get("acc_id")));
                if (img != null) {
                    String url = CommUtil.null2String(map.get("acc_url"));
                    //template = template +"<div class='left2' style='background:url("+ web_url + "/" + img.getPath() + "/" + img.getName() +") no-repeat;'>";
                    if(url!=null&&!"".equals(url)){
                    	 template = template+"<li><a  target='_blank'   href='"+url+"'>"+
                                 " <img src='"+ web_url + "/" + img.getPath() + "/" + img.getName() +"' data-original='"+ web_url + "/" + img.getPath() + "/" + img.getName() +"' width='270' height='475' style='display: inline;'></a></li>";
             					
                    }else{
                    	 template = template+"<li><a style='cursor:initial'>"+
                                 " <img src='"+ web_url + "/" + img.getPath() + "/" + img.getName() +"' data-original='"+ web_url + "/" + img.getPath() + "/" + img.getName() +"' width='270' height='475' style='display: inline;'></a></li>";
             					
                    }
                    
                   		 
                }
            } else {
                AdvertPosition ap = this.advertPositionService.getObjById(CommUtil.null2Long(map
                    .get("adv_id")));
                AdvertPosition obj = new AdvertPosition();
                obj.setAp_type(ap.getAp_type());
                obj.setAp_status(ap.getAp_status());
                obj.setAp_show_type(ap.getAp_show_type());
                obj.setAp_width(ap.getAp_width());
                obj.setAp_height(ap.getAp_height());
                List<Advert> advs = new ArrayList<Advert>();
                for (Advert temp_adv : ap.getAdvs()) {
                    if ((temp_adv.getAd_status() == 1)
                        && (temp_adv.getAd_begin_time().before(new Date()))
                        && (temp_adv.getAd_end_time().after(new Date()))) {
                        advs.add(temp_adv);
                    }
                }
                if (advs.size() > 0) {
                    if (obj.getAp_type().equals("img")) {
                        if (obj.getAp_show_type() == 0) {
                            obj.setAp_acc(((Advert) advs.get(0)).getAd_acc());
                            obj.setAp_acc_url(((Advert) advs.get(0)).getAd_url());
                            obj.setAdv_id(CommUtil.null2String(((Advert) advs.get(0)).getId()));
                        }
                        if (obj.getAp_show_type() == 1) {
                            Random random = new Random();
                            int i = random.nextInt(advs.size());
                            obj.setAp_acc(((Advert) advs.get(i)).getAd_acc());
                            obj.setAp_acc_url(((Advert) advs.get(i)).getAd_url());
                            obj.setAdv_id(CommUtil.null2String(((Advert) advs.get(i)).getId()));
                        }
                    }
                } else {
                    obj.setAp_acc(ap.getAp_acc());
                    obj.setAp_text(ap.getAp_text());
                    obj.setAp_acc_url(ap.getAp_acc_url());
                    Advert adv = new Advert();
                    adv.setAd_url(obj.getAp_acc_url());
                    adv.setAd_acc(ap.getAp_acc());
                    obj.getAdvs().add(adv);
                }
                if (obj.getAp_acc() != null) {
                    /*template = template + "<a href='" + obj.getAp_acc_url()
                               + "' target='_blank'><img src='" + web_url + "/"
                               + obj.getAp_acc().getPath() + "/" + obj.getAp_acc().getName()
                               + "' /></a>";*/
                	// template = template +" <div class='left2' style='background:url("+ web_url + "/" + obj.getAp_acc().getPath() + "/" + obj.getAp_acc().getName() +") no-repeat;'>";
                	 template = template+"<li><a href='"+obj.getAp_acc_url()+"' target='_blank'  >"+
                             " <img src='"+ web_url + "/" + obj.getAp_acc().getPath() + "/" + obj.getAp_acc().getName() +"' data-original='"+ web_url + "/" + obj.getAp_acc().getPath() + "/" + obj.getAp_acc().getName() +"' width='270' height='475' style='display: inline;'></a></li>";
         					 
                }
            }
             
            /*template =template+ "<ul>";
            List<GoodsClass> gc = generic_gf_gc(json1);
            if(gc!=null){
            	template =template+"<div class='focus-btn'>";
            	for(GoodsClass g:gc){
            		
                	template = template+"<span style='opacity: 0.3;'> ";
                	
            	}
            	template = template+"</div>";
            	
            }
            template = template+"</ul>";*/
            
        }
        //template = template + "</div>";
        return template;
    }
    
    public List<GoodsBrand> generic_brand(String json) {
        List<GoodsBrand> brands = new ArrayList();
        if ((json != null) && (!json.equals(""))) {
            Map map = (Map) Json.fromJson(Map.class, json);
            for (int i = 1; i <= 11; i++) {
                String key = "brand_id" + i;
                GoodsBrand brand = this.goodsBrandService.getObjById(CommUtil.null2Long(map
                    .get(key)));
                if (brand != null) {
                    brands.add(brand);
                }
            }
        }
        return brands;
    }
}
