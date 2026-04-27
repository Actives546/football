-- 人员信息表
-- 人员类型：PERSON-人员，PLAYER-球员
-- 性别：0-未知，1-男，2-女
-- 状态：0-禁用，1-正常

DROP TABLE IF EXISTS `person`;
CREATE TABLE `person` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `org_id` BIGINT(20) NOT NULL COMMENT '所属部门ID',
    `person_type` VARCHAR(20) NOT NULL DEFAULT 'PERSON' COMMENT '人员类型：PERSON-人员，PLAYER-球员',
    `person_name` VARCHAR(50) NOT NULL COMMENT '人员姓名',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `gender` INT(1) DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    `birthday` DATE DEFAULT NULL COMMENT '出生日期',
    `id_card` VARCHAR(18) DEFAULT NULL COMMENT '身份证号',
    `address` VARCHAR(200) DEFAULT NULL COMMENT '地址',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `status` INT(1) DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` INT(1) DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_org_id` (`org_id`),
    KEY `idx_person_type` (`person_type`),
    KEY `idx_person_name` (`person_name`),
    KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员信息表';
