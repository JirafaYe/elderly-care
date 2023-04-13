package org.cuit.app.controller;


import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.cuit.app.constant.Constants;
import org.cuit.app.entity.User;
import org.cuit.app.entity.vo.TodoListVO;
import org.cuit.app.service.TodoListService;
import org.cuit.app.utils.R;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 处理todo-list相关请求
 *
 * @author Jirafa
 * @since 2023-03-24
 */
@RestController
@AllArgsConstructor
@RequestMapping("/todo-list")
public class TodoListController {
    private final TodoListService todoListService;

    /**
     * 根据todolist id更新todolist
     * @param vo 请求vo实体类
     * @param request
     * @return
     * @throws SchedulerException
     */
    @PostMapping("/update")
    public R updateTodoList(@Valid @RequestBody TodoListVO vo, HttpServletRequest request) throws SchedulerException {
        User userInfo = (User) request.getAttribute(Constants.USER_ATTRIBUTE);
        Date date = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        if (vo.getDate().before(date)
                || (vo.getBegin() != null && (vo.getBegin().before(vo.getDate()) || vo.getBegin().before(new Date())))) {
            return R.fail("时间不合法");
        }

        //如果当前用户为老人则直接传入从token获取的信息，如果当前用户为监护人则需要指定该参数
        vo.setElderlyName(userInfo.getIsElderly() ? userInfo.getName() : vo.getElderlyName());
        if (StringUtils.isBlank(vo.getElderlyName())) {
            return R.fail("elderlyName为空");
        }
        todoListService.updateTodoList(vo, userInfo.getIsElderly() ? null : userInfo.getId());
        return R.ok();
    }

    /**
     * 添加todolist
     * @param vo
     * @param request
     * @return
     * @throws SchedulerException
     */
    @PostMapping("/add")
    public R addTodoList(@RequestBody TodoListVO vo, HttpServletRequest request) throws SchedulerException {
        User userInfo = (User) request.getAttribute(Constants.USER_ATTRIBUTE);

        Date date = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        if (vo.getDate().before(date)
                || (vo.getBegin() != null && (vo.getBegin().before(vo.getDate()) || vo.getBegin().before(new Date())))) {
            return R.fail("时间不合法");
        }

        vo.setElderlyName(userInfo.getIsElderly() ? userInfo.getName() : vo.getElderlyName());
        if (StringUtils.isBlank(vo.getElderlyName())) {
            return R.fail("elderlyName为空");
        }
        todoListService.addTodoList(vo, userInfo.getIsElderly() ? null : userInfo.getId());
        return R.ok();
    }

    /**
     * 通过id完成todolist
     * @param id todolist id
     * @param request
     * @return
     */
    @PostMapping("/finish")
    public R finishTodoList(@Param("id") String id, HttpServletRequest request) {
        User userInfo = (User) request.getAttribute(Constants.USER_ATTRIBUTE);
        todoListService.finishTodoList(Integer.parseInt(id), userInfo.getIsElderly(), userInfo.getId());
        return R.ok();
    }

    /**
     * 获取用户的todolist
     * 如果当前登录用户是老人则无需传入username直接从token中进行解析获取
     *
     * @param username 用户名
     * @param request
     * @return
     */
    @GetMapping("/get")
    public R getTodoList(String username, HttpServletRequest request) {
        User userInfo = (User) request.getAttribute(Constants.USER_ATTRIBUTE);

        //如果是当前用户老人，判断是否有查看权限
        if (!StringUtils.isBlank(username) && userInfo.getIsElderly() && !userInfo.getName().equals(username)) {
            return R.fail("无查看权限");
        }

        //通过service层获取todolist列表，如果当前用户为监护人时才传入operaterId参数
        List<TodoListVO> todoList =
                todoListService.getTodoList(StringUtils.isBlank(username) ? userInfo.getName() : username
                        , userInfo.getIsElderly() ? null : userInfo.getId());
        return R.ok(todoList);
    }

}

