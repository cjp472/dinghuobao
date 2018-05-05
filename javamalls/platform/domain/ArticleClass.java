package com.javamalls.platform.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_articletype")
public class ArticleClass extends CommonEntity {
    private static final long  serialVersionUID = -2527630118218925902L;
    private String             className;
    private int                sequence;
    private int                level;
    private String             mark;
    private boolean            sysClass;
    @OneToOne(cascade = { javax.persistence.CascadeType.REMOVE }, fetch = FetchType.LAZY)
    @JoinColumn(name = "accid")
    private Accessory          acc;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = ArticleClass.class, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE })
    private ArticleClass       parent;

    @OneToMany(mappedBy = "parent", cascade = { javax.persistence.CascadeType.REMOVE })
    private List<ArticleClass> childs           = new ArrayList<ArticleClass>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "articleClass", cascade = { javax.persistence.CascadeType.REMOVE })
    @OrderBy("createtime desc")
    private List<Article>      articles         = new ArrayList<Article>();

    public int getSequence() {
        return this.sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isSysClass() {
        return this.sysClass;
    }

    public void setSysClass(boolean sysClass) {
        this.sysClass = sysClass;
    }

    public ArticleClass getParent() {
        return this.parent;
    }

    public void setParent(ArticleClass parent) {
        this.parent = parent;
    }

    public List<ArticleClass> getChilds() {
        return this.childs;
    }

    public void setChilds(List<ArticleClass> childs) {
        this.childs = childs;
    }

    public String getMark() {
        return this.mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public Accessory getAcc() {
        return acc;
    }

    public void setAcc(Accessory acc) {
        this.acc = acc;
    }

}
