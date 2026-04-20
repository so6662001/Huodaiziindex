package com.huodaizi.backend.controller.planpricing;

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
class A07PlanPricingAdminIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void listShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/plan-pricing/plans"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void listShouldWorkWithValidToken() throws Exception {
    mockMvc
        .perform(
            get("/api/admin/plan-pricing/plans")
                .header("X-Admin-Token", "test-admin-token")
                .param("planType", "PRO")
                .param("enabled", "true")
                .param("page", "1")
                .param("pageSize", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.total").isNumber())
        .andExpect(jsonPath("$.data.enabledCount").isNumber())
        .andExpect(jsonPath("$.data.items").isArray());
  }

  @Test
  void updateShouldWorkAndSubscriptionShouldRespectEnabledFlag() throws Exception {
    mockMvc
        .perform(
            put("/api/admin/plan-pricing/plans/PLAN_PRO")
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "planCode":"PLAN_PRO",
                      "planName":"进阶版-A07",
                      "planType":"PRO",
                      "billingCycle":"MONTHLY",
                      "price":"4299",
                      "originalPrice":"4999",
                      "suitableFor":"成交增长商家",
                      "recommended":true,
                      "enabled":false,
                      "operator":"a07-admin",
                      "remark":"A07策略调价",
                      "features":[
                        {"code":"PICKUP","name":"提货通","value":"提货单协同与履约留痕","highlight":"增强","sort":1},
                        {"code":"RECONCILE","name":"对账通","value":"对账回款流程管理","highlight":"增强","sort":2},
                        {"code":"CREDIT","name":"信用评分","value":"信用分与改进建议","highlight":"增强","sort":3}
                      ]
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.planCode").value("PLAN_PRO"))
        .andExpect(jsonPath("$.data.enabled").value(false))
        .andExpect(jsonPath("$.data.price").value("4299"));

    mockMvc
        .perform(
            post("/api/v1/inquiries/merchant/subscription")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "merchantId":"S001",
                      "planCode":"PLAN_PRO",
                      "billingCycle":"MONTHLY",
                      "operator":"qa"
                    }
                    """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.message").value("套餐已停用，暂不可开通"));
  }
}
