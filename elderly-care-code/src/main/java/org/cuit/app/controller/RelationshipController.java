package org.cuit.app.controller;


import lombok.AllArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.cuit.app.constant.Constants;
import org.cuit.app.entity.User;
import org.cuit.app.entity.vo.CheckBindingVO;
import org.cuit.app.entity.vo.UserVO;
import org.cuit.app.exception.AuthorizedException;
import org.cuit.app.service.RelationshipService;
import org.cuit.app.utils.R;
import org.cuit.app.webSocket.CheckBindingWebSocket;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.EncodeException;
import java.io.IOException;
import java.util.List;

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

    @PostMapping("/binding")
    public R<?> bindElderly(@Param("username")String username, HttpServletRequest request) throws IOException {
        User userInfo = (User) request.getAttribute(Constants.USER_ATTRIBUTE);
        if(userInfo.getIsElderly()){
            throw new AuthorizedException("老人不具备绑定权限");
        }
        relationshipService.bindElderly(userInfo,username);
        return R.ok("等待老人确认");
    }

    @PostMapping("/check")
    public R<?> checkBinding(@Param("username")String username
            ,@Param("permissible") String  permissible
            ,HttpServletRequest request){
        boolean isPermissible = Boolean.parseBoolean(permissible);
        User userInfo = (User) request.getAttribute(Constants.USER_ATTRIBUTE);
        if(!userInfo.getIsElderly()){
            throw new AuthorizedException("无操作权限");
        }
        relationshipService.checkBinding(isPermissible,userInfo.getId(),username);
        return R.ok();
    }

    @GetMapping("/get")
    public R<?> getBinding(HttpServletRequest request){
        User userInfo = (User) request.getAttribute(Constants.USER_ATTRIBUTE);
        List<UserVO> voList;
        if(userInfo.getIsElderly()){
            voList=relationshipService.getBinder(userInfo.getId());
        }else {
            voList=relationshipService.getElderly(userInfo.getId());
        }
        return R.ok(voList);
    }
}

