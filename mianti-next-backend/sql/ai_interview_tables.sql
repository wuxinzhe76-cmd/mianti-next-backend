-- AI 面试功能表结构

-- 1. 面试会话表
create table if not exists interview_session
(
    id          bigint                             not null comment '主键 (雪花算法)' primary key,
    user_id     bigint                             not null comment '面试者 ID',
    mode        tinyint                            not null comment '模式：1-指定题库，2-大厂随机',
    bank_id     bigint                             null comment '关联题库 ID（若是模式1则有值）',
    status      tinyint  default 0                 not null comment '状态：0-进行中，1-已结束，2-已生成报告',
    score       int                                null comment '本次面试综合评分 (AI生成)',
    create_time datetime default CURRENT_TIMESTAMP not null comment '面试开始时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0                 not null comment '是否删除',
    index idx_user_id (user_id),
    index idx_bank_id (bank_id)
) comment '面试会话' collate = utf8mb4_unicode_ci;

-- 2. 面试问答明细表
create table if not exists interview_record
(
    id          bigint auto_increment comment '主键' primary key,
    session_id  bigint                             not null comment '关联的面试会话 ID',
    question_id bigint                             null comment '当前讨论的具体题目 ID',
    role        varchar(20)                        not null comment '发送方角色：user 或 assistant',
    content     text                               null comment '具体的回答或提问内容',
    round_num   int                                null comment '当前对话属于第几轮',
    create_time datetime default CURRENT_TIMESTAMP not null comment '记录时间',
    index idx_session_id (session_id)
) comment '面试问答明细' collate = utf8mb4_unicode_ci;
