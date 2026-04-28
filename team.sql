-- 球队表
CREATE TABLE IF NOT EXISTS `team` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '球队ID',
    `team_name` VARCHAR(100) NOT NULL COMMENT '球队名称',
    `logo_url` VARCHAR(500) DEFAULT NULL COMMENT '球队Logo URL',
    `region` VARCHAR(100) DEFAULT NULL COMMENT '地区',
    `founding_date` DATE DEFAULT NULL COMMENT '成立时间',
    `home_stadium` VARCHAR(100) DEFAULT NULL COMMENT '主场',
    `head_coach` VARCHAR(50) DEFAULT NULL COMMENT '主教练',
    `contact_person` VARCHAR(50) DEFAULT NULL COMMENT '联系人',
    `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `description` VARCHAR(1000) DEFAULT NULL COMMENT '球队简介',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_team_name` (`team_name`),
    KEY `idx_region` (`region`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_team_name` (`team_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='球队表';

-- 插入测试数据
INSERT INTO `team` (`team_name`, `logo_url`, `region`, `founding_date`, `home_stadium`, `head_coach`, `contact_person`, `status`, `description`, `create_time`, `update_time`, `deleted`)
SELECT '皇家马德里', 'https://example.com/realmadrid.png', '西班牙马德里', '1902-03-06', '圣地亚哥·伯纳乌球场', '安切洛蒂', '弗洛伦蒂诺', 1, '皇家马德里足球俱乐部，简称皇马，是一家位于西班牙首都马德里的足球俱乐部，球队成立于1902年3月6日，前称马德里足球队。', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `team` WHERE `team_name` = '皇家马德里' AND `deleted` = 0);

INSERT INTO `team` (`team_name`, `logo_url`, `region`, `founding_date`, `home_stadium`, `head_coach`, `contact_person`, `status`, `description`, `create_time`, `update_time`, `deleted`)
SELECT '巴塞罗那', 'https://example.com/barcelona.png', '西班牙巴塞罗那', '1899-11-29', '诺坎普球场', '哈维', '拉波尔塔', 1, '巴塞罗那足球俱乐部，简称巴萨，是一家位于西班牙巴塞罗那市的足球俱乐部，西班牙足球甲级联赛传统豪门之一。', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `team` WHERE `team_name` = '巴塞罗那' AND `deleted` = 0);

INSERT INTO `team` (`team_name`, `logo_url`, `region`, `founding_date`, `home_stadium`, `head_coach`, `contact_person`, `status`, `description`, `create_time`, `update_time`, `deleted`)
SELECT '曼彻斯特联', 'https://example.com/manutd.png', '英格兰曼彻斯特', '1878-03-05', '老特拉福德球场', '滕哈格', '阿诺德', 1, '曼彻斯特联足球俱乐部，简称曼联，是一家位于英国曼彻斯特的足球俱乐部，前身为成立于1878年的"牛顿希斯LYR"。', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `team` WHERE `team_name` = '曼彻斯特联' AND `deleted` = 0);

INSERT INTO `team` (`team_name`, `logo_url`, `region`, `founding_date`, `home_stadium`, `head_coach`, `contact_person`, `status`, `description`, `create_time`, `update_time`, `deleted`)
SELECT '利物浦', 'https://example.com/liverpool.png', '英格兰利物浦', '1892-06-03', '安菲尔德球场', '克洛普', '亨利', 1, '利物浦足球俱乐部，简称利物浦，是一家位于英格兰西北部默西赛德郡港口城市利物浦的足球俱乐部，成立于1892年。', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `team` WHERE `team_name` = '利物浦' AND `deleted` = 0);

INSERT INTO `team` (`team_name`, `logo_url`, `region`, `founding_date`, `home_stadium`, `head_coach`, `contact_person`, `status`, `description`, `create_time`, `update_time`, `deleted`)
SELECT '拜仁慕尼黑', 'https://example.com/bayern.png', '德国慕尼黑', '1900-02-27', '安联球场', '图赫尔', '鲁梅尼格', 1, '拜仁慕尼黑足球俱乐部，简称拜仁慕尼黑或拜仁，是一家设于巴伐利亚州首府慕尼黑的德国体育俱乐部，其最著名的是参加德国足球甲级联赛的职业足球队。', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `team` WHERE `team_name` = '拜仁慕尼黑' AND `deleted` = 0);
