package com.javamalls.front.web.h5.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.ctrl.seller.Tools.MenuTools;
import com.javamalls.front.web.tools.AreaViewTools;
import com.javamalls.front.web.tools.OrderViewTools;
import com.javamalls.front.web.tools.StoreViewTools;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.UserStoreDistributor;
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

/**我的供应商
 * 
 *                       
 * @Filename: H5UserDistributorStoreAction.java
 * @Version: 2.7.0
 * @Author: zmw
 *
 */
@Controller
public class H5UserDistributorStoreAction {
    private static final Logger       logger = Logger.getLogger(H5UserDistributorStoreAction.class);
    @Autowired
    private ISysConfigService         configService;
    @Autowired
    private IUserConfigService        userConfigService;
 
    @Autowired
    private IStoreService             storeService;
    
    @Autowired
    private IUserStoreDistributorService userStoreDistributorService;

    /**
     * 我的申请列表 ajax 
     * @throws Exception 
     */
    @RequestMapping({ "/store/{storeId}.htm/mobile/buyer/UserApplyDistributorStore_ajax.htm" })
    public ModelAndView UserApplyDistributorStore_ajax(HttpServletRequest request,
                                                   HttpServletResponse response,
                                                   String currentPage, String orderBy,
                                                   String orderType, String type, String quserName,
                                                   @PathVariable String storeId) throws Exception {
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/h5/h5_userApplyDistributorStore_ajax.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
            JModelAndView.SYSTEM_PATH, request, response);
        User user = SecurityUserHolder.getCurrentUser();
        if (user == null) {
            Store store = this.storeService.getObjById(CommUtil.null2Long(storeId));
            mv = new JModelAndView("h5/login.html", this.configService.getSysConfig(),
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
        qo.setPageSize(6);
        try {
            pList = this.userStoreDistributorService.noLastList(qo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        return mv;
    }
    @RequestMapping({ "/store/{storeId}.htm/mobile/userStoreDistributorDetail.htm" })
    public ModelAndView UserApplyDistributorDetail(HttpServletRequest request,
                                                   HttpServletResponse response,
                                                   String currentPage, String orderBy,
                                                   String orderType, String type, String quserName,
                                                   @PathVariable String storeId,String  id) throws Exception {
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/h5/h5_userApplyDistributorStoreDetail.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
            JModelAndView.SYSTEM_PATH, request, response);
        UserStoreDistributor userStoreDistributor=userStoreDistributorService.getObjById(CommUtil.null2Long(id));
        mv.addObject("obj", userStoreDistributor);
		return mv;
        
        
        
    }
}
