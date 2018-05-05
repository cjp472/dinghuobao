package com.javamalls.front.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.UserStoreRelation;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.IUserService;
import com.javamalls.platform.service.IUserStoreRelationService;

/**申请成为会员
 * 
 *                       
 * @Filename: ApplyMember.java
 * @Version: 2.7.0
 * @Author: 桑森林
 * @Email: sangslmail@163.com
 *
 */
@Controller
public class ApplyMember {
    private static final Logger       logger = Logger.getLogger(ApplyMember.class);

    @Autowired
    private IStoreService             storeService;
    @Autowired
    private IUserStoreRelationService userStoreRelationService;
    @Autowired
    private IUserService              userService;

    /**
     * 申请成为卖家会员
     */
    @RequestMapping(value = { "/store/{id}.htm/applymember.htm" })
    public void ApplyMember(HttpServletResponse response, String store_id) {

        User user = SecurityUserHolder.getCurrentUser();
        int ret = 0;//未登录
        if (store_id != null && !"".equals(store_id)) {
            if (user != null) {
                //判断是不是买家
                boolean flag = true;
                if (user.getUserRole().equals("BUYER") || user.getUserRole().equals("BUYER_SELLER")) {
                    User objById = this.userService.getObjById(user.getId());
                    Store store = objById.getStore();
                    boolean falg1 = true;
                    if (store != null) {
                        if (store_id.equals(store.getId().toString())) {
                            falg1 = false;
                        }
                    }
                    if (falg1) {
                        //判断是否已经是会员
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("user_id", user.getId());
                        map.put("store_id", CommUtil.null2Long(store_id));
                        String sql = "select obj from UserStoreRelation obj where obj.user.id=:user_id and obj.store.id=:store_id ";
                        List<UserStoreRelation> userList = this.userStoreRelationService.query(sql,
                            map, -1, -1);
                        if (userList.size() > 0) {

                            UserStoreRelation userStoreRelation = userList.get(0);
                            //System.out.println(userStoreRelation.getStatus()+"===============================");
                            if (userStoreRelation.getStatus() == 2) {
                                ret = 3;//会员已存在
                            } else if (userStoreRelation.getStatus() == 1) {
                                ret = 4;//已经提交审核
                            } else if (userStoreRelation.getStatus() == 3) {
                                //存在审核失败，修改状态
                                userStoreRelation.setCreatetime(new Date());
                                userStoreRelation.setStatus(1);
                                flag = userStoreRelationService.update(userStoreRelation);

                                if (flag) {
                                    ret = 1;//申请提交成功
                                } else {
                                    ret = 2;//申请保存失败
                                }
                            }

                        } else {
                            UserStoreRelation userStoreRelation = new UserStoreRelation();
                            userStoreRelation.setUser(user);
                            userStoreRelation.setStore(storeService.getObjById(CommUtil
                                .null2Long(store_id)));
                            userStoreRelation.setStatus(1); //改成常量
                            userStoreRelation.setCreatetime(new Date());
                            flag = userStoreRelationService.save(userStoreRelation);
                            if (flag) {
                                ret = 1;//申请提交成功
                            } else {
                                ret = 2;//申请保存失败
                            }
                        }

                    } else {
                        ret = 6;//不能申请自己的供采关系
                    }

                } else {
                    ret = 5;//不是买家
                }

            }
        } else {
            ret = 7;//店铺不存在
        }
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
