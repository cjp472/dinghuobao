package com.javamalls.ctrl.admin.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.javamalls.platform.domain.Area;
import com.javamalls.platform.service.IAreaService;

@Component
public class AreaManageTools {
    @Autowired
    private IAreaService areaService;

    public String generic_area_info(Area area) {
        String area_info = "";
        if (area != null) {
            area_info = area.getAreaName() + " ";
            if (area.getParent() != null) {
                String info = generic_area_info(area.getParent());
                area_info = info + area_info;
            }
        }
        return area_info;
    }
}
