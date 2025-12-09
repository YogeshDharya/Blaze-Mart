package com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public String openApiConfig() {
    // Configuration for OpenAPI can be added here in the future.
    return "OpenAPI Configured";
  }

}
