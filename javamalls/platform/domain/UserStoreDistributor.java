package com.javamalls.platform.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.javamalls.base.domain.CommonEntity;
/**
 * 用户申请店铺分销商
 * @author zmw
 *
 */
@Entity
@Table(name = "jm_user_store_distributor")
public class UserStoreDistributor extends CommonEntity {
 	/**
	 * 
	 */
	private static final long serialVersionUID = -3023507474907793407L;
	//申请人
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
 	private User user; 
 	//店铺
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id")
 	private Store             store;
 	//申请状态:1、审核中2、审核成功3、审核失败
 	private Integer status; 
 	//审核建议
 	private String suggess; 
 	//修改时间
 	private Date updatetime; 
 	//审核失败标示：默认null，1.失败，2.成功重新提交申请
 	private Integer fail;
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}
	

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
     * 获取审核建议
     */
	public java.lang.String getSuggess(){
		return this.suggess;
	}
 		
	/**
     * 设置审核建议
     */
	public void setSuggess(java.lang.String suggess){
		this.suggess = suggess;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	/**
     * 获取审核标示：默认null，1.失败，2.成功重新提交申请
     */
	public Integer getFail() {
		return fail;
	}
	/**
     * 设置审核标示：默认null，1.失败，2.成功重新提交申请
     */
	public void setFail(Integer fail) {
		this.fail = fail;
	}
	
 }