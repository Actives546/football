-- 赛事表
CREATE TABLE IF NOT EXISTS `match` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '赛事ID',
    `match_name` VARCHAR(200) NOT NULL COMMENT '赛事名称',
    `league` VARCHAR(100) NOT NULL COMMENT '联赛/赛事类型',
    `home_team` VARCHAR(100) NOT NULL COMMENT '主队名称',
    `away_team` VARCHAR(100) NOT NULL COMMENT '客队名称',
    `match_time` DATETIME NOT NULL COMMENT '比赛时间',
    `venue` VARCHAR(200) DEFAULT NULL COMMENT '比赛场地',
    `status` VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED' COMMENT '赛事状态：SCHEDULED-未开始, LIVE-进行中, FINISHED-已结束, CANCELLED-已取消',
    `home_score` INT(11) DEFAULT 0 COMMENT '主队得分',
    `away_score` INT(11) DEFAULT 0 COMMENT '客队得分',
    `referee` VARCHAR(100) DEFAULT NULL COMMENT '主裁判',
    `audience` INT(11) DEFAULT NULL COMMENT '观众人数',
    `ticket_price` DECIMAL(10,2) DEFAULT NULL COMMENT '门票价格',
    `description` TEXT DEFAULT NULL COMMENT '赛事描述',
    `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    KEY `idx_league` (`league`),
    KEY `idx_home_team` (`home_team`),
    KEY `idx_away_team` (`away_team`),
    KEY `idx_match_time` (`match_time`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛事表';

-- 插入测试数据
INSERT INTO `match` (`match_name`, `league`, `home_team`, `away_team`, `match_time`, `venue`, `status`, `home_score`, `away_score`, `referee`, `audience`, `ticket_price`, `description`) VALUES
('英超联赛第35轮', '英超联赛', '曼城', '阿森纳', '2024-05-15 20:00:00', '伊蒂哈德球场', 'SCHEDULED', 0, 0, '迈克尔·奥利弗', 53400, 89.99, '英超争冠关键战，曼城主场迎战阿森纳'),
('西甲联赛第36轮', '西甲联赛', '皇家马德里', '巴塞罗那', '2024-05-18 21:00:00', '圣地亚哥·伯纳乌球场', 'SCHEDULED', 0, 0, '希尔·曼萨诺', 80000, 129.99, '西班牙国家德比，皇马主场对阵巴萨'),
('意甲联赛第34轮', '意甲联赛', 'AC米兰', '国际米兰', '2024-05-10 21:45:00', '圣西罗球场', 'FINISHED', 2, 1, '达维德·马萨', 75000, 79.99, '米兰德比，AC米兰主场2-1战胜国际米兰'),
('德甲联赛第32轮', '德甲联赛', '拜仁慕尼黑', '多特蒙德', '2024-05-04 18:30:00', '安联球场', 'FINISHED', 3, 1, '菲利克斯·布里希', 75000, 99.99, '德国国家德比，拜仁3-1战胜多特'),
('法甲联赛第35轮', '法甲联赛', '巴黎圣日耳曼', '马赛', '2024-05-20 21:00:00', '王子公园球场', 'SCHEDULED', 0, 0, '贝努瓦·巴斯蒂安', 48000, 69.99, '法国国家德比，大巴黎主场迎战马赛'),
('欧冠半决赛首回合', '欧冠联赛', '拜仁慕尼黑', '皇家马德里', '2024-04-30 21:00:00', '安联球场', 'FINISHED', 2, 2, '安东尼·泰勒', 75000, 199.99, '欧冠半决赛首回合，拜仁2-2战平皇马'),
('欧冠半决赛次回合', '欧冠联赛', '皇家马德里', '拜仁慕尼黑', '2024-05-08 21:00:00', '圣地亚哥·伯纳乌球场', 'FINISHED', 2, 1, '斯蒂法诺·格拉维纳', 80000, 249.99, '欧冠半决赛次回合，皇马2-1晋级决赛'),
('中超联赛第10轮', '中超联赛', '上海申花', '上海海港', '2024-05-11 19:35:00', '上海体育场', 'FINISHED', 1, 1, '马宁', 45000, 180.00, '上海德比，双方1-1战平');
