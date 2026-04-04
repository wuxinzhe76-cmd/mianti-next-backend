package com.charles.mianti.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charles.mianti.common.ErrorCode;
import com.charles.mianti.exception.BusinessException;
import com.charles.mianti.judge.codesandbox.CodeSandbox;
import com.charles.mianti.judge.codesandbox.CodeSandbox.ExecuteResult;
import com.charles.mianti.mapper.SubmissionMapper;
import com.charles.mianti.model.dto.judge.SubmitCodeRequest;
import com.charles.mianti.model.entity.*;
import com.charles.mianti.model.enums.JudgeVerdictEnum;
import com.charles.mianti.model.vo.JudgeResultVO;
import com.charles.mianti.service.JudgeResultService;
import com.charles.mianti.service.JudgeService;
import com.charles.mianti.service.ProgrammingLanguageService;
import com.charles.mianti.service.QuestionService;
import com.charles.mianti.service.TestCaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 判题服务实现类
 *
 * @author Charles
 */
@Slf4j
@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private SubmissionMapper submissionMapper;

    @Resource
    private QuestionService questionService;

    @Resource
    private TestCaseService testCaseService;

    @Resource
    private ProgrammingLanguageService programmingLanguageService;

    @Resource
    private CodeSandbox codeSandbox;

    @Resource
    private JudgeResultService judgeResultService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitCode(SubmitCodeRequest submitCodeRequest, Long userId) {
        Long questionId = submitCodeRequest.getQuestionId();
        String languageCode = submitCodeRequest.getLanguageCode();
        String code = submitCodeRequest.getCode();

        // 1. 验证题目是否存在
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }

        // 2. 验证编程语言是否支持
        ProgrammingLanguage language = programmingLanguageService.getByCode(languageCode);
        if (language == null || language.getIsActive() != 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的编程语言");
        }

        // 3. 创建提交记录
        Submission submission = new Submission();
        submission.setQuestionId(questionId);
        submission.setUserId(userId);
        submission.setLanguageCode(languageCode);
        submission.setCode(code);
        submission.setStatus(JudgeVerdictEnum.PENDING.getValue());
        submissionMapper.insert(submission);

        // 4. 异步执行判题
        CompletableFuture.runAsync(() -> {
            try {
                executeJudge(submission.getId());
            } catch (Exception e) {
                log.error("判题失败，submissionId: {}", submission.getId(), e);
                // 更新状态为运行错误
                Submission errorSubmission = new Submission();
                errorSubmission.setId(submission.getId());
                errorSubmission.setStatus(JudgeVerdictEnum.RUNTIME_ERROR.getValue());
                errorSubmission.setErrorMessage("判题系统异常：" + e.getMessage());
                submissionMapper.updateById(errorSubmission);
            }
        });

        return submission.getId();
    }

    @Override
    public JudgeResultVO getJudgeResult(Long submissionId) {
        // 1. 查询提交记录
        Submission submission = submissionMapper.selectById(submissionId);
        if (submission == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交记录不存在");
        }

        // 2. 查询判题结果详情
        LambdaQueryWrapper<JudgeResult> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(JudgeResult::getSubmissionId, submissionId);
        JudgeResult judgeResult = judgeResultService.getOne(queryWrapper);

        // 3. 组装返回结果
        JudgeResultVO resultVO = new JudgeResultVO();
        BeanUtil.copyProperties(submission, resultVO);

        if (judgeResult != null) {
            BeanUtil.copyProperties(judgeResult, resultVO, "submissionId", "userId", "languageCode", "code");
            
            // 设置判题结果文本描述
            JudgeVerdictEnum verdictEnum = JudgeVerdictEnum.getEnumByValue(judgeResult.getVerdict());
            if (verdictEnum != null) {
                resultVO.setVerdictText(verdictEnum.getText());
            }

            // 计算通过率
            if (judgeResult.getTotalTestCase() != null && judgeResult.getTotalTestCase() > 0) {
                int passRate = (int) ((double) judgeResult.getPassedTestCase() / judgeResult.getTotalTestCase() * 100);
                resultVO.setTestCaseScore(passRate);
            }
        } else {
            // 判题中，返回当前状态
            JudgeVerdictEnum statusEnum = JudgeVerdictEnum.getEnumByValue(submission.getStatus());
            if (statusEnum != null) {
                resultVO.setVerdictText(statusEnum.getText());
            }
        }

        // 补充题目和语言信息
        Question question = questionService.getById(submission.getQuestionId());
        if (question != null) {
            resultVO.setQuestionTitle(question.getTitle());
        }

        ProgrammingLanguage language = programmingLanguageService.getByCode(submission.getLanguageCode());
        if (language != null) {
            resultVO.setLanguageName(language.getLanguageName());
        }

        return resultVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeJudge(Long submissionId) {
        log.info("开始判题，submissionId: {}", submissionId);

        // 1. 查询提交记录
        Submission submission = submissionMapper.selectById(submissionId);
        if (submission == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交记录不存在");
        }

        // 2. 更新状态为判题中
        Submission updatingSubmission = new Submission();
        updatingSubmission.setId(submissionId);
        updatingSubmission.setStatus(JudgeVerdictEnum.JUDGING.getValue());
        submissionMapper.updateById(updatingSubmission);

        // 3. 获取题目和测试用例
        Question question = questionService.getById(submission.getQuestionId());
        List<TestCase> testCases = testCaseService.listByQuestionId(submission.getQuestionId());

        if (testCases == null || testCases.isEmpty()) {
            // 没有测试用例，直接返回编译错误提示
            JudgeResult judgeResult = createJudgeResult(submission, question, null, 
                JudgeVerdictEnum.RUNTIME_ERROR.getValue(), "暂无测试用例", 0, 0, 0, 0);
            saveJudgeResult(judgeResult);
            return;
        }

        // 4. 逐个执行测试用例
        List<Map<String, Object>> testCaseResults = new ArrayList<>();
        int passedCount = 0;
        int totalScore = 0;
        int maxExecutionTime = 0;
        int maxExecutionMemory = 0;
        String finalVerdict = JudgeVerdictEnum.ACCEPTED.getValue();
        String errorMessage = null;

        for (TestCase testCase : testCases) {
            // 执行代码
            ExecuteResult executeResult = codeSandbox.execute(
                submission.getLanguageCode(),
                submission.getCode(),
                testCase.getInput(),
                question.getTimeLimit() != null ? question.getTimeLimit() : 1000,
                question.getMemoryLimit() != null ? question.getMemoryLimit() : 256
            );

            // 记录测试结果
            Map<String, Object> singleResult = Map.of(
                "testCaseId", testCase.getId(),
                "verdict", executeResult.getVerdict().getValue(),
                "executionTime", executeResult.getExecutionTime(),
                "executionMemory", executeResult.getExecutionMemory(),
                "output", StrUtil.sub(executeResult.getOutput(), 0, 1024), // 限制输出长度
                "expectedOutput", StrUtil.sub(testCase.getOutput(), 0, 1024)
            );
            testCaseResults.add(singleResult);

            // 更新统计信息
            if (executeResult.getExecutionTime() != null) {
                maxExecutionTime = Math.max(maxExecutionTime, executeResult.getExecutionTime());
            }
            if (executeResult.getExecutionMemory() != null) {
                maxExecutionMemory = Math.max(maxExecutionMemory, executeResult.getExecutionMemory());
            }

            // 判断是否正确
            if (executeResult.getVerdict() == CodeSandbox.Verdict.ACCEPTED) {
                // 对比输出结果
                String expectedOutput = normalizeOutput(testCase.getOutput());
                String actualOutput = normalizeOutput(executeResult.getOutput());

                if (expectedOutput.equals(actualOutput)) {
                    passedCount++;
                    totalScore += testCase.getScore();
                } else {
                    finalVerdict = JudgeVerdictEnum.WRONG_ANSWER.getValue();
                    errorMessage = "答案不匹配";
                }
            } else {
                // 出现错误
                finalVerdict = mapVerdict(executeResult.getVerdict());
                errorMessage = executeResult.getErrorMessage();
                break; // 出现错误不再继续测试
            }
        }

        // 5. 保存判题结果
        JudgeResult judgeResult = createJudgeResult(
            submission, question, testCaseResults,
            finalVerdict, errorMessage,
            passedCount, testCases.size(),
            maxExecutionTime, maxExecutionMemory
        );
        saveJudgeResult(judgeResult);

        // 6. 更新提交记录
        Submission resultSubmission = new Submission();
        resultSubmission.setId(submissionId);
        resultSubmission.setStatus(finalVerdict);
        resultSubmission.setExecutionTime(maxExecutionTime);
        resultSubmission.setExecutionMemory(maxExecutionMemory);
        resultSubmission.setPassedTestCase(passedCount);
        resultSubmission.setTotalTestCase(testCases.size());
        resultSubmission.setTestCaseScore(totalScore);
        resultSubmission.setErrorMessage(errorMessage);
        submissionMapper.updateById(resultSubmission);

        // 7. 更新题目统计信息（如果通过）
        if (JudgeVerdictEnum.ACCEPTED.getValue().equals(finalVerdict)) {
            updateQuestionStatistics(question);
        }

        log.info("判题完成，submissionId: {}, verdict: {}", submissionId, finalVerdict);
    }

    /**
     * 标准化输出（去除首尾空白字符和空行）
     */
    private String normalizeOutput(String output) {
        if (StrUtil.isBlank(output)) {
            return "";
        }
        return output.trim().replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n");
    }

    /**
     * 映射判题结果枚举
     */
    private String mapVerdict(CodeSandbox.Verdict verdict) {
        switch (verdict) {
            case ACCEPTED:
                return JudgeVerdictEnum.ACCEPTED.getValue();
            case WRONG_ANSWER:
                return JudgeVerdictEnum.WRONG_ANSWER.getValue();
            case TIME_LIMIT_EXCEEDED:
                return JudgeVerdictEnum.TIME_LIMIT_EXCEEDED.getValue();
            case MEMORY_LIMIT_EXCEEDED:
                return JudgeVerdictEnum.MEMORY_LIMIT_EXCEEDED.getValue();
            case RUNTIME_ERROR:
                return JudgeVerdictEnum.RUNTIME_ERROR.getValue();
            case COMPILE_ERROR:
                return JudgeVerdictEnum.COMPILE_ERROR.getValue();
            default:
                return JudgeVerdictEnum.RUNTIME_ERROR.getValue();
        }
    }

    /**
     * 创建判题结果对象
     */
    private JudgeResult createJudgeResult(Submission submission, Question question,
                                          List<Map<String, Object>> testCaseResults,
                                          String verdict, String errorMessage,
                                          int passedCount, int totalCount,
                                          int executionTime, int executionMemory) {
        JudgeResult judgeResult = new JudgeResult();
        judgeResult.setSubmissionId(submission.getId());
        judgeResult.setQuestionId(submission.getQuestionId());
        judgeResult.setUserId(submission.getUserId());
        judgeResult.setLanguageCode(submission.getLanguageCode());
        judgeResult.setCode(submission.getCode());
        judgeResult.setVerdict(verdict);
        judgeResult.setExecutionTime(executionTime);
        judgeResult.setExecutionMemory(executionMemory);
        judgeResult.setTestCaseResults(JSONUtil.toJsonStr(testCaseResults));
        judgeResult.setErrorMessage(errorMessage);
        judgeResult.setJudgeServer("local-sandbox-01");
        judgeResult.setJudgeTime(new Date());
        return judgeResult;
    }

    /**
     * 保存判题结果
     */
    private void saveJudgeResult(JudgeResult judgeResult) {
        judgeResultService.save(judgeResult);
    }

    /**
     * 更新题目统计信息
     */
    private void updateQuestionStatistics(Question question) {
        question.setSubmissionCount(question.getSubmissionCount() + 1);
        question.setAcceptedCount(question.getAcceptedCount() + 1);
        if (question.getSubmissionCount() > 0) {
            question.setAcceptanceRate((double) question.getAcceptedCount() / question.getSubmissionCount() * 100);
        }
        questionService.updateById(question);
    }
}
