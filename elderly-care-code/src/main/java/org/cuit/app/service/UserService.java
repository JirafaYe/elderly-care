package org.cuit.app.service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.cuit.app.entity.User;
import org.cuit.app.entity.vo.SosMessageVO;
import org.cuit.app.entity.vo.UserVO;
import org.cuit.app.exception.AuthorizedException;
import org.cuit.app.exception.LoginException;
import org.cuit.app.mapper.RelationshipMapper;
import org.cuit.app.mapper.UserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.cuit.app.utils.EncrypDES;
import org.cuit.app.utils.WebSocketUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 用户相关服务类
 *
 * @author jirafa
 * @since 2023-03-14
 */
@Service
@Slf4j
@AllArgsConstructor
public class UserService extends ServiceImpl<UserMapper, User> {
    private final TokenService tokenService;

    private final UserMapper userMapper;

    private final RelationshipMapper relationshipMapper;

    public void sos(String elderlyName,String message) throws IOException {
        List<Integer> binder = relationshipMapper.getBinder(userMapper.selectByName(elderlyName).getId());
        SosMessageVO sosMessageVO = new SosMessageVO(message,elderlyName);
        log.info(sosMessageVO.toString());
        for (User user : userMapper.selectBatchIds(binder)) {
            WebSocketUtils.sendMsg(WebSocketUtils.getGuardianConnection(),user.getId(),sosMessageVO);
        }
    }

    public Map<String, Object> signUp(UserVO vo){
        //密码加密
        vo.setPassword(EncrypDES.encrypt(vo.getPassword()));

        User user = convertToUser(vo);
        userMapper.insert(user);

        if(user.getId()==null) {
            throw new AuthorizedException("注册失败");
        }

        return tokenService.createToken(user);
    }

    public Map<String, Object> reset(User user, String password){
        //密码加密
        user.setPassword(EncrypDES.encrypt(password));

        UpdateWrapper<User> wrapper = new UpdateWrapper<User>().set("password", user.getPassword()).eq("name", user.getName());

        if(userMapper.update(user,wrapper)<=0) {
            throw new AuthorizedException("重置密码失败");
        }

        return tokenService.createToken(user);
    }

    public Map<String,Object> login(UserVO vo) {
        User user = userMapper.selectByName(vo.getName());
        if(user==null) {
            throw new LoginException("用户名错误");
        }
        if(!user.getPassword().equals(EncrypDES.encrypt(vo.getPassword()))){
            throw new LoginException("密码错误");
        }
        return tokenService.createToken(user);
    }

    public User convertToUser(UserVO vo) {
        User user = new User();
        Boolean.parseBoolean(vo.getIsElderly());
        user.setIsElderly(Boolean.parseBoolean(vo.getIsElderly()));
        user.setName(vo.getName());
        user.setPassword(vo.getPassword());
        return user;
    }

    public List<UserVO> convertToUserVO(User... users) {
        LinkedList<UserVO> vos = new LinkedList<>();
        for(User user:users){
            UserVO userVO = new UserVO();
            userVO.setIsElderly(user.getIsElderly().toString());
            userVO.setName(user.getName());
            vos.add(userVO);
        }
        return vos;
    }
}
