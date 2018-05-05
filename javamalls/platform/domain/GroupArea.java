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

/**团购区域
 *                       
 * @Filename: GroupArea.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_group_area")
public class GroupArea extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    private String            ga_name;
    private int               ga_sequence;
    @ManyToOne(fetch = FetchType.LAZY)
    private GroupArea         parent;
    @OneToMany(mappedBy = "parent", cascade = { javax.persistence.CascadeType.REMOVE })
    @OrderBy("ga_sequence asc")
    private List<GroupArea>   childs           = new ArrayList<GroupArea>();
    private int               ga_level;

    public int getGa_level() {
        return this.ga_level;
    }

    public void setGa_level(int ga_level) {
        this.ga_level = ga_level;
    }

    public String getGa_name() {
        return this.ga_name;
    }

    public void setGa_name(String ga_name) {
        this.ga_name = ga_name;
    }

    public int getGa_sequence() {
        return this.ga_sequence;
    }

    public void setGa_sequence(int ga_sequence) {
        this.ga_sequence = ga_sequence;
    }

    public GroupArea getParent() {
        return this.parent;
    }

    public void setParent(GroupArea parent) {
        this.parent = parent;
    }

    public List<GroupArea> getChilds() {
        return this.childs;
    }

    public void setChilds(List<GroupArea> childs) {
        this.childs = childs;
    }
}
