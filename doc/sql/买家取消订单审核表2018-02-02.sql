CREATE TABLE `jm_order_cancel_audit` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单取消审核表',
  `createtime` datetime DEFAULT NULL,
  `disabled` bit(1) DEFAULT NULL,
  `state` int(11) NOT NULL DEFAULT '1' COMMENT '状态 1：已提交、2：已通过、3：已驳回',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `audit_user_id` bigint(20) DEFAULT NULL COMMENT '审核人id',
  `audit_opinion` longtext COLLATE utf8_bin COMMENT '审核意见（此字段可不使用）',
  `order_id` bigint(20) NOT NULL COMMENT '订单表的id',
  `cancel_content` longtext COLLATE utf8_bin COMMENT '申请说明',
  `cancel_reason` int(11) NOT NULL DEFAULT '1' COMMENT '申请原因 1：我不想买、2：买错了、3：其它原因',
  `store_id` bigint(20) NOT NULL COMMENT '店铺id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_bin
