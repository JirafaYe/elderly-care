package org.cuit.app.service;

import lombok.extern.slf4j.Slf4j;
import org.cuit.app.entity.Relationship;
import org.cuit.app.entity.User;
import org.cuit.app.entity.vo.CheckBindingVO;
import org.cuit.app.entity.vo.UserVO;
import org.cuit.app.exception.AppException;
import org.cuit.app.mapper.RelationshipMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.cuit.app.mapper.UserMapper;
import org.cuit.app.utils.R;
import org.cuit.app.webSocket.CheckBindingWebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.EncodeException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    public void bindElderly(User bindingUser,String elderlyName) throws IOException {
        User user = userMapper.selectByName(elderlyName);
        if(user == null){
            throw new AppException("用户名不存在");
        }
        notifyBinding(bindingUser,user);
    }

    public void notifyBinding(User binder,User elderly) throws IOException {
        List<UserVO> userVOS = userService.convertToUserVO(binder, elderly);
        CheckBindingWebSocket.sendMsg(elderly.getId(),new CheckBindingVO(userVOS.get(0),userVOS.get(1)));
    }

    public void checkBinding(boolean isPermissible,Integer elderlyId,String binderName){
        if(isPermissible){
            User binder = userMapper.selectByName(binderName);
            if(binder == null){
                throw new AppException("用户名不存在");
            }
            relationshipMapper.insert(new Relationship(binder.getId(),elderlyId));
        }
    }

}
