package com.javamalls.platform.domain;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.javamalls.base.domain.CommonEntity;

/**
 * 供货商公司信息                      
 * @Filename: CompanyInfo.java
 * @Version: 1.0
 * @Author: hamj
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_company_info")
public class CompanyInfo extends CommonEntity implements java.io.Serializable {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long      serialVersionUID = 1L;
    private String                 company_name;//公司名
    private String                 trade_name;//公司行业
    private String                 address;//详细地址
    private String                 contact_name;//联系人
    private String                 contact_title;//联系人职位
    private String                 contact_mobile;//联系人电话
    private String                 contact_qq;//联系人QQ
    private String                 contact_email;//联系人邮箱
    private String                 contact_website;//公司网址
    @OneToOne(fetch = FetchType.LAZY)
    private User             user;
    @ManyToOne(fetch=FetchType.EAGER)
    private Area                   area;
    
    private String taxpayer_number;//纳税人识别号
    private String header_invoice;//发票抬头
    
    
    /**
     * 纳税人识别号
     * @return
     */
    public String getTaxpayer_number() {
		return taxpayer_number;
	}
    
    /**
     * 纳税人识别号
     * @param taxpayer_number
     */
	public void setTaxpayer_number(String taxpayer_number) {
		this.taxpayer_number = taxpayer_number;
	}
	/**
	 * 发票抬头
	 * @return
	 */
	public String getHeader_invoice() {
		return header_invoice;
	}
	/**
	 * 发票抬头
	 * @param header_invoice
	 */
	public void setHeader_invoice(String header_invoice) {
		this.header_invoice = header_invoice;
	}
	/**
     * 公司名
     * @return
     */
	public String getCompany_name() {
		return company_name;
	}
	/**
	 * 公司名
	 * @param company_name
	 */
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	/**
	 * 公司行业
	 * @return
	 */
	public String getTrade_name() {
		return trade_name;
	}
	/**
	 * 公司行业
	 * @param trade_name
	 */
	public void setTrade_name(String trade_name) {
		this.trade_name = trade_name;
	}
	/**
	 * 详细地址
	 * @return
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * 详细地址
	 * @param address
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * 联系人
	 * @return
	 */
	public String getContact_name() {
		return contact_name;
	}
	/**
	 * 联系人
	 * @param contact_name
	 */
	public void setContact_name(String contact_name) {
		this.contact_name = contact_name;
	}
	/**
	 * 联系人职位
	 * @return
	 */
	public String getContact_title() {
		return contact_title;
	}
	/**
	 * 联系人职位
	 * @param contact_title
	 */
	public void setContact_title(String contact_title) {
		this.contact_title = contact_title;
	}
	/**
	 * 联系人电话
	 * @return
	 */
	public String getContact_mobile() {
		return contact_mobile;
	}
	/**
	 * 联系人电话
	 * @param contact_mobile
	 */
	public void setContact_mobile(String contact_mobile) {
		this.contact_mobile = contact_mobile;
	}
	/**
	 * 联系人QQ
	 * @return
	 */
	public String getContact_qq() {
		return contact_qq;
	}
	/**
	 * 联系人QQ
	 * @param contact_qq
	 */
	public void setContact_qq(String contact_qq) {
		this.contact_qq = contact_qq;
	}
	/**
	 * 联系人邮箱
	 * @return
	 */
	public String getContact_email() {
		return contact_email;
	}
	/**
	 * 联系人邮箱
	 * @param contact_email
	 */
	public void setContact_email(String contact_email) {
		this.contact_email = contact_email;
	}
	/**
	 * 公司网址
	 * @return
	 */
	public String getContact_website() {
		return contact_website;
	}
	/**
	 * 公司网址
	 * @param contact_website
	 */
	public void setContact_website(String contact_website) {
		this.contact_website = contact_website;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Area getArea() {
		return area;
	}
	public void setArea(Area area) {
		this.area = area;
	}
   

}
