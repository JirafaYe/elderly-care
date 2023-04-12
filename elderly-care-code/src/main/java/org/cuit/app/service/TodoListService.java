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

    public void addTodoList(TodoListVO vo, Integer operator) {
        TodoList list = convertToTodoList(vo);
        if (operator != null) {
            authorize(list.getElderlyId(), operator);
        }
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

    public void updateTodoList(TodoListVO vo, Integer operator) throws SchedulerException {
        TodoList list = convertToTodoList(vo);
        if (operator != null) {
            authorize(list.getElderlyId(), operator);
        }

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

    private TodoListVO convertToTodoListVO(TodoList list) {
        TodoListVO vo = new TodoListVO();
        vo.setTodo(list.getTodo());
        vo.setBegin(list.getBeginTime());
        vo.setDate(list.getDate());
        vo.setId(list.getId().toString());
        return vo;
    }

    private void authorize(Integer elderlyId, Integer operator) {
        List<Integer> binders = relationshipMapper.getBinder(elderlyId);
        if (!binders.contains(operator))
            throw new AuthorizedException("无操作权限");
    }

}
