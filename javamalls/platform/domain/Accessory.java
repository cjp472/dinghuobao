package com.javamalls.platform.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.javamalls.base.domain.CommonEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_pic")
public class Accessory extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    private String            name;
    private String            path;
    private float             size;
    private int               width;
    private int               height;
    private String            ext;
    private String            info;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User              user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Album             album;
    @OneToOne(mappedBy = "album_cover", fetch = FetchType.LAZY)
    @JsonIgnore
    private Album             cover_album;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private SysConfig         config;
    @OneToMany(mappedBy = "goods_main_photo")
    @JsonIgnore
    private List<Goods>       goods_main_list  = new ArrayList<Goods>();
    @ManyToMany(mappedBy = "goods_photos", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Goods>       goods_list       = new ArrayList<Goods>();

    public List<Goods> getGoods_main_list() {
        return this.goods_main_list;
    }

    public void setGoods_main_list(List<Goods> goods_main_list) {
        this.goods_main_list = goods_main_list;
    }

    public List<Goods> getGoods_list() {
        return this.goods_list;
    }

    public void setGoods_list(List<Goods> goods_list) {
        this.goods_list = goods_list;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getInfo() {
        return this.info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public float getSize() {
        return this.size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getExt() {
        return this.ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public Album getAlbum() {
        return this.album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Album getCover_album() {
        return this.cover_album;
    }

    public void setCover_album(Album cover_album) {
        this.cover_album = cover_album;
    }

    public SysConfig getConfig() {
        return this.config;
    }

    public void setConfig(SysConfig config) {
        this.config = config;
    }
}
