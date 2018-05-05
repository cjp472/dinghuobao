package com.javamalls.platform.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_interface_log")
public class InterfaceLog extends CommonEntity {

    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;

    private String            interface_name;

    private Integer           system_type;          //接口类型 1.仓库接口

    private String            request_parameter;

    private String            response_parameter;

    private String           status;


    public String getInterface_name() {
        return interface_name;
    }

    public void setInterface_name(String interface_name) {
        this.interface_name = interface_name;
    }

    public Integer getSystem_type() {
        return system_type;
    }

    public void setSystem_type(Integer system_type) {
        this.system_type = system_type;
    }

    public String getRequest_parameter() {
        return request_parameter;
    }

    public void setRequest_parameter(String request_parameter) {
        this.request_parameter = request_parameter;
    }

    public String getResponse_parameter() {
        return response_parameter;
    }

    public void setResponse_parameter(String response_parameter) {
        this.response_parameter = response_parameter;
    }

  

    public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
