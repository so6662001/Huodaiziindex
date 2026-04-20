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
class H5N06PickupScanIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void h5PickupListScanDetailAndStatusShouldWork() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/auth/h5/send-login-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "mobile":"13800138000",
                      "operator":"h5-n06-test"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"));

    String loginResp =
        mockMvc
            .perform(
                post("/api/v1/auth/h5/quick-login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "mobile":"13800138000",
                          "smsCode":"123456",
                          "channel":"H5",
                          "operator":"h5-n06-test"
                        }
                        """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.channel").value("H5"))
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
                get("/api/v1/auth/h5/pickups")
                    .header("X-Auth-Token", token)
                    .param("pageNo", "1")
                    .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.channel").value("H5"))
            .andExpect(jsonPath("$.data.contactMobileMasked").isNotEmpty())
            .andExpect(jsonPath("$.data.records").isArray())
            .andReturn()
            .getResponse()
            .getContentAsString();

    java.util.Map<?, ?> listBody = mapper.readValue(listResp, java.util.Map.class);
    java.util.Map<?, ?> listData = (java.util.Map<?, ?>) listBody.get("data");
    java.util.List<?> records = (java.util.List<?>) listData.get("records");
    if (records == null || records.isEmpty()) {
      return;
    }
    java.util.Map<?, ?> first = (java.util.Map<?, ?>) records.get(0);
    String pickupId = String.valueOf(first.get("pickupId"));
    String pickupNo = String.valueOf(first.get("pickupNo"));

    mockMvc
        .perform(get("/api/v1/auth/h5/pickups/{pickupId}", pickupId).header("X-Auth-Token", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.pickupId").value(pickupId))
        .andExpect(jsonPath("$.data.channel").value("H5"))
        .andExpect(jsonPath("$.data.availableActions").isArray());

    mockMvc
        .perform(
            post("/api/v1/auth/h5/pickups/scan")
                .header("X-Auth-Token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "scanCode":"%s",
                      "operator":"h5-n06-test",
                      "remark":"扫码核验通过"
                    }
                    """
                        .formatted(pickupNo)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.pickupId").value(pickupId))
        .andExpect(jsonPath("$.data.scanResult").value("扫码核验成功，可执行「确认提货/运输中/已签收/已完成」状态流转"));

    mockMvc
        .perform(
            post("/api/v1/auth/h5/pickups/{pickupId}/status", pickupId)
                .header("X-Auth-Token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "status":"IN_TRANSIT",
                      "operator":"h5-n06-test",
                      "remark":"车辆已发车"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.status").value("IN_TRANSIT"))
        .andExpect(jsonPath("$.data.statusText").value("运输中"));
  }

  @Test
  void h5PickupApisShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/v1/auth/h5/pickups"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }
}
