package com.huodaizi.backend.controller.admn16;

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
class Admn16DataApiSubscriptionAdminIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void listShouldRejectWhenMissingToken() throws Exception {
    mockMvc
        .perform(get("/api/admin/data-api-subscriptions"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void listDetailAndUpsertShouldWork() throws Exception {
    String listResp =
        mockMvc
            .perform(
                get("/api/admin/data-api-subscriptions")
                    .header("X-Admin-Token", "test-admin-token")
                    .param("subscriptionStatus", "ACTIVE")
                    .param("apiProductCode", "CREDIT_DATA")
                    .param("billingCycle", "MONTHLY")
                    .param("page", "1")
                    .param("pageSize", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.records").isArray())
            .andExpect(jsonPath("$.data.activeCount").isNumber())
            .andExpect(jsonPath("$.data.trialingCount").isNumber())
            .andReturn()
            .getResponse()
            .getContentAsString();

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    java.util.Map<?, ?> listBody = mapper.readValue(listResp, java.util.Map.class);
    java.util.Map<?, ?> listData = (java.util.Map<?, ?>) listBody.get("data");
    java.util.List<?> records = (java.util.List<?>) listData.get("records");
    String subscriptionId = String.valueOf(((java.util.Map<?, ?>) records.get(0)).get("subscriptionId"));

    mockMvc
        .perform(
            get("/api/admin/data-api-subscriptions/{subscriptionId}", subscriptionId)
                .header("X-Admin-Token", "test-admin-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.subscriptionId").value(subscriptionId))
        .andExpect(jsonPath("$.data.quotaMetrics").isArray())
        .andExpect(jsonPath("$.data.availableActions").isArray());

    String upsertResp =
        mockMvc
            .perform(
                put("/api/admin/data-api-subscriptions")
                    .header("X-Admin-Token", "test-admin-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "subscriptionCode":"DAPI_SUB_CREDIT_PRO_2026Q2",
                          "subscriptionName":"信用数据API订阅增强版",
                          "merchantId":"M000009",
                          "merchantName":"成都锦川钢贸",
                          "apiPackageCode":"CREDIT_DATA",
                          "subscriptionStatus":"ACTIVE",
                          "billingCycle":"MONTHLY",
                          "startDate":"2026-05-01",
                          "endDate":"2026-12-31",
                          "autoRenewFlag":"Y",
                          "throttlePolicy":"AK_SK",
                          "qpsLimit":"80",
                          "dailyQuota":"250000",
                          "monthlyQuota":"7500000",
                          "owner":"数据产品组",
                          "metrics":[
                            {
                              "metricCode":"SUCCESS_RATE",
                              "metricName":"调用成功率",
                              "usedValue":"113000",
                              "quotaValue":"250000",
                              "usageRate":"45.2%",
                              "trend":"+0.18%"
                            },
                            {
                              "metricCode":"P95_LATENCY",
                              "metricName":"P95延迟",
                              "usedValue":"238ms",
                              "quotaValue":"240ms",
                              "usageRate":"99.1%",
                              "trend":"-17ms"
                            }
                          ],
                          "remark":"扩容后继续观察QPS峰值与稳定性",
                          "operator":"admn16-admin"
                        }
                        """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.subscriptionCode").value("DAPI_SUB_CREDIT_PRO_2026Q2"))
            .andExpect(jsonPath("$.data.subscriptionStatus").value("ACTIVE"))
            .andExpect(jsonPath("$.data.apiPackageCode").value("CREDIT_DATA"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    java.util.Map<?, ?> upsertBody = mapper.readValue(upsertResp, java.util.Map.class);
    java.util.Map<?, ?> upsertData = (java.util.Map<?, ?>) upsertBody.get("data");
    String createdSubscriptionId = String.valueOf(upsertData.get("subscriptionId"));

    mockMvc
        .perform(
            put("/api/admin/data-api-subscriptions")
                .header("X-Admin-Token", "test-admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "subscriptionCode":"DAPI_SUB_CREDIT_PRO_2026Q2",
                      "subscriptionName":"信用数据API订阅增强版",
                      "merchantId":"M000009",
                      "merchantName":"成都锦川钢贸",
                      "apiPackageCode":"CREDIT_DATA",
                      "subscriptionStatus":"SUSPENDED",
                      "billingCycle":"MONTHLY",
                      "startDate":"2026-05-01",
                      "endDate":"2026-12-31",
                      "autoRenewFlag":"Y",
                      "throttlePolicy":"AK_SK",
                      "qpsLimit":"80",
                      "dailyQuota":"250000",
                      "monthlyQuota":"7500000",
                      "owner":"数据产品组",
                      "metrics":[
                        {
                          "metricCode":"SUCCESS_RATE",
                          "metricName":"调用成功率",
                          "usedValue":"113000",
                          "quotaValue":"250000",
                          "usageRate":"45.2%",
                          "trend":"+0.18%"
                        }
                      ],
                      "remark":"临时停用，待完成密钥轮转",
                      "operator":"admn16-admin"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.subscriptionId").value(createdSubscriptionId))
        .andExpect(jsonPath("$.data.subscriptionStatus").value("SUSPENDED"))
        .andExpect(jsonPath("$.data.availableActions").isArray());

    mockMvc
        .perform(
            get("/api/admin/data-api-subscriptions")
                .header("X-Admin-Token", "test-admin-token")
                .param("keyword", "密钥轮转")
                .param("subscriptionStatus", "SUSPENDED")
                .param("page", "1")
                .param("pageSize", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.records.length()").value(Matchers.greaterThanOrEqualTo(1)));
  }
}
