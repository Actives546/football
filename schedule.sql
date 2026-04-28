-- 赛程表
CREATE TABLE IF NOT EXISTS `schedule` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '赛程ID',
    `season_id` BIGINT(20) NOT NULL COMMENT '所属赛季ID',
    `schedule_name` VARCHAR(200) NOT NULL COMMENT '赛程名称',
    `status` VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED' COMMENT '赛程状态：SCHEDULED-未开始, LIVE-进行中, FINISHED-已结束',
    `location` VARCHAR(200) DEFAULT NULL COMMENT '比赛地点',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    KEY `idx_season_id` (`season_id`),
    KEY `idx_schedule_name` (`schedule_name`),
    KEY `idx_status` (`status`),
    KEY `idx_location` (`location`),
    KEY `idx_start_time` (`start_time`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛程表';

-- 插入测试数据
INSERT INTO `schedule` (`season_id`, `schedule_name`, `status`, `location`, `start_time`) VALUES
(1, '英超联赛第1轮：曼联vs利物浦', 'FINISHED', '老特拉福德球场', '2024-08-15 20:00:00'),
(1, '英超联赛第1轮：切尔西vs阿森纳', 'FINISHED', '斯坦福桥球场', '2024-08-16 22:00:00'),
(1, '英超联赛第2轮：曼城vs热刺', 'LIVE', '伊蒂哈德球场', '2024-08-24 20:00:00'),
(1, '英超联赛第2轮：利物浦vs切尔西', 'SCHEDULED', '安菲尔德球场', '2024-08-25 22:00:00'),
(2, '西甲联赛第1轮：皇马vs巴塞罗那', 'FINISHED', '伯纳乌球场', '2024-08-16 22:00:00'),
(2, '西甲联赛第2轮：马竞vs瓦伦西亚', 'LIVE', '万达大都会球场', '2024-08-25 20:00:00');
