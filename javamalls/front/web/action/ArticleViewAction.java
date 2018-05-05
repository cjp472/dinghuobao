package com.javamalls.front.web.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.front.web.tools.ArticleViewTools;
import com.javamalls.platform.domain.Article;
import com.javamalls.platform.domain.ArticleClass;
import com.javamalls.platform.domain.query.ArticleQueryObject;
import com.javamalls.platform.service.IArticleClassService;
import com.javamalls.platform.service.IArticleService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

/**文章查看
 *                       
 * @Filename: ArticleViewAction.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class ArticleViewAction {
    @Autowired
    private ISysConfigService    configService;
    @Autowired
    private IUserConfigService   userConfigService;
    @Autowired
    private IArticleService      articleService;
    @Autowired
    private IArticleClassService articleClassService;
    @Autowired
    private ArticleViewTools     articleTools;

    /**文章列表
     * @param request
     * @param response
     * @param param
     * @param currentPage
     * @return
     */
    @RequestMapping({ "/articlepage.htm" })
    public ModelAndView articlepage(HttpServletRequest request, HttpServletResponse response,
                                    String param, String currentPage) {
        ModelAndView mv = new JModelAndView("articlelist.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        ArticleClass ac = null;
        ArticleQueryObject aqo = new ArticleQueryObject();
        aqo.setCurrentPage(Integer.valueOf(CommUtil.null2Int(currentPage)));
        Long id = CommUtil.null2Long(param);
        String mark = "";
        if (id.longValue() == -1L) {
            mark = param;
        }
        if (!mark.equals("")) {
            aqo.addQuery("obj.articleClass.mark", new SysMap("mark", mark), "=");
            ac = this.articleClassService.getObjByPropertyName("mark", mark);
        }
        if (id.longValue() != -1L) {
        	Map<String, Object> map=new HashMap<String, Object>();
        	map.put("id1",id);
        	map.put("id2",id);
            aqo.addQuery(" (obj.articleClass.id=:id1 or obj.articleClass.parent.id=:id2) ",map);
            ac = this.articleClassService.getObjById(id);
        }
        aqo.addQuery("obj.display", new SysMap("display", Boolean.valueOf(true)), "=");
        aqo.setOrderBy("createtime");
        aqo.setOrderType("desc");
        IPageList pList = this.articleService.list(aqo);
        String url = CommUtil.getURL(request) + "/articlepage_" + ac.getId();
        CommUtil.saveIPageList2ModelAndView("", url, "", pList, mv);
        List<ArticleClass> acs = this.articleClassService
            .query(
                "select obj from ArticleClass obj where obj.parent.id is null order by obj.sequence asc",
                null, -1, -1);
        List<Article> articles = this.articleService.query(
            "select obj from Article obj order by obj.createtime desc", null, 0, 6);
        mv.addObject("ac", ac);
        mv.addObject("articles", articles);
        mv.addObject("acs", acs);
        return mv;
    }

    /**文章详情
     * @param request
     * @param response
     * @param param
     * @return
     */
    @SecurityMapping(title = "文章详情", value = "/article.htm*", rtype = "platform", rname = "新闻文章", rcode = "article", rgroup = "前台查看文章")
    @RequestMapping({ "/article.htm" })
    public ModelAndView article(HttpServletRequest request, HttpServletResponse response,
                                String param) {
        ModelAndView mv = new JModelAndView("article.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        Article obj = null;
        Long id = CommUtil.null2Long(param);
        String mark = "";
        if (id.longValue() == -1L) {
            mark = param;
        }
        if (id.longValue() != -1L) {
            obj = this.articleService.getObjById(id);
        }
        if (!mark.equals("")) {
            obj = this.articleService.getObjByProperty("mark", mark);
        }
        List<ArticleClass> acs = this.articleClassService
            .query(
                "select obj from ArticleClass obj where obj.parent.id is null order by obj.sequence asc",
                null, -1, -1);
        List<Article> articles = this.articleService.query(
            "select obj from Article obj order by obj.createtime desc", null, 0, 6);
        mv.addObject("articles", articles);
        mv.addObject("acs", acs);
        mv.addObject("obj", obj);
        mv.addObject("articleTools", this.articleTools);
        return mv;
    }
}
