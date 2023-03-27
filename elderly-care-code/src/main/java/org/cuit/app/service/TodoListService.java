package org.cuit.app.service;

import org.cuit.app.entity.TodoList;
import org.cuit.app.entity.vo.TodoListVO;
import org.cuit.app.exception.AppException;
import org.cuit.app.exception.AuthorizedException;
import org.cuit.app.mapper.RelationshipMapper;
import org.cuit.app.mapper.TodoListMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    private final TodoListMapper todoListMapper;

    private final RelationshipMapper relationshipMapper;

    public void addTodoList(TodoListVO vo,Integer operator){
        List<Integer> binders = relationshipMapper.getBinder(vo.getElderlyId());
        if(!binders.contains(operator))
            throw new AuthorizedException("无操作权限");
        addTodoList(vo);
    }

    public void addTodoList(TodoListVO vo){
        TodoList list = convertToTodoList(vo);
        if(todoListMapper.insert(list)!=1)
            throw new AppException("插入失败");
    }
    public TodoList convertToTodoList(TodoListVO vo){
        TodoList todoList = new TodoList();
        todoList.setTodo(vo.getTodo());
        todoList.setElderlyId(vo.getElderlyId());
        todoList.setDate(vo.getDate());
        todoList.setBegin(vo.getBegin());
        return todoList;
    }

}
