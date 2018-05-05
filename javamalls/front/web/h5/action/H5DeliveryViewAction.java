package com.javamalls.front.web.h5.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.domain.Accessory;
import com.javamalls.platform.domain.DeliveryGoods;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.query.DeliveryGoodsQueryObject;
import com.javamalls.platform.service.IDeliveryGoodsService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

@Controller
public class H5DeliveryViewAction {

    @Autowired
    private ISysConfigService     configService;
    @Autowired
    private IUserConfigService    userConfigService;
    @Autowired
    private IDeliveryGoodsService deliveryGoodsService;
    @Autowired
    private IDeliveryGoodsService ideliveryGoodsService;

    @RequestMapping({ "/mobile/delivery.htm" })
    public ModelAndView delivery(HttpServletRequest request, HttpServletResponse response,
                                 String id, String currentPage, String orderBy, String orderType) {
        ModelAndView mv = new JModelAndView("h5/delivery.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        DeliveryGoodsQueryObject qo = new DeliveryGoodsQueryObject(currentPage, mv, orderBy,
            orderType);
        qo.addQuery("obj.d_status", new SysMap("d_status", Integer.valueOf(1)), "=");
        qo.addQuery("obj.d_begin_time", new SysMap("d_begin_time", new Date()), "<=");
        qo.addQuery("obj.d_end_time", new SysMap("d_end_time", new Date()), ">=");
        qo.setPageSize(Integer.valueOf(1));
        IPageList pList = this.deliveryGoodsService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        return mv;
    }

    //加载更多时走的方法
    @RequestMapping({ "/mobile/deliverymore.htm" })
    public void deliverymore(HttpServletRequest request, HttpServletResponse response, String id,
                             String currentPage, String orderBy, String orderType) {
        ModelAndView mv = new JModelAndView("", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        if (orderBy == null || orderBy.equals("")) {
            orderBy = "createtime";
        }
        if (orderType == null || orderType.equals("")) {
            orderType = "asc";
        }
        DeliveryGoodsQueryObject qo = new DeliveryGoodsQueryObject(currentPage, mv, orderBy,
            orderType);
        if (this.configService.getSysConfig().isIntegralStore()) {
            qo.setPageSize(Integer.valueOf(1));
            IPageList<DeliveryGoods> iPageList = ideliveryGoodsService.list(qo);
            List<DeliveryGoods> evalList = iPageList.getResult();
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

            String imgPath = null;
            String goods_url = null;
            Accessory accessory = this.configService.getSysConfig().getGoodsImage();
            String webPath = mv.getModelMap().get("webPath").toString();
            String imageWebServer = mv.getModelMap().get("imageWebServer").toString();
            Map<String, Object> map = null;
            Goods goods = null;

            for (DeliveryGoods evaluate : evalList) {
                goods = evaluate.getD_goods();
                imgPath = imageWebServer + File.separator + accessory.getPath() + File.separator
                          + accessory.getName();
                if (goods.getGoods_main_photo() != null) {
                    imgPath = imageWebServer + "/" + goods.getGoods_main_photo().getPath()
                              + File.separator + goods.getGoods_main_photo().getName() + "_middle."
                              + goods.getGoods_main_photo().getExt();
                }

                goods_url = webPath + "/mobile/goods_" + evaluate.getD_goods().getId() + ".htm";
                map = new HashMap<String, Object>();

                map.put("imgPath", imgPath);
                map.put("goods_url", goods_url);

                map.put("id", evaluate.getD_goods().getId());
                map.put("name", CommUtil.substring(evaluate.getD_goods().getGoods_name(), 30));
                map.put("price", evaluate.getD_goods().getGoods_price());
                map.put("currentprice", evaluate.getD_goods().getGoods_current_price());

                map.put("dname", evaluate.getD_delivery_goods().getGoods_name());
                map.put("dprice", evaluate.getD_delivery_goods().getGoods_price());

                map.put("createTime", CommUtil.formatShortDate(evaluate.getCreatetime()));
                map.put("currentPage", currentPage);
                list.add(map);
            }
            this.writeData(response, list);
        }
    }

    private void writeData(HttpServletResponse response, List<Map<String, Object>> list) {
        String temp = Json.toJson(list, JsonFormat.compact());
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
