package com.accenture.mail.config;

import com.accenture.mail.service.SQSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
public class SchedulerConfiguration implements SchedulingConfigurer {

    private final SQSService sqsService;

    @Bean()
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(10);
    }

    @Autowired
    public SchedulerConfiguration(SQSService sqsService) {
        this.sqsService = sqsService;
    }


    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
        taskRegistrar.addTriggerTask(() -> {
            sqsService.poll();
        }, triggerContext -> {
            Calendar nextExecutionTime = new GregorianCalendar();
            Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
            nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
            nextExecutionTime.add(Calendar.MILLISECOND, sqsService.getDelay());
            return nextExecutionTime.getTime();
        });
    }
}
