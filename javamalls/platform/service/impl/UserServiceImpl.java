package com.javamalls.platform.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javamalls.base.basedao.IGenericDAO;
import com.javamalls.base.constant.Constant;
import com.javamalls.base.query.GenericPageList;
import com.javamalls.base.query.PageObject;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.Md5Encrypt;
import com.javamalls.payment.chinabank.h5.util.JsonUtil;
import com.javamalls.platform.domain.Album;
import com.javamalls.platform.domain.IntegralLog;
import com.javamalls.platform.domain.Role;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.UserStoreRelation;
import com.javamalls.platform.service.IAlbumService;
import com.javamalls.platform.service.IIntegralLogService;
import com.javamalls.platform.service.IRoleService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserService;
import com.javamalls.platform.service.IUserStoreRelationService;
import com.utils.SendReqAsync;

@Service
@Transactional
public class UserServiceImpl implements IUserService {
    @Resource(name = "userDAO")
    private IGenericDAO<User> userDAO;
    @Autowired
    private IRoleService	roleService;
    @Autowired
    private ISysConfigService	configService;
    @Autowired
    private IIntegralLogService integralLogService;
    @Autowired
    private IAlbumService	albumService;
    @Autowired
    private IStoreService	storeService;
    @Autowired
    private IUserStoreRelationService userStoreRelationService;
    @Autowired
    private SendReqAsync              sendReqAsync;

    public boolean delete(Long id) {
        try {
            this.userDAO.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public User getObjById(Long id) {
        return (User) this.userDAO.get(id);
    }

    public boolean save(User user) {
        try {
            this.userDAO.save(user);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public boolean update(User user) {
        try {
            // 防止子账号保存店铺id导致出错
            if (user.getParent() != null) {
                user.setStore(null);
            }

            this.userDAO.update(user);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public List<User> query(String query, Map params, int begin, int max) {
        return this.userDAO.query(query, params, begin, max);
    }

    public IPageList list(IQueryObject properties) {
        if (properties == null) {
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(User.class, query, params, this.userDAO);
        if (properties != null) {
            PageObject pageObj = properties.getPageObj();
            if (pageObj != null) {
                pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj.getCurrentPage()
                    .intValue(), pageObj.getPageSize() == null ? 0 : pageObj.getPageSize()
                    .intValue());
            }
        } else {
            pList.doList(0, -1);
        }
        return pList;
    }

    public User getObjByProperty(String propertyName, String value) {
        return (User) this.userDAO.getBy(propertyName, value);
    }

	@Override
	public Long queryCount(String paramString, Map paramMap) {
		return this.userDAO.queryCount(paramString, paramMap);
	}
	/**
     * 根据微信身份标识查询用户，若不存在，自动创建新用户
     * @param openid
     * @return
     */
	@Override
    public	User	getUserByWxOpenid(String wxOpenid){
		if(wxOpenid==null||"".equals(wxOpenid)){
			return null;
		}
    	 User user = this.getObjByProperty("wx_openid", wxOpenid);
    	 if(user!=null){
    		 return user;
    	 }
    	 //openid的user不存在，需要创建新用户。
    	 user = createNewUser(wxOpenid);
    	 return user;
    }
	public User createNewUser(String wxOpenId){
		 String userName=UUID.randomUUID().toString();
		 User user = new User();
         user.setUserName(userName);
         user.setUserRole("BUYER");
         user.setCreatetime(new Date());
         user.setWx_openid(wxOpenId);
         user.setMobile(userName);
         user.setPassword(Md5Encrypt.md5(UUID.randomUUID().toString()).toLowerCase());
         HashMap<String,Object> params = new HashMap<String,Object>();
         //    user.setUserOfstore(Integer.valueOf(storeId));
         params.clear();
         params.put("type", "BUYER");
         List<Role> roles = this.roleService.query(
             "select obj from Role obj where obj.type=:type", params, -1, -1);
         user.getRoles().addAll(roles);
         if (this.configService.getSysConfig().isIntegral()) {
             user.setIntegral(this.configService.getSysConfig().getMemberRegister());
             this.save(user);
             IntegralLog log = new IntegralLog();
             log.setCreatetime(new Date());
             log.setContent("用户注册增加" + this.configService.getSysConfig().getMemberRegister()
                            + "分");
             log.setIntegral(this.configService.getSysConfig().getMemberRegister());
             log.setIntegral_user(user);
             log.setType("reg");
             this.integralLogService.save(log);
         } else {
             this.save(user);
         }
         Album album = new Album();
         album.setCreatetime(new Date());
         album.setAlbum_default(true);
         album.setAlbum_name("默认相册");
         album.setAlbum_sequence(-10000);
         album.setUser(user);
         this.albumService.save(album);
         /*
          * 买家注册时自动添加卖家为好友
          */
         /*   try{
            	 SnsFriend friend = new SnsFriend();
                 friend.setCreatetime(new Date());
                 friend.setFromUser(user);
                 friend.setToUser(this.storeService.getObjById(Long.valueOf(storeId)).getUser());
                 this.sndFriendService.save(friend);
            }catch(Exception e){
            	e.printStackTrace();
            }*/
         Store platStore = this.storeService.getObjByProperty("platform", true);//自营店铺
         //和自营店铺B0建立供采关系
         UserStoreRelation userStoreRelation = new UserStoreRelation();
         userStoreRelation.setUser(user);
         userStoreRelation.setStore(platStore);
         userStoreRelation.setStatus(2); //默认审核通过
         userStoreRelation.setCreatetime(new Date());
         userStoreRelationService.save(userStoreRelation);
         
         //调用用户接口
         String write2JsonStr = JsonUtil.write2JsonStr(user);
         sendReqAsync.sendMessageUtil(Constant.USER_INTEFACE_URL_ADD, write2JsonStr, "新增会员");
         return user;
          
	}

}
