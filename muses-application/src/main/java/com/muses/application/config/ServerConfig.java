package com.muses.application.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @ClassName ServerConfig
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/11 16:27
 */
@Slf4j
@Configuration
public class ServerConfig {

    @Bean
    public ThreadPoolTaskScheduler testExecutor() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler  = new ThreadPoolTaskScheduler();
        // 配置线程池大小
        threadPoolTaskScheduler.setPoolSize(20);
        // 设置线程名
        threadPoolTaskScheduler.setThreadNamePrefix("task-scheduling-");
        // 设置等待任务在关机时完成
        //    threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        // 设置等待终止时间
        // threadPoolTaskScheduler.setAwaitTerminationSeconds(60);

        return threadPoolTaskScheduler;
    }
}
