package com.javamalls.base.basedao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.support.JpaDaoSupport;

public class GenericEntityDao extends JpaDaoSupport {
    public Object get(Class clazz, Serializable id) {
        if (id == null) {
            return null;
        }
        return getJpaTemplate().find(clazz, id);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<Object> find(Class clazz, final String queryStr, final Map params, final int begin,
                             final int max) {
        final Class claz = clazz;
        List<Object> ret = (List) getJpaTemplate().execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                String clazzName = claz.getName();
                StringBuffer sb = new StringBuffer("select obj from ");
                sb.append(clazzName).append(" obj").append(" where ").append(queryStr);
                Query query = em.createQuery(sb.toString());
                for (Object key : params.keySet()) {
                    query.setParameter(key.toString(), params.get(key));
                }
                if ((begin >= 0) && (max > 0)) {
                    query.setFirstResult(begin);
                    query.setMaxResults(max);
                }
                query.setHint("org.hibernate.cacheable", Boolean.valueOf(true));
                return query.getResultList();
            }
        });
        if ((ret != null) && (ret.size() >= 0)) {
            return ret;
        }
        return new ArrayList();
    }

    public List query(final String queryStr, final Map params, final int begin, final int max) {
        List list = (List) getJpaTemplate().execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                Query query = em.createQuery(queryStr);
                if ((params != null) && (params.size() > 0)) {
                    for (Object key : params.keySet()) {
                        query.setParameter(key.toString(), params.get(key));
                    }
                }
                if ((begin >= 0) && (max > 0)) {
                    query.setFirstResult(begin);
                    query.setMaxResults(max);
                }
                query.setHint("org.hibernate.cacheable", Boolean.valueOf(true));
                return query.getResultList();
            }
        });
        if ((list != null) && (list.size() > 0)) {
            return list;
        }
        return new ArrayList();
    }

    public long queryCount(final String queryStr, final Map params) {
         Long count =(Long)getJpaTemplate().execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                Query query = em.createQuery(queryStr);
                if ((params != null) && (params.size() > 0)) {
                    for (Object key : params.keySet()) {
                        query.setParameter(key.toString(), params.get(key));
                    }
                }
               
                query.setHint("org.hibernate.cacheable", Boolean.valueOf(true));
                List resultList = query.getResultList();
                if(resultList!=null&&resultList.size()>0){
                	return (Long)resultList.get(0);
                }else{
                	return 0L;
                }
                
            }
        });
        
        return count;
    }
    
    public void remove(Class clazz, Serializable id) throws RuntimeException {
        Object object = get(clazz, id);
        if (object != null) {
            try {
                getJpaTemplate().remove(object);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("无法删除");
            }
        }
    }

    public void save(Object instance) {
        getJpaTemplate().persist(instance);
    }

    public Object getBy(Class clazz, final String propertyName, final Object value) {
        final Class claz = clazz;
        List<Object> ret = (List) getJpaTemplate().execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                String clazzName = claz.getName();
                StringBuffer sb = new StringBuffer("select obj from ");
                sb.append(clazzName).append(" obj");
                Query query = null;
                if ((propertyName != null) && (value != null)) {
                    sb.append(" where obj.").append(propertyName).append(" = :value");
                    query = em.createQuery(sb.toString()).setParameter("value", value);
                } else {
                    query = em.createQuery(sb.toString());
                }
                query.setHint("org.hibernate.cacheable", Boolean.valueOf(true));
                return query.getResultList();
            }
        });
        if ((ret != null) && (ret.size() == 1)) {
            return ret.get(0);
        }
        if ((ret != null) && (ret.size() > 1)) {
            throw new IllegalStateException("worning  --more than one object find!!");
        }
        return null;
    }

    public List executeNamedQuery(final String queryName, final Object[] params, final int begin,
                                  final int max) {
        List ret = (List) getJpaTemplate().execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                Query query = em.createNamedQuery(queryName);
                int parameterIndex = 1;
                if ((params != null) && (params.length > 0)) {
                    for (Object obj : params) {
                        query.setParameter(parameterIndex++, obj);
                    }
                }
                if ((begin >= 0) && (max > 0)) {
                    query.setFirstResult(begin);
                    query.setMaxResults(max);
                }
                query.setHint("org.hibernate.cacheable", Boolean.valueOf(true));
                return query.getResultList();
            }
        });
        if ((ret != null) && (ret.size() >= 0)) {
            return ret;
        }
        return new ArrayList();
    }

    public void update(Object instance) {
        getJpaTemplate().merge(instance);
    }

    public List executeNativeNamedQuery(final String nnq) {
        Object ret = getJpaTemplate().execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                Query query = em.createNativeQuery(nnq);
                return query.getResultList();
            }
        });
        return (List) ret;
    }

    public List executeNativeQuery(final String nnq, final Map params, final int begin,
                                   final int max) {
        List ret = (List) getJpaTemplate().execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                Query query = em.createNativeQuery(nnq);
                /*
                int parameterIndex = 1;
                if (params != null)
                {
                  Iterator its = params.keySet().iterator();
                  while (its.hasNext()) {
                    query.setParameter(CommUtil.null2String(its.next()), 
                      params.get(its.next()));
                  }
                }
                */
                if ((params != null) && (params.size() > 0)) {
                    for (Object key : params.keySet()) {
                        query.setParameter(key.toString(), params.get(key));
                    }
                }

                if ((begin >= 0) && (max > 0)) {
                    query.setFirstResult(begin);
                    query.setMaxResults(max);
                }
                return query.getResultList();
            }
        });
        if ((ret != null) && (ret.size() >= 0)) {
            return ret;
        }
        return new ArrayList();
    }

    public List executeNativeQuery(final String nnq, final Object[] params, final int begin,
                                   final int max) {
        List ret = (List) getJpaTemplate().execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                Query query = em.createNativeQuery(nnq);
                int parameterIndex = 1;
                if ((params != null) && (params.length > 0)) {
                    for (Object obj : params) {
                        query.setParameter(parameterIndex++, obj);
                    }
                }
                if ((begin >= 0) && (max > 0)) {
                    query.setFirstResult(begin);
                    query.setMaxResults(max);
                }
                return query.getResultList();
            }
        });
        if ((ret != null) && (ret.size() >= 0)) {
            return ret;
        }
        return new ArrayList();
    }

    public int executeNativeSQL(final String nnq) {
        Object ret = getJpaTemplate().execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                Query query = em.createNativeQuery(nnq);
                query.setHint("org.hibernate.cacheable", Boolean.valueOf(true));
                return Integer.valueOf(query.executeUpdate());
            }
        });
        return ((Integer) ret).intValue();
    }

    public int batchUpdate(final String jpql, final Object[] params) {
        Object ret = getJpaTemplate().execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                Query query = em.createQuery(jpql);
                int parameterIndex = 1;
                if ((params != null) && (params.length > 0)) {
                    for (Object obj : params) {
                        query.setParameter(parameterIndex++, obj);
                    }
                }
                query.setHint("org.hibernate.cacheable", Boolean.valueOf(true));
                return Integer.valueOf(query.executeUpdate());
            }
        });
        return ((Integer) ret).intValue();
    }

    public void flush() {
        getJpaTemplate().execute(new JpaCallback() {
            public Object doInJpa(EntityManager em) throws PersistenceException {
                em.getTransaction().commit();
                return null;
            }
        });
    }
}
