package org.cuit.app.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.cuit.app.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.mybatis.spring.annotation.MapperScan;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author jirafa
 * @since 2023-03-14
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    User selectByName(String name);

}
