package com.javamalls.platform.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name = "jm_spare_goodsclass")
public class SpareGoodsClass extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long     serialVersionUID = 1L;
    private String                className;
    @Column(columnDefinition = "int default 0")
    private int                   sequence;
    @Column(columnDefinition = "int default 0")
    private int                   level;
    @Column(columnDefinition = "bit default true")
    private boolean               viewInFloor;
    @OneToMany(mappedBy = "parent")
    @OrderBy("sequence asc")
    private List<SpareGoodsClass> childs           = new ArrayList<SpareGoodsClass>();
    @ManyToOne(fetch = FetchType.LAZY)
    private SpareGoodsClass       parent;
    @OneToOne(mappedBy = "sgc", fetch = FetchType.LAZY)
    private SpareGoodsFloor       floor;
    @OneToMany(mappedBy = "spareGoodsClass")
    private List<SpareGoods>      sgs              = new ArrayList<SpareGoods>();

    public List<SpareGoods> getSgs() {
        return this.sgs;
    }

    public void setSgs(List<SpareGoods> sgs) {
        this.sgs = sgs;
    }

    public boolean isViewInFloor() {
        return this.viewInFloor;
    }

    public void setViewInFloor(boolean viewInFloor) {
        this.viewInFloor = viewInFloor;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getSequence() {
        return this.sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<SpareGoodsClass> getChilds() {
        return this.childs;
    }

    public void setChilds(List<SpareGoodsClass> childs) {
        this.childs = childs;
    }

    public SpareGoodsClass getParent() {
        return this.parent;
    }

    public void setParent(SpareGoodsClass parent) {
        this.parent = parent;
    }

    public SpareGoodsFloor getFloor() {
        return this.floor;
    }

    public void setFloor(SpareGoodsFloor floor) {
        this.floor = floor;
    }
}
