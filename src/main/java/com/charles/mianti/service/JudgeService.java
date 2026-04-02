package com.charles.mianti.service;

import com.charles.mianti.model.dto.judge.SubmitCodeRequest;
import com.charles.mianti.model.vo.JudgeResultVO;

/**
 * 判题服务
 *
 * @author Charles
 */
public interface JudgeService {

    /**
     * 提交代码进行判题
     *
     * @param submitCodeRequest 提交请求
     * @param userId 用户 id
     * @return 提交记录 id
     */
    Long submitCode(SubmitCodeRequest submitCodeRequest, Long userId);

    /**
     * 获取判题结果
     *
     * @param submissionId 提交 id
     * @return 判题结果
     */
    JudgeResultVO getJudgeResult(Long submissionId);

    /**
     * 执行判题（异步调用）
     *
     * @param submissionId 提交 id
     */
    void executeJudge(Long submissionId);
}
