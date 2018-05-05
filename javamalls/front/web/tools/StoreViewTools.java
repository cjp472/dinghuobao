package com.javamalls.front.web.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.domain.Evaluate;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.StoreClass;
import com.javamalls.platform.domain.StoreGrade;
import com.javamalls.platform.domain.Strategy;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.service.IEvaluateService;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IStoreClassService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserService;

/**店铺
 *                       
 * @Filename: StoreViewTools.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Component
public class StoreViewTools {
    @Autowired
    private IStoreService      storeService;
    @Autowired
    private ISysConfigService  configService;
    @Autowired
    private IUserService       userService;
    @Autowired
    private IEvaluateService   evaluateService;
    @Autowired
    private IGoodsService      goodsService;
    @Autowired
    private IStoreClassService storeClassService;

    
    public String genericFunction(StoreGrade grade) {
        String fun = "";
        if (grade.getAdd_funciton().equals("")) {
            fun = "无";
        }
        String[] list = grade.getAdd_funciton().split(",");
        for (String s : list) {
            if (s.equals("editor_multimedia")) {
                fun = "富文本编辑器" + fun;
            }
        }
        return fun;
    }

    public String genericImageSuffix(String imageSuffix) {
        String suffix = "";
        String[] list = imageSuffix.split("\\|");
        for (String l : list) {
            suffix = "*." + l + ";" + suffix;
        }
        return suffix.substring(0, suffix.length() - 1);
    }

    /**根据店铺id获取店铺等级
     * @param id
     * @return
     */
    public int generic_store_credit(String id) {
        int credit = 0;
        String sys_credit = this.configService.getSysConfig().getCreditrule();
        Map map = (Map) Json.fromJson(HashMap.class, sys_credit);
        List<Integer> list = new ArrayList();
        for (Iterator it = map.keySet().iterator(); it.hasNext();) {
            String key = (String) it.next();
            list.add(Integer.valueOf(Integer.parseInt(map.get(key).toString())));
        }
        Integer[] ints = (Integer[]) list.toArray(new Integer[list.size()]);
        Arrays.sort(ints, new Comparator() {
            public int compare(Object obj1, Object obj2) {
                int a = CommUtil.null2Int(obj1);
                int b = CommUtil.null2Int(obj2);
                if (a == b) {
                    return 0;
                }
                return a > b ? 1 : -1;
            }
        });
        Store store = this.storeService.getObjById(Long.valueOf(Long.parseLong(id)));
        for (int i = 0; i < ints.length - 1; i++) {
            if ((ints[i].intValue() <= store.getStore_credit())
                && (ints[(i + 1)].intValue() >= store.getStore_credit())) {
                credit = i + 1;
                break;
            }
        }
        if (store.getStore_credit() >= ints[(ints.length - 1)].intValue()) {
            credit = ints.length;
        }
        return credit;
    }

    /**根据用户id获取用户等级
     * @param id
     * @return
     */
    public int generic_user_credit(String id) {
        int credit = 0;
        String user_credit = this.configService.getSysConfig().getUser_creditrule();
        Map map = (Map) Json.fromJson(HashMap.class, user_credit);
        List<Integer> list = new ArrayList<Integer>();
        for (Iterator it = map.keySet().iterator(); it.hasNext();) {
            String key = (String) it.next();
            list.add(Integer.valueOf(Integer.parseInt(map.get(key).toString())));
        }
        Integer[] ints = (Integer[]) list.toArray(new Integer[list.size()]);
        Arrays.sort(ints, new Comparator() {
            public int compare(Object obj1, Object obj2) {
                int a = CommUtil.null2Int(obj1);
                int b = CommUtil.null2Int(obj2);
                if (a == b) {
                    return 0;
                }
                return a > b ? 1 : -1;
            }
        });
        User user = this.userService.getObjById(CommUtil.null2Long(id));
        for (int i = 0; i < ints.length - 1; i++) {
            if ((ints[i].intValue() <= user.getUser_credit())
                && (ints[(i + 1)].intValue() >= user.getUser_credit())) {
                credit = i + 1;
                break;
            }
        }
        if (user.getUser_credit() >= ints[(ints.length - 1)].intValue()) {
            credit = ints.length;
        }
        return credit;
    }

    public List<Store> query_recommend_store(int count) {
        List<Store> list = new ArrayList<Store>();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("recommend", Boolean.valueOf(true));
        list = this.storeService
            .query(
                "select obj from Store obj where obj.store_recommend=:recommend order by obj.store_recommend_time desc",
                params, 0, count);
        return list;
    }

    public List<Goods> query_recommend_store_goods(Store store, int begin, int max) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("recommend", Boolean.valueOf(true));
        params.put("store_id", store.getId());
        params.put("goods_status", Integer.valueOf(0));
        List<Goods> goods = this.goodsService
            .query(
                "select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_recommend=:recommend and obj.goods_status=:goods_status",
                params, begin, max);
        if (goods.size() < 5) {
            int count = 5 - goods.size();
            for (int i = 0; i < count; i++) {
                goods.add(null);
            }
        }
        return goods;
    }

    public int query_evaluate(String store_id, int evaluate_val, String type, String date_symbol,
                              int date_count) {
        Calendar cal = Calendar.getInstance();
        if (type.equals("date")) {
            cal.add(6, date_count);
        }
        if (type.equals("week")) {
            cal.add(3, date_count);
        }
        if (type.equals("month")) {
            cal.add(2, date_count);
        }
        String symbol = ">=";
        if (date_symbol.equals("before")) {
            symbol = "<=";
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("store_id", CommUtil.null2Long(store_id));
        params.put("createtime", cal.getTime());
        params.put("evaluate_buyer_val",
            Integer.valueOf(CommUtil.null2Int(Integer.valueOf(evaluate_val))));
        List<Evaluate> evas = this.evaluateService
            .query(
                "select obj from Evaluate obj where obj.evaluate_goods.goods_store.id=:store_id and obj.evaluate_buyer_val=:evaluate_buyer_val and obj.createtime"
                        + symbol + ":createtime", params, -1, -1);
        return evas.size();
    }

    public Map query_point(Store store) {
        Map<String, Object> map = new HashMap<String, Object>();
        double description_result = 0.0D;
        double service_result = 0.0D;
        double ship_result = 0.0D;
        if (store.getSc() != null) {
            StoreClass sc = this.storeClassService.getObjById(store.getSc().getId());
            float description_evaluate = CommUtil.null2Float(sc.getDescription_evaluate());
            float service_evaluate = CommUtil.null2Float(sc.getService_evaluate());
            float ship_evaluate = CommUtil.null2Float(sc.getShip_evaluate());
            if (store.getPoint() != null) {
                float store_description_evaluate = CommUtil.null2Float(store.getPoint()
                    .getDescription_evaluate());
                float store_service_evaluate = CommUtil.null2Float(store.getPoint()
                    .getService_evaluate());
                float store_ship_evaluate = CommUtil
                    .null2Float(store.getPoint().getShip_evaluate());

                description_result = CommUtil.div(
                    Float.valueOf(store_description_evaluate - description_evaluate),
                    Float.valueOf(description_evaluate));
                service_result = CommUtil.div(
                    Float.valueOf(store_service_evaluate - service_evaluate),
                    Float.valueOf(service_evaluate));
                ship_result = CommUtil.div(Float.valueOf(store_ship_evaluate - ship_evaluate),
                    Float.valueOf(ship_evaluate));
            }
        }
        if (description_result > 0.0D) {
            map.put("description_css", "better");
            map.put("description_type", "高于");
            map.put(
                "description_result",
                CommUtil.null2String(Double.valueOf(CommUtil.mul(
                    Double.valueOf(description_result), Integer.valueOf(100)))) + "%");
        }
        if (description_result == 0.0D) {
            map.put("description_css", "better");
            map.put("description_type", "持平");
            map.put("description_result", "-----");
        }
        if (description_result < 0.0D) {
            map.put("description_css", "lower");
            map.put("description_type", "低于");
            map.put(
                "description_result",
                CommUtil.null2String(Double.valueOf(CommUtil.mul(
                    Double.valueOf(-description_result), Integer.valueOf(100))))
                        + "%");
        }
        if (service_result > 0.0D) {
            map.put("service_css", "better");
            map.put("service_type", "高于");
            map.put(
                "service_result",
                CommUtil.null2String(Double.valueOf(CommUtil.mul(Double.valueOf(service_result),
                    Integer.valueOf(100)))) + "%");
        }
        if (service_result == 0.0D) {
            map.put("service_css", "better");
            map.put("service_type", "持平");
            map.put("service_result", "-----");
        }
        if (service_result < 0.0D) {
            map.put("service_css", "lower");
            map.put("service_type", "低于");
            map.put(
                "service_result",
                CommUtil.null2String(Double.valueOf(CommUtil.mul(Double.valueOf(-service_result),
                    Integer.valueOf(100)))) + "%");
        }
        if (ship_result > 0.0D) {
            map.put("ship_css", "better");
            map.put("ship_type", "高于");
            map.put(
                "ship_result",
                CommUtil.null2String(Double.valueOf(CommUtil.mul(Double.valueOf(ship_result),
                    Integer.valueOf(100)))) + "%");
        }
        if (ship_result == 0.0D) {
            map.put("ship_css", "better");
            map.put("ship_type", "持平");
            map.put("ship_result", "-----");
        }
        if (ship_result < 0.0D) {
            map.put("ship_css", "lower");
            map.put("ship_type", "低于");
            map.put(
                "ship_result",
                CommUtil.null2String(Double.valueOf(CommUtil.mul(Double.valueOf(-ship_result),
                    Integer.valueOf(100)))) + "%");
        }
        return map;
    }
}
