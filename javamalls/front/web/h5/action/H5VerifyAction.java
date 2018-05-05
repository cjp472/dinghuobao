package com.javamalls.front.web.h5.action;

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
import com.javamalls.platform.domain.User;
import com.javamalls.platform.service.IAlbumService;
import com.javamalls.platform.service.IMobileVerifyCodeService;
import com.javamalls.platform.service.IRoleService;
import com.javamalls.platform.service.IUserService;

/**校验验证码、用户、手机号等相关操作
 *                       
 * @Filename: VerifyAction.java
 * @Version: 2.7.0
 * @Author: 刘杰
 * @Email: attay125@163.com
 *
 */
@Controller
public class H5VerifyAction {

    @Autowired
    private IUserService             userService;
    @Autowired
    private IMobileVerifyCodeService mobileverifycodeService;

    @Autowired
    private IRoleService             roleService;
    @Autowired
    private IAlbumService            albumService;

    /**
     * 校验验证码-h5
     * @param request
     * @param response
     * @param code
     * @param code_name
     */
    @RequestMapping({ "/store/{storeId}.htm/mobile/verify_number.htm" })
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

    @RequestMapping({ "/mobile/verify_number.htm" })
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
     * 校验邮箱-h5
     * @param request
     * @param response
     * @param email
     * @param id
     * @param storeId
     */
    @RequestMapping({ "/store/{storeId}.htm/mobile/verify_email.htm" })
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

    @RequestMapping({ "/mobile/verify_email.htm" })
    public void verify_email(HttpServletRequest request, HttpServletResponse response,
                             String email, String id) {
        boolean ret = true;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("email", email);
        params.put("id", CommUtil.null2Long(id));
        List<User> users = this.userService.query(
            "select obj from User obj where obj.email=:email and obj.id!=:id", params, -1, -1);
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
     * 校验手机-h5
     * @param request
     * @param response
     * @param mobile
     * @param id
     * @param storeId
     */
    @RequestMapping({ "/store/{storeId}.htm/mobile/verify_mobile.htm" })
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

    //验证码
    @RequestMapping({ "/store/{storeId}.htm/mobile/verify.htm" })
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

    /**
     * 买家注册验证用户名-h5
     * @param request
     * @param response
     * @param userName
     * @param id
     */
    @RequestMapping({ "/store/{storeId}.htm/mobile/verify_username.htm" })
    public void verify_buyer_username(HttpServletRequest request, HttpServletResponse response,
                                      String userName, String id) {
        boolean ret = true;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userName", userName);
        params.put("id", CommUtil.null2Long(id));
        params.put("mobile", userName);
        List<User> users = this.userService
            .query(
                "select obj from User obj where  (obj.userName=:userName or obj.mobile=:mobile)  and obj.id!=:id and obj.disabled=false ",
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

    @RequestMapping({ "/mobile/verify_username.htm" })
    public void verify_username(HttpServletRequest request, HttpServletResponse response,
                                String userName, String id) {
        boolean ret = true;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userName", userName);
        params.put("id", CommUtil.null2Long(id));
        List<User> users = this.userService
            .query("select obj from User obj where obj.userName=:userName and obj.id!=:id", params,
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

    //登录校验
    @RequestMapping({ "/store/{storeId}.htm/mobile/verify_username_pass.htm" })
    public void buyerverify_username_pass(HttpServletRequest request, HttpServletResponse response,
                                          String userName, String password) {
        boolean ret = true;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userName", userName);
        params.put("mobile", userName);
        List<User> users = this.userService
            .query(
                "select obj from User obj where  1=1 and  (obj.userName=:userName or obj.mobile=:mobile) and obj.disabled=false ",
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
                    "select obj from User obj where obj.disabled=false  and (obj.userName=:userName or obj.mobile=:mobile) and obj.password=:pass ",
                    params, -1, -1);
            if ((users1 != null) && (users1.size() > 0)) {
                /*      
                      //创建该用户

                      User user = new User();
                      user.setUserName(userName);
                      user.setUserRole("BUYER");
                      user.setCreatetime(new Date());
                      user.setMobile(userName);
                      user.setPassword(Md5Encrypt.md5(password).toLowerCase());
                
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
                      System.out.println("登录时创建新用户****************");*/
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
     * 验证短信验证码
     * @param request
     * @param response
     * @param code
     * @param code_name
     */
    @RequestMapping({ "/store/{storeId}.htm/mobile/verify_seller_sms_code.htm" })
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
     * 找回密码验证用户名-h5
     * @param request
     * @param response
     * @param userName
     * @param id
     */
    @RequestMapping({ "/store/{storeId}.htm/mobile/verify_forget_username.htm" })
    public void verify_forget_username(HttpServletRequest request, HttpServletResponse response,
                                       String userName) {
        boolean ret = false;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userName", userName);
        params.put("mobile", userName);
        List<User> users = this.userService
            .query(
                "select obj from User obj where  obj.disabled=false and obj.userRole='BUYER' and (obj.userName=:userName or obj.mobile=:mobile) ",
                params, -1, -1);
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

}
