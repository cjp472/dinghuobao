package com.javamalls.base.domain.virtual;

/**系统map类
 *                       
 * @Filename: SysMap.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
public class SysMap {
    private Object key;
    private Object value;

    public SysMap() {
    }

    public SysMap(Object key, Object value) {
        this.key = key;
        this.value = value;
    }

    public Object getKey() {
        return this.key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
