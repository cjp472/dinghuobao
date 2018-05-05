package com.javamalls.front.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
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
import com.javamalls.ctrl.seller.Tools.MenuTools;
import com.javamalls.front.web.h5.action.Constent;
import com.javamalls.front.web.tools.AreaViewTools;
import com.javamalls.front.web.tools.OrderViewTools;
import com.javamalls.front.web.tools.StoreViewTools;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.query.UserStoreRelationQueryObject;
import com.javamalls.platform.service.IAccessoryService;
import com.javamalls.platform.service.IAlbumService;
import com.javamalls.platform.service.IAreaService;
import com.javamalls.platform.service.IArticleService;
import com.javamalls.platform.service.ICouponInfoService;
import com.javamalls.platform.service.ICouponService;
import com.javamalls.platform.service.IIntegralLogService;
import com.javamalls.platform.service.IMessageService;
import com.javamalls.platform.service.IRoleService;
import com.javamalls.platform.service.ISettleAccountsService;
import com.javamalls.platform.service.ISnsFriendService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.IStoreVIPService;
import com.javamalls.platform.service.IStrategyService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;
import com.javamalls.platform.service.IUserStoreRelationService;

/**我的供应商
 * 
 *                       
 * @Filename: UserContentStoreAction.java
 * @Version: 2.7.0
 * @Author: 桑森林
 * @Email: sangslmail@163.com
 *
 */
@Controller
public class UserContentStoreAction {
    private static final Logger       logger = Logger.getLogger(UserContentStoreAction.class);
    @Autowired
    private ISysConfigService         configService;
    @Autowired
    private IUserConfigService        userConfigService;
    @Autowired
    private IUserService              userService;
    @Autowired
    private IMessageService           messageService;
    @Autowired
    private IStoreService             storeService;
    @Autowired
    private IArticleService           articleService;
    @Autowired
    private StoreViewTools            storeViewTools;
    @Autowired
    private OrderViewTools            orderViewTools;
    @Autowired
    private AreaViewTools             areaViewTools;
    @Autowired
    private MenuTools                 menuTools;
    @Autowired
    private ICouponService            couponService;
    @Autowired
    private IStoreVIPService          storeVIPServ;
    @Autowired
    private IAccessoryService         accessoryService;
    @Autowired
    private ICouponInfoService        couponinfoService;
    @Autowired
    private ISettleAccountsService    settleAccountsService;
    @Autowired
    private IRoleService              roleService;
    @Autowired
    private IAlbumService             albumService;
    @Autowired
    private ISnsFriendService         sndFriendService;
    @Autowired
    private IIntegralLogService       integralLogService;
    @Autowired
    private IStrategyService          strategyService;
    @Autowired
    private IAreaService              areaService;
    @Autowired
    private IUserStoreRelationService userStoreRelationService;

    /**
     * 供应商列表
     * @throws Exception 
     */
    @SecurityMapping(title = "我的供应商", value = "/buyer/UserContentStore.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/buyer/UserContentStore.htm" })
    public ModelAndView UserContentStore(HttpServletRequest request, HttpServletResponse response,
                                         String currentPage, String orderBy, String orderType,
                                         String type, String quserName, @PathVariable String storeId)
                                                                                                     throws Exception {

        String mvurl = "user/default/usercenter/userContentStore.html";
        String loginUrl = "buyer/buyer_login.html";
        //判断终端类型
        String jm_view_type = CommUtil.null2String(request.getSession(false).getAttribute(
            Constent.WEB_TYPE_KEY));
        if (CommUtil.isNotNull(jm_view_type) && jm_view_type.equals("mobile")) {
            mvurl = "user/default/usercenter/h5/h5_userContentStore.html";
            loginUrl = "h5/login.html";
        }

        ModelAndView mv = new JModelAndView(mvurl, this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), JModelAndView.SYSTEM_PATH, request, response);
        User user = SecurityUserHolder.getCurrentUser();
        if (user == null) {
            Store store = this.storeService.getObjById(CommUtil.null2Long(storeId));
            mv = new JModelAndView(loginUrl, this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), JModelAndView.SHOP_PATH, request, response);
            mv.addObject("store", store);
            return mv;
        }
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        if (orderBy == null || orderBy.equals("")) {
            orderBy = "id";
        }
        UserStoreRelationQueryObject qo = new UserStoreRelationQueryObject(currentPage, mv,
            orderBy, orderType);
        qo.addQuery("obj.status", new SysMap("status", 2), "=");//审核通过
        qo.addQuery(
            "obj.user.id",
            new SysMap("user_id", CommUtil.null2Long(SecurityUserHolder.getCurrentUser().getId()
                                                     + "")), "=");
        IPageList pList = null;
        try {
            pList = this.userStoreRelationService.list(qo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        CommUtil.saveIPageList2ModelAndView(url + "/buyer/UserContentStore.htm", "", "", pList, mv);
        return mv;
    }

    /**
     * 我的申请列表
     * @throws Exception 
     */
    @SecurityMapping(title = "我的申请列表", value = "/buyer/UserApplyContentStore.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/buyer/UserApplyContentStore.htm" })
    public ModelAndView UserApplyContentStore(HttpServletRequest request,
                                              HttpServletResponse response, String currentPage,
                                              String orderBy, String orderType, String type,
                                              String quserName, @PathVariable String storeId)
                                                                                             throws Exception {

        String mvurl = "user/default/usercenter/userApplyContentStore.html";
        String loginUrl = "buyer/buyer_login.html";
        //判断终端类型
        String jm_view_type = CommUtil.null2String(request.getSession(false).getAttribute(
            Constent.WEB_TYPE_KEY));
        if (CommUtil.isNotNull(jm_view_type) && jm_view_type.equals("mobile")) {
            mvurl = "user/default/usercenter/h5/h5_userApplyContentStore.html";
            loginUrl = "h5/login.html";
        }

        ModelAndView mv = new JModelAndView(mvurl, this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), JModelAndView.SYSTEM_PATH, request, response);
        User user = SecurityUserHolder.getCurrentUser();
        if (user == null) {
            Store store = this.storeService.getObjById(CommUtil.null2Long(storeId));
            mv = new JModelAndView(loginUrl, this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), JModelAndView.SHOP_PATH, request, response);
            mv.addObject("store", store);
            return mv;
        }
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        if (orderBy == null || orderBy.equals("")) {
            orderBy = "id";
        }
        UserStoreRelationQueryObject qo = new UserStoreRelationQueryObject(currentPage, mv,
            orderBy, orderType);
        qo.addQuery("obj.status", new SysMap("status", 0), "!=");
        qo.addQuery(
            "obj.user.id",
            new SysMap("user_id", CommUtil.null2Long(SecurityUserHolder.getCurrentUser().getId()
                                                     + "")), "=");
        IPageList pList = null;

        //手机端默认展示5条
        if (CommUtil.isNotNull(jm_view_type) && jm_view_type.equals("mobile")) {
            qo.setPageSize(6);
        }
        try {
            pList = this.userStoreRelationService.list(qo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        CommUtil.saveIPageList2ModelAndView(url + "/buyer/UserApplyContentStore.htm", "", "",
            pList, mv);
        return mv;
    }

}
