package com.charles.mianti.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charles.mianti.model.entity.ProgrammingLanguage;
import com.charles.mianti.mapper.ProgrammingLanguageMapper;
import com.charles.mianti.service.ProgrammingLanguageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 编程语言服务实现类
 *
 * @author Charles
 */
@Service
public class ProgrammingLanguageServiceImpl extends ServiceImpl<ProgrammingLanguageMapper, ProgrammingLanguage> implements ProgrammingLanguageService {

    @Resource
    private ProgrammingLanguageMapper programmingLanguageMapper;

    @Override
    public ProgrammingLanguage getByCode(String languageCode) {
        LambdaQueryWrapper<ProgrammingLanguage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProgrammingLanguage::getLanguageCode, languageCode);
        return programmingLanguageMapper.selectOne(queryWrapper);
    }

    @Override
    public List<ProgrammingLanguage> listActiveLanguages() {
        LambdaQueryWrapper<ProgrammingLanguage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProgrammingLanguage::getIsActive, 1);
        queryWrapper.orderByAsc(ProgrammingLanguage::getId);
        return programmingLanguageMapper.selectList(queryWrapper);
    }
}
