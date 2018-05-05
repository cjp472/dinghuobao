ALTER TABLE `jm_sys_config` ADD COLUMN `access_token`  VARCHAR(255) DEFAULT NULL COMMENT '平台微信支付accessToken'  ;

ALTER TABLE `jm_store` ADD COLUMN `access_token`  VARCHAR(255) DEFAULT NULL COMMENT '店铺微信支付accessToken'  ;

INSERT INTO jm_payment(createtime,disabled,content,INSTALL,mark,NAME,TYPE,weixin_appId,weixin_appSecret,weixin_partnerId,weixin_partnerKey,weixin_paySignKey)
VALUES(NOW(),FALSE,"平台微信支付",TRUE,"weixin_wap",
"平台微信支付","admin","wxd2ebd50f2e0b9e06","84dad7e0cf64d03ff478f8e68b0b2ced","1230211702","sway2018012315007615369sway20180","MD5");
 