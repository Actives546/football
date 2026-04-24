-- 赛季表
CREATE TABLE IF NOT EXISTS `season` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '赛季ID',
    `match_id` BIGINT(20) NOT NULL COMMENT '所属赛事ID',
    `season_name` VARCHAR(200) NOT NULL COMMENT '赛季名称',
    `season_year` VARCHAR(20) DEFAULT NULL COMMENT '赛季年份',
    `status` VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED' COMMENT '赛季状态：SCHEDULED-未开始, LIVE-进行中, FINISHED-已结束',
    `start_date` DATE NOT NULL COMMENT '赛季开始日期',
    `end_date` DATE DEFAULT NULL COMMENT '赛季结束日期',
    `description` TEXT DEFAULT NULL COMMENT '赛季描述信息',
    `total_rounds` INT(11) DEFAULT NULL COMMENT '总轮次',
    `current_round` INT(11) DEFAULT 0 COMMENT '当前轮次',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    KEY `idx_match_id` (`match_id`),
    KEY `idx_season_name` (`season_name`),
    KEY `idx_season_year` (`season_year`),
    KEY `idx_status` (`status`),
    KEY `idx_start_date` (`start_date`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛季表';

-- 插入测试数据
INSERT INTO `season` (`match_id`, `season_name`, `season_year`, `status`, `start_date`, `end_date`, `description`, `total_rounds`, `current_round`) VALUES
(1, '2024赛季英超联赛', '2024', 'LIVE', '2024-08-15', '2025-05-25', '2024-2025赛季英格兰足球超级联赛', 38, 35),
(1, '2023赛季英超联赛', '2023', 'FINISHED', '2023-08-12', '2024-05-19', '2023-2024赛季英格兰足球超级联赛', 38, 38),
(2, '2024赛季西甲联赛', '2024', 'LIVE', '2024-08-16', '2025-05-25', '2024-2025赛季西班牙足球甲级联赛', 38, 36),
(3, '2024赛季欧冠联赛', '2024', 'FINISHED', '2024-06-01', '2024-05-25', '2023-2024赛季欧洲冠军联赛', 13, 13);
