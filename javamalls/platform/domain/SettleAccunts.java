package com.javamalls.platform.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

/**结算
 *                       
 * @Filename: SettleAccunts.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_settle_accounts")
public class SettleAccunts extends CommonEntity implements java.io.Serializable {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    private Integer           settle_count;
    private String            settle_date;
    private Date              updatetime;
    private Integer           msg_type;
    private String            msg_content;
    private double            yongjin;
    private Integer           month;
    private Long              opt_user;
    @ManyToOne(fetch = FetchType.LAZY)
    private User              user;

    public Integer getSettle_count() {
        return settle_count;
    }

    public void setSettle_count(Integer settle_count) {
        this.settle_count = settle_count;
    }

    public String getSettle_date() {
        return settle_date;
    }

    public void setSettle_date(String settle_date) {
        this.settle_date = settle_date;
    }

    public Integer getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(Integer msg_type) {
        this.msg_type = msg_type;
    }

    public String getMsg_content() {
        return msg_content;
    }

    public void setMsg_content(String msg_content) {
        this.msg_content = msg_content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getYongjin() {
        return yongjin;
    }

    public void setYongjin(double yongjin) {
        this.yongjin = yongjin;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Long getOpt_user() {
        return opt_user;
    }

    public void setOpt_user(Long optUser) {
        this.opt_user = optUser;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

}
