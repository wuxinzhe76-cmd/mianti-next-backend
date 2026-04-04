package com.charles.mianti.service;

import com.charles.mianti.model.entity.TestCase;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 测试用例服务
 *
 * @author Charles
 */
public interface TestCaseService extends IService<TestCase> {

    /**
     * 根据题目 id 查询测试用例
     *
     * @param questionId 题目 id
     * @return 测试用例列表
     */
    List<TestCase> listByQuestionId(Long questionId);
}
