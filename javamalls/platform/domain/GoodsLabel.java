package com.javamalls.platform.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.javamalls.base.domain.CommonEntity;
@Entity
@Table(name = "jm_goods_label")
public class GoodsLabel extends CommonEntity {
	private static final long serialVersionUID = 7721036155074989894L;
 	//修改时间
 	private Date updatetime; 
 	//创建人
 	@ManyToOne(fetch = FetchType.LAZY)
 	@JoinColumn(name = "createuser")
 	private User createuser; 
 	//修改人
 	@ManyToOne(fetch = FetchType.LAZY)
 	@JoinColumn(name = "updateuser")
 	private User updateuser; 
 	//标签名称
 	private String name; 
 	//排序
 	private Integer sequence; 
 	//是否显示：0是1否
 	private Integer status; 
 	//商店
 	@ManyToOne(fetch = FetchType.LAZY)
 	@JoinColumn(name = "store_id")
 	private Store store; 
 	
 	@Transient
 	private List<GoodslabelGoods> goodslabelGoodsList = new ArrayList<GoodslabelGoods>();
 	
 	
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
	public User getUpdateuser() {
		return updateuser;
	}
 		
	/**
     * 设置修改人
     */
	public void setUpdateuser(User updateuser) {
		this.updateuser = updateuser;
	}
 		
	/**
     * 获取标签名称
     */
	public java.lang.String getName(){
		return this.name;
	}
 		
	

	/**
     * 设置标签名称
     */
	public void setName(java.lang.String name){
		this.name = name;
	}
 		
 		
	/**
     * 获取排序
     */
	public java.lang.Integer getSequence(){
		return this.sequence;
	}
 		
	/**
     * 设置排序
     */
	public void setSequence(java.lang.Integer sequence){
		this.sequence = sequence;
	}
 		
 		
	/**
     * 获取是否显示：0是1否
     */
	public java.lang.Integer getStatus(){
		return this.status;
	}
 		
	/**
     * 设置是否显示：0是1否
     */
	public void setStatus(java.lang.Integer status){
		this.status = status;
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

	public List<GoodslabelGoods> getGoodslabelGoodsList() {
		return goodslabelGoodsList;
	}

	public void setGoodslabelGoodsList(List<GoodslabelGoods> goodslabelGoodsList) {
		this.goodslabelGoodsList = goodslabelGoodsList;
	}
	
	
	
 }