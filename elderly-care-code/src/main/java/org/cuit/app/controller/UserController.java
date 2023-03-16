package org.cuit.app.controller;


import lombok.AllArgsConstructor;
import org.cuit.app.entity.vo.UserVO;
import org.cuit.app.service.UserService;
import org.cuit.app.utils.R;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

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
    public R signUp(@Valid @RequestBody UserVO userVO) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return R.ok(userService.signUp(userVO));
    }

//    @GetMapping("/login")
//    public R login(@Valid @RequestBody UserVO userVO){
//
//    }

}

