package com.javamalls.front.web.tools;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.javamalls.platform.domain.Accessory;
import com.javamalls.platform.domain.SysConfig;
import com.javamalls.platform.service.ISysConfigService;

/**图片验证码
 *                       
 * @Filename: ImageViewTools.java
 * @Version: 2.7.0
 * @Author: 王阳
 * @Email: wangyang@163.com
 *
 */
@Component
public class ImageViewTools {
    @Autowired
    private ISysConfigService configService;

    public String random_login_img() {
        String img = "";
        SysConfig config = this.configService.getSysConfig();
        if (config.getLogin_imgs().size() > 0) {
            Random random = new Random();
            Accessory acc = (Accessory) config.getLogin_imgs().get(
                random.nextInt(config.getLogin_imgs().size()));
            img = acc.getPath() + "/" + acc.getName();
        } else {
            img = "resources/style/common/images/login_img.jpg";
        }
        return img;
    }
}
