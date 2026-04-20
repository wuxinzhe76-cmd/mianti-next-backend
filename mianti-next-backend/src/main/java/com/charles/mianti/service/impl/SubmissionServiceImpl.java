package com.charles.mianti.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charles.mianti.mapper.SubmissionMapper;
import com.charles.mianti.model.entity.ProgrammingLanguage;
import com.charles.mianti.model.entity.Question;
import com.charles.mianti.model.entity.Submission;
import com.charles.mianti.model.entity.User;
import com.charles.mianti.model.enums.JudgeVerdictEnum;
import com.charles.mianti.model.vo.SubmissionVO;
import com.charles.mianti.service.ProgrammingLanguageService;
import com.charles.mianti.service.QuestionService;
import com.charles.mianti.service.SubmissionService;
import com.charles.mianti.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 提交记录服务实现类
 *
 * @author Charles
 */
@Service
public class SubmissionServiceImpl extends ServiceImpl<SubmissionMapper, Submission> implements SubmissionService {

    @Resource
    private SubmissionMapper submissionMapper;

    @Resource
    private QuestionService questionService;

    @Resource
    private ProgrammingLanguageService programmingLanguageService;

    @Resource
    private UserService userService;

    @Override
    public Page<SubmissionVO> getMySubmissions(Long questionId, Long userId, long current, long size) {
        LambdaQueryWrapper<Submission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Submission::getUserId, userId);
        if (questionId != null) {
            queryWrapper.eq(Submission::getQuestionId, questionId);
        }
        queryWrapper.orderByDesc(Submission::getCreateTime);

        Page<Submission> submissionPage = submissionMapper.selectPage(
            new Page<>(current, size), queryWrapper);

        Page<SubmissionVO> submissionVOPage = new Page<>(current, size);
        BeanUtil.copyProperties(submissionPage, submissionVOPage, "records");
        submissionVOPage.setRecords(convertToVOList(submissionPage.getRecords()));

        return submissionVOPage;
    }

    @Override
    public Page<SubmissionVO> getSubmissionsByQuestion(Long questionId, long current, long size) {
        LambdaQueryWrapper<Submission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Submission::getQuestionId, questionId);
        queryWrapper.orderByDesc(Submission::getCreateTime);

        Page<Submission> submissionPage = submissionMapper.selectPage(
            new Page<>(current, size), queryWrapper);

        Page<SubmissionVO> submissionVOPage = new Page<>(current, size);
        BeanUtil.copyProperties(submissionPage, submissionVOPage, "records");
        submissionVOPage.setRecords(convertToVOList(submissionPage.getRecords()));

        return submissionVOPage;
    }

    private List<SubmissionVO> convertToVOList(List<Submission> submissions) {
        return submissions.stream().map(submission -> {
            SubmissionVO vo = new SubmissionVO();
            BeanUtil.copyProperties(submission, vo);

            Question question = questionService.getById(submission.getQuestionId());
            if (question != null) {
                vo.setQuestionTitle(question.getTitle());
            }

            User user = userService.getById(submission.getUserId());
            if (user != null) {
                vo.setUserName(user.getUserName());
            }

            ProgrammingLanguage language = programmingLanguageService.getByCode(submission.getLanguageCode());
            if (language != null) {
                vo.setLanguageName(language.getLanguageName());
            }

            JudgeVerdictEnum statusEnum = JudgeVerdictEnum.getEnumByValue(submission.getStatus());
            if (statusEnum != null) {
                vo.setStatusText(statusEnum.getText());
            }

            if (submission.getTotalTestCase() != null && submission.getTotalTestCase() > 0) {
                int passRate = (int) ((double) submission.getPassedTestCase() / submission.getTotalTestCase() * 100);
                vo.setPassRate(passRate + "%");
            }

            if (submission.getCode() != null) {
                vo.setCodeLength(submission.getCode().length());
            }

            return vo;
        }).collect(Collectors.toList());
    }
}
