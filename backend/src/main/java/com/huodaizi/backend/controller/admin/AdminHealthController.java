package com.huodaizi.backend.controller.admin;

import com.huodaizi.backend.common.ApiResponse;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/health")
public class AdminHealthController {

  @GetMapping
  public ApiResponse<Map<String, Object>> health() {
    return ApiResponse.success(
        Map.of(
            "module",
            "admin",
            "status",
            "UP"));
  }
}
