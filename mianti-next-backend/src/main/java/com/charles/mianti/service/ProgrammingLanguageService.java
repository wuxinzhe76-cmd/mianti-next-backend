package com.charles.mianti.service;

import com.charles.mianti.model.entity.ProgrammingLanguage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 编程语言服务
 *
 * @author Charles
 */
public interface ProgrammingLanguageService extends IService<ProgrammingLanguage> {

    /**
     * 根据代码获取语言
     *
     * @param languageCode 语言代码
     * @return 编程语言
     */
    ProgrammingLanguage getByCode(String languageCode);

    /**
     * 获取所有启用的编程语言
     *
     * @return 编程语言列表
     */
    List<ProgrammingLanguage> listActiveLanguages();
}
