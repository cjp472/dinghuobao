package com.javamalls.front.web.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.domain.GroupGoods;
import com.javamalls.platform.service.IGroupGoodsService;
import com.javamalls.platform.service.IGroupService;

/**团购
 *                       
 * @Filename: GroupViewTools.java
 * @Version: 2.7.0
 * @Author: 王阳
 * @Email: wangyang@163.com
 *
 */
@Component
public class GroupViewTools {
    @Autowired
    private IGroupService      groupService;
    @Autowired
    private IGroupGoodsService groupGoodsService;

    public List<GroupGoods> query_goods(String group_id, int count) {
        List<GroupGoods> list = new ArrayList();
        Map params = new HashMap();
        params.put("group_id", CommUtil.null2Long(group_id));
        list = this.groupGoodsService
            .query(
                "select obj from GroupGoods obj where obj.group.id=:group_id order by obj.createtime desc",
                params, 0, count);
        return list;
    }
}
