package com.charles.mianti.mapper;

import com.charles.mianti.model.entity.TestCase;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Charles
 * @description 针对表【test_case(测试用例)】的数据库操作 Mapper
 * @createDate 2026-03-31 00:00:00
 * @Entity com.charles.mianti.model.entity.TestCase
 */
@Mapper
public interface TestCaseMapper extends BaseMapper<TestCase> {

}
