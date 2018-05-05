package com.javamalls.platform.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

/**首页楼层
 *                       
 * @Filename: GoodsFloor.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_goods_floor")
public class GoodsFloor extends CommonEntity {

    private static final long serialVersionUID = -5649865134444946035L;
    private String            gf_name;
    private String            gf_css;
    private int               gf_sequence;
    private int               gf_goods_count;
    @OneToMany(mappedBy = "parent", cascade = { javax.persistence.CascadeType.REMOVE })
    @OrderBy("gf_sequence asc")
    private List<GoodsFloor>  childs           = new ArrayList<GoodsFloor>();
    @ManyToOne(fetch = FetchType.LAZY)
    private GoodsFloor        parent;
    private int               gf_level;
    private boolean           gf_display;
    @Column(columnDefinition = "LongText")
    private String            gf_gc_list;
    @Column(columnDefinition = "LongText")
    private String            gf_gc_goods;
    @Column(columnDefinition = "LongText")
    private String            gf_list_goods;
    @Column(columnDefinition = "LongText")
    private String            gf_left_adv;
    @Column(columnDefinition = "LongText")
    private String            gf_right_adv;
    @Column(columnDefinition = "LongText")
    private String            gf_brand_list;

    public String getGf_brand_list() {
        return this.gf_brand_list;
    }

    public void setGf_brand_list(String gf_brand_list) {
        this.gf_brand_list = gf_brand_list;
    }

    public String getGf_name() {
        return this.gf_name;
    }

    public void setGf_name(String gf_name) {
        this.gf_name = gf_name;
    }

    public String getGf_css() {
        return this.gf_css;
    }

    public void setGf_css(String gf_css) {
        this.gf_css = gf_css;
    }

    public int getGf_sequence() {
        return this.gf_sequence;
    }

    public void setGf_sequence(int gf_sequence) {
        this.gf_sequence = gf_sequence;
    }

    public int getGf_goods_count() {
        return this.gf_goods_count;
    }

    public void setGf_goods_count(int gf_goods_count) {
        this.gf_goods_count = gf_goods_count;
    }

    public List<GoodsFloor> getChilds() {
        return this.childs;
    }

    public void setChilds(List<GoodsFloor> childs) {
        this.childs = childs;
    }

    public GoodsFloor getParent() {
        return this.parent;
    }

    public void setParent(GoodsFloor parent) {
        this.parent = parent;
    }

    public int getGf_level() {
        return this.gf_level;
    }

    public void setGf_level(int gf_level) {
        this.gf_level = gf_level;
    }

    public boolean isGf_display() {
        return this.gf_display;
    }

    public void setGf_display(boolean gf_display) {
        this.gf_display = gf_display;
    }

    public String getGf_gc_list() {
        return this.gf_gc_list;
    }

    public void setGf_gc_list(String gf_gc_list) {
        this.gf_gc_list = gf_gc_list;
    }

    public String getGf_gc_goods() {
        return this.gf_gc_goods;
    }

    public void setGf_gc_goods(String gf_gc_goods) {
        this.gf_gc_goods = gf_gc_goods;
    }

    public String getGf_list_goods() {
        return this.gf_list_goods;
    }

    public void setGf_list_goods(String gf_list_goods) {
        this.gf_list_goods = gf_list_goods;
    }

    public String getGf_left_adv() {
        return this.gf_left_adv;
    }

    public void setGf_left_adv(String gf_left_adv) {
        this.gf_left_adv = gf_left_adv;
    }

    public String getGf_right_adv() {
        return this.gf_right_adv;
    }

    public void setGf_right_adv(String gf_right_adv) {
        this.gf_right_adv = gf_right_adv;
    }
}
