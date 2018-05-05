CREATE TABLE `jm_order_pay_log` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '实体下单 支付方式列表',
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  `disabled` bit(1) NOT NULL,
  `user_id` bigint(11) DEFAULT NULL COMMENT '会员ID',
  `order_id` bigint(11) DEFAULT NULL COMMENT '订单ID',
  `pay_child_class` int(11) DEFAULT NULL COMMENT '支付子类: 1 银联在线,2支付宝,3 微信，4 刷卡（线下）,5 现金（线下）',
  `should_pay_amount` decimal(12,2) DEFAULT NULL COMMENT '应付金额',
  `actual_pay_amount` decimal(12,2) DEFAULT NULL COMMENT '实际支付金额',
  `pay_status` tinyint(4) DEFAULT NULL COMMENT '支付状态,0未支付,1已支付2.已退款',
  `bank_deal_num` varchar(45) DEFAULT NULL COMMENT '银行交易号    ',
  `bank_retmess` longtext COMMENT '银行返回信息',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE,
  CONSTRAINT `jm_order_pay_log_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `jm_user` (`id`),
  CONSTRAINT `order_id` FOREIGN KEY (`order_id`) REFERENCES `jm_order` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
ALTER TABLE `jm_order` ADD COLUMN `clerkCode`  VARCHAR(255) DEFAULT NULL COMMENT '营业员编号'  ;
 


