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
import com.javamalls.payment.chinabank.h5.util.JsonUtil;
import com.javamalls.payment.weixin.vo.ResultMsg;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.UserStoreDistributor;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.IUserStoreDistributorService;

/**申请成为分销商
 * 
 *                       
 * @Filename: ApplyDistributorAction.java
 * @Version: 2.7.0
 * @Author: zmw
 *
 */
@Controller
public class ApplyDistributorAction {
    private static final Logger       logger = Logger.getLogger(ApplyDistributorAction.class);

    @Autowired
    private IUserStoreDistributorService userStoreDistributorService;
    @Autowired
    private IStoreService storeService;
 
    /**
     * 申请成为分销商
     */
    @RequestMapping(value = { "/store/{id}.htm/applyDistributors.htm" })
    public void ApplyDistributors(HttpServletResponse response, String store_id) {
        User user = SecurityUserHolder.getCurrentUser();
        Store store=storeService.getObjById(CommUtil.null2Long(store_id));
        
        //int ret = 0;//未登录
        ResultMsg   msg=CommUtil.setResultMsgData(null, false, "请您先登录");
        boolean flag = true;
        Map<String, Object> map = new HashMap<String, Object>();
        if (store_id != null && !"".equals(store_id)) {
            if (user != null) {
            	if (user.getStore()==null || user.getStore().getStore_status()!=2) {
            		msg=CommUtil.setResultMsgData(null, false, "您还不是平台的分销商，不能够申请该店铺的分销商");
				}else{
	                map.put("user_id", user.getId());
	                map.put("store_id", CommUtil.null2Long(store_id));
	                String sql=" select obj from UserStoreDistributor obj where obj.user.id=:user_id and obj.store.id=:store_id  "
	                		+ "and obj.disabled=0 order by obj.id desc";
	                List<UserStoreDistributor> userStoreDistributors=this.userStoreDistributorService.query(sql,map, -1, -1);
	                //判断是否已经申请为该店铺的分销商
	                if (userStoreDistributors!=null && userStoreDistributors.size()>0) {
	                	if (userStoreDistributors.get(0).getStatus()==1) {
	                		//ret = 1;//已经申请,待审核
	                		msg=CommUtil.setResultMsgData(null, false, "您已经提交过申请，无需重复提交");
						}else if (userStoreDistributors.get(0).getStatus()==2) {
	                		//ret = 2;//已经申请,审核通过
							msg=CommUtil.setResultMsgData(null, false, "您已经是该店铺的分销商，无需申请");
						}else if (userStoreDistributors.get(0).getStatus()==3) {
							UserStoreDistributor userStoreDistributor=new UserStoreDistributor();
							userStoreDistributor.setDisabled(false);
							userStoreDistributor.setCreatetime(new Date());
							userStoreDistributor.setStatus(1);
							userStoreDistributor.setUser(user);
							userStoreDistributor.setStore(store);
							flag=userStoreDistributorService.save(userStoreDistributor);
							// 成功重新提交申请 
							userStoreDistributors.get(0).setFail(2);
							userStoreDistributorService.update(userStoreDistributors.get(0));
							if (flag) {
								//ret = 3;
								msg=CommUtil.setResultMsgData(null, true, "申请已提交，请耐心等待后台审核");
							}else{
								//ret = 4;
								msg=CommUtil.setResultMsgData(null, false, "申请保存失败");
							}
							
						}
	                	
					}else{
						UserStoreDistributor userStoreDistributor=new UserStoreDistributor();
						userStoreDistributor.setDisabled(false);
						userStoreDistributor.setCreatetime(new Date());
						userStoreDistributor.setStatus(1);
						userStoreDistributor.setUser(user);
						userStoreDistributor.setStore(store);
						flag=userStoreDistributorService.save(userStoreDistributor);
						if (flag) {
							//ret = 3;
							msg=CommUtil.setResultMsgData(null, true, "申请已提交，请耐心等待后台审核");
						}else{
							//ret = 4;
							msg=CommUtil.setResultMsgData(null, false, "申请保存失败");
						}
						
					}
				}
            	
            }
        } else {
            //ret = 5;//店铺不存在
        	msg=CommUtil.setResultMsgData(null, false, "没有找到店铺");
        }
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(JsonUtil.write2JsonStr(msg));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
    }
    
}
