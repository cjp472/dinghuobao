package com.javamalls.platform.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_homepage")
public class HomePage extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    @OneToOne(fetch = FetchType.LAZY)
    private User              owner;
    @OneToMany(mappedBy = "homepage", cascade = { javax.persistence.CascadeType.REMOVE })
    private List<Visit>       customers        = new ArrayList<Visit>();

    public User getOwner() {
        return this.owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<Visit> getCustomers() {
        return this.customers;
    }

    public void setCustomers(List<Visit> customers) {
        this.customers = customers;
    }
}
