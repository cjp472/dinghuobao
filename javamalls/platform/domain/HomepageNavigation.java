package com.javamalls.platform.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.javamalls.base.domain.CommonEntity;
/**
 * 移动端首页导航
 * @author admin
 *
 */
@Entity
@Table(name = "jm_homepage_navigation")
public class HomepageNavigation extends CommonEntity {
 
	private static final long serialVersionUID = 1L;
	//创建人
	@ManyToOne(fetch = FetchType.LAZY)
 	@JoinColumn(name = "create_user_id")
	private User 					createuser;
 	//修改时间
 	private Date 					updatetime; 
 	//修改人
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "update_user_id")
 	private User 					updateUser; 
	//商店
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id")
    private Store                   store;
 	//导航名称
 	private String 					name; 
 	//首页图标
 	@OneToOne(cascade = { javax.persistence.CascadeType.REMOVE },fetch = FetchType.LAZY)
	@JoinColumn(name = "navigation_pic_id")
 	private Accessory navigationPic; 
 	//链接地址
 	private String					address; 
 	/**
     * 获取创建人
     */	
	public User getCreateuser() {
		return createuser;
	}
	/**
     * 设置创建人
     */
	public void setCreateuser(User createuser) {
		this.createuser = createuser;
	}
	/**
     * 获取修改人
     */
	public User getUpdateUser() {
		return updateUser;
	}
	/**
     * 设置修改人
     */
	public void setUpdateUser(User updateUser) {
		this.updateUser = updateUser;
	}
	/**
     * 获取导航图标
     */
	public Accessory getNavigationPic() {
		return navigationPic;
	}
	/**
     * 设置修改时间
     */
	public void setNavigationPic(Accessory navigationPic) {
		this.navigationPic = navigationPic;
	}

	/**
     * 获取修改时间
     */
	public java.util.Date getUpdatetime(){
		return this.updatetime;
	}
 		
	/**
     * 设置修改时间
     */
	public void setUpdatetime(java.util.Date updatetime){
		this.updatetime = updatetime;
	}
 		
	/**
     * 获取导航名称
     */
	public java.lang.String getName(){
		return this.name;
	}
 		
	/**
     * 设置导航名称
     */
	public void setName(java.lang.String name){
		this.name = name;
	}
 		
	/**
     * 获取链接地址
     */
	public java.lang.String getAddress(){
		return this.address;
	}
 		
	/**
     * 设置链接地址
     */
	public void setAddress(java.lang.String address){
		this.address = address;
	}
	/**
     * 获取商店
     */
	public Store getStore() {
		return store;
	}
	/**
     * 设置商店
     */
	public void setStore(Store store) {
		this.store = store;
	}
	
 }