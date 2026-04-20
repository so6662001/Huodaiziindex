package com.huodaizi.backend.controller.admn10;

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
class Admn10BillingRuleAdminIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void listShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/billing-rules"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void listDetailAndUpsertShouldWork() throws Exception {
    String listResp =
        mockMvc
            .perform(
                get("/api/admin/billing-rules")
                    .header("X-Admin-Token", "test-admin-token")
                    .param("ruleStatus", "ACTIVE")
                    .param("sceneCode", "SUBSCRIPTION")
                    .param("page", "1")
                    .param("pageSize", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.records").isArray())
            .andExpect(jsonPath("$.data.activeCount").isNumber())
            .andReturn()
            .getResponse()
            .getContentAsString();

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    java.util.Map<?, ?> listBody = mapper.readValue(listResp, java.util.Map.class);
    java.util.Map<?, ?> listData = (java.util.Map<?, ?>) listBody.get("data");
    java.util.List<?> records = (java.util.List<?>) listData.get("records");
    String ruleId = String.valueOf(((java.util.Map<?, ?>) records.get(0)).get("ruleId"));

    mockMvc
        .perform(get("/api/admin/billing-rules/{ruleId}", ruleId).header("X-Admin-Token", "test-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.ruleId").value(ruleId))
        .andExpect(jsonPath("$.data.availableActions").isArray());

    String upsertResp =
        mockMvc
            .perform(
                put("/api/admin/billing-rules")
                    .header("X-Admin-Token", "test-admin-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "ruleCode":"BR_SUB_RATIO_2026",
                          "ruleName":"套餐订阅比例费（2026版）",
                          "sceneCode":"SUBSCRIPTION",
                          "billingMode":"RATIO",
                          "feeCurrency":"CNY",
                          "basePriceYuan":"0",
                          "minFeeYuan":"299",
                          "maxFeeYuan":"19999",
                          "ladderConfig":"ratio=0.015,round=HALF_UP,scale=2",
                          "effectiveFrom":"2026-05-01",
                          "effectiveTo":"2027-04-30",
                          "ruleStatus":"ACTIVE",
                          "remark":"新增订阅比例费规则",
                          "operator":"admn10-admin"
                        }
                        """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.ruleCode").value("BR_SUB_RATIO_2026"))
            .andExpect(jsonPath("$.data.sceneCode").value("SUBSCRIPTION"))
            .andExpect(jsonPath("$.data.billingMode").value("RATIO"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    java.util.Map<?, ?> upsertBody = mapper.readValue(upsertResp, java.util.Map.class);
    java.util.Map<?, ?> upsertData = (java.util.Map<?, ?>) upsertBody.get("data");
    String createdRuleId = String.valueOf(upsertData.get("ruleId"));

    mockMvc
        .perform(
            put("/api/admin/billing-rules")
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "ruleCode":"BR_SUB_RATIO_2026",
                      "ruleName":"套餐订阅比例费（2026版）",
                      "sceneCode":"SUBSCRIPTION",
                      "billingMode":"RATIO",
                      "feeCurrency":"CNY",
                      "basePriceYuan":"0",
                      "minFeeYuan":"299",
                      "maxFeeYuan":"19999",
                      "ladderConfig":"ratio=0.015,round=HALF_UP,scale=2",
                      "effectiveFrom":"2026-05-01",
                      "effectiveTo":"2027-04-30",
                      "ruleStatus":"DISABLED",
                      "remark":"停用该规则",
                      "operator":"admn10-admin"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.ruleId").value(createdRuleId))
        .andExpect(jsonPath("$.data.ruleStatus").value("DISABLED"))
        .andExpect(jsonPath("$.data.availableActions").isArray());

    mockMvc
        .perform(
            get("/api/admin/billing-rules")
                .header("X-Admin-Token", "test-admin-token")
                .param("keyword", "订阅比例费")
                .param("billingMode", "RATIO")
                .param("page", "1")
                .param("pageSize", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.records.length()").value(Matchers.greaterThanOrEqualTo(1)))
        .andExpect(jsonPath("$.data.records[0].billingMode").value("RATIO"));
  }
}
