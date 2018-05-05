package com.javamalls.platform.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.javamalls.base.domain.CommonEntity;

/**地址
 *                       
 * @Filename: Address.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_addr")
public class Address extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    private String            trueName;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Area              area;
    private String            area_info;
    private String            zip;
    private String            telephone;
    private String            mobile;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User              user;

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTrueName() {
        return this.trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public Area getArea() {
        return this.area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public String getArea_info() {
        return this.area_info;
    }

    public void setArea_info(String area_info) {
        this.area_info = area_info;
    }

    public String getZip() {
        return this.zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
