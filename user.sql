-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `password` VARCHAR(255) DEFAULT NULL COMMENT '密码（BCrypt加密）',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入测试用户（密码使用BCrypt加密，默认密码：123456）
-- 可以使用 BCrypt.hashpw("123456", BCrypt.gensalt()) 生成密码
INSERT INTO `user` (`username`, `phone`, `password`, `nickname`, `avatar`, `email`, `status`) VALUES
('admin', '13800138000', '$2a$10$.q8XrBv.XrBv.XrBvXrBve.XrBv.XrBvXrBvXrBvXrBvXrBvXrBv', '管理员', NULL, 'admin@football.com', 1),
('testuser', '13800138001', '$2a$10$.q8XrBv.XrBv.XrBvXrBve.XrBv.XrBvXrBvXrBvXrBvXrBvXrBv', '测试用户', NULL, 'test@football.com', 1);
