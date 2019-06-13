package com.crio.qeats.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ThreadConfig {

  @Bean(name = "restaurantExecutor")
  public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setMaxPoolSize(4);
    executor.setCorePoolSize(4);
    executor.setQueueCapacity(500);
    executor.setThreadNamePrefix("RestaurantSearch-");
    executor.initialize();
    return executor;
  }
}
