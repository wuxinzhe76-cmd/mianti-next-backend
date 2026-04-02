package com.charles.mianti.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charles.mianti.model.entity.Submission;
import com.baomidou.mybatisplus.extension.service.IService;
import com.charles.mianti.model.vo.SubmissionVO;

/**
 * 提交记录服务
 *
 * @author Charles
 */
public interface SubmissionService extends IService<Submission> {

    /**
     * 获取我的提交记录
     *
     * @param questionId 题目 id（可选）
     * @param userId 用户 id
     * @param current 当前页码
     * @param size 页面大小
     * @return 提交记录分页
     */
    Page<SubmissionVO> getMySubmissions(Long questionId, Long userId, long current, long size);

    /**
     * 根据题目 id 获取提交记录
     *
     * @param questionId 题目 id
     * @param current 当前页码
     * @param size 页面大小
     * @return 提交记录分页
     */
    Page<SubmissionVO> getSubmissionsByQuestion(Long questionId, long current, long size);
}
