package com.javamalls.platform.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

/**团购活动
 *                       
 * @Filename: Group.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_group")
public class Group extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    private String            group_name;
    private Date              beginTime;
    private Date              endTime;
    private Date              joinEndTime;
    private int               status;
    @OneToMany(mappedBy = "group")
    private List<Goods>       goods_list       = new ArrayList<Goods>();
    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER)
    private List<GroupGoods>  gg_list          = new ArrayList<GroupGoods>();

    public List<Goods> getGoods_list() {
        return this.goods_list;
    }

    public void setGoods_list(List<Goods> goods_list) {
        this.goods_list = goods_list;
    }

    public List<GroupGoods> getGg_list() {
        return this.gg_list;
    }

    public void setGg_list(List<GroupGoods> gg_list) {
        this.gg_list = gg_list;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getGroup_name() {
        return this.group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public Date getBeginTime() {
        return this.beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getJoinEndTime() {
        return this.joinEndTime;
    }

    public void setJoinEndTime(Date joinEndTime) {
        this.joinEndTime = joinEndTime;
    }
}
