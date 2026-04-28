-- 场地表
CREATE TABLE IF NOT EXISTS `stadium` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '场地ID',
    `stadium_name` VARCHAR(100) NOT NULL COMMENT '场地名称',
    `address` VARCHAR(300) DEFAULT NULL COMMENT '详细地址',
    `capacity` INT DEFAULT 0 COMMENT '容纳人数',
    `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `photo_url` VARCHAR(500) DEFAULT NULL COMMENT '场地照片URL',
    `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_stadium_name` (`stadium_name`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_stadium_name` (`stadium_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场地表';

-- 插入测试数据
INSERT INTO `stadium` (`stadium_name`, `address`, `capacity`, `contact_phone`, `photo_url`, `status`, `remark`) VALUES
('老特拉福德球场', '英国曼彻斯特斯特雷特福德', 76212, '0161-8688000', 'https://example.com/oldtrafford.jpg', 1, '曼联主场球场'),
('安菲尔德球场', '英国利物浦安菲尔德', 61276, '0151-2632361', 'https://example.com/anfield.jpg', 1, '利物浦主场球场'),
('斯坦福桥球场', '英国伦敦富勒姆', 41631, '020-73869373', 'https://example.com/stanfordbridge.jpg', 1, '切尔西主场球场'),
('伊蒂哈德球场', '英国曼彻斯特', 55017, '0161-4441894', 'https://example.com/etihad.jpg', 1, '曼城主场球场'),
('伯纳乌球场', '西班牙马德里', 81044, '913984300', 'https://example.com/bernabeu.jpg', 1, '皇家马德里主场球场'),
('万达大都会球场', '西班牙马德里', 68456, '913664707', 'https://example.com/metropolitano.jpg', 0, '马竞主场球场（暂时关闭）');
