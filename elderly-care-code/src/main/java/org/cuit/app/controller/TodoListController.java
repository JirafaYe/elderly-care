package org.cuit.app.controller;


import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.cuit.app.constant.Constants;
import org.cuit.app.entity.TodoList;
import org.cuit.app.entity.User;
import org.cuit.app.entity.vo.TodoListVO;
import org.cuit.app.service.TodoListService;
import org.cuit.app.utils.R;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

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
    public R addTodoList(@RequestBody TodoListVO vo, HttpServletRequest request) {
        User userInfo = (User) request.getAttribute(Constants.USER_ATTRIBUTE);
        if (vo.getDate().before(new Date())
                || vo.getBegin() != null && (vo.getBegin().before(new Date()) || vo.getBegin().before(vo.getDate()))) {
            return R.fail("时间不合法");
        }
        todoListService.addTodoList(vo, userInfo.getIsElderly() ? null : userInfo.getId());
        return R.ok();
    }

    @PostMapping("/finish")
    public R finishTodoList() {
        return R.ok();
    }

    //todo:增加分页
    //todo:增加日期筛选
    @GetMapping("/get")
    public R getTodoList(String username, HttpServletRequest request) {
        User userInfo = (User) request.getAttribute(Constants.USER_ATTRIBUTE);
        if(username!=null&&userInfo.getIsElderly()&&!userInfo.getName().equals(username)){
            return R.fail("无查看权限");
        }
        List<TodoListVO> todoList =
                todoListService.getTodoList(StringUtils.isBlank(username)?userInfo.getName():username
                        , userInfo.getIsElderly() ? null : userInfo.getId());
        return R.ok(todoList);
    }

}

