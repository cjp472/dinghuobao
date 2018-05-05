package com.javamalls.front.web.action;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.GoodsClass;
import com.javamalls.platform.domain.query.GoodsQueryObject;
import com.javamalls.platform.service.IAreaService;
import com.javamalls.platform.service.IGoodsBrandService;
import com.javamalls.platform.service.IGoodsClassService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IGoodsSpecPropertyService;
import com.javamalls.platform.service.IGoodsTypePropertyService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

@Controller
public class InsuranceViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGoodsBrandService brandService;
	@Autowired
	private IGoodsSpecPropertyService goodsSpecPropertyService;
	@Autowired
	private IGoodsTypePropertyService goodsTypePropertyService;
	@Autowired
	private IAreaService areaService;

	@RequestMapping({ "/insurance.htm" })
	public ModelAndView store_goods_list(HttpServletRequest request,
			HttpServletResponse response,String iid) throws Exception{
		ModelAndView mv = new JModelAndView("insurance.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}
	
	
	@RequestMapping({ "/insurance_list.htm" })
	public ModelAndView insurance_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage) throws Exception{
		ModelAndView mv = new JModelAndView("insuranceli.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
	
		/**
		 * 保险分类
		 */
		long id = 1; 
		GoodsClass gc = this.goodsClassService.getObjById(id);
		mv.addObject("gc", gc);
		
		Map<Long,List<Goods>> gcmap = new HashMap<Long,List<Goods>>();
		
		for(GoodsClass item : gc.getChilds()  ){
			Long gc_id = item.getId();
			GoodsClass gcitem = this.goodsClassService.getObjById(gc_id);
			Set<Long> ids = genericIds(gcitem);

			Map param = new HashMap();
			param.put("gcid", ids);
			param.put("store_status", Integer.valueOf(2));
			param.put("goods_status", Integer.valueOf(0));
			List<Goods> objs = this.goodsService.query("select obj from Goods obj where obj.disabled=0 and " +
			 		" obj.gc.id in (:gcid) and obj.goods_store.store_status=:store_status and obj.goods_status=:goods_status ", 
			 		param, 0, 8);
			gcmap.put(gc_id, objs);
		}
		mv.addObject("gcmap", gcmap);
		return mv;
	}
	

	private Set<Long> genericIds(GoodsClass gc) {
		Set<Long> ids = new HashSet();
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


}
