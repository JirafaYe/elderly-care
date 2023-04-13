package org.cuit.app.controller;


import lombok.AllArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.cuit.app.constant.Constants;
import org.cuit.app.entity.User;
import org.cuit.app.entity.vo.UserVO;
import org.cuit.app.exception.AuthorizedException;
import org.cuit.app.service.RelationshipService;
import org.cuit.app.utils.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * 接收绑定关系相关操作的请求
 *
 * @author jirafa
 * @since 2023-03-14
 */
@RestController
@AllArgsConstructor
@RequestMapping("/relationship")
public class RelationshipController {
    private final RelationshipService relationshipService;

    /**
     * 由监护人发起关系绑定请求
     * @param username 老人用户名
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("/binding")
    public R<?> bindElderly(@Param("username")String username, HttpServletRequest request) throws IOException {
        //获取当前发起请求的用户信息
        User userInfo = (User) request.getAttribute(Constants.USER_ATTRIBUTE);

        //判断用户是否是监护人
        if(userInfo.getIsElderly()){
            throw new AuthorizedException("老人不具备绑定权限");
        }

        //调用service层方法进行处理，如果出现异常会直接进行抛出
        relationshipService.bindElderly(userInfo,username);
        return R.ok("等待老人确认");
    }

    /**
     * 老人进行关系绑定确认
     * @param username 监护人名字
     * @param permissible 是否同意
     * @param request
     * @return
     */
    @PostMapping("/check")
    public R<?> checkBinding(@Param("username")String username
            ,@Param("permissible") String  permissible
            ,HttpServletRequest request){
        boolean isPermissible = Boolean.parseBoolean(permissible);

        //获取用户信息并进行身份判断
        User userInfo = (User) request.getAttribute(Constants.USER_ATTRIBUTE);
        if(!userInfo.getIsElderly()){
            throw new AuthorizedException("无操作权限");
        }

        //调用service层进行具体操作
        relationshipService.checkBinding(isPermissible,userInfo.getId(),username);
        return R.ok();
    }

    /**
     * 获取绑定列表
     * @param request
     * @return
     */
    @GetMapping("/get")
    public R<?> getBinding(HttpServletRequest request){
        User userInfo = (User) request.getAttribute(Constants.USER_ATTRIBUTE);
        List<UserVO> voList;

        //根据用户信息判断身份并获取相关的绑定信息
        if(userInfo.getIsElderly()){
            voList=relationshipService.getBinder(userInfo.getId());
        }else {
            voList=relationshipService.getElderly(userInfo.getId());
        }

        //前端要求只返回一个用户
        return R.ok(voList!=null?voList.get(0):null);
    }
}

