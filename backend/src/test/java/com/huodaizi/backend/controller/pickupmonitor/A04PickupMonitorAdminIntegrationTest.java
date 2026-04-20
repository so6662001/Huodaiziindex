package com.huodaizi.backend.controller.pickupmonitor;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
    properties = {
      "huodaizi.admin.auth.enabled=true",
      "huodaizi.admin.auth.token=test-admin-token"
    })
class A04PickupMonitorAdminIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void listShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/pickup-monitor/tasks"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void listShouldWorkWithValidToken() throws Exception {
    mockMvc
        .perform(
            get("/api/admin/pickup-monitor/tasks")
                .header("X-Admin-Token", "test-admin-token")
                .param("contactMobile", "13800138000")
                .param("page", "1")
                .param("pageSize", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.overview.totalOrders").isNumber())
        .andExpect(jsonPath("$.data.riskBuckets").isArray())
        .andExpect(jsonPath("$.data.items").isArray());
  }

  @Test
  void batchStatusShouldWork() throws Exception {
    String pickupId = createPickupOrder();
    mockMvc
        .perform(
            put("/api/admin/pickup-monitor/tasks/status")
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "contactMobile":"13800138000",
                      "pickupIds":["%s"],
                      "status":"CONFIRMED",
                      "operator":"a04-admin",
                      "remark":"A04批量确认"
                    }
                    """.formatted(pickupId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.total").value(1))
        .andExpect(jsonPath("$.data.items[0].status").value("CONFIRMED"));
  }

  private String createPickupOrder() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/inquiries/deal/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "inquiryId":"IQ20260418001",
                      "quoteId":"IQ20260418001-Q1",
                      "contactMobile":"13800138000",
                      "buyerCompany":"A04测试采购有限公司",
                      "buyerContact":"赵经理",
                      "buyerPhone":"13800138000"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"));

    String createBody =
        mockMvc
            .perform(
                post("/api/v1/inquiries/pickup-orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "inquiryId":"IQ20260418001",
                          "quoteId":"IQ20260418001-Q1",
                          "contactMobile":"13800138000",
                          "buyerCompany":"A04测试采购有限公司",
                          "buyerContact":"赵经理",
                          "pickupSite":"唐山丰润货场B区",
                          "pickupDate":"2030-04-20",
                          "pickupVehicleNo":"冀A04041",
                          "pickupDriverName":"李师傅",
                          "pickupDriverPhone":"13800008888",
                          "agreedProtocol":true,
                          "remark":"A04测试提货单"
                        }
                        """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    Map<?, ?> body = objectMapper.readValue(createBody, Map.class);
    Map<?, ?> data = (Map<?, ?>) body.get("data");
    return String.valueOf(data.get("pickupId"));
  }
}
