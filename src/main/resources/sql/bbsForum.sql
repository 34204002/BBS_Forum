-- 创建数据库
CREATE DATABASE IF NOT EXISTS ncsu_bbs
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE ncsu_bbs;

-- 1. 用户表（核心登录与权限信息）
CREATE TABLE IF NOT EXISTS `user`
(
    `id`              INT          NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`        VARCHAR(50)  NOT NULL COMMENT '用户名（唯一）',
    `password`        VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    `email`           VARCHAR(100) NOT NULL COMMENT '电子邮箱（唯一）',
    `role`            VARCHAR(20)  NOT NULL DEFAULT 'user' COMMENT '角色：user-普通用户，admin-管理员',
    `status`          TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `points`          INT          NOT NULL DEFAULT 100 COMMENT '用户积分（注册默认100）',
    `last_login_time` DATETIME              DEFAULT NULL COMMENT '最后登录时间',
    `login_count`     INT          NOT NULL DEFAULT 0 COMMENT '登录次数',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`      TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    KEY `idx_role` (`role`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户表';

-- 2. 用户详情表（扩展个人资料，与用户表一对一）
CREATE TABLE IF NOT EXISTS `user_profile`
(
    `id`            INT      NOT NULL AUTO_INCREMENT COMMENT '详情ID',
    `user_id`       INT      NOT NULL COMMENT '关联用户表ID',
    `nickname`      VARCHAR(50)       DEFAULT NULL COMMENT '昵称',
    `avatar`        VARCHAR(255)      DEFAULT 'default_avatar.png' COMMENT '头像URL',
    `phone`         VARCHAR(20)       DEFAULT NULL COMMENT '联系方式',
    `work_nature`   VARCHAR(50)       DEFAULT NULL COMMENT '工作性质',
    `work_location` VARCHAR(100)      DEFAULT NULL COMMENT '工作地点',
    `signature`     VARCHAR(255)      DEFAULT NULL COMMENT '个人签名',
    `create_time`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    CONSTRAINT `fk_user_profile_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户详情表';

-- 3. 板块表
CREATE TABLE IF NOT EXISTS `boards`
(
    `id`          INT         NOT NULL AUTO_INCREMENT COMMENT '板块ID',
    `name`        VARCHAR(50) NOT NULL COMMENT '板块名称（唯一）',
    `description` VARCHAR(255)         DEFAULT NULL COMMENT '板块描述',
    `sort`        INT         NOT NULL DEFAULT 0 COMMENT '板块排序（数字越小越靠前）',
    `post_count`  INT         NOT NULL DEFAULT 0 COMMENT '板块帖子数',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT(1)  NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`),
    KEY `idx_sort` (`sort`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='板块表';

-- 4. 帖子表
CREATE TABLE IF NOT EXISTS `posts`
(
    `id`            INT          NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
    `user_id`       INT          NOT NULL COMMENT '发布用户ID',
    `board_id`      INT          NOT NULL COMMENT '所属板块ID',
    `title`         VARCHAR(200) NOT NULL COMMENT '帖子标题',
    `content`       TEXT         NOT NULL COMMENT '帖子内容（富文本）',
    `is_top`        TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
    `is_essence`    TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否加精：0-否，1-是',
    `is_demand`     TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否需求帖：0-普通帖，1-需求帖',
    `reward_points` INT          NOT NULL DEFAULT 0 COMMENT '需求帖奖励积分',
    `view_count`    INT          NOT NULL DEFAULT 0 COMMENT '浏览量',
    `like_count`    INT          NOT NULL DEFAULT 0 COMMENT '点赞数',
    `comment_count` INT          NOT NULL DEFAULT 0 COMMENT '回复数',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`    TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_board_id` (`board_id`),
    KEY `idx_is_top` (`is_top`),
    KEY `idx_is_essence` (`is_essence`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_posts_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_posts_board` FOREIGN KEY (`board_id`) REFERENCES `boards` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='帖子表';

CREATE TABLE IF NOT EXISTS `comments`
(
    `id`          INT NOT NULL AUTO_INCREMENT COMMENT '回复ID',
    `user_id`     INT NOT NULL COMMENT '回复用户ID',
    `post_id`     INT NOT NULL COMMENT '所属帖子ID',
    `parent_id`   INT DEFAULT NULL COMMENT '父回复ID（NULL为一级回复）',

    `content`     TEXT NOT NULL COMMENT '回复内容',

    `at_usernames` VARCHAR(255) DEFAULT NULL COMMENT '被@的用户名（多个逗号分隔）',

    `like_count`  INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',

    PRIMARY KEY (`id`),

    KEY `idx_user_id` (`user_id`),
    KEY `idx_post_id` (`post_id`),
    KEY `idx_parent_id` (`parent_id`),

    CONSTRAINT `fk_comments_user`
    FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,

    CONSTRAINT `fk_comments_post`
    FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE,

    CONSTRAINT `fk_comments_parent`
    FOREIGN KEY (`parent_id`) REFERENCES `comments` (`id`) ON DELETE CASCADE
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci COMMENT ='回复表';

-- 6. 点赞记录表（联合唯一索引保证一人只能点赞一次）
CREATE TABLE IF NOT EXISTS `likes`
(
    `id`          INT        NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id`     INT        NOT NULL COMMENT '用户ID',
    `target_type` TINYINT(1) NOT NULL COMMENT '目标类型：1-帖子，2-回复',
    `target_id`   INT        NOT NULL COMMENT '目标ID（帖子ID或回复ID）',
    `create_time` DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_target` (`user_id`, `target_type`, `target_id`),
    KEY `idx_target` (`target_type`, `target_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='点赞记录表';

-- 7. 收藏记录表
CREATE TABLE IF NOT EXISTS `favorites`
(
    `id`          INT      NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id`     INT      NOT NULL COMMENT '用户ID',
    `post_id`     INT      NOT NULL COMMENT '帖子ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_post` (`user_id`, `post_id`),
    KEY `idx_post_id` (`post_id`),
    CONSTRAINT `fk_favorites_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_favorites_post` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='收藏记录表';

-- 8. 积分记录表
CREATE TABLE IF NOT EXISTS `point_records`
(
    `id`          INT          NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id`     INT          NOT NULL COMMENT '用户ID',
    `type`        TINYINT(1)   NOT NULL COMMENT '类型：1-获得，2-消耗',
    `reason`      VARCHAR(100) NOT NULL COMMENT '变动原因',
    `points`      INT          NOT NULL COMMENT '变动积分值',
    `balance`     INT          NOT NULL COMMENT '变动后积分余额',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_point_records_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='积分记录表';

-- 9. 系统日志表
CREATE TABLE IF NOT EXISTS `system_logs`
(
    `id`          INT          NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `admin_id`    INT          NOT NULL COMMENT '管理员ID',
    `operation`   VARCHAR(100) NOT NULL COMMENT '操作内容',
    `ip`          VARCHAR(50)  NOT NULL COMMENT '操作IP',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_admin_id` (`admin_id`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_system_logs_admin` FOREIGN KEY (`admin_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='系统日志表';

-- 10.消息通知表
CREATE TABLE notification (
id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',

user_id INT NOT NULL COMMENT '接收通知的用户ID',

from_user_id INT DEFAULT NULL COMMENT '触发操作的用户ID（点赞/评论/@的人）',

type TINYINT NOT NULL COMMENT '通知类型：1点赞帖子 2点赞评论 3评论帖子 4回复评论 5收藏帖子 6@用户',

target_type TINYINT DEFAULT NULL COMMENT '目标类型：1帖子 2评论',

target_id INT DEFAULT NULL COMMENT '目标ID（帖子ID或评论ID）',

content VARCHAR(255) DEFAULT NULL COMMENT '通知内容（可选，用于展示）',

is_read TINYINT DEFAULT 0 COMMENT '是否已读：0未读 1已读',

create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

INDEX idx_notification_user_id (user_id),

INDEX idx_notification_user_read (user_id, is_read),

INDEX idx_notification_create_time (create_time)
);


-- ==================== 初始测试数据 ====================
-- 插入默认管理员账号（用户名：admin，密码：123456，已BCrypt加密）
INSERT INTO `user` (`username`, `password`, `email`, `role`, `points`)
VALUES ('admin', '$2a$10$CaD4RA5qDa.lke2gMSO3u.zf3WtW6I5OEZgWoGoOqUivfNgxf05c6', 'admin@ncsu.edu.cn', 'admin', 1100);

-- 插入管理员个人资料
INSERT INTO `user_profile` (`user_id`, `nickname`, `signature`)
VALUES (1, '论坛管理员', '欢迎来到南昌大学BBS论坛');

-- 插入默认板块
INSERT INTO `boards` (`name`, `description`, `sort`)
VALUES ('技术交流', '分享编程技术、项目经验、学习资源', 1),
       ('求职招聘', '发布招聘信息、求职经验、内推机会', 2),
       ('校园生活', '讨论校园趣事、活动通知、生活服务', 3),
       ('资源共享', '分享学习资料、软件工具、电子书', 4),
       ('闲聊灌水', '轻松聊天、分享日常、娱乐八卦', 5);

-- 插入初始积分记录（管理员注册积分）
INSERT INTO `point_records` (`user_id`, `type`, `reason`, `points`, `balance`)
VALUES (1, 1, '新用户注册奖励', 100, 1100);