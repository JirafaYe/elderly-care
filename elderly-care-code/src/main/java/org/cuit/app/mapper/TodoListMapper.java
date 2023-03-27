package org.cuit.app.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.cuit.app.entity.TodoList;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Jirafa
 * @since 2023-03-24
 */
@Mapper
public interface TodoListMapper extends BaseMapper<TodoList> {
}
