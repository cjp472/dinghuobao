package com.javamalls.ctrl.admin.tools;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.javamalls.platform.domain.Bargain;
import com.javamalls.platform.domain.BargainGoods;
import com.javamalls.platform.service.IBargainGoodsService;
import com.javamalls.platform.service.IBargainService;
import com.javamalls.platform.service.ISysConfigService;

@Component
public class BargainManageTools {
    @Autowired
    private IBargainGoodsService bargainGoodsService;
    @Autowired
    private IBargainService      bargainServicve;
    @Autowired
    private ISysConfigService    configService;

    public BigDecimal query_bargain_rebate(Object bargain_time) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("bt", bargain_time);
        List<Bargain> bargain = this.bargainServicve.query(
            "select obj from Bargain obj where obj.bargain_time =:bt", params, 0, 1);
        BigDecimal bd = null;
        if (bargain.size() > 0) {
            bd = ((Bargain) bargain.get(0)).getRebate();
        } else {
            bd = this.configService.getSysConfig().getBargain_rebate();
        }
        return bd;
    }

    public int query_bargain_maximum(Object bargain_time) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("bt", bargain_time);
        List<Bargain> bargain = this.bargainServicve.query(
            "select obj from Bargain obj where obj.bargain_time =:bt", params, 0, 1);
        int bd = 0;
        if (bargain.size() > 0) {
            bd = ((Bargain) bargain.get(0)).getMaximum();
        } else {
            bd = this.configService.getSysConfig().getBargain_maximum();
        }
        return bd;
    }

    public int query_bargain_audit(Object bargain_time) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("bt", bargain_time);
        List<BargainGoods> bargainGoods = this.bargainGoodsService.query(
            "select obj from BargainGoods obj where obj.bg_time =:bt", params, -1, -1);
        int bd = 0;
        for (BargainGoods bg : bargainGoods) {
            if (bg.getBg_status() == 1) {
                bd++;
            }
        }
        return bd;
    }

    public int query_bargain_apply(Object bargain_time) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("bt", bargain_time);
        List<BargainGoods> bargainGoods = this.bargainGoodsService.query(
            "select obj from BargainGoods obj where obj.bg_time =:bt", params, -1, -1);
        return bargainGoods.size();
    }
}
