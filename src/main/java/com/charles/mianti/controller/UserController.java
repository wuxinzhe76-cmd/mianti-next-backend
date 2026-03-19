package com.charles.mianti.controller;

import com.charles.mianti.common.BaseResponse;
import com.charles.mianti.common.ErrorCode;
import com.charles.mianti.common.ResultUtils;
import com.charles.mianti.constant.UserConstant;
import com.charles.mianti.exception.BusinessException;
import com.charles.mianti.model.dto.user.UserLoginRequest;
import com.charles.mianti.model.dto.user.UserRegisterRequest;
import com.charles.mianti.model.entity.User;
import com.charles.mianti.model.vo.LoginUserVO;
import com.charles.mianti.service.UserService;
import com.charles.mianti.config.WxOpenConfig;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户控制器
 *
 * @author Charles
 * 
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private WxOpenConfig wxOpenConfig;

    @Value("${wx.open.appId}")
    private String wxOpenAppId;

    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest, HttpServletRequest request) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long userId = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(userId);
    }

    @GetMapping("/login/wx_open")
    public BaseResponse<LoginUserVO> userLoginByWxOpen(HttpServletRequest request, HttpServletResponse response,
                                                     @RequestParam("code") String code) {
        WxOAuth2AccessToken accessToken;
        try {
            accessToken = wxOpenConfig.getAccessToken(code);
        } catch (Exception e) {
            log.error("wx open login get token error", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取微信访问令牌失败");
        }
        WxOAuth2UserInfo wxOAuth2UserInfo;
        try {
            wxOAuth2UserInfo = wxOpenConfig.getUserInfo(accessToken);
        } catch (Exception e) {
            log.error("wx open login get user info error", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取微信用户信息失败");
        }
        if (StringUtils.isBlank(wxOAuth2UserInfo.getUnionId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "微信用户信息无效");
        }
        Set<String> forbiddenSet = new HashSet<>(Arrays.asList("微信用户", "微信用户1", "微信用户2"));
        String nickname = wxOAuth2UserInfo.getNickname();
        if (forbiddenSet.contains(nickname)) {
            nickname = nickname + wxOAuth2UserInfo.getOpenid();
            wxOAuth2UserInfo.setNickname(nickname);
        }
        LoginUserVO loginUserVO = userService.userLoginByMpOpen(wxOAuth2UserInfo, request);
        return ResultUtils.success(loginUserVO);
    }

    @GetMapping("/login/wx_open/app_id")
    public BaseResponse<String> getWxOpenAppId() {
        return ResultUtils.success(wxOpenAppId);
    }

    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        try {
            boolean result = userService.userLogout(request);
            return ResultUtils.success(result);
        } catch (Exception e) {
            // 未登录时也视为成功退出
            return ResultUtils.success(true);
        }
    }

    @GetMapping("/current")
    public BaseResponse<LoginUserVO> getCurrentUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(user));
    }
}
