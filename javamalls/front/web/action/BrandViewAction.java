package com.javamalls.front.web.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.front.web.tools.StoreViewTools;
import com.javamalls.platform.domain.GoodsBrand;
import com.javamalls.platform.domain.GoodsBrandCategory;
import com.javamalls.platform.domain.query.GoodsQueryObject;
import com.javamalls.platform.service.IGoodsBrandCategoryService;
import com.javamalls.platform.service.IGoodsBrandService;
import com.javamalls.platform.service.IGoodsClassService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

/**品牌
 *                       
 * @Filename: BrandViewAction.java
 * @Version: 2.7.0
 * @Author: 王阳
 * @Email: wangyang@163.com
 *
 */
@Controller
public class BrandViewAction {
    @Autowired
    private ISysConfigService          configService;
    @Autowired
    private IUserConfigService         userConfigService;
    @Autowired
    private IGoodsClassService         goodsClassService;
    @Autowired
    private IGoodsService              goodsService;
    @Autowired
    private IGoodsBrandService         goodsBrandService;
    @Autowired
    private IGoodsBrandCategoryService goodsBrandCategorySerivce;
    @Autowired
    private StoreViewTools             storeViewTools;

    @RequestMapping({ "/brand.htm" })
    public ModelAndView brand(HttpServletRequest request, HttpServletResponse response,
                              String gbc_id) {
        ModelAndView mv = new JModelAndView("brand.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        List<GoodsBrandCategory> gbcs = this.goodsBrandCategorySerivce.query(
            "select obj from GoodsBrandCategory obj  order by obj.createtime asc", null, -1, -1);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("recommend", Boolean.valueOf(true));
        params.put("audit", Integer.valueOf(1));
        List<GoodsBrand> gbs = this.goodsBrandService
            .query(
                "select obj from GoodsBrand obj where obj.recommend=:recommend and obj.audit=:audit order by obj.sequence asc",
                params, 0, 10);
        mv.addObject("gbs", gbs); //推荐品牌
        mv.addObject("gbcs", gbcs);//品牌分类
        List<GoodsBrand> brands = new ArrayList<GoodsBrand>();
        if ((gbc_id != null) && (!gbc_id.equals(""))) {
            mv.addObject("gbc_id", gbc_id);
            params.clear();
            params.put("gbc_id", CommUtil.null2Long(gbc_id));
            params.put("audit", Integer.valueOf(1));
            brands = this.goodsBrandService
                .query(
                    "select obj from GoodsBrand obj where obj.category.id=:gbc_id and obj.audit=:audit order by obj.sequence asc",
                    params, -1, -1);
        } else {
            params.clear();
            params.put("audit", Integer.valueOf(1));
            brands = this.goodsBrandService.query(
                "select obj from GoodsBrand obj where obj.audit=:audit order by obj.sequence asc",
                params, -1, -1);
        }
        List<Map<String, Object>> all_list = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> other_list = new ArrayList<Map<String, Object>>();
        String list_word = "A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z";
        String[] words = list_word.split(",");
        for (String word : words) {
            Map<String, Object> brand_map = new HashMap<String, Object>();
            List<GoodsBrand> brand_list = new ArrayList<GoodsBrand>();
            for (GoodsBrand gb : brands) {
                if ((!CommUtil.null2String(gb.getFirst_word()).equals(""))
                    && (word.equals(gb.getFirst_word().toUpperCase()))) {
                    brand_list.add(gb);
                }
            }
            brand_map.put("brand_list", brand_list);
            brand_map.put("word", word);
            all_list.add(brand_map);
        }

        for (String word : words) {
            Map<String, Object> othermap = new HashMap<String, Object>();
            List<GoodsBrand> brand_list = new ArrayList<GoodsBrand>();
            for (GoodsBrand gb : brands) {
                if (gb.getFirst_word().equals(word)) {
                    brand_list.add(gb);
                }
            }
            othermap.put("id", word);
            othermap.put("brand_list", brand_list);
            othermap.put("name", word);

            other_list.add(othermap);
        }

        mv.addObject("all_list", all_list); //内容
        mv.addObject("other_list", other_list); //内容

        return mv;
    }

    @RequestMapping({ "/brand_goods.htm" })
    public ModelAndView brand_view(HttpServletRequest request, HttpServletResponse response,
                                   String id, String currentPage, String orderBy, String orderType,
                                   String store_price_begin, String store_price_end, String op,
                                   String goods_name) {
        ModelAndView mv = new JModelAndView("brand_goods.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        if ((op != null) && (!op.equals(""))) {
            mv.addObject("op", op);
        }
        GoodsBrand gb = this.goodsBrandService.getObjById(CommUtil.null2Long(id));
        mv.addObject("gb", gb);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("recommend", Boolean.valueOf(true));
        params.put("audit", Integer.valueOf(1));
        List<GoodsBrand> gbs = this.goodsBrandService
            .query(
                "select obj from GoodsBrand obj where obj.recommend=:recommend and obj.audit=:audit order by obj.sequence asc",
                params, 0, 10);
        mv.addObject("gbs", gbs);
        mv.addObject("storeViewTools", this.storeViewTools);
        GoodsQueryObject gqo = new GoodsQueryObject(currentPage, mv, orderBy, orderType);
        if ((store_price_begin != null) && (!store_price_begin.equals(""))) {
            gqo.addQuery(
                "obj.store_price",
                new SysMap("store_price_begin", BigDecimal.valueOf(CommUtil
                    .null2Float(store_price_begin))), ">=");
            mv.addObject("store_price_begin", store_price_begin);
        }
        if ((store_price_end != null) && (!store_price_end.equals(""))) {
            gqo.addQuery(
                "obj.store_price",
                new SysMap("store_price_end", BigDecimal.valueOf(CommUtil
                    .null2Float(store_price_end))), "<=");
            mv.addObject("store_price_end", store_price_end);
        }
        if ((goods_name != null) && (!goods_name.equals(""))) {
            gqo.addQuery("obj.goods_name", new SysMap("goods_name", "%" + goods_name.trim() + "%"),
                "like");
            mv.addObject("goods_name", goods_name);
        }
        gqo.addQuery("obj.goods_brand.id", new SysMap("goods_brand_id", gb.getId()), "=");
        gqo.addQuery("obj.goods_status", new SysMap("goods_status", Integer.valueOf(0)), "=");
        gqo.setPageSize(Integer.valueOf(20));
        gqo.addQuery("obj.disabled", new SysMap("disabled", false), "=");

        IPageList pList = this.goodsService.list(gqo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        return mv;
    }
}
