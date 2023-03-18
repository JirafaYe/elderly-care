package org.cuit.app.service;

import org.cuit.app.entity.Relationship;
import org.cuit.app.entity.User;
import org.cuit.app.exception.AppException;
import org.cuit.app.mapper.RelationshipMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.cuit.app.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
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
public class RelationshipService extends ServiceImpl<RelationshipMapper, Relationship> {
    private final UserMapper userMapper;

    private final RelationshipMapper relationshipMapper;

    public void bindElderly(Integer operatorId,String username){
        User user = userMapper.selectByName(username);
        if(user == null){
            throw new AppException("用户名不存在");
        }
        relationshipMapper.insert(new Relationship(operatorId, user.getId()));
    }

}
