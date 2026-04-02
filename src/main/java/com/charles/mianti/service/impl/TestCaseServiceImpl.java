package com.charles.mianti.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charles.mianti.model.entity.TestCase;
import com.charles.mianti.mapper.TestCaseMapper;
import com.charles.mianti.service.TestCaseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 测试用例服务实现类
 *
 * @author Charles
 */
@Service
public class TestCaseServiceImpl extends ServiceImpl<TestCaseMapper, TestCase> implements TestCaseService {

    @Resource
    private TestCaseMapper testCaseMapper;

    @Override
    public List<TestCase> listByQuestionId(Long questionId) {
        LambdaQueryWrapper<TestCase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TestCase::getQuestionId, questionId);
        queryWrapper.orderByAsc(TestCase::getIsExample, TestCase::getId);
        return testCaseMapper.selectList(queryWrapper);
    }
}
