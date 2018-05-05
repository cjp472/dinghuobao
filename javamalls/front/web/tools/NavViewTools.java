package com.javamalls.front.web.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.javamalls.platform.domain.Navigation;
import com.javamalls.platform.service.IActivityService;
import com.javamalls.platform.service.IArticleService;
import com.javamalls.platform.service.IGoodsClassService;
import com.javamalls.platform.service.INavigationService;

/**导航
 *                       
 * @Filename: NavViewTools.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Component
public class NavViewTools {
    @Autowired
    private INavigationService navService;
    @Autowired
    private IArticleService    articleService;
    @Autowired
    private IActivityService   activityService;
    @Autowired
    private IGoodsClassService goodsClassService;

    public List<Navigation> queryNav(int location, int count) {
        List<Navigation> navs = new ArrayList();
        Map params = new HashMap();
        params.put("display", Boolean.valueOf(true));
        params.put("location", Integer.valueOf(location));
        params.put("type", "sparegoods");
        navs = this.navService
            .query(
                "select obj from Navigation obj where obj.display=:display and obj.location=:location and obj.type!=:type order by obj.sequence asc",
                params, 0, count);
        return navs;
    }
}
