package com.javamalls.front.web.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.domain.Area;
import com.javamalls.platform.service.IAreaService;

/**获取省市区工具类
 *                       
 * @Filename: AreaViewTools.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Component
public class AreaViewTools {
    @Autowired
    private IAreaService areaService;

    /**
     * 比如传入广州市的ID，返回广东省广州市
     * @param area_id
     * @return
     */
    public String generic_area_info(String area_id) {
        String area_info = "";
        Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
        if (area != null) {
            area_info = area.getAreaName() + " ";
            if (area.getParent() != null) {
                String info = generic_area_info(area.getParent().getId().toString());
                area_info = info + area_info;
            }
        }
        return area_info;
    }
}
