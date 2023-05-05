package com.siemens.devops.monitoring.config;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadConfig {
	
//	private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
	private static final int CPU_COUNT = 1;
	private static final int COUR_SIZE = CPU_COUNT * 2;
	private static final int MAX_COUR_SIZE = CPU_COUNT * 4;

	@Bean
	ThreadPoolTaskExecutor threadPoolTaskExecutor(TaskExecutorBuilder builder) {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(COUR_SIZE);// 设置核心线程数
		threadPoolTaskExecutor.setMaxPoolSize(MAX_COUR_SIZE);// 配置最大线程数
		threadPoolTaskExecutor.setQueueCapacity(MAX_COUR_SIZE * 4);// 配置队列容量（这里设置成最大线程数的四倍）
		threadPoolTaskExecutor.setThreadNamePrefix("test-thread-");// 给线程池设置名称
		threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());// 设置任务的拒绝策略
		return threadPoolTaskExecutor;
	}

}
