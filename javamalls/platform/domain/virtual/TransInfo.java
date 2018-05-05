package com.javamalls.platform.domain.virtual;

import java.util.ArrayList;
import java.util.List;

/**物流信息
 *                       
 * @Filename: TransInfo.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
public class TransInfo {
    private String             message;
    private String             status;
    private String             state;
    private List<TransContent> data = new ArrayList<TransContent>();

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<TransContent> getData() {
        return this.data;
    }

    public void setData(List<TransContent> data) {
        this.data = data;
    }
}
