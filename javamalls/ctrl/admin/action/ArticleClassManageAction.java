package com.javamalls.ctrl.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.easyjf.beans.BeanUtils;
import com.easyjf.beans.BeanWrapper;
import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.WebForm;
import com.javamalls.platform.domain.Accessory;
import com.javamalls.platform.domain.ArticleClass;
import com.javamalls.platform.domain.query.ArticleClassQueryObject;
import com.javamalls.platform.service.IAccessoryService;
import com.javamalls.platform.service.IArticleClassService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

/**文章分类
 *                       
 * @Filename: ArticleClassManageAction.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class ArticleClassManageAction {
    @Autowired
    private ISysConfigService    configService;
    @Autowired
    private IUserConfigService   userConfigService;
    @Autowired
    private IAccessoryService    accessoryService;
    @Autowired
    private IArticleClassService articleClassService;

    @SecurityMapping(title = "文章分类列表", value = "/admin/articleclass_list.htm*", rtype = "admin", rname = "文章分类", rcode = "article_class", rgroup = "网站")
    @RequestMapping({ "/admin/articleclass_list.htm" })
    public ModelAndView list(HttpServletRequest request, HttpServletResponse response,
                             String currentPage, String orderBy, String orderType) {
        ModelAndView mv = new JModelAndView("admin/blue/articleclass_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        ArticleClassQueryObject qo = new ArticleClassQueryObject(currentPage, mv, orderBy,
            orderType);
        qo.addQuery("obj.parent is null", null);
        qo.setOrderBy("sequence");
        qo.setOrderType("asc");
        IPageList pList = this.articleClassService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        return mv;
    }

    @SecurityMapping(title = "文章分类添加", value = "/admin/articleclass_add.htm*", rtype = "admin", rname = "文章分类", rcode = "article_class", rgroup = "网站")
    @RequestMapping({ "/admin/articleclass_add.htm" })
    public ModelAndView add(HttpServletRequest request, HttpServletResponse response, String pid) {
        ModelAndView mv = new JModelAndView("admin/blue/articleclass_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        List<ArticleClass> acs = this.articleClassService.query(
            "select obj from ArticleClass obj where obj.parent is null order by obj.sequence asc",
            null, -1, -1);
        if ((pid != null) && (!pid.equals(""))) {
            ArticleClass obj = new ArticleClass();
            obj.setParent(this.articleClassService.getObjById(Long.valueOf(Long.parseLong(pid))));
            mv.addObject("obj", obj);
        }
        mv.addObject("acs", acs);
        return mv;
    }

    @SecurityMapping(title = "文章分类编辑", value = "/admin/articleclass_edit.htm*", rtype = "admin", rname = "文章分类", rcode = "article_class", rgroup = "网站")
    @RequestMapping({ "/admin/articleclass_edit.htm" })
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response, String id,
                             String currentPage) {
        ModelAndView mv = new JModelAndView("admin/blue/articleclass_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if ((id != null) && (!id.equals(""))) {
            ArticleClass articleClass = this.articleClassService.getObjById(Long.valueOf(Long
                .parseLong(id)));
            List<ArticleClass> acs = this.articleClassService
                .query(
                    "select obj from ArticleClass obj where obj.parent is null order by obj.sequence asc",
                    null, -1, -1);
            mv.addObject("acs", acs);
            mv.addObject("obj", articleClass);
            mv.addObject("currentPage", currentPage);
            mv.addObject("edit", Boolean.valueOf(true));
        }
        return mv;
    }

    /**
     * 文章分类保存
     * 
     * @since 2014-7-11 添加分类缩略图
     * @param request
     * @param response
     * @param id
     * @param pid
     * @param currentPage
     * @param cmd
     * @param list_url
     * @param add_url
     * @return
     */
    @SecurityMapping(title = "文章分类保存", value = "/admin/articleclass_save.htm*", rtype = "admin", rname = "文章分类", rcode = "article_class", rgroup = "网站")
    @RequestMapping({ "/admin/articleclass_save.htm" })
    public ModelAndView save(HttpServletRequest request, HttpServletResponse response, String id,
                             String pid, String currentPage, String cmd, String list_url,
                             String add_url) {
        WebForm wf = new WebForm();
        ArticleClass articleClass = null;
        if (id.equals("")) {
            articleClass = (ArticleClass) wf.toPo(request, ArticleClass.class);
            articleClass.setCreatetime(new Date());
        } else {
            ArticleClass obj = this.articleClassService
                .getObjById(Long.valueOf(Long.parseLong(id)));
            articleClass = (ArticleClass) wf.toPo(request, obj);
        }
        if ((pid != null) && (!pid.equals(""))) {
            ArticleClass parent = this.articleClassService.getObjById(Long.valueOf(Long
                .parseLong(pid)));
            articleClass.setParent(parent);
        }

        // 保存缩略图
        String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
        String saveFilePathName = request.getSession().getServletContext().getRealPath("/")
                                  + uploadFilePath + File.separator + "articleClassThum";
        Map<String, Object> map = new HashMap<String, Object>();
        String fileName = "";
        if (articleClass.getAcc() != null) {
            fileName = articleClass.getAcc().getName();
        }
        try {
            map = CommUtil.saveFileToServer(request, "acc", saveFilePathName, fileName, null);
            Accessory acc = null;
            if (fileName.equals("")) {
                if (map.get("fileName") != "") {
                    acc = new Accessory();
                    acc.setName(CommUtil.null2String(map.get("fileName")));
                    acc.setExt(CommUtil.null2String(map.get("mime")));
                    acc.setSize(CommUtil.null2Float(map.get("fileSize")));
                    acc.setPath(uploadFilePath + "/articleClassThum");
                    acc.setWidth(CommUtil.null2Int(map.get("width")));
                    acc.setHeight(CommUtil.null2Int(map.get("height")));
                    acc.setCreatetime(new Date());
                    this.accessoryService.save(acc);
                    articleClass.setAcc(acc);
                }
            } else if (map.get("fileName") != "") {
                acc = articleClass.getAcc();
                acc.setName(CommUtil.null2String(map.get("fileName")));
                acc.setExt(CommUtil.null2String(map.get("mime")));
                acc.setSize(CommUtil.null2Float(map.get("fileSize")));
                acc.setPath(uploadFilePath + "/articleClassThum");
                acc.setWidth(CommUtil.null2Int(map.get("width")));
                acc.setHeight(CommUtil.null2Int(map.get("height")));
                acc.setCreatetime(new Date());
                this.accessoryService.update(acc);
                articleClass.setAcc(acc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (id.equals("")) {
            this.articleClassService.save(articleClass);
        } else {
            this.articleClassService.update(articleClass);
        }
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("list_url", list_url + "?currentPage=" + currentPage);
        mv.addObject("op_title", "保存文章分类成功");
        if (add_url != null) {
            mv.addObject("add_url", add_url + "?pid=" + pid);
        }
        return mv;
    }

    private Set<Long> genericIds(ArticleClass ac) {
        Set<Long> ids = new HashSet<Long>();
        ids.add(ac.getId());
        for (ArticleClass child : ac.getChilds()) {
            Set<Long> cids = genericIds(child);
            for (Long cid : cids) {
                ids.add(cid);
            }
            ids.add(child.getId());
        }
        return ids;
    }

    @SecurityMapping(title = "文章分类删除", value = "/admin/articleclass_del.htm*", rtype = "admin", rname = "文章分类", rcode = "article_class", rgroup = "网站")
    @RequestMapping({ "/admin/articleclass_del.htm" })
    public String delete(HttpServletRequest request, String mulitId) {
        String[] ids = mulitId.split(",");
        for (String id : ids) {
            if (!id.equals("")) {
                Set<Long> list = genericIds(this.articleClassService.getObjById(Long.valueOf(Long
                    .parseLong(id))));
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("ids", list);
                List<ArticleClass> acs = this.articleClassService
                    .query(
                        "select obj from ArticleClass obj where obj.id in (:ids) order by obj.level desc",
                        params, -1, -1);
                for (ArticleClass ac : acs) {
                    ac.setParent(null);
                    this.articleClassService.delete(ac.getId());
                }
            }
        }
        return "redirect:articleclass_list.htm";
    }

    @SecurityMapping(title = "文章下级分类", value = "/admin/articleclass_data.htm*", rtype = "admin", rname = "文章分类", rcode = "article_class", rgroup = "网站")
    @RequestMapping({ "/admin/articleclass_data.htm" })
    public ModelAndView articleclass_data(HttpServletRequest request, HttpServletResponse response,
                                          String pid, String currentPage) {
        ModelAndView mv = new JModelAndView("admin/blue/articleclass_data.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("pid", Long.valueOf(Long.parseLong(pid)));
        List<ArticleClass> acs = this.articleClassService.query(
            "select obj from ArticleClass obj where obj.parent.id =:pid", map, -1, -1);
        mv.addObject("acs", acs);
        mv.addObject("currentPage", currentPage);
        return mv;
    }

    @SecurityMapping(title = "文章分类AJAX更新", value = "/admin/articleclass_ajax.htm*", rtype = "admin", rname = "文章分类", rcode = "article_class", rgroup = "网站")
    @RequestMapping({ "/admin/articleclass_ajax.htm" })
    public void ajax(HttpServletRequest request, HttpServletResponse response, String id,
                     String fieldName, String value) throws ClassNotFoundException {
        ArticleClass ac = this.articleClassService.getObjById(Long.valueOf(Long.parseLong(id)));
        Field[] fields = ArticleClass.class.getDeclaredFields();
        BeanWrapper wrapper = new BeanWrapper(ac);
        Object val = null;
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                Class clz = Class.forName("java.lang.String");
                if (field.getType().getName().equals("int")) {
                    clz = Class.forName("java.lang.Integer");
                }
                if (field.getType().getName().equals("boolean")) {
                    clz = Class.forName("java.lang.Boolean");
                }
                if (!value.equals("")) {
                    val = BeanUtils.convertType(value, clz);
                } else {
                    val = Boolean.valueOf(!CommUtil.null2Boolean(wrapper
                        .getPropertyValue(fieldName)));
                }
                wrapper.setPropertyValue(fieldName, val);
            }
        }
        this.articleClassService.update(ac);
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(val.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SecurityMapping(title = "文章分类AJAX校验名称", value = "/admin/articleclass_verify.htm*", rtype = "admin", rname = "文章分类", rcode = "article_class", rgroup = "网站")
    @RequestMapping({ "/admin/articleclass_verify.htm" })
    public void articleclass_verify(HttpServletRequest request, HttpServletResponse response,
                                    String className, String id) {
        boolean ret = true;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("className", className);
        params.put("id", CommUtil.null2Long(id));
        List<ArticleClass> gcs = this.articleClassService.query(
            "select obj from ArticleClass obj where obj.className=:className and obj.id!=:id",
            params, -1, -1);
        if ((gcs != null) && (gcs.size() > 0)) {
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
}
