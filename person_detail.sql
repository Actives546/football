-- 人员详细信息表
-- 存储人员的详细补充信息，区分普通人员和球员
-- 关联 person 表的 person_id 字段

DROP TABLE IF EXISTS `person_detail`;
CREATE TABLE `person_detail` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `person_id` BIGINT(20) NOT NULL COMMENT '关联人员ID',
    `position` VARCHAR(100) DEFAULT NULL COMMENT '职位/岗位（通用）',
    `join_date` DATE DEFAULT NULL COMMENT '入职日期（通用）',
    `education` VARCHAR(50) DEFAULT NULL COMMENT '学历（通用）',
    `major` VARCHAR(100) DEFAULT NULL COMMENT '专业（通用）',
    `school` VARCHAR(200) DEFAULT NULL COMMENT '毕业院校（通用）',
    `emergency_contact` VARCHAR(50) DEFAULT NULL COMMENT '紧急联系人（通用）',
    `emergency_phone` VARCHAR(20) DEFAULT NULL COMMENT '紧急联系电话（通用）',
    `work_experience` TEXT DEFAULT NULL COMMENT '工作经验描述（通用）',
    `jersey_number` INT(3) DEFAULT NULL COMMENT '球衣号码（球员专用）',
    `field_position` VARCHAR(50) DEFAULT NULL COMMENT '场上位置（球员专用）',
    `height` INT(3) DEFAULT NULL COMMENT '身高（cm，球员专用）',
    `weight` INT(3) DEFAULT NULL COMMENT '体重（kg，球员专用）',
    `preferred_foot` VARCHAR(20) DEFAULT NULL COMMENT '惯用脚：LEFT-左脚，RIGHT-右脚，BOTH-双脚',
    `nationality` VARCHAR(50) DEFAULT NULL COMMENT '国籍（球员专用）',
    `team_join_date` DATE DEFAULT NULL COMMENT '入队日期（球员专用）',
    `contract_end_date` DATE DEFAULT NULL COMMENT '合同到期日期（球员专用）',
    `market_value` DECIMAL(12, 2) DEFAULT NULL COMMENT '身价（万元，球员专用）',
    `technical_features` TEXT DEFAULT NULL COMMENT '技术特点描述（球员专用）',
    `past_experience` TEXT DEFAULT NULL COMMENT '过往经历描述（球员专用）',
    `remark` TEXT DEFAULT NULL COMMENT '备注（通用）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` INT(1) DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_person_id` (`person_id`),
    KEY `idx_person_id` (`person_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员详细信息表';
