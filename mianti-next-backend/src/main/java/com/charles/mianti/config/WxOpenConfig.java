package com.charles.mianti.config;

import lombok.Data;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信开放平台配置
 *
 * @author Charles
 * 
 */
@Configuration
@Data
public class WxOpenConfig {

    @Value("${wx.open.appId}")
    private String appId;

    @Value("${wx.open.appSecret}")
    private String appSecret;

    @Bean
    public WxMpService wxMpService() {
        WxMpService wxMpService = new WxMpServiceImpl();
        WxMpDefaultConfigImpl configStorage = new WxMpDefaultConfigImpl();
        configStorage.setAppId(appId);
        configStorage.setSecret(appSecret);
        wxMpService.setWxMpConfigStorage(configStorage);
        return wxMpService;
    }

    public WxOAuth2AccessToken getAccessToken(String code) throws Exception {
        return wxMpService().getOAuth2Service().getAccessToken(code);
    }

    public WxOAuth2UserInfo getUserInfo(WxOAuth2AccessToken accessToken) throws Exception {
        return wxMpService().getOAuth2Service().getUserInfo(accessToken, null);
    }
}
