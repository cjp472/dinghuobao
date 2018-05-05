package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;

import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.Article;

@Repository("articleDAO")
public class ArticleDAO extends GenericDAO<Article> {
}
