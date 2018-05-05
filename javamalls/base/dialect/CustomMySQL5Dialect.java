package com.javamalls.base.dialect;

import java.sql.Types;

import org.hibernate.Hibernate;
import org.hibernate.dialect.MySQL5Dialect;

/**
 * hibernate中mysql的varchar类型与数据库中的varchar类型不能对应起来,因此只要在Hibernate里把对应的数据类型成功映射起来就可以成功执行了。
 * @author Administrator
 *
 */
public class CustomMySQL5Dialect extends MySQL5Dialect {
    public CustomMySQL5Dialect() {
        registerHibernateType(Types.LONGVARCHAR, Hibernate.TEXT.getName());
    }
}
