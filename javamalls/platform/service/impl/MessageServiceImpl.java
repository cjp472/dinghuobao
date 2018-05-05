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
import com.javamalls.platform.domain.Message;
import com.javamalls.platform.service.IMessageService;

@Service
@Transactional
public class MessageServiceImpl implements IMessageService {
    @Resource(name = "messageDAO")
    private IGenericDAO<Message> messageDao;

    public boolean save(Message message) {
        try {
            this.messageDao.save(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Message getObjById(Long id) {
        Message message = (Message) this.messageDao.get(id);
        if (message != null) {
            return message;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.messageDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> messageIds) {
        for (Serializable id : messageIds) {
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
        GenericPageList pList = new GenericPageList(Message.class, query, params, this.messageDao);
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

    public boolean update(Message message) {
        try {
            this.messageDao.update(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Message> query(String query, Map params, int begin, int max) {
        return this.messageDao.query(query, params, begin, max);
        
    }

	@Override
	public IPageList nolastlist(IQueryObject properties) {
        if (properties == null) {
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(Message.class, query, params, this.messageDao);
        if (properties != null) {
            PageObject pageObj = properties.getPageObj();
            if (pageObj != null) {
                pList.doNolastList(pageObj.getCurrentPage() == null ? 0 : pageObj.getCurrentPage()
                    .intValue(), pageObj.getPageSize() == null ? 0 : pageObj.getPageSize()
                    .intValue());
            }
        } else {
            pList.doNolastList(0, -1);
        }
        return pList;
    }

	@Override
	public Long queryCount(String query, Map params) {
        return this.messageDao.queryCount(query, params);
	}
}
