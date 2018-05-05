package com.javamalls.base.security.support;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;

import com.javamalls.base.security.SecurityManager;
import com.javamalls.platform.domain.Res;
import com.javamalls.platform.domain.Role;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.service.IResService;
import com.javamalls.platform.service.IUserService;

public class SecurityManagerSupport implements UserDetailsService, SecurityManager {
    @Autowired
    private IUserService userService;
    @Autowired
    private IResService  resService;

    /**根据用户名加载用户和权限
     * @param data
     * @return
     * @throws UsernameNotFoundException
     * @throws DataAccessException
     * @see org.springframework.security.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
     */
    public UserDetails loadUserByUsername(String data) throws UsernameNotFoundException,
                                                      DataAccessException {
        String[] list = data.split(",");
        String userName = list[0];
        String loginRole = "user";
        String loginStore="0";
        if (list.length == 2) {
            loginRole = list[1];
        }
        if (list.length == 3) {
            loginRole = list[1];
            loginStore= list[2];
        }
        
        if(userName==null||"".equals(userName.trim())){
        	  throw new UsernameNotFoundException("User " + userName + " has no GrantedAuthority");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        String sql="select obj from User obj where obj.disabled=false ";
      
        
        
        if(loginStore!=null&&!"".equals(loginStore)&&"-1".equals(loginStore)){//平台管理员登陆
        	sql+=" and obj.userRole='ADMIN_BUYER_SELLER' and obj.userName =:userName ";
        	params.put("userName", userName);
        }else if(loginStore!=null&&!"".equals(loginStore)&&!"0".equals(loginStore)){//买家登录
        //	params.put("userOfstore", Integer.valueOf(loginStore));
        	params.put("userName", userName);
            params.put("mobile", userName);
        	sql+=" and  (obj.userRole='BUYER' or  obj.userRole='BUYER_SELLER') and (obj.userName =:userName or obj.mobile=:mobile) ";
        	//买家登陆从买家账号中取
        }else{//卖家登录
        	sql+=" and (obj.userName =:userName or obj.mobile=:mobile) and (obj.userRole='BUYER_SELLER' or obj.userRole='SELLER') ";
      	  	params.put("userName", userName);
            params.put("mobile", userName);
        }
        List<User> users = this.userService.query(
        		sql, params, 0, 1);
       
        if (users.isEmpty()) {
            throw new UsernameNotFoundException("User " + userName + " has no GrantedAuthority");
        }
        User user = (User) users.get(0);
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        if ((!user.getRoles().isEmpty()) && (user.getRoles() != null)) {
            Iterator<Role> roleIterator = user.getRoles().iterator();
            while (roleIterator.hasNext()) {
                Role role = (Role) roleIterator.next();
                if (loginRole.equalsIgnoreCase("ADMIN")) {
                    GrantedAuthority grantedAuthority = new GrantedAuthorityImpl(role.getRoleCode()
                        .toUpperCase());
                    authorities.add(grantedAuthority);
                } else if (!role.getType().equals("ADMIN")) {
                    GrantedAuthority grantedAuthority = new GrantedAuthorityImpl(role.getRoleCode()
                        .toUpperCase());
                    authorities.add(grantedAuthority);
                }
            }
        }
        GrantedAuthority[] auths = new GrantedAuthority[authorities.size()];
        user.setAuthorities((GrantedAuthority[]) authorities.toArray(auths));
        return user;
    }

    public Map<String, String> loadUrlAuthorities() {
        Map<String, String> urlAuthorities = new HashMap<String, String>();
        Map<String, String> params = new HashMap<String, String>();
        params.put("type", "URL");
        List<Res> urlResources = this.resService.query(
            "select obj from Res obj where obj.type = :type", params, -1, -1);
        for (Res res : urlResources) {
            urlAuthorities.put(res.getValue(), res.getRoleAuthorities());
        }
        return urlAuthorities;
    }
}
