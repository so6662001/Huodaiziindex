package com.huodaizi.backend.controller.admn11;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
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
class Admn11PaymentRefundAdminIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void listShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/payment-refunds"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void listDetailAndReviewShouldWork() throws Exception {
    String listResp =
        mockMvc
            .perform(
                get("/api/admin/payment-refunds")
                    .header("X-Admin-Token", "test-admin-token")
                    .param("refundStatus", "PENDING_REVIEW")
                    .param("page", "1")
                    .param("pageSize", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.records").isArray())
            .andExpect(jsonPath("$.data.pendingCount").isNumber())
            .andReturn()
            .getResponse()
            .getContentAsString();

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    java.util.Map<?, ?> listBody = mapper.readValue(listResp, java.util.Map.class);
    java.util.Map<?, ?> listData = (java.util.Map<?, ?>) listBody.get("data");
    java.util.List<?> records = (java.util.List<?>) listData.get("records");
    String refundId = String.valueOf(((java.util.Map<?, ?>) records.get(0)).get("refundId"));

    mockMvc
        .perform(get("/api/admin/payment-refunds/{refundId}", refundId).header("X-Admin-Token", "test-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.refundId").value(refundId))
        .andExpect(jsonPath("$.data.availableActions").isArray());

    mockMvc
        .perform(
            put("/api/admin/payment-refunds/{refundId}/review", refundId)
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "action":"APPROVE",
                      "reviewRemark":"核验到账与合同条款一致，批准退款",
                      "operator":"admn11-finance"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.refundId").value(refundId))
        .andExpect(jsonPath("$.data.refundStatus").value("APPROVED"))
        .andExpect(jsonPath("$.data.latestRemark").value("核验到账与合同条款一致，批准退款"));

    mockMvc
        .perform(
            get("/api/admin/payment-refunds")
                .header("X-Admin-Token", "test-admin-token")
                .param("refundStatus", "REFUNDED")
                .param("keyword", "原路退款")
                .param("page", "1")
                .param("pageSize", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.records.length()").value(Matchers.greaterThanOrEqualTo(1)));
  }
}
