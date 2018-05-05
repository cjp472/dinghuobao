package com.javamalls.platform.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.javamalls.base.domain.CommonEntity;
/**
 * @author cjl
 * 部门表
CREATE TABLE `jm_store_department` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  `disabled` bit(1) NOT NULL COMMENT '是否弃用',
  `sequence` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `title` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '名称',
  `store_id` bigint(20) NOT NULL COMMENT '店铺id',
  `level` int(11) NOT NULL DEFAULT '1' COMMENT '层级 默认0',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '上级部门id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_store_department")
public class StoreDepartment extends CommonEntity {
	 
    private String  title;//部门名称
    private int  sequence;//排序：数字越小，排序越靠前
    @ManyToOne(fetch = FetchType.LAZY)
    private Store   store;//店铺id
    private int	 level;//层级： 默认0
    @ManyToOne
    @JoinColumn(name="parent_id")
    private StoreDepartment   parent;//上级部门
    @OneToMany(mappedBy = "department")
    private List<User>             salemans               = new ArrayList<User>();//部门下面的业务员列表
    
    
    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY,mappedBy="parent")
    @Where(clause = "disabled=0")
    private List<StoreDepartment>   childs;//子部门列表
    
    
    /**
     * 子部门列表
     * @return
     */
	public List<StoreDepartment> getChilds() {
		return childs;
	}
	/**
	 * 子部门列表
	 * @param childs
	 */
	public void setChilds(List<StoreDepartment> childs) {
		this.childs = childs;
	}
	/**
     * 层级： 默认0
     * @return
     */
    public int getLevel() {
		return level;
	}
    /**
     * 层级： 默认0
     * @param level
     */
	public void setLevel(int level) {
		this.level = level;
	}
	/**
	 * 上级部门
	 * @return
	 */
	public StoreDepartment getParent() {
		return parent;
	}
	/**
	 * 上级部门
	 * @param parent
	 */
	public void setParent(StoreDepartment parent) {
		this.parent = parent;
	}
	/**
     * 部门下面的业务员列表
     * @return
     */
    public List<User> getSalemans() {
		return salemans;
	}
    /**
     * 部门下面的业务员列表
     * @param salemans
     */
	public void setSalemans(List<User> salemans) {
		this.salemans = salemans;
	}
	/**
     * 部门名称
     * @return
     */
	public String getTitle() {
		return title;
	}
	/**
	 * 部门名称
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * 排序：数字越小，排序越靠前
	 * @return
	 */
	public int getSequence() {
		return sequence;
	}
	/**
	 * 排序：数字越小，排序越靠前
	 * @param sequence
	 */
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	/**
	 * 店铺id
	 * @return
	 */
	public Store getStore() {
		return store;
	}
	/**
	 * 店铺id
	 * @param store
	 */
	public void setStore(Store store) {
		this.store = store;
	} 
}
