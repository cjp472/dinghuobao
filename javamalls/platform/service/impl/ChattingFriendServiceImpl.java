package com.javamalls.platform.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javamalls.base.basedao.IGenericDAO;
import com.javamalls.base.query.GenericPageList;
import com.javamalls.base.query.PageObject;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.ChattingFriend;
import com.javamalls.platform.service.IChattingFriendService;

@Service
@Transactional
public class ChattingFriendServiceImpl implements IChattingFriendService {
    @Resource(name = "chattingFriendDAO")
    private IGenericDAO<ChattingFriend> chattingFriendDao;

    public boolean save(ChattingFriend chattingFriend) {
        try {
            this.chattingFriendDao.save(chattingFriend);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ChattingFriend getObjById(Long id) {
        ChattingFriend chattingFriend = (ChattingFriend) this.chattingFriendDao.get(id);
        if (chattingFriend != null) {
            return chattingFriend;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.chattingFriendDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> chattingFriendIds) {
        for (Serializable id : chattingFriendIds) {
            delete((Long) id);
        }
        return true;
    }

    public IPageList list(IQueryObject properties) {
        if (properties == null) {
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(ChattingFriend.class, query, params,
            this.chattingFriendDao);
        if (properties != null) {
            PageObject pageObj = properties.getPageObj();
            if (pageObj != null) {
                pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj.getCurrentPage()
                    .intValue(), pageObj.getPageSize() == null ? 0 : pageObj.getPageSize()
                    .intValue());
            }
        } else {
            pList.doList(0, -1);
        }
        return pList;
    }

    public boolean update(ChattingFriend chattingFriend) {
        try {
            this.chattingFriendDao.update(chattingFriend);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<ChattingFriend> query(String query, Map params, int begin, int max) {
        return this.chattingFriendDao.query(query, params, begin, max);
    }
}
