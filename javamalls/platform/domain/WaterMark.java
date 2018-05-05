package com.javamalls.platform.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_waterlines")
public class WaterMark extends CommonEntity {
    @OneToOne(fetch = FetchType.LAZY)
    private Store     store;
    private boolean   wm_image_open;
    @OneToOne(fetch = FetchType.LAZY)
    private Accessory wm_image;
    private float     wm_image_alpha;
    private int       wm_image_pos;
    private boolean   wm_text_open;
    private String    wm_text;
    private int       wm_text_pos;
    private int       wm_text_font_size;
    private String    wm_text_font;
    private String    wm_text_color;

    public Accessory getWm_image() {
        return this.wm_image;
    }

    public void setWm_image(Accessory wm_image) {
        this.wm_image = wm_image;
    }

    public float getWm_image_alpha() {
        return this.wm_image_alpha;
    }

    public void setWm_image_alpha(float wm_image_alpha) {
        this.wm_image_alpha = wm_image_alpha;
    }

    public int getWm_image_pos() {
        return this.wm_image_pos;
    }

    public void setWm_image_pos(int wm_image_pos) {
        this.wm_image_pos = wm_image_pos;
    }

    public String getWm_text() {
        return this.wm_text;
    }

    public void setWm_text(String wm_text) {
        this.wm_text = wm_text;
    }

    public int getWm_text_pos() {
        return this.wm_text_pos;
    }

    public void setWm_text_pos(int wm_text_pos) {
        this.wm_text_pos = wm_text_pos;
    }

    public String getWm_text_font() {
        return this.wm_text_font;
    }

    public void setWm_text_font(String wm_text_font) {
        this.wm_text_font = wm_text_font;
    }

    public String getWm_text_color() {
        return this.wm_text_color;
    }

    public void setWm_text_color(String wm_text_color) {
        this.wm_text_color = wm_text_color;
    }

    public boolean isWm_image_open() {
        return this.wm_image_open;
    }

    public void setWm_image_open(boolean wm_image_open) {
        this.wm_image_open = wm_image_open;
    }

    public boolean isWm_text_open() {
        return this.wm_text_open;
    }

    public void setWm_text_open(boolean wm_text_open) {
        this.wm_text_open = wm_text_open;
    }

    public Store getStore() {
        return this.store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public int getWm_text_font_size() {
        return this.wm_text_font_size;
    }

    public void setWm_text_font_size(int wm_text_font_size) {
        this.wm_text_font_size = wm_text_font_size;
    }
}
