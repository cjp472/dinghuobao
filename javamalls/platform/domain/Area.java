package com.javamalls.platform.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.javamalls.base.domain.CommonEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_area")
public class Area extends CommonEntity {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1329914360953191740L;
	/**
     *Comment for <code>serialVersionUID</code>
     */
    private String            areaName;
    @OneToMany(mappedBy = "parent",fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<Area>        childs           = new ArrayList<Area>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Area              parent;
    private int               sequence;
    private int               level;
    @Column(columnDefinition = "bit default false")
    private boolean           common;

    public boolean isCommon() {
        return this.common;
    }

    public void setCommon(boolean common) {
        this.common = common;
    }

    public List<Area> getChilds() {
        return this.childs;
    }

    public void setChilds(List<Area> childs) {
        this.childs = childs;
    }

    public Area getParent() {
        return this.parent;
    }

    public void setParent(Area parent) {
        this.parent = parent;
    }

    public String getAreaName() {
        return this.areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getSequence() {
        return this.sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

}
