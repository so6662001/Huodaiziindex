package com.huodaizi.backend.controller.inquiry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class InquirySubscriptionIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void subscriptionPlansShouldReturnItems() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                get("/api/v1/inquiries/merchant/subscription/plans").param("merchantId", "S001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.merchantId").value("S001"))
            .andReturn();

    Map<?, ?> body = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
    Map<?, ?> data = (Map<?, ?>) body.get("data");
    assertThat(((List<?>) data.get("plans"))).isNotEmpty();
  }

  @Test
  void createSubscriptionShouldWork() throws Exception {
    String payload =
        """
        {
          "merchantId":"S001",
          "planCode":"PLAN_PRO",
          "billingCycle":"MONTHLY",
          "seats":3,
          "operator":"王运营"
        }
        """;

    mockMvc
        .perform(
            post("/api/v1/inquiries/merchant/subscription")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("0"))
        .andExpect(jsonPath("$.data.merchantId").value("S001"))
        .andExpect(jsonPath("$.data.subscriptionNo").isNotEmpty())
        .andExpect(jsonPath("$.data.planCode").value("PLAN_PRO"))
        .andExpect(jsonPath("$.data.status").value("ACTIVE"));
  }

  @Test
  void mySubscriptionsShouldContainNewOrder() throws Exception {
    String payload =
        """
        {
          "merchantId":"S001",
          "planCode":"PLAN_ENTERPRISE",
          "billingCycle":"YEARLY",
          "seats":5,
          "operator":"李商务"
        }
        """;

    mockMvc
        .perform(
            post("/api/v1/inquiries/merchant/subscription")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isOk());

    MvcResult result =
        mockMvc
            .perform(get("/api/v1/inquiries/merchant/subscription/mine").param("merchantId", "S001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("0"))
            .andExpect(jsonPath("$.data.merchantId").value("S001"))
            .andReturn();

    Map<?, ?> body = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
    Map<?, ?> data = (Map<?, ?>) body.get("data");
    assertThat(((Number) data.get("activeCount")).intValue()).isGreaterThan(0);
    assertThat(((List<?>) data.get("items"))).isNotEmpty();
  }

  @Test
  void createSubscriptionShouldRejectUnknownPlan() throws Exception {
    String payload =
        """
        {
          "merchantId":"S001",
          "planCode":"PLAN_UNKNOWN",
          "billingCycle":"MONTHLY",
          "seats":2
        }
        """;

    mockMvc
        .perform(
            post("/api/v1/inquiries/merchant/subscription")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("NOT_FOUND"));
  }
}
