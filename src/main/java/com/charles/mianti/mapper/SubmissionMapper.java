package com.charles.mianti.mapper;

import com.charles.mianti.model.entity.Submission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Charles
 * @description 针对表【submission(代码提交)】的数据库操作 Mapper
 * @createDate 2026-03-31 00:00:00
 * @Entity com.charles.mianti.model.entity.Submission
 */
@Mapper
public interface SubmissionMapper extends BaseMapper<Submission> {

}
