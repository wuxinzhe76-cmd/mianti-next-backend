package com.charles.mianti.mapper;

import com.charles.mianti.model.entity.JudgeResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Charles
 * @description 针对表【judge_result(判题结果详情)】的数据库操作 Mapper
 * @createDate 2026-03-31 00:00:00
 * @Entity com.charles.mianti.model.entity.JudgeResult
 */
@Mapper
public interface JudgeResultMapper extends BaseMapper<JudgeResult> {

}
