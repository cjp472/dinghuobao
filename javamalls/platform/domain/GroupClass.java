package com.javamalls.platform.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

/**团购分类 
 *                       
 * @Filename: GroupClass.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_group_class")
public class GroupClass extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    private String            gc_name;
    private int               gc_sequence;
    @ManyToOne(fetch = FetchType.LAZY)
    private GroupClass        parent;
    @OneToMany(mappedBy = "parent", cascade = { javax.persistence.CascadeType.REMOVE })
    @OrderBy("gc_sequence asc")
    private List<GroupClass>  childs           = new ArrayList<GroupClass>();
    @OneToMany(mappedBy = "gg_gc", cascade = { javax.persistence.CascadeType.REMOVE })
    private List<GroupGoods>  ggs              = new ArrayList<GroupGoods>();
    private int               gc_level;

    public List<GroupGoods> getGgs() {
        return this.ggs;
    }

    public void setGgs(List<GroupGoods> ggs) {
        this.ggs = ggs;
    }

    public int getGc_level() {
        return this.gc_level;
    }

    public void setGc_level(int gc_level) {
        this.gc_level = gc_level;
    }

    public String getGc_name() {
        return this.gc_name;
    }

    public void setGc_name(String gc_name) {
        this.gc_name = gc_name;
    }

    public int getGc_sequence() {
        return this.gc_sequence;
    }

    public void setGc_sequence(int gc_sequence) {
        this.gc_sequence = gc_sequence;
    }

    public GroupClass getParent() {
        return this.parent;
    }

    public void setParent(GroupClass parent) {
        this.parent = parent;
    }

    public List<GroupClass> getChilds() {
        return this.childs;
    }

    public void setChilds(List<GroupClass> childs) {
        this.childs = childs;
    }
}
