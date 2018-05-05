package com.javamalls.platform.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.javamalls.base.domain.CommonEntity;

@Entity
@Table(name = "jm_cellphoneverifycode")
public class MobileVerifyCode extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    private String            mobile;
    private String            code;

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
