package com.javamalls.front.web.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.javamalls.platform.domain.Article;
import com.javamalls.platform.service.IArticleService;

/**文章
 *                       
 * @Filename: ArticleViewTools.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Component
public class ArticleViewTools {
    @Autowired
    private IArticleService articleService;

    public Article queryArticle(Long id, int position) {
        String query = "select obj from Article obj where obj.articleClass.id=:class_id and obj.display=:display and ";
        Article article = this.articleService.getObjById(id);
        if (article != null) {
            Map params = new HashMap();
            params.put("createtime", article.getCreatetime());
            params.put("class_id", article.getArticleClass().getId());
            params.put("display", Boolean.valueOf(true));
            if (position > 0) {
                query = query + "obj.createtime<:createtime order by obj.createtime desc";
            } else {
                query = query + "obj.createtime>:createtime order by obj.createtime asc";
            }
            List<Article> objs = this.articleService.query(query, params, 0, 1);
            if (objs.size() > 0) {
                return (Article) objs.get(0);
            }
            Article obj = new Article();
            obj.setTitle("没有了");
            return obj;
        }
        Article obj = new Article();
        obj.setTitle("没有了");
        return obj;
    }
}
