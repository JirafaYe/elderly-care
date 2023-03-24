package org.cuit.app.service;

import org.cuit.app.entity.TodoList;
import org.cuit.app.mapper.TodoListMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Jirafa
 * @since 2023-03-24
 */
@Service
@AllArgsConstructor
public class TodoListService extends ServiceImpl<TodoListMapper, TodoList> {


}
