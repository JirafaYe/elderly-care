package org.cuit.app.controller;


import lombok.AllArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.cuit.app.constant.Constants;
import org.cuit.app.entity.User;
import org.cuit.app.exception.AuthorizedException;
import org.cuit.app.service.RelationshipService;
import org.cuit.app.utils.R;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jirafa
 * @since 2023-03-14
 */
@RestController
@AllArgsConstructor
@RequestMapping("/relationship")
public class RelationshipController {
    private final RelationshipService relationshipService;

    @PostMapping("/bindings")
    public R<?> bindElderly(@Param("username")String username, HttpServletRequest request){
        User userInfo = (User) request.getAttribute(Constants.USER_ATTRIBUTE);
        if(userInfo.getIsElderly()){
            throw new AuthorizedException("老人不具备绑定权限");
        }
        relationshipService.bindElderly(userInfo.getId(),username);
        return R.ok();
    }
}

