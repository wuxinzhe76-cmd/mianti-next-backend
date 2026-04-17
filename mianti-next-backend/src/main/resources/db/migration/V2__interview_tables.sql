CREATE TABLE `interview_session` (
    `id`          BIGINT       NOT NULL COMMENT '主键 (雪花算法)',
    `user_id`     BIGINT       NOT NULL COMMENT '面试者 ID',
    `mode`        TINYINT      NOT NULL COMMENT '模式：1-指定题库，2-大厂随机',
    `bank_id`     BIGINT       DEFAULT NULL COMMENT '关联题库 ID（模式1时有值）',
    `status`      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0-进行中，1-已结束，2-已生成报告',
    `score`       INT          DEFAULT NULL COMMENT '本次面试综合评分 (AI生成)',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '面试开始时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`   TINYINT      NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试会话表';

CREATE TABLE `interview_record` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `session_id`  BIGINT       NOT NULL COMMENT '关联的面试会话 ID',
    `question_id` BIGINT       DEFAULT NULL COMMENT '当前讨论的具体题目 ID',
    `role`        VARCHAR(20)  NOT NULL COMMENT '发送方角色：user 或 assistant',
    `content`     TEXT         NOT NULL COMMENT '具体的回答或提问内容',
    `round_num`   INT          DEFAULT NULL COMMENT '当前对话属于第几轮',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`   TINYINT      NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    INDEX `idx_session_id` (`session_id`),
    INDEX `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试问答明细表';
