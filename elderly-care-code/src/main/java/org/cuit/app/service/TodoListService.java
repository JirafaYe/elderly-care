package org.cuit.app.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.cuit.app.quartz.TodoListJob;
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
import org.cuit.app.utils.DateUtils;
import org.quartz.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;
import java.util.HashMap;
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
@Slf4j
@Service
@AllArgsConstructor
public class TodoListService extends ServiceImpl<TodoListMapper, TodoList> {
    private final TodoListMapper todoListMapper;

    private final RelationshipMapper relationshipMapper;

    private final UserMapper userMapper;

    private final Scheduler scheduler;

    private final TransactionTemplate transactionTemplate;

    /**
     * 添加todolist
     *
     * @param vo
     * @param operator
     */
    public void addTodoList(TodoListVO vo, Integer operator) {
        TodoList list = convertToTodoList(vo);
        if (operator != null) {
            authorize(list.getElderlyId(), operator);
        }

        //将sql操作和设置定时任务包装成事务操作
        transactionTemplate.execute(status ->{
            if (todoListMapper.insert(list) != 1)
                throw new AppException("插入失败");

            vo.setId(list.getId().toString());
            try {
                setQuartzJob(vo,list.getElderlyId());
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
            return null;
        });

    }

    /**
     * 更新todolist
     *
     * @param vo
     * @param operator
     * @throws SchedulerException
     */
    public void updateTodoList(TodoListVO vo, Integer operator) throws SchedulerException {
        TodoList list = convertToTodoList(vo);
        if (operator != null) {
            authorize(list.getElderlyId(), operator);
        }

        //将sql操作和设置定时任务包装成事务操作
        transactionTemplate.execute(status ->{
            if (todoListMapper.update(list,new UpdateWrapper<TodoList>()
                    .eq("id",list.getId())
                    .isNull("delete_time")) != 1)
                throw new AppException("修改失败");
            try {
                setQuartzJob(vo,list.getElderlyId());
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }

            return null;
        });
    }

    /**
     * 进行todolist获取
     *
     * @param elderlyName 老人名字
     * @param operator 操作人id
     * @return
     */
    public List<TodoListVO> getTodoList(String elderlyName, Integer operator) {
        User user = userMapper.selectByName(elderlyName);
        if (!user.getIsElderly()) {
            throw new AuthorizedException("不是老人，无操作权限");
        }
        if (operator != null) {
            authorize(user.getId(), operator);
        }
        List<TodoList> todoList = todoListMapper.selectList(new QueryWrapper<TodoList>()
                .eq("elderly_id", user.getId())
//                .eq("date",date)
                .isNull("delete_time"));
        List<TodoListVO> vos = new LinkedList<>();
        for(TodoList list : todoList){
            vos.add(convertToTodoListVO(list));
        }
        return vos;
    }

    /**
     * 完成todolist
     *
     * @param todoId todolist的id
     * @param isElderly 操作人是否是老人
     * @param operator 操作人id
     */
    public void finishTodoList(Integer todoId,Boolean isElderly,Integer operator) {
        Integer elderlyId = todoListMapper.selectById(todoId).getElderlyId();
        if(!isElderly) {
            authorize(elderlyId,operator);
        }else {
            if(!elderlyId.equals(operator))
                throw new AuthorizedException("Invalid operator");
        }

        int update = todoListMapper.update(new TodoList()
                , new UpdateWrapper<TodoList>().set("delete_time", new Date())
                        .eq("id", todoId)
                        .isNull("delete_time"));
        if(update!=1)
            throw new AppException("更新数据库失败");
    }

    /**
     * 设置定时任务
     *
     * @param vo 需要推送的todolist
     * @param elderlyId todolist对应的老人
     * @throws SchedulerException quartz任务设置可能抛出的异常
     */
    private void setQuartzJob(TodoListVO vo,Integer elderlyId) throws SchedulerException {
        JobKey jobKey = new JobKey(vo.getId());
        if(scheduler.checkExists(jobKey)){
            scheduler.deleteJob(jobKey);
        }

        HashMap<String , Object> map = new HashMap<>();
        map.put("list",vo);
        map.put("elderly",elderlyId);

        JobDetail jobDetail = JobBuilder.newJob(TodoListJob.class)
                .withIdentity(jobKey)
                .setJobData(new JobDataMap(map))
                .build();

        String cron = DateUtils.getCron(vo.getBegin() == null ? vo.getDate() : vo.getBegin());

        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);

        CronTrigger  trigger = TriggerBuilder.newTrigger()
                .withIdentity(TriggerKey.triggerKey(vo.getId()))
                .withSchedule(cronScheduleBuilder).build();
        log.info("设置定时任务======>");
        scheduler.start();
        scheduler.scheduleJob(jobDetail,trigger);
    }

    /**
     * 将vo转换成todolist类型
     *
     * @param vo
     * @return
     */
    private TodoList convertToTodoList(TodoListVO vo) {
        TodoList todoList = new TodoList();
        if(vo.getId() != null)
            todoList.setId(Integer.parseInt(vo.getId()));
        todoList.setTodo(vo.getTodo());
        User user = userMapper.selectByName(vo.getElderlyName());
        if (user == null) {
            throw new AppException("用户名错误");
        }
        todoList.setElderlyId(user.getId());
        todoList.setDate(vo.getDate());
        todoList.setBeginTime(vo.getBegin());
        return todoList;
    }

    /**
     * 转换成todolistVO类型
     *
     * @param list
     * @return
     */
    private TodoListVO convertToTodoListVO(TodoList list) {
        TodoListVO vo = new TodoListVO();
        vo.setTodo(list.getTodo());
        vo.setBegin(list.getBeginTime());
        vo.setDate(list.getDate());
        vo.setId(list.getId().toString());
        return vo;
    }

    /**
     * 通过绑定关系判断监护人是否有操作权限
     *
     * @param elderlyId 老人id
     * @param operator 操作人id（监护人身份）
     */
    private void authorize(Integer elderlyId, Integer operator) {
        List<Integer> binders = relationshipMapper.getBinder(elderlyId);
        if (!binders.contains(operator))
            throw new AuthorizedException("无操作权限");
    }

}
