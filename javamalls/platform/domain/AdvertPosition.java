package com.javamalls.platform.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

/**
 *   广告位                    
 * @Filename: AdvertPosition.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_adv_pos")
public class AdvertPosition extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    private String            ap_title;
    @Column(columnDefinition = "LongText")
    private String            ap_content;
    private String            ap_type;
    private int               ap_status;
    private int               ap_use_status;
    private int               ap_width;
    private int               ap_height;
    private int               ap_price;
    private int               ap_sale_type;
    private int               ap_sys_type;
    private int               ap_show_type;
    @OneToOne(fetch = FetchType.LAZY)
    private Accessory         ap_acc;
    private String            ap_text;
    private String            ap_acc_url;
    @Column(columnDefinition = "LongText")
    private String            ap_code;
    @OneToMany(mappedBy = "ad_ap", cascade = { javax.persistence.CascadeType.REMOVE })
    @OrderBy("ad_slide_sequence asc")
    private List<Advert>      advs             = new ArrayList<Advert>();
    @Transient
    private String            adv_id;
    
     
    private String 			  ap_mark;//广告位标识
    
    
    
    /**
     * 广告位标识
     * @return
     */
    public String getAp_mark() {
		return ap_mark;
	}
    /**
     * 广告位标识
     * @param ap_mark
     */
	public void setAp_mark(String ap_mark) {
		this.ap_mark = ap_mark;
	}

	public List<Advert> getAdvs() {
        return this.advs;
    }

    public void setAdvs(List<Advert> advs) {
        this.advs = advs;
    }

    public int getAp_sys_type() {
        return this.ap_sys_type;
    }

    public void setAp_sys_type(int ap_sys_type) {
        this.ap_sys_type = ap_sys_type;
    }

    public String getAp_title() {
        return this.ap_title;
    }

    public void setAp_title(String ap_title) {
        this.ap_title = ap_title;
    }

    public String getAp_content() {
        return this.ap_content;
    }

    public void setAp_content(String ap_content) {
        this.ap_content = ap_content;
    }

    public String getAp_type() {
        return this.ap_type;
    }

    public void setAp_type(String ap_type) {
        this.ap_type = ap_type;
    }

    public int getAp_status() {
        return this.ap_status;
    }

    public void setAp_status(int ap_status) {
        this.ap_status = ap_status;
    }

    public int getAp_width() {
        return this.ap_width;
    }

    public void setAp_width(int ap_width) {
        this.ap_width = ap_width;
    }

    public int getAp_height() {
        return this.ap_height;
    }

    public void setAp_height(int ap_height) {
        this.ap_height = ap_height;
    }

    public int getAp_price() {
        return this.ap_price;
    }

    public void setAp_price(int ap_price) {
        this.ap_price = ap_price;
    }

    public int getAp_use_status() {
        return this.ap_use_status;
    }

    public void setAp_use_status(int ap_use_status) {
        this.ap_use_status = ap_use_status;
    }

    public Accessory getAp_acc() {
        return this.ap_acc;
    }

    public void setAp_acc(Accessory ap_acc) {
        this.ap_acc = ap_acc;
    }

    public int getAp_sale_type() {
        return this.ap_sale_type;
    }

    public void setAp_sale_type(int ap_sale_type) {
        this.ap_sale_type = ap_sale_type;
    }

    public String getAp_code() {
        return this.ap_code;
    }

    public void setAp_code(String ap_code) {
        this.ap_code = ap_code;
    }

    public int getAp_show_type() {
        return this.ap_show_type;
    }

    public void setAp_show_type(int ap_show_type) {
        this.ap_show_type = ap_show_type;
    }

    public String getAp_acc_url() {
        return this.ap_acc_url;
    }

    public void setAp_acc_url(String ap_acc_url) {
        this.ap_acc_url = ap_acc_url;
    }

    public String getAp_text() {
        return this.ap_text;
    }

    public void setAp_text(String ap_text) {
        this.ap_text = ap_text;
    }

    public String getAdv_id() {
        return this.adv_id;
    }

    public void setAdv_id(String adv_id) {
        this.adv_id = adv_id;
    }
}
