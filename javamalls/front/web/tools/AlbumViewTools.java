package com.javamalls.front.web.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.domain.Accessory;
import com.javamalls.platform.service.IAccessoryService;
import com.javamalls.platform.service.IAlbumService;

/**相册工具类
 *                       
 * @Filename: AlbumViewTools.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Component
public class AlbumViewTools {
    @Autowired
    private IAlbumService     albumService;
    @Autowired
    private IAccessoryService accessoryService;

    public List<Accessory> query_album(String id) {
        List<Accessory> list = new ArrayList();
        if ((id != null) && (!id.equals(""))) {
            Map params = new HashMap();
            params.put("album_id", CommUtil.null2Long(id));
            list = this.accessoryService.query(
                "select obj from Accessory obj where obj.album.id=:album_id", params, -1, -1);
        } else {
            list = this.accessoryService.query(
                "select obj from Accessory obj where obj.album.id is null", null, -1, -1);
        }
        return list;
    }
}
