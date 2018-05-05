package com.javamalls.platform.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

/**文章
 *                       
 * @Filename: Document.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_system_article")
public class Document extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    private String            mark;
    private String            title;
    @Lob
    @Column(columnDefinition = "LongText")
    private String            content;

    public String getMark() {
        return this.mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
