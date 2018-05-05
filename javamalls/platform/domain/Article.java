package com.javamalls.platform.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_article")
public class Article extends CommonEntity {
    private static final long serialVersionUID = 1676909425469822574L;
    private String            title;
    @ManyToOne(fetch = FetchType.EAGER, targetEntity = ArticleClass.class, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "articleClass_id")
    private ArticleClass      articleClass;
    private String            url;
    private int               sequence;
    private boolean           display;
    private String            mark;

    // 文章缩略图
    @OneToOne(cascade = { javax.persistence.CascadeType.REMOVE }, fetch = FetchType.LAZY)
    @JoinColumn(name = "accid")
    private Accessory         acc;

    @Lob
    @Column(columnDefinition = "LongText")
    private String            content;

    @ManyToOne(fetch = FetchType.LAZY)
    private User              user;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArticleClass getArticleClass() {
        return this.articleClass;
    }

    public void setArticleClass(ArticleClass articleClass) {
        this.articleClass = articleClass;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSequence() {
        return this.sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public boolean isDisplay() {
        return this.display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMark() {
        return this.mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public Accessory getAcc() {
        return acc;
    }

    public void setAcc(Accessory acc) {
        this.acc = acc;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
