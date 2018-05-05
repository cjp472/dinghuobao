package com.javamalls.platform.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

/**
 * 
 * @author hamj
 * 仓库表
 *Field		Type	Comment
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '仓库表',
  `createtime` datetime DEFAULT NULL,
  `disabled` bit(1) NOT NULL,
  `name` varchar(255) DEFAULT NULL COMMENT '仓库名称',
  `manager` varchar(255) DEFAULT NULL COMMENT '仓库负责人',
  `warehouse_area` varchar(255) DEFAULT NULL COMMENT '仓库面积',
  `address` varchar(255) DEFAULT NULL COMMENT '详细地址',
  `status` int(1) DEFAULT '0' COMMENT '是否默认0否 1是',
  `store_id` bigint(20) DEFAULT NULL COMMENT '店铺Id',
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_warehouse")
public class Warehouse extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long       serialVersionUID  = 1L;
  
    private  String 		name;//仓库名称
    private  String			manager;//仓库负责人
    private  String  	    warehouse_area;//仓库面积
    private  String 		address;//详细地址
    private  Integer		status;//是否默认0否 1是
    @ManyToOne(fetch = FetchType.LAZY)
    private  Store			store;//对应商品
    
    
    
    @OneToMany(fetch=FetchType.LAZY,mappedBy = "warehouse")
    private List<PurchaseOrder>            warehouse_list       = new ArrayList<PurchaseOrder>();//仓库采购入库单列表
    
    /**
     * 仓库采购入库单列表
     * @return
     */
	public List<PurchaseOrder> getWarehouse_list() {
		return warehouse_list;
	}
	/**
	 * 仓库采购入库单列表
	 * @param warehouse_list
	 */
	public void setWarehouse_list(List<PurchaseOrder> warehouse_list) {
		this.warehouse_list = warehouse_list;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getManager() {
		return manager;
	}
	public void setManager(String manager) {
		this.manager = manager;
	}
	public String getWarehouse_area() {
		return warehouse_area;
	}
	public void setWarehouse_area(String warehouse_area) {
		this.warehouse_area = warehouse_area;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Store getStore() {
		return store;
	}
	public void setStore(Store store) {
		this.store = store;
	}
    
   
    
    
    
}