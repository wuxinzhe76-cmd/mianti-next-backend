package com.charles.mianti.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charles.mianti.common.ErrorCode;
import com.charles.mianti.config.InterviewPromptConstants;
import com.charles.mianti.constant.InterviewRedisConstants;
import com.charles.mianti.exception.BusinessException;
import com.charles.mianti.exception.ThrowUtils;
import com.charles.mianti.mapper.QuestionBankQuestionMapper;
import com.charles.mianti.mapper.QuestionMapper;
import com.charles.mianti.model.dto.interview.AiInterviewResponseDTO;
import com.charles.mianti.model.entity.InterviewRecord;
import com.charles.mianti.model.entity.InterviewSession;
import com.charles.mianti.model.entity.Question;
import com.charles.mianti.model.entity.QuestionBankQuestion;
import com.charles.mianti.model.enums.ActionDirectiveEnum;
import com.charles.mianti.model.enums.InterviewModeEnum;
import com.charles.mianti.mapper.InterviewRecordMapper;
import com.charles.mianti.mapper.InterviewSessionMapper;
import com.charles.mianti.mq.InterviewReportProducer;
import com.charles.mianti.service.AiInterviewStrategyService;
import com.charles.mianti.service.InterviewService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class InterviewServiceImpl implements InterviewService {

    @Resource
    private InterviewSessionMapper interviewSessionMapper;

    @Resource
    private InterviewRecordMapper interviewRecordMapper;

    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private QuestionBankQuestionMapper questionBankQuestionMapper;

    @Resource
    private AiInterviewStrategyService aiInterviewStrategyService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private InterviewReportProducer reportProducer;

    @Override
    public Long startInterview(Long userId, Integer mode, Long bankId) {
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR, "用户 ID 非法");
        InterviewModeEnum modeEnum = InterviewModeEnum.getEnumByValue(mode);
        ThrowUtils.throwIf(modeEnum == null, ErrorCode.PARAMS_ERROR, "面试模式非法");
        if (modeEnum == InterviewModeEnum.SPECIFIED_BANK) {
            ThrowUtils.throwIf(bankId == null || bankId <= 0, ErrorCode.PARAMS_ERROR, "指定题库模式下必须传 bankId");
        }

        // 按模式抽取第一道题
        Question firstQuestion = pickQuestion(mode, bankId);
        ThrowUtils.throwIf(firstQuestion == null, ErrorCode.NOT_FOUND_ERROR, "未找到可用的题目");

        // 创建会话
        InterviewSession session = new InterviewSession();
        session.setUserId(userId);
        session.setMode(mode);
        session.setBankId(bankId);
        session.setStatus(0);
        interviewSessionMapper.insert(session);

        Long sessionId = session.getId();

        // 将热数据写入 Redis：当前题目、轮次、对话历史
        cacheQuestion(sessionId, firstQuestion.getId());
        setRound(sessionId, 0);
        RSet<Long> usedQuestions = getUsedQuestions(sessionId);
        usedQuestions.add(firstQuestion.getId());

        // 生成 AI 开场提问
        String openingQuestion = aiInterviewStrategyService.generateOpeningQuestion(firstQuestion);

        // 将首轮提问加入对话历史（Redis）
        appendHistory(sessionId, "assistant", openingQuestion);

        log.info("Interview session started, sessionId={}, firstQuestionId={}", sessionId, firstQuestion.getId());
        return sessionId;
    }

    @Override
    public AiInterviewResponseDTO answerQuestion(Long sessionId, String answer) {
        ThrowUtils.throwIf(sessionId == null || sessionId <= 0, ErrorCode.PARAMS_ERROR, "会话 ID 非法");
        ThrowUtils.throwIf(answer == null || answer.isBlank(), ErrorCode.PARAMS_ERROR, "回答内容不能为空");

        InterviewSession session = interviewSessionMapper.selectById(sessionId);
        ThrowUtils.throwIf(session == null, ErrorCode.NOT_FOUND_ERROR, "面试会话不存在");
        ThrowUtils.throwIf(session.getStatus() != 0, ErrorCode.OPERATION_ERROR, "面试已结束，无法继续");

        // 推进轮次
        int nextRound = incrRound(sessionId);

        // 保存用户回答到 Redis（热数据）
        appendHistory(sessionId, "user", answer);

        // 从 Redis 获取当前题目 ID
        Long currentQuestionId = getCachedQuestion(sessionId);
        ThrowUtils.throwIf(currentQuestionId == null, ErrorCode.NOT_FOUND_ERROR, "未找到当前题目");
        Question question = questionMapper.selectById(currentQuestionId);
        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR, "题目不存在");

        // 从 Redis 读取对话历史（排除当前轮 user 的回答，保留 assistant 历史）
        List<String> conversationHistory = getConversationHistory(sessionId, nextRound);

        // 调用 AI 评估
        AiInterviewResponseDTO response = aiInterviewStrategyService.evaluateAnswer(question, conversationHistory, answer);

        // 持久化 assistant 回复到 Redis
        appendHistory(sessionId, "assistant", response.getReplyToUser());

        // 按 actionDirective 路由分发
        handleDirective(sessionId, question, response, nextRound);

        return response;
    }

    /**
     * 按模式抽取一道题目（排除已使用过的）
     */
    private Question pickQuestion(Integer mode, Long bankId) {
        if (mode != null && mode.equals(InterviewModeEnum.SPECIFIED_BANK.getValue())) {
            LambdaQueryWrapper<QuestionBankQuestion> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(QuestionBankQuestion::getQuestionBankId, bankId);
            queryWrapper.last("ORDER BY RAND() LIMIT 1");
            QuestionBankQuestion relation = questionBankQuestionMapper.selectOne(queryWrapper);
            if (relation == null) {
                return null;
            }
            return questionMapper.selectById(relation.getQuestionId());
        } else {
            LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.last("ORDER BY RAND() LIMIT 1");
            return questionMapper.selectOne(queryWrapper);
        }
    }

    /**
     * 从指定题库中抽取未使用过的一道题
     */
    private Question pickNextQuestion(Integer mode, Long bankId, RSet<Long> usedQuestions) {
        List<Long> usedList = new ArrayList<>(usedQuestions);
        if (mode != null && mode.equals(InterviewModeEnum.SPECIFIED_BANK.getValue())) {
            LambdaQueryWrapper<QuestionBankQuestion> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(QuestionBankQuestion::getQuestionBankId, bankId);
            if (!usedList.isEmpty()) {
                queryWrapper.notIn(QuestionBankQuestion::getQuestionId, usedList);
            }
            queryWrapper.last("ORDER BY RAND() LIMIT 1");
            QuestionBankQuestion relation = questionBankQuestionMapper.selectOne(queryWrapper);
            if (relation == null) {
                return null;
            }
            return questionMapper.selectById(relation.getQuestionId());
        } else {
            LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
            if (!usedList.isEmpty()) {
                queryWrapper.notIn(Question::getId, usedList);
            }
            queryWrapper.last("ORDER BY RAND() LIMIT 1");
            return questionMapper.selectOne(queryWrapper);
        }
    }

    // ==================== Redis 操作封装 ====================

    private RList<String> getHistory(Long sessionId) {
        String key = InterviewRedisConstants.HISTORY_KEY_PREFIX + sessionId;
        RList<String> list = redissonClient.getList(key);
        list.expire(InterviewRedisConstants.TTL_SECONDS, java.util.concurrent.TimeUnit.SECONDS);
        return list;
    }

    private void appendHistory(Long sessionId, String role, String content) {
        RList<String> history = getHistory(sessionId);
        history.add(role + ": " + content);
        history.expire(InterviewRedisConstants.TTL_SECONDS, java.util.concurrent.TimeUnit.SECONDS);
    }

    private List<String> getConversationHistory(Long sessionId, int currentRound) {
        RList<String> history = getHistory(sessionId);
        // 返回最近 10 条 assistant 的发言作为上下文
        List<String> result = new ArrayList<>();
        int count = 0;
        for (int i = history.size() - 1; i >= 0; i--) {
            String msg = history.get(i);
            if (msg.startsWith("assistant:")) {
                result.add(0, msg);
                count++;
                if (count >= 10) break;
            }
        }
        return result;
    }

    private void cacheQuestion(Long sessionId, Long questionId) {
        String key = InterviewRedisConstants.QUESTION_KEY_PREFIX + sessionId;
        redissonClient.getBucket(key).set(questionId, InterviewRedisConstants.TTL_SECONDS, java.util.concurrent.TimeUnit.SECONDS);
    }

    private Long getCachedQuestion(Long sessionId) {
        String key = InterviewRedisConstants.QUESTION_KEY_PREFIX + sessionId;
        Object val = redissonClient.getBucket(key).get();
        return val != null ? Long.parseLong(val.toString()) : null;
    }

    private int incrRound(Long sessionId) {
        String key = InterviewRedisConstants.ROUND_KEY_PREFIX + sessionId;
        Long val = redissonClient.getAtomicLong(key).incrementAndGet();
        redissonClient.getBucket(key).expire(InterviewRedisConstants.TTL_SECONDS, java.util.concurrent.TimeUnit.SECONDS);
        return val.intValue();
    }

    private void setRound(Long sessionId, int round) {
        String key = InterviewRedisConstants.ROUND_KEY_PREFIX + sessionId;
        org.redisson.api.RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
        atomicLong.set(round);
        atomicLong.expire(InterviewRedisConstants.TTL_SECONDS, java.util.concurrent.TimeUnit.SECONDS);
    }

    private RSet<Long> getUsedQuestions(Long sessionId) {
        String key = InterviewRedisConstants.USED_QUESTIONS_KEY_PREFIX + sessionId;
        RSet<Long> set = redissonClient.getSet(key);
        set.expire(InterviewRedisConstants.TTL_SECONDS, java.util.concurrent.TimeUnit.SECONDS);
        return set;
    }

    /**
     * 按 AI 行为指令进行路由分发
     */
    private void handleDirective(Long sessionId, Question currentQuestion, AiInterviewResponseDTO response, int currentRound) {
        ActionDirectiveEnum directive = response.getActionDirective();
        switch (directive) {
            case DEEP_DIVE -> {
                // 继续追问当前知识点，AI 回复已保存到 Redis
                log.info("DEEP_DIVE: session={}, mastery={}", sessionId, response.getCurrentTopicMastery());
            }
            case NEXT_QUESTION -> {
                // 静默切换题目：从题库中抽取新题，触发 AI 内部调用生成过渡提问
                InterviewSession session = interviewSessionMapper.selectById(sessionId);
                RSet<Long> usedQuestions = getUsedQuestions(sessionId);
                Question nextQuestion = pickNextQuestion(session.getMode(), session.getBankId(), usedQuestions);
                if (nextQuestion != null) {
                    // 缓存新题目
                    cacheQuestion(sessionId, nextQuestion.getId());
                    usedQuestions.add(nextQuestion.getId());

                    // 触发一次内部 AI 调用，让 AI 用自然过渡的语气问出新题目
                    String newQuestionText = aiInterviewStrategyService.generateOpeningQuestion(nextQuestion);
                    appendHistory(sessionId, "assistant", newQuestionText);

                    // 同时持久化到 DB（切换记录）
                    saveRecordToDb(sessionId, nextQuestion.getId(), "assistant", newQuestionText, currentRound + 1);

                    log.info("NEXT_QUESTION: session={}, nextQuestionId={}", sessionId, nextQuestion.getId());
                } else {
                    // 题库已无可用题目，结束面试
                    endSession(sessionId);
                }
            }
            case END_INTERVIEW -> {
                // 结束面试：异步刷入数据库 + 发送 RabbitMQ 消息生成报告
                endSession(sessionId);
                log.info("END_INTERVIEW: session={}", sessionId);
            }
            default -> log.warn("Unknown directive: {}", directive);
        }
    }

    /**
     * 将 Redis 中的对话历史异步刷入数据库，并发送报告生成消息
     */
    private void endSession(Long sessionId) {
        // 更新会话状态
        InterviewSession session = new InterviewSession();
        session.setId(sessionId);
        session.setStatus(2); // 2-已生成报告
        interviewSessionMapper.updateById(session);

        // 将 Redis 中的对话历史刷入 DB
        flushHistoryToDb(sessionId);

        // 发送 RabbitMQ 异步消息生成综合评估报告
        reportProducer.sendReportMessage(sessionId);

        // 清理 Redis 中的热数据
        clearSessionCache(sessionId);
    }

    /**
     * 将 Redis 对话历史刷入 interview_record 表
     */
    private void flushHistoryToDb(Long sessionId) {
        RList<String> history = getHistory(sessionId);
        if (history.isEmpty()) {
            return;
        }
        int roundNum = 1;
        for (String msg : history) {
            String role = msg.contains(":") ? msg.substring(0, msg.indexOf(":")) : "assistant";
            String content = msg.contains(":") ? msg.substring(msg.indexOf(":") + 1).trim() : msg;
            saveRecordToDb(sessionId, getCachedQuestion(sessionId), role, content, roundNum++);
        }
    }

    private void saveRecordToDb(Long sessionId, Long questionId, String role, String content, int roundNum) {
        InterviewRecord record = new InterviewRecord();
        record.setSessionId(sessionId);
        record.setQuestionId(questionId);
        record.setRole(role);
        record.setContent(content);
        record.setRoundNum(roundNum);
        record.setCreateTime(new Date());
        interviewRecordMapper.insert(record);
    }

    /**
     * 清理会话的 Redis 缓存
     */
    private void clearSessionCache(Long sessionId) {
        redissonClient.getBucket(InterviewRedisConstants.HISTORY_KEY_PREFIX + sessionId).delete();
        redissonClient.getBucket(InterviewRedisConstants.QUESTION_KEY_PREFIX + sessionId).delete();
        redissonClient.getBucket(InterviewRedisConstants.ROUND_KEY_PREFIX + sessionId).delete();
        redissonClient.getBucket(InterviewRedisConstants.USED_QUESTIONS_KEY_PREFIX + sessionId).delete();
    }
}
