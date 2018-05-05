package com.javamalls.ctrl.timer;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.javamalls.base.tools.CommUtil;
import com.javamalls.lucene.LuceneThread;
import com.javamalls.lucene.LuceneVo;
import com.javamalls.platform.domain.BargainGoods;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.GoodsCart;
import com.javamalls.platform.domain.Message;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.StoreCart;
import com.javamalls.platform.domain.SysConfig;
import com.javamalls.platform.domain.Template;
import com.javamalls.platform.domain.ZTCGoldLog;
import com.javamalls.platform.service.IBargainGoodsService;
import com.javamalls.platform.service.IGoodsCartService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IMessageService;
import com.javamalls.platform.service.IStoreCartService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.ITemplateService;
import com.javamalls.platform.service.IUserService;
import com.javamalls.platform.service.IZTCGoldLogService;

/**店铺相关定时任务
 *                       
 * @Filename: JobManageAction.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Component("shop_job")
public class JobManageAction {
    @Autowired
    private IGoodsService        goodsService;
    @Autowired
    private IZTCGoldLogService   ztcGoldLogService;
    @Autowired
    private IStoreService        storeService;
    @Autowired
    private ITemplateService     templateService;
    @Autowired
    private IMessageService      messageService;
    @Autowired
    private IUserService         userService;
    @Autowired
    private ISysConfigService    configService;
    @Autowired
    private IBargainGoodsService bargainGoodsService;
    @Autowired
    private IStoreCartService    storeCartService;
    @Autowired
    private IGoodsCartService    goodsCartService;

    public void execute() {
        //竞价直通车购买记录
        Map<String, Object> params = new HashMap<String, Object>();
        /*params.put("ztc_status", Integer.valueOf(2));
        List<Goods> goods_audit_list = this.goodsService.query(
            "select obj from Goods obj where obj.disabled=0 and obj.ztc_status=:ztc_status",
            params, -1, -1);
        for (Goods goods : goods_audit_list) {
            if (goods.getZtc_begin_time().before(new Date())) {
                goods.setZtc_dredge_price(goods.getZtc_price());
                goods.setZtc_status(3);
                this.goodsService.update(goods);
            }
        }
        params.clear();
        params.put("ztc_status", Integer.valueOf(3));
        goods_audit_list = this.goodsService.query(
            "select obj from Goods obj where obj.disabled=0 and obj.ztc_status=:ztc_status",
            params, -1, -1);
        ZTCGoldLog log;
        for (Goods goods : goods_audit_list) {
            if (goods.getZtc_gold() > goods.getZtc_price()) {
                goods.setZtc_gold(goods.getZtc_gold() - goods.getZtc_price());
                goods.setZtc_dredge_price(goods.getZtc_price());
                this.goodsService.update(goods);
                log = new ZTCGoldLog();
                log.setCreatetime(new Date());
                log.setZgl_content("竞价直通车消耗金币");
                log.setZgl_gold(goods.getZtc_price());
                log.setZgl_goods(goods);
                log.setZgl_type(1);
                this.ztcGoldLogService.save(log);
            } else {
                goods.setZtc_status(0);
                goods.setZtc_dredge_price(0);
                goods.setZtc_pay_status(0);
                goods.setZtc_apply_time(null);
                this.goodsService.update(goods);
            }
        }*/

        //到期开通
        List<Store> stores2 = this.storeService.query(
                "select obj from Store obj where obj.store_status=5 and obj.validitybegin is not null", null, -1, -1);
            Message msg2;
            for (Store store : stores2) {
                if (store.getValiditybegin().before(new Date())) {
                    store.setStore_status(2);//延期开通
                    this.storeService.update(store);
                    Template template = this.templateService.getObjByProperty("mark",
                        "msg_toseller_store_auto_opened_notify");
                    if ((template != null) && (template.isOpen())) {
                        msg2 = new Message();
                        msg2.setCreatetime(new Date());
                        msg2.setContent(template.getContent());
                        msg2.setFromUser(this.userService.getObjByProperty("userName", "admin"));
                        msg2.setStatus(0);
                        msg2.setTitle(template.getTitle());
                        msg2.setToUser(store.getUser());
                        msg2.setType(0);
                        this.messageService.save(msg2);
                    }
                }
            }
        
        //关闭已经到期的店铺，并发送消息
        List<Store> stores = this.storeService.query(
            "select obj from Store obj where obj.store_status==2 and obj.validity is not null", null, -1, -1);
        Message msg;
        for (Store store : stores) {
            if (store.getValidity().before(new Date())) {
                store.setStore_status(3);//到期关闭
                this.storeService.update(store);
                Template template = this.templateService.getObjByProperty("mark",
                    "msg_toseller_store_auto_closed_notify");
                if ((template != null) && (template.isOpen())) {
                    msg = new Message();
                    msg.setCreatetime(new Date());
                    msg.setContent(template.getContent());
                    msg.setFromUser(this.userService.getObjByProperty("userName", "admin"));
                    msg.setStatus(0);
                    msg.setTitle(template.getTitle());
                    msg.setToUser(store.getUser());
                    msg.setType(0);
                    this.messageService.save(msg);
                }
            }
        }

        //更新热门搜索 lucene
        params.clear();
        params.put("goods_status", Integer.valueOf(0));
        List<Goods> goods_list = this.goodsService.query(
            "select obj from Goods obj where obj.goods_status=:goods_status", params, -1, -1);
        List<LuceneVo> goods_vo_list = new ArrayList<LuceneVo>();
        for (Goods goods : goods_list) {
            LuceneVo vo = new LuceneVo();
            vo.setVo_id(goods.getId());
            vo.setVo_title(goods.getGoods_name());
            vo.setVo_content(goods.getGoods_details());
            vo.setVo_type("goods");
            vo.setVo_store_price(CommUtil.null2Double(goods.getStore_price()));
            vo.setVo_add_time(goods.getCreatetime().getTime());
            vo.setVo_goods_salenum(goods.getGoods_salenum());
            goods_vo_list.add(vo);
        }
        String goods_lucene_path = System.getProperty("user.dir") + File.separator + "luence"
                                   + File.separator + "goods";
        File file = new File(goods_lucene_path);
        if (!file.exists()) {
            CommUtil.createFolder(goods_lucene_path);
        }
        LuceneThread goods_thread = new LuceneThread(goods_lucene_path, goods_vo_list);
        goods_thread.run();
        SysConfig config = this.configService.getSysConfig();
        config.setLucene_update(new Date());
        this.configService.update(config);

        //修改折扣（特价）商品的状态
        params.clear();
        Calendar cal = Calendar.getInstance();
        /*   cal.add(6, -1);
        params.put("bg_time", CommUtil.formatDate(CommUtil.formatShortDate(cal.getTime())));
        List<BargainGoods> bgs = this.bargainGoodsService.query(
            "select obj from BargainGoods obj where obj.bg_time=:bg_time", params, -1, -1);
        for (BargainGoods bg : bgs) {
            bg.setBg_status(-2);
            this.bargainGoodsService.update(bg);
            Goods goods = bg.getBg_goods();
            goods.setBargain_status(0);
            goods.setGoods_current_price(goods.getStore_price());
            this.goodsService.update(goods);
        }
        params.clear();
        params.put("bg_time", CommUtil.formatDate(CommUtil.formatShortDate(new Date())));
        params.put("bg_status", Integer.valueOf(1));
        bgs = this.bargainGoodsService
            .query(
                "select obj from BargainGoods obj where obj.bg_time=:bg_time and obj.bg_status=:bg_status",
                params, -1, -1);
        Goods goods;
        for (BargainGoods bg : bgs) {
            goods = bg.getBg_goods();
            goods.setBargain_status(2);
            goods.setGoods_current_price(bg.getBg_price());
            this.goodsService.update(goods);
        }*/

        //删除过期的购物车
        params.clear();
        cal = Calendar.getInstance();
        cal.add(6, -1); //一天之前
        params.put("createtime", cal.getTime());
        params.put("sc_status", Integer.valueOf(0));
        List<StoreCart> cart_list = this.storeCartService
            .query(
                "select obj from StoreCart obj where obj.user.id is null and obj.createtime<=:createtime and obj.sc_status=:sc_status",
                params, -1, -1);
        for (StoreCart cart : cart_list) {
            for (GoodsCart gc : cart.getGcs()) {
                gc.getGsps().clear();
                this.goodsCartService.delete(gc.getId());
            }
            this.storeCartService.delete(cart.getId());
        }

        params.clear();
        cal = Calendar.getInstance();
        cal.add(6, -7); //7天之前
        params.put("createtime", cal.getTime());
        params.put("sc_status", Integer.valueOf(0));
        cart_list = this.storeCartService
            .query(
                "select obj from StoreCart obj where obj.user.id is not null and obj.createtime<=:createtime and obj.sc_status=:sc_status",
                params, -1, -1);
        for (StoreCart cart : cart_list) {
            for (GoodsCart gc : cart.getGcs()) {
                gc.getGsps().clear();
                this.goodsCartService.delete(gc.getId());
            }
            this.storeCartService.delete(cart.getId());
        }
    }
}
