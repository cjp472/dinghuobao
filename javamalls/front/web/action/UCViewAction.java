package com.javamalls.front.web.action;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.Md5Encrypt;
import com.javamalls.platform.domain.Album;
import com.javamalls.platform.domain.IntegralLog;
import com.javamalls.platform.domain.Role;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.service.IAlbumService;
import com.javamalls.platform.service.IIntegralLogService;
import com.javamalls.platform.service.IRoleService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserService;

@Controller
public class UCViewAction {
    private static final long   serialVersionUID         = -7377364931916922413L;
    public static boolean       IN_DISCUZ                = true;
    public static String        UC_CLIENT_VERSION        = "1.5.0";
    public static String        UC_CLIENT_RELEASE        = "20081031";
    public static boolean       API_DELETEUSER           = true;
    public static boolean       API_RENAMEUSER           = true;
    public static boolean       API_GETTAG               = true;
    public static boolean       API_SYNLOGIN             = true;
    public static boolean       API_SYNLOGOUT            = true;
    public static boolean       API_UPDATEPW             = true;
    public static boolean       API_UPDATEBADWORDS       = true;
    public static boolean       API_UPDATEHOSTS          = true;
    public static boolean       API_UPDATEAPPS           = true;
    public static boolean       API_UPDATECLIENT         = true;
    public static boolean       API_UPDATECREDIT         = true;
    public static boolean       API_GETCREDITSETTINGS    = true;
    public static boolean       API_GETCREDIT            = true;
    public static boolean       API_UPDATECREDITSETTINGS = true;
    public static String        API_RETURN_SUCCEED       = "1";
    public static String        API_RETURN_FAILED        = "-1";
    public static String        API_RETURN_FORBIDDEN     = "-2";
    @Autowired
    private IUserService        userService;
    @Autowired
    private IRoleService        roleService;
    @Autowired
    private ISysConfigService   configService;
    @Autowired
    private IIntegralLogService integralLogService;
    @Autowired
    private IAlbumService       albumService;

    protected long time() {
        return System.currentTimeMillis() / 1000L;
    }

    protected void jm_login(HttpServletRequest request, HttpServletResponse response,
                            Map<String, String> args) {
        boolean admin_login = CommUtil.null2Boolean(request.getSession(false).getAttribute(
            "admin_login"));
        if (!admin_login) {
            String userName = (String) args.get("username");
            String password = "";
            User user = this.userService.getObjByProperty("userName", userName);
            if (user != null) {
                password = user.getPassword();
            } else {
                user = new User();
                user.setUserName(userName);
                user.setUserRole("BUYER");
                user.setCreatetime(new Date());

                user.setPassword(Md5Encrypt.md5(password).toLowerCase());
                Map params = new HashMap();
                params.put("type", "BUYER");
                List<Role> roles = this.roleService.query(
                    "select obj from Role obj where obj.type=:type", params, -1, -1);
                user.getRoles().addAll(roles);
                if (this.configService.getSysConfig().isIntegral()) {
                    user.setIntegral(this.configService.getSysConfig().getMemberRegister());
                    this.userService.save(user);
                    IntegralLog log = new IntegralLog();
                    log.setCreatetime(new Date());
                    log.setContent("用户注册增加" + this.configService.getSysConfig().getMemberRegister()
                                   + "分");
                    log.setIntegral(this.configService.getSysConfig().getMemberRegister());
                    log.setIntegral_user(user);
                    log.setType("reg");
                    this.integralLogService.save(log);
                } else {
                    this.userService.save(user);
                }
                Album album = new Album();
                album.setCreatetime(new Date());
                album.setAlbum_default(true);
                album.setAlbum_name("默认相册");
                album.setAlbum_sequence(-10000);
                album.setUser(user);
                this.albumService.save(album);
            }
            String url = CommUtil.getURL(request) + "/jm_login.htm?username="
                         + CommUtil.encode(userName) + "&password=" + "jm_thid_login_" + password
                         + "&encode=true";
            try {
                response.sendRedirect(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void jm_logout(HttpServletRequest request, HttpServletResponse response,
                             Map<String, String> args) {
        String url = CommUtil.getURL(request) + "/jm_logout.htm";
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void jm_update_pws(HttpServletRequest request, HttpServletResponse response,
                                 Map<String, String> args) {
        User user = SecurityUserHolder.getCurrentUser();
    }
}
