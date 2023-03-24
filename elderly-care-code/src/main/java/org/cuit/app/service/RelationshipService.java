package org.cuit.app.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.cuit.app.constant.CacheConstant;
import org.cuit.app.entity.Relationship;
import org.cuit.app.entity.User;
import org.cuit.app.entity.vo.CheckBindingVO;
import org.cuit.app.entity.vo.UserVO;
import org.cuit.app.exception.AppException;
import org.cuit.app.mapper.RelationshipMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.cuit.app.mapper.UserMapper;
import org.cuit.app.webSocket.CheckBindingWebSocket;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jirafa
 * @since 2023-03-14
 */
@Service
@AllArgsConstructor
@Slf4j
public class RelationshipService extends ServiceImpl<RelationshipMapper, Relationship> {
    private final UserMapper userMapper;

    private final UserService userService;

    private final RelationshipMapper relationshipMapper;

    private final RedisService redisService;

    private final TransactionTemplate transactionTemplate;

    public void bindElderly(User bindingUser,String elderlyName) throws IOException {
        User user = userMapper.selectByName(elderlyName);
        if(user == null){
            throw new AppException("用户名不存在");
        }
        redisService.setCacheMapValue(CacheConstant.BINDING_MAP, bindingUser.getName(),user.getName());
        notifyBinding(bindingUser,user);
    }

    public void notifyBinding(User binder,User elderly) throws IOException {
        List<UserVO> userVOS = userService.convertToUserVO(binder, elderly);
        CheckBindingWebSocket.sendMsg(elderly.getId(),new CheckBindingVO(userVOS.get(0),userVOS.get(1)));
    }

    public void checkBinding(boolean isPermissible,Integer elderlyId,String binderName){
        if(isPermissible){
            String elderlyName = redisService.getCacheMapValue(CacheConstant.BINDING_MAP, binderName);
            if(StringUtils.isBlank(elderlyName)){
                throw new AppException("非法请求");
            }
            User binder = userMapper.selectByName(binderName);
            if(binder == null){
                throw new AppException("用户名不存在");
            }
            transactionTemplate.execute(status -> {
                if (redisService.deleteCacheMapValue(CacheConstant.BINDING_MAP, binderName)==0L){
                    throw new AppException("redis删除失败");
                }
                relationshipMapper.insert(new Relationship(binder.getId(),elderlyId));
                return null;
            });
        }
    }

    public List<UserVO> getBinder(Integer elderlyId){
        List<Integer> binders = relationshipMapper.getBinder(elderlyId);
        User[] users = new User[binders.size()];
        for (int i = 0; i < binders.size(); i++) {
            users[i] = userMapper.selectById(binders.get(i));
        }
        return userService.convertToUserVO(users);
    }

    public List<UserVO> getElderly(Integer id){
        List<Integer> elderly = relationshipMapper.getElderly(id);
        User[] users = new User[elderly.size()];
        for (int i = 0; i < elderly.size(); i++) {
            users[i] = userMapper.selectById(elderly.get(i));
        }
        return userService.convertToUserVO(users);
    }

}
