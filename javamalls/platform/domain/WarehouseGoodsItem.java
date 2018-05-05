package com.javamalls.platform.domain;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

/**
 * 
 * @author hamj
 * 仓库商品库存表
 *Field		Type	Comment
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createtime` datetime DEFAULT NULL,
  `disabled` bit(1) DEFAULT NULL,
  `goods_item_id` bigint(20) DEFAULT NULL COMMENT '货品',
  `warehouse_id` bigint(20) DEFAULT NULL,
  `warehoust_number` int(11) DEFAULT NULL COMMENT '库存',
  `high_invetory` int(11) DEFAULT '0' COMMENT '上限',
  `min_invetory` int(11) DEFAULT '0' COMMENT '下限',
  `store_id` bigint(20) DEFAULT NULL,
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_warehouse_goods_item")
public class WarehouseGoodsItem extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long       serialVersionUID  = 1L;
  
    @ManyToOne
    private  GoodsItem			goods_item;//货品
    @ManyToOne(fetch = FetchType.LAZY)
    private  Warehouse			warehouse;//仓库
    private Integer warehoust_number;//仓库库存
    private Integer high_invetory;//上线
    private Integer min_invetory;//下限
    @ManyToOne(fetch = FetchType.LAZY)
    private  Store			store;//店铺
    /**
     * 货品
     * @return
     */
	public GoodsItem getGoods_item() {
		return goods_item;
	}
	/**
	 * 货品
	 * @param goods_item
	 */
	public void setGoods_item(GoodsItem goods_item) {
		this.goods_item = goods_item;
	}
	/**
	 * 仓库
	 * @return
	 */
	public Warehouse getWarehouse() {
		return warehouse;
	}
	/**
	 * 仓库
	 * @param warehouse
	 */
	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}
	/**
	 * 仓库库存
	 * @return
	 */
	public Integer getWarehoust_number() {
		return warehoust_number;
	}
	/**
	 * 仓库库存
	 * @param warehoust_number
	 */
	public void setWarehoust_number(Integer warehoust_number) {
		this.warehoust_number = warehoust_number;
	}
	/**
	 * 上限
	 * @return
	 */
	public Integer getHigh_invetory() {
		return high_invetory;
	}
	/**
	 * 上限
	 * @param high_invetory
	 */
	public void setHigh_invetory(Integer high_invetory) {
		this.high_invetory = high_invetory;
	}
	/**
	 * 下限
	 * @return
	 */
	public Integer getMin_invetory() {
		return min_invetory;
	}
	/**
	 * 下限
	 * @param min_invetory
	 */
	public void setMin_invetory(Integer min_invetory) {
		this.min_invetory = min_invetory;
	}
	public Store getStore() {
		return store;
	}
	public void setStore(Store store) {
		this.store = store;
	}
	
    
    
}