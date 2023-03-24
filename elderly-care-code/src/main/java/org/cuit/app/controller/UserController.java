package org.cuit.app.controller;


import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.cuit.app.constant.Constants;
import org.cuit.app.entity.User;
import org.cuit.app.entity.vo.UserVO;
import org.cuit.app.exception.AuthorizedException;
import org.cuit.app.service.UserService;
import org.cuit.app.utils.R;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jirafa
 * @since 2023-03-14
 */
@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public R signUp(@Valid @RequestBody UserVO userVO){
        return R.ok(userService.signUp(userVO));
    }

    @GetMapping("/login")
    public R login(@Valid @RequestBody UserVO userVO){
        return R.ok(userService.login(userVO));
    }

    @PostMapping("/reset")
    public R resetPassword(@Param("password") String password, HttpServletRequest request){
        if(StringUtils.isBlank(password)){
            return R.fail("密码不能为空");
        }
        User userInfo = (User) request.getAttribute(Constants.USER_ATTRIBUTE);
        return R.ok(userService.reset(userInfo,password));
    }

}

