package com.javamalls.platform.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

/**角色
 *                       
 * @Filename: Role.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_role")
public class Role extends CommonEntity implements Comparable {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    private String            roleName;
    private String            roleCode;
    private String            type;
    private String            info;
    @Column(columnDefinition = "bit default true")
    private boolean           display;
    private int               sequence;
    @ManyToOne(fetch = FetchType.LAZY)
    private RoleGroup         rg;
    @ManyToMany(targetEntity = Res.class, fetch = FetchType.LAZY)
    @JoinTable(name = "jm_role_res", joinColumns = { @javax.persistence.JoinColumn(name = "role_id") }, inverseJoinColumns = { @javax.persistence.JoinColumn(name = "res_id") })
    private List<Res>         reses            = new ArrayList<Res>();

    public String getRoleName() {
        return this.roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleCode() {
        return this.roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getInfo() {
        return this.info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<Res> getReses() {
        return this.reses;
    }

    public void setReses(List<Res> reses) {
        this.reses = reses;
    }

    public int compareTo(Object obj) {
        Role role = (Role) obj;
        if (super.getId().equals(role.getId())) {
            return 0;
        }
        return 1;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSequence() {
        return this.sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public RoleGroup getRg() {
        return this.rg;
    }

    public void setRg(RoleGroup rg) {
        this.rg = rg;
    }

    public boolean isDisplay() {
        return this.display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }
}
