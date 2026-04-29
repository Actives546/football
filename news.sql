-- 新闻表
CREATE TABLE IF NOT EXISTS `news` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '新闻ID',
    `title` VARCHAR(200) NOT NULL COMMENT '新闻标题',
    `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
    `category` VARCHAR(50) NOT NULL COMMENT '新闻分类：赛事新闻/官方通知/精彩回顾',
    `content` TEXT COMMENT '富文本内容',
    `status` VARCHAR(20) NOT NULL DEFAULT '草稿' COMMENT '发布状态：草稿/已发布/已下架',
    `view_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '浏览量',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    KEY `idx_title` (`title`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='新闻表';

-- 插入测试数据
INSERT INTO `news` (`title`, `cover_image`, `category`, `content`, `status`, `view_count`) VALUES
('2024赛季英超联赛第35轮精彩回顾', 'https://example.com/news1.jpg', '精彩回顾', '<p>2024赛季英超联赛第35轮比赛精彩回顾，曼城主场迎战阿森纳，最终曼城以2-1战胜对手...</p>', '已发布', 1256),
('关于2024赛季中超联赛赛程调整的通知', 'https://example.com/news2.jpg', '官方通知', '<p>各位球迷朋友，因特殊原因，2024赛季中超联赛第15轮至第20轮的比赛时间将进行调整...</p>', '已发布', 892),
('欧冠半决赛首回合：拜仁慕尼黑vs皇家马德里前瞻', 'https://example.com/news3.jpg', '赛事新闻', '<p>北京时间4月24日凌晨3点，欧冠半决赛首回合将在安联球场展开较量，拜仁慕尼黑主场迎战皇家马德里...</p>', '已发布', 2341),
('2024赛季足协杯1/4决赛对阵情况公布', 'https://example.com/news4.jpg', '赛事新闻', '<p>2024赛季足协杯1/4决赛对阵情况已经公布，具体比赛时间和地点如下...</p>', '草稿', 0),
('2024赛季亚冠联赛小组赛精彩进球集锦', 'https://example.com/news5.jpg', '精彩回顾', '<p>2024赛季亚冠联赛小组赛已经结束，让我们一起来回顾一下那些精彩的进球瞬间...</p>', '已下架', 567);
