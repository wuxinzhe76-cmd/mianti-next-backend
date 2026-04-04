-- 判题系统相关表结构
-- @author Charles
-- @date 2026-03-31

use mianti;

-- 1. 题目类型表（支持选择题、填空题、编程题等）
create table if not exists question_type
(
    id          bigint auto_increment comment 'id' primary key,
    typeName    varchar(256)                       not null comment '类型名称',
    typeCode    varchar(100)                       not null comment '类型代码（PROGRAMMING/CHOICE/FILL_IN）',
    description text                               null comment '类型描述',
    userId      bigint                             not null comment '创建用户 id',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    unique key uk_typeCode (typeCode)
) comment '题目类型表' collate = utf8mb4_unicode_ci;

-- 2. 编程语言表
create table if not exists programming_language
(
    id          bigint auto_increment comment 'id' primary key,
    languageName varchar(256)                      not null comment '语言名称',
    languageCode varchar(100)                      not null comment '语言代码（java/python/cpp）',
    version      varchar(100)                      null comment '版本信息',
    compileCommand varchar(512)                    null comment '编译命令',
    runCommand     varchar(512)                    null comment '运行命令',
    icon         varchar(1024)                     null comment '语言图标',
    isActive     tinyint   default 1               not null comment '是否启用',
    userId       bigint                            not null comment '创建用户 id',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                not null comment '是否删除',
    unique key uk_languageCode (languageCode)
) comment '编程语言表' collate = utf8mb4_unicode_ci;

-- 3. 题目表扩展字段（在原有 question 表基础上添加）
-- 注意：实际执行时需要 ALTER TABLE，这里为了清晰单独列出
alter table question add column if not exists type varchar(50) default 'PROGRAMMING' comment '题目类型（PROGRAMMING/CHOICE/FILL_IN）' after answer;
alter table question add column if not exists difficulty varchar(20) default 'MEDIUM' comment '难度（EASY/MEDIUM/HARD）' after type;
alter table question add column if not exists template text null comment '代码模板（JSON 格式，存储各语言的初始模板）' after difficulty;
alter table question add column if not exists timeLimit int default 1000 comment '时间限制（ms）' after template;
alter table question add column if not exists memoryLimit int default 256 comment '内存限制（MB）' after timeLimit;
alter table question add column if not exists acceptedCount int default 0 comment '通过人数' after memoryLimit;
alter table question add column if not exists submissionCount int default 0 comment '提交次数' after acceptedCount;
alter table question add column if not exists acceptanceRate decimal(5,2) default 0 comment '通过率' after submissionCount;

-- 4. 测试用例表
create table if not exists test_case
(
    id          bigint auto_increment comment 'id' primary key,
    questionId  bigint                             not null comment '题目 id',
    input       text                               not null comment '输入样例',
    output      text                               not null comment '输出样例',
    isExample   tinyint  default 0                 not null comment '是否为示例测试用例（0-隐藏，1-示例）',
    score       int      default 100               not null comment '分值',
    userId      bigint                             not null comment '创建用户 id',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    index idx_questionId (questionId)
) comment '测试用例表' collate = utf8mb4_unicode_ci;

-- 5. 代码提交表
create table if not exists submission
(
    id              bigint auto_increment comment 'id' primary key,
    questionId      bigint                             not null comment '题目 id',
    userId          bigint                             not null comment '用户 id',
    languageCode    varchar(100)                       not null comment '编程语言代码',
    code            text                               not null comment '提交的代码',
    status          varchar(50)                        not null default 'PENDING' comment '提交状态（PENDING/JUDGING/ACCEPTED/WA/TLE/MLE/RE/CE）',
    executionTime   int                                null comment '执行时间（ms）',
    executionMemory int                                null comment '执行内存（KB）',
    testCaseScore   int                                null comment '测试用例得分',
    totalTestCase   int                                null comment '总测试用例数',
    passedTestCase  int                                null comment '通过的测试用例数',
    errorMessage    text                               null comment '错误信息',
    ip              varchar(100)                       null comment '提交 IP',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_questionId (questionId),
    index idx_userId (userId),
    index idx_status (status)
) comment '代码提交表' collate = utf8mb4_unicode_ci;

-- 6. 判题结果详情表
create table if not exists judge_result
(
    id              bigint auto_increment comment 'id' primary key,
    submissionId    bigint                             not null comment '提交 id',
    questionId      bigint                             not null comment '题目 id',
    userId          bigint                             not null comment '用户 id',
    languageCode    varchar(100)                       not null comment '编程语言代码',
    code            text                               not null comment '提交的代码',
    verdict         varchar(50)                        not null comment '判题结果（ACCEPTED/WA/TLE/MLE/RE/CE）',
    executionTime   int                                null comment '执行时间（ms）',
    executionMemory int                                null comment '执行内存（KB）',
    passedTestCase  int                                null comment '通过的测试用例数',
    totalTestCase   int                                null comment '总测试用例数',
    testCaseResults text                               null comment '各测试用例结果（JSON 数组）',
    compileOutput   text                               null comment '编译输出',
    runOutput       text                               null comment '运行输出',
    errorMessage    text                               null comment '错误信息',
    judgeServer     varchar(256)                       null comment '判题服务器',
    judgeTime       datetime                           null comment '判题时间',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    index idx_submissionId (submissionId),
    index idx_userId (userId)
) comment '判题结果详情表' collate = utf8mb4_unicode_ci;

-- 7. 用户答题记录表（用于统计和个人中心）
create table if not exists user_answer
(
    id          bigint auto_increment comment 'id' primary key,
    userId      bigint                             not null comment '用户 id',
    questionId  bigint                             not null comment '题目 id',
    status      varchar(50)                        not null comment '答题状态（SOLVED/ATTEMPTING）',
    bestVerdict varchar(50)                        null comment '最佳判题结果',
    bestTime    int                                null comment '最佳执行时间（ms）',
    bestMemory  int                                null comment '最佳执行内存（KB）',
    attemptCount int     default 0                 not null comment '尝试次数',
    solvedTime  datetime                           null comment '首次通过时间',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    unique key uk_user_question (userId, questionId),
    index idx_userId (userId)
) comment '用户答题记录表' collate = utf8mb4_unicode_ci;

-- 8. 用户收藏题目表（错题本功能）
create table if not exists user_favorite_question
(
    id          bigint auto_increment comment 'id' primary key,
    userId      bigint                             not null comment '用户 id',
    questionId  bigint                             not null comment '题目 id',
    tags        varchar(512)                       null comment '自定义标签（如：错题/收藏/重点）',
    note        text                               null comment '个人笔记',
    wrongCount  int      default 0                 not null comment '答错次数',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    unique key uk_user_question (userId, questionId),
    index idx_userId (userId)
) comment '用户收藏题目表' collate = utf8mb4_unicode_ci;

-- 初始化数据
-- 题目类型
insert into question_type (typeName, typeCode, description, userId) values
('编程题', 'PROGRAMMING', '需要编写完整代码的编程题目', 1),
('选择题', 'CHOICE', '单选或多选题目', 1),
('填空题', 'FILL_IN', '填写关键代码或答案的题目', 1);

-- 编程语言
insert into programming_language (languageName, languageCode, version, compileCommand, runCommand, isActive, userId) values
('Java 17', 'java', '17.0.0', 'javac -encoding UTF-8 Main.java', 'java -Xmx256m Main', 1, 1),
('Python 3', 'python', '3.9.0', null, 'python3 -B main.py', 1, 1),
('C++ 17', 'cpp', 'GCC 11.2.0', 'g++ -std=c++17 -O2 -o main main.cpp', './main', 1, 1),
('JavaScript', 'javascript', 'Node.js 16.0.0', null, 'node main.js', 1, 1);
