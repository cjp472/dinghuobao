package com.javamalls.platform.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.javamalls.base.domain.CommonEntity;
@Entity
@Table(name = "jm_entry_order")
public class EntryOrder extends CommonEntity {

	private static final long serialVersionUID = 1L;
	//修改时间
 	private      Date 							updatetime; 
 	//创建人
 	@ManyToOne(fetch = FetchType.LAZY)
 	@JoinColumn(name = "createuser")
 	private      User 							createuser; 
 	//修改人
 	@ManyToOne(fetch = FetchType.LAZY)
 	@JoinColumn(name = "updateuser")
 	private      User 							updateuser; 
 	//出入单编号
 	private      String 						ordernumber; 
 	//类型（1退货单2调货单3配货单）
 	private      Integer 						type; 
 	//出货商户Id
	@ManyToOne(fetch = FetchType.LAZY)
 	@JoinColumn(name = "out_store_id")
 	private      Store 							outStore; 
 	//入货商户Id
	@ManyToOne(fetch = FetchType.LAZY)
 	@JoinColumn(name = "in_store_id")
 	private      Store 							inStore; 
 	//结束时间
 	private      Date 							endtime; 
 	//物流公司
	@ManyToOne(fetch = FetchType.LAZY)
 	@JoinColumn(name = "courier_company_id")
 	private      ExpressCompany 				courierCompany; 
 	//物流单号
 	private      String 						couriernumber; 
 	//状态（0提交，1暂存）
 	private      Integer 						status; 
 	

 		
	/**
     * 获取修改时间
     */
	public Date getUpdatetime(){
		return this.updatetime;
	}
 		
	/**
     * 设置修改时间
     */
	public void setUpdatetime(Date updatetime){
		this.updatetime = updatetime;
	}
 		
	
 		
	public String getOrdernumber() {
		return ordernumber;
	}

	public void setOrdernumber(String ordernumber) {
		this.ordernumber = ordernumber;
	}

	/**
     * 获取类型（1退货单2调货单3配货单）
     */
	public Integer getType(){
		return this.type;
	}
 		
	/**
     * 设置类型（1退货单2调货单3配货单）
     */
	public void setType(Integer type){
		this.type = type;
	}
 		
	/**
     * 获取结束时间
     */
	public Date getEndtime(){
		return this.endtime;
	}
 		
	/**
     * 设置结束时间
     */
	public void setEndtime(Date endtime){
		this.endtime = endtime;
	}
 		



	public String getCouriernumber() {
		return couriernumber;
	}

	public void setCouriernumber(String couriernumber) {
		this.couriernumber = couriernumber;
	}

	/**
     * 获取状态（0提交，1暂存）
     */
	public Integer getStatus(){
		return this.status;
	}
 		
	/**
     * 设置状态（0提交，1暂存）
     */
	public void setStatus(Integer status){
		this.status = status;
	}

	public User getCreateuser() {
		return createuser;
	}

	public void setCreateuser(User createuser) {
		this.createuser = createuser;
	}

	public User getUpdateuser() {
		return updateuser;
	}

	public void setUpdateuser(User updateuser) {
		this.updateuser = updateuser;
	}

	public Store getOutStore() {
		return outStore;
	}

	public void setOutStore(Store outStore) {
		this.outStore = outStore;
	}

	public Store getInStore() {
		return inStore;
	}

	public void setInStore(Store inStore) {
		this.inStore = inStore;
	}

	public ExpressCompany getCourierCompany() {
		return courierCompany;
	}

	public void setCourierCompany(ExpressCompany courierCompany) {
		this.courierCompany = courierCompany;
	}
	
	
 }