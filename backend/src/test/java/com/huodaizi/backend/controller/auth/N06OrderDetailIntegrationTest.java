package com.huodaizi.backend.controller.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class N06OrderDetailIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void listDetailAndActionShouldWork() throws Exception {
    String loginResp =
        mockMvc
            .perform(
                post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "account":"13800138000",
                          "password":"Demo@123456",
                          "contactMobile":"13800138000"
                        }
                        """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    java.util.Map<?, ?> body = mapper.readValue(loginResp, java.util.Map.class);
    java.util.Map<?, ?> data = (java.util.Map<?, ?>) body.get("data");
    String token = String.valueOf(data.get("token"));

    String listResp =
        mockMvc
            .perform(
                get("/api/v1/auth/orders")
                    .header("X-Auth-Token", token)
                    .param("pageNo", "1")
                    .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.records").isArray())
            .andExpect(jsonPath("$.data.records[0].orderId").exists())
            .andReturn()
            .getResponse()
            .getContentAsString();

    java.util.Map<?, ?> listBody = mapper.readValue(listResp, java.util.Map.class);
    java.util.Map<?, ?> listData = (java.util.Map<?, ?>) listBody.get("data");
    java.util.List<?> records = (java.util.List<?>) listData.get("records");
    java.util.Map<?, ?> first = (java.util.Map<?, ?>) records.get(0);
    String orderId = String.valueOf(first.get("orderId"));

    mockMvc
        .perform(get("/api/v1/auth/orders/{orderId}", orderId).header("X-Auth-Token", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.orderId").value(orderId))
        .andExpect(jsonPath("$.data.timeline").isArray())
        .andExpect(jsonPath("$.data.actions").isArray());

    mockMvc
        .perform(
            post("/api/v1/auth/orders/{orderId}/action", orderId)
                .header("X-Auth-Token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "action":"MARK_PICKUP_IN_PROGRESS",
                      "remark":"测试操作：更新为已交付"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.orderStatus").value("PICKUP_IN_PROGRESS"));
  }
}
