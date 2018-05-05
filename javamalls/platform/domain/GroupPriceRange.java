package com.javamalls.platform.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

/**团购价格区间
 *                       
 * @Filename: GroupPriceRange.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_group_price_range")
public class GroupPriceRange extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    private String            gpr_name;
    private int               gpr_begin;
    private int               gpr_end;

    public String getGpr_name() {
        return this.gpr_name;
    }

    public void setGpr_name(String gpr_name) {
        this.gpr_name = gpr_name;
    }

    public int getGpr_begin() {
        return this.gpr_begin;
    }

    public void setGpr_begin(int gpr_begin) {
        this.gpr_begin = gpr_begin;
    }

    public int getGpr_end() {
        return this.gpr_end;
    }

    public void setGpr_end(int gpr_end) {
        this.gpr_end = gpr_end;
    }
}
