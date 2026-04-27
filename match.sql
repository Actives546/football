-- 赛事表
CREATE TABLE IF NOT EXISTS `match` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '赛事ID',
    `match_name` VARCHAR(200) NOT NULL COMMENT '赛事名称',
    `match_type` VARCHAR(100) NOT NULL COMMENT '赛事类型',
    `status` VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED' COMMENT '赛事状态：SCHEDULED-未开始, LIVE-进行中, FINISHED-已结束, CANCELLED-已取消',
    `description` TEXT DEFAULT NULL COMMENT '赛事描述信息',
    `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    KEY `idx_match_name` (`match_name`),
    KEY `idx_match_type` (`match_type`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛事表';

-- 插入测试数据
INSERT INTO `match` (`match_name`, `match_type`, `status`, `description`) VALUES
('2024赛季英超联赛第35轮', '足球联赛', 'SCHEDULED', '英超联赛第35轮比赛，曼城主场迎战阿森纳'),
('2024赛季西甲联赛第36轮', '足球联赛', 'SCHEDULED', '西班牙国家德比，皇家马德里主场对阵巴塞罗那'),
('2024赛季欧冠半决赛首回合', '杯赛', 'FINISHED', '欧冠半决赛首回合，拜仁慕尼黑主场迎战皇家马德里'),
('2024赛季中超联赛第10轮', '足球联赛', 'FINISHED', '上海德比，上海申花主场对阵上海海港'),
('2024赛季亚冠联赛小组赛', '杯赛', 'LIVE', '亚冠联赛小组赛，中国球队对阵日本球队'),
('2024赛季足协杯1/4决赛', '杯赛', 'SCHEDULED', '足协杯1/4决赛'),
('2024赛季友谊赛', '友谊赛', 'CANCELLED', '国际友谊赛已取消');
