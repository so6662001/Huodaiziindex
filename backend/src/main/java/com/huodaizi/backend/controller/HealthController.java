package com.huodaizi.backend.controller;

import com.huodaizi.backend.common.ApiResponse;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HealthController {

  @Value("${spring.profiles.active:dev}")
  private String activeProfile;

  @GetMapping("/health")
  public ApiResponse<Map<String, Object>> health() {
    return ApiResponse.success(
        Map.of(
            "status", "UP",
            "profile", activeProfile,
            "time", LocalDateTime.now().toString()));
  }
}
