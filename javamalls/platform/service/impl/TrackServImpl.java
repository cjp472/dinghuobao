package com.javamalls.platform.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.base.query.GenericPageList;
import com.javamalls.base.query.PageObject;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.platform.domain.Track;
import com.javamalls.platform.service.ITrackService;

@Service
@Transactional
public class TrackServImpl implements ITrackService {

    @Resource(name = "trackDAO")
    private GenericDAO<Track> trackDAO;

    @Override
    public void save(Track testDrive) {
        this.trackDAO.save(testDrive);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public IPageList list(IQueryObject properties) {
        if (properties == null) {
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(Track.class, query, params, this.trackDAO);
        if (properties != null) {
            PageObject pageObj = properties.getPageObj();
            if (pageObj != null) {
                pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj.getCurrentPage()
                    .intValue(), pageObj.getPageSize() == null ? 0 : pageObj.getPageSize()
                    .intValue());
            }
        }
        return pList;
    }

    @Override
    public Track getObjById(Long id) {
        return (Track) this.trackDAO.get(id);
    }

    @Override
    public void update(Track testDrive) {
        this.trackDAO.update(testDrive);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Track> query(String paramString, Map<String, Object> paramMap, int paramInt1,
                             int paramInt2) {
        return this.trackDAO.query(paramString, paramMap, paramInt1, paramInt2);
    }

    @Override
    public boolean delete(Long id) {
        this.trackDAO.remove(id);
        return true;
    }

    @Override
    public boolean batchDelete(List<Serializable> paramList) {
        for (Serializable id : paramList) {
            delete((Long) id);
        }
        return true;
    }

	@Override
	public IPageList noLastList(IQueryObject properties) {
        if (properties == null) {
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(Track.class, query, params, this.trackDAO);
        if (properties != null) {
            PageObject pageObj = properties.getPageObj();
            if (pageObj != null) {
                pList.doNolastList(pageObj.getCurrentPage() == null ? 0 : pageObj.getCurrentPage()
                    .intValue(), pageObj.getPageSize() == null ? 0 : pageObj.getPageSize()
                    .intValue());
            }
        }
        return pList;
    }

}
