package com.javamalls.platform.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import com.javamalls.base.domain.CommonEntity;

/**
 * 
 * @author cjl
 * 供应商表
 * Field	Type	Comment
id			bigint(20) NOT NULL供应商
createtime	datetime NULL
disabled	bit(1) NOT NULL
name		varchar(255) NULL供应商名称
code		varchar(255) NULL供应商编号
contact		varchar(255) NULL供应商联系人
mobile		varchar(255) NULL手机号
remark		varchar(255) NULL备注
store_id	bigint(20) NULL
  `area_id` bigint(20) DEFAULT NULL COMMENT '所在地区',
  `address` varchar(255) DEFAULT NULL COMMENT '详细地址',
  `phone` varchar(255) DEFAULT NULL COMMENT '座机',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `qq_num` varchar(255) DEFAULT NULL COMMENT 'qq号',
  `account_name` varchar(255) DEFAULT NULL COMMENT '开户名称',
  `account_bank` varchar(255) DEFAULT NULL COMMENT '开户银行',
  `account_bank_number` varchar(255) DEFAULT NULL COMMENT '银行帐号',
  `header_invoice` varchar(255) DEFAULT NULL COMMENT '发票抬头',
  `taxpayer_number` varchar(255) DEFAULT NULL COMMENT '纳税人识别号',
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_supplier")
public class Supplier extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long       serialVersionUID  = 1L;
    private String 	name;//供应商名称
    private String  code;//供应商编号
    private String  contact;//供应商联系人
    private String  mobile;//手机号
    private String  remark;//备注
    @ManyToOne(fetch = FetchType.LAZY)
    private Store   store;//供货商店铺
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Area   area;//供货商所在地区
    private String address;//供货商详细地址
    private String phone;//座机
    private String email;//邮箱
    private String qq_num;//qq号
    private String account_name;//开户名称
    private String account_bank;//开户银行
    private String account_bank_number;//银行帐号
    private String header_invoice;//发票抬头
    private String taxpayer_number;//纳税人识别号
    
    
    @OneToMany(fetch=FetchType.LAZY,mappedBy = "supplier")
    private List<PurchaseOrder>            warehouse_list   = new ArrayList<PurchaseOrder>();//采购入库单列表
    
    
    /**
     * 采购入库单列表
     * @return
     */
    public List<PurchaseOrder> getWarehouse_list() {
		return warehouse_list;
	}
    /**
     * 采购入库单列表
     * @param warehouse_list
     */
	public void setWarehouse_list(List<PurchaseOrder> warehouse_list) {
		this.warehouse_list = warehouse_list;
	}
	/**
     * 供应商名称
     * @return
     */
	public String getName() {
		return name;
	}
	/**
	 * 供应商名称
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 供应商编号
	 * @return
	 */
	public String getCode() {
		return code;
	}
	/**
	 * 供应商编号
	 * @param code
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * 供应商联系人
	 * @return
	 */
	public String getContact() {
		return contact;
	}
	/**
	 * 供应商联系人
	 * @param contact
	 */
	public void setContact(String contact) {
		this.contact = contact;
	}
	/**
	 * 手机号
	 * @return
	 */
	public String getMobile() {
		return mobile;
	}
	/**
	 * 手机号
	 * @param mobile
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	/**
	 * 备注
	 * @return
	 */
	public String getRemark() {
		return remark;
	}
	/**
	 * 备注
	 * @param remark
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public Store getStore() {
		return store;
	}
	public void setStore(Store store) {
		this.store = store;
	}
	public Area getArea() {
		return area;
	}
	public void setArea(Area area) {
		this.area = area;
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
	 * 座机
	 * @return
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * 座机
	 * @param phone
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	/**
	 * 邮箱
	 * @return
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * 邮箱
	 * @param email
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * qq号
	 * @return
	 */
	public String getQq_num() {
		return qq_num;
	}
	/**
	 * qq号
	 * @param qq_num
	 */
	public void setQq_num(String qq_num) {
		this.qq_num = qq_num;
	}
	/**
	 * 开户名称
	 * @return
	 */
	public String getAccount_name() {
		return account_name;
	}
	/**
	 * 开户名称
	 * @param account_name
	 */
	public void setAccount_name(String account_name) {
		this.account_name = account_name;
	}
	/**
	 * 开户银行
	 * @return
	 */
	public String getAccount_bank() {
		return account_bank;
	}
	/**
	 * 开户银行
	 * @param account_bank
	 */
	public void setAccount_bank(String account_bank) {
		this.account_bank = account_bank;
	}
	/**
	 * 银行帐号
	 * @return
	 */
	public String getAccount_bank_number() {
		return account_bank_number;
	}
	/**
	 * 银行帐号
	 * @param account_bank_number
	 */
	public void setAccount_bank_number(String account_bank_number) {
		this.account_bank_number = account_bank_number;
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
    
    
    

}