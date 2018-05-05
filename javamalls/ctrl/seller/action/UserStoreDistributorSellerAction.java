package com.javamalls.ctrl.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.constant.Constant;
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
import com.javamalls.payment.chinabank.h5.util.JsonUtil;
import com.javamalls.payment.weixin.vo.ResultMsg;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.UserStoreDistributor;
import com.javamalls.platform.domain.UserStoreRelation;
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
import com.javamalls.platform.vo.UserStoreRelationVo;
import com.utils.SendReqAsync;

/**用户申请店铺分销商Action卖家中心
 * 
 *                       
 * @Filename: UserStoreDistributorSellerAction.java
 * @Version: 2.7.0
 * @Author: zmw
 *
 */
@Controller
public class UserStoreDistributorSellerAction {
    private static final Logger       logger = Logger.getLogger(UserStoreDistributorSellerAction.class);
    @Autowired
    private ISysConfigService         configService;
    @Autowired
    private IUserConfigService        userConfigService;
    @Autowired
    private IStoreService             storeService;
    @Autowired
    private IUserStoreDistributorService userStoreDistributorService;
    @Autowired
    private StoreViewTools			  storeViewTools;
    
    /**
     * 分销商列表
     * @throws Exception 
     */
    @SecurityMapping(title = "申请列表", value = "/seller/distributor_apply_list.htm*", rtype = "seller", rname = "分销商管理", rcode = "store_user_seller", rgroup = "分销商管理")
    @RequestMapping({ "/seller/distributor_apply_list.htm" })
    public ModelAndView storeUserList(HttpServletRequest request, HttpServletResponse response,
                                     String currentPage, String orderBy, String orderType,
                                     String type, String quserName)
                                                                                   throws Exception {
        ModelAndView mv = new JModelAndView("seller/storeUserListDistributorList.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
            JModelAndView.SHOP_PATH, request, response);
        if (SecurityUserHolder.getCurrentUser() == null) {
            mv = new JModelAndView("login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            return mv;
        }
      User user=SecurityUserHolder.getCurrentUser();
     if (user.getStore().getId() ==null ) {
    	 mv = new JModelAndView("seller/storeUserListDistributorList.html", this.configService.getSysConfig(),
                 this.userConfigService.getUserConfig(), 1, request, response);
             return mv;
	}
     	Store store=storeService.getObjById(user.getStore().getId());
        if (orderBy == null || orderBy.equals("")) {
            orderBy = "id";
        }
       
        UserStoreDistributorQueryObject qo = new UserStoreDistributorQueryObject(currentPage, mv,
                orderBy, orderType);
            qo.addQuery("obj.status", new SysMap("status", 1), "!=");
            qo.addQuery("obj.store.id", new SysMap("store_id", CommUtil.null2Long(store.getId()+ "")), "=");
            qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");
        if (quserName != null && !"".equals(quserName)) {
           // Map<String, Object> map = new HashMap<String, Object>();
            qo.addQuery("obj.user.mobile like '%" + quserName + "%'", null);
        }
        mv.addObject("quserName", quserName);
        IPageList pList = null;
        try {
            pList = this.userStoreDistributorService.list(qo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        mv.addObject("storeViewTools", this.storeViewTools);
        
        
        return mv;
    }
    /**
     * 分销商申请列表
     * @throws Exception 
     */
    @SecurityMapping(title = "申请列表", value = "/seller/distributor_apply_list_new.htm*", rtype = "seller", rname = "分销商管理", rcode = "store_user_seller", rgroup = "分销商管理")
    @RequestMapping({ "/seller/distributor_apply_list_new.htm" })
    public ModelAndView storeUserNew(HttpServletRequest request, HttpServletResponse response,
                                     String currentPage, String orderBy, String orderType,
                                     String type, String quserName)
                                                                                   throws Exception {
        ModelAndView mv = new JModelAndView("seller/auditUserListDistributor.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
            JModelAndView.SHOP_PATH, request, response);
        if (SecurityUserHolder.getCurrentUser() == null) {
            mv = new JModelAndView("login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            return mv;
        }
      User user=SecurityUserHolder.getCurrentUser();
     if (user.getStore().getId() ==null ) {
    	 mv = new JModelAndView("seller/auditUserListDistributor.html", this.configService.getSysConfig(),
                 this.userConfigService.getUserConfig(), 1, request, response);
             return mv;
	}
     	Store store=storeService.getObjById(user.getStore().getId());
        if (orderBy == null || orderBy.equals("")) {
            orderBy = "id";
        }
       
        UserStoreDistributorQueryObject qo = new UserStoreDistributorQueryObject(currentPage, mv,
                orderBy, orderType);
            qo.addQuery("obj.status", new SysMap("status", 1), "=");
            qo.addQuery("obj.store.id", new SysMap("store_id", CommUtil.null2Long(store.getId()+ "")), "=");
            qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");
        if (quserName != null && !"".equals(quserName)) {
           // Map<String, Object> map = new HashMap<String, Object>();
            qo.addQuery("obj.user.mobile like '%" + quserName + "%'", null);
        }
        mv.addObject("quserName", quserName);
        IPageList pList = null;
        try {
            pList = this.userStoreDistributorService.list(qo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        return mv;
    }
    @SecurityMapping(title = "审核分销商", value = "/seller/editUserStoreDistributor.htm*", rtype = "seller", rname = "分销商管理", rcode = "store_user_seller", rgroup = "分销商管理")
    @RequestMapping({ "/seller/editUserStoreDistributor.htm" })
    public ModelAndView editUserStoreDistributor(HttpServletRequest request, HttpServletResponse response,
                                     String currentPage, String orderBy, String orderType,
                                     String type, String quserName,String id)
                                                                                   throws Exception {
    	  logger.info("正在审核分销商"+id);
        ModelAndView mv = new JModelAndView("seller/userStoreDistributor.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
            JModelAndView.SHOP_PATH, request, response);
        if (SecurityUserHolder.getCurrentUser() == null) {
            mv = new JModelAndView("login.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            return mv;
        }
        UserStoreDistributor userStoreDistributor = userStoreDistributorService.getObjById(CommUtil.null2Long(id));
     
        mv.addObject("obj", userStoreDistributor);
        return mv;
    }
    /**
     * 审核分销商
     */
    @RequestMapping("/seller/audit_distributor_apply_new.htm")
    @ResponseBody
    public void editUserStoreDistributorSave(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        String status = request.getParameter("status");
        String suggess=request.getParameter("suggess");
        ResultMsg   msg=CommUtil.setResultMsgData(null, false, "提交信息错误");
        UserStoreDistributor userStoreDistributor = userStoreDistributorService.getObjById(CommUtil
            .null2Long(id));
        if (userStoreDistributor != null ) {
            if (userStoreDistributor.getStatus() == 1) {
            	if (CommUtil.null2Long(status)==2 && suggess!=null && !"".equals(suggess)) {
            		userStoreDistributor.setUpdatetime(new Date());
                    userStoreDistributor.setStatus(2);
                    userStoreDistributor.setSuggess(suggess);
                    Boolean flag = userStoreDistributorService.update(userStoreDistributor);
                    if (flag) {
                    	//ret = 1;
                    	msg=CommUtil.setResultMsgData(null, true, "审核成功");
                        logger.info("审核成功");
                    } else {
                        //ret = 2;
                    	msg=CommUtil.setResultMsgData(null, false, "审核失败");
                        logger.info("审核失败");
                    }
				}else if(CommUtil.null2Long(status)==3 && suggess!=null && !"".equals(suggess)){
					userStoreDistributor.setUpdatetime(new Date());
                    userStoreDistributor.setStatus(3);
                    userStoreDistributor.setSuggess(suggess);
                    userStoreDistributor.setFail(1);
                    Boolean flag = userStoreDistributorService.update(userStoreDistributor);
                    if (flag) {
                    	//ret = 1;
                    	msg=CommUtil.setResultMsgData(null, true, "审核成功");
                        logger.info("审核成功");
                    } else {
                        //ret = 2;
                    	msg=CommUtil.setResultMsgData(null, false, "审核失败");
                        logger.info("审核失败");
                    }
				}else if (suggess==null ||"".equals(suggess)) {
					//ret=4;
					msg=CommUtil.setResultMsgData(null, false, "审核建议不能为空");
				}
               
            } else {
               // ret = 3;
            	msg=CommUtil.setResultMsgData(null, false, "该申请已经被审核，请不要重复审核");
                logger.info("该申请已经被审核，请不要重复审核");
            }

        } else {
            logger.info("审核分销商传参出现异常");
        }
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(JsonUtil.write2JsonStr(msg));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
