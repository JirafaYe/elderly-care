package org.cuit.app.service;

import org.cuit.app.entity.User;
import org.cuit.app.mapper.UserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jirafa
 * @since 2023-03-14
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> {


}
