-- 机构表
CREATE TABLE IF NOT EXISTS `organization` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '机构ID',
    `org_name` VARCHAR(100) NOT NULL COMMENT '机构名称',
    `org_type` VARCHAR(50) DEFAULT NULL COMMENT '机构类型：COMPANY-公司, DEPARTMENT-部门, TEAM-小组, SCHOOL-学校, CLUB-俱乐部, OTHER-其他',
    `org_short_name` VARCHAR(50) DEFAULT NULL COMMENT '机构简写',
    `description` VARCHAR(1000) DEFAULT NULL COMMENT '机构介绍',
    `parent_id` BIGINT(20) DEFAULT 0 COMMENT '父机构ID，0表示顶级机构',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_org_type` (`org_type`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_org_name` (`org_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='机构表';

-- 插入测试数据（如果表中不存在则插入）
-- 示例：先删除已存在的测试数据（如果需要重新初始化）
-- DELETE FROM `organization` WHERE `org_name` IN ('总公司', '技术部', '市场部');

-- 插入测试数据
INSERT INTO `organization` (`org_name`, `org_type`, `org_short_name`, `description`, `parent_id`, `create_time`, `update_time`, `deleted`)
SELECT '总公司', 'COMPANY', '总公司', '集团公司总部，负责整体战略规划和运营管理', 0, NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `organization` WHERE `org_name` = '总公司' AND `deleted` = 0);

INSERT INTO `organization` (`org_name`, `org_type`, `org_short_name`, `description`, `parent_id`, `create_time`, `update_time`, `deleted`)
SELECT '技术部', 'DEPARTMENT', '技术部', '负责产品研发和技术支持，包括前端、后端、测试等团队', (SELECT `id` FROM `organization` WHERE `org_name` = '总公司' AND `deleted` = 0), NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `organization` WHERE `org_name` = '技术部' AND `deleted` = 0);

INSERT INTO `organization` (`org_name`, `org_type`, `org_short_name`, `description`, `parent_id`, `create_time`, `update_time`, `deleted`)
SELECT '市场部', 'DEPARTMENT', '市场部', '负责市场推广、品牌建设和客户关系管理', (SELECT `id` FROM `organization` WHERE `org_name` = '总公司' AND `deleted` = 0), NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `organization` WHERE `org_name` = '市场部' AND `deleted` = 0);

INSERT INTO `organization` (`org_name`, `org_type`, `org_short_name`, `description`, `parent_id`, `create_time`, `update_time`, `deleted`)
SELECT '前端开发组', 'TEAM', '前端组', '负责Web和移动端前端开发工作', (SELECT `id` FROM `organization` WHERE `org_name` = '技术部' AND `deleted` = 0), NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `organization` WHERE `org_name` = '前端开发组' AND `deleted` = 0);

INSERT INTO `organization` (`org_name`, `org_type`, `org_short_name`, `description`, `parent_id`, `create_time`, `update_time`, `deleted`)
SELECT '后端开发组', 'TEAM', '后端组', '负责服务端API开发和数据库设计', (SELECT `id` FROM `organization` WHERE `org_name` = '技术部' AND `deleted` = 0), NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `organization` WHERE `org_name` = '后端开发组' AND `deleted` = 0);

INSERT INTO `organization` (`org_name`, `org_type`, `org_short_name`, `description`, `parent_id`, `create_time`, `update_time`, `deleted`)
SELECT '足球俱乐部', 'CLUB', '足球俱乐部', '专业足球俱乐部，拥有各级别球队', 0, NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `organization` WHERE `org_name` = '足球俱乐部' AND `deleted` = 0);
