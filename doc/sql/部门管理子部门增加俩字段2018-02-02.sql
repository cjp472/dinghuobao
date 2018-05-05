ALTER TABLE `jm_store_department` ADD COLUMN `level`  INT(11) DEFAULT '0' COMMENT '层级 默认0'  ;
ALTER TABLE `jm_store_department` ADD COLUMN `parent_id`  BIGINT(20) DEFAULT NULL COMMENT '上级部门id'  ;
 