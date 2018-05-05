package com.javamalls.platform.test;

import java.beans.PropertyDescriptor;

import com.easyjf.beans.BeanWrapper;
import com.javamalls.base.tools.CommUtil;

public class TestField {
    public static void main(String[] args) throws ClassNotFoundException {
        String field = "store.grade";
        if (field.indexOf(".") > 0) {
            Class entity = Class.forName("com.javamalls.domain."
                                         + CommUtil.first2upper(field.substring(
                                             field.indexOf("_") + 1, field.indexOf("."))));

            String propertyName = field.substring(field.indexOf(".") + 1);
            System.out.println("属性值:" + propertyName);
            BeanWrapper entity_wrapper = new BeanWrapper(entity);
            PropertyDescriptor[] entity_propertys = entity_wrapper.getPropertyDescriptors();
            for (PropertyDescriptor pd : entity_propertys) {
                if (pd.getName().equals(propertyName)) {
                    System.out.println(pd.getName());
                }
            }
        }
    }
}
