package com.javamalls.platform.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_courier_company")
public class ExpressCompany extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    private String            company_name;
    private String            company_mark;
    @Column(columnDefinition = "int default 0")
    private int               company_sequence;
    @Column(columnDefinition = "varchar(255) default 'EXPRESS'")
    private String            company_type;
    @Column(columnDefinition = "int default 0")
    private int               company_status;
    @OneToMany(mappedBy = "ec")
    private List<OrderForm>   ofs              = new ArrayList<OrderForm>();

    public List<OrderForm> getOfs() {
        return this.ofs;
    }

    public void setOfs(List<OrderForm> ofs) {
        this.ofs = ofs;
    }

    public String getCompany_name() {
        return this.company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany_mark() {
        return this.company_mark;
    }

    public void setCompany_mark(String company_mark) {
        this.company_mark = company_mark;
    }

    public String getCompany_type() {
        return this.company_type;
    }

    public void setCompany_type(String company_type) {
        this.company_type = company_type;
    }

    public int getCompany_status() {
        return this.company_status;
    }

    public void setCompany_status(int company_status) {
        this.company_status = company_status;
    }

    public int getCompany_sequence() {
        return this.company_sequence;
    }

    public void setCompany_sequence(int company_sequence) {
        this.company_sequence = company_sequence;
    }
}
