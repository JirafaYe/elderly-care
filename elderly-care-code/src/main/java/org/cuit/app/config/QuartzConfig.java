package org.cuit.app.config;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * 配置定时任务
 */
@Configuration
public class QuartzConfig {

    /**
     * attention:
     * Details：定义quartz调度工厂
     */
    @Bean(name = "scheduler")
    public Scheduler schedulerFactory() throws SchedulerException {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler schedulerStd = schedulerFactory.getScheduler();
        return schedulerStd;
    }
}
