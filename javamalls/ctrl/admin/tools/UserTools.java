package com.javamalls.ctrl.admin.tools;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.concurrent.SessionRegistry;
import org.springframework.stereotype.Component;

import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.service.IUserService;

@Component
public class UserTools {
    @Autowired
    private SessionRegistry sessionRegistry;
    @Autowired
    private IUserService    userSerivce;

    public List<User> query_user() {
        List<User> users = new ArrayList<User>();
        Object[] objs = this.sessionRegistry.getAllPrincipals();
        for (int i = 0; i < objs.length; i++) {
            User user = this.userSerivce
                .getObjByProperty("userName", CommUtil.null2String(objs[i]));

            users.add(user);
        }
        return users;
    }

    public boolean userOnLine(String userName) {
        boolean ret = false;
        List<User> users = query_user();
        for (User user : users) {
            if ((user != null) && (user.getUsername().equals(userName.trim()))) {
                ret = true;
            }
        }
        return ret;
    }
}
