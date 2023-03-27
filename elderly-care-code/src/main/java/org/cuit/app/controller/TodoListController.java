package org.cuit.app.controller;


import lombok.AllArgsConstructor;
import org.cuit.app.constant.Constants;
import org.cuit.app.entity.User;
import org.cuit.app.entity.vo.TodoListVO;
import org.cuit.app.service.TodoListService;
import org.cuit.app.utils.R;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Jirafa
 * @since 2023-03-24
 */
@RestController
@AllArgsConstructor
@RequestMapping("/todo-list")
public class TodoListController {
    private final TodoListService todoListService;

    @PostMapping("/add")
    public R addTodoList(@RequestBody TodoListVO vo, HttpServletRequest request){
        User userInfo = (User) request.getAttribute(Constants.USER_ATTRIBUTE);
        if (userInfo.getIsElderly()) {
            todoListService.addTodoList(vo);
        } else {
            todoListService.addTodoList(vo, userInfo.getId());
        }
        return R.ok();
    }

}

