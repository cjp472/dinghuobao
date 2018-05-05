package com.javamalls.platform.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.annotation.FormIgnore;
import com.javamalls.base.domain.CommonEntity;

/**订单
 *                       
 * @Filename: OrderForm.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_order_file")
public class OrderFormFile extends CommonEntity {
    private static final long serialVersionUID = 728588118388285624L;
    
    private String file_name;//文件名称
    private String file_url;//文件地址
    private String ext;//文件后缀
    private float  size;//文件大小
    @ManyToOne(fetch = FetchType.LAZY)
    private OrderForm orderForm;
	public String getFile_name() {
		return file_name;
	}
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}
	public String getFile_url() {
		return file_url;
	}
	public void setFile_url(String file_url) {
		this.file_url = file_url;
	}
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
	public float getSize() {
		return size;
	}
	public void setSize(float size) {
		this.size = size;
	}
	public OrderForm getOrderForm() {
		return orderForm;
	}
	public void setOrderForm(OrderForm orderForm) {
		this.orderForm = orderForm;
	}
    
    
}
