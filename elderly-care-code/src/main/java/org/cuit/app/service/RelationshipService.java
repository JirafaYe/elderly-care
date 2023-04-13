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
import org.cuit.app.utils.WebSocketUtils;
import org.cuit.app.webSocket.NotifyElderlyWebSocket;
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

    /**
     * 监护人发起关系绑定请求，设置redis相关数据，并使用websocket向老人端推送关系绑定请求
     * @param bindingUser 发起者
     * @param elderlyName 老人
     * @throws IOException
     */
    public void bindElderly(User bindingUser,String elderlyName) throws IOException {
        User user = userMapper.selectByName(elderlyName);
        if(user == null){
            throw new AppException("用户名不存在");
        }
        redisService.setCacheMapValue(CacheConstant.BINDING_MAP, bindingUser.getName(),user.getName());
        notifyBinding(bindingUser,user);
    }

    /**
     * 使用websocket推送绑定通知
     * @param binder 发起者
     * @param elderly 老人
     * @throws IOException
     */
    public void notifyBinding(User binder,User elderly) throws IOException {
        List<UserVO> userVOS = userService.convertToUserVO(binder, elderly);
        WebSocketUtils.sendMsg(WebSocketUtils.getElderlyConnection(),elderly.getId(),new CheckBindingVO(userVOS.get(0),userVOS.get(1)));
    }

    /**
     * 老人进行确定关系绑定
     * @param isPermissible 是否同意
     * @param elderlyId 老人id
     * @param binderName 发起绑定的监护人用户名
     */
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

    /**
     * 老人获取绑定的监护人列表
     * @param elderlyId 老人id
     * @return 监护人列表
     */
    public List<UserVO> getBinder(Integer elderlyId){
        List<Integer> binders = relationshipMapper.getBinder(elderlyId);
        User[] users = new User[binders.size()];
        for (int i = 0; i < binders.size(); i++) {
            users[i] = userMapper.selectById(binders.get(i));
        }
        return userService.convertToUserVO(users);
    }

    /**
     * 获取绑定的老人列表
     * @param id 监护人id
     * @return 绑定的老人列表
     */
    public List<UserVO> getElderly(Integer id){
        List<Integer> elderly = relationshipMapper.getElderly(id);
        User[] users = new User[elderly.size()];
        for (int i = 0; i < elderly.size(); i++) {
            users[i] = userMapper.selectById(elderly.get(i));
        }
        return userService.convertToUserVO(users);
    }

}
