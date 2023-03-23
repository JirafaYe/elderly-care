package org.cuit.app.service;

import org.cuit.app.entity.User;
import org.cuit.app.entity.vo.UserVO;
import org.cuit.app.exception.AuthorizedException;
import org.cuit.app.exception.LoginException;
import org.cuit.app.mapper.UserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.cuit.app.utils.EncrypDES;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author jirafa
 * @since 2023-03-14
 */
@Service
@AllArgsConstructor
public class UserService extends ServiceImpl<UserMapper, User> {
    private final TokenService tokenService;

    private final UserMapper userMapper;

    public Map<String, Object> signUp(UserVO vo){
        //密码加密
        vo.setPassword(EncrypDES.encrypt(vo.getPassword()));

        User user = convertToUser(vo);

        if(user.getId()==null) {
            throw new AuthorizedException("注册失败");
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
        user.setIsElderly(vo.getIsElderly());
        user.setName(vo.getName());
        user.setPassword(vo.getPassword());
        userMapper.insert(user);
        return user;
    }

    public List<UserVO> convertToUserVO(User... users) {
        LinkedList<UserVO> vos = new LinkedList<>();
        for(User user:users){
            UserVO userVO = new UserVO();
            userVO.setIsElderly(user.getIsElderly());
            userVO.setName(user.getName());
            vos.add(userVO);
        }
        return vos;
    }
}
