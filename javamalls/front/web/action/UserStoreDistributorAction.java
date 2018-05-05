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
import com.javamalls.platform.domain.query.UserStoreDistributorQueryObject;
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
import com.javamalls.platform.service.IUserStoreDistributorService;
import com.javamalls.platform.service.IUserStoreRelationService;

/**用户申请店铺分销商Action
 * 
 *                       
 * @Filename: UserStoreDistributorAction.java
 * @Version: 2.7.0
 * @Author: zmw
 *
 */
@Controller
public class UserStoreDistributorAction {
    private static final Logger       logger = Logger.getLogger(UserStoreDistributorAction.class);
    @Autowired
    private ISysConfigService         configService;
    @Autowired
    private IUserConfigService        userConfigService;
    @Autowired
    private IStoreService             storeService;
    @Autowired
    private IUserStoreDistributorService userStoreDistributorService;
    /**
     * 我的分销商申请列表---买家中心
     * @throws Exception 
     */
    @SecurityMapping(title = "我的申请列表", value = "/buyer/distributor_apply_list.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({ "/store/{storeId}.htm/buyer/distributor_apply_list.htm" })
    public ModelAndView UserApplydistributorStoreFront(HttpServletRequest request,
                                              HttpServletResponse response, String currentPage,
                                              String orderBy, String orderType, String type,
                                              String quserName, @PathVariable String storeId)
                                                                                             throws Exception {

        String mvurl = "user/default/usercenter/userApplyDistributorStore.html";
        String loginUrl = "buyer/buyer_login.html";
        //判断终端类型
        String jm_view_type = CommUtil.null2String(request.getSession(false).getAttribute(
            Constent.WEB_TYPE_KEY));
        if (CommUtil.isNotNull(jm_view_type) && jm_view_type.equals("mobile")) {
            mvurl = "user/default/usercenter/h5/h5_userApplyDistributorStore.html";
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
        UserStoreDistributorQueryObject qo = new UserStoreDistributorQueryObject(currentPage, mv,
            orderBy, orderType);
        qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");
        qo.addQuery("obj.user.id", new SysMap("user_id", CommUtil.null2Long(SecurityUserHolder.getCurrentUser().getId()+ "")), "=");
        IPageList pList = null;

        //手机端默认展示5条
        if (CommUtil.isNotNull(jm_view_type) && jm_view_type.equals("mobile")) {
            qo.setPageSize(6);
        }
        try {
            pList = this.userStoreDistributorService.list(qo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        CommUtil.saveIPageList2ModelAndView(url + "/buyer/distributor_apply_list.htm", "", "",
            pList, mv);
        return mv;
    }
    

}
