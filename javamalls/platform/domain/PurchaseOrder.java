package com.javamalls.platform.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
 * 采购入库表
 * @author cjl
 Field		Type	Comment
id			bigint(20) NOT NULL采购入库表
createtime	datetime NULL
disabled	bit(1) NULL
code		varchar(255) NULL入库单号
storage_time	datetime NULL入库时间
warehouse_id	bigint(20) NULL
purchase_type	int(1) NULL入库类型：1采购入库，2退货入库
agent_id		bigint(20) NULL经办人
supplier_id		bigint(20) NULL供应商
remark			varchar(255) NULL备注
store_id		bigint(20) NULL
type_sum		int(11) NULL商品种类总数
goods_sum		int(11) NULL商品总数量
money_total		decimal(12,2) NULL金额总额
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_purchase_order")
public class PurchaseOrder extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long       serialVersionUID  = 1L;
    private String  	code;//入库单编号
    private Date		storage_time;//入库时间
    @ManyToOne(fetch = FetchType.LAZY)
    private Warehouse 	warehouse;//仓库
    private int		 	purchase_type;//入库类型：1采购入库，2退货入库
    @ManyToOne(fetch = FetchType.LAZY)
    private User		agent;//经办人
    @ManyToOne(fetch = FetchType.LAZY)
    private Supplier    supplier;//供应商
    private String      remark;//备注
    @ManyToOne(fetch = FetchType.LAZY)
    private Store       store;//店铺
    private int			type_sum;//商品种类总数
    private String         goods_sum;//商品总数量,避免长度过长时报错
    private String  money_total;//金额总额,避免长度过长时报错
    
    
    
    @OneToMany(fetch=FetchType.LAZY,mappedBy = "purchaseOrder")
    private List<PurchaseOrderGoodsItem>    purchaseOrderGoodsItem_list= new ArrayList<PurchaseOrderGoodsItem>();//采购入库单货品列表
    
    
    /**
     * 采购入库单货品列表
     * @return
     */
    public List<PurchaseOrderGoodsItem> getPurchaseOrderGoodsItem_list() {
		return purchaseOrderGoodsItem_list;
	}
    /**
     * 采购入库单货品列表
     * @param purchaseOrderGoodsItem_list
     */
	public void setPurchaseOrderGoodsItem_list(
			List<PurchaseOrderGoodsItem> purchaseOrderGoodsItem_list) {
		this.purchaseOrderGoodsItem_list = purchaseOrderGoodsItem_list;
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
	 * 入库时间
	 * @return
	 */
	public Date getStorage_time() {
		return storage_time;
	}
	/**
	 * 入库时间
	 * @param storage_time
	 */
	public void setStorage_time(Date storage_time) {
		this.storage_time = storage_time;
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
	 * 入库类型：1采购入库，2退货入库
	 * @return
	 */
	public int getPurchase_type() {
		return purchase_type;
	}
	/**
	 * 入库类型：1采购入库，2退货入库
	 * @param purchase_type
	 */
	public void setPurchase_type(int purchase_type) {
		this.purchase_type = purchase_type;
	}
	/**
	 * 经办人
	 * @return
	 */
	public User getAgent() {
		return agent;
	}
	/**
	 * 经办人
	 * @param agent
	 */
	public void setAgent(User agent) {
		this.agent = agent;
	}
	/**
	 * 供应商
	 * @return
	 */
	public Supplier getSupplier() {
		return supplier;
	}
	/**
	 * 供应商
	 * @param supplier
	 */
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
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
	/**
	 * 供货商店铺
	 * @return
	 */
	public Store getStore() {
		return store;
	}
	/**
	 * 供货商店铺
	 * @param store
	 */
	public void setStore(Store store) {
		this.store = store;
	}
	/**
	 * 商品种类总数
	 * @return
	 */
	public int getType_sum() {
		return type_sum;
	}
	/**
	 * 商品种类总数
	 * @param type_sum
	 */
	public void setType_sum(int type_sum) {
		this.type_sum = type_sum;
	}
	/**
	 * 商品总数量
	 * @return
	 */
	public String getGoods_sum() {
		return goods_sum;
	}
	/**
	 * 商品总数量
	 * @param goods_sum
	 */
	public void setGoods_sum(String goods_sum) {
		this.goods_sum = goods_sum;
	}
	/**
	 * 金额总额
	 * @return
	 */
	public String getMoney_total() {
		return money_total;
	}
	/**
	 * 金额总额
	 * @param money_total
	 */
	public void setMoney_total(String money_total) {
		this.money_total = money_total;
	}
    
    
    
    
    
    
    
    

}