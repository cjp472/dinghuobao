package com.javamalls.platform.test;

import java.util.Date;
import java.util.List;

import org.nutz.json.Json;

import com.javamalls.base.tools.Md5Encrypt;
import com.javamalls.platform.domain.Area;
import com.javamalls.platform.domain.IntegralLog;
import com.javamalls.platform.domain.Role;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.UserStoreRelation;
import com.javamalls.platform.domain.virtual.TransContent;
import com.javamalls.platform.domain.virtual.TransInfo;

public class TestJson {
    public static void main(String[] args) {
       /* String s = "{\"message\":\"ok\",\"status\":\"1\",\"state\":\"3\",\"data\":[{\"time\":\"2012-07-07 13:35:14\",\"context\":\"客户已签收\"},{\"time\":\"2012-07-07 09:10:10\",\"context\":\"离开 [北京石景山营业厅] 派送中，递送员[温]，电话[]\"},{\"time\":\"2012-07-06 19:46:38\",\"context\":\"到达 [北京石景山营业厅]\"},{\"time\":\"2012-07-06 15:22:32\",\"context\":\"离开 [北京石景山营业厅] 派送中，递送员[温]，电话[]\"},{\"time\":\"2012-07-06 15:05:00\",\"context\":\"到达 [北京石景山营业厅]\"},{\"time\":\"2012-07-06 13:37:52\",\"context\":\"离开 [北京_同城中转站] 发往 [北京石景山营业厅]\"},{\"time\":\"2012-07-06 12:54:41\",\"context\":\"到达 [北京_同城中转站]\"},{\"time\":\"2012-07-06 11:11:03\",\"context\":\"离开 [北京运转中心驻站班组] 发往 [北京_同城中转站]\"},{\"time\":\"2012-07-06 10:43:21\",\"context\":\"到达 [北京运转中心驻站班组]\"},{\"time\":\"2012-07-05 21:18:53\",\"context\":\"离开 [福建_厦门支公司] 发往 [北京运转中心_航空]\"},{\"time\":\"2012-07-05 20:07:27\",\"context\":\"已取件，到达 [福建_厦门支公司]\"}]}";
        TransInfo info = (TransInfo) Json.fromJson(TransInfo.class, s);
        System.out.println(info.getMessage());
        for (TransContent tc : info.getData()) {
            System.out.println(tc.getTime() + " " + tc.getContext());
        }*/
    	  User user = new User();
          user.setUserName("18233581234");
          user.setUserRole("BUYER");
          user.setCreatetime(new Date());
          user.setMobile("18233581234");
          user.setPassword(Md5Encrypt.md5("123456").toLowerCase());
      //    user.setUserOfstore(Integer.valueOf(2));
          Area area=new Area();
          area.setAreaName("北京");
          area.setId(Long.parseLong("10000"));
          area.setDisabled(false);
          user.setId(Long.parseLong("1"));
          user.setArea(area);
          UserStoreRelation relation=new UserStoreRelation();
	  //    	relation.setUser_id(Long.parseLong("32886"));
	   //   	relation.setStore_id(Long.parseLong("6"));
	      	relation.setStatus(1);
	      	relation.setId(Long.parseLong("1"));
	      	user.setRelation(relation);
          System.out.println(Json.toJson(user));
          
    	
    }
}
