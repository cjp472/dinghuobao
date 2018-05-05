package com.javamalls.platform.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;
/**
 * 
 * 订单取消审核表 （用户已付款，卖家未发货前，用户申请取消订单操作时产生此表数据）
 * @author cjl
 *
 *
CREATE TABLE `jm_order_cancel_audit` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单取消审核表',
  `createtime` datetime DEFAULT NULL,
  `disabled` bit(1) DEFAULT NULL,
  `state` int(11) NOT NULL DEFAULT '1' COMMENT '状态 1：已提交、2：已通过、3：已驳回',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `audit_user_id` bigint(20) DEFAULT NULL COMMENT '审核人id',
  `audit_opinion` longtext COLLATE utf8_bin COMMENT '审核意见（此字段可不使用）',
  `order_id` bigint(20) NOT NULL COMMENT '订单表的id',
  `cancel_content` longtext COLLATE utf8_bin COMMENT '申请说明',
  `cancel_reason` int(11) NOT NULL DEFAULT '1' COMMENT '申请原因 1：我不想买、2：买错了、3：其它原因',
  `store_id` bigint(20) NOT NULL COMMENT '店铺id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_bin

 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_order_cancel_audit")
public class OrderFormCancelAudit extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    private int            	  state;//状态 1：已提交、2：已通过、3：已驳回
    private Date		      audit_time;//审核时间
    @ManyToOne
    @JoinColumn(name="audit_user_id")
    private User			  audit_user;//审核人
    @Lob
    @Column(columnDefinition = "LongText")
    private String			  audit_opinion;//审核意见（此字段可不使用）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    private OrderForm         of;//订单
    @Lob
    @Column(columnDefinition = "LongText")
    private String            cancel_content;//申请说明
    private int				  cancel_reason;//申请原因 1：我不想买、2：买错了、3：其它原因
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="store_id")
    private Store	         store;//店铺
    
    /**
     * 店铺
     * @return
     */
    public Store getStore() {
		return store;
	}
    /**
     * 店铺
     * @param store
     */
	public void setStore(Store store) {
		this.store = store;
	}
	/**
     * 申请说明
     * @return
     */
    public String getCancel_content() {
		return cancel_content;
	}
    /**
     * 申请说明
     * @param cancel_content
     */
	public void setCancel_content(String cancel_content) {
		this.cancel_content = cancel_content;
	}
	/**
	 * 申请原因 1：我不想买、2：买错了、3：其它原因
	 * @return
	 */
	public int getCancel_reason() {
		return cancel_reason;
	}
	/**
	 * 申请原因 1：我不想买、2：买错了、3：其它原因
	 * @param cancel_reason
	 */
	public void setCancel_reason(int cancel_reason) {
		this.cancel_reason = cancel_reason;
	}
	/**
     * 状态 1：已提交、2：已通过、3：已驳回
     * @return
     */
	public int getState() {
		return state;
	}
	/**
	 * 状态 1：已提交、2：已通过、3：已驳回
	 * @param state
	 */
	public void setState(int state) {
		this.state = state;
	}
	/**
	 * 审核时间
	 * @return
	 */
	public Date getAudit_time() {
		return audit_time;
	}
	/**
	 * 审核时间
	 * @param audit_time
	 */
	public void setAudit_time(Date audit_time) {
		this.audit_time = audit_time;
	}
	/**
	 * 审核人
	 * @return
	 */
	public User getAudit_user() {
		return audit_user;
	}
	/**
	 * 审核人
	 * @param audit_user
	 */
	public void setAudit_user(User audit_user) {
		this.audit_user = audit_user;
	}
	/**
	 * 审核意见（此字段可不使用）
	 * @return
	 */
	public String getAudit_opinion() {
		return audit_opinion;
	}
	/**
	 * 审核意见（此字段可不使用）
	 * @param audit_opinion
	 */
	public void setAudit_opinion(String audit_opinion) {
		this.audit_opinion = audit_opinion;
	}
	/**
	 * 订单
	 * @return
	 */
	public OrderForm getOf() {
		return of;
	}
	/**
	 * 订单
	 * @param of
	 */
	public void setOf(OrderForm of) {
		this.of = of;
	}
    
    
    
}
