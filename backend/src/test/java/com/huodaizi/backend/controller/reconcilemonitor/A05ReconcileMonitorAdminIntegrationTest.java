package com.huodaizi.backend.controller.reconcilemonitor;

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
class A05ReconcileMonitorAdminIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void listShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/reconcile-monitor/tasks"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void listShouldWorkWithValidToken() throws Exception {
    mockMvc
        .perform(
            get("/api/admin/reconcile-monitor/tasks")
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
    String reconcileId = createReconcileOrder();
    mockMvc
        .perform(
            put("/api/admin/reconcile-monitor/tasks/status")
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "contactMobile":"13800138000",
                      "reconcileIds":["%s"],
                      "status":"PARTIAL_PAID",
                      "paidAmount":"300000",
                      "operator":"a05-admin",
                      "remark":"A05批量回款登记"
                    }
                    """
                        .formatted(reconcileId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.total").value(1))
        .andExpect(jsonPath("$.data.items[0].status").value("PARTIAL_PAID"));
  }

  private String createReconcileOrder() throws Exception {
    String pickupId = createPickupOrder();

    String createBody =
        mockMvc
            .perform(
                post("/api/v1/inquiries/reconcile-orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "pickupOrderId":"%s",
                          "contactMobile":"13800138000",
                          "statementMonth":"2026-04",
                          "settleType":"MONTHLY",
                          "dueDate":"2030-05-20",
                          "invoiceTitle":"A05测试采购有限公司",
                          "remark":"A05测试对账单"
                        }
                        """
                            .formatted(pickupId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    Map<?, ?> body = objectMapper.readValue(createBody, Map.class);
    Map<?, ?> data = (Map<?, ?>) body.get("data");
    return String.valueOf(data.get("reconcileId"));
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
                      "buyerCompany":"A05测试采购有限公司",
                      "buyerContact":"钱经理",
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
                          "buyerCompany":"A05测试采购有限公司",
                          "buyerContact":"钱经理",
                          "pickupSite":"唐山丰润货场C区",
                          "pickupDate":"2030-04-20",
                          "pickupVehicleNo":"冀A05051",
                          "pickupDriverName":"王师傅",
                          "pickupDriverPhone":"13800009999",
                          "agreedProtocol":true,
                          "remark":"A05测试提货单"
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
