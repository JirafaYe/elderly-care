package org.cuit.app.quartz;

import lombok.extern.slf4j.Slf4j;
import org.cuit.app.entity.vo.TodoListVO;
import org.cuit.app.utils.WebSocketUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.io.IOException;

/**
 *执行todo-list定时推送
 */
@Slf4j
public class TodoListJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context){

        TodoListVO todoListVO = (TodoListVO) context.getJobDetail().getJobDataMap().get("list");
        Integer id = (Integer) context.getJobDetail().getJobDataMap().get("elderly");
        log.info("定时任务推送todo-list");

        System.out.println(id);

        try {
            WebSocketUtils.sendMsg(WebSocketUtils.getElderlyConnection(),id,todoListVO);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
