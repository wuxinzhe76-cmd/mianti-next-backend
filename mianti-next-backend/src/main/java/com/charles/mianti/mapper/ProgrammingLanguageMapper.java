package com.charles.mianti.mapper;

import com.charles.mianti.model.entity.ProgrammingLanguage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Charles
 * @description 针对表【programming_language(编程语言)】的数据库操作 Mapper
 * @createDate 2026-03-31 00:00:00
 * @Entity com.charles.mianti.model.entity.ProgrammingLanguage
 */
@Mapper
public interface ProgrammingLanguageMapper extends BaseMapper<ProgrammingLanguage> {

}
