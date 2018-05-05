package com.javamalls.platform.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

/**资源
 *                       
 * @Filename: Res.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_res")
public class Res extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    private String            resName;
    private String            type;
    private String            value;
    @ManyToMany(mappedBy = "reses", targetEntity = Role.class, fetch = FetchType.EAGER)
    private List<Role>        roles            = new ArrayList<Role>();
    private int               sequence;
    private String            info;

    @Transient
    public String getRoleAuthorities() {
        List<String> roleAuthorities = new ArrayList<String>();
        for (Role role : this.roles) {
            roleAuthorities.add(role.getRoleCode());
        }
        return StringUtils.join(roleAuthorities.toArray(), ",");
    }

    public String getResName() {
        return this.resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<Role> getRoles() {
        return this.roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public String getInfo() {
        return this.info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getSequence() {
        return this.sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
