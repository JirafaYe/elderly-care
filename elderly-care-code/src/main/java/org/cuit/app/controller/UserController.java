package org.cuit.app.controller;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.cuit.app.constant.Constants;
import org.cuit.app.entity.User;
import org.cuit.app.entity.vo.UserVO;
import org.cuit.app.exception.AuthorizedException;
import org.cuit.app.service.UserService;
import org.cuit.app.utils.R;
import org.cuit.app.utils.WebSocketUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;


/**
 * 用户相关请求处理
 *
 * @author jirafa
 * @since 2023-03-14
 */
@RestController
@RequestMapping("/user")
@AllArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * 用户注册，返回token
     *
     * @param userVO
     * @return
     */
    @PostMapping("/sign-up")
    public R signUp(@Valid @RequestBody UserVO userVO){
        if(userVO.getIsElderly().equals("true")||userVO.getIsElderly().equals("false")){
            return R.ok(userService.signUp(userVO));
        }
        return R.fail("isElderly参数错误");
    }

    /**
     * 用户登录，返回token
     *
     * @param userVO
     * @return
     */
    @PostMapping ("/login")
    public R login(@Valid @RequestBody UserVO userVO){
        return R.ok(userService.login(userVO));
    }

    /**
     * 重置密码
     *
     * @param password
     * @param request
     * @return
     */
    @PostMapping("/reset")
    public R resetPassword(@Param("password") String password, HttpServletRequest request){
        if(StringUtils.isBlank(password)){
            return R.fail("密码不能为空");
        }
        User userInfo = (User) request.getAttribute(Constants.USER_ATTRIBUTE);
        return R.ok(userService.reset(userInfo,password));
    }

    /**
     * 获取用户信息
     *
     * @param request
     * @return
     */
    @GetMapping("/identity")
    public R identity(HttpServletRequest request){
        User userInfo = (User) request.getAttribute(Constants.USER_ATTRIBUTE);
        UserVO userVO = new UserVO();
        userVO.setName(userInfo.getName());
        userVO.setIsElderly(userInfo.getIsElderly().toString());
        return R.ok(userVO);
    }

    /**
     * 发出求救信号
     *
     * @param message
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("/sos")
    public R sos(@Param("message") String message,HttpServletRequest request) throws IOException {
        User user = (User) request.getAttribute(Constants.USER_ATTRIBUTE);
        log.info(message);
        if(!user.getIsElderly()){
            throw new AuthorizedException("无操作权限");
        }
        userService.sos(user.getName(),message);
        return R.ok();
    }

}

