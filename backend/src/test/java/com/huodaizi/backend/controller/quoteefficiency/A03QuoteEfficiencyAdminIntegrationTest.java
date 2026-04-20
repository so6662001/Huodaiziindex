package com.huodaizi.backend.controller.quoteefficiency;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
class A03QuoteEfficiencyAdminIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void listShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/quote-efficiency/tasks"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void listShouldWorkWithValidToken() throws Exception {
    mockMvc
        .perform(
            get("/api/admin/quote-efficiency/tasks")
                .header("X-Admin-Token", "test-admin-token")
                .param("merchantId", "S001")
                .param("quoteTimeoutOnly", "true")
                .param("page", "1")
                .param("pageSize", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.overview.totalLeads").isNumber())
        .andExpect(jsonPath("$.data.agingBuckets").isArray())
        .andExpect(jsonPath("$.data.items").isArray());
  }

  @Test
  void batchAndQuickQuoteShouldWork() throws Exception {
    mockMvc
        .perform(
            put("/api/admin/quote-efficiency/tasks/status")
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "merchantId":"S001",
                      "leadIds":["ML-IQ20260418001-S001"],
                      "status":"CONTACTED",
                      "operator":"a03-admin",
                      "comment":"A03批量跟进"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.total").value(1));

    mockMvc
        .perform(
            post("/api/admin/quote-efficiency/tasks/ML-IQ20260418001-S001/quick-quote")
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "merchantId":"S001",
                      "supplierName":"唐山弘达钢贸",
                      "unitPrice":"3520",
                      "deliveryDays":"2",
                      "paymentTerm":"月结30天",
                      "quoteRemark":"A03快捷报价",
                      "operator":"a03-admin"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.status").value("QUOTED"))
        .andExpect(jsonPath("$.data.quoteAgeMinutes").isNotEmpty());
  }
}
