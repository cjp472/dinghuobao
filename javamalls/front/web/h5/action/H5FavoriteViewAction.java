package com.javamalls.front.web.h5.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.domain.Favorite;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.service.IFavoriteService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

/**收藏
 *                       
 * @Filename: FavoriteViewAction.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class H5FavoriteViewAction {

    @Autowired
    private ISysConfigService  configService;
    @Autowired
    private IUserConfigService userConfigService;
    @Autowired
    private IFavoriteService   favoriteService;
    @Autowired
    private IGoodsService      goodsService;
    @Autowired
    private IStoreService      storeService;

    @RequestMapping({ "/store/{storeId}.htm/mobile/add_goods_favorite.htm" })
    public void add_goods_favorite(HttpServletResponse response, String id) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
        params.put("goods_id", CommUtil.null2Long(id));
        List<Favorite> list = this.favoriteService.query(
            "select obj from Favorite obj where obj.user.id=:user_id and obj.goods.id=:goods_id",
            params, -1, -1);
        int ret = 0;
        if (list.size() == 0) {
            Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
            Favorite obj = new Favorite();
            obj.setCreatetime(new Date());
            obj.setType(0);
            obj.setUser(SecurityUserHolder.getCurrentUser());
            obj.setGoods(goods);
            this.favoriteService.save(obj);
            goods.setGoods_collect(goods.getGoods_collect() + 1);
            this.goodsService.update(goods);
        } else {
            ret = 1;
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

    @RequestMapping({ "/store/{storeId}.htm/mobile/add_store_favorite.htm" })
    public void add_store_favorite(HttpServletResponse response, String id) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
        params.put("store_id", CommUtil.null2Long(id));
        List<Favorite> list = this.favoriteService.query(
            "select obj from Favorite obj where obj.user.id=:user_id and obj.store.id=:store_id",
            params, -1, -1);
        int ret = 0;
        if (list.size() == 0) {
            Favorite obj = new Favorite();
            obj.setCreatetime(new Date());
            obj.setType(1);
            obj.setUser(SecurityUserHolder.getCurrentUser());
            obj.setStore(this.storeService.getObjById(CommUtil.null2Long(id)));
            this.favoriteService.save(obj);
            Store store = obj.getStore();
            store.setFavorite_count(store.getFavorite_count() + 1);
            this.storeService.update(store);
        } else {
            ret = 1;
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
}
