package org.cuit.app.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.cuit.app.entity.TodoList;
import org.cuit.app.entity.User;
import org.cuit.app.entity.vo.TodoListVO;
import org.cuit.app.exception.AppException;
import org.cuit.app.exception.AuthorizedException;
import org.cuit.app.mapper.RelationshipMapper;
import org.cuit.app.mapper.TodoListMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.cuit.app.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * 服务类
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

    private final UserMapper userMapper;

    public void addTodoList(TodoListVO vo, Integer operator) {
        TodoList list = convertToTodoList(vo);
        if (operator != null) {
            authorize(list.getElderlyId(), operator);
        }
        if (todoListMapper.insert(list) != 1)
            throw new AppException("插入失败");
    }

    public List<TodoListVO> getTodoList(String elderlyName, Integer operator) {
        User user = userMapper.selectByName(elderlyName);
        if (!user.getIsElderly()) {
            throw new AuthorizedException("不是老人，无操作权限");
        }
        if (operator != null) {
            authorize(user.getId(), operator);
        }
        List<TodoList> todoList = todoListMapper.selectList(new QueryWrapper<TodoList>().eq("elderly_id", user.getId()));
        List<TodoListVO> vos = new LinkedList<>();
        for(TodoList list : todoList){
            vos.add(convertToTodoListVO(list));
        }
        return vos;
    }

    private TodoList convertToTodoList(TodoListVO vo) {
        TodoList todoList = new TodoList();
        todoList.setTodo(vo.getTodo());
        User user = userMapper.selectByName(vo.getElderlyName());
        if (user == null) {
            throw new AppException("用户名错误");
        }
        todoList.setElderlyId(user.getId());
        todoList.setDate(vo.getDate());
        todoList.setBegin(vo.getBegin());
        return todoList;
    }

    private TodoListVO convertToTodoListVO(TodoList list) {
        TodoListVO vo = new TodoListVO();
        vo.setTodo(list.getTodo());
        vo.setBegin(list.getBegin());
        vo.setDate(list.getDate());
        vo.setId(list.getId());
        return vo;
    }

    private void authorize(Integer elderlyId, Integer operator) {
        List<Integer> binders = relationshipMapper.getBinder(elderlyId);
        if (!binders.contains(operator))
            throw new AuthorizedException("无操作权限");
    }

}
