package org.cuit.app.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.cuit.app.entity.Relationship;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.cuit.app.entity.User;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author jirafa
 * @since 2023-03-14
 */
@Mapper
public interface RelationshipMapper extends BaseMapper<Relationship> {
    int getBinder(Integer id);

    List<Integer> getElderly(Integer id);
}
