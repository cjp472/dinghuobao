package com.javamalls.ctrl.seller.action;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.constant.Constant;
import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.WebForm;
import com.javamalls.payment.chinabank.h5.util.JsonUtil;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.UserGoodsClass;
import com.javamalls.platform.domain.query.UserGoodsClassQueryObject;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserGoodsClassService;
import com.javamalls.platform.vo.RemoveIdJsonVo;
import com.javamalls.platform.vo.UserGoodsClassJsonVo;
import com.utils.SendReqAsync;

@Controller
public class GoodsClassSellerAction {
    @Autowired
    private ISysConfigService      configService;
    @Autowired
    private IUserConfigService     userConfigService;
    @Autowired
    private IUserGoodsClassService usergoodsclassService;
    @Autowired
    private SendReqAsync           sendReqAsync;

    @SecurityMapping(title = "卖家商品分类列表", value = "/seller/usergoodsclass_list.htm*", rtype = "seller", rname = "商品分类", rcode = "usergoodsclass_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/usergoodsclass_list.htm" })
    public ModelAndView usergoodsclass_list(HttpServletRequest request,
                                            HttpServletResponse response, String currentPage,
                                            String orderBy, String orderType) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/usergoodsclass_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        String params = "";
        UserGoodsClassQueryObject qo = new UserGoodsClassQueryObject(currentPage, mv, orderBy,
            orderType);
        qo.setPageSize(Integer.valueOf(20));
        WebForm wf = new WebForm();
        wf.toQueryPo(request, qo, UserGoodsClass.class, mv);
        qo.addQuery("obj.parent.id is null", null);
        qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");
        qo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser()
            .getId()), "=");
        qo.setOrderBy("sequence");
        qo.setOrderType("asc");
        IPageList pList = this.usergoodsclassService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/seller/usergoodsclass_list.htm", "", params,
            pList, mv);
        return mv;
    }

    @SecurityMapping(title = "卖家商品分类保存", value = "/seller/usergoodsclass_save.htm*", rtype = "seller", rname = "商品分类", rcode = "usergoodsclass_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/usergoodsclass_save.htm" })
    public String usergoodsclass_save(HttpServletRequest request, HttpServletResponse response,
                                      String id, String pid) {
        WebForm wf = new WebForm();
        UserGoodsClass usergoodsclass = null;
        if (id.equals("")) {
            usergoodsclass = (UserGoodsClass) wf.toPo(request, UserGoodsClass.class);
            usergoodsclass.setCreatetime(new Date());
        } else {
            UserGoodsClass obj = this.usergoodsclassService.getObjById(Long.valueOf(Long
                .parseLong(id)));
            usergoodsclass = (UserGoodsClass) wf.toPo(request, obj);
        }
        usergoodsclass.setUser(SecurityUserHolder.getCurrentUser());
        if (!pid.equals("")) {
            UserGoodsClass parent = this.usergoodsclassService.getObjById(Long.valueOf(Long
                .parseLong(pid)));
            usergoodsclass.setParent(parent);
        }
        boolean ret = true;
        if (id.equals("")) {
            ret = this.usergoodsclassService.save(usergoodsclass);

            //服装接口
            UserGoodsClassJsonVo vo = new UserGoodsClassJsonVo();
            vo.setClassName(usergoodsclass.getClassName());
            vo.setCreatetime(usergoodsclass.getCreatetime());
            vo.setDisabled(usergoodsclass.isDisabled());
            vo.setDisplay(usergoodsclass.isDisplay());
            vo.setId(usergoodsclass.getId());
            vo.setLevel(usergoodsclass.getLevel());
            if (usergoodsclass.getParent() != null) {
                vo.setParent_id(usergoodsclass.getParent().getId());
            }

            vo.setSequence(usergoodsclass.getSequence());
            vo.setUser_id(usergoodsclass.getUser().getId());
            String write2JsonStr = JsonUtil.write2JsonStr(vo);
            sendReqAsync
                .sendMessageUtil(Constant.STORE_GOODSCLASS_URL_ADD, write2JsonStr, "新增商品分类");

        } else {
            ret = this.usergoodsclassService.update(usergoodsclass);

            //服装接口
            UserGoodsClassJsonVo vo = new UserGoodsClassJsonVo();
            vo.setClassName(usergoodsclass.getClassName());
            vo.setCreatetime(usergoodsclass.getCreatetime());
            vo.setDisabled(usergoodsclass.isDisabled());
            vo.setDisplay(usergoodsclass.isDisplay());
            vo.setId(usergoodsclass.getId());
            vo.setLevel(usergoodsclass.getLevel());
            if (usergoodsclass.getParent() != null) {
                vo.setParent_id(usergoodsclass.getParent().getId());
            }

            vo.setSequence(usergoodsclass.getSequence());
            vo.setUser_id(usergoodsclass.getUser().getId());
            String write2JsonStr = JsonUtil.write2JsonStr(vo);
            sendReqAsync.sendMessageUtil(Constant.STORE_GOODSCLASS_URL_EDIT, write2JsonStr,
                "修改商品分类");
        }
        return "redirect:usergoodsclass_list.htm";
    }

    @SecurityMapping(title = "卖家商品分类删除", value = "/seller/usergoodsclass_del.htm*", rtype = "seller", rname = "商品分类", rcode = "usergoodsclass_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/usergoodsclass_del.htm" })
    public String usergoodsclass_del(HttpServletRequest request, String mulitId) {
        String[] ids = mulitId.split(",");
        for (String id : ids) {
            if (!id.equals("")) {
                UserGoodsClass usergoodsclass = this.usergoodsclassService.getObjById(Long
                    .valueOf(Long.parseLong(id)));
                List<UserGoodsClass> userGoodsClassList = usergoodsclass.getChilds();
                List<RemoveIdJsonVo> dellist = new ArrayList<RemoveIdJsonVo>();

                usergoodsclass.setDisabled(true);
                this.usergoodsclassService.update(usergoodsclass);
                RemoveIdJsonVo vo = new RemoveIdJsonVo();
                vo.setId(usergoodsclass.getId());
                dellist.add(vo);

                for (UserGoodsClass userGoodsClass2 : userGoodsClassList) {
                    userGoodsClass2.setDisabled(true);
                    this.usergoodsclassService.update(userGoodsClass2);

                    RemoveIdJsonVo vo2 = new RemoveIdJsonVo();
                    vo2.setId(userGoodsClass2.getId());
                    dellist.add(vo2);
                }
                if (dellist != null && dellist.size() > 0) {
                    String write2JsonStr = JsonUtil.write2JsonStr(dellist);
                    sendReqAsync.sendMessageUtil(Constant.STORE_GOODSCLASS_URL_DEL, write2JsonStr,
                        "删除商品分类");

                }
            }
        }
        return "redirect:usergoodsclass_list.htm";
    }

    @SecurityMapping(title = "新增卖家商品分类", value = "/seller/address_add.htm*", rtype = "seller", rname = "商品分类", rcode = "usergoodsclass_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/usergoodsclass_add.htm" })
    public ModelAndView usergoodsclass_add(HttpServletRequest request,
                                           HttpServletResponse response, String currentPage,
                                           String pid) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/usergoodsclass_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Map map = new HashMap();
        map.put("uid", SecurityUserHolder.getCurrentUser().getId());
        List<UserGoodsClass> ugcs = this.usergoodsclassService
            .query(
                "select obj from UserGoodsClass obj where obj.parent.id is null and obj.disabled=false and obj.user.id = :uid order by obj.sequence asc",
                map, -1, -1);
        if (!CommUtil.null2String(pid).equals("")) {
            UserGoodsClass parent = this.usergoodsclassService.getObjById(CommUtil.null2Long(pid));
            UserGoodsClass obj = new UserGoodsClass();
            obj.setParent(parent);
            mv.addObject("obj", obj);
        }
        mv.addObject("ugcs", ugcs);
        mv.addObject("currentPage", currentPage);
        return mv;
    }

    @SecurityMapping(title = "编辑卖家商品分类", value = "/seller/usergoodsclass_edit.htm*", rtype = "seller", rname = "商品分类", rcode = "usergoodsclass_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/usergoodsclass_edit.htm" })
    public ModelAndView usergoodsclass_edit(HttpServletRequest request,
                                            HttpServletResponse response, String currentPage,
                                            String id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/usergoodsclass_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("disabled", false);
        map.put("user_id", SecurityUserHolder.getCurrentUser().getId());
        List<UserGoodsClass> ugcs = this.usergoodsclassService
            .query(
                "select obj from UserGoodsClass obj where obj.parent.id is null and obj.disabled=:disabled and obj.user.id=:user_id order by obj.sequence asc",
                map, -1, -1);
        UserGoodsClass obj = this.usergoodsclassService.getObjById(CommUtil.null2Long(id));
        mv.addObject("obj", obj);
        mv.addObject("ugcs", ugcs);
        mv.addObject("currentPage", currentPage);
        return mv;
    }

    @RequestMapping({ "/seller/usergoodsclass_import.htm" })
    @ResponseBody
    public void usergoodsclass_import(HttpServletResponse response,
                                      @RequestParam(value = "excelFile") MultipartFile excelFile) {
        int ret = 0;
        User user = SecurityUserHolder.getCurrentUser();
        if (user != null) {
            ret = usergoodsclassService.importGoodClass(user, excelFile);
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

    @RequestMapping(value = "/seller/usergoodsclass_download.htm")
    public void usergoodsclass_download(HttpServletRequest request, HttpServletResponse response) {
        try {
            String filepath = ((Object) request).getServletContext().getRealPath(
                "/resources/download/" + "分类模板.zip");
            //设置下载的参数信息
            response.setContentType("application/force-download");
            String fileName = URLEncoder.encode("分类模板.zip", "UTF-8");
            response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
            //获取文件输入流和输出流
            FileInputStream fis = new FileInputStream(filepath);
            OutputStream fos = response.getOutputStream();
            byte[] temp = new byte[1024];
            int length = -1;
            do {
                length = fis.read(temp);
                if (length != -1) {
                    fos.write(temp, 0, length);
                }
            } while (length != -1);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
