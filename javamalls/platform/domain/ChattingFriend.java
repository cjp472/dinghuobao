package com.javamalls.platform.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_chatfriend")
public class ChattingFriend extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    @ManyToOne(fetch = FetchType.LAZY)
    private User              user;
    @ManyToOne(fetch = FetchType.LAZY)
    private User              friendUser;

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getFriendUser() {
        return this.friendUser;
    }

    public void setFriendUser(User friendUser) {
        this.friendUser = friendUser;
    }
}
