package com.javamalls.base.tools;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.ModelAndView;

import com.easyjf.beans.BeanUtils;
import com.easyjf.beans.BeanWrapper;
import com.javamalls.base.annotation.FormIgnore;
import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.query.QueryObject;

public class WebForm {

    public void Map2Obj(List<Map> maps, Object obj) {
        BeanWrapper wrapper = new BeanWrapper(obj);
        PropertyDescriptor[] propertys = wrapper.getPropertyDescriptors();
        for (int i = 0; i < propertys.length; i++) {
            String name = propertys[i].getName();
            if ((wrapper.isWritableProperty(name)) && (propertys[i].getWriteMethod() != null)) {
                Object propertyValue = null;
                for (int j = 0; j < maps.size(); j++) {
                    Map map = (Map) maps.get(j);
                    Iterator keys = map.keySet().iterator();
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        if (key.equals(propertys[i].getName())) {
                            FormIgnore fi = null;
                            fi = (FormIgnore) propertys[i].getWriteMethod().getAnnotation(
                                FormIgnore.class);
                            if (fi == null) {
                                try {
                                    Field f = propertys[i].getWriteMethod().getDeclaringClass()
                                        .getDeclaredField(name);
                                    fi = (FormIgnore) f.getAnnotation(FormIgnore.class);
                                } catch (SecurityException e) {
                                    e.printStackTrace();
                                } catch (NoSuchFieldException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (fi == null) {
                                try {
                                    propertyValue = BeanUtils.convertType(map.get(key),
                                        propertys[i].getPropertyType());
                                } catch (Exception e) {
                                    if (propertys[i].getPropertyType().toString().equals("int")) {
                                        propertyValue = Integer.valueOf(0);
                                    }
                                    if (propertys[i].getPropertyType().toString().toLowerCase()
                                        .indexOf("boolean") >= 0) {
                                        propertyValue = Boolean.valueOf(false);
                                    }
                                }
                                wrapper.setPropertyValue(propertys[i].getName(), propertyValue);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 用于填充form表单到实体对象
     * @param <T>
     * @param request
     * @param classType
     * @return
     */
    public <T> T toPo(HttpServletRequest request, Class<T> classType) {
        T obj = null;
        try {
            obj = classType.newInstance();
            Map map = request.getParameterMap();
            Enumeration enum1 = request.getParameterNames();
            List<Map> maps = new ArrayList();
            while (enum1.hasMoreElements()) {
                String paramName = (String) enum1.nextElement();
                String value = request.getParameter(paramName);
                Map m1 = new HashMap();
                m1.put(paramName, value);
                maps.add(m1);
            }
            Map2Obj(maps, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 用于填充form表单到实体对象
     * @param request
     * @param obj
     * @return
     */
    public Object toPo(HttpServletRequest request, Object obj) {
        try {
            Map map = request.getParameterMap();
            Enumeration enum1 = request.getParameterNames();
            List<Map> maps = new ArrayList();
            while (enum1.hasMoreElements()) {
                String paramName = (String) enum1.nextElement();
                String value = request.getParameter(paramName);
                Map m1 = new HashMap();
                m1.put(paramName, value);
                maps.add(m1);
            }
            Map2Obj(maps, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 回写查询条件
     * @param request
     * @param qo  
     * @param classType
     * @param mv
     */
    public void toQueryPo(HttpServletRequest request, QueryObject qo, Class classType,
                          ModelAndView mv) {
        List<SysMap> sms = new ArrayList();
        try {
            Object obj = classType.newInstance();
            BeanWrapper wrapper = new BeanWrapper(obj);
            PropertyDescriptor[] propertys = wrapper.getPropertyDescriptors();
            Map map = request.getParameterMap();
            List<Map> maps = new ArrayList();
            Enumeration enum1 = request.getParameterNames();
            String value = "";
            while (enum1.hasMoreElements()) {
                String paramName = (String) enum1.nextElement();
                value = request.getParameter(paramName);
                Map m1 = new HashMap();
                m1.put(paramName, value);
                maps.add(m1);
            }
            Iterator keyes = maps.iterator();
            while (keyes.hasNext()) {
                Map<String, Object> m = (Map) keyes.next();
                for (String field : m.keySet()) {

                    if (field.indexOf("q_") == 0) {
                        Object para = null;
                        for (PropertyDescriptor pd : propertys) {
                            if (pd.getName().equals(field.substring(2))) {
                                para = BeanUtils.convertType(map.get(field), pd.getPropertyType());
                            }
                        }
                        BeanWrapper entity_wrapper;
                        PropertyDescriptor[] entity_propertys;
                        if (field.indexOf(".") > 0) {
                            Class entity = Class.forName("com.javamalls.platform.domain."
                                                         + CommUtil.first2upper(field.substring(2,
                                                             field.indexOf("."))));

                            String propertyName = field.substring(field.indexOf(".") + 1,
                                field.lastIndexOf("."));
                            entity_wrapper = new BeanWrapper(entity);
                            entity_propertys = entity_wrapper.getPropertyDescriptors();
                            for (PropertyDescriptor pd : entity_propertys) {
                                if (pd.getName().equals(propertyName)) {
                                    BeanWrapper many_to_one_entity = new BeanWrapper(
                                        pd.getPropertyType());
                                    PropertyDescriptor[] many_to_one_entity_propertys = many_to_one_entity
                                        .getPropertyDescriptors();
                                    String many_to_one_propertyname = field.substring(field
                                        .lastIndexOf(".") + 1);
                                    for (PropertyDescriptor many_to_one_pd : entity_propertys) {
                                        if (many_to_one_pd.getName().equals(
                                            many_to_one_propertyname)) {
                                            para = BeanUtils.convertType(map.get(field),
                                                many_to_one_pd.getPropertyType());
                                        }
                                    }
                                }
                            }
                        }
                        boolean add = false;

                        for (int localBeanWrapper1 = 0; localBeanWrapper1 < propertys.length; localBeanWrapper1++) {
                            PropertyDescriptor pd = propertys[localBeanWrapper1];
                            if (field.indexOf(".") < 0) {
                                if (pd.getName().equals(field.substring(2))) {
                                    add = true;
                                }
                            } else if (pd.getName().equals(
                                field.subSequence(field.indexOf(".") + 1, field.lastIndexOf(".")))) {
                                add = true;
                            }
                        }
                        if ((add) && (para != null) && (!para.equals(""))) {
                            if (field.indexOf(".") < 0) {
                                if (para.getClass().toString().endsWith("String")) {
                                    String expression = "like";
                                    qo.addQuery("obj." + field.substring(2),
                                        new SysMap(field.substring(2), "%" + para + "%"),
                                        expression);
                                } else {
                                    qo.addQuery("obj." + field.substring(2),
                                        new SysMap(field.substring(2), para), "=");
                                }
                            } else {
                                String pname = field.substring(field.indexOf(".") + 1);
                                qo.addQuery("obj." + pname, new SysMap(pname.replace(".", "_"),
                                    para), "=");
                            }
                            SysMap sm = new SysMap();
                            sm.setKey(field);
                            sm.setValue(para);
                            sms.add(sm);
                        }
                    }
                }

            }
            mv.addObject("sms", sms);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
