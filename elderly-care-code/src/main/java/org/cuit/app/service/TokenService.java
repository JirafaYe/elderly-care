package org.cuit.app.service;

import lombok.AllArgsConstructor;
import org.cuit.app.constant.CacheConstant;
import org.cuit.app.constant.TokenConstants;
import org.cuit.app.entity.User;
import org.cuit.app.utils.JwtUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Token服务类
 * </p>
 *
 * @author jirafa
 * @since 2023-03-14
 */
@Service
@AllArgsConstructor
public class TokenService {

    private final RedisService redisService;


    /**
     * 创建令牌
     * @param user
     * @return
     */
    public Map<String,Object> createToken(User user){
        Map<String,Object> claims=new HashMap<>();
        claims.put(TokenConstants.USER_ID,user.getId());
        claims.put(TokenConstants.USER_NAME,user.getName());
        claims.put(TokenConstants.USER_IDENTITY,user.getIsElderly());

        Map<String,Object> res= new HashMap<>();
        res.put("access_token", JwtUtils.createToken(claims));
        res.put("expire", TokenConstants.EXPIRATION/1000);

        return res;
    }
}
