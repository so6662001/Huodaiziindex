package com.huodaizi.backend.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(AdminAuthProperties.class)
public class WebMvcConfig implements WebMvcConfigurer {

  private final AdminAuthInterceptor adminAuthInterceptor;

  public WebMvcConfig(AdminAuthInterceptor adminAuthInterceptor) {
    this.adminAuthInterceptor = adminAuthInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(adminAuthInterceptor).addPathPatterns("/api/admin/**");
  }
}
