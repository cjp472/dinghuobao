package com.javamalls.base.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解
 *                       
 * @Filename: SecurityMapping.java
 * @Version: 1.0
 * @Author: 王朋
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.METHOD })
public @interface SecurityMapping {

    /**功能名称
     * @return
     */
    String title() default "";

    /**访问的url
     * @return
     */
    String value() default "";

    /**功能节点
     * @return
     */
    String rname() default "";

    String rcode() default "";

    int rsequence() default 0;

    /**功能模块
     * @return
     */
    String rgroup() default "";

    /**功能分类    平台（admin） 买家（buyer） 卖家（seller）
     * @return
     */
    String rtype() default "";

    boolean display() default true;
}