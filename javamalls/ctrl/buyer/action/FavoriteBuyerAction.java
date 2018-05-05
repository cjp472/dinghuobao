package com.javamalls.ctrl.buyer.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.domain.Favorite;
import com.javamalls.platform.domain.query.FavoriteQueryObject;
import com.javamalls.platform.domain.query.UserTrackQueryObject;
import com.javamalls.platform.service.IFavoriteService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.ITrackService;
import com.javamalls.platform.service.IUserConfigService;

/**收藏管理
 *                       
 * @Filename: FavoriteBuyerAction.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class FavoriteBuyerAction {
    @Autowired
    private ISysConfigService  configService;
    @Autowired
    private IUserConfigService userConfigService;
    @Autowired
    private IFavoriteService   favoriteService;
    @Autowired
    private ITrackService      trackServ;

    /**
     * 浏览过的商品
     * 
     * @param request
     * @param response
     * @param currentPage
     * @param orderBy
     * @param orderType
     * @return
     */
    @SecurityMapping(title = "浏览过的商品", value = "/buyer/track_goods.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/buyer/track_goods.htm" })
    public ModelAndView track_goods(HttpServletRequest request, HttpServletResponse response,
                                    String currentPage, String orderBy, String orderType,@PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/user_track.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
            JModelAndView.SYSTEM_PATH, request, response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        UserTrackQueryObject qo = new UserTrackQueryObject(currentPage, mv, "createtime", "desc");
        qo.addQuery("obj.user.id",
            new SysMap("userid", SecurityUserHolder.getCurrentUser().getId()), "=");
        qo.setOrderBy("createtime");
        IPageList pList = this.trackServ.list(qo);
        mv.addObject("objs", pList.getResult());
         
        CommUtil.saveIPageList2ModelAndView(url + "/buyer/track_goods.htm", "", "", pList, mv);
        return mv;
    }

    @SecurityMapping(title = "用户商品收藏", value = "/buyer/favorite_goods.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/buyer/favorite_goods.htm" })
    public ModelAndView favorite_goods(HttpServletRequest request, HttpServletResponse response,
                                       String currentPage, String orderBy, String orderType,@PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/favorite_goods.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        String params = "";
        FavoriteQueryObject qo = new FavoriteQueryObject(currentPage, mv, orderBy, orderType);
        qo.addQuery("obj.type", new SysMap("type", Integer.valueOf(0)), "=");
        qo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser()
            .getId()), "=");
        qo.setPageSize(15);
        IPageList pList = this.favoriteService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/buyer/favorite_goods.htm", "", params, pList,
            mv);
        return mv;
    }

    @SecurityMapping(title = "用户店铺收藏", value = "/buyer/favorite_store.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/buyer/favorite_store.htm" })
    public ModelAndView favorite_store(HttpServletRequest request, HttpServletResponse response,
                                       String currentPage, String orderBy, String orderType,@PathVariable String storeId) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/favorite_store.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        String params = "";
        FavoriteQueryObject qo = new FavoriteQueryObject(currentPage, mv, orderBy, orderType);
        qo.addQuery("obj.type", new SysMap("type", Integer.valueOf(1)), "=");
        qo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser()
            .getId()), "=");
        IPageList pList = this.favoriteService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/buyer/favorite_store.htm", "", params, pList,
            mv);
        return mv;
    }

    @SecurityMapping(title = "用户收藏删除", value = "/buyer/favorite_del.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/buyer/favorite_del.htm" })
    public String favorite_del(HttpServletRequest request, HttpServletResponse response,
                               String mulitId, String currentPage, int type,@PathVariable String storeId) {
        String[] ids = mulitId.split(",");
        for (String id : ids) {
            if (!id.equals("")) {
                Favorite favorite = this.favoriteService
                    .getObjById(Long.valueOf(Long.parseLong(id)));
                if (favorite != null)
                    this.favoriteService.delete(Long.valueOf(Long.parseLong(id)));
            }
        }
        if (type == 0) {
            return "redirect:"+CommUtil.getURL(request) + "/buyer/favorite_goods.htm?currentPage=" + currentPage;
        }
        return "redirect:"+CommUtil.getURL(request) + "/buyer/favorite_store.htm?currentPage=" + currentPage;
    }
}
