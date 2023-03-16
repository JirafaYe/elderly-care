package org.cuit.app.service;

import io.netty.util.internal.StringUtil;
import org.cuit.app.constant.Constants;
import org.cuit.app.constant.TokenConstants;
import org.cuit.app.entity.User;
import org.cuit.app.entity.vo.UserVO;
import org.cuit.app.exception.AppException;
import org.cuit.app.mapper.UserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.cuit.app.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
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

    public Map<String, Object> signUp(UserVO vo) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //密码加密
        MessageDigest md = MessageDigest.getInstance("MD5");
        String password = new String(md.digest(vo.getPassword().getBytes()), Constants.UTF8);
        System.out.println(password);

        User user = new User();
        user.setIsElderly(vo.getIsElderly());
        user.setName(vo.getName());
        user.setPassword(password);
        userMapper.insert(user);
        if(user.getId()==null) {
            log.error("插入数据失败");
            throw new AppException("注册失败");
        }

        return tokenService.createToken(user);
    }
}
