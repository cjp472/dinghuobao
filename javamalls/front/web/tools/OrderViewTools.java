package com.javamalls.front.web.tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.domain.OrderForm;
import com.javamalls.platform.domain.query.OrderFormQueryObject;
import com.javamalls.platform.service.IOrderFormService;

/**订单
 *                       
 * @Filename: OrderViewTools.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Component
public class OrderViewTools {
    @Autowired
    private IOrderFormService orderFormService;

    public long query_user_order(String order_status) {
        Map<String, Object> params = new HashMap<String, Object>();
        int status = -1;
        if (order_status.equals("order_submit")) {
            status = 10;
        }
        if (order_status.equals("order_pay")) {
            status = 20;
        }
        if (order_status.equals("order_shipping")) {
            status = 30;
        }
        if (order_status.equals("order_receive")) {
            status = 40;
        }
        if (order_status.equals("order_finish")) {
            status = 60;
        }
        if (order_status.equals("order_cancel")) {
            status = 0;
        }
        params.put("status", Integer.valueOf(status));
        params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
        long count=0;
        count= this.orderFormService
            .queryCount(
                "select count(obj) from OrderForm obj where obj.order_status=:status and obj.user.id=:user_id",
                params);
        return count;
    }

    //查询店铺订单
    public int query_store_order(String order_status) {
        if (SecurityUserHolder.getCurrentUser().getStore() != null) {
         //   Map<String, Object> params = new HashMap<String, Object>();
            int status = -1;
            if (order_status.equals("order_submit")) {
                status = 10;
            }
            if (order_status.equals("order_pay")) {
                status = 20;
            }
            if (order_status.equals("order_shipping")) {
                status = 30;
            }
            if (order_status.equals("order_receive")) {
                status = 40;
            }
            if (order_status.equals("order_return_apply")) {
                status = 45;
            }
            if (order_status.equals("order_return")) {
                status = 46;
            }
            if (order_status.equals("order_finish")) {
                status = 60;
            }
            if (order_status.equals("order_cancel")) {
                status = 0;
            }
            OrderFormQueryObject queryObject=new OrderFormQueryObject();
            if (order_status.equals("order_pay")) {
            	Map<String, Object> map=new HashMap<String, Object>();
            	map.put("status1", 16);
            	map.put("status2", 20);
            	 queryObject.addQuery(" (obj.order_status=:status1 or obj.order_status=:status2) ",map);
            }else{
            	 queryObject.addQuery("obj.order_status", new SysMap("status", Integer.valueOf(status)), "=");
            }
           
            queryObject.addQuery("obj.store.id", new SysMap("store_id",  SecurityUserHolder.getCurrentUser().getStore().getId()), "=");
            int count = this.orderFormService.count(queryObject);
           /* params.put("status", );
            params.put("store_id", SecurityUserHolder.getCurrentUser().getStore().getId());
            List<OrderForm> ofs = this.orderFormService
                .query(
                    "select obj from OrderForm obj where obj.order_status=:status and obj.store.id=:store_id",
                    params, -1, -1);*/
            return count;
        }
        return 0;
    }
    
    //根据日期统计订单
    public int query_store_orderByTime(String date_status) {
        if (SecurityUserHolder.getCurrentUser().getStore() != null) {
        	//今日
         
        	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        	Date begin=new Date();
        	Date end=new Date();
        	//昨日
        	if("last_date".equals(date_status)){
        		Calendar calendar=Calendar.getInstance();
        		calendar.add(Calendar.DAY_OF_MONTH,-1);
        		Date time = calendar.getTime();
        		String format = sdf.format(time);
        		begin=CommUtil.formatMaxDate(format+" 00:00:00");
        		end=CommUtil.formatMaxDate(format+" 23:59:59");
        	}else if("now_month".equals(date_status)){//今月
        		Calendar cal=Calendar.getInstance();//获取当前日期
        		cal.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
        		Date beginDate = cal.getTime();
        		String beginformat = sdf.format(beginDate);
        		begin=CommUtil.formatMaxDate(beginformat+" 00:00:00");
        		cal.add(Calendar.MONTH,1);//月增加1天
        		cal.add(Calendar.DAY_OF_MONTH,-1);//日期倒数一日,既得到本月最后一天
        		Date endDate = cal.getTime();
        		String endformat = sdf.format(endDate);
        		end=CommUtil.formatMaxDate(endformat+" 23:59:59");
        		
			}else if("last_month".equals(date_status)){//上月
				
				Calendar cal=Calendar.getInstance();//获取当前日期
				cal.add(Calendar.MONTH,-1);//上个月
				
        		cal.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
        		Date beginDate = cal.getTime();
        		String beginformat = sdf.format(beginDate);
        		begin=CommUtil.formatMaxDate(beginformat+" 00:00:00");
        		cal.add(Calendar.MONTH,1);//月增加1天
        		cal.add(Calendar.DAY_OF_MONTH,-1);//日期倒数一日,既得到本月最后一天
        		Date endDate = cal.getTime();
        		String endformat = sdf.format(endDate);
        		end=CommUtil.formatMaxDate(endformat+" 23:59:59");
			}else{//今天
				Date date=new Date();
				String format = sdf.format(date);
				begin=CommUtil.formatMaxDate(format+" 00:00:00");
				end=CommUtil.formatMaxDate(format+" 23:59:59");
			}
        	
            OrderFormQueryObject queryObject=new OrderFormQueryObject();
            //createtime
            queryObject.addQuery("obj.order_status", new SysMap("status", Integer.valueOf(0)), ">");
            queryObject.addQuery("obj.disabled", new SysMap("disabled",false), "=");
            queryObject.addQuery("obj.store.id", new SysMap("store_id",  SecurityUserHolder.getCurrentUser().getStore().getId()), "=");
            queryObject.addQuery("obj.createtime", new SysMap("begintime",begin), ">=");
            queryObject.addQuery("obj.createtime", new SysMap("endtime",end), "<=");
            
            int count = this.orderFormService.count(queryObject);
      
            return count;
        }
        return 0;
    }
}
