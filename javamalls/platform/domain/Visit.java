package com.javamalls.platform.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_visit")
public class Visit extends CommonEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private HomePage homepage;
    @OneToOne(fetch = FetchType.LAZY)
    private User     user;
    private Date     visitTime;

    public HomePage getHomepage() {
        return this.homepage;
    }

    public void setHomepage(HomePage homepage) {
        this.homepage = homepage;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getVisitTime() {
        return this.visitTime;
    }

    public void setVisitTime(Date visitTime) {
        this.visitTime = visitTime;
    }
}
