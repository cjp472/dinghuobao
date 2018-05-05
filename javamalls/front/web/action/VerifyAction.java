package com.javamalls.front.web.action;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.Md5Encrypt;
import com.javamalls.platform.domain.MobileVerifyCode;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.SysConfig;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.service.IMobileVerifyCodeService;
import com.javamalls.platform.service.IStoreService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserService;

/**校验验证码、用户、手机号等相关操作
 *                       
 * @Filename: VerifyAction.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class VerifyAction {

    @Autowired
    private IUserService             userService;
    @Autowired
    private IStoreService            storeService;
    @Autowired
    private IMobileVerifyCodeService mobileverifycodeService;
    @Autowired
    private ISysConfigService        configService;

    @RequestMapping({ "/verify_number.htm" })
    public void validate_code(HttpServletRequest request, HttpServletResponse response,
                              String code, String code_name) {
        HttpSession session = request.getSession(false);
        String verify_number = "";
        if (CommUtil.null2String(code_name).equals("")) {
            verify_number = (String) session.getAttribute("verify_number");
        } else {
            verify_number = (String) session.getAttribute(code_name);
        }
        boolean ret = true;
        if ((verify_number != null) && (!verify_number.equals(""))
            && (!CommUtil.null2String(code.toUpperCase()).equals(verify_number))) {
            ret = false;
        }
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证短信验证码
     * @param request
     * @param response
     * @param code
     * @param code_name
     */
    @RequestMapping({ "/verify_sms_code.htm" })
    public void validate_sms_code(HttpServletRequest request, HttpServletResponse response,
                                  String mobile_verify_number, String mobile) {
        MobileVerifyCode mvc = this.mobileverifycodeService.getObjByProperty("mobile", mobile);
        boolean ret = true;
        if ((mvc != null) && (mvc.getCode().equalsIgnoreCase(mobile_verify_number))) {
            ret = true;
        } else {
            ret = false;
        }
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证短信验证码
     * @param request
     * @param response
     * @param code
     * @param code_name
     */
    @RequestMapping({ "/store/{storeId}.htm/verify_seller_sms_code.htm" })
    public void validate_seller_sms_code(HttpServletRequest request, HttpServletResponse response,
                                         String mobile_verify_number, String mobile) {
        MobileVerifyCode mvc = this.mobileverifycodeService.getObjByProperty("mobile", mobile);
        boolean ret = true;
        if ((mvc != null) && (mvc.getCode().equalsIgnoreCase(mobile_verify_number))) {
            ret = true;
        } else {
            ret = false;
        }
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证短信验证码
     * @param request
     * @param response
     * @param code
     * @param code_name
     */
    @RequestMapping({ "/verify_seller_sms_code.htm" })
    public void validate_seller_sms_code2(HttpServletRequest request, HttpServletResponse response,
                                          String mobile_verify_number, String mobile) {
        MobileVerifyCode mvc = this.mobileverifycodeService.getObjByProperty("mobile", mobile);
        boolean ret = true;
        if ((mvc != null) && (mvc.getCode().equalsIgnoreCase(mobile_verify_number))) {
            ret = true;
        } else {
            ret = false;
        }
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping({ "/store/{storeId}.htm/verify_number.htm" })
    public void buyer_validate_code(HttpServletRequest request, HttpServletResponse response,
                                    String code, String code_name) {
        HttpSession session = request.getSession(false);
        String verify_number = "";
        if (CommUtil.null2String(code_name).equals("")) {
            verify_number = (String) session.getAttribute("verify_number");
        } else {
            verify_number = (String) session.getAttribute(code_name);
        }
        boolean ret = true;
        if ((verify_number != null) && (!verify_number.equals(""))
            && (!CommUtil.null2String(code.toUpperCase()).equals(verify_number))) {
            ret = false;
        }
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证域名是否重复
     * @param request
     * @param response
     * @param code
     * @param code_name
     */
    @RequestMapping({ "/verify_domianName.htm" })
    public void verify_domianName(HttpServletRequest request, HttpServletResponse response,
                                  String domianName, String id) {
        boolean ret = true;
        Map<String, Object> params = new HashMap<String, Object>();
        if (domianName != null) {
            domianName = domianName.trim();
        }

        SysConfig sysConfig = this.configService.getSysConfig();

        params.put("domainName_info", domianName + sysConfig.getFront_web_after_path());//前缀
        params.put("id", CommUtil.null2Long(id));
        params.put("disabled", false);
        List<Store> users = this.storeService
            .query(
                "select obj from Store obj where obj.domainName_info=:domainName_info and obj.id!=:id and disabled=:disabled",
                params, -1, -1);
        if ((users != null) && (users.size() > 0)) {
            ret = false;
        }
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping({ "/verify_username.htm" })
    public void verify_username(HttpServletRequest request, HttpServletResponse response,
                                String userName, String id) {
        boolean ret = true;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userName", userName);
        params.put("id", CommUtil.null2Long(id));
        params.put("mobile", userName);
        List<User> users = this.userService
            .query(
                "select obj from User obj where obj.disabled=false  and (obj.userName=:userName or obj.mobile=:mobile) and obj.id!=:id",
                params, -1, -1);
        if ((users != null) && (users.size() > 0)) {
            ret = false;
        }
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 登录时验证
     * @param request
     * @param response
     * @param userName
     * @param id
     */
    @RequestMapping({ "/verify_username_pass.htm" })
    public void verify_username_pass(HttpServletRequest request, HttpServletResponse response,
                                     String userName, String password) {
        boolean ret = true;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userName", userName);
        params.put("mobile", userName);
        List<User> users = this.userService
            .query(
                "select obj from User obj where obj.disabled=false and (obj.userRole='BUYER_SELLER' or obj.userRole='SELLER') and (obj.userName=:userName or obj.mobile=:mobile)",
                params, -1, -1);
        if ((users != null) && (users.size() > 0)) {
            User user = users.get(0);
            if (user.getPassword().equals(Md5Encrypt.md5(password).toLowerCase())) {
                ret = true;
            } else {
                ret = false;
            }
        } else {
            ret = false;
        }
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping({ "/store/{storeId}.htm/verify_username_pass.htm" })
    public void buyerverify_username_pass(HttpServletRequest request, HttpServletResponse response,
                                          String userName, String password) {
        boolean ret = true;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userName", userName);

        params.put("mobile", userName);
        List<User> users = this.userService
            .query(
                "select obj from User obj where (obj.userRole='BUYER' or obj.userRole='BUYER_SELLER') and (obj.userName=:userName or obj.mobile=:mobile) and obj.disabled=false ",
                params, -1, -1);
        if ((users != null) && (users.size() > 0)) {
            User user = users.get(0);
            if (user.getPassword().equals(Md5Encrypt.md5(password).toLowerCase())) {
                ret = true;
            } else {
                ret = false;
            }
        } else {
            //如果在该供应商没有找到，则在全部范围内在找一下，如果有则为此供应商创建新用户
            params.clear();
            params.put("userName", userName);
            params.put("mobile", userName);
            params.put("pass", Md5Encrypt.md5(password).toLowerCase());
            List<User> users1 = this.userService
                .query(
                    "select obj from User obj where obj.disabled=false and obj.userRole='BUYER' and (obj.userName=:userName or obj.mobile=:mobile) and obj.password=:pass ",
                    params, -1, -1);
            if ((users1 != null) && (users1.size() > 0)) {
                /* //创建该用户

                 User user = new User();
                 user.setUserName(userName);
                 user.setUserRole("BUYER");
                 user.setCreatetime(new Date());
                 user.setMobile(userName);
                 user.setPassword(Md5Encrypt.md5(password).toLowerCase());
                 user.setUserOfstore(Integer.valueOf(storeId));
                 params.clear();
                 params.put("type", "BUYER");
                 List<Role> roles = this.roleService.query(
                     "select obj from Role obj where obj.type=:type", params, -1, -1);
                 user.getRoles().addAll(roles);
                
                 this.userService.save(user);
                 
                 Album album = new Album();
                 album.setCreatetime(new Date());
                 album.setAlbum_default(true);
                 album.setAlbum_name("默认相册");
                 album.setAlbum_sequence(-10000);
                 album.setUser(user);
                 this.albumService.save(album);
                 */
                ret = true;
            } else {
                ret = false;
            }

        }
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 买家注册验证用户名
     * @param request
     * @param response
     * @param userName
     * @param id
     */
    @RequestMapping({ "/store/{storeId}.htm/verify_buyer_username.htm" })
    public void verify_buyer_username(HttpServletRequest request, HttpServletResponse response,
                                      String userName, String id) {
        boolean ret = true;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userName", userName);
        params.put("id", CommUtil.null2Long(id));
        params.put("mobile", userName);
        List<User> users = this.userService
            .query(
                "select obj from User obj where obj.disabled=false and (obj.userName=:userName or obj.mobile=:mobile)  and obj.id!=:id ",
                params, -1, -1);
        if ((users != null) && (users.size() > 0)) {
            ret = false;
        }

        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping({ "/verify_buyer_username.htm" })
    public void verify_buyer_username2(HttpServletRequest request, HttpServletResponse response,
                                       String userName, String id) {
        boolean ret = true;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userName", userName);
        params.put("id", CommUtil.null2Long(id));
        params.put("mobile", userName);
        List<User> users = this.userService
            .query(
                "select obj from User obj where  (obj.userName=:userName or obj.mobile=:mobile)  and obj.id!=:id and obj.disabled=false",
                params, -1, -1);
        if ((users != null) && (users.size() > 0)) {
            ret = false;
        }

        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证按店铺区分
     * @param request
     * @param response
     * @param email
     * @param id
     */
    @RequestMapping({ "/verify_email.htm" })
    public void verify_email(HttpServletRequest request, HttpServletResponse response,
                             String email, String id, String userOfstore) {
        boolean ret = true;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("email", email);
        params.put("id", CommUtil.null2Long(id));

        List<User> users = this.userService
            .query(
                "select obj from User obj where  obj.disabled=false and obj.email=:email and (obj.userRole='BUYER_SELLER' or obj.userRole='SELLER') and obj.id!=:id ",
                params, -1, -1);
        if ((users != null) && (users.size() > 0)) {
            ret = false;
        }
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping({ "/store/{storeId}.htm/verify_buyer_email.htm" })
    public void verify_buyer_email(HttpServletRequest request, HttpServletResponse response,
                                   String email, String id) {
        boolean ret = true;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("email", email);
        params.put("id", CommUtil.null2Long(id));

        List<User> users = this.userService
            .query(
                "select obj from User obj where obj.email=:email and obj.id!=:id and obj.disabled=false",
                params, -1, -1);
        if ((users != null) && (users.size() > 0)) {
            ret = false;
        }
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping({ "/verify_buyer_email.htm" })
    public void verify_buyer_email2(HttpServletRequest request, HttpServletResponse response,
                                    String email, String id) {
        boolean ret = true;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("email", email);
        params.put("id", CommUtil.null2Long(id));

        List<User> users = this.userService
            .query(
                "select obj from User obj where obj.email=:email and obj.id!=:id and obj.disabled=false",
                params, -1, -1);
        if ((users != null) && (users.size() > 0)) {
            ret = false;
        }
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping({ "/verify_storename.htm" })
    public void verify_storename(HttpServletRequest request, HttpServletResponse response,
                                 String store_name, String id) {
        boolean ret = true;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("store_name", store_name);
        params.put("id", CommUtil.null2Long(id));
        List<Store> users = this.storeService.query(
            "select obj from Store obj where obj.store_name=:store_name and obj.id!=:id", params,
            -1, -1);
        if ((users != null) && (users.size() > 0)) {
            ret = false;
        }
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 订货买家验证
     * @param request
     * @param response
     * @param mobile
     * @param id
     * @param userOfstore
     */
    @RequestMapping({ "/store/{storeId}.htm/verify_buyer_mobile.htm" })
    public void verify_mobile(HttpServletRequest request, HttpServletResponse response,
                              String mobile, String id) {
        boolean ret = true;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mobile", mobile);
        params.put("id", CommUtil.null2Long(id));
        List<User> users = this.userService
            .query(
                "select obj from User obj where obj.disabled=false and obj.mobile=:mobile and obj.id!=:id ",
                params, -1, -1);
        if ((users != null) && (users.size() > 0)) {
            ret = false;
        }
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 供货商验证
     * @param request
     * @param response
     * @param mobile
     * @param id
     * @param userOfstore
     */
    @RequestMapping({ "/verify_buyer_mobile.htm" })
    public void verify_buyer_mobile(HttpServletRequest request, HttpServletResponse response,
                                    String mobile, String id, String userOfstore) {
        boolean ret = true;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mobile", mobile);
        params.put("id", CommUtil.null2Long(id));

        //  System.out.println(userOfstore+"+=============");
        List<User> users = this.userService
            .query(
                "select obj from User obj where obj.disabled=false and (obj.userRole='BUYER_SELLER' or obj.userRole='SELLER') and  obj.mobile=:mobile and obj.id!=:id",
                params, -1, -1);
        if ((users != null) && (users.size() > 0)) {
            ret = false;
        }
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 找回密码验证用户名-买家
     * @param request
     * @param response
     * @param userName
     * @param id
     */
    @RequestMapping({ "/store/{storeId}.htm/verify_forget_username.htm" })
    public void verify_forget_username(HttpServletRequest request, HttpServletResponse response,
                                       String userName) {
        boolean ret = false;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userName", userName);
        params.put("mobile", userName);
        List<User> users = this.userService
            .query(
                "select obj from User obj where obj.disabled=false and (obj.userRole='BUYER' or obj.userRole='BUYER_SELLER') and (obj.userName=:userName or obj.mobile=:mobile) ",
                params, 0, 1);
        if ((users != null) && (users.size() > 0)) {
            ret = true;//存在该用户
        }

        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 找回密码验证用户名-买家
     * @param request
     * @param response
     * @param userName
     * @param id
     */
    @RequestMapping({ "/verify_seller_forget_username.htm" })
    public void verify_seller_forget_username(HttpServletRequest request,
                                              HttpServletResponse response, String userName) {
        boolean ret = false;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userName", userName);
        params.put("mobile", userName);
        Long count = this.userService
            .queryCount(
                "select count(obj) from User obj where obj.disabled=false and (obj.userRole='BUYER_SELLER' or obj.userRole='SELLER') and (obj.userName=:userName or obj.mobile=:mobile)",
                params);
        if ((count != null) && (count > 0)) {
            ret = true;//存在该用户
        }

        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = { "/verify.htm", "/store/{storeId}.htm/verify.htm" })
    public void verify(HttpServletRequest request, HttpServletResponse response, String name)
                                                                                             throws IOException {
        //设置页面不缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");

        HttpSession session = request.getSession(false);

        int width = 73;
        int height = 27;
        BufferedImage image = new BufferedImage(width, height, 1);

        Graphics g = image.getGraphics();

        Random random = new Random();

        g.setColor(getRandColor(100, 200));
        g.fillRect(0, 0, width, height);

        g.setFont(new Font("Arial", 0, 24));

        g.setColor(getRandColor(200, 250));
        for (int i = 0; i < 188; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }
        String sRand = "";
        for (int i = 0; i < 4; i++) {
            String rand = CommUtil.randomInt(1).toUpperCase();
            sRand = sRand + rand;

            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random
                .nextInt(110)));
            g.drawString(rand, 13 * i + 6, 24);
        }
        if (CommUtil.null2String(name).equals("")) {
            session.setAttribute("verify_number", sRand);
        } else {
            session.setAttribute(name, sRand);
        }
        g.dispose();
        ServletOutputStream responseOutputStream = response.getOutputStream();

        ImageIO.write(image, "JPEG", responseOutputStream);

        responseOutputStream.flush();
        responseOutputStream.close();
    }

    //给定范围获得随机颜色
    private Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    //验证员工的客服代码 不能重复
    @RequestMapping({ "/verify_sale_code.htm" })
    public void verify_sale_code(HttpServletRequest request, HttpServletResponse response,
                                 String cus_ser_code, String id, Long storeId) {
        boolean ret = true;
        Map<String, Object> params = new HashMap<String, Object>();

        if (CommUtil.isNullOrEmpty(storeId) || CommUtil.isNullOrEmpty(cus_ser_code)
            || CommUtil.isNullOrEmpty(cus_ser_code)) {
            ret = false;
        } else {
        	if(id!=null&&!"".equals(id)){
        		User user = userService.getObjById(CommUtil.null2Long(id));
                params.put("cus_ser_code", cus_ser_code);
                params.put("id", CommUtil.null2Long(id));
                List<User> users = this.userService
                    .query(
                        "select obj from User obj where obj.disabled=false  and obj.cus_ser_code=:cus_ser_code  and obj.id!=:id and obj.parent.id is not null",
                        params, -1, -1);
                if ((users != null) && (users.size() > 0)) {
                    ret = false;
                }
        	}else{
        		User user = userService.getObjById(CommUtil.null2Long(id));
                params.put("cus_ser_code", cus_ser_code);
                List<User> users = this.userService
                    .query(
                        "select obj from User obj where obj.disabled=false  and obj.cus_ser_code=:cus_ser_code  and obj.parent.id is not null",
                        params, -1, -1);
                if ((users != null) && (users.size() > 0)) {
                    ret = false;
                }
        	}
            
        }

        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
