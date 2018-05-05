package com.javamalls.ctrl.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.WebForm;
import com.javamalls.platform.domain.Accessory;
import com.javamalls.platform.domain.Article;
import com.javamalls.platform.domain.ArticleClass;
import com.javamalls.platform.domain.query.ArticleQueryObject;
import com.javamalls.platform.service.IAccessoryService;
import com.javamalls.platform.service.IArticleClassService;
import com.javamalls.platform.service.IArticleService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

/**文章管理
 *                       
 * @Filename: ArticleManageAction.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class ArticleManageAction {
    @Autowired
    private ISysConfigService    configService;
    @Autowired
    private IUserConfigService   userConfigService;
    @Autowired
    private IArticleService      articleService;
    @Autowired
    private IAccessoryService    accessoryService;
    @Autowired
    private IArticleClassService articleClassService;

    @SecurityMapping(title = "文章列表", value = "/admin/article_list.htm*", rtype = "admin", rname = "文章管理", rcode = "article", rgroup = "网站")
    @RequestMapping({ "/admin/article_list.htm" })
    public ModelAndView list(HttpServletRequest request, HttpServletResponse response,
                             String currentPage, String orderBy, String orderType) {
        ModelAndView mv = new JModelAndView("admin/blue/article_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        String params = "";
        ArticleQueryObject qo = new ArticleQueryObject(currentPage, mv, orderBy, orderType);
        WebForm wf = new WebForm();
        wf.toQueryPo(request, qo, Article.class, mv);
        IPageList pList = this.articleService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/admin/article_list.htm", "", params, pList, mv);
        return mv;
    }

    @SecurityMapping(title = "文章添加", value = "/admin/article_add.htm*", rtype = "admin", rname = "文章管理", rcode = "article", rgroup = "网站")
    @RequestMapping({ "/admin/article_add.htm" })
    public ModelAndView add(HttpServletRequest request, HttpServletResponse response,
                            String currentPage, String class_id) {
        ModelAndView mv = new JModelAndView("admin/blue/article_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        List<ArticleClass> acs = this.articleClassService
            .query(
                "select obj from ArticleClass obj where obj.parent.id is null order by obj.sequence asc",
                null, -1, -1);
        Article obj = new Article();
        obj.setDisplay(true);
        if ((class_id != null) && (!class_id.equals(""))) {
            obj.setArticleClass(this.articleClassService.getObjById(Long.valueOf(Long
                .parseLong(class_id))));
        }
        mv.addObject("obj", obj);
        mv.addObject("acs", acs);
        mv.addObject("currentPage", currentPage);
        return mv;
    }

    @SecurityMapping(title = "文章编辑", value = "/admin/article_edit.htm*", rtype = "admin", rname = "文章管理", rcode = "article", rgroup = "网站")
    @RequestMapping({ "/admin/article_edit.htm" })
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response, String id,
                             String currentPage) {
        ModelAndView mv = new JModelAndView("admin/blue/article_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if ((id != null) && (!id.equals(""))) {
            Article article = this.articleService.getObjById(Long.valueOf(Long.parseLong(id)));
            List<ArticleClass> acs = this.articleClassService
                .query(
                    "select obj from ArticleClass obj where obj.parent.id is null order by obj.sequence asc",
                    null, -1, -1);
            mv.addObject("acs", acs);
            mv.addObject("obj", article);
            mv.addObject("currentPage", currentPage);
            mv.addObject("edit", Boolean.valueOf(true));
        }
        return mv;
    }

    /**
     * 文章保存
     * 
     * @since 2014-7-11 添加文章缩略图
     * @param request
     * @param response
     * @param id
     * @param currentPage
     * @param cmd
     * @param list_url
     * @param add_url
     * @param class_id
     * @param content
     * @return
     */
    @SecurityMapping(title = "文章保存", value = "/admin/article_save.htm*", rtype = "admin", rname = "文章管理", rcode = "article", rgroup = "网站")
    @RequestMapping({ "/admin/article_save.htm" })
    public ModelAndView save(HttpServletRequest request, HttpServletResponse response, String id,
                             String currentPage, String cmd, String list_url, String add_url,
                             String class_id, String content) {
        WebForm wf = new WebForm();
        Article article = null;
        if (id.equals("")) {
            article = (Article) wf.toPo(request, Article.class);
            article.setCreatetime(new Date());
        } else {
            Article obj = this.articleService.getObjById(Long.valueOf(Long.parseLong(id)));
            article = (Article) wf.toPo(request, obj);
        }
        article.setArticleClass(this.articleClassService.getObjById(Long.valueOf(Long
            .parseLong(class_id))));

        article.setUser(SecurityUserHolder.getCurrentUser());

        // 保存缩略图
        String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
        String saveFilePathName = request.getSession().getServletContext().getRealPath("/")
                                  + uploadFilePath + File.separator + "articleThum";
        Map<String, Object> map = new HashMap<String, Object>();
        String fileName = "";
        if (article.getAcc() != null) {
            fileName = article.getAcc().getName();
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
                    acc.setPath(uploadFilePath + "/articleThum");
                    acc.setWidth(CommUtil.null2Int(map.get("width")));
                    acc.setHeight(CommUtil.null2Int(map.get("height")));
                    acc.setCreatetime(new Date());
                    this.accessoryService.save(acc);
                    article.setAcc(acc);
                }
            } else if (map.get("fileName") != "") {
                acc = article.getAcc();
                acc.setName(CommUtil.null2String(map.get("fileName")));
                acc.setExt(CommUtil.null2String(map.get("mime")));
                acc.setSize(CommUtil.null2Float(map.get("fileSize")));
                acc.setPath(uploadFilePath + "/articleThum");
                acc.setWidth(CommUtil.null2Int(map.get("width")));
                acc.setHeight(CommUtil.null2Int(map.get("height")));
                acc.setCreatetime(new Date());
                this.accessoryService.update(acc);
                article.setAcc(acc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (id.equals("")) {
            this.articleService.save(article);
        } else {
            this.articleService.update(article);
        }
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("list_url", list_url);
        mv.addObject("op_title", "保存文章成功");
        if (add_url != null) {
            mv.addObject("add_url", add_url + "?currentPage=" + currentPage + "&class_id="
                                    + class_id);
        }
        return mv;
    }

    @SecurityMapping(title = "文章删除", value = "/admin/article_del.htm*", rtype = "admin", rname = "文章管理", rcode = "article", rgroup = "网站")
    @RequestMapping({ "/admin/article_del.htm" })
    public String delete(HttpServletRequest request, String mulitId) {
        String[] ids = mulitId.split(",");
        for (String id : ids) {
            if (!id.equals("")) {
                Article article = this.articleService.getObjById(Long.valueOf(Long.parseLong(id)));
                if (article != null)
                    this.articleService.delete(Long.valueOf(Long.parseLong(id)));
            }
        }
        return "redirect:article_list.htm";
    }

    @SecurityMapping(title = "文章AJAX更新", value = "/admin/article_ajax.htm*", rtype = "admin", rname = "文章管理", rcode = "article", rgroup = "网站")
    @RequestMapping({ "/admin/article_ajax.htm" })
    public void ajax(HttpServletRequest request, HttpServletResponse response, String id,
                     String fieldName, String value) throws ClassNotFoundException {
        Article obj = this.articleService.getObjById(Long.valueOf(Long.parseLong(id)));
        Field[] fields = Article.class.getDeclaredFields();
        obj.setUser(SecurityUserHolder.getCurrentUser());
        BeanWrapper wrapper = new BeanWrapper(obj);
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
        this.articleService.update(obj);
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

    @RequestMapping({ "/admin/article_mark.htm" })
    public void article_mark(HttpServletRequest request, HttpServletResponse response, String mark,
                             String id) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mark", mark.trim());
        params.put("id", CommUtil.null2Long(id));
        List<Article> arts = this.articleService.query(
            "select obj from Article obj where obj.mark=:mark and obj.id!=:id", params, -1, -1);
        boolean ret = true;
        if (arts.size() > 0) {
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
